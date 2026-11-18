package com.example.data.util

import android.content.Context
import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object TimeFormatUtils {

    fun is24HourFormat(context: Context): Boolean {
        return try {
            DateFormat.is24HourFormat(context)
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Returns device's local UTC offset in hours (e.g. +6.0 for Asia/Dhaka).
     */
    fun getLocalTimeZoneOffsetHours(timeMillis: Long = System.currentTimeMillis()): Double {
        val tz = TimeZone.getDefault()
        return tz.getOffset(timeMillis) / 3_600_000.0
    }

    /**
     * Returns human-readable local timezone label (e.g. "Asia/Dhaka (UTC+06:00)").
     */
    fun getLocalTimeZoneDisplay(timeMillis: Long = System.currentTimeMillis()): String {
        val tz = TimeZone.getDefault()
        val offsetMins = tz.getOffset(timeMillis) / 60_000
        val sign = if (offsetMins >= 0) "+" else "-"
        val absMins = kotlin.math.abs(offsetMins)
        val hours = absMins / 60
        val mins = absMins % 60
        return String.format(Locale.US, "%s (UTC%s%02d:%02d)", tz.id, sign, hours, mins)
    }

    /**
     * Formats decimal hour (e.g. 13.5 -> 13:30 or 01:30 PM) respecting 24-hour vs 12-hour
     */
    fun formatDecimalHour(
        hourDec: Double,
        is24Hour: Boolean,
        locale: Locale = Locale.getDefault()
    ): String {
        val totalMinutes = kotlin.math.round(((hourDec % 24.0 + 24.0) % 24.0) * 60.0).toInt()
        val hours = (totalMinutes / 60) % 24
        val mins = totalMinutes % 60
        return formatHourMinute(hours, mins, is24Hour, locale)
    }

    /**
     * Formats (hour24, minute) respecting 24-hour vs 12-hour local format.
     */
    fun formatHourMinute(
        hour24: Int,
        minute: Int,
        is24Hour: Boolean,
        locale: Locale = Locale.getDefault()
    ): String {
        val h = ((hour24 % 24) + 24) % 24
        val m = minute.coerceIn(0, 59)
        return if (is24Hour) {
            String.format(locale, "%02d:%02d", h, m)
        } else {
            val isPm = h >= 12
            val h12 = when {
                h == 0 -> 12
                h > 12 -> h - 12
                else -> h
            }
            val period = if (isPm) "PM" else "AM"
            String.format(locale, "%02d:%02d %s", h12, m, period)
        }
    }

    /**
     * Converts a stored canonical "HH:mm" (or "hh:mm a") string into a localized display string.
     */
    fun formatCanonicalTimeToLocal(
        canonicalTime: String,
        is24Hour: Boolean,
        locale: Locale = Locale.getDefault()
    ): String {
        val parsed = parseTimeString(canonicalTime) ?: return canonicalTime
        return formatHourMinute(parsed.first, parsed.second, is24Hour, locale)
    }

    /**
     * Formats (hour24, minute) into canonical ASCII "HH:mm" for reliable internal storage.
     */
    fun toCanonicalTime24(hour24: Int, minute: Int): String {
        val h = ((hour24 % 24) + 24) % 24
        val m = minute.coerceIn(0, 59)
        return String.format(Locale.US, "%02d:%02d", h, m)
    }

    /**
     * Converts epoch millis to canonical "HH:mm" in device local timezone.
     */
    fun epochMillisToCanonicalTime24(epochMillis: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = epochMillis }
        return toCanonicalTime24(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
    }

    /**
     * Formats epoch time in millis to localized time string
     */
    fun formatEpochMillis(
        epochMillis: Long,
        is24Hour: Boolean,
        locale: Locale = Locale.getDefault()
    ): String {
        val cal = Calendar.getInstance().apply { timeInMillis = epochMillis }
        return formatHourMinute(
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            is24Hour,
            locale
        )
    }

    /**
     * Formats countdown interval (e.g. "01:25:40")
     */
    fun formatCountdown(
        remainingMillis: Long,
        locale: Locale = Locale.getDefault()
    ): String {
        val rem = maxOf(0L, remainingMillis)
        val hours = rem / (1000 * 3600)
        val mins = (rem % (1000 * 3600)) / (1000 * 60)
        val secs = (rem % (1000 * 60)) / 1000
        return String.format(locale, "%02d:%02d:%02d", hours, mins, secs)
    }

    /**
     * Formats hours and minutes duration (e.g. "01h 25m")
     */
    fun formatHoursMins(
        remainingMillis: Long,
        locale: Locale = Locale.getDefault()
    ): String {
        val rem = maxOf(0L, remainingMillis)
        val hours = rem / (1000 * 3600)
        val mins = (rem % (1000 * 3600)) / (1000 * 60)
        return String.format(locale, "%02dh %02dm", hours, mins)
    }

    /**
     * Formats a time window range (e.g. "01:10 PM - 01:40 PM" or "13:10 - 13:40")
     */
    fun formatTimeRange(
        startMillis: Long,
        endMillis: Long,
        is24Hour: Boolean,
        locale: Locale = Locale.getDefault()
    ): String {
        val start = formatEpochMillis(startMillis, is24Hour, locale)
        val end = formatEpochMillis(endMillis, is24Hour, locale)
        return "$start - $end"
    }

    /**
     * Normalizes Bengali or Arabic-Indic numerals into ASCII digits for parsing.
     */
    private fun normalizeDigits(input: String): String {
        val sb = StringBuilder(input.length)
        for (ch in input) {
            when (ch) {
                in '০'..'৯' -> sb.append(('0'.code + (ch.code - '০'.code)).toChar())
                in '٠'..'٩' -> sb.append(('0'.code + (ch.code - '٠'.code)).toChar())
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Parses "HH:mm" or "hh:mm a" (supporting English/Bengali digits) into (hour24, minute)
     */
    fun parseTimeString(timeStr: String): Pair<Int, Int>? {
        val trimmed = normalizeDigits(timeStr.trim())
        if (trimmed.isEmpty()) return null
        return try {
            if (trimmed.contains(":")) {
                val parts = trimmed.split(":")
                val hour = parts[0].trim().toIntOrNull() ?: return null
                val minPart = parts[1].trim().split(Regex("\\s+"))[0].filter { it.isDigit() }
                val minute = minPart.toIntOrNull() ?: return null
                if (minute !in 0..59) return null
                val upper = trimmed.uppercase(Locale.US)
                val isPm = upper.contains("PM") || trimmed.contains("অপরাহ্ণ") || trimmed.contains("বিকাল") || trimmed.contains("সন্ধ্যা") || trimmed.contains("রাত")
                val isAm = upper.contains("AM") || trimmed.contains("পূর্বাহ্ণ") || trimmed.contains("ভোর") || trimmed.contains("সকাল")
                val finalHour = when {
                    isPm && hour in 1..11 -> hour + 12
                    isAm && hour == 12 -> 0
                    else -> hour
                }
                if (finalHour !in 0..23) return null
                Pair(finalHour, minute)
            } else null
        } catch (_: Exception) {
            null
        }
    }
}
