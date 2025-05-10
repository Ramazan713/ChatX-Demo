package com.example.chatx.features.chat.presentation.chat_detail


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatx.core.presentation.utils.EventHandler
import com.example.chatx.core.presentation.utils.ShowLifecycleToastMessage
import com.example.chatx.features.chat.domain.models.ChatRoom
import com.example.chatx.features.chat.presentation.chat_detail.components.ChatMessageItem
import com.example.chatx.features.chat.presentation.chat_detail.components.TypingIndicator
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChatDetailPageRoot(
    viewModel: ChatDetailViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ChatDetailPage(
        state = state,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailPage(
    state: ChatDetailState,
    onAction: (ChatDetailAction) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val lazyState = rememberLazyListState()
    ShowLifecycleToastMessage(
        state.message
    ) {
        onAction(ChatDetailAction.ClearMessage)
    }

    EventHandler(state.uiEvent) { uiEvent ->
        when(uiEvent){
            ChatDetailUiEvent.ScrollToBottom -> {
                val lastIndex = state.messages.lastIndex
                if(lastIndex >= 0){
                    lazyState.animateScrollToItem(lastIndex)
                }
            }
        }
        onAction(ChatDetailAction.ClearUiEvent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(state.room?.name ?: "")
                },
                navigationIcon = {
                    IconButton(onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { paddings ->
        Column(
            modifier = Modifier
                .padding(paddings),
        ) {
            if(state.isLoading){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Scaffold
            }else if(state.room == null){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "something went wrong",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                return@Scaffold
            }

            if(state.messages.isEmpty()){
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No messages found",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }else{
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    state = lazyState
                ) {
                    items(state.messages) { msg ->
                        ChatMessageItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            msg = msg,
                            currentUsername = state.username,
                            onRetry = {
                                onAction(ChatDetailAction.Retry(msg))
                            }
                        )
                    }
                }
            }

            if(state.room.leftAt != null){
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "You left this chat",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }else{
                TypingIndicator(
                    typingUsers = state.typingUsers,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
                Row(Modifier.padding(8.dp)) {
                    TextField(
                        value = state.inputText,
                        onValueChange = {
                            onAction(ChatDetailAction.OnInputChange(it))
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Mesaj yazın") }
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {onAction(ChatDetailAction.SendMessage) }) {
                        Text("Gönder")
                    }
                }
            }


        }

    }
}

@Preview(showBackground = true)
@Composable
private fun ChatDetailPreview(modifier: Modifier = Modifier) {
    ChatDetailPage(
        state = ChatDetailState(
            isLoading = false,
            room = ChatRoom(
                id = "",
                muted = false,
                name = "roomx",
                isPublic = true,
                leftAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                joinedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            )
        ),
        onAction = {},
        onNavigateBack = {

        },
    )
}