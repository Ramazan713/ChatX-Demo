package com.example.chatx.features.chat.data.mappers

import com.example.chatx.features.chat.data.dtos.RoomDto
import com.example.chatx.features.chat.domain.models.ChatRoom
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


fun RoomDto.toChatRoom(): ChatRoom {
    return ChatRoom(
        id = id,
        name = name,
        isPublic = isPublic,
        leftAt = leftAt?.let { Instant.parse(it).toLocalDateTime(TimeZone.currentSystemDefault()) },
        joinedAt = Instant.parse(joinedAt).toLocalDateTime(TimeZone.currentSystemDefault()),
        updatedAt = Instant.parse(updatedAt).toLocalDateTime(TimeZone.currentSystemDefault()),
        muted = muted
    )
}