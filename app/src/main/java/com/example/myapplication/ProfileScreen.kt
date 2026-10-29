package com.example.myapplication

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.response.AchievementDto
import com.example.myapplication.data.model.response.ProfileData
import com.example.myapplication.ui.components.ErrorRetryCard
import com.example.myapplication.ui.components.LoadingIndicator
import com.example.myapplication.viewmodel.ProfileUiState
import com.example.myapplication.viewmodel.ProfileViewModel

private val Bg = Color(0xFF0F1220)
private val TextPrimary = Color(0xFFF4F6FF)
private val TextSecondary = Color(0xFFC3CBE2)
private val TextMuted = Color(0xFF98A2C0)
private val Cyan = Color(0xFF00D2FF)
private val Purple = Color(0xFF6C5CE7)
private val PurpleLight = Color(0xFFA29BFE)
private val PurpleDark = Color(0xFF5B4BD5)
private val PurpleSoft = Color(0xFFB5A7FF)
private val Green = Color(0xFF2ECC71)
private val Gold = Color(0xFFFFB84D)
private val CardBg = Color(0xFF1A1F36)
private val ActiveCardBg = Color(0xFF25213E)

private val Poppins: FontFamily = FontFamily.SansSerif
private val Inter: FontFamily = FontFamily.SansSerif
private val Mono: FontFamily = FontFamily.Monospace

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel? = null,
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {},
    onSelectTab: (Int) -> Unit = {}
) {
    LaunchedEffect(viewModel) {
        viewModel?.loadProfile()
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
                    ProfileContent(
                        data = state.profile,
                        onBack = onBack,
                        onLogout = onLogout
                    )
                }
            }
        }

        // Shared Bottom Navigation Bar
        AlgoQuestBottomBar(selectedTab = 4, onSelectTab = onSelectTab)
    }
}

@Composable
private fun ProfileContent(
    data: ProfileData,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val user = data.user
    val skills = data.skills
    val achievements = data.achievements

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                    text = "Explorer Profile",
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                )
            }
            Badge("SETTINGS", CardBg, TextSecondary)
        }

        // Profile Identity Header
        val profileShape = RoundedCornerShape(20.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(profileShape)
                .background(ActiveCardBg)
                .border(1.dp, Color.White.copy(alpha = 0.08f), profileShape)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar Circle
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(PurpleLight, Purple, PurpleDark))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.initials.ifBlank { "AQ" },
                    style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 26.sp, color = Color.White)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = user.name,
                    style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, color = TextPrimary)
                )
                val joinedText = if (user.joined.startsWith("Joined", ignoreCase = true)) user.joined else "Joined ${user.joined}"
                Text(
                    text = "${user.email} · $joinedText",
                    style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = TextSecondary)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Badge("LVL ${user.level.toString().padStart(2, '0')} EXPLORER", Purple.copy(alpha = 0.15f), PurpleSoft)
                Badge("TOP ${user.percentile} GLOBAL", Gold.copy(alpha = 0.12f), Gold)
            }
        }

        // Stats Matrix
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard("${user.streak} 🔥", "Day Streak", Gold, Modifier.weight(1f))
            StatCard("${user.xp}", "Total XP", Cyan, Modifier.weight(1f))
            StatCard("${user.solvedLessons} 🎯", "Lessons Solved", Green, Modifier.weight(1f))
        }

        // Skill Mastery Breakdown
        SectionTitle("Skill Mastery")

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            skills.forEachIndexed { idx, skill ->
                val barColor = when (skill.colorTag.lowercase()) {
                    "green" -> Green
                    "cyan" -> Cyan
                    "gold" -> Gold
                    "purple" -> PurpleSoft
                    else -> when (idx % 4) {
                        0 -> Green
                        1 -> Cyan
                        2 -> Gold
                        else -> PurpleSoft
                    }
                }
                SkillBar(skill.title, skill.progress, skill.percent, barColor)
            }
        }

        // Badges Grid
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
                        Box(Modifier.weight(1f))
                    }
                }
            }
        }

        // Logout Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CardBg)
                .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                .clickable(onClick = onLogout),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Log out",
                style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = TextMuted)
            )
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, valueColor: Color, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(CardBg)
            .border(1.dp, Color.White.copy(alpha = 0.06f), shape)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = value, style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = valueColor))
        Text(text = label, style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = TextSecondary))
    }
}

@Composable
private fun SkillBar(title: String, progress: Float, percent: String, color: Color) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(CardBg)
            .border(1.dp, Color.White.copy(alpha = 0.05f), shape)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 12.sp, color = TextPrimary))
            Text(text = percent, style = TextStyle(fontFamily = Mono, fontSize = 11.sp, color = color))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(alpha = 0.06f))
        ) {
            Box(
                Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}

@Composable
private fun BadgeItem(badge: AchievementDto, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(if (badge.isUnlocked) ActiveCardBg else CardBg)
            .border(1.dp, if (badge.isUnlocked) Purple.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.04f), shape)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (badge.isUnlocked) Purple.copy(alpha = 0.2f) else CardBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (badge.isUnlocked) badge.iconEmoji else "🔒",
                style = TextStyle(fontSize = 16.sp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = badge.title,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = if (badge.isUnlocked) TextPrimary else TextMuted
                )
            )
            Text(
                text = badge.description,
                style = TextStyle(fontFamily = Inter, fontSize = 10.sp, color = TextSecondary)
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
    )
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

private fun DrawScope.drawChevronLeft(d: Float) {
    val w = 1.8f * d
    drawLine(TextPrimary, androidx.compose.ui.geometry.Offset(11f * d, 4f * d), androidx.compose.ui.geometry.Offset(5f * d, 10f * d), w, StrokeCap.Round)
    drawLine(TextPrimary, androidx.compose.ui.geometry.Offset(5f * d, 10f * d), androidx.compose.ui.geometry.Offset(11f * d, 16f * d), w, StrokeCap.Round)
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen()
}
