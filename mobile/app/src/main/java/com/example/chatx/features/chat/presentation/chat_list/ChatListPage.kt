package com.example.chatx.features.chat.presentation.chat_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adb
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.unit.dp
import com.example.chatx.core.presentation.utils.ShowLifecycleToastMessage
import com.example.chatx.features.chat.presentation.chat_list.components.RoomRow

@Composable
fun ChatListPageRoot(
    onNavigateToChatDetail: (String) -> Unit,
    viewModel: ChatListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ChatListPage(
        state = state,
        onAction = viewModel::onAction,
        onNavigateToChatDetail = onNavigateToChatDetail
    )
}

@Composable
fun ChatListPage(
    state: ChatListState,
    onAction: (ChatListAction) -> Unit,
    onNavigateToChatDetail: (String) -> Unit,
) {

    ShowLifecycleToastMessage(
        state.message
    ) {
        onAction(ChatListAction.ClearMessage)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onAction(ChatListAction.JoinRoom("roomx"))
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { paddings ->
        LazyColumn(
            modifier = Modifier
                .padding(paddings),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
        ) {
            items(state.rooms){ room ->
                RoomRow(
                    room = room,
                    onJoinClick = {
                        onNavigateToChatDetail(room.id)
                    },
                    onShowClick = {
                        onNavigateToChatDetail(room.id)
                    },
                    onLeaveClick = {
                        onAction(ChatListAction.LeaveRoom(room.id))
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatListPreview(modifier: Modifier = Modifier) {
    ChatListPage(
        state = ChatListState(),
        onAction = {},
        onNavigateToChatDetail = {}
    )
}