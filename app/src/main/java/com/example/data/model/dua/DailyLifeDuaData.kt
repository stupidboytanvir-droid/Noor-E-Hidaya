package com.example.data.model.dua

import com.example.data.model.Dua
import com.example.data.model.HisnulPillar
import com.example.data.model.WaqtCategory

/**
 * 🏡 Comprehensive Authentic Supplications for Daily Life, Travel, Etiquettes, and Distress
 */
object DailyLifeDuaData {

    val duas: List<Dua> = listOf(
        // ---------------------------------------------------------------------
        // 1. SLEEP & WAKING UP (ঘুম ও জাগ্রত অবস্থা)
        // ---------------------------------------------------------------------
        Dua(
            id = "dua_waking_up",
            category = "Sleep",
            categoryBengali = "ঘুম ও জাগরণ",
            waqtCategory = WaqtCategory.FAJR_MORNING,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "ঘুম থেকে জাগ্রত হওয়ার শোকরিয়া দু'আ",
            titleEnglish = "Upon Waking Up in the Morning",
            textArabic = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
            transliteration = "Alhamdu lillahilladhee ahyana ba'da ma amatana wa ilayhin-nushoor.",
            transliterationBengali = "আলহামদু লিল্লা-হিল্লাযী আহ্ইয়ানা- বা‘দা মা- আমা-তানা- ওয়া ইলাইহিন নুশূর।",
            meaningBengali = "সমস্ত প্রশংসা আল্লাহর জন্য, যিনি আমাদেরকে মৃত্যু (নিদ্রা) দেওয়ার পর পুনরায় জীবিত করলেন; এবং তাঁরই নিকট সকলের পুনরুত্থান হবে।",
            meaningEnglish = "All praise is for Allah Who gave us life after having taken it from us and unto Him is the resurrection.",
            reference = "সহীহ বুখারী: ৬৩১২, সহীহ মুসলিম: ২৭১১",
            virtuesBengali = "ঘুম থেকে জেগে এ দু'আ পাঠের মাধ্যমে আল্লাহর রহমতে ঈমানি সচেতনতা নিয়ে দিনের শুভ সূচনা হয়।",
            repetitions = 1
        ),
        Dua(
            id = "dua_sleeping_bismika",
            category = "Sleep",
            categoryBengali = "ঘুম ও জাগরণ",
            waqtCategory = WaqtCategory.ISHA_NIGHT,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "ঘুমানোর পূর্বে প্রধান দু'আ",
            titleEnglish = "Supplication Before Sleeping",
            textArabic = "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
            transliteration = "Bismikallahumma amootu wa ahya.",
            transliterationBengali = "বিসমিকাল্লা-হুম্মা আমূতু ওয়া আহ্ইয়া-।",
            meaningBengali = "হে আল্লাহ! আপনারই নামে আমি মৃত্যুবরণ (নিদ্রা গ্রহণ) করছি এবং জীবিত (জাগ্রত) হব।",
            meaningEnglish = "In Your Name, O Allah, I die and I live.",
            reference = "সহীহ বুখারী: ৬৩২৪, সহীহ মুসলিম: ২৭১১",
            virtuesBengali = "ডান কাত হয়ে ডান হাত গালের নিচে রেখে এই দু'আ পাঠ করে ঘুমানো রাসুলুল্লাহ ﷺ এর পবিত্র সুন্নাত।",
            repetitions = 1
        ),
        Dua(
            id = "dua_sleeping_fitrah",
            category = "Sleep",
            categoryBengali = "ঘুম ও জাগরণ",
            waqtCategory = WaqtCategory.ISHA_NIGHT,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "ঘুমানোর পূর্বে ফিতরাতের ওপর মৃত্যুর গ্যারান্টি দু'আ",
            titleEnglish = "Supplication for Dying upon Natural Faith (Fitrah)",
            textArabic = "اللَّهُمَّ أَسْلَمْتُ نَفْسِي إِلَيْكَ، وَفَوَّضْتُ أَمْرِي إِلَيْكَ، وَوَجَّهْتُ وَجْهِي إِلَيْكَ، وَأَلْجَأْتُ ظَهْرِي إِلَيْكَ، رَغْبَةً وَرَهْبَةً إِلَيْكَ، لَا مَلْجَأَ وَلَا مَنْجَا مِنْكَ إِلَّا إِلَيْكَ، آمَنْتُ بِكِتَابِكَ الَّذِي أَنْزَلْتَ، وَبِنَبِيِّكَ الَّذِي أَرْسَلْتَ",
            transliteration = "Allahumma aslamtu nafsee ilayk, wa fawwadtu amree ilayk, wa wajjahtu wajhee ilayk, wa alja'tu zahree ilayk, raghbatan wa rahbatan ilayk, la malja'a wa la manja minka illa ilayk, amantu bi-kitabikalladhee anzalt, wa bi-nabiyyikalladhee arsalt.",
            transliterationBengali = "আল্লা-হুম্মা আসলামতু নাফসী ইলাইক, ওয়া ফাওওয়াদ্বতু আমরী ইলাইক, ওয়া ওয়াজ্জাহতু ওয়াজহী ইলাইক, ওয়া আলজা’তু যাহরী ইলাইক, রাগবাতাঁও ওয়া রাহবাতান ইলাইক, লা- মালজা’আ ওয়ালা- মানজা- মিনকা ইল্লা- ইলাইক, আ-মানতু বিকিতা-বিকাল্লাযী আনযালত, ওয়া বিনাবিয়্যিকাল্লাযী আরসালত।",
            meaningBengali = "হে আল্লাহ! আমি নিজেকে আপনার কাছে সঁপে দিলাম, আমার সব কাজ আপনার নিকট অর্পণ করলাম, আমার মুখ আপনার দিকে ফেরালাম এবং আমার পিঠ আপনার আশ্রয়ে রাখলাম—আপনার প্রতি গভীর আশা ও ভীতি নিয়ে। আপনি ব্যতীত আপনার পাকড়াও থেকে বাঁচার কোনো আশ্রয় ও মুক্তি নেই। আমি ঈমান আনলাম আপনার অবতীর্ণ কিতাবের ওপর এবং আপনার প্রেরিত নবীর ওপর।",
            meaningEnglish = "O Allah, I submit my soul unto You, and I entrust my affair unto You, and I turn my face towards You, and I commit my back unto You in longing and in fear of You. There is no refuge and no salvation from You except unto You. I believe in Your Book which You have revealed and in Your Prophet whom You have sent.",
            reference = "সহীহ বুখারী: ২৪৭, সহীহ মুসলিম: ২৭১০",
            virtuesBengali = "রাসুলুল্লাহ ﷺ আল-বারাআ ইবনে আযিব (রাঃ)-কে বললেন: তুমি রাতে এ দু'আ পড়ে ঘুমালে যদি সেই রাতে তোমার মৃত্যু ঘটে, তবে তুমি ইসলামের মূল ফিতরাতের ওপর মৃত্যুবরণ করবে।",
            repetitions = 1
        ),
        Dua(
            id = "dua_bad_dream_protection",
            category = "Sleep",
            categoryBengali = "ঘুম ও জাগরণ",
            waqtCategory = WaqtCategory.ISHA_NIGHT,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "ভয়ঙ্কর স্বপ্ন বা দুঃস্বপ্ন দেখলে করণীয় দু'আ",
            titleEnglish = "Protection from Bad Dreams & Nightmares",
            textArabic = "أَعُوذُ بِاللَّهِ مِنَ الشَّيْطَانِ الرَّجِيمِ وَمِنْ شَرِّ هَـٰذِهِ الرُّؤْيَا",
            transliteration = "A'oodhu billahi minash-shaytanir-rajeem wa min sharri hadhihir-ru'ya.",
            transliterationBengali = "আ‘ঊযু বিল্লা-হি মিনাশ শায়ত্বা-নির রাজীম ওয়া মিন শাররি হা-যিহির রু’ইয়া-।",
            meaningBengali = "অভিশপ্ত বিতাড়িত শয়তান থেকে এবং এই স্বপ্নের ক্ষতি ও অনিষ্ট থেকে আল্লাহর আশ্রয় প্রার্থনা করছি।",
            meaningEnglish = "I seek refuge in Allah from Satan the outcast and from the evil of this dream.",
            reference = "সহীহ মুসলিম: ২২৬১, সহীহ বুখারী: ৬৯৮৬",
            virtuesBengali = "খারাপ স্বপ্ন দেখলে বাম দিকে তিনবার হালকা থুতুর ভাব ফেলবে, আল্লাহর কাছে আশ্রয় চাইবে এবং পার্শ্ব পরিবর্তন করবে; তবে সে স্বপ্ন তার কোনো ক্ষতি করতে পারবে না। কাউকে সে স্বপ্নের কথা বলা যাবে না।",
            repetitions = 1
        ),

        // ---------------------------------------------------------------------
        // 2. BATHROOM & CLOTHING (টয়লেট ও পোশাক-পরিচ্ছদ)
        // ---------------------------------------------------------------------
        Dua(
            id = "dua_toilet_enter",
            category = "Travel & Daily Life",
            categoryBengali = "দৈনন্দিন ও সফর",
            waqtCategory = WaqtCategory.DAILY_LIFE,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "টয়লেটে প্রবেশের দু'আ (শয়তান থেকে হিফাযত)",
            titleEnglish = "Entering the Bathroom / Toilet",
            textArabic = "بِسْمِ اللَّهِ، اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْخُبُثِ وَالْخَبَائِثِ",
            transliteration = "Bismillah, Allahumma innee a'oodhu bika minal-khubuthi wal-khaba'ith.",
            transliterationBengali = "বিসমিল্লা-হ, আল্লা-হুম্মা ইন্নী আ‘ঊযু বিকা মিনাল খুবুছি ওয়াল খাবা-য়িস।",
            meaningBengali = "আল্লাহর নামে শুরু করছি। হে আল্লাহ! আমি আপনার নিকট পুরুষ ও নারী শয়তানের অনিষ্ট থেকে আশ্রয় প্রার্থনা করছি।",
            meaningEnglish = "In the Name of Allah. O Allah, I seek refuge in You from the male and female foul spirits (demons).",
            reference = "সহীহ বুখারী: ১৪২, সহীহ মুসলিম: ৩৭৫",
            virtuesBengali = "বাম পা দিয়ে প্রবেশের পূর্বে পাঠ করা সুন্নাত; এটি বান্দা ও শয়তানের দৃষ্টির মাঝে নিরাপত্তা পর্দা স্বরূপ কাজ করে।",
            repetitions = 1
        ),
        Dua(
            id = "dua_toilet_leave",
            category = "Travel & Daily Life",
            categoryBengali = "দৈনন্দিন ও সফর",
            waqtCategory = WaqtCategory.DAILY_LIFE,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "টয়লেট থেকে বের হওয়ার দু'আ (মাগফিরাত)",
            titleEnglish = "Leaving the Bathroom / Toilet",
            textArabic = "غُفْرَانَكَ",
            transliteration = "Ghufranak.",
            transliterationBengali = "গুফরা-নাক।",
            meaningBengali = "হে আল্লাহ! আমি আপনার নিকট ক্ষমা প্রার্থনা করছি।",
            meaningEnglish = "I ask You for Your forgiveness.",
            reference = "সুনানে আবু দাউদ: ৩০, সুনানে তিরমিযী: ৭ (সহীহ)",
            virtuesBengali = "ডান পা দিয়ে বের হওয়ার সময় পাঠ করা সুন্নাত। প্রাকৃতিক প্রয়োজন দূর করে শরীর হালকা করায় বান্দা আত্মিক গুনাহমুক্তির আবেদন জানায়।",
            repetitions = 1
        ),
        Dua(
            id = "dua_wearing_clothes",
            category = "Travel & Daily Life",
            categoryBengali = "দৈনন্দিন ও সফর",
            waqtCategory = WaqtCategory.DAILY_LIFE,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "পোশাক পরিধানের দু'আ (পূর্বের গুনাহ ক্ষমা)",
            titleEnglish = "Wearing Clothes",
            textArabic = "الْحَمْدُ لِلَّهِ الَّذِي كَسَانِي هَـٰذَا الثَّوْبَ وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ",
            transliteration = "Alhamdu lillahilladhee kasanee hadhath-thawba wa razaqaneehi min ghayri hawlim-minnee wa la quwwah.",
            transliterationBengali = "আলহামদু লিল্লা-হিল্লাযী কাসা-নী হা-যাস সাওবা ওয়া রযাক্বানীহি মিন গয়রি হাওলিম মিন্নী ওয়ালা- ক্বুউওয়াহ।",
            meaningBengali = "সমস্ত প্রশংসা আল্লাহর জন্য, যিনি আমাকে এই পোশাক পরিধান করিয়েছেন এবং আমার কোনো চেষ্টা ও সামর্থ্য ছাড়াই আমাকে তা দান করেছেন।",
            meaningEnglish = "All praise is for Allah Who has clothed me with this garment and provided it for me without any power or strength from myself.",
            reference = "সুনানে আবু দাউদ: ৪০২৩, তিরমিযী: ৩৪৫৮ (সহীহ)",
            virtuesBengali = "রাসুলুল্লাহ ﷺ বলেছেন: যে ব্যক্তি নতুন বা যেকোনো পোশাক পরিধানের সময় এ দু'আ পাঠ করবে, তার পূর্ববর্তী সকল গুনাহ ক্ষমা করে দেওয়া হয়।",
            repetitions = 1
        ),

        // ---------------------------------------------------------------------
        // 3. ENTERING & LEAVING HOME (ঘরে প্রবেশ ও বের হওয়া)
        // ---------------------------------------------------------------------
        Dua(
            id = "dua_leaving_home",
            category = "Travel & Daily Life",
            categoryBengali = "দৈনন্দিন ও সফর",
            waqtCategory = WaqtCategory.DAILY_LIFE,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "ঘর থেকে বের হওয়ার শক্তিশালী সুরক্ষাকবচ",
            titleEnglish = "Supplication Upon Leaving the House",
            textArabic = "بِسْمِ اللَّهِ تَوَكَّلْتُ عَلَى اللَّهِ، لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            transliteration = "Bismillahi tawakkaltu 'alallah, la hawla wa la quwwata illa billah.",
            transliterationBengali = "বিসমিল্লা-হি তাওয়াক্কালতু ‘আলাল্লা-হ, লা- হাওলা ওয়ালা- ক্বুউওয়াতা ইল্লা- বিল্লা-হ।",
            meaningBengali = "আল্লাহর নামে বের হচ্ছি, আমি আল্লাহর ওপর ভরসা করলাম। আল্লাহর সাহায্য ব্যতীত কোনো অনিষ্ট থেকে বাঁচার উপায় নেই এবং কোনো কল্যাণ অর্জনের শক্তি নেই।",
            meaningEnglish = "In the Name of Allah, I trust in Allah; there is no power and no strength except with Allah.",
            reference = "সুনানে আবু দাউদ: ৫০৯৫, সুনানে তিরমিযী: ৩৪২৬ (সহীহ)",
            virtuesBengali = "ঘর থেকে বের হওয়ার সময় এ দু'আ পাঠ করলে অদৃশ্য থেকে ঘোষণা দেওয়া হয়: 'তোমাকে হেদায়েত দেওয়া হলো, তোমার জন্য আল্লাহই যথেষ্ট হলেন এবং তোমাকে নিরাপত্তা দেওয়া হলো!' শয়তান তখন অন্য শয়তানকে বলে: যার পথপ্রদর্শন, দায়িত্ব ও নিরাপত্তা স্বয়ং আল্লাহ গ্রহণ করেছেন তার সাথে তুমি কী করতে পারবে?!",
            repetitions = 1
        ),
        Dua(
            id = "dua_entering_home",
            category = "Travel & Daily Life",
            categoryBengali = "দৈনন্দিন ও সফর",
            waqtCategory = WaqtCategory.DAILY_LIFE,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "ঘরে প্রবেশের দু'আ ও সালাম",
            titleEnglish = "Entering the House",
            textArabic = "بِسْمِ اللَّهِ وَلَجْنَا، وَبِسْمِ اللَّهِ خَرَجْنَا، وَعَلَىٰ رَبِّنَا تَوَكَّلْنَا (ثُمَّ يُسَلِّمُ عَلَىٰ أَهْلِهِ)",
            transliteration = "Bismillahi walajna, wa bismillahi kharajna, wa 'ala Rabbina tawakkalna (then greet family with Salam).",
            transliterationBengali = "বিসমিল্লা-হি ওয়ালাজনা-, ওয়া বিসমিল্লা-হি খরাজনা-, ওয়া ‘আলা- রব্বিনা- তাওয়াক্কালনা- (অতঃপর ঘরে সালাম প্রদান করুন)।",
            meaningBengali = "আল্লাহর নামে আমরা প্রবেশ করলাম, আল্লাহর নামেই আমরা বের হয়েছিলাম এবং আমাদের প্রতিপালকের ওপরই আমরা ভরসা করলাম।",
            meaningEnglish = "In the Name of Allah we enter, and in the Name of Allah we leave, and upon our Lord we depend (then extend Salam to family).",
            reference = "সুনানে আবু দাউদ: ৫০৯৬",
            virtuesBengali = "ঘরে প্রবেশের সময় আল্লাহর নাম নিলে ও সালাম দিলে শয়তান হতাশ হয়ে তার সঙ্গীদের বলে: 'আজ রাতে তোমাদের এ ঘরে থাকারও জায়গা নেই, রাতের খাবার খাওয়ারও সুযোগ নেই!'",
            repetitions = 1
        ),

        // ---------------------------------------------------------------------
        // 4. FOOD & DRINK (আহার ও পানীয়)
        // ---------------------------------------------------------------------
        Dua(
            id = "dua_before_eating",
            category = "Travel & Daily Life",
            categoryBengali = "দৈনন্দিন ও সফর",
            waqtCategory = WaqtCategory.DAILY_LIFE,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "খাওয়ার শুরুতে ও শুরুতে ভুলে গেলে দু'আ",
            titleEnglish = "Before Eating & If Forgotten at Start",
            textArabic = "بِسْمِ اللَّهِ (فَإِنْ نَسِيَ فَلْيَقُلْ: بِسْمِ اللَّهِ أَوَّلَهُ وَآخِرَهُ)",
            transliteration = "Bismillah (If forgotten: Bismillahi awwalahu wa akhirahu).",
            transliterationBengali = "বিসমিল্লা-হ (শুরুতে ভুলে গেলে বলবে: বিসমিল্লা-হি আউওয়ালাহূ ওয়া আ-খিরাহূ)।",
            meaningBengali = "আল্লাহর নামে শুরু করছি। (শুরুতে ভুলে গেলে: আল্লাহর নামে এর শুরুতে ও শেষে)।",
            meaningEnglish = "In the Name of Allah (If forgotten at start: In the Name of Allah at its beginning and end).",
            reference = "সুনানে আবু দাউদ: ৩৭৬৭, সুনানে তিরমিযী: ১৮৫৮ (সহীহ)",
            virtuesBengali = "খাবারের শুরুতে বিসমিল্লাহ না বললে শয়তান সেই খাবারের বরকত নষ্ট করে ও তাতে অংশীদার হয়। শুরুতে ভুলে মাঝপথে এ বাক্যটি বললে শয়তান যেটুকু খেয়েছে তা উগরে দিতে বাধ্য হয়।",
            repetitions = 1
        ),
        Dua(
            id = "dua_after_eating",
            category = "Travel & Daily Life",
            categoryBengali = "দৈনন্দিন ও সফর",
            waqtCategory = WaqtCategory.DAILY_LIFE,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "খাবার শেষ করার পর শোকরিয়া দু'আ",
            titleEnglish = "After Finishing a Meal",
            textArabic = "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنَا وَسَقَانَا وَجَعَلَنَا مُسْلِمِينَ",
            transliteration = "Alhamdu lillahilladhee at'amana wa saqana wa ja'alana muslimeen.",
            transliterationBengali = "আলহামদু লিল্লা-হিল্লাযী আত্‘আমানা- ওয়া সাক্বা-না- ওয়াজা‘আলানা- মুসলিমীন।",
            meaningBengali = "সমস্ত প্রশংসা আল্লাহর জন্য, যিনি আমাদেরকে আহার করালেন, পান করালেন এবং আমাদেরকে মুসলিম বানিয়েছেন।",
            meaningEnglish = "All praise is for Allah Who fed us and quenched our thirst and made us Muslims.",
            reference = "সুনানে আবু দাউদ: ৩৮৫০, সুনানে তিরমিযী: ১৮৪৭",
            virtuesBengali = "আল্লাহ তাঁর সেই বান্দার প্রতি অত্যন্ত সন্তুষ্ট হন, যে প্রতিটি লোকমা খাবার গ্রহণ ও পানি পানের পর আল্লাহর শুকরিয়া আদায় করে।",
            repetitions = 1
        ),
        Dua(
            id = "dua_guest_host_blessing",
            category = "Travel & Daily Life",
            categoryBengali = "দৈনন্দিন ও সফর",
            waqtCategory = WaqtCategory.DAILY_LIFE,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "মেহমানদারীর খাবার খেয়ে মেজবানের জন্য দু'আ",
            titleEnglish = "Supplication of Guest for the Host",
            textArabic = "اللَّهُمَّ بَارِكْ لَهُمْ فِيمَا رَزَقْتَهُمْ، وَاغْفِرْ لَهُمْ وَارْحَمْهُمْ",
            transliteration = "Allahumma barik lahum feema razaqtahum, wagh-fir lahum warhamhum.",
            transliterationBengali = "আল্লা-হুম্মা বা-রিক লাহুম ফীমা- রযাক্বতাহুম, ওয়াগফির লাহুম ওয়ারহামহুম।",
            meaningBengali = "হে আল্লাহ! আপনি তাদেরকে যে জীবিকা দান করেছেন তাতে বরকত দিন, তাদেরকে ক্ষমা করুন এবং তাদের প্রতি দয়া করুন।",
            meaningEnglish = "O Allah, bless for them what You have provided them, and forgive them and have mercy on them.",
            reference = "সহীহ মুসলিম: ২০৪২",
            virtuesBengali = "রাসুলুল্লাহ ﷺ কারো বাড়িতে দাওয়াত খেলে মেজবানের বরকত ও মাগফিরাতের জন্য এই মন জুড়ানো আন্তরিক দু'আ করতেন।",
            repetitions = 1
        ),

        // ---------------------------------------------------------------------
        // 5. TRAVEL & MARKETPLACE (সফর ও বাজার)
        // ---------------------------------------------------------------------
        Dua(
            id = "dua_riding_vehicle",
            category = "Travel & Daily Life",
            categoryBengali = "দৈনন্দিন ও সফর",
            waqtCategory = WaqtCategory.DAILY_LIFE,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "যানবাহন ও সওয়ারিতে ওঠার দু'আ",
            titleEnglish = "Mounting a Vehicle / Boarding Transport",
            textArabic = "بِسْمِ اللَّهِ، الْحَمْدُ لِلَّهِ، سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَـٰذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ، وَإِنَّا إِلَىٰ رَبِّنَا لَمُنْقَلِبُونَ",
            transliteration = "Bismillah, alhamdulillah, Subhanalladhee sakh-khara lana hadha wa ma kunna lahu muqrineen, wa inna ila Rabbina lamunqaliboon.",
            transliterationBengali = "বিসমিল্লা-হ, আলহামদু লিল্লা-হ, সুবহা-নাল্লাযী সাখ্খরা লানা- হা-যা- ওয়ামা- কুন্না- লাহূ মুক্বরিনীন, ওয়া ইন্না- ইলা- রব্বিনা- lamunqoliboon।",
            meaningBengali = "আল্লাহর নামে, সমস্ত প্রশংসা আল্লাহর। পবিত্র সেই মহান সত্তা যিনি একে আমাদের বশীভূত করে দিয়েছেন, অথচ আমরা একে বশীভূত করতে সক্ষম ছিলাম না; আর নিশ্চয়ই আমরা আমাদের প্রতিপালকের নিকটেই প্রত্যাবর্তন করব।",
            meaningEnglish = "In the Name of Allah, all praise is for Allah. Glory is to Him Who has subjected this to us, and we were not able to tame it by ourselves, and indeed to our Lord we will return.",
            reference = "সূরা আয-যুখরুফ: ১৩-১৪, সহীহ মুসলিম: ১৩৪২, সুনানে আবু দাউদ: ২৫৯৯",
            virtuesBengali = "গাড়ি, বিমান, ট্রেন বা যেকোনো বাহনে চড়ে এ দু'আ পাঠ করলে নিরাপদ ভ্রমণ নিশ্চিত হয় এবং দুর্ঘটনা থেকে হেফাজত থাকে।",
            repetitions = 1
        ),
        Dua(
            id = "dua_travel_journey_full",
            category = "Travel & Daily Life",
            categoryBengali = "দৈনন্দিন ও সফর",
            waqtCategory = WaqtCategory.DAILY_LIFE,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "সফরের পূর্ণাঙ্গ মাসনুন দু'আ",
            titleEnglish = "Complete Supplication for Traveling (Safar)",
            textArabic = "اللَّهُمَّ إِنَّا نَسْأَلُكَ فِي سَفَرِنَا هَـٰذَا الْبِرَّ وَالتَّقْوَىٰ، وَمِنَ الْعَمَلِ مَا تَرْضَىٰ، اللَّهُمَّ هَوِّنْ عَلَيْنَا سَفَرَنَا هَـٰذَا وَاطْوِ عَنَّا بُعْدَهُ، اللَّهُمَّ أَنْتَ الصَّاحِبُ فِي السَّفَرِ وَالْخَلِيفَةُ فِي الْأَهْلِ، اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنْ وَعْثَاءِ السَّفَرِ وَكَآبَةِ الْمَنْظَرِ وَسُوءِ الْمُنْقَلَبِ فِي الْمَالِ وَالْأَهْلِ",
            transliteration = "Allahumma inna nas-aluka fee safarina hadhal-birra wat-taqwa, wa minal-'amali ma tarda. Allahumma hawwin 'alayna safarana hadha watwi 'anna bu'dah. Allahumma Antas-sahibu fis-safar, wal-khaleefatu fil-ahl. Allahumma innee a'oodhu bika min wa'tha'is-safar, wa ka'abatil-manzar, wa soo'il-munqalabi fil-mali wal-ahl.",
            transliterationBengali = "আল্লা-হুম্মা ইন্না- নাস-আলুকা ফী সাফারিনা- হা-যাল বিররা ওয়াত তাক্বওয়া-, ওয়া মিনাল ‘আমালি মা- তারদ্বা-। আল্লা-হুম্মা হাউউইন ‘আলাইনা- সাফারানা- হা-যা- ওয়াত্ববি ‘আন্না- বু‘দাহূ। আল্লা-হুম্মা আনতাস স্বা-হিবু ফিস সাফারি ওয়াল খালীফাতু ফিল আহল। আল্লা-হুম্মা ইন্নী আ‘ঊযু বিকা মিন ওয়া‘ছা-য়িস সাফারি ওয়া কাআ-বাতিল মানযারি ওয়া সূইল মুনক্বালাবি ফিল মা-লি ওয়াল আহল।",
            meaningBengali = "হে আল্লাহ! আমাদের এই সফরে আপনার কাছে কল্যাণ ও তাকওয়া প্রার্থনা করছি এবং এমন আমল প্রার্থনা করছি যা আপনি পছন্দ করেন। হে আল্লাহ! আমাদের এই সফরকে আমাদের জন্য সহজ করে দিন এবং এর দূরত্ব সংক্ষেপ করে দিন। হে আল্লাহ! আপনিই সফরে একমাত্র সঙ্গী এবং পরিবার-পরিজনের সর্বোত্তম অভিভাবক। হে আল্লাহ! আমি আপনার আশ্রয় চাই সফরের কষ্টক্লেশ থেকে, খারাপ ও মর্মস্পর্শী দৃশ্য থেকে এবং ধন-সম্পদ ও পরিবারে কোনো অকল্যাণকর পরিবর্তন থেকে।",
            meaningEnglish = "O Allah, we ask of You on this journey of ours righteousness and piety, and deeds that please You. O Allah, lighten this journey for us and shorten its distance. O Allah, You are the Companion on the journey and the Caretaker of the family. O Allah, I seek refuge in You from the hardships of traveling, from gloomy sights and from an evil return concerning wealth and family.",
            reference = "সহীহ মুসলিম: ১৩৪২",
            virtuesBengali = "সফরের শুরুতেই এ দু'আ পাঠ করলে আল্লাহ তায়ালা পুরো সফরটিকে হেফাজতে রাখেন এবং সফরকারীর পেছনে রেখে যাওয়া পরিবার ও সম্পদের জিম্মাদারী গ্রহণ করেন।",
            repetitions = 1
        ),
        Dua(
            id = "dua_entering_marketplace",
            category = "Travel & Daily Life",
            categoryBengali = "দৈনন্দিন ও সফর",
            waqtCategory = WaqtCategory.DAILY_LIFE,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "বাজারে প্রবেশের দু'আ (১০ লক্ষ নেকী ও পাপমুক্তি)",
            titleEnglish = "Entering the Market / Bazaar (1 Million Good Deeds)",
            textArabic = "لَا إِلَـٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، يُحْيِي وَيُمِيتُ وَهُوَ حَيٌّ لَا يَمُوتُ، بِيَدِهِ الْخَيْرُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
            transliteration = "La ilaha illallahu wahdahu la shareeka lahu, lahul-mulku wa lahul-hamdu, yuhyee wa yumeetu wa Huwa hayyun la yamoot, biyadihil-khayru wa Huwa 'ala kulli shay'in Qadeer.",
            transliterationBengali = "লা- ইলা-হা ইল্লাল্লা-হু ওয়াহদাহূ লা- শারীকা লাহূ, লাহুল মুলকু ওয়া লাহুল হামদু, ইউহ্यी ওয়া ইউমীতু ওয়াহুওয়া হাইয়্যুল লা- ইয়ামূতু, বিয়াদিহিল খয়রু ওয়াহুওয়া ‘আলা- কুল্লি শাইয়িন ক্বদীর।",
            meaningBengali = "আল্লাহ ছাড়া কোনো সত্য উপাস্য নেই, তিনি এক, তাঁর কোনো শরিক নেই। রাজত্ব একমাত্র তাঁরই এবং সমস্ত প্রশংসাও তাঁরই। তিনিই জীবন দান করেন ও মৃত্যু ঘটান, আর তিনি চিরঞ্জীব যিনি কখনো মৃত্যুবরণ করবেন না। সমস্ত কল্যাণ তাঁরই হাতে এবং তিনি সবকিছুর ওপর সর্বশক্তিমান।",
            meaningEnglish = "None has the right to be worshipped except Allah alone, without partner. To Him belongs all sovereignty and praise; He gives life and causes death, and He is Ever-Living and does not die. In His Hand is all good, and He is over all things omnipotent.",
            reference = "সুনানে তিরমিযী: ৩৪২৮, সুনানে ইবনে মাজাহ: ২২৩৫ (হাসান)",
            virtuesBengali = "রাসুলুল্লাহ ﷺ বলেছেন: যে ব্যক্তি বাজারে প্রবেশ করার সময় এই বাক্যটি পাঠ করবে, আল্লাহ তার আমলনামায় দশ লক্ষ নেকী লিপিবদ্ধ করবেন, তার দশ লক্ষ পাপ ক্ষমা করে দেবেন এবং তার মর্যাদাকে দশ লক্ষ গুণ বৃদ্ধি করবেন!",
            repetitions = 1
        ),

        // ---------------------------------------------------------------------
        // 6. DEBT, ANXIETY & HARDSHIP (ঋণমুক্তি, দুশ্চিন্তা ও বিপদ)
        // ---------------------------------------------------------------------
        Dua(
            id = "dua_debt_relief_mount_uhud",
            category = "Distress & Trials",
            categoryBengali = "বিপদ ও ঋণমুক্তি",
            waqtCategory = WaqtCategory.DISTRESS_TRIALS,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "পাহাড় পরিমাণ ঋণ পরিশোধের অলৌকিক দু'আ",
            titleEnglish = "Freedom from Crushing Debts",
            textArabic = "اللَّهُمَّ اكْفِنِي بِحَلَالِكَ عَنْ حَرَامِكَ، وَأَغْنِنِي بِفَضْلِكَ عَمَّنْ سِوَاكَ",
            transliteration = "Allahummak-finee bi-halalika 'an haramik, wa aghninee bi-fadlika 'amman siwak.",
            transliterationBengali = "আল্লা-হুম্মাকফিনী বিহালা-লিকা ‘আন হারা-মিক, ওয়া আগনিনী বিফাদ্বলিকা ‘আম্মান সিওয়া-ক।",
            meaningBengali = "হে আল্লাহ! হারামের পথ পরিহার করে আপনার হালাল রিজিকই যেন আমার জন্য যথেষ্ট হয় এবং আপনার বিশেষ অনুগ্রহ দিয়ে আপনি ব্যতীত অন্য সবার মুখাপেক্ষিতা থেকে আমাকে মুক্ত ও অভাবহীন করে দিন।",
            meaningEnglish = "O Allah, suffice me with what You have permitted so that I need not that which You have forbidden, and enrich me with Your bounty so that I have no need for anyone besides You.",
            reference = "সুনানে তিরমিযী: ৩৫৬৩, মুসনাদে আহমাদ: ১৩২১ (হাসান সহীহ)",
            virtuesBengali = "হযরত আলী (রাঃ) এক ঋণগ্রস্ত ব্যক্তিকে বলেছিলেন: আমি কি তোমাকে এমন একটি দু'আ শিখিয়ে দেব না যা রাসুলুল্লাহ ﷺ আমাকে শিখিয়েছিলেন? যদি তোমার ওপর উহুদ পাহাড় পরিমাণ ঋণের বোঝাও থাকে, তবুও আল্লাহ তোমার পক্ষ থেকে তা পরিশোধের ব্যবস্থা করে দেবেন!",
            repetitions = 3
        ),
        Dua(
            id = "dua_anxiety_grief_laziness",
            category = "Distress & Trials",
            categoryBengali = "বিপদ ও ঋণমুক্তি",
            waqtCategory = WaqtCategory.DISTRESS_TRIALS,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "দুশ্চিন্তা, পেরেশানি, অলসতা ও ঋণের চাপ দূর করার দু'আ",
            titleEnglish = "Protection from Anxiety, Sorrow, Inability & Debts",
            textArabic = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَالْبُخْلِ وَالْجُبْنِ، وَضَلَعِ الدَّيْنِ وَغَلَبَةِ الرِّجَالِ",
            transliteration = "Allahumma innee a'oodhu bika minal-hammi wal-hazan, wal-'ajzi wal-kasal, wal-bukhli wal-jubn, wa dala'id-dayni wa ghalabatir-rijal.",
            transliterationBengali = "আল্লা-হুম্মা ইন্নী আ‘ঊযু বিকা মিনাল হাম্মি ওয়াল হাযান, ওয়াল ‘আজযি ওয়াল কাসাল, ওয়াল বুখলি ওয়াল জুবন, ওয়া দ্বালা‘ইদ দাইনি ওয়া গলাবাতির রিজা-ল।",
            meaningBengali = "হে আল্লাহ! আমি আপনার আশ্রয় নিচ্ছি দুশ্চিন্তা ও মনোকষ্ট থেকে, অক্ষমতা ও অলসতা থেকে, কৃপণতা ও কাপুরুষতা থেকে, ঋণের তীব্র বোঝা থেকে এবং মানুষের অন্যায় আধিপত্য ও জুলুম থেকে।",
            meaningEnglish = "O Allah, I seek refuge in You from anxiety and sorrow, from inability and laziness, from miserliness and cowardice, from the burden of debt and from the dominance of men.",
            reference = "সহীহ বুখারী: ২৮৯৩, সহীহ মুসলিম: ২৭০৬",
            virtuesBengali = "হযরত আনাস (রাঃ) বলেন: আমি রাসুলুল্লাহ ﷺ এর সর্বাধিক উচ্চারিত প্রার্থনা হিসেবে এই দু'আটি শুনতাম। মন খারাপ, অলসতা ও ঋণের ভার দূর করার অদ্বিতীয় প্রতিষেধক।",
            repetitions = 1
        ),
        Dua(
            id = "dua_calamity_inna_lillah",
            category = "Distress & Trials",
            categoryBengali = "বিপদ ও ঋণমুক্তি",
            waqtCategory = WaqtCategory.DISTRESS_TRIALS,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "বিপদ বা ক্ষতির সম্মুখীন হলে পাঠের দু'আ",
            titleEnglish = "When Struck by a Calamity or Loss",
            textArabic = "إِنَّا لِلَّهِ وَإِنَّا إِلَيْهِ رَاجِعُونَ، اللَّهُمَّ أْجُرْنِي فِي مُصِيبَتِي وَأَخْلِفْ لِي خَيْرًا مِنْهَا",
            transliteration = "Inna lillahi wa inna ilayhi raji'oon, Allahumma-jurnee fee museebatee wa akhlif lee khayram-minha.",
            transliterationBengali = "ইন্না- লিল্লা-হি ওয়া ইন্না- ইলাইহি রাজি‘ঊন, আল্লা-হুম্মা’জুরনী ফী মুসীবাতী ওয়া আখলিফ লী খয়রাম মিনহা-।",
            meaningBengali = "নিশ্চয়ই আমরা আল্লাহরই জন্য এবং নিশ্চিতভাবে আমরা তাঁরই নিকট প্রত্যাবর্তনকারী। হে আল্লাহ! আমার এই বিপদে আমাকে প্রতিদান দিন এবং এর বিনিময়ে আমাকে এর চেয়েও উত্তম কিছু দান করুন।",
            meaningEnglish = "Indeed to Allah we belong and indeed to Him we return. O Allah, reward me in my affliction and grant me in its place that which is better.",
            reference = "সহীহ মুসলিম: ৯১৮",
            virtuesBengali = "উম্মে সালামাহ (রাঃ) বলেন: আবু সালামার মৃত্যুর পর আমি রাসুলুল্লাহ ﷺ এর শেখানো এই দু'আ পাঠ করেছিলাম। ফলে আল্লাহ আবু সালামার চেয়েও উত্তম ব্যক্তি—স্বয়ং বিশ্বনবী মুহাম্মাদুর রাসুলুল্লাহ ﷺ-এর সাথে আমার বিবাহের ব্যবস্থা করে দিলেন!",
            repetitions = 1
        ),
        Dua(
            id = "dua_sneezing_etiquette",
            category = "Travel & Daily Life",
            categoryBengali = "দৈনন্দিন ও সফর",
            waqtCategory = WaqtCategory.DAILY_LIFE,
            hisnulPillar = HisnulPillar.DAILY_LIFE,
            titleBengali = "হাঁচি দিলে ও হাঁচির উত্তর দেওয়ার সুন্নাত দু'আ",
            titleEnglish = "Etiquette & Supplications for Sneezing",
            textArabic = "الْعَاطِسُ: الْحَمْدُ لِلَّهِ\nالسَّامِعُ: يَرْحَمُكَ اللَّهُ\nرَدُّ الْعَاطِسِ: يَهْدِيكُمُ اللَّهُ وَيُصْلِحُ بَالَكُمْ",
            transliteration = "Sneezer: Alhamdu lillah. Listener: Yarhamukallah. Sneezer replies: Yahdeekumullahu wa yuslihu balakum.",
            transliterationBengali = "হাঁচিদাতা বলবে: আলহামদু লিল্লা-হ। শ্রবণকারী বলবে: ইয়ারহামুকাল্লা-হ। জবাবে হাঁচিদাতা বলবে: ইয়াহদীকুমুল্লা-হু ওয়া ইউসলিহু বা-লাকুম।",
            meaningBengali = "হাঁচিদাতা: সমস্ত প্রশংসা আল্লাহর। শ্রবণকারী: আল্লাহ আপনার প্রতি দয়া করুন। হাঁচিদাতা পুনরায়: আল্লাহ আপনাকে সঠিক পথ দেখান এবং আপনার অবস্থা সংশোধন করে দিন।",
            meaningEnglish = "Sneezer: All praise is for Allah. Listener: May Allah have mercy on you. Sneezer: May Allah guide you and rectify your condition.",
            reference = "সহীহ বুখারী: ৬২২৪",
            virtuesBengali = "মুসলমানের ওপর অন্য মুসলমানের যে ছয়টি বিশেষ হক রয়েছে, তার মধ্যে হাঁচির জবাব দেওয়া অন্যতম ফরজ বা ওয়াজিব দায়িত্ব।",
            repetitions = 1
        )
    )
}
