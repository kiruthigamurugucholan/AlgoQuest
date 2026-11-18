package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.response.LearningResourceDto
import com.example.myapplication.data.model.response.LearningResourceTopicDto
import com.example.myapplication.data.repository.LearningResourceRepository
import com.example.myapplication.ui.theme.AlgoTheme
import com.example.myapplication.util.LearningResourceTextFormatter
import com.example.myapplication.viewmodel.LearningResourceUiState
import com.example.myapplication.viewmodel.LearningResourceViewModel

private val ResBg: Color @Composable get() = AlgoTheme.colors.bg
private val ResCardBg: Color @Composable get() = AlgoTheme.colors.cardBg
private val ResActiveCardBg: Color @Composable get() = AlgoTheme.colors.activeCardBg
private val ResFieldBg: Color @Composable get() = AlgoTheme.colors.fieldBg
private val ResBorder: Color @Composable get() = AlgoTheme.colors.border
private val ResTextPrimary: Color @Composable get() = AlgoTheme.colors.textPrimary
private val ResTextSecondary: Color @Composable get() = AlgoTheme.colors.textSecondary
private val ResTextMuted: Color @Composable get() = AlgoTheme.colors.textMuted
private val ResCyan: Color @Composable get() = AlgoTheme.colors.cyan
private val ResPurple: Color @Composable get() = AlgoTheme.colors.purple
private val ResPurpleSoft: Color @Composable get() = AlgoTheme.colors.purpleSoft
private val ResGreen: Color @Composable get() = AlgoTheme.colors.green
private val ResGold: Color @Composable get() = AlgoTheme.colors.gold

private val ResPoppins: FontFamily = FontFamily.SansSerif
private val ResInter: FontFamily = FontFamily.SansSerif
private val ResMono: FontFamily = FontFamily.Monospace

@Composable
fun LearningResourcesScreen(
    viewModel: LearningResourceViewModel? = null,
    initialTopic: String? = null,
    onBack: () -> Unit = {},
    onAskAi: (query: String) -> Unit = {},
    onSelectTab: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val topics by viewModel?.topics?.collectAsState()
        ?: remember { mutableStateOf(LearningResourceRepository.defaultSupportedTopics) }
    val categories by viewModel?.categories?.collectAsState()
        ?: remember { mutableStateOf(LearningResourceRepository.defaultCategories) }
    val searchQuery by viewModel?.searchQuery?.collectAsState()
        ?: remember { mutableStateOf("") }
    val selectedCategory by viewModel?.selectedCategory?.collectAsState()
        ?: remember { mutableStateOf(null) }
    val selectedTopicSlug by viewModel?.selectedTopicSlug?.collectAsState()
        ?: remember { mutableStateOf(null) }
    val uiState by viewModel?.uiState?.collectAsState()
        ?: remember { mutableStateOf<LearningResourceUiState>(LearningResourceUiState.Idle) }

    LaunchedEffect(Unit) {
        viewModel?.loadTopics()
    }

    LaunchedEffect(initialTopic) {
        val target = initialTopic?.trim()
        if (!target.isNullOrEmpty()) {
            viewModel?.fetchResource(target)
        }
    }

    val filteredTopics = remember(topics, searchQuery, selectedCategory) {
        LearningResourceViewModel.filterTopics(topics, searchQuery, selectedCategory)
    }

    val bg = ResBg
    val cardBg = ResCardBg
    val activeCardBg = ResActiveCardBg
    val fieldBg = ResFieldBg
    val border = ResBorder
    val textPrimary = ResTextPrimary
    val textSecondary = ResTextSecondary
    val textMuted = ResTextMuted
    val cyan = ResCyan
    val purple = ResPurple
    val purpleSoft = ResPurpleSoft
    val green = ResGreen
    val gold = ResGold

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .statusBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 44.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Top Header Bar
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
                        ResIconCanvas(16.dp) { d -> drawResChevronLeft(d, textPrimary) }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        Text(
                            text = "Learning Resources",
                            style = TextStyle(
                                fontFamily = ResPoppins,
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp,
                                color = textPrimary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Curated DSA Web Articles · ${topics.size} Topics",
                            style = TextStyle(
                                fontFamily = ResInter,
                                fontSize = 11.5.sp,
                                color = textSecondary
                            )
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
                if (uiState is LearningResourceUiState.Success) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(cyan.copy(alpha = 0.14f))
                            .border(1.dp, cyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { viewModel?.refreshCurrent() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "REFRESH",
                            style = TextStyle(
                                fontFamily = ResMono,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = cyan
                            )
                        )
                    }
                } else {
                    ResBadge("WEB SCRAPER", purple.copy(alpha = 0.16f), purpleSoft)
                }
            }

            // 2. Search Bar for DSA Topics
            val searchShape = RoundedCornerShape(14.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(searchShape)
                    .background(fieldBg)
                    .border(1.dp, border, searchShape)
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ResIconCanvas(16.dp) { d -> drawResSearch(d, textMuted) }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Search DSA topics (e.g. Binary Search, BFS, Stack)...",
                            style = TextStyle(
                                fontFamily = ResInter,
                                fontSize = 13.sp,
                                color = textMuted
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { viewModel?.updateSearchQuery(it) },
                        singleLine = true,
                        cursorBrush = SolidColor(cyan),
                        textStyle = TextStyle(
                            fontFamily = ResInter,
                            fontSize = 13.5.sp,
                            color = textPrimary
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { viewModel?.submitSearch() }),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (searchQuery.isNotEmpty()) {
                    Text(
                        text = "CLEAR",
                        style = TextStyle(
                            fontFamily = ResMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = cyan
                        ),
                        modifier = Modifier.clickable { viewModel?.updateSearchQuery("") }
                    )
                }
            }

            // 3. Category Filter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val allSelected = selectedCategory == null
                CategoryFilterChip(
                    label = "All (${topics.size})",
                    selected = allSelected,
                    accentColor = cyan,
                    onClick = { viewModel?.selectCategory(null) }
                )
                categories.forEach { cat ->
                    val count = topics.count { it.category.equals(cat, ignoreCase = true) }
                    CategoryFilterChip(
                        label = "$cat ($count)",
                        selected = selectedCategory.equals(cat, ignoreCase = true),
                        accentColor = purpleSoft,
                        onClick = { viewModel?.selectCategory(cat) }
                    )
                }
            }

            // 4. Selectable DSA Topic Chips
            if (filteredTopics.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Select a Topic",
                        style = TextStyle(
                            fontFamily = ResPoppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = textPrimary
                        )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filteredTopics.forEach { topic ->
                            val isSelected = selectedTopicSlug == topic.slug
                            TopicPillChip(
                                topic = topic,
                                selected = isSelected,
                                onClick = { viewModel?.fetchResource(topic.slug) }
                            )
                        }
                    }
                }
            }

            // 5. Dynamic Content States (Idle / Loading / Success / Empty / Error)
            when (val state = uiState) {
                is LearningResourceUiState.Idle -> {
                    ResourceIdleCard(
                        suggestedTopics = topics.take(6),
                        onPickTopic = { slug -> viewModel?.fetchResource(slug) }
                    )
                }

                is LearningResourceUiState.Loading -> {
                    ResourceLoadingCard(topicLabel = state.topicLabel)
                }

                is LearningResourceUiState.Success -> {
                    ResourceDetailContent(
                        resource = state.resource,
                        cached = state.cached,
                        onReadMore = { url ->
                            if (LearningResourceRepository.isSafeExternalUrl(url)) {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    Toast.makeText(
                                        context,
                                        "No browser app available to open this link.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Invalid or unsupported external article URL.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onAskAi = {
                            onAskAi("Summarize key interview concepts and time complexity for ${state.resource.topic}.")
                        }
                    )
                }

                is LearningResourceUiState.Empty -> {
                    ResourceEmptyCard(
                        query = state.query,
                        onResetSearch = {
                            viewModel?.updateSearchQuery("")
                            viewModel?.selectCategory(null)
                        },
                        onPickSuggestion = { slug ->
                            viewModel?.updateSearchQuery("")
                            viewModel?.fetchResource(slug)
                        }
                    )
                }

                is LearningResourceUiState.Error -> {
                    ResourceErrorCard(
                        message = state.message,
                        onRetry = { viewModel?.retry() }
                    )
                }
            }

            // Extra clearance spacer so bottom content never crowds or hides behind the floating bottom bar
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Shared Bottom Navigation Bar (unchanged design)
        AlgoQuestBottomBar(selectedTab = 1, onSelectTab = onSelectTab)
    }
}

@Composable
private fun CategoryFilterChip(
    label: String,
    selected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val cardBg = ResCardBg
    val border = ResBorder
    val textSecondary = ResTextSecondary

    Box(
        modifier = Modifier
            .height(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) accentColor.copy(alpha = 0.18f) else cardBg)
            .border(1.dp, if (selected) accentColor else border, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = ResInter,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.5.sp,
                color = if (selected) accentColor else textSecondary
            )
        )
    }
}

@Composable
private fun TopicPillChip(
    topic: LearningResourceTopicDto,
    selected: Boolean,
    onClick: () -> Unit
) {
    val cardBg = ResCardBg
    val activeBg = ResActiveCardBg
    val border = ResBorder
    val textPrimary = ResTextPrimary
    val textSecondary = ResTextSecondary
    val purple = ResPurple
    val cyan = ResCyan

    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = Modifier
            .clip(shape)
            .background(if (selected) activeBg else cardBg)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) cyan else border,
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = topic.name,
            style = TextStyle(
                fontFamily = ResInter,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                fontSize = 13.sp,
                color = if (selected) textPrimary else textSecondary
            )
        )
        Text(
            text = topic.category.uppercase(),
            style = TextStyle(
                fontFamily = ResMono,
                fontSize = 9.5.sp,
                color = if (selected) cyan else purple
            )
        )
    }
}

@Composable
private fun ResourceLoadingCard(topicLabel: String) {
    val cardBg = ResActiveCardBg
    val border = ResBorder
    val textPrimary = ResTextPrimary
    val textSecondary = ResTextSecondary
    val cyan = ResCyan

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg)
            .border(1.dp, border, RoundedCornerShape(18.dp))
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        CircularProgressIndicator(
            color = cyan,
            strokeWidth = 3.dp,
            modifier = Modifier.size(36.dp)
        )
        Text(
            text = "Fetching Learning Resource",
            style = TextStyle(
                fontFamily = ResPoppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = textPrimary
            )
        )
        Text(
            text = "Collecting curated educational notes and key concepts for $topicLabel...",
            style = TextStyle(
                fontFamily = ResInter,
                fontSize = 12.5.sp,
                lineHeight = 18.sp,
                color = textSecondary
            )
        )
    }
}

@Composable
private fun ResourceDetailContent(
    resource: LearningResourceDto,
    cached: Boolean,
    onReadMore: (String) -> Unit,
    onAskAi: () -> Unit
) {
    val cardBg = ResCardBg
    val activeCardBg = ResActiveCardBg
    val border = ResBorder
    val textPrimary = ResTextPrimary
    val textSecondary = ResTextSecondary
    val textMuted = ResTextMuted
    val cyan = ResCyan
    val purple = ResPurple
    val purpleSoft = ResPurpleSoft
    val green = ResGreen
    val gold = ResGold

    // Clean punctuation, normalize whitespace, and format mathematical expressions client-side
    val cleanResource = remember(resource) {
        LearningResourceTextFormatter.formatResource(resource)
    }

    var expandedContent by remember(cleanResource.topicSlug, cleanResource.sourceUrl) { mutableStateOf(false) }

    // Article Hero Card
    val heroShape = RoundedCornerShape(20.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(heroShape)
            .background(activeCardBg)
            .border(
                1.dp,
                Brush.horizontalGradient(listOf(purple.copy(alpha = 0.6f), cyan.copy(alpha = 0.6f))),
                heroShape
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ResBadge(
                    cleanResource.category.ifBlank { "DSA" }.uppercase(),
                    purple.copy(alpha = 0.16f),
                    purpleSoft
                )
                ResBadge(
                    cleanResource.sourceName.ifBlank { "WEB SOURCE" }.uppercase(),
                    cyan.copy(alpha = 0.14f),
                    cyan
                )
            }
            ResBadge(
                if (cached) "SAVED" else "LIVE",
                if (cached) gold.copy(alpha = 0.14f) else green.copy(alpha = 0.16f),
                if (cached) gold else green
            )
        }

        Text(
            text = cleanResource.title.ifBlank { cleanResource.topic },
            style = TextStyle(
                fontFamily = ResPoppins,
                fontWeight = FontWeight.Bold,
                fontSize = 21.sp,
                lineHeight = 28.sp,
                color = textPrimary
            )
        )

        Text(
            text = buildFormattedResourceText(
                text = cleanResource.description,
                mathColor = cyan
            ),
            style = TextStyle(
                fontFamily = ResInter,
                fontSize = 14.sp,
                lineHeight = 22.5.sp,
                color = textSecondary
            )
        )

        // Action Buttons: Read More + Ask AI Ally
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.horizontalGradient(listOf(purple, cyan.copy(alpha = 0.85f))))
                    .clickable { onReadMore(cleanResource.sourceUrl) },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Read More",
                    style = TextStyle(
                        fontFamily = ResPoppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.5.sp,
                        color = Color.White
                    )
                )
                Spacer(Modifier.width(6.dp))
                ResIconCanvas(14.dp) { d -> drawResExternalArrow(d, Color.White) }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(cardBg)
                    .border(1.dp, border, RoundedCornerShape(12.dp))
                    .clickable(onClick = onAskAi),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ask AI Ally",
                    style = TextStyle(
                        fontFamily = ResInter,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = textPrimary
                    )
                )
            }
        }
    }

    // Important Concepts Card
    if (cleanResource.importantPoints.isNotEmpty()) {
        val ptsShape = RoundedCornerShape(18.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ptsShape)
                .background(cardBg)
                .border(1.dp, border, ptsShape)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ResBadge("IMPORTANT CONCEPTS", gold.copy(alpha = 0.14f), gold)
                Text(
                    text = "${cleanResource.importantPoints.size} Key Points",
                    style = TextStyle(
                        fontFamily = ResMono,
                        fontSize = 10.5.sp,
                        color = textMuted
                    )
                )
            }
            Text(
                text = "Key Takeaways & Concepts",
                style = TextStyle(
                    fontFamily = ResPoppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = textPrimary
                )
            )
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                cleanResource.importantPoints.forEachIndexed { index, point ->
                    val itemShape = RoundedCornerShape(12.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(itemShape)
                            .background(activeCardBg.copy(alpha = 0.72f))
                            .border(1.dp, border.copy(alpha = 0.75f), itemShape)
                            .padding(horizontal = 12.dp, vertical = 11.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 1.dp)
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(cyan.copy(alpha = 0.16f))
                                .border(1.dp, cyan.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (index + 1).toString(),
                                style = TextStyle(
                                    fontFamily = ResMono,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.5.sp,
                                    color = cyan
                                )
                            )
                        }
                        Text(
                            text = buildFormattedResourceText(
                                text = point,
                                mathColor = cyan
                            ),
                            style = TextStyle(
                                fontFamily = ResInter,
                                fontSize = 13.5.sp,
                                lineHeight = 21.5.sp,
                                color = textPrimary.copy(alpha = 0.94f)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    // Section Headings Card
    if (cleanResource.headings.isNotEmpty()) {
        val hShape = RoundedCornerShape(18.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(hShape)
                .background(cardBg)
                .border(1.dp, border, hShape)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ResBadge("ARTICLE OUTLINE", purple.copy(alpha = 0.15f), purpleSoft)
            Text(
                text = "Topics Covered in Full Article",
                style = TextStyle(
                    fontFamily = ResPoppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.5.sp,
                    color = textPrimary
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                cleanResource.headings.forEach { heading ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9.dp))
                            .background(activeCardBg)
                            .border(1.dp, border, RoundedCornerShape(9.dp))
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = heading,
                            style = TextStyle(
                                fontFamily = ResInter,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = textPrimary
                            )
                        )
                    }
                }
            }
        }
    }

    // Detailed Reading Content Card
    if (cleanResource.content.isNotEmpty()) {
        val visibleParagraphs = if (expandedContent) cleanResource.content else cleanResource.content.take(3)
        val cShape = RoundedCornerShape(18.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cShape)
                .background(cardBg)
                .border(1.dp, border, cShape)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ResBadge("STUDY NOTES", cyan.copy(alpha = 0.14f), cyan)
                Text(
                    text = "${visibleParagraphs.size} of ${cleanResource.content.size} paragraphs",
                    style = TextStyle(
                        fontFamily = ResMono,
                        fontSize = 10.5.sp,
                        color = textMuted
                    )
                )
            }
            Text(
                text = "Educational Explanation",
                style = TextStyle(
                    fontFamily = ResPoppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.5.sp,
                    color = textPrimary
                )
            )
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                visibleParagraphs.forEach { paragraph ->
                    Text(
                        text = buildFormattedResourceText(
                            text = paragraph,
                            mathColor = cyan
                        ),
                        style = TextStyle(
                            fontFamily = ResInter,
                            fontSize = 14.sp,
                            lineHeight = 23.sp,
                            color = textSecondary
                        )
                    )
                }
            }

            if (cleanResource.content.size > 3) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(cyan.copy(alpha = 0.12f))
                        .border(1.dp, cyan.copy(alpha = 0.35f), RoundedCornerShape(11.dp))
                        .clickable { expandedContent = !expandedContent },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (expandedContent) "Show Less" else "Show All ${cleanResource.content.size} Paragraphs",
                        style = TextStyle(
                            fontFamily = ResInter,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = cyan
                        )
                    )
                }
            }

            // Source attribution footer block
            if (cleanResource.sourceUrl.isNotBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(activeCardBg.copy(alpha = 0.65f))
                        .border(1.dp, border.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                        .clickable { onReadMore(cleanResource.sourceUrl) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = "SOURCE · ${cleanResource.sourceName.ifBlank { "Educational Source" }.uppercase()}",
                        style = TextStyle(
                            fontFamily = ResMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = purpleSoft
                        )
                    )
                    Text(
                        text = cleanResource.sourceUrl,
                        style = TextStyle(
                            fontFamily = ResMono,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = textMuted
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private fun buildFormattedResourceText(
    text: String,
    mathColor: Color
): AnnotatedString {
    return buildAnnotatedString {
        append(text)
        val mathStyle = SpanStyle(
            fontFamily = ResMono,
            fontWeight = FontWeight.Medium,
            color = mathColor
        )
        LearningResourceTextFormatter.inlineMathHighlightRegex.findAll(text).forEach { match ->
            addStyle(
                style = mathStyle,
                start = match.range.first,
                end = match.range.last + 1
            )
        }
    }
}

@Composable
private fun ResourceIdleCard(
    suggestedTopics: List<LearningResourceTopicDto>,
    onPickTopic: (String) -> Unit
) {
    val cardBg = ResActiveCardBg
    val innerBg = ResCardBg
    val border = ResBorder
    val textPrimary = ResTextPrimary
    val textSecondary = ResTextSecondary
    val cyan = ResCyan
    val purpleSoft = ResPurpleSoft

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg)
            .border(1.dp, border, RoundedCornerShape(18.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ResBadge("EXPLORE DSA ARTICLES", cyan.copy(alpha = 0.14f), cyan)
        Text(
            text = "Discover Curated Study Resources",
            style = TextStyle(
                fontFamily = ResPoppins,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = textPrimary
            )
        )
        Text(
            text = "Search above or tap any topic chip to retrieve structured explanations, key concepts, article outlines, and verified external reading links.",
            style = TextStyle(
                fontFamily = ResInter,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = textSecondary
            )
        )
        Text(
            text = "Popular Starting Topics",
            style = TextStyle(
                fontFamily = ResInter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.5.sp,
                color = purpleSoft
            )
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            suggestedTopics.chunked(2).forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pair.forEach { item ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(innerBg)
                                .border(1.dp, border, RoundedCornerShape(10.dp))
                                .clickable { onPickTopic(item.slug) }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = item.name,
                                style = TextStyle(
                                    fontFamily = ResInter,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.5.sp,
                                    color = textPrimary
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    if (pair.size == 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ResourceEmptyCard(
    query: String,
    onResetSearch: () -> Unit,
    onPickSuggestion: (String) -> Unit
) {
    val cardBg = ResCardBg
    val border = ResBorder
    val textPrimary = ResTextPrimary
    val textSecondary = ResTextSecondary
    val gold = ResGold
    val cyan = ResCyan

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg)
            .border(1.dp, border, RoundedCornerShape(18.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ResBadge("NO MATCH FOUND", gold.copy(alpha = 0.14f), gold)
        Text(
            text = if (query.isNotBlank()) "No resources matched \"$query\"" else "No topic selected",
            style = TextStyle(
                fontFamily = ResPoppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = textPrimary
            )
        )
        Text(
            text = "Try searching for one of the 20 supported topics such as Binary Search, Bubble Sort, Stack, Queue, BFS, or Dynamic Programming.",
            style = TextStyle(
                fontFamily = ResInter,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = textSecondary
            )
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(cyan.copy(alpha = 0.15f))
                    .border(1.dp, cyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .clickable(onClick = onResetSearch)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Show All Topics",
                    style = TextStyle(fontFamily = ResInter, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp, color = cyan)
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(ResActiveCardBg)
                    .border(1.dp, border, RoundedCornerShape(10.dp))
                    .clickable { onPickSuggestion("binary_search") }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Try Binary Search",
                    style = TextStyle(fontFamily = ResInter, fontWeight = FontWeight.Medium, fontSize = 12.5.sp, color = textPrimary)
                )
            }
        }
    }
}

@Composable
private fun ResourceErrorCard(
    message: String,
    onRetry: () -> Unit
) {
    val cardBg = ResCardBg
    val textPrimary = ResTextPrimary
    val textSecondary = ResTextSecondary
    val purple = ResPurple
    val cyan = ResCyan
    val gold = ResGold

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg)
            .border(1.dp, gold.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ResBadge("CONNECTION ERROR", gold.copy(alpha = 0.16f), gold)
        Text(
            text = "Unable to Load Resource",
            style = TextStyle(
                fontFamily = ResPoppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                color = textPrimary
            )
        )
        Text(
            text = message,
            style = TextStyle(
                fontFamily = ResInter,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = textSecondary
            )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.horizontalGradient(listOf(purple, cyan.copy(alpha = 0.85f))))
                .clickable(onClick = onRetry),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Retry",
                style = TextStyle(
                    fontFamily = ResPoppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            )
        }
    }
}

@Composable
private fun ResBadge(text: String, background: Color, textColor: Color) {
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
                fontFamily = ResMono,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = textColor
            ),
            maxLines = 1
        )
    }
}

@Composable
private fun ResIconCanvas(size: Dp, draw: DrawScope.(Float) -> Unit) {
    Canvas(Modifier.size(size)) { draw(this, density) }
}

private fun DrawScope.drawResChevronLeft(d: Float, color: Color) {
    val w = 1.8f * d
    drawLine(color, Offset(11f * d, 4f * d), Offset(5f * d, 10f * d), w, StrokeCap.Round)
    drawLine(color, Offset(5f * d, 10f * d), Offset(11f * d, 16f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawResSearch(d: Float, color: Color) {
    drawCircle(
        color = color,
        radius = 4.5f * d,
        center = Offset(7f * d, 7f * d),
        style = Stroke(width = 1.5f * d, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
    drawLine(color, Offset(10.5f * d, 10.5f * d), Offset(14.5f * d, 14.5f * d), 1.8f * d, StrokeCap.Round)
}

private fun DrawScope.drawResExternalArrow(d: Float, color: Color) {
    val w = 1.6f * d
    drawLine(color, Offset(4f * d, 10f * d), Offset(10.5f * d, 3.5f * d), w, StrokeCap.Round)
    drawLine(color, Offset(6f * d, 3.5f * d), Offset(10.5f * d, 3.5f * d), w, StrokeCap.Round)
    drawLine(color, Offset(10.5f * d, 3.5f * d), Offset(10.5f * d, 8f * d), w, StrokeCap.Round)
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun LearningResourcesScreenPreview() {
    LearningResourcesScreen()
}
