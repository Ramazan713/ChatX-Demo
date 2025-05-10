package com.example.chatx.features.chat.domain.models

data class MessagesWithRoom(
    val room: ChatRoom,
    val messages: List<ChatMessage>
)
