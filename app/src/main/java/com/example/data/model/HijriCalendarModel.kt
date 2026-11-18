package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.MintGlow
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.floor

/**
 * Category of Islamic Calendar Events
 */
enum class IslamicEventCategory(val labelBn: String, val labelEn: String, val badgeColor: Color) {
    EID("পবিত্র ঈদ", "Holy Eid", ChampagneGold),
    SACRED_MONTH("পবিত্র মাস", "Sacred Month", MintGlow),
    SUNNAH_FAST("সুন্নাত সিয়াম", "Sunnah Fast", MintGlow),
    BLESSED_NIGHT("বরকতময় রজনী", "Blessed Night", ChampagneGold),
    HISTORICAL("ঐতিহাসিক দিবস", "Historical Day", Color(0xFF64B5F6));

    fun localizedLabel(lang: String): String =
        com.example.ui.theme.LocalizationRegistry.translate(lang, labelBn, labelEn)
}

/**
 * Islamic Event Representation
 */
data class IslamicCalendarEvent(
    val titleBn: String,
    val titleAr: String,
    val category: IslamicEventCategory,
    val descriptionBn: String,
    val virtueRef: String,
    val isFastingRecommended: Boolean = false,
    val isFastingProhibited: Boolean = false,
    val titleEn: String = "",
    val descriptionEn: String = "",
    val virtueRefEn: String = ""
) {
    fun localizedTitle(lang: String): String =
        com.example.ui.theme.LocalizationRegistry.translate(lang, titleBn, titleEn.ifBlank { titleBn })
    fun localizedDescription(lang: String): String =
        com.example.ui.theme.LocalizationRegistry.translate(lang, descriptionBn, descriptionEn.ifBlank { descriptionBn })
    fun localizedVirtueRef(lang: String): String =
        com.example.ui.theme.LocalizationRegistry.translate(lang, virtueRef, virtueRefEn.ifBlank { virtueRef })
}

/**
 * Day Cell for Hijri Calendar Grid
 */
data class HijriDayData(
    val hijriDay: Int,
    val hijriMonthIndex: Int,
    val hijriYear: Int,
    val hijriMonthNameBn: String,
    val hijriMonthNameAr: String,
    val gregorianCalendar: Calendar,
    val gregorianDay: Int,
    val gregorianMonthShortBn: String,
    val gregorianMonthShortEn: String,
    val gregorianYear: Int,
    val dayOfWeek: Int, // Calendar.SUNDAY..Calendar.SATURDAY
    val dayOfWeekNameBn: String,
    val isToday: Boolean,
    val isFriday: Boolean,
    val isAyyamAlBid: Boolean,
    val isSunnahFastingDay: Boolean,
    val fastingNoteBn: String? = null,
    val specialEvents: List<IslamicCalendarEvent> = emptyList(),
    // Day-specific prayer times (approx calculated for the day's coordinates)
    val fajrTime: String = "04:45",
    val sunriseTime: String = "06:05",
    val dhuhrTime: String = "11:48",
    val asrTime: String = "15:02",
    val maghribTime: String = "17:20",
    val ishaTime: String = "18:40",
    val tahajjudTime: String = "03:45",
    val ishraqTime: String = "06:23",
    val hijriMonthNameEn: String = HijriCalendarEngine.HIJRI_MONTHS_EN.getOrElse(hijriMonthIndex) { "" },
    val dayOfWeekNameEn: String = "",
    val fastingNoteEn: String? = null
) {
    val formattedHijriDateBn: String
        get() = "${formatBengaliNumber(hijriDay)} $hijriMonthNameBn, ${formatBengaliNumber(hijriYear)} হিজরি"

    val formattedGregorianDateBn: String
        get() = "$dayOfWeekNameBn, ${formatBengaliNumber(gregorianDay)} $gregorianMonthShortBn ${formatBengaliNumber(gregorianYear)}"

    fun formattedHijriDate(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") {
            formattedHijriDateBn
        } else {
            val month = com.example.data.util.SupportedLanguage.localizedHijriMonth(hijriMonthIndex + 1, lang)
            "${com.example.data.util.SupportedLanguage.formatInt(hijriDay, lang)} $month, ${com.example.data.util.SupportedLanguage.formatInt(hijriYear, lang)} AH"
        }

    fun formattedGregorianDate(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") {
            formattedGregorianDateBn
        } else {
            val dow = dayOfWeekNameEn.ifBlank { dayOfWeekNameBn }
            "$dow, $gregorianDay $gregorianMonthShortEn $gregorianYear"
        }

    fun localizedFastingNote(lang: String): String? =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") fastingNoteBn
        else (fastingNoteEn ?: fastingNoteBn)

    fun localizedGregorianShort(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") gregorianMonthShortBn
        else gregorianMonthShortEn

    companion object {
        fun formatBengaliNumber(number: Int): String {
            val banglaDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
            return number.toString().map { char ->
                if (char in '0'..'9') banglaDigits[char - '0'] else char
            }.joinToString("")
        }
    }
}

/**
 * Hijri Month Representation with Grid layout info
 */
data class HijriMonthData(
    val year: Int,
    val monthIndex: Int,
    val monthNameBn: String,
    val monthNameAr: String,
    val monthNameEn: String,
    val daysCount: Int,
    val firstDayOfWeek: Int, // Calendar.SUNDAY to Calendar.SATURDAY
    val leadingEmptyDays: Int, // Number of empty grid slots before day 1 (0 to 6, Saturday-first or Sunday-first)
    val days: List<HijriDayData>,
    val majorEvents: List<IslamicCalendarEvent>
) {
    fun localizedMonthName(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") monthNameBn
        else com.example.data.util.SupportedLanguage.localizedHijriMonth(monthIndex + 1, lang)
}

/**
 * Overview for Yearly View
 */
data class HijriMonthOverview(
    val monthIndex: Int,
    val monthNameBn: String,
    val monthNameAr: String,
    val monthNameEn: String,
    val daysCount: Int,
    val startGregorianStr: String,
    val endGregorianStr: String,
    val keyEventsSummary: List<String>,
    val startGregorianStrEn: String = startGregorianStr,
    val endGregorianStrEn: String = endGregorianStr,
    val keyEventsSummaryEn: List<String> = keyEventsSummary
) {
    fun localizedMonthName(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") monthNameBn
        else com.example.data.util.SupportedLanguage.localizedHijriMonth(monthIndex + 1, lang)
    fun localizedStartGregorian(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") startGregorianStr else startGregorianStrEn
    fun localizedEndGregorian(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") endGregorianStr else endGregorianStrEn
    fun localizedKeyEvents(lang: String): List<String> =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") keyEventsSummary else keyEventsSummaryEn
}

/**
 * Yearly Summary
 */
data class HijriYearSummary(
    val year: Int,
    val months: List<HijriMonthOverview>
)

/**
 * Complete Hijri Calendar Engine & Astronomical Kuwaiti Converter
 */
object HijriCalendarEngine {

    val HIJRI_MONTHS_BN = listOf(
        "মুহাররম", "সফর", "রবিউল আউয়াল", "রবিউস সানি",
        "জুমাদাল উলা", "জুমাদাস সানিয়া", "রজব", "শা'বান",
        "রমাদান", "শাওয়াল", "জিলকদ", "জিলহজ্জ"
    )

    val HIJRI_MONTHS_AR = listOf(
        "المُحَرَّم", "صَفَر", "رَبِيع الأَوَّل", "رَبِيع الآخِر",
        "جُمَادَى الأُولَى", "جُمَادَى الآخِرَة", "رَجَب", "شَعْبَان",
        "رَمَضَان", "شَوَّال", "ذُو القَعْدَة", "ذُو الحِجَّة"
    )

    val HIJRI_MONTHS_EN = listOf(
        "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
        "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
        "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
    )

    private val GREG_MONTHS_BN = listOf(
        "জানু", "ফেব্রু", "মার্চ", "এপ্রিল", "মে", "জুন",
        "জুলাই", "আগস্ট", "সেপ্টে", "অক্টো", "নভে", "ডিসে"
    )

    private val GREG_MONTHS_EN = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    private val DAY_NAMES_BN = mapOf(
        Calendar.SATURDAY to "শনিবার",
        Calendar.SUNDAY to "রবিবার",
        Calendar.MONDAY to "সোমবার",
        Calendar.TUESDAY to "মঙ্গলবার",
        Calendar.WEDNESDAY to "বুধবার",
        Calendar.THURSDAY to "বৃহস্পতিবার",
        Calendar.FRIDAY to "শুক্রবার"
    )

    private val DAY_NAMES_EN = mapOf(
        Calendar.SATURDAY to "Saturday",
        Calendar.SUNDAY to "Sunday",
        Calendar.MONDAY to "Monday",
        Calendar.TUESDAY to "Tuesday",
        Calendar.WEDNESDAY to "Wednesday",
        Calendar.THURSDAY to "Thursday",
        Calendar.FRIDAY to "Friday"
    )

    /**
     * Check if a Hijri year is a leap year in the 30-year Islamic cycle
     */
    fun isHijriLeapYear(year: Int): Boolean {
        return ((11 * year + 14) % 30) < 11
    }

    /**
     * Number of days in a Hijri month
     */
    fun getDaysInHijriMonth(year: Int, monthIndex: Int): Int {
        return if (monthIndex % 2 == 0) {
            30 // Odd months (0, 2, 4, 6, 8, 10) have 30 days
        } else {
            if (monthIndex == 11 && isHijriLeapYear(year)) 30 else 29
        }
    }

    /**
     * Standard Islamic Tabular Calendar: Convert (Hijri Year, MonthIndex, Day) to Julian Day Number (JD)
     */
    fun hijriToJd(hy: Int, hm: Int, hd: Int): Long {
        val m = hm + 1
        val jd = floor((11.0 * hy + 3.0) / 30.0).toLong() +
                354L * hy +
                30L * m -
                floor((m - 1.0) / 2.0).toLong() +
                hd +
                1948440L -
                385L
        return jd
    }

    /**
     * Convert Gregorian Calendar date to Julian Day Number (JD)
     */
    fun gregorianToJd(year: Int, month: Int, day: Int): Long {
        var m = month
        var y = year
        if (m < 3) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return (floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524).toLong()
    }

    /**
     * Convert Julian Day Number (JD) to Gregorian Calendar date (Year, Month 1..12, Day 1..31)
     */
    fun jdToGregorian(jd: Long): Triple<Int, Int, Int> {
        val l = jd + 68569
        val n = floor((4.0 * l) / 146097.0).toLong()
        val l1 = l - floor((146097.0 * n + 3.0) / 4.0).toLong()
        val i = floor((4000.0 * (l1 + 1.0)) / 1461001.0).toLong()
        val l2 = l1 - floor((1461.0 * i) / 4.0).toLong() + 31
        val j = floor((80.0 * l2) / 2447.0).toLong()
        val d = (l2 - floor((2447.0 * j) / 80.0)).toInt()
        val l3 = floor(j / 11.0).toLong()
        val m = (j + 2 - 12 * l3).toInt()
        val y = (100 * (n - 49) + i + l3).toInt()
        return Triple(y, m, d)
    }

    /**
     * Convert Julian Day Number (JD) to Hijri (Year, MonthIndex 0..11, Day 1..30) using astronomical Kuwaiti algorithm
     */
    fun jdToHijri(jd: Long): Triple<Int, Int, Int> {
        val z = jd - 1948440 + 10632
        val n = floor((z - 1.0) / 10631.0)
        val z1 = z - 10631.0 * n + 354.0
        val j = (floor((10985.0 - z1) / 5316.0)) * (floor((50.0 * z1) / 17719.0)) +
                (floor(z1 / 5670.0)) * (floor((43.0 * z1) / 15238.0))
        val z2 = z1 - (floor((30.0 - j) / 15.0)) * (floor((17719.0 * j) / 50.0)) -
                (floor(j / 16.0)) * (floor((15238.0 * j) / 43.0)) + 29.0
        val m = floor((24.0 * z2) / 709.0).toInt().coerceIn(1, 12)
        val d = (z2 - floor((709.0 * m) / 24.0)).toInt().coerceIn(1, 30)
        val y = (30.0 * n + j - 30.0).toInt()
        return Triple(y, m - 1, d)
    }

    /**
     * Convert Hijri date to Gregorian Calendar with offset support
     */
    fun hijriToGregorian(hy: Int, hm: Int, hd: Int, offsetDays: Int = 0): Calendar {
        val jd = hijriToJd(hy, hm, hd) - offsetDays
        val (gy, gm, gd) = jdToGregorian(jd)
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, gy)
        cal.set(Calendar.MONTH, gm - 1)
        cal.set(Calendar.DAY_OF_MONTH, gd)
        cal.set(Calendar.HOUR_OF_DAY, 12)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal
    }

    /**
     * Convert Gregorian Calendar to Hijri date with offset support
     */
    fun gregorianToHijri(calendar: Calendar, offsetDays: Int = 0): Triple<Int, Int, Int> {
        val y = calendar.get(Calendar.YEAR)
        val m = calendar.get(Calendar.MONTH) + 1
        val d = calendar.get(Calendar.DAY_OF_MONTH)
        val jd = gregorianToJd(y, m, d) + offsetDays
        return jdToHijri(jd)
    }

    /**
     * Build Authentic Islamic Events Catalogue for a given Hijri month & day
     */
    fun getEventsForHijriDay(monthIndex: Int, day: Int): List<IslamicCalendarEvent> {
        val events = mutableListOf<IslamicCalendarEvent>()

        // 1. Muharram
        if (monthIndex == 0) {
            if (day == 1) {
                events.add(
                    IslamicCalendarEvent(
                        titleBn = "পবিত্র হিজরি নববর্ষ",
                        titleAr = "رَأْسُ السَّنَةِ الهِجْرِيَّة",
                        category = IslamicEventCategory.HISTORICAL,
                        descriptionBn = "রাসূলুল্লাহ ﷺ এর ঐতিহাসিক হিজরতের স্মৃতিবিজড়িত পবিত্র নতুন হিজরি বর্ষের প্রথম দিন।",
                        virtueRef = "আল্লাহর নির্ধারিত পবিত্র চার মাসের (আশহুরে হুরুম) অন্তর্ভুক্ত মুহাররম মাস।",
                        titleEn = "Islamic New Year (1 Muharram)",
                        descriptionEn = "The first day of the new Islamic Hijri year, commemorating the historic migration (Hijrah) of the Prophet ﷺ.",
                        virtueRefEn = "Muharram is one of the four sacred months ordained by Allah (Ashhur al-Hurum)."
                    )
                )
            } else if (day == 9) {
                events.add(
                    IslamicCalendarEvent(
                        titleBn = "পবিত্র তাসু'আ (৯ মুহাররম)",
                        titleAr = "يَوْمُ تَاسُوعَاء",
                        category = IslamicEventCategory.SUNNAH_FAST,
                        descriptionBn = "আশুরার পূর্বের দিন সুন্নাত নফল সিয়াম। ইয়াহুদিদের সাথে সাদৃশ্য বর্জনের লক্ষ্যে ৯ বা ১১ তারিখে রোজা রাখার নির্দেশ।",
                        virtueRef = "সহীহ মুসলিম: ১১৩৪ • আগামী বছর বেঁচে থাকলে আমি অবশ্যই ৯ তারিখে রোজা রাখব।",
                        isFastingRecommended = true,
                        titleEn = "Day of Tasu'a (9 Muharram)",
                        descriptionEn = "Recommended Sunnah voluntary fast on the day before Ashura to maintain distinction in worship.",
                        virtueRefEn = "Sahih Muslim: 1134 • 'If I live until next year, I will certainly fast on the ninth day.'"
                    )
                )
            } else if (day == 10) {
                events.add(
                    IslamicCalendarEvent(
                        titleBn = "পবিত্র আশুরা (১০ মুহাররম)",
                        titleAr = "يَوْمُ عَاشُورَاء",
                        category = IslamicEventCategory.SUNNAH_FAST,
                        descriptionBn = "ঐতিহাসিক পুণ্যময় দিবস। মুসা (আঃ) ও বনী ইসরাঈলের ফেরাউন থেকে মুক্তির দিন এবং কারবালার হৃদয়বিদারক শাহাদাতের স্মৃতি।",
                        virtueRef = "সহীহ মুসলিম: ১১৬২ • আশুরার রোজার মাধ্যমে পূর্ববর্তী এক বছরের সগিরা গুনাহ মাফ হয়।",
                        isFastingRecommended = true,
                        titleEn = "Day of Ashura (10 Muharram)",
                        descriptionEn = "A blessed historic day marking the deliverance of Prophet Musa (AS) and Bani Israel from Pharaoh, as well as the martyrdom at Karbala.",
                        virtueRefEn = "Sahih Muslim: 1162 • Fasting on Ashura expiates the minor sins of the preceding year."
                    )
                )
            }
        }

        // 3. Rabi' al-Awwal
        if (monthIndex == 2 && day == 12) {
            events.add(
                IslamicCalendarEvent(
                    titleBn = "সীরাতুন্নবী ﷺ ও আগমন দিবস",
                    titleAr = "مَوْلِدُ النَّبِيّ ﷺ",
                    category = IslamicEventCategory.HISTORICAL,
                    descriptionBn = "বিশ্বজাহানের রহমত আখেরি নবী হযরত মুহাম্মদ মুস্তফা ﷺ এর ধরাধামে আগমন ও ওফাত দিবস। সীরাত পাঠ ও দরূদ পাঠের দিন।",
                    virtueRef = "সূরা আল-আম্বিয়া: ১০৭ • 'আমি আপনাকে সমগ্র বিশ্বজগতের জন্য রহমতস্বরূপ প্রেরণ করেছি।'",
                    titleEn = "Seerat-un-Nabi ﷺ (12 Rabi' al-Awwal)",
                    descriptionEn = "Day of reflection on the life, mercy, and character of the final Messenger Muhammad ﷺ, marked by sending abundant Salawat and studying his Seerah.",
                    virtueRefEn = "Surah Al-Anbiya: 107 • 'And We have not sent you, [O Muhammad], except as a mercy to the worlds.'"
                )
            )
        }

        // 7. Rajab
        if (monthIndex == 6 && day == 27) {
            events.add(
                IslamicCalendarEvent(
                    titleBn = "পবিত্র শবে মেরাজ (লাইলাতুল ইসরা)",
                    titleAr = "لَيْلَةُ الإِسْرَاءِ وَالمِعْرَاج",
                    category = IslamicEventCategory.BLESSED_NIGHT,
                    descriptionBn = "রাসূলুল্লাহ ﷺ এর ঊর্ধ্বাকাশ ভ্রমণ, সিদরাতুল মুনতাহা গমন ও মহান আল্লাহর দরবার থেকে পাঁচ ওয়াক্ত সালাতের উপহার প্রাপ্তির রজনী।",
                    virtueRef = "সূরা বনী ইসরাঈল: ১ • 'পরম পবিত্র সেই সত্তা যিনি নিজ বান্দাকে রাতে মসজিদে হারাম থেকে মসজিদে আকসায় ভ্রমণ করিয়েছিলেন।'",
                    titleEn = "Al-Isra wal-Mi'raj (27 Rajab)",
                    descriptionEn = "Night commemorating the Prophet's ﷺ miraculous Night Journey and Ascension, during which the five daily prayers were gifted.",
                    virtueRefEn = "Surah Al-Isra: 1 • 'Exalted is He who took His Servant by night from al-Masjid al-Haram to al-Masjid al-Aqsa.'"
                )
            )
        }

        // 8. Sha'ban
        if (monthIndex == 7 && day == 15) {
            events.add(
                IslamicCalendarEvent(
                    titleBn = "পবিত্র শবে বরাত (লাইলাতুল বারাআত)",
                    titleAr = "لَيْلَةُ النِّصْفِ مِنْ شَعْبَان",
                    category = IslamicEventCategory.BLESSED_NIGHT,
                    descriptionBn = "মধ্য শাবানের বরকতময় রজনী। মহান আল্লাহ তায়ালা এ রাতে বান্দার প্রতি বিশেষ রহমতের দৃষ্টি দেন এবং অসংখ্য গুনাহগারকে ক্ষমা করেন।",
                    virtueRef = "সুনানে ইবনে মাজাহ: ১৩৮৮ • মধ্য শাবানের রাতে আল্লাহ বান্দাদের ক্ষমা করেন (মুশরিক ও বিদ্বেষ পোষণকারী ব্যতীত)।",
                    isFastingRecommended = true,
                    titleEn = "Mid-Sha'ban Night (15 Sha'ban)",
                    descriptionEn = "A blessed night of seeking forgiveness and mercy in the middle of Sha'ban.",
                    virtueRefEn = "Sunan Ibn Majah: 1388 • Allah looks upon His creation on the night of mid-Sha'ban and forgives all except the idolater and the quarrelsome."
                )
            )
        }

        // 9. Ramadan
        if (monthIndex == 8) {
            if (day == 1) {
                events.add(
                    IslamicCalendarEvent(
                        titleBn = "পবিত্র মাহে রমাদান শুরু",
                        titleAr = "بِدَايَةُ شَهْرِ رَمَضَانَ المُبَارَك",
                        category = IslamicEventCategory.SACRED_MONTH,
                        descriptionBn = "রহমত, মাগফিরাত ও নাজাতের মাস শুরু। জান্নাতের দরজা উন্মুক্ত এবং শয়তানকে শৃঙ্খলিত করা হয়।",
                        virtueRef = "সহীহ বুখারী: ৩৮ • 'যে ব্যক্তি ঈমানের সাথে সওয়াবের আশায় রমাদানের রোজা রাখে, তার অতীতের সব গুনাহ মাফ হয়ে যায়।'",
                        titleEn = "Start of Blessed Ramadan",
                        descriptionEn = "Beginning of the month of mercy, forgiveness, and salvation. The gates of Paradise are opened.",
                        virtueRefEn = "Sahih al-Bukhari: 38 • 'Whoever fasts Ramadan out of faith and seeking reward, his past sins will be forgiven.'"
                    )
                )
            } else if (day == 17) {
                events.add(
                    IslamicCalendarEvent(
                        titleBn = "ঐতিহাসিক বদর দিবস (ইয়াওমুল ফুরকান)",
                        titleAr = "غَزْوَةُ بَدْرٍ الكُبْرَى",
                        category = IslamicEventCategory.HISTORICAL,
                        descriptionBn = "ইসলামের প্রথম প্রকাশ্য বিজয় ও সত্য-মিথ্যার ফায়সালার মহান ঐতিহাসিক দিন (১৭ রমাদান, ২ হিজরি)।",
                        virtueRef = "সূরা আল-ইমরান: ১২৩ • 'অবশ্যই আল্লাহ তোমাদের বদরে সাহায্য করেছিলেন যখন তোমরা ছিলে দুর্বল।'",
                        titleEn = "Battle of Badr (17 Ramadan)",
                        descriptionEn = "The historic Day of Criterion (Yawm al-Furqan) marking Islam's first decisive victory in 2 AH.",
                        virtueRefEn = "Surah Ali 'Imran: 123 • 'And already had Allah given you victory at Badr while you were few in number.'"
                    )
                )
            } else if (day in listOf(21, 23, 25, 27, 29)) {
                val is27th = day == 27
                events.add(
                    IslamicCalendarEvent(
                        titleBn = if (is27th) "পবিত্র শবে কদর (লাইলাতুল কদর)" else "কদরের বিজোড় রজনী ($day রমাদান)",
                        titleAr = "لَيْلَةُ القَدْرِ المُبَارَكَة",
                        category = IslamicEventCategory.BLESSED_NIGHT,
                        descriptionBn = if (is27th) "হাজার মাসের চেয়ে শ্রেষ্ঠ মহিমান্বিত শবে কদর। রাতভর ইবাদত, কুরআন তিলাওয়াত ও কান্নাকাটি।" else "রমাদানের শেষ দশকের বিজোড় রাতে শবে কদর অন্বেষণ ও ইতিকাফের আমল।",
                        virtueRef = "সূরা আল-কদর: ৩ • 'লাইলাতুল কদর হাজার মাসের চেয়েও শ্রেষ্ঠ।'",
                        titleEn = if (is27th) "Laylatul Qadr (Night of Decree)" else "Odd Night of Last Ten ($day Ramadan)",
                        descriptionEn = if (is27th) "The Night of Decree, better than a thousand months—dedicated to night prayer, Quran recitation, and supplication." else "Seek Laylatul Qadr during the odd nights of the last ten days of Ramadan.",
                        virtueRefEn = "Surah Al-Qadr: 3 • 'The Night of Decree is better than a thousand months.'"
                    )
                )
            } else if (day == 30) {
                events.add(
                    IslamicCalendarEvent(
                        titleBn = "জুমাতুল বিদা ও রমাদানের সমাপনী",
                        titleAr = "جُمُعَةُ الوَدَاع",
                        category = IslamicEventCategory.SACRED_MONTH,
                        descriptionBn = "রমাদানের বিদায়ী দিন ও সদকাতুল ফিতর আদায়ের বিশেষ তাগিদ।",
                        virtueRef = "সহীহ বুখারী: ১৫০৩ • ঈদের নামাজের আগেই সদকাতুল ফিতর আদায়ের সুন্নাত নির্দেশ।",
                        titleEn = "End of Ramadan & Zakat al-Fitr",
                        descriptionEn = "Closing day of Ramadan with emphasis on fulfilling Zakat al-Fitr before Eid prayer.",
                        virtueRefEn = "Sahih al-Bukhari: 1503 • Zakat al-Fitr is to be paid before people go out for the Eid prayer."
                    )
                )
            }
        }

        // 10. Shawwal
        if (monthIndex == 9) {
            if (day == 1) {
                events.add(
                    IslamicCalendarEvent(
                        titleBn = "পবিত্র ঈদুল ফিতর",
                        titleAr = "عِيدُ الفِطْرِ المُبَارَك",
                        category = IslamicEventCategory.EID,
                        descriptionBn = "মুসলিম উম্মাহর মহান আনন্দ উৎসব। সালাতুল ঈদ, তাকবীর ও আত্মীয়তার সম্পর্ক বৃদ্ধির পুণ্যময় দিন। এ দিন রোজা রাখা হারাম।",
                        virtueRef = "সহীহ বুখারী: ১৯৯০ • ঈদের দিন রোজা রাখা কঠোরভাবে নিষিদ্ধ।",
                        isFastingProhibited = true,
                        titleEn = "Eid al-Fitr (1 Shawwal)",
                        descriptionEn = "Joyous festival of Eid prayer, Takbirat, charity, and strengthening family ties. Fasting on this day is strictly prohibited.",
                        virtueRefEn = "Sahih al-Bukhari: 1990 • Fasting on the two days of Eid is strictly forbidden."
                    )
                )
            } else if (day in 2..7) {
                events.add(
                    IslamicCalendarEvent(
                        titleBn = "শাওয়ালের ৬ রোজা (নফল)",
                        titleAr = "سِتَّةٌ مِنْ شَوَّال",
                        category = IslamicEventCategory.SUNNAH_FAST,
                        descriptionBn = "শাওয়াল মাসের যেকোনো ৬টি নফল রোজা আদায় করলে সারা বছর রোজা রাখার সমান সওয়াব অর্জিত হয়।",
                        virtueRef = "সহীহ মুসলিম: ১১৬৪ • 'যে রমাদানের পর শাওয়ালের ছয়টি রোজা রাখবে, সে যেন সারা বছরই রোজা রাখল।'",
                        isFastingRecommended = true,
                        titleEn = "Six Fasts of Shawwal (Sunnah)",
                        descriptionEn = "Fasting any six voluntary days in Shawwal after Ramadan carries the reward of fasting the entire year.",
                        virtueRefEn = "Sahih Muslim: 1164 • 'Whoever fasts Ramadan then follows it with six days of Shawwal, it is as if he fasted a lifetime.'"
                    )
                )
            }
        }

        // 12. Dhu al-Hijjah
        if (monthIndex == 11) {
            if (day in 1..8) {
                events.add(
                    IslamicCalendarEvent(
                        titleBn = "জিলহজ্জের প্রথম দশক ($day জিলহজ্জ)",
                        titleAr = "عَشْرُ ذِي الحِجَّة",
                        category = IslamicEventCategory.SACRED_MONTH,
                        descriptionBn = "বছরের সবচেয়ে মর্যাদাপূর্ণ ১০ দিন। নফল রোজা, তাকবীর, তাসবীহ ও তাহলীল পাঠের শ্রেষ্ঠ সময়।",
                        virtueRef = "সহীহ বুখারী: ৯৬৯ • জিলহজ্জের প্রথম দশকের আমলের চেয়ে উত্তম আর কোনো দিনের আমল নেই।",
                        isFastingRecommended = true,
                        titleEn = "First Ten Days of Dhu al-Hijjah (Day $day)",
                        descriptionEn = "The most virtuous ten days of the year—ideal for fasting, Takbir, Dhikr, and righteous deeds.",
                        virtueRefEn = "Sahih al-Bukhari: 969 • There are no days in which righteous deeds are more beloved to Allah than these ten days."
                    )
                )
            } else if (day == 9) {
                events.add(
                    IslamicCalendarEvent(
                        titleBn = "পবিত্র ইয়াওমে আরাফাহ (হজের দিন)",
                        titleAr = "يَوْمُ عَرَفَة",
                        category = IslamicEventCategory.SUNNAH_FAST,
                        descriptionBn = "হজের মূল দিন আরাফাতের ময়দানে অবস্থান। হাজীদের ব্যতীত সাধারণদের জন্য এ দিনের নফল রোজা বিগত ও আগামী এক বছরের গুনাহের কাফফারা।",
                        virtueRef = "সহীহ মুসলিম: ১১৬২ • আরাফার দিনের রোজা পূর্ববর্তী ও পরবর্তী বছরের গুনাহ ক্ষমা করে দেয়।",
                        isFastingRecommended = true,
                        titleEn = "Day of Arafah (9 Dhu al-Hijjah)",
                        descriptionEn = "Pinnacle day of Hajj. For non-pilgrims, fasting this day expiates the sins of the previous and coming year.",
                        virtueRefEn = "Sahih Muslim: 1162 • Fasting the Day of Arafah expiates the sins of the past year and the coming year."
                    )
                )
            } else if (day == 10) {
                events.add(
                    IslamicCalendarEvent(
                        titleBn = "পবিত্র ঈদুল আজহা (কুরবানী)",
                        titleAr = "عِيدُ الأَضْحَى المُبَارَك",
                        category = IslamicEventCategory.EID,
                        descriptionBn = "হযরত ইব্রাহীম (আঃ) ও ইসমাইল (আঃ) এর ত্যাগের স্মরণে ঈদের সালাত ও সামর্থ্যবানদের পশু কুরবানী। এ দিন রোজা রাখা হারাম।",
                        virtueRef = "সূরা আল-কাউসার: ২ • 'অতএব আপনার প্রতিপালকের উদ্দেশ্যে সালাত আদায় করুন এবং কুরবানী করুন।'",
                        isFastingProhibited = true,
                        titleEn = "Eid al-Adha (10 Dhu al-Hijjah)",
                        descriptionEn = "Festival of Sacrifice commemorating the devotion of Prophet Ibrahim (AS) and Ismail (AS). Fasting is prohibited on this day.",
                        virtueRefEn = "Surah Al-Kawthar: 2 • 'So pray to your Lord and sacrifice [to Him alone].'"
                    )
                )
            } else if (day in 11..13) {
                events.add(
                    IslamicCalendarEvent(
                        titleBn = "আইয়ামে তাশরীক ($day জিলহজ্জ)",
                        titleAr = "أَيَّامُ التَّشْرِيق",
                        category = IslamicEventCategory.HISTORICAL,
                        descriptionBn = "কুরবানীর অতিরিক্ত দিনসমূহ। প্রত্যেক ফরজ নামাজের পর তাকবীরে তাশরীক পাঠ ওয়াজিব। এ দিনসমূহে রোজা রাখা হারাম।",
                        virtueRef = "সহীহ মুসলিম: ১১৪১ • তাশরীকের দিনসমূহ হলো পানাহার ও আল্লাহর জিকিরের দিন।",
                        isFastingProhibited = true,
                        titleEn = "Days of Tashriq ($day Dhu al-Hijjah)",
                        descriptionEn = "Days following Eid al-Adha marked by Takbir al-Tashriq after obligatory prayers. Fasting is prohibited.",
                        virtueRefEn = "Sahih Muslim: 1141 • 'The days of Tashriq are days of eating, drinking, and remembrance of Allah.'"
                    )
                )
            }
        }

        // 13, 14, 15 of every month: Ayyam al-Bid (except 13 Dhu al-Hijjah where fasting is haram)
        if (day in 13..15 && !(monthIndex == 11 && day == 13)) {
            events.add(
                IslamicCalendarEvent(
                    titleBn = "আইয়ামে বিজ নফল সিয়াম ($day তারিখ)",
                    titleAr = "أَيَّامُ البِيض",
                    category = IslamicEventCategory.SUNNAH_FAST,
                    descriptionBn = "প্রতি হিজরি চান্দ্রমাসের ১৩, ১৪ ও ১৫ তারিখের বিশেষ সুন্নাত নফল রোজা। রাসুলুল্লাহ ﷺ কখনোই এ রোজা ছাড়তেন না।",
                    virtueRef = "সহীহ বুখারী: ১৯৮১ • প্রতি মাসে তিন দিন রোজা রাখা সারা বছর রোজা রাখার সমতুল্য।",
                    isFastingRecommended = true,
                    titleEn = "Ayyam al-Bid Sunnah Fast (Day $day)",
                    descriptionEn = "Recommended Sunnah fast on the 13th, 14th, and 15th (the White Days) of each lunar month.",
                    virtueRefEn = "Sahih al-Bukhari: 1981 • Fasting three days each month equals fasting for a lifetime."
                )
            )
        }

        return events
    }

    /**
     * Generate Full Month Grid Data
     */
    fun getMonthData(
        year: Int,
        monthIndex: Int,
        offsetDays: Int = 0,
        latitude: Double = 23.8103,
        longitude: Double = 90.4125
    ): HijriMonthData {
        val daysInMonth = getDaysInHijriMonth(year, monthIndex)
        val monthNameBn = HIJRI_MONTHS_BN[monthIndex]
        val monthNameAr = HIJRI_MONTHS_AR[monthIndex]
        val monthNameEn = HIJRI_MONTHS_EN[monthIndex]

        val todayCal = Calendar.getInstance()
        val todayGregY = todayCal.get(Calendar.YEAR)
        val todayGregM = todayCal.get(Calendar.MONTH) + 1
        val todayGregD = todayCal.get(Calendar.DAY_OF_MONTH)

        // Day 1
        val startCal = hijriToGregorian(year, monthIndex, 1, offsetDays)
        val firstDayOfWeek = startCal.get(Calendar.DAY_OF_WEEK) // 1=Sun, 2=Mon, ..., 7=Sat

        // Standard 7-column grid layout (Saturday first, or Sunday first).
        // In Bangladesh/Islamic apps, Saturday is column 0, Sunday is column 1, ..., Friday is column 6.
        val leadingEmptyDays = when (firstDayOfWeek) {
            Calendar.SATURDAY -> 0
            Calendar.SUNDAY -> 1
            Calendar.MONDAY -> 2
            Calendar.TUESDAY -> 3
            Calendar.WEDNESDAY -> 4
            Calendar.THURSDAY -> 5
            Calendar.FRIDAY -> 6
            else -> 0
        }

        val allMajorEvents = mutableListOf<IslamicCalendarEvent>()
        val dayList = mutableListOf<HijriDayData>()

        for (d in 1..daysInMonth) {
            val dayCal = hijriToGregorian(year, monthIndex, d, offsetDays)
            val gy = dayCal.get(Calendar.YEAR)
            val gm = dayCal.get(Calendar.MONTH) // 0-based
            val gd = dayCal.get(Calendar.DAY_OF_MONTH)
            val dow = dayCal.get(Calendar.DAY_OF_WEEK)

            val isToday = (gy == todayGregY && (gm + 1) == todayGregM && gd == todayGregD)
            val isFriday = dow == Calendar.FRIDAY
            val isMondayOrThursday = dow == Calendar.MONDAY || dow == Calendar.THURSDAY
            val isAyyamAlBid = d in 13..15 && !(monthIndex == 11 && d == 13)

            val dayEvents = getEventsForHijriDay(monthIndex, d)
            allMajorEvents.addAll(dayEvents)

            val hasProhibitedFasting = dayEvents.any { it.isFastingProhibited }
            val isSunnahFastingDay = (isMondayOrThursday || isAyyamAlBid || dayEvents.any { it.isFastingRecommended }) && !hasProhibitedFasting

            val fastingNote = when {
                hasProhibitedFasting -> "এ দিন রোজা রাখা নিষিদ্ধ (হারাম)"
                monthIndex == 8 -> "রমাদানের ফরজ সিয়াম"
                isAyyamAlBid -> "আইয়ামে বিজ নফল সিয়াম"
                dayEvents.any { it.isFastingRecommended } -> dayEvents.first { it.isFastingRecommended }.titleBn
                isMondayOrThursday -> if (dow == Calendar.MONDAY) "সোমবারের সুন্নাত রোজা" else "বৃহস্পতিবারের সুন্নাত রোজা"
                else -> null
            }

            val fastingNoteEn = when {
                hasProhibitedFasting -> "Fasting is prohibited (Haram) on this day"
                monthIndex == 8 -> "Obligatory Ramadan Fast"
                isAyyamAlBid -> "Ayyam al-Bid Sunnah Fast"
                dayEvents.any { it.isFastingRecommended } -> dayEvents.first { it.isFastingRecommended }.localizedTitle("en")
                isMondayOrThursday -> if (dow == Calendar.MONDAY) "Monday Sunnah Fast" else "Thursday Sunnah Fast"
                else -> null
            }

            // Calculate day-specific prayer times for this calendar date
            val schedule = PrayerCalculator.calculateSchedule(
                latitude = latitude,
                longitude = longitude,
                calendar = dayCal
            )

            val fajr = schedule.items.firstOrNull { it.prayer == PrayerName.FAJR }?.formattedTime ?: "04:45"
            val sunrise = schedule.items.firstOrNull { it.prayer == PrayerName.SUNRISE }?.formattedTime ?: "06:05"
            val dhuhr = schedule.items.firstOrNull { it.prayer == PrayerName.DHUHR }?.formattedTime ?: "11:48"
            val asr = schedule.items.firstOrNull { it.prayer == PrayerName.ASR }?.formattedTime ?: "15:02"
            val maghrib = schedule.items.firstOrNull { it.prayer == PrayerName.MAGHRIB }?.formattedTime ?: "17:20"
            val isha = schedule.items.firstOrNull { it.prayer == PrayerName.ISHA }?.formattedTime ?: "18:40"
            val tahajjud = schedule.nafalTimes.tahajjudLastThird
            val ishraq = schedule.nafalTimes.ishraq

            dayList.add(
                HijriDayData(
                    hijriDay = d,
                    hijriMonthIndex = monthIndex,
                    hijriYear = year,
                    hijriMonthNameBn = monthNameBn,
                    hijriMonthNameAr = monthNameAr,
                    gregorianCalendar = dayCal,
                    gregorianDay = gd,
                    gregorianMonthShortBn = GREG_MONTHS_BN[gm],
                    gregorianMonthShortEn = GREG_MONTHS_EN[gm],
                    gregorianYear = gy,
                    dayOfWeek = dow,
                    dayOfWeekNameBn = DAY_NAMES_BN[dow] ?: "বার",
                    isToday = isToday,
                    isFriday = isFriday,
                    isAyyamAlBid = isAyyamAlBid,
                    isSunnahFastingDay = isSunnahFastingDay,
                    fastingNoteBn = fastingNote,
                    specialEvents = dayEvents,
                    fajrTime = fajr,
                    sunriseTime = sunrise,
                    dhuhrTime = dhuhr,
                    asrTime = asr,
                    maghribTime = maghrib,
                    ishaTime = isha,
                    tahajjudTime = tahajjud,
                    ishraqTime = ishraq,
                    hijriMonthNameEn = monthNameEn,
                    dayOfWeekNameEn = DAY_NAMES_EN[dow] ?: "Day",
                    fastingNoteEn = fastingNoteEn
                )
            )
        }

        return HijriMonthData(
            year = year,
            monthIndex = monthIndex,
            monthNameBn = monthNameBn,
            monthNameAr = monthNameAr,
            monthNameEn = monthNameEn,
            daysCount = daysInMonth,
            firstDayOfWeek = firstDayOfWeek,
            leadingEmptyDays = leadingEmptyDays,
            days = dayList,
            majorEvents = allMajorEvents.distinctBy { it.titleBn }
        )
    }

    /**
     * Generate Full Year Overview
     */
    fun getYearSummary(year: Int, offsetDays: Int = 0): HijriYearSummary {
        val overviewList = mutableListOf<HijriMonthOverview>()

        for (m in 0..11) {
            val daysCount = getDaysInHijriMonth(year, m)
            val startCal = hijriToGregorian(year, m, 1, offsetDays)
            val endCal = hijriToGregorian(year, m, daysCount, offsetDays)

            val sDay = startCal.get(Calendar.DAY_OF_MONTH)
            val sMonth = GREG_MONTHS_BN[startCal.get(Calendar.MONTH)]
            val sMonthEn = GREG_MONTHS_EN[startCal.get(Calendar.MONTH)]
            val sYear = startCal.get(Calendar.YEAR)

            val eDay = endCal.get(Calendar.DAY_OF_MONTH)
            val eMonth = GREG_MONTHS_BN[endCal.get(Calendar.MONTH)]
            val eMonthEn = GREG_MONTHS_EN[endCal.get(Calendar.MONTH)]
            val eYear = endCal.get(Calendar.YEAR)

            val keyEvents = mutableListOf<String>()
            val keyEventsEn = mutableListOf<String>()
            when (m) {
                0 -> {
                    keyEvents.addAll(listOf("১ মুহাররম: হিজরি নববর্ষ", "১০ মুহাররম: পবিত্র আশুরা"))
                    keyEventsEn.addAll(listOf("1 Muharram: Hijri New Year", "10 Muharram: Day of Ashura"))
                }
                2 -> {
                    keyEvents.add("১২ রবিউল আউয়াল: সীরাতুন্নবী ﷺ")
                    keyEventsEn.add("12 Rabi' al-Awwal: Seerat-un-Nabi ﷺ")
                }
                6 -> {
                    keyEvents.add("২৭ রজব: পবিত্র শবে মেরাজ")
                    keyEventsEn.add("27 Rajab: Al-Isra wal-Mi'raj")
                }
                7 -> {
                    keyEvents.add("১৫ শা'বান: পবিত্র শবে বরাত")
                    keyEventsEn.add("15 Sha'ban: Mid-Sha'ban Night")
                }
                8 -> {
                    keyEvents.addAll(listOf("১ রমাদান: সিয়াম শুরু", "২৭ রমাদান: শবে কদর", "জুমাতুল বিদা"))
                    keyEventsEn.addAll(listOf("1 Ramadan: Fasting Begins", "27 Ramadan: Laylatul Qadr", "End of Ramadan"))
                }
                9 -> {
                    keyEvents.addAll(listOf("১ শাওয়াল: ঈদুল ফিতর", "শাওয়ালের ৬ রোজা"))
                    keyEventsEn.addAll(listOf("1 Shawwal: Eid al-Fitr", "6 Fasts of Shawwal"))
                }
                11 -> {
                    keyEvents.addAll(listOf("১-১০ জিলহজ্জের দশক", "৯ জিলহজ্জ: আরাফাহ", "১০ জিলহজ্জ: ঈদুল আজহা"))
                    keyEventsEn.addAll(listOf("1–10 Dhu al-Hijjah", "9 Dhu al-Hijjah: Arafah", "10 Dhu al-Hijjah: Eid al-Adha"))
                }
                else -> {
                    keyEvents.add("প্রতি মাসের ১৩, ১৪, ১৫: আইয়ামে বিজ রোজা")
                    keyEventsEn.add("13th–15th: Ayyam al-Bid Fasts")
                }
            }

            overviewList.add(
                HijriMonthOverview(
                    monthIndex = m,
                    monthNameBn = HIJRI_MONTHS_BN[m],
                    monthNameAr = HIJRI_MONTHS_AR[m],
                    monthNameEn = HIJRI_MONTHS_EN[m],
                    daysCount = daysCount,
                    startGregorianStr = "$sDay $sMonth $sYear",
                    endGregorianStr = "$eDay $eMonth $eYear",
                    keyEventsSummary = keyEvents,
                    startGregorianStrEn = "$sDay $sMonthEn $sYear",
                    endGregorianStrEn = "$eDay $eMonthEn $eYear",
                    keyEventsSummaryEn = keyEventsEn
                )
            )
        }

        return HijriYearSummary(year = year, months = overviewList)
    }
}
