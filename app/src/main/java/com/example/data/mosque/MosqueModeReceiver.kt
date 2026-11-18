package com.example.data.mosque

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.util.LocaleHelper
import com.example.ui.theme.LocalizationRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MosqueModeReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_MOSQUE_SILENCE = "com.example.action.MOSQUE_SILENCE"
        const val ACTION_MOSQUE_RESTORE = "com.example.action.MOSQUE_RESTORE"
        const val EXTRA_PRAYER_NAME = "extra_prayer_name"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val uiLang = LocaleHelper.getUiLanguageSync(context)
        val fallbackPrayer = LocalizationRegistry.translate(uiLang, "নামাজ", "Salah")
        val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME) ?: fallbackPrayer

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (action) {
                    ACTION_MOSQUE_SILENCE -> {
                        MosqueModeManager.activateMosqueMode(context, prayerName)
                    }
                    ACTION_MOSQUE_RESTORE -> {
                        MosqueModeManager.deactivateMosqueMode(context)
                    }
                    Intent.ACTION_BOOT_COMPLETED,
                    Intent.ACTION_MY_PACKAGE_REPLACED,
                    Intent.ACTION_TIME_CHANGED,
                    Intent.ACTION_TIMEZONE_CHANGED -> {
                        MosqueModeScheduler.scheduleAllMosquePrayers(context)
                    }
                }
            } catch (_: Exception) {
            } finally {
                pendingResult.finish()
            }
        }
    }
}
