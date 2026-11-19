package com.example.data.mosque

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
import com.example.data.model.PrayerSchedule
import com.example.data.preference.AppPreferences
import com.example.data.util.TimeFormatUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.TimeUnit

object MosqueModeScheduler {

    val PRAYERS = listOf(
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

    fun canScheduleExactAlarms(context: Context): Boolean {
        val am = getAlarmManager(context) ?: return false
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            am.canScheduleExactAlarms()
        } else {
            true
        }
    }

    fun openExactAlarmSettingsIntent(context: Context): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = android.net.Uri.parse("package:${context.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        } else {
            Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = android.net.Uri.parse("package:${context.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
    }

    suspend fun scheduleAllMosquePrayers(context: Context) {
        withContext(Dispatchers.IO) {
            val prefs = AppPreferences(context)
            val isEnabled = prefs.mosqueModeEnabled.first()
            if (!isEnabled) {
                cancelMosqueSchedule(context)
                return@withContext
            }

            val calcMethodStr = prefs.calculationMethod.first()
            val juristicStr = prefs.juristicMethod.first()
            val districtLat = prefs.selectedDistrictLat.first()
            val districtLng = prefs.selectedDistrictLng.first()
            val is24Hour = TimeFormatUtils.is24HourFormat(context)
            val tzOffset = TimeFormatUtils.getLocalTimeZoneOffsetHours()

            val method = try {
                CalculationMethod.valueOf(calcMethodStr)
            } catch (_: Exception) {
                CalculationMethod.MUSLIM_WORLD_LEAGUE
            }

            val juristic = JuristicMethod.fromString(juristicStr)
            val configs = prefs.getAllPrayerMosqueConfigs().first()

            val now = System.currentTimeMillis()
            val todayCal = Calendar.getInstance()
            val rawTodaySchedule = PrayerCalculator.calculateSchedule(
                latitude = districtLat,
                longitude = districtLng,
                timeZone = tzOffset,
                calendar = todayCal,
                method = method,
                juristic = juristic,
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
                latitude = districtLat,
                longitude = districtLng,
                timeZone = TimeFormatUtils.getLocalTimeZoneOffsetHours(tomorrowCal.timeInMillis),
                calendar = tomorrowCal,
                method = method,
                juristic = juristic,
                is24Hour = is24Hour
            )
            val tomorrowSchedule = PrayerCalculator.applyMosqueConfigs(
                schedule = rawTomorrowSchedule,
                configs = configs,
                calendar = tomorrowCal,
                is24Hour = is24Hour,
                now = now
            )

            val alarmManager = getAlarmManager(context)
            val workManager = getWorkManager(context)

            var activePrayerNow: Pair<PrayerName, Long>? = null

            for (prayer in PRAYERS) {
                val config = configs[prayer] ?: PrayerMosqueConfig.defaultFor(prayer)
                if (!config.enabled) {
                    cancelSinglePrayer(context, prayer)
                    continue
                }

                val (silenceTarget, restoreTarget) = computePrayerWindow(
                    prayer = prayer,
                    config = config,
                    todaySchedule = todaySchedule,
                    tomorrowSchedule = tomorrowSchedule,
                    now = now
                )

                if (now in silenceTarget until restoreTarget) {
                    activePrayerNow = prayer to restoreTarget
                }

                // 1. AlarmManager exact scheduling
                scheduleAlarm(
                    context = context,
                    alarmManager = alarmManager,
                    prayer = prayer,
                    silenceTarget = silenceTarget,
                    restoreTarget = restoreTarget,
                    now = now
                )

                // 2. WorkManager backup scheduling
                scheduleWorkBackup(
                    context = context,
                    workManager = workManager,
                    prayer = prayer,
                    silenceTarget = silenceTarget,
                    restoreTarget = restoreTarget,
                    now = now
                )
            }

            // 3. Enforce current window state: activate if inside Jamat window, OR auto-restore if expired
            if (activePrayerNow != null) {
                val (activePrayer, activeRestoreTarget) = activePrayerNow
                val uiLang = com.example.data.util.LocaleHelper.getUiLanguageSync(context)
                val localizedPrayer = com.example.data.util.SupportedLanguage.localizedPrayerName(activePrayer.name, uiLang)
                if (!MosqueModeManager.isCurrentlySilenced(context)) {
                    MosqueModeManager.activateMosqueMode(
                        context = context,
                        prayerName = localizedPrayer,
                        restoreAtMillis = activeRestoreTarget
                    )
                } else {
                    // Keep persisted deadline synced with current window
                    AppPreferences.setMosqueRestoreDeadlineSync(context, activeRestoreTarget)
                }
                armDirectRestoreBackups(
                    context = context,
                    prayerName = localizedPrayer,
                    restoreTargetMillis = activeRestoreTarget,
                    prayerOrdinal = activePrayer.ordinal
                )
            } else if (MosqueModeManager.isCurrentlySilenced(context)) {
                val deadline = AppPreferences.getMosqueRestoreDeadlineSync(context)
                // Auto-restore whenever a scheduled session's deadline expired or window no longer active
                if (deadline > 0L) {
                    MosqueModeManager.deactivateMosqueMode(context)
                }
            }

            // 4. Periodic 15-min watchdog to guarantee expired Mosque sessions always unlock even under aggressive OEM Doze
            scheduleWatchdogWorker(workManager)
        }
    }

    private fun scheduleWatchdogWorker(workManager: WorkManager) {
        val watchdogData = workDataOf(
            MosqueModeWorker.KEY_ACTION to MosqueModeWorker.ACTION_WATCHDOG
        )
        val watchdogReq = PeriodicWorkRequestBuilder<MosqueModeWorker>(
            15, TimeUnit.MINUTES
        )
            .setInputData(watchdogData)
            .addTag("mosque_mode")
            .addTag("mosque_mode_watchdog")
            .build()
        workManager.enqueueUniquePeriodicWork(
            "mosque_mode_watchdog_work",
            ExistingPeriodicWorkPolicy.KEEP,
            watchdogReq
        )
    }

    /**
     * Arms triple-redundant restore triggers (primary alarm, +2 min safety alarm, and WorkManager task)
     * right when Mosque Mode enters Vibrate/Silent so the phone is guaranteed to return to Normal mode.
     */
    fun armDirectRestoreBackups(
        context: Context,
        prayerName: String,
        restoreTargetMillis: Long,
        prayerOrdinal: Int = 9
    ) {
        val now = System.currentTimeMillis()
        if (restoreTargetMillis <= now) {
            MosqueModeManager.deactivateMosqueMode(context)
            return
        }

        val alarmManager = getAlarmManager(context)
        if (alarmManager != null) {
            val primaryRestoreReqCode = prayerOrdinal * 10 + 2
            val safetyRestoreReqCode = 900 + prayerOrdinal

            val primaryIntent = Intent(context, MosqueModeReceiver::class.java).apply {
                action = MosqueModeReceiver.ACTION_MOSQUE_RESTORE
                putExtra(MosqueModeReceiver.EXTRA_PRAYER_NAME, prayerName)
                putExtra(MosqueModeReceiver.EXTRA_PRAYER_ORDINAL, prayerOrdinal)
            }
            val primaryPending = PendingIntent.getBroadcast(
                context,
                primaryRestoreReqCode,
                primaryIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            setExactAlarm(alarmManager, restoreTargetMillis, primaryPending)

            // Secondary safety restore alarm 2 minutes after scheduled end in case primary alarm was deferred
            val safetyIntent = Intent(context, MosqueModeReceiver::class.java).apply {
                action = MosqueModeReceiver.ACTION_MOSQUE_RESTORE
                putExtra(MosqueModeReceiver.EXTRA_PRAYER_NAME, prayerName)
                putExtra(MosqueModeReceiver.EXTRA_PRAYER_ORDINAL, prayerOrdinal)
            }
            val safetyPending = PendingIntent.getBroadcast(
                context,
                safetyRestoreReqCode,
                safetyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            setExactAlarm(alarmManager, restoreTargetMillis + 120_000L, safetyPending)
        }

        val workManager = getWorkManager(context)
        val restoreDelay = maxOf(2_000L, restoreTargetMillis - now)
        val restoreData = workDataOf(
            MosqueModeWorker.KEY_ACTION to MosqueModeWorker.ACTION_RESTORE,
            MosqueModeWorker.KEY_PRAYER_NAME to prayerName,
            MosqueModeWorker.KEY_PRAYER_ORDINAL to prayerOrdinal
        )
        val restoreReq = OneTimeWorkRequestBuilder<MosqueModeWorker>()
            .setInitialDelay(restoreDelay, TimeUnit.MILLISECONDS)
            .setInputData(restoreData)
            .addTag("mosque_mode")
            .build()
        workManager.enqueueUniqueWork(
            "mosque_restore_direct_$prayerOrdinal",
            ExistingWorkPolicy.REPLACE,
            restoreReq
        )
    }

    /**
     * Computes the exact Jamat-based Mosque Mode window (silenceStartMillis, restoreEndMillis)
     * in the device's local timezone. Never spans an entire multi-hour Waqt.
     */
    fun computePrayerWindow(
        prayer: PrayerName,
        config: PrayerMosqueConfig,
        todaySchedule: PrayerSchedule,
        tomorrowSchedule: PrayerSchedule,
        now: Long
    ): Pair<Long, Long> {
        // 1. If user explicitly set a manual custom Start & End time for silent/vibration window:
        val customStartPair = config.customStartTime?.let { TimeFormatUtils.parseTimeString(it) }
        val customEndPair = config.customEndTime?.let { TimeFormatUtils.parseTimeString(it) }

        if (customStartPair != null && customEndPair != null) {
            val (sH, sM) = customStartPair
            val (eH, eM) = customEndPair

            val calStartToday = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, sH)
                set(Calendar.MINUTE, sM)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val calEndToday = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, eH)
                set(Calendar.MINUTE, eM)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (calEndToday.timeInMillis <= calStartToday.timeInMillis) {
                calEndToday.add(Calendar.DAY_OF_YEAR, 1)
            }

            val todayStart = calStartToday.timeInMillis
            val todayEnd = calEndToday.timeInMillis

            return if (todayEnd > now) {
                Pair(todayStart, todayEnd)
            } else {
                val calStartTomorrow = (calStartToday.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
                val calEndTomorrow = (calEndToday.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
                Pair(calStartTomorrow.timeInMillis, calEndTomorrow.timeInMillis)
            }
        }

        // 2. Jamat-based window: [JamatTime - bufferBeforeMinutes, JamatTime + durationMinutes]
        val todayItem = todaySchedule.items.find { it.prayer == prayer }
        val tomorrowItem = tomorrowSchedule.items.find { it.prayer == prayer }

        val bufferMs = config.bufferBeforeMinutes.coerceIn(0, 30) * 60_000L
        val durationMs = config.durationMinutes.coerceIn(5, 60) * 60_000L

        val todayJamatMs = if (todayItem != null && todayItem.jamatTimeMillis > 0L) {
            todayItem.jamatTimeMillis
        } else {
            (todayItem?.timeMillis ?: now) + config.defaultJamatOffsetMinutes * 60_000L
        }

        val tomorrowJamatMs = if (tomorrowItem != null && tomorrowItem.jamatTimeMillis > 0L) {
            tomorrowItem.jamatTimeMillis
        } else {
            (tomorrowItem?.timeMillis ?: (now + 24 * 3600_000L)) + config.defaultJamatOffsetMinutes * 60_000L
        }

        val jamatMillis = if ((todayJamatMs + durationMs) > now) {
            todayJamatMs
        } else {
            tomorrowJamatMs
        }

        val silenceTarget = jamatMillis - bufferMs
        val restoreTarget = jamatMillis + durationMs

        return Pair(silenceTarget, restoreTarget)
    }

    /**
     * Legacy wrapper to maintain compatibility with existing callers
     */
    suspend fun scheduleMosqueMode(
        context: Context,
        bufferMinutes: Int = 5,
        durationMinutes: Int = 25
    ) {
        val prefs = AppPreferences(context)
        prefs.setMosqueBuffers(bufferMinutes, durationMinutes)
        scheduleAllMosquePrayers(context)
    }

    private fun scheduleAlarm(
        context: Context,
        alarmManager: AlarmManager?,
        prayer: PrayerName,
        silenceTarget: Long,
        restoreTarget: Long,
        now: Long
    ) {
        if (alarmManager == null) return

        val silenceReqCode = prayer.ordinal * 10 + 1
        val restoreReqCode = prayer.ordinal * 10 + 2

        val uiLang = com.example.data.util.LocaleHelper.getUiLanguageSync(context)
        val localizedPrayer = com.example.data.util.SupportedLanguage.localizedPrayerName(prayer.name, uiLang)

        // Silence / Vibrate Intent (carries exact restore deadline so delayed triggers never lock phone post-Jamat)
        if (silenceTarget > now) {
            val silenceIntent = Intent(context, MosqueModeReceiver::class.java).apply {
                action = MosqueModeReceiver.ACTION_MOSQUE_SILENCE
                putExtra(MosqueModeReceiver.EXTRA_PRAYER_NAME, localizedPrayer)
                putExtra(MosqueModeReceiver.EXTRA_RESTORE_AT_MILLIS, restoreTarget)
                putExtra(MosqueModeReceiver.EXTRA_PRAYER_ORDINAL, prayer.ordinal)
            }
            val silencePending = PendingIntent.getBroadcast(
                context,
                silenceReqCode,
                silenceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            setExactAlarm(alarmManager, silenceTarget, silencePending)
        }

        // Primary Restore Intent + 2-minute Safety Restore Intent
        if (restoreTarget > now) {
            val restoreIntent = Intent(context, MosqueModeReceiver::class.java).apply {
                action = MosqueModeReceiver.ACTION_MOSQUE_RESTORE
                putExtra(MosqueModeReceiver.EXTRA_PRAYER_NAME, localizedPrayer)
                putExtra(MosqueModeReceiver.EXTRA_PRAYER_ORDINAL, prayer.ordinal)
            }
            val restorePending = PendingIntent.getBroadcast(
                context,
                restoreReqCode,
                restoreIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            setExactAlarm(alarmManager, restoreTarget, restorePending)

            val safetyReqCode = 900 + prayer.ordinal
            val safetyPending = PendingIntent.getBroadcast(
                context,
                safetyReqCode,
                restoreIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            setExactAlarm(alarmManager, restoreTarget + 120_000L, safetyPending)
        }
    }

    private fun setExactAlarm(alarmManager: AlarmManager, triggerAtMillis: Long, pendingIntent: PendingIntent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } catch (_: Exception) {
            try {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } catch (_: Exception) {}
        }
    }

    private fun scheduleWorkBackup(
        context: Context,
        workManager: WorkManager,
        prayer: PrayerName,
        silenceTarget: Long,
        restoreTarget: Long,
        now: Long
    ) {
        val uiLang = com.example.data.util.LocaleHelper.getUiLanguageSync(context)
        val localizedPrayer = com.example.data.util.SupportedLanguage.localizedPrayerName(prayer.name, uiLang)

        // 1. WorkManager Silence/Vibrate Request
        if (silenceTarget > now) {
            val silenceDelay = maxOf(1000L, silenceTarget - now)
            val silenceData = workDataOf(
                MosqueModeWorker.KEY_ACTION to MosqueModeWorker.ACTION_SILENCE,
                MosqueModeWorker.KEY_PRAYER_NAME to localizedPrayer,
                MosqueModeWorker.KEY_RESTORE_AT_MILLIS to restoreTarget,
                MosqueModeWorker.KEY_PRAYER_ORDINAL to prayer.ordinal
            )
            val silenceRequest = OneTimeWorkRequestBuilder<MosqueModeWorker>()
                .setInitialDelay(silenceDelay, TimeUnit.MILLISECONDS)
                .setInputData(silenceData)
                .addTag("mosque_mode")
                .addTag("mosque_silence_${prayer.name.lowercase()}")
                .build()

            workManager.enqueueUniqueWork(
                "mosque_silence_${prayer.name.lowercase()}",
                ExistingWorkPolicy.REPLACE,
                silenceRequest
            )
        }

        // 2. WorkManager Restore Request
        if (restoreTarget > now) {
            val restoreDelay = maxOf(2000L, restoreTarget - now)
            val restoreData = workDataOf(
                MosqueModeWorker.KEY_ACTION to MosqueModeWorker.ACTION_RESTORE,
                MosqueModeWorker.KEY_PRAYER_NAME to localizedPrayer,
                MosqueModeWorker.KEY_PRAYER_ORDINAL to prayer.ordinal
            )
            val restoreRequest = OneTimeWorkRequestBuilder<MosqueModeWorker>()
                .setInitialDelay(restoreDelay, TimeUnit.MILLISECONDS)
                .setInputData(restoreData)
                .addTag("mosque_mode")
                .addTag("mosque_restore_${prayer.name.lowercase()}")
                .build()

            workManager.enqueueUniqueWork(
                "mosque_restore_${prayer.name.lowercase()}",
                ExistingWorkPolicy.REPLACE,
                restoreRequest
            )
        }
    }

    private fun cancelSinglePrayer(context: Context, prayer: PrayerName) {
        val alarmManager = getAlarmManager(context)
        val silenceReqCode = prayer.ordinal * 10 + 1
        val restoreReqCode = prayer.ordinal * 10 + 2

        if (alarmManager != null) {
            val silenceIntent = Intent(context, MosqueModeReceiver::class.java).apply {
                action = MosqueModeReceiver.ACTION_MOSQUE_SILENCE
            }
            val silencePending = PendingIntent.getBroadcast(
                context,
                silenceReqCode,
                silenceIntent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (silencePending != null) {
                alarmManager.cancel(silencePending)
                silencePending.cancel()
            }

            val restoreIntent = Intent(context, MosqueModeReceiver::class.java).apply {
                action = MosqueModeReceiver.ACTION_MOSQUE_RESTORE
            }
            val restorePending = PendingIntent.getBroadcast(
                context,
                restoreReqCode,
                restoreIntent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (restorePending != null) {
                alarmManager.cancel(restorePending)
                restorePending.cancel()
            }
            val safetyPending = PendingIntent.getBroadcast(
                context,
                900 + prayer.ordinal,
                restoreIntent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (safetyPending != null) {
                alarmManager.cancel(safetyPending)
                safetyPending.cancel()
            }
        }

        val workManager = getWorkManager(context)
        workManager.cancelUniqueWork("mosque_silence_${prayer.name.lowercase()}")
        workManager.cancelUniqueWork("mosque_restore_${prayer.name.lowercase()}")
        workManager.cancelUniqueWork("mosque_restore_direct_${prayer.ordinal}")
    }

    fun cancelMosqueSchedule(context: Context) {
        for (prayer in PRAYERS) {
            cancelSinglePrayer(context, prayer)
        }
        getWorkManager(context).cancelUniqueWork("mosque_mode_watchdog_work")
        MosqueModeManager.deactivateMosqueMode(context)
    }

    fun testSilenceNow(context: Context, prayerName: String = "পরীক্ষামূলক") {
        MosqueModeManager.activateMosqueMode(context, prayerName)
    }

    fun testRestoreNow(context: Context) {
        MosqueModeManager.deactivateMosqueMode(context)
    }
}
