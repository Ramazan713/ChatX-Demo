package com.example.chatx.features.chat.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.Navigator
import androidx.navigation.compose.composable
import com.example.chatx.features.chat.presentation.chat_list.ChatListPageRoot
import kotlinx.serialization.Serializable

@Serializable
data object ChatListRoute

fun NavController.navigateToChatList(navOptionsBuilder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(ChatListRoute, navOptionsBuilder)
}

fun NavGraphBuilder.chatList(
    onNavigateToChatDetail: (String) -> Unit,
) {
    composable<ChatListRoute> {
        ChatListPageRoot(
            onNavigateToChatDetail = onNavigateToChatDetail
        )
    }
}