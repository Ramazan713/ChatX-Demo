package com.example.chatx.features.chat.presentation.chat_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.chatx.core.domain.manager.SessionManager
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.UUID


//when reconnect refetch messages with restapi

class ChatDetailViewModel(
    private val chatApi: ChatApi,
    private val chatStreamApi: ChatStreamApi,
    private val sessionManager: SessionManager,
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
        listenFlows()
        loadMessages()
        sendTyping()
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
        viewModelScope.launch {
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
            if(realMsg.getIsMine(_state.value.username)){
                state.copy(
                    messages = state.messages.map { msg ->
                        if (msg.clientTempId == realMsg.clientTempId) realMsg else msg
                    }
                )
            }else{
                state.copy(messages = state.messages + realMsg)
            }
        }
    }

    private fun loadMessages(){
        viewModelScope.launch {
            val dataResponse = chatApi.getMessages(args.roomId)
            dataResponse.onFailure { error ->
                _state.update { it.copy(
                    message = error.text
                ) }
            }
            dataResponse.onSuccess { data ->
                _state.update { it.copy(
                    messages = data,
                    uiEvent = ChatDetailUiEvent.ScrollToBottom
                ) }
                ackMessages(data)
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
                }
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
}
