package com.example.chatx.features.auth.presentation

sealed interface AuthAction {

    data class OnUsernameChange(val data: String): AuthAction

    data class OnPasswordChange(val data: String): AuthAction

    data object Login: AuthAction

    data object SignUp: AuthAction

    data object ClearMessage: AuthAction

    data object ClearUiEvent: AuthAction
}