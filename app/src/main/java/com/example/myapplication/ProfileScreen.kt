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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.draw.shadow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.response.AchievementDto
import com.example.myapplication.data.model.response.ProfileData
import com.example.myapplication.ui.components.ErrorRetryCard
import com.example.myapplication.ui.components.LoadingIndicator
import com.example.myapplication.viewmodel.ProfileUiState
import com.example.myapplication.ui.theme.AlgoTheme
import com.example.myapplication.viewmodel.ProfileViewModel

private val Bg: Color @Composable get() = AlgoTheme.colors.bg
private val TextPrimary: Color @Composable get() = AlgoTheme.colors.textPrimary
private val TextSecondary: Color @Composable get() = AlgoTheme.colors.textSecondary
private val TextMuted: Color @Composable get() = AlgoTheme.colors.textMuted
private val Cyan: Color @Composable get() = AlgoTheme.colors.cyan
private val Purple: Color @Composable get() = AlgoTheme.colors.purple
private val PurpleLight: Color @Composable get() = AlgoTheme.colors.purpleLight
private val PurpleDark: Color @Composable get() = AlgoTheme.colors.purpleDark
private val PurpleSoft: Color @Composable get() = AlgoTheme.colors.purpleSoft
private val Green: Color @Composable get() = AlgoTheme.colors.green
private val Gold: Color @Composable get() = AlgoTheme.colors.gold
private val Pink: Color @Composable get() = AlgoTheme.colors.pink
private val CardBg: Color @Composable get() = AlgoTheme.colors.cardBg
private val ActiveCardBg: Color @Composable get() = AlgoTheme.colors.activeCardBg
private val BorderColor: Color @Composable get() = AlgoTheme.colors.border

private val Poppins: FontFamily = FontFamily.SansSerif
private val Inter: FontFamily = FontFamily.SansSerif
private val Mono: FontFamily @Composable get() = AlgoTheme.editorFontFamily

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel? = null,
    onBack: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onLogout: () -> Unit = {},
    onSelectTab: (Int) -> Unit = {},
    showBottomBar: Boolean = true
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val tokenManager = androidx.compose.runtime.remember {
        com.example.myapplication.data.remote.TokenManager.getInstance(context)
    }
    val currentUserId = tokenManager.getUserId()

    LaunchedEffect(currentUserId) {
        if (!currentUserId.isNullOrBlank()) {
            viewModel?.loadProfile()
        }
    }

    val uiState by viewModel?.uiState?.collectAsState() ?: androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf(ProfileUiState.Loading)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (val state = uiState) {
                is ProfileUiState.Loading -> {
                    LoadingIndicator(text = "Loading your explorer profile...")
                }
                is ProfileUiState.Empty -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorRetryCard(
                            message = "No profile data available. Tap Retry to reload.",
                            onRetry = { viewModel?.loadProfile() }
                        )
                    }
                }
                is ProfileUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorRetryCard(
                            message = state.message,
                            onRetry = { viewModel?.loadProfile() }
                        )
                    }
                }
                is ProfileUiState.Success -> {
                    if (state.profile.user.id.toString() == currentUserId) {
                        ProfileContent(
                            data = state.profile,
                            onBack = onBack,
                            onOpenSettings = onOpenSettings,
                            onLogoutClick = { showLogoutDialog = true }
                        )
                    } else {
                        LaunchedEffect(currentUserId) {
                            viewModel?.loadProfile()
                        }
                        LoadingIndicator(text = "Loading your explorer profile...")
                    }
                }
            }
        }

        // Shared Bottom Navigation Bar
        if (showBottomBar) {
            AlgoQuestBottomBar(selectedTab = 4, onSelectTab = onSelectTab)
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Confirm Logout",
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to log out of your account?",
                    style = TextStyle(
                        fontFamily = Inter,
                        fontSize = 14.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                )
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Pink.copy(alpha = 0.15f))
                        .border(1.dp, Pink.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable {
                            showLogoutDialog = false
                            onLogout()
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Confirm Logout",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Pink
                        )
                    )
                }
            },
            dismissButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (AlgoTheme.colors.isDark) Color.White.copy(alpha = 0.05f) else BorderColor.copy(alpha = 0.3f))
                        .border(1.dp, if (AlgoTheme.colors.isDark) Color.White.copy(alpha = 0.1f) else BorderColor, RoundedCornerShape(12.dp))
                        .clickable {
                            showLogoutDialog = false
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cancel",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    )
                }
            },
            containerColor = CardBg,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun ProfileContent(
    data: ProfileData,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit = {},
    onLogoutClick: () -> Unit
) {
    val user = data.user
    val skills = data.skills
    val achievements = data.achievements

    val purple = Purple
    val purpleLight = PurpleLight
    val purpleDark = PurpleDark
    val purpleSoft = PurpleSoft
    val cyan = Cyan
    val gold = Gold
    val green = Green
    val pink = Pink
    val textPrimary = TextPrimary
    val textSecondary = TextSecondary
    val textMuted = TextMuted
    val isDark = AlgoTheme.colors.isDark

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Top Header
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
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardBg)
                        .border(
                            1.dp,
                            if (isDark) Color.White.copy(alpha = 0.08f) else BorderColor,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    IconCanvas(16.dp) { d -> drawChevronLeft(d, textPrimary) }
                }
                Text(
                    text = "Explorer Profile",
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = textPrimary
                    )
                )
            }
            Box(
                modifier = Modifier
                    .height(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CardBg)
                    .border(
                        1.dp,
                        if (isDark) Color.White.copy(alpha = 0.1f) else BorderColor,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable(onClick = onOpenSettings)
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconCanvas(14.dp) { d -> drawGearIcon(d, purpleSoft) }
                    Text(
                        text = "SETTINGS",
                        style = TextStyle(
                            fontFamily = Mono,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.5.sp,
                            letterSpacing = 0.6.sp,
                            color = purpleSoft
                        )
                    )
                }
            }
        }

        // 2. Profile Identity Header Card
        val profileShape = RoundedCornerShape(22.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(profileShape)
                .background(
                    Brush.verticalGradient(
                        listOf(ActiveCardBg, CardBg)
                    )
                )
                .border(
                    1.5.dp,
                    Brush.linearGradient(
                        listOf(purple.copy(alpha = 0.35f), Color.White.copy(alpha = 0.08f), cyan.copy(alpha = 0.2f))
                    ),
                    profileShape
                )
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Polished Avatar with subtle purple gradient and glow
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(
                        elevation = 14.dp,
                        shape = CircleShape,
                        ambientColor = purple.copy(alpha = 0.45f),
                        spotColor = purpleSoft
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.sweepGradient(
                            listOf(purpleLight, cyan.copy(alpha = 0.7f), purple, purpleSoft, purpleLight)
                        )
                    )
                    .padding(2.5.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF8B77FF), purple, Color(0xFF382A88))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.initials.ifBlank { "AQ" },
                        style = TextStyle(
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }

            // Username and email typography
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = user.name,
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 21.sp,
                        color = textPrimary,
                        letterSpacing = 0.2.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val joinedText = if (user.joined.startsWith("Joined", ignoreCase = true)) user.joined else "Joined ${user.joined}"
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = user.email,
                        style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = textSecondary),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "•",
                        style = TextStyle(fontFamily = Inter, fontSize = 10.sp, color = textMuted)
                    )
                    Text(
                        text = joinedText,
                        style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = textSecondary),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Neatly aligned Level and Global Ranking Badges
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Badge(
                    text = "LVL ${user.level.toString().padStart(2, '0')} EXPLORER",
                    background = purple.copy(alpha = 0.14f),
                    textColor = purpleSoft,
                    borderColor = purple.copy(alpha = 0.35f)
                )
                Badge(
                    text = "TOP ${user.percentile} GLOBAL",
                    background = gold.copy(alpha = 0.12f),
                    textColor = gold,
                    borderColor = gold.copy(alpha = 0.3f)
                )
            }
        }

        // 3. Statistics Cards Matrix
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Card 1: Day Streak 🔥
            StatCard(
                value = "${user.streak}",
                label = "Day Streak",
                valueColor = gold,
                iconBadge = {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(gold.copy(alpha = 0.14f))
                            .border(1.dp, gold.copy(alpha = 0.28f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔥", style = TextStyle(fontSize = 15.sp))
                    }
                },
                modifier = Modifier.weight(1f)
            )

            // Card 2: Total XP
            StatCard(
                value = "${user.xp}",
                label = "Total XP",
                valueColor = cyan,
                iconBadge = {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(cyan.copy(alpha = 0.14f))
                            .border(1.dp, cyan.copy(alpha = 0.28f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconCanvas(15.dp) { d -> drawSparkles(d, cyan) }
                    }
                },
                modifier = Modifier.weight(1f)
            )

            // Card 3: Lessons Solved 🎯
            StatCard(
                value = "${user.solvedLessons}",
                label = "Lessons Solved",
                valueColor = green,
                iconBadge = {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(green.copy(alpha = 0.14f))
                            .border(1.dp, green.copy(alpha = 0.28f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🎯", style = TextStyle(fontSize = 15.sp))
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }

        // 4. Skill Mastery Breakdown
        SectionTitle("Skill Mastery")

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            skills.forEachIndexed { idx, skill ->
                val barColor = when (skill.colorTag.lowercase()) {
                    "green" -> green
                    "cyan" -> cyan
                    "gold" -> gold
                    "purple" -> purpleSoft
                    else -> when (idx % 4) {
                        0 -> green
                        1 -> cyan
                        2 -> gold
                        else -> purpleSoft
                    }
                }
                SkillBar(skill.title, skill.progress, skill.percent, barColor)
            }
        }

        // 5. Achievements & Badges Grid
        SectionTitle("Achievements & Badges")

        val chunkedBadges = achievements.chunked(2)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            chunkedBadges.forEach { rowBadges ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowBadges.forEach { badge ->
                        BadgeItem(badge, Modifier.weight(1f))
                    }
                    if (rowBadges.size == 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }

        // 6. Logout Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CardBg)
                .border(1.dp, pink.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                .clickable(onClick = onLogoutClick),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Log out",
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = pink
                )
            )
        }

        // Generous bottom clearance above the bottom navigation bar
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    valueColor: Color,
    iconBadge: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(18.dp)
    val isDark = AlgoTheme.colors.isDark

    Column(
        modifier = modifier
            .clip(shape)
            .background(CardBg)
            .border(
                1.dp,
                if (isDark) Color.White.copy(alpha = 0.06f) else BorderColor,
                shape
            )
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        iconBadge()

        Text(
            text = value,
            style = TextStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                letterSpacing = 0.2.sp,
                color = valueColor,
                textAlign = TextAlign.Center
            ),
            maxLines = 1
        )

        Text(
            text = label,
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SkillBar(title: String, progress: Float, percent: String, color: Color) {
    val shape = RoundedCornerShape(16.dp)
    val isDark = AlgoTheme.colors.isDark

    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "skillProgress"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(CardBg)
            .border(
                1.dp,
                if (isDark) Color.White.copy(alpha = 0.06f) else BorderColor,
                shape
            )
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(color.copy(alpha = 0.12f))
                    .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 7.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = percent,
                    style = TextStyle(
                        fontFamily = Mono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        color = color
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (isDark) Color.White.copy(alpha = 0.08f) else BorderColor.copy(alpha = 0.45f))
        ) {
            if (animatedProgress > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(color.copy(alpha = 0.8f), color)
                            )
                        )
                )
            }
        }
    }
}

@Composable
private fun BadgeItem(badge: AchievementDto, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(16.dp)
    val isDark = AlgoTheme.colors.isDark
    val isUnlocked = badge.isUnlocked
    val purple = Purple
    val purpleSoft = PurpleSoft
    val purpleLight = PurpleLight
    val gold = Gold

    val cardBackground = if (isUnlocked) {
        Brush.verticalGradient(listOf(ActiveCardBg, Color(0xFF221C3D)))
    } else {
        Brush.verticalGradient(
            listOf(
                CardBg.copy(alpha = 0.7f),
                CardBg.copy(alpha = 0.5f)
            )
        )
    }

    val cardBorder = if (isUnlocked) {
        purple.copy(alpha = 0.4f)
    } else {
        if (isDark) Color.White.copy(alpha = 0.04f) else BorderColor.copy(alpha = 0.6f)
    }

    Row(
        modifier = modifier
            .heightIn(min = 96.dp)
            .clip(shape)
            .background(cardBackground)
            .border(1.dp, cardBorder, shape)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isUnlocked) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        ambientColor = purpleSoft.copy(alpha = 0.35f),
                        spotColor = purple.copy(alpha = 0.5f)
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(purpleLight.copy(alpha = 0.4f), purple.copy(alpha = 0.25f))
                        )
                    )
                    .border(1.dp, purpleSoft.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badge.iconEmoji,
                    style = TextStyle(fontSize = 18.sp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color.White.copy(alpha = 0.04f) else BorderColor.copy(alpha = 0.2f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🔒",
                    style = TextStyle(fontSize = 15.sp)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = badge.title,
                style = TextStyle(
                    fontFamily = Poppins,
                    fontWeight = if (isUnlocked) FontWeight.SemiBold else FontWeight.Medium,
                    fontSize = 12.sp,
                    color = if (isUnlocked) TextPrimary else TextMuted
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = badge.description,
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 10.sp,
                    lineHeight = 13.5.sp,
                    color = if (isUnlocked) TextSecondary else TextMuted.copy(alpha = 0.7f)
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (isUnlocked) gold.copy(alpha = 0.12f)
                        else Color.White.copy(alpha = 0.04f)
                    )
                    .border(
                        0.5.dp,
                        if (isUnlocked) gold.copy(alpha = 0.25f)
                        else Color.White.copy(alpha = 0.08f),
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
                Text(
                    text = if (isUnlocked) "+${badge.xpReward} XP" else "LOCKED",
                    style = TextStyle(
                        fontFamily = Mono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.5.sp,
                        color = if (isUnlocked) gold else TextMuted
                    )
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    val purple = Purple
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .width(3.5.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(purple)
        )
        Text(
            text = text,
            style = TextStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                letterSpacing = 0.2.sp,
                color = TextPrimary
            )
        )
    }
}

@Composable
private fun Badge(
    text: String,
    background: Color,
    textColor: Color,
    borderColor: Color? = null,
    onClick: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = Modifier
            .height(26.dp)
            .clip(shape)
            .background(background)
            .then(
                if (borderColor != null) Modifier.border(1.dp, borderColor, shape)
                else Modifier
            )
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            )
            .padding(horizontal = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = Mono,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.5.sp,
                letterSpacing = 0.4.sp,
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

private fun lineStroke(d: Float, w: Float = 1.4f) =
    Stroke(width = w * d, cap = StrokeCap.Round, join = StrokeJoin.Round)

private fun DrawScope.drawChevronLeft(d: Float, color: Color) {
    val w = 1.8f * d
    drawLine(color, Offset(11f * d, 4f * d), Offset(5f * d, 10f * d), w, StrokeCap.Round)
    drawLine(color, Offset(5f * d, 10f * d), Offset(11f * d, 16f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawGearIcon(d: Float, c: Color) {
    val cx = 7f * d
    val cy = 7f * d
    drawCircle(c, radius = 2.6f * d, center = Offset(cx, cy), style = lineStroke(d, 1.2f))
    drawCircle(c, radius = 1.1f * d, center = Offset(cx, cy))
    for (i in 0 until 8) {
        val angle = (i * 45f) * (Math.PI / 180f)
        val cosA = Math.cos(angle).toFloat()
        val sinA = Math.sin(angle).toFloat()
        val start = Offset(cx + cosA * 2.8f * d, cy + sinA * 2.8f * d)
        val end = Offset(cx + cosA * 4.8f * d, cy + sinA * 4.8f * d)
        drawLine(c, start, end, 1.3f * d, StrokeCap.Round)
    }
}

private fun DrawScope.drawSparkles(d: Float, color: Color) {
    drawStar(d, color, cx = 6f, cy = 9f, r = 4.5f)
    drawStar(d, color, cx = 11.5f, cy = 4f, r = 2.5f)
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
    drawPath(path, color, style = lineStroke(d, 1.3f))
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen()
}
