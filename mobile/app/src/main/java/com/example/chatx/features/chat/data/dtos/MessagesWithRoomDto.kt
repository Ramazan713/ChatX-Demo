package com.example.chatx.features.chat.data.dtos

import kotlinx.serialization.Serializable

@Serializable
data class MessagesWithRoomDto(
    val messages: List<MessageDto>,
    val room: RoomDto
)
