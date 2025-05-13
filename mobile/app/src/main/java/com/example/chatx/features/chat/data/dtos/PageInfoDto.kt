package com.example.chatx.features.chat.data.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PageInfoDto(
    val hasNextPage: Boolean,
    val nextItemId: String?,
    val hasPreviousPage: Boolean,
    val previousItemId: String?
)
