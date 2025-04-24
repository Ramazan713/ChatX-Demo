package com.example.chatx.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatx.features.auth.domain.services.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authService: AuthService
): ViewModel(){

    private val _state = MutableStateFlow(AuthState())
    val state get() = _state.asStateFlow()

    fun onAction(action: AuthAction){
        when(action){
            is AuthAction.OnPasswordChange -> {
                _state.update { it.copy(
                    password = action.data
                ) }
            }
            is AuthAction.OnUsernameChange -> {
                _state.update { it.copy(
                    username = action.data
                ) }
            }
            AuthAction.Login -> {
                viewModelScope.launch {
                    val username = _state.value.username
                    val password = _state.value.password
                    val response = authService.login(username, password)
                    response.onFailure { error ->
                        _state.update { it.copy(
                            message = error.text
                        ) }
                    }
                    response.onSuccess {
                        _state.update { it.copy(
                            uiEvent = AuthUiEvent.NavigateToSuccess
                        ) }
                    }
                }
            }
            AuthAction.SignUp -> {
                viewModelScope.launch {
                    val username = _state.value.username
                    val password = _state.value.password
                    val response = authService.signUp(username, password)
                    response.onFailure { error ->
                        _state.update { it.copy(
                            message = error.text
                        ) }
                    }
                    response.onSuccess {
                        _state.update { it.copy(
                            uiEvent = AuthUiEvent.NavigateToSuccess
                        ) }
                    }
                }
            }

            AuthAction.ClearMessage -> {
                _state.update { it.copy(
                    message = null
                ) }
            }
            AuthAction.ClearUiEvent -> {
                _state.update { it.copy(
                    uiEvent = null
                ) }
            }
        }
    }

}
