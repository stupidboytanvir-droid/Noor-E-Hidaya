package com.example.data.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.*
import com.example.data.model.CalculationMethod
import com.example.data.model.JuristicMethod
import com.example.data.model.PrayerCalculator
import com.example.data.model.PrayerName
import com.example.data.preference.AppPreferences
import com.example.data.util.LocaleHelper
import com.example.data.util.TimeFormatUtils
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.concurrent.TimeUnit

object PrayerAlertScheduler {

    private val DAILY_PRAYERS = listOf(
        PrayerName.FAJR,
        PrayerName.DHUHR,
        PrayerName.ASR,
        PrayerName.MAGHRIB,
        PrayerName.ISHA
    )

    private fun getWorkManager(context: Context): WorkManager {
        return try {
            WorkManager.getInstance(context)
        } catch (_: IllegalStateException) {
            try {
                val config = Configuration.Builder().build()
                WorkManager.initialize(context, config)
                WorkManager.getInstance(context)
            } catch (_: Exception) {
                WorkManager.getInstance(context)
            }
        }
    }

    private fun getAlarmManager(context: Context): AlarmManager? {
        return context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    }

    suspend fun schedulePrayerAlerts(context: Context) {
        val prefs = AppPreferences(context)
        val onTimeEnabled = prefs.prayerAlertsEnabled.first()
        val preAlertEnabled = prefs.prePrayerAlertEnabled.first()
        val preMinutes = prefs.prePrayerAlertMinutes.first().coerceIn(5, 45)

        if (!onTimeEnabled && !preAlertEnabled) {
            cancelAllAlerts(context)
            return
        }

        val calcMethodStr = prefs.calculationMethod.first()
        val juristicStr = prefs.juristicMethod.first()
        val lat = prefs.selectedDistrictLat.first()
        val lng = prefs.selectedDistrictLng.first()
        val configs = prefs.getAllPrayerMosqueConfigs().first()
        val uiLang = LocaleHelper.getUiLanguageSync(context)

        val method = try {
            CalculationMethod.valueOf(calcMethodStr)
        } catch (_: Exception) {
            CalculationMethod.MUSLIM_WORLD_LEAGUE
        }

        val juristic = JuristicMethod.fromString(juristicStr)

        val workManager = getWorkManager(context)
        val alarmManager = getAlarmManager(context)
        val now = System.currentTimeMillis()
        val is24Hour = TimeFormatUtils.is24HourFormat(context)
        val tzOffset = TimeFormatUtils.getLocalTimeZoneOffsetHours()

        val todayCal = Calendar.getInstance()
        val rawTodaySchedule = PrayerCalculator.calculateSchedule(
            latitude = lat,
            longitude = lng,
            timeZone = tzOffset,
            calendar = todayCal,
            method = method,
            juristic = juristic,
            contentLang = uiLang,
            is24Hour = is24Hour
        )
        val todaySchedule = PrayerCalculator.applyMosqueConfigs(
            schedule = rawTodaySchedule,
            configs = configs,
            calendar = todayCal,
            is24Hour = is24Hour,
            now = now
        )

        val tomorrowCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
        val rawTomorrowSchedule = PrayerCalculator.calculateSchedule(
            latitude = lat,
            longitude = lng,
            timeZone = TimeFormatUtils.getLocalTimeZoneOffsetHours(tomorrowCal.timeInMillis),
            calendar = tomorrowCal,
            method = method,
            juristic = juristic,
            contentLang = uiLang,
            is24Hour = is24Hour
        )
        val tomorrowSchedule = PrayerCalculator.applyMosqueConfigs(
            schedule = rawTomorrowSchedule,
            configs = configs,
            calendar = tomorrowCal,
            is24Hour = is24Hour,
            now = now
        )

        for (prayer in DAILY_PRAYERS) {
            val todayItem = todaySchedule.items.find { it.prayer == prayer }
            val tomorrowItem = tomorrowSchedule.items.find { it.prayer == prayer }

            // 1. On-time Azan / Waqt alert
            if (onTimeEnabled) {
                val targetItem = if (todayItem != null && todayItem.timeMillis > now) {
                    todayItem
                } else {
                    tomorrowItem
                }

                if (targetItem != null) {
                    val delayMillis = maxOf(1000L, targetItem.timeMillis - now)

                    val inputData = workDataOf(
                        PrayerNotificationWorker.KEY_PRAYER_NAME to prayer.name,
                        PrayerNotificationWorker.KEY_PRAYER_ARABIC to prayer.arabicName,
                        PrayerNotificationWorker.KEY_PRAYER_BENGALI to prayer.bengaliName,
                        PrayerNotificationWorker.KEY_FORMATTED_TIME to targetItem.formattedTime,
                        PrayerNotificationWorker.KEY_IS_PRE_ALERT to false
                    )

                    val workRequest = OneTimeWorkRequestBuilder<PrayerNotificationWorker>()
                        .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                        .setInputData(inputData)
                        .addTag("prayer_alert")
                        .addTag("prayer_${prayer.name.lowercase()}")
                        .build()

                    workManager.enqueueUniqueWork(
                        "prayer_alert_${prayer.name.lowercase()}",
                        ExistingWorkPolicy.REPLACE,
                        workRequest
                    )

                    scheduleExactPrayerAlarm(
                        context = context,
                        alarmManager = alarmManager,
                        requestCode = 2000 + prayer.ordinal * 2,
                        triggerAtMillis = targetItem.timeMillis,
                        prayer = prayer,
                        formattedTime = targetItem.formattedTime,
                        isPreAlert = false,
                        preMinutes = preMinutes
                    )
                }
            } else {
                workManager.cancelUniqueWork("prayer_alert_${prayer.name.lowercase()}")
                cancelAlarm(context, alarmManager, 2000 + prayer.ordinal * 2)
            }

            // 2. Pre-Prayer Wudu & Preparation Reminder (10-15 min BEFORE Jamat time configured in Mosque Mode / Home)
            if (preAlertEnabled) {
                val prayerCfg = configs[prayer]
                val todayPre = todayItem?.let {
                    resolvePrePrayerJamatTrigger(it, prayerCfg, preMinutes, is24Hour)
                }
                val tomorrowPre = tomorrowItem?.let {
                    resolvePrePrayerJamatTrigger(it, prayerCfg, preMinutes, is24Hour)
                }

                val selectedPre = when {
                    todayPre != null && todayPre.first > now -> todayPre
                    else -> tomorrowPre
                }

                if (selectedPre != null) {
                    val (triggerAt, formattedJamat) = selectedPre
                    val delayMillis = maxOf(1000L, triggerAt - now)

                    val preInputData = workDataOf(
                        PrayerNotificationWorker.KEY_PRAYER_NAME to prayer.name,
                        PrayerNotificationWorker.KEY_PRAYER_ARABIC to prayer.arabicName,
                        PrayerNotificationWorker.KEY_PRAYER_BENGALI to prayer.bengaliName,
                        PrayerNotificationWorker.KEY_FORMATTED_TIME to formattedJamat,
                        PrayerNotificationWorker.KEY_IS_PRE_ALERT to true,
                        PrayerNotificationWorker.KEY_PRE_MINUTES to preMinutes
                    )

                    val preWorkRequest = OneTimeWorkRequestBuilder<PrayerNotificationWorker>()
                        .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                        .setInputData(preInputData)
                        .addTag("prayer_alert")
                        .addTag("pre_prayer_${prayer.name.lowercase()}")
                        .build()

                    workManager.enqueueUniqueWork(
                        "pre_prayer_alert_${prayer.name.lowercase()}",
                        ExistingWorkPolicy.REPLACE,
                        preWorkRequest
                    )

                    scheduleExactPrayerAlarm(
                        context = context,
                        alarmManager = alarmManager,
                        requestCode = 2000 + prayer.ordinal * 2 + 1,
                        triggerAtMillis = triggerAt,
                        prayer = prayer,
                        formattedTime = formattedJamat,
                        isPreAlert = true,
                        preMinutes = preMinutes
                    )
                }
            } else {
                workManager.cancelUniqueWork("pre_prayer_alert_${prayer.name.lowercase()}")
                cancelAlarm(context, alarmManager, 2000 + prayer.ordinal * 2 + 1)
            }
        }

        // Schedule periodic daily rescheduler
        scheduleDailyRescheduler(context)
    }

    private fun scheduleExactPrayerAlarm(
        context: Context,
        alarmManager: AlarmManager?,
        requestCode: Int,
        triggerAtMillis: Long,
        prayer: PrayerName,
        formattedTime: String,
        isPreAlert: Boolean,
        preMinutes: Int
    ) {
        if (alarmManager == null || triggerAtMillis <= System.currentTimeMillis()) return
        val intent = Intent(context, PrayerAlertReceiver::class.java).apply {
            action = PrayerAlertReceiver.ACTION_PRAYER_ALARM
            putExtra(PrayerNotificationWorker.KEY_PRAYER_NAME, prayer.name)
            putExtra(PrayerNotificationWorker.KEY_PRAYER_ARABIC, prayer.arabicName)
            putExtra(PrayerNotificationWorker.KEY_PRAYER_BENGALI, prayer.bengaliName)
            putExtra(PrayerNotificationWorker.KEY_FORMATTED_TIME, formattedTime)
            putExtra(PrayerNotificationWorker.KEY_IS_PRE_ALERT, isPreAlert)
            putExtra(PrayerNotificationWorker.KEY_PRE_MINUTES, preMinutes)
        }
        val pending = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
            }
        } catch (_: Exception) {
            try {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
            } catch (_: Exception) {}
        }
    }

    private fun cancelAlarm(context: Context, alarmManager: AlarmManager?, requestCode: Int) {
        if (alarmManager == null) return
        val intent = Intent(context, PrayerAlertReceiver::class.java).apply {
            action = PrayerAlertReceiver.ACTION_PRAYER_ALARM
        }
        val pending = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pending != null) {
            alarmManager.cancel(pending)
            pending.cancel()
        }
    }

    private fun scheduleDailyRescheduler(context: Context) {
        val workManager = getWorkManager(context)
        val periodicRequest = PeriodicWorkRequestBuilder<PrayerRescheduleWorker>(
            24, TimeUnit.HOURS,
            1, TimeUnit.HOURS
        )
            .addTag("prayer_daily_rescheduler")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "prayer_daily_rescheduler_work",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
    }

    fun cancelAllAlerts(context: Context) {
        val workManager = getWorkManager(context)
        val alarmManager = getAlarmManager(context)
        for (prayer in DAILY_PRAYERS) {
            workManager.cancelUniqueWork("prayer_alert_${prayer.name.lowercase()}")
            workManager.cancelUniqueWork("pre_prayer_alert_${prayer.name.lowercase()}")
            cancelAlarm(context, alarmManager, 2000 + prayer.ordinal * 2)
            cancelAlarm(context, alarmManager, 2000 + prayer.ordinal * 2 + 1)
        }
        workManager.cancelUniqueWork("prayer_daily_rescheduler_work")
    }

    /**
     * Computes the exact pre-prayer preparation alert trigger relative to the Jama'at time
     * configured in Mosque Mode / Home screen (NOT Waqt start time).
     */
    fun resolvePrePrayerJamatTrigger(
        item: com.example.data.model.PrayerTimeItem,
        config: com.example.data.mosque.PrayerMosqueConfig?,
        preMinutes: Int,
        is24Hour: Boolean = false
    ): Pair<Long, String> {
        val safePreMins = preMinutes.coerceIn(5, 45)
        val jamatMillis = if (item.jamatTimeMillis > 0L) {
            item.jamatTimeMillis
        } else {
            val defaultOffset = config?.defaultJamatOffsetMinutes
                ?: com.example.data.mosque.PrayerMosqueConfig.defaultJamatOffsetFor(item.prayer)
            item.timeMillis + defaultOffset * 60_000L
        }
        val triggerAt = jamatMillis - safePreMins * 60_000L
        val formattedJamat = item.formattedJamatTime.takeIf { it.isNotBlank() }
            ?: TimeFormatUtils.formatEpochMillis(jamatMillis, is24Hour)
        return triggerAt to formattedJamat
    }

    fun triggerTestAlert(
        context: Context,
        prayer: PrayerName = PrayerName.FAJR,
        isPreAlert: Boolean = false,
        preMinutes: Int = 15
    ) {
        val workManager = getWorkManager(context)
        val uiLang = LocaleHelper.getUiLanguageSync(context)
        val isBn = com.example.data.util.SupportedLanguage.normalizeCode(uiLang) == "bn"
        val timeText = if (isPreAlert) {
            if (isBn) "$preMinutes মি. পর জামাত" else "Jama'at in $preMinutes min"
        } else {
            if (isBn) "এখনই ওয়াক্ত" else "Now"
        }
        val inputData = workDataOf(
            PrayerNotificationWorker.KEY_PRAYER_NAME to prayer.name,
            PrayerNotificationWorker.KEY_PRAYER_ARABIC to prayer.arabicName,
            PrayerNotificationWorker.KEY_PRAYER_BENGALI to prayer.bengaliName,
            PrayerNotificationWorker.KEY_FORMATTED_TIME to timeText,
            PrayerNotificationWorker.KEY_IS_PRE_ALERT to isPreAlert,
            PrayerNotificationWorker.KEY_PRE_MINUTES to preMinutes,
            PrayerNotificationWorker.KEY_IS_TEST to true
        )

        val testRequest = OneTimeWorkRequestBuilder<PrayerNotificationWorker>()
            .setInitialDelay(1, TimeUnit.SECONDS)
            .setInputData(inputData)
            .build()

        workManager.enqueue(testRequest)
    }
}
