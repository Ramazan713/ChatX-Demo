package com.example.chatx.core.data.manager

import android.content.SharedPreferences
import com.example.chatx.core.domain.manager.SessionManager
import com.example.chatx.features.auth.domain.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
private data class UserSaver(
    val id: String,
    val username: String
)


class SessionManagerImpl(
    private val sharedPreferences: SharedPreferences
): SessionManager {

    override suspend fun saveToken(token: String) {
        withContext(Dispatchers.IO){
            sharedPreferences.edit().putString(AUTH_TOKEN, token).apply()
        }
    }

    override suspend fun getToken(): String? {
        return withContext(Dispatchers.IO){
            sharedPreferences.getString(AUTH_TOKEN, null)
        }
    }

    override suspend fun saveUser(user: User) {
        withContext(Dispatchers.IO){
            val userSaver = UserSaver(
                id = user.id,
                username = user.username
            )
            sharedPreferences.edit().putString(AUTH_USER, Json.encodeToString(userSaver)).apply()
        }
    }

    override suspend fun getUser(): User? {
        try{
            return withContext(Dispatchers.IO){
                val userData = sharedPreferences.getString(AUTH_USER, null) ?: return@withContext null
                val userSaver = Json.decodeFromString<UserSaver>(userData)
                User(
                    id = userSaver.id,
                    username = userSaver.username
                )
            }
        }catch (e: Exception){
            return null
        }
    }

    override suspend fun signOut() {
        sharedPreferences.edit().putString(AUTH_USER, null).apply()
        sharedPreferences.edit().putString(AUTH_TOKEN, null).apply()
    }

    companion object {
        private const val AUTH_USER = "AUTH_USER"
        private const val AUTH_TOKEN = "AUTH_TOKEN"
    }
}