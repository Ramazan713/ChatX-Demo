package com.example.chatx.features.chat.data.dtos

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val id: String,
    val roomId: String,
    val username: String,
    val text: String,
    val createdAt: String,
    val tempId: String? = null,
    val readBy: List<String>
)
