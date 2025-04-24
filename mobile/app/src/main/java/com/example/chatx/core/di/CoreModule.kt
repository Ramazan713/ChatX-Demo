package com.example.chatx.core.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.chatx.core.data.manager.SessionManagerImpl
import com.example.chatx.core.domain.manager.SessionManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModule = module {
    single<SharedPreferences> {
        get<Application>().getSharedPreferences("app.preferences", MODE_PRIVATE)
    }

    singleOf(::SessionManagerImpl).bind<SessionManager>()
}