package com.example.chatx.core.features.devices.domain.manager

import com.example.chatx.core.domain.utils.EmptyDefaultResult

interface DeviceTokenManager {

    suspend fun registerDevice(token: String): EmptyDefaultResult

    suspend fun setToken(): EmptyDefaultResult

    suspend fun logOut(): EmptyDefaultResult

    suspend fun checkToken()
}