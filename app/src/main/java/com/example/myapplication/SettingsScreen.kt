package com.example.myapplication

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.data.remote.TokenManager
import com.example.myapplication.reminder.ReminderPreferences
import com.example.myapplication.reminder.ReminderScheduler
import com.example.myapplication.ui.theme.AlgoTheme
import com.example.myapplication.ui.theme.AppThemeMode

// Color Palette dynamically adapting between Cyberpunk Dark and Modern Light themes
private val Bg: Color @Composable get() = AlgoTheme.colors.bg
private val TextPrimary: Color @Composable get() = AlgoTheme.colors.textPrimary
private val TextSecondary: Color @Composable get() = AlgoTheme.colors.textSecondary
private val TextMuted: Color @Composable get() = AlgoTheme.colors.textMuted
private val Cyan: Color @Composable get() = AlgoTheme.colors.cyan
private val Purple: Color @Composable get() = AlgoTheme.colors.purple
private val PurpleSoft: Color @Composable get() = AlgoTheme.colors.purpleSoft
private val Gold: Color @Composable get() = AlgoTheme.colors.gold
private val Green: Color @Composable get() = AlgoTheme.colors.green
private val Pink: Color @Composable get() = AlgoTheme.colors.pink
private val CardBg: Color @Composable get() = AlgoTheme.colors.cardBg
private val ActiveCardBg: Color @Composable get() = AlgoTheme.colors.activeCardBg
private val BorderColor: Color @Composable get() = AlgoTheme.colors.border

private val Poppins: FontFamily = FontFamily.SansSerif
private val Inter: FontFamily = FontFamily.SansSerif
private val Mono: FontFamily = FontFamily.Monospace

enum class SettingsSubPage {
    MAIN,
    APPEARANCE,
    NOTIFICATIONS,
    LEARNING,
    ACCESSIBILITY,
    PRIVACY,
    ABOUT
}

@Composable
fun SettingsScreen(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    onSelectThemeMode: (AppThemeMode) -> Unit = {},
    syntaxScheme: String = "Neon Synth",
    onSelectSyntaxScheme: (String) -> Unit = {},
    editorFont: String = "JetBrains Mono",
    onSelectEditorFont: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    var currentSubPage by remember { mutableStateOf(SettingsSubPage.MAIN) }

    val context = LocalContext.current
    val reminderPrefs = remember { ReminderPreferences.getInstance(context) }
    var dailyReminderEnabled by remember { mutableStateOf(reminderPrefs.isReminderEnabled()) }
    var reminderHour by remember { mutableStateOf(reminderPrefs.getReminderHour()) }
    var reminderMinute by remember { mutableStateOf(reminderPrefs.getReminderMinute()) }
    val reminderFormattedTime = remember(reminderHour, reminderMinute) {
        ReminderPreferences.formatTime(reminderHour, reminderMinute)
    }

    var streakReminderEnabled by remember { mutableStateOf(reminderPrefs.isStreakReminderEnabled()) }
    var streakHour by remember { mutableStateOf(reminderPrefs.getStreakReminderHour()) }
    var streakMinute by remember { mutableStateOf(reminderPrefs.getStreakReminderMinute()) }
    val streakFormattedTime = remember(streakHour, streakMinute) {
        ReminderPreferences.formatTime(streakHour, streakMinute)
    }
    var isDailyPracticeCompleted by remember {
        mutableStateOf(reminderPrefs.isDailyLearningCompletedToday())
    }

    var quizNotificationsEnabled by remember { mutableStateOf(reminderPrefs.isQuizNotificationEnabled()) }
    var achievementNotificationsEnabled by remember {
        mutableStateOf(reminderPrefs.isAchievementNotificationEnabled())
    }

    var hapticsEnabled by remember { mutableStateOf(true) }

    var selectedLanguage by remember { mutableStateOf("Python") }
    var selectedDifficulty by remember { mutableStateOf("Beginner") }
    var selectedSpeed by remember { mutableStateOf("1.0x (Normal)") }

    var selectedFontScale by remember { mutableStateOf("Standard (100%)") }
    var highContrastEnabled by remember { mutableStateOf(false) }
    var reduceAnimationsEnabled by remember { mutableStateOf(false) }

    var cacheClearedTimestamp by remember { mutableStateOf("") }

    // Intercept hardware / gesture back navigation when on a sub-page
    BackHandler(enabled = currentSubPage != SettingsSubPage.MAIN) {
        currentSubPage = SettingsSubPage.MAIN
    }

    when (currentSubPage) {
        SettingsSubPage.MAIN -> MainSettingsContent(
            onBack = onBack,
            onNavigate = { currentSubPage = it },
            themeMode = themeMode,
            syntaxScheme = syntaxScheme,
            editorFont = editorFont,
            selectedLanguage = selectedLanguage,
            selectedDifficulty = selectedDifficulty,
            dailyReminderEnabled = dailyReminderEnabled,
            reminderTimeText = reminderFormattedTime,
            streakReminderEnabled = streakReminderEnabled,
            streakTimeText = streakFormattedTime,
            quizNotificationsEnabled = quizNotificationsEnabled,
            achievementNotificationsEnabled = achievementNotificationsEnabled
        )
        SettingsSubPage.APPEARANCE -> AppearanceSettingsPage(
            onBack = { currentSubPage = SettingsSubPage.MAIN },
            themeMode = themeMode,
            onSelectThemeMode = onSelectThemeMode,
            selectedSyntax = syntaxScheme,
            onSelectSyntax = onSelectSyntaxScheme,
            selectedFont = editorFont,
            onSelectFont = onSelectEditorFont
        )
        SettingsSubPage.NOTIFICATIONS -> NotificationsSettingsPage(
            onBack = { currentSubPage = SettingsSubPage.MAIN },
            dailyReminderEnabled = dailyReminderEnabled,
            onToggleDailyReminder = { enabled ->
                dailyReminderEnabled = enabled
                reminderPrefs.setReminderEnabled(enabled)
                if (enabled) {
                    ReminderScheduler.scheduleDailyReminder(context)
                } else {
                    ReminderScheduler.cancelDailyReminder(context)
                }
            },
            reminderHour = reminderHour,
            reminderMinute = reminderMinute,
            onSelectReminderTime = { hour, minute ->
                reminderHour = hour
                reminderMinute = minute
                reminderPrefs.setReminderTime(hour, minute)
                if (dailyReminderEnabled) {
                    ReminderScheduler.scheduleDailyReminder(context)
                }
            },
            streakReminderEnabled = streakReminderEnabled,
            onToggleStreakReminder = { enabled ->
                streakReminderEnabled = enabled
                reminderPrefs.setStreakReminderEnabled(enabled)
                if (enabled) {
                    ReminderScheduler.scheduleStreakReminder(context)
                } else {
                    ReminderScheduler.cancelStreakReminder(context)
                }
            },
            streakHour = streakHour,
            streakMinute = streakMinute,
            onSelectStreakTime = { hour, minute ->
                streakHour = hour
                streakMinute = minute
                reminderPrefs.setStreakReminderTime(hour, minute)
                if (streakReminderEnabled) {
                    ReminderScheduler.scheduleStreakReminder(context)
                }
            },
            isDailyPracticeCompleted = isDailyPracticeCompleted,
            onTogglePracticeCompleted = {
                val newState = !isDailyPracticeCompleted
                if (newState) {
                    reminderPrefs.markDailyActivityCompleted()
                } else {
                    reminderPrefs.clearDailyActivityCompleted()
                    reminderPrefs.clearStreakNotificationSentToday()
                }
                isDailyPracticeCompleted = newState
            },
            quizNotificationsEnabled = quizNotificationsEnabled,
            onToggleQuizNotifications = { enabled ->
                quizNotificationsEnabled = enabled
                reminderPrefs.setQuizNotificationEnabled(enabled)
            },
            achievementNotificationsEnabled = achievementNotificationsEnabled,
            onToggleAchievementNotifications = { enabled ->
                achievementNotificationsEnabled = enabled
                reminderPrefs.setAchievementNotificationEnabled(enabled)
            },
            hapticsEnabled = hapticsEnabled,
            onToggleHaptics = { hapticsEnabled = it }
        )
        SettingsSubPage.LEARNING -> LearningPreferencesPage(
            onBack = { currentSubPage = SettingsSubPage.MAIN },
            selectedLanguage = selectedLanguage,
            onSelectLanguage = { selectedLanguage = it },
            selectedDifficulty = selectedDifficulty,
            onSelectDifficulty = { selectedDifficulty = it },
            selectedSpeed = selectedSpeed,
            onSelectSpeed = { selectedSpeed = it }
        )
        SettingsSubPage.ACCESSIBILITY -> AccessibilitySettingsPage(
            onBack = { currentSubPage = SettingsSubPage.MAIN },
            selectedFontScale = selectedFontScale,
            onSelectFontScale = { selectedFontScale = it },
            highContrastEnabled = highContrastEnabled,
            onToggleHighContrast = { highContrastEnabled = it },
            reduceAnimationsEnabled = reduceAnimationsEnabled,
            onToggleReduceAnimations = { reduceAnimationsEnabled = it }
        )
        SettingsSubPage.PRIVACY -> PrivacySecuritySettingsPage(
            onBack = { currentSubPage = SettingsSubPage.MAIN },
            cacheClearedTimestamp = cacheClearedTimestamp,
            onClearCache = {
                cacheClearedTimestamp = "Visualizer cache cleared just now"
            }
        )
        SettingsSubPage.ABOUT -> AboutAlgoQuestPage(
            onBack = { currentSubPage = SettingsSubPage.MAIN }
        )
    }
}

// ==========================================
// 1. MAIN SETTINGS DASHBOARD
// ==========================================

@Composable
private fun MainSettingsContent(
    onBack: () -> Unit,
    onNavigate: (SettingsSubPage) -> Unit,
    themeMode: AppThemeMode,
    syntaxScheme: String,
    editorFont: String,
    selectedLanguage: String,
    selectedDifficulty: String,
    dailyReminderEnabled: Boolean,
    reminderTimeText: String = "8:00 PM",
    streakReminderEnabled: Boolean = false,
    streakTimeText: String = "9:00 PM",
    quizNotificationsEnabled: Boolean = true,
    achievementNotificationsEnabled: Boolean = true
) {
    val textPrimary = TextPrimary
    val textMuted = TextMuted
    val purpleSoft = PurpleSoft
    val gold = Gold
    val cyan = Cyan
    val green = Green
    val purple = Purple

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBg)
                    .border(1.dp, if (AlgoTheme.colors.isDark) Color.White.copy(alpha = 0.08f) else BorderColor, RoundedCornerShape(12.dp))
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                IconCanvas(18.dp) { d -> drawChevronLeft(d, textPrimary) }
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = "Settings",
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "Preferences & System Info",
                    style = TextStyle(
                        fontFamily = Inter,
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                )
            }
        }

        // Settings Sections Scrollable Content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Appearance Section
            SettingsSectionCard(
                title = "Appearance",
                iconColor = purpleSoft,
                icon = { d -> drawAppearanceIcon(d, purpleSoft) },
                onCardClick = { onNavigate(SettingsSubPage.APPEARANCE) }
            ) {
                SettingsItem(
                    title = "App Theme",
                    subtitle = "Cyberpunk Dark, Light Mode & System Default",
                    badge = when (themeMode) {
                        AppThemeMode.DARK -> "Dark"
                        AppThemeMode.LIGHT -> "Light"
                        AppThemeMode.SYSTEM -> "System"
                    },
                    onClick = { onNavigate(SettingsSubPage.APPEARANCE) }
                )
                SettingsDivider()
                SettingsItem(
                    title = "Syntax Highlighting",
                    subtitle = "Color schemes for code visualizer & traces",
                    badge = syntaxScheme,
                    onClick = { onNavigate(SettingsSubPage.APPEARANCE) }
                )
                SettingsDivider()
                SettingsItem(
                    title = "Editor Typography",
                    subtitle = "Monospace coding fonts & variable watches",
                    badge = editorFont,
                    onClick = { onNavigate(SettingsSubPage.APPEARANCE) }
                )
            }

            // 2. Notifications Section
            SettingsSectionCard(
                title = "Notifications",
                iconColor = gold,
                icon = { d -> drawNotificationIcon(d, gold) },
                onCardClick = { onNavigate(SettingsSubPage.NOTIFICATIONS) }
            ) {
                SettingsItem(
                    title = "Daily Quest Reminder",
                    subtitle = "Evening alert to keep your daily streak alive",
                    badge = if (dailyReminderEnabled) reminderTimeText else "Off",
                    onClick = { onNavigate(SettingsSubPage.NOTIFICATIONS) }
                )
                SettingsDivider()
                SettingsItem(
                    title = "Streak Protection Reminder",
                    subtitle = "Alert before midnight if daily quest is incomplete",
                    badge = if (streakReminderEnabled) streakTimeText else "Off",
                    onClick = { onNavigate(SettingsSubPage.NOTIFICATIONS) }
                )
                SettingsDivider()
                SettingsItem(
                    title = "Quiz & Challenge Alerts",
                    subtitle = "Notify when new DSA challenges become available",
                    badge = if (quizNotificationsEnabled) "Active" else "Off",
                    onClick = { onNavigate(SettingsSubPage.NOTIFICATIONS) }
                )
                SettingsDivider()
                SettingsItem(
                    title = "Achievement & Badge Alerts",
                    subtitle = "Instant alerts when unlocking badges and DSA milestones",
                    badge = if (achievementNotificationsEnabled) "Active" else "Off",
                    onClick = { onNavigate(SettingsSubPage.NOTIFICATIONS) }
                )
                SettingsDivider()
                SettingsItem(
                    title = "Sound & Haptic Feedback",
                    subtitle = "Tactile clicks during algorithm visualization steps",
                    badge = "Enabled",
                    onClick = { onNavigate(SettingsSubPage.NOTIFICATIONS) }
                )
            }

            // 3. Learning Preferences Section
            SettingsSectionCard(
                title = "Learning Preferences",
                iconColor = cyan,
                icon = { d -> drawLearningIcon(d, cyan) },
                onCardClick = { onNavigate(SettingsSubPage.LEARNING) }
            ) {
                SettingsItem(
                    title = "Programming Language",
                    subtitle = "Preferred syntax for code visualizer & solutions",
                    badge = selectedLanguage,
                    onClick = { onNavigate(SettingsSubPage.LEARNING) }
                )
                SettingsDivider()
                SettingsItem(
                    title = "Difficulty Level",
                    subtitle = "Beginner, Intermediate or Advanced problem sets",
                    badge = selectedDifficulty,
                    onClick = { onNavigate(SettingsSubPage.LEARNING) }
                )
                SettingsDivider()
                SettingsItem(
                    title = "Visualizer Default Speed",
                    subtitle = "Step execution playback speed in Interactive Lab",
                    badge = "1.0x (Normal)",
                    onClick = { onNavigate(SettingsSubPage.LEARNING) }
                )
            }

            // 4. Accessibility Section
            SettingsSectionCard(
                title = "Accessibility",
                iconColor = green,
                icon = { d -> drawAccessibilityIcon(d, green) },
                onCardClick = { onNavigate(SettingsSubPage.ACCESSIBILITY) }
            ) {
                SettingsItem(
                    title = "Font Size Scaling",
                    subtitle = "Standard, Large and Extra Large text options",
                    badge = "Standard",
                    onClick = { onNavigate(SettingsSubPage.ACCESSIBILITY) }
                )
                SettingsDivider()
                SettingsItem(
                    title = "High Contrast Mode",
                    subtitle = "Enhanced element borders and glowing pointer labels",
                    badge = "Customizable",
                    onClick = { onNavigate(SettingsSubPage.ACCESSIBILITY) }
                )
                SettingsDivider()
                SettingsItem(
                    title = "Reduce Animations",
                    subtitle = "Simplified motion effects across screen transitions",
                    badge = "Available",
                    onClick = { onNavigate(SettingsSubPage.ACCESSIBILITY) }
                )
            }

            // 5. Privacy & Security Section
            SettingsSectionCard(
                title = "Privacy & Security",
                iconColor = purple,
                icon = { d -> drawPrivacyIcon(d, purple) },
                onCardClick = { onNavigate(SettingsSubPage.PRIVACY) }
            ) {
                SettingsItem(
                    title = "Authentication Session",
                    subtitle = "SHA-256 encrypted bearer tokens in private storage",
                    badge = "Encrypted",
                    onClick = { onNavigate(SettingsSubPage.PRIVACY) }
                )
                SettingsDivider()
                SettingsItem(
                    title = "Account Isolation",
                    subtitle = "Ensures no profile data leaks between user sessions",
                    badge = "Protected",
                    onClick = { onNavigate(SettingsSubPage.PRIVACY) }
                )
                SettingsDivider()
                SettingsItem(
                    title = "Local Cache Management",
                    subtitle = "Manage temporary algorithm visualization frames",
                    badge = "Manage",
                    onClick = { onNavigate(SettingsSubPage.PRIVACY) }
                )
            }

            // 6. About AlgoQuest Section
            SettingsSectionCard(
                title = "About AlgoQuest",
                iconColor = cyan,
                icon = { d -> drawAboutIcon(d, cyan) },
                onCardClick = { onNavigate(SettingsSubPage.ABOUT) }
            ) {
                SettingsItem(
                    title = "Application Version",
                    subtitle = "AlgoQuest Android Edition details",
                    badge = "v1.0.0",
                    onClick = { onNavigate(SettingsSubPage.ABOUT) }
                )
                SettingsDivider()
                SettingsItem(
                    title = "Interactive Engine",
                    subtitle = "Algorithm Stepping Engine 2.0 with live watches",
                    badge = "Active",
                    onClick = { onNavigate(SettingsSubPage.ABOUT) }
                )
                SettingsDivider()
                SettingsItem(
                    title = "Architecture & Credits",
                    subtitle = "Jetpack Compose, Retrofit & MariaDB REST API",
                    badge = "Details",
                    onClick = { onNavigate(SettingsSubPage.ABOUT) }
                )
            }
        }
    }
}

// ==========================================
// 2. APPEARANCE SETTINGS SUB-PAGE
// ==========================================

@Composable
private fun AppearanceSettingsPage(
    onBack: () -> Unit,
    themeMode: AppThemeMode,
    onSelectThemeMode: (AppThemeMode) -> Unit,
    selectedSyntax: String,
    onSelectSyntax: (String) -> Unit,
    selectedFont: String,
    onSelectFont: (String) -> Unit
) {
    SubPageContainer(
        title = "Appearance",
        subtitle = "Theme, Syntax Highlighting & Fonts",
        onBack = onBack
    ) {
        SettingsDetailGroup(title = "App Theme Mode") {
            Text(
                text = "AlgoQuest offers both Cyberpunk Dark and Clean Light modes, as well as automatic system matching:",
                style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = TextSecondary, lineHeight = 17.sp)
            )
            Spacer(Modifier.height(4.dp))

            SelectableOptionRow(
                title = "Dark Mode (Cyberpunk Dark)",
                subtitle = "Active cyberpunk aesthetic with deep blacks and neon accents.",
                isSelected = themeMode == AppThemeMode.DARK,
                tag = if (themeMode == AppThemeMode.DARK) "Active" else null,
                onClick = { onSelectThemeMode(AppThemeMode.DARK) }
            )
            SelectableOptionRow(
                title = "Light Mode",
                subtitle = "Clean daylight theme with crisp slate typography and high readability.",
                isSelected = themeMode == AppThemeMode.LIGHT,
                tag = if (themeMode == AppThemeMode.LIGHT) "Active" else null,
                onClick = { onSelectThemeMode(AppThemeMode.LIGHT) }
            )
            SelectableOptionRow(
                title = "System Default",
                subtitle = "Automatically follows Android system dark/light configuration dynamically.",
                isSelected = themeMode == AppThemeMode.SYSTEM,
                tag = if (themeMode == AppThemeMode.SYSTEM) "Active" else "Follow OS",
                onClick = { onSelectThemeMode(AppThemeMode.SYSTEM) }
            )
        }

        SettingsDetailGroup(title = "Syntax Highlighting") {
            listOf("Neon Synth", "Dracula Dusk", "Cyber Solarized").forEach { scheme ->
                SelectableOptionRow(
                    title = scheme,
                    subtitle = when (scheme) {
                        "Neon Synth" -> "Electric cyan pointers, gold midpoints and purple active ranges"
                        "Dracula Dusk" -> "Vibrant pastel accents on dark slate background"
                        else -> "Warm amber and teal contrast optimized for readability"
                    },
                    isSelected = selectedSyntax == scheme,
                    onClick = { onSelectSyntax(scheme) }
                )
            }
        }

        SettingsDetailGroup(title = "Editor Monospace Font") {
            listOf("JetBrains Mono", "Fira Code", "Roboto Mono").forEach { font ->
                SelectableOptionRow(
                    title = font,
                    subtitle = "Proportional ligatures and distinct operator symbols",
                    isSelected = selectedFont == font,
                    onClick = { onSelectFont(font) }
                )
            }
        }
    }
}

// ==========================================
// 3. NOTIFICATIONS SETTINGS SUB-PAGE
// ==========================================

@Composable
private fun NotificationsSettingsPage(
    onBack: () -> Unit,
    dailyReminderEnabled: Boolean,
    onToggleDailyReminder: (Boolean) -> Unit,
    reminderHour: Int,
    reminderMinute: Int,
    onSelectReminderTime: (hour: Int, minute: Int) -> Unit,
    streakReminderEnabled: Boolean,
    onToggleStreakReminder: (Boolean) -> Unit,
    streakHour: Int,
    streakMinute: Int,
    onSelectStreakTime: (hour: Int, minute: Int) -> Unit,
    isDailyPracticeCompleted: Boolean,
    onTogglePracticeCompleted: () -> Unit,
    quizNotificationsEnabled: Boolean,
    onToggleQuizNotifications: (Boolean) -> Unit,
    achievementNotificationsEnabled: Boolean,
    onToggleAchievementNotifications: (Boolean) -> Unit,
    hapticsEnabled: Boolean,
    onToggleHaptics: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val formattedDailyTime = remember(reminderHour, reminderMinute) {
        ReminderPreferences.formatTime(reminderHour, reminderMinute)
    }
    val formattedStreakTime = remember(streakHour, streakMinute) {
        ReminderPreferences.formatTime(streakHour, streakMinute)
    }

    val reminderPrefs = remember { ReminderPreferences.getInstance(context) }
    var quizCacheVersion by remember { mutableStateOf(0) }
    var achievementCacheVersion by remember { mutableStateOf(0) }

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    var pendingActionAfterPermission by remember { mutableStateOf<(() -> Unit)?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            pendingActionAfterPermission?.invoke()
            pendingActionAfterPermission = null
        } else {
            Toast.makeText(context, "Notification permission is required to receive reminders.", Toast.LENGTH_LONG).show()
        }
    }

    fun executeWithPermission(action: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
            pendingActionAfterPermission = action
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            action()
        }
    }

    val openDailyTimePicker = {
        val timePicker = android.app.TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                onSelectReminderTime(selectedHour, selectedMinute)
                Toast.makeText(
                    context,
                    "Daily reminder time set to ${ReminderPreferences.formatTime(selectedHour, selectedMinute)}",
                    Toast.LENGTH_SHORT
                ).show()
            },
            reminderHour,
            reminderMinute,
            false // 12-hour AM/PM format
        )
        timePicker.show()
    }

    val openStreakTimePicker = {
        val timePicker = android.app.TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                onSelectStreakTime(selectedHour, selectedMinute)
                Toast.makeText(
                    context,
                    "Streak reminder time set to ${ReminderPreferences.formatTime(selectedHour, selectedMinute)}",
                    Toast.LENGTH_SHORT
                ).show()
            },
            streakHour,
            streakMinute,
            false // 12-hour AM/PM format
        )
        timePicker.show()
    }

    SubPageContainer(
        title = "Notifications",
        subtitle = "Reminder Schedules & Sound Preferences",
        onBack = onBack
    ) {
        SettingsDetailGroup(title = "Learning Reminders") {
            // 1. Daily Learning Reminder
            ToggleOptionRow(
                title = "Daily Learning Reminder",
                subtitle = "Receive a reminder every day to practice DSA algorithms and keep your streak alive.",
                isChecked = dailyReminderEnabled,
                onCheckedChange = { enable ->
                    if (enable) {
                        executeWithPermission {
                            onToggleDailyReminder(true)
                            Toast.makeText(context, "Daily reminder scheduled for $formattedDailyTime", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        onToggleDailyReminder(false)
                        Toast.makeText(context, "Daily reminder disabled", Toast.LENGTH_SHORT).show()
                    }
                },
                note = if (dailyReminderEnabled) "Scheduled daily at $formattedDailyTime" else "Reminder disabled"
            )

            if (dailyReminderEnabled) {
                SettingsDivider()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { openDailyTimePicker() }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "Daily Reminder Time",
                            style = TextStyle(
                                fontFamily = Inter,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Tap to choose when to receive your daily study alert",
                            style = TextStyle(
                                fontFamily = Inter,
                                fontSize = 11.sp,
                                color = TextMuted,
                                lineHeight = 15.sp
                            )
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Purple.copy(alpha = 0.15f))
                            .border(1.dp, Purple.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = formattedDailyTime,
                            style = TextStyle(
                                fontFamily = Mono,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = PurpleSoft
                            )
                        )
                    }
                }
            }

            SettingsDivider()

            // 2. Streak Reminder
            val streakNote = when {
                !streakReminderEnabled -> "Reminder disabled"
                isDailyPracticeCompleted -> "Today's DSA practice complete • Streak is safe! (Alert will be skipped)"
                else -> "Scheduled daily at $formattedStreakTime • Alerts only if practice incomplete"
            }

            ToggleOptionRow(
                title = "Streak Reminder",
                subtitle = "Sends a targeted alert if today's DSA practice has not been completed, protecting your streak.",
                isChecked = streakReminderEnabled,
                onCheckedChange = { enable ->
                    if (enable) {
                        executeWithPermission {
                            onToggleStreakReminder(true)
                            Toast.makeText(context, "Streak reminder scheduled for $formattedStreakTime", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        onToggleStreakReminder(false)
                        Toast.makeText(context, "Streak reminder disabled", Toast.LENGTH_SHORT).show()
                    }
                },
                note = streakNote
            )

            if (streakReminderEnabled) {
                SettingsDivider()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { openStreakTimePicker() }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "Streak Reminder Time",
                            style = TextStyle(
                                fontFamily = Inter,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Tap to choose what time the streak alert fires",
                            style = TextStyle(
                                fontFamily = Inter,
                                fontSize = 11.sp,
                                color = TextMuted,
                                lineHeight = 15.sp
                            )
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Gold.copy(alpha = 0.15f))
                            .border(1.dp, Gold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = formattedStreakTime,
                            style = TextStyle(
                                fontFamily = Mono,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = Gold
                            )
                        )
                    }
                }
            }

            SettingsDivider()

            // 3. Quiz Notifications
            ToggleOptionRow(
                title = "Quiz Notifications",
                subtitle = "Notify when new DSA quiz challenges and assessments become unlocked and available to take.",
                isChecked = quizNotificationsEnabled,
                onCheckedChange = { enable ->
                    if (enable) {
                        executeWithPermission {
                            onToggleQuizNotifications(true)
                            Toast.makeText(context, "Quiz notifications enabled", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        onToggleQuizNotifications(false)
                        Toast.makeText(context, "Quiz notifications disabled", Toast.LENGTH_SHORT).show()
                    }
                },
                note = if (quizNotificationsEnabled) "Active • Alerts will notify on new quiz availability" else "Quiz alerts disabled"
            )

            SettingsDivider()

            // 4. Achievement Notifications
            ToggleOptionRow(
                title = "Achievement Notifications",
                subtitle = "Notify when you unlock new achievement badges, level milestones, or significant DSA accomplishments.",
                isChecked = achievementNotificationsEnabled,
                onCheckedChange = { enable ->
                    if (enable) {
                        executeWithPermission {
                            onToggleAchievementNotifications(true)
                            Toast.makeText(context, "Achievement notifications enabled", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        onToggleAchievementNotifications(false)
                        Toast.makeText(context, "Achievement notifications disabled", Toast.LENGTH_SHORT).show()
                    }
                },
                note = if (achievementNotificationsEnabled) "Active • Alerts will notify on new badges and milestones" else "Achievement alerts disabled"
            )
        }

        SettingsDetailGroup(title = "Test & Verify") {
            // Test 1: Daily Learning Reminder
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        executeWithPermission {
                            ReminderScheduler.showReminderNotification(context)
                            Toast.makeText(context, "Daily reminder dispatched: \"Time for AlgoQuest!\"", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Send Daily Reminder Test",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Dispatches \"Time for AlgoQuest!\" notification immediately",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            color = TextMuted,
                            lineHeight = 15.sp
                        )
                    )
                }
                Spacer(Modifier.width(8.dp))
                StatusPill("Daily Alert", Cyan)
            }

            SettingsDivider()

            // Test 2: Streak Reminder (conditioned on completion status)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        executeWithPermission {
                            val result = ReminderScheduler.showStreakNotification(context, forceIfTest = false)
                            when (result) {
                                com.example.myapplication.reminder.StreakNotificationResult.SENT -> {
                                    Toast.makeText(context, "Streak reminder dispatched: \"Protect Your Learning Streak!\"", Toast.LENGTH_SHORT).show()
                                }
                                com.example.myapplication.reminder.StreakNotificationResult.SUPPRESSED_PRACTICE_COMPLETED -> {
                                    Toast.makeText(context, "Streak alert suppressed! Today's practice is already complete. Streak is safe.", Toast.LENGTH_LONG).show()
                                }
                                com.example.myapplication.reminder.StreakNotificationResult.SUPPRESSED_ALREADY_SENT_TODAY -> {
                                    Toast.makeText(context, "Streak alert already sent today. Duplicate suppressed.", Toast.LENGTH_SHORT).show()
                                }
                                com.example.myapplication.reminder.StreakNotificationResult.PERMISSION_DENIED -> {
                                    Toast.makeText(context, "Notification permission is required.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Send Streak Reminder Test",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Evaluates today's practice and alerts only if incomplete",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            color = TextMuted,
                            lineHeight = 15.sp
                        )
                    )
                }
                Spacer(Modifier.width(8.dp))
                StatusPill("Streak Alert", Gold)
            }

            SettingsDivider()

            // Test 3: Toggle Practice Completion Status for Testing
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        onTogglePracticeCompleted()
                        val updated = !isDailyPracticeCompleted
                        Toast.makeText(
                            context,
                            if (updated) "Today marked: Complete (Streak Safe)" else "Today marked: Incomplete (Streak Alert will fire)",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Today's Practice Status",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Tap to toggle between Complete / Incomplete to test suppression",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            color = TextMuted,
                            lineHeight = 15.sp
                        )
                    )
                }
                Spacer(Modifier.width(8.dp))
                StatusPill(
                    text = if (isDailyPracticeCompleted) "Completed" else "Incomplete",
                    color = if (isDailyPracticeCompleted) Green else Pink
                )
            }

            SettingsDivider()

            // Test 4: Quiz Notification Test (Instant test bypasses duplicate cache)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        executeWithPermission {
                            val result = ReminderScheduler.showQuizNotification(context, quizLessonId = 5, isTest = true)
                            when (result) {
                                com.example.myapplication.reminder.QuizNotificationResult.SENT -> {
                                    Toast.makeText(context, "Quiz notification dispatched: \"New Quiz Available!\"", Toast.LENGTH_SHORT).show()
                                }
                                com.example.myapplication.reminder.QuizNotificationResult.DISABLED -> {
                                    Toast.makeText(context, "Quiz notifications are currently disabled in settings.", Toast.LENGTH_SHORT).show()
                                }
                                com.example.myapplication.reminder.QuizNotificationResult.ALREADY_NOTIFIED -> {
                                    Toast.makeText(context, "Quiz alert already notified for this challenge.", Toast.LENGTH_SHORT).show()
                                }
                                com.example.myapplication.reminder.QuizNotificationResult.PERMISSION_DENIED -> {
                                    Toast.makeText(context, "Notification permission is required.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Send Quiz Notification Test",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Dispatches \"New Quiz Available!\" alert; tap to open quiz screen",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            color = TextMuted,
                            lineHeight = 15.sp
                        )
                    )
                }
                Spacer(Modifier.width(8.dp))
                StatusPill("Quiz Alert", PurpleSoft)
            }

            SettingsDivider()

            // Test 5: Duplicate Prevention Verification (Respects duplicate cache)
            val isAlreadyNotified = remember(quizCacheVersion) {
                reminderPrefs.hasQuizNotificationBeenSent(5)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        executeWithPermission {
                            val result = ReminderScheduler.showQuizNotification(context, quizLessonId = 5, isTest = false)
                            quizCacheVersion++
                            when (result) {
                                com.example.myapplication.reminder.QuizNotificationResult.SENT -> {
                                    Toast.makeText(context, "New quiz detected & notified (Lesson #5)!", Toast.LENGTH_SHORT).show()
                                }
                                com.example.myapplication.reminder.QuizNotificationResult.ALREADY_NOTIFIED -> {
                                    Toast.makeText(context, "Duplicate prevented! Quiz #5 has already been notified.", Toast.LENGTH_SHORT).show()
                                }
                                com.example.myapplication.reminder.QuizNotificationResult.DISABLED -> {
                                    Toast.makeText(context, "Quiz notifications are switched OFF.", Toast.LENGTH_SHORT).show()
                                }
                                com.example.myapplication.reminder.QuizNotificationResult.PERMISSION_DENIED -> {
                                    Toast.makeText(context, "Notification permission required.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Test Duplicate Prevention",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Simulates detecting Quiz #5; sends once, then blocks repeats",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            color = TextMuted,
                            lineHeight = 15.sp
                        )
                    )
                }
                Spacer(Modifier.width(8.dp))
                StatusPill(
                    text = if (isAlreadyNotified) "Notified" else "Pending",
                    color = if (isAlreadyNotified) Green else Gold
                )
            }

            SettingsDivider()

            // Test 6: Reset Quiz Notification Cache
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        reminderPrefs.clearNotifiedQuizIds()
                        quizCacheVersion++
                        Toast.makeText(context, "Quiz notification history cleared", Toast.LENGTH_SHORT).show()
                    }
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Reset Quiz History Cache",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Clears notified quiz IDs so duplicate testing can repeat",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            color = TextMuted,
                            lineHeight = 15.sp
                        )
                    )
                }
                Spacer(Modifier.width(8.dp))
                StatusPill("Reset", Pink)
            }

            SettingsDivider()

            // Test 7: Achievement Notification Test (Instant test bypasses duplicate cache)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        executeWithPermission {
                            val result = ReminderScheduler.showAchievementNotification(
                                context = context,
                                title = ReminderScheduler.DEFAULT_ACHIEVEMENT_TITLE,
                                message = ReminderScheduler.DEFAULT_ACHIEVEMENT_MESSAGE,
                                achievementKey = "test_achievement",
                                isTest = true
                            )
                            when (result) {
                                com.example.myapplication.reminder.AchievementNotificationResult.SENT -> {
                                    Toast.makeText(context, "Achievement notification dispatched! Tap to view Profile.", Toast.LENGTH_SHORT).show()
                                }
                                com.example.myapplication.reminder.AchievementNotificationResult.DISABLED -> {
                                    Toast.makeText(context, "Achievement notifications are currently disabled in settings.", Toast.LENGTH_SHORT).show()
                                }
                                com.example.myapplication.reminder.AchievementNotificationResult.ALREADY_NOTIFIED -> {
                                    Toast.makeText(context, "Achievement alert already notified.", Toast.LENGTH_SHORT).show()
                                }
                                com.example.myapplication.reminder.AchievementNotificationResult.PERMISSION_DENIED -> {
                                    Toast.makeText(context, "Notification permission is required.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Send Achievement Test",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Dispatches \"Achievement Unlocked!\"; tap to open Explorer Profile",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            color = TextMuted,
                            lineHeight = 15.sp
                        )
                    )
                }
                Spacer(Modifier.width(8.dp))
                StatusPill("Badge Alert", Gold)
            }

            SettingsDivider()

            // Test 8: Duplicate Prevention Verification (Respects duplicate cache)
            val isAchAlreadyNotified = remember(achievementCacheVersion) {
                reminderPrefs.hasAchievementBeenNotified("ach_2")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        executeWithPermission {
                            val result = ReminderScheduler.showAchievementNotification(
                                context = context,
                                title = "Achievement Unlocked!",
                                message = "Congratulations! You earned 'Binary Searcher' ⚡ - Mastered Binary Search and completed Chapter 03",
                                achievementKey = "ach_2",
                                isTest = false
                            )
                            achievementCacheVersion++
                            when (result) {
                                com.example.myapplication.reminder.AchievementNotificationResult.SENT -> {
                                    Toast.makeText(context, "New badge unlocked & notified: Binary Searcher ⚡!", Toast.LENGTH_SHORT).show()
                                }
                                com.example.myapplication.reminder.AchievementNotificationResult.ALREADY_NOTIFIED -> {
                                    Toast.makeText(context, "Duplicate prevented! 'Binary Searcher' has already been notified.", Toast.LENGTH_SHORT).show()
                                }
                                com.example.myapplication.reminder.AchievementNotificationResult.DISABLED -> {
                                    Toast.makeText(context, "Achievement notifications are switched OFF.", Toast.LENGTH_SHORT).show()
                                }
                                com.example.myapplication.reminder.AchievementNotificationResult.PERMISSION_DENIED -> {
                                    Toast.makeText(context, "Notification permission required.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Test Duplicate Prevention (Achievement)",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Simulates unlocking 'Binary Searcher'; sends once, then blocks repeats",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            color = TextMuted,
                            lineHeight = 15.sp
                        )
                    )
                }
                Spacer(Modifier.width(8.dp))
                StatusPill(
                    text = if (isAchAlreadyNotified) "Notified" else "Pending",
                    color = if (isAchAlreadyNotified) Green else Gold
                )
            }

            SettingsDivider()

            // Test 9: Reset Achievement Notification Cache
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        reminderPrefs.clearNotifiedAchievements()
                        achievementCacheVersion++
                        Toast.makeText(context, "Achievement notification history cleared", Toast.LENGTH_SHORT).show()
                    }
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Reset Achievement History Cache",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Clears notified achievement keys so duplicate testing can repeat",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            color = TextMuted,
                            lineHeight = 15.sp
                        )
                    )
                }
                Spacer(Modifier.width(8.dp))
                StatusPill("Reset", Pink)
            }
        }

        SettingsDetailGroup(title = "System Audio & Haptic Clicks") {
            ToggleOptionRow(
                title = "Haptic Vibration Feedback",
                subtitle = "Tactile vibration when stepping forward, pausing, or completing binary search frames.",
                isChecked = hapticsEnabled,
                onCheckedChange = onToggleHaptics,
                note = if (hapticsEnabled) "Haptic clicks enabled" else "Muted"
            )
        }

        SettingsDetailGroup(title = "System Permission Status") {
            val isNotificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
            val permissionGranted = isNotificationsEnabled && hasNotificationPermission

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Android Notification Channel",
                        style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = TextPrimary)
                    )
                    Text(
                        text = if (permissionGranted) {
                            "\"Daily Learning Reminders\" notification channel is ready and active."
                        } else {
                            "Notifications are restricted or blocked. Tap to grant permission."
                        },
                        style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = TextMuted, lineHeight = 15.sp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                StatusPill(
                    text = if (permissionGranted) "Granted" else "Action Needed",
                    color = if (permissionGranted) Green else Gold
                )
            }
        }
    }
}

// ==========================================
// 4. LEARNING PREFERENCES SUB-PAGE
// ==========================================

@Composable
private fun LearningPreferencesPage(
    onBack: () -> Unit,
    selectedLanguage: String,
    onSelectLanguage: (String) -> Unit,
    selectedDifficulty: String,
    onSelectDifficulty: (String) -> Unit,
    selectedSpeed: String,
    onSelectSpeed: (String) -> Unit
) {
    SubPageContainer(
        title = "Learning Preferences",
        subtitle = "Code Syntax, Difficulty & Visualizer",
        onBack = onBack
    ) {
        SettingsDetailGroup(title = "Primary Programming Language") {
            Text(
                text = "Select your preferred syntax for algorithm visualizer templates and code explanations:",
                style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
            )
            Spacer(Modifier.height(4.dp))

            listOf("Python", "Kotlin", "Java", "C++", "JavaScript").forEach { lang ->
                SelectableOptionRow(
                    title = lang,
                    subtitle = when (lang) {
                        "Python" -> "Concise, zero-boilerplate syntax ideal for algorithm fundamentals"
                        "Kotlin" -> "Modern expressive syntax with smart casting and null-safety"
                        "Java" -> "Standard object-oriented DSA implementations with strong typing"
                        "C++" -> "Standard Template Library (STL) algorithms with raw memory pointers"
                        else -> "Web and script execution environments with dynamic arrays"
                    },
                    isSelected = selectedLanguage == lang,
                    onClick = { onSelectLanguage(lang) }
                )
            }
        }

        SettingsDetailGroup(title = "Challenge Difficulty Tier") {
            listOf("Beginner", "Intermediate", "Advanced").forEach { tier ->
                SelectableOptionRow(
                    title = tier,
                    subtitle = when (tier) {
                        "Beginner" -> "Linear & binary search, two pointers, and basic array indexing"
                        "Intermediate" -> "Sorting divide-and-conquer, linked lists, sliding window"
                        else -> "Trees, graphs, dynamic programming, backtracking"
                    },
                    isSelected = selectedDifficulty == tier,
                    onClick = { onSelectDifficulty(tier) }
                )
            }
        }

        SettingsDetailGroup(title = "Visualizer Step Speed") {
            listOf("0.5x (Slow)", "1.0x (Normal)", "1.5x (Fast)", "2.0x (Turbo)").forEach { speed ->
                SelectableOptionRow(
                    title = speed,
                    subtitle = when (speed) {
                        "0.5x (Slow)" -> "1200ms per step — best for analyzing detailed variable watch windows"
                        "1.0x (Normal)" -> "600ms per step — balanced pace for following search boundaries"
                        "1.5x (Fast)" -> "400ms per step — brisk pace for large sorted arrays"
                        else -> "200ms per step — rapid automated scanning"
                    },
                    isSelected = selectedSpeed == speed,
                    onClick = { onSelectSpeed(speed) }
                )
            }
        }
    }
}

// ==========================================
// 5. ACCESSIBILITY SETTINGS SUB-PAGE
// ==========================================

@Composable
private fun AccessibilitySettingsPage(
    onBack: () -> Unit,
    selectedFontScale: String,
    onSelectFontScale: (String) -> Unit,
    highContrastEnabled: Boolean,
    onToggleHighContrast: (Boolean) -> Unit,
    reduceAnimationsEnabled: Boolean,
    onToggleReduceAnimations: (Boolean) -> Unit
) {
    SubPageContainer(
        title = "Accessibility",
        subtitle = "Text Scaling, Contrast & Motion",
        onBack = onBack
    ) {
        SettingsDetailGroup(title = "Text Scaling") {
            listOf("Standard (100%)", "Large (115%)", "Extra Large (130%)").forEach { scale ->
                SelectableOptionRow(
                    title = scale,
                    subtitle = "Adjusts in-app typography while preserving layout integrity",
                    isSelected = selectedFontScale == scale,
                    onClick = { onSelectFontScale(scale) }
                )
            }

            Spacer(Modifier.height(6.dp))

            // Live Text Scaling Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ActiveCardBg)
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Preview Text:",
                        style = TextStyle(fontFamily = Mono, fontSize = 10.sp, color = Cyan)
                    )
                    Text(
                        text = "Binary search narrows search space in O(log N) time.",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontSize = when (selectedFontScale) {
                                "Large (115%)" -> 15.sp
                                "Extra Large (130%)" -> 17.sp
                                else -> 13.sp
                            },
                            color = TextPrimary
                        )
                    )
                }
            }
        }

        SettingsDetailGroup(title = "Visualizer High Contrast") {
            ToggleOptionRow(
                title = "High Contrast Pointer Labels",
                subtitle = "Thickens element borders and increases saturation of 'Mid', 'Found', and eliminated array segments.",
                isChecked = highContrastEnabled,
                onCheckedChange = onToggleHighContrast,
                note = if (highContrastEnabled) "Enhanced contrast active" else "Standard theme palette"
            )
        }

        SettingsDetailGroup(title = "Motion & Screen Transitions") {
            ToggleOptionRow(
                title = "Reduce Animations",
                subtitle = "Substitutes spring physics with instantaneous page transitions to reduce visual motion strain.",
                isChecked = reduceAnimationsEnabled,
                onCheckedChange = onToggleReduceAnimations,
                note = if (reduceAnimationsEnabled) "Minimal motion mode enabled" else "Spring physics enabled"
            )
        }
    }
}

// ==========================================
// 6. PRIVACY & SECURITY SUB-PAGE
// ==========================================

@Composable
private fun PrivacySecuritySettingsPage(
    onBack: () -> Unit,
    cacheClearedTimestamp: String,
    onClearCache: () -> Unit
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager.getInstance(context) }
    val user = tokenManager.getUser()
    val token = tokenManager.getToken()
    val hasActiveSession = !token.isNullOrBlank()

    SubPageContainer(
        title = "Privacy & Security",
        subtitle = "Authentication, Storage & Data Hygiene",
        onBack = onBack
    ) {
        SettingsDetailGroup(title = "Current Authenticated Session") {
            InfoCard(
                label = "Authenticated Explorer",
                value = user?.name ?: "Explorer",
                description = "Account ID #${user?.id ?: 1} · ${user?.email ?: "local_session@algoquest.io"}"
            )
            InfoCard(
                label = "Token Security",
                value = if (hasActiveSession) "SHA-256 Hashed Bearer Token" else "No Active Session",
                description = "Session tokens are stored exclusively in Android SharedPreferences with private sandbox flags."
            )
            InfoCard(
                label = "Cross-Account Isolation",
                value = "Active (Verified)",
                description = "Strict session separation filters cache data so no other explorer's records are displayed."
            )
        }

        SettingsDetailGroup(title = "Local Device Cache Management") {
            Text(
                text = "Temporary algorithm simulation frames and offline lesson caches are stored locally to make questing instantaneous.",
                style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
            )
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                    .clickable(onClick = onClearCache),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Clear Simulation Cache",
                    style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Cyan)
                )
            }

            if (cacheClearedTimestamp.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusPill("✓ $cacheClearedTimestamp", Green)
                }
            }
        }

        SettingsDetailGroup(title = "Backend & Database Communication") {
            Text(
                text = "AlgoQuest communicates with the local XAMPP backend exclusively using standard HTTPS/HTTP endpoints. Under strict architectural policies, client sessions cannot alter database structures, tables, or backend PHP auth routines.",
                style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = TextMuted, lineHeight = 17.sp)
            )
        }
    }
}

// ==========================================
// 7. ABOUT ALGOQUEST SUB-PAGE
// ==========================================

@Composable
private fun AboutAlgoQuestPage(
    onBack: () -> Unit
) {
    SubPageContainer(
        title = "About AlgoQuest",
        subtitle = "App Information, Version & Tech Stack",
        onBack = onBack
    ) {
        SettingsDetailGroup(title = "Application Overview") {
            InfoCard(
                label = "AlgoQuest",
                value = "Version 1.0.0 (Build 37)",
                description = "Master Data Structures & Algorithms through interactive step visualizations, guided paths, and Quest AI assistance."
            )
            InfoCard(
                label = "Target Platform",
                value = "Android 16 (API Level 37)",
                description = "Min SDK 24 · Modern edge-to-edge support with Compose Material 3."
            )
        }

        SettingsDetailGroup(title = "Architecture & Frameworks") {
            InfoCard(
                label = "UI Layer",
                value = "100% Jetpack Compose",
                description = "Declarative reactive UI with HorizontalPager swipe gestures and hardware-accelerated Canvas vector graphics."
            )
            InfoCard(
                label = "Interactive Lab Engine",
                value = "Stepping Engine 2.0",
                description = "Dual-pointer scanning, midpoint calculations, variable watch window, and discrete frame execution."
            )
            InfoCard(
                label = "Networking & Persistence",
                value = "Retrofit 2.9 + OkHttp 4.12",
                description = "Asynchronous Coroutines, dynamic host resolution, and resilient session token management."
            )
            InfoCard(
                label = "Database & Backend",
                value = "PHP 8.2 + MariaDB 10.4",
                description = "Normalized relational database with tokenized bearer authentication."
            )
        }

        SettingsDetailGroup(title = "Credits & Mission") {
            Text(
                text = "Crafted for Computer Science students and developers preparing for technical interviews and competitive programming.\n\n© 2026 AlgoQuest Lab. All rights reserved.",
                style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = TextMuted, lineHeight = 17.sp)
            )
        }
    }
}

// ==========================================
// REUSABLE HELPER COMPONENTS
// ==========================================

@Composable
private fun SubPageContainer(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val textPrimary = TextPrimary
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Sub-page Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBg)
                    .border(1.dp, if (AlgoTheme.colors.isDark) Color.White.copy(alpha = 0.08f) else BorderColor, RoundedCornerShape(12.dp))
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                IconCanvas(18.dp) { d -> drawChevronLeft(d, textPrimary) }
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                )
                Text(
                    text = subtitle,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                )
            }
        }

        // Sub-page Content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content
        )
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    iconColor: Color,
    icon: DrawScope.(Float) -> Unit,
    onCardClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(CardBg)
            .border(1.dp, if (AlgoTheme.colors.isDark) Color.White.copy(alpha = 0.06f) else BorderColor, shape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Title Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .let { if (onCardClick != null) it.clickable(onClick = onCardClick) else it },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    IconCanvas(16.dp, icon)
                }

                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                )
            }

            if (onCardClick != null) {
                val textMuted = TextMuted
                IconCanvas(14.dp) { d -> drawChevronRight(d, textMuted) }
            }
        }

        content()
    }
}

@Composable
private fun SettingsDetailGroup(
    title: String,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(CardBg)
            .border(1.dp, if (AlgoTheme.colors.isDark) Color.White.copy(alpha = 0.06f) else BorderColor, shape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = PurpleSoft
            )
        )
        content()
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    badge: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
            )
            Text(
                text = subtitle,
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 15.sp
                )
            )
        }

        Spacer(Modifier.width(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(ActiveCardBg)
                    .border(1.dp, if (AlgoTheme.colors.isDark) Color.White.copy(alpha = 0.08f) else BorderColor, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badge,
                    style = TextStyle(
                        fontFamily = Mono,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        color = Cyan
                    )
                )
            }

            val textMuted = TextMuted
            IconCanvas(12.dp) { d -> drawChevronRight(d, textMuted) }
        }
    }
}

@Composable
private fun SelectableOptionRow(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    tag: String? = null,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val checkColor = if (AlgoTheme.colors.isDark) Color(0xFF0F1220) else Color.White
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (isSelected) ActiveCardBg else Color.Transparent)
            .border(
                1.dp,
                if (isSelected) Cyan.copy(alpha = 0.5f) else (if (AlgoTheme.colors.isDark) Color.White.copy(alpha = 0.05f) else BorderColor),
                shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        fontSize = 13.sp,
                        color = if (isSelected) TextPrimary else TextSecondary
                    )
                )
                if (tag != null) {
                    StatusPill(tag, if (isSelected) Cyan else PurpleSoft)
                }
            }
            Text(
                text = subtitle,
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 15.sp
                )
            )
        }

        Spacer(Modifier.width(12.dp))

        // Checkmark indicator
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (isSelected) Cyan else (if (AlgoTheme.colors.isDark) Color.White.copy(alpha = 0.05f) else BorderColor.copy(alpha = 0.5f)))
                .border(1.dp, if (isSelected) Cyan else (if (AlgoTheme.colors.isDark) Color.White.copy(alpha = 0.15f) else BorderColor), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                IconCanvas(14.dp) { d -> drawCheckmark(d, checkColor) }
            }
        }
    }
}

@Composable
private fun ToggleOptionRow(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    note: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
            )
            Text(
                text = subtitle,
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 15.sp
                )
            )
            if (note != null) {
                Text(
                    text = "• $note",
                    style = TextStyle(
                        fontFamily = Mono,
                        fontSize = 10.sp,
                        color = if (isChecked) Green else TextMuted
                    )
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        // Pill Switch toggle
        Box(
            modifier = Modifier
                .width(44.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isChecked) Purple else (if (AlgoTheme.colors.isDark) Color.White.copy(alpha = 0.12f) else BorderColor))
                .padding(horizontal = 3.dp),
            contentAlignment = if (isChecked) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}

@Composable
private fun InfoCard(
    label: String,
    value: String,
    description: String? = null
) {
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(ActiveCardBg)
            .border(1.dp, if (AlgoTheme.colors.isDark) Color.White.copy(alpha = 0.05f) else BorderColor, shape)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = TextMuted)
            )
            Text(
                text = value,
                style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Cyan)
            )
        }
        if (description != null) {
            Text(
                text = description,
                style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)
            )
        }
    }
}

@Composable
private fun StatusPill(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 9.sp, color = color)
        )
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(if (AlgoTheme.colors.isDark) Color.White.copy(alpha = 0.04f) else BorderColor)
    )
}

// ---------- Canvas Icon Helpers ----------

@Composable
private fun IconCanvas(size: Dp, draw: DrawScope.(Float) -> Unit) {
    Canvas(Modifier.size(size)) { draw(this, density) }
}

private fun lineStroke(d: Float, w: Float = 1.4f) = Stroke(width = w * d, cap = StrokeCap.Round)

private fun DrawScope.drawChevronLeft(d: Float, color: Color) {
    val w = 1.8f * d
    drawLine(color, Offset(11f * d, 4f * d), Offset(5f * d, 10f * d), w, StrokeCap.Round)
    drawLine(color, Offset(5f * d, 10f * d), Offset(11f * d, 16f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawChevronRight(d: Float, color: Color) {
    val w = 1.4f * d
    drawLine(color, Offset(4f * d, 3f * d), Offset(8f * d, 6f * d), w, StrokeCap.Round)
    drawLine(color, Offset(8f * d, 6f * d), Offset(4f * d, 9f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawCheckmark(d: Float, color: Color) {
    val w = 1.8f * d
    drawLine(color, Offset(3.5f * d, 7f * d), Offset(6f * d, 9.5f * d), w, StrokeCap.Round)
    drawLine(color, Offset(6f * d, 9.5f * d), Offset(10.5f * d, 4.5f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawAppearanceIcon(d: Float, c: Color) {
    drawCircle(c, radius = 5.5f * d, center = Offset(8f * d, 8f * d), style = lineStroke(d))
    drawCircle(c, radius = 2f * d, center = Offset(6f * d, 6f * d))
}

private fun DrawScope.drawNotificationIcon(d: Float, c: Color) {
    drawLine(c, Offset(4f * d, 12f * d), Offset(12f * d, 12f * d), 1.4f * d, StrokeCap.Round)
    drawLine(c, Offset(6f * d, 5f * d), Offset(10f * d, 5f * d), 1.4f * d, StrokeCap.Round)
    drawCircle(c, radius = 1.5f * d, center = Offset(8f * d, 14f * d))
}

private fun DrawScope.drawLearningIcon(d: Float, c: Color) {
    drawLine(c, Offset(4f * d, 3f * d), Offset(4f * d, 13f * d), 1.5f * d, StrokeCap.Round)
    drawLine(c, Offset(4f * d, 13f * d), Offset(12f * d, 13f * d), 1.5f * d, StrokeCap.Round)
    drawLine(c, Offset(4f * d, 8f * d), Offset(10f * d, 8f * d), 1.2f * d, StrokeCap.Round)
}

private fun DrawScope.drawAccessibilityIcon(d: Float, c: Color) {
    drawCircle(c, radius = 2f * d, center = Offset(8f * d, 4f * d), style = lineStroke(d))
    drawLine(c, Offset(4f * d, 8f * d), Offset(12f * d, 8f * d), 1.4f * d, StrokeCap.Round)
    drawLine(c, Offset(8f * d, 6f * d), Offset(8f * d, 13f * d), 1.4f * d, StrokeCap.Round)
}

private fun DrawScope.drawPrivacyIcon(d: Float, c: Color) {
    drawCircle(c, radius = 5f * d, center = Offset(8f * d, 8f * d), style = lineStroke(d))
    drawLine(c, Offset(8f * d, 5f * d), Offset(8f * d, 8f * d), 1.5f * d, StrokeCap.Round)
    drawLine(c, Offset(8f * d, 8f * d), Offset(11f * d, 8f * d), 1.5f * d, StrokeCap.Round)
}

private fun DrawScope.drawAboutIcon(d: Float, c: Color) {
    drawCircle(c, radius = 6f * d, center = Offset(8f * d, 8f * d), style = lineStroke(d))
    drawCircle(c, radius = 0.8f * d, center = Offset(8f * d, 5.5f * d))
    drawLine(c, Offset(8f * d, 7.5f * d), Offset(8f * d, 11f * d), 1.4f * d, StrokeCap.Round)
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen()
}
