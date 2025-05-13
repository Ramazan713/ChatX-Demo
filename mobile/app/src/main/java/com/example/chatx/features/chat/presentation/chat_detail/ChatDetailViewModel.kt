package com.example.chatx.features.chat.presentation.chat_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.chatx.core.domain.manager.SessionManager
import com.example.chatx.core.domain.services.ConnectivityObserver
import com.example.chatx.core.domain.utils.UiText
import com.example.chatx.features.chat.domain.api.ChatApi
import com.example.chatx.features.chat.domain.api.ChatStreamApi
import com.example.chatx.features.chat.domain.models.ChatMessage
import com.example.chatx.features.chat.presentation.navigation.ChatDetailRoute
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.util.UUID

class ChatDetailViewModel(
    private val chatApi: ChatApi,
    private val chatStreamApi: ChatStreamApi,
    private val sessionManager: SessionManager,
    private val connectivityObserver: ConnectivityObserver,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    private var isTyping = false
    private val args = savedStateHandle.toRoute<ChatDetailRoute>()

    private val _state = MutableStateFlow(ChatDetailState())
    val state get() = _state.asStateFlow()

    private val pendingJobs = mutableMapOf<String, Job>()

    init {
        viewModelScope.launch {
            sessionManager.getUser()?.let { user ->
                _state.update { it.copy(
                    username = user.username
                ) }
            }
        }

        loadMessages()

        _state
            .map { it.room }
            .filterNotNull()
            .distinctUntilChanged()
            .onEach {
                listenFlows()
                sendTyping()
                listenConnectivity()
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: ChatDetailAction){
        when(action){
            ChatDetailAction.SendMessage -> {
                viewModelScope.launch {
                    val message = _state.value.inputText.trim()
                    _state.update { it.copy(
                        inputText = "",
                    ) }
                    sendMessage(message)
                }
            }
            is ChatDetailAction.OnInputChange -> {
                _state.update { it.copy(
                    inputText = action.inputText
                ) }
            }

            ChatDetailAction.ClearMessage -> {
                _state.update { it.copy(
                    message = null
                ) }
            }

            ChatDetailAction.ClearUiEvent -> {
                _state.update { it.copy(
                    uiEvent = null
                ) }
            }

            is ChatDetailAction.Retry -> {
                retryMessage(action.msg)
            }

            ChatDetailAction.LoadPreviousMessages -> {
                if (_state.value.isPreviousLoading) return
                val pageInfo = _state.value.pageInfo ?: return
                if(!pageInfo.hasPreviousPage) return
                val firstItem = _state.value.messages.firstOrNull() ?: return
                loadMessages(beforeId = firstItem.id)
            }
        }
    }

    private fun markFailed(clientTempId: String) {
        _state.update { st ->
            st.copy(messages = st.messages
                .map { msg ->
                    if (msg.clientTempId == clientTempId)
                        msg.copy(pending = false, failed = true)
                    else msg
                }
            )
        }
    }

    private fun retryMessage(msg: ChatMessage) {
        val tempId = msg.clientTempId ?: return
        println("AppXXXX retryMessage: $msg")
        viewModelScope.launch {
            val messageInState = _state.value.messages.firstOrNull { it.clientTempId == tempId }
            if(messageInState != null && !messageInState.pending && !messageInState.failed) return@launch
            _state.update { st ->
                st.copy(messages = st.messages.map { m ->
                    if (m.clientTempId == tempId) {
                        m.copy(pending = true, failed = false)
                    } else m
                })
            }
            chatStreamApi.sendMessage(args.roomId, msg.text, tempId)
            pendingJobs[tempId]?.cancel()
            pendingJobs[tempId] = viewModelScope.launch {
                delay(10_000)
                markFailed(tempId)
            }
        }
    }

    private fun sendMessage(message: String){
        viewModelScope.launch {
            val tempId = UUID.randomUUID().toString()
            val now   = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val pendingMsg = ChatMessage(
                id = tempId,
                clientTempId = tempId,
                roomId = args.roomId,
                username = _state.value.username,
                text = message,
                createdAt = now,
                pending = true,
                readBy = listOf()
            )
            _state.update { it.copy(
                messages = it.messages + pendingMsg,
                uiEvent = ChatDetailUiEvent.ScrollToBottom
            ) }
            chatStreamApi.sendMessage(args.roomId, message, tempId = tempId)

            pendingJobs[tempId]?.cancel()
            pendingJobs[tempId] = viewModelScope.launch {
                delay(10_000)
                markFailed(tempId)
            }
        }
    }

    private fun replacePendingOrAppend(realMsg: ChatMessage) {
        pendingJobs.remove(realMsg.clientTempId)?.cancel()
        _state.update { state ->
            val merged = if(realMsg.getIsMine(_state.value.username)){
                state.messages.map { msg ->
                    if (msg.clientTempId == realMsg.clientTempId) realMsg else msg
                }
            }else{
                state.messages + realMsg
            }
            state.copy(messages = sortMessages(merged))
        }
    }

    private fun loadMessages(afterId: String? = null, beforeId: String? = null){
        viewModelScope.launch {
            _state.update { it.copy(
                isLoading = it.room == null,
                isPreviousLoading = it.room != null && beforeId != null
            ) }
            val dataResponse = chatApi.getMessagesWithRoom(args.roomId, afterId = afterId, beforeId = beforeId)
            dataResponse.onFailure { error ->
                _state.update { it.copy(
                    message = error.text,
                    isLoading = false,
                    isPreviousLoading = false
                ) }
            }
            dataResponse.onSuccess { messagesWithRoom ->
                _state.update { it.copy(
                    messages = sortMessages(it.messages + messagesWithRoom.messages),
                    uiEvent = if(beforeId != null) it.uiEvent else ChatDetailUiEvent.ScrollToBottom,
                    room = messagesWithRoom.room,
                    pageInfo = if(afterId == null) messagesWithRoom.pageInfo else it.pageInfo,
                    isLoading = false,
                    isPreviousLoading = false
                ) }
                ackMessages(messagesWithRoom.messages)
            }
        }
    }

    private fun listenFlows(){
        chatStreamApi
            .events(args.roomId)
            .onEach { event ->
                when(event){
                    is ChatStreamApi.Event.Error -> {
                        _state.update { it.copy(
                            message = event.error
                        ) }
                    }
                    is ChatStreamApi.Event.NewMessage -> {
                        replacePendingOrAppend(event.message)
                        _state.update { it.copy(
                            uiEvent = ChatDetailUiEvent.ScrollToBottom,
                        ) }
                        ackMessages(listOf(event.message))
                    }
                    is ChatStreamApi.Event.ReadMessages -> {
                        val groupedMessages = event.messages.groupBy { it.id }
                        _state.update { state ->
                            state.copy(
                                messages = state.messages.map{ m -> groupedMessages[m.id]?.firstOrNull() ?: m }
                            )
                        }
                    }
                    is ChatStreamApi.Event.TypingUsers -> {
                        _state.update { it.copy(
                            typingUsers = event.users
                        ) }
                    }

                    is ChatStreamApi.Event.MessageValidationError -> {
                        _state.update { st -> st.copy(
                            messages = st.messages.filterNot { it.clientTempId == event.tempId },
                            message = UiText.Text(event.error)
                        ) }
                    }

                    is ChatStreamApi.Event.ConnectionStatus -> {
                        if(!event.isConnected) return@onEach
                        val lastMessage = _state.value.messages.lastOrNull { !it.failed && !it.pending } ?: return@onEach
                        loadMessages(lastMessage.id)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun listenConnectivity(){
        connectivityObserver
            .isConnected
            .distinctUntilChanged()
            .filter { it }
            .onEach {
                chatStreamApi.reconnect()
            }
            .launchIn(viewModelScope)
    }

    @OptIn(FlowPreview::class)
    private fun sendTyping(){
        val inputFlow = _state.map { it.inputText }
        inputFlow
            .distinctUntilChanged()
            .onEach { text ->
                if(text.isNotEmpty() && !isTyping){
                    chatStreamApi.typing(args.roomId)
                    isTyping = true
                }
            }
            .launchIn(viewModelScope)

        _state
            .map { it.inputText }
            .debounce(1500)
            .onEach {
                if(isTyping){
                    chatStreamApi.stopTyping(args.roomId)
                    isTyping = false
                }
            }
            .launchIn(viewModelScope)
    }



    private fun ackMessages(messages: List<ChatMessage>){
        val ackMessages = messages.mapNotNull { m ->
            if(m.username == _state.value.username) null
            else if(m.readBy.contains(_state.value.username)) null
            else m
        }.map { it.id }
        viewModelScope.launch {
            chatStreamApi.ackMessages(args.roomId, ackMessages)
        }
    }

    private fun sortMessages(list: List<ChatMessage>): List<ChatMessage> =
        list
            .distinctBy { it.id }
            .sortedWith(
                compareBy<ChatMessage> { it.createdAt }
                    .thenBy { it.id }
            )
}
