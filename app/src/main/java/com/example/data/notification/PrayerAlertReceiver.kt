package com.example.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.mosque.MosqueModeScheduler
import com.example.data.preference.AppPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Reliable BroadcastReceiver for exact/inexact prayer time alarms and system lifecycle events
 * (boot, app update, manual clock changes, timezone switches, exact alarm permission grants).
 */
class PrayerAlertReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_PRAYER_ALARM = "com.example.action.PRAYER_ALARM"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (action) {
                    ACTION_PRAYER_ALARM -> {
                        val prayerName = intent.getStringExtra(PrayerNotificationWorker.KEY_PRAYER_NAME) ?: "FAJR"
                        val prayerArabic = intent.getStringExtra(PrayerNotificationWorker.KEY_PRAYER_ARABIC) ?: "الصلاة"
                        val prayerBengali = intent.getStringExtra(PrayerNotificationWorker.KEY_PRAYER_BENGALI) ?: prayerName
                        val formattedTime = intent.getStringExtra(PrayerNotificationWorker.KEY_FORMATTED_TIME) ?: ""
                        val isPreAlert = intent.getBooleanExtra(PrayerNotificationWorker.KEY_IS_PRE_ALERT, false)
                        val preMinutes = intent.getIntExtra(PrayerNotificationWorker.KEY_PRE_MINUTES, 15)

                        PrayerNotificationWorker.createNotificationChannel(context)
                        PrayerNotificationWorker.showPrayerNotification(
                            context = context,
                            prayerName = prayerName,
                            prayerArabic = prayerArabic,
                            prayerBengali = prayerBengali,
                            formattedTime = formattedTime,
                            isPreAlert = isPreAlert,
                            preMinutes = preMinutes
                        )
                        PrayerAlertScheduler.schedulePrayerAlerts(context)
                    }

                    Intent.ACTION_BOOT_COMPLETED,
                    Intent.ACTION_MY_PACKAGE_REPLACED,
                    Intent.ACTION_TIME_CHANGED,
                    Intent.ACTION_TIMEZONE_CHANGED,
                    "android.app.action.SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED" -> {
                        val prefs = AppPreferences(context)
                        if (prefs.prayerAlertsEnabled.first() || prefs.prePrayerAlertEnabled.first()) {
                            PrayerAlertScheduler.schedulePrayerAlerts(context)
                        }
                        if (prefs.mosqueModeEnabled.first()) {
                            MosqueModeScheduler.scheduleAllMosquePrayers(context)
                        }
                    }
                }
            } catch (_: Exception) {
            } finally {
                pendingResult.finish()
            }
        }
    }
}
