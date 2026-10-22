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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
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
private val Green = Color(0xFF2ECC71)
private val Gold = Color(0xFFFFB84D)
private val Pink = Color(0xFFFF5C8A)
private val TrackColor = Color(0xFF303853)

private val Poppins: FontFamily = FontFamily.SansSerif
private val Inter: FontFamily = FontFamily.SansSerif
private val Mono: FontFamily = FontFamily.Monospace

@Composable
fun VisualizeScreen(
    onNext: () -> Unit = {},
    onSkip: () -> Unit = {}
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
                text = "03 / MAKE THE ABSTRACT REAL",
                style = TextStyle(fontFamily = Mono, fontSize = 11.sp, color = TextSecondary)
            )
            Text(
                text = "Skip",
                modifier = Modifier.clickable(onClick = onSkip),
                style = TextStyle(fontFamily = Inter, fontSize = 13.sp, color = TextSecondary)
            )
        }

        // Illustration area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(312.dp),
            contentAlignment = Alignment.Center
        ) {
            SortingCard()
        }

        Badge(
            text = "SEE THE LOGIC",
            background = Cyan.copy(alpha = 0.09f),
            textColor = Cyan
        )

        Text(
            text = "Don’t just read it.\nWatch it click.",
            style = TextStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 30.sp,
                lineHeight = 39.sp,
                color = TextPrimary
            )
        )

        Text(
            text = "Step through every comparison, swap, and search. You control the pace. We make the logic visible.",
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
                Box(
                    Modifier
                        .size(width = 28.dp, height = 7.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Purple)
                )
                Box(Modifier.size(7.dp).clip(CircleShape).background(TrackColor))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Purple)
                    .clickable(onClick = onNext),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconCanvas(18.dp) { d -> drawArrowRight(d) }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "That makes sense. Next",
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                )
            }

            Text(
                text = "Built for your “aha!” moments.",
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

@Composable
private fun SortingCard() {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.horizontalGradient(listOf(Color(0xFF252B46), Color(0xFF1A1F36)))
            )
            .border(1.dp, Color.White.copy(alpha = 0.07f), shape)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Demo header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "bubble_sort()",
                style = TextStyle(fontFamily = Mono, fontSize = 13.sp, color = TextPrimary)
            )
            Badge(
                text = "LIVE PREVIEW",
                background = Cyan.copy(alpha = 0.09f),
                textColor = Cyan
            )
        }

        // Sorting bars: value, colour
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            DataBar(2, Cyan, Modifier.weight(1f))
            DataBar(3, Cyan, Modifier.weight(1f))
            DataBar(7, Gold, Modifier.weight(1f))
            DataBar(5, Gold, Modifier.weight(1f))
            DataBar(8, Green, Modifier.weight(1f))
        }

        // State key
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StateLabel("Default", Cyan)
            StateLabel("Compare", Gold)
            StateLabel("Sorted", Green)
            StateLabel("Swap", Pink)
        }

        // Caption
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            IconCanvas(16.dp) { d -> drawScan(d) }
            Text(
                text = "Compare 7 and 5. Should they swap?",
                modifier = Modifier.weight(1f),
                style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = Gold)
            )
        }
    }
}

@Composable
private fun DataBar(value: Int, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = value.toString(),
            style = TextStyle(fontFamily = Mono, fontSize = 13.sp, color = color)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((value * 14.75f).dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 7.dp, topEnd = 7.dp,
                        bottomStart = 2.dp, bottomEnd = 2.dp
                    )
                )
                .background(
                    Brush.verticalGradient(listOf(color, color.copy(alpha = 0.27f)))
                )
        )
    }
}

@Composable
private fun StateLabel(text: String, color: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(5.dp).clip(CircleShape).background(color))
        Text(
            text = text,
            style = TextStyle(fontFamily = Inter, fontSize = 10.sp, color = TextSecondary)
        )
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

private fun DrawScope.drawArrowRight(d: Float) {
    val w = 2f * d
    drawLine(Color.White, Offset(3f * d, 9f * d), Offset(15f * d, 9f * d), w, StrokeCap.Round)
    drawLine(Color.White, Offset(10f * d, 4f * d), Offset(15f * d, 9f * d), w, StrokeCap.Round)
    drawLine(Color.White, Offset(10f * d, 14f * d), Offset(15f * d, 9f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawScan(d: Float) {
    val w = 1.5f * d
    val c = Gold
    // four corner brackets
    drawLine(c, Offset(2f * d, 6f * d), Offset(2f * d, 2f * d), w, StrokeCap.Round)
    drawLine(c, Offset(2f * d, 2f * d), Offset(6f * d, 2f * d), w, StrokeCap.Round)
    drawLine(c, Offset(10f * d, 2f * d), Offset(14f * d, 2f * d), w, StrokeCap.Round)
    drawLine(c, Offset(14f * d, 2f * d), Offset(14f * d, 6f * d), w, StrokeCap.Round)
    drawLine(c, Offset(14f * d, 10f * d), Offset(14f * d, 14f * d), w, StrokeCap.Round)
    drawLine(c, Offset(14f * d, 14f * d), Offset(10f * d, 14f * d), w, StrokeCap.Round)
    drawLine(c, Offset(6f * d, 14f * d), Offset(2f * d, 14f * d), w, StrokeCap.Round)
    drawLine(c, Offset(2f * d, 14f * d), Offset(2f * d, 10f * d), w, StrokeCap.Round)
    // middle line
    drawLine(c, Offset(4f * d, 8f * d), Offset(12f * d, 8f * d), w, StrokeCap.Round)
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun VisualizeScreenPreview() {
    VisualizeScreen()
}

