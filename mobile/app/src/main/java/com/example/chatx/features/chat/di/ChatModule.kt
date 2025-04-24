package com.example.chatx.features.chat.di

import com.example.chatx.features.chat.data.api.ChatApiImpl
import com.example.chatx.features.chat.data.api.ChatStreamApiSocketIO
import com.example.chatx.features.chat.domain.api.ChatApi
import com.example.chatx.features.chat.domain.api.ChatStreamApi
import com.example.chatx.features.chat.presentation.chat_detail.ChatDetailViewModel
import com.example.chatx.features.chat.presentation.chat_list.ChatListViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val chatModule = module {
    factoryOf(::ChatApiImpl).bind<ChatApi>()
    factoryOf(::ChatStreamApiSocketIO).bind<ChatStreamApi>()

    viewModelOf(::ChatListViewModel)
    viewModelOf(::ChatDetailViewModel)
}