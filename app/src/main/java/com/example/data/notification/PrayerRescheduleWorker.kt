package com.example.data.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

import com.example.data.mosque.MosqueModeManager
import com.example.data.mosque.MosqueModeScheduler
import com.example.data.preference.AppPreferences
import kotlinx.coroutines.flow.first

class PrayerRescheduleWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            MosqueModeManager.restoreIfExpired(context)
            PrayerAlertScheduler.schedulePrayerAlerts(context)
            if (AppPreferences(context).mosqueModeEnabled.first()) {
                MosqueModeScheduler.scheduleAllMosquePrayers(context)
            }
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
