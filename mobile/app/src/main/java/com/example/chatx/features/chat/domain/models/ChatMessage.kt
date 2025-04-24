package com.example.chatx.features.chat.domain.models

import kotlinx.datetime.LocalDateTime

data class ChatMessage(
    val id: String,
    val roomId: String,
    val username: String,
    val text: String,
    val createdAt: LocalDateTime,
    val readBy: List<String>,
    val clientTempId: String? = null,
    val failed: Boolean = false,
    val pending: Boolean = false,
){
    fun getIsMine(currentUsername: String): Boolean {
        return username == currentUsername
    }
}
