package com.example.data.model

enum class RuqyahCategory(
    val id: String,
    val titleBengali: String,
    val titleEnglish: String,
    val subtitleBengali: String
) {
    ALL(
        id = "all",
        titleBengali = "সকল রুকইয়াহ",
        titleEnglish = "All Ruqyah",
        subtitleBengali = "সম্পূর্ণ অডিও সংগ্রহ"
    ),
    GENERAL(
        id = "general",
        titleBengali = "রুকইয়াহ শারঈয়াহ সাধারণ",
        titleEnglish = "General Ruqyah",
        subtitleBengali = "দৈনন্দিন সার্বিক আত্মরক্ষা ও বরকত"
    ),
    EVIL_EYE(
        id = "evil_eye",
        titleBengali = "আইন, নজর ও হিংসা নিবারণ",
        titleEnglish = "Evil Eye, Envy & Jinx",
        subtitleBengali = "কু-নজর, বদনজর ও হাসাদ থেকে হেফাজত"
    ),
    SIHR_MAGIC(
        id = "sihr_magic",
        titleBengali = "যাদু ও আসর দূরীকরণ",
        titleEnglish = "Sihr & Black Magic Protection",
        subtitleBengali = "যাদুর প্রভাব ও জ্বিনের আসর বিনষ্টকারী"
    ),
    PHYSICAL_HEALING(
        id = "physical_healing",
        titleBengali = "শারীরিক অসুস্থতা ও শেফা",
        titleEnglish = "Physical Healing & Shifa",
        subtitleBengali = "রোগ-ব্যাধি নিরাময় ও শেফা লাভের আয়াত"
    ),
    ANXIETY_PEACE(
        id = "anxiety_peace",
        titleBengali = "মানসিক শান্তি, দুশ্চিন্তা ও ভয় মুক্তি",
        titleEnglish = "Anxiety, Stress & Peace",
        subtitleBengali = "অন্তরের প্রশান্তি, অনিদ্রা ও আতঙ্ক দূরীকরণ"
    )
}

data class RuqyahTrack(
    val id: String,
    val titleBengali: String,
    val titleArabic: String,
    val reciterName: String,
    val reciterTitleArabic: String,
    val category: RuqyahCategory,
    val durationText: String,
    val descriptionBengali: String,
    val keyVerses: String,
    val benefits: String,
    val audioUrl: String,
    val fallbackUrls: List<String> = emptyList(),
    val isRecommendedDaily: Boolean = false,
    val titleEnglish: String = "",
    val reciterNameEnglish: String = "",
    val descriptionEnglish: String = "",
    val keyVersesEnglish: String = "",
    val benefitsEnglish: String = "",
    val arabicVersePreview: String = "",
    val playlistUrls: List<String> = emptyList()
) {
    private fun isBn(lang: String) = com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn"

    fun localizedReciterName(lang: String): String =
        if (!isBn(lang)) reciterNameEnglish.ifBlank { reciterName } else reciterName

    fun localizedTitle(lang: String): String =
        if (!isBn(lang)) com.example.ui.theme.LocalizationRegistry.translate(lang, titleBengali, titleEnglish.ifBlank { titleBengali })
        else titleBengali

    fun localizedDescription(lang: String): String =
        if (!isBn(lang)) descriptionEnglish.ifBlank { descriptionBengali } else descriptionBengali

    fun localizedKeyVerses(lang: String): String =
        if (!isBn(lang)) keyVersesEnglish.ifBlank { keyVerses } else keyVerses

    fun localizedBenefits(lang: String): String =
        if (!isBn(lang)) benefitsEnglish.ifBlank { benefits } else benefits
}

object RuqyahRepository {

    val TRACKS = listOf(
        // 1. GENERAL RUQYAH
        RuqyahTrack(
            id = "ruqyah_gen_alafasy_full",
            titleBengali = "পূর্ণাঙ্গ রুকইয়াহ শারঈয়াহ (সার্বিক হেফাজত)",
            titleArabic = "الرقية الشرعية الشاملة للحفظ والبركة",
            reciterName = "শাইখ মিশারী রশীদ আল-আফাসী",
            reciterTitleArabic = "الشيخ مشاري راشد العفاسي",
            category = RuqyahCategory.GENERAL,
            durationText = "০৩:৪৫",
            descriptionBengali = "কুরআনের প্রধান আয়াতসমূহ (আয়াতুল কুরসী ও সুরক্ষা আয়াত) দিয়ে পরিবার ও ঘরের সার্বিক বরকত ও নিরাপত্তার রুকইয়াহ।",
            keyVerses = "সূরা আল-ফাতিহা, আয়াতুল কুরসী (বাক্বারাহ ২৫৫), আল-বাক্বারাহ ২৮৫-২৮৬, তিন কুল",
            benefits = "নবীজী (ﷺ) বলেছেন: যে ব্যক্তি রাতে সূরা বাক্বারাহ পাঠ করবে বা শুনবে, শয়তান তার ঘরে প্রবেশ করতে পারে না।",
            audioUrl = "https://everyayah.com/data/Alafasy_128kbps/002255.mp3",
            fallbackUrls = listOf(
                "https://cdn.islamic.network/quran/audio/128/ar.alafasy/262.mp3",
                "https://everyayah.com/data/Alafasy_128kbps/001001.mp3"
            ),
            playlistUrls = listOf(
                "https://everyayah.com/data/Alafasy_128kbps/002255.mp3",
                "https://everyayah.com/data/Alafasy_128kbps/002285.mp3",
                "https://everyayah.com/data/Alafasy_128kbps/002286.mp3"
            ),
            arabicVersePreview = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ",
            isRecommendedDaily = true,
            titleEnglish = "Complete Ruqyah Shariah (Overall Protection)",
            descriptionEnglish = "Comprehensive Ruqyah with Ayat-ul-Kursi and key Quranic verses for family blessing and home protection.",
            keyVersesEnglish = "Al-Fatihah, Ayat-ul-Kursi (Al-Baqarah 255), Al-Baqarah 285-286, Three Quls",
            benefitsEnglish = "The Prophet (SAW) said: Whoever recites or listens to Surah Al-Baqarah at night, Shaytan cannot enter their home.",
            reciterNameEnglish = "Sheikh Mishary Rashid Al-Afasy"
        ),
        RuqyahTrack(
            id = "ruqyah_gen_ajmi",
            titleBengali = "রুকইয়াহ শারঈয়াহ (শক্তিশালী তেলাওয়াত)",
            titleArabic = "الرقية الشرعية المؤثرة",
            reciterName = "শাইখ আহমেদ বিন আলী আল-আজমী",
            reciterTitleArabic = "الشيخ أحمد بن علي العجمي",
            category = RuqyahCategory.GENERAL,
            durationText = "০২:৪০",
            descriptionBengali = "হৃদয়স্পর্শী উদাত্ত কণ্ঠে ঘরের অশুভ শক্তি দূরীকরণ ও রহমত বৃদ্ধির বিশেষ রুকইয়াহ।",
            keyVerses = "আয়াতুল কুরসী, সূরা আল-হাশর ২১-২৪, চার কুল",
            benefits = "নিয়মিত সকালে ও সন্ধ্যায় পাঠ বা শ্রবণ অন্তরে নিরাপত্তা এনে দেয়।",
            audioUrl = "https://everyayah.com/data/Ahmed_ibn_Ali_al-Ajamy_128kbps_ketaballah.net/002255.mp3",
            fallbackUrls = listOf(
                "https://everyayah.com/data/Alafasy_128kbps/059021.mp3",
                "https://cdn.islamic.network/quran/audio/128/ar.ahmedajamy/262.mp3"
            ),
            playlistUrls = listOf(
                "https://everyayah.com/data/Ahmed_ibn_Ali_al-Ajamy_128kbps_ketaballah.net/002255.mp3",
                "https://everyayah.com/data/Ahmed_ibn_Ali_al-Ajamy_128kbps_ketaballah.net/059021.mp3",
                "https://everyayah.com/data/Ahmed_ibn_Ali_al-Ajamy_128kbps_ketaballah.net/059022.mp3"
            ),
            arabicVersePreview = "لَوْ أَنزَلْنَا هَٰذَا الْقُرْآنَ عَلَىٰ جَبَلٍ لَّرَأَيْتَهُ خَاشِعًا مُّتَصَدِّعًا مِّنْ خَشْيَةِ اللَّهِ",
            isRecommendedDaily = false,
            titleEnglish = "Ruqyah Shariah (Powerful Recitation)",
            descriptionEnglish = "Soul-stirring recitation to remove negative spiritual influences from the home and invite divine mercy.",
            keyVersesEnglish = "Ayat-ul-Kursi, Al-Hashr 21-24, Four Quls",
            benefitsEnglish = "Regular morning and evening recitation or listening brings serenity and protection to the heart.",
            reciterNameEnglish = "Sheikh Ahmad bin Ali Al-Ajmi"
        ),
        RuqyahTrack(
            id = "ruqyah_gen_sudais",
            titleBengali = "হারামাইন শরীফের রুকইয়াহ ও মু'আওবিযাত",
            titleArabic = "رقية الحرمين الشريفين والمعوذات",
            reciterName = "শাইখ আব্দুর রহমান আস-সুদাইস",
            reciterTitleArabic = "الشيخ عبد الرحمن السديس",
            category = RuqyahCategory.GENERAL,
            durationText = "০২:৫৫",
            descriptionBengali = "মক্কা মুকাররমার ইমামের কণ্ঠে আয়াতুল কুরসী ও সুরক্ষার সূরাসমূহ সম্বলিত বিশুদ্ধ রুকইয়াহ।",
            keyVerses = "আয়াতুল কুরসী, সূরা আল-ফালাক, সূরা আন-নাস, মাসনূন সুরক্ষা",
            benefits = "আল্লাহর নৈকট্য ও আধ্যাত্মিক শক্তি লাভের জন্য অত্যন্ত ফলপ্রসূ।",
            audioUrl = "https://everyayah.com/data/Abdurrahmaan_As-Sudais_192kbps/002255.mp3",
            fallbackUrls = listOf(
                "https://everyayah.com/data/Abdurrahmaan_As-Sudais_192kbps/113001.mp3",
                "https://cdn.islamic.network/quran/audio/128/ar.abdurrahmaansudais/262.mp3"
            ),
            playlistUrls = listOf(
                "https://everyayah.com/data/Abdurrahmaan_As-Sudais_192kbps/002255.mp3",
                "https://everyayah.com/data/Abdurrahmaan_As-Sudais_192kbps/113001.mp3",
                "https://everyayah.com/data/Abdurrahmaan_As-Sudais_192kbps/114001.mp3"
            ),
            arabicVersePreview = "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ ۝ مِن شَرِّ مَا خَلَقَ",
            isRecommendedDaily = false,
            titleEnglish = "Haramain Ruqyah & Protection Surahs",
            descriptionEnglish = "Pure Ruqyah featuring Ayat-ul-Kursi and Al-Mu'awwidhat recited by the Imam of Makkah.",
            keyVersesEnglish = "Ayat-ul-Kursi, Surah Al-Falaq, Surah An-Nas",
            benefitsEnglish = "Highly beneficial for attaining closeness to Allah and spiritual strength.",
            reciterNameEnglish = "Sheikh Abdur-Rahman As-Sudais"
        ),

        // 2. EVIL EYE & ENVY
        RuqyahTrack(
            id = "ruqyah_eye_khalid",
            titleBengali = "বদনজর ও হিংসা নাশক রুকইয়াহ (আইন ও হাসাদ)",
            titleArabic = "رقية إبطال العين والحسد القوية",
            reciterName = "শাইখ মিশারী রশীদ আল-আফাসী",
            reciterTitleArabic = "الشيخ مشاري راشد العفاسي",
            category = RuqyahCategory.EVIL_EYE,
            durationText = "০২:১৫",
            descriptionBengali = "মানুষ ও জ্বিনের ক্ষতিকর দৃষ্টি, বিষাক্ত হিংসা ও হাসাদের ধ্বংসাত্মক প্রভাব দূরীকরণের রুকইয়াহ।",
            keyVerses = "সূরা আল-কালাম ৫১-৫২, সূরা আল-ফালাক, সূরা আন-নাস",
            benefits = "নবীজী (ﷺ) বলেছেন: বদনজর সত্য, তা মানুষকে কবরে এবং উটকে হাঁড়িতে পাঠাতে পারে। (সহীহ মুসলিম)",
            audioUrl = "https://everyayah.com/data/Alafasy_128kbps/068051.mp3",
            fallbackUrls = listOf(
                "https://cdn.islamic.network/quran/audio/128/ar.alafasy/5322.mp3",
                "https://everyayah.com/data/Alafasy_128kbps/113001.mp3"
            ),
            playlistUrls = listOf(
                "https://everyayah.com/data/Alafasy_128kbps/068051.mp3",
                "https://everyayah.com/data/Alafasy_128kbps/068052.mp3",
                "https://everyayah.com/data/Alafasy_128kbps/113005.mp3"
            ),
            arabicVersePreview = "وَإِن يَكَادُ الَّذِينَ كَفَرُوا لَيُزْلِقُونَكَ بِأَبْصَارِهِمْ لَمَّا سَمِعُوا الذِّكْرَ",
            isRecommendedDaily = true,
            titleEnglish = "Ruqyah Against Evil Eye & Envy (Ayn & Hasad)",
            descriptionEnglish = "Ruqyah for neutralizing harmful gazes and destructive envy from humans and jinn.",
            keyVersesEnglish = "Al-Qalam 51-52, Al-Falaq, An-Nas",
            benefitsEnglish = "The Prophet (SAW) said: The evil eye is real; it can put a person in the grave and a camel in the cooking pot. (Sahih Muslim)",
            reciterNameEnglish = "Sheikh Mishary Rashid Al-Afasy"
        ),
        RuqyahTrack(
            id = "ruqyah_eye_ghamdi",
            titleBengali = "শিশু ও পরিবারের নজর নিবারণ রুকইয়াহ",
            titleArabic = "رقية حماية الأطفال والبيت من العين",
            reciterName = "শাইখ সাদ আল-গামদী",
            reciterTitleArabic = "الشيخ سعد الغامدي",
            category = RuqyahCategory.EVIL_EYE,
            durationText = "০১:৫৫",
            descriptionBengali = "শিশুদের কান্না, অহেতুক ভয় ও পরিবারের সদস্যদের হঠাৎ পরিবর্তনের ক্ষেত্রে অত্যন্ত উপকারী।",
            keyVerses = "সূরা আল-ইখলাস, সূরা আল-ফালাক, সূরা আন-নাস",
            benefits = "তিন কুল পাঠ করে ফুঁ দেওয়ার মাধ্যমে নবীজী (ﷺ) আল্লাহর আশ্রয় প্রার্থনা করতেন।",
            audioUrl = "https://everyayah.com/data/Ghamadi_40kbps/112001.mp3",
            fallbackUrls = listOf(
                "https://everyayah.com/data/Alafasy_128kbps/112001.mp3",
                "https://everyayah.com/data/Alafasy_128kbps/114001.mp3"
            ),
            playlistUrls = listOf(
                "https://everyayah.com/data/Ghamadi_40kbps/112001.mp3",
                "https://everyayah.com/data/Ghamadi_40kbps/113001.mp3",
                "https://everyayah.com/data/Ghamadi_40kbps/114001.mp3"
            ),
            arabicVersePreview = "قُلْ هُوَ اللَّهُ أَحَدٌ ۝ اللَّهُ الصَّمَدُ",
            isRecommendedDaily = false,
            titleEnglish = "Ruqyah for Children & Family Protection",
            descriptionEnglish = "Especially helpful for excessive crying in children, sudden fear, and unsettling changes at home.",
            keyVersesEnglish = "Al-Ikhlas, Al-Falaq, An-Nas & Prophetic protection Du'as",
            benefitsEnglish = "Reciting the Three Quls and blowing gently over family members is an authentic Prophetic protection.",
            reciterNameEnglish = "Sheikh Sa'ad Al-Ghamdi"
        ),

        // 3. SIHR & BLACK MAGIC
        RuqyahTrack(
            id = "ruqyah_sihr_alafasy",
            titleBengali = "যাদু ও কুফরী বিনষ্টকারী রুকইয়াহ (ইবত্বালুস সিহর)",
            titleArabic = "رقية إبطال وفك السحر والمس",
            reciterName = "শাইখ মিশারী রশীদ আল-আফাসী",
            reciterTitleArabic = "الشيخ مشاري راشد العفاسي",
            category = RuqyahCategory.SIHR_MAGIC,
            durationText = "০২:৪৫",
            descriptionBengali = "যাদুর প্রভাব, শারীরিক জড়তা ও জ্বিনের আসর বিনষ্টকারী কুরআনের শক্তিশালী আয়াতসমূহ।",
            keyVerses = "সূরা ইউনুস ৮১-৮২, সূরা আল-আ'রাফ ১১৭-১১৮, সূরা ত্বাহা ৬৯",
            benefits = "মূসা (আঃ) বললেন: তোমরা যা এনেছ তা যাদু, নিশ্চয়ই আল্লাহ তা বাতিল করে দেবেন। (ইউনুস: ৮১)",
            audioUrl = "https://everyayah.com/data/Alafasy_128kbps/010081.mp3",
            fallbackUrls = listOf(
                "https://everyayah.com/data/Alafasy_128kbps/020069.mp3",
                "https://everyayah.com/data/Alafasy_128kbps/002255.mp3"
            ),
            playlistUrls = listOf(
                "https://everyayah.com/data/Alafasy_128kbps/010081.mp3",
                "https://everyayah.com/data/Alafasy_128kbps/010082.mp3",
                "https://everyayah.com/data/Alafasy_128kbps/020069.mp3"
            ),
            arabicVersePreview = "مَا جِئْتُم بِهِ السِّحْرُ ۖ إِنَّ اللَّهَ سَيُبْطِلُهُ ۖ إِنَّ اللَّهَ لَا يُصْلِحُ عَمَلَ الْمُفْسِدِينَ",
            isRecommendedDaily = true,
            titleEnglish = "Ruqyah for Nullifying Magic & Possession (Ibtal-us-Sihr)",
            descriptionEnglish = "Powerful Quranic verses aimed at breaking the effects of sorcery, lethargy, and jinn affliction.",
            keyVersesEnglish = "Yunus 81-82, Al-A'raf 117-118, Ta-Ha 69",
            benefitsEnglish = "Musa (AS) said: What you have brought is magic; surely Allah will make it void. (Yunus: 81)",
            reciterNameEnglish = "Sheikh Mishary Rashid Al-Afasy"
        ),
        RuqyahTrack(
            id = "ruqyah_sihr_jibril",
            titleBengali = "আসর ও শয়তানী উপদ্রব বিতাড়ক রুকইয়াহ",
            titleArabic = "رقية حرق ودحر الشياطين والمس",
            reciterName = "শাইখ মুহাম্মদ জিবরীল",
            reciterTitleArabic = "الشيخ محمد جبريل",
            category = RuqyahCategory.SIHR_MAGIC,
            durationText = "০২:৩০",
            descriptionBengali = "ঘরের আসর, তীব্র অস্বস্তি ও খারাপ প্রভাব নিবারণে আস-সাফফাত ও আল-জিনের আয়াত।",
            keyVerses = "সূরা আস-সাফফাত ১-৭, সূরা আল-জিন ১-৩, সূরা আল-বাক্বারাহ ১০২",
            benefits = "যে ঘরে সূরা বাক্বারাহ ও রুকইয়াহ তেলাওয়াত হয়, সে ঘরে শয়তান টিকতে পারে না।",
            audioUrl = "https://everyayah.com/data/Muhammad_Jibreel_128kbps/037001.mp3",
            fallbackUrls = listOf(
                "https://everyayah.com/data/Alafasy_128kbps/072001.mp3",
                "https://everyayah.com/data/Alafasy_128kbps/037001.mp3"
            ),
            playlistUrls = listOf(
                "https://everyayah.com/data/Muhammad_Jibreel_128kbps/037001.mp3",
                "https://everyayah.com/data/Muhammad_Jibreel_128kbps/037002.mp3",
                "https://everyayah.com/data/Muhammad_Jibreel_128kbps/072001.mp3"
            ),
            arabicVersePreview = "وَالصَّافَّاتِ صَفًّا ۝ فَالزَّاجِرَاتِ زَجْرًا ۝ وَحِفْظًا مِّن كُلِّ شَيْطَانٍ مَّارِدٍ",
            isRecommendedDaily = false,
            titleEnglish = "Ruqyah Against Spiritual Harm & Disturbance",
            descriptionEnglish = "Verses from As-Saffat and Al-Jinn for repelling severe spiritual distress and home disturbances.",
            keyVersesEnglish = "As-Saffat 1-7, Al-Jinn 1-3, Al-Baqarah 102",
            benefitsEnglish = "Shaytan cannot remain in a house where Surah Al-Baqarah and Ruqyah are recited.",
            reciterNameEnglish = "Sheikh Muhammad Jibril"
        ),

        // 4. PHYSICAL HEALING & SHIFA
        RuqyahTrack(
            id = "ruqyah_shifa_muaiqly",
            titleBengali = "কুরআনের ছয় শেফা আয়াত ও আরোগ্য লাভ",
            titleArabic = "آيات الشفاء الستة والسكينة",
            reciterName = "শাইখ মাহের আল-মুয়াইক্বলী",
            reciterTitleArabic = "الشيخ ماهر المعيقلي",
            category = RuqyahCategory.PHYSICAL_HEALING,
            durationText = "০২:২০",
            descriptionBengali = "শারীরিক অসুস্থতা, ব্যথা-বেদনা ও জটিল রোগ নিরাময়ে কুরআনের খাস শেফার আয়াতসমূহ।",
            keyVerses = "আল-ইসরা ৮২, আশ-শু'আরা ৮০, ইউনুস ৫৭, আন-নাহল ৬৯",
            benefits = "আল্লাহ বলেন: আমি কুরআনে এমন বিষয় নাযিল করি যা মুমিনদের জন্য শেফা ও রহমত। (আল-ইসরা: ৮২)",
            audioUrl = "https://everyayah.com/data/MaherAlMuaiqly128kbps/017082.mp3",
            fallbackUrls = listOf(
                "https://everyayah.com/data/Alafasy_128kbps/017082.mp3",
                "https://everyayah.com/data/Alafasy_128kbps/026080.mp3"
            ),
            playlistUrls = listOf(
                "https://everyayah.com/data/MaherAlMuaiqly128kbps/017082.mp3",
                "https://everyayah.com/data/MaherAlMuaiqly128kbps/026080.mp3",
                "https://everyayah.com/data/MaherAlMuaiqly128kbps/010057.mp3"
            ),
            arabicVersePreview = "وَنُنَزِّلُ مِنَ الْقُرْآنِ مَا هُوَ شِفَاءٌ وَرَحْمَةٌ لِّلْمُؤْمِنِينَ",
            isRecommendedDaily = true,
            titleEnglish = "Six Verses of Shifa & Physical Recovery",
            descriptionEnglish = "The six Quranic verses of healing (Ayat-ush-Shifa) for physical illness, pain, and chronic ailments.",
            keyVersesEnglish = "Al-Isra 82, Ash-Shu'ara 80, Yunus 57, An-Nahl 69",
            benefitsEnglish = "Allah says: We send down in the Quran that which is a healing and a mercy for the believers. (Al-Isra: 82)",
            reciterNameEnglish = "Sheikh Maher Al-Muaiqly"
        ),
        RuqyahTrack(
            id = "ruqyah_shifa_dossari",
            titleBengali = "কঠিন রোগ ও শারীরিক ব্যথায় রুকইয়াহ",
            titleArabic = "رقية المريض والشفاء من الأسقام",
            reciterName = "শাইখ ইয়াসির আদ-দুসারী",
            reciterTitleArabic = "الشيخ ياسر الدوسري",
            category = RuqyahCategory.PHYSICAL_HEALING,
            durationText = "০১:৫০",
            descriptionBengali = "রোগী ও ব্যথাতুর অঙ্গের ওপর হাত রেখে শোনার উপযোগী আরোগ্যদায়ক রুকইয়াহ।",
            keyVerses = "আইয়ুব (আঃ)-এর দো'আ (আল-আম্বিয়া ৮৩), সূরা আল-ফাতিহা, ফুসসিলাত ৪৪",
            benefits = "বিসমিল্লাহ ৩ বার ও আ'উজু বি'ইযযাতিল্লাহি... দো'আ ৭ বার পড়ার সুন্নত তরিকা।",
            audioUrl = "https://everyayah.com/data/Yasser_Ad-Dussary_128kbps/021083.mp3",
            fallbackUrls = listOf(
                "https://everyayah.com/data/Alafasy_128kbps/021083.mp3",
                "https://everyayah.com/data/Alafasy_128kbps/041044.mp3"
            ),
            playlistUrls = listOf(
                "https://everyayah.com/data/Yasser_Ad-Dussary_128kbps/021083.mp3",
                "https://everyayah.com/data/Yasser_Ad-Dussary_128kbps/041044.mp3"
            ),
            arabicVersePreview = "أَنِّي مَسَّنِيَ الضُّرُّ وَأَنتَ أَرْحَمُ الرَّاحِمِينَ",
            isRecommendedDaily = false,
            titleEnglish = "Ruqyah for Illness & Bodily Pain",
            descriptionEnglish = "Healing recitation suited to listen while placing a hand on the painful area.",
            keyVersesEnglish = "Du'a of Ayyub (AS) (Al-Anbiya 83), Al-Fatihah, Fussilat 44",
            benefitsEnglish = "Sunnah practice: Say Bismillah 3 times and 'A'udhu bi-'izzatillahi...' 7 times.",
            reciterNameEnglish = "Sheikh Yasser Ad-Dosari"
        ),

        // 5. ANXIETY, STRESS & PEACE
        RuqyahTrack(
            id = "ruqyah_peace_idris",
            titleBengali = "অন্তরের প্রশান্তি, দুশ্চিন্তা ও মানসিক চাপ মুক্তি",
            titleArabic = "رقية السكينة وانشراح الصدر وطرد القلق",
            reciterName = "শাইখ মিশারী রশীদ আল-আফাসী",
            reciterTitleArabic = "الشيخ مشاري راشد العفاسي",
            category = RuqyahCategory.ANXIETY_PEACE,
            durationText = "০২:১০",
            descriptionBengali = "হতাশা, বিষণ্ণতা, মনের ব্যাকুলতা ও বুক ভারী লাগার অনুভূতি দূর করে পরম প্রশান্তি প্রদানকারী আয়াত।",
            keyVerses = "সূরা আর-রাদ ২৮, আল-ফাতহ ৪, আল-ইনশিরাহ ৫-৬",
            benefits = "জেনে রাখো, আল্লাহর স্মরণেই কেবল অন্তরসমূহ পরম প্রশান্তি লাভ করে। (সূরা আর-রাদ: ২৮)",
            audioUrl = "https://everyayah.com/data/Alafasy_128kbps/013028.mp3",
            fallbackUrls = listOf(
                "https://cdn.islamic.network/quran/audio/128/ar.alafasy/1735.mp3",
                "https://everyayah.com/data/Alafasy_128kbps/048004.mp3"
            ),
            playlistUrls = listOf(
                "https://everyayah.com/data/Alafasy_128kbps/013028.mp3",
                "https://everyayah.com/data/Alafasy_128kbps/048004.mp3",
                "https://everyayah.com/data/Alafasy_128kbps/094005.mp3"
            ),
            arabicVersePreview = "الَّذِينَ آمَنُوا وَتَطْمَئِنُّ قُلُوبُهُم بِذِكْرِ اللَّهِ ۗ أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
            isRecommendedDaily = true,
            titleEnglish = "Peace of Heart, Relief from Anxiety & Stress",
            descriptionEnglish = "Verses that ease sadness, restlessness, and heaviness of the chest to restore inner tranquility.",
            keyVersesEnglish = "Ar-Ra'd 28, Al-Fath 4, Al-Inshirah 5-6",
            benefitsEnglish = "Unquestionably, by the remembrance of Allah do hearts find rest. (Ar-Ra'd: 28)",
            reciterNameEnglish = "Sheikh Mishary Rashid Al-Afasy"
        ),
        RuqyahTrack(
            id = "ruqyah_peace_alafasy_sleep",
            titleBengali = "অনিদ্রা, দুঃস্বপ্ন ও আতঙ্ক দূরীকরণ রুকইয়াহ",
            titleArabic = "رقية النوم الهادئ وطرد الكوابيس والفزع",
            reciterName = "শাইখ মিশারী রশীদ আল-আফাসী",
            reciterTitleArabic = "الشيخ مشاري راشد العفاسي",
            category = RuqyahCategory.ANXIETY_PEACE,
            durationText = "০১:৪৫",
            descriptionBengali = "রাতে ঘুমানোর পূর্বে ভয়, দুঃস্বপ্ন ও অস্থিরতা দূর করে নিশ্চিন্ত ঘুমের জন্য পরীক্ষিত রুকইয়াহ।",
            keyVerses = "সূরা আশ-শারহ (আলাম নাশরাহ), আয়াতুল কুরসী, তিন কুল",
            benefits = "ঘুমানোর পূর্বে রুকইয়াহ শ্রবণ আত্মাকে প্রশান্ত করে এবং শয়তানের ভীতি বিনষ্ট করে।",
            audioUrl = "https://everyayah.com/data/Alafasy_128kbps/094001.mp3",
            fallbackUrls = listOf(
                "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6091.mp3",
                "https://everyayah.com/data/Alafasy_128kbps/002255.mp3"
            ),
            playlistUrls = listOf(
                "https://everyayah.com/data/Alafasy_128kbps/094001.mp3",
                "https://everyayah.com/data/Alafasy_128kbps/094005.mp3",
                "https://everyayah.com/data/Alafasy_128kbps/002255.mp3"
            ),
            arabicVersePreview = "أَلَمْ نَشْرَحْ لَكَ صَدْرَكَ ۝ فَإِنَّ مَعَ الْعُسْرِ يُسْرًا",
            isRecommendedDaily = false,
            titleEnglish = "Ruqyah for Peaceful Sleep & Relief from Nightmares",
            descriptionEnglish = "Soothing recitation before sleep to calm fear, nightmares, and restlessness.",
            keyVersesEnglish = "Ash-Sharh, Ayat-ul-Kursi, Three Quls",
            benefitsEnglish = "Listening to Ruqyah before sleep calms the soul and removes nocturnal anxiety.",
            reciterNameEnglish = "Sheikh Mishary Rashid Al-Afasy"
        )
    )

    fun getTracksForCategory(category: RuqyahCategory): List<RuqyahTrack> {
        return if (category == RuqyahCategory.ALL) {
            TRACKS
        } else {
            TRACKS.filter { it.category == category }
        }
    }
}
