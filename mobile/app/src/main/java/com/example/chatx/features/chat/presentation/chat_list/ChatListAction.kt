package com.example.chatx.features.chat.presentation.chat_list

sealed interface ChatListAction {

    data class JoinRoom(val roomName: String): ChatListAction

    data class LeaveRoom(val roomId: String): ChatListAction

    data object ClearMessage: ChatListAction
}