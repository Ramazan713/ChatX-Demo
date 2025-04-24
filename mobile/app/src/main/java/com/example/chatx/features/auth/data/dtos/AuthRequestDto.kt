package com.example.chatx.features.auth.data.dtos

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequestDto(
    val username: String,
    val password: String
)
