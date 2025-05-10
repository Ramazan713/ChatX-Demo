package com.example.chatx.features.chat.domain.api

import com.example.chatx.core.domain.utils.DefaultResult
import com.example.chatx.core.domain.utils.EmptyDefaultResult
import com.example.chatx.features.chat.domain.models.ChatMessage
import com.example.chatx.features.chat.domain.models.ChatRoom
import com.example.chatx.features.chat.domain.models.MessagesWithRoom
import kotlinx.datetime.Instant

interface ChatApi {

    suspend fun getPublicRooms(): DefaultResult<List<ChatRoom>>

    suspend fun joinRoom(roomName: String): DefaultResult<ChatRoom>

    suspend fun leftRoom(roomId: String): DefaultResult<ChatRoom>

    suspend fun muteRoom(roomId: String): DefaultResult<ChatRoom>

    suspend fun unMuteRoom(roomId: String): DefaultResult<ChatRoom>

    suspend fun deleteRoom(roomId: String): EmptyDefaultResult

    suspend fun getMessages(
        roomId: String,
        lastReceivedAt: Instant? = null,
        lastReceivedId: String? = null
    ): DefaultResult<List<ChatMessage>>

    suspend fun getMessagesWithRoom(
        roomId: String,
        lastReceivedAt: Instant? = null,
        lastReceivedId: String? = null
    ): DefaultResult<MessagesWithRoom>
}