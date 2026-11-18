package com.example.data.model.dua

import com.example.data.model.Dua
import com.example.data.model.HisnulPillar
import com.example.data.model.WaqtCategory

/**
 * 🌙 Comprehensive Authentic Supplications for Sawm, Ramadan, Suhoor, Iftar & Laylat al-Qadr
 */
object SawmRamadanDuaData {

    val duas: List<Dua> = listOf(
        // ---------------------------------------------------------------------
        // 1. IFTAR & SUHOOR (সেহরি ও ইফতার)
        // ---------------------------------------------------------------------
        Dua(
            id = "dua_iftar_sahih",
            category = "Fasting",
            categoryBengali = "সাওম",
            waqtCategory = WaqtCategory.MAGHRIB_EVENING,
            hisnulPillar = HisnulPillar.FASTING_SAWM,
            titleBengali = "ইফতারের বিশুদ্ধতম সহীহ দু'আ (যাহাবায যামাউ)",
            titleEnglish = "Authentic Supplication for Breaking the Fast (Iftar)",
            textArabic = "ذَهَبَ الظَّمَأُ، وَابْتَلَّتِ الْعُرُوقُ، وَثَبَتَ الْأَجْرُ إِنْ شَاءَ اللَّهُ",
            transliteration = "Dhahabadh-dhama'u, wabtallatil-'urooqu, wa thabatal-ajru in sha Allah.",
            transliterationBengali = "যাহাবায যমউ, ওয়াবতাল্লাতিল ‘উরূক্বু, ওয়া ছাবাতাল আজরু ইন শা-আল্লা-হ।",
            meaningBengali = "পিপাসা দূরীভূত হলো, শিরা-উপশিরা সিক্ত ও সতেজ হলো এবং ইনশাআল্লাহ সওয়াব ও প্রতিদান নিশ্চিত হলো।",
            meaningEnglish = "The thirst is gone, the veins are moistened and the reward is confirmed, if Allah wills.",
            reference = "সুনানে আবু দাউদ: ২৩৫৭, সুনানে দারা কুতনী: ২/৪০১ (সহীহ)",
            virtuesBengali = "ইফতার মুখে নেওয়ার সাথে সাথে এই সুন্নাত দু'আ পাঠ করা উত্তম। সারাদিনের পিপাসার পর এটি বান্দার কৃতজ্ঞতা ও সওয়াব নিশ্চিত করে।",
            repetitions = 1
        ),
        Dua(
            id = "dua_iftar_allahumma_laka_sumtu",
            category = "Fasting",
            categoryBengali = "সাওম",
            waqtCategory = WaqtCategory.MAGHRIB_EVENING,
            hisnulPillar = HisnulPillar.FASTING_SAWM,
            titleBengali = "ইফতারের প্রখ্যাত দু'আ (আল্লাহুম্মা লাকা সুমতু)",
            titleEnglish = "Traditional Iftar Supplication",
            textArabic = "اللَّهُمَّ لَكَ صُمْتُ، وَعَلَىٰ رِزْقِكَ أَفْطَرْتُ",
            transliteration = "Allahumma laka sumtu, wa 'ala rizqika aftartu.",
            transliterationBengali = "আল্লা-হুম্মা লাকা সুমতু, ওয়া ‘আলা- রিযক্বিকা আফত্বারতু।",
            meaningBengali = "হে আল্লাহ! আপনারই সন্তুষ্টির জন্য আমি রোজা রেখেছি এবং আপনারই দানকৃত রিজিকে ইফতার করছি।",
            meaningEnglish = "O Allah, for You I have fasted and with Your provision I have broken my fast.",
            reference = "সুনানে আবু দাউদ: ২৩৫৮",
            virtuesBengali = "ইফতারের মুহূর্তে বান্দার দোয়া ফেরত দেওয়া হয় না; এটি আল্লাহর জন্য রোজা রাখার আন্তরিক নিবেদন।",
            repetitions = 1
        ),
        Dua(
            id = "dua_iftar_at_someone_home",
            category = "Fasting",
            categoryBengali = "সাওম",
            waqtCategory = WaqtCategory.MAGHRIB_EVENING,
            hisnulPillar = HisnulPillar.FASTING_SAWM,
            titleBengali = "অন্যের বাড়িতে ইফতার করলে মেজবানের জন্য দু'আ",
            titleEnglish = "Supplication When Breaking Fast at Someone's House",
            textArabic = "أَفْطَرَ عِنْدَكُمُ الصَّائِمُونَ، وَأَكَلَ طَعَامَكُمُ الْأَبْرَارُ، وَصَلَّتْ عَلَيْكُمُ الْمَلَائِكَةُ",
            transliteration = "Aftara 'indakumus-sa'imoon, wa akala ta'amakumul-abrar, wa sallat 'alaykumul-mala'ikah.",
            transliterationBengali = "আফত্বারা ‘ইনদাকুমুস স-য়িমূনা, ওয়া আকালা ত্বা‘আ-মাকুমুল আবরা-রু, ওয়া সল্লাত ‘আলাইকুমুল মালা-ইকাহ।",
            meaningBengali = "রোজাদাররা যেন আপনাদের ঘরে এসে ইফতার করে, নেককার ও পুণ্যবানেরা যেন আপনাদের খাবার আহার করে এবং ফেরেশতারা যেন আপনাদের মাগফিরাতের জন্য আল্লাহর দরবারে দু'আ করতে থাকে।",
            meaningEnglish = "May the fasting break their fast in your home, and may the dutiful eat your food, and may the angels send blessings upon you.",
            reference = "সুনানে আবু দাউদ: ২০৪২, সুনানে ইবনে মাজাহ: ১৭৪৯ (সহীহ)",
            virtuesBengali = "রাসুলুল্লাহ ﷺ সাদ ইবনে উবাদাহ (রাঃ) এর ঘরে ইফতারের পর এই বরকতময় দু'আ করেছিলেন। মেজবানের রিজিক ও নেক আমল বৃদ্ধির জন্য সর্বোত্তম উপহার।",
            repetitions = 1
        ),
        Dua(
            id = "dua_suhoor_blessing",
            category = "Fasting",
            categoryBengali = "সাওম",
            waqtCategory = WaqtCategory.FAJR_MORNING,
            hisnulPillar = HisnulPillar.FASTING_SAWM,
            titleBengali = "সেহরির বরকত ও নিয়তের আমল",
            titleEnglish = "Blessing of Suhoor (Pre-Dawn Meal)",
            textArabic = "تَسَحَّرُوا فَإِنَّ فِي السَّحُورِ بَرَكَةً (نَوَيْتُ أَنْ أَصُومَ صَوْمَ شَهْرِ رَمَضَانَ)",
            transliteration = "Tasahharoo fa-inna fis-sahoori barakah.",
            transliterationBengali = "তাসাহ্হারূ ফা-ইন্না ফিস সাহূরি বারাকাহ। (মনে মনে সংকল্প: আমি আল্লাহর সন্তুষ্টিতে রোজা রাখার সংকল্প করছি)।",
            meaningBengali = "তোমরা সেহরি খাও, কেননা সেহরির আহারের মধ্যে বিশেষ বরকত ও কল্যাণ রয়েছে।",
            meaningEnglish = "Eat Suhoor, for indeed in Suhoor there is blessing.",
            reference = "সহীহ বুখারী: ১৯২৩, সহীহ মুসলিম: ১০৯৫",
            virtuesBengali = "রাসুলুল্লাহ ﷺ ইরশাদ করেছেন: আল্লাহ তায়ালা ও তাঁর ফেরেশতারা সেহরি গ্রহণকারীদের জন্য রহমত ও মাগফিরাতের দু'আ করেন। মুখে মুখে জটিল আরবি বাক্য উচ্চারণ নয়, বরং সেহরি খাওয়াই রোজার প্রকৃত নিয়ত।",
            repetitions = 1
        ),

        // ---------------------------------------------------------------------
        // 2. PATIENCE & ANGER RESTRAINT WHILE FASTING (রোজায় ধৈর্য)
        // ---------------------------------------------------------------------
        Dua(
            id = "dua_fasting_patience_innee_sa'im",
            category = "Fasting",
            categoryBengali = "সাওম",
            waqtCategory = WaqtCategory.ALL,
            hisnulPillar = HisnulPillar.FASTING_SAWM,
            titleBengali = "রোজায় কেউ গালাগালি বা ঝগড়া করতে এলে করণীয়",
            titleEnglish = "When Provoked or Insulted While Fasting",
            textArabic = "إِنِّي صَائِمٌ، إِنِّي صَائِمٌ",
            transliteration = "Innee sa'imun, innee sa'im.",
            transliterationBengali = "ইন্নী স-য়িম, ইন্নী স-য়িম।",
            meaningBengali = "আমি সিয়াম পালনকারী (রোজাদার), আমি সিয়াম পালনকারী।",
            meaningEnglish = "I am fasting, I am fasting.",
            reference = "সহীহ বুখারী: ১৮৯৪, সহীহ মুসলিম: ১১৫১",
            virtuesBengali = "নবীজি ﷺ বলেছেন: রোজা হলো ঢালস্বরূপ। অতএব তোমাদের কেউ রোজা থাকলে সে যেন কোনো অশ্লীল কথা না বলে এবং জাহেলী আচরণ না করে। যদি কেউ তাকে গালি দেয় বা ঝগড়া করতে আসে, সে যেন কেবল বলে—'আমি রোজাদার!'",
            repetitions = 2
        ),

        // ---------------------------------------------------------------------
        // 3. LAYLAT AL-QADR & RAMADAN SUPPLICATIONS (শবে কদর ও ক্ষমা)
        // ---------------------------------------------------------------------
        Dua(
            id = "dua_laylatul_qadr_chief",
            category = "Fasting",
            categoryBengali = "সাওম",
            waqtCategory = WaqtCategory.ISHA_NIGHT,
            hisnulPillar = HisnulPillar.FASTING_SAWM,
            titleBengali = "পবিত্র শবে কদরের সর্বশ্রেষ্ঠ দু'আ",
            titleEnglish = "Premier Supplication of Laylat al-Qadr (Night of Decree)",
            textArabic = "اللَّهُمَّ إِنَّكَ عَفُوٌّ تُحِبُّ الْعَفْوَ فَاعْفُ عَنِّي",
            transliteration = "Allahumma innaka 'Afuwwun tuhibbul-'afwa fa'fu 'annee.",
            transliterationBengali = "আল্লা-হুম্মা ইন্নাকা ‘আফুউবুন তুহিব্বুল ‘আফওয়া ফা‘ফু ‘আন্নী।",
            meaningBengali = "হে আল্লাহ! আপনি পরম ক্ষমাশীল, আপনি ক্ষমা করতেই ভালোবাসেন; অতএব আমাকে আপনি ক্ষমা করে দিন।",
            meaningEnglish = "O Allah, You are Most Forgiving, and You love to forgive, so forgive me.",
            reference = "সুনানে তিরমিযী: ৩৫১৩, সুনানে ইবনে মাজাহ: ৩৮৫০ (সহীহ)",
            virtuesBengali = "উম্মুল মুমিনীন আয়েশা (রাঃ) রাসুলুল্লাহ ﷺ-কে জিজ্ঞাসা করলেন: হে আল্লাহর রাসুল! আমি যদি বুঝতে পারি কোন রাতটি শবে কদর, তবে আমি কী বলব? নবীজি ﷺ তাকে এই হৃদয়স্পর্শী দু'আটি বেশি বেশি পাঠ করতে বললেন।",
            repetitions = 10
        ),
        Dua(
            id = "dua_sighting_crescent_moon",
            category = "Fasting",
            categoryBengali = "সাওম",
            waqtCategory = WaqtCategory.MAGHRIB_EVENING,
            hisnulPillar = HisnulPillar.FASTING_SAWM,
            titleBengali = "নতুন চাঁদ (রমাদান/ঈদ/মাসের চাঁদ) দেখার দু'আ",
            titleEnglish = "Upon Sighting the Crescent Moon (Hilal)",
            textArabic = "اللَّهُ أَكْبَرُ، اللَّهُمَّ أَهِلَّهُ عَلَيْنَا بِالْأَمْنِ وَالْإِيمَانِ، وَالسَّلَامَةِ وَالْإِسْلَامِ، وَالتَّوْفِيقِ لِمَا تُحِبُّ وَتَرْضَىٰ، رَبُّنَا وَرَبُّكَ اللَّهُ",
            transliteration = "Allahu Akbar, Allahumma ahillahu 'alayna bil-amni wal-eeman, was-salamati wal-Islam, wat-tawfeeqi lima tuhibbu wa tarda, Rabbuna wa Rabbukallah.",
            transliterationBengali = "আল্লা-হু আকবার, আল্লা-হুম্মা আহিল্লাহূ ‘আলাইনা- বিল আমনি ওয়াল ঈমা-ন, ওয়াস সালা-মাতি ওয়াল ইসলা-ম, ওয়াত তাওফীক্বি লিমা- তুহিব্বু ওয়া তারদ্বা-, রব্বুনা- ওয়া রব্বুকাল্লা-হ।",
            meaningBengali = "আল্লাহ সর্বশ্রেষ্ঠ। হে আল্লাহ! এই নতুন চাঁদকে আমাদের ওপর নিরাপত্তা ও ঈমান, শান্তি ও ইসলাম এবং আপনার পছন্দনীয় ও সন্তুষ্টির কাজের তাওফিক সহকারে উদিত করুন। (হে চাঁদ!) আমাদের এবং তোমার রব একমাত্র আল্লাহ।",
            meaningEnglish = "Allah is the Greatest. O Allah, bring it upon us with security and faith, with peace and Islam, and with success in doing what You love and are pleased with. Our Lord and your Lord is Allah.",
            reference = "সুনানে তিরমিযী: ৩৪৫১, সুনানে দারেমী: ১৬৯৭ (সহীহ)",
            virtuesBengali = "রমাদান, শাওয়াল বা যেকোনো হিজরি মাসের চাঁদ দেখে পাঠ করলে সমগ্র মাস বরকত ও সার্বিক নিরাপত্তায় অতিবাহিত হয়।",
            repetitions = 1
        )
    )
}
