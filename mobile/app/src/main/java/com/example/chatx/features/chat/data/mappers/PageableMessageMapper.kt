package com.example.chatx.features.chat.data.mappers

import com.example.chatx.features.chat.data.dtos.PageInfoDto
import com.example.chatx.features.chat.data.dtos.PageableMessagesDto
import com.example.chatx.features.chat.domain.models.PageInfo
import com.example.chatx.features.chat.domain.models.PageableMessages


fun PageableMessagesDto.toModel(): PageableMessages {
    return PageableMessages(
        pageInfo = pageInfo.toModel(),
        messages = messages.map { it.toChatMessage() }
    )
}


fun PageInfoDto.toModel(): PageInfo{
    return PageInfo(
        hasNextPage, nextItemId, hasPreviousPage, previousItemId
    )
}