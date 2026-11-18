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
        const val KEY_PRAYER_NAME = "mosque_prayer_name"
    }

    override suspend fun doWork(): Result {
        val action = inputData.getString(KEY_ACTION) ?: ACTION_SILENCE
        val prayerName = inputData.getString(KEY_PRAYER_NAME) ?: "নামাজ"

        if (action == ACTION_SILENCE) {
            MosqueModeManager.activateMosqueMode(context, prayerName)
        } else {
            MosqueModeManager.deactivateMosqueMode(context)
        }

        return Result.success()
    }
}
