package com.example.chatx.features.chat.presentation.chat_detail

import com.example.chatx.features.chat.domain.models.ChatMessage

sealed interface ChatDetailAction {

    data object SendMessage: ChatDetailAction

    data class OnInputChange(val inputText: String): ChatDetailAction

    data object ClearMessage: ChatDetailAction

    data object ClearUiEvent: ChatDetailAction

    data class Retry(val msg: ChatMessage): ChatDetailAction

    data object LoadPreviousMessages: ChatDetailAction
}