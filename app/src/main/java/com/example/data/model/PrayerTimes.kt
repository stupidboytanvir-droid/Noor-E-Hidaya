package com.example.data.model

import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.*

enum class CalculationMethod(val displayName: String, val fajrAngle: Double, val ishaAngle: Double) {
    MUSLIM_WORLD_LEAGUE("Muslim World League", 18.0, 17.0),
    KARACHI("Karachi (Univ. of Islamic Sciences)", 18.0, 18.0),
    ISNA("Islamic Society of North America (ISNA)", 15.0, 15.0),
    MAKKAH("Umm Al-Qura, Makkah", 18.5, 19.0)
}

enum class JuristicMethod(
    val id: String,
    val titleBengali: String,
    val displayName: String,
    val shadowFactor: Double,
    val descriptionBengali: String
) {
    HANAFI(
        id = "HANAFI",
        titleBengali = "হানাফী (Hanafi)",
        displayName = "Hanafi",
        shadowFactor = 2.0,
        descriptionBengali = "ইমাম আবু হানিফা (রহ.) — আসর ওয়াক্ত শুরু: বস্তুর ছায়া ২ গুণ"
    ),
    SHAFII(
        id = "SHAFII",
        titleBengali = "শাফেয়ী (Shafi'i)",
        displayName = "Shafi'i",
        shadowFactor = 1.0,
        descriptionBengali = "ইমাম শাফেয়ী (রহ.) — আসর ওয়াক্ত শুরু: বস্তুর ছায়া ১ গুণ"
    ),
    MALIKI(
        id = "MALIKI",
        titleBengali = "মালিকী (Maliki)",
        displayName = "Maliki",
        shadowFactor = 1.0,
        descriptionBengali = "ইমাম মালিক (রহ.) — আসর ওয়াক্ত শুরু: বস্তুর ছায়া ১ গুণ"
    ),
    HANBALI(
        id = "HANBALI",
        titleBengali = "হাম্বলী (Hanbali)",
        displayName = "Hanbali",
        shadowFactor = 1.0,
        descriptionBengali = "ইমাম আহমদ ইবনে হাম্বল (রহ.) — আসর ওয়াক্ত শুরু: বস্তুর ছায়া ১ গুণ"
    );

    companion object {
        val STANDARD = SHAFII // Standard fallback for older preferences referencing STANDARD

        fun fromString(value: String): JuristicMethod {
            return when (value.uppercase()) {
                "HANAFI" -> HANAFI
                "SHAFII", "SHAFI'I" -> SHAFII
                "MALIKI" -> MALIKI
                "HANBALI" -> HANBALI
                "STANDARD" -> SHAFII
                else -> HANAFI
            }
        }
    }
}

enum class PrayerName(val englishName: String, val arabicName: String, val bengaliName: String) {
    FAJR("Fajr", "الفجر", "ফজর"),
    SUNRISE("Sunrise", "الشروق", "সূর্যোদয়"),
    DHUHR("Dhuhr", "الظهر", "যোহর"),
    ASR("Asr", "العصر", "আসর"),
    MAGHRIB("Maghrib", "المغرب", "মাগরিব"),
    ISHA("Isha", "العشاء", "ইশা")
}

data class PrayerTimeItem(
    val prayer: PrayerName,
    val timeMillis: Long,
    val formattedTime: String,
    val isNext: Boolean = false,
    val isPassed: Boolean = false,
    val endTimeMillis: Long = 0L,
    val formattedEndTime: String = "",
    val formattedRange: String = formattedTime,
    val jamatTimeMillis: Long = 0L,
    val formattedJamatTime: String = "",
    val isCustomTime: Boolean = false,
    val isCustomJamat: Boolean = false
)

data class ProhibitedTimes(
    val sunriseStart: String,
    val sunriseEnd: String,
    val zawalStart: String,
    val zawalEnd: String,
    val sunsetStart: String,
    val sunsetEnd: String
)

data class NafalTimes(
    val ishraq: String,
    val chasht: String,
    val awwabin: String,
    val tahajjudLastThird: String
)

data class PrayerSchedule(
    val items: List<PrayerTimeItem>,
    val currentWaqt: PrayerName,
    val nextWaqt: PrayerName,
    val remainingMillis: Long,
    val remainingFormatted: String,
    val progressPercent: Float,
    val sahurRemainingFormatted: String,
    val iftarRemainingFormatted: String,
    val prohibitedTimes: ProhibitedTimes,
    val nafalTimes: NafalTimes,
    val hijriDate: String,
    val gregorianDate: String,
    val locationName: String = "Dhaka, Bangladesh"
)

object PrayerCalculator {

    fun calculateSchedule(
        latitude: Double = 23.8103, // Default Dhaka
        longitude: Double = 90.4125,
        timeZone: Double = 6.0,
        calendar: Calendar = Calendar.getInstance(),
        method: CalculationMethod = CalculationMethod.MUSLIM_WORLD_LEAGUE,
        juristic: JuristicMethod = JuristicMethod.STANDARD,
        contentLang: String = "bn",
        is24Hour: Boolean = false,
        hijriOffset: Int = 0
    ): PrayerSchedule {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Julian date
        val d = 367 * year - (7 * (year + (month + 9) / 12)) / 4 + (275 * month) / 9 + day - 730531.5

        // Sun coordinates
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(toRadians(g)) + 0.020 * sin(toRadians(2 * g)))
        val e = 23.439 - 0.00000036 * d
        val ra = toDegrees(atan2(cos(toRadians(e)) * sin(toRadians(l)), cos(toRadians(l)))) / 15.0
        val dSun = fixHour(ra)
        val declination = toDegrees(asin(sin(toRadians(e)) * sin(toRadians(l))))
        val eqOfTime = q / 15.0 - dSun

        val noon = fixHour(12.0 + timeZone - longitude / 15.0 - eqOfTime)

        fun sunAngleTime(angle: Double, isMorning: Boolean): Double {
            val cosH = (-sin(toRadians(angle)) - sin(toRadians(latitude)) * sin(toRadians(declination))) /
                    (cos(toRadians(latitude)) * cos(toRadians(declination)))
            if (cosH > 1.0 || cosH < -1.0) return noon
            val h = toDegrees(acos(cosH)) / 15.0
            return noon + (if (isMorning) -h else h)
        }

        fun asrTime(factor: Double): Double {
            val angle = -toDegrees(atan(1.0 / (factor + tan(toRadians(abs(latitude - declination))))))
            return sunAngleTime(angle, false)
        }

        val fajrHour = sunAngleTime(method.fajrAngle, true)
        val sunriseHour = sunAngleTime(0.833, true)
        val dhuhrHour = noon + (2.0 / 60.0) // 2 min safety
        val asrHour = asrTime(juristic.shadowFactor)
        val maghribHour = sunAngleTime(0.833, false)
        val ishaHour = if (method == CalculationMethod.MAKKAH) {
            maghribHour + 1.5
        } else {
            sunAngleTime(method.ishaAngle, false)
        }

        fun toEpoch(hourDec: Double): Long {
            val cal = calendar.clone() as Calendar
            val hours = hourDec.toInt()
            val mins = ((hourDec - hours) * 60).toInt()
            val secs = ((((hourDec - hours) * 60) - mins) * 60).toInt()
            cal.set(Calendar.HOUR_OF_DAY, hours)
            cal.set(Calendar.MINUTE, mins)
            cal.set(Calendar.SECOND, secs)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

        fun format(hourDec: Double): String {
            return com.example.data.util.TimeFormatUtils.formatDecimalHour(hourDec, is24Hour)
        }

        val now = System.currentTimeMillis()
        val fajrTime = toEpoch(fajrHour)
        val sunriseTime = toEpoch(sunriseHour)
        val dhuhrTime = toEpoch(dhuhrHour)
        val asrTime = toEpoch(asrHour)
        val maghribTime = toEpoch(maghribHour)
        val ishaTime = toEpoch(ishaHour)

        val tomorrowFajrTime = fajrTime + 24 * 3600 * 1000L
        val tomorrowFajrFormatted = format(fajrHour)

        fun defaultJamat(prayer: PrayerName, startMillis: Long): Pair<Long, String> {
            val offsetMins = com.example.data.mosque.PrayerMosqueConfig.defaultJamatOffsetFor(prayer)
            val jamatMs = startMillis + offsetMins * 60_000L
            return jamatMs to com.example.data.util.TimeFormatUtils.formatEpochMillis(jamatMs, is24Hour)
        }

        val (fajrJamatMs, fajrJamatStr) = defaultJamat(PrayerName.FAJR, fajrTime)
        val (dhuhrJamatMs, dhuhrJamatStr) = defaultJamat(PrayerName.DHUHR, dhuhrTime)
        val (asrJamatMs, asrJamatStr) = defaultJamat(PrayerName.ASR, asrTime)
        val (maghribJamatMs, maghribJamatStr) = defaultJamat(PrayerName.MAGHRIB, maghribTime)
        val (ishaJamatMs, ishaJamatStr) = defaultJamat(PrayerName.ISHA, ishaTime)

        val timesList = listOf(
            PrayerTimeItem(
                prayer = PrayerName.FAJR,
                timeMillis = fajrTime,
                formattedTime = format(fajrHour),
                endTimeMillis = sunriseTime,
                formattedEndTime = format(sunriseHour),
                formattedRange = "${format(fajrHour)} - ${format(sunriseHour)}",
                jamatTimeMillis = fajrJamatMs,
                formattedJamatTime = fajrJamatStr
            ),
            PrayerTimeItem(
                prayer = PrayerName.SUNRISE,
                timeMillis = sunriseTime,
                formattedTime = format(sunriseHour),
                endTimeMillis = sunriseTime + 15 * 60 * 1000L,
                formattedEndTime = format(sunriseHour + 0.25),
                formattedRange = "${format(sunriseHour)} - ${format(sunriseHour + 0.25)}"
            ),
            PrayerTimeItem(
                prayer = PrayerName.DHUHR,
                timeMillis = dhuhrTime,
                formattedTime = format(dhuhrHour),
                endTimeMillis = asrTime,
                formattedEndTime = format(asrHour),
                formattedRange = "${format(dhuhrHour)} - ${format(asrHour)}",
                jamatTimeMillis = dhuhrJamatMs,
                formattedJamatTime = dhuhrJamatStr
            ),
            PrayerTimeItem(
                prayer = PrayerName.ASR,
                timeMillis = asrTime,
                formattedTime = format(asrHour),
                endTimeMillis = maghribTime,
                formattedEndTime = format(maghribHour),
                formattedRange = "${format(asrHour)} - ${format(maghribHour)}",
                jamatTimeMillis = asrJamatMs,
                formattedJamatTime = asrJamatStr
            ),
            PrayerTimeItem(
                prayer = PrayerName.MAGHRIB,
                timeMillis = maghribTime,
                formattedTime = format(maghribHour),
                endTimeMillis = ishaTime,
                formattedEndTime = format(ishaHour),
                formattedRange = "${format(maghribHour)} - ${format(ishaHour)}",
                jamatTimeMillis = maghribJamatMs,
                formattedJamatTime = maghribJamatStr
            ),
            PrayerTimeItem(
                prayer = PrayerName.ISHA,
                timeMillis = ishaTime,
                formattedTime = format(ishaHour),
                endTimeMillis = tomorrowFajrTime,
                formattedEndTime = tomorrowFajrFormatted,
                formattedRange = "${format(ishaHour)} - $tomorrowFajrFormatted",
                jamatTimeMillis = ishaJamatMs,
                formattedJamatTime = ishaJamatStr
            )
        )

        // Find current and next Waqt
        val validPrayerTimes = timesList.filter { it.prayer != PrayerName.SUNRISE }
        var currentWaqt = PrayerName.ISHA
        var nextWaqt = PrayerName.FAJR
        var nextTime = fajrTime + 24 * 3600 * 1000L
        var prevTime = ishaTime

        if (now < fajrTime) {
            currentWaqt = PrayerName.ISHA
            nextWaqt = PrayerName.FAJR
            nextTime = fajrTime
            prevTime = ishaTime - 24 * 3600 * 1000L
        } else if (now in fajrTime until sunriseTime) {
            currentWaqt = PrayerName.FAJR
            nextWaqt = PrayerName.DHUHR
            nextTime = dhuhrTime
            prevTime = fajrTime
        } else if (now in sunriseTime until dhuhrTime) {
            currentWaqt = PrayerName.FAJR // or Duha
            nextWaqt = PrayerName.DHUHR
            nextTime = dhuhrTime
            prevTime = sunriseTime
        } else if (now in dhuhrTime until asrTime) {
            currentWaqt = PrayerName.DHUHR
            nextWaqt = PrayerName.ASR
            nextTime = asrTime
            prevTime = dhuhrTime
        } else if (now in asrTime until maghribTime) {
            currentWaqt = PrayerName.ASR
            nextWaqt = PrayerName.MAGHRIB
            nextTime = maghribTime
            prevTime = asrTime
        } else if (now in maghribTime until ishaTime) {
            currentWaqt = PrayerName.MAGHRIB
            nextWaqt = PrayerName.ISHA
            nextTime = ishaTime
            prevTime = maghribTime
        } else {
            currentWaqt = PrayerName.ISHA
            nextWaqt = PrayerName.FAJR
            nextTime = fajrTime + 24 * 3600 * 1000L
            prevTime = ishaTime
        }

        val remainingMillis = max(0L, nextTime - now)
        val remHours = remainingMillis / (1000 * 3600)
        val remMins = (remainingMillis % (1000 * 3600)) / (1000 * 60)
        val remSecs = (remainingMillis % (1000 * 60)) / 1000
        val remainingFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", remHours, remMins, remSecs)

        val totalInterval = max(1L, nextTime - prevTime)
        val progressPercent = ((now - prevTime).toFloat() / totalInterval.toFloat()).coerceIn(0f, 1f)

        // Sahur (ends at Fajr) & Iftar (at Maghrib)
        val sahurTarget = if (now < fajrTime) fajrTime else fajrTime + 24 * 3600 * 1000L
        val sahurRem = max(0L, sahurTarget - now)
        val sH = sahurRem / (1000 * 3600)
        val sM = (sahurRem % (1000 * 3600)) / (1000 * 60)
        val sahurFormatted = String.format(Locale.getDefault(), "%02dh %02dm", sH, sM)

        val iftarTarget = if (now < maghribTime) maghribTime else maghribTime + 24 * 3600 * 1000L
        val iftarRem = max(0L, iftarTarget - now)
        val iH = iftarRem / (1000 * 3600)
        val iM = (iftarRem % (1000 * 3600)) / (1000 * 60)
        val iftarFormatted = String.format(Locale.getDefault(), "%02dh %02dm", iH, iM)

        // Prohibited Times (Makruh)
        val sunriseEndHour = sunriseHour + (15.0 / 60.0)
        val zawalStartHour = noon - (12.0 / 60.0)
        val sunsetStartHour = maghribHour - (15.0 / 60.0)
        val prohibitedTimes = ProhibitedTimes(
            sunriseStart = format(sunriseHour),
            sunriseEnd = format(sunriseEndHour),
            zawalStart = format(zawalStartHour),
            zawalEnd = format(noon),
            sunsetStart = format(sunsetStartHour),
            sunsetEnd = format(maghribHour)
        )

        // Nafal Times
        val ishraqHour = sunriseHour + (18.0 / 60.0)
        val chashtHour = sunriseHour + (60.0 / 60.0)
        val awwabinHour = maghribHour + (15.0 / 60.0)
        val nightDuration = ((fajrHour + 24.0) - maghribHour) % 24.0
        val lastThirdHour = ((maghribHour + nightDuration * (2.0 / 3.0)) % 24.0)

        val nafalTimes = NafalTimes(
            ishraq = format(ishraqHour),
            chasht = format(chashtHour),
            awwabin = format(awwabinHour),
            tahajjudLastThird = format(lastThirdHour)
        )

        // Accurate Hijri conversion with moon sighting offset
        val hijriDate = calculateHijriDate(calendar, hijriOffset, contentLang)
        val dateLocale = com.example.data.util.SupportedLanguage.fromCode(contentLang).locale
        val gregorianDate = String.format(dateLocale, "%te %tB, %tY", calendar, calendar, calendar)

        val items = timesList.map { item ->
            item.copy(
                isNext = item.prayer == nextWaqt,
                isPassed = now > item.timeMillis
            )
        }

        return PrayerSchedule(
            items = items,
            currentWaqt = currentWaqt,
            nextWaqt = nextWaqt,
            remainingMillis = remainingMillis,
            remainingFormatted = remainingFormatted,
            progressPercent = progressPercent,
            sahurRemainingFormatted = sahurFormatted,
            iftarRemainingFormatted = iftarFormatted,
            prohibitedTimes = prohibitedTimes,
            nafalTimes = nafalTimes,
            hijriDate = hijriDate,
            gregorianDate = gregorianDate
        )
    }

    private fun calculateHijriDate(calendar: Calendar, offsetDays: Int = 0, lang: String = "bn"): String {
        val (yHijri, mHijri, dHijri) = HijriCalendarEngine.gregorianToHijri(calendar, offsetDays)
        return if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") {
            val monthNameBn = HijriCalendarEngine.HIJRI_MONTHS_BN.getOrElse(mHijri) { "হিজরি" }
            "${HijriDayData.formatBengaliNumber(dHijri)} $monthNameBn, ${HijriDayData.formatBengaliNumber(yHijri)} হিজরি"
        } else {
            val monthName = com.example.data.util.SupportedLanguage.localizedHijriMonth(mHijri + 1, lang)
            val dStr = com.example.data.util.SupportedLanguage.formatInt(dHijri, lang)
            val yStr = com.example.data.util.SupportedLanguage.formatInt(yHijri, lang)
            "$dStr $monthName, $yStr AH"
        }
    }

    private fun fixHour(a: Double): Double {
        var res = a - 24.0 * floor(a / 24.0)
        if (res < 0) res += 24.0
        return res
    }

    private fun fixAngle(a: Double): Double {
        var res = a - 360.0 * floor(a / 360.0)
        if (res < 0) res += 360.0
        return res
    }

    /**
     * Applies user-customized local mosque Waqt start times and Jamat times onto a calculated PrayerSchedule.
     * Ensures countdowns, current/next Waqt, and Jamat windows stay 100% consistent across the app.
     */
    fun applyMosqueConfigs(
        schedule: PrayerSchedule,
        configs: Map<PrayerName, com.example.data.mosque.PrayerMosqueConfig>,
        calendar: Calendar = Calendar.getInstance(),
        is24Hour: Boolean = false,
        now: Long = System.currentTimeMillis()
    ): PrayerSchedule {
        if (configs.isEmpty()) return schedule

        fun parseOnDay(timeStr: String?, dayOffset: Int = 0): Long? {
            val pair = timeStr?.let { com.example.data.util.TimeFormatUtils.parseTimeString(it) } ?: return null
            val cal = (calendar.clone() as Calendar).apply {
                if (dayOffset != 0) add(Calendar.DAY_OF_YEAR, dayOffset)
                set(Calendar.HOUR_OF_DAY, pair.first)
                set(Calendar.MINUTE, pair.second)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return cal.timeInMillis
        }

        // 1. Resolve custom Waqt start times
        val baseItems = schedule.items.map { item ->
            val cfg = configs[item.prayer]
            val customWaqtMs = parseOnDay(cfg?.customPrayerTime)
            if (customWaqtMs != null) {
                item.copy(
                    timeMillis = customWaqtMs,
                    formattedTime = com.example.data.util.TimeFormatUtils.formatEpochMillis(customWaqtMs, is24Hour),
                    isCustomTime = true
                )
            } else {
                item
            }
        }

        val fajrItem = baseItems.first { it.prayer == PrayerName.FAJR }
        val sunriseItem = baseItems.first { it.prayer == PrayerName.SUNRISE }
        val dhuhrItem = baseItems.first { it.prayer == PrayerName.DHUHR }
        val asrItem = baseItems.first { it.prayer == PrayerName.ASR }
        val maghribItem = baseItems.first { it.prayer == PrayerName.MAGHRIB }
        val ishaItem = baseItems.first { it.prayer == PrayerName.ISHA }

        val fajrTomorrowMs = parseOnDay(configs[PrayerName.FAJR]?.customPrayerTime, dayOffset = 1)
            ?: (fajrItem.timeMillis + 24 * 3600 * 1000L)

        // 2. Resolve end times & Jamat times
        val updatedItemsRaw = baseItems.map { item ->
            val nextEndMs = when (item.prayer) {
                PrayerName.FAJR -> sunriseItem.timeMillis
                PrayerName.SUNRISE -> sunriseItem.timeMillis + 15 * 60 * 1000L
                PrayerName.DHUHR -> asrItem.timeMillis
                PrayerName.ASR -> maghribItem.timeMillis
                PrayerName.MAGHRIB -> ishaItem.timeMillis
                PrayerName.ISHA -> fajrTomorrowMs
            }.coerceAtLeast(item.timeMillis + 60_000L)

            val formattedEnd = com.example.data.util.TimeFormatUtils.formatEpochMillis(nextEndMs, is24Hour)
            val cfg = configs[item.prayer]
            val customJamatMs = parseOnDay(cfg?.jamatTime)
            val jamatMs = when {
                item.prayer == PrayerName.SUNRISE -> 0L
                customJamatMs != null -> customJamatMs
                else -> item.timeMillis + (cfg?.defaultJamatOffsetMinutes ?: com.example.data.mosque.PrayerMosqueConfig.defaultJamatOffsetFor(item.prayer)) * 60_000L
            }
            val formattedJamat = if (jamatMs > 0L) {
                com.example.data.util.TimeFormatUtils.formatEpochMillis(jamatMs, is24Hour)
            } else ""

            item.copy(
                endTimeMillis = nextEndMs,
                formattedEndTime = formattedEnd,
                formattedRange = "${item.formattedTime} - $formattedEnd",
                jamatTimeMillis = jamatMs,
                formattedJamatTime = formattedJamat,
                isCustomJamat = customJamatMs != null
            )
        }

        val uFajr = updatedItemsRaw.first { it.prayer == PrayerName.FAJR }
        val uSunrise = updatedItemsRaw.first { it.prayer == PrayerName.SUNRISE }
        val uDhuhr = updatedItemsRaw.first { it.prayer == PrayerName.DHUHR }
        val uAsr = updatedItemsRaw.first { it.prayer == PrayerName.ASR }
        val uMaghrib = updatedItemsRaw.first { it.prayer == PrayerName.MAGHRIB }
        val uIsha = updatedItemsRaw.first { it.prayer == PrayerName.ISHA }

        var currentWaqt = PrayerName.ISHA
        var nextWaqt = PrayerName.FAJR
        var nextTime = uFajr.timeMillis + 24 * 3600 * 1000L
        var prevTime = uIsha.timeMillis

        if (now < uFajr.timeMillis) {
            currentWaqt = PrayerName.ISHA
            nextWaqt = PrayerName.FAJR
            nextTime = uFajr.timeMillis
            prevTime = uIsha.timeMillis - 24 * 3600 * 1000L
        } else if (now in uFajr.timeMillis until uSunrise.timeMillis) {
            currentWaqt = PrayerName.FAJR
            nextWaqt = PrayerName.DHUHR
            nextTime = uDhuhr.timeMillis
            prevTime = uFajr.timeMillis
        } else if (now in uSunrise.timeMillis until uDhuhr.timeMillis) {
            currentWaqt = PrayerName.FAJR
            nextWaqt = PrayerName.DHUHR
            nextTime = uDhuhr.timeMillis
            prevTime = uSunrise.timeMillis
        } else if (now in uDhuhr.timeMillis until uAsr.timeMillis) {
            currentWaqt = PrayerName.DHUHR
            nextWaqt = PrayerName.ASR
            nextTime = uAsr.timeMillis
            prevTime = uDhuhr.timeMillis
        } else if (now in uAsr.timeMillis until uMaghrib.timeMillis) {
            currentWaqt = PrayerName.ASR
            nextWaqt = PrayerName.MAGHRIB
            nextTime = uMaghrib.timeMillis
            prevTime = uAsr.timeMillis
        } else if (now in uMaghrib.timeMillis until uIsha.timeMillis) {
            currentWaqt = PrayerName.MAGHRIB
            nextWaqt = PrayerName.ISHA
            nextTime = uIsha.timeMillis
            prevTime = uMaghrib.timeMillis
        } else {
            currentWaqt = PrayerName.ISHA
            nextWaqt = PrayerName.FAJR
            nextTime = fajrTomorrowMs
            prevTime = uIsha.timeMillis
        }

        val remainingMillis = max(0L, nextTime - now)
        val remainingFormatted = com.example.data.util.TimeFormatUtils.formatCountdown(remainingMillis)
        val totalInterval = max(1L, nextTime - prevTime)
        val progressPercent = ((now - prevTime).toFloat() / totalInterval.toFloat()).coerceIn(0f, 1f)

        val sahurTarget = if (now < uFajr.timeMillis) uFajr.timeMillis else fajrTomorrowMs
        val sahurFormatted = com.example.data.util.TimeFormatUtils.formatHoursMins(sahurTarget - now)

        val iftarTarget = if (now < uMaghrib.timeMillis) uMaghrib.timeMillis else uMaghrib.timeMillis + 24 * 3600 * 1000L
        val iftarFormatted = com.example.data.util.TimeFormatUtils.formatHoursMins(iftarTarget - now)

        val finalItems = updatedItemsRaw.map { item ->
            item.copy(
                isNext = item.prayer == nextWaqt,
                isPassed = now > item.timeMillis
            )
        }

        return schedule.copy(
            items = finalItems,
            currentWaqt = currentWaqt,
            nextWaqt = nextWaqt,
            remainingMillis = remainingMillis,
            remainingFormatted = remainingFormatted,
            progressPercent = progressPercent,
            sahurRemainingFormatted = sahurFormatted,
            iftarRemainingFormatted = iftarFormatted
        )
    }

    private fun toRadians(deg: Double) = deg * Math.PI / 180.0
    private fun toDegrees(rad: Double) = rad * 180.0 / Math.PI
}
