package com.example.myapplication

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Helper for tab boundaries and index calculation on horizontal swipe */
object BottomBarSwipeHelper {
    const val MIN_TAB = 0
    const val MAX_TAB = 4
    const val DEFAULT_SWIPE_THRESHOLD_DP = 36f

    fun getNextTabOnSwipeLeft(currentTab: Int): Int {
        return if (currentTab < MAX_TAB) currentTab + 1 else currentTab
    }

    fun getNextTabOnSwipeRight(currentTab: Int): Int {
        return if (currentTab > MIN_TAB) currentTab - 1 else currentTab
    }
}

/** Pure drag gesture tracker for unit testability and single-action per swipe */
class BottomBarSwipeTracker(
    val swipeThresholdPx: Float = 100f,
    val onNavigate: (Int) -> Unit
) {
    var dragAccumulator: Float = 0f
        private set
    var hasSwipedInGesture: Boolean = false
        private set

    fun onDragStart() {
        dragAccumulator = 0f
        hasSwipedInGesture = false
    }

    fun onDrag(dragAmount: Float, currentTab: Int): Boolean {
        if (hasSwipedInGesture) return false

        dragAccumulator += dragAmount
        if (dragAccumulator <= -swipeThresholdPx) {
            hasSwipedInGesture = true
            val nextTab = BottomBarSwipeHelper.getNextTabOnSwipeLeft(currentTab)
            if (nextTab != currentTab) {
                onNavigate(nextTab)
                return true
            }
            return false
        } else if (dragAccumulator >= swipeThresholdPx) {
            hasSwipedInGesture = true
            val prevTab = BottomBarSwipeHelper.getNextTabOnSwipeRight(currentTab)
            if (prevTab != currentTab) {
                onNavigate(prevTab)
                return true
            }
            return false
        }
        return false
    }

    fun onDragEnd() {
        dragAccumulator = 0f
        hasSwipedInGesture = false
    }

    fun onDragCancel() {
        dragAccumulator = 0f
        hasSwipedInGesture = false
    }
}

/** Helper for continuous drag-to-select navigation calculations and boundary clamping */
object BottomBarDragHelper {
    const val TAB_COUNT = 5
    const val MIN_TAB = 0
    const val MAX_TAB = 4

    /**
     * Calculates the continuous/fractional tab position [0.0f .. 4.0f]
     * based on touch X position and total navigation bar width.
     */
    fun calculateContinuousTab(x: Float, barWidth: Float): Float {
        if (barWidth <= 0f) return 0f
        val fraction = (x / barWidth).coerceIn(0f, 1f)
        return (fraction * TAB_COUNT.toFloat() - 0.5f).coerceIn(
            MIN_TAB.toFloat(),
            MAX_TAB.toFloat()
        )
    }

    /**
     * Calculates the discrete closest tab index (0..4)
     * based on touch X position and total navigation bar width.
     */
    fun calculateClosestTab(x: Float, barWidth: Float): Int {
        if (barWidth <= 0f) return 0
        val fraction = (x / barWidth).coerceIn(0f, 1f)
        val tab = (fraction * TAB_COUNT.toFloat()).toInt().coerceIn(MIN_TAB, MAX_TAB)
        return tab
    }
}

/** Pure drag gesture tracker for unit testability and continuous navigation state */
class BottomBarDragStateTracker(
    var barWidth: Float = 360f,
    val onNavigate: (Int) -> Unit
) {
    var isDragging: Boolean = false
        private set
    var currentContinuousTab: Float = 0f
        private set
    var currentPreviewTab: Int = 0
        private set

    fun onDragStart(startX: Float) {
        isDragging = true
        currentContinuousTab = BottomBarDragHelper.calculateContinuousTab(startX, barWidth)
        currentPreviewTab = BottomBarDragHelper.calculateClosestTab(startX, barWidth)
    }

    fun onDrag(currentX: Float) {
        if (!isDragging) return
        currentContinuousTab = BottomBarDragHelper.calculateContinuousTab(currentX, barWidth)
        currentPreviewTab = BottomBarDragHelper.calculateClosestTab(currentX, barWidth)
    }

    fun onDragEnd(currentSelectedTab: Int) {
        if (!isDragging) return
        isDragging = false
        val targetTab = currentPreviewTab
        if (targetTab != currentSelectedTab) {
            onNavigate(targetTab)
        }
    }

    fun onDragCancel() {
        isDragging = false
    }
}

private val NavContainerBg = Color(0xFF1B1F38)
private val ActivePillBg = Color(0xFF2B2550)
private val TextMuted = Color(0xFF8C98B8)
private val Purple = Color(0xFF6C5CE7)
private val PurpleSoft = Color(0xFFC0B6FF)
private val Cyan = Color(0xFF00D2FF)
private val Gold = Color(0xFFFFB84D)
private val Inter: FontFamily = FontFamily.SansSerif

@Composable
fun AlgoQuestBottomBar(
    selectedTab: Int, // 0: Home, 1: Map, 2: Visualizer, 3: AI Ally, 4: Profile
    onSelectTab: (Int) -> Unit
) {
    val containerShape = RoundedCornerShape(32.dp)

    val currentSelectedTab by rememberUpdatedState(selectedTab)
    val currentOnSelectTab by rememberUpdatedState(onSelectTab)

    var isDragging by remember { mutableStateOf(false) }
    var dragX by remember { mutableStateOf(0f) }
    var barWidthPx by remember { mutableStateOf(0) }

    val currentContinuousTab = remember(isDragging, dragX, barWidthPx, selectedTab) {
        if (isDragging && barWidthPx > 0) {
            BottomBarDragHelper.calculateContinuousTab(dragX, barWidthPx.toFloat())
        } else {
            selectedTab.toFloat()
        }
    }

    val currentPreviewTab = remember(isDragging, dragX, barWidthPx, selectedTab) {
        if (isDragging && barWidthPx > 0) {
            BottomBarDragHelper.calculateClosestTab(dragX, barWidthPx.toFloat())
        } else {
            selectedTab
        }
    }

    val animatedTabPosition by animateFloatAsState(
        targetValue = currentContinuousTab,
        animationSpec = if (isDragging) snap() else tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "tabPosition"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp, top = 4.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Main Floating Glass Shell
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .onSizeChanged { barWidthPx = it.width }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            dragX = offset.x
                        },
                        onDragEnd = {
                            val finalTab = if (barWidthPx > 0) {
                                BottomBarDragHelper.calculateClosestTab(dragX, barWidthPx.toFloat())
                            } else currentSelectedTab

                            isDragging = false
                            if (finalTab != currentSelectedTab) {
                                currentOnSelectTab(finalTab)
                            }
                        },
                        onDragCancel = {
                            isDragging = false
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            dragX = (dragX + dragAmount).coerceIn(0f, barWidthPx.toFloat())
                            change.consume()
                        }
                    )
                }
                .shadow(
                    elevation = 20.dp,
                    shape = containerShape,
                    ambientColor = Purple,
                    spotColor = Cyan
                )
                .clip(containerShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF222846), NavContainerBg, Color(0xFF15192E))
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.horizontalGradient(
                        listOf(Purple.copy(alpha = 0.6f), Cyan.copy(alpha = 0.5f), Purple.copy(alpha = 0.6f))
                    ),
                    shape = containerShape
                )
                .padding(horizontal = 6.dp, vertical = 6.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            val totalWidth = maxWidth
            val tabWidth = totalWidth / 5
            val pillWidth = (tabWidth - 6.dp).coerceIn(36.dp, tabWidth)
            val pillHeight = 48.dp
            val pillShape = RoundedCornerShape(22.dp)

            // Dynamic sliding indicator pill that follows the finger continuously
            val pillOffsetX = (tabWidth * animatedTabPosition) + (tabWidth - pillWidth) / 2


            Box(
                modifier = Modifier
                    .offset(x = pillOffsetX)
                    .size(width = pillWidth, height = pillHeight)
                    .clip(pillShape)
                    .background(ActivePillBg)
                    .border(
                        width = 1.dp,
                        color = PurpleSoft.copy(alpha = 0.45f),
                        shape = pillShape
                    )
            )

            // Five interactive tabs evenly distributed across available width
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 0: Home
                NavTabItem(
                    label = "Home",
                    selected = currentPreviewTab == 0,
                    onClick = { onSelectTab(0) },
                    modifier = Modifier.weight(1f)
                ) { d, active -> drawHomeIcon(d, if (active) Cyan else TextMuted) }

                // Tab 1: Map
                NavTabItem(
                    label = "Map",
                    selected = currentPreviewTab == 1,
                    onClick = { onSelectTab(1) },
                    modifier = Modifier.weight(1f)
                ) { d, active -> drawMapIcon(d, if (active) Cyan else TextMuted) }

                // Tab 2: Visualizer Lab (Featured Central Hub)
                CenterOrbTabItem(
                    selected = currentPreviewTab == 2,
                    onClick = { onSelectTab(2) },
                    modifier = Modifier.weight(1f)
                )

                // Tab 3: AI Ally
                NavTabItem(
                    label = "AI Ally",
                    selected = currentPreviewTab == 3,
                    onClick = { onSelectTab(3) },
                    modifier = Modifier.weight(1f)
                ) { d, active -> drawSparkles(d, if (active) Cyan else TextMuted) }

                // Tab 4: Profile
                NavTabItem(
                    label = "Profile",
                    selected = currentPreviewTab == 4,
                    onClick = { onSelectTab(4) },
                    modifier = Modifier.weight(1f)
                ) { d, active -> drawUserIcon(d, if (active) Cyan else TextMuted) }
            }
        }
    }
}

@Composable
private fun NavTabItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: DrawScope.(Float, Boolean) -> Unit
) {
    val pillShape = RoundedCornerShape(22.dp)

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(pillShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Box(
                modifier = Modifier.size(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(Modifier.size(20.dp)) {
                    icon(this, density, selected)
                }
            }
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 9.5.sp,
                    color = if (selected) PurpleSoft else TextMuted
                ),
                maxLines = 1
            )
        }
    }
}

/** Featured Center Floating Orb for Visualizer Lab */
@Composable
private fun CenterOrbTabItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val orbShape = CircleShape
    val glowColor by animateColorAsState(
        targetValue = if (selected) Cyan else Purple,
        animationSpec = tween(250),
        label = "glowColor"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .shadow(
                        elevation = if (selected) 6.dp else 2.dp,
                        shape = orbShape,
                        ambientColor = glowColor,
                        spotColor = glowColor
                    )
                    .clip(orbShape)
                    .background(
                        Brush.radialGradient(
                            colors = if (selected) listOf(Cyan, Purple) else listOf(Purple, NavContainerBg)
                        )
                    )
                    .border(
                        1.5.dp,
                        Brush.sweepGradient(listOf(Cyan, Gold, Purple, Cyan)),
                        orbShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Canvas(Modifier.size(16.dp)) {
                    drawLab(density, Color.White)
                }
            }
            Text(
                text = "Viz Lab",
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 9.5.sp,
                    color = if (selected) Cyan else PurpleSoft
                ),
                maxLines = 1
            )
        }
    }
}

private fun lineStroke(d: Float, w: Float = 1.4f) =
    Stroke(width = w * d, cap = StrokeCap.Round, join = StrokeJoin.Round)

private fun DrawScope.drawHomeIcon(d: Float, color: Color) {
    val path = Path().apply {
        moveTo(3f * d, 9f * d)
        lineTo(9f * d, 3f * d)
        lineTo(15f * d, 9f * d)
        lineTo(15f * d, 15f * d)
        lineTo(3f * d, 15f * d)
        close()
    }
    drawPath(path, color, style = lineStroke(d, 1.4f))
}

private fun DrawScope.drawMapIcon(d: Float, color: Color) {
    val path = Path().apply {
        moveTo(3f * d, 5f * d)
        lineTo(7f * d, 3f * d)
        lineTo(11f * d, 5f * d)
        lineTo(15f * d, 3f * d)
        lineTo(15f * d, 13f * d)
        lineTo(11f * d, 15f * d)
        lineTo(7f * d, 13f * d)
        lineTo(3f * d, 15f * d)
        close()
    }
    drawPath(path, color, style = lineStroke(d, 1.4f))
}

private fun DrawScope.drawLab(d: Float, color: Color) {
    drawLine(color, Offset(4f * d, 15f * d), Offset(4f * d, 10f * d), 2f * d, StrokeCap.Round)
    drawLine(color, Offset(9f * d, 15f * d), Offset(9f * d, 5f * d), 2f * d, StrokeCap.Round)
    drawLine(color, Offset(14f * d, 15f * d), Offset(14f * d, 2f * d), 2f * d, StrokeCap.Round)
}

private fun DrawScope.drawUserIcon(d: Float, color: Color) {
    drawCircle(color, radius = 3f * d, center = Offset(9f * d, 5f * d), style = lineStroke(d))
    drawArc(color, 180f, 180f, false, Offset(3f * d, 9f * d), Size(12f * d, 10f * d), style = lineStroke(d, 1.4f))
}

private fun DrawScope.drawSparkles(d: Float, color: Color) {
    drawStar(d, color, cx = 8f, cy = 12f, r = 6.5f)
    drawStar(d, color, cx = 15.5f, cy = 4.8f, r = 3.3f)
}

private fun DrawScope.drawStar(d: Float, color: Color, cx: Float, cy: Float, r: Float) {
    val i = r * 0.3f
    val path = Path().apply {
        moveTo(cx * d, (cy - r) * d)
        lineTo((cx + i) * d, (cy - i) * d)
        lineTo((cx + r) * d, cy * d)
        lineTo((cx + i) * d, (cy + i) * d)
        lineTo(cx * d, (cy + r) * d)
        lineTo((cx - i) * d, (cy - i) * d)
        close()
    }
    drawPath(path, color, style = lineStroke(d, 1.4f))
}
