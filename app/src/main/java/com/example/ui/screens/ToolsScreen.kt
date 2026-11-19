package com.example.ui.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.preference.AppPreferences
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.speech.tts.TextToSpeech
import com.example.data.audio.ArabicVoiceTone
import com.example.data.audio.AudioPlayerManager
import com.example.data.model.RuqyahCategory
import com.example.data.model.RuqyahRepository
import com.example.data.model.RuqyahTrack
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AsmaUlHusnaRepository
import com.example.data.model.PrayerName
import com.example.data.mosque.MosqueModeManager
import com.example.data.mosque.MosqueModeScheduler
import com.example.data.mosque.MosqueRingerMode
import com.example.data.util.TimeFormatUtils
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontFamily
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun ToolsScreen(
    toolsViewModel: ToolsViewModel,
    tasbeehViewModel: TasbeehViewModel,
    moreViewModel: MoreViewModel,
    onNavigateToTab: ((AppTab) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val activeTool by toolsViewModel.activeTool.collectAsState()

    BackHandler(enabled = activeTool != ActiveToolTab.DASHBOARD) {
        toolsViewModel.closeTool()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (activeTool) {
            ActiveToolTab.DASHBOARD -> {
                ToolsDashboardView(
                    onSelectTool = { toolsViewModel.openTool(it) },
                    onNavigateToTab = onNavigateToTab
                )
            }
            ActiveToolTab.RUQYAH_SHARIAH -> {
                RuqyahShariahAudioView(
                    audioPlayer = toolsViewModel.audioPlayer,
                    onBack = { toolsViewModel.closeTool() }
                )
            }
            ActiveToolTab.ARABIC_PRONUNCIATION -> {
                ArabicPronunciationTrainerStudioView(
                    toolsViewModel = toolsViewModel,
                    audioPlayer = moreViewModel.audioPlayer,
                    onBack = { toolsViewModel.closeTool() }
                )
            }
            ActiveToolTab.AI_ASSISTANT -> {
                NoorIslamicAiView(
                    viewModel = moreViewModel,
                    ttsManager = toolsViewModel.ttsManager,
                    onBack = { toolsViewModel.closeTool() }
                )
            }
            ActiveToolTab.TASBEEH -> {
                Tactile3DTasbeehView(
                    viewModel = tasbeehViewModel,
                    onBack = { toolsViewModel.closeTool() }
                )
            }
            ActiveToolTab.QIBLA -> {
                QiblaCompassView(
                    viewModel = toolsViewModel,
                    onBack = { toolsViewModel.closeTool() }
                )
            }
            ActiveToolTab.ZAKAT -> {
                ZakatCalculatorView(
                    viewModel = toolsViewModel,
                    onBack = { toolsViewModel.closeTool() }
                )
            }
            ActiveToolTab.MOSQUE_MODE -> {
                MosqueModeView(
                    viewModel = toolsViewModel,
                    onBack = { toolsViewModel.closeTool() }
                )
            }
            ActiveToolTab.ASMA_UL_HUSNA -> {
                AsmaUlHusnaView(
                    onBack = { toolsViewModel.closeTool() }
                )
            }
            ActiveToolTab.SUNNAH_HEALTH -> {
                SunnahHealthScreen(
                    onBack = { toolsViewModel.closeTool() }
                )
            }
            ActiveToolTab.ISLAMIC_INHERITANCE -> {
                InheritanceWealthScreen(
                    onBack = { toolsViewModel.closeTool() }
                )
            }
            ActiveToolTab.JANAZAH_WASIYYAH -> {
                JanazahWasiyyahScreen(
                    onBack = { toolsViewModel.closeTool() }
                )
            }
            ActiveToolTab.HADITH_LIBRARY -> {
                HadithLibraryScreen(
                    onBack = { toolsViewModel.closeTool() },
                    ttsManager = toolsViewModel.ttsManager,
                    audioPlayer = toolsViewModel.audioPlayer
                )
            }
            ActiveToolTab.DEBT_LEDGER -> {
                DebtLedgerScreen(
                    onBack = { toolsViewModel.closeTool() }
                )
            }
            ActiveToolTab.NEARBY_MOSQUE -> {
                NearbyMosqueScreen(
                    onBack = { toolsViewModel.closeTool() }
                )
            }
            ActiveToolTab.HIJRI_CALENDAR -> {
                HijriCalendarScreen(
                    onBack = { toolsViewModel.closeTool() }
                )
            }
        }
    }
}

@Composable
fun ToolsDashboardView(
    onSelectTool: (ActiveToolTab) -> Unit,
    onNavigateToTab: ((AppTab) -> Unit)? = null
) {
    val context = LocalContext.current
    val prefs = remember { AppPreferences(context) }
    val database = remember { com.example.data.local.AppDatabase.getInstance(context) }
    val cloudSync = remember { com.example.data.cloud.CloudSyncRepository(context, prefs, database) }
    val coroutineScope = rememberCoroutineScope()
    val todayString = remember { java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()) }
    val checkedAmals by prefs.getTodayAmals(todayString).collectAsState(initial = emptySet())

    var showAmalDialog by remember { mutableStateOf(false) }
    var showHijriDialog by remember { mutableStateOf(false) }
    var showHadithDialog by remember { mutableStateOf(false) }

    val prayerAlerts by prefs.prayerAlertsEnabled.collectAsState(initial = true)
    val fastingAlerts by prefs.fastingReminderEnabled.collectAsState(initial = true)
    val jumuahAlerts by prefs.jumuahReminderEnabled.collectAsState(initial = true)
    val dailyHadithAlerts by prefs.dailyHadithNotifEnabled.collectAsState(initial = true)

    val knowledgeItems = remember {
        listOf(
            KnowledgeItem(
                id = "quran",
                titleBengali = "কুরআন",
                titleEnglish = "Quran",
                icon3DType = Icon3DType.HOLY_QURAN,
                onClick = { onNavigateToTab?.invoke(AppTab.QURAN) }
            ),
            KnowledgeItem(
                id = "asmaul_husna",
                titleBengali = "আসমাউল হুসনা",
                titleEnglish = "99 Names",
                icon3DType = Icon3DType.ASMA_UL_HUSNA,
                onClick = { onSelectTool(ActiveToolTab.ASMA_UL_HUSNA) }
            ),
            KnowledgeItem(
                id = "hadith",
                titleBengali = "হাদীস",
                titleEnglish = "Hadith",
                icon3DType = Icon3DType.TADABBUR_QURAN,
                onClick = { onSelectTool(ActiveToolTab.HADITH_LIBRARY) }
            ),
            KnowledgeItem(
                id = "dua",
                titleBengali = "দু'আ",
                titleEnglish = "Du'a",
                icon3DType = Icon3DType.DUA_HANDS,
                onClick = { onNavigateToTab?.invoke(AppTab.DUA) }
            ),
            KnowledgeItem(
                id = "ruqyah",
                titleBengali = "রুকইয়াহ",
                titleEnglish = "Ruqyah",
                icon3DType = Icon3DType.RUQYAH_SHIELD,
                onClick = { onSelectTool(ActiveToolTab.RUQYAH_SHARIAH) }
            )
        )
    }

    val toolCardItems = remember {
        listOf(
            ToolCardItem(
                id = "qibla",
                titleBengali = "কিবলা কম্পাস",
                subtitleBengali = "কা'বা শরীফের লাইভ ডিগ্রি",
                icon3DType = Icon3DType.QIBLA_COMPASS,
                isPremiumCrown = true,
                statusBadge = "লাইভ ডিগ্রি",
                statusColor = MintGlow,
                onClick = { onSelectTool(ActiveToolTab.QIBLA) },
                titleEnglish = "Qibla Compass",
                subtitleEnglish = "Live Kaaba bearing & dial",
                statusBadgeEnglish = "Live Degree"
            ),
            ToolCardItem(
                id = "tasbeeh",
                titleBengali = "ডিজিটাল তাসবীহ",
                subtitleBengali = "কাউন্টার ও স্পর্শ জিকির",
                icon3DType = Icon3DType.TASBEEH_BEADS,
                isPremiumCrown = true,
                statusBadge = "স্পর্শ জিকির",
                statusColor = ChampagneGold,
                onClick = { onSelectTool(ActiveToolTab.TASBEEH) },
                titleEnglish = "Digital Tasbeeh",
                subtitleEnglish = "Counter & tactile dhikr",
                statusBadgeEnglish = "Tactile"
            ),
            ToolCardItem(
                id = "nearby_mosque",
                titleBengali = "মসজিদ ফাইন্ডার",
                subtitleBengali = "কাছাকাছি মসজিদ ও ম্যাপ",
                icon3DType = Icon3DType.MOSQUE_HOME,
                isPremiumCrown = true,
                statusBadge = "লাইভ রাডার",
                statusColor = MintGlow,
                onClick = { onSelectTool(ActiveToolTab.NEARBY_MOSQUE) },
                titleEnglish = "Mosque Finder",
                subtitleEnglish = "Nearby mosques & map",
                statusBadgeEnglish = "Live Radar"
            ),
            ToolCardItem(
                id = "calendar_hijri",
                titleBengali = "হিজরি ক্যালেন্ডার",
                subtitleBengali = "তারিখ ও নফল সিয়াম",
                icon3DType = Icon3DType.TOOLS_ASTROLABE,
                isPremiumCrown = false,
                statusBadge = "হিজরি সন",
                statusColor = ChampagneGold,
                onClick = { onSelectTool(ActiveToolTab.HIJRI_CALENDAR) },
                titleEnglish = "Hijri Calendar",
                subtitleEnglish = "Dates & Sunnah fasts",
                statusBadgeEnglish = "Hijri"
            ),
            ToolCardItem(
                id = "zakat_calculator",
                titleBengali = "যাকাত ক্যালকুলেটর",
                subtitleBengali = "নিসাব ও সম্পদের হিসাব",
                icon3DType = Icon3DType.ZAKAT_SCALE,
                isPremiumCrown = false,
                statusBadge = "নিসাব হিসাব",
                statusColor = MintGlow,
                onClick = { onSelectTool(ActiveToolTab.ZAKAT) },
                titleEnglish = "Zakat Calculator",
                subtitleEnglish = "Nisab & wealth calculation",
                statusBadgeEnglish = "Nisab"
            ),
            ToolCardItem(
                id = "audio_studio",
                titleBengali = "তিলাওয়াত স্টুডিও",
                subtitleBengali = "আরবি উচ্চারণ ও মাখরাজ",
                icon3DType = Icon3DType.ARABIC_TTS,
                isPremiumCrown = false,
                statusBadge = "টিটিএস সাউন্ড",
                statusColor = ChampagneGold,
                onClick = { onSelectTool(ActiveToolTab.ARABIC_PRONUNCIATION) },
                titleEnglish = "Recitation Studio",
                subtitleEnglish = "Arabic pronunciation & TTS",
                statusBadgeEnglish = "TTS Audio"
            ),
            ToolCardItem(
                id = "umrah_tracker",
                titleBengali = "সুন্নাহ ও স্বাস্থ্য",
                subtitleBengali = "তিব্বুন নববী ও নফল রোজা",
                icon3DType = Icon3DType.SUNNAH_HEALTH,
                isPremiumCrown = true,
                statusBadge = "নফল রোজা",
                statusColor = MintGlow,
                onClick = { onSelectTool(ActiveToolTab.SUNNAH_HEALTH) },
                titleEnglish = "Sunnah & Health",
                subtitleEnglish = "Prophetic medicine & fasts",
                statusBadgeEnglish = "Sunnah"
            ),
            ToolCardItem(
                id = "auto_silent",
                titleBengali = "মসজিদ মোড",
                subtitleBengali = "নামাজে স্বয়ংক্রিয় সাইলেন্ট",
                icon3DType = Icon3DType.MOSQUE_DND,
                isPremiumCrown = true,
                statusBadge = "অটো সাইলেন্ট",
                statusColor = ChampagneGold,
                onClick = { onSelectTool(ActiveToolTab.MOSQUE_MODE) },
                titleEnglish = "Mosque Mode",
                subtitleEnglish = "Auto-silent during prayer",
                statusBadgeEnglish = "Auto-Silent"
            ),
            ToolCardItem(
                id = "ai_widget",
                titleBengali = "ইসলামিক এআই",
                subtitleBengali = "স্মার্ট প্রশ্নোত্তর ও ফতোয়া",
                icon3DType = Icon3DType.AI_ASSISTANT,
                isPremiumCrown = true,
                statusBadge = "স্মার্ট সঙ্গী",
                statusColor = MintGlow,
                onClick = { onSelectTool(ActiveToolTab.AI_ASSISTANT) },
                titleEnglish = "Islamic AI",
                subtitleEnglish = "Smart Q&A & guidance",
                statusBadgeEnglish = "Companion"
            ),
            ToolCardItem(
                id = "inheritance_builder",
                titleBengali = "ফারায়েজ ও মিরাস",
                subtitleBengali = "উত্তরাধিকার সম্পত্তি বণ্টন",
                icon3DType = Icon3DType.INHERITANCE_WEALTH,
                isPremiumCrown = false,
                statusBadge = "ফারায়েজ",
                statusColor = ChampagneGold,
                onClick = { onSelectTool(ActiveToolTab.ISLAMIC_INHERITANCE) },
                titleEnglish = "Faraid Inheritance",
                subtitleEnglish = "Islamic estate distribution",
                statusBadgeEnglish = "Faraid"
            ),
            ToolCardItem(
                id = "debt_ledger",
                titleBengali = "স্মার্ট হিসাব খাতা",
                subtitleBengali = "ঋণ ও লেনদেন হিসাব",
                icon3DType = Icon3DType.ZAKAT_SCALE,
                isPremiumCrown = true,
                statusBadge = "ক্লাউড ব্যাকআপ",
                statusColor = MintGlow,
                onClick = { onSelectTool(ActiveToolTab.DEBT_LEDGER) },
                titleEnglish = "Debt & Ledger",
                subtitleEnglish = "Loans & financial records",
                statusBadgeEnglish = "Cloud Sync"
            ),
            ToolCardItem(
                id = "hadith_library",
                titleBengali = "সহীহ হাদিস সংগ্রহ",
                subtitleBengali = "সিহাহ সিত্তাহ পাঠাগার",
                icon3DType = Icon3DType.HOLY_QURAN,
                isPremiumCrown = true,
                statusBadge = "সিহাহ সিত্তাহ",
                statusColor = ChampagneGold,
                onClick = { onSelectTool(ActiveToolTab.HADITH_LIBRARY) },
                titleEnglish = "Hadith Library",
                subtitleEnglish = "Sihah Sittah collection",
                statusBadgeEnglish = "Authentic"
            )
        )
    }

    val reminders = listOf(
        ReminderHubItem(
            id = "prayer_alerts",
            titleBengali = "নামাজের আযান ও ওয়াক্ত অ্যালার্ট",
            subtitleBengali = "ওয়াক্ত শুরু ও জামা'আতের সাথে সাথে নোটিফিকেশন",
            isEnabled = prayerAlerts,
            onToggle = { enabled ->
                coroutineScope.launch { prefs.setPrayerAlertsEnabled(enabled) }
            },
            titleEnglish = "Adhan & Prayer Alerts",
            subtitleEnglish = "Notifications at prayer start and Jama'at time"
        ),
        ReminderHubItem(
            id = "sahur_iftar",
            titleBengali = "সেহরি ও ইফতার সতর্কবার্তা",
            subtitleBengali = "সেহরি শেষ ও ইফতারের ৩ মিনিট পূর্বের সতর্কতা অ্যালার্ট",
            isEnabled = fastingAlerts,
            onToggle = { enabled ->
                coroutineScope.launch { prefs.setFastingReminderEnabled(enabled) }
            },
            titleEnglish = "Sahur & Iftar Reminders",
            subtitleEnglish = "3-minute warning alerts before Sahur end and Iftar"
        ),
        ReminderHubItem(
            id = "jumuah_kahf",
            titleBengali = "জুমু'আ ও সূরা কাহফ রিমাইন্ডার",
            subtitleBengali = "বৃহস্পতিবার মাগরিব থেকে জুমু'আর দিন সূরা কাহফ পাঠ",
            isEnabled = jumuahAlerts,
            onToggle = { enabled ->
                coroutineScope.launch { prefs.setJumuahReminderEnabled(enabled) }
            },
            titleEnglish = "Jumu'ah & Surah Kahf",
            subtitleEnglish = "Weekly reminder to recite Surah Al-Kahf on Friday"
        ),
        ReminderHubItem(
            id = "daily_hadith",
            titleBengali = "দৈনন্দিন হাদিস নোটিফিকেশন",
            subtitleBengali = "প্রতিদিন সকালে একটি সহীহ হাদিস ও হেদায়েতের বার্তা",
            isEnabled = dailyHadithAlerts,
            onToggle = { enabled ->
                coroutineScope.launch { prefs.setDailyHadithNotif(enabled) }
            },
            titleEnglish = "Daily Hadith Notification",
            subtitleEnglish = "Receive an authentic Hadith inspiration every morning"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 12.dp)
        ) {
            Text(
                text = uiText("ইসলামিক টুলবক্স", "Islamic Toolbox"),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = uiText(
                    "প্রয়োজনীয় ইসলামিক সেবা ও দৈনন্দিন সহায়কসমূহ",
                    "Essential Islamic tools & daily utilities"
                ),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = ChampagneGold.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        // 📊 1. TOP AMAL TRACKER CARD
        TopAmalTrackerCard(
            completedCount = checkedAmals.size,
            totalCount = 14,
            onClick = { showAmalDialog = true }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 📚 2. "REQUIRED KNOWLEDGE" SECTION
        RequiredKnowledgeSection(items = knowledgeItems)

        Spacer(modifier = Modifier.height(24.dp))

        // 🛠️ 3. "TOOLS" SECTION
        ToolsGridSection(tools = toolCardItems)

        Spacer(modifier = Modifier.height(24.dp))

        // ⏰ 4. "REMINDER" SECTION
        ReminderSection(reminders = reminders)

        Spacer(modifier = Modifier.height(120.dp))
    }

    // Dialogs
    if (showAmalDialog) {
        AmalTrackerDialog(
            dateString = todayString,
            checkedAmals = checkedAmals,
            onToggleAmal = { key ->
                coroutineScope.launch {
                    prefs.toggleTodayAmal(todayString, key)
                    val updated = prefs.getTodayAmals(todayString).first()
                    cloudSync.autoSyncTodayAmal(todayString, updated)
                }
            },
            onDismiss = { showAmalDialog = false }
        )
    }

    if (showHijriDialog) {
        HijriCalendarDialog(onDismiss = { showHijriDialog = false })
    }

    if (showHadithDialog) {
        HadithViewerDialog(onDismiss = { showHadithDialog = false })
    }
}

// -----------------------------------------------------------------------------
// 1. TACTILE 3D TASBEEH ENGINE (ZERO-LATENCY, ROUNDS & CUSTOM DUAS)
// -----------------------------------------------------------------------------
@Composable
fun Tactile3DTasbeehView(
    viewModel: TasbeehViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val allDhikrs by viewModel.allAvailableDhikrs.collectAsState()
    val uiLang = LocalAppLanguage.current
    val contentLang = LocalContentLanguage.current
    val scope = rememberCoroutineScope()
    val beadScrollAnim = remember { Animatable(0f) }

    var showTargetDialog by remember { mutableStateOf(false) }
    var showDuaSelectorDialog by remember { mutableStateOf(false) }
    var showAddDuaDialog by remember { mutableStateOf(false) }
    var customTargetInput by remember { mutableStateOf("") }

    // Dialog: Custom Target Count
    if (showTargetDialog) {
        AlertDialog(
            onDismissRequest = { showTargetDialog = false },
            containerColor = DarkEmeraldElevated,
            title = {
                Text(
                    text = uiText("কাস্টম লক্ষ্য নির্ধারণ করুন", "Set Custom Target"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = uiText(
                            "আপনার কাঙ্ক্ষিত জিকির লক্ষ্য সংখ্যা লিখুন (যেমন: ৩৩, ১০০, ১০০০ বা যেকোনো সংখ্যা):",
                            "Enter your desired dhikr target count (e.g., 33, 100, 1000, or any number):"
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    OutlinedTextField(
                        value = customTargetInput,
                        onValueChange = { customTargetInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text(uiText("লক্ষ্য সংখ্যা", "Target Count"), color = SlateGrayMuted) },
                        placeholder = { Text(uiText("যেমন: 500", "e.g. 500"), color = SlateGrayMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = MintGlow,
                            unfocusedBorderColor = HairlineGold,
                            cursorColor = MintGlow
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Quick suggestion chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(7, 33, 40, 100, 313, 1000).forEach { num ->
                            AssistChip(
                                onClick = { customTargetInput = num.toString() },
                                label = { Text("$num", color = ChampagneGold, fontSize = 12.sp) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = DarkEmeraldSurface
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = customTargetInput.toIntOrNull()
                        if (num != null && num > 0) {
                            viewModel.setTarget(num)
                        }
                        showTargetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MintGlow)
                ) {
                    Text(uiText("সংরক্ষণ", "Save"), color = ObsidianBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTargetDialog = false }) {
                    Text(uiText("বাতিল", "Cancel"), color = SlateGrayMuted)
                }
            }
        )
    }

    // Dialog: Select Dhikr / Dua
    if (showDuaSelectorDialog) {
        AlertDialog(
            onDismissRequest = { showDuaSelectorDialog = false },
            containerColor = DarkEmeraldElevated,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiText("জিকির ও দোয়া নির্বাচন", "Select Dhikr & Du'a"),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    IconButton(
                        onClick = {
                            showDuaSelectorDialog = false
                            showAddDuaDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = uiText("নতুন দোয়া যুক্ত করুন", "Add New Du'a"),
                            tint = MintGlow
                        )
                    }
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Button(
                            onClick = {
                                showDuaSelectorDialog = false
                                showAddDuaDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MintGlow.copy(alpha = 0.15f),
                                contentColor = MintGlow
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MintGlow.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, tint = MintGlow)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(uiText("+ কাস্টম দোয়া বা জিকির যুক্ত করুন", "+ Add Custom Dhikr or Du'a"), fontWeight = FontWeight.SemiBold)
                        }
                    }

                    items(allDhikrs) { preset ->
                        val isSelected = preset.id == uiState.currentPreset.id
                        Card(
                            onClick = {
                                viewModel.selectPreset(preset)
                                scope.launch { beadScrollAnim.snapTo(0f) }
                                showDuaSelectorDialog = false
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) DarkEmeraldSurface else ObsidianBackground.copy(alpha = 0.6f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.5.dp,
                                    color = if (isSelected) ChampagneGold else HairlineGold.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = preset.arabic,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontFamily = AmiriFontFamily,
                                                color = if (isSelected) ChampagneGold else TextPrimary
                                            )
                                        )
                                        if (preset.isCustom) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Surface(
                                                color = ChampagneGold.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = uiText("কাস্টম", "Custom"),
                                                    color = ChampagneGold,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${preset.localizedTransliteration(contentLang)} • ${preset.localizedMeaning(contentLang)}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted),
                                        fontSize = 11.sp
                                    )
                                }

                                if (preset.isCustom) {
                                    IconButton(
                                        onClick = { viewModel.deleteCustomDua(preset.id) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.DeleteOutline,
                                            contentDescription = uiText("মুছে ফেলুন", "Delete"),
                                            tint = ProhibitedRose.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDuaSelectorDialog = false }) {
                    Text(uiText("বন্ধ করুন", "Close"), color = ChampagneGold)
                }
            }
        )
    }

    // Dialog: Add Custom Dhikr / Dua
    if (showAddDuaDialog) {
        var newArabic by remember { mutableStateOf("") }
        var newTrans by remember { mutableStateOf("") }
        var newMeaning by remember { mutableStateOf("") }
        var newTarget by remember { mutableStateOf("100") }

        AlertDialog(
            onDismissRequest = { showAddDuaDialog = false },
            containerColor = DarkEmeraldElevated,
            title = {
                Text(
                    text = uiText("নতুন কাস্টম দোয়া বা জিকির", "New Custom Dhikr or Du'a"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = newArabic,
                        onValueChange = { newArabic = it },
                        label = { Text(uiText("আরবি পাঠ (ঐচ্ছিক)", "Arabic Text (Optional)"), color = SlateGrayMuted) },
                        placeholder = { Text("حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ", color = SlateGrayMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = MintGlow,
                            unfocusedBorderColor = HairlineGold
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newTrans,
                        onValueChange = { newTrans = it },
                        label = { Text(uiText("দোয়ার নাম / উচ্চারণ", "Name / Transliteration"), color = SlateGrayMuted) },
                        placeholder = { Text("Hasbunallahu wa ni'mal wakeel", color = SlateGrayMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = MintGlow,
                            unfocusedBorderColor = HairlineGold
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newMeaning,
                        onValueChange = { newMeaning = it },
                        label = { Text(uiText("অর্থ বা তাৎপর্য", "Meaning / Note"), color = SlateGrayMuted) },
                        placeholder = { Text(uiText("যেমন: আল্লাহই আমাদের জন্য যথেষ্ট", "e.g. Allah is sufficient for us"), color = SlateGrayMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = MintGlow,
                            unfocusedBorderColor = HairlineGold
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newTarget,
                        onValueChange = { newTarget = it.filter { ch -> ch.isDigit() } },
                        label = { Text(uiText("টার্গেট / লক্ষ্য সংখ্যা", "Target Count"), color = SlateGrayMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = MintGlow,
                            unfocusedBorderColor = HairlineGold
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val targetInt = newTarget.toIntOrNull() ?: 100
                        viewModel.addCustomDua(
                            arabic = newArabic,
                            transliteration = newTrans,
                            bengaliMeaning = newMeaning,
                            target = targetInt
                        )
                        showAddDuaDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MintGlow)
                ) {
                    Text(uiText("সংরক্ষণ করুন", "Save"), color = ObsidianBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDuaDialog = false }) {
                    Text(uiText("বাতিল", "Cancel"), color = SlateGrayMuted)
                }
            }
        )
    }

    // Root layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Text(
                    text = uiText("ডিজিটাল তাসবীহ", "Digital Tasbeeh"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = ChampagneGold,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                IconButton(
                    onClick = {
                        viewModel.reset()
                        scope.launch { beadScrollAnim.snapTo(0f) }
                    },
                    modifier = Modifier.testTag("tasbeeh_reset_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Reset",
                        tint = SlateGrayMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Current Phrase & Dua Card
            FrostedGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDuaSelectorDialog = true },
                backgroundColor = DarkEmeraldElevated
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (uiState.currentPreset.isCustom) {
                            Surface(
                                color = ChampagneGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = uiText("কাস্টম দোয়া", "Custom Du'a"),
                                    color = ChampagneGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkEmeraldSurface)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SwapHoriz,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = uiText("জিকির পরিবর্তন", "Change Dhikr"),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = ChampagneGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = uiState.currentPreset.arabic,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = AmiriFontFamily,
                            fontSize = 24.sp,
                            color = ChampagneGold,
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${uiState.currentPreset.localizedTransliteration(contentLang)} • ${uiState.currentPreset.localizedMeaning(contentLang)}",
                        style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Target Count Selector Chips (33, 100, 1000, Custom)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiText("টার্গেট:", "Target:"),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SlateGrayMuted,
                        fontSize = 12.sp
                    )
                )

                listOf(33, 100, 1000).forEach { targetValue ->
                    val isSelected = uiState.targetCount == targetValue
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setTarget(targetValue) },
                        label = {
                            Text(
                                text = "$targetValue",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = DarkEmeraldSurface,
                            selectedContainerColor = MintGlow.copy(alpha = 0.2f),
                            selectedLabelColor = MintGlow,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = HairlineGold.copy(alpha = 0.4f),
                            selectedBorderColor = MintGlow
                        )
                    )
                }

                // Custom Target Button
                val isCustomTarget = uiState.targetCount !in listOf(33, 100, 1000)
                FilterChip(
                    selected = isCustomTarget,
                    onClick = {
                        customTargetInput = uiState.targetCount.toString()
                        showTargetDialog = true
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = null,
                            tint = if (isCustomTarget) MintGlow else ChampagneGold,
                            modifier = Modifier.size(12.dp)
                        )
                    },
                    label = {
                        Text(
                            text = if (isCustomTarget) "${uiState.targetCount} (${uiText("কাস্টম", "Custom")})" else uiText("কাস্টম...", "Custom..."),
                            fontSize = 12.sp,
                            fontWeight = if (isCustomTarget) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = DarkEmeraldSurface,
                        selectedContainerColor = MintGlow.copy(alpha = 0.2f),
                        selectedLabelColor = MintGlow,
                        labelColor = ChampagneGold
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isCustomTarget,
                        borderColor = HairlineGold.copy(alpha = 0.4f),
                        selectedBorderColor = MintGlow
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Round Progress & Completed Indicator Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkEmeraldSurface)
                    .border(1.dp, HairlineGold.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = if (uiState.completedRounds > 0) MintGlow else SlateGrayMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = uiText("সম্পন্ন রাউন্ড: ${uiState.completedRounds}", "Completed Rounds: ${uiState.completedRounds}"),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (uiState.completedRounds > 0) MintGlow else TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                // Progress to current round
                val progressFraction = if (uiState.targetCount > 0) {
                    (uiState.currentCount.toFloat() / uiState.targetCount).coerceIn(0f, 1f)
                } else 0f

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .width(80.dp)
                            .height(6.dp)
                            .clip(CircleShape),
                        color = MintGlow,
                        trackColor = ObsidianBackground
                    )
                    Text(
                        text = "${(progressFraction * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateGrayMuted,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Milestone Round Celebration Banner
            AnimatedVisibility(visible = uiState.roundCelebrationMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                uiState.roundCelebrationMessage?.let { msg ->
                    MintPillBadge(
                        text = msg,
                        icon = Icons.Filled.CheckCircle
                    )
                }
            }

            // -----------------------------------------------------------------
            // ZERO-LATENCY TOUCH-DOWN INTERACTIVE 3D BEAD AREA (CONTINUOUS CHAIN)
            // -----------------------------------------------------------------
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            // Instant zero-delay touch down action!
                            val down = awaitFirstDown(requireUnconsumed = false)
                            viewModel.increment()
                            scope.launch {
                                val currentTarget = beadScrollAnim.targetValue
                                val nextTarget = if (currentTarget >= beadScrollAnim.value) currentTarget + 1f else beadScrollAnim.value + 1f
                                beadScrollAnim.animateTo(
                                    targetValue = nextTarget,
                                    animationSpec = spring(
                                        dampingRatio = 0.72f,
                                        stiffness = 380f
                                    )
                                )
                            }
                            waitForUpOrCancellation()
                        }
                    }
                    .testTag("tasbeeh_full_screen_tap"),
                contentAlignment = Alignment.Center
            ) {
                RealisticBeadChainCanvas(
                    scrollOffset = beadScrollAnim.value,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Minimalist Elevated HUD Display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkEmeraldElevated.copy(alpha = 0.75f))
                    .border(1.dp, HairlineGold.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = "${uiState.currentCount}",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = ChampagneGold
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiText("লক্ষ্য: ${uiState.targetCount}", "Goal: ${uiState.targetCount}"),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateGrayMuted,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text("•", color = HairlineGold, fontSize = 11.sp)
                    Text(
                        text = uiText("রাউন্ড: ${uiState.completedRounds}", "Rounds: ${uiState.completedRounds}"),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MintGlow,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text("•", color = HairlineGold, fontSize = 11.sp)
                    Text(
                        text = uiText("মোট: ${uiState.totalCount}", "Total: ${uiState.totalCount}"),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ChampagneGold,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Text(
                    text = uiText(
                        "যেকোনো স্থানে স্পর্শ করলেই ইনস্ট্যান্ট ক্লিক শব্দ, ভাইব্রেশন ও গুটি রোল হবে",
                        "Tap anywhere for instant click sound, haptic feedback & bead roll"
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted.copy(alpha = 0.85f)),
                    fontSize = 10.5.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// -----------------------------------------------------------------------------
// REALISTIC 3D BEAD CHAIN CANVAS (INFINITE ROLLING LOOP & PERSPECTIVE)
// -----------------------------------------------------------------------------
@Composable
fun RealisticBeadChainCanvas(
    scrollOffset: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val centerY = height / 2f
        val halfHeight = height / 2f
        val spacingPx = 38.dp.toPx()

        // 1. Organic natural drape curve for the Tasbeeh strand
        fun getStrandX(y: Float): Float {
            val progress = ((y - (centerY - halfHeight)) / height).coerceIn(0f, 1f)
            return centerX + 12.dp.toPx() * kotlin.math.sin(kotlin.math.PI.toFloat() * progress)
        }

        // 2. Draw Silk Braided Cord
        val cordPath = Path().apply {
            val steps = 30
            moveTo(getStrandX(0f), 0f)
            for (i in 1..steps) {
                val y = (height / steps) * i
                lineTo(getStrandX(y), y)
            }
        }

        // Cord ambient drop shadow
        drawPath(
            path = cordPath,
            color = Color.Black.copy(alpha = 0.4f),
            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
        )

        // Cord golden silk thread with metallic sheen
        drawPath(
            path = cordPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    ChampagneGold.copy(alpha = 0.25f),
                    ChampagneGold.copy(alpha = 0.9f),
                    ChampagneGold.copy(alpha = 0.25f)
                )
            ),
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )

        // 3. Focal Active Counting Zone Guide (Behind Beads at centerY)
        val focalX = getStrandX(centerY)

        // Soft ethereal golden focal glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    ChampagneGold.copy(alpha = 0.20f),
                    Color.Transparent
                ),
                center = Offset(focalX, centerY),
                radius = 52.dp.toPx()
            ),
            radius = 52.dp.toPx(),
            center = Offset(focalX, centerY)
        )

        // Delicate dashed guideline ring
        drawCircle(
            color = ChampagneGold.copy(alpha = 0.35f),
            radius = 32.dp.toPx(),
            center = Offset(focalX, centerY),
            style = Stroke(
                width = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )
        )

        // Subtle left and right alignment notches
        drawLine(
            color = ChampagneGold.copy(alpha = 0.75f),
            start = Offset(focalX - 44.dp.toPx(), centerY),
            end = Offset(focalX - 35.dp.toPx(), centerY),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = ChampagneGold.copy(alpha = 0.75f),
            start = Offset(focalX + 35.dp.toPx(), centerY),
            end = Offset(focalX + 44.dp.toPx(), centerY),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        // 4. Calculate Visible Beads along the Continuous Chain
        val uMin = (-halfHeight - spacingPx * 1.5f) / spacingPx - scrollOffset
        val uMax = (halfHeight + spacingPx * 1.5f) / spacingPx - scrollOffset

        val kStart = kotlin.math.floor(uMin).toInt()
        val kEnd = kotlin.math.ceil(uMax).toInt()

        for (k in kStart..kEnd) {
            val u = k + scrollOffset
            val y = centerY + u * spacingPx
            val x = getStrandX(y)
            val distFromCenter = kotlin.math.abs(y - centerY)
            val normDist = (distFromCenter / halfHeight).coerceIn(0f, 1.3f)

            // Dynamic 3D perspective sizing:
            val rCenter = 21.dp.toPx()
            val rEdge = 13.5.dp.toPx()
            val r = rCenter - (rCenter - rEdge) * (normDist.coerceIn(0f, 1f) * normDist.coerceIn(0f, 1f))

            // Seamless alpha fade for beads entering at top and exiting at bottom
            val alpha = if (normDist > 0.65f) {
                (1f - (normDist - 0.65f) / 0.45f).coerceIn(0f, 1f)
            } else {
                1f
            }

            if (alpha > 0.02f && r > 2f) {
                // Every 33rd bead is a decorative Carved Emerald & Gold Minaret/Separator bead (Imam / Fasilah)
                val isMilestoneBead = (k != 0 && kotlin.math.abs(k) % 33 == 0)

                // A. Ambient Drop Shadow (gives elevation above dark velvet canvas)
                drawCircle(
                    color = Color.Black.copy(alpha = 0.5f * alpha),
                    radius = r + 2.dp.toPx(),
                    center = Offset(x + 2.dp.toPx(), y + 3.5.dp.toPx())
                )

                if (isMilestoneBead) {
                    // Carved Emerald & Gold Separator Bead
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF69F0AE),
                                Color(0xFF00C853),
                                Color(0xFF0F5A38),
                                Color(0xFF042013)
                            ),
                            center = Offset(x - r * 0.35f, y - r * 0.35f),
                            radius = r * 1.25f
                        ),
                        radius = r,
                        center = Offset(x, y)
                    )
                    // Gold filigree equator ring
                    drawCircle(
                        color = ChampagneGold.copy(alpha = 0.9f * alpha),
                        radius = r * 0.7f,
                        center = Offset(x, y),
                        style = Stroke(width = 1.8.dp.toPx())
                    )
                } else {
                    // Polished Sandalwood & Brushed Champagne-Gold Spherical Bead
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFF7E0),
                                Color(0xFFE5C158),
                                Color(0xFFB58428),
                                Color(0xFF5E3911),
                                Color(0xFF281604)
                            ),
                            center = Offset(x - r * 0.35f, y - r * 0.35f),
                            radius = r * 1.2f
                        ),
                        radius = r,
                        center = Offset(x, y)
                    )

                    // Delicate lathe-turned equator wood grain ring
                    drawCircle(
                        color = Color(0xFFF7E2A8).copy(alpha = 0.3f * alpha),
                        radius = r * 0.75f,
                        center = Offset(x, y),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }

                // Cord entrance/exit drill holes
                drawOval(
                    color = Color(0xFF1B0E03).copy(alpha = 0.85f * alpha),
                    topLeft = Offset(x - 2.5.dp.toPx(), y - r - 0.5.dp.toPx()),
                    size = Size(5.dp.toPx(), 3.dp.toPx())
                )
                drawOval(
                    color = Color(0xFF1B0E03).copy(alpha = 0.85f * alpha),
                    topLeft = Offset(x - 2.5.dp.toPx(), y + r - 2.5.dp.toPx()),
                    size = Size(5.dp.toPx(), 3.dp.toPx())
                )

                // Extra specular glint when bead is resting in the active center focus
                if (normDist < 0.12f) {
                    val glintAlpha = (1f - normDist / 0.12f) * 0.45f * alpha
                    drawCircle(
                        color = Color.White.copy(alpha = glintAlpha),
                        radius = 3.dp.toPx(),
                        center = Offset(x - r * 0.32f, y - r * 0.32f)
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 2. ELITE 3D GOLDEN QIBLA COMPASS & ACCURACY VERIFIER
// -----------------------------------------------------------------------------
@Composable
fun QiblaCompassView(
    viewModel: ToolsViewModel,
    onBack: () -> Unit
) {
    val qiblaState by viewModel.qiblaState.collectAsState()
    val uiLang = LocalAppLanguage.current
    var showCalibrationInfo by remember { mutableStateOf(false) }

    val angleDiff = ((qiblaState.qiblaBearingDegrees - qiblaState.deviceAzimuthDegrees + 540f) % 360f) - 180f
    val absAngleDiff = kotlin.math.abs(angleDiff).toInt()
    val turnInstruction = when {
        qiblaState.isAligned -> uiText(
            "আলহামদুলিল্লাহ! সরাসরি কা'বা শরীফের মুখোমুখি আছেন",
            "Alhamdulillah! You are facing the Qibla directly"
        )
        angleDiff > 0 -> uiText("ডানদিকে আরও ${absAngleDiff}° ঘুরুন →", "Turn ${absAngleDiff}° to your right →")
        else -> uiText("← বামদিকে আরও ${absAngleDiff}° ঘুরুন", "← Turn ${absAngleDiff}° to your left")
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 120.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Top Navigation Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiText("পবিত্র কিবলা কম্পাস", "Sacred Qibla Compass"),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = uiText(
                            "কা'বা শরীফ অভিমুখ ও জ্যোতির্বিজ্ঞানসম্মত নির্দেশক",
                            "Kaaba bearing & astronomical direction finder"
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateGrayMuted,
                            fontSize = 11.sp
                        )
                    )
                }

                // Calibration Status Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (qiblaState.isCalibrated) DarkEmeraldElevated else Color(0xFF2C1114))
                        .border(
                            0.5.dp,
                            if (qiblaState.isCalibrated) MintGlow.copy(alpha = 0.5f) else Color(0xFFFFB4BA),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { showCalibrationInfo = !showCalibrationInfo }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (qiblaState.isCalibrated) MintGlow else Color(0xFFFFB4BA))
                        )
                        Text(
                            text = if (qiblaState.isCalibrated) uiText("সেন্সর নির্ভুল", "Calibrated") else uiText("ক্যালিব্রেট", "Calibrate"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (qiblaState.isCalibrated) MintGlow else Color(0xFFFFB4BA),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }

        // 2. Real-Time Target Lock Banner
        item {
            FrostedGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (qiblaState.isAligned) MintGlow else HairlineGold
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (qiblaState.isAligned) MintGlow.copy(alpha = 0.2f) else DarkEmeraldElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (qiblaState.isAligned) Icons.Filled.Verified else Icons.Filled.Explore,
                            contentDescription = null,
                            tint = if (qiblaState.isAligned) MintGlow else ChampagneGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (qiblaState.isAligned) uiText("সঠিক কিবলামুখী অবস্থান", "Aligned with Qibla") else turnInstruction,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = if (qiblaState.isAligned) MintGlow else ChampagneGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = uiText(
                                "কিবলা কোণ: ${qiblaState.qiblaBearingDegrees.toInt()}° (${qiblaState.localizedQiblaDirectionName(uiLang)})",
                                "Qibla Angle: ${qiblaState.qiblaBearingDegrees.toInt()}° (${qiblaState.localizedQiblaDirectionName(uiLang)})"
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

        // 3. THE 3D GOLDEN DIAL CANVAS (The Core Astronomical Instrument)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background subtle ambient halo
                Box(
                    modifier = Modifier
                        .size(310.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    if (qiblaState.isAligned) MintGlow.copy(alpha = 0.18f) else ChampagneGold.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // The 3D Rotating Dial Chassis
                Box(
                    modifier = Modifier
                        .size(290.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF040A09))
                        .border(
                            width = if (qiblaState.isAligned) 2.5.dp else 1.5.dp,
                            brush = Brush.sweepGradient(
                                colors = if (qiblaState.isAligned) {
                                    listOf(MintGlow, ChampagneGold, MintGlow, Color(0xFF00875A), MintGlow)
                                } else {
                                    listOf(
                                        Color(0xFFFFE082),
                                        Color(0xFFD4AF37),
                                        Color(0xFFAA771C),
                                        Color(0xFFF6D365),
                                        Color(0xFF8A6014),
                                        Color(0xFFFFE082)
                                    )
                                }
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val rotationAngle = -qiblaState.deviceAzimuthDegrees

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(rotationAngle)
                    ) {
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        val radius = size.minDimension / 2f

                        // Inner circular bezel ring
                        drawCircle(
                            color = DarkEmeraldElevated,
                            radius = radius - 14.dp.toPx(),
                            style = Stroke(width = 1.dp.toPx())
                        )
                        drawCircle(
                            color = HairlineGold,
                            radius = radius - 30.dp.toPx(),
                            style = Stroke(width = 0.5.dp.toPx())
                        )

                        // 360-degree Radial Graduation Ticks & Degree Numbers
                        val tickPaint = Paint().apply {
                            color = SlateGrayMuted.copy(alpha = 0.5f).toArgb()
                            textSize = 8.5.sp.toPx()
                            textAlign = Paint.Align.CENTER
                            isAntiAlias = true
                        }

                        val cardinalPaint = Paint().apply {
                            color = TextPrimary.toArgb()
                            textSize = 13.sp.toPx()
                            typeface = Typeface.DEFAULT_BOLD
                            textAlign = Paint.Align.CENTER
                            isAntiAlias = true
                        }

                        val northPaint = Paint().apply {
                            color = ChampagneGold.toArgb()
                            textSize = 15.sp.toPx()
                            typeface = Typeface.DEFAULT_BOLD
                            textAlign = Paint.Align.CENTER
                            isAntiAlias = true
                        }

                        for (deg in 0 until 360 step 5) {
                            val rad = Math.toRadians((deg - 90).toDouble())
                            val isMajor = deg % 30 == 0
                            val isMedium = deg % 15 == 0 && !isMajor

                            val tickLen = when {
                                isMajor -> 11.dp.toPx()
                                isMedium -> 7.dp.toPx()
                                else -> 4.dp.toPx()
                            }

                            val startR = radius - 2.dp.toPx() - tickLen
                            val endR = radius - 2.dp.toPx()

                            val x1 = cx + (startR * kotlin.math.cos(rad)).toFloat()
                            val y1 = cy + (startR * kotlin.math.sin(rad)).toFloat()
                            val x2 = cx + (endR * kotlin.math.cos(rad)).toFloat()
                            val y2 = cy + (endR * kotlin.math.sin(rad)).toFloat()

                            drawLine(
                                color = when {
                                    isMajor -> ChampagneGold.copy(alpha = 0.85f)
                                    isMedium -> ChampagneGold.copy(alpha = 0.45f)
                                    else -> SlateGrayMuted.copy(alpha = 0.25f)
                                },
                                start = Offset(x1, y1),
                                end = Offset(x2, y2),
                                strokeWidth = if (isMajor) 2.dp.toPx() else 1.dp.toPx()
                            )

                            // Degree Numbers (0, 30, 60... 330)
                            if (isMajor && deg % 30 == 0) {
                                val textR = radius - 20.dp.toPx()
                                val tx = cx + (textR * kotlin.math.cos(rad)).toFloat()
                                val ty = cy + (textR * kotlin.math.sin(rad)).toFloat() + 3.dp.toPx()
                                drawIntoCanvas {
                                    it.nativeCanvas.drawText("$deg°", tx, ty, tickPaint)
                                }
                            }
                        }

                        // Cardinal Directions: N (0°), E (90°), S (180°), W (270°)
                        // 1. True North (0°) with 3D Glowing Gold Arrow
                        val northRad = Math.toRadians(-90.0)
                        val northArrowTipR = radius - 3.dp.toPx()
                        val northArrowBaseR = radius - 14.dp.toPx()
                        val nxTip = cx + (northArrowTipR * kotlin.math.cos(northRad)).toFloat()
                        val nyTip = cy + (northArrowTipR * kotlin.math.sin(northRad)).toFloat()

                        val arrowPath = Path().apply {
                            moveTo(nxTip, nyTip)
                            lineTo(nxTip - 7.dp.toPx(), cy - northArrowBaseR)
                            lineTo(nxTip, cy - northArrowBaseR + 3.dp.toPx())
                            lineTo(nxTip + 7.dp.toPx(), cy - northArrowBaseR)
                            close()
                        }
                        drawPath(arrowPath, brush = Brush.linearGradient(listOf(Color(0xFFFFE082), ChampagneGold)))

                        drawIntoCanvas {
                            val nr = radius - 35.dp.toPx()
                            it.nativeCanvas.drawText("N", cx, cy - nr, northPaint)
                            // East (90°)
                            it.nativeCanvas.drawText("E", cx + nr, cy + 4.dp.toPx(), cardinalPaint)
                            // South (180°)
                            it.nativeCanvas.drawText("S", cx, cy + nr + 4.dp.toPx(), cardinalPaint)
                            // West (270°)
                            it.nativeCanvas.drawText("W", cx - nr, cy + 4.dp.toPx(), cardinalPaint)
                        }

                        // DYNAMIC QIBLA NEEDLE & 3D KAABA INDICATOR
                        val kaabaAngle = qiblaState.qiblaBearingDegrees
                        val kaabaRad = Math.toRadians((kaabaAngle - 90).toDouble())
                        val needleMaxR = radius - 38.dp.toPx()

                        val kxTip = cx + (needleMaxR * kotlin.math.cos(kaabaRad)).toFloat()
                        val kyTip = cy + (needleMaxR * kotlin.math.sin(kaabaRad)).toFloat()

                        // 3D Beveled Needle pointing toward Kaaba
                        val perpRad = kaabaRad + Math.PI / 2.0
                        val halfWidth = 7.dp.toPx()
                        val baseR = 16.dp.toPx()

                        val leftBaseX = cx + (baseR * kotlin.math.cos(kaabaRad) - halfWidth * kotlin.math.cos(perpRad)).toFloat()
                        val leftBaseY = cy + (baseR * kotlin.math.sin(kaabaRad) - halfWidth * kotlin.math.sin(perpRad)).toFloat()

                        val rightBaseX = cx + (baseR * kotlin.math.cos(kaabaRad) + halfWidth * kotlin.math.cos(perpRad)).toFloat()
                        val rightBaseY = cy + (baseR * kotlin.math.sin(kaabaRad) + halfWidth * kotlin.math.sin(perpRad)).toFloat()

                        // Left Needle Facet
                        val leftFacetPath = Path().apply {
                            moveTo(kxTip, kyTip)
                            lineTo(leftBaseX, leftBaseY)
                            lineTo(cx, cy)
                            close()
                        }
                        drawPath(
                            path = leftFacetPath,
                            color = if (qiblaState.isAligned) MintGlow else ChampagneGold
                        )

                        // Right Needle Facet (darker shading for 3D depth)
                        val rightFacetPath = Path().apply {
                            moveTo(kxTip, kyTip)
                            lineTo(rightBaseX, rightBaseY)
                            lineTo(cx, cy)
                            close()
                        }
                        drawPath(
                            path = rightFacetPath,
                            color = if (qiblaState.isAligned) MintGlowSubdued else ChampagneGoldMuted
                        )

                        // Miniature 3D Kaaba Monument at outer rim
                        val kaabaIconR = radius - 16.dp.toPx()
                        val kIconX = cx + (kaabaIconR * kotlin.math.cos(kaabaRad)).toFloat()
                        val kIconY = cy + (kaabaIconR * kotlin.math.sin(kaabaRad)).toFloat()

                        // Kaaba outer glow when aligned
                        if (qiblaState.isAligned) {
                            drawCircle(
                                color = MintGlow.copy(alpha = 0.35f),
                                radius = 16.dp.toPx(),
                                center = Offset(kIconX, kIconY)
                            )
                        }

                        // Kaaba Cube Body
                        val kaabaSize = 18.dp.toPx()
                        val kaabaRect = Path().apply {
                            addRoundRect(
                                androidx.compose.ui.geometry.RoundRect(
                                    left = kIconX - kaabaSize / 2f,
                                    top = kIconY - kaabaSize / 2f,
                                    right = kIconX + kaabaSize / 2f,
                                    bottom = kIconY + kaabaSize / 2f,
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                                )
                            )
                        }
                        drawPath(kaabaRect, color = Color(0xFF0F1716))
                        drawPath(kaabaRect, color = if (qiblaState.isAligned) MintGlow else ChampagneGold, style = Stroke(width = 1.dp.toPx()))

                        // Golden Kiswah embroidery line across upper portion of Kaaba
                        val kiswahY = kIconY - kaabaSize / 6f
                        drawLine(
                            color = ChampagneGold,
                            start = Offset(kIconX - kaabaSize / 2f + 2.dp.toPx(), kiswahY),
                            end = Offset(kIconX + kaabaSize / 2f - 2.dp.toPx(), kiswahY),
                            strokeWidth = 2.dp.toPx()
                        )

                        // Center Astronomical Brass Hub
                        drawCircle(
                            color = Color(0xFF0A1312),
                            radius = 20.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(ChampagneGold, Color(0xFF8A6014)),
                                center = Offset(cx, cy),
                                radius = 16.dp.toPx()
                            ),
                            radius = 14.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                        drawCircle(
                            color = if (qiblaState.isAligned) MintGlow else ChampagneGold,
                            radius = 6.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                    }
                }
            }
        }

        // 4. Real-Time Telemetry Grid (3 High-Precision Metric Cards)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Qibla Bearing
                FrostedGlassCard(modifier = Modifier.weight(1f)) {
                    Text(
                        text = uiText("কিবলা কোণ", "Qibla Angle"),
                        style = MaterialTheme.typography.labelSmall.copy(color = MintGlow, fontSize = 11.sp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${qiblaState.qiblaBearingDegrees.toInt()}°",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = qiblaState.localizedQiblaDirectionName(uiLang),
                        style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 10.sp),
                        maxLines = 1
                    )
                }

                // Current Device Heading
                FrostedGlassCard(modifier = Modifier.weight(1f)) {
                    Text(
                        text = uiText("বর্তমান দিক", "Heading"),
                        style = MaterialTheme.typography.labelSmall.copy(color = ChampagneGold, fontSize = 11.sp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${qiblaState.deviceAzimuthDegrees.toInt()}°",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = qiblaState.localizedHeadingDirectionName(uiLang),
                        style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 10.sp),
                        maxLines = 1
                    )
                }

                // Distance to Kaaba
                FrostedGlassCard(modifier = Modifier.weight(1f)) {
                    Text(
                        text = uiText("কা'বার দূরত্ব", "To Kaaba"),
                        style = MaterialTheme.typography.labelSmall.copy(color = SlateGrayMuted, fontSize = 11.sp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${qiblaState.distanceToKaabaKm}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = uiText("কিলোমিটার", "km"),
                        style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 10.sp)
                    )
                }
            }
        }

        // 5. Accuracy & Sensor Calibration Verifier Card
        item {
            FrostedGlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (qiblaState.isCalibrated) MintGlow else Color(0xFFFFB4BA))
                        )
                        Column {
                            Text(
                                text = uiText("ম্যাগনেটোমিটার সেন্সর স্ট্যাটাস", "Magnetometer Sensor Status"),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = ChampagneGold,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                text = qiblaState.localizedAccuracyDescription(uiLang),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (qiblaState.isCalibrated) MintGlow else Color(0xFFFFB4BA),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    TextButton(onClick = { showCalibrationInfo = !showCalibrationInfo }) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = ChampagneGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showCalibrationInfo) uiText("বন্ধ করুন", "Hide") else uiText("ক্যালিব্রেশন গাইড", "Calibration Guide"),
                            fontSize = 11.sp,
                            color = ChampagneGold
                        )
                    }
                }

                AnimatedVisibility(visible = showCalibrationInfo) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkEmeraldElevated)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = uiText(
                                "কম্পাস সেন্সর ক্যালিব্রেশন নির্দেশিকা (Figure-8):",
                                "Compass Calibration Guide (Figure-8):"
                            ),
                            style = MaterialTheme.typography.labelSmall.copy(color = MintGlow, fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiText(
                                "১. মোবাইলটি অনুভূমিকভাবে সমতলে বা হাতের তালুতে সোজা রাখুন।\n" +
                                        "২. বাতাসে ফোনটিকে ইংরেজি '8' (Figure-8) অক্ষরের মতো ২ থেকে ৩ বার ধীরে ধীরে ঘুরিয়ে নিন।\n" +
                                        "৩. ফোনের কাছাকাছি কোনো চুম্বক, মেটাল কভার বা ল্যাপটপ থাকলে তা দূরে রাখুন।",
                                "1. Hold your phone flat on your palm.\n" +
                                        "2. Move it slowly in a Figure-8 motion 2-3 times in the air.\n" +
                                        "3. Keep away from magnets, metallic cases, or laptops."
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 11.sp, lineHeight = 17.sp)
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 3. ZAKAT & FITRAH CALCULATOR
// -----------------------------------------------------------------------------
@Composable
fun ZakatCalculatorView(
    viewModel: ToolsViewModel,
    onBack: () -> Unit
) {
    val zakatState by viewModel.zakatState.collectAsState()

    var cashText by remember { mutableStateOf("") }
    var bankText by remember { mutableStateOf("") }
    var goldText by remember { mutableStateOf("") }
    var silverText by remember { mutableStateOf("") }
    var goodsText by remember { mutableStateOf("") }
    var debtText by remember { mutableStateOf("") }
    var familyText by remember { mutableStateOf("1") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = uiText("যাকাত ও ফিতরা ক্যালকুলেটর", "Zakat & Fitrah Calculator"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = ChampagneGold,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.size(48.dp))
            }
        }

        // Summary Card
        item {
            FrostedGlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DarkEmeraldElevated,
                borderColor = HairlineGold
            ) {
                Text(
                    text = uiText("প্রদেয় যাকাত (২.৫%):", "Payable Zakat (2.5%):"),
                    style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted)
                )
                Text(
                    text = "৳ ${String.format("%.2f", zakatState.calculatedZakat)}",
                    style = MaterialTheme.typography.displayMedium.copy(
                        color = ChampagneGold,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (zakatState.isEligibleForZakat) uiText(
                        "আপনার সম্পদ নিসাব অতিক্রম করেছে (যাকাত ফরজ)",
                        "Your wealth exceeds Nisab (Zakat is obligatory)"
                    ) else uiText(
                        "আপনার সম্পদ নিসাবের নিচে",
                        "Your wealth is below the Nisab threshold"
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (zakatState.isEligibleForZakat) MintGlow else SlateGrayMuted
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = HairlineBorder)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = uiText(
                            "ফিতরা (${zakatState.familyMembersForFitrah} সদস্য):",
                            "Fitrah (${zakatState.familyMembersForFitrah} members):"
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted)
                    )
                    Text(
                        text = "৳ ${String.format("%.0f", zakatState.calculatedFitrah)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }

        // Input Fields
        item {
            SectionHeader(title = uiText("সম্পদের বিবরণ (টাকা / গ্রাম)", "Asset Details (Currency / Grams)"))
        }

        item {
            NumberInputField(label = uiText("নগদ টাকা (হাতে)", "Cash in Hand"), value = cashText) {
                cashText = it
                viewModel.updateZakat(cash = it.toDoubleOrNull() ?: 0.0)
            }
        }
        item {
            NumberInputField(label = uiText("ব্যাংক ব্যালেন্স ও সেভিংস", "Bank Balance & Savings"), value = bankText) {
                bankText = it
                viewModel.updateZakat(bank = it.toDoubleOrNull() ?: 0.0)
            }
        }
        item {
            NumberInputField(label = uiText("স্বর্ণ (গ্রাম)", "Gold (Grams)"), value = goldText) {
                goldText = it
                viewModel.updateZakat(goldGrams = it.toDoubleOrNull() ?: 0.0)
            }
        }
        item {
            NumberInputField(label = uiText("রৌপ্য (গ্রাম)", "Silver (Grams)"), value = silverText) {
                silverText = it
                viewModel.updateZakat(silverGrams = it.toDoubleOrNull() ?: 0.0)
            }
        }
        item {
            NumberInputField(label = uiText("ব্যবসায়িক পণ্যমূল্য", "Business Inventory Value"), value = goodsText) {
                goodsText = it
                viewModel.updateZakat(goods = it.toDoubleOrNull() ?: 0.0)
            }
        }
        item {
            NumberInputField(label = uiText("বকেয়া ঋণ ও দায়", "Debts & Liabilities"), value = debtText) {
                debtText = it
                viewModel.updateZakat(debts = it.toDoubleOrNull() ?: 0.0)
            }
        }
        item {
            NumberInputField(label = uiText("পরিবারের সদস্য সংখ্যা (ফিতরার জন্য)", "Family Members (for Fitrah)"), value = familyText) {
                familyText = it
                viewModel.updateZakat(familyMembers = it.toIntOrNull() ?: 1)
            }
        }
    }
}

@Composable
fun NumberInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = DarkEmeraldSurface,
            unfocusedContainerColor = DarkEmeraldSurface,
            focusedBorderColor = ChampagneGold,
            unfocusedBorderColor = HairlineBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
        ),
        singleLine = true
    )
}

// -----------------------------------------------------------------------------
// 4. MOSQUE MODE (AUTO-SILENT)
// -----------------------------------------------------------------------------
@Composable
fun MosqueModeView(
    viewModel: ToolsViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uiLang = LocalAppLanguage.current
    val isEnabled by viewModel.mosqueModeEnabled.collectAsState()
    val ringerMode by viewModel.mosqueRingerMode.collectAsState()
    val preAlertEnabled by viewModel.prePrayerAlertEnabled.collectAsState()
    val preAlertMinutes by viewModel.prePrayerAlertMinutes.collectAsState()
    val prayerSchedule by viewModel.prayerSchedule.collectAsState()
    val configs by viewModel.mosqueConfigs.collectAsState()
    val isSilencedNow by viewModel.isMosqueSilencedNow.collectAsState()
    val autoReplySmsEnabled by viewModel.mosqueAutoReplySmsEnabled.collectAsState()
    val autoReplyMessage by viewModel.mosqueAutoReplyMessage.collectAsState()
    val lastAutoSmsStatus by viewModel.lastAutoSmsStatus.collectAsState()

    var showPermissionDialog by remember { mutableStateOf(false) }
    var showPermissionDeniedDialog by remember { mutableStateOf(false) }
    var deniedPermissionsList by remember { mutableStateOf<List<String>>(emptyList()) }
    var pendingTestSmsAfterGrant by remember { mutableStateOf(false) }
    var showEditMessageDialog by remember { mutableStateOf(false) }
    var showTestSmsDialog by remember { mutableStateOf(false) }

    val is24Hour = remember(context) { TimeFormatUtils.is24HourFormat(context) }
    val localTzDisplay = remember { TimeFormatUtils.getLocalTimeZoneDisplay() }
    var hasFullSilentAccess by remember {
        mutableStateOf(MosqueModeManager.isFullSilentAccessGranted(context))
    }

    val requiredAutoReplyPermissions = remember {
        listOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG
        )
    }

    fun getMissingAutoReplyPermissions(): List<String> {
        return requiredAutoReplyPermissions.filter { perm ->
            ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED
        }
    }

    fun checkHasAutoReplyPermissions(): Boolean {
        return getMissingAutoReplyPermissions().isEmpty()
    }

    var hasAutoReplyPermissions by remember {
        mutableStateOf(checkHasAutoReplyPermissions())
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasAutoReplyPermissions = checkHasAutoReplyPermissions()
                hasFullSilentAccess = MosqueModeManager.isFullSilentAccessGranted(context)
                viewModel.refreshLastAutoSmsStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        val missing = getMissingAutoReplyPermissions()
        hasAutoReplyPermissions = missing.isEmpty()
        if (missing.isEmpty()) {
            viewModel.toggleMosqueAutoReplySms(true)
            Toast.makeText(
                context,
                localizedText(
                    uiLang,
                    "নামাজকালীন অটো-এসএমএস সফলভাবে সক্রিয় ও সংরক্ষিত হয়েছে",
                    "Prayer Auto-SMS reply enabled and saved"
                ),
                Toast.LENGTH_SHORT
            ).show()
            if (pendingTestSmsAfterGrant) {
                pendingTestSmsAfterGrant = false
                showTestSmsDialog = true
            }
        } else {
            pendingTestSmsAfterGrant = false
            viewModel.toggleMosqueAutoReplySms(false)
            deniedPermissionsList = missing
            showPermissionDeniedDialog = true
        }
    }

    LaunchedEffect(ringerMode) {
        hasFullSilentAccess = MosqueModeManager.isFullSilentAccessGranted(context)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = uiText("মসজিদ মোড (সাইলেন্ট ও ভাইব্রেশন)", "Mosque Mode (Silent & Vibrate)"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = ChampagneGold,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.size(48.dp))
            }
        }

        // 1. Master Toggle & Current Status Card (Jamat-Based)
        item {
            FrostedGlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DarkEmeraldElevated,
                borderColor = if (isEnabled) MintGlow.copy(alpha = 0.4f) else HairlineBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = uiText("স্বয়ংক্রিয় জামাত মসজিদ মোড", "Auto Jamat Mosque Mode"),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiText(
                                "পুরো ওয়াক্তের বদলে শুধুমাত্র জামাতের নির্দিষ্ট সময়ে (শুরু ও শেষ বাফার অনুযায়ী) ফোন ভাইব্রেশন/সাইলেন্ট হবে এবং পরে পূর্বের রিংটোন ফিরে আসবে।",
                                "Automatically switches your phone to Vibrate/Silent around Jama'at time (using start/end buffers) and restores your ringer afterward."
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateGrayMuted,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { viewModel.toggleMosqueMode(it) },
                        modifier = Modifier.testTag("mosque_mode_master_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ChampagneGold,
                            checkedTrackColor = MintGlow.copy(alpha = 0.5f),
                            uncheckedThumbColor = SlateGrayMuted,
                            uncheckedTrackColor = DarkEmeraldSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isEnabled) {
                        MintPillBadge(
                            text = uiText(
                                "জামাত মোড চালু • ${ringerMode.localizedShortTitle(uiLang)}",
                                "Jamat Mode On • ${ringerMode.localizedShortTitle(uiLang)}"
                            ),
                            icon = Icons.Filled.Schedule
                        )
                    }
                    if (isSilencedNow) {
                        GoldPillBadge(
                            text = uiText(
                                "বর্তমানে ${ringerMode.localizedShortTitle(uiLang)} চালু",
                                "Currently ${ringerMode.localizedShortTitle(uiLang)}"
                            ),
                            icon = if (ringerMode == MosqueRingerMode.VIBRATE) Icons.Filled.Vibration else Icons.AutoMirrored.Filled.VolumeOff
                        )
                    }
                }
            }
        }

        // 2. Vibration vs Silent Mode Selector Card (No DND confusion)
        item {
            FrostedGlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DarkEmeraldElevated,
                borderColor = ChampagneGold.copy(alpha = 0.35f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = uiText("মসজিদ রিংগার ধরন নির্বাচন", "Mosque Ringer Mode"),
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = ChampagneGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = ringerMode.localizedSubtitle(uiLang),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateGrayMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(MosqueRingerMode.VIBRATE, MosqueRingerMode.SILENT).forEach { mode ->
                        val selected = ringerMode == mode
                        Surface(
                            onClick = {
                                hasFullSilentAccess = MosqueModeManager.isFullSilentAccessGranted(context)
                                viewModel.setMosqueRingerMode(mode)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 52.dp)
                                .testTag("mosque_mode_option_${mode.id.lowercase()}"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) DarkEmeraldCard else DarkEmeraldSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (selected) 1.2.dp else 0.5.dp,
                                color = if (selected) MintGlow else HairlineBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (mode == MosqueRingerMode.VIBRATE) Icons.Filled.Vibration else Icons.AutoMirrored.Filled.VolumeOff,
                                    contentDescription = mode.localizedTitle(uiLang),
                                    tint = if (selected) MintGlow else SlateGrayMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = mode.localizedTitle(uiLang),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = if (selected) MintGlow else TextPrimary,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }

                if (ringerMode == MosqueRingerMode.SILENT && !hasFullSilentAccess) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E1408))
                            .border(0.5.dp, ChampagneGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uiText(
                                "পূর্ণ সাইলেন্টের জন্য সাউন্ড অ্যাক্সেস অনুমতি দিন (নতুবা ভাইব্রেশন সক্রিয় থাকবে)",
                                "Grant Sound Access permission for full Silent mode (otherwise Vibrate will be used)"
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = ChampagneGold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                context.startActivity(MosqueModeManager.openSoundAccessSettingsIntent())
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(uiText("অনুমতি", "Allow"), color = MintGlow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                val canExactAlarms = MosqueModeScheduler.canScheduleExactAlarms(context)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && !canExactAlarms) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E1408))
                            .border(0.5.dp, ChampagneGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uiText(
                                "জামাত শেষে ঠিক সময়ে ফোন নরমাল মোডে ফেরত আনতে 'Alarms & Reminders' অনুমতি দিন।",
                                "Allow 'Alarms & Reminders' so Mosque Mode restores Normal ringer right on time."
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = ChampagneGold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                try {
                                    context.startActivity(MosqueModeScheduler.openExactAlarmSettingsIntent(context))
                                } catch (_: Exception) {}
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("mosque_exact_alarm_allow_button")
                        ) {
                            Text(uiText("অনুমতি দিন", "Allow"), color = MintGlow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 3. Pre-Prayer Wudu & Preparation Alert (10-15 Minutes Before Jama'at Time) Card
        item {
            FrostedGlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DarkEmeraldElevated,
                borderColor = if (preAlertEnabled) MintGlow.copy(alpha = 0.4f) else HairlineBorder
            ) {
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
                            Icon(
                                imageVector = Icons.Filled.NotificationsActive,
                                contentDescription = null,
                                tint = if (preAlertEnabled) MintGlow else ChampagneGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = uiText(
                                    "জামাতের ${preAlertMinutes} মিনিট পূর্বে প্রস্তুতি এলার্ট",
                                    "${preAlertMinutes} Min Pre-Jama'at Prep Alert"
                                ),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiText(
                                "আপনার সেট করা জামাতের সময়ের ${preAlertMinutes} মিনিট আগে ওযু, পরিষ্কার-পরিচ্ছন্ন হওয়া ও নামাজের প্রস্তুতির এলার্ট পাঠাবে।",
                                "Sends a Wudu and preparation alert ${preAlertMinutes} minutes before your configured Jama'at time."
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateGrayMuted,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Switch(
                        checked = preAlertEnabled,
                        onCheckedChange = { viewModel.togglePrePrayerAlert(it) },
                        modifier = Modifier.testTag("mosque_pre_alert_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ChampagneGold,
                            checkedTrackColor = MintGlow.copy(alpha = 0.5f),
                            uncheckedThumbColor = SlateGrayMuted,
                            uncheckedTrackColor = DarkEmeraldSurface
                        )
                    )
                }

                if (preAlertEnabled) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(10, 15, 20, 30).forEach { mins ->
                            val selected = preAlertMinutes == mins
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) DarkEmeraldCard else DarkEmeraldSurface)
                                    .border(0.6.dp, if (selected) ChampagneGold else HairlineBorder, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.setPrePrayerAlertMinutes(mins) }
                                    .padding(vertical = 8.dp)
                                    .testTag("pre_alert_mins_$mins"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = uiText("$mins মি. আগে", "$mins min before"),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (selected) ChampagneGold else TextPrimary,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Automated Auto-Reply SMS During Mosque Mode
        item {
            FrostedGlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DarkEmeraldElevated,
                borderColor = if (autoReplySmsEnabled) MintGlow.copy(alpha = 0.5f) else HairlineBorder
            ) {
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
                            Icon(
                                imageVector = Icons.Filled.Sms,
                                contentDescription = null,
                                tint = if (autoReplySmsEnabled) MintGlow else ChampagneGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = uiText("নামাজকালীন অটো-এসএমএস রিপ্লাই", "Auto-SMS Reply During Prayer"),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiText(
                                "নামাজ চলাকালীন ইনকামিং কল আসলে কলারকে আপনার সিম কার্ড থেকে অটোমেটিক টেক্সট মেসেজ পাঠানো হবে।",
                                "Automatically sends an SMS reply via your device SIM when incoming calls arrive during prayer."
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateGrayMuted,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Switch(
                        checked = autoReplySmsEnabled,
                        onCheckedChange = { willEnable ->
                            if (willEnable) {
                                val missing = getMissingAutoReplyPermissions()
                                if (missing.isEmpty()) {
                                    hasAutoReplyPermissions = true
                                    viewModel.toggleMosqueAutoReplySms(true)
                                    Toast.makeText(
                                        context,
                                        localizedText(
                                            uiLang,
                                            "অটো-এসএমএস রিপ্লাই সক্রিয় ও সংরক্ষিত হয়েছে",
                                            "Auto-SMS reply enabled and saved"
                                        ),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    pendingTestSmsAfterGrant = false
                                    permissionsLauncher.launch(missing.toTypedArray())
                                }
                            } else {
                                viewModel.toggleMosqueAutoReplySms(false)
                            }
                        },
                        modifier = Modifier.testTag("mosque_auto_sms_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ChampagneGold,
                            checkedTrackColor = MintGlow.copy(alpha = 0.5f),
                            uncheckedThumbColor = SlateGrayMuted,
                            uncheckedTrackColor = DarkEmeraldSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Zero-cost notice badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = DarkEmeraldSurface,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = 0.5.dp,
                            color = ChampagneGold.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoneyOff,
                        contentDescription = null,
                        tint = MintGlow,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = uiText(
                            "১০০% সম্পূর্ণ ফ্রি: কোনো ক্লাউড বা গেটওয়ে ফি নেই। আপনার ডিভাইসের নিজস্ব সিম কার্ডের স্ট্যান্ডার্ড এসএমএস ব্যবহার করা হয়।",
                            "100% Free: No cloud or gateway fees. Uses standard SMS directly from your SIM card."
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MintGlow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Current message preview card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = ObsidianBackground.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .border(
                            width = 0.5.dp,
                            color = if (autoReplySmsEnabled) MintGlow.copy(alpha = 0.3f) else HairlineBorder,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiText("বর্তমান অটো-রিপ্লাই টেক্সট বার্তা:", "Current Auto-Reply Message:"),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = ChampagneGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        if (autoReplySmsEnabled) {
                            MintPillBadge(
                                text = uiText("সক্রিয়", "Active"),
                                icon = Icons.Filled.CheckCircle
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "“$autoReplyMessage”",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showEditMessageDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkEmeraldSurface,
                                contentColor = ChampagneGold
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(uiText("বার্তা এডিট করুন", "Edit Message"), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick = {
                                if (checkHasAutoReplyPermissions()) {
                                    showTestSmsDialog = true
                                } else {
                                    pendingTestSmsAfterGrant = true
                                    showPermissionDialog = true
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = TextPrimary
                            ),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, HairlineBorder),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("mosque_test_sms_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                tint = MintGlow,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(uiText("টেস্ট এসএমএস", "Test SMS"), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Live Permission & Last Dispatch Status Indicator
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkEmeraldSurface)
                            .clickable(enabled = !hasAutoReplyPermissions) {
                                showPermissionDialog = true
                            }
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (hasAutoReplyPermissions) Icons.Filled.VerifiedUser else Icons.Filled.WarningAmber,
                            contentDescription = null,
                            tint = if (hasAutoReplyPermissions) MintGlow else ChampagneGold,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = if (hasAutoReplyPermissions) {
                                uiText("সকল সিম ও কল পারমিশন অনুমোদিত", "All SIM & call permissions granted")
                            } else {
                                uiText("প্রয়োজনীয় পারমিশন অনুমোদিত নয় — অনুমতি দিতে ট্যাপ করুন", "Required permissions missing — tap to grant")
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (hasAutoReplyPermissions) MintGlow else ChampagneGold,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    val (lastStatusText, lastStatusOk, lastStatusTs) = lastAutoSmsStatus
                    if (lastStatusTs > 0L && lastStatusText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (lastStatusOk) MintGlow.copy(alpha = 0.08f) else Color(0xFF331418)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (lastStatusOk) Icons.Filled.CheckCircle else Icons.Filled.ErrorOutline,
                                contentDescription = null,
                                tint = if (lastStatusOk) MintGlow else Color(0xFFFF8A93),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = uiText("সর্বশেষ অবস্থা: $lastStatusText", "Last status: $lastStatusText"),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (lastStatusOk) TextPrimary else Color(0xFFFFB4BA),
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // 5. Section Header for Custom Mosque & Jamat Schedule
        item {
            SectionHeader(
                title = uiText("ওয়াক্ত ও জামাত ভিত্তিক সময়সূচি", "Waqt & Jama'at Schedule"),
                subtitle = uiText(
                    "লোকাল সময় ($localTzDisplay) • জামাত ও কাস্টম ওয়াক্ত নির্ধারণ করুন",
                    "Local time ($localTzDisplay) • Configure Jama'at & custom Waqt"
                )
            )
        }

        // 6. Per-Prayer Mosque Schedule Cards (Fajr, Dhuhr, Asr, Maghrib, Isha)
        items(MosqueModeScheduler.PRAYERS) { prayer ->
            val config = configs[prayer] ?: com.example.data.mosque.PrayerMosqueConfig.defaultFor(prayer)
            val scheduleItem = prayerSchedule?.items?.find { it.prayer == prayer }
            PrayerMosqueItemCard(
                prayer = prayer,
                config = config,
                scheduleItem = scheduleItem,
                prayerSchedule = prayerSchedule,
                ringerMode = ringerMode,
                is24Hour = is24Hour,
                onToggleEnabled = { viewModel.togglePrayerMosque(prayer, it) },
                onSetBuffer = { viewModel.setPrayerBuffer(prayer, it) },
                onSetDuration = { viewModel.setPrayerDuration(prayer, it) },
                onSetCustomTimes = { start, end -> viewModel.setPrayerCustomTimes(prayer, start, end) },
                onSetWaqtAndJamat = { waqt24, jamat24 -> viewModel.setPrayerWaqtAndJamatTimes(prayer, waqt24, jamat24) },
                onResetTimings = { viewModel.resetPrayerTimings(prayer) }
            )
        }

        // 7. Instant Quick Test Controls & Profile Restore
        item {
            FrostedGlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = uiText("তাৎক্ষণিক টেস্ট ও ম্যানুয়াল নিয়ন্ত্রণ", "Quick Test & Manual Controls"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = ChampagneGold,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = uiText(
                        "পরীক্ষামূলকভাবে ${ringerMode.titleBengali} চালু করলে পূর্বের রিংটোন মোড সংরক্ষিত হবে এবং পুনরুদ্ধারে তা হুবহু ফিরে আসবে:",
                        "Testing ${ringerMode.localizedTitle(uiLang)} saves your current ringer profile and restores it cleanly afterward:"
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.testSilenceNow() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C1114),
                            contentColor = Color(0xFFFFB4BA)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mosque_test_activate_button")
                    ) {
                        Icon(
                            imageVector = if (ringerMode == MosqueRingerMode.VIBRATE) Icons.Filled.Vibration else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = uiText(
                                "এখনই ${ringerMode.localizedShortTitle(uiLang)}",
                                "${ringerMode.localizedShortTitle(uiLang)} Now"
                            ),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = { viewModel.testRestoreNow() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkEmeraldElevated,
                            contentColor = MintGlow
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mosque_test_restore_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(uiText("রিংটোন পুনরুদ্ধার", "Restore Ringer"), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    // --- DIALOGS FOR AUTO-REPLY SMS ---
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = {
                showPermissionDialog = false
                pendingTestSmsAfterGrant = false
            },
            containerColor = DarkEmeraldElevated,
            titleContentColor = ChampagneGold,
            textContentColor = TextPrimary,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Security,
                        contentDescription = null,
                        tint = ChampagneGold
                    )
                    Text(
                        text = uiText("অটো-এসএমএস পারমিশন আবশ্যক", "Auto-SMS Permissions Required"),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = uiText(
                            "নামাজ চলাকালীন ইনকামিং কল শনাক্ত করে সরাসরি আপনার সিম কার্ড থেকে বিনামূল্যে অটোমেটিক টেক্সট পাঠাতে নিচের পারমিশনগুলো প্রয়োজন:",
                            "To detect incoming calls during prayer and send a free SMS directly via your SIM card, please grant:"
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 12.sp)
                    )
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.Phone, contentDescription = null, tint = MintGlow, modifier = Modifier.size(16.dp))
                        Text(
                            text = uiText(
                                "ফোন স্টেট ও কল লগ (READ_PHONE_STATE, READ_CALL_LOG): নামাজে থাকা অবস্থায় কল আসার সাথে সাথে কলারের নম্বর শনাক্ত করার জন্য।",
                                "Phone State & Call Log: Identify the caller's number when a call arrives during prayer."
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontSize = 11.sp)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.Sms, contentDescription = null, tint = MintGlow, modifier = Modifier.size(16.dp))
                        Text(
                            text = uiText(
                                "এসএমএস প্রেরণ (SEND_SMS): কোনো ইন্টারনেট বা থার্ড-পার্টি চার্জ ছাড়া সরাসরি আপনার নিজস্ব সিম থেকে বার্তা পাঠাতে।",
                                "Send SMS: Send the reply directly from your SIM with zero internet or gateway fees."
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontSize = 11.sp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionDialog = false
                        val missing = getMissingAutoReplyPermissions()
                        if (missing.isEmpty()) {
                            hasAutoReplyPermissions = true
                            viewModel.toggleMosqueAutoReplySms(true)
                        } else {
                            permissionsLauncher.launch(missing.toTypedArray())
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChampagneGold,
                        contentColor = ObsidianBackground
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(uiText("অনুমতি দিন", "Grant Permission"), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        pendingTestSmsAfterGrant = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = SlateGrayMuted)
                ) {
                    Text(uiText("বাতিল", "Cancel"))
                }
            }
        )
    }

    if (showPermissionDeniedDialog) {
        val activity = context as? Activity
        val canPromptAgain = activity != null && deniedPermissionsList.any { perm ->
            ActivityCompat.shouldShowRequestPermissionRationale(activity, perm)
        }

        fun openAppPermissionSettings() {
            try {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (_: Exception) {
                Toast.makeText(
                    context,
                    localizedText(uiLang, "দয়া করে ফোনের সেটিংস থেকে অ্যাপ পারমিশন চালু করুন", "Please enable app permissions from device Settings"),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        AlertDialog(
            onDismissRequest = { showPermissionDeniedDialog = false },
            containerColor = DarkEmeraldElevated,
            titleContentColor = ChampagneGold,
            textContentColor = TextPrimary,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.WarningAmber,
                        contentDescription = null,
                        tint = ChampagneGold
                    )
                    Text(
                        text = uiText("পারমিশন ছাড়া অটো-এসএমএস কাজ করবে না", "Permissions Required for Auto-SMS"),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = uiText(
                            "নামাজকালীন ফ্রি অটো-এসএমএস চালু করতে নিচের অনুমোদনগুলো প্রয়োজন, যা এখনো দেওয়া হয়নি:",
                            "The following permissions are required for free Auto-SMS replies during prayer:"
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 12.sp)
                    )

                    if (deniedPermissionsList.contains(Manifest.permission.SEND_SMS)) {
                        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.Sms, contentDescription = null, tint = Color(0xFFFF8A93), modifier = Modifier.size(16.dp))
                            Text(
                                text = uiText(
                                    "• এসএমএস প্রেরণ (SEND_SMS): আপনার ফোনের সিম কার্ড দিয়ে বিনামূল্যে উত্তর পাঠাতে।",
                                    "• Send SMS (SEND_SMS): Send replies for free via your SIM card."
                                ),
                                style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontSize = 11.sp)
                            )
                        }
                    }
                    if (deniedPermissionsList.contains(Manifest.permission.READ_PHONE_STATE)) {
                        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.Phone, contentDescription = null, tint = Color(0xFFFF8A93), modifier = Modifier.size(16.dp))
                            Text(
                                text = uiText(
                                    "• ফোন কল স্টেট (READ_PHONE_STATE): নামাজের সময় কল আসছে কিনা বুঝতে।",
                                    "• Phone State (READ_PHONE_STATE): Detect incoming calls during prayer."
                                ),
                                style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontSize = 11.sp)
                            )
                        }
                    }
                    if (deniedPermissionsList.contains(Manifest.permission.READ_CALL_LOG)) {
                        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.History, contentDescription = null, tint = Color(0xFFFF8A93), modifier = Modifier.size(16.dp))
                            Text(
                                text = uiText(
                                    "• কল লগ (READ_CALL_LOG): মিসড কল করা ব্যক্তির নম্বর শনাক্ত করতে।",
                                    "• Call Log (READ_CALL_LOG): Identify the caller's phone number."
                                ),
                                style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontSize = 11.sp)
                            )
                        }
                    }

                    if (!canPromptAgain) {
                        Text(
                            text = uiText(
                                "টিপস: আপনি পূর্বে অনুমতি প্রত্যাখ্যান করায় সিস্টেম পপ-আপ আর দেখাবে না। নিচের 'অ্যাপ সেটিংস খুলুন' বাটনে গিয়ে Permissions থেকে SMS এবং Phone/Call Logs চালু করুন।",
                                "Tip: Since permissions were previously denied, open App Settings below and enable SMS and Phone/Call Logs under Permissions."
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = ChampagneGold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionDeniedDialog = false
                        if (canPromptAgain) {
                            val missing = getMissingAutoReplyPermissions()
                            if (missing.isNotEmpty()) {
                                permissionsLauncher.launch(missing.toTypedArray())
                            }
                        } else {
                            openAppPermissionSettings()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChampagneGold,
                        contentColor = ObsidianBackground
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (canPromptAgain) uiText("পুনরায় অনুমতি দিন", "Try Again") else uiText("অ্যাপ সেটিংস খুলুন", "Open App Settings"),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (canPromptAgain) {
                        TextButton(
                            onClick = {
                                showPermissionDeniedDialog = false
                                openAppPermissionSettings()
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = MintGlow)
                        ) {
                            Text(uiText("সেটিংস", "Settings"))
                        }
                    }
                    TextButton(
                        onClick = { showPermissionDeniedDialog = false },
                        colors = ButtonDefaults.textButtonColors(contentColor = SlateGrayMuted)
                    ) {
                        Text(uiText("বন্ধ করুন", "Close"))
                    }
                }
            }
        )
    }

    if (showEditMessageDialog) {
        var tempMessage by remember { mutableStateOf(autoReplyMessage) }
        val presets = listOf(
            "আমি এখন নামাজে/সালাতে আছি। নামাজ শেষে আপনার সাথে যোগাযোগ করব ইনশাআল্লাহ।",
            "আসসালামু আলাইকুম, বর্তমানে মসজিদে নামাজে রত আছি। নামাজ শেষে ফিরতি কল বা মেসেজ দেওয়া হবে ইনশাআল্লাহ।",
            "I am currently in prayer (Salah). I will get back to you shortly, InshaAllah."
        )

        AlertDialog(
            onDismissRequest = { showEditMessageDialog = false },
            containerColor = DarkEmeraldElevated,
            titleContentColor = ChampagneGold,
            textContentColor = TextPrimary,
            title = {
                Text(
                    text = uiText("অটো-রিপ্লাই বার্তা কাস্টমাইজ", "Customize Auto-Reply Message"),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = uiText(
                            "নামাজ চলাকালীন ইনকামিং কল মিসড হলে কলারকে এই বার্তাটি পাঠানো হবে:",
                            "This SMS will be sent to callers when you miss a call during prayer:"
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 12.sp)
                    )

                    OutlinedTextField(
                        value = tempMessage,
                        onValueChange = { if (it.length <= 160) tempMessage = it },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MintGlow,
                            unfocusedBorderColor = HairlineBorder,
                            cursorColor = MintGlow
                        ),
                        supportingText = {
                            Text(
                                text = uiText("${tempMessage.length} / 160 অক্ষর", "${tempMessage.length} / 160 characters"),
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateGrayMuted)
                            )
                        }
                    )

                    Text(
                        text = uiText("দ্রুত সিলেক্ট করুন (প্রিসেট):", "Quick Select (Presets):"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    presets.forEach { preset ->
                        Surface(
                            onClick = { tempMessage = preset },
                            shape = RoundedCornerShape(8.dp),
                            color = if (tempMessage == preset) MintGlow.copy(alpha = 0.15f) else DarkEmeraldSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                0.5.dp,
                                if (tempMessage == preset) MintGlow else HairlineBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = preset,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (tempMessage == preset) MintGlow else TextPrimary,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalMsg = tempMessage.trim()
                        if (finalMsg.isNotBlank()) {
                            viewModel.setMosqueAutoReplyMessage(finalMsg)
                            showEditMessageDialog = false
                            Toast.makeText(
                                context,
                                localizedText(uiLang, "অটো-রিপ্লাই বার্তা সংরক্ষিত হয়েছে", "Auto-reply message saved"),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                localizedText(uiLang, "মেসেজ খালি রাখা যাবে না", "Message cannot be empty"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChampagneGold,
                        contentColor = ObsidianBackground
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(uiText("সংরক্ষণ করুন", "Save"), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEditMessageDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = SlateGrayMuted)
                ) {
                    Text(uiText("বাতিল", "Cancel"))
                }
            }
        )
    }

    if (showTestSmsDialog) {
        var testPhoneNumber by remember { mutableStateOf("") }
        var isSending by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showTestSmsDialog = false },
            containerColor = DarkEmeraldElevated,
            titleContentColor = ChampagneGold,
            textContentColor = TextPrimary,
            title = {
                Text(
                    text = uiText("টেস্ট অটো-এসএমএস পাঠান", "Send Test Auto-SMS"),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = uiText(
                            "আপনার বর্তমান সেট করা অটো-রিপ্লাই বার্তা টেস্ট করতে একটি ফোন নম্বর লিখুন:",
                            "Enter a phone number to test your current auto-reply SMS message:"
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 12.sp)
                    )

                    OutlinedTextField(
                        value = testPhoneNumber,
                        onValueChange = { testPhoneNumber = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(uiText("যেমন: 01700000000", "e.g. 01700000000"), color = SlateGrayMuted, fontSize = 12.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MintGlow,
                            unfocusedBorderColor = HairlineBorder,
                            cursorColor = MintGlow
                        )
                    )

                    Text(
                        text = uiText("পাঠানো হবে: “$autoReplyMessage”", "Message to send: “$autoReplyMessage”"),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ChampagneGold,
                            fontSize = 11.sp
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = testPhoneNumber.trim()
                        if (target.isNotBlank()) {
                            isSending = true
                            viewModel.sendTestAutoReplySms(target) { success, msg ->
                                isSending = false
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                if (success) {
                                    showTestSmsDialog = false
                                }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                localizedText(uiLang, "ফোন নম্বর লিখুন", "Please enter a phone number"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    enabled = !isSending,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MintGlow,
                        contentColor = ObsidianBackground
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isSending) uiText("পাঠানো হচ্ছে...", "Sending...") else uiText("মেসেজ পাঠান", "Send SMS"),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showTestSmsDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = SlateGrayMuted)
                ) {
                    Text(uiText("বাতিল", "Cancel"))
                }
            }
        )
    }
}

@Composable
fun PrayerMosqueItemCard(
    prayer: PrayerName,
    config: com.example.data.mosque.PrayerMosqueConfig,
    scheduleItem: com.example.data.model.PrayerTimeItem?,
    prayerSchedule: com.example.data.model.PrayerSchedule?,
    ringerMode: MosqueRingerMode,
    is24Hour: Boolean,
    onToggleEnabled: (Boolean) -> Unit,
    onSetBuffer: (Int) -> Unit,
    onSetDuration: (Int) -> Unit,
    onSetCustomTimes: (String?, String?) -> Unit,
    onSetWaqtAndJamat: (String?, String?) -> Unit,
    onResetTimings: () -> Unit
) {
    val context = LocalContext.current
    val uiLang = LocalAppLanguage.current
    var isCustomMode by remember(config) {
        mutableStateOf(!config.customStartTime.isNullOrBlank() && !config.customEndTime.isNullOrBlank())
    }

    // Time picker targets: "WAQT", "JAMAT", "MANUAL_START", "MANUAL_END"
    var activePickerTarget by remember { mutableStateOf<String?>(null) }

    val waqtPair = remember(config.customPrayerTime, scheduleItem?.timeMillis) {
        config.customPrayerTime?.let { TimeFormatUtils.parseTimeString(it) }
            ?: scheduleItem?.timeMillis?.let { ms ->
                val c = Calendar.getInstance().apply { timeInMillis = ms }
                Pair(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))
            } ?: Pair(12, 0)
    }

    val jamatPair = remember(config.jamatTime, scheduleItem?.jamatTimeMillis, waqtPair) {
        config.jamatTime?.let { TimeFormatUtils.parseTimeString(it) }
            ?: scheduleItem?.jamatTimeMillis?.takeIf { it > 0L }?.let { ms ->
                val c = Calendar.getInstance().apply { timeInMillis = ms }
                Pair(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))
            } ?: run {
                val totalMin = waqtPair.first * 60 + waqtPair.second + config.defaultJamatOffsetMinutes
                Pair((totalMin / 60) % 24, totalMin % 60)
            }
    }

    val defaultManualStart24 = remember(jamatPair, config.bufferBeforeMinutes) {
        val total = (jamatPair.first * 60 + jamatPair.second - config.bufferBeforeMinutes + 1440) % 1440
        TimeFormatUtils.toCanonicalTime24(total / 60, total % 60)
    }
    val defaultManualEnd24 = remember(jamatPair, config.durationMinutes) {
        val total = (jamatPair.first * 60 + jamatPair.second + config.durationMinutes) % 1440
        TimeFormatUtils.toCanonicalTime24(total / 60, total % 60)
    }

    val manualStartPair = remember(config.customStartTime, defaultManualStart24) {
        TimeFormatUtils.parseTimeString(config.customStartTime ?: defaultManualStart24) ?: Pair(13, 10)
    }
    val manualEndPair = remember(config.customEndTime, defaultManualEnd24) {
        TimeFormatUtils.parseTimeString(config.customEndTime ?: defaultManualEnd24) ?: Pair(13, 40)
    }

    val computedWindowText = remember(prayerSchedule, config, is24Hour, uiLang) {
        if (prayerSchedule != null) {
            val (startMs, endMs) = MosqueModeScheduler.computePrayerWindow(
                prayer = prayer,
                config = config,
                todaySchedule = prayerSchedule,
                tomorrowSchedule = prayerSchedule,
                now = 0L
            )
            val sStr = TimeFormatUtils.formatEpochMillis(startMs, is24Hour)
            val eStr = TimeFormatUtils.formatEpochMillis(endMs, is24Hour)
            val totalMins = ((endMs - startMs) / 60_000L).coerceAtLeast(1L)
            localizedText(uiLang, "$sStr – $eStr ($totalMins মি.)", "$sStr – $eStr ($totalMins min)")
        } else {
            val sStr = TimeFormatUtils.formatHourMinute(manualStartPair.first, manualStartPair.second, is24Hour)
            val eStr = TimeFormatUtils.formatHourMinute(manualEndPair.first, manualEndPair.second, is24Hour)
            "$sStr – $eStr"
        }
    }

    FrostedGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("mosque_prayer_card_${prayer.name.lowercase()}"),
        backgroundColor = if (config.enabled) DarkEmeraldElevated else DarkEmeraldSurface.copy(alpha = 0.6f),
        borderColor = if (config.enabled) ChampagneGold.copy(alpha = 0.35f) else HairlineBorder
    ) {
        // Prayer Header & Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (config.enabled) DarkEmeraldCard else DarkEmeraldSurface)
                        .border(0.5.dp, if (config.enabled) ChampagneGold else HairlineBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = when (prayer) {
                        PrayerName.FAJR -> Icons.Filled.WbTwilight
                        PrayerName.DHUHR -> Icons.Filled.WbSunny
                        PrayerName.ASR -> Icons.Filled.FilterVintage
                        PrayerName.MAGHRIB -> Icons.Filled.NightsStay
                        PrayerName.ISHA -> Icons.Filled.Bedtime
                        else -> Icons.Filled.AccessTime
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (config.enabled) ChampagneGold else SlateGrayMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = prayer.localizedName(uiLang),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (config.enabled) TextPrimary else SlateGrayMuted
                            )
                        )
                        Text(
                            text = prayer.arabicName,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = ChampagneGold.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        )
                    }
                    Text(
                        text = if (config.enabled) "${ringerMode.localizedShortTitle(uiLang)}: $computedWindowText"
                        else uiText("মসজিদ মোড নিষ্ক্রিয়", "Mosque Mode Disabled"),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (config.enabled) MintGlow else SlateGrayMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Switch(
                checked = config.enabled,
                onCheckedChange = onToggleEnabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = ChampagneGold,
                    checkedTrackColor = MintGlow.copy(alpha = 0.4f),
                    uncheckedThumbColor = SlateGrayMuted,
                    uncheckedTrackColor = DarkEmeraldSurface
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Local Mosque Waqt & Jamat Time Customization Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val waqtDisplay = scheduleItem?.formattedTime
                ?: TimeFormatUtils.formatHourMinute(waqtPair.first, waqtPair.second, is24Hour)
            val jamatDisplay = scheduleItem?.formattedJamatTime
                ?: TimeFormatUtils.formatHourMinute(jamatPair.first, jamatPair.second, is24Hour)

            Surface(
                onClick = { activePickerTarget = "WAQT" },
                modifier = Modifier
                    .weight(1f)
                    .testTag("mosque_edit_waqt_${prayer.name.lowercase()}"),
                shape = RoundedCornerShape(10.dp),
                color = DarkEmeraldSurface,
                border = androidx.compose.foundation.BorderStroke(
                    0.6.dp,
                    if (config.customPrayerTime != null) MintGlow else HairlineBorder
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (config.customPrayerTime != null) uiText("কাস্টম ওয়াক্ত", "Custom Waqt")
                            else uiText("ওয়াক্ত শুরু", "Waqt Start"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SlateGrayMuted,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = waqtDisplay,
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = ChampagneGold,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = uiText("ওয়াক্ত পরিবর্তন", "Edit Waqt"),
                        tint = ChampagneGold.copy(alpha = 0.8f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Surface(
                onClick = { activePickerTarget = "JAMAT" },
                modifier = Modifier
                    .weight(1f)
                    .testTag("mosque_edit_jamat_${prayer.name.lowercase()}"),
                shape = RoundedCornerShape(10.dp),
                color = DarkEmeraldSurface,
                border = androidx.compose.foundation.BorderStroke(
                    0.6.dp,
                    if (config.jamatTime != null) MintGlow else ChampagneGold.copy(alpha = 0.35f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (config.jamatTime != null) uiText("কাস্টম জামাত", "Custom Jamat")
                            else uiText("জামাতের সময়", "Jama'at Time"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SlateGrayMuted,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = jamatDisplay,
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = MintGlow,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = uiText("জামাত পরিবর্তন", "Edit Jama'at"),
                        tint = MintGlow,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            if (config.customPrayerTime != null || config.jamatTime != null) {
                IconButton(
                    onClick = {
                        onResetTimings()
                        Toast.makeText(
                            context,
                            localizedText(
                                uiLang,
                                "${prayer.bengaliName}-এর সময় স্বাভাবিক করা হয়েছে",
                                "${prayer.englishName} timings reset to default"
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkEmeraldSurface)
                        .testTag("mosque_reset_timings_${prayer.name.lowercase()}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = uiText("স্বাভাবিক সময়", "Reset Timings"),
                        tint = ChampagneGold,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        if (config.enabled) {
            Spacer(modifier = Modifier.height(12.dp))

            // Sub Mode Selector: Jamat Buffer vs Fixed Manual Local Time
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkEmeraldSurface)
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (!isCustomMode) DarkEmeraldElevated else Color.Transparent)
                        .clickable {
                            isCustomMode = false
                            onSetCustomTimes(null, null)
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiText("জামাত বাফার (স্বয়ংক্রিয়)", "Jama'at Buffer (Auto)"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (!isCustomMode) ChampagneGold else SlateGrayMuted,
                            fontWeight = if (!isCustomMode) FontWeight.SemiBold else FontWeight.Normal
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isCustomMode) DarkEmeraldElevated else Color.Transparent)
                        .clickable {
                            isCustomMode = true
                            val startToSave = config.customStartTime ?: defaultManualStart24
                            val endToSave = config.customEndTime ?: defaultManualEnd24
                            onSetCustomTimes(startToSave, endToSave)
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiText("নির্দিষ্ট সময়সূচি (লোকাল)", "Fixed Local Schedule"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isCustomMode) ChampagneGold else SlateGrayMuted,
                            fontWeight = if (isCustomMode) FontWeight.SemiBold else FontWeight.Normal
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (!isCustomMode) {
                // Jamat Buffer Selection
                Text(
                    text = uiText(
                        "জামাতের পূর্বে ${ringerMode.localizedShortTitle(uiLang)} শুরু:",
                        "Start ${ringerMode.localizedShortTitle(uiLang)} before Jama'at:"
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 11.sp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(0, 5, 10, 15).forEach { min ->
                        val isSel = config.bufferBeforeMinutes == min
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) DarkEmeraldCard else DarkEmeraldSurface)
                                .border(0.5.dp, if (isSel) ChampagneGold else HairlineBorder, RoundedCornerShape(6.dp))
                                .clickable { onSetBuffer(min) }
                                .padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (min == 0) uiText("জামাতেই", "At Jama'at") else uiText("$min মি. আগে", "${min}m before"),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSel) ChampagneGold else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.SemiBold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = uiText(
                        "জামাত শেষে স্বাভাবিক হবে (${ringerMode.localizedShortTitle(uiLang)} স্থায়িত্ব):",
                        "Restore after Jama'at (${ringerMode.localizedShortTitle(uiLang)} duration):"
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 11.sp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(15, 20, 25, 30).forEach { dur ->
                        val isSel = config.durationMinutes == dur
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) DarkEmeraldCard else DarkEmeraldSurface)
                                .border(0.5.dp, if (isSel) ChampagneGold else HairlineBorder, RoundedCornerShape(6.dp))
                                .clickable { onSetDuration(dur) }
                                .padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiText("$dur মি. পর", "${dur}m after"),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSel) ChampagneGold else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.SemiBold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiText("জামাত-ভিত্তিক সময়সীমা: $computedWindowText", "Jama'at Window: $computedWindowText"),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MintGlow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            } else {
                // Local Time Picker Selector Buttons with OK / Save Confirmation
                val startDisplay = TimeFormatUtils.formatHourMinute(manualStartPair.first, manualStartPair.second, is24Hour)
                val endDisplay = TimeFormatUtils.formatHourMinute(manualEndPair.first, manualEndPair.second, is24Hour)

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = uiText(
                            "ট্যাপ করে স্থানীয় সময়ে (${TimeFormatUtils.getLocalTimeZoneDisplay()}) শুরু ও শেষ নির্ধারণ করুন:",
                            "Tap to set start & end in local time (${TimeFormatUtils.getLocalTimeZoneDisplay()}):"
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 11.sp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            onClick = { activePickerTarget = "MANUAL_START" },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 54.dp)
                                .testTag("manual_start_picker_${prayer.name.lowercase()}"),
                            shape = RoundedCornerShape(10.dp),
                            color = DarkEmeraldSurface,
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, ChampagneGold.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = uiText("শুরুর সময়", "Start Time"),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = SlateGrayMuted,
                                            fontSize = 10.sp
                                        )
                                    )
                                    Text(
                                        text = startDisplay,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            color = ChampagneGold,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Filled.Schedule,
                                    contentDescription = uiText("শুরুর সময় সেট করুন", "Set Start Time"),
                                    tint = ChampagneGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Surface(
                            onClick = { activePickerTarget = "MANUAL_END" },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 54.dp)
                                .testTag("manual_end_picker_${prayer.name.lowercase()}"),
                            shape = RoundedCornerShape(10.dp),
                            color = DarkEmeraldSurface,
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, MintGlow.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = uiText("শেষের সময়", "End Time"),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = SlateGrayMuted,
                                            fontSize = 10.sp
                                        )
                                    )
                                    Text(
                                        text = endDisplay,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            color = MintGlow,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Filled.Schedule,
                                    contentDescription = uiText("শেষের সময় সেট করুন", "Set End Time"),
                                    tint = MintGlow,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Reusable LocalTimePickerDialog with explicit "OK • সংরক্ষণ করুন" button
    activePickerTarget?.let { target ->
        val pName = prayer.localizedName(uiLang)
        val rShort = ringerMode.localizedShortTitle(uiLang)
        val (title, subtitle, initPair) = when (target) {
            "WAQT" -> Triple(
                uiText("$pName — ওয়াক্ত শুরুর সময়", "$pName — Waqt Start Time"),
                uiText("আপনার এলাকার মসজিদের নির্ধারিত ওয়াক্ত সময় বেছে নিন", "Choose your local mosque's Waqt start time"),
                waqtPair
            )
            "JAMAT" -> Triple(
                uiText("$pName — জামাতের সময়", "$pName — Jama'at Time"),
                uiText("আপনার মসজিদের জামাতের সময় অনুযায়ী মসজিদ মোড চলবে", "Mosque Mode will run based on this Jama'at time"),
                jamatPair
            )
            "MANUAL_START" -> Triple(
                uiText("$pName — $rShort শুরু", "$pName — Start $rShort"),
                uiText("স্থানীয় সময় অনুযায়ী $rShort শুরুর সময় সেট করুন", "Set local start time for $rShort"),
                manualStartPair
            )
            else -> Triple(
                uiText("$pName — রিংটোন ফিরে আসার সময়", "$pName — Restore Time"),
                uiText("স্থানীয় সময় অনুযায়ী জামাত শেষের সময় সেট করুন", "Set local time to restore ringer"),
                manualEndPair
            )
        }

        LocalTimePickerDialog(
            title = title,
            subtitle = subtitle,
            initialHour = initPair.first,
            initialMinute = initPair.second,
            onConfirm = { h24, m ->
                val canonical = TimeFormatUtils.toCanonicalTime24(h24, m)
                when (target) {
                    "WAQT" -> onSetWaqtAndJamat(canonical, config.jamatTime)
                    "JAMAT" -> onSetWaqtAndJamat(config.customPrayerTime, canonical)
                    "MANUAL_START" -> {
                        val endToSave = config.customEndTime ?: defaultManualEnd24
                        onSetCustomTimes(canonical, endToSave)
                    }
                    "MANUAL_END" -> {
                        val startToSave = config.customStartTime ?: defaultManualStart24
                        onSetCustomTimes(startToSave, canonical)
                    }
                }
                activePickerTarget = null
                Toast.makeText(
                    context,
                    localizedText(
                        uiLang,
                        "${prayer.bengaliName}-এর সময় সংরক্ষিত হয়েছে (${TimeFormatUtils.formatHourMinute(h24, m, is24Hour)})",
                        "${prayer.englishName} time saved (${TimeFormatUtils.formatHourMinute(h24, m, is24Hour)})"
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDismiss = { activePickerTarget = null }
        )
    }
}

// -----------------------------------------------------------------------------
// 5. ISLAMIC RUQYAH SHARIAH AUDIO MODULE
// -----------------------------------------------------------------------------
@Composable
fun RuqyahShariahAudioView(
    audioPlayer: AudioPlayerManager,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val uiLang = LocalAppLanguage.current
    val contentLang = LocalContentLanguage.current

    LaunchedEffect(Unit) {
        com.example.data.audio.MediaCacheProvider.prewarmUrls(
            context,
            RuqyahRepository.TRACKS.take(4).map { it.audioUrl }
        )
    }

    var selectedCategory by remember { mutableStateOf(RuqyahCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var showEtiquetteDialog by remember { mutableStateOf(false) }

    val playbackState by audioPlayer.playbackState.collectAsState()

    val filteredTracks = remember(selectedCategory, searchQuery, uiLang, contentLang) {
        val baseList = RuqyahRepository.getTracksForCategory(selectedCategory)
        if (searchQuery.isBlank()) {
            baseList
        } else {
            val query = searchQuery.trim().lowercase()
            baseList.filter {
                it.titleBengali.lowercase().contains(query) ||
                it.localizedTitle(uiLang).lowercase().contains(query) ||
                it.reciterName.lowercase().contains(query) ||
                it.localizedReciterName(uiLang).lowercase().contains(query) ||
                it.descriptionBengali.lowercase().contains(query) ||
                it.localizedDescription(contentLang).lowercase().contains(query) ||
                it.keyVerses.lowercase().contains(query) ||
                it.localizedKeyVerses(contentLang).lowercase().contains(query)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = uiText("ফিরে যান", "Back"),
                        tint = TextPrimary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = uiText("রুকইয়াহ শারঈয়াহ অডিও", "Ruqyah Shariah Audio"),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = uiText("কুরআনিক শেফা ও সুরক্ষা লাইব্রেরি", "Quranic Healing & Protection Library"),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateGrayMuted,
                            fontSize = 11.sp
                        )
                    )
                }

                IconButton(onClick = { showEtiquetteDialog = true }) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = uiText("আদব ও নিয়মাবলী", "Etiquette & Guidelines"),
                        tint = ChampagneGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 100% Male Voice Policy Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkEmeraldElevated.copy(alpha = 0.7f))
                    .border(0.5.dp, HairlineBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Security,
                        contentDescription = null,
                        tint = MintGlow,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = uiText("১০০% সহীহ ও পুরুষ ক্বারীদের কণ্ঠ", "100% Authentic Male Qari Recitations"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MintGlow,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = uiText("রুকইয়াহ, ক্বারী বা সূরার নাম খুঁজুন...", "Search Ruqyah, Qari or Surah..."),
                        style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = uiText("খুঁজুন", "Search"),
                        tint = SlateGrayMuted,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = uiText("মুছুন", "Clear"),
                                tint = SlateGrayMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ChampagneGold,
                    unfocusedBorderColor = HairlineBorder,
                    focusedContainerColor = DarkEmeraldSurface,
                    unfocusedContainerColor = DarkEmeraldSurface,
                    cursorColor = ChampagneGold
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Categories Horizontal Scroll
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(RuqyahCategory.entries) { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) ChampagneGold.copy(alpha = 0.2f) else DarkEmeraldSurface
                            )
                            .border(
                                width = if (isSelected) 1.2.dp else 0.5.dp,
                                color = if (isSelected) ChampagneGold else HairlineBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                            .testTag("ruqyah_cat_${category.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category.localizedTitle(uiLang),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) ChampagneGold else TextPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Track list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = if (playbackState.currentUrl.isNotBlank()) 140.dp else 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filteredTracks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiText("কোনো রুকইয়াহ পাওয়া যায়নি", "No Ruqyah tracks found"),
                                style = MaterialTheme.typography.bodyMedium.copy(color = SlateGrayMuted)
                            )
                        }
                    }
                } else {
                    items(filteredTracks, key = { it.id }) { track ->
                        val ruqyahMediaId = "ruqyah_${track.id}"
                        val isThisTrackActive = playbackState.currentMediaId == ruqyahMediaId ||
                            playbackState.currentUrl == track.audioUrl
                        val isThisTrackPlaying = isThisTrackActive && playbackState.isPlaying
                        val isThisTrackBuffering = isThisTrackActive && playbackState.isBuffering

                        FrostedGlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (isThisTrackActive) 1.dp else 0.5.dp,
                                    color = if (isThisTrackActive) ChampagneGold else HairlineBorder,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .testTag("ruqyah_card_${track.id}")
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Category Pill & Male Reciter Badge
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(ChampagneGold.copy(alpha = 0.12f))
                                            .border(0.5.dp, ChampagneGold.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = track.category.localizedTitle(uiLang),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = ChampagneGold,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.AccessTime,
                                            contentDescription = null,
                                            tint = SlateGrayMuted,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = track.durationText,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = SlateGrayMuted,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Titles (Localized + Arabic)
                                Text(
                                    text = track.localizedTitle(uiLang),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = if (isThisTrackActive) ChampagneGold else TextPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp
                                    )
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = track.titleArabic,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = AmiriFontFamily,
                                        color = ChampagneGold.copy(alpha = 0.85f),
                                        fontSize = 14.sp
                                    )
                                )

                                if (track.arabicVersePreview.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = track.arabicVersePreview,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = AmiriFontFamily,
                                            color = TextPrimary,
                                            fontSize = 15.sp,
                                            lineHeight = 24.sp
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                val ruqyahTranslit = remember(track.id, contentLang) {
                                    track.localizedArabicTransliteration(contentLang)
                                }
                                if (ruqyahTranslit.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${uiText("উচ্চারণ:", "Transliteration:")} $ruqyahTranslit",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MintGlow.copy(alpha = 0.9f),
                                            fontSize = 11.5.sp,
                                            lineHeight = 17.sp
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Reciter with Icon
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = null,
                                        tint = MintGlow,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = track.localizedReciterName(uiLang),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MintGlow,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 12.sp
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Description
                                Text(
                                    text = track.localizedDescription(contentLang),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SlateGrayMuted,
                                        fontSize = 11.5.sp,
                                        lineHeight = 16.sp
                                    )
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Key verses
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = uiText("আয়াতসমূহ:", "Verses:"),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ChampagneGold.copy(alpha = 0.7f),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    Text(
                                        text = track.localizedKeyVerses(contentLang),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextPrimary.copy(alpha = 0.75f),
                                            fontSize = 10.sp
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Play / Pause & Benefits row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Benefits snippet
                                    Text(
                                        text = track.localizedBenefits(contentLang),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = SlateGrayMuted.copy(alpha = 0.8f),
                                            fontSize = 10.sp
                                        ),
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Play / Pause Button
                                    Button(
                                        onClick = {
                                            if (isThisTrackPlaying) {
                                                audioPlayer.pause()
                                            } else if (isThisTrackActive) {
                                                audioPlayer.resume()
                                            } else {
                                                val startIdx = filteredTracks.indexOfFirst { it.id == track.id }.coerceAtLeast(0)
                                                val playlistItems = filteredTracks.map { item ->
                                                    com.example.data.audio.AudioTrackItem(
                                                        url = item.audioUrl,
                                                        title = item.localizedTitle(uiLang),
                                                        subtitle = "${item.localizedReciterName(uiLang)} • ${item.durationText}",
                                                        mediaId = "ruqyah_${item.id}",
                                                        fallbackUrls = item.fallbackUrls,
                                                        fallbackArabicText = item.arabicVersePreview
                                                    )
                                                }
                                                audioPlayer.playPlaylist(
                                                    items = playlistItems,
                                                    startIndex = startIdx
                                                )
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isThisTrackPlaying) MintGlow else ChampagneGold,
                                            contentColor = Color.Black
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            if (isThisTrackBuffering) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(14.dp),
                                                    strokeWidth = 2.dp,
                                                    color = Color.Black
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = if (isThisTrackPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                                    contentDescription = if (isThisTrackPlaying) uiText("পজ", "Pause") else uiText("চালান", "Play"),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            Text(
                                                text = if (isThisTrackBuffering) {
                                                    uiText("বাফারিং...", "Buffering...")
                                                } else if (isThisTrackPlaying) {
                                                    uiText("পজ", "Pause")
                                                } else {
                                                    uiText("শুনুন", "Listen")
                                                },
                                                fontSize = 11.sp,
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
        }

        // 2. Sticky Bottom Audio Player Bar
        if (playbackState.currentUrl.isNotBlank()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, ChampagneGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = DarkEmeraldElevated.copy(alpha = 0.96f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = playbackState.currentTitle,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = ChampagneGold,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    ),
                                    maxLines = 1
                                )
                                Text(
                                    text = playbackState.currentSubtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MintGlow,
                                        fontSize = 11.sp
                                    ),
                                    maxLines = 1
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                IconButton(
                                    onClick = { audioPlayer.skipPrevious() },
                                    enabled = playbackState.hasPrevious,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .testTag("ruqyah_skip_prev_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.SkipPrevious,
                                        contentDescription = uiText("পূর্ববর্তী", "Previous"),
                                        tint = if (playbackState.hasPrevious) ChampagneGold else SlateGrayMuted.copy(alpha = 0.4f),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                // Play / Pause
                                IconButton(
                                    onClick = {
                                        if (playbackState.isPlaying) {
                                            audioPlayer.pause()
                                        } else {
                                            audioPlayer.resume()
                                        }
                                    },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .testTag("ruqyah_play_pause_btn")
                                ) {
                                    Icon(
                                        imageVector = if (playbackState.isPlaying) Icons.Filled.PauseCircle else Icons.Filled.PlayCircle,
                                        contentDescription = uiText("চালান/বিরতি", "Play/Pause"),
                                        tint = ChampagneGold,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { audioPlayer.skipNext() },
                                    enabled = playbackState.hasNext,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .testTag("ruqyah_skip_next_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.SkipNext,
                                        contentDescription = uiText("পরবর্তী", "Next"),
                                        tint = if (playbackState.hasNext) ChampagneGold else SlateGrayMuted.copy(alpha = 0.4f),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                // Stop
                                IconButton(
                                    onClick = { audioPlayer.stop() },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .testTag("ruqyah_stop_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Stop,
                                        contentDescription = uiText("থামান", "Stop"),
                                        tint = SlateGrayMuted,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }

                        // Progress slider
                        if (playbackState.durationMs > 0) {
                            val currentSec = playbackState.currentPositionMs / 1000
                            val totalSec = playbackState.durationMs / 1000
                            val currentText = "%02d:%02d".format(currentSec / 60, currentSec % 60)
                            val totalText = "%02d:%02d".format(totalSec / 60, totalSec % 60)

                            Slider(
                                value = playbackState.currentPositionMs.toFloat(),
                                onValueChange = { audioPlayer.seekTo(it.toInt()) },
                                valueRange = 0f..playbackState.durationMs.toFloat(),
                                colors = SliderDefaults.colors(
                                    thumbColor = ChampagneGold,
                                    activeTrackColor = ChampagneGold,
                                    inactiveTrackColor = SlateGrayMuted.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(24.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = currentText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SlateGrayMuted,
                                        fontSize = 10.sp
                                    )
                                )
                                Text(
                                    text = totalText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SlateGrayMuted,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Etiquette Dialog
    if (showEtiquetteDialog) {
        AlertDialog(
            onDismissRequest = { showEtiquetteDialog = false },
            title = {
                Text(
                    text = uiText("রুকইয়াহ শারঈয়াহ শোনার আদব ও নিয়ম", "Etiquette & Rules for Listening to Ruqyah"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = uiText(
                            "১. ওযু ও পবিত্রতা: পবিত্র অবস্থায় কিবলামুখী হয়ে বসা উত্তম।",
                            "1. Wudu & Purity: It is best to sit in a state of Wudu facing the Qibla."
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                    )
                    Text(
                        text = uiText(
                            "২. একনিষ্ঠ নিয়ত: রোগমুক্তি বা হেফাজত কেবল আল্লাহর পক্ষ থেকেই আসে—এই পূর্ণ বিশ্বাস রাখা।",
                            "2. Sincere Intention: Hold firm conviction that healing and protection come solely from Allah."
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                    )
                    Text(
                        text = uiText(
                            "৩. গভীর মনোযোগ: হেডফোন ব্যবহার করে শান্ত মনে চোখ বন্ধ করে আয়াতগুলোর অর্থের দিকে খেয়াল করা।",
                            "3. Deep Focus: Use headphones if helpful, close your eyes calmly, and reflect on the verses."
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                    )
                    Text(
                        text = uiText(
                            "৪. পানি বা তেলের ওপর দম: রুকইয়াহ শেষে পানিতে ফুঁ দিয়ে পান করা বা অলিভ অয়েলে ফুঁ দিয়ে শরীরে মালিশ করা সুন্নাত সম্মত।",
                            "4. Blowing on Water/Oil: Lightly blowing on drinking water or olive oil after Ruqyah is in accordance with Sunnah."
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                    )
                    Text(
                        text = uiText(
                            "৫. নিয়মিত আমল: প্রতিদিন অন্তত ১-২ বার (ফজর ও মাগরিবের পর) একটানা ২১ দিন শোনা ফলদায়ক।",
                            "5. Consistency: Listening 1–2 times daily (after Fajr and Maghrib) consistently is beneficial."
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showEtiquetteDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChampagneGold,
                        contentColor = Color.Black
                    )
                ) {
                    Text(uiText("বুঝতে পেরেছি", "Got It"), fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkEmeraldSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// -----------------------------------------------------------------------------
// 6. ASMA-UL-HUSNA
// -----------------------------------------------------------------------------
@Composable
fun AsmaUlHusnaView(onBack: () -> Unit) {
    val contentLang = LocalContentLanguage.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = uiText("ফিরে যান", "Back"),
                    tint = TextPrimary
                )
            }
            Text(
                text = uiText("আসমা-উল-হুসনা (আল্লাহর ৯৯ নাম)", "Asma-ul-Husna (99 Names of Allah)"),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = ChampagneGold,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(AsmaUlHusnaRepository.NAMES) { item ->
                FrostedGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(DarkEmeraldElevated),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${item.number}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = ChampagneGold,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                            Column {
                                Text(
                                    text = item.localizedTransliteration(contentLang),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = TextPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = item.localizedMeaning(contentLang),
                                    style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted)
                                )
                            }
                        }

                        Text(
                            text = item.arabic,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = AmiriFontFamily,
                                color = ChampagneGold,
                                fontSize = 20.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 7. NOOR ISLAMIC AI ASSISTANT (GEMINI COMPANION IN TOOLS HUB)
// -----------------------------------------------------------------------------
@Composable
fun NoorIslamicAiView(
    viewModel: MoreViewModel,
    ttsManager: com.example.data.audio.ArabicTtsManager? = null,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var inputQuery by remember { mutableStateOf("") }
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    LaunchedEffect(uiState.chatMessages.size, uiState.chatMessages.lastOrNull()?.text?.length) {
        if (uiState.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.chatMessages.lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = uiText("ফিরে যান", "Back"),
                    tint = TextPrimary
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = uiText("নূর ইসলামিক এআই সহকারী", "Noor Islamic AI Assistant"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = ChampagneGold,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = uiText("কুরআন ও সুন্নাহর আলোকে প্রশ্নোত্তর", "Q&A Guided by Quran & Sunnah"),
                    style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 11.sp)
                )
            }

            Spacer(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Suggestion prompt chips
        Text(
            text = uiText("প্রস্তাবিত জিজ্ঞাসা:", "Suggested Prompts:"),
            style = MaterialTheme.typography.labelSmall.copy(color = ChampagneGold)
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.suggestedChips) { chip ->
                Box(
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkEmeraldElevated)
                        .border(0.5.dp, HairlineBorder, RoundedCornerShape(14.dp))
                        .clickable(enabled = !uiState.isAiThinking) { viewModel.sendAiPrompt(chip) }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .testTag("ai_suggested_chip"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = chip,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                    )
                }
            }
        }

        if (!uiState.aiError.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF4A1F24))
                    .border(0.5.dp, Color(0xFFFF8A80).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("ai_chat_error_banner"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = uiState.aiError.orEmpty(),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFFFFCDD2),
                        fontSize = 11.5.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = { viewModel.dismissAiError() },
                    modifier = Modifier.testTag("ai_chat_error_dismiss")
                ) {
                    Text(
                        text = uiText("বন্ধ করুন", "Close"),
                        color = ChampagneGold,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chat message list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(uiState.chatMessages, key = { it.id }) { msg ->
                val isUser = msg.sender == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 300.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isUser) ChampagneGold.copy(alpha = 0.2f) else DarkEmeraldSurface)
                            .border(
                                0.5.dp,
                                if (isUser) ChampagneGold.copy(alpha = 0.4f) else HairlineBorder,
                                RoundedCornerShape(16.dp)
                            )
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isUser) uiText("আপনি", "You") else uiText("নূর এআই সহকারী", "Noor AI Assistant"),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isUser) ChampagneGold else MintGlow,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                if (!isUser) {
                                    IconButton(
                                        onClick = {
                                            viewModel.updateCustomArabicText(msg.text)
                                            viewModel.playCustomArabicAudio(
                                                textOverride = msg.text,
                                                ttsManager = ttsManager
                                            )
                                        },
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = uiText("আরবি অডিও শুনুন", "Listen Arabic audio"),
                                            tint = ChampagneGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = msg.text,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextPrimary,
                                    lineHeight = 22.sp
                                )
                            )
                        }
                    }
                }
            }

            if (uiState.isAiThinking) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MintGlow,
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = uiText("নূর এআই উত্তর প্রস্তুত করছে...", "Noor AI is preparing a response..."),
                            style = MaterialTheme.typography.bodySmall.copy(color = MintGlow)
                        )
                    }
                }
            }
        }

        // Bottom Input Field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 80.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputQuery,
                onValueChange = { inputQuery = it },
                placeholder = {
                    Text(
                        uiText("ইসলামিক জিজ্ঞাসা লিখুন...", "Ask an Islamic question..."),
                        style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_assistant_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkEmeraldSurface,
                    unfocusedContainerColor = DarkEmeraldSurface,
                    focusedBorderColor = ChampagneGold,
                    unfocusedBorderColor = HairlineBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            IconButton(
                onClick = {
                    if (inputQuery.isNotBlank() && !uiState.isAiThinking) {
                        viewModel.sendAiPrompt(inputQuery.trim())
                        inputQuery = ""
                    }
                },
                enabled = inputQuery.isNotBlank() && !uiState.isAiThinking,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(ChampagneGold)
                    .testTag("ai_assistant_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = uiText("পাঠান", "Send"),
                    tint = ObsidianBackground,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 8. ARABIC HARAKAT & TRANSLITERATION STUDIO (TOOLS HUB)
// -----------------------------------------------------------------------------
data class HarakatSampleItem(
    val title: String,
    val arabicWithHarakat: String,
    val transliterationBengali: String,
    val transliterationEnglish: String,
    val meaningBengali: String,
    val audioUrl: String,
    val titleEnglish: String = "",
    val meaningEnglish: String = "",
    val playlistUrls: List<String> = emptyList(),
    val reciterLabelBn: String = "শেখ মিশারী আল-আফাসী (তাজবীদসহ)",
    val reciterLabelEn: String = "Sheikh Mishary Al-Afasy (Tajweed)"
) {
    val allAudioUrls: List<String>
        get() = if (playlistUrls.isNotEmpty()) playlistUrls else listOfNotNull(audioUrl.takeIf { it.isNotBlank() })

    fun localizedTitle(lang: String): String =
        LocalizationRegistry.translate(lang, title, titleEnglish.ifBlank { title })

    fun localizedReciter(lang: String): String =
        LocalizationRegistry.translate(lang, reciterLabelBn, reciterLabelEn)

    fun localizedTransliteration(contentLang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(contentLang) == "bn") {
            transliterationBengali
        } else {
            transliterationEnglish.ifBlank { transliterationBengali }
        }

    fun localizedMeaning(contentLang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(contentLang) == "bn") {
            meaningBengali
        } else {
            meaningEnglish.ifBlank { meaningBengali }
        }
}

val HARAKAT_SAMPLES = listOf(
    HarakatSampleItem(
        title = "আয়াতুল কুরসী (২:২৫৫)",
        arabicWithHarakat = "اللَّهُ لَا إِلَـٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
        transliterationBengali = "আল্লা-হু লা- ইলা-হা ইল্লা- হুওয়াল হাইয়্যুল ক্বাইয়্যূম। লা- তা'খুযুহূ সিনাতুঁও ওয়ালা- নাওম। লাহূ মা- ফিস সামা-ওয়া-তি ওয়ামা- ফিল আরদ্ব। মান যাল্লাযী ইয়্যাশফা‘উ ‘ইনদাহূ ইল্লা- বিইযনিহ। ইয়া‘লামু মা- বাইনা আইদীহিম ওয়ামা- খলফাহুম, ওয়ালা- ইউহীতূনা বিশাইয়িম মিন ‘ইলমিহী ইল্লা- বিমা- শা-আ। ওয়াসি‘আ কুরসিইয়্যুহুস সামা-ওয়া-তি ওয়াল আরদ্ব, ওয়ালা- ইয়াঊদুহূ হিফযুহুমা-, ওয়াহুওয়াল ‘আলিইয়্যুল ‘আযীম।",
        transliterationEnglish = "Allahu la ilaha illa Huwa, Al-Hayyul-Qayyum. La ta'khudhuhu sinatuw-wa la nawm. Lahu ma fis-samawati wa ma fil-ard. Man dhal-ladhee yashfa'u 'indahu illa bi-idhnih. Ya'lamu ma bayna aydeehim wa ma khalfahum, wa la yuheetoona bi-shay'im-min 'ilmihi illa bima sha'a. Wasi'a kursiyyuhus-samawati wal-ard, wa la ya'ooduhu hifzuhuma, wa Huwal-'Aliyyul-'Azeem.",
        meaningBengali = "আল্লাহ, তিনি ছাড়া কোনো সত্য উপাস্য নেই; তিনি চিরঞ্জীব, সর্বসত্তার ধারক। তন্দ্রা বা নিদ্রা তাঁকে স্পর্শ করে না। আসমান ও জমিনে যা কিছু আছে সব তাঁরই।",
        audioUrl = "https://everyayah.com/data/Alafasy_128kbps/002255.mp3",
        titleEnglish = "Ayat al-Kursi (2:255)",
        meaningEnglish = "Allah—there is no deity except Him, the Ever-Living, the Sustainer of existence. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and on the earth."
    ),
    HarakatSampleItem(
        title = "সাইয়্যিদুল ইস্তিগফার",
        arabicWithHarakat = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَـٰهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَىٰ عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
        transliterationBengali = "আল্লা-হুম্মা আনতা রব্বী লা- ইলা-হা ইল্লা- আনতা, খলাক্বতানী ওয়া আনা ‘আবদুকা, ওয়া আনা ‘আলা- ‘আহ্‌দিকা ওয়া ওয়া‘দিকা মাসতাত্বা‘তু, আ‘ঊযু বিকা মিন শাররি মা- সনা‘তু, আবূউ লাকা বিনি‘মাতিকা ‘আলাইয়্যা, ওয়া আবূউ বিযানবী ফাগফির লী ফা-ইন্নাহূ লা- ইয়্যাগফিরুয যুনূবা ইল্লা- আনতা।",
        transliterationEnglish = "Allahumma Anta Rabbi la ilaha illa Anta, khalaqtani wa ana 'abduka, wa ana 'ala 'ahdika wa wa'dika mastata'tu, a'udhu bika min sharri ma sana'tu, abu'u laka bini'matika 'alayya, wa abu'u bidhanbi faghfir li fa-innahu la yaghfirudh-dhunuba illa Anta.",
        meaningBengali = "হে আল্লাহ! আপনিই আমার প্রতিপালক। আপনি ছাড়া কোনো সত্য ইলাহ নেই। আপনি আমাকে সৃষ্টি করেছেন এবং আমি আপনার বান্দা।",
        audioUrl = "https://www.hisnmuslim.com/audio/ar/79.mp3",
        titleEnglish = "Sayyidul Istighfar",
        meaningEnglish = "O Allah, You are my Lord; there is none worthy of worship except You. You created me and I am Your servant.",
        reciterLabelBn = "হিসনুল মুসলিম ক্বারী (পুরুষ কণ্ঠ)",
        reciterLabelEn = "Hisnul Muslim Reciter (Male)"
    ),
    HarakatSampleItem(
        title = "সূরা আল-ইখলাস (১১২)",
        arabicWithHarakat = "قُلْ هُوَ اللَّهُ أَحَدٌ ۞ اللَّهُ الصَّمَدُ ۞ لَمْ يَلِدْ وَلَمْ يُولَدْ ۞ وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ",
        transliterationBengali = "ক্বুল হুওয়াল্লা-হু আহাদ। আল্লা-হুস সামাদ। লাম ইয়ালিদ ওয়ালাম ইউ-লাদ। ওয়ালাম ইয়াকুল্লাহূ কুফুওয়ান আহাদ।",
        transliterationEnglish = "Qul Huwallahu Ahad. Allahus-Samad. Lam yalid wa lam yoolad. Wa lam yakul-lahu kufuwan ahad.",
        meaningBengali = "বলুন, তিনিই আল্লাহ, একক অদ্বিতীয়। আল্লাহ কারো মুখাপেক্ষী নন। তিনি কাউকে জন্ম দেননি এবং তাঁকেও জন্ম দেওয়া হয়নি।",
        audioUrl = "https://everyayah.com/data/Alafasy_128kbps/112001.mp3",
        titleEnglish = "Surah Al-Ikhlas (112)",
        meaningEnglish = "Say, 'He is Allah, [who is] One, Allah, the Eternal Refuge. He neither begets nor is born, nor is there to Him any equivalent.'",
        playlistUrls = listOf(
            "https://everyayah.com/data/Alafasy_128kbps/112001.mp3",
            "https://everyayah.com/data/Alafasy_128kbps/112002.mp3",
            "https://everyayah.com/data/Alafasy_128kbps/112003.mp3",
            "https://everyayah.com/data/Alafasy_128kbps/112004.mp3"
        )
    ),
    HarakatSampleItem(
        title = "ইউনুস (আঃ)-এর দু'আ",
        arabicWithHarakat = "لَّا إِلَـٰهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ",
        transliterationBengali = "লা- ইলা-হা ইল্লা- আনতা সুবহা-নাকা ইন্নী কুনতু মিনায য-লিমীন।",
        transliterationEnglish = "La ilaha illa Anta subhanaka inni kuntu minaz-zalimeen.",
        meaningBengali = "আপনি ব্যতীত কোনো সত্য উপাস্য নেই; আপনি পরম পবিত্র! নিশ্চয়ই আমি অপরাধীদের অন্তর্ভুক্ত হয়ে গেছি।",
        audioUrl = "https://www.hisnmuslim.com/audio/ar/124.mp3",
        titleEnglish = "Du'a of Yunus (AS)",
        meaningEnglish = "There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers.",
        reciterLabelBn = "হিসনুল মুসলিম ক্বারী (পুরুষ কণ্ঠ)",
        reciterLabelEn = "Hisnul Muslim Reciter (Male)"
    ),
    HarakatSampleItem(
        title = "দরূদে ইব্রাহিম",
        arabicWithHarakat = "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَىٰ إِبْرَاهِيمَ وَعَلَىٰ آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ",
        transliterationBengali = "আল্লা-হুম্মা সল্লি ‘আলা- মুহাম্মাদিঁও ওয়া ‘আলা- আ-লি মুহাম্মাদ, কামা- সল্লাইতা ‘আলা- ইবরা-হীমা ওয়া ‘আলা- আ-লি ইবরা-হীম, ইন্নাকা হামীদুম মাজীদ।",
        transliterationEnglish = "Allahumma salli 'ala Muhammadin wa 'ala aali Muhammad, kama sallayta 'ala Ibraheema wa 'ala aali Ibraheem, innaka Hameedum Majeed.",
        meaningBengali = "হে আল্লাহ! মুহাম্মদ (সাঃ) ও তাঁর পরিবারের ওপর রহমত বর্ষণ করুন, যেমন আপনি ইব্রাহিম (আঃ) ও তাঁর পরিবারের ওপর রহমত বর্ষণ করেছিলেন।",
        audioUrl = "https://www.hisnmuslim.com/audio/ar/53.mp3",
        titleEnglish = "Durood Ibrahim",
        meaningEnglish = "O Allah, send blessings upon Muhammad and the family of Muhammad, as You sent blessings upon Ibrahim and the family of Ibrahim; indeed You are Praiseworthy and Glorious.",
        reciterLabelBn = "হিসনুল মুসলিম ক্বারী (পুরুষ কণ্ঠ)",
        reciterLabelEn = "Hisnul Muslim Reciter (Male)"
    ),
    HarakatSampleItem(
        title = "সুবহানাল্লাহি ওয়া বিহামদিহি",
        arabicWithHarakat = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ سُبْحَانَ اللَّهِ الْعَظِيمِ",
        transliterationBengali = "সুবহা-নাল্লা-হি ওয়া বিহামদিহী, সুবহা-নাল্লা-হিল ‘আযীম।",
        transliterationEnglish = "Subhanallahi wa bihamdihi, Subhanallahil-'Azeem.",
        meaningBengali = "আল্লাহর পবিত্রতা ঘোষণা করছি তাঁর প্রশংসার সাথে, মহান আল্লাহ পবিত্র।",
        audioUrl = "https://www.hisnmuslim.com/audio/ar/91.mp3",
        titleEnglish = "SubhanAllahi wa Bihamdihi",
        meaningEnglish = "Glory and praise be to Allah, Glory be to Allah the Supreme.",
        reciterLabelBn = "হিসনুল মুসলিম ক্বারী (পুরুষ কণ্ঠ)",
        reciterLabelEn = "Hisnul Muslim Reciter (Male)"
    ),
    HarakatSampleItem(
        title = "ইলম বৃদ্ধির দু'আ",
        arabicWithHarakat = "رَّبِّ زِدْنِي عِلْمًا",
        transliterationBengali = "রব্বি যিদনী ‘ইলমা-।",
        transliterationEnglish = "Rabbi zidnee 'ilma.",
        meaningBengali = "হে আমার প্রতিপালক! আমার জ্ঞান বৃদ্ধি করে দিন। (সূরা ত্বা-হা: ১১৪)",
        audioUrl = "https://everyayah.com/data/Alafasy_128kbps/020114.mp3",
        titleEnglish = "Du'a for Knowledge",
        meaningEnglish = "My Lord, increase me in knowledge. (Surah Ta-Ha: 114)"
    ),
    HarakatSampleItem(
        title = "লা হাওলা ওয়ালা কুওয়াতা",
        arabicWithHarakat = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ الْعَلِيِّ الْعَظِيمِ",
        transliterationBengali = "লা- হাওলা ওয়ালা- ক্বুওয়াতা ইল্লা- বিল্লা-হিল ‘আলিইয়্যিল ‘আযীম।",
        transliterationEnglish = "La hawla wa la quwwata illa billahil-'Aliyyil-'Azeem.",
        meaningBengali = "আল্লাহ ছাড়া অনিষ্ট দূর করার এবং কল্যাণ লাভ করার কোনো শক্তি কারো নেই।",
        audioUrl = "https://www.hisnmuslim.com/audio/ar/260.mp3",
        titleEnglish = "La Hawla wa La Quwwata",
        meaningEnglish = "There is no power nor strength except through Allah, the Most High, the Supreme.",
        reciterLabelBn = "হিসনুল মুসলিম ক্বারী (পুরুষ কণ্ঠ)",
        reciterLabelEn = "Hisnul Muslim Reciter (Male)"
    )
)

enum class ArabicAudioEngineMode {
    TTS_MALE,
    QARI_RECITER
}

@Composable
fun ArabicPronunciationTrainerStudioView(
    toolsViewModel: ToolsViewModel,
    audioPlayer: com.example.data.audio.AudioPlayerManager,
    onBack: () -> Unit
) {
    val uiLang = LocalAppLanguage.current
    val contentLang = LocalContentLanguage.current
    val ttsState by toolsViewModel.ttsState.collectAsState()
    val playbackState by audioPlayer.playbackState.collectAsState()

    var selectedIndex by remember { mutableStateOf(0) }
    var customArabicText by remember { mutableStateOf(HARAKAT_SAMPLES[0].arabicWithHarakat) }
    var customTransliteration by remember {
        mutableStateOf(
            HARAKAT_SAMPLES[0].localizedTransliteration(contentLang)
        )
    }
    var customMeaning by remember(contentLang, selectedIndex) {
        mutableStateOf(
            if (selectedIndex in HARAKAT_SAMPLES.indices) {
                HARAKAT_SAMPLES[selectedIndex].localizedMeaning(contentLang)
            } else ""
        )
    }
    var currentAudioUrls by remember { mutableStateOf(HARAKAT_SAMPLES[0].allAudioUrls) }
    var fontSizeSp by remember { mutableStateOf(24) }
    var speechRate by remember { mutableStateOf(0.85f) }
    var selectedMode by remember { mutableStateOf(ArabicAudioEngineMode.QARI_RECITER) }
    var validationError by remember { mutableStateOf<String?>(null) }
    var isCopied by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Surface audio player / TTS errors immediately so playback never fails silently
    LaunchedEffect(playbackState.error, ttsState.errorMessage) {
        val err = playbackState.error ?: if (selectedMode == ArabicAudioEngineMode.TTS_MALE) ttsState.errorMessage else null
        if (!err.isNullOrBlank()) {
            validationError = err
        }
    }

    LaunchedEffect(customArabicText, contentLang, selectedIndex) {
        val preset = HARAKAT_SAMPLES.getOrNull(selectedIndex)
        val trimmedInput = customArabicText.trim()
        customTransliteration = if (trimmedInput.isBlank()) {
            ""
        } else if (preset != null && trimmedInput == preset.arabicWithHarakat.trim()) {
            preset.localizedTransliteration(contentLang)
        } else {
            com.example.data.util.ArabicTransliterationHelper.transliterate(
                arabic = trimmedInput,
                langCode = contentLang
            )
        }
    }

    val isSpeakingOrPlaying = ttsState.isSpeaking || playbackState.isPlaying || playbackState.isBuffering

    val harakatButtons = remember(uiLang) {
        listOf(
            " َ " to localizedText(uiLang, "যবর", "Fatha"),
            " ِ " to localizedText(uiLang, "যের", "Kasra"),
            " ُ " to localizedText(uiLang, "পেশ", "Damma"),
            " ّ " to localizedText(uiLang, "তাশদীদ", "Shaddah"),
            " ْ " to localizedText(uiLang, "সুকুন", "Sukun"),
            " ً " to localizedText(uiLang, "দুই যবর", "Fathatan"),
            " ٍ " to localizedText(uiLang, "দুই যের", "Kasratan"),
            " ٌ " to localizedText(uiLang, "দুই পেশ", "Dammatan"),
            " ٰ " to localizedText(uiLang, "খাড়া যবর", "Alif Khanj."),
            " ۞ " to localizedText(uiLang, "আয়াত", "Ayah")
        )
    }

    fun handlePlayOrStop() {
        val trimmed = customArabicText.trim()
        if (trimmed.isEmpty()) {
            validationError = localizedText(
                uiLang,
                "অনুগ্রহ করে কোনো আরবি টেক্সট, আয়াত বা দু'আ লিখুন",
                "Please enter any Arabic text, Ayah, or Du'a"
            )
            return
        }
        validationError = null
        audioPlayer.clearError()

        if (isSpeakingOrPlaying) {
            toolsViewModel.stopTts()
            audioPlayer.stop()
            return
        }

        val activePreset = HARAKAT_SAMPLES.getOrNull(selectedIndex)
        val presetMatchesInput = activePreset != null && trimmed == activePreset.arabicWithHarakat.trim()
        val presetUrls = if (presetMatchesInput) currentAudioUrls else emptyList()

        if (selectedMode == ArabicAudioEngineMode.QARI_RECITER && presetUrls.isNotEmpty()) {
            toolsViewModel.stopTts()
            audioPlayer.setSpeed(speechRate)
            toolsViewModel.playTilawatSample(
                urls = presetUrls,
                title = activePreset?.localizedTitle(uiLang) ?: localizedText(uiLang, "সহীহ উচ্চারণ", "Authentic Pronunciation"),
                subtitle = activePreset?.localizedReciter(uiLang) ?: localizedText(uiLang, "শেখ মিশারী আল-আফাসী (তাজবীদসহ)", "Sheikh Mishary Al-Afasy (Tajweed)"),
                fallbackArabicText = trimmed
            )
        } else {
            audioPlayer.stop()
            val ok = toolsViewModel.speakArabic(trimmed, speed = speechRate)
            if (!ok) {
                validationError = toolsViewModel.ttsState.value.errorMessage ?: localizedText(
                    uiLang,
                    "এই ডিভাইসে আরবি টিটিএস প্যাক প্রস্তুত নয় — নিচ থেকে নির্বাচিত আয়াত ও দু'আ শুনুন অথবা TTS সেটিংস থেকে আরবি ভয়েস ইনস্টল করুন।",
                    "Arabic TTS voice pack is not installed — use Selected Ayah & Du'a mode or install Arabic voice in TTS Settings."
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                toolsViewModel.stopTts()
                audioPlayer.stop()
                onBack()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = uiText("ফিরে যান", "Back"),
                    tint = TextPrimary
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = uiText("আরবি উচ্চারণ ও তিলাওয়াত", "Arabic Pronunciation & Recitation"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = ChampagneGold,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = uiText("বিশুদ্ধ পাঠ ও সঠিক উচ্চারণ শিক্ষা", "Learn Authentic Articulation & Tajweed"),
                    style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 11.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Audio Engine & Speed Selection Bar
            item {
                FrostedGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = uiText("সুললিত তিলাওয়াত সুর", "Recitation Audio Engine"),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = ChampagneGold,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val studioReady = selectedMode == ArabicAudioEngineMode.QARI_RECITER || ttsState.isInitialized
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(if (studioReady) MintGlow else Color(0xFFFFB4BA))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when {
                                        selectedMode == ArabicAudioEngineMode.QARI_RECITER && currentAudioUrls.isNotEmpty() ->
                                            uiText("স্টুডিও ক্বারী অডিও প্রস্তুত (পুরুষ কণ্ঠ)", "Studio Male Qari Ready")
                                        ttsState.isInitialized ->
                                            uiText("সুললিত আরবি পুরুষ কণ্ঠ প্রস্তুত", "Arabic Male Voice Ready")
                                        ttsState.requiresLanguageData ->
                                            uiText("কাস্টম টেক্সটের জন্য আরবি ভয়েস প্যাক লাগবে", "Install Arabic voice for custom text")
                                        else ->
                                            uiText("অডিও প্রস্তুত হচ্ছে...", "Initializing audio...")
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (studioReady) MintGlow else SlateGrayMuted,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // Speech Speed Selectors (0.75x, 0.85x, 1.0x)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(0.75f to "0.75x", 0.85f to "0.85x", 1.0f to "1.0x").forEach { (speed, label) ->
                                val isSelected = speechRate == speed
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) ChampagneGold else DarkEmeraldElevated)
                                        .clickable {
                                            speechRate = speed
                                            toolsViewModel.setTtsSpeechRate(speed)
                                            audioPlayer.setSpeed(speed)
                                        }
                                        .padding(horizontal = 8.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) ObsidianBackground else SlateGrayMuted,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Soulful Voice Tone Selector Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkEmeraldElevated)
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uiText("সুর ও গাম্ভীর্য:", "Voice Tone:"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ChampagneGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            ArabicVoiceTone.values().forEach { tone ->
                                val isSelected = ttsState.selectedTone == tone
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) MintGlow.copy(alpha = 0.2f) else DarkEmeraldSurface)
                                        .border(0.5.dp, if (isSelected) MintGlow else HairlineBorder, RoundedCornerShape(6.dp))
                                        .clickable {
                                            toolsViewModel.setTtsVoiceTone(tone)
                                            speechRate = tone.speechRate
                                        }
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = when (tone) {
                                            ArabicVoiceTone.SOULFUL_MELODIOUS -> uiText("সুললিত", "Melodious")
                                            ArabicVoiceTone.DEEP_REVERENT -> uiText("গম্ভীর", "Reverent")
                                            ArabicVoiceTone.CLEAR_TAJWEED -> uiText("তালিম", "Tajweed")
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) MintGlow else SlateGrayMuted,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mode Switcher: TTS vs Studio Qari
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkEmeraldElevated)
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedMode == ArabicAudioEngineMode.TTS_MALE) MintGlow else Color.Transparent)
                                .clickable { selectedMode = ArabicAudioEngineMode.TTS_MALE }
                                .padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiText("যেকোনো আরবি পাঠ", "Any Arabic Text"),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (selectedMode == ArabicAudioEngineMode.TTS_MALE) ObsidianBackground else TextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedMode == ArabicAudioEngineMode.QARI_RECITER) ChampagneGold else Color.Transparent)
                                .clickable { selectedMode = ArabicAudioEngineMode.QARI_RECITER }
                                .padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiText("নির্বাচিত আয়াত ও দু'আ", "Selected Ayah & Du'a"),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (selectedMode == ArabicAudioEngineMode.QARI_RECITER) ObsidianBackground else TextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }

            // Notice if Language Pack is required
            if (ttsState.requiresLanguageData) {
                item {
                    FrostedGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = MakruhBorder
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Filled.Info, contentDescription = null, tint = MakruhBorder)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = uiText("আরবি ভয়েস ডেটা প্রয়োজন", "Arabic Voice Data Required"),
                                    style = MaterialTheme.typography.titleSmall.copy(color = MakruhBorder, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = uiText(
                                        "সরাসরি স্পষ্ট উচ্চারণের জন্য অ্যান্ড্রয়েড টিটিএস সেটিংসে Arabic ভয়েস ইনস্টল থাকতে পারে।",
                                        "For clear offline articulation, Arabic voice data may need to be enabled in Android TTS Settings."
                                    ),
                                    style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 11.sp)
                                )
                            }
                            Button(
                                onClick = {
                                    try {
                                        context.startActivity(Intent("com.android.settings.TTS_SETTINGS"))
                                    } catch (_: Exception) {
                                        try {
                                            context.startActivity(Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA))
                                        } catch (_: Exception) {}
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MakruhBackground,
                                    contentColor = MakruhBorder
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(uiText("TTS সেটিংস", "TTS Settings"), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Quick Preset Selection Chips
            item {
                Text(
                    text = uiText(
                        "প্রস্তাবিত প্রসিদ্ধ আয়াত ও দো'আসমূহ (ট্যাপ করে লোড করুন):",
                        "Featured Verses & Du'as (Tap to load):"
                    ),
                    style = MaterialTheme.typography.labelSmall.copy(color = ChampagneGold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(HARAKAT_SAMPLES.indices.toList()) { idx ->
                        val item = HARAKAT_SAMPLES[idx]
                        val isSelected = selectedIndex == idx
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) DarkEmeraldCard else DarkEmeraldSurface)
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.5.dp,
                                    color = if (isSelected) ChampagneGold else HairlineBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedIndex = idx
                                    customArabicText = item.arabicWithHarakat
                                    customTransliteration = item.localizedTransliteration(contentLang)
                                    customMeaning = item.localizedMeaning(contentLang)
                                    currentAudioUrls = item.allAudioUrls
                                    selectedMode = ArabicAudioEngineMode.QARI_RECITER
                                    validationError = null
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = item.localizedTitle(uiLang),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isSelected) ChampagneGold else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }

            // CUSTOM ARABIC TEXT INPUT BOX (Core User Workflow Feature)
            item {
                FrostedGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiText("আরবি টেক্সট পেস্ট বা টাইপ করুন:", "Paste or type Arabic text:"),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MintGlow,
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Paste from clipboard button
                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                    val clipData = clipboard?.primaryClip
                                    if (clipData != null && clipData.itemCount > 0) {
                                        val pasted = clipData.getItemAt(0).text?.toString() ?: ""
                                        if (pasted.isNotBlank()) {
                                            selectedIndex = -1
                                            customArabicText = pasted
                                            customMeaning = ""
                                            currentAudioUrls = emptyList()
                                            selectedMode = ArabicAudioEngineMode.TTS_MALE
                                            validationError = null
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DarkEmeraldElevated,
                                    contentColor = ChampagneGold
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Filled.ContentPaste, contentDescription = uiText("পেস্ট", "Paste"), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(uiText("পেস্ট করুন", "Paste"), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }

                            // Clear text button
                            if (customArabicText.isNotBlank()) {
                                Button(
                                    onClick = {
                                        selectedIndex = -1
                                        customArabicText = ""
                                        customTransliteration = ""
                                        customMeaning = ""
                                        currentAudioUrls = emptyList()
                                        validationError = null
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF2C1114),
                                        contentColor = Color(0xFFFFB4BA)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(Icons.Filled.Clear, contentDescription = uiText("মুছুন", "Clear"), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(uiText("মুছুন", "Clear"), fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customArabicText,
                        onValueChange = {
                            if (selectedIndex in HARAKAT_SAMPLES.indices && it.trim() != HARAKAT_SAMPLES[selectedIndex].arabicWithHarakat.trim()) {
                                selectedIndex = -1
                                customMeaning = ""
                                currentAudioUrls = emptyList()
                                selectedMode = ArabicAudioEngineMode.TTS_MALE
                            }
                            customArabicText = it
                            if (it.isNotBlank()) validationError = null
                        },
                        placeholder = {
                            Text(
                                text = uiText(
                                    "এখানে আরবি আয়াত, দু'আ বা শব্দ লিখুন বা পেস্ট করুন...",
                                    "Type or paste Arabic Ayah, Du'a, or words here..."
                                ),
                                color = SlateGrayMuted,
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("arabic_tts_input_field"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkEmeraldSurface,
                            unfocusedContainerColor = DarkEmeraldSurface,
                            focusedBorderColor = ChampagneGold,
                            unfocusedBorderColor = HairlineBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        textStyle = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = AmiriFontFamily,
                            fontSize = 20.sp,
                            lineHeight = 32.sp,
                            textAlign = TextAlign.Right
                        ),
                        minLines = 3
                    )

                    // Inline validation error notification
                    AnimatedVisibility(visible = validationError != null) {
                        Text(
                            text = validationError ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFFFB4BA),
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }

            // Quick Harakat Diacritics Insertion Toolbar
            item {
                FrostedGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = uiText("হরকত টুলবার (ট্যাপ করে আরবিতে যুক্ত করুন):", "Harakat Toolbar (Tap to append to Arabic):"),
                        style = MaterialTheme.typography.labelSmall.copy(color = MintGlow)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(harakatButtons) { (mark, label) ->
                            Button(
                                onClick = {
                                    selectedIndex = -1
                                    customArabicText += mark.trim()
                                    validationError = null
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DarkEmeraldElevated,
                                    contentColor = ChampagneGold
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text(
                                    text = "$mark ($label)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // High-Resolution Amiri Display & Articulation Card
            item {
                FrostedGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = HairlineGold
                ) {
                    // Header of the card with font scaling and copy button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = uiText("ফন্ট সাইজ:", "Font Size:"),
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateGrayMuted)
                            )
                            IconButton(onClick = { if (fontSizeSp > 18) fontSizeSp -= 2 }, modifier = Modifier.size(32.dp)) {
                                Text("A-", style = MaterialTheme.typography.labelSmall.copy(color = SlateGrayMuted))
                            }
                            IconButton(onClick = { if (fontSizeSp < 36) fontSizeSp += 2 }, modifier = Modifier.size(32.dp)) {
                                Text("A+", style = MaterialTheme.typography.labelSmall.copy(color = ChampagneGold))
                            }
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText("Arabic Harakat Text", customArabicText)
                                clipboard.setPrimaryClip(clip)
                                isCopied = true
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isCopied) Icons.Filled.Check else Icons.Filled.ContentCopy,
                                contentDescription = uiText("কপি করুন", "Copy text"),
                                tint = if (isCopied) MintGlow else ChampagneGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Complete Arabic Text with Harakat (Amiri Font)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkEmeraldElevated)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = if (customArabicText.isNotBlank()) customArabicText else uiText("কোনো আরবি টেক্সট নেই", "No Arabic text entered"),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = AmiriFontFamily,
                                fontSize = fontSizeSp.sp,
                                lineHeight = (fontSizeSp * 1.6).sp,
                                textAlign = TextAlign.Right,
                                color = if (customArabicText.isNotBlank()) TextPrimary else SlateGrayMuted
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (customTransliteration.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = uiText("উচ্চারণ:", "Transliteration:"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MintGlow,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = customTransliteration,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                color = TextPrimary
                            )
                        )
                    }

                    if (customMeaning.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiText("অনুবাদ / অর্থ:", "Translation / Meaning:"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ChampagneGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = customMeaning,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = SlateGrayMuted,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }

            // PROMINENT SAHIH PLAY PRONUNCIATION BUTTON
            item {
                FrostedGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { handlePlayOrStop() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSpeakingOrPlaying) MintGlow else ChampagneGold,
                                contentColor = ObsidianBackground
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("play_pronunciation_button")
                        ) {
                            if (isSpeakingOrPlaying) {
                                Icon(
                                    imageVector = Icons.Filled.Stop,
                                    contentDescription = uiText("থামান", "Stop"),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = uiText("উচ্চারণ থামান", "Stop Pronunciation"),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = uiText("উচ্চারণ শুনুন", "Play Pronunciation"),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = uiText("সহীহ উচ্চারণ শুনুন", "Play Pronunciation"),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Male voice policy reassurance
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Verified,
                                contentDescription = null,
                                tint = MintGlow,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = uiText("১০০% পুরুষ কণ্ঠ নিশ্চিত", "100% Male Pronunciation Profile"),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SlateGrayMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}



