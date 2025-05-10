package com.example.chatx.features.chat.domain.api

import com.example.chatx.core.domain.utils.UiText
import com.example.chatx.features.chat.domain.models.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatStreamApi {
    fun events(roomId: String): Flow<Event>

    fun reconnect()

    suspend fun sendMessage(roomId: String, message: String, tempId: String? = null)

    suspend fun typing(roomId: String)

    suspend fun stopTyping(roomId: String)

    suspend fun ackMessages(roomId: String, messageIds: List<String>)

    sealed interface Event {
        data class NewMessage(val message: ChatMessage): Event
        data class TypingUsers(val users: List<String>): Event
        data class ReadMessages(val messages: List<ChatMessage>): Event
        data class Error(val error: UiText): Event
        data class ConnectionStatus(val isConnected: Boolean): Event

        data class MessageValidationError(
            val error: String,
            val userMessage: String,
            val tempId: String?
        ): Event
    }
}