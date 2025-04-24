package com.example.chatx.features.home.presentation

sealed interface HomeAction {

    data object ClearUiEvent: HomeAction

    data object SignOut: HomeAction
}