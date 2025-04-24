package com.example.chatx.features.app.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.chatx.features.app.presentation.extensions.navigateToBar
import com.example.chatx.features.app.presentation.model.kBottomBarRoutes
import com.example.chatx.features.auth.domain.models.User


@OptIn(
    ExperimentalSharedTransitionApi::class
)
@Composable
fun MyApp(
    user: User?,
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController()
) {
    val backStackEntry by navHostController.currentBackStackEntryAsState()

    val currentDestination by remember(backStackEntry) {
        derivedStateOf {
            backStackEntry?.destination
        }
    }

    val currentAppNavRoute by remember(currentDestination) {
        derivedStateOf {
            kBottomBarRoutes.find { barRoute ->
                currentDestination?.hierarchy?.any { it.hasRoute(barRoute.route::class) } != false
            }
        }
    }

    val showNavigationBar by remember(currentAppNavRoute) {
        derivedStateOf {
            currentAppNavRoute != null
        }
    }

    Column(
        modifier = modifier
    ) {
        Scaffold(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            bottomBar = {
                AnimatedVisibility(visible = showNavigationBar) {
                    BottomAppBar {
                        kBottomBarRoutes.forEach { barItem ->
                            val selected = currentAppNavRoute == barItem
                            NavigationBarItem(
                                selected = selected,
                                label = {
                                    Text(text = barItem.title.asString())
                                },
                                onClick = {
                                    navHostController.navigateToBar(barItem)
                                },
                                icon = {
                                    Icon(
                                        imageVector = barItem.getCurrentIconVector(selected),
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
            },
        ) { paddings ->
            AppNavHost(
                isAuthenticated = user != null,
                navHostController = navHostController,
                modifier = Modifier
                    .padding(paddings)
            )
        }
    }
}