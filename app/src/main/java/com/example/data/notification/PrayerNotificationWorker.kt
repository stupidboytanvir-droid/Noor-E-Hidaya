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

    companion object {
        const val CHANNEL_ID = "noor_prayer_alerts_channel"
        const val CHANNEL_NAME = "Prayer Times & Adhan Alerts"
        const val KEY_PRAYER_NAME = "key_prayer_name"
        const val KEY_PRAYER_ARABIC = "key_prayer_arabic"
        const val KEY_PRAYER_BENGALI = "key_prayer_bengali"
        const val KEY_FORMATTED_TIME = "key_formatted_time"
        const val KEY_IS_PRE_ALERT = "key_is_pre_alert"
        const val KEY_PRE_MINUTES = "key_pre_minutes"

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val uiLang = LocaleHelper.getUiLanguageSync(context)
                val channelName = LocalizationRegistry.translate(
                    uiLang,
                    "নামাজের সময় ও আযান এলার্ট",
                    CHANNEL_NAME
                )
                val channelDesc = LocalizationRegistry.translate(
                    uiLang,
                    "ফজর, যোহর, আসর, মাগরিব, এশার ওয়াক্ত ও পূর্ব-সতর্কবার্তা",
                    "Notifications for Fajr, Dhuhr, Asr, Maghrib, Isha prayer times and pre-prayer reminders"
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

        fun showPrayerNotification(
            context: Context,
            prayerName: String,
            prayerArabic: String,
            prayerBengali: String,
            formattedTime: String,
            isPreAlert: Boolean,
            preMinutes: Int
        ) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val uiLang = LocaleHelper.getUiLanguageSync(context)
            val isBn = SupportedLanguage.normalizeCode(uiLang) == "bn"
            val localizedPrayer = SupportedLanguage.localizedPrayerName(prayerName, uiLang)
                .ifBlank { if (isBn) prayerBengali else prayerName }

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

            val title = if (isPreAlert) {
                if (isBn) {
                    "⏰ $preMinutes মিনিট পর $localizedPrayer নামাজের ওয়াক্ত • $prayerArabic"
                } else {
                    val label = LocalizationRegistry.translate(uiLang, "প্রি-নামাজ রিমাইন্ডার", "Prayer Reminder")
                    "⏰ $localizedPrayer ($preMinutes min) • $label • $prayerArabic"
                }
            } else {
                if (isBn) {
                    "$localizedPrayer নামাজের ওয়াক্ত শুরু হয়েছে • $prayerArabic"
                } else {
                    val label = LocalizationRegistry.translate(uiLang, "ওয়াক্ত শুরু", "Prayer Time Started")
                    "$localizedPrayer — $label • $prayerArabic"
                }
            }

            val content = if (isPreAlert) {
                if (isBn) {
                    if (formattedTime.isNotBlank()) {
                        "$localizedPrayer ওয়াক্ত ($formattedTime) শুরু হতে আর $preMinutes মিনিট বাকি। ওযু ও নামাজের প্রস্তুতি নিন।"
                    } else {
                        "$localizedPrayer ওয়াক্ত শুরু হতে আর $preMinutes মিনিট বাকি। ওযু ও নামাজের প্রস্তুতি নিন।"
                    }
                } else {
                    val timeSuffix = if (formattedTime.isNotBlank()) " ($formattedTime)" else ""
                    "$localizedPrayer$timeSuffix begins in $preMinutes minutes. Please prepare for Wudu and Salah."
                }
            } else {
                if (isBn) {
                    if (formattedTime.isNotBlank()) {
                        "সময়: $formattedTime। হাইয়্যা আলাস সালাহ (নামাজের দিকে এসো)। ওয়াক্তমতো জামাতে নামাজ আদায় করুন।"
                    } else {
                        "হাইয়্যা আলাস সালাহ (নামাজের দিকে এসো)। ওয়াক্তমতো জামাতে নামাজ আদায় করুন।"
                    }
                } else {
                    val timePrefix = if (formattedTime.isNotBlank()) "$formattedTime — " else ""
                    "${timePrefix}Hayya 'alas-Salah (Come to Prayer). Time for $localizedPrayer."
                }
            }

            val brand = LocalizationRegistry.translate(uiLang, "নূর-এ-হেদায়াহ", "Noor-e-Hidayah")
            val summaryText = if (isPreAlert) {
                "$brand • ${LocalizationRegistry.translate(uiLang, "প্রি-নামাজ রিমাইন্ডার", "Pre-Prayer Alert")} ($preMinutes min)"
            } else {
                "$brand • ${LocalizationRegistry.translate(uiLang, "ওয়াক্ত এলার্ট", "Prayer Alert")}"
            }

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(content)
                        .setBigContentTitle(title)
                        .setSummaryText(summaryText)
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

        createNotificationChannel(context)
        showPrayerNotification(
            context = context,
            prayerName = prayerName,
            prayerArabic = prayerArabic,
            prayerBengali = prayerBengali,
            formattedTime = formattedTime,
            isPreAlert = isPreAlert,
            preMinutes = preMinutes
        )

        try {
            PrayerAlertScheduler.schedulePrayerAlerts(context)
        } catch (_: Exception) {}

        return Result.success()
    }
}
