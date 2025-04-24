package com.example.chatx.features.home.presentation

data class HomeState(
    val isLoading: Boolean = false,
    val uiEvent: HomeUIEvent? = null
)
