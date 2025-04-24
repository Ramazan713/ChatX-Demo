package com.example.chatx.features.auth.data.api

import com.example.chatx.BuildConfig
import com.example.chatx.core.domain.utils.DefaultResult
import com.example.chatx.core.domain.utils.safeCall
import com.example.chatx.features.auth.data.dtos.AuthRequestDto
import com.example.chatx.features.auth.data.dtos.AuthResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class AuthApiImpl: AuthApi {

    private val BASE_URL = BuildConfig.CHAT_BASE_URL

    private val client = HttpClient(Android){
        install(ContentNegotiation){
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    override suspend fun login(username: String, password: String): DefaultResult<AuthResponseDto> {
        println("AppXXXX logni: $username::$password")
        return safeCall {
            client.post {
                contentType(ContentType.Application.Json)
                url("$BASE_URL/api/auth/login")
                setBody(AuthRequestDto(username, password))
            }.body<AuthResponseDto>()
        }
    }

    override suspend fun signUp(
        username: String,
        password: String
    ): DefaultResult<AuthResponseDto> {
        return safeCall {
            client.post {
                contentType(ContentType.Application.Json)
                url("$BASE_URL/api/auth/signUp")
                setBody(AuthRequestDto(username, password))
            }.body<AuthResponseDto>()
        }
    }
}