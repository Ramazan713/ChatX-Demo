package com.example.chatx.features.chat.domain.models

data class PageInfo(
    val hasNextPage: Boolean,
    val nextItemId: String?,
    val hasPreviousPage: Boolean,
    val previousItemId: String?
)
