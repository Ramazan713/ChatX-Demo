package com.example.chatx.features.auth.data.api

import com.example.chatx.core.domain.utils.DefaultResult
import com.example.chatx.features.auth.data.dtos.AuthResponseDto

interface AuthApi {

    suspend fun login(username: String, password: String): DefaultResult<AuthResponseDto>

    suspend fun signUp(username: String, password: String): DefaultResult<AuthResponseDto>
}