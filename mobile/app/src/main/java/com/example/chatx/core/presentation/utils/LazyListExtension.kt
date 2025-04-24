package com.example.chatx.core.presentation.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull

@Composable
fun LazyGridState.rememberLazyGridExtensions(): LazyListExtension {
    return rememberLazyListExtensions(
        onItemsCount = { layoutInfo.totalItemsCount },
        onScrollToPos = { pos ->
            scrollToItem(pos)
        },
        onAnimateToPos = { pos ->
            animateScrollToItem(pos)
        }
    )
}

@Composable
fun LazyListState.rememberLazyListExtensions(): LazyListExtension {
    return rememberLazyListExtensions(
        onItemsCount = { layoutInfo.totalItemsCount },
        onScrollToPos = { pos ->
            scrollToItem(pos)
        },
        onAnimateToPos = { pos ->
            animateScrollToItem(pos)
        }
    )
}

@Composable
fun PagerState.rememberPagerStateExtensions(): LazyListExtension {
    return rememberLazyListExtensions(
        onItemsCount = { pageCount },
        onScrollToPos = { pos ->
            scrollToPage(pos)
        },
        onAnimateToPos = { pos ->
            animateScrollToPage(pos)
        }
    )
}


class LazyListExtension(
    private val onNavToPos: suspend (Int) -> Unit,
    private val onAnimateToPos: suspend (Int) -> Unit,
){
    suspend fun navToPos(pos: Int, animateToPos: Boolean = false){
        if(animateToPos){
            onAnimateToPos(pos)
        }else{
            onNavToPos(pos)
        }
    }
}

@Composable
private fun rememberLazyListExtensions(
    onScrollToPos: suspend (Int) -> Unit,
    onAnimateToPos: suspend (Int) -> Unit,
    onItemsCount: () -> Int
): LazyListExtension {

    var navToPos by rememberSaveable {
        mutableStateOf<Int?>(null)
    }
    var animateToPos by rememberSaveable {
        mutableStateOf<Int?>(null)
    }
    val completableDeferred by remember {
        mutableStateOf<CompletableDeferred<Unit>?>(null)
    }


    LaunchedEffect(onItemsCount(), navToPos, onScrollToPos) {
        snapshotFlow { navToPos }
            .filterNotNull()
            .filter {
                onItemsCount() != 0 && it <= onItemsCount()
            }
            .distinctUntilChanged()
            .collectLatest { pos ->
                onScrollToPos(pos)
                navToPos = null
                completableDeferred?.complete(Unit)
            }
    }

    LaunchedEffect(onItemsCount(), animateToPos, onScrollToPos) {
        snapshotFlow { animateToPos }
            .filterNotNull()
            .filter {
                onItemsCount() != 0 && it <= onItemsCount()
            }
            .distinctUntilChanged()
            .collectLatest { pos ->
                onAnimateToPos(pos)
                animateToPos = null
                completableDeferred?.complete(Unit)
            }
    }

    return remember {
        LazyListExtension(
            onNavToPos = { pos ->
                navToPos = pos
                completableDeferred?.await()
            },
            onAnimateToPos = { pos ->
                animateToPos = pos
                completableDeferred?.await()
            }
        )
    }
}