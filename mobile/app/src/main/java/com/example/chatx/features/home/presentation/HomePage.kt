package com.example.chatx.features.home.presentation

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.chatx.core.presentation.utils.EventHandler

@Composable
fun HomePageRoot(
    onNavigateToAuth: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomePage(
        state = state,
        onAction = viewModel::onAction,
        onNavigateToAuth = onNavigateToAuth
    )
}

@Composable
fun HomePage(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    onNavigateToAuth: () -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    EventHandler(
        state.uiEvent
    ) { uiEvent ->
        when(uiEvent){
            HomeUIEvent.SignOut -> onNavigateToAuth()
        }
        onAction(HomeAction.ClearUiEvent)
    }
    Scaffold(

    ) { paddings ->
        Column(
            modifier = Modifier
                .padding(paddings)
        ) {
            TextButton(
                onClick = {
                    onAction(HomeAction.SignOut)
                }
            ) {
                Text("Sign Out")
            }

            TextButton(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            ) {
                Text("Request notification permission")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomePreview(modifier: Modifier = Modifier) {
    HomePage(
        state = HomeState(),
        onAction = {},
        onNavigateToAuth = {}
    )
}