package com.example.data.repository

import com.example.data.model.Hadith
import com.example.data.model.HadithBook
import com.example.data.model.HadithChapter

object HadithMasterRepository {

    val HADITH_BOOKS: List<HadithBook> = listOf(
        HadithBook(
            id = "bukhari",
            nameBengali = "সহীহ বুখারী",
            nameArabic = "صحيح البخاري",
            compiler = "ইমাম মুহাম্মদ ইবনে ইসমাইল আল-বুখারী (রহ.)",
            totalHadithCount = "৭,৫৬৩টি হাদিস",
            totalChaptersCount = 97,
            totalPagesCount = 1890,
            descriptionBengali = "কুরআনের পরেই সর্বাধিক বিশুদ্ধ গ্রন্থ। এতে বিশুদ্ধ সনদে সংকলিত দৈনন্দিন জীবনের সকল বিধিবিধান রয়েছে।",
            colorHex = "#00E676",
            verifiedSahih = true,
            nameEnglish = "Sahih al-Bukhari",
            compilerEnglish = "Imam Muhammad ibn Ismail al-Bukhari (RA)",
            totalHadithCountEnglish = "7,563 Hadiths",
            descriptionEnglish = "The most authentic book after the Quran, compiling verified Prophetic traditions across daily Islamic jurisprudence and life."
        ),
        HadithBook(
            id = "muslim",
            nameBengali = "সহীহ মুসলিম",
            nameArabic = "صحيح مسلم",
            compiler = "ইমাম মুসলিম ইবনুল হাজ্জাজ (রহ.)",
            totalHadithCount = "৭,৫০০টি হাদিস",
            totalChaptersCount = 56,
            totalPagesCount = 1550,
            descriptionBengali = "অনবদ্য বিন্যাস ও শক্তিশালী সনদের অপূর্ব সমন্বয়। বিশুদ্ধতার দিক থেকে দ্বিতীয় স্তম্ভ।",
            colorHex = "#E5C158",
            verifiedSahih = true,
            nameEnglish = "Sahih Muslim",
            compilerEnglish = "Imam Muslim ibn al-Hajjaj (RA)",
            totalHadithCountEnglish = "7,500 Hadiths",
            descriptionEnglish = "Renowned for its immaculate arrangement and rigorous chains of narration; the second pillar of authentic Hadith."
        ),
        HadithBook(
            id = "tirmidhi",
            nameBengali = "জামে আত-তিরমিযী",
            nameArabic = "جامع الترمذي",
            compiler = "ইমাম আবু ঈসা মুহাম্মদ আত-তিরমিযী (রহ.)",
            totalHadithCount = "৩,৯৫৬টি হাদিস",
            totalChaptersCount = 49,
            totalPagesCount = 1200,
            descriptionBengali = "হাদিসের মান (সহীহ, হাসান, জয়ীফ) নির্ণয় ও ফুকাহায়ে কেরামের মতামতের প্রামাণিক গ্রন্থ।",
            colorHex = "#FFB300",
            verifiedSahih = true,
            nameEnglish = "Jami' at-Tirmidhi",
            compilerEnglish = "Imam Abu Isa Muhammad at-Tirmidhi (RA)",
            totalHadithCountEnglish = "3,956 Hadiths",
            descriptionEnglish = "Authoritative collection featuring Hadith grading (Sahih, Hasan, Da'if) and comparative juristic commentary."
        ),
        HadithBook(
            id = "abudawood",
            nameBengali = "সুনান আবু দাউদ",
            nameArabic = "سنن أبي داود",
            compiler = "ইমাম আবু দাউদ সুলাইমান আস-সিজিস্তানী (রহ.)",
            totalHadithCount = "৫,২৭৪টি হাদিস",
            totalChaptersCount = 43,
            totalPagesCount = 1350,
            descriptionBengali = "শরীয়তের হুকুম-আহকাম ও ফিকহি মাসআলা সংক্রান্ত হাদিসের সবচেয়ে সমৃদ্ধ কোষ।",
            colorHex = "#80CBC4",
            verifiedSahih = true,
            nameEnglish = "Sunan Abi Dawud",
            compilerEnglish = "Imam Abu Dawud Sulayman as-Sijistani (RA)",
            totalHadithCountEnglish = "5,274 Hadiths",
            descriptionEnglish = "Rich treasury of Hadith reports focusing on Shariah rulings and practical jurisprudence."
        ),
        HadithBook(
            id = "nasai",
            nameBengali = "সুনান আন-নাসাঈ",
            nameArabic = "سنن النسائي",
            compiler = "ইমাম আহমাদ ইবনে শু'আইব আন-নাসাঈ (রহ.)",
            totalHadithCount = "৫,৭৫৮টি হাদিস",
            totalChaptersCount = 52,
            totalPagesCount = 1420,
            descriptionBengali = "ফিকহুল হাদিস ও সুক্ষ্ম সনদের বিচারে অন্যতম শ্রেষ্ঠ ও প্রামাণিক সুনান গ্রন্থ।",
            colorHex = "#26A69A",
            verifiedSahih = true,
            nameEnglish = "Sunan an-Nasa'i",
            compilerEnglish = "Imam Ahmad ibn Shu'ayb an-Nasa'i (RA)",
            totalHadithCountEnglish = "5,758 Hadiths",
            descriptionEnglish = "One of the finest Sunan works noted for meticulous narrator criticism and legal clarity."
        ),
        HadithBook(
            id = "ibnmajah",
            nameBengali = "সুনান ইবনে মাজাহ",
            nameArabic = "سنن ابن ماجه",
            compiler = "ইমাম মুহাম্মদ ইবনে ইয়াজিদ ইবনে মাজাহ (রহ.)",
            totalHadithCount = "৪,৩৪১টি হাদিস",
            totalChaptersCount = 37,
            totalPagesCount = 1100,
            descriptionBengali = "সিহাহ সিত্তার ষষ্ঠ গ্রন্থ। অধ্যায় বিন্যাসের চমৎকারিত্ব ও বিরল মাসআলার অনন্য সমাবেশ।",
            colorHex = "#4DB6AC",
            verifiedSahih = true,
            nameEnglish = "Sunan Ibn Majah",
            compilerEnglish = "Imam Muhammad ibn Yazid ibn Majah (RA)",
            totalHadithCountEnglish = "4,341 Hadiths",
            descriptionEnglish = "The sixth book of Sihah Sittah, acclaimed for clear chapter structuring and unique legal narrations."
        ),
        HadithBook(
            id = "riyadus_salihin",
            nameBengali = "রিয়াদুস সালেহীন",
            nameArabic = "رياض الصالحين",
            compiler = "ইমাম ইয়াহইয়া ইবনে শারাফ আন-নববী (রহ.)",
            totalHadithCount = "১,৮৯৬টি হাদিস",
            totalChaptersCount = 372,
            totalPagesCount = 850,
            descriptionBengali = "আত্মশুদ্ধি, আখলাক ও নেককারদের পথপ্রদর্শক বিশ্ববিখ্যাত হাদিস সংকলন।",
            colorHex = "#81C784",
            verifiedSahih = true,
            nameEnglish = "Riyad as-Salihin",
            compilerEnglish = "Imam Yahya ibn Sharaf an-Nawawi (RA)",
            totalHadithCountEnglish = "1,896 Hadiths",
            descriptionEnglish = "Renowned compilation of Prophetic traditions guiding spiritual purification, character, and righteousness."
        )
    )

    val CHAPTERS: Map<String, List<HadithChapter>> = mapOf(
        "bukhari" to listOf(
            HadithChapter(
                id = "bukhari_ch_1",
                bookId = "bukhari",
                chapterNumber = 1,
                nameBengali = "কিতাবুল ওহী (ওহীর সূচনা পর্ব)",
                nameArabic = "كتاب بدء الوحي",
                hadithRange = "১ - ৭",
                hadithCount = 7,
                startPage = 1,
                endPage = 4,
                nameEnglish = "Book of Revelation (Kitab Bad' al-Wahi)"
            ),
            HadithChapter(
                id = "bukhari_ch_2",
                bookId = "bukhari",
                chapterNumber = 2,
                nameBengali = "কিতাবুল ঈমান (ঈমান ও বিশ্বাস)",
                nameArabic = "كتاب الإيمان",
                hadithRange = "৮ - ৫৮",
                hadithCount = 51,
                startPage = 5,
                endPage = 22,
                nameEnglish = "Book of Faith (Kitab al-Iman)"
            ),
            HadithChapter(
                id = "bukhari_ch_3",
                bookId = "bukhari",
                chapterNumber = 3,
                nameBengali = "কিতাবুল ইলম (জ্ঞান অর্জন ও মর্যাদা)",
                nameArabic = "كتاب العلم",
                hadithRange = "৫৯ - ১৩৪",
                hadithCount = 76,
                startPage = 23,
                endPage = 48,
                nameEnglish = "Book of Knowledge (Kitab al-'Ilm)"
            ),
            HadithChapter(
                id = "bukhari_ch_4",
                bookId = "bukhari",
                chapterNumber = 4,
                nameBengali = "কিতাবুল ওযু (পবিত্রতা ও ওযুর বিধান)",
                nameArabic = "كتاب الوضوء",
                hadithRange = "১৩৫ - ২৪৭",
                hadithCount = 113,
                startPage = 49,
                endPage = 85,
                nameEnglish = "Book of Ablution (Kitab al-Wudu)"
            ),
            HadithChapter(
                id = "bukhari_ch_5",
                bookId = "bukhari",
                chapterNumber = 8,
                nameBengali = "কিতাবুস সালাত (নামাজ প্রতিষ্ঠা)",
                nameArabic = "كتاب الصلاة",
                hadithRange = "৩৪৯ - ৫২০",
                hadithCount = 172,
                startPage = 86,
                endPage = 150,
                nameEnglish = "Book of Prayers (Kitab as-Salat)"
            ),
            HadithChapter(
                id = "bukhari_ch_6",
                bookId = "bukhari",
                chapterNumber = 24,
                nameBengali = "কিতাবুয যাকাত (যাকাত ও দানশীলতা)",
                nameArabic = "كتاب الزكاة",
                hadithRange = "১৩৯৫ - ১৫১৩",
                hadithCount = 119,
                startPage = 151,
                endPage = 195,
                nameEnglish = "Book of Obligatory Charity (Kitab az-Zakat)"
            ),
            HadithChapter(
                id = "bukhari_ch_7",
                bookId = "bukhari",
                chapterNumber = 30,
                nameBengali = "কিতাবুস সাওম (রোজা ও রমজান)",
                nameArabic = "كتاب الصوم",
                hadithRange = "১৮৯১ - ২০৬০",
                hadithCount = 170,
                startPage = 196,
                endPage = 245,
                nameEnglish = "Book of Fasting (Kitab as-Sawm)"
            ),
            HadithChapter(
                id = "bukhari_ch_8",
                bookId = "bukhari",
                chapterNumber = 78,
                nameBengali = "কিতাবুল আদব (সদাচার ও উত্তম ব্যবহার)",
                nameArabic = "كتاب الأدب",
                hadithRange = "৫৯৭০ - ৬২৪৩",
                hadithCount = 274,
                startPage = 246,
                endPage = 320,
                nameEnglish = "Book of Good Manners (Kitab al-Adab)"
            ),
            HadithChapter(
                id = "bukhari_ch_9",
                bookId = "bukhari",
                chapterNumber = 80,
                nameBengali = "কিতাবুদ দাওয়াত (দু'আ ও ইস্তিগফার)",
                nameArabic = "كتاب الدعوات",
                hadithRange = "৬৩৩৮ - ৬৪১২",
                hadithCount = 75,
                startPage = 321,
                endPage = 360,
                nameEnglish = "Book of Invocations (Kitab ad-Da'awat)"
            ),
            HadithChapter(
                id = "bukhari_ch_10",
                bookId = "bukhari",
                chapterNumber = 81,
                nameBengali = "কিতাবুর রিকাক (হৃদয়গলানো উপদেশ)",
                nameArabic = "كتاب الرقاق",
                hadithRange = "৬৪১৬ - ৬৫৯৩",
                hadithCount = 178,
                startPage = 361,
                endPage = 420,
                nameEnglish = "Book of Heart-Softeners (Kitab ar-Riqaq)"
            )
        ),
        "muslim" to listOf(
            HadithChapter(
                id = "muslim_ch_1",
                bookId = "muslim",
                chapterNumber = 1,
                nameBengali = "কিতাবুল ঈমান (তাওহীদ ও পরকাল)",
                nameArabic = "كتاب الإيمان",
                hadithRange = "১ - ৪৩৬",
                hadithCount = 436,
                startPage = 1,
                endPage = 95,
                nameEnglish = "Book of Faith (Kitab al-Iman)"
            ),
            HadithChapter(
                id = "muslim_ch_2",
                bookId = "muslim",
                chapterNumber = 2,
                nameBengali = "কিতাবুত তাহারাত (পবিত্রতা অর্জনের নিয়ম)",
                nameArabic = "كتاب الطهارة",
                hadithRange = "৪৩৭ - ৫৮৩",
                hadithCount = 147,
                startPage = 96,
                endPage = 140,
                nameEnglish = "Book of Purification (Kitab at-Taharah)"
            ),
            HadithChapter(
                id = "muslim_ch_3",
                bookId = "muslim",
                chapterNumber = 4,
                nameBengali = "কিতাবুস সালাত (ফরজ ও সুন্নত নামাজ)",
                nameArabic = "كتاب الصلاة",
                hadithRange = "৭৩৪ - ১১৬০",
                hadithCount = 427,
                startPage = 141,
                endPage = 230,
                nameEnglish = "Book of Prayers (Kitab as-Salat)"
            ),
            HadithChapter(
                id = "muslim_ch_4",
                bookId = "muslim",
                chapterNumber = 13,
                nameBengali = "কিতাবুস সিয়াম (সিয়াম সাধনা ও তারাবিহ)",
                nameArabic = "كتاب الصيام",
                hadithRange = "২৪৯৫ - ২৭৭৮",
                hadithCount = 284,
                startPage = 231,
                endPage = 300,
                nameEnglish = "Book of Fasting (Kitab as-Siyam)"
            ),
            HadithChapter(
                id = "muslim_ch_5",
                bookId = "muslim",
                chapterNumber = 35,
                nameBengali = "কিতাবুল বিররি ওয়াস সিলাহ (আত্মীয়তার বন্ধন)",
                nameArabic = "كتاب البر والصلة والآداب",
                hadithRange = "৬৪৮৮ - ৬৬৭২",
                hadithCount = 185,
                startPage = 301,
                endPage = 365,
                nameEnglish = "Book of Virtue & Good Manners (Kitab al-Birr)"
            )
        ),
        "tirmidhi" to listOf(
            HadithChapter(
                id = "tirmidhi_ch_1",
                bookId = "tirmidhi",
                chapterNumber = 1,
                nameBengali = "পবিত্রতা ও ওযুর অধ্যায়",
                nameArabic = "أبواب الطهارة",
                hadithRange = "১ - ১৪৮",
                hadithCount = 148,
                startPage = 1,
                endPage = 50,
                nameEnglish = "Chapters on Purification"
            ),
            HadithChapter(
                id = "tirmidhi_ch_2",
                bookId = "tirmidhi",
                chapterNumber = 2,
                nameBengali = "সালাতের ওয়াক্ত ও রুকন",
                nameArabic = "أبواب الصلاة",
                hadithRange = "১৪৯ - ৪৫১",
                hadithCount = 303,
                startPage = 51,
                endPage = 130,
                nameEnglish = "Chapters on Prayer"
            ),
            HadithChapter(
                id = "tirmidhi_ch_3",
                bookId = "tirmidhi",
                chapterNumber = 27,
                nameBengali = "সদাচার ও আত্মীয়তার হক",
                nameArabic = "أبواب البر والصلة",
                hadithRange = "১৮৯৭ - ২০৩২",
                hadithCount = 136,
                startPage = 131,
                endPage = 185,
                nameEnglish = "Chapters on Righteousness & Kinship"
            ),
            HadithChapter(
                id = "tirmidhi_ch_4",
                bookId = "tirmidhi",
                chapterNumber = 48,
                nameBengali = "রাসূল (সাঃ)-এর প্রিয় দু'আসমূহ",
                nameArabic = "أبواب الدعوات",
                hadithRange = "৩৩৭৩ - ৩৬০৪",
                hadithCount = 232,
                startPage = 186,
                endPage = 250,
                nameEnglish = "Chapters on Supplications"
            )
        ),
        "abudawood" to listOf(
            HadithChapter(
                id = "abudawood_ch_1",
                bookId = "abudawood",
                chapterNumber = 1,
                nameBengali = "পবিত্রতা অর্জনের বিধান",
                nameArabic = "كتاب الطهارة",
                hadithRange = "১ - ৩৯০",
                hadithCount = 390,
                startPage = 1,
                endPage = 90,
                nameEnglish = "Purification (Kitab at-Taharah)"
            ),
            HadithChapter(
                id = "abudawood_ch_2",
                bookId = "abudawood",
                chapterNumber = 2,
                nameBengali = "সালাতের নিয়ম ও ক্বেরাত",
                nameArabic = "كتاب الصلاة",
                hadithRange = "৩৯১ - ১১৬১",
                hadithCount = 771,
                startPage = 91,
                endPage = 230,
                nameEnglish = "Prayer (Kitab as-Salat)"
            ),
            HadithChapter(
                id = "abudawood_ch_3",
                bookId = "abudawood",
                chapterNumber = 40,
                nameBengali = "সুন্নাহ আঁকড়ে ধরা ও বিদ'আত বর্জন",
                nameArabic = "كتاب السنة",
                hadithRange = "৪৫৯৬ - ৪৭৭২",
                hadithCount = 177,
                startPage = 231,
                endPage = 280,
                nameEnglish = "Model Behavior of the Prophet (Kitab as-Sunnah)"
            ),
            HadithChapter(
                id = "abudawood_ch_4",
                bookId = "abudawood",
                chapterNumber = 41,
                nameBengali = "উত্তম শিষ্টাচার ও চালচলন",
                nameArabic = "كتاب الأدب",
                hadithRange = "৪৭৭৩ - ৫২৭৪",
                hadithCount = 502,
                startPage = 281,
                endPage = 360,
                nameEnglish = "General Behavior (Kitab al-Adab)"
            )
        ),
        "nasai" to listOf(
            HadithChapter(
                id = "nasai_ch_1",
                bookId = "nasai",
                chapterNumber = 1,
                nameBengali = "পবিত্রতা ও মিসওয়াকের সুন্নত",
                nameArabic = "كتاب الطهارة",
                hadithRange = "১ - ৩২৫",
                hadithCount = 325,
                startPage = 1,
                endPage = 80,
                nameEnglish = "The Book of Purification"
            ),
            HadithChapter(
                id = "nasai_ch_2",
                bookId = "nasai",
                chapterNumber = 5,
                nameBengali = "সালাত ও কিবলার আহকাম",
                nameArabic = "كتاب القبلة",
                hadithRange = "৭৪৩ - ৮৮৫",
                hadithCount = 143,
                startPage = 81,
                endPage = 140,
                nameEnglish = "The Book of Salah & Qiblah"
            ),
            HadithChapter(
                id = "nasai_ch_3",
                bookId = "nasai",
                chapterNumber = 20,
                nameBengali = "তাহাজ্জুদ ও কিয়ামুল লাইল",
                nameArabic = "كتاب قيام الليل وتطوع النهار",
                hadithRange = "১৬০০ - ১৮১৭",
                hadithCount = 218,
                startPage = 141,
                endPage = 200,
                nameEnglish = "The Book of Night Prayer (Qiyam al-Layl)"
            )
        ),
        "ibnmajah" to listOf(
            HadithChapter(
                id = "ibnmajah_ch_1",
                bookId = "ibnmajah",
                chapterNumber = 1,
                nameBengali = "সুন্নাহর মর্যাদা ও আনুগত্য",
                nameArabic = "كتاب المقدمة في فضائل أصحاب رسول الله",
                hadithRange = "১ - ২৬৬",
                hadithCount = 266,
                startPage = 1,
                endPage = 70,
                nameEnglish = "The Book of the Sunnah"
            ),
            HadithChapter(
                id = "ibnmajah_ch_2",
                bookId = "ibnmajah",
                chapterNumber = 12,
                nameBengali = "হালাল বাণিজ্য ও সততা",
                nameArabic = "كتاب التجارات",
                hadithRange = "২১৩৭ - ২৩১৭",
                hadithCount = 181,
                startPage = 71,
                endPage = 125,
                nameEnglish = "The Chapters on Business Transactions"
            ),
            HadithChapter(
                id = "ibnmajah_ch_3",
                bookId = "ibnmajah",
                chapterNumber = 37,
                nameBengali = "দুনিয়াবিমুখতা (যুহদ) ও আখিরাত",
                nameArabic = "كتاب الزهد",
                hadithRange = "৪১০১ - ৪৩৪১",
                hadithCount = 241,
                startPage = 126,
                endPage = 190,
                nameEnglish = "The Book of Zuhd (Asceticism)"
            )
        ),
        "riyadus_salihin" to listOf(
            HadithChapter(
                id = "riyadus_ch_1",
                bookId = "riyadus_salihin",
                chapterNumber = 1,
                nameBengali = "ইখলাস ও খাঁটি নিয়ত",
                nameArabic = "باب الإخلاص وإحضار النية",
                hadithRange = "১ - ১২",
                hadithCount = 12,
                startPage = 1,
                endPage = 15,
                nameEnglish = "Sincerity & Intention (Ikhlas)"
            ),
            HadithChapter(
                id = "riyadus_ch_2",
                bookId = "riyadus_salihin",
                chapterNumber = 2,
                nameBengali = "তাওবা ও আল্লাহর দিকে প্রত্যাবর্তন",
                nameArabic = "باب التوبة",
                hadithRange = "১৩ - ২৬",
                hadithCount = 14,
                startPage = 16,
                endPage = 35,
                nameEnglish = "Repentance (At-Tawbah)"
            ),
            HadithChapter(
                id = "riyadus_ch_3",
                bookId = "riyadus_salihin",
                chapterNumber = 3,
                nameBengali = "ধৈর্য ও অবিচলতা (সবর)",
                nameArabic = "باب الصبر",
                hadithRange = "২৭ - ৫৪",
                hadithCount = 28,
                startPage = 36,
                endPage = 65,
                nameEnglish = "Patience & Perseverance (As-Sabr)"
            ),
            HadithChapter(
                id = "riyadus_ch_4",
                bookId = "riyadus_salihin",
                chapterNumber = 25,
                nameBengali = "আল্লাহর ওপর তাওয়াক্কুল ও ভরসা",
                nameArabic = "باب اليقين والتوكل",
                hadithRange = "৭৪ - ৮৩",
                hadithCount = 10,
                startPage = 66,
                endPage = 85,
                nameEnglish = "Reliance Upon Allah (At-Tawakkul)"
            )
        )
    )

    fun getChaptersForBook(bookId: String): List<HadithChapter> {
        return CHAPTERS[bookId] ?: emptyList()
    }

    fun getChapter(bookId: String, chapterId: String): HadithChapter? {
        return CHAPTERS[bookId]?.find { it.id == chapterId }
    }
}
