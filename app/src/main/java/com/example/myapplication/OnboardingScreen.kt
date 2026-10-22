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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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
private val PurpleSoft = Color(0xFFB5A7FF)
private val Green = Color(0xFF2ECC71)
private val Gold = Color(0xFFFFB84D)
private val CardBg = Color(0xFF1A1F36)
private val TrackColor = Color(0xFF303853)
private val OrbitColor = Color(0xFF2A2F55)

private val Poppins: FontFamily = FontFamily.SansSerif
private val Inter: FontFamily = FontFamily.SansSerif
private val Mono: FontFamily = FontFamily.Monospace

@Composable
fun OnboardingScreen(
    onStart: () -> Unit = {},
    onSkip: () -> Unit = {},
    onLogin: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "02 / FIND YOUR NEXT LEVEL",
                style = TextStyle(fontFamily = Mono, fontSize = 11.sp, color = TextSecondary)
            )
            Text(
                text = "Skip",
                modifier = Modifier.clickable(onClick = onSkip),
                style = TextStyle(fontFamily = Inter, fontSize = 13.sp, color = TextSecondary)
            )
        }

        QuestIllustration()

        Badge(
            text = "LEARN WITH PURPOSE",
            background = Purple.copy(alpha = 0.09f),
            textColor = PurpleSoft
        )

        Text(
            text = "Small quests.\nLegendary progress.",
            style = TextStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 30.sp,
                lineHeight = 39.sp,
                color = TextPrimary
            )
        )

        Text(
            text = "Turn DSA into a daily adventure. Follow your path, earn XP, and build skills that stay with you.",
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
                Box(
                    Modifier
                        .size(width = 28.dp, height = 7.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Purple)
                )
                Box(Modifier.size(7.dp).clip(CircleShape).background(TrackColor))
                Box(Modifier.size(7.dp).clip(CircleShape).background(TrackColor))
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
                IconCanvas(18.dp) { d -> drawArrowRight(d) }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Start my quest",
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                )
            }

            Text(
                text = "Already an explorer?  Log in",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onLogin),
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

@Composable
private fun QuestIllustration() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(312.dp)
    ) {
        // Design is 342dp wide; centred on any screen
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .width(342.dp)
                .fillMaxHeight()
        ) {
            Canvas(Modifier.fillMaxSize()) {
                val d = density
                val c = Offset(171f * d, 152f * d)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Purple.copy(alpha = 0.16f), Purple.copy(alpha = 0.04f)),
                        center = c,
                        radius = 132f * d
                    ),
                    radius = 132f * d,
                    center = c
                )
                drawCircle(OrbitColor, radius = 132f * d, center = c, style = Stroke(1f * d))
                drawRect(
                    color = TrackColor,
                    topLeft = Offset(169f * d, 44f * d),
                    size = Size(2f * d, 224f * d)
                )
            }

            // Completed checkpoint
            Box(
                modifier = Modifier
                    .offset(149.dp, 18.dp)
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF17392F))
                    .border(1.dp, Green, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconCanvas(20.dp) { d -> drawCheck(d) }
            }

            // Daily quest card
            Column(
                modifier = Modifier
                    .offset(24.dp, 84.dp)
                    .width(294.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBg)
                    .border(1.dp, Color(0xFF9585FF).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Badge(
                        text = "DAILY QUEST",
                        background = Cyan.copy(alpha = 0.09f),
                        textColor = Cyan
                    )
                    IconCanvas(20.dp) { d -> drawSwords(d) }
                }
                Text(
                    text = "Conquer arrays",
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 21.sp,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "One concept. One challenge. Real progress.",
                    style = TextStyle(fontFamily = Inter, fontSize = 13.sp, color = TextSecondary)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.07f))
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(0.66f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(Purple)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "2 of 3 steps done",
                        style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = TextSecondary)
                    )
                    Text(
                        text = "+150 XP",
                        style = TextStyle(fontFamily = Mono, fontSize = 12.sp, color = Gold)
                    )
                }
            }

            // Next checkpoint
            Box(
                modifier = Modifier
                    .offset(151.dp, 268.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF272347))
                    .border(1.dp, Purple, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconCanvas(18.dp) { d -> drawFlag(d) }
            }
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

private fun DrawScope.drawCheck(d: Float) {
    val w = 2f * d
    drawLine(Green, Offset(5f * d, 10f * d), Offset(9f * d, 14f * d), w, StrokeCap.Round)
    drawLine(Green, Offset(9f * d, 14f * d), Offset(15f * d, 6f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawSwords(d: Float) {
    val w = 1.8f * d
    drawLine(Cyan, Offset(4f * d, 16f * d), Offset(15f * d, 5f * d), w, StrokeCap.Round)
    drawLine(Cyan, Offset(16f * d, 16f * d), Offset(5f * d, 5f * d), w, StrokeCap.Round)
    drawLine(Cyan, Offset(3f * d, 13f * d), Offset(7f * d, 17f * d), w, StrokeCap.Round)
    drawLine(Cyan, Offset(17f * d, 13f * d), Offset(13f * d, 17f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawFlag(d: Float) {
    val w = 1.8f * d
    val c = PurpleSoft
    drawLine(c, Offset(5f * d, 3f * d), Offset(5f * d, 16f * d), w, StrokeCap.Round)
    drawLine(c, Offset(5f * d, 4f * d), Offset(14f * d, 4f * d), w, StrokeCap.Round)
    drawLine(c, Offset(14f * d, 4f * d), Offset(11f * d, 7f * d), w, StrokeCap.Round)
    drawLine(c, Offset(11f * d, 7f * d), Offset(14f * d, 10f * d), w, StrokeCap.Round)
    drawLine(c, Offset(14f * d, 10f * d), Offset(5f * d, 10f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawArrowRight(d: Float) {
    val w = 2f * d
    drawLine(Color.White, Offset(3f * d, 9f * d), Offset(15f * d, 9f * d), w, StrokeCap.Round)
    drawLine(Color.White, Offset(10f * d, 4f * d), Offset(15f * d, 9f * d), w, StrokeCap.Round)
    drawLine(Color.White, Offset(10f * d, 14f * d), Offset(15f * d, 9f * d), w, StrokeCap.Round)
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun OnboardingScreenPreview() {
    OnboardingScreen()
}

