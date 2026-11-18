package com.example.data.update

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

/**
 * Periodic background worker that checks for new app updates on GitHub
 * and fires a high-priority notification so tapping it directly downloads the APK.
 */
class AppUpdateCheckWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val manager = InAppUpdateManager(applicationContext)
            manager.checkGitHubUpdates(notifyUser = true)
            manager.unregister()
            Result.success()
        } catch (e: Exception) {
            Log.w(TAG, "Background update check failed: ${e.message}")
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "AppUpdateCheckWorker"
        private const val UNIQUE_WORK_NAME = "noor_github_update_check"

        fun schedulePeriodicCheck(context: Context) {
            try {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

                val request = PeriodicWorkRequestBuilder<AppUpdateCheckWorker>(
                    6, TimeUnit.HOURS
                )
                    .setConstraints(constraints)
                    .build()

                WorkManager.getInstance(context.applicationContext).enqueueUniquePeriodicWork(
                    UNIQUE_WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
            } catch (e: Exception) {
                Log.w(TAG, "Unable to schedule AppUpdateCheckWorker: ${e.message}")
            }
        }
    }
}
