package com.example.chatx.features.chat.data.api

import com.example.chatx.BuildConfig
import com.example.chatx.core.domain.manager.SessionManager
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
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class ChatApiImpl(
    private val sessionManager: SessionManager
): ChatApi {
    private val BASE_URL=BuildConfig.CHAT_BASE_URL

    private val client = HttpClient(Android){
        install(ContentNegotiation){
            json(Json{
                ignoreUnknownKeys = true
            })
        }
        install(Auth){
            bearer {
                runBlocking {
                    sessionManager.getToken()
                }?.let { BearerTokens(it, null) }
            }
        }

        defaultRequest {
            val token = runBlocking { sessionManager.getToken() }
            headers {
                append(HttpHeaders.Authorization, "Bearer ${token}")
            }
            contentType(ContentType.Application.Json)
        }
    }

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

    override suspend fun getMessages(roomId: String): DefaultResult<List<ChatMessage>> {
        return safeCall {
            client.get {
                url("$BASE_URL/api/chat/messages/$roomId")
            }.body<List<MessageDto>>().map { it.toChatMessage() }
        }
    }

}