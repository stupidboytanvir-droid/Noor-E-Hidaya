package com.example.data.model.dua

import com.example.data.model.Dua
import com.example.data.model.HisnulPillar
import com.example.data.model.WaqtCategory

/**
 * 🌅 Comprehensive Authentic Morning & Evening Adhkar (Fortress of a Muslim - Hisn al-Muslim)
 */
object MorningEveningAzkarData {

    val duas: List<Dua> = listOf(
        // ---------------------------------------------------------------------
        // 1. CHIEF SUPPLICATIONS (প্রধান আযকার)
        // ---------------------------------------------------------------------
        Dua(
            id = "dua_sayyidul_istighfar",
            category = "Morning & Evening",
            categoryBengali = "সকাল-সন্ধ্যা",
            waqtCategory = WaqtCategory.FAJR_MORNING,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "সাইয়্যিদুল ইস্তিগফার (সর্বশ্রেষ্ঠ ক্ষমা প্রার্থনা)",
            titleEnglish = "Sayyidul Istighfar (Chief of Forgiveness)",
            textArabic = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَـٰهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَىٰ عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
            transliteration = "Allahumma Anta Rabbi la ilaha illa Anta, khalaqtani wa ana 'abduka, wa ana 'ala 'ahdika wa wa'dika mastata'tu, a'udhu bika min sharri ma sana'tu, abu'u laka bini'matika 'alayya, wa abu'u bidhanbi faghfir li fa-innahu la yaghfirudh-dhunuba illa Anta.",
            transliterationBengali = "আল্লা-হুম্মা আনতা রব্বী লা- ইলা-হা ইল্লা- আনতা, খলাক্বতানী ওয়া আনা ‘আবদুকা, ওয়া আনা ‘আলা- ‘আহ্‌দিকা ওয়া ওয়া‘দিকা মাসতাত্বা‘তু, আ‘ঊযু বিকা মিন শাররি মা- সনা‘তু, আবূউ লাকা বিনি‘মাতিকা ‘আলাইয়্যা, ওয়া আবূউ বিযানবী ফাগফির লী ফা-ইন্নাহূ লা- ইয়্যাগফিরুয যুনূবা ইল্লা- আনতা।",
            meaningBengali = "হে আল্লাহ! আপনিই আমার প্রতিপালক। আপনি ব্যতীত কোনো সত্য উপাস্য নেই। আপনি আমাকে সৃষ্টি করেছেন এবং আমি আপনার বান্দা। আমি আমার সাধ্যানুযায়ী আপনার অঙ্গীকার ও প্রতিশ্রুতির ওপর প্রতিষ্ঠিত আছি। আমি আমার কৃতকর্মের অনিষ্ট থেকে আপনার আশ্রয় চাই। আমার ওপর আপনার নিয়ামত স্বীকার করছি এবং আমার পাপও স্বীকার করছি। অতএব আমাকে ক্ষমা করুন, কেননা আপনি ব্যতীত পাপ ক্ষমা করার কেউ নেই।",
            meaningEnglish = "O Allah, You are my Lord; none has the right to be worshipped except You. You created me and I am Your servant, and I abide by Your covenant and promise as best I can. I seek refuge in You from the evil of what I have done. I acknowledge Your favor upon me and I acknowledge my sin, so forgive me, for none forgives sins except You.",
            reference = "সহীহ বুখারী: ৬৩০৬",
            virtuesBengali = "যে ব্যক্তি দৃঢ় বিশ্বাসের সাথে সকালে পাঠ করে সন্ধ্যার পূর্বে মারা যাবে সে জান্নাতী হবে; আর যে সন্ধ্যায় পাঠ করে সকালের পূর্বে মারা যাবে সেও জান্নাতবাসী হবে।",
            repetitions = 1
        ),
        Dua(
            id = "dua_bismillahilladhi_azkar",
            category = "Morning & Evening",
            categoryBengali = "সকাল-সন্ধ্যা",
            waqtCategory = WaqtCategory.FAJR_MORNING,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "সকল অনিষ্ট ও বিষাক্ত প্রাণী থেকে রক্ষার দু'আ (৩ বার)",
            titleEnglish = "Protection from All Harm on Earth & Heaven",
            textArabic = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
            transliteration = "Bismillahilladhee la yadurru ma'as-mihi shay'un fil-ardi wa la fis-sama'i wa Huwas-Sami'ul-'Alim.",
            transliterationBengali = "বিসমিল্লা-হিল্লাযী লা- ইয়াদুররু মা‘আসমিহী শাইয়ুন ফিল আরদ্বি ওয়ালা- ফিস সামা-য়ি, ওয়াহুওয়াস সামী‘উল ‘আলীম।",
            meaningBengali = "আল্লাহর নামে, যাঁর নামের সাথে জমিন ও আসমানের কোনো কিছুই কোনো ক্ষতি করতে পারে না; আর তিনি সর্বশ্রোতা, সর্বজ্ঞাতা।",
            meaningEnglish = "In the Name of Allah, with Whose Name nothing upon earth or in the heavens can cause harm, and He is the All-Hearing, the All-Knowing.",
            reference = "সুনানে আবু দাউদ: ৫০৮৮, সুনানে তিরমিযী: ৩৩৮৮ (সহীহ)",
            virtuesBengali = "সকালে ও সন্ধ্যায় ৩ বার পাঠ করলে কোনো বিষাক্ত কীট, আকস্মিক বিপদ বা কোনো সৃষ্টি ক্ষতি করতে পারে না।",
            repetitions = 3
        ),
        Dua(
            id = "dua_three_quls_morning_evening",
            category = "Morning & Evening",
            categoryBengali = "সকাল-সন্ধ্যা",
            waqtCategory = WaqtCategory.FAJR_MORNING,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "তিন কুল (সূরা ইখলাস, ফালাক্ব ও নাস - ৩ বার)",
            titleEnglish = "The Three Quls (Morning & Evening Shield - 3 Times)",
            textArabic = "بِسْمِ اللَّهِ الرَّحْمَـٰنِ الرَّحِيمِ\nقُلْ هُوَ اللَّهُ أَحَدٌ ۝ اللَّهُ الصَّمَدُ ۝ لَمْ يَلِدْ وَلَمْ يُولَدْ ۝ وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ ۝\nقُلْ أَعُوذُ بِرَبِّ الْفَلَقِ ۝ مِن شَرِّ مَا خَلَقَ ۝ وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ ۝ وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ ۝ وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ ۝\nقُلْ أَعُوذُ بِرَبِّ النَّاسِ ۝ مَلِكِ النَّاسِ ۝ إِلَـٰهِ النَّاسِ ۝ مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ ۝ الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ ۝ مِنَ الْجِنَّةِ وَالنَّاسِ ۝",
            transliteration = "Surah Al-Ikhlas, Surah Al-Falaq, Surah An-Nas (Recited 3 times each morning and evening).",
            transliterationBengali = "সূরা আল-ইখলাস, সূরা আল-ফালাক্ব ও সূরা আন-নাস (সকালে ৩ বার ও সন্ধ্যায় ৩ বার পাঠ করুন)।",
            meaningBengali = "একক আল্লাহর তাওহীদ, সকল সৃষ্টির অনিষ্ট, জাদু-টোনার গিট এবং শয়তান ও মানুষের মনের কুমন্ত্রণা থেকে বিশ্ব জাহানের প্রতিপালকের নিকট পূর্ণাঙ্গ আশ্রয় গ্রহণ।",
            meaningEnglish = "Declaration of absolute Oneness of Allah, and seeking refuge with the Lord of daybreak and the Lord of mankind from every created evil, witchcraft, jealousy and whispers.",
            reference = "সুনানে আবু দাউদ: ৫০৮২, সুনানে তিরমিযী: ৩৫৭৫ (হাসান সহীহ)",
            virtuesBengali = "রাসুলুল্লাহ ﷺ আব্দুল্লাহ ইবনে খুবাইব (রাঃ)-কে বললেন: তুমি সকালে ও সন্ধ্যায় ৩ বার এই তিনটি সূরা পাঠ করবে, তা তোমাকে প্রতিটি ক্ষতিকর বিষয় থেকে রক্ষার জন্য যথেষ্ট হবে।",
            repetitions = 3
        ),
        Dua(
            id = "dua_hasbiyallahu_seven_times",
            category = "Morning & Evening",
            categoryBengali = "সকাল-সন্ধ্যা",
            waqtCategory = WaqtCategory.FAJR_MORNING,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "সকল দুশ্চিন্তা নিরসনে আল্লাহর ওপর পূর্ণ নির্ভরতা (৭ বার)",
            titleEnglish = "Sufficiency in Allah (Recited 7 Times)",
            textArabic = "حَسْبِيَ اللَّهُ لَا إِلَـٰهَ إِلَّا هُوَ ۖ عَلَيْهِ تَوَكَّلْتُ ۖ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ",
            transliteration = "Hasbiyallahu la ilaha illa Huwa, 'alayhi tawakkaltu wa Huwa Rabbul-'Arshil-'Azeem.",
            transliterationBengali = "হাসবিয়াল্লা-হু লা- ইলা-হা ইল্লা- হুওয়া, ‘আলাইহি তাওয়াক্কালতু ওয়াহুওয়া রব্বুল ‘আরশিল ‘আযীম।",
            meaningBengali = "আল্লাহই আমার জন্য যথেষ্ট, তিনি ব্যতীত কোনো সত্য উপাস্য নেই; তাঁরই ওপর আমি পূর্ণ নির্ভর করেছি এবং তিনি মহান আরশের অধিপতি।",
            meaningEnglish = "Sufficient for me is Allah; there is no deity except Him. On Him I have relied, and He is the Lord of the Great Throne.",
            reference = "সূরা আত-তাওবাহ: ১২৯, সুনানে আবু দাউদ: ৫০৮১ (সহীহ সূত্রে মওকুফ)",
            virtuesBengali = "যে ব্যক্তি সকালে ও সন্ধ্যায় ৭ বার এ বাক্যটি বলবে, তার দুনিয়া ও আখেরাতের সকল দুশ্চিন্তা দূর করার জন্য আল্লাহ তায়ালাই যথেষ্ট হয়ে যান।",
            repetitions = 7
        ),

        // ---------------------------------------------------------------------
        // 2. CREATION-COUNT TASBIH & SUBHANALLAH (সৃষ্টির সমপরিমাণ জিকির)
        // ---------------------------------------------------------------------
        Dua(
            id = "dua_juwayriyah_creation_tasbih",
            category = "Morning & Evening",
            categoryBengali = "সকাল-সন্ধ্যা",
            waqtCategory = WaqtCategory.FAJR_MORNING,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "সারা দিনের সকল জিকিরের চেয়ে ভারী তাসবীহ (৩ বার)",
            titleEnglish = "The Heavy Weight Tasbih of Juwayriyah (3 Times)",
            textArabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، عَدَدَ خَلْقِهِ، وَرِضَا نَفْسِهِ، وَزِنَةَ عَرْشِهِ، وَمِدَادَ كَلِمَاتِهِ",
            transliteration = "Subhanallahi wa bihamdihi, 'adada khalqihi, wa rida nafsihi, wa zinata 'arshihi, wa midada kalimatih.",
            transliterationBengali = "সুবহা-নাল্লা-হি ওয়া বিহামদিহী, ‘আদাদা খলক্বিহী, ওয়া রিদ্বা- নাফসিহী, ওয়া যিনাতা ‘আরশিহী, ওয়া মিদা-দা কালিমা-তিহ।",
            meaningBengali = "আল্লাহর প্রশংসার সাথে তাঁর পবিত্রতা বর্ণনা করছি—তাঁর সৃষ্টির সংখ্যার সমপরিমাণ, তাঁর নিজের সন্তুষ্টির সমপরিমাণ, তাঁর মহান আরশের ওজনের সমপরিমাণ এবং তাঁর মহিমান্বিত বাক্যসমূহের কালির সমপরিমাণ।",
            meaningEnglish = "Glory is to Allah and praise is to Him, by the number of His creation and by the pleasure of Himself and by the weight of His Throne and by the ink of His words.",
            reference = "সহীহ মুসলিম: ২৭২৬",
            virtuesBengali = "নবীজি ﷺ মা জুওয়াইরিয়া (রাঃ)-কে বললেন: আমি ফজরের পর থেকে যে ৪টি বাক্য ৩ বার পড়েছি, তা যদি তোমার সকাল থেকে করা সমস্ত জিকিরের সাথে ওজন করা হয়, তবে এগুলোর ওজনই বেশি ভারী হবে!",
            repetitions = 3
        ),
        Dua(
            id = "dua_subhanallahi_wa_bihamdihi_100",
            category = "Morning & Evening",
            categoryBengali = "সকাল-সন্ধ্যা",
            waqtCategory = WaqtCategory.FAJR_MORNING,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "সমুদ্রের ফেনা পরিমাণ পাপ ক্ষমা (১০০ বার)",
            titleEnglish = "SubhanAllahi wa Bihamdihi (100 Times Daily)",
            textArabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
            transliteration = "Subhanallahi wa bihamdih.",
            transliterationBengali = "সুবহা-নাল্লা-হি ওয়া বিহামদিহ্।",
            meaningBengali = "আল্লাহর পবিত্রতা ঘোষণা করছি তাঁর প্রশংসার সাথে।",
            meaningEnglish = "Glory is to Allah and all praise is for Him.",
            reference = "সহীহ বুখারী: ৬৪০৫, সহীহ মুসলিম: ২৬৯১",
            virtuesBengali = "যে ব্যক্তি দিনে ১০০ বার 'সুবহানাল্লাহি ওয়া বিহামদিহী' পাঠ করবে, তার সকল সগিরা গুনাহ ক্ষমা করে দেওয়া হবে—যদিও তা সমুদ্রের ফেনার চেয়েও বেশি হয়!",
            repetitions = 100
        ),
        Dua(
            id = "dua_la_ilaha_illallah_100_times",
            category = "Morning & Evening",
            categoryBengali = "সকাল-সন্ধ্যা",
            waqtCategory = WaqtCategory.FAJR_MORNING,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "দশটি দাস মুক্ত ও শয়তান থেকে দুর্গ লাভ (১০০ বার)",
            titleEnglish = "Greatest Adhkar for Protection (100 Times Daily)",
            textArabic = "لَا إِلَـٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
            transliteration = "La ilaha illallahu wahdahu la shareeka lahu, lahul-mulku wa lahul-hamdu wa Huwa 'ala kulli shay'in Qadeer.",
            transliterationBengali = "লা- ইলা-হা ইল্লাল্লা-হু ওয়াহদাহূ লা- শারীকা লাহূ, লাহুল মুলকু ওয়া লাহুল হামদু ওয়াহুওয়া ‘আলা- কুল্লি শাইয়িন ক্বদীর।",
            meaningBengali = "আল্লাহ ছাড়া কোনো সত্য উপাস্য নেই, তিনি এক, তাঁর কোনো শরিক নেই। রাজত্ব একমাত্র তাঁরই এবং সমস্ত প্রশংসাও তাঁরই, আর তিনি সবকিছুর ওপর সর্বশক্তিমান।",
            meaningEnglish = "None has the right to be worshipped except Allah alone, without partner. To Him belongs all sovereignty and praise and He is over all things omnipotent.",
            reference = "সহীহ বুখারী: ৩২৯৭, সহীহ মুসলিম: ২৬৯৩",
            virtuesBengali = "দিনে ১০০ বার পাঠ করলে ১০ জন দাস মুক্ত করার সওয়াব মিলে, ১০০ নেকি লেখা হয়, ১০০ গুনাহ মাফ হয় এবং সারাদিনের জন্য শয়তানের অনিষ্ট থেকে নিরাপত্তা দুর্গ তৈরি হয়।",
            repetitions = 100
        ),
        Dua(
            id = "dua_evening_arrival_amsayna",
            category = "Morning & Evening",
            categoryBengali = "সকাল-সন্ধ্যা",
            waqtCategory = WaqtCategory.MAGHRIB_EVENING,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "সন্ধ্যার আগমন ও রাতের অনিষ্ট থেকে মুক্তি",
            titleEnglish = "Evening Arrival Supplication (Amsayna)",
            textArabic = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَـٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ. رَبِّ أَسْأَلُكَ خَيْرَ مَا فِي هَـٰذِهِ اللَّيْلَةِ وَخَيْرَ مَا بَعْدَهَا، وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِي هَـٰذِهِ اللَّيْلَةِ وَشَرِّ مَا بَعْدَهَا",
            transliteration = "Amsayna wa amsal-mulku lillahi, wal-hamdu lillahi, la ilaha illallahu wahdahu la shareeka lahu, lahul-mulku wa lahul-hamdu wa Huwa 'ala kulli shay'in Qadeer. Rabbi as-aluka khayra ma fee hadhihil-laylati wa khayra ma ba'daha, wa a'oodhu bika min sharri ma fee hadhihil-laylati wa sharri ma ba'daha.",
            transliterationBengali = "আমসাইনা- ওয়া আমসাল মুলকু লিল্লা-হ, ওয়ালহামদু লিল্লা-হ, লা- ইলা-হা ইল্লাল্লা-হু ওয়াহদাহূ লা- শারীকা লাহূ, লাহুল মুলকু ওয়া লাহুল হামদু ওয়াহুওয়া ‘আলা- কুল্লি শাইয়িন ক্বদীর। রব্বি আস-আলুকা খয়রা মা- ফী হা-যিহিল লায়লাতি ওয়া খয়রা মা- বা‘দাহা-, ওয়া আ‘ঊযু বিকা মিন শাররি মা- ফী হা-যিহিল লায়লাতি ওয়া শাররি মা- বা‘দাহা-।",
            meaningBengali = "আমরা সন্ধ্যায় উপনীত হয়েছি এবং সমগ্র সার্বভৌমত্বও আল্লাহর জন্য সন্ধ্যায় উপনীত হয়েছে। সমস্ত প্রশংসা আল্লাহর। আল্লাহ ব্যতীত কোনো সত্য উপাস্য নেই, তিনি একক, তাঁর কোনো শরিক নেই। হে আমার রব! আমি এই রাতের কল্যাণ ও এর পরবর্তী সময়ের কল্যাণ আপনার কাছে প্রার্থনা করছি; এবং এই রাতের অনিষ্ট ও এর পরবর্তী অনিষ্ট থেকে আপনার আশ্রয় চাচ্ছি।",
            meaningEnglish = "We have reached the evening and at this very evening unto Allah belongs all sovereignty, and all praise is for Allah. None has the right to be worshipped except Allah alone, without partner. My Lord, I ask of You the good of this night and the good of what follows it, and I seek refuge in You from the evil of this night and the evil of what follows it.",
            reference = "সহীহ মুসলিম: ২৭২৩",
            virtuesBengali = "সূর্যাস্ত বা মাগরিবের পর এ দু'আ পাঠ করলে সমগ্র রাত শয়তানের অনিষ্ট, দুঃস্বপ্ন ও বিপদাপদ থেকে নিরাপদে অতিবাহিত হয়।",
            repetitions = 1
        ),
        Dua(
            id = "dua_protection_shirk_conscious_unconscious",
            category = "Morning & Evening",
            categoryBengali = "সকাল-সন্ধ্যা",
            waqtCategory = WaqtCategory.FAJR_MORNING,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "জ্ঞাত ও অজ্ঞাত সকল শিরক থেকে আত্মরক্ষার দু'আ",
            titleEnglish = "Protection from Conscious & Unconscious Shirk",
            textArabic = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ أَنْ أُشْرِكَ بِكَ وَأَنَا أَعْلَمُ، وَأَسْتَغْفِرُكَ لِمَا لَا أَعْلَمُ",
            transliteration = "Allahumma innee a'oodhu bika an ushrika bika wa ana a'lam, wa astaghfiruka lima la a'lam.",
            transliterationBengali = "আল্লা-হুম্মা ইন্নী আ‘ঊযু বিকা আন উশরিকা বিকা ওয়া আনা- আ‘লাম, ওয়া আসতাগফিরুকা লিমা- লা- আ‘লাম।",
            meaningBengali = "হে আল্লাহ! আমি জেনে-শুনে আপনার সাথে কোনো কিছুকে শরিক করা থেকে আপনার কাছে আশ্রয় চাচ্ছি; এবং যে শিরক সম্পর্কে আমার জানা নেই তার জন্য আপনার কাছে ক্ষমা প্রার্থনা করছি।",
            meaningEnglish = "O Allah, I seek refuge in You from knowingly associating partners with You, and I seek Your forgiveness for that which I do not know.",
            reference = "আল-আদাবুল মুফরাদ: ৭১৬, মুসনাদে আহমাদ: ১৯৬০৬ (সহীহ)",
            virtuesBengali = "রাসুলুল্লাহ ﷺ আবু বকর (রাঃ)-কে বললেন: শিরক পিঁপড়ার নিঃশব্দ চলার চেয়েও বেশি গুপ্ত। এই দু'আ পাঠ করলে বড় ও ছোট (রিয়া বা লোক দেখানো) সকল শিরক থেকে আল্লাহ হেফাজত করেন।",
            repetitions = 3
        )
    )
}
