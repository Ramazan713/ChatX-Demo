package com.example.chatx.core.domain.services

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {

    val isConnected: Flow<Boolean>

    fun hasConnection(): Boolean
}