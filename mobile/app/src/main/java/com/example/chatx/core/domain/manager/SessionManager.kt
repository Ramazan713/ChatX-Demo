package com.example.chatx.core.domain.manager

import com.example.chatx.features.auth.domain.models.TokenData
import com.example.chatx.features.auth.domain.models.User

interface SessionManager {

    suspend fun saveToken(token: TokenData)

    suspend fun getToken(): TokenData?

    suspend fun saveUser(user: User)

    suspend fun getUser(): User?

    suspend fun signOut()
}