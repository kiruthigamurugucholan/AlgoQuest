package com.example.myapplication

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.catalogue.DsaLessonContent
import com.example.myapplication.data.model.catalogue.DsaLessonOutline
import com.example.myapplication.data.model.catalogue.LessonCodeBlock
import com.example.myapplication.data.model.catalogue.LessonPracticeQuestion
import com.example.myapplication.data.model.catalogue.LessonSection
import com.example.myapplication.data.model.catalogue.LessonSectionType
import com.example.myapplication.data.model.catalogue.TopicDifficulty
import com.example.myapplication.data.repository.DsaLessonContentRepository
import com.example.myapplication.data.repository.DsaTopicCatalogueRepository
import com.example.myapplication.ui.theme.AlgoTheme
import com.example.myapplication.ui.visualizer.DsaUniversalVisualizer
import com.example.myapplication.viewmodel.LearningViewModel

private val LessonBg: Color @Composable get() = AlgoTheme.colors.bg
private val LessonCardBg: Color @Composable get() = AlgoTheme.colors.cardBg
private val LessonActiveCardBg: Color @Composable get() = AlgoTheme.colors.activeCardBg
private val LessonCodeBg: Color @Composable get() = AlgoTheme.colors.codeBg
private val LessonBorder: Color @Composable get() = AlgoTheme.colors.border
private val LessonTextPrimary: Color @Composable get() = AlgoTheme.colors.textPrimary
private val LessonTextSecondary: Color @Composable get() = AlgoTheme.colors.textSecondary
private val LessonTextMuted: Color @Composable get() = AlgoTheme.colors.textMuted
private val LessonCyan: Color @Composable get() = AlgoTheme.colors.cyan
private val LessonPurple: Color @Composable get() = AlgoTheme.colors.purple
private val LessonPurpleSoft: Color @Composable get() = AlgoTheme.colors.purpleSoft
private val LessonGreen: Color @Composable get() = AlgoTheme.colors.green
private val LessonGold: Color @Composable get() = AlgoTheme.colors.gold

private val LessonPoppins: FontFamily = FontFamily.SansSerif
private val LessonInter: FontFamily = FontFamily.SansSerif
private val LessonMono: FontFamily = FontFamily.Monospace

@Composable
fun LessonScreen(
    subtopicId: String,
    lessonId: String,
    learningViewModel: LearningViewModel? = null,
    onBack: () -> Unit = {},
    onOpenLesson: (lessonId: String) -> Unit = {},
    onVisualize: (slug: String) -> Unit = {},
    onOpenQuiz: (quizLessonId: Int) -> Unit = {},
    onAskAi: (query: String) -> Unit = {},
    onLessonCompletedSynced: () -> Unit = {},
    onSelectTab: (Int) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(lessonId) {
        learningViewModel?.clearCatalogueCompletionResult()
        scrollState.scrollTo(0)
    }

    val subtopic = remember(subtopicId) {
        DsaTopicCatalogueRepository.getSubtopicById(subtopicId)
    }
    val category = remember(subtopic) {
        subtopic?.let { DsaTopicCatalogueRepository.getCategoryById(it.categoryId) }
    }
    val lessonContent = remember(lessonId) {
        DsaLessonContentRepository.getLessonContent(lessonId)
    }
    val outlines = remember(subtopic) {
        subtopic?.let { DsaLessonContentRepository.getLessonOutlinesForSubtopic(it) } ?: emptyList()
    }
    val currentOutline = remember(outlines, lessonId) {
        outlines.firstOrNull { it.lessonId == lessonId }
    }

    val completedIds by learningViewModel?.completedCatalogueLessonIds?.collectAsState()
        ?: remember { mutableStateOf(emptySet()) }
    val isCompleting by learningViewModel?.isCompletingCatalogueLesson?.collectAsState()
        ?: remember { mutableStateOf(false) }
    val completionResult by learningViewModel?.catalogueCompletionResult?.collectAsState()
        ?: remember { mutableStateOf(null) }

    val isCompleted = completedIds.contains(lessonId)

    var currentMode by remember(lessonId) {
        mutableStateOf(if (lessonId.endsWith("_viz")) "VISUALIZE" else "LEARN")
    }

    val currentLessonNumber = lessonContent?.lessonNumber ?: currentOutline?.lessonNumber ?: 1
    val totalLessonsInTopic = outlines.size.coerceAtLeast(1)

    val topicProgress = remember(subtopic, completedIds) {
        subtopic?.let { DsaLessonContentRepository.calculateTopicProgress(it, completedIds) }
    }

    val prevOutline = remember(subtopic, currentLessonNumber) {
        subtopic?.let { DsaLessonContentRepository.getPreviousLessonOutline(it, currentLessonNumber) }
    }
    val nextOutline = remember(subtopic, currentLessonNumber) {
        subtopic?.let { DsaLessonContentRepository.getNextLessonOutline(it, currentLessonNumber) }
    }

    fun navigateToOutline(outline: DsaLessonOutline) {
        when {
            outline.lessonType == "VISUALIZER" -> {
                currentMode = "VISUALIZE"
            }
            outline.lessonType == "QUIZ" -> {
                if (outline.quizLessonId != null) {
                    onOpenQuiz(outline.quizLessonId)
                } else {
                    currentMode = "PRACTICE"
                }
            }
            else -> {
                currentMode = "LEARN"
                onOpenLesson(outline.lessonId)
            }
        }
    }

    val bg = LessonBg
    val cardBg = LessonCardBg
    val activeCardBg = LessonActiveCardBg
    val border = LessonBorder
    val textPrimary = LessonTextPrimary
    val textSecondary = LessonTextSecondary
    val textMuted = LessonTextMuted
    val cyan = LessonCyan
    val purple = LessonPurple
    val purpleSoft = LessonPurpleSoft
    val green = LessonGreen
    val gold = LessonGold

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Top Navigation Bar
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
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(cardBg)
                            .border(1.dp, border, CircleShape)
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center
                    ) {
                        LessonIconCanvas(16.dp) { d -> drawLessonChevronLeft(d, textPrimary) }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        Text(
                            text = "Lesson ${currentLessonNumber.toString().padStart(2, '0')}",
                            style = TextStyle(
                                fontFamily = LessonPoppins,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 17.sp,
                                color = textPrimary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = subtopic?.name ?: "DSA Lesson",
                            style = TextStyle(
                                fontFamily = LessonInter,
                                fontSize = 11.5.sp,
                                color = textSecondary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
                if (isCompleted) {
                    LessonBadge("COMPLETED ✓", green.copy(alpha = 0.16f), green)
                } else {
                    LessonBadge(
                        "LESSON $currentLessonNumber OF $totalLessonsInTopic",
                        purple.copy(alpha = 0.14f),
                        purpleSoft
                    )
                }
            }

            // 2. Lesson Header & Topic Progress Card
            val heroShape = RoundedCornerShape(20.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(heroShape)
                    .background(activeCardBg)
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            listOf(purple.copy(alpha = 0.55f), cyan.copy(alpha = 0.55f))
                        ),
                        shape = heroShape
                    )
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Category + Difficulty + XP row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LessonBadge(
                            text = (category?.name ?: "DSA CATALOGUE").uppercase(),
                            background = cyan.copy(alpha = 0.14f),
                            textColor = cyan
                        )
                        if (subtopic != null) {
                            val (diffBg, diffColor) = when (subtopic.difficulty) {
                                TopicDifficulty.EASY -> Pair(green.copy(alpha = 0.15f), green)
                                TopicDifficulty.MEDIUM -> Pair(gold.copy(alpha = 0.15f), gold)
                                TopicDifficulty.HARD -> Pair(purple.copy(alpha = 0.18f), purpleSoft)
                            }
                            LessonBadge(subtopic.difficulty.displayName.uppercase(), diffBg, diffColor)
                        }
                    }

                    val xpValue = lessonContent?.xpReward ?: currentOutline?.xpReward ?: 40
                    Text(
                        text = "+$xpValue XP",
                        style = TextStyle(
                            fontFamily = LessonMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = gold
                        )
                    )
                }

                // Lesson Title
                Text(
                    text = lessonContent?.title
                        ?: currentOutline?.title
                        ?: "Lesson ${currentLessonNumber.toString().padStart(2, '0')}",
                    style = TextStyle(
                        fontFamily = LessonPoppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        lineHeight = 26.sp,
                        color = textPrimary
                    )
                )

                // Lesson Subtitle / Metadata
                val subtitleText = lessonContent?.subtitle
                    ?: subtopic?.description
                    ?: "Structured DSA learning module"
                Text(
                    text = subtitleText,
                    style = TextStyle(
                        fontFamily = LessonInter,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = textSecondary
                    )
                )

                // Meta tags (Duration & Topic Name)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val durationText = lessonContent?.let { "${it.estimatedMinutes} MIN READ" }
                        ?: currentOutline?.duration?.uppercase()
                        ?: "8 MIN"
                    LessonBadge(durationText, gold.copy(alpha = 0.12f), gold)
                    if (subtopic != null) {
                        LessonBadge("TOPIC: ${subtopic.name.uppercase()}", purple.copy(alpha = 0.12f), purpleSoft)
                    }
                }

                // Topic Lesson Progress Bar
                if (topicProgress != null) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Topic Progress: ${topicProgress.completedCount} of ${topicProgress.totalCount} lessons complete",
                                style = TextStyle(
                                    fontFamily = LessonInter,
                                    fontSize = 11.5.sp,
                                    color = textSecondary
                                )
                            )
                            Text(
                                text = "${topicProgress.progressPercentage}%",
                                style = TextStyle(
                                    fontFamily = LessonMono,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    color = cyan
                                )
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                        ) {
                            val fillFraction = (topicProgress.progressPercentage / 100f).coerceIn(0.03f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(if (topicProgress.completedCount == 0) 0f else fillFraction)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Brush.horizontalGradient(listOf(purple, cyan)))
                            )
                        }
                    }
                }
            }

            // 2.5 Mode Switcher Tabs: Learn | Visualize | Practice
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "LEARN" to "📖 Learn",
                    "VISUALIZE" to "👁️ Visualize",
                    "PRACTICE" to "✍️ Practice"
                ).forEach { (mode, label) ->
                    val selected = currentMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selected) purple else cardBg)
                            .border(
                                1.dp,
                                if (selected) cyan else border,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { currentMode = mode },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = TextStyle(
                                fontFamily = LessonInter,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.5.sp,
                                color = if (selected) Color.White else textSecondary
                            )
                        )
                    }
                }
            }

            // 3. Body: Driven by Current Mode
            when (currentMode) {
                "VISUALIZE" -> {
                    DsaUniversalVisualizer(
                        subtopicId = subtopicId,
                        isLabCompleted = completedIds.contains("${subtopicId}_viz"),
                        onCompleteLab = {
                            val vizOutline = outlines.firstOrNull { it.lessonType == "VISUALIZER" }
                            val vizId = vizOutline?.lessonId ?: "${subtopicId}_viz"
                            val xp = vizOutline?.xpReward ?: 25
                            val syntheticLesson = DsaLessonContent(
                                lessonId = vizId,
                                subtopicId = subtopicId,
                                lessonNumber = vizOutline?.lessonNumber ?: 2,
                                title = vizOutline?.title ?: "Interactive Visualizer Lab",
                                subtitle = "Interactive Visualizer Lab for ${subtopic?.name ?: "this topic"}",
                                estimatedMinutes = 5,
                                xpReward = xp,
                                sections = emptyList()
                            )
                            learningViewModel?.completeCatalogueLesson(
                                lesson = syntheticLesson,
                                onSyncedWithBackend = onLessonCompletedSynced
                            )
                        }
                    )
                }

                "PRACTICE" -> {
                    if (lessonContent != null && lessonContent.practiceQuestions.isNotEmpty()) {
                        LessonPracticeQuestionsCard(questions = lessonContent.practiceQuestions)
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(cardBg)
                                .border(1.dp, border, RoundedCornerShape(14.dp))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Topic Practice & Challenge",
                                style = TextStyle(fontFamily = LessonPoppins, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textPrimary)
                            )
                            Text(
                                text = "Test your knowledge on ${subtopic?.name ?: "this topic"} with interactive challenge questions.",
                                style = TextStyle(fontFamily = LessonInter, fontSize = 12.5.sp, color = textSecondary)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(purple)
                                    .clickable {
                                        val quizOutline = outlines.firstOrNull { it.lessonType == "QUIZ" && it.quizLessonId != null }
                                        if (quizOutline?.quizLessonId != null) {
                                            onOpenQuiz(quizOutline.quizLessonId)
                                        } else {
                                            onAskAi("Give me a challenging practice quiz question on ${subtopic?.name ?: "this DSA topic"}.")
                                        }
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Start Topic Challenge",
                                    style = TextStyle(fontFamily = LessonInter, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                )
                            }
                        }
                    }
                }

                else -> {
                    // "LEARN" mode: Full structured lesson content
                    if (lessonContent != null) {
                        // Learning Objectives Card
                        if (lessonContent.learningObjectives.isNotEmpty()) {
                            LearningObjectivesCard(objectives = lessonContent.learningObjectives)
                        }

                        // Complexity Analysis Card
                        val effectiveTime = lessonContent.timeComplexity ?: subtopic?.timeComplexity
                        val effectiveSpace = lessonContent.spaceComplexity ?: subtopic?.spaceComplexity
                        if (!effectiveTime.isNullOrBlank() || !effectiveSpace.isNullOrBlank()) {
                            ComplexityAnalysisCard(timeComplexity = effectiveTime, spaceComplexity = effectiveSpace)
                        }

                        // Interactive Visualization Quick Card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Brush.horizontalGradient(listOf(purple.copy(alpha = 0.25f), cyan.copy(alpha = 0.15f))))
                                .border(1.dp, cyan.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                .clickable { currentMode = "VISUALIZE" }
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(purple.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                LessonIconCanvas(18.dp) { d -> drawLessonSparkles(d, cyan) }
                            }
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "Interactive Visualization Available",
                                    style = TextStyle(fontFamily = LessonPoppins, fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp, color = textPrimary)
                                )
                                Text(
                                    text = "Watch ${subtopic?.name ?: "this algorithm"} execute step by step with play/pause and custom inputs",
                                    style = TextStyle(fontFamily = LessonInter, fontSize = 11.5.sp, color = textSecondary)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(purple)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Visualize ▶",
                                    style = TextStyle(fontFamily = LessonInter, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                                )
                            }
                        }

                        // Educational Sections
                        lessonContent.sections.forEach { section ->
                            LessonSectionCard(section = section)
                        }

                        // Code Examples with Step-by-Step Explanations
                        if (lessonContent.codeExamples.isNotEmpty()) {
                            Text(
                                text = "Code Examples & Walkthrough",
                                style = TextStyle(
                                    fontFamily = LessonPoppins,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                    color = textPrimary
                                )
                            )
                            lessonContent.codeExamples.forEach { block ->
                                LessonCodeBlockCard(codeBlock = block)
                            }
                        }

                        // Key Takeaways Summary Card
                        if (lessonContent.keyTakeaways.isNotEmpty()) {
                            KeyTakeawaysCard(takeaways = lessonContent.keyTakeaways)
                        }

                        // Interactive Practice Questions Card
                        if (lessonContent.practiceQuestions.isNotEmpty()) {
                            LessonPracticeQuestionsCard(questions = lessonContent.practiceQuestions)
                        }

                        // Completion & XP Reward Section
                        LessonCompletionCard(
                            lesson = lessonContent,
                            isCompleted = isCompleted,
                            isSubmitting = isCompleting,
                            feedbackMessage = completionResult
                                ?.takeIf { it.lessonId == lessonContent.lessonId }
                                ?.message,
                            feedbackIsSuccess = completionResult?.isCompleted ?: isCompleted,
                            onMarkCompleted = {
                                learningViewModel?.completeCatalogueLesson(
                                    lesson = lessonContent,
                                    onSyncedWithBackend = onLessonCompletedSynced
                                )
                            }
                        )
                    } else {
                        // Dedicated Coming Soon Screen (replaces any Toast behaviour)
                        LessonComingSoonCard(
                            categoryName = category?.name ?: "DSA Catalogue",
                            topicName = subtopic?.name ?: "This Topic",
                            topicDescription = subtopic?.description
                                ?: "Structured lesson content for this topic is currently being authored.",
                            lessonType = currentOutline?.lessonType ?: "THEORY",
                            onAskAi = {
                                val topicLabel = subtopic?.name ?: "this DSA topic"
                                onAskAi("Teach me $topicLabel with beginner-friendly explanations, real-world examples, and Kotlin code.")
                            },
                            onBackToTopic = onBack
                        )
                    }
                }
            }

            // 4. Ask AI Ally Helper Card (shown on implemented lessons)
            if (lessonContent != null) {
                val aiShape = RoundedCornerShape(14.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(aiShape)
                        .background(cardBg)
                        .border(1.dp, border, aiShape)
                        .clickable {
                            onAskAi("I'm studying Lesson ${lessonContent.lessonNumber}: ${lessonContent.title}. Can you give me a quick practice question or clarify any tricky parts?")
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(purple.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        LessonIconCanvas(16.dp) { d -> drawLessonSparkles(d, purpleSoft) }
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "Have a doubt? Ask AI Ally",
                            style = TextStyle(
                                fontFamily = LessonInter,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.5.sp,
                                color = textPrimary
                            )
                        )
                        Text(
                            text = "Get extra examples or test your understanding interactively",
                            style = TextStyle(
                                fontFamily = LessonInter,
                                fontSize = 11.5.sp,
                                color = textSecondary
                            )
                        )
                    }
                    LessonIconCanvas(14.dp) { d -> drawLessonChevronRight(d, textSecondary) }
                }
            }

            // 5. Previous / Next Lesson Navigation
            if (prevOutline != null || nextOutline != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Previous Button
                    val prevEnabled = prevOutline != null
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (prevEnabled) cardBg else cardBg.copy(alpha = 0.4f))
                            .border(
                                1.dp,
                                if (prevEnabled) border else border.copy(alpha = 0.4f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable(enabled = prevEnabled) {
                                prevOutline?.let { navigateToOutline(it) }
                            }
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LessonIconCanvas(14.dp) { d ->
                            drawLessonChevronLeft(d, if (prevEnabled) textPrimary else textMuted)
                        }
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (prevOutline != null) "Prev Lesson" else "First Lesson",
                            style = TextStyle(
                                fontFamily = LessonInter,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.5.sp,
                                color = if (prevEnabled) textPrimary else textMuted
                            ),
                            maxLines = 1
                        )
                    }

                    // Next Button
                    val nextEnabled = nextOutline != null
                    val nextLabel = when {
                        nextOutline == null -> "End of Topic"
                        nextOutline.lessonType == "VISUALIZER" && nextOutline.hasContent -> "Visualizer Lab"
                        nextOutline.lessonType == "QUIZ" && nextOutline.hasContent -> "Take Quiz"
                        else -> "Next Lesson"
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (nextEnabled) purple.copy(alpha = 0.2f) else cardBg.copy(alpha = 0.4f)
                            )
                            .border(
                                1.dp,
                                if (nextEnabled) purple.copy(alpha = 0.6f) else border.copy(alpha = 0.4f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable(enabled = nextEnabled) {
                                nextOutline?.let { navigateToOutline(it) }
                            }
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = nextLabel,
                            style = TextStyle(
                                fontFamily = LessonInter,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.5.sp,
                                color = if (nextEnabled) textPrimary else textMuted
                            ),
                            maxLines = 1
                        )
                        Spacer(Modifier.width(6.dp))
                        LessonIconCanvas(14.dp) { d ->
                            drawLessonChevronRight(d, if (nextEnabled) cyan else textMuted)
                        }
                    }
                }
            }
        }

        // Shared Bottom Navigation Bar
        AlgoQuestBottomBar(selectedTab = 1, onSelectTab = onSelectTab)
    }
}

@Composable
private fun LearningObjectivesCard(objectives: List<String>) {
    val activeCardBg = LessonActiveCardBg
    val cyan = LessonCyan
    val textPrimary = LessonTextPrimary

    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(activeCardBg)
            .border(1.dp, cyan.copy(alpha = 0.4f), shape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        LessonBadge("LEARNING OBJECTIVES", cyan.copy(alpha = 0.16f), cyan)

        Text(
            text = "What You'll Master in This Lesson",
            style = TextStyle(
                fontFamily = LessonPoppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = textPrimary
            )
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            objectives.forEach { obj ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 3.dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(cyan.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        LessonIconCanvas(10.dp) { d -> drawLessonCheck(d, cyan) }
                    }
                    Text(
                        text = obj,
                        style = TextStyle(
                            fontFamily = LessonInter,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = textPrimary.copy(alpha = 0.92f)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ComplexityAnalysisCard(timeComplexity: String?, spaceComplexity: String?) {
    val cardBg = LessonCardBg
    val border = LessonBorder
    val cyan = LessonCyan
    val purple = LessonPurpleSoft
    val green = LessonGreen
    val textPrimary = LessonTextPrimary

    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(cardBg)
            .border(1.dp, border, shape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        LessonBadge("ASYMPTOTIC COMPLEXITY", green.copy(alpha = 0.14f), green)

        Text(
            text = "Algorithm Efficiency Profile",
            style = TextStyle(
                fontFamily = LessonPoppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = textPrimary
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!timeComplexity.isNullOrBlank()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(cyan.copy(alpha = 0.08f))
                        .border(1.dp, cyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "TIME COMPLEXITY",
                        style = TextStyle(fontFamily = LessonMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = cyan)
                    )
                    Text(
                        text = timeComplexity,
                        style = TextStyle(fontFamily = LessonMono, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    )
                }
            }
            if (!spaceComplexity.isNullOrBlank()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(purple.copy(alpha = 0.08f))
                        .border(1.dp, purple.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "SPACE COMPLEXITY",
                        style = TextStyle(fontFamily = LessonMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = purple)
                    )
                    Text(
                        text = spaceComplexity,
                        style = TextStyle(fontFamily = LessonMono, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonSectionCard(section: LessonSection) {
    val cardBg = LessonCardBg
    val border = LessonBorder
    val textPrimary = LessonTextPrimary
    val textSecondary = LessonTextSecondary
    val cyan = LessonCyan
    val purple = LessonPurple
    val purpleSoft = LessonPurpleSoft
    val green = LessonGreen
    val gold = LessonGold

    val (badgeBg, badgeColor) = when (section.type) {
        LessonSectionType.INTRODUCTION -> Pair(cyan.copy(alpha = 0.14f), cyan)
        LessonSectionType.CONCEPT_EXPLANATION -> Pair(purple.copy(alpha = 0.16f), purpleSoft)
        LessonSectionType.USE_CASES -> Pair(cyan.copy(alpha = 0.14f), cyan)
        LessonSectionType.REAL_WORLD_EXAMPLES -> Pair(gold.copy(alpha = 0.14f), gold)
        LessonSectionType.PERFORMANCE_IMPACT -> Pair(green.copy(alpha = 0.15f), green)
        LessonSectionType.KEY_TAKEAWAYS -> Pair(green.copy(alpha = 0.15f), green)
    }

    val cardShape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(cardBg)
            .border(1.dp, border, cardShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        LessonBadge(section.type.badgeLabel, badgeBg, badgeColor)

        Text(
            text = section.title,
            style = TextStyle(
                fontFamily = LessonPoppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = textPrimary
            )
        )

        Text(
            text = section.body,
            style = TextStyle(
                fontFamily = LessonInter,
                fontSize = 13.5.sp,
                lineHeight = 21.sp,
                color = textSecondary
            )
        )

        if (section.bulletPoints.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                section.bulletPoints.forEach { point ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 7.dp)
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(badgeColor)
                        )
                        Text(
                            text = point,
                            style = TextStyle(
                                fontFamily = LessonInter,
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                color = textPrimary.copy(alpha = 0.92f)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        if (!section.highlightNote.isNullOrBlank()) {
            val noteShape = RoundedCornerShape(12.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(noteShape)
                    .background(badgeColor.copy(alpha = 0.09f))
                    .border(1.dp, badgeColor.copy(alpha = 0.35f), noteShape)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "KEY INSIGHT",
                    style = TextStyle(
                        fontFamily = LessonMono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = badgeColor
                    )
                )
                Text(
                    text = section.highlightNote,
                    style = TextStyle(
                        fontFamily = LessonInter,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.5.sp,
                        lineHeight = 19.sp,
                        color = textPrimary
                    )
                )
            }
        }
    }
}

@Composable
private fun LessonCodeBlockCard(codeBlock: LessonCodeBlock) {
    val cardBg = LessonCardBg
    val codeBg = LessonCodeBg
    val border = LessonBorder
    val textPrimary = LessonTextPrimary
    val textSecondary = LessonTextSecondary
    val cyan = LessonCyan
    val purple = LessonPurple
    val purpleSoft = LessonPurpleSoft

    val cardShape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(cardBg)
            .border(1.dp, border, cardShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = codeBlock.title,
                style = TextStyle(
                    fontFamily = LessonPoppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.5.sp,
                    color = textPrimary
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            LessonBadge(codeBlock.language, purple.copy(alpha = 0.16f), purpleSoft)
        }

        // Scrollable Code Surface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(codeBg)
                .border(1.dp, border, RoundedCornerShape(12.dp))
                .horizontalScroll(rememberScrollState())
                .padding(14.dp)
        ) {
            Text(
                text = codeBlock.code,
                style = TextStyle(
                    fontFamily = LessonMono,
                    fontSize = 12.sp,
                    lineHeight = 19.sp,
                    color = cyan.copy(alpha = 0.95f)
                )
            )
        }

        // Step-by-Step Walkthrough
        if (codeBlock.stepByStepExplanation.isNotEmpty()) {
            Text(
                text = "Step-by-Step Explanation",
                style = TextStyle(
                    fontFamily = LessonInter,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = textPrimary
                )
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                codeBlock.stepByStepExplanation.forEach { step ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(purple.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = step.stepNumber.toString(),
                                style = TextStyle(
                                    fontFamily = LessonMono,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = purpleSoft
                                )
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = step.lineReference,
                                style = TextStyle(
                                    fontFamily = LessonMono,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    color = cyan
                                )
                            )
                            Text(
                                text = step.explanation,
                                style = TextStyle(
                                    fontFamily = LessonInter,
                                    fontSize = 12.5.sp,
                                    lineHeight = 19.sp,
                                    color = textSecondary
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyTakeawaysCard(takeaways: List<String>) {
    val activeCardBg = LessonActiveCardBg
    val green = LessonGreen
    val textPrimary = LessonTextPrimary

    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(activeCardBg)
            .border(1.dp, green.copy(alpha = 0.4f), shape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        LessonBadge("KEY TAKEAWAYS", green.copy(alpha = 0.16f), green)

        Text(
            text = "What to Remember",
            style = TextStyle(
                fontFamily = LessonPoppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = textPrimary
            )
        )

        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
            takeaways.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(green.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        LessonIconCanvas(11.dp) { d -> drawLessonCheck(d, green) }
                    }
                    Text(
                        text = item,
                        style = TextStyle(
                            fontFamily = LessonInter,
                            fontSize = 13.sp,
                            lineHeight = 19.5.sp,
                            color = textPrimary.copy(alpha = 0.94f)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonPracticeQuestionsCard(questions: List<LessonPracticeQuestion>) {
    val cardBg = LessonCardBg
    val border = LessonBorder
    val textPrimary = LessonTextPrimary
    val textSecondary = LessonTextSecondary
    val purple = LessonPurple
    val purpleSoft = LessonPurpleSoft
    val green = LessonGreen
    val red = Color(0xFFEF5350)

    val selectedAnswers = remember { mutableStateMapOf<String, Int>() }

    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(cardBg)
            .border(1.dp, purple.copy(alpha = 0.4f), shape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        LessonBadge("PRACTICE QUESTIONS & QUIZ", purple.copy(alpha = 0.16f), purpleSoft)

        Text(
            text = "Test Your Understanding",
            style = TextStyle(
                fontFamily = LessonPoppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = textPrimary
            )
        )

        questions.forEachIndexed { qIdx, pq ->
            val selectedOption = selectedAnswers[pq.id]
            val hasAnswered = selectedOption != null
            val isCorrect = selectedOption == pq.correctOptionIndex

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(LessonActiveCardBg)
                    .border(1.dp, border, RoundedCornerShape(12.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Q${qIdx + 1}. ${pq.question}",
                    style = TextStyle(
                        fontFamily = LessonInter,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.5.sp,
                        lineHeight = 20.sp,
                        color = textPrimary
                    )
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    pq.options.forEachIndexed { optIdx, optionText ->
                        val isThisSelected = selectedOption == optIdx
                        val isThisCorrect = optIdx == pq.correctOptionIndex

                        val (optBg, optBorder, optText) = when {
                            !hasAnswered -> Triple(cardBg, border, textPrimary)
                            isThisSelected && isCorrect -> Triple(green.copy(alpha = 0.16f), green, green)
                            isThisSelected && !isCorrect -> Triple(red.copy(alpha = 0.16f), red, red)
                            isThisCorrect -> Triple(green.copy(alpha = 0.12f), green.copy(alpha = 0.6f), green)
                            else -> Triple(cardBg.copy(alpha = 0.5f), border.copy(alpha = 0.5f), textSecondary)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(optBg)
                                .border(1.dp, optBorder, RoundedCornerShape(8.dp))
                                .clickable(enabled = !hasAnswered) {
                                    selectedAnswers[pq.id] = optIdx
                                }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val optionLabel = ('A' + optIdx).toString()
                            Text(
                                text = "[$optionLabel]",
                                style = TextStyle(fontFamily = LessonMono, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = optText)
                            )
                            Text(
                                text = optionText,
                                style = TextStyle(fontFamily = LessonInter, fontSize = 12.5.sp, lineHeight = 17.sp, color = optText),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                if (hasAnswered) {
                    val explanationBg = if (isCorrect) green.copy(alpha = 0.1f) else LessonGold.copy(alpha = 0.1f)
                    val explanationBorder = if (isCorrect) green.copy(alpha = 0.4f) else LessonGold.copy(alpha = 0.4f)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(explanationBg)
                            .border(1.dp, explanationBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (isCorrect) "✓ Correct!" else "Explanation:",
                            style = TextStyle(fontFamily = LessonInter, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (isCorrect) green else LessonGold)
                        )
                        Text(
                            text = pq.explanation,
                            style = TextStyle(fontFamily = LessonInter, fontSize = 12.sp, lineHeight = 17.sp, color = textPrimary)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonCompletionCard(
    lesson: DsaLessonContent,
    isCompleted: Boolean,
    isSubmitting: Boolean,
    feedbackMessage: String?,
    feedbackIsSuccess: Boolean,
    onMarkCompleted: () -> Unit
) {
    val cardBg = LessonCardBg
    val border = LessonBorder
    val textPrimary = LessonTextPrimary
    val textSecondary = LessonTextSecondary
    val purple = LessonPurple
    val cyan = LessonCyan
    val green = LessonGreen
    val gold = LessonGold

    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(cardBg)
            .border(
                1.dp,
                if (isCompleted) green.copy(alpha = 0.45f) else border,
                shape
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isCompleted) "Lesson Completed!" else "Finish This Lesson",
                style = TextStyle(
                    fontFamily = LessonPoppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = if (isCompleted) green else textPrimary
                )
            )
            LessonBadge(
                text = if (lesson.backendLessonId != null) "+${lesson.xpReward} XP REWARD" else "+${lesson.xpReward} XP TARGET",
                background = gold.copy(alpha = 0.14f),
                textColor = gold
            )
        }

        Text(
            text = if (isCompleted) {
                "Great job finishing Lesson ${lesson.lessonNumber.toString().padStart(2, '0')}! Your progress for this topic has been updated."
            } else {
                "Once you've reviewed the concepts and code examples above, mark this lesson as completed to track your topic progress."
            },
            style = TextStyle(
                fontFamily = LessonInter,
                fontSize = 12.5.sp,
                lineHeight = 18.sp,
                color = textSecondary
            )
        )

        val btnShape = RoundedCornerShape(12.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(btnShape)
                .background(
                    when {
                        isCompleted -> Brush.horizontalGradient(listOf(green.copy(alpha = 0.2f), green.copy(alpha = 0.14f)))
                        else -> Brush.horizontalGradient(listOf(purple, cyan.copy(alpha = 0.85f)))
                    }
                )
                .then(
                    if (isCompleted) Modifier.border(1.dp, green.copy(alpha = 0.5f), btnShape) else Modifier
                )
                .clickable(enabled = !isCompleted && !isSubmitting, onClick = onMarkCompleted),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isCompleted) {
                LessonIconCanvas(16.dp) { d -> drawLessonCheck(d, green) }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Completed",
                    style = TextStyle(
                        fontFamily = LessonPoppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = green
                    )
                )
            } else {
                Text(
                    text = if (isSubmitting) "Saving Progress..." else "Mark as Completed",
                    style = TextStyle(
                        fontFamily = LessonPoppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                )
            }
        }

        if (!feedbackMessage.isNullOrBlank()) {
            val bannerColor = if (feedbackIsSuccess) green else gold
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(bannerColor.copy(alpha = 0.1f))
                    .border(1.dp, bannerColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = feedbackMessage,
                    style = TextStyle(
                        fontFamily = LessonInter,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = textPrimary
                    )
                )
            }
        }
    }
}

@Composable
private fun LessonComingSoonCard(
    categoryName: String,
    topicName: String,
    topicDescription: String,
    lessonType: String,
    onAskAi: () -> Unit,
    onBackToTopic: () -> Unit
) {
    val cardBg = LessonCardBg
    val activeCardBg = LessonActiveCardBg
    val border = LessonBorder
    val textPrimary = LessonTextPrimary
    val textSecondary = LessonTextSecondary
    val purple = LessonPurple
    val purpleSoft = LessonPurpleSoft
    val cyan = LessonCyan
    val gold = LessonGold

    val heading = when (lessonType) {
        "VISUALIZER" -> "Interactive Visualizer Coming Soon"
        "QUIZ" -> "Topic Mastery Quiz Coming Soon"
        else -> "Lesson Content Coming Soon"
    }

    val subMessage = when (lessonType) {
        "VISUALIZER" -> "We are building a step-by-step visual simulation lab for $topicName. In the meantime, you can explore Binary Search, Bubble Sort, and Two Pointers in the Visualizer tab or ask AI Ally to walk through it!"
        "QUIZ" -> "A dedicated mastery quiz with practice questions for $topicName is being prepared. You can ask AI Ally to quiz you on this topic right now!"
        else -> "Full structured lesson notes, real-world analogies, and annotated code examples for $topicName ($categoryName) are being authored for an upcoming update."
    }

    val cardShape = RoundedCornerShape(18.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(cardBg)
            .border(1.dp, purple.copy(alpha = 0.4f), cardShape)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        LessonBadge("IN DEVELOPMENT", gold.copy(alpha = 0.14f), gold)

        Text(
            text = heading,
            style = TextStyle(
                fontFamily = LessonPoppins,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                color = textPrimary
            )
        )

        Text(
            text = subMessage,
            style = TextStyle(
                fontFamily = LessonInter,
                fontSize = 13.5.sp,
                lineHeight = 20.sp,
                color = textSecondary
            )
        )

        // Topic Overview preview box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(activeCardBg)
                .border(1.dp, border, RoundedCornerShape(12.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "TOPIC OVERVIEW",
                style = TextStyle(
                    fontFamily = LessonMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = cyan
                )
            )
            Text(
                text = topicDescription,
                style = TextStyle(
                    fontFamily = LessonInter,
                    fontSize = 12.5.sp,
                    lineHeight = 19.sp,
                    color = textPrimary
                )
            )
        }

        // Ask AI Ally CTA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.horizontalGradient(listOf(purple, cyan.copy(alpha = 0.85f))))
                .clickable(onClick = onAskAi),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LessonIconCanvas(16.dp) { d -> drawLessonSparkles(d, Color.White) }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Learn Now with AI Ally",
                style = TextStyle(
                    fontFamily = LessonPoppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.5.sp,
                    color = Color.White
                )
            )
        }

        // Back to Topic button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(activeCardBg)
                .border(1.dp, border, RoundedCornerShape(12.dp))
                .clickable(onClick = onBackToTopic),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Back to $topicName",
                style = TextStyle(
                    fontFamily = LessonInter,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = purpleSoft
                )
            )
        }
    }
}

@Composable
private fun LessonBadge(text: String, background: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .height(24.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(background)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = LessonMono,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = textColor
            ),
            maxLines = 1
        )
    }
}

// ---------- Lightweight Canvas Icons ----------

@Composable
private fun LessonIconCanvas(size: Dp, draw: DrawScope.(Float) -> Unit) {
    Canvas(Modifier.size(size)) { draw(this, density) }
}

private fun DrawScope.drawLessonChevronLeft(d: Float, color: Color) {
    val w = 1.8f * d
    drawLine(color, Offset(11f * d, 4f * d), Offset(5f * d, 10f * d), w, StrokeCap.Round)
    drawLine(color, Offset(5f * d, 10f * d), Offset(11f * d, 16f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawLessonChevronRight(d: Float, color: Color) {
    val w = 1.6f * d
    drawLine(color, Offset(5f * d, 3f * d), Offset(10f * d, 8f * d), w, StrokeCap.Round)
    drawLine(color, Offset(10f * d, 8f * d), Offset(5f * d, 13f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawLessonCheck(d: Float, color: Color) {
    val w = 1.9f * d
    drawLine(color, Offset(2.5f * d, 6.2f * d), Offset(5f * d, 8.8f * d), w, StrokeCap.Round)
    drawLine(color, Offset(5f * d, 8.8f * d), Offset(9.5f * d, 3.2f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawLessonSparkles(d: Float, color: Color) {
    drawLessonStar(d, color, cx = 8f, cy = 11f, r = 5.5f)
    drawLessonStar(d, color, cx = 14f, cy = 4.5f, r = 2.8f)
}

private fun DrawScope.drawLessonStar(d: Float, color: Color, cx: Float, cy: Float, r: Float) {
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
    drawPath(path, color, style = Stroke(width = 1.2f * d, join = StrokeJoin.Round))
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun LessonScreenPreview() {
    LessonScreen(
        subtopicId = "prog_intro_ds",
        lessonId = DsaLessonContentRepository.LESSON_INTRO_DS_01_ID
    )
}
