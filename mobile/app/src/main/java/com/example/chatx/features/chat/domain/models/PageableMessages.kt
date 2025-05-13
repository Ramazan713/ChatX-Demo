package com.example.chatx.features.chat.domain.models

data class PageableMessages(
    val messages: List<ChatMessage>,
    val pageInfo: PageInfo
)
