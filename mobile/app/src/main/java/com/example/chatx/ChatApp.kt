package com.example.chatx

import android.app.Application
import com.example.chatx.core.di.coreModule
import com.example.chatx.features.auth.di.authModule
import com.example.chatx.features.chat.di.chatModule
import com.example.chatx.features.home.di.homeModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ChatApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ChatApp)
            modules(
                homeModule, chatModule, coreModule, authModule
            )
        }
    }
}