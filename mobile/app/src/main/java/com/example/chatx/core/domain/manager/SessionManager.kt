package com.example.chatx.core.domain.manager

import com.example.chatx.features.auth.domain.models.TokenData
import com.example.chatx.features.auth.domain.models.User
import kotlinx.coroutines.flow.Flow

interface SessionManager {

    val events: Flow<Event>

    suspend fun setSessionExpired()

    suspend fun saveToken(token: TokenData)

    suspend fun getToken(): TokenData?

    suspend fun saveUser(user: User)

    suspend fun getUser(): User?

    suspend fun signOut()

    sealed interface Event {
        data object SessionExpired: Event
    }
}