package com.example.chatx.features.chat.presentation.chat_list

import com.example.chatx.core.domain.utils.UiText
import com.example.chatx.features.chat.domain.models.ChatRoom

data class ChatListState(
    val isLoading: Boolean = false,
    val rooms: List<ChatRoom> = emptyList(),
    val message: UiText? = null
)
