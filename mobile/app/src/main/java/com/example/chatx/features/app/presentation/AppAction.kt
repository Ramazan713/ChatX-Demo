package com.example.chatx.features.app.presentation

sealed interface AppAction {

    data object ClearUiEvent: AppAction
}