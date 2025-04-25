package com.example.chatx.core.features.devices.data.manager

import android.content.SharedPreferences
import com.example.chatx.core.domain.utils.EmptyDefaultResult
import com.example.chatx.core.domain.utils.EmptyResult
import com.example.chatx.core.features.devices.domain.manager.DeviceTokenManager
import com.example.chatx.core.features.devices.domain.services.DeviceTokenService
import com.example.chatx.features.auth.domain.models.User
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DeviceTokenManagerImpl(
    private val deviceTokenService: DeviceTokenService,
    private val sharedPreferences: SharedPreferences
): DeviceTokenManager {

    override suspend fun registerDevice(token: String): EmptyDefaultResult {
        return executeRegisterToken(token)
    }

    override suspend fun setToken(): EmptyDefaultResult {
        val token = Firebase.messaging.token.await()
        return executeRegisterToken(token)
    }

    override suspend fun logOut(): EmptyDefaultResult {
        return withContext(NonCancellable){
            val token = sharedPreferences.getString(TOKEN_KEY, null)
                ?: return@withContext EmptyResult.errorWithText("token not found")
            val response = deviceTokenService.deleteDevice(token)
            sharedPreferences.edit().putString(TOKEN_KEY, null).apply()
            response
        }
    }

    override suspend fun checkToken() {
        val token = sharedPreferences.getString(TOKEN_KEY, null)
        if(token != null) return
        setToken()
    }



    private suspend fun executeRegisterToken(token: String): EmptyDefaultResult {
        withContext(Dispatchers.IO){
            sharedPreferences.edit().putString(TOKEN_KEY, token).apply()
        }
        return deviceTokenService.registerDevice(token)
    }


    companion object {
        const val TOKEN_KEY = "FCM_TOKEN"
    }
}