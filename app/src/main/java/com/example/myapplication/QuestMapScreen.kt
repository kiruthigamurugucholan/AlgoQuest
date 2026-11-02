package com.example.myapplication

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.AlgoTheme
import com.example.myapplication.viewmodel.LearningViewModel
import com.example.myapplication.viewmodel.TopicsUiState

private val Bg: Color @Composable get() = AlgoTheme.colors.bg
private val TextPrimary: Color @Composable get() = AlgoTheme.colors.textPrimary
private val TextSecondary: Color @Composable get() = AlgoTheme.colors.textSecondary
private val TextMuted: Color @Composable get() = AlgoTheme.colors.textMuted
private val Cyan: Color @Composable get() = AlgoTheme.colors.cyan
private val Purple: Color @Composable get() = AlgoTheme.colors.purple
private val PurpleSoft: Color @Composable get() = AlgoTheme.colors.purpleSoft
private val Green: Color @Composable get() = AlgoTheme.colors.green
private val Gold: Color @Composable get() = AlgoTheme.colors.gold
private val CardBg: Color @Composable get() = AlgoTheme.colors.cardBg
private val ActiveCardBg: Color @Composable get() = AlgoTheme.colors.activeCardBg
private val BorderColor: Color @Composable get() = AlgoTheme.colors.border

private val Poppins: FontFamily = FontFamily.SansSerif
private val Inter: FontFamily = FontFamily.SansSerif
private val Mono: FontFamily = FontFamily.Monospace

enum class NodeStatus { COMPLETED, ACTIVE, NEXT, LOCKED }

data class QuestNode(
    val id: Int,
    val chapterNumber: Int,
    val title: String,
    val subtitle: String,
    val chapter: String,
    val lessons: String,
    val completedLessons: Int,
    val totalLessons: Int,
    val totalXp: Int,
    val xp: String,
    val status: NodeStatus,
    val iconType: String,
    val icon: DrawScope.(Float) -> Unit
)

@Composable
fun QuestMapScreen(
    learningViewModel: LearningViewModel? = null,
    onBack: () -> Unit = {},
    onSelectChapter: (chapterId: Int) -> Unit = {},
    onSelectTab: (Int) -> Unit = {},
    showBottomBar: Boolean = true
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        learningViewModel?.loadTopics()
    }

    val topicsState by learningViewModel?.topicsState?.collectAsState() ?: remember {
        mutableStateOf(null)
    }

    val dynamicTopics = (topicsState as? TopicsUiState.Success)?.topics

    val textPrimary = TextPrimary
    val textSecondary = TextSecondary
    val textMuted = TextMuted
    val purple = Purple
    val purpleSoft = PurpleSoft
    val cyan = Cyan
    val green = Green
    val gold = Gold
    val borderColor = BorderColor
    val isDark = AlgoTheme.colors.isDark

    fun getIconForType(type: String): DrawScope.(Float) -> Unit = when (type) {
        "array" -> { d -> drawArrayIcon(d, green) }
        "pointer" -> { d -> drawPointerIcon(d, green) }
        "search" -> { d -> drawSearchIcon(d, purpleSoft) }
        "sort" -> { d -> drawSortIcon(d, cyan) }
        "link" -> { d -> drawLinkIcon(d, textMuted) }
        "tree" -> { d -> drawTreeIcon(d, textMuted) }
        else -> { d -> drawGraphIcon(d, textMuted) }
    }

    fun parseStatus(s: String): NodeStatus = when (s.uppercase()) {
        "COMPLETED" -> NodeStatus.COMPLETED
        "ACTIVE" -> NodeStatus.ACTIVE
        "NEXT" -> NodeStatus.NEXT
        else -> NodeStatus.LOCKED
    }

    val nodes = dynamicTopics?.map { dt ->
        QuestNode(
            id = dt.id,
            chapterNumber = dt.chapterNumber,
            title = dt.title,
            subtitle = dt.subtitle,
            chapter = dt.chapterLabel,
            lessons = dt.lessonsLabel,
            completedLessons = dt.completedLessons,
            totalLessons = dt.totalLessons,
            totalXp = dt.totalXp,
            xp = "+${dt.totalXp} XP",
            status = parseStatus(dt.status),
            iconType = dt.iconType,
            icon = getIconForType(dt.iconType)
        )
    } ?: listOf(
        QuestNode(1, 1, "Arrays & Memory", "Continuous buffers, indexing, and memory layout", "Chapter 01", "4/4 Lessons", 4, 4, 400, "+400 XP", NodeStatus.COMPLETED, "array") { d -> drawArrayIcon(d, green) },
        QuestNode(2, 2, "Two Pointers & Sliding", "Opposite ends, fast/slow runners, and window bounds", "Chapter 02", "5/5 Lessons", 5, 5, 500, "+500 XP", NodeStatus.COMPLETED, "pointer") { d -> drawPointerIcon(d, green) },
        QuestNode(3, 3, "Searching Algorithms", "Linear scan vs binary search cuts the world in half", "Chapter 03", "2/5 Lessons", 2, 5, 900, "+900 XP", NodeStatus.ACTIVE, "search") { d -> drawSearchIcon(d, purpleSoft) },
        QuestNode(4, 4, "Sorting & Divide/Conquer", "Divide, sort subproblems, and merge in O(N log N)", "Chapter 04", "0/5 Lessons", 0, 5, 650, "+650 XP", NodeStatus.NEXT, "sort") { d -> drawSortIcon(d, cyan) },
        QuestNode(5, 5, "Linked Lists & Pointers", "Nodes, references, cycle detection, and reversal", "Chapter 05", "0/5 Lessons", 0, 5, 700, "+700 XP", NodeStatus.LOCKED, "link") { d -> drawLinkIcon(d, textMuted) },
        QuestNode(6, 6, "Trees & Binary Trees", "Hierarchical data, traversals, and BST properties", "Chapter 06", "0/5 Lessons", 0, 5, 800, "+800 XP", NodeStatus.LOCKED, "tree") { d -> drawTreeIcon(d, textMuted) },
        QuestNode(7, 7, "Graphs & BFS / DFS", "Vertices, edges, queues, stacks, and pathfinding", "Chapter 07", "0/5 Lessons", 0, 5, 1000, "+1000 XP", NodeStatus.LOCKED, "graph") { d -> drawGraphIcon(d, textMuted) }
    )

    // Active Chapter Node (for Current Quest Card)
    val activeNode = nodes.firstOrNull { it.status == NodeStatus.ACTIVE }
        ?: nodes.firstOrNull { it.status == NodeStatus.NEXT }
        ?: nodes.firstOrNull()

    // Inline banner notice when tapping a locked chapter
    var lockedMessage by remember { mutableStateOf<String?>(null) }

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
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Header: Back navigation, Title & Live Syllabus badge
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
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CardBg)
                            .border(
                                1.dp,
                                if (isDark) Color.White.copy(alpha = 0.08f) else borderColor,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center
                    ) {
                        IconCanvas(16.dp) { d -> drawChevronLeft(d, textPrimary) }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Quest Map",
                            style = TextStyle(
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = textPrimary
                            )
                        )
                        Text(
                            text = "Adventure Roadmap · 7 Chapters",
                            style = TextStyle(
                                fontFamily = Inter,
                                fontSize = 11.5.sp,
                                color = textSecondary
                            )
                        )
                    }
                }
                // Preserved Live Syllabus Badge
                Badge("LIVE SYLLABUS", gold.copy(alpha = 0.12f), gold)
            }

            // 1. Current Quest Card (Hero Banner)
            if (activeNode != null) {
                val bannerShape = RoundedCornerShape(18.dp)
                val activeProgress = if (activeNode.totalLessons > 0) {
                    (activeNode.completedLessons.toFloat() / activeNode.totalLessons.toFloat()).coerceIn(0.05f, 1f)
                } else 0.05f

                val animatedActiveProgress by animateFloatAsState(
                    targetValue = activeProgress,
                    animationSpec = tween(650, easing = FastOutSlowInEasing),
                    label = "activeQuestProgress"
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(bannerShape)
                        .background(ActiveCardBg)
                        .border(
                            width = 1.5.dp,
                            brush = if (isDark) {
                                Brush.horizontalGradient(listOf(purple, cyan.copy(alpha = 0.8f)))
                            } else {
                                Brush.linearGradient(listOf(borderColor, borderColor))
                            },
                            shape = bannerShape
                        )
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Tag row: CURRENT QUEST badge and actual XP reward
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Badge("CURRENT QUEST", purple.copy(alpha = 0.18f), purpleSoft)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(gold.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = activeNode.xp,
                                style = TextStyle(
                                    fontFamily = Mono,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    color = gold
                                )
                            )
                        }
                    }

                    // Chapter Title, Icon and Subtitle
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(purple.copy(alpha = 0.2f))
                                .border(1.dp, cyan.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            IconCanvas(22.dp, activeNode.icon)
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = activeNode.title,
                                style = TextStyle(
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = textPrimary
                                )
                            )
                            Text(
                                text = "${activeNode.chapter} · ${activeNode.subtitle}",
                                style = TextStyle(
                                    fontFamily = Inter,
                                    fontSize = 12.sp,
                                    color = textSecondary
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Progress Bar & Lesson Count
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (isDark) Color.White.copy(alpha = 0.08f) else borderColor.copy(alpha = 0.5f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(animatedActiveProgress)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Brush.horizontalGradient(listOf(purple, cyan)))
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${activeNode.completedLessons} of ${activeNode.totalLessons} lessons completed",
                                style = TextStyle(
                                    fontFamily = Inter,
                                    fontSize = 11.sp,
                                    color = textSecondary
                                )
                            )
                            Text(
                                text = "${(activeProgress * 100).toInt()}% complete",
                                style = TextStyle(
                                    fontFamily = Inter,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp,
                                    color = cyan
                                )
                            )
                        }
                    }

                    // Prominent Continue Quest Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.horizontalGradient(listOf(purple, Color(0xFF7C6BF0))))
                            .clickable { onSelectChapter(activeNode.id) },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconCanvas(16.dp) { d -> drawArrowRight(d) }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Continue ${activeNode.chapter}",
                            style = TextStyle(
                                fontFamily = Inter,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        )
                    }
                }
            }

            // Locked Chapter Notice Alert Banner
            AnimatedVisibility(
                visible = lockedMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                lockedMessage?.let { msg ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2A2236))
                            .border(1.dp, purple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IconCanvas(16.dp) { d -> drawLock(d, gold) }
                        Text(
                            text = msg,
                            style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = textPrimary),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "DISMISS",
                            style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = cyan),
                            modifier = Modifier.clickable { lockedMessage = null }
                        )
                    }
                }
            }

            // Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Adventure Roadmap",
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = textPrimary
                    )
                )
                Text(
                    text = "${nodes.count { it.status == NodeStatus.COMPLETED }}/${nodes.size} Completed",
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = green
                    )
                )
            }

            // 2. Vertical Adventure Roadmap & Chapter Cards
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                nodes.forEachIndexed { idx, node ->
                    RoadmapNodeItem(
                        node = node,
                        isLast = idx == nodes.size - 1,
                        onClick = {
                            if (node.status == NodeStatus.LOCKED) {
                                val prevNode = nodes.getOrNull(idx - 1)
                                val prevName = prevNode?.chapter ?: "the previous chapter"
                                val notice = "Complete $prevName first to unlock ${node.chapter}!"
                                Toast.makeText(context, notice, Toast.LENGTH_SHORT).show()
                                lockedMessage = notice
                            } else {
                                onSelectChapter(node.id)
                            }
                        }
                    )
                }
            }
        }

        // Shared Bottom Navigation Bar (if enabled in standalone usage)
        if (showBottomBar) {
            AlgoQuestBottomBar(selectedTab = 1, onSelectTab = onSelectTab)
        }
    }
}

@Composable
private fun RoadmapNodeItem(
    node: QuestNode,
    isLast: Boolean,
    onClick: () -> Unit
) {
    val green = Green
    val purple = Purple
    val purpleSoft = PurpleSoft
    val cyan = Cyan
    val textPrimary = TextPrimary
    val textSecondary = TextSecondary
    val textMuted = TextMuted
    val gold = Gold
    val borderColor = BorderColor
    val isDark = AlgoTheme.colors.isDark

    // Infinite breathing glow for the active chapter node
    val infiniteTransition = rememberInfiniteTransition(label = "nodePulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Node Status Indicator & Connecting Line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(42.dp)
        ) {
            val circleColor = when (node.status) {
                NodeStatus.COMPLETED -> green
                NodeStatus.ACTIVE -> cyan
                NodeStatus.NEXT -> purpleSoft
                NodeStatus.LOCKED -> textMuted.copy(alpha = 0.35f)
            }

            val circleBg = when (node.status) {
                NodeStatus.COMPLETED -> green.copy(alpha = 0.16f)
                NodeStatus.ACTIVE -> purple.copy(alpha = 0.28f)
                NodeStatus.NEXT -> purpleSoft.copy(alpha = 0.14f)
                NodeStatus.LOCKED -> CardBg
            }

            Box(
                modifier = Modifier
                    .size(if (node.status == NodeStatus.ACTIVE) 40.dp else 36.dp)
                    .scale(if (node.status == NodeStatus.ACTIVE) pulseScale else 1f)
                    .clip(CircleShape)
                    .background(circleBg)
                    .border(
                        width = if (node.status == NodeStatus.ACTIVE) 2.2.dp else 2.dp,
                        brush = if (node.status == NodeStatus.ACTIVE) {
                            Brush.sweepGradient(listOf(cyan, purple, cyan))
                        } else {
                            Brush.linearGradient(listOf(circleColor, circleColor))
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (node.status) {
                    NodeStatus.COMPLETED -> IconCanvas(16.dp) { d -> drawCheck(d, green) }
                    NodeStatus.LOCKED -> IconCanvas(14.dp) { d -> drawLock(d, textMuted) }
                    NodeStatus.ACTIVE -> IconCanvas(18.dp) { d ->
                        // Draw chapter icon with cyan accent
                        node.icon(this, d)
                    }
                    NodeStatus.NEXT -> IconCanvas(16.dp, node.icon)
                }
            }

            // Connecting line to next node
            if (!isLast) {
                val lineHeight = 64.dp
                Canvas(
                    modifier = Modifier
                        .width(3.dp)
                        .height(lineHeight)
                ) {
                    val isLocked = node.status == NodeStatus.LOCKED
                    val pathEffect = if (isLocked) {
                        PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                    } else null

                    val strokeColor = when (node.status) {
                        NodeStatus.COMPLETED -> green.copy(alpha = 0.55f)
                        NodeStatus.ACTIVE -> cyan.copy(alpha = 0.5f)
                        NodeStatus.NEXT -> purpleSoft.copy(alpha = 0.35f)
                        NodeStatus.LOCKED -> textMuted.copy(alpha = 0.25f)
                    }

                    drawLine(
                        color = strokeColor,
                        start = Offset(size.width / 2, 0f),
                        end = Offset(size.width / 2, size.height),
                        strokeWidth = 2.5.dp.toPx(),
                        cap = StrokeCap.Round,
                        pathEffect = pathEffect
                    )
                }
            }
        }

        // 3. Chapter Card
        val cardShape = RoundedCornerShape(16.dp)
        val isCardActive = node.status == NodeStatus.ACTIVE
        val progressPercent = if (node.totalLessons > 0) {
            (node.completedLessons.toFloat() / node.totalLessons.toFloat()).coerceIn(0f, 1f)
        } else 0f

        Column(
            modifier = Modifier
                .weight(1f)
                .clip(cardShape)
                .background(if (isCardActive) ActiveCardBg else CardBg)
                .border(
                    width = if (isCardActive) 1.5.dp else 1.dp,
                    brush = when (node.status) {
                        NodeStatus.ACTIVE -> Brush.horizontalGradient(listOf(purple, cyan))
                        NodeStatus.COMPLETED -> Brush.linearGradient(listOf(green.copy(alpha = 0.35f), green.copy(alpha = 0.2f)))
                        NodeStatus.NEXT -> Brush.linearGradient(listOf(purpleSoft.copy(alpha = 0.3f), purpleSoft.copy(alpha = 0.15f)))
                        NodeStatus.LOCKED -> Brush.linearGradient(listOf(if (isDark) Color.White.copy(alpha = 0.04f) else borderColor, if (isDark) Color.White.copy(alpha = 0.04f) else borderColor))
                    },
                    shape = cardShape
                )
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Chapter header row: Chapter Label & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = node.chapter,
                    style = TextStyle(
                        fontFamily = Mono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = when (node.status) {
                            NodeStatus.ACTIVE -> cyan
                            NodeStatus.COMPLETED -> green
                            NodeStatus.NEXT -> purpleSoft
                            NodeStatus.LOCKED -> textMuted
                        }
                    )
                )

                // Chapter Status Badge
                when (node.status) {
                    NodeStatus.COMPLETED -> Badge("COMPLETED", green.copy(alpha = 0.12f), green)
                    NodeStatus.ACTIVE -> Badge("CURRENT", cyan.copy(alpha = 0.15f), cyan)
                    NodeStatus.NEXT -> Badge("AVAILABLE", purple.copy(alpha = 0.15f), purpleSoft)
                    NodeStatus.LOCKED -> Badge("LOCKED", textMuted.copy(alpha = 0.1f), textMuted)
                }
            }

            // Chapter Title & Subtitle
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = node.title,
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = if (node.status == NodeStatus.LOCKED) textMuted else textPrimary
                    )
                )
                Text(
                    text = node.subtitle,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontSize = 11.5.sp,
                        color = textSecondary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Small Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.5.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isDark) Color.White.copy(alpha = 0.06f) else borderColor.copy(alpha = 0.4f))
            ) {
                if (progressPercent > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressPercent)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                when (node.status) {
                                    NodeStatus.COMPLETED -> Brush.linearGradient(listOf(green, green))
                                    NodeStatus.ACTIVE -> Brush.horizontalGradient(listOf(purple, cyan))
                                    else -> Brush.linearGradient(listOf(purpleSoft, purpleSoft))
                                }
                            )
                    )
                }
            }

            // Bottom stats row: Lessons completed & XP reward
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = node.lessons,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontSize = 11.5.sp,
                        color = if (node.status == NodeStatus.LOCKED) textMuted else textSecondary
                    )
                )
                Text(
                    text = node.xp,
                    style = TextStyle(
                        fontFamily = Mono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        color = if (node.status == NodeStatus.LOCKED) textMuted.copy(alpha = 0.5f) else gold
                    )
                )
            }
        }
    }
}

@Composable
private fun Badge(text: String, background: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .height(24.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(background)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = textColor
            )
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

private fun DrawScope.drawArrowRight(d: Float) {
    val w = 2f * d
    drawLine(Color.White, Offset(3f * d, 9f * d), Offset(15f * d, 9f * d), w, StrokeCap.Round)
    drawLine(Color.White, Offset(10f * d, 4f * d), Offset(15f * d, 9f * d), w, StrokeCap.Round)
    drawLine(Color.White, Offset(10f * d, 14f * d), Offset(15f * d, 9f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawChevronLeft(d: Float, color: Color) {
    val w = 1.8f * d
    drawLine(color, Offset(11f * d, 4f * d), Offset(5f * d, 10f * d), w, StrokeCap.Round)
    drawLine(color, Offset(5f * d, 10f * d), Offset(11f * d, 16f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawCheck(d: Float, color: Color) {
    val w = 2f * d
    drawLine(color, Offset(4f * d, 9f * d), Offset(7f * d, 12f * d), w, StrokeCap.Round)
    drawLine(color, Offset(7f * d, 12f * d), Offset(12f * d, 4f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawLock(d: Float, color: Color) {
    drawRect(color, Offset(4f * d, 7f * d), Size(8f * d, 6f * d), style = lineStroke(d, 1.2f))
    drawArc(color, 180f, 180f, false, Offset(5f * d, 3f * d), Size(6f * d, 6f * d), style = lineStroke(d, 1.2f))
}

private fun DrawScope.drawArrayIcon(d: Float, color: Color) {
    drawRect(color, Offset(2f * d, 5f * d), Size(12f * d, 6f * d), style = lineStroke(d))
    drawLine(color, Offset(6f * d, 5f * d), Offset(6f * d, 11f * d), 1.2f * d)
    drawLine(color, Offset(10f * d, 5f * d), Offset(10f * d, 11f * d), 1.2f * d)
}

private fun DrawScope.drawPointerIcon(d: Float, color: Color) {
    drawLine(color, Offset(3f * d, 8f * d), Offset(13f * d, 8f * d), 1.5f * d, StrokeCap.Round)
    drawLine(color, Offset(3f * d, 8f * d), Offset(6f * d, 5f * d), 1.5f * d, StrokeCap.Round)
    drawLine(color, Offset(13f * d, 8f * d), Offset(10f * d, 11f * d), 1.5f * d, StrokeCap.Round)
}

private fun DrawScope.drawSearchIcon(d: Float, color: Color) {
    drawCircle(color, radius = 4f * d, center = Offset(7f * d, 7f * d), style = lineStroke(d))
    drawLine(color, Offset(10f * d, 10f * d), Offset(14f * d, 14f * d), 1.8f * d, StrokeCap.Round)
}

private fun DrawScope.drawSortIcon(d: Float, color: Color) {
    drawLine(color, Offset(3f * d, 13f * d), Offset(3f * d, 9f * d), 2f * d, StrokeCap.Round)
    drawLine(color, Offset(8f * d, 13f * d), Offset(8f * d, 5f * d), 2f * d, StrokeCap.Round)
    drawLine(color, Offset(13f * d, 13f * d), Offset(13f * d, 2f * d), 2f * d, StrokeCap.Round)
}

private fun DrawScope.drawLinkIcon(d: Float, color: Color) {
    drawCircle(color, radius = 2.5f * d, center = Offset(5f * d, 8f * d), style = lineStroke(d))
    drawCircle(color, radius = 2.5f * d, center = Offset(11f * d, 8f * d), style = lineStroke(d))
    drawLine(color, Offset(7.5f * d, 8f * d), Offset(8.5f * d, 8f * d), 1.5f * d)
}

private fun DrawScope.drawTreeIcon(d: Float, color: Color) {
    drawCircle(color, radius = 2f * d, center = Offset(8f * d, 3f * d), style = lineStroke(d))
    drawCircle(color, radius = 2f * d, center = Offset(4f * d, 11f * d), style = lineStroke(d))
    drawCircle(color, radius = 2f * d, center = Offset(12f * d, 11f * d), style = lineStroke(d))
    drawLine(color, Offset(8f * d, 5f * d), Offset(4f * d, 9f * d), 1.2f * d)
    drawLine(color, Offset(8f * d, 5f * d), Offset(12f * d, 9f * d), 1.2f * d)
}

private fun DrawScope.drawGraphIcon(d: Float, color: Color) {
    drawCircle(color, radius = 1.8f * d, center = Offset(4f * d, 4f * d), style = lineStroke(d))
    drawCircle(color, radius = 1.8f * d, center = Offset(12f * d, 4f * d), style = lineStroke(d))
    drawCircle(color, radius = 1.8f * d, center = Offset(8f * d, 12f * d), style = lineStroke(d))
    drawLine(color, Offset(4f * d, 4f * d), Offset(12f * d, 4f * d), 1.2f * d)
    drawLine(color, Offset(4f * d, 4f * d), Offset(8f * d, 12f * d), 1.2f * d)
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun QuestMapScreenPreview() {
    QuestMapScreen()
}
