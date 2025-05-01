package com.example.chatx.features.auth.data.dtos

data class AuthResponseWithTokenDto(
    val dto: AuthResponseDto,
    val refreshToken: String?
)
