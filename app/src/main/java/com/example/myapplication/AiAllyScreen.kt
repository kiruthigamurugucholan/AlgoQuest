package com.example.myapplication

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF0F1220)
private val TextPrimary = Color(0xFFF4F6FF)
private val TextSecondary = Color(0xFFC3CBE2)
private val Cyan = Color(0xFF00D2FF)
private val Purple = Color(0xFF6C5CE7)
private val PurpleLight = Color(0xFFA29BFE)
private val PurpleDark = Color(0xFF5B4BD5)
private val PurpleSoft = Color(0xFFB5A7FF)
private val Green = Color(0xFF2ECC71)
private val TrackColor = Color(0xFF303853)

private val Poppins: FontFamily = FontFamily.SansSerif
private val Inter: FontFamily = FontFamily.SansSerif
private val Mono: FontFamily = FontFamily.Monospace

@Composable
fun AiAllyScreen(onStart: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "04 / NEVER GET STUCK ALONE",
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(fontFamily = Mono, fontSize = 11.sp, color = TextSecondary)
        )

        // Chat illustration
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(312.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Learner message
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .width(260.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Purple)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Why is binary search so much faster?",
                            style = TextStyle(
                                fontFamily = Inter,
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                color = TextPrimary
                            )
                        )
                    }
                }

                // AI reply card
                val cardShape = RoundedCornerShape(16.dp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(cardShape)
                        .background(Color(0xFF24213F))
                        .border(1.dp, Color.White.copy(alpha = 0.07f), cardShape)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MiniMark()
                        Text(
                            text = "Quest AI · Your learning ally",
                            modifier = Modifier.weight(1f),
                            style = TextStyle(
                                fontFamily = Inter,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                        )
                        IconCanvas(16.dp) { d -> drawSparkles(d, Cyan) }
                    }
                    Text(
                        text = "Imagine finding a word in a dictionary. You don’t start at A — you open the middle and cut the search in half.",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                            color = TextPrimary
                        )
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Badge("1,023 items", Cyan.copy(alpha = 0.09f), Cyan)
                        Badge("At most 10 steps", Green.copy(alpha = 0.09f), Green)
                    }
                }
            }
        }

        Badge(
            text = "YOUR PERSONAL AI ALLY",
            background = Purple.copy(alpha = 0.09f),
            textColor = PurpleSoft
        )

        Text(
            text = "A little guidance.\nA lot of possibility.",
            style = TextStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 30.sp,
                lineHeight = 39.sp,
                color = TextPrimary
            )
        )

        Text(
            text = "Ask the “why,” untangle your code, and get a plan that grows with you. Your next breakthrough is one question away.",
            style = TextStyle(
                fontFamily = Inter,
                fontSize = 15.sp,
                lineHeight = 23.sp,
                color = TextSecondary
            )
        )

        // Bottom actions
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Bottom)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(7.dp).clip(CircleShape).background(TrackColor))
                Box(Modifier.size(7.dp).clip(CircleShape).background(TrackColor))
                Box(
                    Modifier
                        .size(width = 28.dp, height = 7.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Purple)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Purple)
                    .clickable(onClick = onStart),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconCanvas(18.dp) { d -> drawSparkles(d, Color.White, 18f) }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Let’s build my path",
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                )
            }

            Text(
                text = "Free to start. Made to go far.",
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

/** Small AlgoQuest logo (28dp) used in the tutor row. */
@Composable
private fun MiniMark() {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(PurpleLight, Purple, PurpleDark),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val d = density
            val w = 1.6f * d
            fun o(x: Float, y: Float) = Offset(x * d, y * d)
            val white = Color.White
            drawLine(white, o(10f, 8f), o(6f, 14f), w, StrokeCap.Round)
            drawLine(white, o(6f, 14f), o(10f, 20f), w, StrokeCap.Round)
            drawLine(white, o(18f, 8f), o(22f, 14f), w, StrokeCap.Round)
            drawLine(white, o(22f, 14f), o(18f, 20f), w, StrokeCap.Round)
            drawLine(white, o(16f, 6f), o(12f, 22f), w, StrokeCap.Round)
        }
    }
}

@Composable
private fun Badge(text: String, background: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .height(26.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .padding(horizontal = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = textColor
            )
        )
    }
}

// ---------- Small icons drawn in code ----------

@Composable
private fun IconCanvas(size: Dp, draw: DrawScope.(Float) -> Unit) {
    Canvas(Modifier.size(size)) { draw(this, density) }
}

/** Two sparkle stars. [box] is the icon's design size (16 by default). */
private fun DrawScope.drawSparkles(d: Float, color: Color, box: Float = 16f) {
    val s = box / 16f
    drawStar(d, color, cx = 6.5f * s, cy = 9.5f * s, r = 5.5f * s)
    drawStar(d, color, cx = 12.5f * s, cy = 3.8f * s, r = 2.8f * s)
}

private fun DrawScope.drawStar(d: Float, color: Color, cx: Float, cy: Float, r: Float) {
    val i = r * 0.3f
    val path = Path().apply {
        moveTo(cx * d, (cy - r) * d)
        lineTo((cx + i) * d, (cy - i) * d)
        lineTo((cx + r) * d, cy * d)
        lineTo((cx + i) * d, (cy + i) * d)
        lineTo(cx * d, (cy + r) * d)
        lineTo((cx - i) * d, (cy + i) * d)
        lineTo((cx - r) * d, cy * d)
        lineTo((cx - i) * d, (cy - i) * d)
        close()
    }
    drawPath(path, color, style = Stroke(width = 1.4f * d, join = StrokeJoin.Round))
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AiAllyScreenPreview() {
    AiAllyScreen()
}

