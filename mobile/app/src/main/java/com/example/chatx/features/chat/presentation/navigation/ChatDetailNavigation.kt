package com.example.chatx.features.chat.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.example.chatx.features.chat.presentation.chat_detail.ChatDetailPageRoot
import kotlinx.serialization.Serializable

@Serializable
data class ChatDetailRoute(
    val roomId: String
)

fun NavController.navigateToChatDetail(roomId: String) {
    navigate(ChatDetailRoute(roomId))
}

fun NavGraphBuilder.chatDetail(
    onNavigateBack: () -> Unit
) {
    composable<ChatDetailRoute>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "chatx://chat/{roomId}"
            }
        )
    ) {
        ChatDetailPageRoot(
            onNavigateBack = onNavigateBack
        )
    }
}
