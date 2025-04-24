package com.example.chatx.features.app.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import com.example.chatx.features.app.presentation.extensions.navigateToBar
import com.example.chatx.features.app.presentation.model.AppNavRoute
import com.example.chatx.features.auth.presentation.navigation.AuthRoute
import com.example.chatx.features.auth.presentation.navigation.auth
import com.example.chatx.features.auth.presentation.navigation.navigateToAuth
import com.example.chatx.features.chat.presentation.navigation.ChatListRoute
import com.example.chatx.features.chat.presentation.navigation.chatDetail
import com.example.chatx.features.chat.presentation.navigation.chatList
import com.example.chatx.features.chat.presentation.navigation.navigateToChatDetail
import com.example.chatx.features.chat.presentation.navigation.navigateToChatList
import com.example.chatx.features.home.presentation.navigation.HomeRoute
import com.example.chatx.features.home.presentation.navigation.home

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun AppNavHost(
    isAuthenticated: Boolean,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        modifier = modifier,
        navController = navHostController,
        startDestination = if(isAuthenticated) ChatListRoute else AuthRoute
    ){
        home(
            onNavigateToAuth = {
                navHostController.navigateToAuth(
                    navOptionsBuilder = {
                        popUpTo(navHostController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                )
            }
        )

        auth(
            onNavigateSuccess = {
                navHostController.navigateToChatList(
                    navOptionsBuilder = {
                        popUpTo(navHostController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                )
            }
        )

        chatList(
            onNavigateToChatDetail = {
                navHostController.navigateToChatDetail(it)
            }
        )

        chatDetail(
            onNavigateBack = {
                navHostController.navigateUp()
            }
        )
    }
}