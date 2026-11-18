package com.example.data.mosque

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.preference.AppPreferences

/**
 * Receives asynchronous SIM/radio SMS transmission results from SmsManager
 * so background Auto-SMS failures never fail silently.
 */
class SmsSentReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "SmsSentReceiver"
        const val ACTION_SMS_SENT = "com.example.action.MOSQUE_SMS_SENT"
        const val EXTRA_PHONE_NUMBER = "extra_phone_number"
        const val EXTRA_MESSAGE_TEXT = "extra_message_text"
        const val EXTRA_IS_TEST = "extra_is_test"

        private const val NOTIFICATION_CHANNEL_ID = "noor_mosque_autoreply_channel"
        private const val NOTIFICATION_CHANNEL_NAME = "Mosque Auto-Reply SMS"
        private const val NOTIFICATION_ID_SENT = 919
        private const val NOTIFICATION_ID_ERROR = 920

        fun postStatusNotification(
            context: Context,
            title: String,
            shortText: String,
            bigText: String,
            isError: Boolean
        ) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val granted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                    if (!granted) return
                }

                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        NOTIFICATION_CHANNEL_ID,
                        NOTIFICATION_CHANNEL_NAME,
                        if (isError) NotificationManager.IMPORTANCE_DEFAULT else NotificationManager.IMPORTANCE_LOW
                    ).apply {
                        description = "Shows confirmation or delivery errors for Mosque Mode Auto-SMS"
                        enableVibration(isError)
                        setShowBadge(false)
                    }
                    nm.createNotificationChannel(channel)
                }

                val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(shortText)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
                    .setPriority(if (isError) NotificationCompat.PRIORITY_DEFAULT else NotificationCompat.PRIORITY_LOW)
                    .setAutoCancel(true)
                    .build()

                nm.notify(if (isError) NOTIFICATION_ID_ERROR else NOTIFICATION_ID_SENT, notification)
            } catch (se: SecurityException) {
                Log.w(TAG, "Notification permission rejected while posting SMS status: ${se.message}")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to post SMS status notification: ${e.message}")
            }
        }

        fun mapSmsErrorToBengali(resultCode: Int): String {
            return when (resultCode) {
                SmsManager.RESULT_ERROR_RADIO_OFF -> "ফোনের রেডিও বা ফ্লাইট মোড চালু আছে"
                SmsManager.RESULT_ERROR_NO_SERVICE -> "সিম কার্ডে মোবাইল নেটওয়ার্ক সিগন্যাল নেই"
                SmsManager.RESULT_ERROR_NULL_PDU -> "এসএমএস ফরম্যাট ত্রুটি (PDU অনুপস্থিত)"
                SmsManager.RESULT_ERROR_LIMIT_EXCEEDED -> "সিমের এসএমএস পাঠানোর সীমা অতিক্রম করেছে"
                SmsManager.RESULT_ERROR_SHORT_CODE_NOT_ALLOWED,
                SmsManager.RESULT_ERROR_SHORT_CODE_NEVER_ALLOWED -> "এই নম্বরে এসএমএস পাঠানো অনুমোদিত নয়"
                SmsManager.RESULT_ERROR_GENERIC_FAILURE -> "সিম বা ব্যালেন্স সমস্যার কারণে এসএমএস পাঠানো যায়নি"
                else -> "অজানা সিম/নেটওয়ার্ক ত্রুটি (কোড: $resultCode)"
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_SMS_SENT) return

        val phoneNumber = intent.getStringExtra(EXTRA_PHONE_NUMBER) ?: ""
        val messageText = intent.getStringExtra(EXTRA_MESSAGE_TEXT) ?: AppPreferences.getMosqueAutoReplyMsgSync(context)
        val isTest = intent.getBooleanExtra(EXTRA_IS_TEST, false)

        if (resultCode == Activity.RESULT_OK) {
            val label = if (isTest) {
                "টেস্ট এসএমএস সফলভাবে প্রেরিত ($phoneNumber)"
            } else {
                "অটো-এসএমএস সফলভাবে প্রেরিত ($phoneNumber)"
            }
            AppPreferences.setLastAutoSmsStatusSync(context, label, isSuccess = true)
            Log.i(TAG, "SIM SMS sent successfully to $phoneNumber (isTest=$isTest)")

            postStatusNotification(
                context = context,
                title = if (isTest) "টেস্ট অটো-এসএমএস প্রেরিত" else "সালাতকালীন অটো-এসএমএস প্রেরিত",
                shortText = "$phoneNumber নম্বরে সিম থেকে বার্তা পাঠানো হয়েছে",
                bigText = "প্রেরিত বার্তা ($phoneNumber):\n\"$messageText\"",
                isError = false
            )
        } else {
            val reason = mapSmsErrorToBengali(resultCode)
            if (phoneNumber.isNotBlank()) {
                AppPreferences.clearSmsCooldownSync(context, MosqueCallReceiver.normalizePhoneNumber(phoneNumber))
            }
            val statusMsg = "এসএমএস ব্যর্থ ($phoneNumber): $reason"
            AppPreferences.setLastAutoSmsStatusSync(context, statusMsg, isSuccess = false)
            Log.e(TAG, "SIM SMS send failed for $phoneNumber: $reason (code=$resultCode)")

            postStatusNotification(
                context = context,
                title = "অটো-এসএমএস পাঠানো ব্যর্থ",
                shortText = reason,
                bigText = "$phoneNumber নম্বরে অটো-রিপ্লাই পাঠানো যায়নি।\nকারণ: $reason",
                isError = true
            )
        }
    }
}
