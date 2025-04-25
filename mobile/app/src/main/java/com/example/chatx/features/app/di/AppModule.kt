package com.example.chatx.features.app.di

import com.example.chatx.ChatApp
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {
    single<CoroutineScope> {
        (androidApplication() as ChatApp).applicationScope
    }
}