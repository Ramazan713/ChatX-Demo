package com.example.chatx.features.chat.data.api

import com.example.chatx.BuildConfig
import com.example.chatx.core.domain.manager.SessionManager
import com.example.chatx.core.domain.utils.UiText
import com.example.chatx.features.auth.domain.services.AuthService
import com.example.chatx.features.chat.data.dtos.MessageDto
import com.example.chatx.features.chat.data.mappers.toChatMessage
import com.example.chatx.features.chat.domain.api.ChatStreamApi
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.Polling
import io.socket.engineio.client.transports.WebSocket
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject

private inline fun <reified T> Json.decodeSocket(arg: Any?): T =
    decodeFromString(arg?.toString() ?: error("Missing payload"))

class ChatStreamApiSocketIO(
    private val sessionManager: SessionManager,
    private val authService: AuthService
): ChatStreamApi {
    private var socket: Socket? = null
    private var username = ""

    private val json = Json {
        ignoreUnknownKeys = true
    }

    override fun events(roomId: String): Flow<ChatStreamApi.Event> {
        return callbackFlow {
            var attempts = 0
            var delayMillis = 1000L

            suspend fun doConnect() {
                socket?.disconnect();
                socket?.off()
                val socket = createSocket()
                socket.on(Socket.EVENT_CONNECT) {
                    trySend(ChatStreamApi.Event.ConnectionStatus(true))
                    socket.emit("join room", JSONObject().apply {
                        put("roomId", roomId)
                    })
                }
                socket.on(Socket.EVENT_DISCONNECT){
                    trySend(ChatStreamApi.Event.ConnectionStatus(false))
                }

                registerMessageEvents(socket)
                registerTypingEvents(socket)
                registerReadMessagesEvents(socket)
                registerValidationErrorEvents(socket)
                registerConnectErrorEvents(socket) {
                    launch {
                        delay(delayMillis)
                        doConnect()
                        attempts++
                        delayMillis *= 2
                        if(attempts == 3){
                            trySend(ChatStreamApi.Event.Error(UiText.Text("Connection Error")))
                        }
                    }
                }
                this@ChatStreamApiSocketIO.socket = socket
                socket.connect()
            }

            doConnect()

            awaitClose {
                socket?.disconnect()
                socket?.off()
                this@ChatStreamApiSocketIO.socket = null
            }
        }
    }

    override fun reconnect() {
        socket?.disconnect()
        socket?.connect()
    }

    private suspend fun createSocket(): Socket {
        val token = sessionManager.getToken()?.token
        username = sessionManager.getUser()?.username ?: ""
        val opts = IO.Options().apply {
            transports = arrayOf(WebSocket.NAME, Polling.NAME)
            reconnection = true
            extraHeaders = mapOf("Authorization" to listOf("Bearer $token"))
        }
        return IO.socket("${BuildConfig.CHAT_BASE_URL}/chat", opts)
    }


    override suspend fun sendMessage(roomId: String, message: String, tempId: String?) {
        socket?.emit("room message", JSONObject().apply {
            put("roomId", roomId)
            put("message", message)
            put("tempId", tempId)
        })
    }

    override suspend fun typing(roomId: String) {
        socket?.emit("typing", JSONObject().apply {
            put("roomId", roomId)
        })
    }

    override suspend fun stopTyping(roomId: String) {
        socket?.emit("stop typing", JSONObject().apply {
            put("roomId", roomId)
        })
    }

    override suspend fun ackMessages(roomId: String, messageIds: List<String>) {
        if(messageIds.isEmpty()) return
        socket?.emit("read messages", JSONObject().apply {
            put("roomId", roomId)
            put("messageIds", JSONArray().apply { messageIds.forEach { put(it) } })
        })
    }

    private fun ProducerScope<ChatStreamApi.Event>.registerMessageEvents(socket: Socket) {
        socket.on("room message") { args ->
            val dto = json.decodeSocket<MessageDto>(args[0])
            trySend(ChatStreamApi.Event.NewMessage(dto.toChatMessage()))
        }
    }

    private fun ProducerScope<ChatStreamApi.Event>.registerTypingEvents(socket: Socket) {
        socket.on("typing") { args ->
            val raw = args[0].toString()
            val jsonArray = JSONObject(raw).getJSONArray("usernames")
            val users = (0 until jsonArray.length()).mapNotNull { i ->
                jsonArray.getString(i)?.let { if (it == username) null else it }
            }
            trySend(ChatStreamApi.Event.TypingUsers(users))
        }
    }

    private fun ProducerScope<ChatStreamApi.Event>.registerReadMessagesEvents(socket: Socket){
        socket.on("read messages") { args ->
            val rawJson = args.getOrNull(0)?.toString() ?: return@on
            val dtoList = json.decodeFromString<List<MessageDto>>(rawJson)
            trySend(ChatStreamApi.Event.ReadMessages(dtoList.map { it.toChatMessage() } ))
        }
    }

    private fun ProducerScope<ChatStreamApi.Event>.registerValidationErrorEvents(socket: Socket){
        socket.on("validation error") { args ->
            val raw = args.getOrNull(0)?.toString() ?: return@on
            try{
                val json = JSONObject(raw)
                val event = json.optString("event")
                val error = json.optJSONArray("errors")?.optJSONObject(0)?.getString("message") ?: return@on
                if(event == "room message"){
                    val payload = json.optJSONObject("payload")
                    val tempId = payload?.optString("tempId")
                    val userMessage = payload?.optString("message") ?: ""
                    trySend(ChatStreamApi.Event.MessageValidationError(error, userMessage, tempId))
                }else{
                    trySend(ChatStreamApi.Event.Error(UiText.Text(error)))
                }
            }catch (e: Exception){
                println("AppXXXX e: $e")
            }

        }
    }

    private fun ProducerScope<ChatStreamApi.Event>.registerConnectErrorEvents(socket: Socket, reconnect: () -> Unit){
        socket.on(Socket.EVENT_CONNECT_ERROR) { args ->
            trySend(ChatStreamApi.Event.ConnectionStatus(false))
            val errorRaw = args.getOrNull(0)?.toString() ?: "{}"
            val errorData = try {
                JSONObject(errorRaw)
            } catch (e: Exception) {
                JSONObject()
            }

            when (errorData.optString("message", "unknown")) {
                "token_expired", "invalid_token" -> {
                    launch {
                        val success = authService.refresh()
                        success.onFailure { err ->
                            trySend(ChatStreamApi.Event.Error(err.text))
                        }
                        reconnect()
                    }
                }
            }

            println("AppXXXX EVENT_CONNECT_ERROR: $errorData")
        }
    }
}