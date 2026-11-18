package com.example.data.model

import java.util.Calendar
import kotlin.math.floor

/**
 * Detailed Hijri Date representation
 */
data class HijriDateDetails(
    val day: Int,
    val monthIndex: Int, // 0 to 11
    val monthNameEn: String,
    val monthNameBn: String,
    val year: Int,
    val isFriday: Boolean,
    val isMondayOrThursday: Boolean,
    val isAyyamAlBid: Boolean
) {
    val formattedDateBn: String
        get() = "$day $monthNameBn, $year হিজরি"

    val formattedDateEn: String
        get() = "$day $monthNameEn, $year AH"

    fun formattedDate(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") {
            formattedDateBn
        } else {
            val month = com.example.data.util.SupportedLanguage.localizedHijriMonth(monthIndex + 1, lang)
            "${com.example.data.util.SupportedLanguage.formatInt(day, lang)} $month, ${com.example.data.util.SupportedLanguage.formatInt(year, lang)} AH"
        }
}

enum class AmalPlanType {
    JUMAT_SPECIAL,
    AYYAM_AL_BID_FAST,
    MONDAY_THURSDAY_FAST,
    FIRST_TEN_DHUL_HIJJAH,
    ASHURA_MUHARRAM,
    RAMADAN_SPECIAL,
    DAILY_SUNNAH_ROUTINE
}

data class AmalChecklistItem(
    val id: String,
    val titleBn: String,
    val subTextBn: String? = null,
    val hadithRef: String? = null,
    val rewardNoteBn: String? = null,
    val titleEn: String = "",
    val subTextEn: String? = null,
    val hadithRefEn: String? = null,
    val rewardNoteEn: String? = null
) {
    fun localizedTitle(lang: String): String =
        com.example.ui.theme.LocalizationRegistry.translate(lang, titleBn, titleEn.ifBlank { titleBn })

    fun localizedSubText(lang: String): String? =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") subTextBn
        else subTextEn?.takeIf { it.isNotBlank() } ?: subTextBn

    fun localizedHadithRef(lang: String): String? =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") hadithRef
        else hadithRefEn?.takeIf { it.isNotBlank() } ?: hadithRef
}

data class DynamicAmalPlan(
    val type: AmalPlanType,
    val titleBn: String,
    val subtitleBn: String,
    val hijriDayBadge: String,
    val badgeColorHex: String,
    val hadithReference: String,
    val isFastingDay: Boolean,
    val fastingVirtueBn: String? = null,
    val checklistItems: List<AmalChecklistItem>,
    val titleEn: String = "",
    val subtitleEn: String = "",
    val hijriDayBadgeEn: String = "",
    val hadithReferenceEn: String = "",
    val fastingVirtueEn: String? = null
) {
    fun localizedTitle(lang: String): String =
        com.example.ui.theme.LocalizationRegistry.translate(lang, titleBn, titleEn.ifBlank { titleBn })

    fun localizedSubtitle(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") subtitleBn
        else subtitleEn.ifBlank { subtitleBn }

    fun localizedHijriDayBadge(lang: String): String =
        com.example.ui.theme.LocalizationRegistry.translate(lang, hijriDayBadge, hijriDayBadgeEn.ifBlank { hijriDayBadge })

    fun localizedHadithReference(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") hadithReference
        else hadithReferenceEn.ifBlank { hadithReference }

    fun localizedFastingVirtue(lang: String): String? =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") fastingVirtueBn
        else fastingVirtueEn?.takeIf { it.isNotBlank() } ?: fastingVirtueBn
}

object DynamicHijriAmalProvider {

    private val HIJRI_MONTHS_BN = listOf(
        "মুহাররম", "সফর", "রবিউল আউয়াল", "রবিউস সানি",
        "জুমাদাল উলা", "জুমাদাস সানিয়া", "রজব", "শা'বান",
        "রমাদান", "শাওয়াল", "জিলকদ", "জিলহজ্জ"
    )

    private val HIJRI_MONTHS_EN = listOf(
        "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
        "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
        "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
    )

    /**
     * Compute current Hijri Date Details using astronomical Kuwaiti algorithm
     */
    fun getHijriDetails(calendar: Calendar = Calendar.getInstance(), offsetDays: Int = 0): HijriDateDetails {
        val (yHijri, mHijri, dHijri) = HijriCalendarEngine.gregorianToHijri(calendar, offsetDays)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        // Friday starts Thursday Maghrib (17:30) through Friday sunset
        val isFriday = (dayOfWeek == Calendar.FRIDAY) || (dayOfWeek == Calendar.THURSDAY && hour >= 17)
        val isMondayOrThursday = (dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.THURSDAY) && !isFriday
        val isAyyamAlBid = dHijri in 13..15

        return HijriDateDetails(
            day = dHijri,
            monthIndex = mHijri,
            monthNameEn = HIJRI_MONTHS_EN[mHijri],
            monthNameBn = HIJRI_MONTHS_BN[mHijri],
            year = yHijri,
            isFriday = isFriday,
            isMondayOrThursday = isMondayOrThursday,
            isAyyamAlBid = isAyyamAlBid
        )
    }

    /**
     * Dynamically determines today's Islamic Amal plan based on Hijri Date & Day of Week
     */
    fun getTodayAmalPlan(calendar: Calendar = Calendar.getInstance(), offsetDays: Int = 0): DynamicAmalPlan {
        val hijri = getHijriDetails(calendar, offsetDays)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // 1. Friday condition takes precedence
        if (hijri.isFriday) {
            return DynamicAmalPlan(
                type = AmalPlanType.JUMAT_SPECIAL,
                titleBn = "জুমু'আতুল মুবারক এর বিশেষ আমল",
                titleEn = "Jumu'ah Mubarak Special Deeds",
                subtitleBn = "সাপ্তাহিক ঈদের দিন ও দরূদ পাঠের সর্বোত্তম সময়",
                subtitleEn = "Weekly Eid & best day for Salawat upon the Prophet ﷺ",
                hijriDayBadge = "পবিত্র জুমু'আ দিবস",
                hijriDayBadgeEn = "Blessed Jumu'ah",
                badgeColorHex = "#E5C158",
                hadithReference = "সুনানে আবু দাউদ ১০৪৭",
                hadithReferenceEn = "Sunan Abi Dawud 1047",
                isFastingDay = false,
                checklistItems = listOf(
                    AmalChecklistItem(
                        id = "j_ghusl",
                        titleBn = "গোসল ও পরিষ্কার পোশাক পরিধান",
                        titleEn = "Take Ghusl & wear clean garments",
                        subTextBn = "জুমু'আর দিনে গোসল করা সুন্নাতে মুয়াক্কাদাহ",
                        subTextEn = "Taking Ghusl on Friday is an emphasized Sunnah",
                        hadithRef = "বুখারী ৮৭৭",
                        hadithRefEn = "Bukhari 877"
                    ),
                    AmalChecklistItem(
                        id = "j_kahf",
                        titleBn = "সূরা আল-কাহাফ তিলাওয়াত",
                        titleEn = "Recite Surah Al-Kahf",
                        subTextBn = "দুই জুমু'আর মধ্যবর্তী সময় নূর দ্বারা আলোকিত থাকে",
                        subTextEn = "A spiritual light shines between one Friday and the next",
                        hadithRef = "সহীহ তারগীব ৭৩৬",
                        hadithRefEn = "Sahih al-Targhib 736"
                    ),
                    AmalChecklistItem(
                        id = "j_durood",
                        titleBn = "নবীজির ﷺ উপর অধিক দরূদ পাঠ",
                        titleEn = "Send abundant Salawat upon the Prophet ﷺ",
                        subTextBn = "আমার উপর অধিক দরূদ পড়, তা আমার কাছে পেশ করা হয়",
                        subTextEn = "Increase blessings upon me on Friday, for your Salawat is presented to me",
                        hadithRef = "আবু দাউদ ১০৪৭",
                        hadithRefEn = "Abu Dawud 1047"
                    ),
                    AmalChecklistItem(
                        id = "j_early",
                        titleBn = "আগে আগে মসজিদে উপস্থিত হওয়া",
                        titleEn = "Arrive early at the mosque",
                        subTextBn = "প্রথম ওয়াক্তে গেলে একটি উট কুরবানির সাওয়াব",
                        subTextEn = "Arriving in the first hour equals the reward of offering a camel",
                        hadithRef = "মুসলিম ৮৫০",
                        hadithRefEn = "Muslim 850"
                    ),
                    AmalChecklistItem(
                        id = "j_dua_hour",
                        titleBn = "আসর পরবর্তী বিশেষ মুহূর্তে দো'আ",
                        titleEn = "Make Du'a in the blessed hour after Asr",
                        subTextBn = "জুমু'আর দিনে এমন এক মুহূর্ত আছে যাতে দো'আ কবুল হয়",
                        subTextEn = "There is an hour on Friday when a Muslim's Du'a is surely answered",
                        hadithRef = "বুখারী ৯৩৫",
                        hadithRefEn = "Bukhari 935"
                    )
                )
            )
        }

        // 2. Ramadan Special Month (Month Index 8)
        if (hijri.monthIndex == 8) {
            return DynamicAmalPlan(
                type = AmalPlanType.RAMADAN_SPECIAL,
                titleBn = "পবিত্র মাহে রমাদানের আমল",
                titleEn = "Blessed Ramadan Daily Plan",
                subtitleBn = "${hijri.day} রমাদান • মাগফিরাত ও কুরআনের মাস",
                subtitleEn = "${hijri.day} Ramadan • Month of Forgiveness & Qur'an",
                hijriDayBadge = "${hijri.day} রমাদান মুবারক",
                hijriDayBadgeEn = "${hijri.day} Ramadan Mubarak",
                badgeColorHex = "#00E676",
                hadithReference = "সহীহ বুখারী ৩৮",
                hadithReferenceEn = "Sahih al-Bukhari 38",
                isFastingDay = true,
                fastingVirtueBn = "যে ব্যক্তি ঈমান ও সাওয়াবের আশায় রোজা রাখে তার পূর্বের সকল গুনাহ ক্ষমা করা হয়।",
                fastingVirtueEn = "Whoever fasts Ramadan out of sincere faith and hoping for reward, all their past sins are forgiven.",
                checklistItems = listOf(
                    AmalChecklistItem(
                        id = "ram_fast",
                        titleBn = "ফরজ রোজা হেফাজত ও সহিহ নিয়ত",
                        titleEn = "Guard obligatory fast with sincere intention",
                        subTextBn = "পাপ কাজ ও অনর্থক কথা পরিহার করে রোজা পূর্ণ করা",
                        subTextEn = "Abstain from falsehood, anger, and idle speech while fasting",
                        hadithRef = "বুখারী ১৯০৪",
                        hadithRefEn = "Bukhari 1904"
                    ),
                    AmalChecklistItem(
                        id = "ram_quran",
                        titleBn = "দৈনিক ১ পারা কুরআন তিলাওয়াত",
                        titleEn = "Recite 1 Juz of the Holy Qur'an daily",
                        subTextBn = "রমাদানে জিব্রাইল (আ) নবীজির সাথে কুরআন দাওর করতেন",
                        subTextEn = "Jibril (AS) reviewed the Qur'an with the Prophet ﷺ every night of Ramadan",
                        hadithRef = "বুখারী ৬",
                        hadithRefEn = "Bukhari 6"
                    ),
                    AmalChecklistItem(
                        id = "ram_tarawih",
                        titleBn = "তারাবীহ / কিয়ামুল লাইল আদায়",
                        titleEn = "Perform Tarawih & Qiyam al-Layl",
                        subTextBn = "যে ব্যক্তি তারাবীহ পড়ে তার অতীতের গুনাহ ক্ষমা হয়",
                        subTextEn = "Whoever stands in night prayer during Ramadan with faith is forgiven",
                        hadithRef = "বুখারী ২০০৯",
                        hadithRefEn = "Bukhari 2009"
                    ),
                    AmalChecklistItem(
                        id = "ram_iftar_dua",
                        titleBn = "ইফতারের পূর্ব মুহূর্তে একনিষ্ঠ দো'আ",
                        titleEn = "Sincere Du'a right before Iftar",
                        subTextBn = "রোজাদারের ইফতারের সময় দো'আ ফিরিয়ে দেওয়া হয় না",
                        subTextEn = "The Du'a of a fasting person at the time of breaking fast is not rejected",
                        hadithRef = "ইবনে মাজাহ ১৭৫৩",
                        hadithRefEn = "Ibn Majah 1753"
                    ),
                    AmalChecklistItem(
                        id = "ram_sadaqah",
                        titleBn = "রোজাদারকে ইফতার করানো ও দান-সদকাহ",
                        titleEn = "Feed a fasting person & give Sadaqah",
                        subTextBn = "নবীজি রমাদানে প্রবাহিত বায়ুর চেয়েও অধিক দানশীল হতেন",
                        subTextEn = "The Prophet ﷺ was more generous in Ramadan than the blowing wind",
                        hadithRef = "বুখারী ৩২২০",
                        hadithRefEn = "Bukhari 3220"
                    )
                )
            )
        }

        // 3. First 10 Days of Dhul Hijjah (Month Index 11, Days 1-10)
        if (hijri.monthIndex == 11 && hijri.day in 1..10) {
            val isArafah = hijri.day == 9
            return DynamicAmalPlan(
                type = AmalPlanType.FIRST_TEN_DHUL_HIJJAH,
                titleBn = if (isArafah) "ইয়াওমে আরাফাহর বরকতময় আমল" else "জিলহজ্জের প্রথম দশকের ফযিলতপূর্ণ আমল",
                titleEn = if (isArafah) "Blessed Deeds for Day of Arafah" else "Virtuous First 10 Days of Dhu al-Hijjah",
                subtitleBn = "${hijri.day} জিলহজ্জ • বছরের শ্রেষ্ঠ দশ দিন",
                subtitleEn = "${hijri.day} Dhu al-Hijjah • Best 10 Days of the Year",
                hijriDayBadge = if (isArafah) "৯ জিলহজ্জ • ইয়াওমে আরাফাহ" else "${hijri.day} জিলহজ্জ দিবস",
                hijriDayBadgeEn = if (isArafah) "9 Dhu al-Hijjah • Arafah" else "${hijri.day} Dhu al-Hijjah",
                badgeColorHex = "#FFD54F",
                hadithReference = "সহীহ বুখারী ৯৬৯",
                hadithReferenceEn = "Sahih al-Bukhari 969",
                isFastingDay = true,
                fastingVirtueBn = if (isArafah) "আরাফাহর দিনের এক দিনের রোজা বিগত এক বছর ও আগামী এক বছরের গুনাহের কাফফারা।" else "জিলহজ্জের প্রথম ৯ দিনের নফল রোজা ও তাকবীরে তাশরীক বিশেষ সাওয়াবের।",
                fastingVirtueEn = if (isArafah) "Fasting on the Day of Arafah expiates the sins of the previous year and the coming year." else "Voluntary fasting and abundant Takbir during these 10 days carry immense reward.",
                checklistItems = listOf(
                    AmalChecklistItem(
                        id = "dh_fast",
                        titleBn = if (isArafah) "আরাফাহর সিয়াম (রোজা)" else "নফল সিয়াম পালন",
                        titleEn = if (isArafah) "Fast on the Day of Arafah" else "Observe voluntary fasting",
                        subTextBn = "আরাফাহর রোজা বিগত ও আগামী বছরের গুনাহ মাফ করে",
                        subTextEn = "Fasting Arafah wipes away sins of the past and upcoming year",
                        hadithRef = "মুসলিম ১১৬২",
                        hadithRefEn = "Muslim 1162"
                    ),
                    AmalChecklistItem(
                        id = "dh_takbir",
                        titleBn = "তাকবীরে তাশরীক ও অধিক তাহলীল",
                        titleEn = "Recite Takbir, Tahlil & Tahmid abundantly",
                        subTextBn = "আল্লাহু আকবার, লা ইলাহা ইল্লাল্লাহ, আলহামদুলিল্লাহ অধিক পাঠ",
                        subTextEn = "Say Allahu Akbar, La ilaha illallah, and Alhamdulillah frequently",
                        hadithRef = "আহমাদ ৫৪৪৭",
                        hadithRefEn = "Ahmad 5447"
                    ),
                    AmalChecklistItem(
                        id = "dh_quran",
                        titleBn = "কুরআন তিলাওয়াত ও ইস্তিগফার বৃদ্ধি",
                        titleEn = "Increase Qur'an recitation & Istighfar",
                        subTextBn = "এই দশ দিনের সৎ আমল আল্লাহর নিকট সর্বাধিক প্রিয়",
                        subTextEn = "No righteous deeds are more beloved to Allah than those in these 10 days",
                        hadithRef = "বুখারী ৯৬৯",
                        hadithRefEn = "Bukhari 969"
                    ),
                    AmalChecklistItem(
                        id = "dh_charity",
                        titleBn = "গরীব-অসহায়কে দান-সদকাহ",
                        titleEn = "Give Sadaqah to those in need",
                        subTextBn = "কুরবানির দিনগুলোর ত্যাগের প্রস্তুতি ও সদকাহ",
                        subTextEn = "Cultivate generosity and prepare your heart for Qurbani",
                        hadithRef = "তিরমিযী ৮০৭",
                        hadithRefEn = "Tirmidhi 807"
                    )
                )
            )
        }

        // 4. Ashura - 9th & 10th Muharram (Month Index 0)
        if (hijri.monthIndex == 0 && hijri.day in 9..10) {
            return DynamicAmalPlan(
                type = AmalPlanType.ASHURA_MUHARRAM,
                titleBn = "পবিত্র আশুরার আমল ও সিয়াম",
                titleEn = "Blessed Ashura Fasting & Deeds",
                subtitleBn = "${hijri.day} মুহাররম • ঐতিহাসিক মুবারক দিন",
                subtitleEn = "${hijri.day} Muharram • Sacred Historical Day",
                hijriDayBadge = "${hijri.day} মুহাররম (আশুরা)",
                hijriDayBadgeEn = "${hijri.day} Muharram (Ashura)",
                badgeColorHex = "#4FC3F7",
                hadithReference = "সহীহ মুসলিম ১১৬২",
                hadithReferenceEn = "Sahih Muslim 1162",
                isFastingDay = true,
                fastingVirtueBn = "নবীজি ﷺ ইরশাদ করেছেন: আশুরার দিনের রোজা পূর্ববর্তী এক বছরের গুনাহ মোচন করে দেয়।",
                fastingVirtueEn = "The Prophet ﷺ said: Fasting the Day of Ashura expiates the sins of the preceding year.",
                checklistItems = listOf(
                    AmalChecklistItem(
                        id = "ash_fast",
                        titleBn = "আশুরার রোজা (৯ ও ১০ই মুহাররম)",
                        titleEn = "Fast on Ashura (9th & 10th Muharram)",
                        subTextBn = "ইহুদিদের বিপরীত করার জন্য ৯ ও ১০ই রোজা রাখা সুন্নাত",
                        subTextEn = "Fasting the 9th alongside the 10th of Muharram is the prophetic Sunnah",
                        hadithRef = "মুসলিম ১১৩৪",
                        hadithRefEn = "Muslim 1134"
                    ),
                    AmalChecklistItem(
                        id = "ash_istighfar",
                        titleBn = "অধিক তওবা ও ইস্তিগফার পাঠ",
                        titleEn = "Abundant Tawbah & Istighfar",
                        subTextBn = "মুহাররম আল্লাহর মাস এবং ক্ষমার অনন্য সুযোগ",
                        subTextEn = "Muharram is the sacred month of Allah—a special season of repentance",
                        hadithRef = "তিরমিযী ৭৪০",
                        hadithRefEn = "Tirmidhi 740"
                    ),
                    AmalChecklistItem(
                        id = "ash_family",
                        titleBn = "পরিবারের ওপর মুক্তহস্তে ব্যয় ও সদকাহ",
                        titleEn = "Be generous to your family & give charity",
                        subTextBn = "যে ব্যক্তি আশুরার দিন পরিবারে প্রাচুর্য দান করে আল্লাহ বছরজুড়ে প্রাচুর্য দেন",
                        subTextEn = "Whoever expands provision for his family on Ashura, Allah expands for him all year",
                        hadithRef = "বায়হাকী",
                        hadithRefEn = "Bayhaqi"
                    )
                )
            )
        }

        // 5. Ayyam al-Bid (13th, 14th, 15th of Hijri month)
        if (hijri.isAyyamAlBid) {
            return DynamicAmalPlan(
                type = AmalPlanType.AYYAM_AL_BID_FAST,
                titleBn = "আইয়ামে বীজের সুন্নাত রোজা ও আমল",
                titleEn = "Ayyam al-Bid Sunnah Fasting & Deeds",
                subtitleBn = "প্রতি হিজরি মাসের ১৩, ১৪ ও ১৫ তারিখের বিশেষ সিয়াম",
                subtitleEn = "White Days fasting on the 13th, 14th & 15th Hijri",
                hijriDayBadge = "${hijri.day} ${hijri.monthNameBn} (আইয়ামে বীজ)",
                hijriDayBadgeEn = "${hijri.day} ${hijri.monthNameEn} (White Days)",
                badgeColorHex = "#00E676",
                hadithReference = "সুনানে তিরমিযী ৭৬১",
                hadithReferenceEn = "Jami' at-Tirmidhi 761",
                isFastingDay = true,
                fastingVirtueBn = "মাসে ৩ দিন রোজা রাখা সারা বছর রোজা রাখার সমান সাওয়াব (দাহর রোজা)।",
                fastingVirtueEn = "Fasting three days every month brings the reward of fasting the entire year.",
                checklistItems = listOf(
                    AmalChecklistItem(
                        id = "bid_fast",
                        titleBn = "আইয়ামে বীজের নফল রোজা",
                        titleEn = "Observe Ayyam al-Bid voluntary fast",
                        subTextBn = "নবীজি ﷺ প্রতি হিজরি মাসের ১৩, ১৪, ১৫ তারিখ রোজা রাখতেন",
                        subTextEn = "The Prophet ﷺ used to fast the 13th, 14th, and 15th of every lunar month",
                        hadithRef = "নাসায়ী ২৪২০",
                        hadithRefEn = "Nasa'i 2420"
                    ),
                    AmalChecklistItem(
                        id = "bid_zikr",
                        titleBn = "সকাল-সন্ধ্যার মাসনুন জিকির আদায়",
                        titleEn = "Morning & evening Adhkar",
                        subTextBn = "প্রতিটি নেক আমলের সাওয়াব ১০ গুণ পর্যন্ত বৃদ্ধি করা হয়",
                        subTextEn = "Every good deed is multiplied tenfold in reward",
                        hadithRef = "বুখারী ১৯৭৫",
                        hadithRefEn = "Bukhari 1975"
                    ),
                    AmalChecklistItem(
                        id = "bid_quran",
                        titleBn = "অন্তত আধা ঘণ্টা কুরআন তিলাওয়াত",
                        titleEn = "Recite Qur'an for at least 30 minutes",
                        subTextBn = "কুরআন কিয়ামতের দিন তিলাওয়াতকারীর জন্য শাফায়াত করবে",
                        subTextEn = "The Qur'an will intercede on Judgment Day for those who recite it",
                        hadithRef = "মুসলিম ৮০৪",
                        hadithRefEn = "Muslim 804"
                    ),
                    AmalChecklistItem(
                        id = "bid_dua",
                        titleBn = "ইফতার ও তাহাজ্জুদের মুহূর্তে দো'আ",
                        titleEn = "Du'a at Iftar & Tahajjud",
                        subTextBn = "রোজাদারের মুখনিঃসৃত দো'আ আল্লাহর নিকট প্রত্যাখ্যাত হয় না",
                        subTextEn = "The supplication of a fasting believer is never turned away",
                        hadithRef = "বায়হাকী",
                        hadithRefEn = "Bayhaqi"
                    )
                )
            )
        }

        // 6. Monday & Thursday Sunnah Fasting
        if (hijri.isMondayOrThursday) {
            val dayNameBn = if (dayOfWeek == Calendar.MONDAY) "সোমবার" else "বৃহস্পতিবার"
            val dayNameEn = if (dayOfWeek == Calendar.MONDAY) "Monday" else "Thursday"
            return DynamicAmalPlan(
                type = AmalPlanType.MONDAY_THURSDAY_FAST,
                titleBn = "$dayNameBn এর সুন্নাত আমল ও সিয়াম",
                titleEn = "$dayNameEn Sunnah Fasting & Deeds",
                subtitleBn = "সাপ্তাহিক আমলনামা আল্লাহর দরবারে পেশ করার বিশেষ দিন",
                subtitleEn = "The day deeds are presented before Allah",
                hijriDayBadge = "$dayNameBn সুন্নাত দিবস",
                hijriDayBadgeEn = "$dayNameEn Sunnah Day",
                badgeColorHex = "#81C784",
                hadithReference = "সুনানে তিরমিযী ৭৪৭",
                hadithReferenceEn = "Jami' at-Tirmidhi 747",
                isFastingDay = true,
                fastingVirtueBn = "নবীজি ﷺ বলেছেন: সোম ও বৃহস্পতিবার আল্লাহর নিকট বান্দার আমলনামা পেশ করা হয়; আমি চাই রোজাদার অবস্থায় আমার আমল পেশ হোক।",
                fastingVirtueEn = "The Prophet ﷺ said: Deeds are presented to Allah on Mondays and Thursdays, and I love that my deeds be presented while I am fasting.",
                checklistItems = listOf(
                    AmalChecklistItem(
                        id = "mon_thu_fast",
                        titleBn = "$dayNameBn এর সুন্নাত সিয়াম পালন",
                        titleEn = "Observe $dayNameEn Sunnah fast",
                        subTextBn = "আমলনামা পেশের দিনে রোজাদার থাকা রাসুলুল্লাহর ﷺ প্রিয় আমল",
                        subTextEn = "Fasting when deeds are presented was a beloved Sunnah of the Prophet ﷺ",
                        hadithRef = "তিরমিযী ৭৪৭",
                        hadithRefEn = "Tirmidhi 747"
                    ),
                    AmalChecklistItem(
                        id = "mon_thu_durood",
                        titleBn = "নবীজির ﷺ শানে ১০০ বার দরূদ পাঠ",
                        titleEn = "Recite Salawat 100 times upon the Prophet ﷺ",
                        subTextBn = "সোমবার নবীজির ﷺ জন্ম ও নবুওয়াত প্রাপ্তির বরকতময় দিন",
                        subTextEn = "Monday is the blessed day of the Prophet's ﷺ birth and revelation",
                        hadithRef = "মুসলিম ১১৬২",
                        hadithRefEn = "Muslim 1162"
                    ),
                    AmalChecklistItem(
                        id = "mon_thu_istighfar",
                        titleBn = "সাইয়্যিদুল ইস্তিগফার ও খাঁটি তওবা",
                        titleEn = "Sayyidul Istighfar & sincere repentance",
                        subTextBn = "যে ব্যক্তি সকালে ও সন্ধ্যায় এটি বিশ্বাস সহকারে পড়বে সে জান্নাতি",
                        subTextEn = "Whoever recites it morning and evening with conviction is among the people of Jannah",
                        hadithRef = "বুখারী ৬৩০৬",
                        hadithRefEn = "Bukhari 6306"
                    ),
                    AmalChecklistItem(
                        id = "mon_thu_charity",
                        titleBn = "গোপনে ক্ষুদ্র হলেও সদকাহ দান",
                        titleEn = "Give hidden Sadaqah, even if small",
                        subTextBn = "গোপন সদকাহ আল্লাহর ক্রোধ প্রশমিত করে",
                        subTextEn = "Secret charity extinguishes the anger of Allah",
                        hadithRef = "সহীহ জামে ৩৩৮৩",
                        hadithRefEn = "Sahih al-Jami 3383"
                    )
                )
            )
        }

        // 7. General Weekdays: Dynamic Rotating Daily Virtues Plan
        // Rotate virtue emphasis based on day of week to keep it fresh and actionable
        return when (dayOfWeek) {
            Calendar.SATURDAY -> DynamicAmalPlan(
                type = AmalPlanType.DAILY_SUNNAH_ROUTINE,
                titleBn = "শনিবারের আত্মশুদ্ধি ও দ্বীনি আমল",
                titleEn = "Saturday Spiritual Growth & Knowledge",
                subtitleBn = "${hijri.day} ${hijri.monthNameBn} • অন্তরের প্রশান্তি ও ইলম অন্বেষণ",
                subtitleEn = "${hijri.day} ${hijri.monthNameEn} • Heart Serenity & Seeking Knowledge",
                hijriDayBadge = "${hijri.day} ${hijri.monthNameBn} • শনিবার",
                hijriDayBadgeEn = "${hijri.day} ${hijri.monthNameEn} • Saturday",
                badgeColorHex = "#BA68C8",
                hadithReference = "সহীহ মুসলিম ২৬৯৯",
                hadithReferenceEn = "Sahih Muslim 2699",
                isFastingDay = false,
                checklistItems = listOf(
                    AmalChecklistItem(
                        id = "sat_ilm",
                        titleBn = "অন্তত একটি সহীহ হাদিস ও তাফসীর অধ্যয়ন",
                        titleEn = "Study at least one Sahih Hadith & Tafsir",
                        subTextBn = "যে ব্যক্তি ইলম অন্বেষণের পথে চলে, আল্লাহ তার জন্য জান্নাতের পথ সহজ করে দেন",
                        subTextEn = "Whoever treads a path seeking knowledge, Allah makes easy for him the path to Paradise",
                        hadithRef = "মুসলিম ২৬৯৯",
                        hadithRefEn = "Muslim 2699"
                    ),
                    AmalChecklistItem(
                        id = "sat_adhkar",
                        titleBn = "সকাল ও সন্ধ্যার মাসনুন হেফাজতের দো'আ",
                        titleEn = "Morning & evening protection Adhkar",
                        subTextBn = "বিসমিল্লাহিল্লাজি লা ইয়াদুররু মা'আসমিহি... ৩ বার",
                        subTextEn = "Recite 'Bismillahilladhi la yadurru ma'asmihi...' 3 times",
                        hadithRef = "তিরমিযী ৩৩৮৮",
                        hadithRefEn = "Tirmidhi 3388"
                    ),
                    AmalChecklistItem(
                        id = "sat_sadaqah",
                        titleBn = "কোনো অসহায়কে খাবার অথবা সদকাহ দান",
                        titleEn = "Feed someone in need or give Sadaqah",
                        subTextBn = "দান সদকাহ সম্পদে কখনোই কমতি ঘটায় না",
                        subTextEn = "Charity never decreases wealth",
                        hadithRef = "মুসলিম ২৫৮৮",
                        hadithRefEn = "Muslim 2588"
                    ),
                    AmalChecklistItem(
                        id = "sat_mulk",
                        titleBn = "ঘুমানোর পূর্বে সূরা আল-মুলক তিলাওয়াত",
                        titleEn = "Recite Surah Al-Mulk before sleep",
                        subTextBn = "কবরের আযাব থেকে সুরক্ষাকারী ৩০ আয়াতবিশিষ্ট সূরা",
                        subTextEn = "A 30-verse Surah that protects from the punishment of the grave",
                        hadithRef = "তিরমিযী ২৮৯১",
                        hadithRefEn = "Tirmidhi 2891"
                    )
                )
            )
            Calendar.SUNDAY -> DynamicAmalPlan(
                type = AmalPlanType.DAILY_SUNNAH_ROUTINE,
                titleBn = "রবিবারের তাওহীদ ও শোকরগোযারি আমল",
                titleEn = "Sunday Gratitude & Tawhid Routine",
                subtitleBn = "${hijri.day} ${hijri.monthNameBn} • নিয়ামতের কৃতজ্ঞতা ও ইখলাস",
                subtitleEn = "${hijri.day} ${hijri.monthNameEn} • Gratitude for Blessings & Sincerity",
                hijriDayBadge = "${hijri.day} ${hijri.monthNameBn} • রবিবার",
                hijriDayBadgeEn = "${hijri.day} ${hijri.monthNameEn} • Sunday",
                badgeColorHex = "#4DB6AC",
                hadithReference = "সূরা ইব্রাহীম: ৭",
                hadithReferenceEn = "Surah Ibrahim 14:7",
                isFastingDay = false,
                checklistItems = listOf(
                    AmalChecklistItem(
                        id = "sun_shukr",
                        titleBn = "১০০ বার 'আলহামদুলিল্লাহ' ও নিয়ামতের শোকর",
                        titleEn = "Recite 'Alhamdulillah' 100x with gratitude",
                        subTextBn = "যদি তোমরা কৃতজ্ঞতা স্বীকার কর, তবে আমি অবশ্যই তোমাদের বাড়িয়ে দেব",
                        subTextEn = "If you are grateful, I will surely increase you in favor",
                        hadithRef = "সূরা ইব্রাহীম ৭",
                        hadithRefEn = "Surah Ibrahim 7"
                    ),
                    AmalChecklistItem(
                        id = "sun_duha",
                        titleBn = "চাশতের (সালাতুদ দুহা) ২ বা ৪ রাকাত সালাত",
                        titleEn = "Pray 2 or 4 Rak'ahs of Salatul Duha",
                        subTextBn = "মানবদেহের ৩৬০টি গ্রন্থির সদকাহ স্বরূপ সালাতুদ দুহা যথেষ্ট",
                        subTextEn = "Two Rak'ahs of Duha suffice as charity for all 360 joints of the body",
                        hadithRef = "মুসলিম ৭২০",
                        hadithRefEn = "Muslim 720"
                    ),
                    AmalChecklistItem(
                        id = "sun_kinship",
                        titleBn = "আত্মীয়-স্বজনের খোঁজ নেওয়া ও সালাম বিনিময়",
                        titleEn = "Check in on relatives & spread Salam",
                        subTextBn = "যে রিযিক ও আয়ু বৃদ্ধি পেতে চায় সে যেন আত্মীয়তার বন্ধন রক্ষা করে",
                        subTextEn = "Whoever wishes for expanded provision and longevity should uphold ties of kinship",
                        hadithRef = "বুখারী ২০৬৭",
                        hadithRefEn = "Bukhari 2067"
                    ),
                    AmalChecklistItem(
                        id = "sun_tasbih",
                        titleBn = "১০০ বার 'সুবহানাল্লাহি ওয়া বিহামদিহি' পাঠ",
                        titleEn = "Recite 'SubhanAllahi wa bihamdihi' 100x",
                        subTextBn = "সমুদ্রের ফেনা পরিমাণ গুনাহ থাকলেও ক্ষমা করে দেওয়া হয়",
                        subTextEn = "Sins are forgiven even if they are as abundant as the foam of the sea",
                        hadithRef = "বুখারী ৬৪০৫",
                        hadithRefEn = "Bukhari 6405"
                    )
                )
            )
            Calendar.TUESDAY -> DynamicAmalPlan(
                type = AmalPlanType.DAILY_SUNNAH_ROUTINE,
                titleBn = "মঙ্গলবারের ইস্তিগফার ও তওবার আমল",
                titleEn = "Tuesday Istighfar & Renewal Plan",
                subtitleBn = "${hijri.day} ${hijri.monthNameBn} • গুনাহ মাফ ও মানসিক প্রশান্তি",
                subtitleEn = "${hijri.day} ${hijri.monthNameEn} • Seeking Forgiveness & Inner Peace",
                hijriDayBadge = "${hijri.day} ${hijri.monthNameBn} • মঙ্গলবার",
                hijriDayBadgeEn = "${hijri.day} ${hijri.monthNameEn} • Tuesday",
                badgeColorHex = "#4DD0E1",
                hadithReference = "সহীহ বুখারী ৬৩০৭",
                hadithReferenceEn = "Sahih al-Bukhari 6307",
                isFastingDay = false,
                checklistItems = listOf(
                    AmalChecklistItem(
                        id = "tue_istighfar",
                        titleBn = "১০০ বার 'আস্তাগফিরুল্লাহ' পাঠ",
                        titleEn = "Recite 'Astaghfirullah' 100 times",
                        subTextBn = "নবীজি ﷺ দিনে ৭০ বারেরও বেশি আল্লাহর কাছে ক্ষমা চাইতেন",
                        subTextEn = "The Prophet ﷺ sought Allah's forgiveness more than 70 times every day",
                        hadithRef = "বুখারী ৬৩০৭",
                        hadithRefEn = "Bukhari 6307"
                    ),
                    AmalChecklistItem(
                        id = "tue_tahajjud",
                        titleBn = "রাতের শেষ তৃতীয়াংশে তাহাজ্জুদ ও রোনাজারি",
                        titleEn = "Wake for Tahajjud in the last third of the night",
                        subTextBn = "আল্লাহ প্রথম আসমানে নেমে এসে বান্দার দো'আ কবুল করেন",
                        subTextEn = "Allah descends to the lowest heaven and answers those who supplicate",
                        hadithRef = "বুখারী ১১৪৫",
                        hadithRefEn = "Bukhari 1145"
                    ),
                    AmalChecklistItem(
                        id = "tue_tongue",
                        titleBn = "অনর্থক কথা ও গীবত থেকে জিহ্বা হেফাজত",
                        titleEn = "Guard tongue from backbiting & idle talk",
                        subTextBn = "যে ব্যক্তি আল্লাহ ও আখেরাতে বিশ্বাস করে সে যেন ভালো কথা বলে অথবা চুপ থাকে",
                        subTextEn = "Whoever believes in Allah and the Last Day should speak good or remain silent",
                        hadithRef = "বুখারী ৬৪৭৫",
                        hadithRefEn = "Bukhari 6475"
                    ),
                    AmalChecklistItem(
                        id = "tue_quran",
                        titleBn = "সূরা ওয়াকিয়াহ বা সূরা আর-রহমান তিলাওয়াত",
                        titleEn = "Recite Surah Al-Waqi'ah or Ar-Rahman",
                        subTextBn = "কুরআনের সাথে সম্পর্ক অন্তরের যাবতীয় দুশ্চিন্তা দূর করে",
                        subTextEn = "Closeness to the Qur'an removes anxiety from the heart",
                        hadithRef = "বায়হাকী",
                        hadithRefEn = "Bayhaqi"
                    )
                )
            )
            Calendar.WEDNESDAY -> DynamicAmalPlan(
                type = AmalPlanType.DAILY_SUNNAH_ROUTINE,
                titleBn = "বুধবারের দো'আ ও তাওয়াক্কুল এর আমল",
                titleEn = "Wednesday Du'a & Tawakkul Plan",
                subtitleBn = "${hijri.day} ${hijri.monthNameBn} • আল্লাহর ওপর পূর্ণ নির্ভরতা",
                subtitleEn = "${hijri.day} ${hijri.monthNameEn} • Complete Trust in Allah",
                hijriDayBadge = "${hijri.day} ${hijri.monthNameBn} • বুধবার",
                hijriDayBadgeEn = "${hijri.day} ${hijri.monthNameEn} • Wednesday",
                badgeColorHex = "#AED581",
                hadithReference = "মুসনাদে আহমাদ ১৪৪২৮",
                hadithReferenceEn = "Musnad Ahmad 14428",
                isFastingDay = false,
                checklistItems = listOf(
                    AmalChecklistItem(
                        id = "wed_dua",
                        titleBn = "জোহর ও আসরের মধ্যবর্তী সময়ে বিশেষ দো'আ",
                        titleEn = "Make special Du'a between Dhuhr & Asr",
                        subTextBn = "জাবের (রা) বর্ণিত: বুধবার জোহর ও আসরের মাঝে দো'আ কবুল হয়",
                        subTextEn = "Jabir (RA) reported that Du'a on Wednesday between Dhuhr and Asr is answered",
                        hadithRef = "মুসনাদে আহমাদ ১৪৪২৮",
                        hadithRefEn = "Ahmad 14428"
                    ),
                    AmalChecklistItem(
                        id = "wed_hawqala",
                        titleBn = "১০০ বার 'লা হাওলা ওয়ালা কুওয়্যাতা ইল্লা বিল্লাহ'",
                        titleEn = "Recite 'La hawla wa la quwwata illa billah' 100x",
                        subTextBn = "এটি জান্নাতের রত্নভাণ্ডারের একটি অমূল্য গুপ্তধন",
                        subTextEn = "It is one of the precious treasures of Paradise",
                        hadithRef = "বুখারী ৪২০৫",
                        hadithRefEn = "Bukhari 4205"
                    ),
                    AmalChecklistItem(
                        id = "wed_parents",
                        titleBn = "পিতা-মাতার জন্য বিশেষভাবে দো'আ করা",
                        titleEn = "Make sincere Du'a for your parents",
                        subTextBn = "'রব্বির হামহুমা কামা রববায়ানী সগীরা' পাঠ",
                        subTextEn = "Recite 'Rabbir hamhuma kama rabbayani saghira'",
                        hadithRef = "সূরা বনী ইসরাঈল ২৪",
                        hadithRefEn = "Al-Isra 17:24"
                    ),
                    AmalChecklistItem(
                        id = "wed_baqarah",
                        titleBn = "সূরা বাকারার শেষ দুই আয়াত আমানুর রাসুল পাঠ",
                        titleEn = "Recite last 2 verses of Surah Al-Baqarah",
                        subTextBn = "রাতের বেলা এটি পাঠ করলে সমস্ত অনিষ্ট থেকে রক্ষার জন্য যথেষ্ট",
                        subTextEn = "Reciting them at night suffices for protection against all harm",
                        hadithRef = "বুখারী ৫০০৯",
                        hadithRefEn = "Bukhari 5009"
                    )
                )
            )
            else -> DynamicAmalPlan(
                type = AmalPlanType.DAILY_SUNNAH_ROUTINE,
                titleBn = "দৈনন্দিন মুমিন জীবনের সুন্নাত রুটিন",
                titleEn = "Daily Believer's Sunnah Routine",
                subtitleBn = "${hijri.day} ${hijri.monthNameBn} • তাকওয়া ও সাওয়াবের আমল",
                subtitleEn = "${hijri.day} ${hijri.monthNameEn} • Taqwa & Righteous Deeds",
                hijriDayBadge = "${hijri.day} ${hijri.monthNameBn}",
                hijriDayBadgeEn = "${hijri.day} ${hijri.monthNameEn}",
                badgeColorHex = "#00E676",
                hadithReference = "সহীহ বুখারী ৬৪৬৫",
                hadithReferenceEn = "Sahih al-Bukhari 6465",
                isFastingDay = false,
                checklistItems = listOf(
                    AmalChecklistItem(
                        id = "gen_5salat",
                        titleBn = "৫ ওয়াক্ত নামাজ প্রথম ওয়াক্তে জামায়াতে আদায়",
                        titleEn = "Pray all 5 daily Salah on time",
                        subTextBn = "আল্লাহর নিকট সবচেয়ে প্রিয় আমল সময়মত নামাজ আদায় করা",
                        subTextEn = "The deed most beloved to Allah is prayer performed on time",
                        hadithRef = "বুখারী ৫২৭",
                        hadithRefEn = "Bukhari 527"
                    ),
                    AmalChecklistItem(
                        id = "gen_ayat_kursi",
                        titleBn = "প্রত্যেক ফরজ নামাজের পর আয়াতুল কুরসী পাঠ",
                        titleEn = "Recite Ayat al-Kursi after every Fard prayer",
                        subTextBn = "মৃত্যু ব্যতীত জান্নাতে প্রবেশে কোনো বাধা থাকে না",
                        subTextEn = "Nothing stands between the reciter and Paradise except death",
                        hadithRef = "নাসায়ী ৯৯২৮",
                        hadithRefEn = "Nasa'i 9928"
                    ),
                    AmalChecklistItem(
                        id = "gen_durood",
                        titleBn = "দিনের শুরুতে ও শেষে ১০ বার দরূদ শরীফ",
                        titleEn = "Recite Salawat 10x morning & evening",
                        subTextBn = "কিয়ামতের দিন নবীজির ﷺ বিশেষ শাফায়াত লাভ হবে",
                        subTextEn = "Earns the Prophet's ﷺ special intercession on Judgment Day",
                        hadithRef = "তাবারানী",
                        hadithRefEn = "Tabarani"
                    ),
                    AmalChecklistItem(
                        id = "gen_mulk",
                        titleBn = "রাতে ঘুমানোর পূর্বে সূরা মুলক তিলাওয়াত",
                        titleEn = "Recite Surah Al-Mulk before sleeping",
                        subTextBn = "কবরের আযাব থেকে রক্ষাকারী পবিত্র সূরা",
                        subTextEn = "Protects from the trial and torment of the grave",
                        hadithRef = "তিরমিযী ২৮৯১",
                        hadithRefEn = "Tirmidhi 2891"
                    )
                )
            )
        }
    }
}
