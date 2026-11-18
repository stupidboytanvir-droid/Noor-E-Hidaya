package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.DailyHadith
import com.example.data.model.District
import com.example.data.model.DistrictDirectory
import com.example.data.model.HadithRepository
import com.example.data.model.PrayerName
import com.example.data.model.PrayerSchedule
import com.example.data.model.PrayerTimeItem
import com.example.data.util.TimeFormatUtils
import com.example.ui.components.DynamicAmalHubCard
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.IslamicNasihatPlayCard
import com.example.ui.components.LocalTimePickerDialog
import com.example.ui.components.MintPillBadge
import com.example.ui.components.SectionHeader
import com.example.ui.components.SpiritualHabitInsightsCard
import com.example.ui.components.ThreeDimensionalIcon
import com.example.ui.components.Icon3DType
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val schedule by mainViewModel.schedule.collectAsState()
    val mosqueConfigs by mainViewModel.mosqueConfigs.collectAsState()
    val isDetectingLocation by mainViewModel.isDetectingLocation.collectAsState()
    val isGpsLocation by mainViewModel.isGpsLocation.collectAsState()
    val selectedHadith by mainViewModel.selectedHadith.collectAsState()
    val streakDays by mainViewModel.prayerStreak.collectAsState()
    val quranMinutesToday by mainViewModel.quranReadMinutesToday.collectAsState()
    val salawatCount by mainViewModel.salawatJumatCount.collectAsState()
    val duasReadCount by mainViewModel.duasReadCount.collectAsState()
    val totalTasbihCount by mainViewModel.totalTasbihCount.collectAsState()
    val hadithReadMinutes by mainViewModel.hadithReadMinutes.collectAsState()
    val todayCompletedPrayers by mainViewModel.todayCompletedPrayersCount.collectAsState()
    val monthlyHistory by mainViewModel.monthlyWorshipHistory.collectAsState()
    val dailyNasihahState by mainViewModel.dailyNasihahState.collectAsState()
    val todayHadith = remember { HadithRepository.getTodayHadith() }
    val audioPlayer = remember(context) { com.example.data.audio.AudioPlayerManager.getInstance(context) }

    var showLocationDialog by remember { mutableStateOf(false) }
    var showWorshipAnalyticsSheet by remember { mutableStateOf(false) }
    var editingPrayerItem by remember { mutableStateOf<PrayerTimeItem?>(null) }

    LaunchedEffect(Unit) {
        mainViewModel.ensureDailyNasihahLoaded()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 18.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Integrated Glassmorphic Header (Greeting, City Pin & Dual Calendar Ribbon)
            item {
                IntegratedHomeHeader(
                    schedule = schedule,
                    isGpsLocation = isGpsLocation,
                    onLocationClick = { showLocationDialog = true },
                    onNavigateToTools = { mainViewModel.selectTab(AppTab.TOOLS) }
                )
            }

            // 2. Refined & Centered Hero Countdown Arc Card with Abundant Breathing Room
            item {
                schedule?.let { s ->
                    HeroCountdownCard(schedule = s)
                }
            }

            // 3. Expanded Daily Prayer Schedule & Time Ranges Section (Clean Spacious Cards)
            item {
                schedule?.let { s ->
                    DailyPrayerScheduleSection(
                        schedule = s,
                        onEditPrayer = { item ->
                            if (item.prayer != PrayerName.SUNRISE) {
                                editingPrayerItem = item
                            }
                        }
                    )
                }
            }

            // 4. Sahur & Iftar Live Tracker Bar (Spacious Split Gradient)
            item {
                schedule?.let { s ->
                    SahurIftarBar(schedule = s)
                }
            }

            // 5. Dynamic Hijri & Friday-Aware Amal Hub (Auto Jumat on Fridays, Daily Amals on Other Days)
            item {
                DynamicAmalHubCard(
                    salawatCount = salawatCount,
                    onIncrementSalawat = { mainViewModel.incrementSalawatJumat() },
                    onOpenSurahKahf = { mainViewModel.navigateToSurah(18) }
                )
            }

            // 6. Dynamic AI-Powered Believer's Nasihah Card (Strictly Language-Aware via Gemini)
            item {
                IslamicNasihatPlayCard(
                    uiState = dailyNasihahState,
                    onRefresh = { mainViewModel.refreshDailyNasihah(forceRefresh = true) }
                )
            }

            // 7. Restored Daily Hadith Segment (Multilingual Arabic + Phonetic Transliteration + Audio + Explanation)
            item {
                DailyHadithHomeCard(
                    hadith = todayHadith,
                    audioPlayer = audioPlayer,
                    onOpenDetails = {
                        mainViewModel.recordHadithRead(1)
                        mainViewModel.showHadithDetails(todayHadith)
                    }
                )
            }

            // 8. Spiritual Habit Streaks & Insights (Real Zero-Initialized Counters + 12-Month Analytics)
            item {
                SpiritualHabitInsightsCard(
                    streakDays = streakDays,
                    quranMinutesToday = quranMinutesToday,
                    completedPrayersToday = todayCompletedPrayers,
                    duasReadCount = duasReadCount,
                    totalTasbihCount = totalTasbihCount,
                    salawatTotal = salawatCount,
                    hadithMinutes = hadithReadMinutes,
                    monthlyHistory = monthlyHistory,
                    onOpenMonthlyAnalytics = { showWorshipAnalyticsSheet = true }
                )
            }

            // 8. Collapsible / Tabbed Auxiliary Times (Nafl Prayers & Makruh Warning Times)
            item {
                schedule?.let { s ->
                    AuxiliaryPrayerTimesCard(schedule = s)
                }
            }
        }

        // Location Auto-Detect & District Picker Dialog
        if (showLocationDialog) {
            HomeLocationSelectorDialog(
                currentLocationLabel = schedule?.locationName ?: uiText("ঢাকা, বাংলাদেশ", "Dhaka, Bangladesh"),
                isGpsLocation = isGpsLocation,
                isDetecting = isDetectingLocation,
                onAutoDetect = {
                    mainViewModel.autoDetectLocationIfPermitted(force = true) { ok, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        if (ok) showLocationDialog = false
                    }
                },
                onSelectDistrict = { district ->
                    val uiLang = mainViewModel.uiLanguage.value
                    mainViewModel.selectDistrictFromHome(district)
                    val isBn = com.example.data.util.SupportedLanguage.normalizeCode(uiLang) == "bn"
                    val distLabel = if (isBn) district.nameBengali else district.nameEnglish
                    val msg = localizedText(uiLang, "অবস্থান সেট হয়েছে: $distLabel", "Location set: $distLabel")
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    showLocationDialog = false
                },
                onDismiss = { showLocationDialog = false }
            )
        }

        // Custom Mosque Waqt & Jamat Time Editor Dialog
        editingPrayerItem?.let { item ->
            val cfg = mosqueConfigs[item.prayer]
            val uiLang = LocalAppLanguage.current
            val prayerDisplay = item.prayer.localizedName(uiLang)
            val savedToast = uiText("$prayerDisplay-এর মসজিদ সময় সংরক্ষিত হয়েছে", "$prayerDisplay mosque time saved")
            val resetToast = uiText("$prayerDisplay-এর স্বাভাবিক সময় ফিরিয়ে আনা হয়েছে", "$prayerDisplay restored to default")
            CustomPrayerTimingDialog(
                item = item,
                customWaqt24 = cfg?.customPrayerTime,
                customJamat24 = cfg?.jamatTime,
                onSave = { newWaqt24, newJamat24 ->
                    mainViewModel.updatePrayerAndJamatTime(item.prayer, newWaqt24, newJamat24)
                    Toast.makeText(context, savedToast, Toast.LENGTH_SHORT).show()
                    editingPrayerItem = null
                },
                onReset = {
                    mainViewModel.resetPrayerCustomTime(item.prayer)
                    Toast.makeText(context, resetToast, Toast.LENGTH_SHORT).show()
                    editingPrayerItem = null
                },
                onDismiss = { editingPrayerItem = null }
            )
        }

        // 12-Month Worship Analytics & History Sheet
        if (showWorshipAnalyticsSheet) {
            com.example.ui.components.WorshipHistoryAnalyticsBottomSheet(
                monthlyHistory = monthlyHistory,
                currentQuranMinutes = quranMinutesToday,
                currentDuaCount = duasReadCount,
                currentTasbihCount = totalTasbihCount,
                currentDuroodCount = salawatCount,
                currentHadithMinutes = hadithReadMinutes,
                onDismiss = { showWorshipAnalyticsSheet = false }
            )
        }

        // Hadith Architectural Modal Bottom Sheet
        if (selectedHadith != null) {
            ModalBottomSheet(
                onDismissRequest = { mainViewModel.closeHadithDetails() },
                containerColor = DarkEmeraldSurface,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .width(36.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(SlateGrayMuted.copy(alpha = 0.5f))
                    )
                }
            ) {
                HadithDetailSheetContent(
                    hadith = selectedHadith!!,
                    onClose = { mainViewModel.closeHadithDetails() }
                )
            }
        }
    }
}

/**
 * 1. Compact Integrated Glassmorphic Header Card
 */
@Composable
fun IntegratedHomeHeader(
    schedule: PrayerSchedule?,
    isGpsLocation: Boolean = false,
    onLocationClick: () -> Unit = {},
    onNavigateToTools: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentHour = remember { java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) }
    val timeGreeting = when (currentHour) {
        in 4..11 -> uiText("শুভ সকাল", "Good Morning")
        in 12..15 -> uiText("শুভ দুপুর", "Good Afternoon")
        in 16..18 -> uiText("শুভ বিকেল", "Good Evening")
        in 19..22 -> uiText("শুভ সন্ধ্যা", "Good Night")
        else -> uiText("শুভ রাত্রি", "Peaceful Night")
    }

    FrostedGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("integrated_home_header"),
        backgroundColor = DarkEmeraldSurface.copy(alpha = 0.85f),
        borderColor = HairlineGold
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Primary Row: Mosque Emblem + Greeting + Location Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 36dp Golden Mosque Medallion Icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(DarkEmeraldElevated, DarkEmeraldSurface)
                            )
                        )
                        .border(1.2.dp, ChampagneGold.copy(alpha = 0.75f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    ThreeDimensionalIcon(
                        type = Icon3DType.MOSQUE_HOME,
                        size = 18.dp,
                        isActive = true,
                        accentColor = ChampagneGold
                    )
                }

                // Greeting & Subtitle Container (Takes remaining space with weight 1f)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = uiText("আসসালামু আলাইকুম", "Assalamu Alaikum"),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MintGlow.copy(alpha = 0.15f))
                                .border(0.5.dp, MintGlow.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = timeGreeting,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MintGlow,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                maxLines = 1
                            )
                        }
                    }

                    Text(
                        text = uiText("ঈমান ও আমলে বরকতময় হোক আপনার দিন", "May your day be blessed with Iman & Ibadah"),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateGrayMuted,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Interactive Location Pill (Tap to GPS Auto-Detect or Switch District)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onLocationClick)
                        .background(DarkEmeraldElevated.copy(alpha = 0.8f))
                        .border(
                            0.6.dp,
                            if (isGpsLocation) MintGlow.copy(alpha = 0.5f) else HairlineBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("home_location_pill"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isGpsLocation) Icons.Filled.MyLocation else Icons.Filled.LocationOn,
                        contentDescription = uiText("অবস্থান পরিবর্তন", "Change Location"),
                        tint = if (isGpsLocation) MintGlow else ChampagneGold,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = schedule?.locationName ?: uiText("ঢাকা, বাংলাদেশ", "Dhaka, Bangladesh"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Bottom Ribbon: Sleek Full-Width Dual Calendar Badge (Hijri & Gregorian)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onNavigateToTools)
                    .background(DarkEmeraldElevated.copy(alpha = 0.5f))
                    .border(0.5.dp, HairlineBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = schedule?.hijriDate ?: uiText("১৪৪৭ হিজরি", "1447 AH"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SlateGrayMuted.copy(alpha = 0.6f),
                            fontSize = 9.sp
                        )
                    )
                    Text(
                        text = schedule?.gregorianDate ?: "Today",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SlateGrayMuted,
                            fontSize = 10.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = uiText("হিজরি ক্যালেন্ডার", "Hijri Calendar"),
                        tint = SlateGrayMuted.copy(alpha = 0.7f),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

/**
 * Dynamic Celestial Day/Night Phase Enum
 */
enum class TimeCelestialPhase(val labelBengali: String, val labelEnglish: String) {
    DAWN_FAJR("ভোর / সুবহে সাদিক", "Dawn / Fajr"),
    DAY_SUNNY("উজ্জ্বল দিন / দ্বিপ্রহর", "Day / Dhuhr & Asr"),
    SUNSET_TWILIGHT("সূর্যাস্ত / মাগরিব", "Sunset / Maghrib"),
    NIGHT_CELESTIAL("রজনী / ইশা ও তাহাজ্জুদ", "Night / Isha & Tahajjud")
}

fun getCelestialPhase(currentWaqt: PrayerName): TimeCelestialPhase {
    val cal = java.util.Calendar.getInstance()
    val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
    return when (currentWaqt) {
        PrayerName.FAJR, PrayerName.SUNRISE -> TimeCelestialPhase.DAWN_FAJR
        PrayerName.DHUHR, PrayerName.ASR -> TimeCelestialPhase.DAY_SUNNY
        PrayerName.MAGHRIB -> TimeCelestialPhase.SUNSET_TWILIGHT
        PrayerName.ISHA -> TimeCelestialPhase.NIGHT_CELESTIAL
        else -> when (hour) {
            in 4..6 -> TimeCelestialPhase.DAWN_FAJR
            in 7..16 -> TimeCelestialPhase.DAY_SUNNY
            in 17..18 -> TimeCelestialPhase.SUNSET_TWILIGHT
            else -> TimeCelestialPhase.NIGHT_CELESTIAL
        }
    }
}

/**
 * Lightweight Dynamic Celestial Canvas Background
 */
@Composable
fun CelestialHeroBackground(
    phase: TimeCelestialPhase,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "celestial_anim")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )
    val rayAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rayAngle"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val stars = remember {
        listOf(
            0.10f to 0.22f,
            0.22f to 0.15f,
            0.38f to 0.32f,
            0.52f to 0.18f,
            0.66f to 0.28f,
            0.80f to 0.14f,
            0.92f to 0.30f,
            0.15f to 0.68f,
            0.30f to 0.78f,
            0.48f to 0.62f,
            0.70f to 0.74f,
            0.85f to 0.65f,
            0.95f to 0.80f
        )
    }

    Canvas(modifier = modifier) {
        when (phase) {
            TimeCelestialPhase.NIGHT_CELESTIAL -> {
                // Obsidian Midnight to Deep Teal Gradient
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF03080A), Color(0xFF071415), Color(0xFF0B1F1E))
                    )
                )

                // Twinkling Celestial Stars
                stars.forEachIndexed { index, (sx, sy) ->
                    val starAlpha = (shimmer * (0.4f + (index % 4) * 0.18f)).coerceIn(0.15f, 0.95f)
                    drawCircle(
                        color = Color.White.copy(alpha = starAlpha),
                        radius = (1.2f + (index % 3) * 0.7f).dp.toPx(),
                        center = Offset(size.width * sx, size.height * sy)
                    )
                }

                // Glowing Crescent Moon
                val moonCenter = Offset(size.width * 0.82f, size.height * 0.34f)
                val moonRadius = 14.dp.toPx()

                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(ChampagneGold.copy(alpha = 0.22f * shimmer), Color.Transparent),
                        center = moonCenter,
                        radius = moonRadius * 2.5f
                    ),
                    radius = moonRadius * 2.5f,
                    center = moonCenter
                )

                val moonOuter = Path().apply {
                    addOval(Rect(center = moonCenter, radius = moonRadius))
                }
                val moonInner = Path().apply {
                    addOval(Rect(center = moonCenter.copy(x = moonCenter.x - 5.dp.toPx(), y = moonCenter.y - 4.dp.toPx()), radius = moonRadius * 0.92f))
                }
                val crescentPath = Path().apply {
                    op(moonOuter, moonInner, PathOperation.Difference)
                }
                drawPath(crescentPath, color = ChampagneGold.copy(alpha = 0.88f))
            }
            TimeCelestialPhase.DAWN_FAJR -> {
                // Deep Indigo to Dawn Rose / Amber Gradient
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF0B0E1F), Color(0xFF1B1832), Color(0xFF3B2236), Color(0xFF582A30))
                    )
                )

                val dawnSunCenter = Offset(size.width * 0.84f, size.height * 0.82f)
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color(0xFFFFA258).copy(alpha = 0.45f * pulse), Color(0xFFFF5252).copy(alpha = 0.15f), Color.Transparent),
                        center = dawnSunCenter,
                        radius = 55.dp.toPx() * pulse
                    ),
                    radius = 55.dp.toPx() * pulse,
                    center = dawnSunCenter
                )
                drawCircle(
                    color = Color(0xFFFFDE8A).copy(alpha = 0.85f),
                    radius = 13.dp.toPx(),
                    center = dawnSunCenter
                )
                drawLine(
                    color = Color(0xFFFFB273).copy(alpha = 0.35f),
                    start = Offset(0f, size.height * 0.88f),
                    end = Offset(size.width, size.height * 0.88f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            TimeCelestialPhase.DAY_SUNNY -> {
                // Vibrant Daylight Emerald-Teal Radiance
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF071F1E), Color(0xFF0B2E2A), Color(0xFF0E3E37))
                    )
                )

                val sunCenter = Offset(size.width * 0.82f, size.height * 0.35f)
                val sunRadius = 14.dp.toPx()

                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color(0xFFFFDF79).copy(alpha = 0.25f * pulse), Color.Transparent),
                        center = sunCenter,
                        radius = sunRadius * 3.5f
                    ),
                    radius = sunRadius * 3.5f,
                    center = sunCenter
                )
                drawCircle(
                    color = Color(0xFFFFDE7A).copy(alpha = 0.9f),
                    radius = sunRadius,
                    center = sunCenter
                )

                rotate(degrees = rayAngle, pivot = sunCenter) {
                    for (i in 0 until 8) {
                        val angleRad = (i * 45f) * (Math.PI / 180.0).toFloat()
                        val rStart = sunRadius + 4.dp.toPx()
                        val rEnd = sunRadius + 12.dp.toPx()
                        val start = Offset(sunCenter.x + kotlin.math.cos(angleRad) * rStart, sunCenter.y + kotlin.math.sin(angleRad) * rStart)
                        val end = Offset(sunCenter.x + kotlin.math.cos(angleRad) * rEnd, sunCenter.y + kotlin.math.sin(angleRad) * rEnd)
                        drawLine(
                            color = Color(0xFFFFDF79).copy(alpha = 0.55f),
                            start = start,
                            end = end,
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
            TimeCelestialPhase.SUNSET_TWILIGHT -> {
                // Sunset Twilight Violet to Crimson / Amber
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF140D22), Color(0xFF2C132E), Color(0xFF531A2A), Color(0xFF732722))
                    )
                )

                val sunsetCenter = Offset(size.width * 0.83f, size.height * 0.78f)
                val sunRadius = 15.dp.toPx()
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color(0xFFFF7043).copy(alpha = 0.5f * pulse), Color(0xFFD32F2F).copy(alpha = 0.2f), Color.Transparent),
                        center = sunsetCenter,
                        radius = sunRadius * 3.2f
                    ),
                    radius = sunRadius * 3.2f,
                    center = sunsetCenter
                )
                drawCircle(
                    color = Color(0xFFFFB74D).copy(alpha = 0.9f),
                    radius = sunRadius,
                    center = sunsetCenter
                )
                drawLine(
                    color = Color(0xFFFF8A65).copy(alpha = 0.4f),
                    start = Offset(0f, size.height * 0.85f),
                    end = Offset(size.width, size.height * 0.85f),
                    strokeWidth = 1.5.dp.toPx()
                )
            }
        }

        // Lightweight dark scrim to ensure 100% foreground contrast
        drawRect(color = Color.Black.copy(alpha = 0.22f))
    }
}

/**
 * 2. Refined & Centered Hero Countdown Arc Card with Abundant Breathing Room & Dynamic Celestial Sky
 */
@Composable
fun HeroCountdownCard(schedule: PrayerSchedule) {
    val phase = remember(schedule.currentWaqt) { getCelestialPhase(schedule.currentWaqt) }

    val uiLang = LocalAppLanguage.current
    val currentWaqtName = schedule.currentWaqt.localizedName(uiLang)
    val nextWaqtName = schedule.nextWaqt.localizedName(uiLang)

    FrostedGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_countdown_card"),
        cornerRadius = 22.dp,
        contentPadding = 20.dp,
        backgroundColor = DarkEmeraldSurface.copy(alpha = 0.8f),
        borderColor = HairlineGold
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // 1. Dynamic Time-Aware Celestial Canvas Background
            CelestialHeroBackground(
                phase = phase,
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(22.dp))
            )

            // 2. Foreground Card Content with Generous Spacing & Centered Balance
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Ribbon: Current Waqt Pill + Celestial Phase Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Active Waqt Pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkEmeraldElevated.copy(alpha = 0.85f))
                            .border(0.8.dp, MintGlow.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(MintGlow)
                        )
                        Text(
                            text = uiText("বর্তমান ওয়াক্ত: $currentWaqtName", "Current Waqt: $currentWaqtName"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    // Celestial Phase Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkEmeraldElevated.copy(alpha = 0.75f))
                            .border(0.6.dp, ChampagneGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = localizedText(uiLang, phase.labelBengali, phase.labelEnglish),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ChampagneGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                // Centered Hero Section: Circular Progress Arc HUD & Countdown
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Circular Progress HUD (104dp)
                    Box(
                        modifier = Modifier
                            .size(104.dp)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Background track
                            drawCircle(
                                color = Color.White.copy(alpha = 0.08f),
                                style = Stroke(width = 3.dp.toPx())
                            )
                            // Progress arc (Champagne Gold)
                            val sweep = schedule.progressPercent * 360f
                            drawArc(
                                color = ChampagneGold,
                                startAngle = -90f,
                                sweepAngle = sweep,
                                useCenter = false,
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${(schedule.progressPercent * 100).toInt()}%",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 18.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = uiText("ওয়াক্ত শেষ", "Waqt Elapsed"),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = SlateGrayMuted
                                )
                            )
                        }
                    }

                    // Centered countdown timer & next waqt label
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = uiText("$nextWaqtName ওয়াক্ত শুরু হতে বাকি", "Time remaining until $nextWaqtName"),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = SlateGrayMuted,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )

                        Text(
                            text = schedule.remainingFormatted,
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Light,
                                color = ChampagneGold,
                                letterSpacing = 2.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            modifier = Modifier.testTag("countdown_text")
                        )
                    }
                }

                // Bottom Info Bar inside Hero Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.25f))
                        .border(0.5.dp, HairlineBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val nextItem = schedule.items.firstOrNull { it.prayer == schedule.nextWaqt }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccessTime,
                            contentDescription = null,
                            tint = ChampagneGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = uiText(
                                "পরবর্তী: $nextWaqtName (${nextItem?.formattedTime ?: "--:--"})",
                                "Next: $nextWaqtName (${nextItem?.formattedTime ?: "--:--"})"
                            ),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    Text(
                        text = schedule.locationName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SlateGrayMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Returns prayer icon corresponding to each Waqt
 */
fun getPrayerIcon(prayer: PrayerName): ImageVector {
    return when (prayer) {
        PrayerName.FAJR -> Icons.Filled.WbTwilight
        PrayerName.SUNRISE -> Icons.Filled.WbSunny
        PrayerName.DHUHR -> Icons.Filled.LightMode
        PrayerName.ASR -> Icons.Filled.BrightnessMedium
        PrayerName.MAGHRIB -> Icons.Filled.NightsStay
        PrayerName.ISHA -> Icons.Filled.DarkMode
    }
}

/**
 * 3. Expanded Daily Prayer Schedule & Time Ranges Section (Clean Spacious Cards)
 */
@Composable
fun DailyPrayerScheduleSection(
    schedule: PrayerSchedule,
    onEditPrayer: (PrayerTimeItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isGridView by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Header with View Mode Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionHeader(
                title = uiText("দৈনিক নামাজের সময়সূচি", "Daily Prayer Schedule"),
                subtitle = uiText("ওয়াক্ত ও জামাত (ট্যাপ করে মসজিদের সময় সেট করুন)", "Waqt & Jamat (Tap to customize mosque time)"),
                trailing = {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkEmeraldElevated.copy(alpha = 0.7f))
                            .border(0.5.dp, HairlineBorder, RoundedCornerShape(8.dp))
                            .padding(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { isGridView = false },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.List,
                                contentDescription = uiText("তালিকা ভিউ", "List View"),
                                tint = if (!isGridView) MintGlow else SlateGrayMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(
                            onClick = { isGridView = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.GridOn,
                                contentDescription = uiText("গ্রিড ভিউ", "Grid View"),
                                tint = if (isGridView) MintGlow else SlateGrayMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            )
        }

        if (isGridView) {
            // 2-Column Grid of Prominent Prayer Cards
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val chunked = schedule.items.chunked(2)
                chunked.forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pair.forEach { item ->
                            Box(modifier = Modifier.weight(1f)) {
                                PrayerGridCard(
                                    item = item,
                                    isActive = item.prayer == schedule.currentWaqt,
                                    isNext = item.isNext,
                                    onClick = { onEditPrayer(item) }
                                )
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        } else {
            // Spacious Vertical List Cards
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                schedule.items.forEach { item ->
                    PrayerListItemCard(
                        item = item,
                        isActive = item.prayer == schedule.currentWaqt,
                        isNext = item.isNext,
                        onEditClick = { onEditPrayer(item) }
                    )
                }
            }
        }
    }
}

/**
 * Prominent Prayer List Item Card with Left Icon/Name, Jamat Time, & Right Start/End Range
 */
@Composable
fun PrayerListItemCard(
    item: PrayerTimeItem,
    isActive: Boolean,
    isNext: Boolean,
    onEditClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isEditable = item.prayer != PrayerName.SUNRISE
    val cardBg = when {
        isActive -> DarkEmeraldElevated
        isNext -> DarkEmeraldSurface.copy(alpha = 0.95f)
        else -> DarkEmeraldSurface.copy(alpha = 0.75f)
    }

    val borderColor = when {
        isActive -> MintGlow.copy(alpha = 0.85f)
        isNext -> ChampagneGold.copy(alpha = 0.65f)
        else -> HairlineBorder
    }

    val borderWidth = if (isActive || isNext) 1.2.dp else 0.5.dp

    FrostedGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isEditable) Modifier.clickable(onClick = onEditClick) else Modifier
            )
            .testTag("prayer_item_${item.prayer.name.lowercase()}"),
        cornerRadius = 18.dp,
        contentPadding = 14.dp,
        backgroundColor = cardBg,
        borderColor = borderColor,
        borderWidth = borderWidth
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Prayer Name & Icon (Left side, bold font)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                val uiLang = LocalAppLanguage.current
                val prayerLabel = item.prayer.localizedName(uiLang)
                // 40dp Icon Medallion
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (isActive) MintGlow.copy(alpha = 0.16f)
                            else if (isNext) ChampagneGold.copy(alpha = 0.14f)
                            else Color.White.copy(alpha = 0.04f)
                        )
                        .border(
                            width = 0.8.dp,
                            color = if (isActive) MintGlow.copy(alpha = 0.6f) else HairlineBorder,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getPrayerIcon(item.prayer),
                        contentDescription = prayerLabel,
                        tint = if (isActive) MintGlow else if (isNext) ChampagneGold else TextPrimary,
                        modifier = Modifier.size(19.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = prayerLabel,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) MintGlow else TextPrimary
                            )
                        )
                        if (isActive) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(MintGlow)
                            )
                        }
                        if (item.isCustomTime || item.isCustomJamat) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ChampagneGold.copy(alpha = 0.16f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = uiText("কাস্টম", "Custom"),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 8.sp,
                                        color = ChampagneGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    if (isEditable && item.formattedJamatTime.isNotBlank()) {
                        Text(
                            text = uiText("জামাত: ${item.formattedJamatTime}", "Jamat: ${item.formattedJamatTime}"),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = MintGlow.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium
                            )
                        )
                    } else {
                        val normUi = com.example.data.util.SupportedLanguage.normalizeCode(uiLang)
                        Text(
                            text = when (normUi) {
                                "bn" -> item.prayer.englishName
                                "ar" -> item.prayer.englishName
                                else -> item.prayer.arabicName
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = SlateGrayMuted
                            )
                        )
                    }
                }
            }

            // 2. Explicit Start & End Time Range & Active Indicator (Right side)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Range in Champagne Gold
                    Text(
                        text = "${item.formattedTime} - ${item.formattedEndTime}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChampagneGold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                    )

                    // Status or label pill
                    if (isActive) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MintGlow.copy(alpha = 0.15f))
                                .border(0.6.dp, MintGlow.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 7.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(MintGlow)
                            )
                            Text(
                                text = uiText("চলমান ওয়াক্ত", "Current Waqt"),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = MintGlow,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    } else if (isNext) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ChampagneGold.copy(alpha = 0.14f))
                                .border(0.6.dp, ChampagneGold.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = uiText("পরবর্তী ওয়াক্ত", "Next Waqt"),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = ChampagneGold,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    } else {
                        Text(
                            text = uiText("ওয়াক্তের সময়সীমা", "Waqt Window"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                color = SlateGrayMuted.copy(alpha = 0.7f)
                            )
                        )
                    }
                }

                if (isEditable) {
                    Icon(
                        imageVector = Icons.Filled.Tune,
                        contentDescription = uiText("সময় কাস্টমাইজ করুন", "Customize Time"),
                        tint = SlateGrayMuted.copy(alpha = 0.8f),
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

/**
 * 2-Column Prayer Grid Card
 */
@Composable
fun PrayerGridCard(
    item: PrayerTimeItem,
    isActive: Boolean,
    isNext: Boolean,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiLang = LocalAppLanguage.current
    val isEditable = item.prayer != PrayerName.SUNRISE
    val cardBg = when {
        isActive -> DarkEmeraldElevated
        isNext -> DarkEmeraldSurface.copy(alpha = 0.95f)
        else -> DarkEmeraldSurface.copy(alpha = 0.75f)
    }

    val borderColor = when {
        isActive -> MintGlow.copy(alpha = 0.85f)
        isNext -> ChampagneGold.copy(alpha = 0.65f)
        else -> HairlineBorder
    }

    FrostedGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isEditable) Modifier.clickable(onClick = onClick) else Modifier
            )
            .testTag("prayer_grid_${item.prayer.name.lowercase()}"),
        cornerRadius = 18.dp,
        contentPadding = 12.dp,
        backgroundColor = cardBg,
        borderColor = borderColor,
        borderWidth = if (isActive || isNext) 1.2.dp else 0.5.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            if (isActive) MintGlow.copy(alpha = 0.16f)
                            else if (isNext) ChampagneGold.copy(alpha = 0.14f)
                            else Color.White.copy(alpha = 0.04f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getPrayerIcon(item.prayer),
                        contentDescription = null,
                        tint = if (isActive) MintGlow else if (isNext) ChampagneGold else TextPrimary,
                        modifier = Modifier.size(17.dp)
                    )
                }

                if (isActive) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MintGlow.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = uiText("চলমান", "Active"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                color = MintGlow,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                } else if (isNext) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ChampagneGold.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = uiText("পরবর্তী", "Next"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                color = ChampagneGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = item.prayer.localizedName(uiLang),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) MintGlow else TextPrimary
                    )
                )
                Text(
                    text = "${item.formattedTime} - ${item.formattedEndTime}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ChampagneGold,
                        fontFamily = FontFamily.Monospace
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (isEditable && item.formattedJamatTime.isNotBlank()) {
                    Text(
                        text = uiText("জামাত: ${item.formattedJamatTime}", "Jamat: ${item.formattedJamatTime}"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = MintGlow
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeLocationSelectorDialog(
    currentLocationLabel: String,
    isGpsLocation: Boolean,
    isDetecting: Boolean,
    onAutoDetect: () -> Unit,
    onSelectDistrict: (District) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    val permDeniedMsg = uiText("অটো লোকেশনের জন্য লোকেশন পারমিশন প্রয়োজন", "Location permission is required for auto-detect")
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val granted = perms.values.any { it }
        if (granted) {
            onAutoDetect()
        } else {
            Toast.makeText(context, permDeniedMsg, Toast.LENGTH_SHORT).show()
        }
    }

    val filteredDistricts = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            DistrictDirectory.BANGLADESH_DISTRICTS
        } else {
            val q = searchQuery.trim().lowercase()
            DistrictDirectory.BANGLADESH_DISTRICTS.filter {
                it.nameBengali.contains(q) ||
                it.nameEnglish.lowercase().contains(q) ||
                it.divisionBengali.contains(q)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkEmeraldSurface,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = uiText("অবস্থান ও জেলা নির্ধারণ", "Select Location & District"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = uiText("বর্তমান: ", "Current: ") + "$currentLocationLabel ${if (isGpsLocation) "(GPS)" else ""}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MintGlow,
                        fontSize = 11.sp
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        if (fine || coarse) {
                            onAutoDetect()
                        } else {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    enabled = !isDetecting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("home_auto_detect_gps_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MintGlow,
                        contentColor = ObsidianBackground
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isDetecting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = ObsidianBackground,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(uiText("অবস্থান শনাক্ত হচ্ছে...", "Detecting GPS..."), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    } else {
                        Icon(
                            imageVector = Icons.Filled.MyLocation,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(uiText("বর্তমান জিপিএস অবস্থান শনাক্ত করুন", "Auto-Detect GPS Location"), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(uiText("জেলা খুঁজুন...", "Search district..."), color = SlateGrayMuted, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = HairlineBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredDistricts, key = { it.id }) { district ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkEmeraldElevated)
                                .border(0.5.dp, HairlineBorder, RoundedCornerShape(8.dp))
                                .clickable { onSelectDistrict(district) }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val isBnDistrictUi = com.example.data.util.SupportedLanguage.normalizeCode(LocalAppLanguage.current) == "bn"
                            Column {
                                Text(
                                    text = if (isBnDistrictUi) district.nameBengali else district.nameEnglish,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = TextPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = if (isBnDistrictUi) {
                                        "${district.nameEnglish} • বিভাগ: ${district.divisionBengali}"
                                    } else {
                                        val countryLabel = LocalizationRegistry.translate(LocalAppLanguage.current, "বাংলাদেশ", "Bangladesh")
                                        "${district.nameEnglish}, $countryLabel"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SlateGrayMuted,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(uiText("বন্ধ করুন", "Close"), color = ChampagneGold)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun CustomPrayerTimingDialog(
    item: PrayerTimeItem,
    customWaqt24: String?,
    customJamat24: String?,
    onSave: (String?, String?) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val uiLang = LocalAppLanguage.current
    val prayerLabel = item.prayer.localizedName(uiLang)
    val is24Hour = remember(context) { TimeFormatUtils.is24HourFormat(context) }

    val initialWaqtPair = remember(item.timeMillis, customWaqt24) {
        customWaqt24?.let { TimeFormatUtils.parseTimeString(it) } ?: run {
            val c = Calendar.getInstance().apply { timeInMillis = item.timeMillis }
            Pair(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))
        }
    }
    val initialJamatPair = remember(item.jamatTimeMillis, customJamat24) {
        customJamat24?.let { TimeFormatUtils.parseTimeString(it) } ?: run {
            val ms = if (item.jamatTimeMillis > 0L) item.jamatTimeMillis else item.timeMillis + 25 * 60_000L
            val c = Calendar.getInstance().apply { timeInMillis = ms }
            Pair(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))
        }
    }

    var draftWaqt24 by remember { mutableStateOf(customWaqt24) }
    var draftJamat24 by remember { mutableStateOf(customJamat24) }
    var pickerTarget by remember { mutableStateOf<String?>(null) }

    val activeWaqtPair = remember(draftWaqt24, initialWaqtPair) {
        draftWaqt24?.let { TimeFormatUtils.parseTimeString(it) } ?: initialWaqtPair
    }
    val activeJamatPair = remember(draftJamat24, initialJamatPair) {
        draftJamat24?.let { TimeFormatUtils.parseTimeString(it) } ?: initialJamatPair
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkEmeraldSurface,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = uiText("$prayerLabel — মসজিদ ও জামাতের সময়", "$prayerLabel — Mosque & Jamat Time"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = uiText(
                        "আপনার স্থানীয় মসজিদের সময় অনুযায়ী ওয়াক্ত বা জামাতের সময় নির্ধারণ করুন (${TimeFormatUtils.getLocalTimeZoneDisplay()})",
                        "Set Waqt or Jamat time to match your local mosque (${TimeFormatUtils.getLocalTimeZoneDisplay()})"
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SlateGrayMuted,
                        fontSize = 11.sp
                    )
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    onClick = { pickerTarget = "WAQT" },
                    shape = RoundedCornerShape(12.dp),
                    color = DarkEmeraldElevated,
                    border = androidx.compose.foundation.BorderStroke(
                        0.8.dp,
                        if (draftWaqt24 != null) MintGlow else HairlineBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_dialog_waqt_btn")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = uiText("ওয়াক্ত শুরুর সময়", "Waqt Start Time"),
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateGrayMuted)
                            )
                            Text(
                                text = TimeFormatUtils.formatHourMinute(activeWaqtPair.first, activeWaqtPair.second, is24Hour),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = ChampagneGold,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                        Icon(Icons.Filled.Schedule, contentDescription = null, tint = ChampagneGold)
                    }
                }

                Surface(
                    onClick = { pickerTarget = "JAMAT" },
                    shape = RoundedCornerShape(12.dp),
                    color = DarkEmeraldElevated,
                    border = androidx.compose.foundation.BorderStroke(
                        0.8.dp,
                        if (draftJamat24 != null) MintGlow else ChampagneGold.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_dialog_jamat_btn")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = uiText("মসজিদের জামাতের সময়", "Mosque Jamat Time"),
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateGrayMuted)
                            )
                            Text(
                                text = TimeFormatUtils.formatHourMinute(activeJamatPair.first, activeJamatPair.second, is24Hour),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = MintGlow,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                        Icon(Icons.Filled.Mosque, contentDescription = null, tint = MintGlow)
                    }
                }

                if (draftWaqt24 != null || draftJamat24 != null || item.isCustomTime || item.isCustomJamat) {
                    OutlinedButton(
                        onClick = onReset,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(0.6.dp, ChampagneGold.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(uiText("স্বাভাবিক সময়ে ফিরিয়ে আনুন", "Reset to Default"), color = ChampagneGold, fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(draftWaqt24, draftJamat24) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChampagneGold,
                    contentColor = ObsidianBackground
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("custom_prayer_save_button")
            ) {
                Text(uiText("OK • সংরক্ষণ করুন", "OK • Save"), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(uiText("বাতিল", "Cancel"), color = SlateGrayMuted)
            }
        },
        shape = RoundedCornerShape(18.dp)
    )

    pickerTarget?.let { target ->
        val isWaqt = target == "WAQT"
        val pair = if (isWaqt) activeWaqtPair else activeJamatPair
        LocalTimePickerDialog(
            title = if (isWaqt) uiText("$prayerLabel — ওয়াক্ত সময়", "$prayerLabel — Waqt Time") else uiText("$prayerLabel — জামাতের সময়", "$prayerLabel — Jamat Time"),
            subtitle = uiText("স্থানীয় সময় অনুযায়ী পরিবর্তন করে OK চাপুন", "Adjust in local time and tap OK"),
            initialHour = pair.first,
            initialMinute = pair.second,
            onConfirm = { h24, m ->
                val canonical = TimeFormatUtils.toCanonicalTime24(h24, m)
                if (isWaqt) {
                    draftWaqt24 = canonical
                } else {
                    draftJamat24 = canonical
                }
                pickerTarget = null
            },
            onDismiss = { pickerTarget = null }
        )
    }
}

/**
 * Backwards compatibility delegate for PrayerCapsulesRow
 */
@Composable
fun PrayerCapsulesRow(schedule: PrayerSchedule) {
    DailyPrayerScheduleSection(schedule = schedule)
}

/**
 * 4. Sahur & Iftar Live Tracker Bar (Spacious Split Gradient)
 */
@Composable
fun SahurIftarBar(schedule: PrayerSchedule) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF0D251D),
                        Color(0xFF091A14)
                    )
                )
            )
            .border(0.6.dp, MintGlow.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(MintGlow.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.NightsStay,
                    contentDescription = null,
                    tint = MintGlow,
                    modifier = Modifier.size(17.dp)
                )
            }
            Column {
                Text(
                    text = uiText("সাহরির শেষ সময়", "Sahur Ends"),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = SlateGrayMuted
                    )
                )
                Text(
                    text = schedule.sahurRemainingFormatted,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 13.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .width(1.dp)
                .height(28.dp)
                .background(HairlineBorder)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(ChampagneGold.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.WbTwilight,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(17.dp)
                )
            }
            Column {
                Text(
                    text = uiText("ইফতারের সময় বাকি", "Time to Iftar"),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = SlateGrayMuted
                    )
                )
                Text(
                    text = schedule.iftarRemainingFormatted,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 13.sp,
                        color = ChampagneGold,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

/**
 * 8. Collapsible / Tabbed Auxiliary Prayer Times (Nafl & Makruh Warning Times)
 */
@Composable
fun AuxiliaryPrayerTimesCard(schedule: PrayerSchedule) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Nafl, 1 = Makruh

    FrostedGlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = if (selectedTab == 1) MakruhBackground else DarkEmeraldSurface,
        borderColor = if (selectedTab == 1) MakruhBorder.copy(alpha = 0.4f) else HairlineBorder,
        borderWidth = 0.6.dp
    ) {
        // Tab selector pill row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(DarkEmeraldElevated.copy(alpha = 0.7f))
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Tab 0: Nafl
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedTab == 0) DarkEmeraldSurface else Color.Transparent)
                    .border(
                        if (selectedTab == 0) 0.8.dp else 0.dp,
                        if (selectedTab == 0) ChampagneGold.copy(alpha = 0.6f) else Color.Transparent,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { selectedTab = 0 }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.DarkMode,
                        contentDescription = null,
                        tint = if (selectedTab == 0) ChampagneGold else SlateGrayMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = uiText("নফল সালাতের সময়", "Nafl Prayer Times"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selectedTab == 0) TextPrimary else SlateGrayMuted
                        )
                    )
                }
            }

            // Tab 1: Makruh
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedTab == 1) MakruhBackground else Color.Transparent)
                    .border(
                        if (selectedTab == 1) 0.8.dp else 0.dp,
                        if (selectedTab == 1) MakruhBorder.copy(alpha = 0.6f) else Color.Transparent,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { selectedTab = 1 }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.WarningAmber,
                        contentDescription = null,
                        tint = if (selectedTab == 1) Color(0xFFFFB4BA) else SlateGrayMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = uiText("মাকরূহ (নিষিদ্ধ) সময়", "Prohibited (Makruh) Times"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selectedTab == 1) Color(0xFFFFB4BA) else SlateGrayMuted
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Content Display
        if (selectedTab == 0) {
            // Nafl Schedule
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NafalItem(title = uiText("ইশরাক", "Ishraq"), time = schedule.nafalTimes.ishraq)
                NafalItem(title = uiText("চাশত (দুহা)", "Duha (Chasht)"), time = schedule.nafalTimes.chasht)
                NafalItem(title = uiText("আওয়াবীন", "Awwabin"), time = schedule.nafalTimes.awwabin)
                NafalItem(title = uiText("তাহাজ্জুদ শেষ ১/৩", "Tahajjud Last 1/3"), time = schedule.nafalTimes.tahajjudLastThird, isSpecial = true)
            }
        } else {
            // Makruh Prohibited Schedule
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProhibitedTimeColumn(
                    label = uiText("সূর্যোদয়", "Sunrise"),
                    timeRange = "${schedule.prohibitedTimes.sunriseStart} - ${schedule.prohibitedTimes.sunriseEnd}"
                )
                ProhibitedTimeColumn(
                    label = uiText("মধ্যাহ্ন (যাওয়াল)", "Zenith (Zawal)"),
                    timeRange = "${schedule.prohibitedTimes.zawalStart} - ${schedule.prohibitedTimes.zawalEnd}"
                )
                ProhibitedTimeColumn(
                    label = uiText("সূর্যাস্ত", "Sunset"),
                    timeRange = "${schedule.prohibitedTimes.sunsetStart} - ${schedule.prohibitedTimes.sunsetEnd}"
                )
            }
        }
    }
}

@Composable
fun ProhibitedTimesCard(schedule: PrayerSchedule) {
    FrostedGlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = MakruhBackground,
        borderColor = MakruhBorder.copy(alpha = 0.4f),
        borderWidth = 0.7.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.WarningAmber,
                contentDescription = null,
                tint = MakruhBorder,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = uiText("নামাজ আদায়ের মাকরূহ (নিষিদ্ধ) সময়সমূহ", "Prohibited (Makruh) Prayer Times"),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 13.sp,
                    color = Color(0xFFFFB4BA),
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ProhibitedTimeColumn(
                label = uiText("সূর্যোদয়", "Sunrise"),
                timeRange = "${schedule.prohibitedTimes.sunriseStart} - ${schedule.prohibitedTimes.sunriseEnd}"
            )
            ProhibitedTimeColumn(
                label = uiText("মধ্যাহ্ন (যাওয়াল)", "Zenith (Zawal)"),
                timeRange = "${schedule.prohibitedTimes.zawalStart} - ${schedule.prohibitedTimes.zawalEnd}"
            )
            ProhibitedTimeColumn(
                label = uiText("সূর্যাস্ত", "Sunset"),
                timeRange = "${schedule.prohibitedTimes.sunsetStart} - ${schedule.prohibitedTimes.sunsetEnd}"
            )
        }
    }
}

@Composable
fun ProhibitedTimeColumn(label: String, timeRange: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = SlateGrayMuted
            )
        )
        Text(
            text = timeRange,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        )
    }
}

@Composable
fun NafalScheduleCard(schedule: PrayerSchedule) {
    FrostedGlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        SectionHeader(
            title = uiText("নফল নামাজের সময়", "Nafl Prayer Schedule"),
            subtitle = uiText("তাহাজ্জুদ, ইশরাক ও আওয়াবীন", "Tahajjud, Ishraq & Awwabin")
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NafalItem(title = uiText("ইশরাক", "Ishraq"), time = schedule.nafalTimes.ishraq)
            NafalItem(title = uiText("চাশত (দুহা)", "Duha (Chasht)"), time = schedule.nafalTimes.chasht)
            NafalItem(title = uiText("আওয়াবীন", "Awwabin"), time = schedule.nafalTimes.awwabin)
            NafalItem(title = uiText("তাহাজ্জুদ শেষ ১/৩", "Tahajjud Last 1/3"), time = schedule.nafalTimes.tahajjudLastThird, isSpecial = true)
        }
    }
}

@Composable
fun NafalItem(title: String, time: String, isSpecial: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = if (isSpecial) ChampagneGold else SlateGrayMuted
            )
        )
        Text(
            text = time,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSpecial) MintGlow else TextPrimary
            )
        )
    }
}

@Composable
fun DailyHadithHomeCard(
    hadith: DailyHadith,
    audioPlayer: com.example.data.audio.AudioPlayerManager,
    onOpenDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiLang = LocalAppLanguage.current
    val contentLang = LocalContentLanguage.current
    val playbackState by audioPlayer.playbackState.collectAsState()
    val hadithMediaPrefix = "tts_${hadith.localizedTitle(uiLang).hashCode()}"
    val isThisPlaying = playbackState.isPlaying && playbackState.currentMediaId.startsWith(hadithMediaPrefix)
    val isThisBuffering = playbackState.isBuffering && playbackState.currentMediaId.startsWith(hadithMediaPrefix)

    FrostedGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenDetails)
            .testTag("home_daily_hadith_card"),
        borderColor = ChampagneGold.copy(alpha = 0.45f)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
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
                            .background(ChampagneGold.copy(alpha = 0.16f))
                            .border(0.6.dp, ChampagneGold.copy(alpha = 0.45f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MenuBook,
                            contentDescription = uiText("আজকের হাদিস", "Today's Hadith"),
                            tint = ChampagneGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = uiText("আজকের হাদিস", "Today's Hadith"),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = ChampagneGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = uiText("Daily Prophetic Wisdom", "Daily Prophetic Wisdom"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MintGlow,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkEmeraldElevated)
                        .border(0.5.dp, HairlineBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = hadith.localizedBookReference(uiLang),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SlateGrayMuted,
                            fontSize = 10.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Text(
                text = hadith.localizedTitle(uiLang),
                style = MaterialTheme.typography.titleSmall.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkEmeraldElevated.copy(alpha = 0.85f))
                    .border(0.5.dp, ChampagneGold.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = hadith.textArabic,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = AmiriFontFamily,
                        fontSize = 18.sp,
                        lineHeight = 30.sp,
                        textAlign = TextAlign.Right,
                        color = ChampagneGold
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            val transliterationText = remember(hadith.id, contentLang) {
                hadith.localizedTransliteration(contentLang)
            }
            if (transliterationText.isNotBlank()) {
                Text(
                    text = "${uiText("উচ্চারণ:", "Transliteration:")} $transliterationText",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MintGlow.copy(alpha = 0.92f),
                        fontSize = 11.5.sp,
                        lineHeight = 17.sp
                    )
                )
            }

            Text(
                text = "${uiText("অর্থ:", "Translation:")} ${hadith.localizedTranslation(contentLang)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            )

            Text(
                text = hadith.localizedNarrator(contentLang),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = SlateGrayMuted,
                    fontSize = 11.sp
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        if (isThisPlaying || isThisBuffering) {
                            audioPlayer.stop()
                        } else {
                            audioPlayer.playArabicTextSpeech(
                                text = hadith.textArabic,
                                title = hadith.localizedTitle(uiLang),
                                subtitle = hadith.localizedBookReference(uiLang),
                                preferOnline = true
                            )
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        0.8.dp,
                        if (isThisPlaying) MintGlow else ChampagneGold.copy(alpha = 0.55f)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isThisPlaying) MintGlow else ChampagneGold
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("home_hadith_play_btn")
                ) {
                    if (isThisBuffering) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = ChampagneGold
                        )
                    } else {
                        Icon(
                            imageVector = if (isThisPlaying) Icons.Filled.Stop else Icons.Filled.VolumeUp,
                            contentDescription = uiText("শুনুন", "Listen"),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isThisPlaying) uiText("থামান", "Stop") else uiText("শুনুন", "Listen"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Button(
                    onClick = onOpenDetails,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChampagneGold,
                        contentColor = ObsidianBackground
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(48.dp)
                        .testTag("home_hadith_details_btn")
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoStories,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = uiText("ব্যাখ্যা পড়ুন", "Read Explanation"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun HadithDetailSheetContent(
    hadith: DailyHadith,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val uiLang = LocalAppLanguage.current
    val contentLang = LocalContentLanguage.current
    val explanation = hadith.localizedExplanation(contentLang)
    val transliterationText = remember(hadith.id, contentLang) {
        hadith.localizedTransliteration(contentLang)
    }
    val audioPlayer = remember(context) { com.example.data.audio.AudioPlayerManager.getInstance(context) }
    val playbackState by audioPlayer.playbackState.collectAsState()
    val hadithMediaPrefix = "tts_${hadith.localizedTitle(uiLang).hashCode()}"
    val isPlaying = playbackState.isPlaying && playbackState.currentMediaId.startsWith(hadithMediaPrefix)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = hadith.localizedTitle(uiLang),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = ChampagneGold
                    )
                )
                Text(
                    text = "${hadith.localizedNarrator(uiLang)} • ${hadith.localizedBookReference(uiLang)}",
                    style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted)
                )
            }
            IconButton(
                onClick = {
                    if (isPlaying) {
                        audioPlayer.stop()
                    } else {
                        audioPlayer.playArabicTextSpeech(
                            text = hadith.textArabic,
                            title = hadith.localizedTitle(uiLang),
                            subtitle = hadith.localizedBookReference(uiLang),
                            preferOnline = true
                        )
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .testTag("hadith_sheet_play_btn")
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Stop else Icons.Filled.VolumeUp,
                    contentDescription = uiText("শুনুন", "Listen"),
                    tint = if (isPlaying) MintGlow else ChampagneGold
                )
            }
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = uiText("বন্ধ করুন", "Close"),
                    tint = SlateGrayMuted
                )
            }
        }

        // Arabic text with Amiri font
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkEmeraldElevated)
                .padding(16.dp)
        ) {
            Text(
                text = hadith.textArabic,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = AmiriFontFamily,
                    fontSize = 22.sp,
                    lineHeight = 36.sp,
                    textAlign = TextAlign.Right,
                    color = TextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (transliterationText.isNotBlank()) {
            Text(
                text = "${uiText("উচ্চারণ:", "Transliteration:")} $transliterationText",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MintGlow.copy(alpha = 0.92f),
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            )
        }

        Text(
            text = contentText("অনুবাদ:", "Translation:"),
            style = MaterialTheme.typography.labelLarge.copy(color = MintGlow)
        )
        Text(
            text = hadith.localizedTranslation(contentLang),
            style = MaterialTheme.typography.bodyLarge.copy(
                lineHeight = 24.sp,
                color = TextPrimary
            )
        )

        if (explanation.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MintGlow.copy(alpha = 0.08f))
                    .border(0.5.dp, MintGlow.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = contentText("সংক্ষিপ্ত ব্যাখ্যা ও শিক্ষা:", "Brief Reflection:"),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = explanation,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextPrimary.copy(alpha = 0.9f),
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }
    }
}
