package com.example.chatx.features.auth.domain.models

data class TokenData(
    val token: String,
    val refreshToken: String
)
