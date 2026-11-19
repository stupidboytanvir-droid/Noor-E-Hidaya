package com.example.data.mosque

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class MosqueModeWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val KEY_ACTION = "mosque_action"
        const val ACTION_SILENCE = "action_silence"
        const val ACTION_RESTORE = "action_restore"
        const val ACTION_WATCHDOG = "action_watchdog"
        const val KEY_PRAYER_NAME = "mosque_prayer_name"
        const val KEY_RESTORE_AT_MILLIS = "mosque_restore_at_millis"
        const val KEY_PRAYER_ORDINAL = "mosque_prayer_ordinal"
    }

    override suspend fun doWork(): Result {
        val action = inputData.getString(KEY_ACTION) ?: ACTION_SILENCE
        val prayerName = inputData.getString(KEY_PRAYER_NAME) ?: "নামাজ"
        val restoreAtMillis = inputData.getLong(KEY_RESTORE_AT_MILLIS, 0L)
        val prayerOrdinal = inputData.getInt(KEY_PRAYER_ORDINAL, 9)

        when (action) {
            ACTION_SILENCE -> {
                val now = System.currentTimeMillis()
                if (restoreAtMillis > 0L && now >= restoreAtMillis) {
                    MosqueModeManager.deactivateMosqueMode(context)
                    MosqueModeScheduler.scheduleAllMosquePrayers(context)
                } else {
                    MosqueModeManager.activateMosqueMode(
                        context = context,
                        prayerName = prayerName,
                        restoreAtMillis = restoreAtMillis
                    )
                    if (restoreAtMillis > now) {
                        MosqueModeScheduler.armDirectRestoreBackups(
                            context = context,
                            prayerName = prayerName,
                            restoreTargetMillis = restoreAtMillis,
                            prayerOrdinal = prayerOrdinal
                        )
                    }
                }
            }
            ACTION_RESTORE -> {
                MosqueModeManager.deactivateMosqueMode(context)
                try {
                    MosqueModeScheduler.scheduleAllMosquePrayers(context)
                } catch (_: Exception) {}
            }
            ACTION_WATCHDOG -> {
                MosqueModeManager.restoreIfExpired(context)
                try {
                    MosqueModeScheduler.scheduleAllMosquePrayers(context)
                } catch (_: Exception) {}
            }
        }

        return Result.success()
    }
}
