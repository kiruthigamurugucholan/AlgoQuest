package com.example.myapplication.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action
        val prefs = ReminderPreferences.getInstance(context)

        when (action) {
            ReminderScheduler.ACTION_STREAK_REMINDER -> {
                if (prefs.isStreakReminderEnabled()) {
                    ReminderScheduler.showStreakNotification(context)
                    // Reschedule streak reminder for the next day
                    ReminderScheduler.scheduleStreakReminder(context)
                }
            }
            ReminderScheduler.ACTION_DAILY_REMINDER, null -> {
                if (prefs.isReminderEnabled()) {
                    ReminderScheduler.showReminderNotification(context)
                    // Reschedule daily reminder for the next day
                    ReminderScheduler.scheduleDailyReminder(context)
                }
            }
        }
    }
}
