package com.example.data.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class PrayerRescheduleWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            PrayerAlertScheduler.schedulePrayerAlerts(context)
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
