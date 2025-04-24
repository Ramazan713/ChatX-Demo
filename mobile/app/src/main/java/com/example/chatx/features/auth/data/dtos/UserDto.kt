package com.example.chatx.features.auth.data.dtos

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val username: String
)
