package com.example.chatx.features.auth.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.example.chatx.features.auth.presentation.AuthPageRoot
import kotlinx.serialization.Serializable

@Serializable
data object AuthRoute

fun NavController.navigateToAuth(navOptionsBuilder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(AuthRoute, navOptionsBuilder)
}

fun NavGraphBuilder.auth(
    onNavigateSuccess: () -> Unit,
) {
    composable<AuthRoute> {
        AuthPageRoot(
            onNavigateSuccess = onNavigateSuccess
        )
    }
}