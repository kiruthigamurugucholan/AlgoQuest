package com.example.myapplication

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp, top = 4.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Main Floating Glass Shell
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
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
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 0: Home
            NavTabItem(
                label = "Home",
                selected = selectedTab == 0,
                onClick = { onSelectTab(0) }
            ) { d, active -> drawHomeIcon(d, if (active) Cyan else TextMuted) }

            // Tab 1: Map
            NavTabItem(
                label = "Map",
                selected = selectedTab == 1,
                onClick = { onSelectTab(1) }
            ) { d, active -> drawMapIcon(d, if (active) Cyan else TextMuted) }

            // Tab 2: Visualizer Lab (Featured Elevated Hub)
            CenterOrbTabItem(
                selected = selectedTab == 2,
                onClick = { onSelectTab(2) }
            )

            // Tab 3: AI Ally
            NavTabItem(
                label = "AI Ally",
                selected = selectedTab == 3,
                onClick = { onSelectTab(3) }
            ) { d, active -> drawSparkles(d, if (active) Cyan else TextMuted) }

            // Tab 4: Profile
            NavTabItem(
                label = "Profile",
                selected = selectedTab == 4,
                onClick = { onSelectTab(4) }
            ) { d, active -> drawUserIcon(d, if (active) Cyan else TextMuted) }
        }
    }
}

@Composable
private fun NavTabItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: DrawScope.(Float, Boolean) -> Unit
) {
    val pillBg by animateColorAsState(
        targetValue = if (selected) ActivePillBg else Color.Transparent,
        animationSpec = tween(250),
        label = "pillBg"
    )

    val pillShape = RoundedCornerShape(22.dp)

    Box(
        modifier = Modifier
            .height(48.dp)
            .clip(pillShape)
            .background(pillBg)
            .border(
                width = if (selected) 1.dp else 0.dp,
                color = if (selected) PurpleSoft.copy(alpha = 0.4f) else Color.Transparent,
                shape = pillShape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Canvas(Modifier.size(20.dp)) {
                icon(this, density, selected)
            }
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 9.5.sp,
                    color = if (selected) PurpleSoft else TextMuted
                )
            )
        }
    }
}

/** Featured Center Floating Orb for Visualizer Lab */
@Composable
private fun CenterOrbTabItem(
    selected: Boolean,
    onClick: () -> Unit
) {
    val orbShape = CircleShape
    val glowColor by animateColorAsState(
        targetValue = if (selected) Cyan else Purple,
        animationSpec = tween(250),
        label = "glowColor"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.offset(y = (-6).dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .shadow(elevation = 12.dp, shape = orbShape, ambientColor = glowColor, spotColor = glowColor)
                .clip(orbShape)
                .background(
                    Brush.radialGradient(
                        colors = if (selected) listOf(Cyan, Purple) else listOf(Purple, NavContainerBg)
                    )
                )
                .border(2.dp, Brush.sweepGradient(listOf(Cyan, Gold, Purple, Cyan)), orbShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Canvas(Modifier.size(20.dp)) {
                drawLab(density, Color.White)
            }
        }
        Text(
            text = "Viz Lab",
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 9.sp,
                color = if (selected) Cyan else PurpleSoft
            )
        )
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
    drawCircle(color, radius = 3f * d, center = Offset(9f * d, 5f * d), style = lineStroke(d, 1.4f))
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
