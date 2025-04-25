package com.example.chatx.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatx.core.domain.manager.SessionManager
import com.example.chatx.core.features.devices.domain.manager.DeviceTokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class HomeViewModel(
    private val sessionManager: SessionManager,
    private val deviceTokenManager: DeviceTokenManager
): ViewModel(){

    private val _state = MutableStateFlow(HomeState())
    val state get() = _state.asStateFlow()

    fun onAction(action: HomeAction){
        when(action){
            HomeAction.ClearUiEvent -> {
                _state.update { it.copy(
                    uiEvent = null
                ) }
            }
            HomeAction.SignOut -> {
                viewModelScope.launch {
                    deviceTokenManager.logOut()
                    sessionManager.signOut()
                    _state.update { it.copy(
                        uiEvent = HomeUIEvent.SignOut
                    ) }
                }
            }
        }
    }

}
