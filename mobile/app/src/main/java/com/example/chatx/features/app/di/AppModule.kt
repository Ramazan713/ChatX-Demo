package com.example.chatx.features.app.di

import com.example.chatx.ChatApp
import com.example.chatx.features.app.presentation.AppViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<CoroutineScope> {
        (androidApplication() as ChatApp).applicationScope
    }
    viewModelOf(::AppViewModel)
}