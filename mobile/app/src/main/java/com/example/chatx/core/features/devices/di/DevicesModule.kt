package com.example.chatx.core.features.devices.di

import com.example.chatx.core.features.devices.data.manager.DeviceTokenManagerImpl
import com.example.chatx.core.features.devices.data.services.DeviceTokenServiceImpl
import com.example.chatx.core.features.devices.domain.manager.DeviceTokenManager
import com.example.chatx.core.features.devices.domain.services.DeviceTokenService
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val devicesModule = module {
    factoryOf(::DeviceTokenServiceImpl).bind<DeviceTokenService>()
    singleOf(::DeviceTokenManagerImpl).bind<DeviceTokenManager>()
}