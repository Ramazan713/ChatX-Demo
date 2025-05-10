package com.example.chatx.features.chat.presentation.chat_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatx.core.domain.utils.UiText
import com.example.chatx.features.chat.domain.api.ChatApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatListViewModel(
    private val chatApi: ChatApi
): ViewModel(){

    private val _state = MutableStateFlow(ChatListState())
    val state get() = _state.asStateFlow()

    init {
        loadRooms()
    }

    fun onAction(action: ChatListAction){
        when(action){
            is ChatListAction.JoinRoom -> {
                viewModelScope.launch {
                    val response = chatApi.joinRoom(action.roomName)
                    response.onFailure { error ->
                        _state.update { it.copy(
                            message = error.text
                        ) }
                    }
                    response.onSuccess { data ->
                        _state.update {
                            val newRooms = it.rooms.toMutableList()
                            newRooms.add(0, data)
                            it.copy(
                                rooms = newRooms.toSet().toList()
                            )
                        }
                    }
                }
            }

            ChatListAction.ClearMessage -> {
                _state.update { it.copy(message = null) }
            }

            is ChatListAction.Leave -> {
                viewModelScope.launch {
                    val response = chatApi.leftRoom(action.roomId)
                    response.onFailure { error ->
                        _state.update { it.copy(
                            message = error.text
                        ) }
                    }
                    response.onSuccess { data ->
                        _state.update {
                            val newRooms = it.rooms.toMutableList()
                            newRooms.indexOfFirst { it.id == data.id }.let { index ->
                                if(index != -1){
                                    newRooms[index] = data
                                }
                            }
                            it.copy(
                                rooms = newRooms,
                                message = UiText.Text("success")
                            )
                        }
                    }
                }
            }

            is ChatListAction.ShowDialog -> {
                _state.update { it.copy(
                    dialogEvent = action.dialogEvent
                ) }
            }

            is ChatListAction.Delete -> {
                viewModelScope.launch {
                    val response = chatApi.deleteRoom(action.roomId)
                    response.onFailure { error ->
                        _state.update { it.copy(
                            message = error.text
                        ) }
                    }
                    response.onSuccess {
                        _state.update {
                            val newRooms = it.rooms.toMutableList()
                            newRooms.removeIf { it.id == action.roomId }
                            it.copy(
                                rooms = newRooms,
                                message = UiText.Text("success")
                            )
                        }
                    }
                }
            }
            is ChatListAction.Mute -> {
                viewModelScope.launch {
                    val response = chatApi.muteRoom(action.roomId)
                    response.onFailure { error ->
                        _state.update { it.copy(
                            message = error.text
                        ) }
                    }
                    response.onSuccess { data ->
                        _state.update {
                            val newRooms = it.rooms.toMutableList()
                            newRooms.indexOfFirst { it.id == data.id }.let { index ->
                                if(index != -1){
                                    newRooms[index] = data
                                }
                            }
                            it.copy(
                                rooms = newRooms,
                                message = UiText.Text("success")
                            )
                        }
                    }
                }
            }
            is ChatListAction.UnMute -> {
                viewModelScope.launch {
                    val response = chatApi.unMuteRoom(action.roomId)
                    response.onFailure { error ->
                        _state.update { it.copy(
                            message = error.text
                        ) }
                    }
                    response.onSuccess { data ->
                        _state.update {
                            val newRooms = it.rooms.toMutableList()
                            newRooms.indexOfFirst { it.id == data.id }.let { index ->
                                if(index != -1){
                                    newRooms[index] = data
                                }
                            }
                            it.copy(
                                rooms = newRooms,
                                message = UiText.Text("success")
                            )
                        }
                    }
                }
            }
        }
    }



    private fun loadRooms(){
        viewModelScope.launch {
            val roomsResponse = chatApi.getPublicRooms()
            roomsResponse.onFailure { error ->
                _state.update { it.copy(
                    message = error.text
                ) }
            }

            roomsResponse.onSuccess { data ->
                _state.update { it.copy(
                    rooms = data,
                ) }
            }
        }
    }


}
