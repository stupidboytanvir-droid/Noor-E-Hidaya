package com.example.data.mosque

import com.example.data.model.PrayerName

enum class MosqueRingerMode(
    val id: String,
    val titleBengali: String,
    val shortTitle: String,
    val subtitleBengali: String,
    val titleEnglish: String = "",
    val shortTitleEnglish: String = "",
    val subtitleEnglish: String = ""
) {
    VIBRATE(
        id = "VIBRATE",
        titleBengali = "ভাইব্রেশন মোড (Vibration)",
        shortTitle = "ভাইব্রেশন",
        subtitleBengali = "ফোন শুধু ভাইব্রেট করবে — জরুরি কল বুঝতে পারবেন, মসজিদে শব্দ হবে না",
        titleEnglish = "Vibration Mode",
        shortTitleEnglish = "Vibrate",
        subtitleEnglish = "Phone only vibrates — notice urgent calls without disturbing the mosque"
    ),
    SILENT(
        id = "SILENT",
        titleBengali = "সাইলেন্ট মোড (Silent)",
        shortTitle = "সাইলেন্ট",
        subtitleBengali = "ফোন সম্পূর্ণ নিঃশব্দ থাকবে — কোনো রিংটোন বা ভাইব্রেশন হবে না",
        titleEnglish = "Silent Mode",
        shortTitleEnglish = "Silent",
        subtitleEnglish = "Phone stays completely silent — no ringtone or vibration during prayer"
    );

    fun localizedTitle(lang: String): String =
        com.example.ui.theme.LocalizationRegistry.translate(lang, titleBengali, titleEnglish.ifBlank { titleBengali })

    fun localizedShortTitle(lang: String): String =
        com.example.ui.theme.LocalizationRegistry.translate(lang, shortTitle, shortTitleEnglish.ifBlank { shortTitle })

    fun localizedSubtitle(lang: String): String =
        com.example.ui.theme.LocalizationRegistry.translate(lang, subtitleBengali, subtitleEnglish.ifBlank { subtitleBengali })

    companion object {
        fun fromId(id: String?): MosqueRingerMode {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: VIBRATE
        }
    }
}

data class PrayerMosqueConfig(
    val prayer: PrayerName,
    val enabled: Boolean = true,
    val bufferBeforeMinutes: Int = 5,
    val durationMinutes: Int = defaultDurationFor(prayer),
    val customStartTime: String? = null,  // Optional fixed silent window start in "HH:mm"
    val customEndTime: String? = null,    // Optional fixed silent window end in "HH:mm"
    val customPrayerTime: String? = null, // Optional custom Waqt/Azan start in "HH:mm"
    val jamatTime: String? = null,        // Optional custom Mosque Jamat time in "HH:mm"
    val defaultJamatOffsetMinutes: Int = defaultJamatOffsetFor(prayer)
) {
    fun serialize(): String {
        return "$enabled|$bufferBeforeMinutes|$durationMinutes|${customStartTime.orEmpty()}|${customEndTime.orEmpty()}|${customPrayerTime.orEmpty()}|${jamatTime.orEmpty()}|$defaultJamatOffsetMinutes"
    }

    companion object {
        fun defaultDurationFor(prayer: PrayerName): Int {
            return when (prayer) {
                PrayerName.MAGHRIB -> 20
                PrayerName.DHUHR -> 30
                else -> 25
            }
        }

        fun defaultJamatOffsetFor(prayer: PrayerName): Int {
            return when (prayer) {
                PrayerName.FAJR -> 25
                PrayerName.DHUHR -> 60
                PrayerName.ASR -> 25
                PrayerName.MAGHRIB -> 5
                PrayerName.ISHA -> 30
                PrayerName.SUNRISE -> 0
            }
        }

        fun defaultFor(prayer: PrayerName): PrayerMosqueConfig {
            return PrayerMosqueConfig(
                prayer = prayer,
                enabled = true,
                bufferBeforeMinutes = 5,
                durationMinutes = defaultDurationFor(prayer),
                defaultJamatOffsetMinutes = defaultJamatOffsetFor(prayer)
            )
        }

        fun deserialize(prayer: PrayerName, raw: String?): PrayerMosqueConfig {
            if (raw.isNullOrBlank()) return defaultFor(prayer)
            return try {
                val parts = raw.split("|")
                val enabled = parts.getOrNull(0)?.toBooleanStrictOrNull() ?: true
                val buffer = parts.getOrNull(1)?.toIntOrNull() ?: 5
                val duration = parts.getOrNull(2)?.toIntOrNull() ?: defaultDurationFor(prayer)
                val start = parts.getOrNull(3)?.ifBlank { null }
                val end = parts.getOrNull(4)?.ifBlank { null }
                val customPrayer = parts.getOrNull(5)?.ifBlank { null }
                val jamat = parts.getOrNull(6)?.ifBlank { null }
                val jamatOffset = parts.getOrNull(7)?.toIntOrNull() ?: defaultJamatOffsetFor(prayer)
                PrayerMosqueConfig(
                    prayer = prayer,
                    enabled = enabled,
                    bufferBeforeMinutes = buffer,
                    durationMinutes = duration,
                    customStartTime = start,
                    customEndTime = end,
                    customPrayerTime = customPrayer,
                    jamatTime = jamat,
                    defaultJamatOffsetMinutes = jamatOffset
                )
            } catch (_: Exception) {
                defaultFor(prayer)
            }
        }
    }
}
