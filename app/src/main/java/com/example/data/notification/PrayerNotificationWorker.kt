package com.example.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.MainActivity
import com.example.R
import com.example.data.util.LocaleHelper
import com.example.data.util.SupportedLanguage
import com.example.ui.theme.LocalizationRegistry

class PrayerNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    data class PrayerNotificationCopy(
        val title: String,
        val content: String,
        val summary: String
    )

    companion object {
        const val CHANNEL_ID = "noor_prayer_alerts_channel"
        const val CHANNEL_NAME = "Prayer Times & Adhan Alerts"
        const val KEY_PRAYER_NAME = "key_prayer_name"
        const val KEY_PRAYER_ARABIC = "key_prayer_arabic"
        const val KEY_PRAYER_BENGALI = "key_prayer_bengali"
        const val KEY_FORMATTED_TIME = "key_formatted_time"
        const val KEY_IS_PRE_ALERT = "key_is_pre_alert"
        const val KEY_PRE_MINUTES = "key_pre_minutes"
        const val KEY_IS_TEST = "key_is_test"

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val uiLang = LocaleHelper.getUiLanguageSync(context)
                val channelName = LocalizationRegistry.translate(
                    uiLang,
                    "নামাজের সময় ও ওয়াক্ত এলার্ট",
                    CHANNEL_NAME
                )
                val channelDesc = LocalizationRegistry.translate(
                    uiLang,
                    "প্রতি ওয়াক্ত শুরুর নোটিফিকেশন এবং জামাতের পূর্বে ওযু ও প্রস্তুতির রিমাইন্ডার",
                    "Waqt start notifications and pre-Jama'at Wudu & prayer preparation reminders"
                )
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = channelDesc
                    enableLights(true)
                    lightColor = Color.parseColor("#E5C158")
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 300, 200, 300)
                }
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        fun buildNotificationCopy(
            prayerName: String,
            prayerArabic: String,
            prayerBengali: String,
            formattedTime: String,
            isPreAlert: Boolean,
            preMinutes: Int,
            uiLang: String
        ): PrayerNotificationCopy {
            val isBn = SupportedLanguage.normalizeCode(uiLang) == "bn"
            val localizedPrayer = SupportedLanguage.localizedPrayerName(prayerName, uiLang)
                .ifBlank { if (isBn) prayerBengali else prayerName }
            val brand = LocalizationRegistry.translate(uiLang, "নূর-এ-হেদায়াহ", "Noor-e-Hidayah")

            if (isPreAlert) {
                val title = if (isBn) {
                    "⏰ আর $preMinutes মিনিট পর $localizedPrayer-এর জামাত • $prayerArabic"
                } else {
                    "⏰ $localizedPrayer Jama'at in $preMinutes min • $prayerArabic"
                }
                val content = if (isBn) {
                    if (formattedTime.isNotBlank()) {
                        "$localizedPrayer-এর জামাত ($formattedTime) শুরু হতে আর $preMinutes মিনিট বাকি। ওযু ও পরিষ্কার-পরিচ্ছন্ন হয়ে নামাজের জন্য প্রস্তুত হোন।"
                    } else {
                        "$localizedPrayer-এর জামাত শুরু হতে আর $preMinutes মিনিট বাকি। ওযু ও পরিষ্কার-পরিচ্ছন্ন হয়ে নামাজের জন্য প্রস্তুত হোন।"
                    }
                } else {
                    val timeSuffix = if (formattedTime.isNotBlank()) " ($formattedTime)" else ""
                    "$localizedPrayer Jama'at$timeSuffix begins in $preMinutes minutes. Perform Wudu, freshen up, and get ready for Salah."
                }
                val summary = "$brand • ${LocalizationRegistry.translate(uiLang, "জামাত প্রস্তুতি এলার্ট", "Jama'at Prep Alert")} ($preMinutes min)"
                return PrayerNotificationCopy(title = title, content = content, summary = summary)
            }

            // Waqt Start Alert: Strictly announces the Waqt transition (previous Waqt ended -> new Waqt started)
            val prevPrayerKey = when (prayerName.uppercase()) {
                "FAJR" -> "ISHA"
                "ASR" -> "DHUHR"
                "MAGHRIB" -> "ASR"
                "ISHA" -> "MAGHRIB"
                else -> null
            }
            val title = if (isBn) {
                "$localizedPrayer-এর ওয়াক্ত শুরু হয়েছে • $prayerArabic"
            } else {
                "$localizedPrayer Waqt Has Started • $prayerArabic"
            }
            val timePart = if (formattedTime.isNotBlank()) " ($formattedTime)" else ""
            val content = if (isBn) {
                if (prevPrayerKey != null) {
                    val prevBn = SupportedLanguage.localizedPrayerName(prevPrayerKey, uiLang)
                    "${prevBn}-এর ওয়াক্ত শেষ হয়েছে, এখন ${localizedPrayer}-এর ওয়াক্ত শুরু হয়েছে$timePart।"
                } else {
                    "এখন ${localizedPrayer}-এর ওয়াক্ত শুরু হয়েছে$timePart।"
                }
            } else {
                if (prevPrayerKey != null) {
                    val prevEn = SupportedLanguage.localizedPrayerName(prevPrayerKey, uiLang)
                    "$prevEn Waqt has ended and $localizedPrayer Waqt has now begun$timePart."
                } else {
                    "$localizedPrayer Waqt has now begun$timePart."
                }
            }
            val summary = "$brand • ${LocalizationRegistry.translate(uiLang, "ওয়াক্ত এলার্ট", "Waqt Alert")}"
            return PrayerNotificationCopy(title = title, content = content, summary = summary)
        }

        fun showPrayerNotification(
            context: Context,
            prayerName: String,
            prayerArabic: String,
            prayerBengali: String,
            formattedTime: String,
            isPreAlert: Boolean,
            preMinutes: Int,
            isTest: Boolean = false
        ) {
            if (!isTest) {
                val dedupeKey = "${prayerName.uppercase()}_${if (isPreAlert) "JAMAT_PREP" else "WAQT_START"}"
                if (!com.example.data.preference.AppPreferences.shouldAllowPrayerNotificationSync(context, dedupeKey)) {
                    return
                }
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val uiLang = LocaleHelper.getUiLanguageSync(context)
            val copy = buildNotificationCopy(
                prayerName = prayerName,
                prayerArabic = prayerArabic,
                prayerBengali = prayerBengali,
                formattedTime = formattedTime,
                isPreAlert = isPreAlert,
                preMinutes = preMinutes,
                uiLang = uiLang
            )

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                prayerName.hashCode() + (if (isPreAlert) 500 else 0),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(copy.title)
                .setContentText(copy.content)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(copy.content)
                        .setBigContentTitle(copy.title)
                        .setSummaryText(copy.summary)
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setColor(Color.parseColor("#0A1312"))
                .setAutoCancel(true)
                .setSound(soundUri)
                .setVibrate(longArrayOf(0, 300, 200, 300))
                .setContentIntent(pendingIntent)
                .build()

            val baseId = when (prayerName.lowercase()) {
                "fajr" -> 101
                "dhuhr" -> 102
                "asr" -> 103
                "maghrib" -> 104
                "isha" -> 105
                else -> 100
            }
            val notificationId = if (isPreAlert) baseId + 100 else baseId

            try {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    androidx.core.content.ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    notificationManager.notify(notificationId, notification)
                }
            } catch (_: SecurityException) {}
        }
    }

    override suspend fun doWork(): Result {
        val prayerName = inputData.getString(KEY_PRAYER_NAME) ?: "FAJR"
        val prayerArabic = inputData.getString(KEY_PRAYER_ARABIC) ?: "الصلاة"
        val prayerBengali = inputData.getString(KEY_PRAYER_BENGALI) ?: "নামাজ"
        val formattedTime = inputData.getString(KEY_FORMATTED_TIME) ?: ""
        val isPreAlert = inputData.getBoolean(KEY_IS_PRE_ALERT, false)
        val preMinutes = inputData.getInt(KEY_PRE_MINUTES, 15)
        val isTest = inputData.getBoolean(KEY_IS_TEST, false)

        createNotificationChannel(context)
        showPrayerNotification(
            context = context,
            prayerName = prayerName,
            prayerArabic = prayerArabic,
            prayerBengali = prayerBengali,
            formattedTime = formattedTime,
            isPreAlert = isPreAlert,
            preMinutes = preMinutes,
            isTest = isTest
        )

        if (!isTest) {
            try {
                PrayerAlertScheduler.schedulePrayerAlerts(context)
            } catch (_: Exception) {}
        }

        return Result.success()
    }
}
