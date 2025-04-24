package com.example.chatx.features.home.di

import com.example.chatx.features.home.presentation.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module{
    viewModelOf(::HomeViewModel)
}