package com.example.data.model

data class DailyHadith(
    val id: String,
    val title: String,
    val textArabic: String,
    val translationBengali: String,
    val translationEnglish: String,
    val narrator: String,
    val bookReference: String,
    val audioUrl: String = "",
    val titleEnglish: String = "",
    val narratorEnglish: String = "",
    val bookReferenceEnglish: String = "",
    val explanationBengali: String = "",
    val explanationEnglish: String = ""
)

object HadithRepository {

    val TODAY_HADITH = DailyHadith(
        id = "hadith_niyyah",
        title = "কাজের ফলাফল নিয়তের উপর নির্ভরশীল",
        titleEnglish = "Actions Are Judged by Intentions",
        textArabic = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى",
        translationBengali = "নিশ্চয়ই সমস্ত কাজের ফলাফল নিয়তের ওপর নির্ভরশীল। আর প্রত্যেক ব্যক্তি যা নিয়ত করবে কেবল তাই সে পাবে।",
        translationEnglish = "Actions are according to intentions, and every person will have only what they intended.",
        narrator = "হযরত উমর ইবনুল খাত্তাব (রাঃ)",
        narratorEnglish = "Umar ibn al-Khattab (RA)",
        bookReference = "সহীহ বুখারী: ১, সহীহ মুসলিম: ১৯০৭",
        bookReferenceEnglish = "Sahih al-Bukhari: 1, Sahih Muslim: 1907",
        audioUrl = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/001001.mp3",
        explanationBengali = "ইসলামের প্রতিটি ইবাদত ও দৈনন্দিন কাজের গ্রহণযোগ্যতা অন্তরের নিয়ত বা ইখলাসের ওপর নির্ভর করে। খাঁটি নিয়ত সাধারণ কাজকেও ইবাদতে পরিণত করে।",
        explanationEnglish = "Every act of worship and daily deed depends upon sincerity of intention (Niyyah). A pure intention for the sake of Allah transforms even ordinary habits into rewarded worship."
    )

    val HADITHS = listOf(
        TODAY_HADITH,
        DailyHadith(
            id = "hadith_quran_best",
            title = "কুরআন শিক্ষা ও প্রচারের শ্রেষ্ঠত্ব",
            titleEnglish = "The Virtue of Learning & Teaching the Qur'an",
            textArabic = "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ",
            translationBengali = "তোমাদের মধ্যে সর্বোত্তম ব্যক্তি সে, যে নিজে কুরআন শিখে এবং অপরকে তা শিক্ষা দেয়।",
            translationEnglish = "The best among you are those who learn the Qur'an and teach it.",
            narrator = "হযরত উসমান ইবনে আফফান (রাঃ)",
            narratorEnglish = "Uthman ibn Affan (RA)",
            bookReference = "সহীহ বুখারী: ৫০২৭",
            bookReferenceEnglish = "Sahih al-Bukhari: 5027",
            audioUrl = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/001002.mp3",
            explanationBengali = "কুরআন তিলাওয়াত, অর্থ অনুধাবন ও তা অন্যের মাঝে ছড়িয়ে দেওয়া উম্মতের সবচেয়ে মর্যাদাপূর্ণ খেদমত।",
            explanationEnglish = "Learning the recitation and meaning of the Noble Qur'an and passing it on to others elevates a believer to the highest rank of service."
        ),
        DailyHadith(
            id = "hadith_smiling",
            title = "মুসলিমের মুখে হাসি ফোটানো সদকা",
            titleEnglish = "A Smile for Your Brother Is Charity",
            textArabic = "تَبَسُّمُكَ فِي وَجْهِ أَخِيكَ لَكَ صَدَقَةٌ",
            translationBengali = "তোমার ভাইয়ের মুখের দিকে তাকিয়ে তোমার একটু মুচকি হাসাও তোমার জন্য একটি সদকা।",
            translationEnglish = "Your smiling in the face of your brother is an act of charity for you.",
            narrator = "হযরত আবু যার আল-গিফারী (রাঃ)",
            narratorEnglish = "Abu Dharr al-Ghifari (RA)",
            bookReference = "জামে তিরমিযী: ১৯৫৬",
            bookReferenceEnglish = "Jami' at-Tirmidhi: 1956",
            audioUrl = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/001003.mp3",
            explanationBengali = "সদকাহ কেবল অর্থ ব্যয়ের মাঝে সীমাবদ্ধ নয়; সুন্দর ব্যবহার ও আন্তরিক হাসিও আল্লাহর নিকট দান হিসেবে গণ্য।",
            explanationEnglish = "Charity is not limited to wealth; warmth, kindness, and a sincere smile strengthen brotherhood and carry genuine reward."
        ),
        DailyHadith(
            id = "hadith_cleanliness_iman",
            title = "পবিত্রতা ঈমানের অর্ধেক",
            titleEnglish = "Purity Is Half of Faith",
            textArabic = "الطُّهُورُ شَطْرُ الْإِيمَانِ، وَالْحَمْدُ لِلَّهِ تَمْلَأُ الْمِيزَانَ",
            translationBengali = "পবিত্রতা ঈমানের অর্ধেক এবং ‘আলহামদুলিল্লাহ’ আমলের পাল্লাকে পূর্ণ করে দেয়।",
            translationEnglish = "Purity is half of faith, and Alhamdulillah (praise be to Allah) fills the scales.",
            narrator = "হযরত আবু মালিক আল-আশআরী (রাঃ)",
            narratorEnglish = "Abu Malik al-Ash'ari (RA)",
            bookReference = "সহীহ মুসলিম: ২২৩",
            bookReferenceEnglish = "Sahih Muslim: 223",
            explanationBengali = "বাহ্যিক ও অভ্যন্তরীণ পবিত্রতা এবং সর্বাবস্থায় আল্লাহর শুকরিয়া আদায় করা মুমিনের জীবনের মূল ভিত্তি।",
            explanationEnglish = "Maintaining both physical cleanliness and spiritual purity, paired with gratitude to Allah, weighs heavily on the scale of deeds."
        ),
        DailyHadith(
            id = "hadith_good_character",
            title = "সর্বোত্তম চরিত্রবানই প্রিয়তম",
            titleEnglish = "The Best Among You in Character",
            textArabic = "إِنَّ مِنْ خِيَارِكُمْ أَحْسَنَكُمْ أَخْلَاقًا",
            translationBengali = "নিশ্চয়ই তোমাদের মধ্যে সর্বোত্তম ব্যক্তি সে, যার চরিত্র ও আচার-ব্যবহার সবচেয়ে সুন্দর।",
            translationEnglish = "Indeed, the best among you are those who have the best manners and character.",
            narrator = "হযরত আব্দুল্লাহ ইবনে আমর (রাঃ)",
            narratorEnglish = "Abdullah ibn Amr (RA)",
            bookReference = "সহীহ বুখারী: ৩৫৫৯",
            bookReferenceEnglish = "Sahih al-Bukhari: 3559",
            explanationBengali = "উত্তম চরিত্র, নম্রতা ও সত্যবাদিতা ইবাদতের সৌন্দর্য বাড়িয়ে দেয় এবং কিয়ামতের দিন মীযানের পাল্লায় সবচেয়ে ভারী হবে।",
            explanationEnglish = "Noble character, gentleness, and honesty beautify a believer's worship and carry the greatest weight on the Day of Judgment."
        ),
        DailyHadith(
            id = "hadith_easy_religion",
            title = "দ্বীন সহজ ও কল্যাণময়",
            titleEnglish = "Make Matters Easy, Not Difficult",
            textArabic = "يَسِّرُوا وَلَا تُعَسِّرُوا، وَبَشِّرُوا وَلَا تُنَفِّرُوا",
            translationBengali = "তোমরা মানুষের জন্য সহজ করো, কঠিন করো না; সুসংবাদ দাও এবং ঘৃণা বা বিমুখতা সৃষ্টি করো না।",
            translationEnglish = "Make things easy for people and do not make them difficult; give glad tidings and do not drive them away.",
            narrator = "হযরত আনাস ইবনে মালিক (রাঃ)",
            narratorEnglish = "Anas ibn Malik (RA)",
            bookReference = "সহীহ বুখারী: ৬৯",
            bookReferenceEnglish = "Sahih al-Bukhari: 69",
            explanationBengali = "দাওয়াত, পরিবার ও সমাজে কঠোরতার পরিবর্তে প্রজ্ঞা, সহানুভূতি ও আশাবাদের বার্তা ছড়িয়ে দেওয়া সুন্নাহ।",
            explanationEnglish = "In family life and community outreach, spreading mercy, wisdom, and hope reflects the authentic Prophetic way."
        ),
        DailyHadith(
            id = "hadith_speak_good",
            title = "সত্য ও কল্যাণকর কথা বলা",
            titleEnglish = "Speak Good or Remain Silent",
            textArabic = "مَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الْآخِرِ فَلْيَقُلْ خَيْرًا أَوْ لِيَصْمُتْ",
            translationBengali = "যে ব্যক্তি আল্লাহ ও পরকালের প্রতি ঈমান রাখে, সে যেন উত্তম কথা বলে অথবা চুপ থাকে।",
            translationEnglish = "Whoever believes in Allah and the Last Day should speak what is good or remain silent.",
            narrator = "হযরত আবু হুরায়রা (রাঃ)",
            narratorEnglish = "Abu Hurairah (RA)",
            bookReference = "সহীহ বুখারী: ৬০১৮",
            bookReferenceEnglish = "Sahih al-Bukhari: 6018",
            explanationBengali = "জিহ্বার হেফাজত, গীবত ও অপ্রয়োজনীয় কথা পরিহার করা মুমিনের ঈমানের পরিপক্বতার প্রমাণ।",
            explanationEnglish = "Guarding the tongue from backbiting and idle talk is a clear sign of mature faith."
        ),
        DailyHadith(
            id = "hadith_love_brother",
            title = "ভ্রাতৃত্ব ও ভালোবাসার মানদণ্ড",
            titleEnglish = "Love for Your Brother What You Love for Yourself",
            textArabic = "لَا يُؤْمِنُ أَحَدُكُمْ حَتَّىٰ يُحِبَّ لِأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ",
            translationBengali = "তোমাদের কেউ প্রকৃত মুমিন হতে পারবে না, যতক্ষণ না সে নিজের জন্য যা পছন্দ করে তার ভাইয়ের জন্যও তা পছন্দ করে।",
            translationEnglish = "None of you truly believes until he loves for his brother what he loves for himself.",
            narrator = "হযরত আনাস ইবনে মালিক (রাঃ)",
            narratorEnglish = "Anas ibn Malik (RA)",
            bookReference = "সহীহ বুখারী: ১৩",
            bookReferenceEnglish = "Sahih al-Bukhari: 13",
            explanationBengali = "হিংসা-বিদ্বেষ মুক্ত হৃদয় এবং অপরের কল্যাণ কামনা করা পরিপূর্ণ ঈমানের অবিচ্ছেদ্য অংশ।",
            explanationEnglish = "A heart free of envy that genuinely wishes good for others is an essential pillar of complete faith."
        ),
        DailyHadith(
            id = "hadith_strong_believer",
            title = "ক্রোধ নিয়ন্ত্রণই প্রকৃত বীরত্ব",
            titleEnglish = "True Strength Is Controlling Anger",
            textArabic = "لَيْسَ الشَّدِيدُ بِالصُّرَعَةِ، إِنَّمَا الشَّدِيدُ الَّذِي يَمْلِكُ نَفْسَهُ عِنْدَ الْغَضَبِ",
            translationBengali = "কুস্তিতে যে জয়ী হয় সে প্রকৃত শক্তিশালী নয়; বরং প্রকৃত শক্তিশালী হলো সেই ব্যক্তি যে রাগের সময় নিজেকে নিয়ন্ত্রণ করতে পারে।",
            translationEnglish = "The strong person is not the one who overcomes others by physical strength, rather the strong one is he who controls himself while in anger.",
            narrator = "হযরত আবু হুরায়রা (রাঃ)",
            narratorEnglish = "Abu Hurairah (RA)",
            bookReference = "সহীহ বুখারী: ৬১১৪",
            bookReferenceEnglish = "Sahih al-Bukhari: 6114",
            explanationBengali = "রাগের মুহূর্তে ধৈর্য ধারণ ও 'আউযুবিল্লাহ' পাঠ করা শয়তানের প্ররোচনা থেকে হৃদয়কে রক্ষা করে।",
            explanationEnglish = "Practicing restraint in moments of anger protects relationships and reflects inner spiritual discipline."
        ),
        DailyHadith(
            id = "hadith_mercy_creatures",
            title = "সৃষ্টির প্রতি দয়া ও অনুগ্রহ",
            titleEnglish = "Show Mercy to Those on Earth",
            textArabic = "الرَّاحِمُونَ يَرْحَمُهُمُ الرَّحْمَٰنُ، ارْحَمُوا مَنْ فِي الْأَرْضِ يَرْحَمْكُمْ مَنْ فِي السَّمَاءِ",
            translationBengali = "দয়াশীলদের প্রতি পরম দয়াময় আল্লাহ দয়া করেন। তোমরা পৃথিবীবাসীর প্রতি দয়া করো, তবে আকাশের অধিপতি আল্লাহ তোমাদের প্রতি দয়া করবেন।",
            translationEnglish = "The Merciful One shows mercy to those who are merciful. Show mercy to those on earth, and He Who is in heaven will show mercy to you.",
            narrator = "হযরত আব্দুল্লাহ ইবনে আমর (রাঃ)",
            narratorEnglish = "Abdullah ibn Amr (RA)",
            bookReference = "জামে তিরমিযী: ১৯২৪",
            bookReferenceEnglish = "Jami' at-Tirmidhi: 1924",
            explanationBengali = "মানুষ ও সকল সৃষ্টির প্রতি সহানুভূতি, ক্ষমাশীলতা ও দয়া আল্লাহর অসীম রহমত আকর্ষণ করে।",
            explanationEnglish = "Compassion toward family, neighbors, and all creatures invites Allah's boundless mercy."
        )
    )

    fun getTodayHadith(): DailyHadith {
        val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        return HADITHS[dayOfYear % HADITHS.size]
    }
}
