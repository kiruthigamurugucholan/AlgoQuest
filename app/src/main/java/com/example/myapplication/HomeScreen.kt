package com.example.myapplication

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import com.example.myapplication.data.model.response.AchievementDto
import com.example.myapplication.ui.theme.AlgoTheme
import com.example.myapplication.viewmodel.DashboardUiState
import com.example.myapplication.viewmodel.DashboardViewModel
import com.example.myapplication.viewmodel.ProfileUiState
import com.example.myapplication.viewmodel.ProfileViewModel

private val Bg: Color @Composable get() = AlgoTheme.colors.bg
private val TextPrimary: Color @Composable get() = AlgoTheme.colors.textPrimary
private val TextSecondary: Color @Composable get() = AlgoTheme.colors.textSecondary
private val Cyan: Color @Composable get() = AlgoTheme.colors.cyan
private val Purple: Color @Composable get() = AlgoTheme.colors.purple
private val PurpleSoft: Color @Composable get() = AlgoTheme.colors.purpleSoft
private val LinkColor: Color @Composable get() = AlgoTheme.colors.link
private val Gold: Color @Composable get() = AlgoTheme.colors.gold
private val CardBg: Color @Composable get() = AlgoTheme.colors.cardBg
private val QuestCardBg: Color @Composable get() = AlgoTheme.colors.activeCardBg
private val BorderColor: Color @Composable get() = AlgoTheme.colors.border

private val Poppins: FontFamily = FontFamily.SansSerif
private val Inter: FontFamily = FontFamily.SansSerif
private val Mono: FontFamily = FontFamily.Monospace

@Composable
fun HomeScreen(
    dashboardViewModel: DashboardViewModel? = null,
    profileViewModel: ProfileViewModel? = null,
    onContinueQuest: () -> Unit = {},
    onQuestMap: () -> Unit = {},
    onOpenChapter: () -> Unit = {},
    onAskAi: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenVisualizer: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onSelectTab: (Int) -> Unit = {},
    showBottomBar: Boolean = true
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val tokenManager = remember {
        com.example.myapplication.data.remote.TokenManager.getInstance(context)
    }
    val currentUserId = tokenManager.getUserId()
    val cachedUser = tokenManager.getUser()

    LaunchedEffect(currentUserId) {
        if (!currentUserId.isNullOrBlank()) {
            dashboardViewModel?.loadDashboard()
            profileViewModel?.loadProfile()
        }
    }

    val uiState by dashboardViewModel?.uiState?.collectAsState() ?: remember {
        mutableStateOf(null)
    }
    val profileUiState by profileViewModel?.uiState?.collectAsState() ?: remember {
        mutableStateOf(null)
    }

    val dashboardData = (uiState as? DashboardUiState.Success)?.data?.takeIf {
        it.user.id.toString() == currentUserId
    }
    val profileData = (profileUiState as? ProfileUiState.Success)?.profile?.takeIf {
        it.user.id.toString() == currentUserId
    }
    val safeCachedUser = cachedUser?.takeIf { it.id.toString() == currentUserId }

    // Dynamic user metrics from authenticated session
    val userName = dashboardData?.user?.name ?: profileData?.user?.name ?: safeCachedUser?.name ?: "Explorer"
    val userLevel = dashboardData?.user?.level ?: profileData?.user?.level ?: safeCachedUser?.level ?: 1
    val userStreak = dashboardData?.user?.streak ?: profileData?.user?.streak ?: safeCachedUser?.streak ?: 1
    val rawXp = dashboardData?.user?.xp ?: profileData?.user?.xp ?: safeCachedUser?.xp ?: 0
    val userXp = String.format("%,d", rawXp)
    val percentile = dashboardData?.user?.percentile ?: profileData?.user?.percentile ?: "top 90%"

    // Level progression calculation
    val levelBaseXp = ((userLevel - 1) * 500).coerceAtLeast(0)
    val nextLevelTargetXp = (userLevel * 500).coerceAtLeast(500)
    val xpInCurrentLevel = (rawXp - levelBaseXp).coerceAtLeast(0)
    val xpRangeForLevel = (nextLevelTargetXp - levelBaseXp).coerceAtLeast(1)
    val levelProgress = (xpInCurrentLevel.toFloat() / xpRangeForLevel.toFloat()).coerceIn(0.08f, 1f)

    // Daily quest metrics
    val questTitle = dashboardData?.dailyQuest?.title ?: "Find it. Faster."
    val questDesc = dashboardData?.dailyQuest?.description ?: "Master binary search, visualize the logic,\nthen put your instincts to the test."
    val questXp = dashboardData?.dailyQuest?.xpReward ?: 150
    val questStep = dashboardData?.dailyQuest?.currentStep ?: 1
    val questTotalSteps = (dashboardData?.dailyQuest?.requiredSteps ?: 3).coerceAtLeast(1)
    val isQuestCompleted = dashboardData?.dailyQuest?.isCompleted ?: (questStep >= questTotalSteps)
    val targetQuestProgress = if (isQuestCompleted) 1f else (questStep.toFloat() / questTotalSteps.toFloat()).coerceIn(0.1f, 1f)
    val animatedQuestProgress by animateFloatAsState(
        targetValue = targetQuestProgress,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "questProgress"
    )

    // Active learning topic
    val activeTopicTitle = dashboardData?.activeTopic?.title ?: "Searching algorithms"
    val activeTopicCompleted = dashboardData?.activeTopic?.completedLessons ?: 2
    val activeTopicTotal = dashboardData?.activeTopic?.totalLessons ?: 5
    val activeTopicSubtitle = "Chapter 03 · $activeTopicCompleted of $activeTopicTotal lessons complete"

    // Achievements from real profile data
    val achievements = profileData?.achievements ?: emptyList()
    val unlockedAchievements = achievements.filter { it.isUnlocked }

    // Theme color cache for lambdas
    val cyan = Cyan
    val gold = Gold
    val purple = Purple
    val purpleSoft = PurpleSoft
    val textPrimary = TextPrimary
    val textSecondary = TextSecondary
    val borderColor = BorderColor
    val isDark = AlgoTheme.colors.isDark

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
            // 1. Top Header: Greeting, Level Badge & Settings Gear
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Welcome back,",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = textSecondary
                        )
                    )
                    Text(
                        text = userName,
                        style = TextStyle(
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = textPrimary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(onClick = onOpenProfile)
                    ) {
                        Badge(
                            text = String.format("LVL %02d", userLevel),
                            background = purple.copy(alpha = 0.12f),
                            textColor = purpleSoft
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CardBg)
                            .border(
                                width = 1.dp,
                                color = if (isDark) Color.White.copy(alpha = 0.08f) else borderColor,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable(onClick = onOpenSettings),
                        contentAlignment = Alignment.Center
                    ) {
                        IconCanvas(18.dp) { d -> drawGearIcon(d, cyan) }
                    }
                }
            }

            // 2. Progress Cards: Day Streak & Total XP (with level progress)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Streak Card
                MetricCard(
                    value = "$userStreak",
                    label = "Day Streak",
                    valueColor = gold,
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(gold.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🔥", fontSize = 13.sp)
                        }
                    },
                    subWidget = {
                        Text(
                            text = if (userStreak > 1) "Active streak · keep going" else "Day 1 · building habits",
                            style = TextStyle(fontFamily = Inter, fontSize = 10.5.sp, color = textSecondary),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onOpenProfile)
                )

                // Total XP Card
                MetricCard(
                    value = userXp,
                    label = "Total XP",
                    valueColor = cyan,
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(cyan.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            IconCanvas(13.dp) { d -> drawSparkles(d, cyan) }
                        }
                    },
                    subWidget = {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (isDark) Color.White.copy(alpha = 0.08f) else borderColor.copy(alpha = 0.5f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(levelProgress)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(cyan)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = percentile,
                                    style = TextStyle(fontFamily = Inter, fontSize = 10.sp, color = textSecondary)
                                )
                                Text(
                                    text = "LVL ${userLevel + 1}",
                                    style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 10.sp, color = cyan)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onOpenProfile)
                )
            }

            // 3. Daily Quest Card
            val questShape = RoundedCornerShape(16.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(questShape)
                    .background(QuestCardBg)
                    .border(
                        width = 1.dp,
                        color = if (isDark) Color.White.copy(alpha = 0.08f) else borderColor,
                        shape = questShape
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Badge("DAILY QUEST", purple.copy(alpha = 0.15f), purpleSoft)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(gold.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isQuestCompleted) "COMPLETED" else "+$questXp XP",
                            style = TextStyle(
                                fontFamily = Mono,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = if (isQuestCompleted) Color(0xFF10B981) else gold
                            )
                        )
                    }
                }
                Text(
                    text = questTitle,
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        lineHeight = 26.sp,
                        color = textPrimary
                    )
                )
                Text(
                    text = questDesc,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = textSecondary
                    )
                )

                // Animated Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isDark) Color.White.copy(alpha = 0.08f) else borderColor.copy(alpha = 0.4f))
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(animatedQuestProgress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    if (isQuestCompleted) listOf(cyan, Color(0xFF10B981))
                                    else listOf(purple, cyan)
                                )
                            )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isQuestCompleted) "All $questTotalSteps steps completed!" else "$questStep of $questTotalSteps steps complete",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.5.sp,
                            color = if (isQuestCompleted) Color(0xFF10B981) else textSecondary
                        )
                    )
                    Text(
                        text = if (isQuestCompleted) "Daily Goal Met" else "~12 min left",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontSize = 11.5.sp,
                            color = textSecondary
                        )
                    )
                }

                // Continue Quest Action Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isQuestCompleted) {
                                Brush.horizontalGradient(listOf(Color(0xFF059669), Color(0xFF10B981)))
                            } else {
                                Brush.horizontalGradient(listOf(purple, Color(0xFF7C6BF0)))
                            }
                        )
                        .clickable(onClick = onContinueQuest),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconCanvas(18.dp) { d ->
                        if (isQuestCompleted) drawCheckIcon(d) else drawArrowRight(d)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (isQuestCompleted) "Review quest" else "Continue quest",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.5.sp,
                            color = Color.White
                        )
                    )
                }
            }

            // 4. Learning Path Section
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
                        color = textPrimary
                    )
                )
                Text(
                    text = "Quest map",
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(onClick = onQuestMap)
                        .padding(horizontal = 4.dp, vertical = 2.dp),
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
                iconTint = cyan,
                onClick = onOpenChapter
            ) { d -> drawScanSearch(d, cyan) }

            PathCard(
                title = "Interactive Visualizer Lab",
                subtitle = "Step through Binary Search & Sorting",
                iconTint = gold,
                onClick = onOpenVisualizer
            ) { d -> drawLab(d, gold) }

            PathCard(
                title = "Your next breakthrough?",
                subtitle = "Ask Quest AI to explain any concept",
                iconTint = purpleSoft,
                onClick = onAskAi
            ) { d -> drawSparkles(d, purpleSoft) }

            // 5. Achievement Preview Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent achievements",
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = textPrimary
                    )
                )
                Text(
                    text = "View profile",
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(onClick = onOpenProfile)
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = LinkColor
                    )
                )
            }

            if (unlockedAchievements.isNotEmpty()) {
                unlockedAchievements.take(2).forEach { achievement ->
                    AchievementMiniCard(
                        achievement = achievement,
                        onClick = onOpenProfile
                    )
                }
            } else {
                AchievementEmptyState(onClick = onOpenProfile)
            }
        }

        // Shared Reusable Bottom Navigation Bar (if enabled in standalone usage)
        if (showBottomBar) {
            AlgoQuestBottomBar(selectedTab = 0, onSelectTab = onSelectTab)
        }
    }
}

@Composable
private fun MetricCard(
    value: String,
    label: String,
    valueColor: Color,
    icon: @Composable () -> Unit,
    subWidget: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(CardBg)
            .border(
                1.dp,
                if (AlgoTheme.colors.isDark) Color.White.copy(alpha = 0.06f) else BorderColor,
                shape
            )
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label.uppercase(),
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.5.sp,
                    letterSpacing = 0.8.sp,
                    color = TextSecondary
                )
            )
            icon()
        }
        Text(
            text = value,
            style = TextStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                color = valueColor
            )
        )
        if (subWidget != null) {
            subWidget()
        }
    }
}

@Composable
private fun PathCard(
    title: String,
    subtitle: String,
    iconTint: Color,
    onClick: () -> Unit,
    icon: DrawScope.(Float) -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    val textSecondary = TextSecondary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                if (AlgoTheme.colors.isDark) {
                    Brush.horizontalGradient(listOf(Color(0xFF222842), CardBg))
                } else {
                    Brush.horizontalGradient(listOf(Color(0xFFF8FAFC), CardBg))
                }
            )
            .border(
                width = 1.dp,
                color = if (AlgoTheme.colors.isDark) Color.White.copy(alpha = 0.07f) else BorderColor,
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconTint.copy(alpha = 0.12f))
                .border(
                    width = 1.dp,
                    color = iconTint.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(12.dp)
                ),
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
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.5.sp,
                    color = TextPrimary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 12.sp,
                    color = textSecondary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (AlgoTheme.colors.isDark) Color.White.copy(alpha = 0.04f) else Color.Black.copy(alpha = 0.03f)),
            contentAlignment = Alignment.Center
        ) {
            IconCanvas(14.dp) { d -> drawChevronRight(d, textSecondary) }
        }
    }
}

@Composable
private fun AchievementMiniCard(
    achievement: AchievementDto,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(CardBg)
            .border(
                width = 1.dp,
                color = if (AlgoTheme.colors.isDark) Color.White.copy(alpha = 0.06f) else BorderColor,
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Gold.copy(alpha = 0.12f))
                .border(1.dp, Gold.copy(alpha = 0.25f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = achievement.iconEmoji.ifBlank { "🏆" },
                fontSize = 18.sp
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = achievement.title,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.5.sp,
                    color = TextPrimary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = achievement.description,
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 11.5.sp,
                    color = TextSecondary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Badge(
            text = "+${achievement.xpReward} XP",
            background = Gold.copy(alpha = 0.1f),
            textColor = Gold
        )
    }
}

@Composable
private fun AchievementEmptyState(
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(CardBg)
            .border(
                width = 1.dp,
                color = if (AlgoTheme.colors.isDark) Color.White.copy(alpha = 0.06f) else BorderColor,
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Purple.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🎯", fontSize = 18.sp)
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "No badges unlocked yet",
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
            )
            Text(
                text = "Complete lessons and daily quests to earn badges!",
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 11.5.sp,
                    color = TextSecondary
                )
            )
        }
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

private fun DrawScope.drawCheckIcon(d: Float) {
    val w = 2.2f * d
    drawLine(Color.White, Offset(4f * d, 9f * d), Offset(7.5f * d, 13f * d), w, StrokeCap.Round)
    drawLine(Color.White, Offset(7.5f * d, 13f * d), Offset(14f * d, 5f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawChevronRight(d: Float, color: Color) {
    val w = 1.6f * d
    drawLine(color, Offset(6f * d, 3f * d), Offset(11f * d, 8f * d), w, StrokeCap.Round)
    drawLine(color, Offset(11f * d, 8f * d), Offset(6f * d, 13f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawScanSearch(d: Float, c: Color) {
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

private fun DrawScope.drawLab(d: Float, c: Color) {
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

private fun DrawScope.drawGearIcon(d: Float, c: Color) {
    val cx = 9f * d
    val cy = 9f * d
    drawCircle(c, radius = 3.4f * d, center = Offset(cx, cy), style = lineStroke(d, 1.4f))
    drawCircle(c, radius = 1.4f * d, center = Offset(cx, cy))
    for (i in 0 until 8) {
        val angle = (i * 45f) * (Math.PI / 180f)
        val cosA = Math.cos(angle).toFloat()
        val sinA = Math.sin(angle).toFloat()
        val start = Offset(cx + cosA * 3.6f * d, cy + sinA * 3.6f * d)
        val end = Offset(cx + cosA * 6.2f * d, cy + sinA * 6.2f * d)
        drawLine(c, start, end, 1.6f * d, StrokeCap.Round)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}
