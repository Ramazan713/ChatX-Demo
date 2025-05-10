package com.example.chatx.core.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.chatx.core.data.manager.SessionManagerImpl
import com.example.chatx.core.data.services.ConnectivityObserverImpl
import com.example.chatx.core.domain.manager.SessionManager
import com.example.chatx.core.domain.services.ConnectivityObserver
import com.example.chatx.features.auth.domain.services.AuthService
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


val coreModule = module {
    single<SharedPreferences> {
        get<Application>().getSharedPreferences("app.preferences", MODE_PRIVATE)
    }

    singleOf(::SessionManagerImpl).bind<SessionManager>()

    singleOf(::ConnectivityObserverImpl).bind<ConnectivityObserver>()

    factory {
        val sessionManager = get<SessionManager>()
        val authService = get<AuthService>()
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        runBlocking {
                            sessionManager.getToken()
                        }?.let { data ->
                            BearerTokens(data.token, data.refreshToken)
                        }
                    }
                    refreshTokens {
                        authService.refresh().getSuccessData?.let { data ->
                            BearerTokens(data.token, data.refreshToken)
                        }
                    }
                }
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }
}