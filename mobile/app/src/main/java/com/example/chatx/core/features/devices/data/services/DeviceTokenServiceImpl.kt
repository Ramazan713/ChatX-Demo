package com.example.chatx.core.features.devices.data.services

import com.example.chatx.BuildConfig
import com.example.chatx.core.domain.manager.SessionManager
import com.example.chatx.core.domain.utils.EmptyDefaultResult
import com.example.chatx.core.domain.utils.safeCall
import com.example.chatx.core.features.devices.data.dtos.RegisterDeviceRequestDto
import com.example.chatx.core.features.devices.domain.services.DeviceTokenService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.delete
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class DeviceTokenServiceImpl(
    private val sessionManager: SessionManager
): DeviceTokenService {

    private val client = HttpClient(Android){
        install(ContentNegotiation){
            json(Json {
                ignoreUnknownKeys = true
            })
        }

        defaultRequest {
            headers {
                runBlocking {
                    sessionManager.getToken()?.token
                }?.let { token ->
                    set("Authorization", "Bearer $token")
                }
            }
            contentType(ContentType.Application.Json)
        }
    }

    override suspend fun registerDevice(token: String): EmptyDefaultResult {
        return safeCall {
            client.post {
                url("${BuildConfig.CHAT_BASE_URL}/api/devices")
                setBody(RegisterDeviceRequestDto(token, "android"))
            }.body()
        }
    }

    override suspend fun deleteDevice(token: String): EmptyDefaultResult {
        return safeCall {
            client.delete {
                url("${BuildConfig.CHAT_BASE_URL}/api/devices/$token")
            }.body()
        }
    }
}