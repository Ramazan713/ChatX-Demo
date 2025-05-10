package com.example.chatx.features.chat.data.api

import com.example.chatx.BuildConfig
import com.example.chatx.core.domain.utils.DefaultResult
import com.example.chatx.core.domain.utils.safeCall
import com.example.chatx.features.chat.data.dtos.JoinRoomDto
import com.example.chatx.features.chat.data.dtos.MessageDto
import com.example.chatx.features.chat.data.dtos.RoomDto
import com.example.chatx.features.chat.data.mappers.toChatMessage
import com.example.chatx.features.chat.data.mappers.toChatRoom
import com.example.chatx.features.chat.domain.api.ChatApi
import com.example.chatx.features.chat.domain.models.ChatMessage
import com.example.chatx.features.chat.domain.models.ChatRoom
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import kotlinx.datetime.Instant

class ChatApiImpl(
    private val client: HttpClient
): ChatApi {
    private val BASE_URL=BuildConfig.CHAT_BASE_URL

    override suspend fun getPublicRooms(): DefaultResult<List<ChatRoom>> {
        return safeCall {
            client.get {
                url("${BASE_URL}/api/chat/rooms")
            }.body<List<RoomDto>>().map { it.toChatRoom() }
        }
    }

    override suspend fun joinRoom(roomName: String): DefaultResult<ChatRoom> {
        return safeCall {
            client.post {
                url("$BASE_URL/api/chat/join")
                setBody(JoinRoomDto(name = roomName, isPublic = true))
            }.body<RoomDto>().toChatRoom()
        }
    }

    override suspend fun leftRoom(roomId: String): DefaultResult<ChatRoom> {
        return safeCall {
            client.post {
                url("$BASE_URL/api/chat/leave/$roomId")
            }.body<RoomDto>().toChatRoom()
        }
    }

    override suspend fun getMessages(roomId: String, lastReceivedAt: Instant?, lastReceivedId: String?): DefaultResult<List<ChatMessage>> {
        return safeCall {
            client.get {
                url("$BASE_URL/api/chat/messages/$roomId")
                lastReceivedAt?.let { parameter("since", lastReceivedAt) }
                lastReceivedId?.let { parameter("afterId", lastReceivedId) }
            }.body<List<MessageDto>>().map { it.toChatMessage() }
        }
    }

}