package com.example.chatx.features.chat.presentation.chat_list

sealed interface ChatListAction {

    data class JoinRoom(val roomName: String): ChatListAction

    data class Leave(val roomId: String): ChatListAction

    data class Mute(val roomId: String): ChatListAction

    data class UnMute(val roomId: String): ChatListAction

    data class Delete(val roomId: String): ChatListAction

    data class ShowDialog(val dialogEvent: ChatListDialogEvent?): ChatListAction

    data object ClearMessage: ChatListAction
}