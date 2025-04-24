package com.example.chatx.core.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController

@Composable
fun NavigationListener(
    navController: NavHostController,
    onDestinationChange: (destination: NavDestination) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnDestinationChange by rememberUpdatedState(onDestinationChange)

    DisposableEffect(lifecycleOwner, navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            currentOnDestinationChange(destination)
        }

        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }
}