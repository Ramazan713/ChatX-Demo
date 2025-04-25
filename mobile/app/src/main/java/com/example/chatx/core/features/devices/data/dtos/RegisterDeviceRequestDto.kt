package com.example.chatx.core.features.devices.data.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RegisterDeviceRequestDto(
    val token: String,
    val platform: String
)
