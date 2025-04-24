package com.example.chatx.features.home.presentation

sealed interface HomeUIEvent {

    data object SignOut: HomeUIEvent
}