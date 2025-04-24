package com.masterplus.animals.core.extentions

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Modifier.customPropagateClick(
    propagateOnTap: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
    onLongPress: (() -> Unit)? = null
): Modifier = if (enabled) {
    val configuration = LocalViewConfiguration.current
    this.then(
        Modifier.pointerInput(propagateOnTap) {
            coroutineScope {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    if (!propagateOnTap) {
                        down.consume()
                    }
                    var longPressTriggered = false

                    val longPressJob = launch {
                        delay(configuration.longPressTimeoutMillis)
                        longPressTriggered = true

                        if(propagateOnTap){
                            down.consume()
                        }
                        onLongPress?.invoke()
                    }
                    val up = waitForUpOrCancellation()

                    longPressJob.cancel()
                    if (up != null && !longPressTriggered) {
                        onClick()
                    }
                }
            }
        }
    )
} else {
    this
}



@Composable
fun Modifier.clickableWithoutRipple(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
): Modifier{
    return clickable(
        indication = null,
        interactionSource = remember {
            MutableInteractionSource()
        },
        enabled = enabled,
        role = role,
        onClickLabel = onClickLabel,
        onClick = onClick
    )
}

@Composable
fun Modifier.useBackground(
    backgroundColor: Color?,
    shape: Shape = RectangleShape
): Modifier{
    if(backgroundColor == null) return this
    return this.background(backgroundColor, shape)
}

@Composable
fun Modifier.useBorder(
    borderWidth: Dp?,
    color: Color = MaterialTheme.colorScheme.outlineVariant,
    shape: Shape = RectangleShape
): Modifier{
    if(borderWidth == null) return this
    return this.border(borderWidth,color, shape)
}

@Composable
fun Modifier.fillMaxWithOrientation(): Modifier{
    val orientation = LocalConfiguration.current.orientation
    return if(orientation == Configuration.ORIENTATION_PORTRAIT) this.fillMaxWidth()
    else this.fillMaxHeight()
}

fun Modifier.clearFocusOnTap(): Modifier = composed {
    val focusManager = LocalFocusManager.current
    Modifier.pointerInput(Unit) {
        awaitEachGesture {
            awaitFirstDown(pass = PointerEventPass.Initial)
            val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
            if (upEvent != null) {
                focusManager.clearFocus()
            }
        }
    }
}
