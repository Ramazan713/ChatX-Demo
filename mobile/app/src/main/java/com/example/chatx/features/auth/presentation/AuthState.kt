package com.example.chatx.features.auth.presentation

import com.example.chatx.core.domain.utils.UiText

data class AuthState(
    val username: String = "",
    val password: String = "",
    val message: UiText? = null,
    val uiEvent: AuthUiEvent? = null
)
