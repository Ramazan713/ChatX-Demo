package com.example.chatx.features.auth.data.mappers

import com.example.chatx.features.auth.data.dtos.UserDto
import com.example.chatx.features.auth.domain.models.User

fun UserDto.toUser(): User {
    return User(
        id = id,
        username = username
    )
}