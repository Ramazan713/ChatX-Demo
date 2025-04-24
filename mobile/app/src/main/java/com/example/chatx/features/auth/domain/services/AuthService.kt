package com.example.chatx.features.auth.domain.services

import com.example.chatx.core.domain.utils.DefaultResult
import com.example.chatx.features.auth.domain.models.User

interface AuthService {

    suspend fun login(username: String, password: String): DefaultResult<User>

    suspend fun signUp(username: String, password: String): DefaultResult<User>
}