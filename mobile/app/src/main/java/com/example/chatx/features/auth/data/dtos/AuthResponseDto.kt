package com.example.chatx.features.auth.data.dtos

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseDto(
    val user: UserDto,
    val token: String
)
