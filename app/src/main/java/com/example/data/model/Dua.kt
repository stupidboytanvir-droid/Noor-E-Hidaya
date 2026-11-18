package com.example.data.model

import java.util.Calendar
import com.example.data.model.dua.DailyLifeDuaData
import com.example.data.model.dua.MorningEveningAzkarData
import com.example.data.model.dua.QuranicRabbanaDuaData
import com.example.data.model.dua.SalahDuaData
import com.example.data.model.dua.SawmRamadanDuaData
import com.example.data.model.dua.SpecialMomentsDuaData

enum class HisnulPillar(val id: String, val titleBengali: String, val subtitleBengali: String) {
    SALAH_PRAYER("salah_prayer", "সালাত-নামাজ", "ওযু, আজান, সানা, রুকু, সেজদা ও সালামের দু'আ"),
    DAILY_LIFE("daily_life", "দৈনন্দিন জীবন", "সকাল-সন্ধ্যা, ঘুম, সুরক্ষা ও সফর"),
    FASTING_SAWM("fasting_sawm", "সাওম", "সেহরি, ইফতার ও রমজানের আমল"),
    QURANIC_RABBANA("quranic_rabbana", "কুরআন থেকে", "কুরআনের সর্বশ্রেষ্ঠ রব্বানা দোয়াসমূহ"),
    BOOKMARKS_CUSTOM("bookmarks_custom", "বিবিধ ও বুকমার্ক", "সংরক্ষিত দু'আ ও ব্যক্তিগত আমল")
}

enum class WaqtCategory(
    val id: String,
    val titleBengali: String,
    val subtitleBengali: String,
    val titleEnglish: String = "",
    val subtitleEnglish: String = ""
) {
    ALL("all", "সকল দু'আ", "হিসনুল মুসলিম সমগ্র", "All Duas", "Complete Hisnul Muslim collection"),
    FAJR_MORNING("fajr_morning", "সকালের আমল (ফজর)", "ফজর ও সকালবেলার গুরুত্বপূর্ণ মাসনুন যিকির", "Morning Adhkar (Fajr)", "Essential Sunnah supplications after Fajr"),
    DHUHR_ASR("dhuhr_asr", "দুপুর ও আসর", "কর্মব্যস্ততা, হালাল রিযিক্ব ও বরকতের দু'আ", "Midday & Asr", "Duas for work, halal sustenance & barakah"),
    MAGHRIB_EVENING("maghrib_evening", "সন্ধ্যার আমল (মাগরিব)", "সন্ধ্যা ও সূর্যাস্ত পরবর্তী প্রতিরক্ষা", "Evening Adhkar (Maghrib)", "Protection supplications after sunset"),
    ISHA_NIGHT("isha_night", "রাতের আমল ও ঘুম", "ইশা, তাহাজ্জুদ ও নিদ্রার দু'আ", "Night & Sleep", "Duas for Isha, Tahajjud & restful sleep"),
    AFTER_SALAT("after_salat", "সালাতের পরের দু'আ", "ফরজ সালাত পরবর্তী মাসনুন আযকার", "After Obligatory Prayer", "Authentic Sunnah adhkar after fard salah"),
    DISTRESS_TRIALS("distress_trials", "বিপদ ও ঋণমুক্তি", "পেরেশানি, শোক ও ঋণ থেকে পরিত্রাণ", "Relief from Distress", "Seeking refuge from anxiety, grief & debt"),
    RUQYAH_PROTECTION("ruqyah_protection", "রুকিয়া ও সুরক্ষা", "বদনজর, শয়তান ও অনিষ্ট থেকে হিফাযত", "Ruqyah & Protection", "Shield from evil eye, harm & Shaytan"),
    ISTIGHFAR("istighfar", "ক্ষমা ও তাওবা", "গুনাহ মাফ ও আল্লাহর নৈকট্য", "Forgiveness & Repentance", "Istighfar & seeking nearness to Allah"),
    DAILY_LIFE("daily_life", "দৈনন্দিন ও সফর", "ঘর, মসজিদ, সফর ও দৈনন্দিন শিষ্টাচার", "Daily Life & Travel", "Home, mosque, journey & everyday Sunnahs");

    fun localizedTitle(lang: String): String =
        com.example.ui.theme.LocalizationRegistry.translate(lang, titleBengali, titleEnglish)
    fun localizedSubtitle(lang: String): String =
        com.example.ui.theme.LocalizationRegistry.translate(lang, subtitleBengali, subtitleEnglish)
}

data class Dua(
    val id: String,
    val category: String,
    val categoryBengali: String,
    val waqtCategory: WaqtCategory = WaqtCategory.ALL,
    val hisnulPillar: HisnulPillar = HisnulPillar.DAILY_LIFE,
    val titleBengali: String,
    val titleEnglish: String,
    val textArabic: String,
    val transliteration: String,
    val transliterationBengali: String = "",
    val meaningBengali: String,
    val meaningEnglish: String,
    val reference: String,
    val virtuesBengali: String = "",
    val repetitions: Int = 1,
    val audioUrl: String = ""
)

object DuaRepository {

    val CATEGORIES = listOf(
        "All" to "সকল দু'আ",
        "Morning & Evening" to "সকাল-সন্ধ্যা",
        "Salat & Wudu" to "সালাতের পর",
        "Sleep" to "ঘুম ও জাগরণ",
        "Distress & Trials" to "বিপদ ও ঋণমুক্তি",
        "Ruqyah & Protection" to "রুকিয়া ও সুরক্ষা",
        "Forgiveness & Repentance" to "ক্ষমা ও তাওবা",
        "Travel & Daily Life" to "দৈনন্দিন ও সফর"
    )

    fun getCurrentWaqtCategory(hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)): WaqtCategory {
        return when {
            hour in 4..11 -> WaqtCategory.FAJR_MORNING
            hour in 12..16 -> WaqtCategory.DHUHR_ASR
            hour in 17..19 -> WaqtCategory.MAGHRIB_EVENING
            else -> WaqtCategory.ISHA_NIGHT
        }
    }

    val DUAS: List<Dua> by lazy {
        SalahDuaData.duas +
        DailyLifeDuaData.duas +
        SawmRamadanDuaData.duas +
        MorningEveningAzkarData.duas +
        QuranicRabbanaDuaData.duas +
        SpecialMomentsDuaData.duas
    }

    fun getDuasByPillar(pillar: HisnulPillar): List<Dua> {
        return DUAS.filter { it.hisnulPillar == pillar }
    }
}
