package com.example.chatx.core.presentation.utils

import androidx.compose.runtime.Composable
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf

@Composable
fun <T: Any> getPreviewLazyPagingData(
    items: List<T>,
    sourceLoadStates: LoadStates = previewPagingLoadStates(),
    mediatorLoadStates: LoadStates? = null,
): LazyPagingItems<T> {
    return flowOf(
        PagingData.from(items, sourceLoadStates = sourceLoadStates, mediatorLoadStates = mediatorLoadStates)
    ).collectAsLazyPagingItems()
}

fun previewPagingLoadStates(
    refresh: LoadState = LoadState.NotLoading(false),
    prepend: LoadState = LoadState.NotLoading(false),
    append: LoadState = LoadState.NotLoading(false),
): LoadStates {
    return LoadStates(
        refresh = refresh,
        prepend = prepend,
        append = append
    )
}