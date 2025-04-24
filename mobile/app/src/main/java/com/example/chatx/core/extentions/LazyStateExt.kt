package com.example.chatx.core.extentions

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridLayoutInfo
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridLayoutInfo
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull

@Composable
fun LazyListState.customScrolltoPos(
    pos: Int
) {
    var navToPos by rememberSaveable(pos) {
        mutableStateOf<Int?>(null)
    }

    LaunchedEffect(this, navToPos) {
        snapshotFlow { navToPos }
            .filterNotNull()
            .filter { layoutInfo.totalItemsCount != 0 }
            .distinctUntilChanged()
            .collectLatest { pos ->
                scrollToItem(pos)
                navToPos = null
            }
    }
}


@Composable
fun LazyListState.visibleMiddlePosition(): Int{
    return remember(this) {
        derivedStateOf {
            (firstVisibleItemIndex + (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0)) / 2
        }
    }.value
}

@Composable
fun LazyGridState.visibleMiddlePosition(): Int{
    return remember(this) {
        derivedStateOf {
            (firstVisibleItemIndex + (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0)) / 2
        }
    }.value
}

@Composable
fun LazyStaggeredGridState.visibleMiddlePosition(): Int{
    return remember {
        derivedStateOf {
            (firstVisibleItemIndex + (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0)) / 2
        }
    }.value
}




@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}


@Composable
fun LazyGridState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

@Composable
fun LazyStaggeredGridState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}


fun LazyGridLayoutInfo.isNumberInRange(number: Int): Boolean?{
    visibleItemsInfo.let { info->
        val first = info.firstOrNull()?.index
        val last = info.lastOrNull()?.index
        if(first == null || last == null) return null
        return number in first..last
    }
}



fun LazyStaggeredGridLayoutInfo.isNumberInRange(number: Int): Boolean?{
    visibleItemsInfo.let { info->
        val first = info.firstOrNull()?.index
        val last = info.lastOrNull()?.index
        if(first == null || last == null) return null
        return number in first..last
    }
}
