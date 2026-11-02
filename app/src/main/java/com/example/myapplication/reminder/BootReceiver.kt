package com.example.myapplication.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            val prefs = ReminderPreferences.getInstance(context)
            if (prefs.isReminderEnabled()) {
                ReminderScheduler.scheduleDailyReminder(context)
            }
            if (prefs.isStreakReminderEnabled()) {
                ReminderScheduler.scheduleStreakReminder(context)
            }
        }
    }
}
