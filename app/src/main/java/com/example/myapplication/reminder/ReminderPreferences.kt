package com.example.myapplication.reminder

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReminderPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "algoquest_reminder_prefs"

        // Daily Learning Reminder keys
        private const val KEY_REMINDER_ENABLED = "daily_learning_reminder_enabled"
        private const val KEY_REMINDER_HOUR = "daily_learning_reminder_hour"
        private const val KEY_REMINDER_MINUTE = "daily_learning_reminder_minute"

        // Streak Reminder keys
        private const val KEY_STREAK_REMINDER_ENABLED = "streak_reminder_enabled"
        private const val KEY_STREAK_REMINDER_HOUR = "streak_reminder_hour"
        private const val KEY_STREAK_REMINDER_MINUTE = "streak_reminder_minute"

        // Quiz Notifications keys
        private const val KEY_QUIZ_NOTIFICATIONS_ENABLED = "quiz_notifications_enabled"
        private const val KEY_NOTIFIED_QUIZ_IDS = "notified_quiz_ids"

        // Achievement Notifications keys
        private const val KEY_ACHIEVEMENT_NOTIFICATIONS_ENABLED = "achievement_notifications_enabled"
        private const val KEY_NOTIFIED_ACHIEVEMENT_IDS = "notified_achievement_ids"

        // Daily Learning Activity tracking & deduplication keys
        private const val KEY_LAST_COMPLETED_LEARNING_DATE = "last_completed_learning_date"
        private const val KEY_LAST_STREAK_NOTIFICATION_DATE = "last_streak_notification_sent_date"

        const val DEFAULT_HOUR = 20 // 8:00 PM
        const val DEFAULT_MINUTE = 0

        const val DEFAULT_STREAK_HOUR = 21 // 9:00 PM
        const val DEFAULT_STREAK_MINUTE = 0

        @Volatile
        private var instance: ReminderPreferences? = null

        fun getInstance(context: Context): ReminderPreferences {
            return instance ?: synchronized(this) {
                instance ?: ReminderPreferences(context.applicationContext).also { instance = it }
            }
        }

        fun getTodayDateString(): String {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return formatter.format(Date())
        }

        fun formatTime(hour: Int, minute: Int): String {
            val period = if (hour >= 12) "PM" else "AM"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            return String.format(Locale.US, "%02d:%02d %s", displayHour, minute, period)
        }
    }

    // --- Daily Learning Reminder ---

    fun isReminderEnabled(): Boolean {
        return prefs.getBoolean(KEY_REMINDER_ENABLED, false)
    }

    fun setReminderEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_REMINDER_ENABLED, enabled).apply()
    }

    fun getReminderHour(): Int {
        return prefs.getInt(KEY_REMINDER_HOUR, DEFAULT_HOUR)
    }

    fun getReminderMinute(): Int {
        return prefs.getInt(KEY_REMINDER_MINUTE, DEFAULT_MINUTE)
    }

    fun setReminderTime(hour: Int, minute: Int) {
        prefs.edit()
            .putInt(KEY_REMINDER_HOUR, hour.coerceIn(0, 23))
            .putInt(KEY_REMINDER_MINUTE, minute.coerceIn(0, 59))
            .apply()
    }

    fun getFormattedTime(): String {
        return formatTime(getReminderHour(), getReminderMinute())
    }

    // --- Streak Reminder ---

    fun isStreakReminderEnabled(): Boolean {
        return prefs.getBoolean(KEY_STREAK_REMINDER_ENABLED, false)
    }

    fun setStreakReminderEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_STREAK_REMINDER_ENABLED, enabled).apply()
    }

    fun getStreakReminderHour(): Int {
        return prefs.getInt(KEY_STREAK_REMINDER_HOUR, DEFAULT_STREAK_HOUR)
    }

    fun getStreakReminderMinute(): Int {
        return prefs.getInt(KEY_STREAK_REMINDER_MINUTE, DEFAULT_STREAK_MINUTE)
    }

    fun setStreakReminderTime(hour: Int, minute: Int) {
        prefs.edit()
            .putInt(KEY_STREAK_REMINDER_HOUR, hour.coerceIn(0, 23))
            .putInt(KEY_STREAK_REMINDER_MINUTE, minute.coerceIn(0, 59))
            .apply()
    }

    fun getFormattedStreakTime(): String {
        return formatTime(getStreakReminderHour(), getStreakReminderMinute())
    }

    // --- Quiz Notifications ---

    fun isQuizNotificationEnabled(): Boolean {
        return prefs.getBoolean(KEY_QUIZ_NOTIFICATIONS_ENABLED, true)
    }

    fun setQuizNotificationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_QUIZ_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    fun hasQuizNotificationBeenSent(quizId: Int): Boolean {
        val notifiedSet = prefs.getStringSet(KEY_NOTIFIED_QUIZ_IDS, emptySet()) ?: emptySet()
        return notifiedSet.contains(quizId.toString())
    }

    fun markQuizNotificationSent(quizId: Int) {
        val existing = prefs.getStringSet(KEY_NOTIFIED_QUIZ_IDS, emptySet()) ?: emptySet()
        val updated = existing.toMutableSet().apply { add(quizId.toString()) }
        prefs.edit().putStringSet(KEY_NOTIFIED_QUIZ_IDS, updated).apply()
    }

    fun clearNotifiedQuizIds() {
        prefs.edit().remove(KEY_NOTIFIED_QUIZ_IDS).apply()
    }

    // --- Achievement Notifications ---

    fun isAchievementNotificationEnabled(): Boolean {
        return prefs.getBoolean(KEY_ACHIEVEMENT_NOTIFICATIONS_ENABLED, true)
    }

    fun setAchievementNotificationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ACHIEVEMENT_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    fun hasAchievementBeenNotified(achievementKey: String): Boolean {
        val notifiedSet = prefs.getStringSet(KEY_NOTIFIED_ACHIEVEMENT_IDS, emptySet()) ?: emptySet()
        return notifiedSet.contains(achievementKey)
    }

    fun hasAchievementBeenNotified(achievementId: Int): Boolean {
        return hasAchievementBeenNotified(achievementId.toString())
    }

    fun markAchievementNotified(achievementKey: String) {
        val existing = prefs.getStringSet(KEY_NOTIFIED_ACHIEVEMENT_IDS, emptySet()) ?: emptySet()
        val updated = existing.toMutableSet().apply { add(achievementKey) }
        prefs.edit().putStringSet(KEY_NOTIFIED_ACHIEVEMENT_IDS, updated).apply()
    }

    fun markAchievementNotified(achievementId: Int) {
        markAchievementNotified(achievementId.toString())
    }

    fun clearNotifiedAchievements() {
        prefs.edit().remove(KEY_NOTIFIED_ACHIEVEMENT_IDS).apply()
    }

    // --- Daily Learning Activity Tracking ---

    fun markDailyActivityCompleted(date: String = getTodayDateString()) {
        prefs.edit().putString(KEY_LAST_COMPLETED_LEARNING_DATE, date).apply()
    }

    fun clearDailyActivityCompleted() {
        prefs.edit().remove(KEY_LAST_COMPLETED_LEARNING_DATE).apply()
    }

    fun isDailyLearningCompletedToday(today: String = getTodayDateString()): Boolean {
        val lastDate = prefs.getString(KEY_LAST_COMPLETED_LEARNING_DATE, null)
        return lastDate == today
    }

    // --- Duplicate Notification Prevention ---

    fun hasStreakNotificationBeenSentToday(today: String = getTodayDateString()): Boolean {
        val lastSent = prefs.getString(KEY_LAST_STREAK_NOTIFICATION_DATE, null)
        return lastSent == today
    }

    fun markStreakNotificationSentToday(today: String = getTodayDateString()) {
        prefs.edit().putString(KEY_LAST_STREAK_NOTIFICATION_DATE, today).apply()
    }

    fun clearStreakNotificationSentToday() {
        prefs.edit().remove(KEY_LAST_STREAK_NOTIFICATION_DATE).apply()
    }
}
