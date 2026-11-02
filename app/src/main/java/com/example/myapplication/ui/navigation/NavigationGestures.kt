package com.example.myapplication.ui.navigation

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Intercepts and consumes horizontal drag gestures within a child container (such as code editors,
 * text inputs, and visualizer parameter controls) so that the parent HorizontalPager does not
 * accidentally intercept the gesture and trigger a page swipe while the user is typing or selecting.
 */
fun Modifier.preventPagerSwipe(): Modifier = this.pointerInput(Unit) {
    detectHorizontalDragGestures { change, _ ->
        change.consume()
    }
}

/**
 * A NestedScrollConnection that absorbs remaining horizontal scroll from scrollable children
 * (such as horizontal scroll containers like the Live Variables Watch Window)
 * so that reaching the start/end boundary does not bubble up and trigger a page swipe in HorizontalPager.
 */
val ConsumeHorizontalScrollConnection = object : NestedScrollConnection {
    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        return Offset(available.x, 0f)
    }
}

fun Modifier.consumeNestedHorizontalScroll(): Modifier =
    this.nestedScroll(ConsumeHorizontalScrollConnection)

