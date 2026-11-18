package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.preference.AppPreferences
import com.example.ui.components.FrostedGlassCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Calendar

enum class CalendarViewMode {
    MONTH_GRID,
    YEAR_OVERVIEW
}

/**
 * Full Month & Year Islamic Hijri Calendar Screen for Noor-e-Hidayah
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HijriCalendarScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val prefs = remember { AppPreferences(context) }

    val uiLang = LocalAppLanguage.current
    val hijriOffset by prefs.hijriOffset.collectAsState(initial = 0)
    val districtLat by prefs.selectedDistrictLat.collectAsState(initial = 23.8103)
    val districtLng by prefs.selectedDistrictLng.collectAsState(initial = 90.4125)
    val districtName by prefs.selectedDistrictName.collectAsState(initial = "ঢাকা")

    // Determine initial Hijri date based on current time & offset
    val initialHijri = remember(hijriOffset) {
        HijriCalendarEngine.gregorianToHijri(Calendar.getInstance(), hijriOffset)
    }

    var selectedYear by remember { mutableIntStateOf(initialHijri.first) }
    var selectedMonthIndex by remember { mutableIntStateOf(initialHijri.second) }
    var viewMode by remember { mutableStateOf(CalendarViewMode.MONTH_GRID) }

    var selectedDayData by remember { mutableStateOf<HijriDayData?>(null) }
    var showMoonOffsetDialog by remember { mutableStateOf(false) }
    var showYearPickerDropdown by remember { mutableStateOf(false) }

    // Generate Month Data reactively when year, monthIndex, or offset changes
    val monthData = remember(selectedYear, selectedMonthIndex, hijriOffset, districtLat, districtLng) {
        HijriCalendarEngine.getMonthData(
            year = selectedYear,
            monthIndex = selectedMonthIndex,
            offsetDays = hijriOffset,
            latitude = districtLat,
            longitude = districtLng
        )
    }

    // Generate Year Overview reactively
    val yearSummary = remember(selectedYear, hijriOffset) {
        HijriCalendarEngine.getYearSummary(selectedYear, hijriOffset)
    }

    // Auto-select today if viewing today's month
    LaunchedEffect(monthData) {
        val todayCell = monthData.days.firstOrNull { it.isToday }
        if (todayCell != null && selectedDayData == null) {
            selectedDayData = todayCell
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // -----------------------------------------------------------------
            // 1. TOP APP BAR
            // -----------------------------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Button (48dp touch target)
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("btn_calendar_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = uiText("ফিরে যান", "Back"),
                        tint = ChampagneGold,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Title Header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = uiText("হিজরি ক্যালেন্ডার", "Hijri Calendar"),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = uiText("ইসলামিক সন ও গুরুত্বপূর্ণ দিবস", "Islamic Dates & Key Events"),
                        color = SlateGrayMuted,
                        fontSize = 11.sp
                    )
                }

                // Right Actions: Moon Offset Pill & Year Dropdown Trigger
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Moon Sighting Offset Badge Button (48dp min touch area)
                    Box(
                        modifier = Modifier
                            .height(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkEmeraldSurface)
                            .border(0.8.dp, ChampagneGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                showMoonOffsetDialog = true
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("btn_moon_offset"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DarkMode,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(14.dp)
                            )
                            val daySuffix = uiText("দিন", "d")
                            val offsetText = when {
                                hijriOffset > 0 -> "+$hijriOffset $daySuffix"
                                hijriOffset < 0 -> "$hijriOffset $daySuffix"
                                else -> "±${formatLocalizedInt(0, uiLang)} $daySuffix"
                            }
                            Text(
                                text = "${uiText("চাঁদ", "Moon")}: $offsetText",
                                color = ChampagneGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Year Dropdown Button
                    Box(
                        modifier = Modifier
                            .height(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkEmeraldElevated)
                            .border(0.8.dp, MintGlow.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                            .clickable { showYearPickerDropdown = true }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("btn_year_picker"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${formatLocalizedInt(selectedYear, uiLang)} ${uiText("হিজরি", "AH")}",
                                color = MintGlow,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = uiText("বছর নির্বাচন", "Select Year"),
                                tint = MintGlow,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Year Selection Dropdown Menu
                        DropdownMenu(
                            expanded = showYearPickerDropdown,
                            onDismissRequest = { showYearPickerDropdown = false },
                            modifier = Modifier.background(DarkEmeraldSurface)
                        ) {
                            listOf(1445, 1446, 1447, 1448, 1449, 1450).forEach { yr ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${formatLocalizedInt(yr, uiLang)} ${uiText("হিজরি", "AH")}",
                                            color = if (yr == selectedYear) ChampagneGold else Color.White,
                                            fontWeight = if (yr == selectedYear) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        selectedYear = yr
                                        showYearPickerDropdown = false
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // -----------------------------------------------------------------
            // 2. VIEW MODE TOGGLE (MONTH GRID VS YEAR OVERVIEW)
            // -----------------------------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkEmeraldSurface)
                    .border(0.5.dp, HairlineBorder, RoundedCornerShape(12.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Month View Tab
                val isMonthMode = viewMode == CalendarViewMode.MONTH_GRID
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .run {
                            if (isMonthMode) {
                                background(
                                    Brush.horizontalGradient(listOf(DarkEmeraldElevated, DarkEmeraldSurface))
                                ).border(1.dp, ChampagneGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            } else {
                                background(Color.Transparent)
                            }
                        }
                        .clickable {
                            viewMode = CalendarViewMode.MONTH_GRID
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = null,
                            tint = if (isMonthMode) ChampagneGold else SlateGrayMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = uiText("মাসিক গ্রিড", "Monthly Grid"),
                            color = if (isMonthMode) ChampagneGold else SlateGrayMuted,
                            fontWeight = if (isMonthMode) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.5.sp
                        )
                    }
                }

                // Year Overview Tab
                val isYearMode = viewMode == CalendarViewMode.YEAR_OVERVIEW
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .run {
                            if (isYearMode) {
                                background(
                                    Brush.horizontalGradient(listOf(DarkEmeraldElevated, DarkEmeraldSurface))
                                ).border(1.dp, ChampagneGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            } else {
                                background(Color.Transparent)
                            }
                        }
                        .clickable {
                            viewMode = CalendarViewMode.YEAR_OVERVIEW
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Event,
                            contentDescription = null,
                            tint = if (viewMode == CalendarViewMode.YEAR_OVERVIEW) ChampagneGold else SlateGrayMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = uiText("বার্ষিক সারসংক্ষেপ", "Yearly Overview"),
                            color = if (viewMode == CalendarViewMode.YEAR_OVERVIEW) ChampagneGold else SlateGrayMuted,
                            fontWeight = if (viewMode == CalendarViewMode.YEAR_OVERVIEW) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.5.sp
                        )
                    }
                }
            }

            // -----------------------------------------------------------------
            // 3. MAIN CONTENT BODY
            // -----------------------------------------------------------------
            when (viewMode) {
                CalendarViewMode.MONTH_GRID -> {
                    MonthGridView(
                        monthData = monthData,
                        selectedDay = selectedDayData,
                        onSelectDay = { day ->
                            selectedDayData = day
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        onPreviousMonth = {
                            if (selectedMonthIndex == 0) {
                                selectedMonthIndex = 11
                                selectedYear -= 1
                            } else {
                                selectedMonthIndex -= 1
                            }
                            selectedDayData = null
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        onNextMonth = {
                            if (selectedMonthIndex == 11) {
                                selectedMonthIndex = 0
                                selectedYear += 1
                            } else {
                                selectedMonthIndex += 1
                            }
                            selectedDayData = null
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        onGoToToday = {
                            val todayHijri = HijriCalendarEngine.gregorianToHijri(Calendar.getInstance(), hijriOffset)
                            selectedYear = todayHijri.first
                            selectedMonthIndex = todayHijri.second
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        onJumpToMonth = { mIndex ->
                            selectedMonthIndex = mIndex
                            selectedDayData = null
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    )
                }

                CalendarViewMode.YEAR_OVERVIEW -> {
                    YearOverviewView(
                        yearSummary = yearSummary,
                        currentHijriYear = initialHijri.first,
                        currentHijriMonth = initialHijri.second,
                        onSelectMonth = { mIndex ->
                            selectedMonthIndex = mIndex
                            viewMode = CalendarViewMode.MONTH_GRID
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    )
                }
            }
        }

        // -----------------------------------------------------------------
        // 4. DAY DETAIL BOTTOM SHEET
        // -----------------------------------------------------------------
        if (selectedDayData != null && viewMode == CalendarViewMode.MONTH_GRID) {
            DayDetailBottomSheet(
                dayData = selectedDayData!!,
                districtName = districtName,
                onDismiss = { selectedDayData = null }
            )
        }

        // -----------------------------------------------------------------
        // 5. MOON SIGHTING MANUAL OFFSET DIALOG
        // -----------------------------------------------------------------
        if (showMoonOffsetDialog) {
            MoonSightingOffsetDialog(
                currentOffset = hijriOffset,
                onSaveOffset = { newOffset ->
                    coroutineScope.launch {
                        prefs.setHijriOffset(newOffset)
                    }
                    showMoonOffsetDialog = false
                    val offsetPrefix = if (newOffset > 0) "+$newOffset" else "$newOffset"
                    Toast.makeText(
                        context,
                        localizedText(
                            uiLang,
                            "চাঁদ সমন্বয় হালনাগাদ হয়েছে ($offsetPrefix দিন)",
                            "Moon offset updated ($offsetPrefix days)"
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onDismiss = { showMoonOffsetDialog = false }
            )
        }
    }
}

/**
 * 📅 1. MONTH GRID VIEW (Header, 7-Column Day Names, Day Cells, Quick Month Bar)
 */
@Composable
private fun MonthGridView(
    monthData: HijriMonthData,
    selectedDay: HijriDayData?,
    onSelectDay: (HijriDayData) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onGoToToday: () -> Unit,
    onJumpToMonth: (Int) -> Unit
) {
    val uiLang = LocalAppLanguage.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        // ---------------------------------------------------------------------
        // MONTH NAVIGATION BANNER (Prev < Month Name > Next)
        // ---------------------------------------------------------------------
        FrostedGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            cornerRadius = 16.dp,
            backgroundColor = DarkEmeraldSurface,
            borderColor = ChampagneGold.copy(alpha = 0.35f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Previous Month Arrow (48dp touch target)
                IconButton(
                    onClick = onPreviousMonth,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("btn_prev_month")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = uiText("পূর্ববর্তী মাস", "Previous Month"),
                        tint = ChampagneGold,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Month Title & Arabic Calligraphy
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${monthData.localizedMonthName(uiLang)} ${formatLocalizedInt(monthData.year, uiLang)} ${uiText("হিজরি", "AH")}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.5.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${monthData.monthNameAr} • ${formatLocalizedInt(monthData.daysCount, uiLang)} ${uiText("দিন", "days")}",
                        color = ChampagneGold,
                        fontFamily = AmiriFontFamily,
                        fontSize = 13.5.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Next Month Arrow (48dp touch target)
                IconButton(
                    onClick = onNextMonth,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("btn_next_month")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = uiText("পরবর্তী মাস", "Next Month"),
                        tint = ChampagneGold,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // ---------------------------------------------------------------------
        // 7-COLUMN WEEKDAY HEADER ROW
        // ---------------------------------------------------------------------
        val weekdays = listOf(
            uiText("শনি", "Sat") to false,
            uiText("রবি", "Sun") to false,
            uiText("সোম", "Mon") to false,
            uiText("মঙ্গল", "Tue") to false,
            uiText("বুধ", "Wed") to false,
            uiText("বৃহঃ", "Thu") to false,
            uiText("শুক্র", "Fri") to true // Jumu'ah Friday highlighted
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            weekdays.forEach { (name, isFriday) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name,
                        color = if (isFriday) MintGlow else SlateGrayMuted,
                        fontWeight = if (isFriday) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ---------------------------------------------------------------------
        // CALENDAR DAY CELLS GRID (7 COLUMNS)
        // ---------------------------------------------------------------------
        val totalSlots = monthData.leadingEmptyDays + monthData.days.size
        val paddedSlots = if (totalSlots % 7 != 0) totalSlots + (7 - (totalSlots % 7)) else totalSlots

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(paddedSlots) { index ->
                if (index < monthData.leadingEmptyDays || index >= monthData.leadingEmptyDays + monthData.days.size) {
                    // Empty Cell placeholder for alignment
                    Box(modifier = Modifier.aspectRatio(0.85f))
                } else {
                    val dayIndex = index - monthData.leadingEmptyDays
                    val dayData = monthData.days[dayIndex]
                    val isSelected = selectedDay?.hijriDay == dayData.hijriDay

                    DayGridCell(
                        dayData = dayData,
                        isSelected = isSelected,
                        onClick = { onSelectDay(dayData) }
                    )
                }
            }
        }

        // ---------------------------------------------------------------------
        // QUICK MONTH JUMPER BAR (12 MONTHS CHIPS)
        // ---------------------------------------------------------------------
        val quickMonths = (1..12).map { com.example.data.util.SupportedLanguage.localizedHijriMonth(it, uiLang) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            quickMonths.forEachIndexed { idx, name ->
                val isSelectedMonth = idx == monthData.monthIndex
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelectedMonth) ChampagneGold else DarkEmeraldSurface)
                        .border(
                            0.5.dp,
                            if (isSelectedMonth) ChampagneGold else HairlineBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onJumpToMonth(idx) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name,
                        color = if (isSelectedMonth) ObsidianBackground else Color.White,
                        fontSize = 11.sp,
                        fontWeight = if (isSelectedMonth) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

/**
 * Single Day Grid Cell with Dual Dates & Badges
 */
@Composable
private fun DayGridCell(
    dayData: HijriDayData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val uiLang = LocalAppLanguage.current
    val cellBackground = when {
        dayData.isToday -> Brush.verticalGradient(listOf(DarkEmeraldElevated, DarkEmeraldSurface))
        isSelected -> Brush.verticalGradient(listOf(DarkEmeraldSurface, DarkEmeraldElevated))
        else -> Brush.verticalGradient(listOf(DarkEmeraldSurface.copy(alpha = 0.85f), DarkEmeraldSurface))
    }

    val borderColor = when {
        isSelected -> ChampagneGold
        dayData.isToday -> MintGlow
        dayData.specialEvents.isNotEmpty() -> ChampagneGold.copy(alpha = 0.5f)
        dayData.isFriday -> MintGlow.copy(alpha = 0.25f)
        else -> HairlineBorder
    }

    Box(
        modifier = Modifier
            .aspectRatio(0.85f)
            .clip(RoundedCornerShape(10.dp))
            .background(cellBackground)
            .border(
                width = if (isSelected || dayData.isToday) 1.5.dp else 0.6.dp,
                color = borderColor,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .testTag("day_cell_${dayData.hijriDay}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(2.dp)
        ) {
            // Hijri Day
            Text(
                text = formatLocalizedInt(dayData.hijriDay, uiLang),
                color = when {
                    dayData.isToday -> MintGlow
                    isSelected -> ChampagneGold
                    dayData.isFriday -> MintGlow
                    else -> Color.White
                },
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            // Gregorian Date (e.g. "4 Nov" or "৪ নভে")
            Text(
                text = "${formatLocalizedInt(dayData.gregorianDay, uiLang)} ${dayData.localizedGregorianShort(uiLang)}",
                color = if (dayData.isToday) ChampagneGold else SlateGrayMuted,
                fontSize = 9.sp,
                maxLines = 1
            )

            // Event / Fasting Indicator Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.5.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                // Gold Dot: Islamic Special Event
                if (dayData.specialEvents.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(ChampagneGold)
                    )
                }

                // Mint Dot: Sunnah Fasting (Mon/Thu, Ayyam al-Bid, etc.)
                if (dayData.isSunnahFastingDay && dayData.specialEvents.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(MintGlow)
                    )
                }
            }
        }

        // Today pill indicator in top-left
        if (dayData.isToday) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(3.dp)
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(MintGlow)
            )
        }
    }
}

/**
 * 📆 2. YEAR OVERVIEW VIEW (12 Months Grid with Islamic Highlights)
 */
@Composable
private fun YearOverviewView(
    yearSummary: HijriYearSummary,
    currentHijriYear: Int,
    currentHijriMonth: Int,
    onSelectMonth: (Int) -> Unit
) {
    val uiLang = LocalAppLanguage.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            // Year Summary Banner
            FrostedGlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 14.dp,
                backgroundColor = DarkEmeraldSurface,
                borderColor = ChampagneGold.copy(alpha = 0.35f)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiText(
                            "পবিত্র ${HijriDayData.formatBengaliNumber(yearSummary.year)} হিজরি সন",
                            "Blessed Hijri Year ${yearSummary.year} AH"
                        ),
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiText(
                            "১২টি চান্দ্রমাসের পূর্ণ ইসলামিক দিনপঞ্জি • যেকোনো মাসে ট্যাপ করে পূর্ণ গ্রিড দেখুন",
                            "Complete 12-Month Islamic Lunar Calendar • Tap any month to view its grid"
                        ),
                        color = SlateGrayMuted,
                        fontSize = 11.5.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 12 Months Cards
        items(yearSummary.months.size) { idx ->
            val month = yearSummary.months[idx]
            val isCurrentMonth = (yearSummary.year == currentHijriYear && month.monthIndex == currentHijriMonth)

            FrostedGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectMonth(month.monthIndex) }
                    .testTag("month_overview_${month.monthIndex}"),
                cornerRadius = 14.dp,
                backgroundColor = if (isCurrentMonth) DarkEmeraldElevated else DarkEmeraldSurface,
                borderColor = if (isCurrentMonth) MintGlow.copy(alpha = 0.6f) else ChampagneGold.copy(alpha = 0.22f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Month Number Medallion
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(if (isCurrentMonth) MintGlow else ChampagneGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = formatLocalizedInt(month.monthIndex + 1, uiLang),
                                    color = if (isCurrentMonth) ObsidianBackground else ChampagneGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            Column {
                                Text(
                                    text = month.localizedMonthName(uiLang),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = month.monthNameAr,
                                    color = ChampagneGold,
                                    fontFamily = AmiriFontFamily,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Days count badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkEmeraldSurface)
                                .border(0.5.dp, HairlineBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${formatLocalizedInt(month.daysCount, uiLang)} ${uiText("দিন", "days")}",
                                color = if (isCurrentMonth) MintGlow else SlateGrayMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Gregorian Span
                    Text(
                        text = "${uiText("খ্রিষ্টীয় মেয়াদ:", "Gregorian Span:")} ${month.localizedStartGregorian(uiLang)} ${uiText("হতে", "to")} ${month.localizedEndGregorian(uiLang)}",
                        color = SlateGrayMuted,
                        fontSize = 11.sp
                    )

                    // Key Events Pills
                    val events = month.localizedKeyEvents(uiLang)
                    if (events.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            events.forEach { eventStr ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(DarkEmeraldSurface)
                                        .border(0.5.dp, ChampagneGold.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = eventStr,
                                        color = ChampagneGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
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

/**
 * 🕌 3. DAY DETAIL BOTTOM SHEET (Day-Specific Prayer Times, Historical Events & Sunnah Amals)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayDetailBottomSheet(
    dayData: HijriDayData,
    districtName: String,
    onDismiss: () -> Unit
) {
    val uiLang = LocalAppLanguage.current
    val contentLang = LocalContentLanguage.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkEmeraldSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SlateGrayMuted.copy(alpha = 0.5f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 10.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header: Hijri & Gregorian Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = dayData.formattedHijriDate(uiLang),
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${dayData.formattedGregorianDate(uiLang)} • $districtName",
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = uiText("বন্ধ করুন", "Close"),
                        tint = SlateGrayMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Special Islamic Events Card
            if (dayData.specialEvents.isNotEmpty()) {
                dayData.specialEvents.forEach { event ->
                    FrostedGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        cornerRadius = 14.dp,
                        backgroundColor = DarkEmeraldElevated,
                        borderColor = ChampagneGold.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = event.localizedTitle(uiLang),
                                    color = ChampagneGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(DarkEmeraldSurface)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = event.category.localizedLabel(uiLang),
                                        color = event.category.badgeColor,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = event.titleAr,
                                color = MintGlow,
                                fontFamily = AmiriFontFamily,
                                fontSize = 13.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = event.localizedDescription(contentLang),
                                color = Color.White,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "${uiText("দলিল:", "Reference:")} ${event.localizedVirtueRef(contentLang)}",
                                color = SlateGrayMuted,
                                fontSize = 11.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Fasting Status Card
            val fastingNote = dayData.localizedFastingNote(uiLang)
            if (fastingNote != null) {
                FrostedGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 12.dp,
                    backgroundColor = DarkEmeraldElevated,
                    borderColor = MintGlow.copy(alpha = 0.4f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MintGlow.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.NightsStay,
                                contentDescription = null,
                                tint = MintGlow,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = uiText("সিয়াম ও নফল রোজার বিধান", "Fasting & Sunnah Ruling"),
                                color = MintGlow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = fastingNote,
                                color = Color.White,
                                fontSize = 11.5.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Day-Specific 5 Waqt Prayer Times
            Text(
                text = uiText("এ দিনের নামাজের ওয়াক্তসূচি ($districtName):", "Prayer Schedule ($districtName):"),
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            val waqtList = listOf(
                uiText("ফজর", "Fajr") to dayData.fajrTime,
                uiText("সূর্যোদয়", "Sunrise") to dayData.sunriseTime,
                uiText("যোহর", "Dhuhr") to dayData.dhuhrTime,
                uiText("আসর", "Asr") to dayData.asrTime,
                uiText("মাগরিব", "Maghrib") to dayData.maghribTime,
                uiText("ইশা", "Isha") to dayData.ishaTime,
                uiText("তাহাজ্জুদ", "Tahajjud") to dayData.tahajjudTime,
                uiText("ইশরাক", "Ishraq") to dayData.ishraqTime
            )

            // Grid of prayer times
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                waqtList.chunked(4).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rowItems.forEach { (name, time) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkEmeraldElevated)
                                    .border(0.5.dp, HairlineBorder, RoundedCornerShape(8.dp))
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = name, color = SlateGrayMuted, fontSize = 10.sp)
                                    Text(text = time, color = ChampagneGold, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Close button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChampagneGold,
                    contentColor = ObsidianBackground
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(uiText("ঠিক আছে", "Done"), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

/**
 * 🌙 4. MOON SIGHTING MANUAL OFFSET DIALOG (-2 to +2 Days)
 */
@Composable
private fun MoonSightingOffsetDialog(
    currentOffset: Int,
    onSaveOffset: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedOffset by remember { mutableIntStateOf(currentOffset) }
    val haptic = LocalHapticFeedback.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkEmeraldSurface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.DarkMode,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = uiText("চাঁদ দেখা ও তারিখ সমন্বয়", "Moon Sighting Date Adjustment"),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = uiText(
                        "স্থানীয় চাঁদ দেখার ঘোষণার ওপর ভিত্তি করে হিজরি ক্যালেন্ডারের তারিখ ১ বা ২ দিন সমন্বয় করুন। এই সমন্বয়টি সমগ্র অ্যাপের ওয়াক্ত, আমল হাব ও ক্যালেন্ডার গ্রিডে কার্যকর হবে।",
                        "Adjust the Hijri date by ±1 or 2 days according to local moon sighting announcements. This applies across all prayer times and calendar views."
                    ),
                    color = SlateGrayMuted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                val options = listOf(
                    -2 to uiText("২ দিন পিছিয়ে (-২ দিন)", "2 days back (-2 days)"),
                    -1 to uiText("১ দিন পিছিয়ে (-১ দিন)", "1 day back (-1 day)"),
                    0 to uiText("স্বাভাবিক জ্যোতির্বৈজ্ঞানিক হিসাব (০ দিন)", "Standard astronomical calculation (0 days)"),
                    1 to uiText("১ দিন এগিয়ে (+১ দিন)", "1 day forward (+1 day)"),
                    2 to uiText("২ দিন এগিয়ে (+২ দিন)", "2 days forward (+2 days)")
                )

                options.forEach { (offsetVal, label) ->
                    val isSelected = selectedOffset == offsetVal
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) DarkEmeraldElevated else Color.Transparent)
                            .clickable {
                                selectedOffset = offsetVal
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                selectedOffset = offsetVal
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = ChampagneGold,
                                unselectedColor = SlateGrayMuted
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            color = if (isSelected) ChampagneGold else Color.White,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSaveOffset(selectedOffset) },
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
            TextButton(onClick = onDismiss) {
                Text(uiText("বাতিল", "Cancel"), color = SlateGrayMuted)
            }
        }
    )
}
