package com.example.chatx.features.chat.data.api

import com.example.chatx.BuildConfig
import com.example.chatx.core.domain.utils.DefaultResult
import com.example.chatx.core.domain.utils.EmptyDefaultResult
import com.example.chatx.core.domain.utils.safeCall
import com.example.chatx.features.chat.data.dtos.JoinRoomDto
import com.example.chatx.features.chat.data.dtos.MessagesWithRoomDto
import com.example.chatx.features.chat.data.dtos.PageableMessagesDto
import com.example.chatx.features.chat.data.dtos.RoomDto
import com.example.chatx.features.chat.data.dtos.UpdateRoomDto
import com.example.chatx.features.chat.data.mappers.toChatRoom
import com.example.chatx.features.chat.data.mappers.toModel
import com.example.chatx.features.chat.domain.api.ChatApi
import com.example.chatx.features.chat.domain.models.ChatRoom
import com.example.chatx.features.chat.domain.models.MessagesWithRoom
import com.example.chatx.features.chat.domain.models.PageableMessages
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import kotlinx.datetime.Instant

class ChatApiImpl(
    private val client: HttpClient
): ChatApi {
    private val BASE_URL=BuildConfig.CHAT_BASE_URL

    override suspend fun getRooms(): DefaultResult<List<ChatRoom>> {
        return safeCall {
            client.get {
                url("${BASE_URL}/api/rooms")
            }.body<List<RoomDto>>().map { it.toChatRoom() }
        }
    }

    override suspend fun joinRoom(roomName: String): DefaultResult<ChatRoom> {
        return safeCall {
            client.post {
                url("$BASE_URL/api/rooms")
                setBody(JoinRoomDto(name = roomName, isPublic = true))
            }.body<RoomDto>().toChatRoom()
        }
    }

    override suspend fun leftRoom(roomId: String): DefaultResult<ChatRoom> {
        return safeCall {
            client.patch {
                url("$BASE_URL/api/rooms/$roomId")
                setBody(UpdateRoomDto(left=true))
            }.body<RoomDto>().toChatRoom()
        }
    }

    override suspend fun muteRoom(roomId: String): DefaultResult<ChatRoom> {
        return safeCall {
            client.patch {
                url("$BASE_URL/api/rooms/$roomId")
                setBody(UpdateRoomDto(muted=true))
            }.body<RoomDto>().toChatRoom()
        }
    }

    override suspend fun unMuteRoom(roomId: String): DefaultResult<ChatRoom> {
        return safeCall {
            client.patch {
                url("$BASE_URL/api/rooms/$roomId")
                setBody(UpdateRoomDto(muted=false))
            }.body<RoomDto>().toChatRoom()
        }
    }

    override suspend fun deleteRoom(roomId: String): EmptyDefaultResult {
        return safeCall {
            client.delete {
                url("$BASE_URL/api/rooms/$roomId")
            }.body<Unit>()
        }
    }

    override suspend fun getMessages(
        roomId: String,
        afterId: String?,
        beforeId: String?,
        limit: Int,
    ): DefaultResult<PageableMessages> {
        return safeCall {
            client.get {
                url("$BASE_URL/api/rooms/$roomId/messages")
                afterId?.let { parameter("afterId", it) }
                beforeId?.let { parameter("beforeId", it) }
                parameter("limit", limit)
            }.body<PageableMessagesDto>().toModel()
        }
    }

    override suspend fun getMessagesWithRoom(
        roomId: String,
        afterId: String?,
        beforeId: String?,
        limit: Int,
    ): DefaultResult<MessagesWithRoom> {
        return safeCall {
            client.get {
                url("$BASE_URL/api/rooms/$roomId")
                parameter("include", "messages")
                afterId?.let { parameter("afterId", it) }
                beforeId?.let { parameter("beforeId", it) }
                parameter("limit", limit)
            }.body<MessagesWithRoomDto>().toModel()
        }
    }

}