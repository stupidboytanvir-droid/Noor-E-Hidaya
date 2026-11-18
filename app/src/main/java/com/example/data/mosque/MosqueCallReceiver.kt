package com.example.data.mosque

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.CallLog
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.data.preference.AppPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver listening for incoming phone calls during active Mosque Mode.
 * Dispatches an automated, 100% free SMS reply via the device's native SIM card (SmsManager)
 * when an incoming call is missed or rejected during prayer.
 *
 * Persists call state and anti-spam cooldowns in SharedPreferences so background process
 * restarts between RINGING and IDLE never cause dropped replies or duplicate SMS spam.
 */
class MosqueCallReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "MosqueCallReceiver"

        // Cooldown period per phone number (10 minutes) to prevent duplicate SMS spam
        private const val REPEAT_CALL_COOLDOWN_MS = 10 * 60 * 1000L

        // Maximum age for an active RINGING state before considering it stale (5 minutes)
        private const val MAX_RING_STATE_AGE_MS = 5 * 60 * 1000L

        fun normalizePhoneNumber(number: String): String {
            return number.filter { it.isDigit() || it == '+' }.trim()
        }

        fun isValidPhoneNumber(number: String?): Boolean {
            if (number.isNullOrBlank()) return false
            val trimmed = number.trim()
            if (trimmed.equals("unknown", ignoreCase = true) ||
                trimmed.equals("private", ignoreCase = true) ||
                trimmed == "-1" ||
                trimmed == "-2" ||
                trimmed == "-3"
            ) {
                return false
            }
            val clean = normalizePhoneNumber(trimmed)
            return clean.count { it.isDigit() } >= 5
        }

        fun resolveSmsManager(context: Context): SmsManager? {
            return try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    context.getSystemService(SmsManager::class.java)
                        ?: @Suppress("DEPRECATION") SmsManager.getDefault()
                } else {
                    @Suppress("DEPRECATION")
                    SmsManager.getDefault()
                }
            } catch (e: Exception) {
                Log.w(TAG, "Primary SmsManager lookup failed, trying getDefault(): ${e.message}")
                try {
                    @Suppress("DEPRECATION")
                    SmsManager.getDefault()
                } catch (fallbackEx: Exception) {
                    Log.e(TAG, "SmsManager unavailable on device: ${fallbackEx.message}", fallbackEx)
                    null
                }
            }
        }

        /**
         * Dispatches a free native SIM SMS using Android's SmsManager with PendingIntent
         * delivery confirmation and complete error handling (no silent failures).
         */
        fun sendNativeSimSms(
            context: Context,
            rawPhoneNumber: String,
            rawMessageText: String,
            isTest: Boolean = false
        ): Pair<Boolean, String> {
            val cleanNumber = normalizePhoneNumber(rawPhoneNumber)
            if (!isValidPhoneNumber(cleanNumber)) {
                val errMsg = "সঠিক ফোন নম্বর পাওয়া যায়নি ($rawPhoneNumber)"
                Log.w(TAG, errMsg)
                AppPreferences.setLastAutoSmsStatusSync(context, errMsg, isSuccess = false)
                return false to errMsg
            }

            val hasSmsPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasSmsPermission) {
                val errMsg = "এসএমএস পাঠানোর অনুমতি (SEND_SMS) দেওয়া হয়নি"
                Log.w(TAG, "SEND_SMS permission not granted; cannot send auto-reply to $cleanNumber.")
                AppPreferences.setLastAutoSmsStatusSync(context, errMsg, isSuccess = false)
                if (!isTest) {
                    SmsSentReceiver.postStatusNotification(
                        context = context,
                        title = "অটো-এসএমএস পাঠাতে অনুমতি প্রয়োজন",
                        shortText = errMsg,
                        bigText = "$cleanNumber নম্বরে অটো-এসএমএস পাঠানো যায়নি কারণ SEND_SMS পারমিশন চালু নেই। অ্যাপের মসজিদ মোড থেকে অনুমতি দিন।",
                        isError = true
                    )
                }
                return false to errMsg
            }

            val messageText = rawMessageText.trim().ifBlank { AppPreferences.DEFAULT_AUTO_REPLY_SMS }

            val smsManager = resolveSmsManager(context)
            if (smsManager == null) {
                val errMsg = "ডিভাইসে সিম/এসএমএস সেবা (SmsManager) পাওয়া যায়নি"
                Log.e(TAG, errMsg)
                AppPreferences.setLastAutoSmsStatusSync(context, errMsg, isSuccess = false)
                if (!isTest) {
                    SmsSentReceiver.postStatusNotification(
                        context = context,
                        title = "অটো-এসএমএস পাঠানো ব্যর্থ",
                        shortText = errMsg,
                        bigText = "$cleanNumber নম্বরে এসএমএস পাঠানো যায়নি: ডিভাইস সিম বা টেলিফোনি সাপোর্ট করছে না।",
                        isError = true
                    )
                }
                return false to errMsg
            }

            return try {
                val requestCode = (cleanNumber.hashCode() and 0x7FFFFFFF) % 100_000
                val sentIntent = Intent(context, SmsSentReceiver::class.java).apply {
                    action = SmsSentReceiver.ACTION_SMS_SENT
                    putExtra(SmsSentReceiver.EXTRA_PHONE_NUMBER, cleanNumber)
                    putExtra(SmsSentReceiver.EXTRA_MESSAGE_TEXT, messageText)
                    putExtra(SmsSentReceiver.EXTRA_IS_TEST, isTest)
                }
                val sentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
                val sentPendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    sentIntent,
                    sentFlags
                )

                val parts = try {
                    smsManager.divideMessage(messageText)
                } catch (_: Exception) {
                    null
                }

                if (parts == null || parts.size <= 1) {
                    smsManager.sendTextMessage(
                        cleanNumber,
                        null,
                        messageText,
                        sentPendingIntent,
                        null
                    )
                } else {
                    val sentIntents = ArrayList<PendingIntent?>(parts.size).apply {
                        for (i in parts.indices) {
                            add(if (i == parts.lastIndex) sentPendingIntent else null)
                        }
                    }
                    smsManager.sendMultipartTextMessage(
                        cleanNumber,
                        null,
                        parts,
                        sentIntents,
                        null
                    )
                }

                val pendingStatus = if (isTest) {
                    "টেস্ট এসএমএস সিমের মাধ্যমে প্রেরণ প্রক্রিয়াধীন ($cleanNumber)"
                } else {
                    "অটো-এসএমএস সিম থেকে প্রেরিত ($cleanNumber)"
                }
                AppPreferences.setLastAutoSmsStatusSync(context, pendingStatus, isSuccess = true)
                Log.i(TAG, "Dispatched native SIM SMS via SmsManager to $cleanNumber (isTest=$isTest)")
                true to "আপনার সিম কার্ড থেকে এসএমএস প্রেরণের জন্য পাঠানো হয়েছে"
            } catch (se: SecurityException) {
                val errMsg = "সিম এসএমএস নিরাপত্তা অনুমতি প্রত্যাখ্যাত: ${se.localizedMessage ?: "SEND_SMS"}"
                Log.e(TAG, errMsg, se)
                AppPreferences.setLastAutoSmsStatusSync(context, errMsg, isSuccess = false)
                if (!isTest) {
                    SmsSentReceiver.postStatusNotification(
                        context = context,
                        title = "অটো-এসএমএস পাঠানো ব্যর্থ",
                        shortText = "এসএমএস পাঠানোর অনুমতি নেই",
                        bigText = "$cleanNumber নম্বরে বার্তা পাঠানো যায়নি: $errMsg",
                        isError = true
                    )
                }
                false to errMsg
            } catch (e: Exception) {
                val errMsg = "এসএমএস পাঠাতে সমস্যা হয়েছে: ${e.localizedMessage ?: e.javaClass.simpleName}"
                Log.e(TAG, errMsg, e)
                AppPreferences.setLastAutoSmsStatusSync(context, errMsg, isSuccess = false)
                if (!isTest) {
                    SmsSentReceiver.postStatusNotification(
                        context = context,
                        title = "অটো-এসএমএস পাঠানো ব্যর্থ",
                        shortText = errMsg,
                        bigText = "$cleanNumber নম্বরে অটো-রিপ্লাই পাঠানো যায়নি।\nত্রুটি: $errMsg",
                        isError = true
                    )
                }
                false to errMsg
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            if (intent.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) return

            val stateStr = intent.getStringExtra(TelephonyManager.EXTRA_STATE) ?: return
            @Suppress("DEPRECATION")
            val incomingNumberExtra = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            val isAutoReplyEnabled = AppPreferences.isMosqueAutoReplySmsEnabledSync(context)
            val isMosqueActiveNow = MosqueModeManager.isCurrentlySilenced(context)

            when (stateStr) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    if (!isAutoReplyEnabled || !isMosqueActiveNow) {
                        return
                    }

                    val now = System.currentTimeMillis()
                    val previousRingTs = AppPreferences.getCallRingTimestampSync(context)
                    val wasAlreadyRinging = AppPreferences.getCallWasRingingSync(context) &&
                        (now - previousRingTs) < MAX_RING_STATE_AGE_MS

                    if (!wasAlreadyRinging) {
                        // Fresh incoming call: reset answered flag & clear any stale number first
                        AppPreferences.setCallWasRingingSync(context, true)
                        AppPreferences.setCallWasAnsweredSync(context, false)
                        AppPreferences.setCallRingTimestampSync(context, now)
                        AppPreferences.setCallStartedInMosqueSync(context, true)
                        AppPreferences.setCallLastNumberSync(context, null)
                    }

                    if (!incomingNumberExtra.isNullOrBlank()) {
                        AppPreferences.setCallLastNumberSync(context, incomingNumberExtra.trim())
                    }
                }

                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    if (AppPreferences.getCallWasRingingSync(context)) {
                        // Call was answered — mark answered so IDLE will NOT send auto-SMS
                        AppPreferences.setCallWasAnsweredSync(context, true)
                    }
                }

                TelephonyManager.EXTRA_STATE_IDLE -> {
                    val now = System.currentTimeMillis()
                    val wasRinging = AppPreferences.getCallWasRingingSync(context)
                    val wasAnswered = AppPreferences.getCallWasAnsweredSync(context)
                    val ringTs = AppPreferences.getCallRingTimestampSync(context)
                    val startedInMosque = AppPreferences.getCallStartedInMosqueSync(context)
                    val savedNumber = AppPreferences.getCallLastNumberSync(context)

                    // Always reset call tracking state immediately for the next call
                    AppPreferences.clearCallTrackingSync(context)

                    val isRecentRing = (now - ringTs) in 0L..MAX_RING_STATE_AGE_MS
                    if (!wasRinging || wasAnswered || !isRecentRing) {
                        return
                    }

                    if (!isAutoReplyEnabled || (!isMosqueActiveNow && !startedInMosque)) {
                        return
                    }

                    val directNumber = incomingNumberExtra?.takeIf { it.isNotBlank() }
                        ?: savedNumber?.takeIf { it.isNotBlank() }

                    if (directNumber != null) {
                        if (isValidPhoneNumber(directNumber)) {
                            dispatchAutoReplySms(context, directNumber)
                        } else {
                            Log.d(TAG, "Skipped invalid/private caller number: $directNumber")
                        }
                    } else {
                        // Fallback for Android 10+ devices where EXTRA_INCOMING_NUMBER may arrive via CallLog
                        val pendingResult = goAsync()
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val fallbackNumber = queryRecentMissedCallNumber(context)
                                if (isValidPhoneNumber(fallbackNumber)) {
                                    dispatchAutoReplySms(context, fallbackNumber!!)
                                } else {
                                    val errMsg = "কলারের নম্বর শনাক্ত করা যায়নি (READ_CALL_LOG অনুমোদিত কিনা যাচাই করুন)"
                                    Log.w(TAG, errMsg)
                                    AppPreferences.setLastAutoSmsStatusSync(context, errMsg, isSuccess = false)
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "CallLog fallback failed: ${e.message}", e)
                            } finally {
                                pendingResult.finish()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unhandled error in MosqueCallReceiver.onReceive: ${e.message}", e)
            try {
                AppPreferences.setLastAutoSmsStatusSync(
                    context,
                    "কল ডিটেকশন ত্রুটি: ${e.localizedMessage ?: "অজানা ত্রুটি"}",
                    isSuccess = false
                )
            } catch (_: Exception) {}
        }
    }

    private fun queryRecentMissedCallNumber(context: Context): String? {
        val hasCallLogPerm = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALL_LOG
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasCallLogPerm) return null

        return try {
            val cutoff = System.currentTimeMillis() - (2 * 60 * 1000L)
            val projection = arrayOf(
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE
            )
            val selection = "${CallLog.Calls.DATE} >= ? AND (${CallLog.Calls.TYPE} = ? OR ${CallLog.Calls.TYPE} = ? OR ${CallLog.Calls.TYPE} = ?)"
            val selectionArgs = arrayOf(
                cutoff.toString(),
                CallLog.Calls.MISSED_TYPE.toString(),
                CallLog.Calls.REJECTED_TYPE.toString(),
                CallLog.Calls.INCOMING_TYPE.toString()
            )
            context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                "${CallLog.Calls.DATE} DESC"
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getString(0)
                } else {
                    null
                }
            }
        } catch (se: SecurityException) {
            Log.w(TAG, "CallLog query denied: ${se.message}")
            null
        } catch (e: Exception) {
            Log.w(TAG, "CallLog query failed: ${e.message}")
            null
        }
    }

    private fun dispatchAutoReplySms(context: Context, rawPhoneNumber: String) {
        val cleanNumber = normalizePhoneNumber(rawPhoneNumber)
        if (!isValidPhoneNumber(cleanNumber)) return

        // Anti-spam cooldown check persisted across process restarts
        AppPreferences.pruneSmsCooldownsSync(context)
        val now = System.currentTimeMillis()
        val lastSent = AppPreferences.getSmsCooldownTimestampSync(context, cleanNumber)
        if (lastSent > 0L && (now - lastSent) < REPEAT_CALL_COOLDOWN_MS) {
            Log.d(TAG, "Suppressed duplicate SMS to $cleanNumber (10-min cooldown active).")
            return
        }

        val messageText = AppPreferences.getMosqueAutoReplyMsgSync(context)
        val (dispatched, _) = sendNativeSimSms(
            context = context,
            rawPhoneNumber = cleanNumber,
            rawMessageText = messageText,
            isTest = false
        )
        if (dispatched) {
            AppPreferences.setSmsCooldownTimestampSync(context, cleanNumber, now)
        }
    }
}
