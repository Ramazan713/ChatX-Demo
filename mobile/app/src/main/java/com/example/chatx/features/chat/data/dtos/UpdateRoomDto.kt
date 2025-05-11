package com.example.chatx.features.chat.data.dtos

import kotlinx.serialization.Serializable

@Serializable
data class UpdateRoomDto(
    val muted: Boolean? = null,
    val left: Boolean? = null
)
