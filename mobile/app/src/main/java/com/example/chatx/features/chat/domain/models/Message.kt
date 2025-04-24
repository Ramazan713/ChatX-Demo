package com.example.chatx.features.chat.domain.models

data class Message(
    val username: String,
    val message: String,
    val id: String? = null
)
