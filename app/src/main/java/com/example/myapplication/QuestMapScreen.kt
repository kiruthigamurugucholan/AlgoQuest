package com.example.myapplication

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.PathEffect
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

enum class NodeStatus { COMPLETED, ACTIVE, NEXT, LOCKED }

data class QuestNode(
    val id: Int,
    val title: String,
    val chapter: String,
    val lessons: String,
    val xp: String,
    val status: NodeStatus,
    val icon: DrawScope.(Float) -> Unit
)

@Composable
fun QuestMapScreen(
    onBack: () -> Unit = {},
    onSelectChapter: (chapterId: Int) -> Unit = {},
    onSelectTab: (Int) -> Unit = {}
) {
    val nodes = listOf(
        QuestNode(1, "Arrays & Memory", "Chapter 01", "4/4 Lessons", "+100 XP", NodeStatus.COMPLETED) { d -> drawArrayIcon(d) },
        QuestNode(2, "Two Pointers & Sliding", "Chapter 02", "5/5 Lessons", "+150 XP", NodeStatus.COMPLETED) { d -> drawPointerIcon(d) },
        QuestNode(3, "Searching Algorithms", "Chapter 03", "2/5 Lessons", "+200 XP", NodeStatus.ACTIVE) { d -> drawSearchIcon(d) },
        QuestNode(4, "Sorting & Divide/Conquer", "Chapter 04", "0/5 Lessons", "+250 XP", NodeStatus.NEXT) { d -> drawSortIcon(d) },
        QuestNode(5, "Linked Lists & Pointers", "Chapter 05", "Locked", "+300 XP", NodeStatus.LOCKED) { d -> drawLinkIcon(d) },
        QuestNode(6, "Trees & Binary Trees", "Chapter 06", "Locked", "+350 XP", NodeStatus.LOCKED) { d -> drawTreeIcon(d) },
        QuestNode(7, "Graphs & BFS / DFS", "Chapter 07", "Locked", "+400 XP", NodeStatus.LOCKED) { d -> drawGraphIcon(d) }
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
                    Column {
                        Text(
                            text = "Quest Map",
                            style = TextStyle(
                                fontFamily = Poppins,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Your personalized roadmap",
                            style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = TextSecondary)
                        )
                    }
                }
                Badge("2,450 XP", Gold.copy(alpha = 0.12f), Gold)
            }

            // Active Chapter Banner
            val bannerShape = RoundedCornerShape(16.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(bannerShape)
                    .background(ActiveCardBg)
                    .border(1.dp, Purple.copy(alpha = 0.4f), bannerShape)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Purple.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    IconCanvas(22.dp) { d -> drawSearchIcon(d) }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Badge("CURRENT QUEST", Purple.copy(alpha = 0.12f), PurpleSoft)
                    Text(
                        text = "Searching Algorithms",
                        style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
                    )
                    Text(
                        text = "Master Binary Search & Ternary Search",
                        style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = TextSecondary)
                    )
                }
            }

            Text(
                text = "Learning Nodes",
                style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
            )

            // Roadmap node tree
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                nodes.forEachIndexed { idx, node ->
                    RoadmapNodeItem(
                        node = node,
                        isLast = idx == nodes.size - 1,
                        onClick = { onSelectChapter(node.id) }
                    )
                }
            }
        }

        // Shared Bottom Navigation Bar
        AlgoQuestBottomBar(selectedTab = 1, onSelectTab = onSelectTab)
    }
}

@Composable
private fun RoadmapNodeItem(
    node: QuestNode,
    isLast: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = node.status != NodeStatus.LOCKED, onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Node Status Indicator & Connecting Line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            // Node icon bubble
            val circleColor = when (node.status) {
                NodeStatus.COMPLETED -> Green
                NodeStatus.ACTIVE -> Purple
                NodeStatus.NEXT -> Cyan
                NodeStatus.LOCKED -> TextMuted.copy(alpha = 0.3f)
            }

            val circleBg = when (node.status) {
                NodeStatus.COMPLETED -> Green.copy(alpha = 0.15f)
                NodeStatus.ACTIVE -> Purple.copy(alpha = 0.25f)
                NodeStatus.NEXT -> Cyan.copy(alpha = 0.15f)
                NodeStatus.LOCKED -> CardBg
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(circleBg)
                    .border(2.dp, circleColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                when (node.status) {
                    NodeStatus.COMPLETED -> IconCanvas(16.dp) { d -> drawCheck(d) }
                    NodeStatus.LOCKED -> IconCanvas(14.dp) { d -> drawLock(d) }
                    else -> IconCanvas(16.dp, node.icon)
                }
            }

            // Connecting line to next node
            if (!isLast) {
                Canvas(
                    modifier = Modifier
                        .width(2.dp)
                        .height(48.dp)
                ) {
                    val pathEffect = if (node.status == NodeStatus.LOCKED) PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f) else null
                    drawLine(
                        color = circleColor.copy(alpha = 0.4f),
                        start = Offset(1.dp.toPx(), 0f),
                        end = Offset(1.dp.toPx(), 48.dp.toPx()),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = pathEffect
                    )
                }
            }
        }

        // Node card info
        val cardShape = RoundedCornerShape(14.dp)
        val isCardActive = node.status == NodeStatus.ACTIVE
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(cardShape)
                .background(if (isCardActive) ActiveCardBg else CardBg)
                .border(
                    1.dp,
                    when (node.status) {
                        NodeStatus.ACTIVE -> Purple
                        NodeStatus.COMPLETED -> Green.copy(alpha = 0.3f)
                        NodeStatus.NEXT -> Cyan.copy(alpha = 0.3f)
                        NodeStatus.LOCKED -> Color.White.copy(alpha = 0.04f)
                    },
                    cardShape
                )
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = node.chapter,
                        style = TextStyle(fontFamily = Mono, fontSize = 10.sp, color = TextMuted)
                    )
                    Badge(
                        text = when (node.status) {
                            NodeStatus.COMPLETED -> "COMPLETED"
                            NodeStatus.ACTIVE -> "IN PROGRESS"
                            NodeStatus.NEXT -> "READY"
                            NodeStatus.LOCKED -> "LOCKED"
                        },
                        background = when (node.status) {
                            NodeStatus.COMPLETED -> Green.copy(alpha = 0.1f)
                            NodeStatus.ACTIVE -> Purple.copy(alpha = 0.15f)
                            NodeStatus.NEXT -> Cyan.copy(alpha = 0.1f)
                            NodeStatus.LOCKED -> CardBg
                        },
                        textColor = when (node.status) {
                            NodeStatus.COMPLETED -> Green
                            NodeStatus.ACTIVE -> PurpleSoft
                            NodeStatus.NEXT -> Cyan
                            NodeStatus.LOCKED -> TextMuted
                        }
                    )
                }

                Text(
                    text = node.title,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = if (node.status == NodeStatus.LOCKED) TextMuted else TextPrimary
                    )
                )

                Text(
                    text = "${node.lessons} · ${node.xp}",
                    style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = TextSecondary)
                )
            }

            if (node.status != NodeStatus.LOCKED) {
                IconCanvas(16.dp) { d -> drawChevronRight(d) }
            }
        }
    }
}

@Composable
private fun Badge(text: String, background: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .height(22.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(background)
            .padding(horizontal = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Medium, fontSize = 9.sp, color = textColor)
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

private fun DrawScope.drawLock(d: Float) {
    val c = TextMuted
    drawArc(c, 180f, 180f, false, Offset(4f * d, 2f * d), Size(6f * d, 6f * d), style = lineStroke(d))
    drawRect(c, Offset(3f * d, 7f * d), Size(8f * d, 6f * d), style = lineStroke(d))
}

private fun DrawScope.drawArrayIcon(d: Float) {
    val c = Green
    drawRect(c, Offset(2f * d, 5f * d), Size(12f * d, 6f * d), style = lineStroke(d))
    drawLine(c, Offset(6f * d, 5f * d), Offset(6f * d, 11f * d), 1.2f * d)
    drawLine(c, Offset(10f * d, 5f * d), Offset(10f * d, 11f * d), 1.2f * d)
}

private fun DrawScope.drawPointerIcon(d: Float) {
    val c = Green
    drawLine(c, Offset(3f * d, 8f * d), Offset(13f * d, 8f * d), 1.5f * d, StrokeCap.Round)
    drawLine(c, Offset(3f * d, 8f * d), Offset(6f * d, 5f * d), 1.5f * d, StrokeCap.Round)
    drawLine(c, Offset(13f * d, 8f * d), Offset(10f * d, 11f * d), 1.5f * d, StrokeCap.Round)
}

private fun DrawScope.drawSearchIcon(d: Float) {
    val c = PurpleSoft
    drawCircle(c, radius = 4f * d, center = Offset(7f * d, 7f * d), style = lineStroke(d))
    drawLine(c, Offset(10f * d, 10f * d), Offset(14f * d, 14f * d), 1.8f * d, StrokeCap.Round)
}

private fun DrawScope.drawSortIcon(d: Float) {
    val c = Cyan
    drawLine(c, Offset(3f * d, 13f * d), Offset(3f * d, 9f * d), 2f * d, StrokeCap.Round)
    drawLine(c, Offset(8f * d, 13f * d), Offset(8f * d, 5f * d), 2f * d, StrokeCap.Round)
    drawLine(c, Offset(13f * d, 13f * d), Offset(13f * d, 2f * d), 2f * d, StrokeCap.Round)
}

private fun DrawScope.drawLinkIcon(d: Float) {
    val c = TextMuted
    drawCircle(c, radius = 2.5f * d, center = Offset(5f * d, 8f * d), style = lineStroke(d))
    drawCircle(c, radius = 2.5f * d, center = Offset(11f * d, 8f * d), style = lineStroke(d))
    drawLine(c, Offset(7.5f * d, 8f * d), Offset(8.5f * d, 8f * d), 1.5f * d)
}

private fun DrawScope.drawTreeIcon(d: Float) {
    val c = TextMuted
    drawCircle(c, radius = 2f * d, center = Offset(8f * d, 3f * d), style = lineStroke(d))
    drawCircle(c, radius = 2f * d, center = Offset(4f * d, 11f * d), style = lineStroke(d))
    drawCircle(c, radius = 2f * d, center = Offset(12f * d, 11f * d), style = lineStroke(d))
    drawLine(c, Offset(8f * d, 5f * d), Offset(4f * d, 9f * d), 1.2f * d)
    drawLine(c, Offset(8f * d, 5f * d), Offset(12f * d, 9f * d), 1.2f * d)
}

private fun DrawScope.drawGraphIcon(d: Float) {
    val c = TextMuted
    drawCircle(c, radius = 1.8f * d, center = Offset(4f * d, 4f * d), style = lineStroke(d))
    drawCircle(c, radius = 1.8f * d, center = Offset(12f * d, 4f * d), style = lineStroke(d))
    drawCircle(c, radius = 1.8f * d, center = Offset(8f * d, 12f * d), style = lineStroke(d))
    drawLine(c, Offset(4f * d, 4f * d), Offset(12f * d, 4f * d), 1.2f * d)
    drawLine(c, Offset(4f * d, 4f * d), Offset(8f * d, 12f * d), 1.2f * d)
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun QuestMapScreenPreview() {
    QuestMapScreen()
}
