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
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JanazahWasiyyahScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiLang = LocalAppLanguage.current
    val haptic = LocalHapticFeedback.current
    val clipboardManager = LocalClipboardManager.current
    var selectedTab by remember { mutableStateOf(0) } // 0: Janazah & Burial, 1: Will Builder
    var isWasiyyahCopied by remember { mutableStateOf(false) }

    val defaultAdviceBn = "আমার পরিবার ও সন্তানদের প্রতি শেষ উপদেশ: জীবনের সর্বাবস্থায় তাকওয়া অবলম্বন করবে, পাঁচ ওয়াক্ত নামাজ জামাতে আদায় করবে, হারাম উপার্জন ও বিদআত থেকে বেঁচে থাকবে এবং পরস্পরে ভ্রাতৃত্ব বজায় রাখবে।"
    val defaultAdviceEn = "Final advice to my family and children: uphold Taqwa in all circumstances, establish the five daily prayers in congregation, stay away from unlawful earnings and innovations, and preserve brotherhood and unity among one another."

    // Wasiyyah Form State
    var testatorName by remember { mutableStateOf("") }
    var debtsText by remember { mutableStateOf("") }
    var amanahText by remember { mutableStateOf("") }
    var missedWorshipFidya by remember { mutableStateOf("") }
    var charitableBequest by remember { mutableStateOf("") }
    var adviceToFamily by remember { mutableStateOf(localizedText(uiLang, defaultAdviceBn, defaultAdviceEn)) }

    LaunchedEffect(uiLang) {
        if (adviceToFamily == defaultAdviceBn || adviceToFamily == defaultAdviceEn) {
            adviceToFamily = localizedText(uiLang, defaultAdviceBn, defaultAdviceEn)
        }
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
                modifier = Modifier.testTag("janazah_back_button")
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
                    text = uiText("জানাজা ও শেষ সফরের প্রস্তুতি", "Janazah & Final Journey"),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = uiText("সুন্নাহ জানাজা নির্দেশিকা ও ডিজিটাল ওয়াসিয়াতনামা", "Sunnah Janazah Guide & Digital Wasiyyah"),
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
                    .testTag("janazah_tab_guide"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiText("জানাজা ও কাফন-দাফন", "Janazah & Burial"),
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
                    .testTag("janazah_tab_wasiyyah"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiText("ডিজিটাল অসিয়তনামা", "Digital Wasiyyah"),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (selectedTab == 1) ObsidianBackground else TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedTab == 0) {
            // Janazah & Burial Guide View
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Step 1: Ghusl (গোসল)
                item {
                    JanazahStepCard(
                        stepNumber = formatLocalizedInt(1, uiLang),
                        titleBengali = uiText("মাইয়্যেতের গোসল (সুন্নাহ পদ্ধতি)", "Ghusl of the Deceased (Sunnah Method)"),
                        summary = uiText(
                            "মাইয়্যেতের সতর (নাভি থেকে হাঁটু) ঢেকে ধীরস্থিরভাবে বেজোড় সংখ্যায় (৩ বা ৫ বার) গোসল করানো ওয়াজিব।",
                            "Covering the Awrah (navel to knees) and gently washing the body an odd number of times (3 or 5) is obligatory."
                        ),
                        details = if (com.example.data.util.SupportedLanguage.normalizeCode(uiLang) != "bn") {
                            listOf(
                                "Begin by performing Wudu gently (wiping mouth and nose with a damp cloth instead of pouring water inside).",
                                "Wash the body with water infused with lote (Sidr) leaves, finishing the final wash with camphor-scented pure water.",
                                "Men should wash men and women should wash women (except spouses, who may wash one another)."
                            )
                        } else {
                            listOf(
                                "গোসলের শুরুতে স্বাভাবিক অজু করানো (নাক ও মুখে পানি না দিয়ে ভেজা কাপড় দিয়ে পরিষ্কার করা)।",
                                "কুল বা বরই পাতা সিদ্ধ পানিতে শরীর পরিষ্কার করা এবং সবশেষে কর্পূর মিশ্রিত পবিত্র পানি ঢালা।",
                                "মহিলাকে কেবল মহিলারা এবং পুরুষকে কেবল পুরুষরাই গোসল করাবেন (ব্যতিক্রম: স্বামী-স্ত্রী পরস্পরে)।"
                            )
                        }
                    )
                }

                // Step 2: Kafan (কাফন)
                item {
                    JanazahStepCard(
                        stepNumber = formatLocalizedInt(2, uiLang),
                        titleBengali = uiText("কাফন পরিধানের নিয়ম", "Shrouding (Kafan) Guidelines"),
                        summary = uiText(
                            "সাদা সুতি পরিষ্কার কাপড় ব্যবহার করা মুস্তাহাব। সুগন্ধি (আতর/লোবান) বিজোড় সংখ্যায় ধোঁয়া দেওয়া সুন্নাত।",
                            "Using clean white cotton sheets is recommended, lightly perfuming the shroud an odd number of times."
                        ),
                        details = if (com.example.data.util.SupportedLanguage.normalizeCode(uiLang) != "bn") {
                            listOf(
                                "For men (3 cloths): Lifafah (outer sheet), Izar (lower wrap), and Qamis (shirt).",
                                "For women (5 cloths): Lifafah, Izar, Qamis, Khimar (headscarf), and Sinaband (chest wrap).",
                                "Applying pure musk or camphor onto the spots of prostration (forehead, nose, hands, knees) is Sunnah."
                            )
                        } else {
                            listOf(
                                "পুরুষের জন্য ৩ কাপড়: লেফাফা (বড় চাদর), ইযার (লুঙ্গি) ও কামীস (জামা)।",
                                "নারীর জন্য ৫ কাপড়: লেফাফা, ইযার, কামীস, খিমার (মাথার ওড়না) ও সিনা-বন্দ (বুকের চাদর)।",
                                "মাইয়্যেতের সাজদা ও সিজদার অঙ্গসমূহে (কপাল, নাক, হাত, হাঁটু) খাঁটি আতর বা কর্পূর লাগানো সুন্নাত।"
                            )
                        }
                    )
                }

                // Step 3: Janazah 4-Takbir Salah (জানাজার সালাত)
                item {
                    FrostedGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = DarkEmeraldSurface,
                        borderColor = ChampagneGold.copy(alpha = 0.35f)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(ChampagneGold.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        formatLocalizedInt(3, uiLang),
                                        color = ChampagneGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                Text(
                                    text = uiText("৪ তাকবিরে জানাজার নামাজ", "4-Takbir Janazah Prayer"),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = ChampagneGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Takbir 1
                            Text(
                                text = uiText("১ম তাকবির (আল্লাহু আকবার):", "1st Takbir (Allahu Akbar):"),
                                style = MaterialTheme.typography.labelMedium.copy(color = MintGlow, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = uiText(
                                    "হাত বাঁধা অবস্থায় সানা পাঠ: 'সুবহানাকাল্লাহুম্মা ওয়া বিহামদিকা, ওয়া তাবারাকাসমুকা, ওয়া তা'আলা জাদ্দুকা, ওয়া জাল্লা সানাউকা, ওয়া লা ইলাহা গাইরুক'। (কিংবা সূরা আল-ফাতিহা পাঠ করা)।",
                                    "Fold hands and recite Thana: 'Subhanaka Allahumma wa bihamdika, wa tabarakasmuka, wa ta'ala jadduka, wa jalla thana'uka, wa la ilaha ghayruk' (or Surah al-Fatihah)."
                                ),
                                style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontSize = 11.sp, lineHeight = 16.sp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Takbir 2
                            Text(
                                text = uiText("২য় তাকবির (আল্লাহু আকবার):", "2nd Takbir (Allahu Akbar):"),
                                style = MaterialTheme.typography.labelMedium.copy(color = MintGlow, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = uiText(
                                    "দুরুদে ইব্রাহীম পাঠ (নামাজের দুরুদ): 'আল্লাহুম্মা সাল্লি আলা মুহাম্মাদিওঁ ওয়া আলা আলি মুহাম্মাদ...'",
                                    "Recite Durood Ibrahim: 'Allahumma salli 'ala Muhammadin wa 'ala ali Muhammad...'"
                                ),
                                style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontSize = 11.sp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Takbir 3
                            Text(
                                text = uiText("৩য় তাকবির (আল্লাহু আকবার):", "3rd Takbir (Allahu Akbar):"),
                                style = MaterialTheme.typography.labelMedium.copy(color = MintGlow, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = uiText("প্রাপ্তবয়স্কদের জন্য জানাজার দোয়া:", "Janazah Supplication for Adults:"),
                                style = MaterialTheme.typography.labelSmall.copy(color = ChampagneGold, fontSize = 11.sp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "اللَّهُمَّ اغْفِرْ لِحَيِّنَا وَمَيِّتِنَا، وَشَاهِدِنَا وَغَائِبِنَا، وَصَغِيرِنَا وَكَبِيرِنَا، وَذَكَرِنَا وَأُنْثَانَا، اللَّهُمَّ مَنْ أَحْيَيْتَهُ مِنَّا فَأَحْيِهِ عَلَى الْإِسْلَامِ، وَمَنْ تَوَفَّيْتَهُ مِنَّا فَتَوَفَّهُ عَلَى الْإِيمَانِ",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontFamily = AmiriFontFamily,
                                    fontSize = 17.sp,
                                    lineHeight = 28.sp,
                                    textAlign = TextAlign.Right,
                                    color = ChampagneGold
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = uiText(
                                    "উচ্চারণ: আল্লাহুম্মাগফির লিহাইয়্যিনা ওয়া মায়্যিতিনা, ওয়া শাহিদিনা ওয়া গাইবিনা, ওয়া সাগীরিনা ওয়া কাবীরিনা, ওয়া যাকারিনা ওয়া উনছানা। আল্লাহুম্মা মান আহয়াইতাহু মিন্না ফা আহয়িহী 'আলাল ইসলাম, ওয়া মান তাওয়াফ্ফাইতাহু মিন্না ফাতাওয়াফ্ফাহু 'আলাল ঈমান।",
                                    "Transliteration: Allahummaghfir lihayyina wa mayyitina, wa shahidina wa gha'ibina, wa saghirina wa kabirina, wa dhakarina wa unthana. Allahumma man ahyaytahu minna fa-ahyihi 'alal-Islam, wa man tawaffaytahu minna fatawaffahu 'alal-Iman."
                                ),
                                style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 11.sp, lineHeight = 15.sp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Takbir 4
                            Text(
                                text = uiText("৪র্থ তাকবির (আল্লাহু আকবার):", "4th Takbir (Allahu Akbar):"),
                                style = MaterialTheme.typography.labelMedium.copy(color = MintGlow, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = uiText(
                                    "সামান্য ক্ষণ বিরতি দিয়ে উভয় দিকে সালাম ফিরিয়ে নামাজ সম্পন্ন করা: 'আসসালামু আলাইকুম ওয়া রাহমাতুল্লাহ'।",
                                    "Pause briefly and conclude the prayer by turning with Salam to both sides: 'Assalamu 'alaykum wa Rahmatullah'."
                                ),
                                style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontSize = 11.sp)
                            )
                        }
                    }
                }

                // Step 4: Burial & Dua
                item {
                    JanazahStepCard(
                        stepNumber = formatLocalizedInt(4, uiLang),
                        titleBengali = uiText("দাফন ও কবর জিয়ারত", "Burial & Visiting the Grave"),
                        summary = uiText(
                            "মাইয়্যেতকে কবরে ডান কাতে কিবলামুখী করে শোয়ানো এবং কবরের মুখে বাঁশ/মাটি দেওয়া।",
                            "Place the deceased on their right side facing the Qiblah and cover the grave gently with earth."
                        ),
                        details = if (com.example.data.util.SupportedLanguage.normalizeCode(uiLang) != "bn") {
                            listOf(
                                "Recite when placing in the grave: 'Bismillahi wa 'ala millati Rasulillah' (بِسْمِ اللَّهِ وَعَلَى مِلَّةِ رَسُولِ اللَّهِ — In the name of Allah and upon the way of the Messenger of Allah).",
                                "Those present throw three handfuls of soil gently starting from the head side.",
                                "After burial, remain briefly by the grave to pray for their forgiveness and steadfastness during questioning."
                            )
                        } else {
                            listOf(
                                "কবরে রাখার সময় সুন্নাত দোয়া: 'বিসমিল্লাহি ওয়া আলা মিল্লাতি রাসুলিল্লাহ' (بِسْمِ اللَّهِ وَعَلَى مِلَّةِ رَسُولِ اللَّهِ - আল্লাহর নামে এবং রাসূলের আদর্শের ওপর)।",
                                "কবরের মাথার দিক থেকে উপস্থিত প্রত্যেকে ৩ বার দুই হাতে মাটি দেওয়া।",
                                "দাফন শেষে কবরের পাশে কিছু সময় দাঁড়িয়ে মাইয়্যেতের ক্ষমার জন্য এবং মুনকার-নাকিরের প্রশ্নের উত্তর সুদৃঢ় হওয়ার জন্য দোয়া করা।"
                            )
                        }
                    )
                }
            }
        } else {
            // Digital Islamic Will (Wasiyyah) Builder View
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
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = uiText("ইসলামিক ওয়াসিয়াতনামাহর গুরুত্ব", "Importance of an Islamic Will (Wasiyyah)"),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = ChampagneGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = uiText(
                                        "\"কোনো মুসলিমের কাছে যদি অসিয়ত করার মতো সম্পদ থাকে, তবে নিজের কাছে অসিয়তনামা লিখিত না রেখে দুই রাত কাটানোও বৈধ নয়।\" (সহীহ বুখারী: ২৭৩৮)",
                                        "\"It is not right for a Muslim who has anything to bequeath to spend two nights without having his written will with him.\" (Sahih al-Bukhari: 2738)"
                                    ),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SlateGrayMuted,
                                        fontSize = 10.sp,
                                        lineHeight = 15.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Input Form
                item {
                    FrostedGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = DarkEmeraldSurface
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = testatorName,
                                onValueChange = { testatorName = it },
                                label = { Text(uiText("আপনার পূর্ণ নাম", "Your Full Name")) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ChampagneGold,
                                    unfocusedBorderColor = HairlineBorder
                                )
                            )

                            OutlinedTextField(
                                value = debtsText,
                                onValueChange = { debtsText = it },
                                label = { Text(uiText("দেনা ও ঋণ (পাওনাদারের নাম ও পরিমাণ)", "Debts & Obligations (Creditors & Amounts)")) },
                                placeholder = { Text(uiText("যেমন: জনাব করিমের পাওনা ৳ ২০,০০০, ব্যাংক লোন...", "e.g., Mr. Karim owed 20,000, bank dues...")) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ChampagneGold,
                                    unfocusedBorderColor = HairlineBorder
                                )
                            )

                            OutlinedTextField(
                                value = amanahText,
                                onValueChange = { amanahText = it },
                                label = { Text(uiText("গচ্ছিত আমানত (কারো কোনো বস্তু বা সম্পদ রাখা থাকলে)", "Trusts & Amanah Held for Others")) },
                                placeholder = { Text(uiText("যেমন: ভাইয়ের সংরক্ষিত জমির দলিল, প্রতিবেশীর গহনা...", "e.g., Land deed kept for brother, neighbor's item...")) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ChampagneGold,
                                    unfocusedBorderColor = HairlineBorder
                                )
                            )

                            OutlinedTextField(
                                value = missedWorshipFidya,
                                onValueChange = { missedWorshipFidya = it },
                                label = { Text(uiText("অপূর্ণ ইবাদত ও ফিদয়া (কাযা নামাজ/রোজা)", "Missed Worship & Fidyah (Qaza Salah/Fasts)")) },
                                placeholder = { Text(uiText("যেমন: শারীরিক অসুস্থতার কারণে বিগত রমজানের ৫টি রোজার ফিদয়া আদায়...", "e.g., Fidyah for 5 missed Ramadan fasts due to illness...")) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ChampagneGold,
                                    unfocusedBorderColor = HairlineBorder
                                )
                            )

                            OutlinedTextField(
                                value = charitableBequest,
                                onValueChange = { charitableBequest = it },
                                label = { Text(uiText("সাদাকাহ ও অসিয়ত (সর্বোচ্চ ১/৩ অংশ)", "Charitable Bequest (Up to 1/3 of Net Estate)")) },
                                placeholder = { Text(uiText("যেমন: স্থানীয় এতিমখানায় ৳ ৫০,০০০ অথবা অমুক মসজিদ তহবিলে...", "e.g., 50,000 to local orphanage or mosque fund...")) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ChampagneGold,
                                    unfocusedBorderColor = HairlineBorder
                                )
                            )

                            OutlinedTextField(
                                value = adviceToFamily,
                                onValueChange = { adviceToFamily = it },
                                label = { Text(uiText("পরিবার ও উত্তরসূরিদের প্রতি আধ্যাত্মিক উপদেশ", "Spiritual Advice to Family & Heirs")) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ChampagneGold,
                                    unfocusedBorderColor = HairlineBorder
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Button(
                                onClick = {
                                    val formattedWasiyyah = if (com.example.data.util.SupportedLanguage.normalizeCode(uiLang) != "bn") {
                                        """
                                            📜 Bismillahir Rahmanir Rahim 📜
                                            === ISLAMIC WASIYYAH (WILL) ===
                                            Testator: ${if (testatorName.isNotBlank()) testatorName else "A Believing Servant of Allah"}
                                            
                                            1. Settlement of Debts & Liabilities:
                                            ${if (debtsText.isNotBlank()) debtsText else "No known debts. Any verified rightful claim should be settled promptly."}
                                            
                                            2. Return of Trusts (Amanah):
                                            ${if (amanahText.isNotBlank()) amanahText else "No items held in trust."}
                                            
                                            3. Fidyah for Missed Obligations:
                                            ${if (missedWorshipFidya.isNotBlank()) missedWorshipFidya else "Seek Allah's forgiveness and mercy."}
                                            
                                            4. Charitable Bequest (Max 1/3 of Net Estate):
                                            ${if (charitableBequest.isNotBlank()) charitableBequest else "Distribute remaining estate strictly according to Quranic Faraidh rules."}
                                            
                                            5. Final Counsel to Family & Children:
                                            $adviceToFamily
                                            
                                            — Prepared via Noor-e-Hidayah Islamic App
                                        """.trimIndent()
                                    } else {
                                        """
                                            📜 বিসমিল্লাহির রাহমানির রাহিম 📜
                                            === ইসলামিক ওয়াসিয়াতনামাহ ===
                                            ওয়াসিয়তকারী: ${if (testatorName.isNotBlank()) testatorName else "একজন ঈমানদার বান্দা"}
                                            
                                            ১. দেনা ও ঋণ পরিশোধের নির্দেশনা:
                                            ${if (debtsText.isNotBlank()) debtsText else "কোনো জানা পাওনা নেই। কারো দাবি থাকলে ন্যায্য প্রমাণ সাপেক্ষে পরিশোধ করতে হবে।"}
                                            
                                            ২. গচ্ছিত আমানত ফেরত:
                                            ${if (amanahText.isNotBlank()) amanahText else "কোনো আমানত জমা নেই।"}
                                            
                                            ৩. কাযা ইবাদতের ফিদয়া:
                                            ${if (missedWorshipFidya.isNotBlank()) missedWorshipFidya else "আল্লাহর কাছে ক্ষমা প্রার্থনা।"}
                                            
                                            ৪. দান ও সাদাকাহ (সর্বোচ্চ ১/৩ অংশের ভেতর):
                                            ${if (charitableBequest.isNotBlank()) charitableBequest else "শরিয়ত নির্দেশিত ওয়ারিশগণের মাঝে ফারায়েজ অনুযায়ী বণ্টন।"}
                                            
                                            ৫. সন্তান ও পরিবারের প্রতি নসীহত:
                                            $adviceToFamily
                                            
                                            — প্রস্তুতকৃত: নূর-এ-হেদায়াত ইসলামিক অ্যাপ
                                        """.trimIndent()
                                    }

                                    clipboardManager.setText(AnnotatedString(formattedWasiyyah))
                                    isWasiyyahCopied = true
                                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                },
                                modifier = Modifier.fillMaxWidth().testTag("copy_wasiyyah_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ChampagneGold,
                                    contentColor = ObsidianBackground
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = if (isWasiyyahCopied) Icons.Filled.Check else Icons.Filled.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isWasiyyahCopied) {
                                        uiText("অসিয়তনামাহ ক্লিপবোর্ডে কপি হয়েছে!", "Wasiyyah copied to clipboard!")
                                    } else {
                                        uiText("অসিয়তনামা ফরম্যাট কপি করুন", "Copy Formatted Wasiyyah")
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JanazahStepCard(
    stepNumber: String,
    titleBengali: String,
    summary: String,
    details: List<String>
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(ChampagneGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stepNumber, color = ChampagneGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Text(
                        text = titleBengali,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold,
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
                text = summary,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextPrimary.copy(alpha = 0.9f),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            )

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    details.forEach { d ->
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
                                text = d,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
