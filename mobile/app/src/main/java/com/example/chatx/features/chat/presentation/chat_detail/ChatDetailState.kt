package com.example.chatx.features.chat.presentation.chat_detail

import com.example.chatx.core.domain.utils.UiText
import com.example.chatx.features.auth.domain.models.User
import com.example.chatx.features.chat.domain.models.ChatMessage

data class ChatDetailState(
    val isLoading: Boolean = false,
    val inputText: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val message: UiText? = null,
    val uiEvent: ChatDetailUiEvent? = null,
    val username: String = "",
    val typingUsers: List<String> = emptyList()
)
