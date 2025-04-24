package com.example.chatx.features.auth.presentation

sealed interface AuthUiEvent {

    data object NavigateToSuccess: AuthUiEvent
}