package com.example.data.model.dua

import com.example.data.model.Dua
import com.example.data.model.HisnulPillar
import com.example.data.model.WaqtCategory

/**
 * 🌿 Comprehensive Authentic Supplications for Shifa, Ruqyah, Rain/Thunder, Fear, Cemetery & Tawbah
 */
object SpecialMomentsDuaData {

    val duas: List<Dua> = listOf(
        // ---------------------------------------------------------------------
        // 1. SHIFA, PAIN & VISITING THE SICK (রোগমুক্তি, শারীরিক ব্যথা ও সেবা)
        // ---------------------------------------------------------------------
        Dua(
            id = "dua_shifa_pain_in_body",
            category = "Ruqyah & Protection",
            categoryBengali = "রুকিয়া ও সুরক্ষা",
            waqtCategory = WaqtCategory.RUQYAH_PROTECTION,
            hisnulPillar = HisnulPillar.BOOKMARKS_CUSTOM,
            titleBengali = "শরীরের যেকোনো ব্যথায় হাত রেখে নিরাময়ের আমল",
            titleEnglish = "Supplication for Pain in the Body",
            textArabic = "بِسْمِ اللَّهِ (ثَلَاثًا)\nأَعُوذُ بِاللَّهِ وَقُدْرَتِهِ مِنْ شَرِّ مَا أَجِدُ وَأُحَاذِرُ (سَبْعَ مَرَّاتٍ)",
            transliteration = "Bismillah (3 times), A'oodhu billahi wa qudratihi min sharri ma ajidu wa uhadhir (7 times).",
            transliterationBengali = "ব্যথার স্থানে হাত রেখে ৩ বার: বিসমিল্লা-হ। অতঃপর ৭ বার: আ‘ঊযু বিল্লা-হি ওয়া ক্বুদরাতিহী মিন শাররি মা- আজিদু ওয়া উহা-যির।",
            meaningBengali = "আল্লাহর নামে শুরু করছি (৩ বার)। আমি যা অনুভব করছি এবং যার আশঙ্কা করছি, তার অনিষ্ট থেকে আমি আল্লাহ এবং তাঁর কুদরতের আশ্রয় প্রার্থনা করছি (৭ বার)।",
            meaningEnglish = "In the Name of Allah (3 times). I seek refuge in Allah and in His Power from the evil of what I find and of what I fear (7 times).",
            reference = "সহীহ মুসলিম: ২২০২",
            virtuesBengali = "উসমান ইবনে আবিল আস (রাঃ) ব্যথায় কাতর হয়ে নবীজি ﷺ এর কাছে আসলে তিনি ব্যথার স্থানে ডান হাত রেখে এ দু'আ পাঠের নির্দেশ দেন। তিনি বলেন: আমি তা করার পর আল্লাহ আমাকে পূর্ণ সুস্থতা দান করলেন।",
            repetitions = 7
        ),
        Dua(
            id = "dua_visiting_sick_seven_times",
            category = "Ruqyah & Protection",
            categoryBengali = "রুকিয়া ও সুরক্ষা",
            waqtCategory = WaqtCategory.RUQYAH_PROTECTION,
            hisnulPillar = HisnulPillar.BOOKMARKS_CUSTOM,
            titleBengali = "রোগী দেখতে গেলে আরোগ্য কামনার দু'আ (৭ বার)",
            titleEnglish = "Supplication When Visiting a Sick Person (7 Times)",
            textArabic = "أَسْأَلُ اللَّهَ الْعَظِيمَ، رَبَّ الْعَرْشِ الْعَظِيمِ، أَنْ يَشْفِيَكَ",
            transliteration = "As-alullahal-'Azeem, Rabbal-'Arshil-'Azeem, ay-yashfiyak.",
            transliterationBengali = "আস-আলুল্লা-হাল ‘আযীম, রব্বাল ‘আরশিল ‘আযীম, আঁই ইয়্যাশফিয়াক।",
            meaningBengali = "আমি মহান আরশের মালিক মহান আল্লাহর নিকট প্রার্থনা করছি, তিনি যেন আপনাকে পরিপূর্ণ আরোগ্য ও শেফা দান করেন।",
            meaningEnglish = "I ask Allah the Magnificent, the Lord of the Magnificent Throne, to heal you.",
            reference = "সুনানে আবু দাউদ: ৩১০৬, সুনানে তিরমিযী: ২০৮৩ (সহীহ)",
            virtuesBengali = "রাসুলুল্লাহ ﷺ বলেছেন: কোনো মুসলিম যদি কোনো অসুস্থ ব্যক্তিকে দেখতে গিয়ে তার শয্যাপাশে ৭ বার এ দু'আ পাঠ করে, তবে তার মৃত্যুর নির্ধারিত সময় না এসে থাকলে আল্লাহ অবশ্যই তাকে সে রোগ থেকে মুক্তি দেবেন।",
            repetitions = 7
        ),
        Dua(
            id = "dua_prophet_ruqyah_shifa",
            category = "Ruqyah & Protection",
            categoryBengali = "রুকিয়া ও সুরক্ষা",
            waqtCategory = WaqtCategory.RUQYAH_PROTECTION,
            hisnulPillar = HisnulPillar.BOOKMARKS_CUSTOM,
            titleBengali = "অসুস্থ ব্যক্তির জন্য রাসুলুল্লাহ ﷺ এর পবিত্র রুকিয়া",
            titleEnglish = "Prophetic Ruqyah for Illness & Sickness",
            textArabic = "اللَّهُمَّ رَبَّ النَّاسِ، أَذْهِبِ الْبَأْسَ، اشْفِ أَنْتَ الشَّافِي، لَا شِفَاءَ إِلَّا شِفَاؤُكَ، شِفَاءً لَا يُغَادِرُ سَقَمًا",
            transliteration = "Allahumma Rabban-nas, adh-hibil-ba's, ishfi Antash-Shafee, la shifa'a illa shifa'uk, shifa'al-la yughadiru saqama.",
            transliterationBengali = "আল্লা-হুম্মা রব্বান না-স, আযহিবিল বা’স, ইশফি আনতাশ শা-ফী, লা- শিফা-আয়া ইল্লা- শিফা-উক, শিফা-আল লা- ইউগা-দিরু সাক্বামা-।",
            meaningBengali = "হে আল্লাহ! মানবজাতির প্রতিপালক! কষ্ট দূর করে দিন, রোগ নিরাময় করুন; আপনিই একমাত্র নিরাময়কারী। আপনার শেফা ছাড়া আর কোনো আরোগ্য নেই; এমন রোগমুক্তি দান করুন যা কোনো ব্যাধিকে অবশিষ্ট রাখে না।",
            meaningEnglish = "O Allah, Lord of mankind, remove the disease and grant recovery; You are the Healer, there is no healing except Your healing—a healing that leaves behind no illness.",
            reference = "সহীহ বুখারী: ৫৭৪৩, সহীহ মুসলিম: ২১৯১",
            virtuesBengali = "পরিবারের কেউ অসুস্থ হলে নবীজি ﷺ নিজের ডান হাত তার গায়ে বুলিয়ে দিয়ে এই পরম স্নেহমাখা দু'আটি পাঠ করতেন।",
            repetitions = 3
        ),

        // ---------------------------------------------------------------------
        // 2. NATURAL PHENOMENA (বৃষ্টি, মেঘ, ঝড় ও বজ্রপাত)
        // ---------------------------------------------------------------------
        Dua(
            id = "dua_thunder_lightning",
            category = "Travel & Daily Life",
            categoryBengali = "দৈনন্দিন ও সফর",
            waqtCategory = WaqtCategory.ALL,
            hisnulPillar = HisnulPillar.BOOKMARKS_CUSTOM,
            titleBengali = "বজ্রপাতের গর্জন শুনলে পাঠের দু'আ",
            titleEnglish = "Upon Hearing Thunder & Lightning",
            textArabic = "سُبْحَانَ الَّذِي يُسَبِّحُ الرَّعْدُ بِحَمْدِهِ وَالْمَلَائِكَةُ مِنْ خِيفَتِهِ",
            transliteration = "Subhanalladhee yusabbihur-ra'du bi-hamdihi wal-mala'ikatu min kheefatih.",
            transliterationBengali = "সুবহা-নাল্লাযী ইউসাব্বিহুর রা‘দু বিহামদিহী ওয়াল মালা-ইকাতু মিন খীফাতিহ।",
            meaningBengali = "পবিত্র সেই মহান সত্তা, যাঁর প্রশংসার সাথে মেঘের গর্জন এবং তাঁর ভয়ে ফেরেশতাকুল তাসবীহ পাঠ করে।",
            meaningEnglish = "Glory is to Him Whom the thunder glorifies with His praise, and the angels out of fear of Him.",
            reference = "মুয়াত্তা মালেক: ১৮০৮, আল-আদাবুল মুফরাদ: ৭২৩ (সহীহ)",
            virtuesBengali = "আব্দুল্লাহ ইবন যুবায়ের (রাঃ) যখনই মেঘের গর্জন শুনতেন কথা বলা বন্ধ করে দিয়ে এ বাক্য পাঠ করতেন। এতে বজ্রপাতের ধ্বংসলীলা থেকে নিরাপত্তা পাওয়া যায়।",
            repetitions = 1
        ),
        Dua(
            id = "dua_beneficial_rain",
            category = "Travel & Daily Life",
            categoryBengali = "দৈনন্দিন ও সফর",
            waqtCategory = WaqtCategory.ALL,
            hisnulPillar = HisnulPillar.BOOKMARKS_CUSTOM,
            titleBengali = "বৃষ্টি বর্ষণ শুরু হলে বরকতের দু'আ",
            titleEnglish = "Upon Rainfall for Beneficial Rain",
            textArabic = "اللَّهُمَّ صَيِّبًا نَافِعًا",
            transliteration = "Allahumma sayyiban nafi'a.",
            transliterationBengali = "আল্লা-হুম্মা স্বাইয়্যিবান না-ফি‘আ-।",
            meaningBengali = "হে আল্লাহ! আমাদের জন্য এই বৃষ্টিকে প্রচুর ও কল্যাণকর হিসেবে বর্ষণ করুন।",
            meaningEnglish = "O Allah, may it be a beneficial rainstorm.",
            reference = "সহীহ বুখারী: ১০৩২",
            virtuesBengali = "বৃষ্টি বর্ষণকালীন সময় দু'আ কবুলের অন্যতম সর্বোত্তম মুহূর্ত। ক্ষতিকর প্লাবনের বদলে সুফলা বৃষ্টির জন্য নবীজির সুন্নাত।",
            repetitions = 1
        ),

        // ---------------------------------------------------------------------
        // 3. FEAR, OPPRESSOR & SUDDEN TRIAL (শত্রুভয়, জালিমের ভয় ও বিপদ)
        // ---------------------------------------------------------------------
        Dua(
            id = "dua_fear_of_oppressor",
            category = "Distress & Trials",
            categoryBengali = "বিপদ ও ঋণমুক্তি",
            waqtCategory = WaqtCategory.DISTRESS_TRIALS,
            hisnulPillar = HisnulPillar.BOOKMARKS_CUSTOM,
            titleBengali = "জালিম শাসক বা শত্রুভয়ের সম্মুখীন হলে আশ্রয়",
            titleEnglish = "When Fearing an Oppressor or Enemy",
            textArabic = "اللَّهُمَّ إِنَّا نَجْعَلُكَ فِي نُحُورِهِمْ، وَنَعُوذُ بِكَ مِنْ شُرُورِهِمْ",
            transliteration = "Allahumma inna naj'aluka fee nuhoorihim, wa na'oodhu bika min shuroorihim.",
            transliterationBengali = "আল্লা-হুম্মা ইন্না- নাজ‘আলুকা ফী নুহূরিহিম, ওয়া না‘ঊযু বিকা মিন শুরূরিহিম।",
            meaningBengali = "হে আল্লাহ! আমরা আপনাকে তাদের মুখোমুখি রাখছি (তাদের বিরুদ্ধে প্রতিরোধস্বরূপ) এবং তাদের যাবতীয় অনিষ্ট ও চক্রান্ত থেকে আপনারই আশ্রয় নিচ্ছি।",
            meaningEnglish = "O Allah, we place You before them and we take refuge in You from their wickedness.",
            reference = "সুনানে আবু দাউদ: ১৫৩৭, নাসাঈ কুবরা: ৮৬৩০ (সহীহ)",
            virtuesBengali = "আবু বুরদাহ ইবনে নিয়ার (রাঃ) বর্ণনা করেন, রাসুলুল্লাহ ﷺ যখনই কোনো সম্প্রদায় বা প্রভাবশালী জালেমের অনিষ্টের ভয় করতেন, তখন এ দু'আ পাঠ করতেন।",
            repetitions = 3
        ),
        Dua(
            id = "dua_hasbunallah_sufficient",
            category = "Distress & Trials",
            categoryBengali = "বিপদ ও ঋণমুক্তি",
            waqtCategory = WaqtCategory.DISTRESS_TRIALS,
            hisnulPillar = HisnulPillar.BOOKMARKS_CUSTOM,
            titleBengali = "হাসবুনাল্লাহু ওয়া নি‘মাল ওয়াকীল (চূড়ান্ত ঈমানী নির্ভরতা)",
            titleEnglish = "Hasbunallahu wa Ni'mal Wakeel",
            textArabic = "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ",
            transliteration = "Hasbunallahu wa ni'mal-wakeel.",
            transliterationBengali = "হাসবুনাল্লা-হু ওয়া নি‘মাল ওয়াকীল।",
            meaningBengali = "আল্লাহই আমাদের জন্য যথেষ্ট এবং তিনি কতই না চমৎকার কর্মবিধায়ক ও অভিভাবক!",
            meaningEnglish = "Allah is sufficient for us, and He is the best Disposer of affairs.",
            reference = "সূরা আলে ইমরান: ১৭৩, সহীহ বুখারী: ৪৫৬৩",
            virtuesBengali = "ইবনে আব্বাস (রাঃ) বলেন: আগুনে নিক্ষিপ্ত হওয়ার মুহূর্তে ইবরাহীম (আঃ) এ বাক্য বলেছিলেন এবং সাহাবায়ে কেরাম যখন শুনেছিলেন শত্রুবাহিনী ধেয়ে আসছে, তখন তাঁরাও এ বাক্য বলে নির্ভীক হয়েছিলেন।",
            repetitions = 7
        ),

        // ---------------------------------------------------------------------
        // 4. CEMETERY & TAWBAH (কবর জিয়ারত ও তাওবা)
        // ---------------------------------------------------------------------
        Dua(
            id = "dua_cemetery_ziyarat_qubur",
            category = "Travel & Daily Life",
            categoryBengali = "দৈনন্দিন ও সফর",
            waqtCategory = WaqtCategory.ALL,
            hisnulPillar = HisnulPillar.BOOKMARKS_CUSTOM,
            titleBengali = "কবর জিয়ারতের মাসনুন সালাম ও দু'আ",
            titleEnglish = "Supplication Upon Visiting the Graveyard (Qubur)",
            textArabic = "السَّلَامُ عَلَيْكُمْ دَارَ قَوْمٍ مُّؤْمِنِينَ، وَإِنَّا إِن شَاءَ اللَّهُ بِكُمْ لَاحِقُونَ، نَسْأَلُ اللَّهَ لَنَا وَلَكُمُ الْعَافِيَةَ",
            transliteration = "As-salamu 'alaykum dara qawmim-mu'mineen, wa inna in sha Allahu bikum lahiqoon, nas-alullaha lana wa lakumul-'afiyah.",
            transliterationBengali = "আসসালা-মু ‘আলাইকুম দা-রা ক্বওমিম মু’মিনীন, ওয়া ইন্না- ইন শা-আল্লা-হু বিকুম লা-হিক্বূন, নাস-আলুল্লা-হা লানা- ওয়া লাকুমুল ‘আ-ফিয়াহ।",
            meaningBengali = "হে মোমিন কবরবাসীগণ! আপনাদের ওপর শান্তি বর্ষিত হোক। আমরাও নিশ্চিতভাবে ইনশাআল্লাহ আপনাদের সাথে মিলিত হতে যাচ্ছি। আমরা আল্লাহর কাছে আমাদের ও আপনাদের জন্য ক্ষমা ও নিরাপত্তা প্রার্থনা করছি।",
            meaningEnglish = "Peace be upon you, O inhabitants of the abode of believers! Indeed, we shall, if Allah wills, join you. We ask Allah for ourselves and for you well-being.",
            reference = "সহীহ মুসলিম: ৯৭৪, ৯৭৫",
            virtuesBengali = "কবরস্থানে প্রবেশ করে কবরবাসীর মাগফিরাত ও নিজের আখেরাতের স্মরণে রাসুলুল্লাহ ﷺ এর শেখানো সুন্নাত সালাম।",
            repetitions = 1
        ),
        Dua(
            id = "dua_forgiveness_even_desertion",
            category = "Forgiveness & Repentance",
            categoryBengali = "ক্ষমা ও তাওবা",
            waqtCategory = WaqtCategory.ISTIGHFAR,
            hisnulPillar = HisnulPillar.BOOKMARKS_CUSTOM,
            titleBengali = "যুদ্ধক্ষেত্র থেকে পালানোর মতো মহা পাপও ক্ষমার ইস্তিগফার",
            titleEnglish = "Seeking Supreme Forgiveness (Tawbah)",
            textArabic = "أَسْتَغْفِرُ اللَّهَ الَّذِي لَا إِلَـٰهَ إِلَّا هُوَ الْحَيَّ الْقَيُّومَ وَأَتُوبُ إِلَيْهِ",
            transliteration = "Astaghfirullahalladhee la ilaha illa Huwal-Hayyul-Qayyoomu wa atoobu ilayh.",
            transliterationBengali = "আস্তাগফিরুল্লা-হাল্লাযী লা- ইলা-হা ইল্লা- হুওয়াল হাইয়্যুল ক্বাইয়্যূমু ওয়া আতূবু ইলাইহ।",
            meaningBengali = "আমি সেই আল্লাহর নিকট ক্ষমা প্রার্থনা করছি যিনি ছাড়া কোনো সত্য উপাস্য নেই, যিনি চিরঞ্জীব ও সর্বসত্তার ধারক; এবং আমি তাঁরই দরবারে খাঁটি তাওবা করছি।",
            meaningEnglish = "I seek the forgiveness of Allah, there is no deity except Him, the Ever-Living, the Sustainer, and I repent unto Him.",
            reference = "সুনানে আবু দাউদ: ১৫১৭, সুনানে তিরমিযী: ৩৫৭৭ (সহীহ)",
            virtuesBengali = "রাসুলুল্লাহ ﷺ বলেছেন: যে ব্যক্তি এই ইস্তিগফার পাঠ করবে, তার অতীত সমস্ত পাপ মাফ করে দেওয়া হবে—এমনকি যদি সে যুদ্ধের ময়দান থেকে পৃষ্ঠপ্রদর্শন করার মতো কবিরা গুনাহও করে থাকে!",
            repetitions = 3
        ),
        Dua(
            id = "dua_purification_of_soul_taqwa",
            category = "Forgiveness & Repentance",
            categoryBengali = "ক্ষমা ও তাওবা",
            waqtCategory = WaqtCategory.ISTIGHFAR,
            hisnulPillar = HisnulPillar.BOOKMARKS_CUSTOM,
            titleBengali = "অন্তর ও আত্মার পরিশুদ্ধি (তাকওয়া) লাভের দু'আ",
            titleEnglish = "Purification of the Soul & Piety",
            textArabic = "اللَّهُمَّ آتِ نَفْسِي تَقْوَاهَا، وَزَكِّهَا أَنْتَ خَيْرُ مَنْ زَكَّاهَا، أَنْتَ وَلِيُّهَا وَمَوْلَاهَا",
            transliteration = "Allahumma aati nafsee taqwaha, wa zakkiha Anta khayru man zakkaha, Anta Waliyyuha wa Mawlaha.",
            transliterationBengali = "আল্লা-হুম্মা আ-তি নাফসী তাক্বওয়া-হা-, ওয়া যাক্কিহা- আনতা খয়রু মান যাক্কা-হা-, আনতা ওয়ালিইয়্যুহা- ওয়া মাওলা-হা-।",
            meaningBengali = "হে আল্লাহ! আমার আত্মাকে তাকওয়া (খোদাভীতি) দান করুন এবং তাকে পরিশুদ্ধ করুন; আপনিই তো আত্মার সর্বোত্তম পরিশোধনকারী। আপনিই তার একমাত্র অভিভাবক ও পরম বন্ধু।",
            meaningEnglish = "O Allah, grant my soul its piety and purify it; You are the best of those who purify it. You are its Guardian and its Protector.",
            reference = "সহীহ মুসলিম: ২৭২২",
            virtuesBengali = "হৃদয়ের কুপ্রবৃত্তি দমন ও মনের পাপাসক্তি দূর করে আল্লাহর খাঁটি ওলী হওয়ার জন্য এটি অত্যন্ত শক্তিমান দু'আ।",
            repetitions = 1
        )
    )
}
