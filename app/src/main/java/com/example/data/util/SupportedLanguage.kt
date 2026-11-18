package com.example.data.util

import java.util.Locale

enum class LanguageRegion(
    val id: String,
    val labelEnglish: String
) {
    BANGLADESH_SOUTH_ASIA("south_asia", "South Asia"),
    MIDDLE_EAST("middle_east", "Middle East & Global"),
    SOUTHEAST_ASIA("southeast_asia", "Southeast Asia"),
    EUROPE("europe", "Europe")
}

/**
 * Defines all 12 officially supported UI & Content languages across Asia, the Middle East, and Europe.
 */
enum class SupportedLanguage(
    val code: String,
    val nativeName: String,
    val englishName: String,
    val regionLabel: String,
    val badgeCode: String,
    val region: LanguageRegion,
    val isRtl: Boolean = false,
    val locale: Locale = Locale.forLanguageTag(code)
) {
    BENGALI(
        code = "bn",
        nativeName = "বাংলা",
        englishName = "Bengali",
        regionLabel = "Bangladesh / India",
        badgeCode = "BD",
        region = LanguageRegion.BANGLADESH_SOUTH_ASIA,
        isRtl = false,
        locale = Locale("bn", "BD")
    ),
    ENGLISH(
        code = "en",
        nativeName = "English",
        englishName = "English",
        regionLabel = "International",
        badgeCode = "EN",
        region = LanguageRegion.MIDDLE_EAST,
        isRtl = false,
        locale = Locale.ENGLISH
    ),
    ARABIC(
        code = "ar",
        nativeName = "العربية",
        englishName = "Arabic",
        regionLabel = "Middle East & North Africa",
        badgeCode = "AR",
        region = LanguageRegion.MIDDLE_EAST,
        isRtl = true,
        locale = Locale("ar", "SA")
    ),
    URDU(
        code = "ur",
        nativeName = "اردو",
        englishName = "Urdu",
        regionLabel = "Pakistan / India",
        badgeCode = "UR",
        region = LanguageRegion.BANGLADESH_SOUTH_ASIA,
        isRtl = true,
        locale = Locale("ur", "PK")
    ),
    INDONESIAN(
        code = "id",
        nativeName = "Bahasa Indonesia",
        englishName = "Indonesian",
        regionLabel = "Indonesia",
        badgeCode = "ID",
        region = LanguageRegion.SOUTHEAST_ASIA,
        isRtl = false,
        locale = Locale("id", "ID")
    ),
    MALAY(
        code = "ms",
        nativeName = "Bahasa Melayu",
        englishName = "Malay",
        regionLabel = "Malaysia / Brunei",
        badgeCode = "MS",
        region = LanguageRegion.SOUTHEAST_ASIA,
        isRtl = false,
        locale = Locale("ms", "MY")
    ),
    HINDI(
        code = "hi",
        nativeName = "हिन्दी",
        englishName = "Hindi",
        regionLabel = "India",
        badgeCode = "HI",
        region = LanguageRegion.BANGLADESH_SOUTH_ASIA,
        isRtl = false,
        locale = Locale("hi", "IN")
    ),
    TURKISH(
        code = "tr",
        nativeName = "Türkçe",
        englishName = "Turkish",
        regionLabel = "Türkiye / Europe",
        badgeCode = "TR",
        region = LanguageRegion.EUROPE,
        isRtl = false,
        locale = Locale("tr", "TR")
    ),
    GERMAN(
        code = "de",
        nativeName = "Deutsch",
        englishName = "German",
        regionLabel = "Germany / Austria / Switzerland",
        badgeCode = "DE",
        region = LanguageRegion.EUROPE,
        isRtl = false,
        locale = Locale.GERMAN
    ),
    FRENCH(
        code = "fr",
        nativeName = "Français",
        englishName = "French",
        regionLabel = "France / Belgium",
        badgeCode = "FR",
        region = LanguageRegion.EUROPE,
        isRtl = false,
        locale = Locale.FRENCH
    ),
    ALBANIAN(
        code = "sq",
        nativeName = "Shqip",
        englishName = "Albanian",
        regionLabel = "Albania / Kosovo",
        badgeCode = "SQ",
        region = LanguageRegion.EUROPE,
        isRtl = false,
        locale = Locale("sq", "AL")
    ),
    BOSNIAN(
        code = "bs",
        nativeName = "Bosanski",
        englishName = "Bosnian",
        regionLabel = "Bosnia & Herzegovina",
        badgeCode = "BS",
        region = LanguageRegion.EUROPE,
        isRtl = false,
        locale = Locale("bs", "BA")
    );

    companion object {
        val defaultLanguage: SupportedLanguage = BENGALI

        fun normalizeCode(raw: String?): String {
            return fromCode(raw).code
        }

        fun fromCode(raw: String?): SupportedLanguage {
            val cleaned = raw?.trim()?.lowercase(Locale.US).orEmpty()
            if (cleaned.isEmpty()) return BENGALI
            val primary = cleaned.substringBefore('-').substringBefore('_')
            // Legacy Indonesian tag "in" maps to "id"
            val mapped = if (primary == "in") "id" else primary
            return entries.firstOrNull { it.code == mapped } ?: ENGLISH
        }

        fun formatDigits(raw: String, langCode: String): String {
            return when (normalizeCode(langCode)) {
                "bn" -> {
                    val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
                    buildString(raw.length) {
                        for (c in raw) {
                            append(if (c in '0'..'9') bnDigits[c - '0'] else c)
                        }
                    }
                }
                "ar" -> {
                    val arDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
                    buildString(raw.length) {
                        for (c in raw) {
                            append(if (c in '0'..'9') arDigits[c - '0'] else c)
                        }
                    }
                }
                else -> raw
            }
        }

        fun formatInt(value: Int, langCode: String): String =
            formatDigits(value.toString(), langCode)

        fun localizedPrayerName(prayerId: String, langCode: String): String {
            val lang = fromCode(langCode)
            return when (prayerId.uppercase(Locale.US)) {
                "FAJR" -> when (lang) {
                    BENGALI -> "ফজর"
                    ARABIC -> "الفجر"
                    URDU -> "فجر"
                    INDONESIAN -> "Subuh"
                    MALAY -> "Subuh"
                    HINDI -> "फ़ज्र"
                    TURKISH -> "İmsak / Sabah"
                    GERMAN -> "Fadschr"
                    FRENCH -> "Fajr"
                    ALBANIAN -> "Sabahu"
                    BOSNIAN -> "Sabah"
                    else -> "Fajr"
                }
                "SUNRISE" -> when (lang) {
                    BENGALI -> "সূর্যোদয়"
                    ARABIC -> "الشروق"
                    URDU -> "طلوعِ آفتاب"
                    INDONESIAN -> "Terbit"
                    MALAY -> "Syuruk"
                    HINDI -> "सूर्योदय"
                    TURKISH -> "Güneş"
                    GERMAN -> "Sonnenaufgang"
                    FRENCH -> "Lever du soleil"
                    ALBANIAN -> "Lindja e diellit"
                    BOSNIAN -> "Izlazak sunca"
                    else -> "Sunrise"
                }
                "DHUHR" -> when (lang) {
                    BENGALI -> "যোহর"
                    ARABIC -> "الظهر"
                    URDU -> "ظہر"
                    INDONESIAN -> "Dzuhur"
                    MALAY -> "Zohor"
                    HINDI -> "ज़ुहर"
                    TURKISH -> "Öğle"
                    GERMAN -> "Dhuhr"
                    FRENCH -> "Dhuhr"
                    ALBANIAN -> "Dreka"
                    BOSNIAN -> "Podne"
                    else -> "Dhuhr"
                }
                "JUMUAH" -> when (lang) {
                    BENGALI -> "জুমা"
                    ARABIC -> "الجمعة"
                    URDU -> "جمعہ"
                    INDONESIAN -> "Jumat"
                    MALAY -> "Jumaat"
                    HINDI -> "जुमुआ"
                    TURKISH -> "Cuma"
                    GERMAN -> "Dschumua"
                    FRENCH -> "Jumu'ah"
                    ALBANIAN -> "Xhumaja"
                    BOSNIAN -> "Džuma"
                    else -> "Jumu'ah"
                }
                "ASR" -> when (lang) {
                    BENGALI -> "আসর"
                    ARABIC -> "العصر"
                    URDU -> "عصر"
                    INDONESIAN -> "Ashar"
                    MALAY -> "Asar"
                    HINDI -> "असर"
                    TURKISH -> "İkindi"
                    GERMAN -> "Asr"
                    FRENCH -> "Asr"
                    ALBANIAN -> "Ikindia"
                    BOSNIAN -> "Ikindija"
                    else -> "Asr"
                }
                "MAGHRIB" -> when (lang) {
                    BENGALI -> "মাগরিব"
                    ARABIC -> "المغرب"
                    URDU -> "مغرب"
                    INDONESIAN -> "Maghrib"
                    MALAY -> "Maghrib"
                    HINDI -> "मग़रिब"
                    TURKISH -> "Akşam"
                    GERMAN -> "Maghrib"
                    FRENCH -> "Maghrib"
                    ALBANIAN -> "Akshami"
                    BOSNIAN -> "Akšam"
                    else -> "Maghrib"
                }
                "ISHA" -> when (lang) {
                    BENGALI -> "এশা"
                    ARABIC -> "العشاء"
                    URDU -> "عشاء"
                    INDONESIAN -> "Isya"
                    MALAY -> "Isyak"
                    HINDI -> "इशा"
                    TURKISH -> "Yatsı"
                    GERMAN -> "Ischa"
                    FRENCH -> "Isha"
                    ALBANIAN -> "Jacia"
                    BOSNIAN -> "Jacija"
                    else -> "Isha"
                }
                else -> prayerId
            }
        }

        fun localizedHijriMonth(monthIndex1Based: Int, langCode: String): String {
            val lang = fromCode(langCode)
            val enMonths = listOf(
                "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
                "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
                "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
            )
            val bnMonths = listOf(
                "মুহাররম", "সফর", "রবিউল আউয়াল", "রবিউস সানি",
                "জমাদিউল আউয়াল", "জমাদিউস সানি", "রজব", "শাবান",
                "রমজান", "শাওয়াল", "জিলকদ", "জিলহজ"
            )
            val arMonths = listOf(
                "محرم", "صفر", "ربيع الأول", "ربيع الآخر",
                "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
                "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
            )
            val urMonths = listOf(
                "محرم الحرام", "صفر المظفر", "ربیع الاول", "ربیع الثانی",
                "جمادی الاول", "جمادی الثانی", "رجب المرجب", "شعبان المعظم",
                "رمضان المبارک", "شوال المکرم", "ذوالقعدہ", "ذوالحجہ"
            )
            val idMsMonths = listOf(
                "Muharram", "Safar", "Rabiul Awal", "Rabiul Akhir",
                "Jumadil Awal", "Jumadil Akhir", "Rajab", "Sya'ban",
                "Ramadan", "Syawal", "Dzulqa'dah", "Dzulhijjah"
            )
            val trMonths = listOf(
                "Muharrem", "Safer", "Rebiülevvel", "Rebiülahir",
                "Cemaziyelevvel", "Cemaziyelahir", "Recep", "Şaban",
                "Ramazan", "Şevval", "Zilkade", "Zilhicce"
            )
            val idx = (monthIndex1Based - 1).coerceIn(0, 11)
            return when (lang) {
                BENGALI -> bnMonths[idx]
                ARABIC -> arMonths[idx]
                URDU -> urMonths[idx]
                INDONESIAN, MALAY -> idMsMonths[idx]
                TURKISH -> trMonths[idx]
                else -> enMonths[idx]
            }
        }
    }
}
