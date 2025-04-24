package com.example.chatx.features.home.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.example.chatx.features.home.presentation.HomePageRoot
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavController.navigateToHome(navOptionsBuilder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(HomeRoute, navOptionsBuilder)
}

fun NavGraphBuilder.home(
    onNavigateToAuth: () -> Unit
) {
    composable<HomeRoute> {
        HomePageRoot(
            onNavigateToAuth = onNavigateToAuth
        )
    }
}