package com.example.chatx.features.auth.data.services

import com.example.chatx.core.domain.manager.SessionManager
import com.example.chatx.core.domain.utils.DefaultResult
import com.example.chatx.core.domain.utils.Result
import com.example.chatx.core.domain.utils.UiText
import com.example.chatx.core.domain.utils.map
import com.example.chatx.features.auth.data.api.AuthApi
import com.example.chatx.features.auth.data.dtos.AuthResponseWithTokenDto
import com.example.chatx.features.auth.data.mappers.toUser
import com.example.chatx.features.auth.domain.models.TokenData
import com.example.chatx.features.auth.domain.models.User
import com.example.chatx.features.auth.domain.services.AuthService

class AuthServiceImpl(
    private val authApi: AuthApi,
    private val sessionManager: SessionManager
): AuthService {

    override suspend fun login(username: String, password: String): DefaultResult<User> {
        val response = authApi.login(username, password)
        val data = response.getSuccessData ?: return Result.errorWithUiText(response.getFailureError?.text ?: UiText.Text("Error"))
        saveSession(data)
        return response.map { data.dto.user.toUser() }
    }

    override suspend fun signUp(username: String, password: String): DefaultResult<User> {
        val response = authApi.signUp(username, password)
        val data = response.getSuccessData ?: return Result.errorWithUiText(response.getFailureError?.text ?: UiText.Text("Error"))
        saveSession(data)
        return response.map { data.dto.user.toUser() }
    }

    override suspend fun refresh(): DefaultResult<TokenData> {
        val response = authApi.refresh()
        val data = response.getSuccessData ?: return Result.errorWithUiText(response.getFailureError?.text ?: UiText.Text("Error"))
        saveSession(data)
        return response.map { data.toTokenData() }
    }

    private suspend fun saveSession(data: AuthResponseWithTokenDto){
        sessionManager.saveUser(data.dto.user.toUser())
        sessionManager.saveToken(data.toTokenData())
    }

    private fun AuthResponseWithTokenDto.toTokenData(): TokenData{
        return TokenData(
            token = dto.token,
            refreshToken = refreshToken ?: ""
        )
    }
}