package com.example.chatx.core.features.devices.domain.services

import com.example.chatx.core.domain.utils.EmptyDefaultResult

interface DeviceTokenService {

    suspend fun registerDevice(token: String): EmptyDefaultResult

    suspend fun deleteDevice(token: String): EmptyDefaultResult
}