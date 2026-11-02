package com.example.myapplication.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import java.util.Calendar

object ReminderScheduler {
    const val CHANNEL_ID = "algoquest_daily_learning_reminder"
    const val CHANNEL_NAME = "Daily Learning Reminders"

    // Daily Learning Reminder Constants
    const val NOTIFICATION_ID = 4001
    const val ALARM_REQUEST_CODE = 4002
    const val NOTIFICATION_TITLE = "Time for AlgoQuest!"
    const val NOTIFICATION_MESSAGE = "Spend a few minutes practising DSA today."

    // Streak Reminder Constants
    const val STREAK_ALARM_REQUEST_CODE = 4003
    const val STREAK_NOTIFICATION_ID = 4004
    const val STREAK_NOTIFICATION_TITLE = "Protect Your Learning Streak!"
    const val STREAK_NOTIFICATION_MESSAGE = "Complete today's DSA practice to keep your streak alive."

    // Quiz Notification Constants
    const val QUIZ_NOTIFICATION_ID = 4005
    const val QUIZ_NOTIFICATION_TITLE = "New Quiz Available!"
    const val QUIZ_NOTIFICATION_MESSAGE = "A new DSA challenge is waiting for you. Test your knowledge!"

    // Achievement Notification Constants
    const val ACHIEVEMENT_CHANNEL_ID = "algoquest_achievements"
    const val ACHIEVEMENT_CHANNEL_NAME = "Achievements & Badges"
    const val ACHIEVEMENT_NOTIFICATION_ID = 4006
    const val DEFAULT_ACHIEVEMENT_TITLE = "Achievement Unlocked!"
    const val DEFAULT_ACHIEVEMENT_MESSAGE = "Congratulations! You earned a new badge in AlgoQuest."
    const val EXTRA_ACHIEVEMENT_ID = "TARGET_ACHIEVEMENT_ID"

    const val EXTRA_DESTINATION_SCREEN = "DESTINATION_SCREEN"
    const val EXTRA_QUIZ_LESSON_ID = "TARGET_QUIZ_LESSON_ID"

    const val ACTION_DAILY_REMINDER = "com.example.myapplication.ACTION_DAILY_REMINDER"
    const val ACTION_STREAK_REMINDER = "com.example.myapplication.ACTION_STREAK_REMINDER"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

            val reminderChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily learning, streak protection, and quiz notifications."
                enableLights(true)
                enableVibration(true)
            }
            notificationManager?.createNotificationChannel(reminderChannel)

            val achievementChannel = NotificationChannel(
                ACHIEVEMENT_CHANNEL_ID,
                ACHIEVEMENT_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for unlocked achievement badges, learning milestones, and accomplishments."
                enableLights(true)
                enableVibration(true)
            }
            notificationManager?.createNotificationChannel(achievementChannel)
        }
    }

    fun calculateNextTriggerMillis(hour: Int, minute: Int, nowMillis: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= nowMillis) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return calendar.timeInMillis
    }

    // ==========================================
    // 1. Daily Learning Reminder
    // ==========================================

    fun scheduleDailyReminder(context: Context) {
        val prefs = ReminderPreferences.getInstance(context)
        if (!prefs.isReminderEnabled()) {
            cancelDailyReminder(context)
            return
        }

        createNotificationChannel(context)

        val hour = prefs.getReminderHour()
        val minute = prefs.getReminderMinute()
        val triggerAtMillis = calculateNextTriggerMillis(hour, minute)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            action = ACTION_DAILY_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        scheduleAlarm(alarmManager, triggerAtMillis, pendingIntent)
    }

    fun cancelDailyReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            action = ACTION_DAILY_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun showReminderNotification(context: Context) {
        createNotificationChannel(context)

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText(NOTIFICATION_MESSAGE)
            .setStyle(NotificationCompat.BigTextStyle().bigText(NOTIFICATION_MESSAGE))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        } catch (_: SecurityException) {
            // Handled if permission is revoked
        }
    }

    // ==========================================
    // 2. Streak Reminder
    // ==========================================

    fun scheduleStreakReminder(context: Context) {
        val prefs = ReminderPreferences.getInstance(context)
        if (!prefs.isStreakReminderEnabled()) {
            cancelStreakReminder(context)
            return
        }

        createNotificationChannel(context)

        val hour = prefs.getStreakReminderHour()
        val minute = prefs.getStreakReminderMinute()
        val triggerAtMillis = calculateNextTriggerMillis(hour, minute)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            action = ACTION_STREAK_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            STREAK_ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        scheduleAlarm(alarmManager, triggerAtMillis, pendingIntent)
    }

    fun cancelStreakReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            action = ACTION_STREAK_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            STREAK_ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    /**
     * Dispatches the Streak Reminder notification ONLY if the user has not completed
     * their daily learning activity today and has not already received a streak alert today.
     */
    fun showStreakNotification(context: Context, forceIfTest: Boolean = false): StreakNotificationResult {
        val prefs = ReminderPreferences.getInstance(context)
        val today = ReminderPreferences.getTodayDateString()

        if (!forceIfTest) {
            if (prefs.isDailyLearningCompletedToday(today)) {
                return StreakNotificationResult.SUPPRESSED_PRACTICE_COMPLETED
            }

            if (prefs.hasStreakNotificationBeenSentToday(today)) {
                return StreakNotificationResult.SUPPRESSED_ALREADY_SENT_TODAY
            }
        }

        createNotificationChannel(context)

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(STREAK_NOTIFICATION_TITLE)
            .setContentText(STREAK_NOTIFICATION_MESSAGE)
            .setStyle(NotificationCompat.BigTextStyle().bigText(STREAK_NOTIFICATION_MESSAGE))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        return try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(STREAK_NOTIFICATION_ID, builder.build())
            prefs.markStreakNotificationSentToday(today)
            StreakNotificationResult.SENT
        } catch (_: SecurityException) {
            StreakNotificationResult.PERMISSION_DENIED
        }
    }

    // ==========================================
    // 3. Quiz Notifications
    // ==========================================

    /**
     * Dispatches the "New Quiz Available!" notification.
     * Tapping the notification opens MainActivity directly to QuizScreen (screen 13) with quizLessonId.
     *
     * @param quizLessonId The lesson ID associated with the available quiz (e.g. 5)
     * @param isTest Whether this is a manual test dispatch (bypasses duplicate check)
     */
    fun showQuizNotification(
        context: Context,
        quizLessonId: Int = 5,
        isTest: Boolean = false
    ): QuizNotificationResult {
        val prefs = ReminderPreferences.getInstance(context)

        if (!prefs.isQuizNotificationEnabled()) {
            return QuizNotificationResult.DISABLED
        }

        if (!isTest && prefs.hasQuizNotificationBeenSent(quizLessonId)) {
            return QuizNotificationResult.ALREADY_NOTIFIED
        }

        createNotificationChannel(context)

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_DESTINATION_SCREEN, 13) // Screen 13 = QuizScreen
            putExtra(EXTRA_QUIZ_LESSON_ID, quizLessonId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            2,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(QUIZ_NOTIFICATION_TITLE)
            .setContentText(QUIZ_NOTIFICATION_MESSAGE)
            .setStyle(NotificationCompat.BigTextStyle().bigText(QUIZ_NOTIFICATION_MESSAGE))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        return try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(QUIZ_NOTIFICATION_ID, builder.build())
            if (!isTest) {
                prefs.markQuizNotificationSent(quizLessonId)
            }
            QuizNotificationResult.SENT
        } catch (_: SecurityException) {
            QuizNotificationResult.PERMISSION_DENIED
        }
    }

    // ==========================================
    // 4. Achievement Notifications
    // ==========================================

    /**
     * Dispatches an Achievement or Learning Milestone notification.
     * Tapping the notification opens MainActivity directly to Explorer Profile (screen 12).
     *
     * @param title Title of the notification (e.g. "Achievement Unlocked!" or "Learning Milestone Reached!")
     * @param message Detailed celebration message customized for the achievement
     * @param achievementKey Unique key (e.g. "ach_2", "milestone_level_2") for deduplication
     * @param isTest Whether this is a manual test dispatch (bypasses duplicate check)
     */
    fun showAchievementNotification(
        context: Context,
        title: String = DEFAULT_ACHIEVEMENT_TITLE,
        message: String = DEFAULT_ACHIEVEMENT_MESSAGE,
        achievementKey: String = "test_achievement",
        isTest: Boolean = false
    ): AchievementNotificationResult {
        val prefs = ReminderPreferences.getInstance(context)

        if (!prefs.isAchievementNotificationEnabled()) {
            return AchievementNotificationResult.DISABLED
        }

        if (!isTest && prefs.hasAchievementBeenNotified(achievementKey)) {
            return AchievementNotificationResult.ALREADY_NOTIFIED
        }

        createNotificationChannel(context)

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_DESTINATION_SCREEN, 12) // Screen 12 = Explorer Profile
            putExtra(EXTRA_ACHIEVEMENT_ID, achievementKey)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            3,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, ACHIEVEMENT_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        return try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(ACHIEVEMENT_NOTIFICATION_ID, builder.build())
            if (!isTest) {
                prefs.markAchievementNotified(achievementKey)
            }
            AchievementNotificationResult.SENT
        } catch (_: SecurityException) {
            AchievementNotificationResult.PERMISSION_DENIED
        }
    }

    /**
     * Inspects ProfileData for newly unlocked achievement badges or milestones.
     * Automatically dispatches notifications for each newly unlocked item.
     */
    fun checkAndNotifyAchievements(
        context: Context,
        profile: com.example.myapplication.data.model.response.ProfileData
    ) {
        val prefs = ReminderPreferences.getInstance(context)
        if (!prefs.isAchievementNotificationEnabled()) return

        // 1. Check Unlocked Achievement Badges
        profile.achievements.forEach { ach ->
            if (ach.isUnlocked) {
                val key = "ach_${ach.id}"
                if (!prefs.hasAchievementBeenNotified(key)) {
                    val emoji = if (ach.iconEmoji.isNotBlank()) " ${ach.iconEmoji}" else ""
                    val message = "Congratulations! You earned '${ach.title}'$emoji - ${ach.description}"
                    showAchievementNotification(
                        context = context,
                        title = "Achievement Unlocked!",
                        message = message,
                        achievementKey = key,
                        isTest = false
                    )
                }
            }
        }

        // 2. Check Learning Milestones (Level, Solved Lessons, Streak)
        val user = profile.user
        if (user.level > 1) {
            val levelKey = "milestone_level_${user.level}"
            if (!prefs.hasAchievementBeenNotified(levelKey)) {
                showAchievementNotification(
                    context = context,
                    title = "Learning Milestone Reached!",
                    message = "Level Up! You reached Level ${user.level} as an Explorer. Keep conquering algorithms!",
                    achievementKey = levelKey,
                    isTest = false
                )
            }
        }

        if (user.solvedLessons >= 5) {
            val milestoneCount = (user.solvedLessons / 5) * 5
            val solvedKey = "milestone_solved_$milestoneCount"
            if (!prefs.hasAchievementBeenNotified(solvedKey)) {
                showAchievementNotification(
                    context = context,
                    title = "Learning Accomplishment!",
                    message = "Milestone achieved! You have completed $milestoneCount DSA lessons.",
                    achievementKey = solvedKey,
                    isTest = false
                )
            }
        }

        if (user.streak >= 7) {
            val streakKey = "milestone_streak_7"
            if (!prefs.hasAchievementBeenNotified(streakKey)) {
                showAchievementNotification(
                    context = context,
                    title = "Streak Milestone!",
                    message = "Incredible dedication! You maintained a 7-day learning streak in AlgoQuest.",
                    achievementKey = streakKey,
                    isTest = false
                )
            }
        }
    }

    private fun scheduleAlarm(alarmManager: AlarmManager, triggerAtMillis: Long, pendingIntent: PendingIntent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } catch (_: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }
}

enum class StreakNotificationResult {
    SENT,
    SUPPRESSED_PRACTICE_COMPLETED,
    SUPPRESSED_ALREADY_SENT_TODAY,
    PERMISSION_DENIED
}

enum class QuizNotificationResult {
    SENT,
    DISABLED,
    ALREADY_NOTIFIED,
    PERMISSION_DENIED
}

enum class AchievementNotificationResult {
    SENT,
    DISABLED,
    ALREADY_NOTIFIED,
    PERMISSION_DENIED
}
