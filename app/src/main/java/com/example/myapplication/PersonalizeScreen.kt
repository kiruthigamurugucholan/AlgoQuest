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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import com.example.myapplication.viewmodel.AuthViewModel

private val Bg = Color(0xFF0F1220)
private val TextPrimary = Color(0xFFF4F6FF)
private val TextSecondary = Color(0xFFC3CBE2)
private val TextMuted = Color(0xFF98A2C0)
private val Purple = Color(0xFF6C5CE7)
private val PurpleSoft = Color(0xFFB5A7FF)
private val CardBg = Color(0xFF1A1F36)
private val SelectedBg = Color(0xFF272347)
private val BorderColor = Color(0xFF303853)

private val Poppins: FontFamily = FontFamily.SansSerif
private val Inter: FontFamily = FontFamily.SansSerif

private class Goal(
    val title: String,
    val subtitle: String,
    val icon: DrawScope.(Float) -> Unit
)

private val goals = listOf(
    Goal("Ace technical interviews", "Patterns, practice, and confidence") { d -> drawBriefcase(d) },
    Goal("Build foundations", "Core concepts, one step at a time") { d -> drawGradCap(d) },
    Goal("Level up at work", "Skills that grow with your career") { d -> drawRocket(d) }
)

private val levels = listOf("Beginner", "Some experience", "Advanced")

private val levelNotes = listOf(
    "New to algorithms? We’ll start from the very beginning, one small step at a time.",
    "You know the basics. We’ll turn that knowledge into problem-solving instincts.",
    "You’re comfortable with the fundamentals. Let’s sharpen speed, patterns, and depth."
)

private val commitments = listOf(
    "10 min" to "Casual",
    "15 min" to "Steady",
    "30 min" to "Focused"
)

@Composable
fun PersonalizeScreen(
    authViewModel: AuthViewModel? = null,
    onBuildPath: (goal: String, level: String, minutesPerDay: String) -> Unit = { _, _, _ -> }
) {
    var selectedGoal by rememberSaveable { mutableIntStateOf(0) }
    var selectedLevel by rememberSaveable { mutableIntStateOf(1) }
    var selectedTime by rememberSaveable { mutableIntStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "A few details. A path built around you.\nYou can change these anytime.",
            style = TextStyle(
                fontFamily = Inter,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = TextSecondary
            )
        )

        SectionTitle("What brings you here?")

        // Selected goal (large card)
        val cardShape = RoundedCornerShape(16.dp)
        val selected = goals[selectedGoal]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(SelectedBg)
                .border(1.dp, Color.White.copy(alpha = 0.07f), cardShape)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GoalIcon(selected, 24.dp)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = selected.title,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                )
                Text(
                    text = selected.subtitle,
                    style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = TextSecondary)
                )
            }
            IconCanvas(20.dp) { d -> drawCircleCheck(d) }
        }

        // Other goals (small cards)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            goals.indices.filter { it != selectedGoal }.forEach { index ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .clip(cardShape)
                        .background(CardBg)
                        .clickable { selectedGoal = index }
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GoalIcon(goals[index], 20.dp)
                    Text(
                        text = goals[index].title,
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                    )
                }
            }
        }

        SectionTitle("Where are you starting?")

        // Segmented control
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardBg)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            levels.forEachIndexed { index, label ->
                val active = index == selectedLevel
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (active) Purple else Color.Transparent)
                        .clickable { selectedLevel = index },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        maxLines = 1,
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = if (active) Color.White else TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }

        Text(
            text = levelNotes[selectedLevel],
            style = TextStyle(
                fontFamily = Inter,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = TextSecondary
            )
        )

        SectionTitle("Your daily commitment")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            commitments.forEachIndexed { index, (time, label) ->
                val active = index == selectedTime
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(cardShape)
                        .background(if (active) SelectedBg else CardBg)
                        .border(1.dp, if (active) Purple else BorderColor, cardShape)
                        .clickable { selectedTime = index }
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = time,
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = label,
                        style = TextStyle(
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            color = if (active) PurpleSoft else TextSecondary
                        )
                    )
                }
            }
        }

        // Build path button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Purple)
                .clickable {
                    val goal = goals[selectedGoal].title
                    val level = levels[selectedLevel]
                    val timeStr = commitments[selectedTime].first
                    val minutes = timeStr.replace(" min", "").toIntOrNull() ?: 15
                    if (authViewModel != null) {
                        authViewModel.personalize(goal, level, minutes) {
                            onBuildPath(goal, level, timeStr)
                        }
                    } else {
                        onBuildPath(goal, level, timeStr)
                    }
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconCanvas(18.dp) { d -> drawSparkles(d) }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Build my learning path",
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = Color.White
                )
            )
        }

        Text(
            text = "Personalized for you. Powered by Quest AI.",
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                fontFamily = Inter,
                fontSize = 11.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = TextPrimary
        )
    )
}

@Composable
private fun GoalIcon(goal: Goal, iconSize: Dp) {
    val scale = iconSize.value / 20f
    Canvas(Modifier.size(iconSize)) {
        goal.icon(this, density * scale)
    }
}

// ---------- Small icons drawn in code (all designed in a 20 x 20 box) ----------

@Composable
private fun IconCanvas(size: Dp, draw: DrawScope.(Float) -> Unit) {
    Canvas(Modifier.size(size)) { draw(this, density * (size.value / 20f)) }
}

private fun lineStroke(d: Float, w: Float = 1.5f) =
    Stroke(width = w * d, cap = StrokeCap.Round, join = StrokeJoin.Round)

private fun DrawScope.drawBriefcase(d: Float) {
    val c = PurpleSoft
    drawRoundRect(
        color = c, topLeft = Offset(2.5f * d, 6.5f * d), size = Size(15f * d, 10.5f * d),
        cornerRadius = CornerRadius(2f * d), style = lineStroke(d)
    )
    drawLine(c, Offset(7.5f * d, 6.5f * d), Offset(7.5f * d, 4.5f * d), 1.5f * d, StrokeCap.Round)
    drawLine(c, Offset(7.5f * d, 4.5f * d), Offset(12.5f * d, 4.5f * d), 1.5f * d, StrokeCap.Round)
    drawLine(c, Offset(12.5f * d, 4.5f * d), Offset(12.5f * d, 6.5f * d), 1.5f * d, StrokeCap.Round)
    drawLine(c, Offset(2.5f * d, 11f * d), Offset(17.5f * d, 11f * d), 1.5f * d, StrokeCap.Round)
}

private fun DrawScope.drawGradCap(d: Float) {
    val c = PurpleSoft
    val top = Path().apply {
        moveTo(10f * d, 4f * d)
        lineTo(18f * d, 8f * d)
        lineTo(10f * d, 12f * d)
        lineTo(2f * d, 8f * d)
        close()
    }
    drawPath(top, c, style = lineStroke(d))
    val base = Path().apply {
        moveTo(5f * d, 10.5f * d)
        lineTo(5f * d, 13.5f * d)
        quadraticTo(10f * d, 17f * d, 15f * d, 13.5f * d)
        lineTo(15f * d, 10.5f * d)
    }
    drawPath(base, c, style = lineStroke(d))
    drawLine(c, Offset(18f * d, 8f * d), Offset(18f * d, 13f * d), 1.5f * d, StrokeCap.Round)
}

private fun DrawScope.drawRocket(d: Float) {
    val c = PurpleSoft
    val body = Path().apply {
        moveTo(10f * d, 2.5f * d)
        cubicTo(14f * d, 4.5f * d, 15f * d, 9f * d, 13f * d, 13f * d)
        lineTo(7f * d, 13f * d)
        cubicTo(5f * d, 9f * d, 6f * d, 4.5f * d, 10f * d, 2.5f * d)
        close()
    }
    drawPath(body, c, style = lineStroke(d))
    drawCircle(c, radius = 1.3f * d, center = Offset(10f * d, 8f * d), style = lineStroke(d))
    drawLine(c, Offset(7f * d, 13f * d), Offset(4.5f * d, 15.5f * d), 1.5f * d, StrokeCap.Round)
    drawLine(c, Offset(13f * d, 13f * d), Offset(15.5f * d, 15.5f * d), 1.5f * d, StrokeCap.Round)
    drawLine(c, Offset(10f * d, 14.5f * d), Offset(10f * d, 17.5f * d), 1.5f * d, StrokeCap.Round)
}

private fun DrawScope.drawCircleCheck(d: Float) {
    val c = PurpleSoft
    drawCircle(c, radius = 8.5f * d, center = Offset(10f * d, 10f * d), style = lineStroke(d))
    drawLine(c, Offset(6.5f * d, 10f * d), Offset(9f * d, 12.5f * d), 1.5f * d, StrokeCap.Round)
    drawLine(c, Offset(9f * d, 12.5f * d), Offset(13.5f * d, 7.5f * d), 1.5f * d, StrokeCap.Round)
}

private fun DrawScope.drawSparkles(d: Float) {
    drawStar(d, Color.White, cx = 8f, cy = 12f, r = 6.5f)
    drawStar(d, Color.White, cx = 15.5f, cy = 4.8f, r = 3.3f)
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
        lineTo((cx - r) * d, cy * d)
        lineTo((cx - i) * d, (cy - i) * d)
        close()
    }
    drawPath(path, color, style = lineStroke(d, 1.4f))
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PersonalizeScreenPreview() {
    PersonalizeScreen()
}
