package com.example.data.model

data class QuranWord(
    val arabic: String,
    val transliteration: String,
    val bengaliMeaning: String,
    val rootWord: String,
    val grammarContext: String,
    val englishMeaning: String = "",
    val grammarContextEnglish: String = ""
) {
    fun localizedTransliteration(contentLang: String): String =
        com.example.data.util.ArabicTransliterationHelper.transliterate(
            arabic = arabic,
            langCode = contentLang,
            bnCurated = transliteration
        )

    fun localizedMeaning(contentLang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(contentLang) == "bn") {
            bengaliMeaning
        } else {
            englishMeaning.ifBlank { bengaliMeaning }
        }

    fun localizedGrammarContext(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") {
            grammarContext
        } else {
            grammarContextEnglish.ifBlank { "Arabic Word (Ism / Fi'l / Harf)" }
        }
}

data class AyahWordAnalysis(
    val surahNumber: Int,
    val ayahNumber: Int,
    val words: List<QuranWord>
)

data class DailyTadabbur(
    val id: Int,
    val surahName: String,
    val ayahRef: String,
    val arabicText: String,
    val transliterationBengali: String,
    val translationBengali: String,
    val tafsirExcerpt: String,
    val reflectionLessons: List<String>,
    val practicalAction: String,
    val surahNameEn: String = "",
    val ayahRefEn: String = "",
    val transliterationEn: String = "",
    val translationEnglish: String = "",
    val tafsirExcerptEn: String = "",
    val reflectionLessonsEn: List<String> = emptyList(),
    val practicalActionEn: String = ""
) {
    fun localizedSurahName(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") surahName
        else surahNameEn.ifBlank { surahName }

    fun localizedAyahRef(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") ayahRef
        else ayahRefEn.ifBlank { ayahRef }

    fun localizedTranslation(contentLang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(contentLang) == "bn") translationBengali
        else translationEnglish.ifBlank { translationBengali }

    fun localizedTransliteration(contentLang: String): String =
        com.example.data.util.ArabicTransliterationHelper.transliterate(
            arabic = arabicText,
            langCode = contentLang,
            bnCurated = transliterationBengali,
            enCurated = transliterationEn
        )

    fun localizedTafsirExcerpt(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") tafsirExcerpt
        else tafsirExcerptEn.ifBlank { tafsirExcerpt }

    fun localizedReflectionLessons(lang: String): List<String> =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) != "bn" && reflectionLessonsEn.isNotEmpty()) reflectionLessonsEn
        else reflectionLessons

    fun localizedPracticalAction(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") practicalAction
        else practicalActionEn.ifBlank { practicalAction }
}

object WordByWordRepository {

    // Curated rich lexical dictionary for prominent Surahs and verses
    private val VERSE_DICTIONARY: Map<String, List<QuranWord>> = mapOf(
        // Surah Al-Fatiha (1:1 - 1:7)
        "1_1" to listOf(
            QuranWord("بِسْمِ", "বিসমি", "নামে", "سمو", "হরফে জার + বিশেষ্য (اسم)", "In the name", "Preposition + Noun"),
            QuranWord("اللَّهِ", "আল্লাহি", "আল্লাহর", "اله", "ইসমে জালালাত (বিশেষ্য)", "of Allah", "Proper Divine Noun"),
            QuranWord("الرَّحْمَٰنِ", "আর-রাহমানি", "পরম করুণাময়", "رحم", "সিফাত / গুণবাচক বিশেষণ", "the Entirely Merciful", "Adjective (Sifah)"),
            QuranWord("الرَّحِيمِ", "আর-রাহীমি", "অসীম দয়ালু", "رحم", "সিফাত / অতিশয় দয়ালু", "the Especially Merciful", "Adjective (Sifah)")
        ),
        "1_2" to listOf(
            QuranWord("الْحَمْدُ", "আল-হামদু", "সকল প্রশংসা", "حمد", "মুফতাদা / বিশেষ্য (اسم)", "All praise", "Subject Noun"),
            QuranWord("لِلَّهِ", "লিল্লাহি", "আল্লাহর জন্য", "اله", "জার ও মাজরুর", "is due to Allah", "Prepositional Phrase"),
            QuranWord("رَبِّ", "রব্বি", "যিনি প্রতিপালক", "ربب", "মুদাফ / অভিভাবক ও পালনকর্তা", "Lord", "Possessive Noun"),
            QuranWord("الْعَالَمِينَ", "আল-আলামীন", "সমগ্র সৃষ্টিজগতের", "علم", "মুদাফ ইলাইহি / বহুবচন", "of the worlds", "Plural Noun")
        ),
        "1_3" to listOf(
            QuranWord("الرَّحْمَٰنِ", "আর-রাহমানি", "পরম করুণাময়", "رحم", "গুণবাচক বিশেষ্য", "The Entirely Merciful", "Adjective (Sifah)"),
            QuranWord("الرَّحِيمِ", "আর-রাহীমি", "চির কৃপাময়", "رحم", "গুণবাচক বিশেষ্য", "The Especially Merciful", "Adjective (Sifah)")
        ),
        "1_4" to listOf(
            QuranWord("مَالِكِ", "মালিকি", "একচ্ছত্র অধিপতি", "ملك", "বিশেষণ / বাদশাহ ও মালিক", "Master / Sovereign", "Active Participle"),
            QuranWord("يَوْمِ", "ইয়াওমি", "দিবসের", "يوم", "মুদাফ / দিন", "of the Day", "Possessive Noun"),
            QuranWord("الدِّينِ", "আদ-দীনি", "প্রতিদান / বিচার", "دين", "মুদাফ ইলাইহি / প্রতিফল", "of Recompense", "Genitive Noun")
        ),
        "1_5" to listOf(
            QuranWord("إِيَّاكَ", "ইয়্যাকা", "কেবলমাত্র আপনারই", "ايا", "মাকবুল জমীর / নির্দিষ্টকরণ", "You alone", "Detached Object Pronoun"),
            QuranWord("نَعْبُدُ", "না'বুদু", "আমরা ইবাদত করি", "عبد", "মুযারে ফে'ল (বর্তমান/ভবিষ্যৎ ক্রিয়া)", "we worship", "Present Verb (1st Plural)"),
            QuranWord("وَإِيَّاكَ", "ওয়া-ইয়্যাকা", "এবং একমাত্র আপনারই কাছে", "ايا", "অব্যয় + সর্বনাম", "and You alone", "Conjunction + Pronoun"),
            QuranWord("نَسْتَعِينُ", "নাস্তা'ঈন", "আমরা সাহায্য প্রার্থনা করি", "عون", "মুযারে ফে'ল / সাহায্য চাওয়া", "we ask for help", "Present Verb (Form X)")
        ),
        "1_6" to listOf(
            QuranWord("اهْدِنَا", "ইহদিনা", "আমাদের পরিচালিত করুন", "هدي", "আমরের ফে'ল (প্রার্থনামূলক আদেশ) + জমীর", "Guide us", "Imperative Verb + Pronoun"),
            QuranWord("الصِّرَاطَ", "আস-সিরাতা", "সঠিক সরল পথে", "صرط", "মাফ'উল / পথ", "to the path", "Direct Object Noun"),
            QuranWord("الْمُسْتَقِيمَ", "আল-মুস্তাকীমা", "যা অবিচল ও সুদৃঢ়", "قوم", "সিফাত / সরল ও সঠিক", "the straight", "Adjective (Sifah)")
        ),
        "1_7" to listOf(
            QuranWord("صِرَاطَ", "সিরাতা", "তাদের পথে", "صرط", "বদল / পথ", "The path", "Appositive Noun"),
            QuranWord("الَّذِينَ", "আল্লাযীনা", "যাদেরকে", "الذي", "ইসমে মাওসুল / বহুবচন", "of those", "Relative Pronoun"),
            QuranWord("أَنْعَمْتَ", "আন'আমতা", "আপনি পুরস্কৃত করেছেন", "نعم", "মাযী ফে'ল (অতীত ক্রিয়া)", "You have blessed", "Past Verb (Form IV)"),
            QuranWord("عَلَيْهِمْ", "আলাইহিম", "যাদের ওপর", "على", "জার ও মাজরুর", "upon them", "Preposition + Pronoun"),
            QuranWord("غَيْرِ", "গাইরি", "তাদের পথ নয়", "غير", "মুদাফ / ব্যতিরেকে", "not of those", "Negating Noun"),
            QuranWord("الْمَغْضُوبِ", "আল-মাগদূবি", "যারা ক্রোধে নিপতিত", "غضب", "ইসমে মাফ'উল", "who earned anger", "Passive Participle"),
            QuranWord("عَلَيْهِمْ", "আলাইহিম", "যাদের ওপর", "على", "জার ও মাজরুর", "upon them", "Preposition + Pronoun"),
            QuranWord("وَلَا", "ওয়ালা", "এবং যারা নয়", "لا", "সংযোজক অব্যয় ও না-বোধক", "nor", "Conjunction + Negation"),
            QuranWord("الضَّالِّينَ", "আদ-দ্বো-ল্লীন", "পথভ্রষ্ট ও বিপথগামী", "ضلل", "ইসমে ফায়েল / বহুবচন", "of those astray", "Active Participle Plural")
        ),
        // Ayat al-Kursi (2:255)
        "2_255" to listOf(
            QuranWord("اللَّهُ", "আল্লাহু", "আল্লাহ", "اله", "ইসমে জালালাত (মুফতাদা)", "Allah", "Subject Divine Noun"),
            QuranWord("لَا", "লা", "কোনো নেই", "لا", "না-বোধক হরফ", "there is no", "Absolute Negation"),
            QuranWord("إِلَٰهَ", "ইলাহা", "সত্য উপাস্য", "اله", "ইসমে লা", "deity", "Noun"),
            QuranWord("إِلَّا", "ইল্লা", "ব্যতীত", "الا", "ব্যতিক্রম হরফ", "except", "Exception Particle"),
            QuranWord("هُوَ", "হুওয়া", "তিনি", "هو", "জমীর / সর্বনাম", "Him", "Pronoun"),
            QuranWord("الْحَيُّ", "আল-হাইয়্যু", "চিরঞ্জীব", "حيي", "সিফাত / চির জীবিত", "the Ever-Living", "Attribute Noun"),
            QuranWord("الْقَيُّومُ", "আল-কাইয়্যুমু", "সকল কিছুর ধারক ও নিয়ন্ত্রক", "قوم", "সিফাত / স্বপ্রতিষ্ঠিত", "Sustainer of all", "Attribute Noun"),
            QuranWord("لَا", "লা", "স্পর্শ করে না", "لا", "নাফি হরফ", "neither", "Negation Particle"),
            QuranWord("تَأْخُذُهُ", "তা'খুযুহু", "তাঁকে ধরে", "اخذ", "ফে'ল মুযারে + জমীর", "overtakes Him", "Present Verb + Pronoun"),
            QuranWord("سِنَةٌ", "সিনাতুন", "তন্দ্রা বা ক্লান্তি", "وسن", "ফায়েল / বিশেষ্য", "drowsiness", "Subject Noun"),
            QuranWord("وَلَا", "ওয়ালা", "এবং না", "لا", "অব্যয়", "nor", "Conjunction + Negation"),
            QuranWord("نَوْمٌ", "নাওমুন", "গভীর নিদ্রা", "نوم", "বিশেষ্য / ঘুম", "sleep", "Noun")
        ),
        // Surah Al-Ikhlas (112:1 - 112:4)
        "112_1" to listOf(
            QuranWord("قُلْ", "কুল", "বলুন", "قول", "ফে'লে আমর (আদেশসূচক ক্রিয়া)", "Say", "Imperative Verb"),
            QuranWord("هُوَ", "হুওয়া", "তিনি", "هو", "সর্বনাম", "He is", "Pronoun"),
            QuranWord("اللَّهُ", "আল্লাহু", "আল্লাহ", "اله", "ইসমে জালালাত", "Allah", "Divine Noun"),
            QuranWord("أَحَدٌ", "আহাদ", "এক ও অদ্বিতীয়", "احد", "সিফাত / একক", "the One", "Adjective")
        ),
        "112_2" to listOf(
            QuranWord("اللَّهُ", "আল্লাহু", "আল্লাহ", "اله", "মুফতাদা", "Allah", "Subject Noun"),
            QuranWord("الصَّمَدُ", "আস-সামাদ", "অমুখাপেক্ষী (সকলের আশ্রয়)", "صمد", "খবর / পরম আশ্রয়", "Eternal Refuge", "Attribute Noun")
        ),
        "112_3" to listOf(
            QuranWord("لَمْ", "লাম", "না", "لم", "অতীতের না-বোধক হরফ", "Neither", "Past Negation"),
            QuranWord("يَلِدْ", "য়ালিদ", "তিনি জন্ম দেননি", "ولد", "মুযারে মাজযুম", "begets", "Jussive Verb"),
            QuranWord("وَلَمْ", "ওয়া লাম", "এবং না", "لم", "অব্যয়", "nor", "Conjunction + Negation"),
            QuranWord("يُولَدْ", "ইউলাদ", "তিনি জন্ম নেননি", "ولد", "মাজহুল ক্রিয়া (কর্মবাচ্য)", "is born", "Passive Verb")
        ),
        "112_4" to listOf(
            QuranWord("وَلَمْ", "ওয়া লাম", "এবং নেই", "لم", "হরফ", "Nor", "Conjunction + Negation"),
            QuranWord("يَكُن", "ইয়াকুন", "হতে পারে", "كون", "নাকেস ক্রিয়া", "is there", "Jussive Verb"),
            QuranWord("لَّهُ", "লাহু", "তাঁর", "ل", "জার ও মাজরুর", "to Him", "Prepositional Phrase"),
            QuranWord("كُفُوًا", "কুফুওয়ান", "সমকক্ষ / সমতুল্য", "كفو", "খবরে কানা", "any equivalent", "Predicate Noun"),
            QuranWord("أَحَدٌ", "আহাদ", "কেউই", "احد", "ইসমে কানা", "anyone", "Subject Noun")
        ),
        // Surah Al-Asr (103:1 - 103:3)
        "103_1" to listOf(
            QuranWord("وَالْعَصْرِ", "ওয়াল-'আসর", "মহাকালের শপথ", "عصر", "হরফে কসম + বিশেষ্য", "By Time", "Oath Particle + Noun")
        ),
        "103_2" to listOf(
            QuranWord("إِنَّ", "ইন্না", "নিশ্চয়ই", "انن", "তাকিদ হরফ (দৃঢ়তাসূচক)", "Indeed", "Emphatic Particle"),
            QuranWord("الْإِنسَانَ", "আল-ইনসানা", "সকল মানুষ", "انس", "ইসমে ইন্না", "mankind", "Noun"),
            QuranWord("لَفِي", "লাফী", "অবশ্যই নিমজ্জিত", "في", "লামে মুজাহলাকা + জার", "is surely in", "Emphasis + Preposition"),
            QuranWord("خُسْرٍ", "খুসরিন", "মহাক্ষতিতে", "خسر", "মাজরুর বিশেষ্য", "loss", "Genitive Noun")
        ),
        "103_3" to listOf(
            QuranWord("إِلَّا", "ইল্লা", "ব্যতীত তারা", "الا", "ইসতিসনা হরফ", "Except", "Exception Particle"),
            QuranWord("الَّذِينَ", "আল্লাযীনা", "যারা", "الذي", "ইসমে মাওসুল", "those who", "Relative Pronoun"),
            QuranWord("آمَنُوا", "আমানূ", "ঈমান এনেছে", "امن", "ফে'লে মাযী (অতীত বহুবচন)", "believe", "Past Verb Plural"),
            QuranWord("وَعَمِلُوا", "ওয়া 'আমিলূ", "এবং সৎকর্ম করেছে", "عمل", "সংযোজক + ক্রিয়া", "and do", "Conjunction + Verb"),
            QuranWord("الصَّالِحَاتِ", "আস-সালিহাতি", "নেক আমলসমূহ", "صلح", "মাফ'উল বহুবচন", "righteous deeds", "Plural Noun"),
            QuranWord("وَتَوَاصَوْا", "ওয়া তাওয়াসাও", "এবং পরস্পরে উপদেশ দিয়েছে", "وصي", "পারস্পরিক উপদেশ", "and advise each other", "Reciprocal Verb"),
            QuranWord("بِالْحَقِّ", "বিল-হাক্কি", "সত্যের", "حقق", "জার ও মাজরুর", "to truth", "Preposition + Noun"),
            QuranWord("وَتَوَاصَوْا", "ওয়া তাওয়াসাও", "এবং পরস্পরে নসীহত করেছে", "وصي", "ক্রিয়া", "and advise each other", "Reciprocal Verb"),
            QuranWord("بِالصَّبْرِ", "বিস-সাবর", "ধৈর্য ও সহনশীলতার", "صبر", "জার ও মাজরুর", "to patience", "Preposition + Noun")
        )
    )

    /**
     * Retrieves rich word-by-word analysis for a verse,
     * or generates an intelligent contextual breakdown if not in static dictionary.
     */
    fun getWordsForAyah(surahNumber: Int, ayahNumber: Int, arabicText: String): List<QuranWord> {
        val key = "${surahNumber}_$ayahNumber"
        val existing = VERSE_DICTIONARY[key]
        if (existing != null) return existing

        // Algorithmic breakdown fallback: splits Arabic tokens cleanly
        val rawTokens = arabicText.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
        return rawTokens.mapIndexed { index, token ->
            val clean = token.replace("[\u064B-\u065F\u0670\u06D6-\u06ED]".toRegex(), "")
            val (bnMean, enMean) = deriveContextMeaning(clean, index)
            val (bnGram, enGram) = deriveGrammarRole(token, clean)
            QuranWord(
                arabic = token,
                transliteration = com.example.data.util.ArabicTransliterationHelper.transliterate(token, "bn"),
                bengaliMeaning = bnMean,
                rootWord = deriveRootToken(clean),
                grammarContext = bnGram,
                englishMeaning = enMean,
                grammarContextEnglish = enGram
            )
        }
    }

    private fun deriveContextMeaning(clean: String, index: Int): Pair<String, String> {
        return when {
            clean.startsWith("ال") -> "নির্দিষ্ট সত্তা / বিষয়" to "Definite noun / subject"
            clean.startsWith("و") -> "এবং / শপথসূচক" to "And / by"
            clean.startsWith("ف") -> "অতঃপর / সুতরাং" to "Then / so"
            clean.startsWith("ب") -> "সহ / দ্বারা" to "With / by"
            clean.startsWith("ل") -> "জন্য / কারণে" to "For / belonging to"
            clean.contains("الله") -> "আল্লাহ তা'আলা" to "Allah"
            clean.contains("رب") -> "প্রতিপালক রব" to "Lord & Sustainer"
            clean.contains("قال") -> "বললেন / বলেছিলেন" to "Said"
            clean.contains("كان") -> "হয়েছিল / বিদ্যমান ছিল" to "Was / existed"
            clean.contains("علم") -> "জ্ঞান ও প্রজ্ঞা" to "Knowledge / wisdom"
            clean.contains("رحم") -> "রহমত ও দয়া" to "Mercy"
            clean.contains("يوم") -> "দিন / দিবস" to "Day"
            clean.contains("ارض") -> "জমিন / পৃথিবী" to "Earth"
            clean.contains("سما") -> "আকাশমন্ডলী" to "Heavens"
            else -> "শব্দের অর্থ [পদ #${index + 1}]" to "Word #${index + 1}"
        }
    }

    private fun deriveRootToken(clean: String): String {
        val trimmed = clean.removePrefix("ال").removePrefix("و").removePrefix("ف").removePrefix("ب").removePrefix("ل")
        return if (trimmed.length >= 3) trimmed.take(3) else if (trimmed.isNotEmpty()) trimmed else "أصل"
    }

    private fun deriveGrammarRole(raw: String, clean: String): Pair<String, String> {
        return when {
            raw.startsWith("بِ") || raw.startsWith("فِي") || raw.startsWith("عَلَى") ->
                "জার ও মাজরুর (অব্যয়যুক্ত)" to "Prepositional Phrase"
            clean.startsWith("ال") ->
                "মু'আররাফ বিল লাম (বিশেষ্য/اسم)" to "Definite Noun (Ism)"
            raw.startsWith("وَ") ->
                "সংযোজক অব্যয় (حرف عطف)" to "Conjunction (Harf)"
            raw.endsWith("ُونَ") || raw.endsWith("ِينَ") ->
                "জমে মুযাক্কার সালেম (বহুবচন)" to "Sound Masculine Plural"
            raw.endsWith("تُ") || raw.endsWith("نَا") ->
                "ফে'লে মাযী (অতীত ক্রিয়া)" to "Past Tense Verb"
            raw.startsWith("يَ") || raw.startsWith("تَ") || raw.startsWith("نَ") ->
                "ফে'লে মুযারে (বর্তমান/ভবিষ্যৎ)" to "Present/Future Verb"
            else ->
                "কুরআনিক শব্দ (اسم / فعل / حرف)" to "Arabic Word (Ism / Fi'l / Harf)"
        }
    }
}

object TadabburRepository {

    val DAILY_REFLECTIONS: List<DailyTadabbur> = listOf(
        DailyTadabbur(
            id = 1,
            surahName = "সূরা আল-বাক্বারাহ",
            surahNameEn = "Surah Al-Baqarah",
            ayahRef = "আয়াত ১৫২",
            ayahRefEn = "Ayah 152",
            arabicText = "فَاذْكُرُونِي أَذْكُرْكُمْ وَاشْكُرُوا لِي وَلَا تَكْفُرُونِ",
            transliterationBengali = "ফাযকুরূনী আযকুরকুম ওয়াশকুরূ লী ওয়ালা তাকফুরূন",
            transliterationEn = "Fadhkuruni adhkurkum washkuru li wa la takfurun",
            translationBengali = "সুতরাং তোমরা আমাকে স্মরণ কর, আমিও তোমাদের স্মরণ রাখব। আর আমার প্রতি কৃতজ্ঞ হও এবং অকৃতজ্ঞ হয়ো না।",
            translationEnglish = "So remember Me; I will remember you. And be grateful to Me and do not deny Me.",
            tafsirExcerpt = "ইমাম ইবনে কাসির (রহ.) বলেন: বান্দা যখন দুনিয়ায় তাঁর পালনকর্তা আল্লাহকে নির্জনে বা মজলিসে স্মরণ করে, আল্লাহ তা'আলা এর চেয়েও উত্তম ও সম্মানিত ফেরেশতাদের মজলিসে সেই বান্দাকে মর্যাদার সাথে স্মরণ করেন। এই আয়াতের দাবি হলো জীবনের প্রতিটি পদক্ষেপে আল্লাহর উপস্থিতি অনুভব করা।",
            tafsirExcerptEn = "Imam Ibn Kathir (RH) explains: When a servant remembers Allah in solitude or in a gathering, Allah remembers that servant with honor in a far loftier assembly of angels. This verse calls us to live every moment mindful of Allah's presence.",
            reflectionLessons = listOf(
                "আল্লাহর স্মরণই হৃদয়ের অস্থিরতা ও একাকিত্ব দূর করার একমাত্র চিরন্তন সমাধান।",
                "যেকোনো নিয়ামত পেলে মুখে 'আলহামদুলিল্লাহ' বলা এবং সেটিকে আল্লাহর আনুগত্যে ব্যয় করাই প্রকৃত কৃতজ্ঞতা।",
                "বিপদ বা ব্যস্ততায় আল্লাহকে ভুলে যাওয়া আত্মার সবচেয়ে বড় অপচয়।"
            ),
            reflectionLessonsEn = listOf(
                "Remembrance of Allah (Dhikr) is the lasting remedy for restlessness and loneliness.",
                "True gratitude means saying Alhamdulillah with the tongue and using blessings in Allah's obedience.",
                "Forgetting Allah amid busyness or hardship is the greatest loss a soul can suffer."
            ),
            practicalAction = "আজ দিনে অন্তত ১০০ বার আন্তরিকতার সাথে 'সুবহানাল্লাহি ওয়া বিহামদিহি' পাঠ করে আল্লাহর কৃতজ্ঞতা প্রকাশ করুন।",
            practicalActionEn = "Express gratitude today by sincerely reciting 'SubhanAllahi wa bihamdihi' at least 100 times."
        ),
        DailyTadabbur(
            id = 2,
            surahName = "সূরা আল-কাহফ",
            surahNameEn = "Surah Al-Kahf",
            ayahRef = "আয়াত ৪৬",
            ayahRefEn = "Ayah 46",
            arabicText = "الْمَالُ وَالْبَنُونَ زِينَةُ الْحَيَاةِ الدُّنْيَا ۖ وَالْبَاقِيَاتُ الصَّالِحَاتُ خَيْرٌ عِندَ رَبِّكَ ثَوَابًا وَخَيْرٌ أَمَلًا",
            transliterationBengali = "আল-মালু ওয়াল বানূনা যীনাতুল হায়াতিদ দুনয়া, ওয়াল বাক্বিয়াতুস সালিহাতু খাইরুন 'ইনদা রব্বিকা সাওয়াবাও ওয়া খাইরুন আমালা",
            transliterationEn = "Al-malu wal-banuna zinatul-hayatid-dunya, wal-baqiyatus-salihatu khayrun 'inda Rabbika thawaban wa khayrun amala",
            translationBengali = "ধন-সম্পদ এবং সন্তান-সন্ততি পার্থিব জীবনের সৌন্দর্য মাত্র; কিন্তু চিরস্থায়ী সৎকাজসমূহ আপনার প্রতিপালকের নিকট পুরস্কারের দিক দিয়ে শ্রেষ্ঠ এবং আশা-আকাঙ্ক্ষার দিক দিয়েও উত্তম।",
            translationEnglish = "Wealth and children are an adornment of this worldly life, but the enduring righteous deeds are better with your Lord for reward and better for one's hope.",
            tafsirExcerpt = "ইমাম সাদী (রহ.) ব্যাখ্যা করেছেন: পার্থিব সম্পদ ও পরিবার ক্ষণস্থায়ী মোহের মতো। আর 'আল-বাকিয়াতুস সালিহাত' হলো সেই সকল নেক আমল যা কিয়ামতের ময়দানে চিরস্থায়ী হয়ে আপনার সামনে হাজির থাকবে—যেমন নামাজ, তাসবীহ, তাহলীল, তাহমীদ ও আন্তরিক দান।",
            tafsirExcerptEn = "Imam al-Sa'di (RH) explains: Worldly wealth and status are fleeting ornaments, whereas 'Al-Baqiyat al-Salihat'—Salah, Tasbih, Tahlil, Tahmid, and sincere charity—remain forever with Allah as your lasting treasure.",
            reflectionLessons = listOf(
                "পার্থিব সঞ্চয় এখানেই থাকবে, কেবল আমল মৃত্যুর ওপারে সাথে যাবে।",
                "সন্তান ও সম্পদ যেন আল্লাহর জিকির থেকে গাফেল না করে ফেলে।",
                "ছোট ছোট নেক আমলকে তুচ্ছ মনে করবেন না; এগুলোই আখিরাতের আসল সঞ্চয়।"
            ),
            reflectionLessonsEn = listOf(
                "Worldly savings stay behind; only righteous deeds accompany us beyond death.",
                "Never allow wealth or responsibilities to distract the heart from remembering Allah.",
                "Never belittle small good deeds—they form the enduring capital of the Hereafter."
            ),
            practicalAction = "আজ কোনো পরিচিত অভাবীকে গোপনে কিছু অর্থ বা খাদ্য সাদাকাহ করুন।",
            practicalActionEn = "Quietly offer a small meal or charity to someone in need today."
        ),
        DailyTadabbur(
            id = 3,
            surahName = "সূরা আশ-শারহ",
            surahNameEn = "Surah Ash-Sharh",
            ayahRef = "আয়াত ৫-৬",
            ayahRefEn = "Ayah 5-6",
            arabicText = "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا ۝ إِنَّ مَعَ الْعُسْرِ يُسْرًا",
            transliterationBengali = "ফা ইন্না মা'আল 'উসরি ইউসরা, ইন্না মা'আল 'উসরি ইউসরা",
            transliterationEn = "Fa inna ma'al-'usri yusra, inna ma'al-'usri yusra",
            translationBengali = "নিশ্চয়ই কষ্টের সাথেই স্বস্তি রয়েছে। অবশ্যই কষ্টের সাথেই স্বস্তি রয়েছে।",
            translationEnglish = "For indeed, with hardship comes ease. Indeed, with hardship comes ease.",
            tafsirExcerpt = "আরবি ব্যাকরণে 'আল-'উসর' (কষ্ট) নির্দিষ্ট বিশেষ্য হিসেবে এসেছে, আর 'ইউসর' (স্বস্তি) অনির্দিষ্ট হিসেবে দুবার এসেছে। রাসুলুল্লাহ (সা.) বলেছেন: 'এক কষ্ট কখনো দুই স্বস্তির ওপর বিজয়ী হতে পারে না।' অর্থাৎ প্রতিটি পরীক্ষার গভীরে আল্লাহ একাধিক সমাধানের রহমত লুকিয়ে রেখেছেন।",
            tafsirExcerptEn = "In Arabic grammar, 'al-usr' (hardship) is definite (meaning one hardship), while 'yusran' (ease) is repeated indefinitely (meaning multiple forms of ease). As the Prophet ﷺ noted, one hardship can never overcome two eases.",
            reflectionLessons = listOf(
                "কষ্ট ও স্বস্তি পালাক্রমে আসে না, বরং কষ্টের ভেতরেই স্বস্তির বীজ লুকায়িত থাকে।",
                "ধৈর্য ধারণকারী মুমিন কখনো আল্লাহর রহমত থেকে নিরাশ হয় না।",
                "বর্তমান কঠিন পরিস্থিতি চিরস্থায়ী নয়; মুক্তির সকাল অতি নিকটে।"
            ),
            reflectionLessonsEn = listOf(
                "Ease does not merely follow hardship—seeds of relief are placed right within the trial.",
                "A patient believer never despairs of Allah's mercy.",
                "Every difficulty is temporary; the dawn of relief is closer than it seems."
            ),
            practicalAction = "জীবনের যেকোনো চলমান উদ্বেগের কথা সিজদায় গিয়ে মন খুলে আল্লাহর কাছে নিবেদন করুন।",
            practicalActionEn = "Pour out your current worries directly to Allah in prolonged Sujood (prostration) today."
        ),
        DailyTadabbur(
            id = 4,
            surahName = "সূরা আল-মুলক",
            surahNameEn = "Surah Al-Mulk",
            ayahRef = "আয়াত ২",
            ayahRefEn = "Ayah 2",
            arabicText = "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا",
            transliterationBengali = "আল্লাযী খালাকাল মাওতা ওয়াল হায়াতা লিয়াবলুওয়াকুম আইয়্যুকুম আহসানু 'আমালা",
            transliterationEn = "Alladhi khalaqal-mawta wal-hayata liyabluwakum ayyukum ahsanu 'amala",
            translationBengali = "যিনি সৃষ্টি করেছেন মৃত্যু ও জীবন, তোমাদের পরীক্ষা করার জন্য—আমলের দিক দিয়ে তোমাদের মধ্যে কে সর্বোত্তম?",
            translationEnglish = "He who created death and life to test you as to which of you is best in deed.",
            tafsirExcerpt = "ফুদাইল ইবনে ইয়াদ (রহ.) বলেন: 'আহসানু আমালা' অর্থ সবচেয়ে ইখলাসপূর্ণ ও সবচেয়ে সুন্নাহসম্মত আমল। আমল যদি খাঁটি নিয়তে হয় কিন্তু সুন্নাহ অনুযায়ী না হয়, তা কবুল হবে না; আর যদি সুন্নাহ মতো হয় কিন্তু নিয়ত খাঁটি না হয়, তবুও কবুল হবে না।",
            tafsirExcerptEn = "Al-Fudayl ibn 'Iyad (RH) explained that 'best in deed' means the most sincere and the most aligned with the Sunnah. A deed done with sincerity yet outside the Sunnah is unaccepted, and one following the Sunnah without sincerity is also unaccepted.",
            reflectionLessons = listOf(
                "জীবনের লক্ষ্য পার্থিব প্রতিযোগিতা নয়, বরং রবের সন্তুষ্টি অর্জনে শ্রেষ্ঠ আমল পেশ করা।",
                "আমলের পরিমাণের চেয়ে গুণগত মান ও ইখলাস আল্লাহর কাছে সর্বাধিক প্রিয়।",
                "মৃত্যুর স্মরণ জীবনের প্রতিটি মুহূর্তকে অর্থপূর্ণ ও সতর্ক করে তোলে।"
            ),
            reflectionLessonsEn = listOf(
                "Life's true purpose is not worldly rivalry, but presenting deeds pleasing to your Lord.",
                "Quality, sincerity, and devotion outweigh sheer quantity in the sight of Allah.",
                "Remembering death gives clarity, urgency, and meaning to every hour."
            ),
            practicalAction = "আজকের প্রতি ওয়াক্তের নামাজ তাড়াহুড়ো না করে ধীরস্থিরভাবে খুশু-খুযুর সাথে আদায় করুন।",
            practicalActionEn = "Pray each Salah today calmly without rushing, focusing on Khushu' (humble presence)."
        )
    )

    fun getTodayTadabbur(): DailyTadabbur {
        val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        val index = (dayOfYear - 1) % DAILY_REFLECTIONS.size
        return DAILY_REFLECTIONS[index]
    }
}
