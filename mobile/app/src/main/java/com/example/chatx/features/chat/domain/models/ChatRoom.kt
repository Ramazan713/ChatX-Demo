package com.example.chatx.features.chat.domain.models

import kotlinx.datetime.LocalDateTime

data class ChatRoom(
    val id: String,
    val isPublic: Boolean,
    val name: String,
    val leftAt: LocalDateTime?,
    val joinedAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
