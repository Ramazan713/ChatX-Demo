package com.example.chatx.features.auth.data.api

import com.example.chatx.BuildConfig
import com.example.chatx.core.domain.manager.SessionManager
import com.example.chatx.core.domain.utils.DefaultResult
import com.example.chatx.core.domain.utils.safeCall
import com.example.chatx.features.auth.data.dtos.AuthRequestDto
import com.example.chatx.features.auth.data.dtos.AuthResponseDto
import com.example.chatx.features.auth.data.dtos.AuthResponseWithTokenDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.cookies.cookies
import io.ktor.client.plugins.cookies.get
import io.ktor.client.request.cookie
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class AuthApiImpl(
    private val sessionManager: SessionManager,
): AuthApi {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        install(HttpCookies)
    }

    private val BASE_URL = BuildConfig.CHAT_BASE_URL

    override suspend fun login(username: String, password: String): DefaultResult<AuthResponseWithTokenDto> {
        return handleAuth {
            client.post {
                contentType(ContentType.Application.Json)
                url("$BASE_URL/api/auth/login")
                setBody(AuthRequestDto(username, password))
            }
        }
    }

    override suspend fun signUp(
        username: String,
        password: String
    ): DefaultResult<AuthResponseWithTokenDto> {
        return handleAuth {
            client.post {
                contentType(ContentType.Application.Json)
                url("$BASE_URL/api/auth/signUp")
                setBody(AuthRequestDto(username, password))
            }
        }
    }

    override suspend fun refresh(): DefaultResult<AuthResponseWithTokenDto> {
        return handleAuth {
            val token = sessionManager.getToken()!!
            client.post {
                cookie("refreshToken", token.refreshToken, httpOnly = true, path = "$BASE_URL/api/auth/refresh")
                contentType(ContentType.Application.Json)
                url("$BASE_URL/api/auth/refresh")
            }
        }
    }


    private suspend inline fun handleAuth(
        crossinline execute: suspend () -> HttpResponse,
    ): DefaultResult<AuthResponseWithTokenDto>{
        return safeCall {
            val response = execute()
            val result = response.body<AuthResponseDto>()

            val cookies = client.cookies(response.request.url.toString())
            val refreshToken = cookies["refreshToken"]?.value

            AuthResponseWithTokenDto(
                dto = result,
                refreshToken = refreshToken
            )
        }
    }
}