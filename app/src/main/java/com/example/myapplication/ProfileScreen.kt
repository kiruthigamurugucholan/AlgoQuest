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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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

data class AchievementBadge(
    val title: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean
)

@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {},
    onSelectTab: (Int) -> Unit = {}
) {
    val achievements = listOf(
        AchievementBadge("Streak Master", "Maintained 7-day streak", "🔥", true),
        AchievementBadge("Binary Searcher", "Mastered Binary Search", "⚡", true),
        AchievementBadge("AI Explorer", "Asked 10 questions to Quest AI", "🤖", true),
        AchievementBadge("Array Legend", "Completed Array chapter", "🎯", true),
        AchievementBadge("Graph Pioneer", "Unlock Chapter 07", "🔒", false),
        AchievementBadge("DP Titan", "Unlock Chapter 08", "🔒", false)
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
                        text = "AM",
                        style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 26.sp, color = Color.White)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(text = "Alex Morgan", style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, color = TextPrimary))
                    Text(text = "@alex_morgan · Joined October 2024", style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = TextSecondary))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Badge("LVL 08 EXPLORER", Purple.copy(alpha = 0.15f), PurpleSoft)
                    Badge("TOP 12% GLOBAL", Gold.copy(alpha = 0.12f), Gold)
                }
            }

            // Stats Matrix
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard("7 🔥", "Day Streak", Gold, Modifier.weight(1f))
                StatCard("2,450", "Total XP", Cyan, Modifier.weight(1f))
                StatCard("14 🎯", "Lessons Solved", Green, Modifier.weight(1f))
            }

            // Skill Mastery Breakdown
            SectionTitle("Skill Mastery")

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SkillBar("Array & Two Pointers", 0.92f, "92%", Green)
                SkillBar("Searching Algorithms", 0.85f, "85%", Cyan)
                SkillBar("Sorting Algorithms", 0.60f, "60%", Gold)
                SkillBar("Trees & Binary Trees", 0.25f, "25%", PurpleSoft)
            }

            // Badges Grid
            SectionTitle("Achievements & Badges")

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BadgeItem(achievements[0], Modifier.weight(1f))
                    BadgeItem(achievements[1], Modifier.weight(1f))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BadgeItem(achievements[2], Modifier.weight(1f))
                    BadgeItem(achievements[3], Modifier.weight(1f))
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
                Text(text = "Log out", style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = TextMuted))
            }
        }

        // Shared Bottom Navigation Bar
        AlgoQuestBottomBar(selectedTab = 4, onSelectTab = onSelectTab)
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
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}

@Composable
private fun BadgeItem(badge: AchievementBadge, modifier: Modifier = Modifier) {
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
            Text(text = badge.iconEmoji, style = TextStyle(fontSize = 16.sp))
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
    drawLine(TextPrimary, Offset(11f * d, 4f * d), Offset(5f * d, 10f * d), w, StrokeCap.Round)
    drawLine(TextPrimary, Offset(5f * d, 10f * d), Offset(11f * d, 16f * d), w, StrokeCap.Round)
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen()
}
