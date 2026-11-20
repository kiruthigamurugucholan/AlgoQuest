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
import com.example.myapplication.data.model.catalogue.TopicDifficulty
import com.example.myapplication.data.repository.AdventureRoadmapRepository
import com.example.myapplication.data.repository.DsaLessonContentRepository
import com.example.myapplication.data.repository.DsaTopicCatalogueRepository
import com.example.myapplication.ui.theme.AlgoTheme
import com.example.myapplication.viewmodel.LearningViewModel
import com.example.myapplication.viewmodel.TopicDetailUiState

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
private val CardBorder: Color @Composable get() = AlgoTheme.colors.border

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
    val isLocked: Boolean,
    val isQuiz: Boolean = false,
    val statusBadge: String? = null
)

@Composable
fun ChapterDetailScreen(
    topicId: Int = 3,
    subtopicId: String? = null,
    learningViewModel: LearningViewModel? = null,
    onBack: () -> Unit = {},
    onStartLesson: (slug: String?) -> Unit = {},
    onOpenCatalogueLesson: (subtopicId: String, lessonId: String) -> Unit = { _, _ -> },
    onOpenLearningResources: (topicName: String?) -> Unit = {},
    onOpenQuiz: (lessonId: Int) -> Unit = {},
    onVisualize: (slug: String?) -> Unit = {},
    onAskAi: (topicName: String?) -> Unit = {},
    onSelectTab: (Int) -> Unit = {}
) {
    LaunchedEffect(topicId, subtopicId) {
        if (subtopicId == null) {
            learningViewModel?.loadTopicDetail(topicId)
        } else {
            learningViewModel?.loadCatalogueProgress()
        }
    }

    val subtopic = remember(subtopicId) {
        subtopicId?.let { DsaTopicCatalogueRepository.getSubtopicById(it) }
    }
    val subtopicCategory = remember(subtopic) {
        subtopic?.let { DsaTopicCatalogueRepository.getCategoryById(it.categoryId) }
    }

    val completedCatalogueIds by learningViewModel?.completedCatalogueLessonIds?.collectAsState()
        ?: remember { mutableStateOf(emptySet()) }
    val catalogueOutlines = remember(subtopic) {
        subtopic?.let { DsaLessonContentRepository.getLessonOutlinesForSubtopic(it) } ?: emptyList()
    }
    val catalogueProgress = remember(subtopic, completedCatalogueIds) {
        subtopic?.let { DsaLessonContentRepository.calculateTopicProgress(it, completedCatalogueIds) }
    }

    val detailState by learningViewModel?.detailState?.collectAsState() ?: remember {
        mutableStateOf(null)
    }

    val dynamicDetail = (detailState as? TopicDetailUiState.Success)?.data

    val adventureChapter = remember(topicId) {
        AdventureRoadmapRepository.getChapterById(topicId)
    }
    val adventureLessons = remember(topicId) {
        AdventureRoadmapRepository.getLessonsForChapter(topicId)
    }

    val chapterTitle = if (subtopic != null) subtopic.title else (dynamicDetail?.topic?.title ?: adventureChapter?.title ?: "Chapter Details")
    val chapterLabel = if (subtopic != null) (subtopicCategory?.name?.uppercase() ?: "DSA TOPIC") else (dynamicDetail?.topic?.chapterLabel ?: adventureChapter?.chapterLabel?.uppercase() ?: "CHAPTER $topicId")
    val chapterSubtitle = if (subtopic != null) subtopic.title else (dynamicDetail?.topic?.subtitle ?: adventureChapter?.subtitle ?: "")
    val chapterDesc = if (subtopic != null) subtopic.summary else (dynamicDetail?.topic?.description ?: adventureChapter?.description ?: "")
    val totalXp = if (subtopic != null) subtopic.xpReward else (dynamicDetail?.topic?.totalXp ?: adventureChapter?.totalXp ?: 500)

    val lessons: List<LessonItem> = if (subtopic != null) {
        emptyList()
    } else if (!dynamicDetail?.lessons.isNullOrEmpty()) {
        dynamicDetail!!.lessons.map { dl ->
            val advL = adventureLessons.firstOrNull { it.id == dl.id }
            LessonItem(
                id = dl.id,
                title = dl.title,
                duration = dl.duration,
                xp = dl.xp,
                isCompleted = dl.isCompleted,
                isCurrent = dl.isCurrent,
                isLocked = dl.isLocked,
                isQuiz = dl.lessonType == "QUIZ" || advL?.isQuiz == true,
                statusBadge = if (dl.lessonType == "QUIZ" || advL?.isQuiz == true) "QUIZ"
                              else if (dl.lessonType == "VISUALIZER" || advL?.lessonType == "VISUALIZER") "VISUALIZER"
                              else "THEORY"
            )
        }
    } else {
        adventureLessons.mapIndexed { idx, al ->
            val isCompleted = topicId in listOf(1, 2)
            val isCurrent = !isCompleted && (idx == 0 || (topicId == 3 && idx == 2))
            val isLocked = !isCompleted && idx > 0 && !(topicId == 3 && idx <= 2)
            LessonItem(
                id = al.id,
                title = al.title,
                duration = al.duration,
                xp = al.xp,
                isCompleted = isCompleted,
                isCurrent = isCurrent,
                isLocked = isLocked,
                isQuiz = al.isQuiz,
                statusBadge = if (al.isQuiz) "QUIZ" else if (al.lessonType == "VISUALIZER") "VISUALIZER" else "THEORY"
            )
        }
    }

    val completedCount = dynamicDetail?.topic?.completedLessons ?: lessons.count { it.isCompleted }
    val totalCount = dynamicDetail?.topic?.totalLessons ?: lessons.size
    val progressPct = dynamicDetail?.topic?.progressPercentage ?: (if (totalCount > 0) (completedCount * 100) / totalCount else 0)

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
                    modifier = Modifier.weight(1f, fill = false),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val chevronColor = TextPrimary
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CardBg)
                            .border(1.dp, CardBorder, CircleShape)
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center
                    ) {
                        IconCanvas(16.dp) { d -> drawChevronLeft(d, chevronColor) }
                    }
                    Text(
                        text = if (subtopic != null) subtopic.title else "Chapter Details",
                        style = TextStyle(
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        ),
                        maxLines = 1
                    )
                }
                Spacer(Modifier.width(8.dp))
                Badge(chapterLabel, Purple.copy(alpha = 0.12f), PurpleSoft)
            }

            // Chapter / Subtopic Hero Card
            val heroShape = RoundedCornerShape(20.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(heroShape)
                    .background(ActiveCardBg)
                    .border(1.dp, CardBorder, heroShape)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (subtopic != null) {
                        val (diffBg, diffColor) = when (subtopic.difficulty) {
                            TopicDifficulty.EASY -> Pair(Green.copy(alpha = 0.15f), Green)
                            TopicDifficulty.MEDIUM -> Pair(Gold.copy(alpha = 0.15f), Gold)
                            TopicDifficulty.HARD -> Pair(Purple.copy(alpha = 0.15f), PurpleSoft)
                        }
                        Badge(subtopic.difficulty.displayName.uppercase(), diffBg, diffColor)
                    } else {
                        Badge(chapterTitle.uppercase(), Cyan.copy(alpha = 0.12f), Cyan)
                    }
                    Text(
                        text = if (subtopic != null) "+${subtopic.xpReward} XP" else "+$totalXp XP Total",
                        style = TextStyle(fontFamily = Mono, fontSize = 12.sp, color = Gold)
                    )
                }

                Text(
                    text = chapterSubtitle,
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp,
                        color = TextPrimary
                    )
                )

                Text(
                    text = chapterDesc,
                    style = TextStyle(fontFamily = Inter, fontSize = 13.sp, lineHeight = 19.sp, color = TextSecondary)
                )

                if (subtopic != null) {
                    // Complexity & Duration Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (!subtopic.timeComplexity.isNullOrEmpty()) {
                            Badge("TIME: ${subtopic.timeComplexity}", Cyan.copy(alpha = 0.12f), Cyan)
                        }
                        if (!subtopic.spaceComplexity.isNullOrEmpty()) {
                            Badge("SPACE: ${subtopic.spaceComplexity}", Purple.copy(alpha = 0.12f), PurpleSoft)
                        }
                        Badge("${subtopic.estimatedMinutes} MIN", Gold.copy(alpha = 0.12f), Gold)
                    }

                    // Topic Lesson Progress Bar
                    if (catalogueProgress != null) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${catalogueProgress.completedCount} of ${catalogueProgress.totalCount} lessons complete",
                                    style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = TextSecondary)
                                )
                                Text(
                                    text = "${catalogueProgress.progressPercentage}%",
                                    style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Cyan)
                                )
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
                                        .fillMaxWidth(
                                            if (catalogueProgress.completedCount == 0) 0f
                                            else (catalogueProgress.progressPercentage / 100f).coerceIn(0.04f, 1f)
                                        )
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Cyan)
                                )
                            }
                        }
                    }
                } else {
                    // Progress bar for legacy chapter
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "$completedCount of $totalCount lessons complete", style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = TextSecondary))
                            Text(text = "$progressPct%", style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Cyan))
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
                                    .fillMaxWidth((progressPct / 100f).coerceIn(0.02f, 1f))
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Cyan)
                            )
                        }
                    }
                }

                // Quick actions inside hero
                val canViz = if (subtopic != null) {
                    subtopic.hasVisualizer && !subtopic.visualizerSlug.isNullOrEmpty()
                } else {
                    adventureChapter?.primaryVisualizerSlug?.isNotEmpty() == true
                }
                val targetChapterSlug = if (subtopic != null) {
                    subtopic.visualizerSlug
                } else {
                    adventureChapter?.primaryVisualizerSlug ?: "array_traversal"
                }
                val targetSubtopicId = if (subtopic != null) subtopic.id else (adventureChapter?.primarySubtopicId ?: "array_traversal")
                val targetLessonId = if (subtopic != null) "${subtopic.id}_l1" else (adventureLessons.firstOrNull { it.lessonType != "QUIZ" && it.lessonType != "VISUALIZER" }?.catalogueLessonId ?: "${targetSubtopicId}_l1")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Study Lesson / Learn Button (ALWAYS opens LessonScreen)
                    val playIconColor = Cyan
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Cyan.copy(alpha = 0.18f))
                            .border(1.dp, Cyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable {
                                onOpenCatalogueLesson(targetSubtopicId, targetLessonId)
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconCanvas(16.dp) { d -> drawPlay(d, playIconColor) }
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Study Lesson",
                            style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Cyan)
                        )
                    }

                    // 2. Visualize Lab (if available) or Ask AI Ally
                    if (canViz && !targetChapterSlug.isNullOrEmpty()) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Purple)
                                .clickable {
                                    onVisualize(targetChapterSlug)
                                },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconCanvas(16.dp) { d -> drawEye(d) }
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Visualize Lab",
                                style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = Color.White)
                            )
                        }
                    } else {
                        val aiSparkleColor = PurpleSoft
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CardBg)
                                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    onAskAi(subtopic?.title ?: chapterTitle)
                                },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconCanvas(16.dp) { d -> drawSparkles(d, aiSparkleColor) }
                            Spacer(Modifier.width(6.dp))
                            Text(text = "Ask AI Ally", style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = TextPrimary))
                        }
                    }
                }

                // Row 2: Secondary actions (Ask AI Ally if canViz + Web Learning Resources)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (canViz && !targetChapterSlug.isNullOrEmpty()) {
                        val aiSparkleColor = PurpleSoft
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CardBg)
                                .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                                .clickable {
                                    onAskAi(subtopic?.title ?: chapterTitle)
                                },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconCanvas(14.dp) { d -> drawSparkles(d, aiSparkleColor) }
                            Spacer(Modifier.width(6.dp))
                            Text(text = "Ask AI Ally", style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 12.sp, color = TextPrimary))
                        }
                    }

                    // Curated Web Learning Resources shortcut
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Cyan.copy(alpha = 0.08f))
                            .border(1.dp, Cyan.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                            .clickable { onOpenLearningResources(subtopic?.title ?: chapterTitle) },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Web Learning Resources",
                            style = TextStyle(
                                fontFamily = Inter,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = Cyan
                            )
                        )
                    }
                }
            }

            Text(
                text = if (subtopic != null) "Topic Learning Modules" else "Lesson Roadmap",
                style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextPrimary)
            )

            // Lessons / Modules List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (subtopic != null) {
                    catalogueOutlines.forEach { outline ->
                        val isDone = completedCatalogueIds.contains(outline.lessonId)
                        val badgeLabel = when {
                            isDone -> "COMPLETED"
                            outline.lessonType == "VISUALIZER" && outline.hasContent -> "VISUALIZER"
                            outline.lessonType == "QUIZ" && outline.hasContent -> "QUIZ"
                            outline.hasContent -> "READY"
                            else -> "COMING SOON"
                        }
                        LessonRowItem(
                            lesson = LessonItem(
                                id = outline.lessonNumber,
                                title = outline.title,
                                duration = outline.duration,
                                xp = "+${outline.xpReward} XP",
                                isCompleted = isDone,
                                isCurrent = outline.hasContent && !isDone,
                                isLocked = false,
                                isQuiz = outline.lessonType == "QUIZ",
                                statusBadge = badgeLabel
                            ),
                            onClick = {
                                when {
                                    outline.lessonType == "VISUALIZER" && outline.hasContent && !outline.visualizerSlug.isNullOrEmpty() -> {
                                        onVisualize(outline.visualizerSlug)
                                    }
                                    outline.lessonType == "QUIZ" && outline.hasContent && outline.quizLessonId != null -> {
                                        onOpenQuiz(outline.quizLessonId)
                                    }
                                    else -> {
                                        onOpenCatalogueLesson(subtopic.id, outline.lessonId)
                                    }
                                }
                            }
                        )
                    }
                } else {
                    lessons.forEach { lesson ->
                        val advLesson = adventureLessons.firstOrNull { it.id == lesson.id }
                        LessonRowItem(
                            lesson = lesson,
                            onClick = {
                                if (lesson.isQuiz) {
                                    val quizId = advLesson?.quizLessonId ?: 1
                                    onOpenQuiz(quizId)
                                } else if (advLesson?.lessonType == "VISUALIZER") {
                                    val slug = advLesson.visualizerSlug ?: adventureChapter?.primaryVisualizerSlug ?: "array_traversal"
                                    onVisualize(slug)
                                } else {
                                    val targetSubtopicId = advLesson?.subtopicId ?: adventureChapter?.primarySubtopicId ?: "array_traversal"
                                    val targetLessonId = advLesson?.catalogueLessonId ?: "${targetSubtopicId}_l1"
                                    onOpenCatalogueLesson(targetSubtopicId, targetLessonId)
                                }
                            }
                        )
                    }
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
                    else -> CardBorder
                },
                cardShape
            )
            .clickable(enabled = !lesson.isLocked, onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val iconBg = when {
            lesson.isCompleted -> Green.copy(alpha = 0.15f)
            lesson.isCurrent -> Cyan.copy(alpha = 0.15f)
            else -> CardBorder.copy(alpha = 0.2f)
        }

        val checkColor = Green
        val playColor = Cyan
        val lockColor = TextMuted
        val chevronColor = TextSecondary

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            when {
                lesson.isCompleted -> IconCanvas(16.dp) { d -> drawCheck(d, checkColor) }
                lesson.isCurrent -> IconCanvas(16.dp) { d -> drawPlay(d, playColor) }
                lesson.isLocked -> IconCanvas(14.dp) { d -> drawLock(d, lockColor) }
                else -> IconCanvas(16.dp) { d -> drawPlay(d, lockColor) }
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lesson ${lesson.id.toString().padStart(2, '0')}",
                    style = TextStyle(fontFamily = Mono, fontSize = 10.sp, color = TextMuted)
                )
                if (lesson.statusBadge != null) {
                    val (bBg, bColor) = when (lesson.statusBadge) {
                        "COMPLETED" -> Pair(Green.copy(alpha = 0.15f), Green)
                        "READY", "VISUALIZER" -> Pair(Cyan.copy(alpha = 0.15f), Cyan)
                        "QUIZ" -> Pair(Purple.copy(alpha = 0.15f), PurpleSoft)
                        else -> Pair(Gold.copy(alpha = 0.12f), Gold)
                    }
                    Badge(lesson.statusBadge, bBg, bColor)
                } else if (lesson.isQuiz) {
                    Badge("QUIZ", Purple.copy(alpha = 0.15f), PurpleSoft)
                }
            }
            Text(
                text = lesson.title,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = if (lesson.isLocked) TextMuted else TextPrimary
                )
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = lesson.duration, style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = TextMuted))
                Text(text = lesson.xp, style = TextStyle(fontFamily = Mono, fontSize = 11.sp, color = Gold))
            }
        }

        IconCanvas(14.dp) { d -> drawChevronRight(d, chevronColor) }
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
            style = TextStyle(
                fontFamily = Mono,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                color = textColor
            )
        )
    }
}

// ---------- Small canvas icons ----------

@Composable
private fun IconCanvas(size: Dp, draw: DrawScope.(Float) -> Unit) {
    Canvas(Modifier.size(size)) { draw(this, density) }
}

private fun lineStroke(d: Float, w: Float = 1.5f) =
    Stroke(width = w * d, cap = StrokeCap.Round, join = StrokeJoin.Round)

private fun DrawScope.drawChevronLeft(d: Float, color: Color) {
    val w = 1.8f * d
    drawLine(color, Offset(11f * d, 4f * d), Offset(5f * d, 10f * d), w, StrokeCap.Round)
    drawLine(color, Offset(5f * d, 10f * d), Offset(11f * d, 16f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawChevronRight(d: Float, color: Color) {
    val w = 1.6f * d
    drawLine(color, Offset(5f * d, 3f * d), Offset(10f * d, 8f * d), w, StrokeCap.Round)
    drawLine(color, Offset(10f * d, 8f * d), Offset(5f * d, 13f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawCheck(d: Float, color: Color) {
    val w = 2f * d
    drawLine(color, Offset(4f * d, 9f * d), Offset(7f * d, 12f * d), w, StrokeCap.Round)
    drawLine(color, Offset(7f * d, 12f * d), Offset(12f * d, 4f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawPlay(d: Float, color: Color) {
    val path = Path().apply {
        moveTo(6f * d, 4f * d)
        lineTo(13f * d, 8f * d)
        lineTo(6f * d, 12f * d)
        close()
    }
    drawPath(path, color)
}

private fun DrawScope.drawLock(d: Float, color: Color) {
    drawRect(color, Offset(3f * d, 6f * d), Size(8f * d, 6f * d), style = lineStroke(d, 1.2f))
    drawArc(color, 180f, 180f, false, Offset(4.5f * d, 2.5f * d), Size(5f * d, 5f * d), style = lineStroke(d, 1.2f))
}

private fun DrawScope.drawEye(d: Float) {
    val c = Color.White
    drawCircle(c, radius = 2.5f * d, center = Offset(8f * d, 8f * d))
    drawArc(c, 0f, 180f, false, Offset(2f * d, 3f * d), Size(12f * d, 10f * d), style = lineStroke(d, 1.5f))
    drawArc(c, 180f, 180f, false, Offset(2f * d, 3f * d), Size(12f * d, 10f * d), style = lineStroke(d, 1.5f))
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
        lineTo((cx - i) * d, (cy - i) * d)
        close()
    }
    drawPath(path, color, style = Stroke(width = 1.2f * d, join = StrokeJoin.Round))
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ChapterDetailScreenPreview() {
    ChapterDetailScreen()
}
