package com.example.chatx.features.app.presentation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.chatx.core.domain.utils.UiText
import com.example.chatx.features.chat.presentation.navigation.ChatListRoute
import com.example.chatx.features.home.presentation.navigation.HomeRoute

sealed class AppNavRoute(
    val route: Any,
    val selectedIconVector: ImageVector,
    val unSelectedIconVector: ImageVector,
    val title: UiText
) {

    data object Home: AppNavRoute(
        route = HomeRoute,
        selectedIconVector = Icons.Filled.Home,
        unSelectedIconVector = Icons.Outlined.Home,
        title = UiText.Text("Home")
    )

    data object Chat: AppNavRoute(
        route = ChatListRoute,
        selectedIconVector = Icons.Filled.Chat,
        unSelectedIconVector = Icons.Outlined.Chat,
        title = UiText.Text("Chat")
    )


    fun getCurrentIconVector(selected: Boolean): ImageVector{
        return if(selected) selectedIconVector else unSelectedIconVector
    }

}

val kBottomBarRoutes = listOf(
    AppNavRoute.Home, AppNavRoute.Chat
)