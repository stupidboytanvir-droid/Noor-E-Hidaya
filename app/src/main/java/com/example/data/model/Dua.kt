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

    /**
     * Verified 100% Male Reciter studio MP3 URLs (Hisnul Muslim Saudi Male Reciter & Sheikh Mishary Al-Afasy).
     * Every Dua with a matching studio recording streams immediately; remaining Duas use male-only ArabicTtsManager.
     */
    internal val VERIFIED_MALE_DUA_AUDIO_URLS: Map<String, String> = mapOf(
        // Daily Life & Sunnah Adhkar (Hisnul Muslim Male Reciter)
        "dua_waking_up" to "https://www.hisnmuslim.com/audio/ar/1.mp3",
        "dua_sleeping_bismika" to "https://www.hisnmuslim.com/audio/ar/105.mp3",
        "dua_sleeping_fitrah" to "https://www.hisnmuslim.com/audio/ar/111.mp3",
        "dua_bad_dream_protection" to "https://www.hisnmuslim.com/audio/ar/193.mp3",
        "dua_toilet_enter" to "https://www.hisnmuslim.com/audio/ar/10.mp3",
        "dua_toilet_leave" to "https://www.hisnmuslim.com/audio/ar/11.mp3",
        "dua_wearing_clothes" to "https://www.hisnmuslim.com/audio/ar/5.mp3",
        "dua_leaving_home" to "https://www.hisnmuslim.com/audio/ar/16.mp3",
        "dua_entering_home" to "https://www.hisnmuslim.com/audio/ar/18.mp3",
        "dua_before_eating" to "https://www.hisnmuslim.com/audio/ar/178.mp3",
        "dua_after_eating" to "https://www.hisnmuslim.com/audio/ar/180.mp3",
        "dua_guest_host_blessing" to "https://www.hisnmuslim.com/audio/ar/182.mp3",
        "dua_riding_vehicle" to "https://www.hisnmuslim.com/audio/ar/206.mp3",
        "dua_travel_journey_full" to "https://www.hisnmuslim.com/audio/ar/207.mp3",
        "dua_entering_marketplace" to "https://www.hisnmuslim.com/audio/ar/209.mp3",
        "dua_debt_relief_mount_uhud" to "https://www.hisnmuslim.com/audio/ar/136.mp3",
        "dua_anxiety_grief_laziness" to "https://www.hisnmuslim.com/audio/ar/121.mp3",
        "dua_calamity_inna_lillah" to "https://www.hisnmuslim.com/audio/ar/154.mp3",
        "dua_sneezing_etiquette" to "https://www.hisnmuslim.com/audio/ar/189.mp3",

        // Morning & Evening Adhkar (Hisnul Muslim Male Reciter)
        "dua_sayyidul_istighfar" to "https://www.hisnmuslim.com/audio/ar/79.mp3",
        "dua_bismillahilladhi_azkar" to "https://www.hisnmuslim.com/audio/ar/86.mp3",
        "dua_three_quls_morning_evening" to "https://www.hisnmuslim.com/audio/ar/76.mp3",
        "dua_hasbiyallahu_seven_times" to "https://www.hisnmuslim.com/audio/ar/83.mp3",
        "dua_juwayriyah_creation_tasbih" to "https://www.hisnmuslim.com/audio/ar/94.mp3",
        "dua_subhanallahi_wa_bihamdihi_100" to "https://www.hisnmuslim.com/audio/ar/91.mp3",
        "dua_la_ilaha_illallah_100_times" to "https://www.hisnmuslim.com/audio/ar/93.mp3",
        "dua_evening_arrival_amsayna" to "https://www.hisnmuslim.com/audio/ar/77.mp3",
        "dua_protection_shirk_conscious_unconscious" to "https://www.hisnmuslim.com/audio/ar/203.mp3",

        // Quranic Rabbana & Prophetic Supplications (Sheikh Mishary Al-Afasy — Male)
        "dua_rabbana_atina" to "https://everyayah.com/data/Alafasy_128kbps/002201.mp3",
        "dua_rabbana_patience_victory" to "https://everyayah.com/data/Alafasy_128kbps/002250.mp3",
        "dua_rabbana_la_tuakhidhna_baqarah" to "https://everyayah.com/data/Alafasy_128kbps/002286.mp3",
        "dua_rabbana_la_tuzigh_quloobana" to "https://everyayah.com/data/Alafasy_128kbps/003008.mp3",
        "dua_rabbana_hab_lana_min_azwajina" to "https://everyayah.com/data/Alafasy_128kbps/025074.mp3",
        "dua_parents_mercy_rabbirhamhuma" to "https://everyayah.com/data/Alafasy_128kbps/017024.mp3",
        "dua_parents_forgiveness_nuh" to "https://everyayah.com/data/Alafasy_128kbps/071028.mp3",
        "dua_musa_seeking_goodness_faqeer" to "https://everyayah.com/data/Alafasy_128kbps/028024.mp3",
        "dua_musa_speech_courage_shrah" to "https://everyayah.com/data/Alafasy_128kbps/020025.mp3",
        "dua_rabbi_zidnee_ilma" to "https://everyayah.com/data/Alafasy_128kbps/020114.mp3",
        "dua_yunus_fish_belly_karb" to "https://everyayah.com/data/Alafasy_128kbps/021087.mp3",
        "dua_ayyub_illness_shifa" to "https://everyayah.com/data/Alafasy_128kbps/021083.mp3",
        "dua_ibrahim_righteous_progeny" to "https://everyayah.com/data/Alafasy_128kbps/037100.mp3",
        "dua_refuge_satanic_whispers" to "https://everyayah.com/data/Alafasy_128kbps/023097.mp3",

        // Salah & Wudu Adhkar (Hisnul Muslim Male Reciter & Al-Afasy)
        "dua_wudu_before" to "https://www.hisnmuslim.com/audio/ar/12.mp3",
        "dua_wudu_after_shahada" to "https://www.hisnmuslim.com/audio/ar/13.mp3",
        "dua_wudu_after_tawwabeen" to "https://www.hisnmuslim.com/audio/ar/14.mp3",
        "dua_going_to_mosque" to "https://www.hisnmuslim.com/audio/ar/19.mp3",
        "dua_entering_mosque" to "https://www.hisnmuslim.com/audio/ar/20.mp3",
        "dua_leaving_mosque" to "https://www.hisnmuslim.com/audio/ar/21.mp3",
        "dua_sana_subhanaka" to "https://www.hisnmuslim.com/audio/ar/28.mp3",
        "dua_sana_wajjahtu" to "https://www.hisnmuslim.com/audio/ar/29.mp3",
        "dua_ruku_subhana_rabbiyal_azeem" to "https://www.hisnmuslim.com/audio/ar/33.mp3",
        "dua_ruku_subbuhun_quddus" to "https://www.hisnmuslim.com/audio/ar/35.mp3",
        "dua_rising_ruku_tahmeed" to "https://www.hisnmuslim.com/audio/ar/39.mp3",
        "dua_sujud_subhana_rabbiyal_aala" to "https://www.hisnmuslim.com/audio/ar/41.mp3",
        "dua_sujud_all_sins_forgiveness" to "https://www.hisnmuslim.com/audio/ar/46.mp3",
        "dua_between_sujuds_jalsah" to "https://www.hisnmuslim.com/audio/ar/49.mp3",
        "dua_tashahhud_attahiyyat" to "https://www.hisnmuslim.com/audio/ar/52.mp3",
        "dua_durood_ibrahim" to "https://www.hisnmuslim.com/audio/ar/53.mp3",
        "dua_masura_innee_zalamtu" to "https://www.hisnmuslim.com/audio/ar/57.mp3",
        "dua_before_salam_four_trials" to "https://www.hisnmuslim.com/audio/ar/55.mp3",
        "dua_after_salam_istighfar_salam" to "https://www.hisnmuslim.com/audio/ar/66.mp3",
        "dua_after_salam_la_ilaha_illallah" to "https://www.hisnmuslim.com/audio/ar/67.mp3",
        "dua_ayatul_kursi_after_salat" to "https://everyayah.com/data/Alafasy_128kbps/002255.mp3",
        "dua_qunut_witr" to "https://www.hisnmuslim.com/audio/ar/116.mp3",
        "dua_istikhara_full" to "https://www.hisnmuslim.com/audio/ar/74.mp3",
        "dua_waswasah_in_prayer" to "https://www.hisnmuslim.com/audio/ar/138.mp3",

        // Sawm & Ramadan (Hisnul Muslim Male Reciter)
        "dua_iftar_sahih" to "https://www.hisnmuslim.com/audio/ar/176.mp3",
        "dua_iftar_at_someone_home" to "https://www.hisnmuslim.com/audio/ar/184.mp3",
        "dua_fasting_patience_innee_sa'im" to "https://www.hisnmuslim.com/audio/ar/186.mp3",
        "dua_sighting_crescent_moon" to "https://www.hisnmuslim.com/audio/ar/175.mp3",

        // Special Moments & Ruqyah (Hisnul Muslim Male Reciter)
        "dua_shifa_pain_in_body" to "https://www.hisnmuslim.com/audio/ar/243.mp3",
        "dua_visiting_sick_seven_times" to "https://www.hisnmuslim.com/audio/ar/148.mp3",
        "dua_thunder_lightning" to "https://www.hisnmuslim.com/audio/ar/168.mp3",
        "dua_beneficial_rain" to "https://www.hisnmuslim.com/audio/ar/172.mp3",
        "dua_fear_of_oppressor" to "https://www.hisnmuslim.com/audio/ar/126.mp3",
        "dua_hasbunallah_sufficient" to "https://www.hisnmuslim.com/audio/ar/128.mp3",
        "dua_cemetery_ziyarat_qubur" to "https://www.hisnmuslim.com/audio/ar/165.mp3",
        "dua_forgiveness_even_desertion" to "https://www.hisnmuslim.com/audio/ar/250.mp3"
    )

    val DUAS: List<Dua> by lazy {
        val raw = SalahDuaData.duas +
            DailyLifeDuaData.duas +
            SawmRamadanDuaData.duas +
            MorningEveningAzkarData.duas +
            QuranicRabbanaDuaData.duas +
            SpecialMomentsDuaData.duas
        raw.map { dua ->
            if (dua.audioUrl.isNotBlank()) dua
            else {
                val mappedUrl = VERIFIED_MALE_DUA_AUDIO_URLS[dua.id]
                if (!mappedUrl.isNullOrBlank()) dua.copy(audioUrl = mappedUrl) else dua
            }
        }
    }

    fun getDuasByPillar(pillar: HisnulPillar): List<Dua> {
        return DUAS.filter { it.hisnulPillar == pillar }
    }
}
