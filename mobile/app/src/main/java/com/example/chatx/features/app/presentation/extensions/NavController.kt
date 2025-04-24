package com.example.chatx.features.app.presentation.extensions

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.chatx.features.app.presentation.model.AppNavRoute


fun NavHostController.navigateToBar(barItem: AppNavRoute){
    navigate(barItem.route){
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}