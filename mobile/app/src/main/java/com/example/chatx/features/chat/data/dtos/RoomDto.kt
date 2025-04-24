package com.example.chatx.features.chat.data.dtos

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable



@Serializable
data class RoomDto(
    val id: String,
    val isPublic: Boolean,
    val name: String,
    val leftAt: String?,
    val joinedAt: String,
    val updatedAt: String
)
