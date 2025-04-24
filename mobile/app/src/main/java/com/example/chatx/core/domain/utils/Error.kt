package com.example.chatx.core.domain.utils

interface Error {
}

data class ErrorText(val text: UiText): Error
