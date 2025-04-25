package com.example.chatx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.chatx.core.domain.manager.SessionManager
import com.example.chatx.core.features.devices.domain.manager.DeviceTokenManager
import com.example.chatx.features.app.presentation.MyApp
import com.example.chatx.ui.theme.ChatXTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val sessionManager by inject<SessionManager>()
    private val deviceTokenManager by inject<DeviceTokenManager>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val user = runBlocking {
            sessionManager.getUser()
        }

        lifecycleScope.launch {
            deviceTokenManager.checkToken()
        }

        setContent {
            ChatXTheme {
                MyApp(
                    user = user
                )
            }
        }
    }
}

