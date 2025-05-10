package com.example.chatx.features.chat.presentation.chat_list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.dp
import com.example.chatx.core.presentation.dialogs.ShowGetTextDialog
import com.example.chatx.core.presentation.dialogs.ShowQuestionDialog
import com.example.chatx.core.presentation.selections.ShowSelectBottomMenuItems
import com.example.chatx.core.presentation.utils.ShowLifecycleToastMessage
import com.example.chatx.features.chat.presentation.chat_list.components.RoomRow
import com.example.chatx.features.chat.presentation.chat_list.enums.RoomRowMenuItem

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

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
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
                    onAction(ChatListAction.ShowDialog(ChatListDialogEvent.ShowJoinRoomDia))
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
                    onMenuItemClick = {
                        onAction(ChatListAction.ShowDialog(ChatListDialogEvent.ShowBottomMenu(room)))
                    }
                )
            }
        }
    }


    state.dialogEvent?.let { dialogEvent ->
        val close = remember { {
            onAction(ChatListAction.ShowDialog(null))
        } }

        when(dialogEvent){
            ChatListDialogEvent.ShowJoinRoomDia -> {
                ShowGetTextDialog(
                    title = "Room Name",
                    onClosed = close,
                    onApproved = { text ->
                        onAction(ChatListAction.JoinRoom(text))
                    }
                )
            }
            is ChatListDialogEvent.ShowBottomMenu -> {
                val room = dialogEvent.room
                ShowSelectBottomMenuItems(
                    items = RoomRowMenuItem.getItems(room.muted, room.leftAt != null),
                    onClose = close,
                    onClickItem = { menuItem ->
                        when(menuItem){
                            RoomRowMenuItem.Mute -> {
                                onAction(ChatListAction.Mute(room.id))
                                close()
                            }
                            RoomRowMenuItem.UnMute -> {
                                onAction(ChatListAction.UnMute(room.id))
                                close()
                            }
                            RoomRowMenuItem.Leave -> {
                                onAction(ChatListAction.ShowDialog(ChatListDialogEvent.AskLeaveRoom(
                                    roomName = room.name,
                                    onApproved = {
                                        onAction(ChatListAction.Leave(room.id))
                                    }
                                )))
                            }
                            RoomRowMenuItem.Delete -> {
                                onAction(ChatListAction.ShowDialog(ChatListDialogEvent.AskDeleteRoom(
                                    roomName = room.name,
                                    onApproved = {
                                        onAction(ChatListAction.Delete(room.id))
                                    }
                                )))
                            }
                        }
                    }
                )
            }

            is ChatListDialogEvent.AskDeleteRoom -> {
                ShowQuestionDialog(
                    title = "Do you want to delete the room ${dialogEvent.roomName}",
                    content = "This action can not be undone",
                    onApproved = dialogEvent.onApproved,
                    onClosed = close
                )
            }

            is ChatListDialogEvent.AskLeaveRoom -> ShowQuestionDialog(
                title = "Do you want to leave the room ${dialogEvent.roomName}",
                content = "This action can not be undone",
                onApproved = dialogEvent.onApproved,
                onClosed = close
            )
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