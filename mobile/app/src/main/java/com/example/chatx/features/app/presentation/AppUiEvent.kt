package com.example.chatx.features.app.presentation

sealed interface AppUiEvent {
    data object NavigateToAuth: AppUiEvent
}