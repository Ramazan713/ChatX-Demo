package com.example.chatx.features.chat.data.mappers

import com.example.chatx.features.chat.data.dtos.MessagesWithRoomDto
import com.example.chatx.features.chat.domain.models.MessagesWithRoom

fun MessagesWithRoomDto.toModel(): MessagesWithRoom {
    return MessagesWithRoom(
        messages = messages.map { it.toChatMessage() },
        room = room.toChatRoom()
    )
}