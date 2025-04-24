package com.example.chatx.features.auth.di

import com.example.chatx.features.auth.data.api.AuthApi
import com.example.chatx.features.auth.data.api.AuthApiImpl
import com.example.chatx.features.auth.data.services.AuthServiceImpl
import com.example.chatx.features.auth.domain.services.AuthService
import com.example.chatx.features.auth.presentation.AuthViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


val authModule = module {
    singleOf(::AuthApiImpl).bind<AuthApi>()
    singleOf(::AuthServiceImpl).bind<AuthService>()

    viewModelOf(::AuthViewModel)
}