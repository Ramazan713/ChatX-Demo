package com.example.chatx.features.chat.domain.api

import com.example.chatx.core.domain.utils.DefaultResult
import com.example.chatx.features.chat.domain.models.ChatMessage
import com.example.chatx.features.chat.domain.models.ChatRoom
import kotlinx.datetime.Instant

interface ChatApi {

    suspend fun getPublicRooms(): DefaultResult<List<ChatRoom>>

    suspend fun joinRoom(roomName: String): DefaultResult<ChatRoom>

    suspend fun leftRoom(roomId: String): DefaultResult<ChatRoom>

    suspend fun getMessages(
        roomId: String,
        lastReceivedAt: Instant? = null,
        lastReceivedId: String? = null
    ): DefaultResult<List<ChatMessage>>
}