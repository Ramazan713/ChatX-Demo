package com.example.chatx.features.chat.data.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PageableMessagesDto(
    val messages: List<MessageDto>,
    val pageInfo: PageInfoDto
)
