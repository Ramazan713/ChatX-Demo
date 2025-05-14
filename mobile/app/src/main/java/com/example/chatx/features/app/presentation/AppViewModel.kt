package com.example.chatx.features.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatx.core.domain.manager.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class AppViewModel(
    private val sessionManager: SessionManager
): ViewModel(){

    private val _state = MutableStateFlow(AppState())
    val state get() = _state.asStateFlow()

    init {
        sessionManager
            .events
            .onEach { event ->
                when(event){
                    SessionManager.Event.SessionExpired -> {
                        _state.update { it.copy(
                            uiEvent = AppUiEvent.NavigateToAuth
                        ) }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: AppAction){
        when(action){
            AppAction.ClearUiEvent -> {
                _state.update { it.copy(
                    uiEvent = null
                ) }
            }
        }
    }

}
