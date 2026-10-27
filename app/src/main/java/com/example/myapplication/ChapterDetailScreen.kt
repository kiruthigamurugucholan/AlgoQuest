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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF0F1220)
private val TextPrimary = Color(0xFFF4F6FF)
private val TextSecondary = Color(0xFFC3CBE2)
private val TextMuted = Color(0xFF98A2C0)
private val Cyan = Color(0xFF00D2FF)
private val Purple = Color(0xFF6C5CE7)
private val PurpleSoft = Color(0xFFB5A7FF)
private val Green = Color(0xFF2ECC71)
private val Gold = Color(0xFFFFB84D)
private val CardBg = Color(0xFF1A1F36)
private val ActiveCardBg = Color(0xFF25213E)

private val Poppins: FontFamily = FontFamily.SansSerif
private val Inter: FontFamily = FontFamily.SansSerif
private val Mono: FontFamily = FontFamily.Monospace

data class LessonItem(
    val id: Int,
    val title: String,
    val duration: String,
    val xp: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean,
    val isLocked: Boolean
)

@Composable
fun ChapterDetailScreen(
    onBack: () -> Unit = {},
    onStartLesson: (lessonId: Int) -> Unit = {},
    onVisualize: () -> Unit = {},
    onAskAi: () -> Unit = {},
    onSelectTab: (Int) -> Unit = {}
) {
    val lessons = listOf(
        LessonItem(1, "Linear vs Binary Search: The 1M Step Difference", "8 min", "+100 XP", isCompleted = true, isCurrent = false, isLocked = false),
        LessonItem(2, "Binary Search Core Implementation & Invariants", "12 min", "+150 XP", isCompleted = true, isCurrent = false, isLocked = false),
        LessonItem(3, "Finding First & Last Position of Target", "15 min", "+150 XP", isCompleted = false, isCurrent = true, isLocked = false),
        LessonItem(4, "Search in Rotated Sorted Array", "18 min", "+200 XP", isCompleted = false, isCurrent = false, isLocked = true),
        LessonItem(5, "Chapter 03 Mastery Test & Challenge", "10 min", "+300 XP", isCompleted = false, isCurrent = false, isLocked = true)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CardBg)
                            .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center
                    ) {
                        IconCanvas(16.dp) { d -> drawChevronLeft(d) }
                    }
                    Text(
                        text = "Chapter Details",
                        style = TextStyle(
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                    )
                }
                Badge("CHAPTER 03", Purple.copy(alpha = 0.12f), PurpleSoft)
            }

            // Chapter Hero Card
            val heroShape = RoundedCornerShape(20.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(heroShape)
                    .background(ActiveCardBg)
                    .border(1.dp, Color.White.copy(alpha = 0.08f), heroShape)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Badge("SEARCHING ALGORITHMS", Cyan.copy(alpha = 0.12f), Cyan)
                    Text(text = "+900 XP Total", style = TextStyle(fontFamily = Mono, fontSize = 12.sp, color = Gold))
                }

                Text(
                    text = "Divide & Conquer Search",
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        color = TextPrimary
                    )
                )

                Text(
                    text = "Learn how cutting your search space in half reduces O(N) linear lookups into ultra-fast O(log N) operations.",
                    style = TextStyle(fontFamily = Inter, fontSize = 13.sp, lineHeight = 19.sp, color = TextSecondary)
                )

                // Progress bar
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "2 of 5 lessons complete", style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = TextSecondary))
                        Text(text = "40%", style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Cyan))
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.07f))
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth(0.4f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(Cyan)
                        )
                    }
                }

                // Quick actions inside hero
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Purple)
                            .clickable(onClick = onVisualize),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconCanvas(16.dp) { d -> drawEye(d) }
                        Spacer(Modifier.width(6.dp))
                        Text(text = "Visualize Lab", style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = Color.White))
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardBg)
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                            .clickable(onClick = onAskAi),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconCanvas(16.dp) { d -> drawSparkles(d, PurpleSoft) }
                        Spacer(Modifier.width(6.dp))
                        Text(text = "Ask AI Ally", style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = TextPrimary))
                    }
                }
            }

            Text(
                text = "Lesson Roadmap",
                style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextPrimary)
            )

            // Lessons List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                lessons.forEach { lesson ->
                    LessonRowItem(
                        lesson = lesson,
                        onClick = { onStartLesson(lesson.id) }
                    )
                }
            }
        }

        // Shared Bottom Navigation Bar
        AlgoQuestBottomBar(selectedTab = 1, onSelectTab = onSelectTab)
    }
}

@Composable
private fun LessonRowItem(
    lesson: LessonItem,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(if (lesson.isCurrent) ActiveCardBg else CardBg)
            .border(
                1.dp,
                when {
                    lesson.isCurrent -> Cyan
                    lesson.isCompleted -> Green.copy(alpha = 0.3f)
                    else -> Color.White.copy(alpha = 0.05f)
                },
                cardShape
            )
            .clickable(enabled = !lesson.isLocked, onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status indicator icon
        val iconBg = when {
            lesson.isCompleted -> Green.copy(alpha = 0.15f)
            lesson.isCurrent -> Cyan.copy(alpha = 0.15f)
            else -> Color.White.copy(alpha = 0.05f)
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            when {
                lesson.isCompleted -> IconCanvas(16.dp) { d -> drawCheck(d) }
                lesson.isCurrent -> IconCanvas(16.dp) { d -> drawPlay(d) }
                else -> IconCanvas(14.dp) { d -> drawLock(d) }
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = "Lesson ${lesson.id.toString().padStart(2, '0')}",
                style = TextStyle(fontFamily = Mono, fontSize = 10.sp, color = TextMuted)
            )
            Text(
                text = lesson.title,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = if (lesson.isLocked) TextMuted else TextPrimary
                )
            )
            Text(
                text = "${lesson.duration} · ${lesson.xp}",
                style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = TextSecondary)
            )
        }

        if (!lesson.isLocked) {
            IconCanvas(16.dp) { d -> drawChevronRight(d) }
        }
    }
}

@Composable
private fun Badge(text: String, background: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .height(24.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Medium, fontSize = 10.sp, color = textColor)
        )
    }
}

// ---------- Canvas icons ----------

@Composable
private fun IconCanvas(size: Dp, draw: DrawScope.(Float) -> Unit) {
    Canvas(Modifier.size(size)) { draw(this, density) }
}

private fun lineStroke(d: Float, w: Float = 1.5f) =
    Stroke(width = w * d, cap = StrokeCap.Round, join = StrokeJoin.Round)

private fun DrawScope.drawChevronLeft(d: Float) {
    val w = 1.8f * d
    drawLine(TextPrimary, Offset(11f * d, 4f * d), Offset(5f * d, 10f * d), w, StrokeCap.Round)
    drawLine(TextPrimary, Offset(5f * d, 10f * d), Offset(11f * d, 16f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawChevronRight(d: Float) {
    val w = 1.6f * d
    drawLine(TextSecondary, Offset(5f * d, 3f * d), Offset(10f * d, 8f * d), w, StrokeCap.Round)
    drawLine(TextSecondary, Offset(10f * d, 8f * d), Offset(5f * d, 13f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawCheck(d: Float) {
    val w = 2f * d
    drawLine(Green, Offset(4f * d, 9f * d), Offset(7f * d, 12f * d), w, StrokeCap.Round)
    drawLine(Green, Offset(7f * d, 12f * d), Offset(13f * d, 5f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawPlay(d: Float) {
    val path = Path().apply {
        moveTo(6f * d, 4f * d)
        lineTo(14f * d, 9f * d)
        lineTo(6f * d, 14f * d)
        close()
    }
    drawPath(path, Cyan)
}

private fun DrawScope.drawLock(d: Float) {
    val c = TextMuted
    drawArc(c, 180f, 180f, false, Offset(4f * d, 2f * d), Size(6f * d, 6f * d), style = lineStroke(d))
    drawRect(c, Offset(3f * d, 7f * d), Size(8f * d, 6f * d), style = lineStroke(d))
}

private fun DrawScope.drawEye(d: Float) {
    val c = Color.White
    drawCircle(c, radius = 2.5f * d, center = Offset(8f * d, 8f * d), style = lineStroke(d))
    drawArc(c, 210f, 120f, false, Offset(2f * d, 4f * d), Size(12f * d, 8f * d), style = lineStroke(d))
    drawArc(c, 30f, 120f, false, Offset(2f * d, 4f * d), Size(12f * d, 8f * d), style = lineStroke(d))
}

private fun DrawScope.drawSparkles(d: Float, color: Color) {
    drawStar(d, color, cx = 8f, cy = 11f, r = 5.5f)
    drawStar(d, color, cx = 14f, cy = 4.5f, r = 2.8f)
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
    drawPath(path, color, style = lineStroke(d, 1.2f))
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ChapterDetailScreenPreview() {
    ChapterDetailScreen()
}
