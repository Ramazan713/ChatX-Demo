package com.example.chatx.features.auth.data.api

import com.example.chatx.core.domain.utils.DefaultResult
import com.example.chatx.features.auth.data.dtos.AuthResponseDto
import com.example.chatx.features.auth.data.dtos.AuthResponseWithTokenDto

interface AuthApi {

    suspend fun login(username: String, password: String): DefaultResult<AuthResponseWithTokenDto>

    suspend fun signUp(username: String, password: String): DefaultResult<AuthResponseWithTokenDto>

    suspend fun refresh(): DefaultResult<AuthResponseWithTokenDto>
}