package com.example.myapplication

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val Bg = Color(0xFF0F1220)
private val TextPrimary = Color(0xFFF4F6FF)
private val TextSecondary = Color(0xFFC3CBE2)
private val TextMuted = Color(0xFF98A2C0)
private val Cyan = Color(0xFF00D2FF)
private val Purple = Color(0xFF6C5CE7)
private val PurpleLight = Color(0xFFA29BFE)
private val PurpleDark = Color(0xFF5B4BD5)
private val Orange = Color(0xFFFFB347)
private val TrackColor = Color(0xFF303853)
private val OrbitColor = Color(0xFF2A2F55)
private val ConstellationColor = Color(0xFF3D3F8F)

private val Poppins: FontFamily = FontFamily.SansSerif
private val Inter: FontFamily = FontFamily.SansSerif
private val JetBrainsMono: FontFamily = FontFamily.Monospace

@Composable
fun SplashScreen(onFinished: () -> Unit = {}) {
    var target by remember { mutableStateOf(0f) }
    val progress by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = 2500, easing = FastOutSlowInEasing),
        label = "progress"
    )
    LaunchedEffect(Unit) {
        target = 1f
        delay(2800)
        onFinished()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "01 / THE ADVENTURE BEGINS",
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        )

        BrandUniverse()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "AlgoQuest",
                style = TextStyle(
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 36.sp,
                    lineHeight = 47.sp,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
            )
            Text(
                text = "Big ideas. Small steps.\nAlgorithms that finally click.",
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            )
            Row(
                modifier = Modifier.padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    Modifier
                        .size(width = 24.dp, height = 2.dp)
                        .background(Cyan)
                )
                Text(
                    text = "LEARN • BUILD • LEVEL UP",
                    style = TextStyle(
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = Cyan
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Bottom)
        ) {
            Box(
                modifier = Modifier
                    .width(144.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(TrackColor)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(progress.coerceIn(0.02f, 1f))
                        .height(3.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Purple)
                )
            }
            Text(
                text = "Preparing your next breakthrough…",
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            )
            Text(
                text = "AI-POWERED LEARNING  /  v1.0",
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
private fun BrandUniverse() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(404.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val d = density
            val ox = (size.width - 342f * d) / 2f
            fun p(x: Float, y: Float) = Offset(ox + x * d, y * d)

            val center = p(171f, 202f)

            drawCircle(OrbitColor.copy(alpha = 0.7f), radius = 150f * d, center = center, style = Stroke(1f * d))
            drawCircle(OrbitColor, radius = 110f * d, center = center, style = Stroke(1f * d))

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Purple.copy(alpha = 0.22f), Purple.copy(alpha = 0.12f)),
                    center = center,
                    radius = 80f * d
                ),
                radius = 80f * d,
                center = center
            )

            val a = p(109f, 42f)
            val b = p(281f, 107f)
            val c = p(327f, 300f)
            val e = p(166f, 350f)
            val f = p(19f, 208f)
            val col = ConstellationColor.copy(alpha = 0.7f)
            listOf(a to b, b to c, c to e, e to f, f to a, a to e).forEach { (s, t) ->
                drawLine(col, s, t, strokeWidth = 1f * d)
            }

            drawCircle(Cyan, radius = 5f * d, center = p(37f, 221f))
            drawCircle(Purple, radius = 5f * d, center = p(267f, 142f))
            drawCircle(Orange, radius = 4f * d, center = p(165f, 329f))
        }

        AppMark()
    }
}

@Composable
private fun AppMark() {
    val shape = RoundedCornerShape(28.dp)
    Box(
        modifier = Modifier
            .size(104.dp)
            .shadow(
                elevation = 24.dp,
                shape = shape,
                ambientColor = Purple,
                spotColor = Purple
            )
            .clip(shape)
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
            val w = 3f * d
            fun o(x: Float, y: Float) = Offset(x * d, y * d)
            val white = Color.White

            drawLine(white, o(37f, 30f), o(22f, 52f), strokeWidth = w, cap = StrokeCap.Round)
            drawLine(white, o(22f, 52f), o(37f, 74f), strokeWidth = w, cap = StrokeCap.Round)
            drawLine(white, o(67f, 30f), o(82f, 52f), strokeWidth = w, cap = StrokeCap.Round)
            drawLine(white, o(82f, 52f), o(67f, 74f), strokeWidth = w, cap = StrokeCap.Round)
            drawLine(white, o(60f, 23f), o(45f, 79f), strokeWidth = w, cap = StrokeCap.Round)
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SplashScreenPreview() {
    SplashScreen()
}