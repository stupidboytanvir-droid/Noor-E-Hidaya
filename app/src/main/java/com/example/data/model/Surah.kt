package com.example.data.model

data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val nameBengali: String,
    val translationEnglish: String,
    val translationBengali: String,
    val numberOfAyahs: Int,
    val revelationType: String // "Meccan" or "Medinan"
)

data class Ayah(
    val surahNumber: Int,
    val ayahNumber: Int,
    val textArabic: String,
    val transliteration: String,
    val transliterationBengali: String = "",
    val translationBengali: String,
    val translationEnglish: String,
    val audioUrl: String
)

object QuranData {

    val SURAHS: List<Surah> = listOf(
        Surah(1, "الفَاتِحة", "Al-Fatihah", "আল-ফাতিহা", "The Opening", "সূচনা", 7, "Meccan"),
        Surah(2, "البَقَرَة", "Al-Baqarah", "আল-বাক্বারাহ", "The Cow", "গাভী", 286, "Medinan"),
        Surah(3, "آل عِمرَان", "Ali 'Imran", "আলে ইমরান", "Family of Imran", "ইমরানের পরিবার", 200, "Medinan"),
        Surah(4, "النِّسَاء", "An-Nisa", "আন-নিসা", "The Women", "নারী", 176, "Medinan"),
        Surah(5, "المَائدة", "Al-Ma'idah", "আল-মায়েদা", "The Table Spread", "খাদ্য পরিবেশিত টেবিল", 120, "Medinan"),
        Surah(6, "الأنعَام", "Al-An'am", "আল-আনআম", "The Cattle", "গৃহপালিত পশু", 165, "Meccan"),
        Surah(7, "الأعرَاف", "Al-A'raf", "আল-আরাফ", "The Heights", "উঁচু স্থানসমূহ", 206, "Meccan"),
        Surah(8, "الأنفَال", "Al-Anfal", "আল-আনফাল", "The Spoils of War", "যুদ্ধলব্ধ ধনসম্পদ", 75, "Medinan"),
        Surah(9, "التَّوبَة", "At-Tawbah", "আত-তাওবাহ", "The Repentance", "অনুশোচনা", 129, "Medinan"),
        Surah(10, "يُونُس", "Yunus", "ইউনুস", "Jonah", "ইউনুস (আঃ)", 109, "Meccan"),
        Surah(11, "هُود", "Hud", "হুদ", "Hud", "হুদ (আঃ)", 123, "Meccan"),
        Surah(12, "يُوسُف", "Yusuf", "ইউসুফ", "Joseph", "ইউসুফ (আঃ)", 111, "Meccan"),
        Surah(13, "الرَّعْد", "Ar-Ra'd", "আর-রাদ", "The Thunder", "বজ্রনাদ", 43, "Medinan"),
        Surah(14, "إبرَاهِيم", "Ibrahim", "ইব্রাহিম", "Abraham", "ইব্রাহিম (আঃ)", 52, "Meccan"),
        Surah(15, "الحِجْر", "Al-Hijr", "আল-হিজর", "The Rocky Tract", "পাথুরে পাহাড়", 99, "Meccan"),
        Surah(16, "النَّحْل", "An-Nahl", "আন-নাহল", "The Bee", "মৌমাছি", 128, "Meccan"),
        Surah(17, "الإسرَاء", "Al-Isra", "আল-ইসরা", "The Night Journey", "নৈশ ভ্রমণ", 111, "Meccan"),
        Surah(18, "الكَهْف", "Al-Kahf", "আল-কাহফ", "The Cave", "গুহা", 110, "Meccan"),
        Surah(19, "مَريَم", "Maryam", "মারইয়াম", "Mary", "মারইয়াম (আঃ)", 98, "Meccan"),
        Surah(20, "طه", "Ta-Ha", "ত্বা-হা", "Ta-Ha", "ত্বা-হা", 135, "Meccan"),
        Surah(21, "الأنبيَاء", "Al-Anbiya", "আল-আম্বিয়া", "The Prophets", "নবীগণ", 112, "Meccan"),
        Surah(22, "الحَجّ", "Al-Hajj", "আল-হাজ্জ", "The Pilgrimage", "হজ্জ", 78, "Medinan"),
        Surah(23, "المُؤمِنُون", "Al-Mu'minun", "আল-মুমিনুন", "The Believers", "মুমিনগণ", 118, "Meccan"),
        Surah(24, "النُّور", "An-Nur", "আন-নূর", "The Light", "আলো", 64, "Medinan"),
        Surah(25, "الفُرقَان", "Al-Furqan", "আল-ফুরকান", "The Criterion", "সত্য-মিথ্যার মানদণ্ড", 77, "Meccan"),
        Surah(26, "الشُّعَرَاء", "Ash-Shu'ara", "আশ-শুয়ারা", "The Poets", "কবিগণ", 227, "Meccan"),
        Surah(27, "النَّمْل", "An-Naml", "আন-নামল", "The Ant", "পিপীলিকা", 93, "Meccan"),
        Surah(28, "القَصَص", "Al-Qasas", "আল-কাসাস", "The Stories", "কাহিনী", 88, "Meccan"),
        Surah(29, "العَنكبُوت", "Al-Ankabut", "আল-আনকাবুত", "The Spider", "মাকড়শা", 69, "Meccan"),
        Surah(30, "الرُّوم", "Ar-Rum", "আর-রূম", "The Romans", "রোমান জাতি", 60, "Meccan"),
        Surah(31, "لُقمَان", "Luqman", "লুকমান", "Luqman", "লুকমান", 34, "Meccan"),
        Surah(32, "السَّجدَة", "As-Sajdah", "আস-সাজদাহ", "The Prostration", "সিজদা", 30, "Meccan"),
        Surah(33, "الأحزَاب", "Al-Ahzab", "আল-আহযাব", "The Combined Forces", "জোটবদ্ধ বাহিনী", 73, "Medinan"),
        Surah(34, "سَبَأ", "Saba", "সাবা", "Sheba", "সাবা জাতি", 54, "Meccan"),
        Surah(35, "فَاطِر", "Fatir", "ফাতির", "Originator", "সৃষ্টিকর্তা", 45, "Meccan"),
        Surah(36, "يس", "Ya-Sin", "ইয়া-সিন", "Ya-Sin", "ইয়া-সিন", 83, "Meccan"),
        Surah(37, "الصَّافَّات", "As-Saffat", "আস-সাফফাত", "Those who set the Ranks", "সারিবদ্ধ দল", 182, "Meccan"),
        Surah(38, "ص", "Sad", "সোয়াদ", "The Letter Sad", "সোয়াদ বর্ণ", 88, "Meccan"),
        Surah(39, "الزُّمَر", "Az-Zumar", "আজ-জুমার", "The Troops", "দলবদ্ধ জনতা", 75, "Meccan"),
        Surah(40, "غَافِر", "Ghafir", "গাফির", "The Forgiver", "ক্ষমাশীল", 85, "Meccan"),
        Surah(41, "فُصِّلَت", "Fussilat", "ফুসসিলাত", "Explained in Detail", "সুস্পষ্ট বিবরণ", 54, "Meccan"),
        Surah(42, "الشُّورَى", "Ash-Shura", "আশ-শুরা", "The Consultation", "পরামর্শ", 53, "Meccan"),
        Surah(43, "الزُّخْرُف", "Az-Zukhruf", "আজ-জু Ruf", "The Ornaments of Gold", "সোনার অলংকার", 89, "Meccan"),
        Surah(44, "الدُّخَان", "Ad-Dukhan", "আদ-দুখান", "The Smoke", "ধোঁয়া", 59, "Meccan"),
        Surah(45, "الجَاثيَة", "Al-Jathiyah", "আল-জাসিয়া", "The Crouching", "নতজানু", 37, "Meccan"),
        Surah(46, "الأحقَاف", "Al-Ahqaf", "আল-আহকাফ", "The Wind-Curved Sandhills", "বালুর পাহাড়", 35, "Meccan"),
        Surah(47, "مُحَمَّد", "Muhammad", "মুহাম্মদ", "Muhammad", "মুহাম্মদ (সাঃ)", 38, "Medinan"),
        Surah(48, "الفَتْح", "Al-Fath", "আল-ফাতহ", "The Victory", "বিজয়", 29, "Medinan"),
        Surah(49, "الحُجُرَات", "Al-Hujurat", "আল-হুজুরাত", "The Rooms", "কক্ষসমূহ", 18, "Medinan"),
        Surah(50, "ق", "Qaf", "ক্বাফ", "The Letter Qaf", "ক্বাফ বর্ণ", 45, "Meccan"),
        Surah(51, "الذَّاريَات", "Adh-Dhariyat", "আজ-যারিয়াত", "The Winnowing Winds", "বিক্ষেপকারী বাতাস", 60, "Meccan"),
        Surah(52, "الطُّور", "At-Tur", "আত-তুর", "The Mount", "তুর পাহাড়", 49, "Meccan"),
        Surah(53, "النَّجْم", "An-Najm", "আন-নাজম", "The Star", "নক্ষত্র", 62, "Meccan"),
        Surah(54, "القَمَر", "Al-Qamar", "আল-ক্বামার", "The Moon", "চাঁদ", 55, "Meccan"),
        Surah(55, "الرَّحْمَن", "Ar-Rahman", "আর-রহমান", "The Beneficent", "পরম দয়াময়", 78, "Medinan"),
        Surah(56, "الوَاقِعَة", "Al-Waqi'ah", "আল-ওয়াক্বিয়া", "The Inevitable", "নিশ্চিত ঘটনা", 96, "Meccan"),
        Surah(57, "الحَدِيد", "Al-Hadid", "আল-হাদিদ", "The Iron", "লোহা", 29, "Medinan"),
        Surah(58, "المُجَادلة", "Al-Mujadila", "আল-মুজাদালাহ", "The Pleading Woman", "বিতর্ককারিণী নারী", 22, "Medinan"),
        Surah(59, "الحَشْر", "Al-Hashr", "আল-হাশর", "The Exile", "সমাবেশ", 24, "Medinan"),
        Surah(60, "المُمتَحنَة", "Al-Mumtahanah", "আল-মুমতাহিনা", "The Examined One", "পরীক্ষিতা নারী", 13, "Medinan"),
        Surah(61, "الصَّفّ", "As-Saff", "আস-সাফ", "The Ranks", "সারিবদ্ধ সৈন্য", 14, "Medinan"),
        Surah(62, "الجُمُعَة", "Al-Jumu'ah", "আল-জুমুআহ", "Friday", "শুক্রবার", 11, "Medinan"),
        Surah(63, "المُنَافِقُون", "Al-Munafiqun", "আল-মুনাফিকুন", "The Hypocrites", "মুনাফিকগণ", 11, "Medinan"),
        Surah(64, "التَّغَابُن", "At-Taghabun", "আত-তাগাবুন", "Mutual Disillusion", "লাভ-ক্ষতির দিন", 18, "Medinan"),
        Surah(65, "الطَّلاَق", "At-Talaq", "আত-তালাক", "The Divorce", "তালাক", 12, "Medinan"),
        Surah(66, "التَّحْرِيم", "At-Tahrim", "আত-তাহরিম", "The Prohibition", "নিষিদ্ধকরণ", 12, "Medinan"),
        Surah(67, "المُلْك", "Al-Mulk", "আল-মুলক", "The Sovereignty", "সার্বভৌম কর্তৃত্ব", 30, "Meccan"),
        Surah(68, "القَلَم", "Al-Qalam", "আল-কলম", "The Pen", "কলম", 52, "Meccan"),
        Surah(69, "الحَاقَّة", "Al-Haqqah", "আল-হাক্কাহ", "The Inevitable Reality", "অনিবার্য সত্য", 52, "Meccan"),
        Surah(70, "المَعَارِج", "Al-Ma'arij", "আল-মাআরিজ", "The Ascending Stairways", "উন্নয়নের সোপান", 44, "Meccan"),
        Surah(71, "نُوح", "Nuh", "নূহ", "Noah", "নূহ (আঃ)", 28, "Meccan"),
        Surah(72, "الجِنّ", "Al-Jinn", "আল-জ্বিন", "The Jinn", "জিন জাতি", 28, "Meccan"),
        Surah(73, "المُزَّمِّل", "Al-Muzzammil", "আল-মুযযাম্মিল", "The Enshrouded One", "বস্ত্রাবৃত", 20, "Meccan"),
        Surah(74, "المُدَّثِّر", "Al-Muddaththir", "আল-মুদ্দাসসির", "The Cloaked One", "চাদরাবৃত", 56, "Meccan"),
        Surah(75, "القِيَامَة", "Al-Qiyamah", "আল-কিয়ামাহ", "The Resurrection", "পুনরুত্থান", 40, "Meccan"),
        Surah(76, "الإنسَان", "Al-Insan", "আল-ইনসান", "Man", "মানবজাতি", 31, "Medinan"),
        Surah(77, "المُرسَلاَت", "Al-Mursalat", "আল-মুরসালাত", "The Emissaries", "প্রেরিত বাতাস", 50, "Meccan"),
        Surah(78, "النَّبَأ", "An-Naba", "আন-নাবা", "The Great News", "মহাসংবাদ", 40, "Meccan"),
        Surah(79, "النَّازِعَات", "An-Nazi'at", "আন-নাযিআত", "Those who drag forth", "উৎপাটনকারী ফেরেশতা", 46, "Meccan"),
        Surah(80, "عَبَسَ", "'Abasa", "আবাসা", "He Frowned", "তিনি ভ্রূকুটি করলেন", 42, "Meccan"),
        Surah(81, "التَّكْوِير", "At-Takwir", "আত-তাকভীর", "The Overthrowing", "সূর্য নিষ্প্রভকরণ", 29, "Meccan"),
        Surah(82, "الانفِطَار", "Al-Infitar", "আল-ইনফিতার", "The Cleaving", "বিদীর্ণ হওয়া", 19, "Meccan"),
        Surah(83, "المطففين", "Al-Mutaffifin", "আল-মুতাফফিফিন", "Defrauding", "মাপে কম দানকারী", 36, "Meccan"),
        Surah(84, "الانشِقَاق", "Al-Inshiqaq", "আল-ইনশিক্বাক্ব", "The Splitting Open", "খন্ড-বিখন্ড হওয়া", 25, "Meccan"),
        Surah(85, "البُرُوج", "Al-Buruj", "আল-বুরূজ", "The Constellations", "নক্ষত্রপুঞ্জ", 22, "Meccan"),
        Surah(86, "الطَّارِق", "At-Tariq", "আত-তারিক", "The Nightcomer", "নৈশ আগমনকারী", 17, "Meccan"),
        Surah(87, "الأَعْلَى", "Al-A'la", "আল-আলা", "The Most High", "সর্বোচ্চ", 19, "Meccan"),
        Surah(88, "الغَاشِيَة", "Al-Ghashiyah", "আল-গাশিয়াহ", "The Overwhelming", "গ্রাসকারী বিপদ", 26, "Meccan"),
        Surah(89, "الفَجْر", "Al-Fajr", "আল-ফজর", "The Dawn", "ঊষা", 30, "Meccan"),
        Surah(90, "البَلَد", "Al-Balad", "আল-বালাদ", "The City", "নগরী", 20, "Meccan"),
        Surah(91, "الشَّمْس", "Ash-Shams", "আশ-শামস", "The Sun", "সূর্য", 15, "Meccan"),
        Surah(92, "اللَّيْل", "Al-Layl", "আল-লাইল", "The Night", "রাত", 21, "Meccan"),
        Surah(93, "الضُّحَى", "Ad-Duha", "আদ-দুহা", "The Morning Hours", "পূর্বাহ্ণ", 11, "Meccan"),
        Surah(94, "الشَّرْح", "Ash-Sharh", "আশ-শারহ", "The Relief", "বক্ষ প্রশস্তকরণ", 8, "Meccan"),
        Surah(95, "التِّين", "At-Tin", "আত-তীন", "The Fig", "ডুমুর", 8, "Meccan"),
        Surah(96, "العَلَق", "Al-'Alaq", "আল-আলাক", "The Clot", "রক্তপিণ্ড", 19, "Meccan"),
        Surah(97, "القَدْر", "Al-Qadr", "আল-ক্বদর", "The Power", "মর্যাদাময় রাত", 5, "Meccan"),
        Surah(98, "البَيِّنَة", "Al-Bayyinah", "আল-বায়্যিনাহ", "The Clear Proof", "সুস্পষ্ট প্রমাণ", 8, "Medinan"),
        Surah(99, "الزَّلْزَلَة", "Az-Zalzalah", "আজ-যালযালাহ", "The Earthquake", "ভূমিকম্প", 8, "Medinan"),
        Surah(100, "العَادِيَات", "Al-'Adiyat", "আল-আদিয়াত", "The Courser", "অভিযানকারী অশ্ব", 11, "Meccan"),
        Surah(101, "القَارِعَة", "Al-Qari'ah", "আল-কারিআহ", "The Calamity", "মহা প্রলয়", 11, "Meccan"),
        Surah(102, "التَّكَاثُر", "At-Takathur", "আত-তাকাসুর", "Rivalry in world increase", "প্রাচুর্যের প্রতিযোগিতা", 8, "Meccan"),
        Surah(103, "العَصْر", "Al-'Asr", "আল-আসর", "The Declining Day", "মহাকাল", 3, "Meccan"),
        Surah(104, "الهُمَزَة", "Al-Humazah", "আল-হুমাযাহ", "The Traducer", "পরনিন্দুক", 9, "Meccan"),
        Surah(105, "الفِيل", "Al-Fil", "আল-ফিল", "The Elephant", "হাতি", 5, "Meccan"),
        Surah(106, "قُرَيْش", "Quraysh", "কুরাইশ", "Quraysh", "কুরাইশ বংশ", 4, "Meccan"),
        Surah(107, "المَاعُون", "Al-Ma'un", "আল-মাউন", "Small Kindnesses", "সাহায্য-সহায়তা", 7, "Meccan"),
        Surah(108, "الكَوْثَر", "Al-Kawthar", "আল-কাউসার", "Abundance", "প্রচুর কল্যাণ", 3, "Meccan"),
        Surah(109, "الكَافِرُون", "Al-Kafirun", "আল-কাফিরুন", "The Disbelievers", "কাফিরগণ", 6, "Meccan"),
        Surah(110, "النَّصْر", "An-Nasr", "আন-নাসর", "Divine Support", "সাহায্য", 3, "Medinan"),
        Surah(111, "المَسَد", "Al-Masad", "আল-মাসাদ", "The Palm Fibre", "খেজুরের শক্ত দড়ি", 5, "Meccan"),
        Surah(112, "الإخْلاَص", "Al-Ikhlas", "আল-ইখলাস", "The Sincerity", "একনিষ্ঠতা", 4, "Meccan"),
        Surah(113, "الفَلَق", "Al-Falaq", "আল-ফালাক", "The Daybreak", "নিশিভোর", 5, "Meccan"),
        Surah(114, "النَّاس", "An-Nas", "আন-নাস", "Mankind", "মানবজাতি", 6, "Meccan")
    )

    fun getAyahsForSurah(surahNumber: Int, qariId: String = "alafasy"): List<Ayah> {
        val qari = QariDirectory.getById(qariId)
        val formattedSurah = String.format("%03d", surahNumber)
        return when (surahNumber) {
            1 -> listOf(
                Ayah(1, 1, "بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ", "Bismillahir Rahmanir Raheem", "বিস্‌মিল্লা-হির রাহ্‌মা-নির রাহীম", "শুরু করছি আল্লাহর নামে যিনি পরম করুণাময়, অতি দয়ালু।", "In the name of Allah, the Entirely Merciful, the Especially Merciful.", "${qari.audioBaseUrl}/001001.mp3"),
                Ayah(1, 2, "ٱلْحَمْدُ لِلَّهِ رَبِّ ٱلْعَـٰلَمِينَ", "Alhamdu lillahi Rabbil 'aalameen", "আলহামদু লিল্লা-হি রাব্বিল ‘আ-লামীন", "সমস্ত প্রশংসা আল্লাহ্‌র জন্য যিনি সকল সৃষ্টির রব।", "All praise is due to Allah, Lord of the worlds.", "${qari.audioBaseUrl}/001002.mp3"),
                Ayah(1, 3, "ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ", "Ar-Rahmanir Raheem", "আর-রাহ্‌মা-নির রাহীম", "পরম করুণাময়, অতি দয়ালু।", "The Entirely Merciful, the Especially Merciful.", "${qari.audioBaseUrl}/001003.mp3"),
                Ayah(1, 4, "مَـٰلِكِ يَوْمِ ٱلدِّينِ", "Maaliki Yawmid-Deen", "মা-লিকি ইয়াওমিদ্দীন", "বিচার দিবসের একমাত্র মালিক।", "Sovereign of the Day of Recompense.", "${qari.audioBaseUrl}/001004.mp3"),
                Ayah(1, 5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "Iyyaaka na'budu wa iyyaaka nasta'een", "ইয়্যা-কা না‘বুদু ওয়া ইয়্যা-কা নাস্‌তা‘ঈন", "আমরা কেবল আপনারই ইবাদত করি এবং কেবল আপনারই সাহায্য প্রার্থনা করি।", "It is You we worship and You we ask for help.", "${qari.audioBaseUrl}/001005.mp3"),
                Ayah(1, 6, "ٱهْدِنَا ٱلصِّرَٰطَ ٱلْمُسْتَقِيمَ", "Ihdinas-Siraatal-Mustaqeem", "ইহ্‌দিনাস সিরা-তাল মুস্‌তাক্বীম", "আমাদেরকে সরল সঠিক পথ প্রদর্শন করুন।", "Guide us to the straight path.", "${qari.audioBaseUrl}/001006.mp3"),
                Ayah(1, 7, "صِرَٰطَ ٱلَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ ٱلْمَغْضُوبِ عَلَيْهِمْ وَلَا ٱلضَّآلِّينَ", "Siraatal-ladheena an'amta 'alayhim ghayril-maghdoobi 'alayhim wa lad-daalleen", "সিরা-তাল্লাযীনা আন‘আমতা ‘আলাইহিম গাইরিল মাগদূবি ‘আলাইহিম ওয়ালাদ্দ-ল্লীন", "তাদের পথ যাদেরকে আপনি পুরস্কৃত করেছেন; তাদের পথ নয় যারা ক্রোধের শিকার এবং পথভ্রষ্ট নয়।", "The path of those upon whom You have bestowed favor, not of those who have evoked Your anger or of those who are astray.", "${qari.audioBaseUrl}/001007.mp3")
            )
            112 -> listOf(
                Ayah(112, 1, "قُلْ هُوَ اللَّهُ أَحَدٌ", "Qul Huwallahu Ahad", "ক্বুল হুওয়াল্লা-হু আহাদ", "বলুন, তিনিই আল্লাহ, অদ্বিতীয়।", "Say, He is Allah, [who is] One,", "${qari.audioBaseUrl}/112001.mp3"),
                Ayah(112, 2, "اللَّهُ الصَّمَدُ", "Allahus-Samad", "আল্লা-হুস্‌ সামাদ", "আল্লাহ কারো মুখাপেক্ষী নন, সকলেই তাঁর মুখাপেক্ষী।", "Allah, the Eternal Refuge.", "${qari.audioBaseUrl}/112002.mp3"),
                Ayah(112, 3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "Lam yalid wa lam yoolad", "লাম ইয়ালিদ্‌ ওয়া লাম ইউ-লাদ", "তিনি কাউকেও জন্ম দেননি এবং তাঁকেও জন্ম দেওয়া হয়নি।", "He neither begets nor is born,", "${qari.audioBaseUrl}/112003.mp3"),
                Ayah(112, 4, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "Wa lam yakul-lahu kufuwan ahad", "ওয়া লাম ইয়াকুল্লাহূ কুফুওয়ান আহাদ", "এবং তাঁর সমকক্ষ কেউই নেই।", "Nor is there to Him any equivalent.", "${qari.audioBaseUrl}/112004.mp3")
            )
            113 -> listOf(
                Ayah(113, 1, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "Qul a'oodhu bi Rabbil-falaq", "ক্বুল আ‘ঊযু বিরাব্বিল ফালাক্ব", "বলুন, আমি আশ্রয় প্রার্থনা করছি প্রভাতের পালনকর্তার,", "Say, I seek refuge in the Lord of daybreak", "${qari.audioBaseUrl}/113001.mp3"),
                Ayah(113, 2, "مِن شَرِّ مَا خَلَقَ", "Min sharri maa khalaq", "মিন শাররি মা- খলাক্ব", "তিনি যা সৃষ্টি করেছেন তার অনিষ্ট থেকে,", "From the evil of that which He created", "${qari.audioBaseUrl}/113002.mp3"),
                Ayah(113, 3, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "Wa min sharri ghaasiqin idhaa waqab", "ওয়া মিন শাররি গা-সিক্বিন ইযা- অক্বাব", "এবং ঘোর অন্ধকারের অনিষ্ট থেকে যখন তা সমাগত হয়,", "And from the evil of darkness when it settles", "${qari.audioBaseUrl}/113003.mp3"),
                Ayah(113, 4, "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "Wa min sharrin-naffaathaati fil-'uqad", "ওয়া মিন শাররিন নাফ্‌ফা-সা-তি ফিল ‘উক্বাফ", "এবং গ্রন্থিতে ফুৎকারকারিণীদের অনিষ্ট থেকে,", "And from the evil of the blowers in knots", "${qari.audioBaseUrl}/113004.mp3"),
                Ayah(113, 5, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "Wa min sharri haasidin idhaa hasad", "ওয়া মিন শাররি হা-সিদিন ইযা- হাসাদ", "এবং হিংসুকের অনিষ্ট থেকে যখন সে হিংসা করে।", "And from the evil of an envier when he envies.", "${qari.audioBaseUrl}/113005.mp3")
            )
            114 -> listOf(
                Ayah(114, 1, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "Qul a'oodhu bi Rabbin-naas", "ক্বুল আ‘ঊযু বিরাব্বিন না-স", "বলুন, আমি আশ্রয় প্রার্থনা করছি মানুষের পালনকর্তার,", "Say, I seek refuge in the Lord of mankind,", "${qari.audioBaseUrl}/114001.mp3"),
                Ayah(114, 2, "مَلِكِ النَّاسِ", "Malikin-naas", "মালিকিন না-স", "মানুষের অধিপতির,", "The Sovereign of mankind,", "${qari.audioBaseUrl}/114002.mp3"),
                Ayah(114, 3, "إِلَـٰهِ النَّاسِ", "Ilaahin-naas", "ইলা-হিন না-স", "মানুষের উপাস্যের,", "The God of mankind,", "${qari.audioBaseUrl}/114003.mp3"),
                Ayah(114, 4, "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "Min sharril-waswaasil-khannaas", "মিন শাররিল ওয়াসওয়াসিল খান্নাস", "পলায়নপর কুমন্ত্রণাদাতার অনিষ্ট থেকে,", "From the evil of the retreating whisperer -", "${qari.audioBaseUrl}/114004.mp3"),
                Ayah(114, 5, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "Alladhee yuwaswisu fee sudoorin-naas", "আল্লাযী ইউওয়াসউইসু ফী সুদূরিন না-স", "যে মানুষের অন্তরে কুমন্ত্রণা দেয়,", "Who whispers [evil] into the breasts of mankind -", "${qari.audioBaseUrl}/114005.mp3"),
                Ayah(114, 6, "مِنَ الْجِنَّةِ وَالنَّاسِ", "Minal-jinnati wan-naas", "মিনাল জিন্নাতি ওয়ান না-স", "জিন অথবা মানুষের মধ্য থেকে।", "From among the jinn and mankind.", "${qari.audioBaseUrl}/114006.mp3")
            )
            67 -> listOf(
                Ayah(67, 1, "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", "Tabaarakal-ladhee biyadihil-mulku wa Huwa 'alaa kulli shay'in Qadeer", "তাবারাকাল্লাযী বিয়াদিহিল মুলকু ওয়াহুওয়া আলা কুল্লি শাইয়িন ক্বাদীর", "পুণ্যময় তিনি যাঁর হাতে রয়েছে সকল রাজত্ব এবং তিনি সর্ববিষয়ে সর্বশক্তিমান।", "Blessed is He in whose hand is dominion, and He is over all things competent -", "${qari.audioBaseUrl}/067001.mp3"),
                Ayah(67, 2, "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا ۚ وَهُوَ الْعَزِيزُ الْغَفُورُ", "Alladhee khalaqal-mawta wal-hayaata liyabluwakum ayyukum ahsanu 'amalaa", "আল্লাযী খালাকাল মাওতা ওয়াল হায়াতা লিয়াবলুওয়াকুম আইয়ুকুম আহসানু আমালা", "যিনি সৃষ্টি করেছেন মৃত্যু ও জীবন তোমাদের পরীক্ষা করার জন্য যে কে কর্মে শ্রেষ্ঠ।", "[He] who created death and life to test you as to which of you is best in deed - and He is the Exalted in Might, the Forgiving -", "${qari.audioBaseUrl}/067002.mp3"),
                Ayah(67, 3, "الَّذِي خَلَقَ سَبْعَ سَمَاوَاتٍ طِبَاقًا ۖ مَّا تَرَىٰ فِي خَلْقِ الرَّحْمَـٰنِ مِن تَفَاوُتٍ", "Alladhee khalaqa sab'a samaawaatin tibaaqan maa taraa fee khalqir-Rahmaani min tafaawut", "আল্লাযী খালাকা সাবআ সামাওয়া-তিন তিবা-কান", "যিনি সাত আকাশ স্তরে স্তরে সৃষ্টি করেছেন। পরম দয়াময়ের সৃষ্টিতে তুমি কোন খুঁত দেখতে পাবে না।", "[And] who created seven heavens in layers. You do not see in the creation of the Most Merciful any inconsistency.", "${qari.audioBaseUrl}/067003.mp3")
            )
            else -> {
                val s = SURAHS.firstOrNull { it.number == surahNumber } ?: SURAHS[0]
                List(minOf(s.numberOfAyahs, 5)) { idx ->
                    val ayahNum = idx + 1
                    val formattedAyah = String.format("%03d", ayahNum)
                    Ayah(
                        surahNumber = surahNumber,
                        ayahNumber = ayahNum,
                        textArabic = if (ayahNum == 1) "بِسْمِ اللَّهِ الرَّحْمَـٰنِ الرَّحِيمِ ۞ وَالْقُرْآنِ الْحَكِيمِ" else "إِنَّكَ لَمِنَ الْمُرْسَلِينَ ۞ عَلَىٰ صِرَاطٍ مُّسْتَقِيمٍ",
                        transliteration = "Ayah $ayahNum of Surah ${s.nameEnglish}",
                        transliterationBengali = "সূরা ${s.nameBengali}-এর $ayahNum নং আয়াতের বিশুদ্ধ উচ্চারণ",
                        translationBengali = "${s.nameBengali} সূরার ${ayahNum} নং আয়াত। নিশ্চয়ই আল্লাহর বাণী চিরন্তন সত্য।",
                        translationEnglish = "Ayah $ayahNum of Surah ${s.nameEnglish}. Indeed the remembrance of Allah brings peace to hearts.",
                        audioUrl = "${qari.audioBaseUrl}/$formattedSurah$formattedAyah.mp3"
                    )
                }
            }
        }
    }
}
