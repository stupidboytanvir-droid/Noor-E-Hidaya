package com.example.data.model.dua

import com.example.data.model.Dua
import com.example.data.model.HisnulPillar
import com.example.data.model.WaqtCategory

/**
 * 🕌 Comprehensive Authentic Supplications for Salah, Wudu, and Mosque
 */
object SalahDuaData {

    val duas: List<Dua> = listOf(
        // ---------------------------------------------------------------------
        // 1. WUDU SUPPLICATIONS (ওযুর দু'আসমূহ)
        // ---------------------------------------------------------------------
        Dua(
            id = "dua_wudu_before",
            category = "Salat & Wudu",
            categoryBengali = "সালাত-নামাজ",
            waqtCategory = WaqtCategory.ALL,
            hisnulPillar = HisnulPillar.SALAH_PRAYER,
            titleBengali = "ওযু শুরুর প্রারম্ভিক দু'আ (বিসমিল্লাহ)",
            titleEnglish = "Before Performing Wudu (Ablution)",
            textArabic = "بِسْمِ اللَّهِ",
            transliteration = "Bismillah.",
            transliterationBengali = "বিসমিল্লা-হ।",
            meaningBengali = "আল্লাহর নামে শুরু করছি।",
            meaningEnglish = "In the Name of Allah.",
            reference = "আবু দাউদ: ১০১, তিরমিযী: ২৫, ইবনে মাজাহ: ৩৯৭ (সহীহ)",
            virtuesBengali = "রাসুলুল্লাহ ﷺ ইরশাদ করেছেন: যে ব্যক্তি ওযুর শুরুতে আল্লাহর নাম স্মরণ করে না, তার পূর্ণাঙ্গ ওযু হয় না।",
            repetitions = 1
        ),
        Dua(
            id = "dua_wudu_after_shahada",
            category = "Salat & Wudu",
            categoryBengali = "সালাত-নামাজ",
            waqtCategory = WaqtCategory.ALL,
            hisnulPillar = HisnulPillar.SALAH_PRAYER,
            titleBengali = "ওযু সমাপনান্তে কালিমা শাহাদাত",
            titleEnglish = "After Completing Wudu (Shahadah)",
            textArabic = "أَشْهَدُ أَنْ لَا إِلَـٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ",
            transliteration = "Ash-hadu alla ilaha illallahu wahdahu la shareeka lahu, wa ash-hadu anna Muhammadan 'abduhu wa Rasooluh.",
            transliterationBengali = "আশহাদু আল লা- ইলা-হা ইল্লাল্লা-হু ওয়াহদাহূ লা- শারীকা লাহূ, ওয়া আশহাদু আন্না মুহাম্মাদান ‘আবদুহূ ওয়া রাসূলুহ।",
            meaningBengali = "আমি সাক্ষ্য দিচ্ছি যে, আল্লাহ ব্যতীত কোনো সত্য উপাস্য নেই, তিনি একক, তাঁর কোনো শরিক নেই; এবং আমি সাক্ষ্য দিচ্ছি যে, মুহাম্মদ ﷺ তাঁর বান্দা ও রাসুল।",
            meaningEnglish = "I testify that none has the right to be worshipped except Allah alone, without partner; and I testify that Muhammad is His servant and His Messenger.",
            reference = "সহীহ মুসলিম: ২৩৪",
            virtuesBengali = "যে ব্যক্তি সুন্দরভাবে ওযু করে এই দু'আ পাঠ করবে, তার জন্য জান্নাতের আটটি দরজাই খুলে দেওয়া হবে; সে যে দরজা দিয়ে ইচ্ছা জান্নাতে প্রবেশ করতে পারবে।",
            repetitions = 1
        ),
        Dua(
            id = "dua_wudu_after_tawwabeen",
            category = "Salat & Wudu",
            categoryBengali = "সালাত-নামাজ",
            waqtCategory = WaqtCategory.ALL,
            hisnulPillar = HisnulPillar.SALAH_PRAYER,
            titleBengali = "ওযু শেষের তাওবা ও পবিত্রতার দু'আ",
            titleEnglish = "Seeking Purity & Repentance After Wudu",
            textArabic = "اللَّهُمَّ اجْعَلْنِي مِنَ التَّوَّابِينَ وَاجْعَلْنِي مِنَ الْمُتَطَهِّرِينَ",
            transliteration = "Allahummaj-'alnee minat-tawwabeena waj-'alnee minal-mutatahhireen.",
            transliterationBengali = "আল্লা-হুম্মাজ-‘আলনী মিনাত তাউওয়াবীনা ওয়াজ-‘আলনী মিনাল মুতাত্বহহিরীন।",
            meaningBengali = "হে আল্লাহ! আমাকে তওবাকারীদের অন্তর্ভুক্ত করুন এবং আমাকে পবিত্রতা অর্জনকারীদের অন্তর্ভুক্ত করুন।",
            meaningEnglish = "O Allah, make me of the repentant and make me of the purified.",
            reference = "সুনানে তিরমিযী: ৫৫ (সহীহ)",
            virtuesBengali = "আত্মিক ও শারীরিক উভয় প্রকার পবিত্রতা অর্জনের সর্বোত্তম সুন্নাত প্রার্থনা।",
            repetitions = 1
        ),

        // ---------------------------------------------------------------------
        // 2. MOSQUE SUPPLICATIONS (মসজিদের দু'আসমূহ)
        // ---------------------------------------------------------------------
        Dua(
            id = "dua_going_to_mosque",
            category = "Salat & Wudu",
            categoryBengali = "সালাত-নামাজ",
            waqtCategory = WaqtCategory.ALL,
            hisnulPillar = HisnulPillar.SALAH_PRAYER,
            titleBengali = "মসজিদে যাওয়ার সময় অন্তরে নূর লাভের দু'আ",
            titleEnglish = "Supplication When Going to the Mosque",
            textArabic = "اللَّهُمَّ اجْعَلْ فِي قَلْبِي نُورًا، وَفِي لِسَانِي نُورًا، وَفِي سَمْعِي نُورًا، وَفِي بَصَرِي نُورًا، وَمِنْ فَوْقِي نُورًا، وَمِنْ تَحْتِي نُورًا، وَعَنْ يَمِينِي نُورًا، وَعَنْ شِمَالِي نُورًا، وَمِنْ أَمَامِي نُورًا، وَمِنْ خَلْفِي نُورًا، وَاجْعَلْ فِي نَفْسِي نُورًا، وَأَعْظِمْ لِي نُورًا",
            transliteration = "Allahummaj-'al fee qalbee noora, wa fee lisanee noora, wa fee sam'ee noora, wa fee basaree noora, wa min fawqee noora, wa min tahtee noora, wa 'an yameenee noora, wa 'an shimalee noora, wa min amamee noora, wa min khalfee noora, waj-'al fee nafsee noora, wa a'zim lee noora.",
            transliterationBengali = "আল্লা-হুম্মাজ-‘আল ফী ক্বালবী নূরা-, ওয়া ফী লিসা-নী নূরা-, ওয়া ফী সাম‘ঈ নূরা-, ওয়া ফী বাসারী নূরা-, ওয়া মিন ফাওক্বী নূরা-, ওয়া মিন তাহতী নূরা-, ওয়া ‘আন ইয়ামীনি নূরা-, ওয়া ‘আন শিমা-লী নূরা-, ওয়া মিন আমা-মী নূরা-, ওয়া মিন খালফী নূরা-, ওয়াজ-‘আল ফী নাফসী নূরা-, ওয়া আ‘যিম লী নূরা-।",
            meaningBengali = "হে আল্লাহ! আমার অন্তরে নূর দিন, আমার জিহ্বায় নূর দিন, আমার কানে নূর দিন, আমার চোখে নূর দিন, আমার উপরে নূর দিন, আমার নিচে নূর দিন, আমার ডানে নূর দিন, আমার বামে নূর দিন, আমার সামনে নূর দিন, আমার পেছনে নূর দিন, আমার সত্তায় নূর দিন এবং আমার জন্য নূরকে মহান করে দিন।",
            meaningEnglish = "O Allah, place within my heart light, and upon my tongue light, and within my ears light, and within my eyes light, and above me light, and beneath me light, and on my right light, and on my left light, and before me light, and behind me light, and make within my soul light, and magnify for me light.",
            reference = "সহীহ মুসলিম: ৭৬৩, সহীহ বুখারী: ৬৩১৬",
            virtuesBengali = "ফজরের নামাজের জন্য মসজিদে যাওয়ার পথে রাসুলুল্লাহ ﷺ এ দু'আটি অত্যন্ত আবেগভরে পাঠ করতেন।",
            repetitions = 1
        ),
        Dua(
            id = "dua_entering_mosque",
            category = "Salat & Wudu",
            categoryBengali = "সালাত-নামাজ",
            waqtCategory = WaqtCategory.ALL,
            hisnulPillar = HisnulPillar.SALAH_PRAYER,
            titleBengali = "মসজিদে প্রবেশের দু'আ",
            titleEnglish = "Entering the Mosque",
            textArabic = "بِسْمِ اللَّهِ وَالصَّلَاةُ وَالسَّلَامُ عَلَىٰ رَسُولِ اللَّهِ، اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ",
            transliteration = "Bismillahi was-salatu was-salamu 'ala Rasoolillah, Allahummaf-tah lee abwaba rahmatik.",
            transliterationBengali = "বিসমিল্লা-হি ওয়াস সলা-তু ওয়াস সালা-মু ‘আলা- রাসূলিল্লা-হ, আল্লা-হুম্মাফতাহ্ লী আবওয়া-বা রহমাতিক।",
            meaningBengali = "আল্লাহর নামে প্রবেশ করছি এবং আল্লাহর রাসুলের ওপর দরূদ ও সালাম বর্ষিত হোক। হে আল্লাহ! আমার জন্য আপনার রহমতের সকল দরজা খুলে দিন।",
            meaningEnglish = "In the Name of Allah, and peace and blessings be upon the Messenger of Allah. O Allah, open for me the doors of Your mercy.",
            reference = "সহীহ মুসলিম: ৭১৩, সুনানে আবু দাউদ: ৪৬৫",
            virtuesBengali = "ডান পা দিয়ে মসজিদে প্রবেশকালে পাঠ করা সুন্নাত; এর মাধ্যমে বান্দা আল্লাহর রহমতের ছায়াতলে আশ্রয় নেয়।",
            repetitions = 1
        ),
        Dua(
            id = "dua_leaving_mosque",
            category = "Salat & Wudu",
            categoryBengali = "সালাত-নামাজ",
            waqtCategory = WaqtCategory.ALL,
            hisnulPillar = HisnulPillar.SALAH_PRAYER,
            titleBengali = "মসজিদ থেকে বের হওয়ার দু'আ",
            titleEnglish = "Leaving the Mosque",
            textArabic = "بِسْمِ اللَّهِ وَالصَّلَاةُ وَالسَّلَامُ عَلَىٰ رَسُولِ اللَّهِ، اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ فَضْلِكَ، اللَّهُمَّ اعْصِمْنِي مِنَ الشَّيْطَانِ الرَّجِيمِ",
            transliteration = "Bismillahi was-salatu was-salamu 'ala Rasoolillah, Allahumma innee as-aluka min fadlik, Allahum-ma'simnee minash-shaytanir-rajeem.",
            transliterationBengali = "বিসমিল্লা-হি ওয়াস সলা-তু ওয়াস সালা-মু ‘আলা- রাসূলিল্লা-হ, আল্লা-হুম্মা ইন্নী আস-আলুকা মিন ফাদ্বলিক, আল্লা-হুম্মা‘সিম্নী মিনাশ শায়ত্বা-নির রাজীম।",
            meaningBengali = "আল্লাহর নামে বের হচ্ছি এবং আল্লাহর রাসুলের ওপর দরূদ ও সালাম। হে আল্লাহ! আমি আপনার অনুগ্রহ প্রার্থনা করছি। হে আল্লাহ! আমাকে বিতাড়িত শয়তান থেকে রক্ষা করুন।",
            meaningEnglish = "In the Name of Allah, and peace and blessings be upon the Messenger of Allah. O Allah, I ask of You from Your bounty. O Allah, protect me from the accursed Satan.",
            reference = "সহীহ মুসলিম: ৭১৩, সুনানে ইবনে মাজাহ: ৭৭৩ (সহীহ)",
            virtuesBengali = "বাম পা দিয়ে মসজিদ থেকে বের হওয়ার সময় আল্লাহর অনুগ্রহ (রিযিক ও বরকত) ও শয়তানের প্ররোচনা থেকে সুরক্ষা লাভের আমল।",
            repetitions = 1
        ),

        // ---------------------------------------------------------------------
        // 3. PRAYER BEGINNING & SANA (তাকবীরে তাহরীমা ও ছানা)
        // ---------------------------------------------------------------------
        Dua(
            id = "dua_sana_subhanaka",
            category = "Salat & Wudu",
            categoryBengali = "সালাত-নামাজ",
            waqtCategory = WaqtCategory.ALL,
            hisnulPillar = HisnulPillar.SALAH_PRAYER,
            titleBengali = "ছানা (নামাজের প্রারম্ভিক প্রশংসা)",
            titleEnglish = "Opening Supplication (Sanaa)",
            textArabic = "سُبْحَانَكَ اللَّهُمَّ وَبِحَمْدِكَ، وَتَبَارَكَ اسْمُكَ، وَتَعَالَىٰ جَدُّكَ، وَلَا إِلَـٰهَ غَيْرُكَ",
            transliteration = "Subhanakallahumma wa bihamdika, wa tabarakasmuka, wa ta'ala jadduka, wa la ilaha ghayruk.",
            transliterationBengali = "সুবহা-নাকাল্লা-হুম্মা ওয়া বিহামদিকা, ওয়া তাবা-রকাসমুকা, ওয়া তা‘আ-লা- জাদ্দুকা, ওয়ালা- ইলা-হা গয়রুক।",
            meaningBengali = "হে আল্লাহ! আপনার প্রশংসার সাথে আপনার পবিত্রতা ঘোষণা করছি, আপনার নাম বরকতময়, আপনার মর্যাদা সমুন্নত এবং আপনি ব্যতীত কোনো সত্য উপাস্য নেই।",
            meaningEnglish = "Glory is to You, O Allah, and all praise is for You; blessed is Your Name and exalted is Your Majesty, and none has the right to be worshipped except You.",
            reference = "সুনানে আবু দাউদ: ৭৭৬, সুনানে তিরমিযী: ২৪৩ (সহীহ)",
            virtuesBengali = "তাকবীরে তাহরীমার পর সূরা ফাতিহা পাঠের পূর্বে পাঠ করা অত্যন্ত মর্যাদাপূর্ণ সুন্নাত।",
            repetitions = 1
        ),
        Dua(
            id = "dua_sana_wajjahtu",
            category = "Salat & Wudu",
            categoryBengali = "সালাত-নামাজ",
            waqtCategory = WaqtCategory.ISHA_NIGHT,
            hisnulPillar = HisnulPillar.SALAH_PRAYER,
            titleBengali = "তাহাজ্জুদ ও সালাতের দীর্ঘ ছানা (ওয়াজ্জাহতু)",
            titleEnglish = "Opening Supplication in Night Prayer",
            textArabic = "وَجَّهْتُ وَجْهِيَ لِلَّذِي فَطَرَ السَّمَاوَاتِ وَالْأَرْضَ حَنِيفًا وَمَا أَنَا مِنَ الْمُشْرِكِينَ، إِنَّ صَلَاتِي وَنُسُكِي وَمَحْيَايَ وَمَمَاتِي لِلَّهِ رَبِّ الْعَالَمِينَ، لَا شَرِيكَ لَهُ وَبِذَٰلِكَ أُمِرْتُ وَأَنَا مِنَ الْمُسْلِمِينَ",
            transliteration = "Wajjahtu wajhiya lilladhee fataras-samawati wal-arda haneefan wa ma ana minal-mushrikeen. Inna salatee wa nusukee wa mahyaya wa mamatee lillahi Rabbil-'alameen, la shareeka lahu wa bi-dhalika umirtu wa ana minal-muslimeen.",
            transliterationBengali = "ওয়াজ্জাহতু ওয়াজহিয়া লিল্লাযী ফাত্বারাস সামা-ওয়া-তি ওয়াল আরদ্বা হানীফাঁও ওয়ামা- আনা মিনাল মুশরিকীন। ইন্না সলা-তী ওয়া নুসুকী ওয়া মাহ্ইয়ায়া ওয়া মামা-তী লিল্লা-হি রব্বিল ‘আ-লামীন, লা- শারীকা লাহূ ওয়া বিযা-লিকা উমিরতু ওয়া আনা মিনাল মুসলিমীন।",
            meaningBengali = "আমি একনিষ্ঠভাবে তাঁরই দিকে আমার মুখ ফেরালাম, যিনি আসমান ও জমিন সৃষ্টি করেছেন; এবং আমি অংশীবাদীদের অন্তর্ভুক্ত নই। নিশ্চয়ই আমার সালাত, আমার কুরবানী, আমার জীবন ও আমার মৃত্যু সারা জাহানের প্রতিপালক আল্লাহরই জন্য। তাঁর কোনো শরিক নেই, আমি এ বিষয়েই আদিষ্ট হয়েছি এবং আমি আত্মসমর্পণকারীদের অন্তর্ভুক্ত।",
            meaningEnglish = "I have turned my face towards Him Who created the heavens and the earth, as a true believer, and I am not of the polytheists. Indeed, my prayer, my sacrifice, my living and my dying are for Allah, the Lord of all creation; He has no partner, and of this I have been commanded, and I am of the Muslims.",
            reference = "সহীহ মুসলিম: ৭৭১",
            virtuesBengali = "হযরত আলী (রাঃ) বর্ণিত; রাসুলুল্লাহ ﷺ রাতে তাহাজ্জুদ ও সালাতে দাঁড়িয়ে এই হৃদয়স্পর্শী তাওহীদের সাক্ষ্য পাঠ করতেন।",
            repetitions = 1
        ),

        // ---------------------------------------------------------------------
        // 4. RUKU SUPPLICATIONS (রুকুর তাসবীহ)
        // ---------------------------------------------------------------------
        Dua(
            id = "dua_ruku_subhana_rabbiyal_azeem",
            category = "Salat & Wudu",
            categoryBengali = "সালাত-নামাজ",
            waqtCategory = WaqtCategory.ALL,
            hisnulPillar = HisnulPillar.SALAH_PRAYER,
            titleBengali = "রুকুর প্রধান তাসবীহ (৩ বার)",
            titleEnglish = "Supplication in Ruku (Bowing)",
            textArabic = "سُبْحَانَ رَبِّيَ الْعَظِيمِ",
            transliteration = "Subhana Rabbiyal-'Azeem.",
            transliterationBengali = "সুবহা-না রব্বিয়াল ‘আযীম।",
            meaningBengali = "আমার মহান প্রতিপালকের পবিত্রতা ও মহিমা ঘোষণা করছি।",
            meaningEnglish = "Glory is to my Lord, the Most Magnificent.",
            reference = "সহীহ মুসলিম: ৭৭২, সুনানে আবু দাউদ: ৮৭০",
            virtuesBengali = "রুকুতে কমপক্ষে ৩ বার ধীরস্থিরভাবে পাঠ করা সুন্নাতে মুয়াক্কাদাহ।",
            repetitions = 3
        ),
        Dua(
            id = "dua_ruku_subbuhun_quddus",
            category = "Salat & Wudu",
            categoryBengali = "সালাত-নামাজ",
            waqtCategory = WaqtCategory.ALL,
            hisnulPillar = HisnulPillar.SALAH_PRAYER,
            titleBengali = "রুকু ও সেজদার ফেরেশতাদের তাসবীহ",
            titleEnglish = "Tasbih of Angels in Ruku & Sujud",
            textArabic = "سُبُّوحٌ قُدُّوسٌ، رَبُّ الْمَلَائِكَةِ وَالرُّوحِ",
            transliteration = "Subboohun Quddoos, Rabbul-mala'ikati war-Rooh.",
            transliterationBengali = "সুব্বূহুন ক্বুদ্দূস, রব্বুল মালা-ইকাতি ওয়ার রূহ।",
            meaningBengali = "তিনি অত্যন্ত পবিত্র ও মহিমান্বিত; ফেরেশতাকুল ও জিব্রাইল (আঃ) এর প্রতিপালক।",
            meaningEnglish = "Perfect and Holy He is, Lord of the angels and the Spirit (Gabriel).",
            reference = "সহীহ মুসলিম: ৪৮৭",
            virtuesBengali = "মা আয়েশা (রাঃ) বর্ণনা করেন, রাসুলুল্লাহ ﷺ রুকু ও সেজদায় প্রায়শই এ বাক্যটি পাঠ করতেন।",
            repetitions = 1
        ),
        Dua(
            id = "dua_rising_ruku_tahmeed",
            category = "Salat & Wudu",
            categoryBengali = "সালাত-নামাজ",
            waqtCategory = WaqtCategory.ALL,
            hisnulPillar = HisnulPillar.SALAH_PRAYER,
            titleBengali = "রুকু থেকে ওঠার পর তাহমীদ (বিশাল সওয়াব)",
            titleEnglish = "Rising from Ruku (Rabbana Lakal Hamd)",
            textArabic = "رَبَّنَا وَلَكَ الْحَمْدُ، حَمْدًا كَثِيرًا طَيِّبًا مُبَارَكًا فِيهِ",
            transliteration = "Rabbana wa lakal-hamd, hamdan katheeran tayyiban mubarakan feeh.",
            transliterationBengali = "রব্বানা- ওয়া লাকাল হামদ, হামদান কাছীরন ত্বাইয়্যিবাম মুবা-রকান ফীহ।",
            meaningBengali = "হে আমাদের রব! সমস্ত প্রশংসা আপনারই; এমন প্রশংসা যা অশেষ, পবিত্র এবং বরকতময়।",
            meaningEnglish = "Our Lord, and to You belongs all praise; abundant, beautiful and blessed praise.",
            reference = "সহীহ বুখারী: ৭৯৯",
            virtuesBengali = "রাসুলুল্লাহ ﷺ ইরশাদ করেছেন: আমি দেখলাম ত্রিশজনেরও বেশি ফেরেশতা প্রতিযোগিতা করছেন কে এই বাক্যের সওয়াব প্রথমে লিপিবদ্ধ করবেন!",
            repetitions = 1
        ),

        // ---------------------------------------------------------------------
        // 5. SUJUD SUPPLICATIONS (সেজদার দু'আসমূহ)
        // ---------------------------------------------------------------------
        Dua(
            id = "dua_sujud_subhana_rabbiyal_aala",
            category = "Salat & Wudu",
            categoryBengali = "সালাত-নামাজ",
            waqtCategory = WaqtCategory.ALL,
            hisnulPillar = HisnulPillar.SALAH_PRAYER,
            titleBengali = "সেজদার প্রধান তাসবীহ (৩ বার)",
            titleEnglish = "Supplication in Sujud (Prostration)",
            textArabic = "سُبْحَانَ رَبِّيَ الْأَعْلَىٰ",
            transliteration = "Subhana Rabbiyal-A'la.",
            transliterationBengali = "সুবহা-না রব্বিয়াল আ‘লা-।",
            meaningBengali = "আমার সর্বোন্নত ও সর্বোচ্চ প্রতিপালকের পবিত্রতা ঘোষণা করছি।",
            meaningEnglish = "Glory is to my Lord, the Most High.",
            reference = "সহীহ মুসলিম: ৭৭২, সুনানে আবু দাউদ: ৮৭০",
            virtuesBengali = "বান্দা সেজদারত অবস্থায় তার রবের সবচেয়ে নিকটবর্তী হয়; তাই সেজদায় এ তাসবীহের পাশাপাশি বেশি বেশি দু'আ করা সুন্নাত।",
            repetitions = 3
        ),
        Dua(
            id = "dua_sujud_all_sins_forgiveness",
            category = "Salat & Wudu",
            categoryBengali = "সালাত-নামাজ",
            waqtCategory = WaqtCategory.ALL,
            hisnulPillar = HisnulPillar.SALAH_PRAYER,
            titleBengali = "সেজদায় সকল ছোট-বড় গুনাহ মাফের দু'আ",
            titleEnglish = "Seeking Forgiveness of All Sins in Sujud",
            textArabic = "اللَّهُمَّ اغْفِرْ لِي ذَنْبِي كُلَّهُ، دِقَّهُ وَجِلَّهُ، وَأَوَّلَهُ وَآخِرَهُ، وَعَلَانِيَتَهُ وَسِرَّهُ",
            transliteration = "Allahummagh-fir lee dhanbee kullahu, diqqahu wa jillahu, wa awwalahu wa akhirahu, wa 'alaniyatahu wa sirrahu.",
            transliterationBengali = "আল্লা-হুম্মাগফির লী যানবী কুল্লাহূ, দিক্ক্বাহূ ওয়া জিল্লাহূ, ওয়া আউওয়ালাহূ ওয়া আ-খিরাহূ, ওয়া ‘আলা-নিয়্যাতাহূ ওয়া সিররাহূ।",
            meaningBengali = "হে আল্লাহ! আমার সমস্ত গুনাহ ক্ষমা করে দিন—সূক্ষ্ম ও সুস্পষ্ট, পূর্বের ও পরের, প্রকাশ্য ও অপ্রকাশ্য সকল পাপ।",
            meaningEnglish = "O Allah, forgive me all my sins, small and great, first and last, open and secret.",
            reference = "সহীহ মুসলিম: ৪৮৩",
            virtuesBengali = "রাসুলুল্লাহ ﷺ সেজদায় অত্যন্ত বিনম্র কণ্ঠে এই সার্বিক ক্ষমার দু'আ পাঠ করতেন।",
            repetitions = 1
        ),
        Dua(
            id = "dua_between_sujuds_jalsah",
            category = "Salat & Wudu",
            categoryBengali = "সালাত-নামাজ",
            waqtCategory = WaqtCategory.ALL,
            hisnulPillar = HisnulPillar.SALAH_PRAYER,
            titleBengali = "দুই সেজদার মধ্যবর্তী বৈঠকের দু'আ",
            titleEnglish = "Sitting Between Two Prostrations (Jalsah)",
            textArabic = "اللَّهُمَّ اغْفِرْ لِي، وَارْحَمْنِي، وَاهْدِنِي، وَعَافِنِي، وَارْزُقْنِي",
            transliteration = "Allahummagh-fir lee, warhamnee, wahdinee, wa 'afinee, warzuqnee.",
            transliterationBengali = "আল্লা-হুম্মাগফির লী, ওয়ারহামনী, ওয়াহদিনী, ওয়া ‘আ-ফিনী, ওয়ারযুক্বনী।",
            meaningBengali = "হে আল্লাহ! আমাকে ক্ষমা করুন, আমার প্রতি দয়া করুন, আমাকে সঠিক পথ দেখান, আমাকে নিরাপত্তা ও সুস্থতা দান করুন এবং আমাকে জীবিকা দান করুন।",
            meaningEnglish = "O Allah, forgive me, have mercy on me, guide me, grant me well-being and provide for me.",
            reference = "সুনানে আবু দাউদ: ৮৫০, সুনানে তিরমিযী: ২৮৪ (সহীহ)",
            virtuesBengali = "সালাতে দুই সেজদার মাঝে শান্ত হয়ে বসে এ দু'আটি পাঠ করা মোস্তাহাব ও অতি বরকতময়।",
            repetitions = 1
        ),

        // ---------------------------------------------------------------------
        // 6. TASHAHHUD, DUROOD & SITTING (তাশাহহুদ, দরূদ ও দোয়া মাসুরা)
        // ---------------------------------------------------------------------
        Dua(
            id = "dua_tashahhud_attahiyyat",
            category = "Salat & Wudu",
            categoryBengali = "সালাত-নামাজ",
            waqtCategory = WaqtCategory.AFTER_SALAT,
            hisnulPillar = HisnulPillar.SALAH_PRAYER,
            titleBengali = "তাশাহহুদ (আত্তাহিয়্যাতু)",
            titleEnglish = "Tashahhud (Sitting in Prayer)",
            textArabic = "التَّحِيَّاتُ لِلَّهِ وَالصَّلَوَاتُ وَالطَّيِّبَاتُ، السَّلَامُ عَلَيْكَ أَيُّهَا النَّبِيُّ وَرَحْمَةُ اللَّهِ وَبَرَكَاتُهُ، السَّلَامُ عَلَيْنَا وَعَلَىٰ عِبَادِ اللَّهِ الصَّالِحِينَ، أَشْهَدُ أَنْ لَا إِلَـٰهَ إِلَّا اللَّهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ",
            transliteration = "At-tahiyyatu lillahi was-salawatu wat-tayyibatu, as-salamu 'alayka ayyuhan-Nabiyyu wa rahmatullahi wa barakatuh, as-salamu 'alayna wa 'ala 'ibadillahis-saliheen. Ash-hadu alla ilaha illallahu wa ash-hadu anna Muhammadan 'abduhu wa Rasooluh.",
            transliterationBengali = "আত্তাহিয়্যা-তু লিল্লা-হি ওয়াস সলাওয়া-তু ওয়াত ত্বাইয়্যিবা-তু, আসসালা-মু ‘আলাইকা আইয়্যুহান নাবিয়্যূ ওয়া রহমাতুল্লা-হি ওয়া বারাকা-তুহ, আসসালা-মু ‘আলাইনা- ওয়া ‘আলা- ‘ইবা-দিল্লা-হিস স-লিহীন। আশহাদু আল্লা- ইলা-হা ইল্লাল্লা-হু ওয়া আশহাদু আন্না মুহাম্মাদান ‘আবদুহূ ওয়া রাসূলুহ।",
            meaningBengali = "যাবতীয় মৌখিক, শারীরিক ও আর্থিক ইবাদত আল্লাহর জন্য। হে নবী! আপনার ওপর শান্তি, আল্লাহর রহমত ও বরকত বর্ষিত হোক। আমাদের ওপর এবং আল্লাহর নেককার বান্দাদের ওপরও শান্তি বর্ষিত হোক। আমি সাক্ষ্য দিচ্ছি আল্লাহ ছাড়া কোনো সত্য উপাস্য নেই এবং মুহাম্মদ ﷺ আল্লাহর বান্দা ও রাসুল।",
            meaningEnglish = "All compliments, prayers and pure words are due to Allah. Peace be upon you, O Prophet, and the mercy of Allah and His blessings. Peace be upon us and on the righteous servants of Allah. I bear witness that none has the right to be worshipped except Allah, and I bear witness that Muhammad is His servant and messenger.",
            reference = "সহীহ বুখারী: ৮৩১, সহীহ মুসলিম: ৪০২",
            virtuesBengali = "সালাতের প্রথম ও শেষ বৈঠকে বসার সময় ফরজ ওয়াজিব হিসেবে পঠিত সর্বশ্রেষ্ঠ তাওহীদী তাসবীহ।",
            repetitions = 1
        ),
        Dua(
            id = "dua_durood_ibrahim",
            category = "Salat & Wudu",
            categoryBengali = "সালাত-নামাজ",
            waqtCategory = WaqtCategory.AFTER_SALAT,
            hisnulPillar = HisnulPillar.SALAH_PRAYER,
            titleBengali = "দরূদে ইবরাহীম (সালাতের দরূদ)",
            titleEnglish = "Durood Ibrahim (Salat)",
            textArabic = "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَىٰ إِبْرَاهِيمَ وَعَلَىٰ آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ، اللَّهُمَّ بَارِكْ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ كَمَا بَارَكْتَ عَلَىٰ إِبْرَاهِيمَ وَعَلَىٰ آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ",
            transliteration = "Allahumma salli 'ala Muhammadin wa 'ala ali Muhammadin kama sallayta 'ala Ibrahima wa 'ala ali Ibrahima innaka Hameedum Majeed. Allahumma barik 'ala Muhammadin wa 'ala ali Muhammadin kama barakta 'ala Ibrahima wa 'ala ali Ibrahima innaka Hameedum Majeed.",
            transliterationBengali = "আল্লা-হুম্মা সল্লি ‘আলা- মুহাম্মাদিঁও ওয়া ‘আলা- আ-লি মুহাম্মাদ, কামা- সল্লাইতা ‘আলা- ইবরা-হীমা ওয়া ‘আলা- আ-লি ইবরা-হীম, ইন্নাকা হামীদুম মাজীদ। আল্লা-হুম্মা বা-রিক ‘আলা- মুহাম্মাদিঁও ওয়া ‘আলা- আ-লি মুহাম্মাদ, কামা- বা-রকতা ‘আলা- ইবরা-হীমা ওয়া ‘আলা- আ-লি ইবরা-হীম, ইন্নাকা হামীদুম মাজীদ।",
            meaningBengali = "হে আল্লাহ! মুহাম্মদ ﷺ ও তাঁর পরিবারের ওপর রহমত বর্ষণ করুন, যেভাবে আপনি ইবরাহীম (আঃ) ও তাঁর পরিবারের ওপর রহমত বর্ষণ করেছিলেন। নিশ্চয়ই আপনি প্রশংসিত ও মহিমান্বিত। হে আল্লাহ! মুহাম্মদ ﷺ ও তাঁর পরিবারের ওপর বরকত দান করুন, যেভাবে আপনি ইবরাহীম (আঃ) ও তাঁর পরিবারের ওপর বরকত দান করেছিলেন। নিশ্চয়ই আপনি প্রশংসিত ও মহিমান্বিত।",
            meaningEnglish = "O Allah, bestow Your favor on Muhammad and on the family of Muhammad as You have bestowed Your favor on Ibrahim and on the family of Ibrahim, surely You are Praiseworthy, Most Glorious.",
            reference = "সহীহ বুখারী: ৩৩৭০, সহীহ মুসলিম: ৪০৫",
            virtuesBengali = "সর্বোত্তম ও সর্বশ্রেষ্ঠ দরূদ। এক বার পাঠ করলে আল্লাহ দশটি রহমত বর্ষণ করেন ও দশটি মর্যাদা বৃদ্ধি করেন।",
            repetitions = 1
        ),
        Dua(
            id = "dua_masura_innee_zalamtu",
            category = "Salat & Wudu",
            categoryBengali = "সালাত-নামাজ",
            waqtCategory = WaqtCategory.AFTER_SALAT,
            hisnulPillar = HisnulPillar.SALAH_PRAYER,
            titleBengali = "দোয়ায়ে মাসুরা (সালামের পূর্বের দু'আ)",
            titleEnglish = "Dua Masura (Before Salam in Prayer)",
            textArabic = "اللَّهُمَّ إِنِّي ظَلَمْتُ نَفْسِي ظُلْمًا كَثِيرًا، وَلَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ، فَاغْفِرْ لِي مَغْفِرَةً مِنْ عِنْدِكَ وَارْحَمْنِي إِنَّكَ أَنْتَ الْغَفُورُ الرَّحِيمُ",
            transliteration = "Allahumma innee zalamtu nafsee zulman katheeran, wa la yaghfirudh-dhunooba illa Anta, faghfir lee maghfiratan min 'indika warhamnee innaka Antal-Ghafoorur-Raheem.",
            transliterationBengali = "আল্লা-হুম্মা ইন্নী যলামতু নাফসী যুলমান কাছীরঁও ওয়ালা- ইয়্যাগফিরুয যুনূবা ইল্লা- আনতা, ফাগফির লী মাগফিরাতাম মিন ‘ইনদিকা ওয়ারহামনী, ইন্নাকা আনতাল গফূরুর রহীম।",
            meaningBengali = "হে আল্লাহ! আমি আমার নিজের ওপর অনেক জুলুম করেছি; আর আপনি ব্যতীত গুনাহ ক্ষমা করার কেউ নেই। অতএব আপনার পক্ষ থেকে আমাকে বিশেষ ক্ষমা দান করুন এবং আমার প্রতি দয়া করুন; নিশ্চয়ই আপনি পরম ক্ষমাশীল ও অসীম দয়ালু।",
            meaningEnglish = "O Allah, I have wronged myself greatly and none forgives sins except You, so grant me forgiveness from You and have mercy on me, indeed You are the Most Forgiving, Most Merciful.",
            reference = "সহীহ বুখারী: ৮৩৪, সহীহ মুসলিম: ২৭০৫",
            virtuesBengali = "হযরত আবু বকর সিদ্দিক (রাঃ)-কে রাসুলুল্লাহ ﷺ নামাজের শেষ বৈঠকে সালাম ফেরানোর পূর্বে পাঠ করার জন্য শিক্ষা দিয়েছিলেন।",
            repetitions = 1
        ),
        // ---------------------------------------------------------------------
        // 7. BEFORE SALAM & AFTER SALAM (সালাম পূর্ব ও পরবর্তী যিকির)
        // ---------------------------------------------------------------------
        Dua(
            id = "dua_before_salam_four_trials",
            category = "Salat & Wudu",
            categoryBengali = "সালাত-নামাজ",
            waqtCategory = WaqtCategory.AFTER_SALAT,
            hisnulPillar = HisnulPillar.SALAH_PRAYER,
            titleBengali = "সালাম ফেরানোর পূর্বে ৪টি ভয়াবহ ফিতনা থেকে আশ্রয়",
            titleEnglish = "Protection from 4 Great Afflictions Before Salam",
            textArabic = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنْ عَذَابِ جَهَنَّمَ، وَمِنْ عَذَابِ الْقَبْرِ، وَمِنْ فِتْنَةِ الْمَحْيَا وَالْمَمَاتِ، وَمِنْ شَرِّ فِتْنَةِ الْمَسِيحِ الدَّجَّالِ",
            transliteration = "Allahumma innee a'oodhu bika min 'adhabi jahannam, wa min 'adhabil-qabr, wa min fitnatil-mahya wal-mamat, wa min sharri fitnatil-maseehid-dajjal.",
            transliterationBengali = "আল্লা-হুম্মা ইন্নী আ‘ঊযু বিকা মিন ‘আযা-বি জাহান্নাম, ওয়া মিন ‘আযা-বিল ক্বাবর, ওয়া মিন ফিতনাতিল মাহ্ইয়া- ওয়াল মামা-ত, ওয়া মিন শাররি ফিতনাতিল মাসীহিদ্দাজ্জা-ল।",
            meaningBengali = "হে আল্লাহ! আমি আপনার কাছে আশ্রয় চাই জাহান্নামের আযাব থেকে, কবরের আযাব থেকে, জীবন ও মৃত্যুর ফিতনা থেকে এবং দাজ্জালের ভয়াবহ অনিষ্ট ও ফিতনা থেকে।",
            meaningEnglish = "O Allah, I seek refuge in You from the torment of Hellfire, and from the torment of the grave, and from the trials of living and dying, and from the evil trial of the False Messiah (Dajjal).",
            reference = "সহীহ মুসলিম: ৫৮৮, সহীহ বুখারী: ১৩৭৭",
            virtuesBengali = "রাসুলুল্লাহ ﷺ তাশাহহুদ ও দরূদ পাঠের পর সালাম ফেরানোর আগে এ দু'আ পাঠ করার অত্যন্ত জোরালো নির্দেশ দিয়েছেন।",
            repetitions = 1
        ),
        Dua(
            id = "dua_after_salam_istighfar_salam",
            category = "Salat & Wudu",
            categoryBengali = "সালাতের পর",
            waqtCategory = WaqtCategory.AFTER_SALAT,
            hisnulPillar = HisnulPillar.SALAH_PRAYER,
            titleBengali = "সালাম ফেরানোর সাথে সাথে ইস্তিগফার ও শান্তির দু'আ",
            titleEnglish = "Supplication Immediately After Salam",
            textArabic = "أَسْتَغْفِرُ اللَّهَ (ثَلَاثًا)، اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ، تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
            transliteration = "Astaghfirullah (3 times), Allahumma Antas-Salamu wa minkas-salam, tabarakta ya Dhal-Jalali wal-Ikram.",
            transliterationBengali = "আস্তাগফিরুল্লা-হ (৩ বার), আল্লা-হুম্মা আনতাস সালা-মু ওয়া মিনকাস সালা-ম, তাবা-রকতা ইয়া- যাল জালা-লি ওয়াল ইকরাম।",
            meaningBengali = "আমি আল্লাহর নিকট ক্ষমা প্রার্থনা করছি (৩ বার)। হে আল্লাহ! আপনিই শান্তি এবং আপনার নিকট হতেই শান্তি আসে। আপনি বরকতময়, হে মহিমান্বিত ও মর্যাদার অধিকারী সত্তা!",
            meaningEnglish = "I seek the forgiveness of Allah (three times). O Allah, You are Peace and from You comes peace. Blessed are You, O Owner of Majesty and Honor.",
            reference = "সহীহ মুসলিম: ৫৯১",
            virtuesBengali = "রাসুলুল্লাহ ﷺ প্রত্যেক ফরজ সালাতের সালাম ফেরানোর সাথে সাথেই এ বাক্যগুলো উচ্চারণ করতেন।",
            repetitions = 1
        ),
        Dua(
            id = "dua_after_salam_la_ilaha_illallah",
            category = "Salat & Wudu",
            categoryBengali = "সালাতের পর",
            waqtCategory = WaqtCategory.AFTER_SALAT,
            hisnulPillar = HisnulPillar.SALAH_PRAYER,
            titleBengali = "ফরজ সালাত পরবর্তী সার্বভৌমত্বের তাওহীদ",
            titleEnglish = "Tawheed & Sovereignty After Obligatory Prayer",
            textArabic = "لَا إِلَـٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ، اللَّهُمَّ لَا مَانِعَ لِمَا أَعْطَيْتَ، وَلَا مُعْطِيَ لِمَا مَنَعْتَ، وَلَا يَنْفَعُ ذَا الْجَدِّ مِنْكَ الْجَدُّ",
            transliteration = "La ilaha illallahu wahdahu la shareeka lahu, lahul-mulku wa lahul-hamdu wa Huwa 'ala kulli shay'in Qadeer. Allahumma la mani'a lima a'tayta, wa la mu'tiya lima mana'ta, wa la yanfa'u dhal-jaddi minkal-jadd.",
            transliterationBengali = "লা- ইলা-হা ইল্লাল্লা-হু ওয়াহদাহূ লা- শারীকা লাহূ, লাহুল মুলকু ওয়া লাহুল হামদু ওয়াহুওয়া ‘আলা- কুল্লি শাইয়িন ক্বদীর। আল্লা-হুম্মা লা- মা-নি‘আ লিমা- আ‘ত্বায়তা, ওয়ালা- মু‘ত্বিয়া লিমা- মানা‘তা, ওয়ালা- ইয়ানফা‘উ যাল জাদ্দি মিনকাল জাদ্দ।",
            meaningBengali = "আল্লাহ ছাড়া কোনো সত্য উপাস্য নেই, তিনি এক, তাঁর কোনো অংশীদার নেই। রাজত্ব একমাত্র তাঁরই, সমস্ত প্রশংসাও তাঁরই এবং তিনি সবকিছুর ওপর সর্বশক্তিমান। হে আল্লাহ! আপনি যা দান করেন তা রোধ করার কেউ নেই, আর আপনি যা রোধ করেন তা দান করার কেউ নেই; আর কোনো ধনী ব্যক্তির ধন-সম্পদ আপনার নিকট কোনো উপকারে আসবে না।",
            meaningEnglish = "None has the right to be worshipped except Allah alone, without partner. To Him belongs all sovereignty and praise and He is over all things omnipotent. O Allah, none can withhold what You give, and none can give what You withhold, and no fortune can benefit its possessor against You.",
            reference = "সহীহ বুখারী: ৮৪৪, সহীহ মুসলিম: ৫৯৩",
            virtuesBengali = "মুগীরাহ ইবনে শু‘বাহ (রাঃ) বলেন, রাসুলুল্লাহ ﷺ প্রত্যেক সালাতের পর এ দু'আটি নিয়মিত পাঠ করতেন।",
            repetitions = 1
        ),
        Dua(
            id = "dua_ayatul_kursi_after_salat",
            category = "Salat & Wudu",
            categoryBengali = "সালাতের পর",
            waqtCategory = WaqtCategory.AFTER_SALAT,
            hisnulPillar = HisnulPillar.SALAH_PRAYER,
            titleBengali = "আয়াতুল কুরসী (ফরজ নামাজের পর জান্নাতের চাবি)",
            titleEnglish = "Ayatul Kursi (After Every Obligatory Prayer)",
            textArabic = "اللَّهُ لَا إِلَـٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
            transliteration = "Allahu la ilaha illa Huwal-Hayyul-Qayyum, la ta'khudhuhu sinatun wa la nawm, lahu ma fis-samawati wa ma fil-ard, man dhal-ladhee yashfa'u 'indahu illa bi-idhnihi, ya'lamu ma bayna aydeehim wa ma khalfahum, wa la yuheetoona bi-shay'im-min 'ilmihi illa bima sha'a, wasi'a kursiyyuhus-samawati wal-arda, wa la ya'ooduhu hifzuhuma, wa Huwal-'Aliyyul-'Azeem.",
            transliterationBengali = "আল্লা-হু লা- ইলা-হা ইল্লা- হুওয়াল হাইয়্যুল ক্বাইয়্যূম, লা- তা’খুযুহূ সিনাতুঁও ওয়ালা- নাওম, লাহূ মা- ফিস সামা-ওয়া-তি ওয়ামা- ফিল আরদ্ব, মান যাল্লাযী ইয়্যাশফা‘উ ‘ইনদাহূ ইল্লা- বিইযনিহ, ইয়া‘লামু মা-バイনা আইদীহিম ওয়ামা- খলফাহুম, ওয়ালা- ইউহীতূনা বিশাইয়িম মিন ‘ইলমিহী ইল্লা- বিমা- শা-আ, ওয়াসি‘আ কুরসিইয়্যুহুস সামা-ওয়া-তি ওয়াল আরদ্ব, ওয়ালা- ইয়াউওদুহূ হিফযুহুমা-, ওয়াহুওয়াল ‘আলিয়্যুল ‘আযীম।",
            meaningBengali = "আল্লাহ, তিনি ছাড়া কোনো সত্য উপাস্য নেই, তিনি চিরঞ্জীব, সর্বসত্তার ধারক। তাঁকে তন্দ্রা ও নিদ্রা স্পর্শ করে না। আসমান ও জমিনে যা কিছু আছে সব তাঁরই। কে সে, যে তাঁর অনুমতি ছাড়া তাঁর নিকট সুপারিশ করবে? তাদের সামনে ও পেছনে যা কিছু আছে তা তিনি জানেন। তাঁর জ্ঞানের কোনো কিছু তারা বেষ্টন করতে পারে না, তবে তিনি যা ইচ্ছা করেন তা ছাড়া। তাঁর কুরসী আসমান ও জমিন পরিব্যাপ্ত করে আছে এবং এ দুয়ের রক্ষণাবেক্ষণ তাঁকে ক্লান্ত করে না; তিনি সর্বোচ্চ, সর্বশ্রেষ্ঠ।",
            meaningEnglish = "Allah - there is no deity except Him, the Ever-Living, the Sustainer of all existence. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and whatever is on the earth. Who is it that can intercede with Him except by His permission? He knows what is before them and what will be after them, and they encompass not a thing of His knowledge except for what He wills. His Kursi extends over the heavens and the earth, and their preservation tires Him not. And He is the Most High, the Most Great.",
            reference = "সূরা আল-বাক্বারা: ২৫৫, সুনানে নাসাঈ কুবরা: ৯৯২৮ (সহীহ)",
            virtuesBengali = "রাসুলুল্লাহ ﷺ বলেছেন: যে ব্যক্তি প্রত্যেক ফরজ সালাতের পর আয়াতুল কুরসী পাঠ করবে, তার জান্নাতে প্রবেশের পথে মৃত্যু ছাড়া আর কোনো অন্তরায় থাকে না।",
            repetitions = 1
        ),

        // ---------------------------------------------------------------------
        // 7. SPECIAL PRAYERS (বিতর কুনুত, ইস্তিখারা ও তাহাজ্জুদ)
        // ---------------------------------------------------------------------
        Dua(
            id = "dua_qunut_witr",
            category = "Salat & Wudu",
            categoryBengali = "সালাত-নামাজ",
            waqtCategory = WaqtCategory.ISHA_NIGHT,
            hisnulPillar = HisnulPillar.SALAH_PRAYER,
            titleBengali = "দোয়ায়ে কুনুত (বিতর সালাত)",
            titleEnglish = "Dua al-Qunut (Witr Prayer)",
            textArabic = "اللَّهُمَّ اهْدِنِي فِيمَنْ هَدَيْتَ، وَعَافِنِي فِيمَنْ عَافَيْتَ، وَتَوَلَّنِي فِيمَنْ تَوَلَّيْتَ، وَبَارِكْ لِي فِيمَا أَعْطَيْتَ، وَقِنِي شَرَّ مَا قَضَيْتَ، فَإِنَّكَ تَقْضِي وَلَا يُقْضَىٰ عَلَيْكَ، إِنَّهُ لَا يَذِلُّ مَنْ وَالَيْتَ، وَلَا يَعِزُّ مَنْ عَادَيْتَ، تَبَارَكْتَ رَبَّنَا وَتَعَالَيْتَ",
            transliteration = "Allahummahdinee feeman hadayt, wa 'afinee feeman 'afayt, wa tawallanee feeman tawallayt, wa barik lee feema a'tayt, wa qinee sharra ma qadayt, fa-innaka taqdee wa la yuqda 'alayk, innahu la yadhillu man walayt, wa la ya'izzu man 'adayt, tabarakta Rabbana wa ta'alayt.",
            transliterationBengali = "আল্লা-হুম্মাহদিনী ফীমান হাদায়ত, ওয়া ‘আ-ফিনী ফীমান ‘আ-ফায়ত, ওয়া তাওয়াল্লানী ফীমান তাওয়াল্লায়ত, ওয়া বা-রিক লী ফীমা- আ‘ত্বায়ত, ওয়া ক্বিনী শাররা মা- ক্বদ্বায়ত, ফা-ইন্নাকা তাক্বদ্বী ওয়ালা- ইয়ুক্বদ্বা- ‘আলায়ক, ইন্নাহূ লা- ইয়াযিল্লু মাওঁ ওয়া-লায়ত, ওয়ালা- ইয়া‘ইযযু মান ‘আ-দায়ত, তাবা-রকতা রব্বানা- ওয়া তা‘আ-লায়ত।",
            meaningBengali = "হে আল্লাহ! আমাকে তাদের অন্তর্ভুক্ত করে হেদায়েত দিন যাদের আপনি হেদায়েত দিয়েছেন; আমাকে তাদের অন্তর্ভুক্ত করে সুস্থতা দিন যাদের আপনি সুস্থতা দিয়েছেন; আমাকে তাদের অন্তর্ভুক্ত করে অভিভাবকত্ব দিন যাদের আপনি অভিভাবকত্ব দিয়েছেন; আমাকে যা দান করেছেন তাতে বরকত দিন; এবং আপনার নির্ধারিত মন্দ ফায়সালা থেকে আমাকে রক্ষা করুন। কেননা আপনিই ফায়সালা দেন এবং আপনার ওপর কোনো ফায়সালা খাটে না। নিশ্চয়ই আপনি যার বন্ধু হন সে কখনো লাঞ্ছিত হয় না, আর আপনি যার শত্রু হন সে কখনো মর্যাদা পায় না। হে আমাদের রব! আপনি বরকতময় এবং সর্বোচ্চ মহিমান্বিত।",
            meaningEnglish = "O Allah, guide me among those whom You have guided, and grant me well-being among those whom You have granted well-being, and take me as an ally among those whom You have taken as allies, and bless for me what You have given, and save me from the evil of what You have decreed. For indeed, You decree and none can decree over You. Indeed, he is not humiliated whom You take as an ally, nor is he honored whom You take as an enemy. Blessed are You, our Lord, and Exalted.",
            reference = "সুনানে আবু দাউদ: ১৪২৫, সুনানে তিরমিযী: ৪৬৪ (সহীহ)",
            virtuesBengali = "হাসান ইবনে আলী (রাঃ) বলেন, রাসুলুল্লাহ ﷺ আমাকে বিতরের কুনুতে পাঠ করার জন্য এই মহা বরকতময় শব্দগুলো শিখিয়েছিলেন।",
            repetitions = 1
        ),
        Dua(
            id = "dua_istikhara_full",
            category = "Salat & Wudu",
            categoryBengali = "সালাত-নামাজ",
            waqtCategory = WaqtCategory.ALL,
            hisnulPillar = HisnulPillar.SALAH_PRAYER,
            titleBengali = "সালাতুল ইস্তিখারার পূর্ণ দু'আ (সঠিক সিদ্ধান্তের পথ)",
            titleEnglish = "Complete Supplication of Istikharah",
            textArabic = "اللَّهُمَّ إِنِّي أَسْتَخِيرُكَ بِعِلْمِكَ، وَأَسْتَقْدِرُكَ بِقُدْرَتِكَ، وَأَسْأَلُكَ مِنْ فَضْلِكَ الْعَظِيمِ، فَإِنَّكَ تَقْدِرُ وَلَا أَقْدِرُ، وَتَعْلَمُ وَلَا أَعْلَمُ، وَأَنْتَ عَلَّامُ الْغُيُوبِ. اللَّهُمَّ إِنْ كُنْتَ تَعْلَمُ أَنَّ هَـٰذَا الْأَمْرَ خَيْرٌ لِي فِي دِينِي وَمَعَاشِي وَعَاقِبَةِ أَمْرِي فَاقْدُرْهُ لِي وَيَسِّرْهُ لِي ثُمَّ بَارِكْ لِي فِيهِ، وَإِنْ كُنْتَ تَعْلَمُ أَنَّ هَـٰذَا الْأَمْرَ شَرٌّ لِي فِي دِينِي وَمَعَاشِي وَعَاقِبَةِ أَمْرِي فَاصْرِفْهُ عَنِّي وَاصْرِفْنِي عَنْهُ، وَاقْدُرْ لِيَ الْخَيْرَ حَيْثُ كَانَ ثُمَّ أَرْضِنِي بِهِ",
            transliteration = "Allahumma innee astakheeruka bi-'ilmika, wa astaqdiruka bi-qudratika, wa as-aluka min fadlikal-'azeem, fa-innaka taqdiru wa la aqdir, wa ta'lamu wa la a'lam, wa Anta 'Allamul-ghuyoob. Allahumma in kunta ta'lamu anna hadhal-amra khayrul-lee fee deenee wa ma'ashee wa 'aqibati amree faqdurhu lee wa yassirhu lee thumma barik lee feeh; wa in kunta ta'lamu anna hadhal-amra sharrul-lee fee deenee wa ma'ashee wa 'aqibati amree fasrifhu 'annee wasrifnee 'anhu, waqdur liyal-khayra haythu kana thumma ardinee bih.",
            transliterationBengali = "আল্লা-হুম্মা ইন্নী আসতাখীরুকা বি‘ইলমিকা, ওয়া আসতাক্বদিরুকা বিক্বুদরাতিকা, ওয়া আস-আলুকা মিন ফাদ্বলিকাল ‘আযীম, ফা-ইন্নাকা তাক্বদিরু ওয়ালা- আক্বদির, ওয়া তা‘লামু ওয়ালা- আ‘লাম, ওয়া আনতা ‘আল্লা-মুল গুয়ূব। আল্লা-হুম্মা ইন কুনতা তা‘লামু আন্না হা-যাল আমরা খয়রুল লী ফী দীনী ওয়া মা‘আ-শী ওয়া ‘আ-ক্বিবাতি আমরী ফাক্বদুরহু লী ওয়া ইয়াসসিরহু লী ছুম্মা বা-রিক লী ফীহ; ওয়া ইন কুনতা তা‘লামু আন্না হা-যাল আমরা শাররুল লী ফী দীনী ওয়া মা‘আ-শী ওয়া ‘আ-ক্বিবাতি আমরী ফাসরিফহু ‘আন্নী ওয়াসরিফনী ‘আনহু, ওয়াক্বদুর লিয়াল খয়রা হায়ছু কা-না ছুম্মা আরদ্বিনী বিহ।",
            meaningBengali = "হে আল্লাহ! আমি আপনার জ্ঞানের সাহায্যে আপনার কাছে কল্যাণ প্রার্থনা করছি এবং আপনার কুদরতের সাহায্যে আপনার কাছে শক্তি প্রার্থনা করছি; আর আপনার মহান অনুগ্রহ ভিক্ষা করছি। কারণ আপনি সবকিছুর ক্ষমতা রাখেন, আমি ক্ষমতা রাখি না; আপনি সবকিছু জানেন, আমি কিছুই জানি না; এবং আপনি অদৃশ্য বিষয়ে পূর্ণ মহাজ্ঞানী। হে আল্লাহ! আপনার জ্ঞানে যদি এই কাজ (উদ্দিষ্ট বিষয়টি মনে মনে উল্লেখ করুন) আমার দ্বীন, আমার জীবিকা এবং আমার পরিণামের জন্য কল্যাণকর হয়, তবে তা আমার জন্য নির্ধারিত করে দিন, তা সহজ করে দিন এবং তাতে বরকত দান করুন। আর যদি আপনার জ্ঞানে এই বিষয়টি আমার দ্বীন, আমার জীবিকা এবং আমার পরিণামের জন্য ক্ষতিকর হয়, তবে তা আমার থেকে ফিরিয়ে নিন এবং আমাকেও তা থেকে দূরে সরিয়ে রাখুন; আর যেখানেই কল্যাণ থাকুক তা আমার জন্য নির্ধারণ করুন, অতঃপর তাতেই আমার মনকে সন্তুষ্ট করে দিন।",
            meaningEnglish = "O Allah, I seek Your guidance through Your knowledge, and I seek ability through Your power, and I ask of Your great bounty, for indeed You have power and I have none, and You know and I do not know, and You are the Knower of the unseen. O Allah, if You know that this matter is good for me in my religion, my livelihood and the outcome of my affair, then decree it for me, ease it for me, and bless me in it. And if You know that this matter is bad for me in my religion, my livelihood and the outcome of my affair, then turn it away from me and turn me away from it, and decree for me good wherever it may be, and make me pleased with it.",
            reference = "সহীহ বুখারী: ১১৬২, সহীহ মুসলিম: ৭৭২",
            virtuesBengali = "জাবির ইবনে আব্দুল্লাহ (রাঃ) বলেন: রাসুলুল্লাহ ﷺ কুরআনের সূরা শেখানোর মতোই গুরুত্ব সহকারে আমাদের জীবনের প্রতিটি পদক্ষেপে এই ইস্তিখারা শিক্ষা দিতেন।",
            repetitions = 1
        ),
        Dua(
            id = "dua_waswasah_in_prayer",
            category = "Salat & Wudu",
            categoryBengali = "সালাত-নামাজ",
            waqtCategory = WaqtCategory.ALL,
            hisnulPillar = HisnulPillar.SALAH_PRAYER,
            titleBengali = "নামাজে শয়তানের কুমন্ত্রণা (ওয়াসওয়াসা) দূর করার আমল",
            titleEnglish = "Overcoming Satanic Whispers in Prayer",
            textArabic = "أَعُوذُ بِاللَّهِ مِنَ الشَّيْطَانِ الرَّجِيمِ (وَاتْفُلْ عَلَىٰ يَسَارِكَ ثَلَاثًا)",
            transliteration = "A'oodhu billahi minash-shaytanir-rajeem (and spit lightly to the left 3 times).",
            transliterationBengali = "আ‘ঊযু বিল্লা-হি মিনাশ শায়ত্বা-নির রাজীম (এবং বাম দিকে ৩ বার হালকা থুতুর ভাব ফেলুন)।",
            meaningBengali = "বিতাড়িত অভিশপ্ত শয়তানের অনিষ্ট থেকে আল্লাহর কাছে আশ্রয় প্রার্থনা করছি।",
            meaningEnglish = "I seek refuge in Allah from Satan the outcast (and spit dryly to the left three times).",
            reference = "সহীহ মুসলিম: ২২৭৩",
            virtuesBengali = "উসমান ইবনে আবিল আস (রাঃ) এসে বললেন: হে আল্লাহর রাসুল! 'খিনযাব' নামক শয়তান আমার নামাজে কিরাত ও ধ্যানে বিভ্রম সৃষ্টি করে। নবীজি ﷺ তাকে বাম দিকে ৩ বার মৃদু থুতু ফেলে এ বাক্য পাঠের নির্দেশ দেন। তিনি বলেন: আমি তা করার পর আল্লাহ আমাকে সে পেরেশানি থেকে মুক্ত করে দিলেন।",
            repetitions = 1
        )
    )
}
