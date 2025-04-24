package com.example.chatx.features.chat.data.mappers

import com.example.chatx.features.chat.data.dtos.MessageDto
import com.example.chatx.features.chat.domain.models.ChatMessage
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


fun MessageDto.toChatMessage(): ChatMessage {
    return ChatMessage(
        id = id,
        text = text,
        roomId = roomId,
        username = username,
        createdAt = Instant.parse(createdAt).toLocalDateTime(TimeZone.currentSystemDefault()),
        readBy = readBy,
        clientTempId = tempId
    )
}