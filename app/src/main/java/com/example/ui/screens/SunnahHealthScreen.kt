package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

data class SunnahRemedy(
    val id: String,
    val nameBengali: String,
    val nameArabic: String,
    val description: String,
    val hadithCitation: String,
    val hadithTextBengali: String,
    val benefits: List<String>,
    val howToUse: String,
    val nameEnglish: String = "",
    val descriptionEnglish: String = "",
    val hadithCitationEnglish: String = "",
    val hadithTextEnglish: String = "",
    val benefitsEnglish: List<String> = emptyList(),
    val howToUseEnglish: String = ""
) {
    fun localizedName(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) != "bn") nameEnglish.ifBlank { nameBengali } else nameBengali

    fun localizedDescription(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) != "bn") descriptionEnglish.ifBlank { description } else description

    fun localizedCitation(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) != "bn") hadithCitationEnglish.ifBlank { hadithCitation } else hadithCitation

    fun localizedHadithText(contentLang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(contentLang) != "bn") hadithTextEnglish.ifBlank { hadithTextBengali } else hadithTextBengali

    fun localizedBenefits(lang: String): List<String> =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) != "bn" && benefitsEnglish.isNotEmpty()) benefitsEnglish else benefits

    fun localizedHowToUse(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) != "bn") howToUseEnglish.ifBlank { howToUse } else howToUse
}

data class FastingScheduleItem(
    val titleBengali: String,
    val subtitle: String,
    val hadithCitation: String,
    val virtueBengali: String,
    val isWeekly: Boolean = false,
    val hijriDays: String = "",
    val titleEnglish: String = "",
    val subtitleEnglish: String = "",
    val hadithCitationEnglish: String = "",
    val virtueEnglish: String = "",
    val hijriDaysEnglish: String = ""
) {
    fun localizedTitle(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) != "bn") titleEnglish.ifBlank { titleBengali } else titleBengali

    fun localizedSubtitle(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) != "bn") subtitleEnglish.ifBlank { subtitle } else subtitle

    fun localizedCitation(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) != "bn") hadithCitationEnglish.ifBlank { hadithCitation } else hadithCitation

    fun localizedVirtue(contentLang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(contentLang) != "bn") virtueEnglish.ifBlank { virtueBengali } else virtueBengali
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SunnahHealthScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiLang = LocalAppLanguage.current
    val contentLang = LocalContentLanguage.current
    val haptic = LocalHapticFeedback.current
    var selectedTab by remember { mutableStateOf(0) } // 0: Nafl Fasting, 1: Tibbun Nabawi
    var completedFastsThisMonth by remember { mutableStateOf(2) }
    var reminderEnabled by remember { mutableStateOf(true) }

    val remedies = remember {
        listOf(
            SunnahRemedy(
                id = "black_seed",
                nameBengali = "কালোজিরা (হাব্বাতুস সাওদা)",
                nameArabic = "الحَبَّة السَّوْدَاء",
                description = "রোগ প্রতিরোধ ক্ষমতা ও সর্দি-কাশি নিরাময়ে অনন্য ভেষজ",
                hadithCitation = "সহীহ বুখারী: ৫৬৮৭, সহীহ মুসলিম: ২২১৫",
                hadithTextBengali = "রাসূলুল্লাহ (সা.) ইরশাদ করেছেন: 'তোমরা অবশ্যই কালোজিরা ব্যবহার করবে। কেননা এতে মৃত্যু ব্যতীত সকল রোগের উপশম রয়েছে।'",
                benefits = listOf(
                    "রোগ প্রতিরোধ ক্ষমতা (Immunity) বহুগুণে বৃদ্ধি করে",
                    "শ্বাসকষ্ট ও দীর্ঘস্থায়ী সর্দি-কাশিতে অত্যন্ত উপকারী",
                    "হজমরস বৃদ্ধি এবং পেটের গ্যাস দূর করে"
                ),
                howToUse = "প্রতিদিন সকালে এক চিমটি কালোজিরা চিবিয়ে অথবা ১ চা চামচ খাঁটি মধুর সাথে কালোজিরা তেল মিশিয়ে সেব্য।",
                nameEnglish = "Black Seed (Habbatul Barakah)",
                descriptionEnglish = "Remarkable Prophetic herb for immunity and respiratory wellness",
                hadithCitationEnglish = "Sahih al-Bukhari: 5687, Sahih Muslim: 2215",
                hadithTextEnglish = "The Messenger of Allah ﷺ said: 'Use this black seed regularly, because it contains a cure for every disease except death.'",
                benefitsEnglish = listOf(
                    "Significantly strengthens natural immune response",
                    "Soothes seasonal congestion, cough, and breathing discomfort",
                    "Aids digestive enzymes and reduces bloating"
                ),
                howToUseEnglish = "Chew a pinch of black seeds each morning or mix black seed oil with a teaspoon of raw honey."
            ),
            SunnahRemedy(
                id = "pure_honey",
                nameBengali = "খাঁটি মধু (আসাল)",
                nameArabic = "العَسَل",
                description = "কুরআনে ঘোষিত সার্বজনীন শিফা ও শক্তির উৎস",
                hadithCitation = "সূরা আন-নাহল: ৬৯, সহীহ বুখারী: ৫৬৮৪",
                hadithTextBengali = "আল্লাহ তা'আলা ইরশাদ করেন: 'তাদের উদর হতে নির্গত হয় বিবিধ বর্ণের পানীয়, যাতে রয়েছে মানুষের জন্য নিরাময়।' রাসুল (সা.) বলেন: 'মধু ও কুরআনের মাধ্যমে শিফা গ্রহণ করো।'",
                benefits = listOf(
                    "প্রাকৃতিক অ্যান্টি-ব্যাকটেরিয়াল ও অ্যান্টি-অক্সিডেন্ট সমৃদ্ধ",
                    "গলা ব্যথা, কাশি ও দুর্বলতা দ্রুত দূর করে",
                    "পাকস্থলীর আলসার ও প্রদাহ কমাতে সাহায্য করে"
                ),
                howToUse = "সকালে খালি পেটে এক গ্লাস কুসুম গরম পানিতে ১ টেবিল চামচ খাঁটি মধু মিশিয়ে পান করা সুন্নাত।",
                nameEnglish = "Raw Honey (Al-'Asal)",
                descriptionEnglish = "Quranically affirmed source of healing and vitality",
                hadithCitationEnglish = "Surah an-Nahl: 69, Sahih al-Bukhari: 5684",
                hadithTextEnglish = "Allah says: 'There emerges from their bellies a drink, varying in colors, in which there is healing for people.' The Prophet ﷺ also advised seeking healing in honey and the Quran.",
                benefitsEnglish = listOf(
                    "Rich in natural antibacterial and antioxidant compounds",
                    "Quickly relieves sore throat, cough, and fatigue",
                    "Soothes stomach lining and digestive inflammation"
                ),
                howToUseEnglish = "Following the Sunnah, dissolve one tablespoon of raw honey in lukewarm water on an empty stomach."
            ),
            SunnahRemedy(
                id = "olive_oil",
                nameBengali = "যায়তুনের তেল (যাইতুয যায়তুন)",
                nameArabic = "زَيْت الزَّيْتُون",
                description = "বরকতময় বৃক্ষের তেল যা ত্বক ও হৃদযন্ত্রের সুরক্ষা দেয়",
                hadithCitation = "জামে আত-তিরমিযী: ১৮৫১, সুনান ইবনে মাজাহ: ৩৩১৯",
                hadithTextBengali = "রাসূলুল্লাহ (সা.) বলেছেন: 'তোমরা যায়তুনের তেল খাও এবং শরীরে মালিশ করো; কেননা এটি একটি বরকতময় পবিত্র বৃক্ষ থেকে উৎপাদিত।'",
                benefits = listOf(
                    "সুস্থ কোলেস্টেরল বজায় রাখতে ও হৃদরোগের ঝুঁকি কমাতে সহায়ক",
                    "শুষ্ক ত্বক ও চুল মসৃণ ও উজ্জ্বল করে",
                    "জয়েন্টের ব্যথা ও প্রদাহ উপশমে মালিশ হিসেবে কার্যকর"
                ),
                howToUse = "সালাদ বা খাবারের সাথে এক্সট্রা ভার্জিন অলিভ অয়েল খাওয়া এবং গোসলের পর ত্বকে মালিশ করা উত্তম।",
                nameEnglish = "Olive Oil (Zayt az-Zaytun)",
                descriptionEnglish = "Oil from the blessed tree protecting heart and skin",
                hadithCitationEnglish = "Jami' at-Tirmidhi: 1851, Sunan Ibn Majah: 3319",
                hadithTextEnglish = "The Messenger of Allah ﷺ said: 'Eat olive oil and anoint yourselves with it, for indeed it comes from a blessed tree.'",
                benefitsEnglish = listOf(
                    "Supports healthy cholesterol and cardiovascular wellness",
                    "Nourishes dry skin and strengthens hair naturally",
                    "Effective for soothing joint stiffness via topical massage"
                ),
                howToUseEnglish = "Consume extra-virgin olive oil with meals/salads and apply lightly to skin after bathing."
            ),
            SunnahRemedy(
                id = "ajwa_dates",
                nameBengali = "আজওয়া খেজুর (তামরুল আজওয়া)",
                nameArabic = "تَمْر العَجْوَة",
                description = "মদিনা মুনাওয়ারার জান্নাতী খেজুর ও বিষ-যাদুর প্রতিরোধক",
                hadithCitation = "সহীহ বুখারী: ৫৪৪৫, সহীহ মুসলিম: ২০৪৭",
                hadithTextBengali = "রাসূলুল্লাহ (সা.) বলেছেন: 'যে ব্যক্তি প্রতিদিন সকালে সাতটি আজওয়া খেজুর খাবে, সেদিন কোনো বিষ কিংবা যাদু তার কোনো ক্ষতি করতে পারবে না।'",
                benefits = listOf(
                    "তাত্ক্ষণিক শারীরিক শক্তি যোগায় ও ক্লান্তি দূর করে",
                    "রক্তস্বল্পতা দূর করে এবং উচ্চ রক্তচাপ নিয়ন্ত্রণে সহায়তা করে",
                    "গর্ভবতী ও প্রসূতি মায়েদের শক্তির জন্য অত্যন্ত পুষ্টিকর"
                ),
                howToUse = "সকালে ফজর নামাজের পর খালি পেটে ৭টি অথবা ৩টি খেজুর চিবিয়ে খাওয়া সুন্নাহ।",
                nameEnglish = "Ajwa Dates (Tamr al-'Ajwah)",
                descriptionEnglish = "Blessed Madinah dates for energy and spiritual protection",
                hadithCitationEnglish = "Sahih al-Bukhari: 5445, Sahih Muslim: 2047",
                hadithTextEnglish = "The Messenger of Allah ﷺ said: 'Whoever eats seven Ajwa dates in the morning will not be harmed by poison or magic on that day.'",
                benefitsEnglish = listOf(
                    "Provides clean, rapid energy and relieves fatigue",
                    "Supports healthy iron levels and blood pressure balance",
                    "Highly nourishing for expectant and nursing mothers"
                ),
                howToUseEnglish = "Eat 7 (or 3) Ajwa dates in the morning after Fajr on an empty stomach."
            ),
            SunnahRemedy(
                id = "talbina",
                nameBengali = "তালবিনা (যবের ছাতু ও দুধের পায়েস)",
                nameArabic = "التَّلْبِينَة",
                description = "শোকগ্রস্ত ও অসুস্থ ব্যক্তির হৃদয়ে প্রশান্তি দানকারী খাবার",
                hadithCitation = "সহীহ বুখারী: ৫৬৮৯, সহীহ মুসলিম: ২২১৬",
                hadithTextBengali = "উম্মুল মুমিনীন আয়েশা (রা.) থেকে বর্ণিত, রাসূলুল্লাহ (সা.) বলেছেন: 'তালবিনা রোগীর হৃদয়ে প্রশান্তি এনে দেয় এবং কিছুটা শোক-বেদনা দূর করে দেয়।'",
                benefits = listOf(
                    "বিষণ্ণতা, মানসিক চাপ ও উদ্বেগ উপশম করে",
                    "উচ্চ আঁশযুক্ত হওয়ায় কোষ্ঠকাঠিন্য প্রতিরোধ করে",
                    "দুর্বল ও রোগাক্রান্ত রোগীর হজমের ওপর কোনো চাপ সৃষ্টি করে না"
                ),
                howToUse = "যবের গুঁড়া (Barley powder) দুধ ও পানির সাথে জ্বাল দিয়ে পাতলা পায়েস তৈরি করে মধু সহযোগে খেতে হয়।",
                nameEnglish = "Talbinah (Barley & Milk Porridge)",
                descriptionEnglish = "Soothing Prophetic nourishment that comforts the heart",
                hadithCitationEnglish = "Sahih al-Bukhari: 5689, Sahih Muslim: 2216",
                hadithTextEnglish = "Aisha (RA) narrated that the Messenger of Allah ﷺ said: 'Talbinah gives rest to the heart of the patient and relieves some of their grief.'",
                benefitsEnglish = listOf(
                    "Helps ease sadness, mental fatigue, and emotional stress",
                    "High soluble fiber supports gentle digestion",
                    "Ideal light nourishment during recovery from illness"
                ),
                howToUseEnglish = "Simmer stone-ground barley flour with milk and water until smooth, then sweeten with honey."
            ),
            SunnahRemedy(
                id = "hijama_cupping",
                nameBengali = "হিজামা (রক্তমোক্ষণ / Cupping)",
                nameArabic = "الحِجَامَة",
                description = "শরীরের দূষিত রক্ত নিষ্কাশনের শ্রেষ্ঠ নববী চিকিৎসা",
                hadithCitation = "সহীহ বুখারী: ৫৬৯৬, সুনান আবু দাউদ: ৩৮৫৭",
                hadithTextBengali = "রাসূলুল্লাহ (সা.) ইরশাদ করেছেন: 'তোমরা যেসব পদ্ধতিতে চিকিৎসা গ্রহণ করো, তার মধ্যে সর্বোত্তম চিকিৎসা হলো হিজামা করা।'",
                benefits = listOf(
                    "দীর্ঘস্থায়ী মাথাব্যথা ও মাইগ্রেনের চমৎকার চিকিৎসা",
                    "রক্ত সঞ্চালন বৃদ্ধি করে এবং ঘাড় ও পিঠের পেশীর টান দূর করে",
                    "চন্দ্রমাসের ১৭, ১৯ ও ২১ তারিখে হিজামা করা বিশেষভাবে মুস্তাহাব"
                ),
                howToUse = "একজন অভিজ্ঞ ও প্রশিক্ষণপ্রাপ্ত সুন্নাহ প্র্যাকটিশনারের মাধ্যমে জীবাণুমুক্ত কাপ ব্যবহারে হিজামা গ্রহণ করুন।",
                nameEnglish = "Hijama (Wet Cupping Therapy)",
                descriptionEnglish = "Time-honored Prophetic therapy for circulation & relief",
                hadithCitationEnglish = "Sahih al-Bukhari: 5696, Sunan Abi Dawud: 3857",
                hadithTextEnglish = "The Messenger of Allah ﷺ said: 'Indeed, the best of remedies you have is cupping (Hijama).'",
                benefitsEnglish = listOf(
                    "Helps alleviate chronic headaches and tension migraines",
                    "Improves local micro-circulation and eases neck/back stiffness",
                    "Recommended especially on the 17th, 19th, and 21st of the Hijri month"
                ),
                howToUseEnglish = "Consult a certified, hygienic practitioner using sterile single-use cups."
            )
        )
    }

    val fastingGuides = remember {
        listOf(
            FastingScheduleItem(
                titleBengali = "সোমবার ও বৃহস্পতিবারের নফল রোজা",
                subtitle = "সাপ্তাহিক সুন্নাত",
                hadithCitation = "জামে আত-তিরমিযী: ৭৪৭",
                virtueBengali = "রাসূলুল্লাহ (সা.) বলেন: 'সোমবার ও বৃহস্পতিবারে আল্লাহর দরবারে বান্দার আমল পেশ করা হয়। আর আমি পছন্দ করি যে, রোজা থাকা অবস্থায় যেন আমার আমল পেশ করা হোক।'",
                isWeekly = true,
                titleEnglish = "Monday & Thursday Voluntary Fasts",
                subtitleEnglish = "Weekly Sunnah",
                hadithCitationEnglish = "Jami' at-Tirmidhi: 747",
                virtueEnglish = "The Prophet ﷺ said: 'Deeds are presented on Monday and Thursday, and I love that my deeds be presented while I am fasting.'"
            ),
            FastingScheduleItem(
                titleBengali = "আইয়ামে বিজ (১৩, ১৪ ও ১৫ তারিখের রোজা)",
                subtitle = "প্রতি হিজরি চন্দ্র মাসের মধ্যভাগে",
                hadithCitation = "সুনান আবু দাউদ: ২৪৪৯",
                virtueBengali = "রাসূলুল্লাহ (সা.) ইরশাদ করেন: 'প্রতি মাসে তিন দিন রোজা রাখা সারা বছর রোজা রাখার সমান।' (১৩, ১৪ ও ১৫ তারিখের উজ্জ্বল চাঁদের রাতসমূহের দিন)।",
                hijriDays = "১৩, ১৪, ১৫ চন্দ্রমাস",
                titleEnglish = "Ayyam al-Bid (13th, 14th & 15th Hijri)",
                subtitleEnglish = "Middle of every lunar month",
                hadithCitationEnglish = "Sunan Abi Dawud: 2449",
                virtueEnglish = "The Prophet ﷺ said: 'Fasting three days every month is equivalent to fasting the entire year.'",
                hijriDaysEnglish = "13, 14, 15 Hijri"
            ),
            FastingScheduleItem(
                titleBengali = "আশুরা ও তাসু'আ (মহররমের ৯ ও ১০ তারিখ)",
                subtitle = "পূর্ববর্তী ১ বছরের গুনাহ মাফের সুসংবাদ",
                hadithCitation = "সহীহ মুসলিম: ১১৬২",
                virtueBengali = "নবী করীম (সা.) বলেন: 'আমি আল্লাহর কাছে আশা রাখি যে, আশুরার রোজা বিগত এক বছরের গুনাহের কাফফারা হবে।' ইহুদীদের বিপরীত করার জন্য ৯ ও ১০ মহররম রোজা রাখা মুস্তাহাব।",
                titleEnglish = "Ashura & Tasu'a (9th & 10th Muharram)",
                subtitleEnglish = "Expiation for the previous year's minor sins",
                hadithCitationEnglish = "Sahih Muslim: 1162",
                virtueEnglish = "The Prophet ﷺ said: 'I hope that Allah will accept the fast of Ashura as expiation for the past year.' Fasting the 9th alongside the 10th is recommended."
            ),
            FastingScheduleItem(
                titleBengali = "আরাফার দিনের রোজা (৯ জিলহজ্ব)",
                subtitle = "হজের দিন (হাজীদের ব্যতীত অন্যদের জন্য)",
                hadithCitation = "সহীহ মুসলিম: ১১৬২",
                virtueBengali = "রাসূলুল্লাহ (সা.) বলেন: 'আরাফার দিনের রোজা বিগত এক বছর ও আগামী এক বছরের গুনাহের কাফফারা হিসেবে গৃহীত হয়।'",
                titleEnglish = "Day of Arafah (9th Dhul-Hijjah)",
                subtitleEnglish = "For non-pilgrims",
                hadithCitationEnglish = "Sahih Muslim: 1162",
                virtueEnglish = "The Prophet ﷺ said: 'Fasting on the Day of Arafah expiates the sins of the preceding year and the coming year.'"
            ),
            FastingScheduleItem(
                titleBengali = "শাওয়ালের ছয় রোজা",
                subtitle = "রমজানের পরপরই সারা বছরের সাওয়াব",
                hadithCitation = "সহীহ মুসলিম: ১১৬৪",
                virtueBengali = "যে ব্যক্তি রমজানের রোজা রাখল অতঃপর শাওয়াল মাসে ছয়টি রোজা রাখল, সে যেন সারা বছরই রোজা রাখল।",
                titleEnglish = "Six Days of Shawwal",
                subtitleEnglish = "Reward of fasting an entire year",
                hadithCitationEnglish = "Sahih Muslim: 1164",
                virtueEnglish = "Whoever fasts Ramadan and follows it with six days of Shawwal, it is as if they fasted the entire year."
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("sunnah_health_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = uiText("পিছনে যান", "Back"),
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = uiText("সুন্নাহ হেলথ ও নফল রোজা", "Sunnah Health & Fasting"),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = uiText("তিব্বুন নববী (Prophetic Medicine) ও নফল সিয়াম গাইড", "Tibbun Nabawi & Voluntary Fasting Guide"),
                    style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 11.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Selector Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkEmeraldElevated)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedTab == 0) ChampagneGold else Color.Transparent)
                    .clickable {
                        selectedTab = 0
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                    }
                    .padding(vertical = 8.dp)
                    .testTag("sunnah_tab_fasting"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiText("নফল রোজা গাইড", "Nafl Fasting Guide"),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (selectedTab == 0) ObsidianBackground else TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedTab == 1) ChampagneGold else Color.Transparent)
                    .clickable {
                        selectedTab = 1
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                    }
                    .padding(vertical = 8.dp)
                    .testTag("sunnah_tab_remedies"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiText("তিব্বুন নববী (ভেষজ)", "Tibbun Nabawi"),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (selectedTab == 1) ObsidianBackground else TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedTab == 0) {
            // Fasting Companion View
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Monthly Tracker Card
                item {
                    FrostedGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = DarkEmeraldSurface,
                        borderColor = MintGlow.copy(alpha = 0.4f)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = uiText("চলতি মাসে নফল রোজা", "Nafl Fasts This Month"),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    )
                                    Text(
                                        text = uiText("সুন্নাহ অনুসরণে আত্মশুদ্ধি অর্জন", "Spiritual renewal through Sunnah fasting"),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = SlateGrayMuted,
                                            fontSize = 11.sp
                                        )
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            if (completedFastsThisMonth > 0) completedFastsThisMonth--
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Filled.Remove, contentDescription = null, tint = SlateGrayMuted)
                                    }
                                    Text(
                                        text = uiText(
                                            "${formatLocalizedInt(completedFastsThisMonth, uiLang)} দিন",
                                            "$completedFastsThisMonth days"
                                        ),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = MintGlow,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    )
                                    IconButton(
                                        onClick = {
                                            completedFastsThisMonth++
                                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Filled.Add, contentDescription = null, tint = ChampagneGold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Reminder row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkEmeraldElevated)
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.NotificationsActive,
                                        contentDescription = null,
                                        tint = if (reminderEnabled) ChampagneGold else SlateGrayMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = uiText("সোমবার/বৃহস্পতিবারের সিয়াম রিমাইন্ডার", "Monday/Thursday Fasting Reminder"),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextPrimary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }

                                Switch(
                                    checked = reminderEnabled,
                                    onCheckedChange = { reminderEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = ChampagneGold,
                                        checkedTrackColor = ChampagneGold.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.height(24.dp)
                                )
                            }
                        }
                    }
                }

                items(fastingGuides) { item ->
                    FastingItemCard(item = item, uiLang = uiLang, contentLang = contentLang)
                }
            }
        } else {
            // Tibbun Nabawi (Prophetic Remedies) View
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(DarkEmeraldElevated, ObsidianBackground)
                                )
                            )
                            .border(0.5.dp, ChampagneGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Spa,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = uiText("তিব্বুন নববী ও সুন্নাহ স্বাস্থ্যবিজ্ঞান", "Tibbun Nabawi & Prophetic Wellness"),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = ChampagneGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                )
                                Text(
                                    text = uiText(
                                        "রাসূলুল্লাহ (সা.) কর্তৃক প্রশংসিত প্রাকৃতিক পথ্য ও প্রমাণিত নিরাময় পদ্ধতি",
                                        "Natural remedies & wellness practices praised by the Prophet ﷺ"
                                    ),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SlateGrayMuted,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }

                items(remedies) { remedy ->
                    RemedyCard(remedy = remedy, uiLang = uiLang, contentLang = contentLang)
                }
            }
        }
    }
}

@Composable
fun FastingItemCard(
    item: FastingScheduleItem,
    uiLang: String = LocalAppLanguage.current,
    contentLang: String = LocalContentLanguage.current
) {
    FrostedGlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = DarkEmeraldSurface
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = item.localizedTitle(uiLang),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = item.localizedSubtitle(uiLang),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MintGlow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(DarkEmeraldElevated)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = item.localizedCitation(uiLang),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SlateGrayMuted,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.localizedVirtue(contentLang),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextPrimary.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            )
        }
    }
}

@Composable
fun RemedyCard(
    remedy: SunnahRemedy,
    uiLang: String = LocalAppLanguage.current,
    contentLang: String = LocalContentLanguage.current
) {
    var isExpanded by remember { mutableStateOf(false) }

    FrostedGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        backgroundColor = DarkEmeraldSurface
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = remedy.localizedName(uiLang),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = ChampagneGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                    }
                    Text(
                        text = remedy.nameArabic,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = AmiriFontFamily,
                            color = MintGlow,
                            fontSize = 14.sp
                        )
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = SlateGrayMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = remedy.localizedDescription(uiLang),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextPrimary,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Hadith Quote Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkEmeraldElevated)
                    .border(0.5.dp, HairlineBorder, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = remedy.localizedCitation(uiLang),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = remedy.localizedHadithText(contentLang),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextPrimary.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = uiText("চিকিৎসা ও স্বাস্থ্যগত উপকারিতা:", "Health & Wellness Benefits:"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    remedy.localizedBenefits(uiLang).forEach { b ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(MintGlow)
                            )
                            Text(
                                text = b,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MintGlow.copy(alpha = 0.12f))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = uiText("সেবন ও ব্যবহারের সঠিক পদ্ধতি:", "Recommended Usage:"),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MintGlow,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = remedy.localizedHowToUse(uiLang),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextPrimary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
