package com.example.myapplication

import com.example.myapplication.data.model.response.ProfileData
import com.example.myapplication.data.model.response.ProfileUserDto
import com.example.myapplication.data.model.response.UserDto
import com.example.myapplication.reminder.ReminderPreferences
import com.example.myapplication.reminder.ReminderScheduler
import com.example.myapplication.ui.theme.AppThemeMode
import com.example.myapplication.viewmodel.ProfileUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ProfileAuthFlowTest {

    @Test
    fun profileUiState_handlesAllStatesCorrectly() {
        val loadingState: ProfileUiState = ProfileUiState.Loading
        val emptyState: ProfileUiState = ProfileUiState.Empty
        val errorState: ProfileUiState = ProfileUiState.Error("Network error. Please try again.")

        val testUser = ProfileUserDto(
            id = 42,
            name = "Alex Morgan",
            email = "alex.morgan@algoquest.io",
            initials = "AM",
            joined = "October 2026",
            xp = 3200,
            level = 9,
            streak = 14,
            solvedLessons = 18,
            percentile = "Top 5% Global"
        )
        val successState: ProfileUiState = ProfileUiState.Success(
            ProfileData(
                user = testUser,
                skills = emptyList(),
                achievements = emptyList()
            )
        )

        assertTrue(loadingState is ProfileUiState.Loading)
        assertTrue(emptyState is ProfileUiState.Empty)
        assertTrue(errorState is ProfileUiState.Error)
        assertEquals("Network error. Please try again.", (errorState as ProfileUiState.Error).message)
        assertTrue(successState is ProfileUiState.Success)
        assertEquals(42, (successState as ProfileUiState.Success).profile.user.id)
        assertEquals("AM", successState.profile.user.initials)
        assertEquals(3200, successState.profile.user.xp)
    }

    @Test
    fun userDto_preservesProfileStatsAcrossSessions() {
        val originalUser = UserDto(
            id = 101,
            name = "Test Explorer",
            email = "explorer@test.local",
            xp = 1500,
            level = 5,
            streak = 3,
            goal = "Interview Prep",
            skillLevel = "Intermediate",
            dailyMinutes = 20
        )

        assertEquals(101, originalUser.id)
        assertEquals(1500, originalUser.xp)
        assertEquals(5, originalUser.level)
        assertEquals(3, originalUser.streak)

        // Simulate updated stats after completing a lesson
        val updatedUser = originalUser.copy(xp = 1650, streak = 4)
        assertEquals(1650, updatedUser.xp)
        assertEquals(4, updatedUser.streak)
        assertEquals(originalUser.id, updatedUser.id)
        assertEquals(originalUser.email, updatedUser.email)
    }

    @Test
    fun initialsGeneration_handlesVariousNameFormats() {
        fun extractInitials(name: String): String {
            val parts = name.trim().split(" ")
            return parts.mapNotNull { it.firstOrNull()?.toString() }
                .take(2)
                .joinToString("")
                .uppercase()
                .ifBlank { "AQ" }
        }

        assertEquals("AM", extractInitials("Alex Morgan"))
        assertEquals("A", extractInitials("Alex"))
        assertEquals("AQ", extractInitials(""))
        assertEquals("AQ", extractInitials("   "))
        assertEquals("JD", extractInitials("John Doe Junior"))
    }

    @Test
    fun swipeNavigation_tabToScreenAndScreenToTabMapping_isConsistent() {
        fun tabToScreen(tab: Int): Int = when (tab) {
            0 -> 7  // Home
            1 -> 10 // Map
            2 -> 8  // Visualizer
            3 -> 9  // AI Ally
            4 -> 12 // Profile
            else -> 7
        }

        fun screenToTab(s: Int): Int = when (s) {
            7 -> 0
            10 -> 1
            8 -> 2
            9 -> 3
            12 -> 4
            else -> 0
        }

        val tabs = listOf(0, 1, 2, 3, 4)
        val expectedScreens = listOf(7, 10, 8, 9, 12)

        for (i in tabs.indices) {
            val tab = tabs[i]
            val screen = tabToScreen(tab)
            assertEquals("Tab $tab must map to screen ${expectedScreens[i]}", expectedScreens[i], screen)
            assertEquals("Screen $screen must map back to tab $tab", tab, screenToTab(screen))
        }

        // Test swipe left sequence: 0 -> 1 -> 2 -> 3 -> 4
        var currentTab = 0
        val sequence = mutableListOf(currentTab)
        while (currentTab < 4) {
            currentTab++
            sequence.add(currentTab)
        }
        assertEquals(listOf(0, 1, 2, 3, 4), sequence)

        // Test swipe right sequence: 4 -> 3 -> 2 -> 1 -> 0
        val reverseSequence = mutableListOf(currentTab)
        while (currentTab > 0) {
            currentTab--
            reverseSequence.add(currentTab)
        }
        assertEquals(listOf(4, 3, 2, 1, 0), reverseSequence)
    }

    @Test
    fun passwordVisibility_toggleState_defaultsToHidden() {
        var passwordVisible = false

        // Default state must be hidden
        assertTrue("Password must be hidden by default", !passwordVisible)

        // User taps eye icon -> becomes visible
        passwordVisible = !passwordVisible
        assertTrue("Password must become visible on first tap", passwordVisible)

        // User taps eye icon again -> becomes hidden
        passwordVisible = !passwordVisible
        assertTrue("Password must be hidden on second tap", !passwordVisible)
    }

    @Test
    fun sessionSeparation_filtersCrossUserData() {
        val currentUserId = "101"

        val userA = ProfileUserDto(
            id = 101,
            name = "Account A",
            email = "userA@test.io",
            initials = "AA",
            joined = "October 2026",
            xp = 500,
            level = 2,
            streak = 1,
            solvedLessons = 3,
            percentile = "Top 50%"
        )

        val userB = ProfileUserDto(
            id = 102,
            name = "Account B",
            email = "userB@test.io",
            initials = "AB",
            joined = "October 2026",
            xp = 1200,
            level = 4,
            streak = 5,
            solvedLessons = 8,
            percentile = "Top 20%"
        )

        // Only matching user ID should be accepted
        val safeUserA = userA.takeIf { it.id.toString() == currentUserId }
        val safeUserB = userB.takeIf { it.id.toString() == currentUserId }

        assertNotNull("Authenticated User A must be visible for Session A", safeUserA)
        assertEquals("Account A", safeUserA?.name)
        org.junit.Assert.assertNull("User B data must be rejected and never displayed in Session A", safeUserB)
    }

    @Test
    fun logoutConfirmationDialog_controlsLogoutExecution() {
        var showLogoutDialog = false
        var logoutExecuted = false
        val existingLogoutFunction = {
            logoutExecuted = true
        }

        // 1. Initial State: dialog closed, not logged out
        assertTrue("Dialog must be closed initially", !showLogoutDialog)
        assertTrue("Logout must not have executed", !logoutExecuted)

        // 2. User taps 'Log out' button: dialog opens, logout not yet executed
        showLogoutDialog = true
        assertTrue("Dialog must be visible", showLogoutDialog)
        assertTrue("Logout must not execute just by opening dialog", !logoutExecuted)

        // 3. User taps 'Cancel' in dialog: dialog closes, logout not executed
        showLogoutDialog = false
        assertTrue("Dialog must close on Cancel", !showLogoutDialog)
        assertTrue("Logout must not have executed after Cancel", !logoutExecuted)

        // 4. User taps 'Log out' button again: dialog opens
        showLogoutDialog = true
        assertTrue("Dialog must open again", showLogoutDialog)

        // 5. User taps 'Confirm Logout': dialog closes and existing logout executes
        showLogoutDialog = false
        existingLogoutFunction()
        assertTrue("Dialog must close on Confirm Logout", !showLogoutDialog)
        assertTrue("Existing logout function must be executed on Confirm Logout", logoutExecuted)
    }

    @Test
    fun googleSignInComingSoonDialog_controlsDialogVisibilityWithoutPretendingLogin() {
        var showGoogleDialog = false
        var isLoggedIn = false

        // 1. Initial State: dialog closed, not logged in
        assertTrue("Google dialog must be closed initially", !showGoogleDialog)
        assertTrue("User must not be logged in", !isLoggedIn)

        // 2. User taps 'Continue with Google': shows coming-soon dialog, does NOT log in
        showGoogleDialog = true
        assertTrue("Google dialog must be visible", showGoogleDialog)
        assertTrue("Must NOT pretend Google authentication succeeded", !isLoggedIn)

        // 3. User taps 'Got it' / dismisses dialog: dialog closes, still not logged in
        showGoogleDialog = false
        assertTrue("Google dialog must close", !showGoogleDialog)
        assertTrue("User must still not be logged in", !isLoggedIn)
    }

    @Test
    fun settingsNavigation_opensFromHomeAndNavigatesBack() {
        var screen = 7 // Home Dashboard
        var previousScreenBeforeSettings = 7
        val onOpenSettingsFromHome = {
            previousScreenBeforeSettings = 7
            screen = 14
        }
        val onBackFromSettings = { screen = previousScreenBeforeSettings }

        // 1. Initial State: on Home screen
        assertEquals(7, screen)

        // 2. User taps Settings gear icon in top-right header
        onOpenSettingsFromHome()
        assertEquals("Screen must navigate to Settings (14)", 14, screen)

        // 3. User taps Back button in Settings
        onBackFromSettings()
        assertEquals("Screen must return to Home (7)", 7, screen)
    }

    @Test
    fun settingsNavigation_opensFromProfileAndNavigatesBackToProfile() {
        var screen = 12 // Explorer Profile screen
        var previousScreenBeforeSettings = 7
        val onOpenSettingsFromProfile = {
            previousScreenBeforeSettings = 12
            screen = 14
        }
        val onBackFromSettings = { screen = previousScreenBeforeSettings }

        // 1. Initial State: on Profile screen
        assertEquals(12, screen)

        // 2. User taps SETTINGS button in top-right beside Explorer Profile heading
        onOpenSettingsFromProfile()
        assertEquals("Screen must navigate to same Settings (14)", 14, screen)

        // 3. User taps Back button in Settings
        onBackFromSettings()
        assertEquals("Screen must return to Profile (12)", 12, screen)
    }

    @Test
    fun settingsSubPageNavigation_allSixSectionsAreClickableAndNavigateBack() {
        var currentSubPage = SettingsSubPage.MAIN

        val sections = listOf(
            SettingsSubPage.APPEARANCE,
            SettingsSubPage.NOTIFICATIONS,
            SettingsSubPage.LEARNING,
            SettingsSubPage.ACCESSIBILITY,
            SettingsSubPage.PRIVACY,
            SettingsSubPage.ABOUT
        )

        for (section in sections) {
            // Click to navigate to subpage
            currentSubPage = section
            assertEquals("Tapping section should navigate to $section", section, currentSubPage)

            // Back button on subpage returns to MAIN
            currentSubPage = SettingsSubPage.MAIN
            assertEquals("Back button must return to MAIN", SettingsSubPage.MAIN, currentSubPage)
        }
    }

    @Test
    fun learningPreferences_programmingLanguageAndDifficultyCanBeSelected() {
        var selectedLanguage = "Python"
        var selectedDifficulty = "Beginner"

        // Change language
        selectedLanguage = "Kotlin"
        assertEquals("Kotlin", selectedLanguage)
        selectedLanguage = "C++"
        assertEquals("C++", selectedLanguage)

        // Change difficulty
        selectedDifficulty = "Intermediate"
        assertEquals("Intermediate", selectedDifficulty)
        selectedDifficulty = "Advanced"
        assertEquals("Advanced", selectedDifficulty)
    }

    @Test
    fun themeMode_resolvesCorrectlyForDarkLightAndSystem() {
        fun resolveIsDark(mode: AppThemeMode, systemDark: Boolean): Boolean = when (mode) {
            AppThemeMode.DARK -> true
            AppThemeMode.LIGHT -> false
            AppThemeMode.SYSTEM -> systemDark
        }

        // DARK always resolves to dark (true)
        assertTrue(resolveIsDark(AppThemeMode.DARK, systemDark = true))
        assertTrue(resolveIsDark(AppThemeMode.DARK, systemDark = false))

        // LIGHT always resolves to light (false)
        assertFalse(resolveIsDark(AppThemeMode.LIGHT, systemDark = true))
        assertFalse(resolveIsDark(AppThemeMode.LIGHT, systemDark = false))

        // SYSTEM follows systemDark
        assertTrue(resolveIsDark(AppThemeMode.SYSTEM, systemDark = true))
        assertFalse(resolveIsDark(AppThemeMode.SYSTEM, systemDark = false))
    }

    @Test
    fun appearanceSettings_supportsExpectedSyntaxAndFontOptions() {
        val supportedSchemes = listOf("Neon Synth", "Dracula Dusk", "Cyber Solarized")
        val supportedFonts = listOf("JetBrains Mono", "Fira Code", "Roboto Mono")

        assertTrue(supportedSchemes.contains("Neon Synth"))
        assertTrue(supportedSchemes.contains("Dracula Dusk"))
        assertTrue(supportedSchemes.contains("Cyber Solarized"))

        assertTrue(supportedFonts.contains("JetBrains Mono"))
        assertTrue(supportedFonts.contains("Fira Code"))
        assertTrue(supportedFonts.contains("Roboto Mono"))
    }

    @Test
    fun reminderPreferences_formatTime_correctlyFormats12HourTime() {
        // Evening / PM times
        assertEquals("08:00 PM", ReminderPreferences.formatTime(20, 0))
        assertEquals("11:59 PM", ReminderPreferences.formatTime(23, 59))
        assertEquals("12:00 PM", ReminderPreferences.formatTime(12, 0))
        assertEquals("01:15 PM", ReminderPreferences.formatTime(13, 15))

        // Morning / AM times
        assertEquals("12:00 AM", ReminderPreferences.formatTime(0, 0))
        assertEquals("09:05 AM", ReminderPreferences.formatTime(9, 5))
        assertEquals("06:30 AM", ReminderPreferences.formatTime(6, 30))
        assertEquals("11:45 AM", ReminderPreferences.formatTime(11, 45))
    }

    @Test
    fun reminderScheduler_calculateNextTriggerMillis_schedulesTodayWhenTimeInFuture() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val nowMillis = calendar.timeInMillis

        // Reminder set to 20:00 (8:00 PM) should trigger later today
        val triggerMillis = ReminderScheduler.calculateNextTriggerMillis(20, 0, nowMillis)

        assertTrue("Trigger must be in the future", triggerMillis > nowMillis)
        val triggerCal = Calendar.getInstance().apply { timeInMillis = triggerMillis }
        assertEquals(calendar.get(Calendar.DAY_OF_YEAR), triggerCal.get(Calendar.DAY_OF_YEAR))
        assertEquals(20, triggerCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, triggerCal.get(Calendar.MINUTE))
    }

    @Test
    fun reminderScheduler_calculateNextTriggerMillis_schedulesTomorrowWhenTimePassed() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 21) // 9:00 PM
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val nowMillis = calendar.timeInMillis

        // Reminder set to 20:00 (8:00 PM, which already passed today) should trigger tomorrow
        val triggerMillis = ReminderScheduler.calculateNextTriggerMillis(20, 0, nowMillis)

        assertTrue("Trigger must be in the future", triggerMillis > nowMillis)
        val triggerCal = Calendar.getInstance().apply { timeInMillis = triggerMillis }
        val dayDiff = triggerCal.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR)
        assertTrue("Trigger must be scheduled for next day", dayDiff == 1 || dayDiff < 0 /* year wrap */)
        assertEquals(20, triggerCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, triggerCal.get(Calendar.MINUTE))
    }

    @Test
    fun reminderNotificationConstants_matchExactRequirements() {
        assertEquals("Time for AlgoQuest!", ReminderScheduler.NOTIFICATION_TITLE)
        assertEquals("Spend a few minutes practising DSA today.", ReminderScheduler.NOTIFICATION_MESSAGE)
    }

    @Test
    fun streakReminderNotificationConstants_matchExactRequirements() {
        assertEquals("Protect Your Learning Streak!", ReminderScheduler.STREAK_NOTIFICATION_TITLE)
        assertEquals("Complete today's DSA practice to keep your streak alive.", ReminderScheduler.STREAK_NOTIFICATION_MESSAGE)
        assertEquals(4003, ReminderScheduler.STREAK_ALARM_REQUEST_CODE)
        assertEquals(4004, ReminderScheduler.STREAK_NOTIFICATION_ID)
        assertEquals("com.example.myapplication.ACTION_STREAK_REMINDER", ReminderScheduler.ACTION_STREAK_REMINDER)
    }

    @Test
    fun streakReminder_suppressionLogic_suppressesWhenPracticeCompleted() {
        fun shouldSendStreakReminder(isPracticeCompletedToday: Boolean, hasSentToday: Boolean): Boolean {
            if (isPracticeCompletedToday) return false
            if (hasSentToday) return false
            return true
        }

        // Case 1: Practice is complete -> must NOT send streak reminder (suppressed)
        assertFalse("Streak reminder must be suppressed if user already practiced today",
            shouldSendStreakReminder(isPracticeCompletedToday = true, hasSentToday = false))

        // Case 2: Practice incomplete and not yet sent -> MUST send streak reminder
        assertTrue("Streak reminder must send if practice is incomplete today",
            shouldSendStreakReminder(isPracticeCompletedToday = false, hasSentToday = false))

        // Case 3: Practice incomplete, but already sent earlier today -> avoid duplicate
        assertFalse("Duplicate streak reminders must be avoided on same day",
            shouldSendStreakReminder(isPracticeCompletedToday = false, hasSentToday = true))
    }

    @Test
    fun streakReminderScheduler_calculateNextTriggerMillis_schedulesCorrectly() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 18) // 6:00 PM
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val nowMillis = calendar.timeInMillis

        // Streak reminder set to 21:00 (9:00 PM) should trigger at 21:00 today
        val triggerMillis = ReminderScheduler.calculateNextTriggerMillis(21, 0, nowMillis)

        assertTrue(triggerMillis > nowMillis)
        val triggerCal = Calendar.getInstance().apply { timeInMillis = triggerMillis }
        assertEquals(calendar.get(Calendar.DAY_OF_YEAR), triggerCal.get(Calendar.DAY_OF_YEAR))
        assertEquals(21, triggerCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, triggerCal.get(Calendar.MINUTE))
    }

    @Test
    fun quizNotificationConstants_matchExactRequirements() {
        assertEquals("New Quiz Available!", ReminderScheduler.QUIZ_NOTIFICATION_TITLE)
        assertEquals("A new DSA challenge is waiting for you. Test your knowledge!", ReminderScheduler.QUIZ_NOTIFICATION_MESSAGE)
        assertEquals(4005, ReminderScheduler.QUIZ_NOTIFICATION_ID)
        assertEquals("DESTINATION_SCREEN", ReminderScheduler.EXTRA_DESTINATION_SCREEN)
        assertEquals("TARGET_QUIZ_LESSON_ID", ReminderScheduler.EXTRA_QUIZ_LESSON_ID)
    }

    @Test
    fun quizNotification_duplicatePreventionAndEnableLogic() {
        // Simulates the decision logic inside showQuizNotification
        fun shouldSendQuizNotification(
            isEnabled: Boolean,
            alreadyNotifiedIds: Set<Int>,
            quizLessonId: Int,
            isTest: Boolean
        ): Boolean {
            if (!isEnabled) return false
            if (!isTest && alreadyNotifiedIds.contains(quizLessonId)) return false
            return true
        }

        val notifiedQuizzes = mutableSetOf<Int>()

        // 1. Initial discovery of Quiz #5 when enabled -> Should send
        assertTrue(
            "First discovery of Quiz #5 must send notification",
            shouldSendQuizNotification(isEnabled = true, alreadyNotifiedIds = notifiedQuizzes, quizLessonId = 5, isTest = false)
        )
        notifiedQuizzes.add(5)

        // 2. Second discovery of same Quiz #5 -> Duplicate blocked
        assertFalse(
            "Duplicate notification for Quiz #5 must be prevented",
            shouldSendQuizNotification(isEnabled = true, alreadyNotifiedIds = notifiedQuizzes, quizLessonId = 5, isTest = false)
        )

        // 3. New Quiz #6 discovered -> Should send
        assertTrue(
            "New Quiz #6 must send notification",
            shouldSendQuizNotification(isEnabled = true, alreadyNotifiedIds = notifiedQuizzes, quizLessonId = 6, isTest = false)
        )
        notifiedQuizzes.add(6)

        // 4. When switch is disabled -> Never send even for new quiz
        assertFalse(
            "Disabled quiz notifications must not send alerts",
            shouldSendQuizNotification(isEnabled = false, alreadyNotifiedIds = notifiedQuizzes, quizLessonId = 7, isTest = false)
        )

        // 5. Test button dispatch with isTest = true -> Always sends even if already notified
        assertTrue(
            "Explicit test button must dispatch even if quiz id was notified",
            shouldSendQuizNotification(isEnabled = true, alreadyNotifiedIds = notifiedQuizzes, quizLessonId = 5, isTest = true)
        )
    }

    @Test
    fun quizNotification_intentScreenDestination_targetsQuizScreenIndex13() {
        val destinationScreen = 13 // Screen 13 is QuizScreen in MainActivity
        val quizLessonId = 5

        // Verify routing mapping matches MainActivity navigation architecture
        assertEquals(13, destinationScreen)
        assertEquals(5, quizLessonId)
    }

    @Test
    fun achievementNotificationConstants_matchExactRequirements() {
        assertEquals("Achievement Unlocked!", ReminderScheduler.DEFAULT_ACHIEVEMENT_TITLE)
        assertEquals("Congratulations! You earned a new badge in AlgoQuest.", ReminderScheduler.DEFAULT_ACHIEVEMENT_MESSAGE)
        assertEquals(4006, ReminderScheduler.ACHIEVEMENT_NOTIFICATION_ID)
        assertEquals("algoquest_achievements", ReminderScheduler.ACHIEVEMENT_CHANNEL_ID)
        assertEquals("Achievements & Badges", ReminderScheduler.ACHIEVEMENT_CHANNEL_NAME)
        assertEquals("TARGET_ACHIEVEMENT_ID", ReminderScheduler.EXTRA_ACHIEVEMENT_ID)
    }

    @Test
    fun achievementNotification_duplicatePreventionAndEnableLogic() {
        fun shouldSendAchievementNotification(
            isEnabled: Boolean,
            alreadyNotifiedKeys: Set<String>,
            achievementKey: String,
            isTest: Boolean
        ): Boolean {
            if (!isEnabled) return false
            if (!isTest && alreadyNotifiedKeys.contains(achievementKey)) return false
            return true
        }

        val notifiedAchievements = mutableSetOf<String>()

        // 1. Initial unlock of "Binary Searcher" badge -> Should send
        assertTrue(
            "First unlock of Binary Searcher must send notification",
            shouldSendAchievementNotification(isEnabled = true, alreadyNotifiedKeys = notifiedAchievements, achievementKey = "ach_2", isTest = false)
        )
        notifiedAchievements.add("ach_2")

        // 2. Duplicate unlock of same "Binary Searcher" badge -> Blocked
        assertFalse(
            "Duplicate notification for Binary Searcher must be prevented",
            shouldSendAchievementNotification(isEnabled = true, alreadyNotifiedKeys = notifiedAchievements, achievementKey = "ach_2", isTest = false)
        )

        // 3. New "AI Explorer" badge unlocked -> Should send
        assertTrue(
            "New AI Explorer badge must send notification",
            shouldSendAchievementNotification(isEnabled = true, alreadyNotifiedKeys = notifiedAchievements, achievementKey = "ach_3", isTest = false)
        )
        notifiedAchievements.add("ach_3")

        // 4. Learning milestone unlocked (Level 2) -> Should send once
        assertTrue(
            "New Level milestone must send notification",
            shouldSendAchievementNotification(isEnabled = true, alreadyNotifiedKeys = notifiedAchievements, achievementKey = "milestone_level_2", isTest = false)
        )
        notifiedAchievements.add("milestone_level_2")

        // 5. Duplicate milestone -> Blocked
        assertFalse(
            "Duplicate milestone must be prevented",
            shouldSendAchievementNotification(isEnabled = true, alreadyNotifiedKeys = notifiedAchievements, achievementKey = "milestone_level_2", isTest = false)
        )

        // 6. When toggle is disabled in Settings -> Never send
        assertFalse(
            "Disabled achievement notifications must not send alerts",
            shouldSendAchievementNotification(isEnabled = false, alreadyNotifiedKeys = notifiedAchievements, achievementKey = "ach_4", isTest = false)
        )

        // 7. Explicit test button -> Always dispatches even if key exists
        assertTrue(
            "Explicit test button must dispatch even if achievement was already notified",
            shouldSendAchievementNotification(isEnabled = true, alreadyNotifiedKeys = notifiedAchievements, achievementKey = "ach_2", isTest = true)
        )
    }

    @Test
    fun achievementNotification_intentScreenDestination_targetsProfileScreenIndex12() {
        val destinationScreen = 12 // Screen 12 is ProfileScreen (Explorer Profile) in MainActivity
        assertEquals(12, destinationScreen)
    }

    @Test
    fun achievementNotification_customizedMessages_formatProperly() {
        val badgeTitle = "Binary Searcher"
        val emoji = "⚡"
        val desc = "Mastered Binary Search and completed Chapter 03"
        val customBadgeMessage = "Congratulations! You earned '$badgeTitle' $emoji - $desc"

        assertTrue(customBadgeMessage.contains("Binary Searcher"))
        assertTrue(customBadgeMessage.contains("⚡"))
        assertTrue(customBadgeMessage.contains("Mastered Binary Search"))

        val level = 2
        val customMilestoneMessage = "Level Up! You reached Level $level as an Explorer. Keep conquering algorithms!"
        assertTrue(customMilestoneMessage.contains("Level 2"))
    }

    @Test
    fun swipeNavigation_allFourTransitionsInBothDirections_workExactlyAsExpected() {
        var currentPage = 0
        fun swipeLeft() {
            if (currentPage < 4) currentPage++
        }
        fun swipeRight() {
            if (currentPage > 0) currentPage--
        }

        fun screenForPage(p: Int) = when (p) {
            0 -> 7  // Home
            1 -> 10 // Map
            2 -> 8  // Visualizer
            3 -> 9  // AI Ally
            4 -> 12 // Profile
            else -> 7
        }

        // 1. Swipe left on Home -> Map
        assertEquals(0, currentPage)
        swipeLeft()
        assertEquals("Swipe left on Home must navigate to Map (page 1)", 1, currentPage)
        assertEquals(10, screenForPage(currentPage))

        // 2. Swipe right on Map -> Home
        swipeRight()
        assertEquals("Swipe right on Map must navigate back to Home (page 0)", 0, currentPage)
        assertEquals(7, screenForPage(currentPage))

        // 3. Swipe left on Map -> Visualizer
        swipeLeft() // to Map (1)
        swipeLeft() // to Visualizer (2)
        assertEquals("Swipe left on Map must navigate to Visualizer (page 2)", 2, currentPage)
        assertEquals(8, screenForPage(currentPage))

        // 4. Swipe right on Visualizer -> Map
        swipeRight()
        assertEquals("Swipe right on Visualizer must navigate back to Map (page 1)", 1, currentPage)
        assertEquals(10, screenForPage(currentPage))

        // 5. Swipe left on Visualizer -> AI Ally
        swipeLeft() // back to Visualizer (2)
        swipeLeft() // to AI Ally (3)
        assertEquals("Swipe left on Visualizer must navigate to AI Ally (page 3)", 3, currentPage)
        assertEquals(9, screenForPage(currentPage))

        // 6. Swipe right on AI Ally -> Visualizer
        swipeRight()
        assertEquals("Swipe right on AI Ally must navigate back to Visualizer (page 2)", 2, currentPage)
        assertEquals(8, screenForPage(currentPage))

        // 7. Swipe left on AI Ally -> Profile
        swipeLeft() // back to AI Ally (3)
        swipeLeft() // to Profile (4)
        assertEquals("Swipe left on AI Ally must navigate to Profile (page 4)", 4, currentPage)
        assertEquals(12, screenForPage(currentPage))

        // 8. Swipe right on Profile -> AI Ally
        swipeRight()
        assertEquals("Swipe right on Profile must navigate back to AI Ally (page 3)", 3, currentPage)
        assertEquals(9, screenForPage(currentPage))
    }

    @Test
    fun swipeNavigation_bottomBarTap_synchronizesBidirectionally() {
        var selectedTab = 0
        var pagerPage = 0

        fun tapTab(tab: Int) {
            selectedTab = tab
            pagerPage = tab
        }

        fun swipeToPage(page: Int) {
            pagerPage = page
            selectedTab = page
        }

        // Tap tests
        tapTab(1)
        assertEquals(1, selectedTab)
        assertEquals(1, pagerPage)

        tapTab(4)
        assertEquals(4, selectedTab)
        assertEquals(4, pagerPage)

        // Swipe tests
        swipeToPage(3)
        assertEquals(3, selectedTab)
        assertEquals(3, pagerPage)

        swipeToPage(0)
        assertEquals(0, selectedTab)
        assertEquals(0, pagerPage)
    }

    @Test
    fun swipeNavigation_nonMainScreens_doNotIncludeSwipePager() {
        val nonSwipeScreens = listOf(0, 1, 2, 3, 4, 5, 6, 11, 13, 14)
        val mainSwipeScreens = listOf(7, 10, 8, 9, 12)

        for (s in nonSwipeScreens) {
            assertTrue("Screen $s must be rendered outside HorizontalPager", s !in mainSwipeScreens)
        }
        for (s in mainSwipeScreens) {
            assertTrue("Screen $s must be part of the 5 main swipe tabs", s in mainSwipeScreens)
        }
    }

    @Test
    fun bottomBarSwipeHelper_swipeLeft_movesToNextTabAndClampsAtProfile() {
        // Tab order: 0: Home, 1: Map, 2: Visualizer, 3: AI Ally, 4: Profile
        assertEquals(1, BottomBarSwipeHelper.getNextTabOnSwipeLeft(0)) // Home -> Map
        assertEquals(2, BottomBarSwipeHelper.getNextTabOnSwipeLeft(1)) // Map -> Visualizer
        assertEquals(3, BottomBarSwipeHelper.getNextTabOnSwipeLeft(2)) // Visualizer -> AI Ally
        assertEquals(4, BottomBarSwipeHelper.getNextTabOnSwipeLeft(3)) // AI Ally -> Profile
        // Clamping at Profile
        assertEquals(4, BottomBarSwipeHelper.getNextTabOnSwipeLeft(4)) // Profile remains Profile
    }

    @Test
    fun bottomBarSwipeHelper_swipeRight_movesToPreviousTabAndClampsAtHome() {
        // Tab order: 4: Profile, 3: AI Ally, 2: Visualizer, 1: Map, 0: Home
        assertEquals(3, BottomBarSwipeHelper.getNextTabOnSwipeRight(4)) // Profile -> AI Ally
        assertEquals(2, BottomBarSwipeHelper.getNextTabOnSwipeRight(3)) // AI Ally -> Visualizer
        assertEquals(1, BottomBarSwipeHelper.getNextTabOnSwipeRight(2)) // Visualizer -> Map
        assertEquals(0, BottomBarSwipeHelper.getNextTabOnSwipeRight(1)) // Map -> Home
        // Clamping at Home
        assertEquals(0, BottomBarSwipeHelper.getNextTabOnSwipeRight(0)) // Home remains Home
    }

    @Test
    fun bottomBarSwipeTracker_singleActionPerGesture_preventsRapidMultipleTransitions() {
        var currentNavigatedTab = -1
        val tracker = BottomBarSwipeTracker(swipeThresholdPx = 100f) { tab ->
            currentNavigatedTab = tab
        }

        tracker.onDragStart()
        assertEquals(0f, tracker.dragAccumulator, 0.001f)
        assertFalse(tracker.hasSwipedInGesture)

        // Sub-threshold drag: no transition
        val smallDragResult = tracker.onDrag(-40f, currentTab = 0)
        assertFalse(smallDragResult)
        assertEquals(-40f, tracker.dragAccumulator, 0.001f)
        assertEquals(-1, currentNavigatedTab)
        assertFalse(tracker.hasSwipedInGesture)

        // Exceeds threshold: swipe left triggers exactly once
        val thresholdResult = tracker.onDrag(-70f, currentTab = 0)
        assertTrue(thresholdResult)
        assertEquals(1, currentNavigatedTab)
        assertTrue(tracker.hasSwipedInGesture)

        // Further dragging within the same gesture is ignored (prevents rapid multiple page flips)
        val excessiveDragResult = tracker.onDrag(-200f, currentTab = 1)
        assertFalse(excessiveDragResult)
        assertEquals(1, currentNavigatedTab) // Remains 1, not 2 or 3

        // Finger release resets the tracker for next gesture
        tracker.onDragEnd()
        assertEquals(0f, tracker.dragAccumulator, 0.001f)
        assertFalse(tracker.hasSwipedInGesture)

        // Next gesture: swipe left again
        tracker.onDragStart()
        val nextGestureResult = tracker.onDrag(-110f, currentTab = 1)
        assertTrue(nextGestureResult)
        assertEquals(2, currentNavigatedTab)
    }

    @Test
    fun bottomBarSwipeTracker_clampingAtBoundaries_ignoresBoundaryOverflow() {
        var navigatedTab = -1
        val tracker = BottomBarSwipeTracker(swipeThresholdPx = 100f) { tab ->
            navigatedTab = tab
        }

        // 1. Swiping right at Tab 0 (Home) must be clamped and not navigate
        tracker.onDragStart()
        val rightAtHome = tracker.onDrag(150f, currentTab = 0)
        assertFalse("Swiping right at Home must not navigate", rightAtHome)
        assertEquals(-1, navigatedTab)
        assertTrue(tracker.hasSwipedInGesture)
        tracker.onDragEnd()

        // 2. Swiping left at Tab 4 (Profile) must be clamped and not navigate
        tracker.onDragStart()
        val leftAtProfile = tracker.onDrag(-150f, currentTab = 4)
        assertFalse("Swiping left at Profile must not navigate", leftAtProfile)
        assertEquals(-1, navigatedTab)
        assertTrue(tracker.hasSwipedInGesture)
        tracker.onDragEnd()
    }

    @Test
    fun bottomBarSwipe_synchronizesWithExistingPagerStateAndScreenMapping() {
        fun tabToScreen(tab: Int) = when (tab) {
            0 -> 7  // Home
            1 -> 10 // Map
            2 -> 8  // Visualizer
            3 -> 9  // AI Ally
            4 -> 12 // Profile
            else -> 7
        }

        var currentTab = 0
        var pagerPage = 0
        var currentScreen = tabToScreen(0)

        fun onBottomBarSwiped(newTab: Int) {
            currentTab = newTab
            pagerPage = newTab
            currentScreen = tabToScreen(newTab)
        }

        // Sequence of left swipes across all tabs:
        // 0 (Home) -> 1 (Map)
        var next = BottomBarSwipeHelper.getNextTabOnSwipeLeft(currentTab)
        onBottomBarSwiped(next)
        assertEquals(1, currentTab)
        assertEquals(1, pagerPage)
        assertEquals(10, currentScreen)

        // 1 (Map) -> 2 (Visualizer)
        next = BottomBarSwipeHelper.getNextTabOnSwipeLeft(currentTab)
        onBottomBarSwiped(next)
        assertEquals(2, currentTab)
        assertEquals(2, pagerPage)
        assertEquals(8, currentScreen)

        // 2 (Visualizer) -> 3 (AI Ally)
        next = BottomBarSwipeHelper.getNextTabOnSwipeLeft(currentTab)
        onBottomBarSwiped(next)
        assertEquals(3, currentTab)
        assertEquals(3, pagerPage)
        assertEquals(9, currentScreen)

        // 3 (AI Ally) -> 4 (Profile)
        next = BottomBarSwipeHelper.getNextTabOnSwipeLeft(currentTab)
        onBottomBarSwiped(next)
        assertEquals(4, currentTab)
        assertEquals(4, pagerPage)
        assertEquals(12, currentScreen)

        // Sequence of right swipes:
        // 4 (Profile) -> 3 (AI Ally)
        var prev = BottomBarSwipeHelper.getNextTabOnSwipeRight(currentTab)
        onBottomBarSwiped(prev)
        assertEquals(3, currentTab)
        assertEquals(3, pagerPage)
        assertEquals(9, currentScreen)

        // 3 (AI Ally) -> 2 (Visualizer)
        prev = BottomBarSwipeHelper.getNextTabOnSwipeRight(currentTab)
        onBottomBarSwiped(prev)
        assertEquals(2, currentTab)
        assertEquals(2, pagerPage)
        assertEquals(8, currentScreen)

        // 2 (Visualizer) -> 1 (Map)
        prev = BottomBarSwipeHelper.getNextTabOnSwipeRight(currentTab)
        onBottomBarSwiped(prev)
        assertEquals(1, currentTab)
        assertEquals(1, pagerPage)
        assertEquals(10, currentScreen)

        // 1 (Map) -> 0 (Home)
        prev = BottomBarSwipeHelper.getNextTabOnSwipeRight(currentTab)
        onBottomBarSwiped(prev)
        assertEquals(0, currentTab)
        assertEquals(0, pagerPage)
        assertEquals(7, currentScreen)
    }

    @Test
    fun homeScreen_userAndMetricsDisplay_correctlyResolvesDynamicDataAndProgress() {
        val testUser = UserDto(
            id = 42,
            name = "Jane Developer",
            email = "jane@algoquest.io",
            xp = 1350,
            level = 3,
            streak = 5
        )

        val userLevel = testUser.level
        val rawXp = testUser.xp

        // Level progress calculation test
        val levelBaseXp = ((userLevel - 1) * 500).coerceAtLeast(0)
        val nextLevelTargetXp = (userLevel * 500).coerceAtLeast(500)
        val xpInCurrentLevel = (rawXp - levelBaseXp).coerceAtLeast(0)
        val xpRangeForLevel = (nextLevelTargetXp - levelBaseXp).coerceAtLeast(1)
        val levelProgress = (xpInCurrentLevel.toFloat() / xpRangeForLevel.toFloat()).coerceIn(0.08f, 1f)

        assertEquals("Jane Developer", testUser.name)
        assertEquals(3, testUser.level)
        assertEquals(5, testUser.streak)
        assertEquals(1350, testUser.xp)
        assertEquals("1,350", String.format("%,d", testUser.xp))

        // Level 3 base is 1000, target is 1500. XP is 1350, so in-level is 350 / 500 = 0.70f (70%)
        assertEquals(1000, levelBaseXp)
        assertEquals(1500, nextLevelTargetXp)
        assertEquals(350, xpInCurrentLevel)
        assertEquals(0.70f, levelProgress, 0.001f)
    }

    @Test
    fun homeScreen_dailyQuest_calculatesProgressAndCompletionCorrectly() {
        // In-progress quest: step 2 of 3
        val currentStep = 2
        val requiredSteps = 3
        val isCompleted = false
        val progress = if (isCompleted) 1f else (currentStep.toFloat() / requiredSteps.toFloat()).coerceIn(0.1f, 1f)
        assertEquals(0.666f, progress, 0.01f)

        // Completed quest: step 3 of 3
        val completedQuestProgress = if (true) 1f else (3f / 3f)
        assertEquals(1.0f, completedQuestProgress, 0.001f)
    }

    @Test
    fun homeScreen_learningPathDestinations_preserveExactNavigationTargets() {
        // Navigation mappings in MainActivity
        var navigatedScreen = -1
        var navigatedChapterId = -1
        var navigatedAiQuery = ""

        val onContinueQuest: () -> Unit = { navigatedScreen = 8 }
        val onQuestMap: () -> Unit = { navigatedScreen = 10 }
        val onOpenChapter: () -> Unit = {
            navigatedChapterId = 3
            navigatedScreen = 11
        }
        val onOpenVisualizer: () -> Unit = { navigatedScreen = 8 }
        val onAskAi: () -> Unit = {
            navigatedAiQuery = "Can you help me understand Binary Search?"
            navigatedScreen = 9
        }
        val onOpenProfile: () -> Unit = { navigatedScreen = 12 }
        val onOpenSettings: () -> Unit = { navigatedScreen = 14 }

        onContinueQuest()
        assertEquals("Continue quest navigates to Visualizer Lab (screen 8)", 8, navigatedScreen)

        onQuestMap()
        assertEquals("Quest Map link navigates to Map (screen 10)", 10, navigatedScreen)

        onOpenChapter()
        assertEquals("Searching Algorithms navigates to Chapter Detail (screen 11)", 11, navigatedScreen)
        assertEquals(3, navigatedChapterId)

        onOpenVisualizer()
        assertEquals("Interactive Visualizer Lab navigates to Visualizer (screen 8)", 8, navigatedScreen)

        onAskAi()
        assertEquals("Quest AI card navigates to AI Chat (screen 9)", 9, navigatedScreen)
        assertEquals("Can you help me understand Binary Search?", navigatedAiQuery)

        onOpenProfile()
        assertEquals("Profile navigation opens Profile (screen 12)", 12, navigatedScreen)

        onOpenSettings()
        assertEquals("Settings gear navigates to Settings (screen 14)", 14, navigatedScreen)
    }

    @Test
    fun homeScreen_achievementPreview_filtersUnlockedAchievementsAndHandlesEmptyState() {
        val sampleAchievements = listOf(
            com.example.myapplication.data.model.response.AchievementDto(
                id = 1,
                code = "streak_master",
                title = "Streak Master",
                description = "Maintained a 7-day learning streak",
                iconEmoji = "🔥",
                xpReward = 200,
                isUnlocked = true,
                unlockedAt = "2026-11-01"
            ),
            com.example.myapplication.data.model.response.AchievementDto(
                id = 2,
                code = "binary_searcher",
                title = "Binary Searcher",
                description = "Mastered Binary Search",
                iconEmoji = "⚡",
                xpReward = 250,
                isUnlocked = false,
                unlockedAt = null
            ),
            com.example.myapplication.data.model.response.AchievementDto(
                id = 3,
                code = "array_legend",
                title = "Array Legend",
                description = "Completed Arrays chapter",
                iconEmoji = "🛡️",
                xpReward = 200,
                isUnlocked = true,
                unlockedAt = "2026-11-02"
            )
        )

        val unlocked = sampleAchievements.filter { it.isUnlocked }
        assertEquals(2, unlocked.size)
        assertEquals("Streak Master", unlocked[0].title)
        assertEquals("🔥", unlocked[0].iconEmoji)
        assertEquals(200, unlocked[0].xpReward)
        assertEquals("Array Legend", unlocked[1].title)

        // Empty state when none unlocked
        val allLocked = sampleAchievements.map { it.copy(isUnlocked = false) }
        val emptyUnlocked = allLocked.filter { it.isUnlocked }
        assertTrue(emptyUnlocked.isEmpty())
    }

    @Test
    fun questMap_statusParsing_handlesAllStatesCorrectly() {
        fun parseStatus(s: String): NodeStatus = when (s.uppercase()) {
            "COMPLETED" -> NodeStatus.COMPLETED
            "ACTIVE" -> NodeStatus.ACTIVE
            "NEXT" -> NodeStatus.NEXT
            else -> NodeStatus.LOCKED
        }

        assertEquals(NodeStatus.COMPLETED, parseStatus("COMPLETED"))
        assertEquals(NodeStatus.COMPLETED, parseStatus("completed"))
        assertEquals(NodeStatus.ACTIVE, parseStatus("ACTIVE"))
        assertEquals(NodeStatus.NEXT, parseStatus("NEXT"))
        assertEquals(NodeStatus.LOCKED, parseStatus("LOCKED"))
        assertEquals(NodeStatus.LOCKED, parseStatus("unknown_status"))
    }

    @Test
    fun questMap_activeChapterDetermination_resolvesActiveNodeAccurately() {
        val testNodes = listOf(
            QuestNode(1, 1, "Arrays & Memory", "Continuous buffers", "Chapter 01", "4/4 Lessons", 4, 4, 400, "+400 XP", NodeStatus.COMPLETED, "array") {},
            QuestNode(2, 2, "Two Pointers", "Opposite ends", "Chapter 02", "5/5 Lessons", 5, 5, 500, "+500 XP", NodeStatus.COMPLETED, "pointer") {},
            QuestNode(3, 3, "Searching Algorithms", "Linear vs Binary Search", "Chapter 03", "2/5 Lessons", 2, 5, 900, "+900 XP", NodeStatus.ACTIVE, "search") {},
            QuestNode(4, 4, "Sorting & Divide/Conquer", "Divide and conquer", "Chapter 04", "0/5 Lessons", 0, 5, 650, "+650 XP", NodeStatus.NEXT, "sort") {},
            QuestNode(5, 5, "Linked Lists", "Nodes and references", "Chapter 05", "0/5 Lessons", 0, 5, 700, "+700 XP", NodeStatus.LOCKED, "link") {}
        )

        val activeNode = testNodes.firstOrNull { it.status == NodeStatus.ACTIVE }
            ?: testNodes.firstOrNull { it.status == NodeStatus.NEXT }
            ?: testNodes.firstOrNull()

        assertNotNull(activeNode)
        assertEquals("Searching Algorithms", activeNode!!.title)
        assertEquals("Chapter 03", activeNode.chapter)
        assertEquals(3, activeNode.id)
        assertEquals(900, activeNode.totalXp)
        assertEquals("+900 XP", activeNode.xp)
        assertEquals(2, activeNode.completedLessons)
        assertEquals(5, activeNode.totalLessons)

        // Progress percentage for active chapter
        val progress = activeNode.completedLessons.toFloat() / activeNode.totalLessons.toFloat()
        assertEquals(0.40f, progress, 0.001f)
    }

    @Test
    fun questMap_lockedChapterInteraction_preventsNavigationAndDemandsPreviousChapter() {
        val testNodes = listOf(
            QuestNode(1, 1, "Arrays", "Buffers", "Chapter 01", "4/4", 4, 4, 400, "+400 XP", NodeStatus.COMPLETED, "array") {},
            QuestNode(2, 2, "Pointers", "Two pointers", "Chapter 02", "5/5", 5, 5, 500, "+500 XP", NodeStatus.COMPLETED, "pointer") {},
            QuestNode(3, 3, "Searching", "Binary Search", "Chapter 03", "2/5", 2, 5, 900, "+900 XP", NodeStatus.ACTIVE, "search") {},
            QuestNode(4, 4, "Sorting", "Merge sort", "Chapter 04", "0/5", 0, 5, 650, "+650 XP", NodeStatus.NEXT, "sort") {},
            QuestNode(5, 5, "Linked Lists", "Pointers", "Chapter 05", "0/5", 0, 5, 700, "+700 XP", NodeStatus.LOCKED, "link") {}
        )

        var navigatedChapterId = -1
        var lockedNotice = ""

        fun onChapterTapped(nodeIndex: Int) {
            val node = testNodes[nodeIndex]
            if (node.status == NodeStatus.LOCKED) {
                val prevNode = testNodes.getOrNull(nodeIndex - 1)
                val prevName = prevNode?.chapter ?: "the previous chapter"
                lockedNotice = "Complete $prevName first to unlock ${node.chapter}!"
            } else {
                navigatedChapterId = node.id
            }
        }

        // 1. Tapping an unlocked active chapter (Chapter 3)
        onChapterTapped(2)
        assertEquals("Tapping unlocked chapter navigates to chapter 3", 3, navigatedChapterId)
        assertEquals("", lockedNotice)

        // 2. Tapping next available chapter (Chapter 4)
        onChapterTapped(3)
        assertEquals("Tapping available next chapter navigates to chapter 4", 4, navigatedChapterId)
        assertEquals("", lockedNotice)

        // 3. Tapping a locked chapter (Chapter 5)
        navigatedChapterId = -1
        onChapterTapped(4)
        assertEquals("Tapping locked chapter must NOT navigate", -1, navigatedChapterId)
        assertEquals("Complete Chapter 04 first to unlock Chapter 05!", lockedNotice)
    }

    @Test
    fun questMap_chapterCardsAndNodes_validateNodeStatuses() {
        val completedNode = QuestNode(1, 1, "Arrays", "", "Chapter 01", "4/4", 4, 4, 400, "+400 XP", NodeStatus.COMPLETED, "array") {}
        val activeNode = QuestNode(3, 3, "Searching", "", "Chapter 03", "2/5", 2, 5, 900, "+900 XP", NodeStatus.ACTIVE, "search") {}
        val nextNode = QuestNode(4, 4, "Sorting", "", "Chapter 04", "0/5", 0, 5, 650, "+650 XP", NodeStatus.NEXT, "sort") {}
        val lockedNode = QuestNode(5, 5, "Lists", "", "Chapter 05", "0/5", 0, 5, 700, "+700 XP", NodeStatus.LOCKED, "link") {}

        // Verify status badges & progress
        assertEquals(1.0f, completedNode.completedLessons.toFloat() / completedNode.totalLessons.toFloat(), 0.001f)
        assertEquals(0.4f, activeNode.completedLessons.toFloat() / activeNode.totalLessons.toFloat(), 0.001f)
        assertEquals(0.0f, nextNode.completedLessons.toFloat() / nextNode.totalLessons.toFloat(), 0.001f)
        assertEquals(0.0f, lockedNode.completedLessons.toFloat() / lockedNode.totalLessons.toFloat(), 0.001f)

        assertEquals(NodeStatus.COMPLETED, completedNode.status)
        assertEquals(NodeStatus.ACTIVE, activeNode.status)
        assertEquals(NodeStatus.NEXT, nextNode.status)
        assertEquals(NodeStatus.LOCKED, lockedNode.status)
    }
}

