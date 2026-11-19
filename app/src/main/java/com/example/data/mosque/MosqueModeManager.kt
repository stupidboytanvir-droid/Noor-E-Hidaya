package com.example.data.mosque

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.preference.AppPreferences
import com.example.data.util.LocaleHelper
import com.example.data.util.SupportedLanguage
import com.example.ui.theme.LocalizationRegistry

object MosqueModeManager {

    private const val MOSQUE_CHANNEL_ID = "noor_mosque_mode_channel"
    private const val MOSQUE_CHANNEL_NAME = "Mosque Mode (Auto Silent / Vibration)"
    private const val NOTIFICATION_ID = 909

    /**
     * Checks whether the system permits setting AudioManager.RINGER_MODE_SILENT directly.
     * Note: RINGER_MODE_VIBRATE works on all Android devices without requiring policy access.
     */
    fun isFullSilentAccessGranted(context: Context): Boolean {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nm?.isNotificationPolicyAccessGranted == true
        } else {
            true
        }
    }

    fun openSoundAccessSettingsIntent(): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        } else {
            Intent(Settings.ACTION_SOUND_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
    }

    fun getCurrentRingerMode(context: Context): Int {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        return am?.ringerMode ?: AudioManager.RINGER_MODE_NORMAL
    }

    private val stateLock = Any()

    fun isCurrentlySilenced(context: Context): Boolean {
        return AppPreferences.isMosqueActiveSync(context)
    }

    fun activateMosqueMode(
        context: Context,
        prayerName: String = "নামাজ",
        preferredMode: MosqueRingerMode = AppPreferences.getMosqueRingerModeSync(context),
        restoreAtMillis: Long = 0L
    ) {
        synchronized(stateLock) {
            val now = System.currentTimeMillis()
            // Guard: if a delayed alarm/worker fires AFTER the Jamat window already ended,
            // never enter silence/vibrate — ensure Normal mode is restored instead.
            if (restoreAtMillis > 0L && now >= restoreAtMillis) {
                deactivateMosqueMode(context)
                return
            }

            val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

            // 1. Capture and save current ringer mode ONLY on initial transition into Mosque Mode.
            // Never save the target Mosque mode itself (VIBRATE/SILENT), which would trap the device forever.
            val wasActive = AppPreferences.isMosqueActiveSync(context)
            if (!wasActive && am != null) {
                val currentMode = am.ringerMode
                val targetOsMode = if (preferredMode == MosqueRingerMode.VIBRATE) {
                    AudioManager.RINGER_MODE_VIBRATE
                } else {
                    AudioManager.RINGER_MODE_SILENT
                }
                val modeToSave = if (currentMode == targetOsMode) {
                    AudioManager.RINGER_MODE_NORMAL
                } else {
                    currentMode
                }
                AppPreferences.setSavedRingerModeSync(context, modeToSave)
            }
            AppPreferences.setIsMosqueActiveSync(context, true)
            if (restoreAtMillis > 0L) {
                AppPreferences.setMosqueRestoreDeadlineSync(context, restoreAtMillis)
            }

            // 2. Apply user's chosen Mosque Mode: VIBRATE or SILENT
            var appliedMode = preferredMode
            if (preferredMode == MosqueRingerMode.VIBRATE) {
                try {
                    am?.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                } catch (_: Exception) {}
            } else {
                try {
                    am?.ringerMode = AudioManager.RINGER_MODE_SILENT
                } catch (_: Exception) {
                    appliedMode = MosqueRingerMode.VIBRATE
                    try {
                        am?.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                    } catch (_: Exception) {}
                }
            }

            // 3. Post discreet ongoing notification
            showMosqueNotification(context, prayerName, appliedMode)
        }
    }

    fun deactivateMosqueMode(context: Context) {
        synchronized(stateLock) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

            // 1. Determine safe restore target — never stay trapped in the Mosque target mode
            val savedMode = AppPreferences.getSavedRingerModeSync(context)
            val configuredMosqueMode = AppPreferences.getMosqueRingerModeSync(context)
            val mosqueTargetOsMode = if (configuredMosqueMode == MosqueRingerMode.VIBRATE) {
                AudioManager.RINGER_MODE_VIBRATE
            } else {
                AudioManager.RINGER_MODE_SILENT
            }
            val targetMode = when {
                savedMode == mosqueTargetOsMode -> AudioManager.RINGER_MODE_NORMAL
                savedMode == AudioManager.RINGER_MODE_NORMAL ||
                    savedMode == AudioManager.RINGER_MODE_VIBRATE ||
                    savedMode == AudioManager.RINGER_MODE_SILENT -> savedMode
                else -> AudioManager.RINGER_MODE_NORMAL
            }

            // Clear DND interruption filter first if active & permitted (prevents OEMs from blocking Normal ringer)
            try {
                if (nm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    nm.isNotificationPolicyAccessGranted &&
                    nm.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL
                ) {
                    nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
                }
            } catch (_: Exception) {}

            // 2. Restore ringer mode and ensure audible ring volume when restoring NORMAL
            try {
                am?.ringerMode = targetMode
            } catch (_: Exception) {
                try {
                    am?.ringerMode = AudioManager.RINGER_MODE_NORMAL
                } catch (_: Exception) {}
            }

            if (targetMode == AudioManager.RINGER_MODE_NORMAL && am != null) {
                try {
                    val maxRing = am.getStreamMaxVolume(AudioManager.STREAM_RING)
                    if (maxRing > 0 && am.getStreamVolume(AudioManager.STREAM_RING) == 0) {
                        am.setStreamVolume(
                            AudioManager.STREAM_RING,
                            maxOf(1, maxRing / 2),
                            0
                        )
                    }
                    if (am.ringerMode != AudioManager.RINGER_MODE_NORMAL) {
                        am.ringerMode = AudioManager.RINGER_MODE_NORMAL
                    }
                } catch (_: Exception) {}
            }

            AppPreferences.setIsMosqueActiveSync(context, false)
            AppPreferences.setMosqueRestoreDeadlineSync(context, 0L)
            AppPreferences.setSavedRingerModeSync(context, AudioManager.RINGER_MODE_NORMAL)

            // 3. Remove ongoing Mosque notification
            nm?.cancel(NOTIFICATION_ID)
        }
    }

    /**
     * Checks if a scheduled Mosque Mode session has passed its restore deadline and immediately
     * unlocks back to Normal ringer mode. Safe to call on resume, receivers, and watchdog workers.
     */
    fun restoreIfExpired(context: Context, now: Long = System.currentTimeMillis()): Boolean {
        synchronized(stateLock) {
            if (!AppPreferences.isMosqueActiveSync(context)) return false
            val deadline = AppPreferences.getMosqueRestoreDeadlineSync(context)
            if (deadline > 0L && now >= deadline) {
                deactivateMosqueMode(context)
                return true
            }
            return false
        }
    }

    private fun showMosqueNotification(
        context: Context,
        prayerName: String,
        appliedMode: MosqueRingerMode
    ) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        val uiLang = LocaleHelper.getUiLanguageSync(context)
        val isBn = SupportedLanguage.normalizeCode(uiLang) == "bn"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MOSQUE_CHANNEL_ID,
                LocalizationRegistry.translate(uiLang, "মসজিদ মোড (অটো সাইলেন্ট / ভাইব্রেশন)", MOSQUE_CHANNEL_NAME),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows active status when device is in Silent or Vibration mode during Jamat"
                setShowBadge(false)
            }
            nm.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val modeLabel = if (appliedMode == MosqueRingerMode.VIBRATE) {
            LocalizationRegistry.translate(uiLang, "ভাইব্রেশন", "Vibration")
        } else {
            LocalizationRegistry.translate(uiLang, "সাইলেন্ট", "Silent")
        }
        val mosqueTitle = LocalizationRegistry.translate(uiLang, "মসজিদ মোড", "Mosque Mode")
        val contentText = if (isBn) {
            "জামাত চলাকালীন মোবাইল স্বয়ংক্রিয়ভাবে $modeLabel মোডে আছে। জামাত শেষে পূর্বের রিংটোন ফিরে আসবে।"
        } else {
            "Device is in $modeLabel mode during Jamat and will restore automatically afterward."
        }
        val titleText = if (isBn) {
            "🕌 মসজিদ মোড সক্রিয় ($prayerName • $modeLabel)"
        } else {
            "🕌 $mosqueTitle ($prayerName • $modeLabel)"
        }

        val notification = NotificationCompat.Builder(context, MOSQUE_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(titleText)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setAutoCancel(false)
            .setColor(android.graphics.Color.parseColor("#0A1312"))
            .setContentIntent(pendingIntent)
            .build()

        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                nm.notify(NOTIFICATION_ID, notification)
            }
        } catch (_: SecurityException) {}
    }
}
