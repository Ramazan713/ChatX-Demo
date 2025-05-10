package com.example.chatx.features.chat.presentation.chat_list

import com.example.chatx.features.chat.domain.models.ChatRoom

sealed interface ChatListDialogEvent {

    data object ShowJoinRoomDia: ChatListDialogEvent

    data class ShowBottomMenu(
        val room: ChatRoom
    ): ChatListDialogEvent

    data class AskDeleteRoom(
        val roomName: String,
        val onApproved: () -> Unit
    ): ChatListDialogEvent

    data class AskLeaveRoom(
        val roomName: String,
        val onApproved: () -> Unit
    ): ChatListDialogEvent
}