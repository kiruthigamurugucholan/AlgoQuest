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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.viewmodel.DashboardUiState
import com.example.myapplication.viewmodel.DashboardViewModel

private val Bg = Color(0xFF0F1220)
private val TextPrimary = Color(0xFFF4F6FF)
private val TextSecondary = Color(0xFFC3CBE2)
private val Cyan = Color(0xFF00D2FF)
private val Purple = Color(0xFF6C5CE7)
private val PurpleSoft = Color(0xFFB5A7FF)
private val LinkColor = Color(0xFFAA9FFF)
private val Gold = Color(0xFFFFB84D)
private val CardBg = Color(0xFF1A1F36)
private val QuestCardBg = Color(0xFF25213E)

private val Poppins: FontFamily = FontFamily.SansSerif
private val Inter: FontFamily = FontFamily.SansSerif
private val Mono: FontFamily = FontFamily.Monospace

@Composable
fun HomeScreen(
    dashboardViewModel: DashboardViewModel? = null,
    onContinueQuest: () -> Unit = {},
    onQuestMap: () -> Unit = {},
    onOpenChapter: () -> Unit = {},
    onAskAi: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenVisualizer: () -> Unit = {},
    onSelectTab: (Int) -> Unit = {}
) {
    LaunchedEffect(Unit) {
        dashboardViewModel?.loadDashboard()
    }

    val uiState by dashboardViewModel?.uiState?.collectAsState() ?: androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf(null)
    }

    val dashboardData = (uiState as? DashboardUiState.Success)?.data

    val userName = dashboardData?.user?.name ?: "Explorer"
    val userLevel = dashboardData?.user?.level ?: 8
    val userStreak = dashboardData?.user?.streak ?: 7
    val userXp = dashboardData?.user?.xp?.let { String.format("%,d", it) } ?: "2,450"
    val percentile = dashboardData?.user?.percentile ?: "top 12%"

    val questTitle = dashboardData?.dailyQuest?.title ?: "Find it. Faster."
    val questDesc = dashboardData?.dailyQuest?.description ?: "Master binary search, visualize the logic,\nthen put your instincts to the test."
    val questXp = dashboardData?.dailyQuest?.xpReward ?: 150
    val questStep = dashboardData?.dailyQuest?.currentStep ?: 1
    val questTotalSteps = dashboardData?.dailyQuest?.requiredSteps ?: 3
    val questProgress = (questStep.toFloat() / questTotalSteps.toFloat()).coerceIn(0.1f, 1f)

    val activeTopicTitle = dashboardData?.activeTopic?.title ?: "Searching algorithms"
    val activeTopicCompleted = dashboardData?.activeTopic?.completedLessons ?: 2
    val activeTopicTotal = dashboardData?.activeTopic?.totalLessons ?: 5
    val activeTopicSubtitle = "Chapter 03 · $activeTopicCompleted of $activeTopicTotal lessons complete"

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
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Level status & profile header trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Welcome back, $userName.",
                    style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = TextSecondary)
                )
                Box(
                    modifier = Modifier.clickable(onClick = onOpenProfile)
                ) {
                    Badge(String.format("LVL %02d", userLevel), Purple.copy(alpha = 0.09f), PurpleSoft)
                }
            }

            // Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard("$userStreak 🔥", "day streak", Gold, Modifier.weight(1f).clickable(onClick = onOpenProfile))
                MetricCard(userXp, "total XP · $percentile", Cyan, Modifier.weight(1f).clickable(onClick = onOpenProfile))
            }

            // Daily quest card
            val questShape = RoundedCornerShape(16.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(questShape)
                    .background(QuestCardBg)
                    .border(1.dp, Color.White.copy(alpha = 0.07f), questShape)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Badge("DAILY QUEST", Purple.copy(alpha = 0.09f), PurpleSoft)
                    Text(
                        text = "+$questXp XP",
                        style = TextStyle(fontFamily = Mono, fontSize = 12.sp, color = Gold)
                    )
                }
                Text(
                    text = questTitle,
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp,
                        lineHeight = 29.sp,
                        color = TextPrimary
                    )
                )
                Text(
                    text = questDesc,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = TextSecondary
                    )
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
                            .fillMaxWidth(questProgress)
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
                        text = "$questStep of $questTotalSteps steps complete",
                        style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = TextSecondary)
                    )
                    Text(
                        text = "~12 min left",
                        style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = TextSecondary)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Purple)
                        .clickable(onClick = onContinueQuest),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconCanvas(18.dp) { d -> drawArrowRight(d) }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Continue quest",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    )
                }
            }

            // Section heading
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your learning path",
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "Quest map",
                    modifier = Modifier.clickable(onClick = onQuestMap),
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = LinkColor
                    )
                )
            }

            PathCard(
                title = activeTopicTitle,
                subtitle = activeTopicSubtitle,
                tileColor = Cyan.copy(alpha = 0.11f),
                onClick = onOpenChapter
            ) { d -> drawScanSearch(d) }

            PathCard(
                title = "Interactive Visualizer Lab",
                subtitle = "Step through Binary Search & Sorting",
                tileColor = Gold.copy(alpha = 0.11f),
                onClick = onOpenVisualizer
            ) { d -> drawLab(d) }

            PathCard(
                title = "Your next breakthrough?",
                subtitle = "Ask Quest AI to explain any concept",
                tileColor = Purple.copy(alpha = 0.11f),
                onClick = onAskAi
            ) { d -> drawSparkles(d, PurpleSoft) }
        }

        // Shared Reusable Bottom Navigation Bar
        AlgoQuestBottomBar(selectedTab = 0, onSelectTab = onSelectTab)
    }
}

@Composable
private fun MetricCard(value: String, label: String, valueColor: Color, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(CardBg)
            .border(1.dp, Color.White.copy(alpha = 0.06f), shape)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            style = TextStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 23.sp,
                lineHeight = 30.sp,
                color = valueColor
            )
        )
        Text(
            text = label,
            style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = TextSecondary)
        )
    }
}

@Composable
private fun PathCard(
    title: String,
    subtitle: String,
    tileColor: Color,
    onClick: () -> Unit,
    icon: DrawScope.(Float) -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(Color(0xFF252B46), CardBg)))
            .border(1.dp, Color.White.copy(alpha = 0.07f), shape)
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(tileColor),
            contentAlignment = Alignment.Center
        ) {
            IconCanvas(20.dp, icon)
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
            )
            Text(
                text = subtitle,
                style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = TextSecondary)
            )
        }
        IconCanvas(16.dp) { d -> drawChevronRight(d) }
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

private fun lineStroke(d: Float, w: Float = 1.5f) =
    Stroke(width = w * d, cap = StrokeCap.Round, join = StrokeJoin.Round)

private fun DrawScope.drawArrowRight(d: Float) {
    val w = 2f * d
    drawLine(Color.White, Offset(3f * d, 9f * d), Offset(15f * d, 9f * d), w, StrokeCap.Round)
    drawLine(Color.White, Offset(10f * d, 4f * d), Offset(15f * d, 9f * d), w, StrokeCap.Round)
    drawLine(Color.White, Offset(10f * d, 14f * d), Offset(15f * d, 9f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawChevronRight(d: Float) {
    val w = 1.6f * d
    drawLine(TextSecondary, Offset(6f * d, 3f * d), Offset(11f * d, 8f * d), w, StrokeCap.Round)
    drawLine(TextSecondary, Offset(11f * d, 8f * d), Offset(6f * d, 13f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawScanSearch(d: Float) {
    val c = Cyan
    val w = 1.5f * d
    drawLine(c, Offset(2.5f * d, 7f * d), Offset(2.5f * d, 2.5f * d), w, StrokeCap.Round)
    drawLine(c, Offset(2.5f * d, 2.5f * d), Offset(7f * d, 2.5f * d), w, StrokeCap.Round)
    drawLine(c, Offset(13f * d, 2.5f * d), Offset(17.5f * d, 2.5f * d), w, StrokeCap.Round)
    drawLine(c, Offset(17.5f * d, 2.5f * d), Offset(17.5f * d, 7f * d), w, StrokeCap.Round)
    drawLine(c, Offset(17.5f * d, 13f * d), Offset(17.5f * d, 17.5f * d), w, StrokeCap.Round)
    drawLine(c, Offset(17.5f * d, 17.5f * d), Offset(13f * d, 17.5f * d), w, StrokeCap.Round)
    drawLine(c, Offset(7f * d, 17.5f * d), Offset(2.5f * d, 17.5f * d), w, StrokeCap.Round)
    drawLine(c, Offset(2.5f * d, 17.5f * d), Offset(2.5f * d, 13f * d), w, StrokeCap.Round)
    drawCircle(c, radius = 3f * d, center = Offset(9.5f * d, 9.5f * d), style = lineStroke(d))
    drawLine(c, Offset(11.8f * d, 11.8f * d), Offset(14f * d, 14f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawLab(d: Float) {
    val c = Gold
    drawLine(c, Offset(4f * d, 15f * d), Offset(4f * d, 10f * d), 2f * d, StrokeCap.Round)
    drawLine(c, Offset(9f * d, 15f * d), Offset(9f * d, 5f * d), 2f * d, StrokeCap.Round)
    drawLine(c, Offset(14f * d, 15f * d), Offset(14f * d, 2f * d), 2f * d, StrokeCap.Round)
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
        lineTo((cx - r) * d, cy * d)
        lineTo((cx - i) * d, (cy - i) * d)
        close()
    }
    drawPath(path, color, style = lineStroke(d, 1.4f))
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}
