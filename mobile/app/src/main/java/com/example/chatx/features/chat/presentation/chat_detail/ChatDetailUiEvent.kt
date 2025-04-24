package com.example.chatx.features.chat.presentation.chat_detail

sealed interface ChatDetailUiEvent {

    data object ScrollToBottom: ChatDetailUiEvent
}