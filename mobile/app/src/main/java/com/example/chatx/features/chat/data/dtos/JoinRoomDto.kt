package com.example.chatx.features.chat.data.dtos

import kotlinx.serialization.Serializable

@Serializable
data class JoinRoomDto(
    val name: String,
    val isPublic: Boolean? = null
)
