package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SystemUpdate
import com.example.data.security.SecurityGuard
import com.example.data.update.InAppUpdateManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.PhotoCamera
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.ui.theme.ThemeMode
import com.example.ui.theme.parseHexColor
import com.example.ui.theme.toHex
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.data.cloud.SyncStatus
import com.example.data.model.CalculationMethod
import com.example.data.model.District
import com.example.data.model.DistrictDirectory
import com.example.data.model.JuristicMethod
import com.example.data.model.PrayerName
import com.example.data.model.QariDirectory
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.Icon3DType
import com.example.ui.components.SectionHeader
import com.example.ui.components.ThreeDimensionalIcon
import com.example.ui.theme.AppAccentTheme
import com.example.ui.theme.AppArabicFont
import com.example.ui.theme.AppCanvasTheme
import com.example.ui.theme.AppTextScale
import com.example.ui.theme.AppUiFont
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DarkEmeraldElevated
import com.example.ui.theme.DarkEmeraldSurface
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.LocalAppColors
import com.example.ui.theme.MintGlow
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.SlateGrayMuted
import com.example.ui.theme.TextPrimary
import com.example.data.util.LocaleHelper
import com.example.data.util.LocaleHelper.findActivity
import com.example.ui.theme.LocalAppLanguage
import com.example.ui.theme.LocalContentLanguage
import com.example.ui.theme.formatLocalizedInt
import com.example.ui.theme.isEnglishUi
import com.example.ui.theme.localizedText
import com.example.ui.theme.uiText
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.MoreUiState
import com.example.ui.viewmodel.MoreViewModel

/**
 * Clean, Apple/Linear-Grade Minimalist Settings & Preferences Hub for Noor-e-Hidayah.
 * High-density clutter has been eliminated in favor of clean grouped containers,
 * unified profile-cloud master card, and focused modal sheets.
 */
@Composable
fun MoreScreen(
    moreViewModel: MoreViewModel,
    authViewModel: AuthViewModel? = null,
    onNavigateToAuth: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by moreViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Dialog & Modal sheet states
    var showDistrictDialog by remember { mutableStateOf(false) }
    var showMazhabDialog by remember { mutableStateOf(false) }
    var showCalcMethodDialog by remember { mutableStateOf(false) }
    var showAccentDialog by remember { mutableStateOf(false) }
    var showCustomColorDialog by remember { mutableStateOf(false) }
    var showGranularColorsDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showTypographyDialog by remember { mutableStateOf(false) }
    var showTextScaleDialog by remember { mutableStateOf(false) }
    var showFastingCautionsDialog by remember { mutableStateOf(false) }
    var showHijriAdjustDialog by remember { mutableStateOf(false) }
    var showQariDialog by remember { mutableStateOf(false) }
    var showStorageDialog by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var showSecurityDialog by remember { mutableStateOf(false) }
    var showWorshipAnalyticsSheet by remember { mutableStateOf(false) }

    val updateManager = remember { InAppUpdateManager(context) }
    val updateState by updateManager.updateState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp) // Generous 20dp breathing space
        ) {
            // Header
            item {
                SectionHeader(
                    title = uiText("সেটিংস ও পছন্দসমূহ", "Settings & Preferences"),
                    subtitle = uiText("প্রোফাইল, থিম, জেলা ও এবাদত সেটিংস", "Profile, theme, location & worship preferences")
                )
            }

            // 1. STREAMLINED UNIFIED TOP PROFILE & CLOUD SYNC CARD
            item {
                val syncToastMsg = uiText("ক্লাউড সিঙ্ক শুরু হয়েছে...", "Cloud sync started...")
                val restoreToastMsg = uiText("ক্লাউড থেকে ডেটা পুনরুদ্ধার শুরু হয়েছে...", "Restoring data from cloud...")
                UnifiedProfileCloudSyncCard(
                    uiState = uiState,
                    authViewModel = authViewModel,
                    onLoginClick = onNavigateToAuth,
                    onLogoutClick = { showLogoutConfirmDialog = true },
                    onManualSyncClick = {
                        authViewModel?.triggerCloudBackup()
                        Toast.makeText(context, syncToastMsg, Toast.LENGTH_SHORT).show()
                    },
                    onRestoreClick = {
                        authViewModel?.triggerCloudRestore()
                        Toast.makeText(context, restoreToastMsg, Toast.LENGTH_SHORT).show()
                    },
                    onOpenMonthlyAnalytics = { showWorshipAnalyticsSheet = true }
                )
            }

            // 2. LOCATION & PRAYER SETUP
            item {
                SettingsSection(
                    title = uiText("অবস্থান ও ইবাদত পদ্ধতি", "Location & Prayer Calculation"),
                    subtitle = uiText("নামাজের ওয়াক্ত ও জেলাভিত্তিক জ্যোতির্বৈজ্ঞানিক হিসাব", "Prayer times & astronomical location setup")
                ) {
                    SettingsNavigationRow(
                        icon = Icons.Default.LocationOn,
                        title = uiText("জেলা ও জিপিএস অবস্থান", "District & GPS Location"),
                        subtitle = if (uiState.isGpsLocation) {
                            uiText("স্বয়ংক্রিয় জিপিএস অবস্থান সক্রিয়", "Auto GPS location active")
                        } else {
                            uiText("স্থানীয় সূর্যোদয় ও সূর্যাস্তের নির্ভুল সময়", "Accurate local sunrise & sunset timings")
                        },
                        trailingValue = if (uiState.isGpsLocation) "${uiState.locationDisplayName} (GPS)" else uiState.selectedDistrictName,
                        onClick = { showDistrictDialog = true }
                    )
                    HorizontalDivider(color = HairlineBorder.copy(alpha = 0.4f), thickness = 0.5.dp)

                    val juristicObj = JuristicMethod.fromString(uiState.juristicMethod)
                    SettingsNavigationRow(
                        icon = Icons.Default.MenuBook,
                        title = uiText("মাযহাব ও ফিকহ পদ্ধতি (আহলুস সুন্নাহ)", "Juristic Method (Asr Shadow)"),
                        subtitle = uiText("হানাফী, শাফেয়ী, মালিকী ও হাম্বলী—চার সহীহ মাযহাবের আসর ওয়াক্ত", "Hanafi, Shafi'i, Maliki & Hanbali Asr rulings"),
                        trailingValue = if (isEnglishUi()) juristicObj.displayName else juristicObj.titleBengali,
                        onClick = { showMazhabDialog = true }
                    )
                    HorizontalDivider(color = HairlineBorder.copy(alpha = 0.4f), thickness = 0.5.dp)

                    val calcName = when (uiState.calculationMethod) {
                        CalculationMethod.KARACHI.name -> uiText("করাচি ইসলামিক বিশ্ববিদ্যালয়", "Univ. of Islamic Sciences, Karachi")
                        CalculationMethod.MUSLIM_WORLD_LEAGUE.name -> uiText("মুসলিম ওয়ার্ল্ড লীগ (MWL)", "Muslim World League (MWL)")
                        CalculationMethod.MAKKAH.name -> uiText("উম্মুল কুরা, মক্কা", "Umm Al-Qura, Makkah")
                        CalculationMethod.ISNA.name -> uiText("উত্তর আমেরিকা ইসলামিক সোসাইটি (ISNA)", "ISNA (North America)")
                        else -> uiText("মুসলিম ওয়ার্ল্ড লীগ", "Muslim World League")
                    }
                    SettingsNavigationRow(
                        icon = Icons.Default.Calculate,
                        title = uiText("নামাজের হিসাব পদ্ধতি", "Prayer Calculation Method"),
                        subtitle = uiText("ফজর ও এশার সূর্যকোণ নির্ধারণ", "Fajr & Isha solar angle convention"),
                        trailingValue = calcName,
                        onClick = { showCalcMethodDialog = true }
                    )
                }
            }

            // 3. APPEARANCE & DISPLAY
            item {
                SettingsSection(
                    title = uiText("দৃশ্যমানতা ও থিম স্টাইল", "Appearance & Display"),
                    subtitle = uiText("গ্লোবাল থিম মোড, ফন্ট ও লিপিশৈলী", "Theme mode, canvas colors, fonts & typography")
                ) {
                    // Global 2-Mode Theme Selector (Segmented Switch)
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiText("গ্লোবাল থিম মোড (Theme Mode)", "Global Theme Mode"),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            )
                            val currentMode = ThemeMode.fromId(uiState.themeMode)
                            Text(
                                text = if (isEnglishUi()) currentMode.titleEnglish else currentMode.titleBengali,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        ThemeModeSegmentedRow(
                            selectedMode = ThemeMode.fromId(uiState.themeMode),
                            onSelect = { selected ->
                                moreViewModel.setThemeMode(selected.id)
                            }
                        )
                    }

                    HorizontalDivider(color = HairlineBorder.copy(alpha = 0.4f), thickness = 0.5.dp)

                    if (ThemeMode.fromId(uiState.themeMode) == ThemeMode.CUSTOM) {
                        val customColor = parseHexColor(uiState.customAccentHex, ChampagneGold)
                        SettingsNavigationRow(
                            icon = Icons.Default.ColorLens,
                            title = uiText("অবাধ কাস্টম কালার পিকার", "Custom Accent Color Picker"),
                            subtitle = uiText("আরজিবি ও হিউ হুইল থেকে যেকোনো প্রিয় রঙ নির্ধারণ করুন", "Choose any custom color via Hue/RGB wheel"),
                            trailingValue = uiState.customAccentHex.uppercase(),
                            trailingColor = customColor,
                            onClick = { showCustomColorDialog = true }
                        )
                        HorizontalDivider(color = HairlineBorder.copy(alpha = 0.4f), thickness = 0.5.dp)
                    } else if (ThemeMode.fromId(uiState.themeMode) == ThemeMode.DARK) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = uiText("ডার্ক ক্যানভাস ব্যাকগ্রাউন্ড", "Dark Canvas Background"),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                )
                                val activeCanvas = AppCanvasTheme.fromId(uiState.canvasTheme)
                                Text(
                                    text = if (isEnglishUi()) activeCanvas.titleEnglish else activeCanvas.titleBengali,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            CanvasThemeSwatchRow(
                                selectedId = uiState.canvasTheme,
                                onSelect = { moreViewModel.setCanvasTheme(it) }
                            )
                        }
                        HorizontalDivider(color = HairlineBorder.copy(alpha = 0.4f), thickness = 0.5.dp)

                        val activeAccent = AppAccentTheme.fromId(uiState.accentTheme)
                        SettingsNavigationRow(
                            icon = Icons.Default.Palette,
                            title = uiText("অ্যাকসেন্ট ও হাইলাইট রঙ", "Accent & Highlight Color"),
                            subtitle = uiText("বাটন, ওয়াক্ত ও সক্রিয় ব্যাজের প্রিয় আভা", "Color tone for buttons, badges & active indicators"),
                            trailingValue = if (isEnglishUi()) activeAccent.titleEnglish else activeAccent.titleBengali,
                            trailingColor = activeAccent.primaryAccent,
                            onClick = { showAccentDialog = true }
                        )
                        HorizontalDivider(color = HairlineBorder.copy(alpha = 0.4f), thickness = 0.5.dp)
                    }

                    val uiLangObj = com.example.data.util.SupportedLanguage.fromCode(uiState.uiLanguage)
                    val contentLangObj = com.example.data.util.SupportedLanguage.fromCode(uiState.contentLanguage)
                    val langLabel = "UI: ${uiLangObj.nativeName} • ${uiText("অর্থ", "Meaning")}: ${contentLangObj.nativeName}"
                    SettingsNavigationRow(
                        icon = Icons.Default.Translate,
                        title = uiText("ভাষা নির্ধারণ", "Language Settings"),
                        subtitle = uiText("অ্যাপ ইউজার ইন্টারফেস ও অনুবাদ ভাষা", "App interface & Quran/Dua translation language"),
                        trailingValue = langLabel,
                        onClick = { showLanguageDialog = true }
                    )

                    HorizontalDivider(color = HairlineBorder.copy(alpha = 0.4f), thickness = 0.5.dp)

                    val uiFontObj = AppUiFont.fromId(uiState.uiFont)
                    val arabicFontObj = AppArabicFont.fromId(uiState.arabicFont)
                    val fontLabel = if (isEnglishUi()) {
                        "${uiFontObj.titleEnglish} • ${arabicFontObj.titleEnglish}"
                    } else {
                        "${uiFontObj.titleBengali} • ${arabicFontObj.titleBengali}"
                    }
                    SettingsNavigationRow(
                        icon = Icons.Default.TextFields,
                        title = uiText("ফন্ট ও আরবি লিপিশৈলী", "Fonts & Arabic Script"),
                        subtitle = uiText("ইউআই ফন্ট ও মুসহাফ ক্যালিগ্রাফি", "UI font family & Mushaf calligraphy"),
                        trailingValue = fontLabel,
                        onClick = { showTypographyDialog = true }
                    )

                    HorizontalDivider(color = HairlineBorder.copy(alpha = 0.4f), thickness = 0.5.dp)

                    val scaleObj = AppTextScale.fromScale(uiState.textScale)
                    SettingsNavigationRow(
                        icon = Icons.Default.FormatSize,
                        title = uiText("টেক্সট সাইজ ও স্কেলিং", "Text Size & Scaling"),
                        subtitle = uiText("স্ক্রিন লেখা পাঠের আকার ও অনুপাত", "Adjust global reading font scale"),
                        trailingValue = if (isEnglishUi()) scaleObj.labelEnglish else scaleObj.labelBengali,
                        onClick = { showTextScaleDialog = true }
                    )

                    HorizontalDivider(color = HairlineBorder.copy(alpha = 0.4f), thickness = 0.5.dp)

                    SettingsNavigationRow(
                        icon = Icons.Default.FormatPaint,
                        title = uiText("উপাদানভিত্তিক কাস্টম কালার ওভাররাইড", "Element Color Overrides"),
                        subtitle = uiText("আরবি হরফ, অর্থ ও উচ্চারণের স্বতন্ত্র রঙ নির্ধারণ", "Independent colors for Arabic, translation & transliteration"),
                        trailingValue = uiText("কাস্টমাইজ", "Customize"),
                        trailingColor = MaterialTheme.colorScheme.primary,
                        onClick = { showGranularColorsDialog = true }
                    )
                }
            }

            // 4. FASTING & NOTIFICATION ALERTS
            item {
                SettingsSection(
                    title = uiText("রমজান, রোজা ও নোটিফিকেশন", "Ramadan, Fasting & Notifications"),
                    subtitle = uiText("সাহুর-ইফতার সতর্কতা বাফার ও নামাজের আযান অ্যালার্ট", "Sahur/Iftar caution buffers & Azan prayer alerts")
                ) {
                    SettingsNavigationRow(
                        icon = Icons.Default.Bedtime,
                        title = uiText("সাহুর ও ইফতার সতর্কতা সময়", "Sahur & Iftar Caution Offsets"),
                        subtitle = uiText("আযানের পূর্ব ও পরের সতর্কতা মিনিট", "Caution minutes before Fajr & after Maghrib"),
                        trailingValue = uiText(
                            "সাহুর: ${uiState.sahurCautionMins} মি. • ইফতার: ${uiState.iftarCautionMins} মি.",
                            "Sahur: ${uiState.sahurCautionMins}m • Iftar: ${uiState.iftarCautionMins}m"
                        ),
                        onClick = { showFastingCautionsDialog = true }
                    )

                    HorizontalDivider(color = HairlineBorder.copy(alpha = 0.4f), thickness = 0.5.dp)

                    SettingsNavigationRow(
                        icon = Icons.Default.DarkMode,
                        title = uiText("হিজরি তারিখ সমন্বয় (চাঁদ দেখা)", "Hijri Date Offset (Moon Sighting)"),
                        subtitle = uiText("স্থানীয় চাঁদ দেখার ওপর ভিত্তি করে সমন্বয়", "Adjust based on local crescent sighting"),
                        trailingValue = when {
                            uiState.hijriOffset > 0 -> uiText("+${uiState.hijriOffset} দিন", "+${uiState.hijriOffset} day(s)")
                            uiState.hijriOffset < 0 -> uiText("${uiState.hijriOffset} দিন", "${uiState.hijriOffset} day(s)")
                            else -> uiText("±০ দিন (স্বাভাবিক)", "±0 days (Standard)")
                        },
                        onClick = { showHijriAdjustDialog = true }
                    )

                    HorizontalDivider(color = HairlineBorder.copy(alpha = 0.4f), thickness = 0.5.dp)

                    SettingsToggleRow(
                        icon = Icons.Default.NotificationsActive,
                        title = uiText("নামাজের ওয়াক্তে আযান অ্যালার্ট", "Prayer Time Azan Alerts"),
                        subtitle = uiText("প্রতি ওয়াক্তের নির্ধারিত সময়ে স্মরণিকা", "Reminder notification at each prayer start time"),
                        checked = uiState.prayerAlertsEnabled,
                        onCheckedChange = { checked ->
                            if (checked &&
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                            ) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                            moreViewModel.togglePrayerAlerts(checked)
                        }
                    )

                    HorizontalDivider(color = HairlineBorder.copy(alpha = 0.4f), thickness = 0.5.dp)

                    SettingsToggleRow(
                        icon = Icons.Default.Schedule,
                        title = uiText(
                            "নামাজের ${uiState.prePrayerAlertMinutes} মিনিট পূর্বে প্রি-এলার্ট",
                            "Pre-Prayer Alert (${uiState.prePrayerAlertMinutes} mins before)"
                        ),
                        subtitle = uiText("নামাজ শুরুর পূর্বেই প্রস্তুতি ও ওযুর সতর্কতা নোটিফিকেশন", "Wudu & preparation reminder before prayer begins"),
                        checked = uiState.prePrayerAlertEnabled,
                        onCheckedChange = { checked ->
                            if (checked &&
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                            ) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                            moreViewModel.togglePrePrayerAlert(checked)
                        }
                    )

                    if (uiState.prePrayerAlertEnabled) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(10, 15, 20, 30).forEach { mins ->
                                val selected = uiState.prePrayerAlertMinutes == mins
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) ChampagneGold.copy(alpha = 0.18f) else DarkEmeraldElevated)
                                        .border(
                                            0.6.dp,
                                            if (selected) ChampagneGold else HairlineBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { moreViewModel.setPrePrayerAlertMinutes(mins) }
                                        .padding(vertical = 7.dp)
                                        .testTag("more_pre_alert_mins_$mins"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = uiText("$mins মি. আগে", "$mins min prior"),
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

                    HorizontalDivider(color = HairlineBorder.copy(alpha = 0.4f), thickness = 0.5.dp)

                    SettingsToggleRow(
                        icon = Icons.Default.AutoStories,
                        title = uiText("দৈনিক আয়াত ও হাদিস নোটিফিকেশন", "Daily Ayah & Hadith Notifications"),
                        subtitle = uiText("প্রতিদিনের অনুপ্রেরণাদায়ী ইসলামিক বাণী", "Daily inspirational Quran & Hadith reminders"),
                        checked = uiState.dailyAyahNotifEnabled,
                        onCheckedChange = { checked ->
                            moreViewModel.setDailyAyahNotif(checked)
                            moreViewModel.setDailyHadithNotif(checked)
                        }
                    )

                    HorizontalDivider(color = HairlineBorder.copy(alpha = 0.4f), thickness = 0.5.dp)

                    val testAzanToast = uiText("টেস্ট আযান অ্যালার্ট পাঠানো হয়েছে", "Test Azan alert sent")
                    val testPreToast = uiText("টেস্ট প্রি-এলার্ট পাঠানো হয়েছে", "Test pre-alert sent")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                moreViewModel.sendTestAlert(PrayerName.FAJR)
                                Toast.makeText(context, testAzanToast, Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(0.6.dp, ChampagneGold.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(uiText("টেস্ট আযান", "Test Azan"), color = ChampagneGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                moreViewModel.sendTestPrePrayerAlert(PrayerName.ASR)
                                Toast.makeText(context, testPreToast, Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(0.6.dp, MintGlow.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = MintGlow,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(uiText("টেস্ট প্রি-এলার্ট", "Test Pre-Alert"), color = MintGlow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 5. OFFLINE AUDIO & QARI
            item {
                SettingsSection(
                    title = uiText("অফলাইন অডিও ও স্টোরেজ", "Offline Audio & Storage"),
                    subtitle = uiText("কুরআন তিলাওয়াতকারী ক্বারী ও ডাউনলোডকৃত ফাইল", "Reciter voice selection & cached audio storage")
                ) {
                    val qariName = QariDirectory.MALE_RECITERS.find { it.id == uiState.selectedQariId }?.name
                        ?: "Sheikh Mishary Rashid Al-Afasy"
                    SettingsNavigationRow(
                        icon = Icons.Default.RecordVoiceOver,
                        title = uiText("প্রিয় তিলাওয়াতকারী ক্বারী", "Preferred Quran Reciter"),
                        subtitle = uiText("কুরআন ও রুকইয়াহর প্রধান সুমধুর কণ্ঠ", "Default Qari voice for Quran & Ruqyah playback"),
                        trailingValue = qariName,
                        onClick = { showQariDialog = true }
                    )

                    HorizontalDivider(color = HairlineBorder.copy(alpha = 0.4f), thickness = 0.5.dp)

                    SettingsNavigationRow(
                        icon = Icons.Default.Storage,
                        title = uiText("অডিও ক্যাশ ও মেমোরি পরিষ্কার", "Audio Cache & Storage"),
                        subtitle = uiText("অপ্রয়োজনীয় ডাউনলোড করা অডিও সরান", "Manage & clear cached audio files"),
                        trailingValue = uiText("ক্যাশ পরিষ্কার", "Clear Cache"),
                        trailingColor = MintGlow,
                        onClick = { showStorageDialog = true }
                    )
                }
            }

            // 6. APP SECURITY & IN-APP UPDATES
            item {
                SettingsSection(
                    title = uiText("অ্যাপ নিরাপত্তা ও আপডেট", "App Security & Updates"),
                    subtitle = uiText("অখণ্ডতা যাচাই, রুট গার্ড ও গুগল প্লে আপডেট", "Integrity verification, root guard & Play updates")
                ) {
                    val securityReport = remember { SecurityGuard.getSecurityReport(context) }
                    SettingsNavigationRow(
                        icon = Icons.Default.Security,
                        title = uiText("অ্যাপ সিকিউরিটি ও ইন্টিগ্রিটি", "App Security & Integrity"),
                        subtitle = if (securityReport.isSecure) {
                            uiText("ডিভাইস সুরক্ষিত ও অফিশিয়াল স্বাক্ষর", "Device secure & official signature verified")
                        } else {
                            uiText("সতর্কতা: রুট/টেস্ট এনভায়রনমেন্ট", "Warning: Rooted/Test environment detected")
                        },
                        trailingValue = if (securityReport.isSecure) uiText("সুরক্ষিত", "Protected") else uiText("সতর্কতা", "Warning"),
                        trailingColor = if (securityReport.isSecure) MintGlow else Color(0xFFFF5252),
                        onClick = { showSecurityDialog = true }
                    )

                    HorizontalDivider(color = HairlineBorder.copy(alpha = 0.4f), thickness = 0.5.dp)

                    val updateLabel = when (val state = updateState) {
                        is InAppUpdateManager.UpdateUiState.Checking -> uiText("পরীক্ষা হচ্ছে...", "Checking...")
                        is InAppUpdateManager.UpdateUiState.AvailableGitHub -> uiText(
                            "আপডেট (v${state.info.versionName})",
                            "Update (v${state.info.versionName})"
                        )
                        is InAppUpdateManager.UpdateUiState.Available -> uiText("নতুন আপডেট!", "Update Available!")
                        is InAppUpdateManager.UpdateUiState.Downloaded -> uiText("ইনস্টল করুন", "Install Update")
                        else -> uiText("সর্বশেষ সংস্করণ", "Up to Date")
                    }
                    val availToast = uiText("নতুন আপডেট পাওয়া গেছে! ডাউনলোড শুরু হচ্ছে...", "Update available! Starting download...")
                    val latestToast = uiText("আপনি অ্যাপের সর্বশেষ সংস্করণ ব্যবহার করছেন।", "You are using the latest version of the app.")
                    SettingsNavigationRow(
                        icon = Icons.Default.SystemUpdate,
                        title = uiText("নতুন আপডেট পরীক্ষা করুন", "Check for App Updates"),
                        subtitle = uiText("স্বয়ংক্রিয় ভার্সন ও বাগ-ফিক্স আপডেট", "Automatic version & bug-fix updates"),
                        trailingValue = updateLabel,
                        trailingColor = if (
                            updateState is InAppUpdateManager.UpdateUiState.Available ||
                            updateState is InAppUpdateManager.UpdateUiState.AvailableGitHub
                        ) ChampagneGold else SlateGrayMuted,
                        onClick = {
                            when (val state = updateState) {
                                is InAppUpdateManager.UpdateUiState.Downloaded -> {
                                    updateManager.completeUpdate()
                                }
                                is InAppUpdateManager.UpdateUiState.AvailableGitHub -> {
                                    InAppUpdateManager.downloadApkDirectly(
                                        context = context,
                                        downloadUrl = state.info.apkDownloadUrl,
                                        versionName = state.info.versionName
                                    )
                                }
                                else -> {
                                    coroutineScope.launch {
                                        val ghUpdate = updateManager.checkGitHubUpdates(notifyUser = true)
                                        if (ghUpdate != null) {
                                            Toast.makeText(context, availToast, Toast.LENGTH_SHORT).show()
                                            InAppUpdateManager.downloadApkDirectly(
                                                context = context,
                                                downloadUrl = ghUpdate.apkDownloadUrl,
                                                versionName = ghUpdate.versionName
                                            )
                                        } else {
                                            updateManager.checkForUpdates { hasUpdate ->
                                                val msg = if (hasUpdate) availToast else latestToast
                                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }

            // 7. POLISHED MINIMALIST BRAND FOOTER
            item {
                MinimalBrandFooter(
                    onSupportClick = { showSupportDialog = true },
                    onFeedbackClick = { showFeedbackDialog = true }
                )
            }
        }

        // ================= DIALOGS & MODAL SHEETS =================

        // 1. Bangladesh 64 District & GPS Auto-Detect Dialog
        if (showDistrictDialog) {
            BangladeshDistrictSelectorDialog(
                currentDistrictId = uiState.selectedDistrictId,
                isDetectingLocation = uiState.isDetectingLocation,
                onAutoDetectLocation = {
                    moreViewModel.autoDetectCurrentLocation { success, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        if (success) {
                            showDistrictDialog = false
                        }
                    }
                },
                onSelectDistrict = { district ->
                    moreViewModel.setDistrict(district)
                    showDistrictDialog = false
                },
                onDismiss = { showDistrictDialog = false }
            )
        }

        // 2. Mazhab / Asr Shadow Method Dialog
        if (showMazhabDialog) {
            MazhabSelectorDialog(
                currentMethod = uiState.juristicMethod,
                onSelect = { method ->
                    moreViewModel.setJuristicMethod(method)
                    showMazhabDialog = false
                },
                onDismiss = { showMazhabDialog = false }
            )
        }

        // 3. Calculation Method Dialog
        if (showCalcMethodDialog) {
            CalculationMethodDialog(
                currentMethod = uiState.calculationMethod,
                onSelect = { method ->
                    moreViewModel.setCalculationMethod(method)
                    showCalcMethodDialog = false
                },
                onDismiss = { showCalcMethodDialog = false }
            )
        }

        // 4. Accent Theme Dialog
        if (showAccentDialog) {
            AccentSelectorDialog(
                currentAccent = uiState.accentTheme,
                onSelect = { accentId ->
                    moreViewModel.setAccentTheme(accentId)
                    showAccentDialog = false
                },
                onDismiss = { showAccentDialog = false }
            )
        }

        // 4b. Unrestricted Custom Color Picker Dialog
        if (showCustomColorDialog) {
            UnrestrictedColorPickerDialog(
                initialHex = uiState.customAccentHex,
                onColorSelected = { hex ->
                    moreViewModel.setCustomAccentHex(hex)
                    showCustomColorDialog = false
                },
                onDismiss = { showCustomColorDialog = false }
            )
        }

        // 4c. Granular Element Color Overrides Dialog
        if (showGranularColorsDialog) {
            GranularColorOverridesDialog(
                currentArabicHex = uiState.customArabicColor,
                currentTranslationHex = uiState.customTranslationColor,
                currentTransliterationHex = uiState.customTransliterationColor,
                onSave = { arabic, translation, transliteration ->
                    moreViewModel.setCustomArabicColor(arabic)
                    moreViewModel.setCustomTranslationColor(translation)
                    moreViewModel.setCustomTransliterationColor(transliteration)
                    showGranularColorsDialog = false
                },
                onDismiss = { showGranularColorsDialog = false }
            )
        }

        // 5. Language Selector Dialog
        if (showLanguageDialog) {
            val activity = context.findActivity()
            LanguageSelectorDialog(
                currentUiLang = uiState.uiLanguage,
                currentContentLang = uiState.contentLanguage,
                onSelect = { ui, content ->
                    val prevUi = uiState.uiLanguage
                    moreViewModel.setUiLanguage(ui)
                    moreViewModel.setContentLanguage(content)
                    showLanguageDialog = false
                    LocaleHelper.applyLanguageSelection(
                        activity = activity,
                        context = context,
                        uiLang = ui,
                        contentLang = content,
                        recreateIfUiChanged = (prevUi != ui)
                    )
                    val uiObj = com.example.data.util.SupportedLanguage.fromCode(ui)
                    val savedMsg = localizedText(
                        ui,
                        "ভাষা সেটিংস সংরক্ষিত হয়েছে (UI: ${uiObj.nativeName})",
                        "Language preferences saved (UI: ${uiObj.nativeName})"
                    )
                    Toast.makeText(context, savedMsg, Toast.LENGTH_SHORT).show()
                },
                onDismiss = { showLanguageDialog = false }
            )
        }

        // 6. Typography & Script Dialog
        if (showTypographyDialog) {
            TypographyScriptDialog(
                currentUiFont = uiState.uiFont,
                currentArabicFont = uiState.arabicFont,
                onSelectUiFont = { moreViewModel.setUiFont(it) },
                onSelectArabicFont = { moreViewModel.setArabicFont(it) },
                onDismiss = { showTypographyDialog = false }
            )
        }

        // 7. Text Scaling Presets Dialog
        if (showTextScaleDialog) {
            TextScaleDialog(
                currentScale = uiState.textScale,
                onSelect = { scale ->
                    moreViewModel.setTextScale(scale)
                    showTextScaleDialog = false
                },
                onDismiss = { showTextScaleDialog = false }
            )
        }

        // 8. Fasting Cautions Stepper Dialog
        if (showFastingCautionsDialog) {
            FastingCautionsDialog(
                sahurMins = uiState.sahurCautionMins,
                iftarMins = uiState.iftarCautionMins,
                onSave = { s, i ->
                    moreViewModel.setSahurCautionMins(s)
                    moreViewModel.setIftarCautionMins(i)
                    showFastingCautionsDialog = false
                },
                onDismiss = { showFastingCautionsDialog = false }
            )
        }

        // Hijri Moon Sighting Adjustment Dialog
        if (showHijriAdjustDialog) {
            val hijriSavedToast = uiText("হিজরি চাঁদ সমন্বয় সংরক্ষিত হয়েছে", "Hijri moon sighting offset saved")
            HijriMoonSightingAdjustDialog(
                currentOffset = uiState.hijriOffset,
                onSave = { offset ->
                    moreViewModel.setHijriOffset(offset)
                    showHijriAdjustDialog = false
                    Toast.makeText(context, hijriSavedToast, Toast.LENGTH_SHORT).show()
                },
                onDismiss = { showHijriAdjustDialog = false }
            )
        }

        // 9. Qari Reciter Dialog
        if (showQariDialog) {
            QariSelectorDialog(
                selectedId = uiState.selectedQariId,
                onSelect = { qariId ->
                    moreViewModel.setQari(qariId)
                    showQariDialog = false
                },
                onDismiss = { showQariDialog = false }
            )
        }

        // 10. Storage & Audio Cache Dialog
        if (showStorageDialog) {
            val cacheClearedToast = uiText("অডিও ক্যাশ সফলভাবে পরিষ্কার করা হয়েছে", "Audio cache cleared successfully")
            AudioStorageDialog(
                onClearCache = { includeDownloads ->
                    com.example.data.audio.OfflineAudioManager.clearAllAudioCache(
                        context = context,
                        includeOfflineDownloads = includeDownloads
                    )
                    Toast.makeText(context, cacheClearedToast, Toast.LENGTH_SHORT).show()
                },
                onDismiss = { showStorageDialog = false }
            )
        }

        // 11. Support Dialog
        if (showSupportDialog) {
            SupportDialog(onDismiss = { showSupportDialog = false })
        }

        // 12. Feedback Dialog
        if (showFeedbackDialog) {
            FeedbackDialog(onDismiss = { showFeedbackDialog = false })
        }

        // 13. Security Report Dialog
        if (showSecurityDialog) {
            SecurityReportDialog(onDismiss = { showSecurityDialog = false })
        }

        // 14. Logout Confirmation Dialog
        if (showLogoutConfirmDialog) {
            val loggedOutToast = uiText("সফলভাবে লগআউট হয়েছে (অতিথি মোড)", "Signed out successfully (Guest Mode)")
            AlertDialog(
                onDismissRequest = { showLogoutConfirmDialog = false },
                title = {
                    Text(
                        text = uiText("লগ আউট করতে চান?", "Sign Out?"),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                text = {
                    Text(
                        text = uiText(
                            "লগ আউট করলেও আপনার স্থানীয় বুকমার্ক ও সংরক্ষিত তথ্য এই ডিভাইসে সুরক্ষিত থাকবে। আপনি যেকোনো সময় পুনরায় লগইন করে ক্লাউড ব্যাকআপ চালু করতে পারবেন।",
                            "Your local bookmarks and data will remain safely stored on this device. You can sign back in anytime to resume cloud backup."
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, lineHeight = 18.sp)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutConfirmDialog = false
                            authViewModel?.signOut()
                            Toast.makeText(context, loggedOutToast, Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))
                    ) {
                        Text(uiText("লগ আউট করুন", "Sign Out"), color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutConfirmDialog = false }) {
                        Text(uiText("বাতিল", "Cancel"), color = SlateGrayMuted)
                    }
                },
                containerColor = DarkEmeraldSurface,
                shape = RoundedCornerShape(16.dp)
            )
        }

        if (showWorshipAnalyticsSheet) {
            com.example.ui.components.WorshipHistoryAnalyticsBottomSheet(
                monthlyHistory = uiState.monthlyHistory,
                currentQuranMinutes = uiState.quranReadMinutes,
                currentDuaCount = uiState.duasReadCount,
                currentTasbihCount = uiState.totalTasbihCount,
                currentDuroodCount = uiState.salawatCount,
                currentHadithMinutes = uiState.hadithReadMinutes,
                onDismiss = { showWorshipAnalyticsSheet = false }
            )
        }
    }
}

// ================= COMPONENT SPECIFICATIONS =================

/**
 * 1. STREAMLINED UNIFIED TOP PROFILE & CLOUD SYNC CARD
 */
@Composable
fun UnifiedProfileCloudSyncCard(
    uiState: MoreUiState,
    authViewModel: AuthViewModel?,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onManualSyncClick: () -> Unit,
    onRestoreClick: () -> Unit = {},
    onOpenMonthlyAnalytics: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isLoggedIn = authViewModel?.isUserLoggedIn?.collectAsState()?.value ?: false
    val userName = authViewModel?.currentUserName?.collectAsState()?.value
        ?: com.example.data.preference.AppPreferences.DEFAULT_USER_NAME
    val userEmail = authViewModel?.currentUserEmail?.collectAsState()?.value ?: ""
    val userPhone = authViewModel?.currentUserPhone?.collectAsState()?.value ?: ""
    val authProvider = authViewModel?.currentAuthProvider?.collectAsState()?.value ?: "GUEST"
    val photoUrl = authViewModel?.currentUserPhotoUrl?.collectAsState()?.value
    val syncStatus = authViewModel?.syncStatus?.collectAsState()?.value ?: SyncStatus.Idle
    val isSyncing = syncStatus is SyncStatus.Syncing

    val photoSavedMsg = uiText("প্রোফাইল ছবি আপডেট হয়েছে", "Profile photo updated")
    val photoErrorMsg = uiText("ছবি প্রসেস করা যায়নি। অনুগ্রহ করে অন্য ছবি বেছে নিন", "Unable to process selected image")

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { pickedUri ->
        if (pickedUri != null) {
            scope.launch {
                val dataUri = encodeAvatarDataUri(context, pickedUri)
                if (dataUri != null) {
                    authViewModel?.updateProfilePhoto(dataUri)
                    Toast.makeText(context, photoSavedMsg, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, photoErrorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val decodedAvatarModel = remember(photoUrl) {
        decodeAvatarModel(photoUrl)
    }

    FrostedGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("unified_profile_cloud_card")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Main Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Avatar with Champagne Gold Ring, Upload Photo Picker & Sync Indicator
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clickable(
                                onClickLabel = uiText("প্রোফাইল ছবি পরিবর্তন", "Change Profile Photo")
                            ) {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .testTag("edit_profile_photo_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(DarkEmeraldElevated)
                                .border(1.8.dp, ChampagneGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (decodedAvatarModel != null) {
                                SubcomposeAsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(decodedAvatarModel)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = uiText("প্রোফাইল ছবি পরিবর্তন", "Change Profile Photo"),
                                    contentScale = ContentScale.Crop,
                                    error = {
                                        val initial = userName.firstOrNull()?.uppercaseChar()?.toString() ?: "ন"
                                        Text(
                                            text = initial,
                                            color = ChampagneGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .testTag("user_avatar_image")
                                )
                            } else if (isLoggedIn && userName.isNotBlank()) {
                                val initial = userName.first().uppercaseChar().toString()
                                Text(
                                    text = initial,
                                    color = ChampagneGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            } else {
                                ThreeDimensionalIcon(
                                    type = Icon3DType.MOSQUE_HOME,
                                    size = 30.dp,
                                    isActive = true,
                                    accentColor = ChampagneGold
                                )
                            }
                        }

                        // Camera edit badge
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(if (isLoggedIn) ChampagneGold else DarkEmeraldElevated)
                                .border(1.2.dp, DarkEmeraldSurface, CircleShape)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = uiText("প্রোফাইল ছবি পরিবর্তন", "Change Profile Photo"),
                                tint = if (isLoggedIn) ObsidianBackground else ChampagneGold,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }

                    // User Identity & Sync Badge
                    Column {
                        Text(
                            text = if (isLoggedIn) {
                                when {
                                    authProvider == "PHONE" -> userName.ifBlank { userPhone.ifBlank { uiText("মুসাফির", "Traveler") } }
                                    else -> userName.ifBlank { userEmail.substringBefore("@").ifBlank { uiText("সম্মানিত মুমিন", "Valued Believer") } }
                                }
                            } else {
                                uiText("অতিথি মোড (Guest)", "Guest Mode")
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = if (isLoggedIn) {
                                when {
                                    authProvider == "PHONE" -> userPhone.ifBlank { uiText("ফোন ভেরিফাইড", "Phone Verified") }
                                    userEmail.isNotBlank() -> userEmail
                                    else -> uiText("গুগল একাউন্ট সংযুক্ত", "Google Account Linked")
                                }
                            } else {
                                uiText("লগইন করে ক্লাউড ব্যাকআপ চালু করুন", "Sign in to enable cloud backup")
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateGrayMuted,
                                fontSize = 11.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // Compact Live Sync Status Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            when (syncStatus) {
                                SyncStatus.Syncing -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(10.dp),
                                        color = ChampagneGold,
                                        strokeWidth = 1.5.dp
                                    )
                                    Text(
                                        text = uiText("সিঙ্ক হচ্ছে...", "Syncing..."),
                                        color = ChampagneGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                is SyncStatus.Error -> {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFF8A80))
                                    )
                                    Text(
                                        text = syncStatus.message,
                                        color = Color(0xFFFF8A80),
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                else -> {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(if (isLoggedIn) MintGlow else SlateGrayMuted)
                                    )
                                    Text(
                                        text = if (isLoggedIn) uiText("✓ লাইভ অটো-সিঙ্ক সক্রিয়", "✓ Live Auto-Sync Active") else uiText("অফলাইন মোড", "Offline Mode"),
                                        color = if (isLoggedIn) MintGlow else SlateGrayMuted,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // Trailing Actions (Backup, Restore & Logout/Login)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isLoggedIn) {
                        IconButton(
                            onClick = onManualSyncClick,
                            enabled = !isSyncing,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("quick_manual_sync_button")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkEmeraldElevated)
                                    .border(0.5.dp, ChampagneGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = uiText("ব্যাকআপ ও সিঙ্ক", "Backup & Sync"),
                                    tint = ChampagneGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = onRestoreClick,
                            enabled = !isSyncing,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("quick_cloud_restore_button")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkEmeraldElevated)
                                    .border(0.5.dp, MintGlow.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDownload,
                                    contentDescription = uiText("ক্লাউড থেকে রিস্টোর", "Restore from Cloud"),
                                    tint = MintGlow,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = onLogoutClick,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("logout_icon_button")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkEmeraldElevated)
                                    .border(0.5.dp, Color(0xFFE57373).copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = uiText("লগ আউট করুন", "Sign Out"),
                                    tint = Color(0xFFFF8A80),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = onLoginClick,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ChampagneGold,
                                contentColor = ObsidianBackground
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("login_action_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Login,
                                contentDescription = uiText("লগইন", "Sign In"),
                                tint = ObsidianBackground,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = uiText("লগইন", "Sign In"),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ObsidianBackground
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = HairlineBorder.copy(alpha = 0.4f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(10.dp))

            val uiLang = LocalAppLanguage.current
            // 5 Live Real Worship Counters Strip (Zero-initialized for new users)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FlatStatCounter(
                    icon = "📖",
                    count = if (com.example.data.util.SupportedLanguage.normalizeCode(uiLang) == "bn") {
                        "${formatLocalizedInt(uiState.quranReadMinutes, uiLang)} মি."
                    } else {
                        "${uiState.quranReadMinutes}m"
                    },
                    label = uiText("কুরআন", "Quran")
                )
                FlatStatCounter(
                    icon = "🤲",
                    count = formatLocalizedInt(uiState.duasReadCount, uiLang),
                    label = uiText("দু'আ", "Duas")
                )
                FlatStatCounter(
                    icon = "📿",
                    count = formatLocalizedInt(uiState.totalTasbihCount, uiLang),
                    label = uiText("তাসবীহ", "Tasbih")
                )
                FlatStatCounter(
                    icon = "💚",
                    count = formatLocalizedInt(uiState.salawatCount, uiLang),
                    label = uiText("দরূদ", "Durood")
                )
                FlatStatCounter(
                    icon = "📚",
                    count = if (com.example.data.util.SupportedLanguage.normalizeCode(uiLang) == "bn") {
                        "${formatLocalizedInt(uiState.hadithReadMinutes, uiLang)} মি."
                    } else {
                        "${uiState.hadithReadMinutes}m"
                    },
                    label = uiText("হাদিস", "Hadith")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 12-Month Worship Analytics & Archive CTA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkEmeraldElevated)
                    .border(0.5.dp, ChampagneGold.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                    .clickable { onOpenMonthlyAnalytics() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("more_open_12month_analytics"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiText("📊 ১২ মাসের সকল ইবাদতের ইতিহাস ও চার্ট", "📊 12-Month Worship History & Charts"),
                    color = ChampagneGold,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = uiText("১২ মাসের চার্ট খুলুন", "Open 12-month charts"),
                    tint = ChampagneGold,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

private fun decodeAvatarModel(photoUrl: String?): Any? {
    if (photoUrl.isNullOrBlank()) return null
    if (photoUrl.startsWith("data:image", ignoreCase = true)) {
        val base64Part = photoUrl.substringAfter("base64,", "")
        if (base64Part.isBlank()) return null
        return try {
            android.util.Base64.decode(base64Part, android.util.Base64.DEFAULT)
        } catch (_: Exception) {
            null
        }
    }
    return photoUrl
}

private suspend fun encodeAvatarDataUri(context: android.content.Context, uri: Uri): String? = withContext(Dispatchers.IO) {
    try {
        val boundsOpts = android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { stream ->
            android.graphics.BitmapFactory.decodeStream(stream, null, boundsOpts)
        }
        val maxDim = maxOf(boundsOpts.outWidth, boundsOpts.outHeight)
        var sampleSize = 1
        while (maxDim / sampleSize > 512) {
            sampleSize *= 2
        }

        val decodeOpts = android.graphics.BitmapFactory.Options().apply { inSampleSize = sampleSize }
        val decoded = context.contentResolver.openInputStream(uri)?.use { stream ->
            android.graphics.BitmapFactory.decodeStream(stream, null, decodeOpts)
        } ?: return@withContext null

        val targetMax = 256
        val ratio = minOf(
            targetMax.toFloat() / decoded.width.coerceAtLeast(1),
            targetMax.toFloat() / decoded.height.coerceAtLeast(1),
            1.0f
        )
        val scaledWidth = (decoded.width * ratio).toInt().coerceAtLeast(1)
        val scaledHeight = (decoded.height * ratio).toInt().coerceAtLeast(1)
        val scaled = if (scaledWidth != decoded.width || scaledHeight != decoded.height) {
            android.graphics.Bitmap.createScaledBitmap(decoded, scaledWidth, scaledHeight, true)
        } else {
            decoded
        }

        val baos = java.io.ByteArrayOutputStream()
        scaled.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, baos)
        if (scaled !== decoded) decoded.recycle()
        scaled.recycle()

        val bytes = baos.toByteArray()
        val encoded = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
        val dataUri = "data:image/jpeg;base64,$encoded"
        if (dataUri.length <= 150_000) dataUri else null
    } catch (_: Exception) {
        null
    }
}

@Composable
private fun FlatStatCounter(icon: String, count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(text = icon, fontSize = 12.sp)
            Text(
                text = count,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = SlateGrayMuted,
                fontSize = 10.sp
            )
        )
    }
}

/**
 * 2. GROUPED SETTINGS SECTION CONTAINER
 */
@Composable
fun SettingsSection(
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = ChampagneGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SlateGrayMuted,
                        fontSize = 11.sp
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkEmeraldSurface)
                .border(0.5.dp, HairlineBorder, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

/**
 * 3. CLEAN SETTINGS NAVIGATION ROW (iOS / Linear Grade)
 */
@Composable
fun SettingsNavigationRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    trailingValue: String? = null,
    trailingColor: Color = ChampagneGold,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkEmeraldElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(16.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateGrayMuted,
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (trailingValue != null) {
                Text(
                    text = trailingValue,
                    color = trailingColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = SlateGrayMuted.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * 4. CLEAN SETTINGS TOGGLE ROW
 */
@Composable
fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkEmeraldElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(16.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateGrayMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = ObsidianBackground,
                checkedTrackColor = ChampagneGold,
                uncheckedThumbColor = SlateGrayMuted,
                uncheckedTrackColor = DarkEmeraldElevated
            )
        )
    }
}

/**
 * 4B. GLOBAL 2-MODE THEME SEGMENTED ROW (Dark, Custom)
 */
@Composable
fun ThemeModeSegmentedRow(
    selectedMode: ThemeMode,
    onSelect: (ThemeMode) -> Unit
) {
    val modes = listOf(
        Triple(ThemeMode.DARK, uiText("ডার্ক মোড", "Dark Mode"), Icons.Default.DarkMode),
        Triple(ThemeMode.CUSTOM, uiText("কাস্টম রঙ", "Custom Color"), Icons.Default.Palette)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        modes.forEach { (mode, label, icon) ->
            val isSelected = selectedMode == mode
            val activeBg = MaterialTheme.colorScheme.primary
            val activeContent = MaterialTheme.colorScheme.onPrimary
            val inactiveContent = MaterialTheme.colorScheme.onSurfaceVariant

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) activeBg else Color.Transparent)
                    .clickable { onSelect(mode) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = if (isSelected) activeContent else inactiveContent
                    )
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) activeContent else inactiveContent
                    )
                }
            }
        }
    }
}

/**
 * 5. HORIZONTAL SWATCH ROW FOR CANVAS THEMES
 */
@Composable
fun CanvasThemeSwatchRow(
    selectedId: String,
    onSelect: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(AppCanvasTheme.entries) { theme ->
            val isSelected = selectedId.equals(theme.id, ignoreCase = true)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) DarkEmeraldElevated else DarkEmeraldSurface)
                    .border(
                        width = if (isSelected) 1.5.dp else 0.5.dp,
                        color = if (isSelected) ChampagneGold else HairlineBorder,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelect(theme.id) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(theme.background)
                            .border(0.5.dp, HairlineBorder, CircleShape)
                    )
                    Text(
                        text = if (isEnglishUi()) theme.titleEnglish else theme.titleBengali,
                        color = if (isSelected) ChampagneGold else TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = MintGlow,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 6. POLISHED MINIMALIST BRAND FOOTER
 */
@Composable
fun MinimalBrandFooter(
    onSupportClick: () -> Unit,
    onFeedbackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ThreeDimensionalIcon(
            type = Icon3DType.MOSQUE_HOME,
            size = 36.dp,
            isActive = true,
            accentColor = ChampagneGold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = uiText("নূর-এ-হেদায়াত • সংস্করণ ১.০ এলিট প্রো", "Noor-e-Hidayah • Version 1.0 Elite Pro"),
            style = MaterialTheme.typography.titleSmall.copy(
                color = ChampagneGold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        )
        Text(
            text = uiText("শতভাগ বিজ্ঞাপনমুক্ত ও সাদাকাহ জারিয়াহ হিসেবে পরিচালিত", "100% Ad-Free & Dedicated as Sadaqah Jariyah"),
            style = MaterialTheme.typography.bodySmall.copy(
                color = SlateGrayMuted,
                fontSize = 10.sp
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = uiText("যোগাযোগ ও সাপোর্ট", "Contact & Support"),
                color = ChampagneGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onSupportClick() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            Box(modifier = Modifier.size(3.dp).clip(CircleShape).background(SlateGrayMuted))

            Text(
                text = uiText("ভুলত্রুটি পরামর্শ", "Feedback & Corrections"),
                color = TextPrimary.copy(alpha = 0.8f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onFeedbackClick() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

// ================= MODAL DIALOGS =================

/**
 * AUTHENTIC MAZHAB & JURISPRUDENCE (আহলুস সুন্নাহ ওয়াল জামা'আতের ৪ সহীহ মাযহাব)
 * Strictly features: Hanafi, Shafi'i, Maliki, Hanbali.
 */
@Composable
fun MazhabSelectorDialog(
    currentMethod: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = uiText("মাযহাব ও ফিকহ পদ্ধতি নির্বাচন", "Select Juristic Method (Madhhab)"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = uiText("কুরআন ও সহীহ সুন্নাহ ভিত্তিক ৪টি গ্রহণযোগ্য মাযহাব (আহলুস সুন্নাহ)", "4 authentic Sunni schools for Asr shadow calculation"),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val currentObj = JuristicMethod.fromString(currentMethod)
                JuristicMethod.entries.forEach { method ->
                    val isSelected = currentObj == method
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
                            .border(
                                width = if (isSelected) 1.5.dp else 0.5.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onSelect(method.name) }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = if (isEnglishUi()) method.displayName else method.titleBengali,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = uiText("${method.shadowFactor.toInt()}x ছায়া", "${method.shadowFactor.toInt()}x Shadow"),
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isEnglishUi()) {
                                    "Asr starts when object shadow is ${method.shadowFactor.toInt()}x its height"
                                } else {
                                    method.descriptionBengali
                                },
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                        if (isSelected) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(uiText("ঠিক আছে", "OK"), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * CALCULATION METHOD DIALOG
 */
@Composable
fun CalculationMethodDialog(
    currentMethod: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = uiText("নামাজের হিসাব পদ্ধতি", "Prayer Calculation Method"),
                style = MaterialTheme.typography.titleMedium.copy(color = ChampagneGold, fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val methods = listOf(
                    Pair(CalculationMethod.MUSLIM_WORLD_LEAGUE.name, uiText("মুসলিম ওয়ার্ল্ড লীগ (MWL)", "Muslim World League (MWL)")),
                    Pair(CalculationMethod.KARACHI.name, uiText("করাচি ইসলামিক বিশ্ববিদ্যালয়", "Univ. of Islamic Sciences, Karachi")),
                    Pair(CalculationMethod.MAKKAH.name, uiText("উম্মুল কুরা বিশ্ববিদ্যালয়, মক্কা", "Umm Al-Qura University, Makkah")),
                    Pair(CalculationMethod.ISNA.name, uiText("উত্তর আমেরিকা ইসলামিক সোসাইটি (ISNA)", "Islamic Society of North America (ISNA)"))
                )
                methods.forEach { (id, label) ->
                    val isSelected = currentMethod == id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) DarkEmeraldElevated else DarkEmeraldSurface)
                            .border(1.dp, if (isSelected) ChampagneGold else HairlineBorder, RoundedCornerShape(10.dp))
                            .clickable { onSelect(id) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) ChampagneGold else TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MintGlow, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(uiText("ঠিক আছে", "OK"), color = ChampagneGold) }
        },
        containerColor = DarkEmeraldSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * ACCENT COLOR PICKER DIALOG
 */
@Composable
fun AccentSelectorDialog(
    currentAccent: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = uiText("অ্যাকসেন্ট ও হাইলাইট রঙ", "Accent & Highlight Color"),
                style = MaterialTheme.typography.titleMedium.copy(color = ChampagneGold, fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppAccentTheme.entries.forEach { accent ->
                    val isSelected = currentAccent.equals(accent.id, ignoreCase = true)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) DarkEmeraldElevated else DarkEmeraldSurface)
                            .border(1.dp, if (isSelected) accent.primaryAccent else HairlineBorder, RoundedCornerShape(10.dp))
                            .clickable { onSelect(accent.id) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(accent.primaryAccent)
                            )
                            Text(
                                text = if (isEnglishUi()) accent.titleEnglish else accent.titleBengali,
                                color = if (isSelected) accent.primaryAccent else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = accent.primaryAccent, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(uiText("ঠিক আছে", "OK"), color = ChampagneGold) }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * UNRESTRICTED CUSTOM COLOR PICKER DIALOG
 * Provides full Hue, Saturation, and Value controls plus Hex code input.
 */
@Composable
fun UnrestrictedColorPickerDialog(
    initialHex: String,
    onColorSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var hexText by remember { mutableStateOf(initialHex.removePrefix("#").uppercase()) }
    var hue by remember {
        val hsv = FloatArray(3)
        val parsed = parseHexColor(initialHex, ChampagneGold)
        android.graphics.Color.colorToHSV(parsed.toArgb(), hsv)
        mutableStateOf(hsv[0]) // 0 to 360
    }
    var saturation by remember {
        val hsv = FloatArray(3)
        val parsed = parseHexColor(initialHex, ChampagneGold)
        android.graphics.Color.colorToHSV(parsed.toArgb(), hsv)
        mutableStateOf(hsv[1]) // 0 to 1
    }
    var value by remember {
        val hsv = FloatArray(3)
        val parsed = parseHexColor(initialHex, ChampagneGold)
        android.graphics.Color.colorToHSV(parsed.toArgb(), hsv)
        mutableStateOf(hsv[2]) // 0 to 1
    }

    // Current derived color from HSV
    val currentColor = remember(hue, saturation, value) {
        val hsv = floatArrayOf(hue, saturation, value)
        Color(android.graphics.Color.HSVToColor(hsv))
    }

    val presetPalette = listOf(
        "#00E676", "#00B0FF", "#E5C158", "#FF5252",
        "#FF9100", "#D500F9", "#00E5FF", "#76FF03",
        "#FFD700", "#1DE9B6", "#FF4081", "#651FFF"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = currentColor
                )
                Text(
                    text = uiText("অবাধ কাস্টম কালার পিকার", "Custom Color Picker"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Color Preview Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(currentColor)
                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#${currentColor.toHex().removePrefix("#")}",
                        color = if (value > 0.6f && saturation < 0.6f) Color.Black else Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                }

                // Hue Slider (0° to 360°)
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uiText("হিউ স্পেকট্রাম (Hue)", "Hue Spectrum"),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${hue.toInt()}°",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = hue,
                        onValueChange = {
                            hue = it
                            val hsv = floatArrayOf(hue, saturation, value)
                            hexText = Color(android.graphics.Color.HSVToColor(hsv)).toHex().removePrefix("#")
                        },
                        valueRange = 0f..360f,
                        colors = SliderDefaults.colors(
                            thumbColor = currentColor,
                            activeTrackColor = currentColor
                        )
                    )
                }

                // Saturation Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uiText("স্যাচুরেশন (Saturation)", "Saturation"),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${(saturation * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = saturation,
                        onValueChange = {
                            saturation = it
                            val hsv = floatArrayOf(hue, saturation, value)
                            hexText = Color(android.graphics.Color.HSVToColor(hsv)).toHex().removePrefix("#")
                        },
                        valueRange = 0.1f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = currentColor,
                            activeTrackColor = currentColor
                        )
                    )
                }

                // Brightness / Value Slider (উজ্জ্বলতা)
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uiText("উজ্জ্বলতা (Brightness)", "Brightness"),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${(value * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = value,
                        onValueChange = {
                            value = it
                            val hsv = floatArrayOf(hue, saturation, value)
                            hexText = Color(android.graphics.Color.HSVToColor(hsv)).toHex().removePrefix("#")
                        },
                        valueRange = 0.2f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = currentColor,
                            activeTrackColor = currentColor
                        )
                    )
                }

                // Quick Preset Swatches
                Column {
                    Text(
                        text = uiText("জনপ্রিয় কুইক সোয়াচ", "Quick Presets"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(presetPalette) { hex ->
                            val color = parseHexColor(hex)
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (currentColor.toHex().equals(hex, ignoreCase = true)) 2.dp else 0.5.dp,
                                        color = if (currentColor.toHex().equals(hex, ignoreCase = true)) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        val hsv = FloatArray(3)
                                        android.graphics.Color.colorToHSV(color.toArgb(), hsv)
                                        hue = hsv[0]
                                        saturation = hsv[1]
                                        value = hsv[2]
                                        hexText = hex.removePrefix("#")
                                    }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onColorSelected("#${currentColor.toHex().removePrefix("#")}")
                },
                colors = ButtonDefaults.buttonColors(containerColor = currentColor)
            ) {
                Text(
                    text = uiText("প্রয়োগ করুন", "Apply"),
                    color = if (value > 0.6f && saturation < 0.6f) Color.Black else Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(uiText("বাতিল", "Cancel"), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * GRANULAR COLOR OVERRIDES DIALOG
 * Allows independently customizing Arabic text, Bengali translation, and pronunciation colors.
 */
@Composable
fun GranularColorOverridesDialog(
    currentArabicHex: String,
    currentTranslationHex: String,
    currentTransliterationHex: String,
    onSave: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var arabicHex by remember { mutableStateOf(currentArabicHex) }
    var translationHex by remember { mutableStateOf(currentTranslationHex) }
    var transliterationHex by remember { mutableStateOf(currentTransliterationHex) }

    val presetPalette = listOf(
        "", "#E5C158", "#00E676", "#00B0FF", "#FFFFFF",
        "#111827", "#FFD700", "#FF5252", "#FF9100", "#1DE9B6"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FormatPaint,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = uiText("উপাদানভিত্তিক কালার ওভাররাইড", "Granular Color Overrides"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = uiText(
                        "থিম মোডের পাশাপাশি আপনি নির্দিষ্ট উপাদানের জন্য কাস্টম রঙ নির্ধারণ করতে পারেন:",
                        "Customize colors independently for specific text elements:"
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.5.sp
                    )
                )

                // 1. Arabic Text Color Row
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiText("১. আরবি আয়াত ও হাদিস লিপি", "1. Arabic Script (Quran & Hadith)"),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.5.sp
                            )
                        )
                        val previewColor = if (arabicHex.isNotBlank()) parseHexColor(arabicHex) else LocalAppColors.current.arabicTextColor
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(previewColor)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        )
                    }
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(presetPalette) { hex ->
                            val isSelected = arabicHex.equals(hex, ignoreCase = true)
                            val displayColor = if (hex.isEmpty()) LocalAppColors.current.textPrimary else parseHexColor(hex)
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(displayColor)
                                    .border(
                                        width = if (isSelected) 2.5.dp else 0.5.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                                    .clickable { arabicHex = hex },
                                contentAlignment = Alignment.Center
                            ) {
                                if (hex.isEmpty()) {
                                    Text(uiText("স্বা.", "Auto"), fontSize = 8.sp, fontWeight = FontWeight.Bold, color = if (LocalAppColors.current.isDark) Color.Black else Color.White)
                                }
                            }
                        }
                    }
                }

                // 2. Translation Color Row
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiText("২. অনুবাদ ও তাফসির টেক্সট", "2. Translation & Tafsir Text"),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.5.sp
                            )
                        )
                        val previewColor = if (translationHex.isNotBlank()) parseHexColor(translationHex) else LocalAppColors.current.translationTextColor
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(previewColor)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        )
                    }
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(presetPalette) { hex ->
                            val isSelected = translationHex.equals(hex, ignoreCase = true)
                            val displayColor = if (hex.isEmpty()) LocalAppColors.current.textPrimary else parseHexColor(hex)
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(displayColor)
                                    .border(
                                        width = if (isSelected) 2.5.dp else 0.5.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                                    .clickable { translationHex = hex },
                                contentAlignment = Alignment.Center
                            ) {
                                if (hex.isEmpty()) {
                                    Text(uiText("স্বা.", "Auto"), fontSize = 8.sp, fontWeight = FontWeight.Bold, color = if (LocalAppColors.current.isDark) Color.Black else Color.White)
                                }
                            }
                        }
                    }
                }

                // 3. Transliteration Color Row
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiText("৩. উচ্চারণ (Transliteration)", "3. Transliteration Text"),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.5.sp
                            )
                        )
                        val previewColor = if (transliterationHex.isNotBlank()) parseHexColor(transliterationHex) else LocalAppColors.current.transliterationTextColor
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(previewColor)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        )
                    }
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(presetPalette) { hex ->
                            val isSelected = transliterationHex.equals(hex, ignoreCase = true)
                            val displayColor = if (hex.isEmpty()) LocalAppColors.current.transliterationTextColor else parseHexColor(hex)
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(displayColor)
                                    .border(
                                        width = if (isSelected) 2.5.dp else 0.5.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                                    .clickable { transliterationHex = hex },
                                contentAlignment = Alignment.Center
                            ) {
                                if (hex.isEmpty()) {
                                    Text(uiText("স্বা.", "Auto"), fontSize = 8.sp, fontWeight = FontWeight.Bold, color = if (LocalAppColors.current.isDark) Color.Black else Color.White)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(arabicHex, translationHex, transliterationHex) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = uiText("সংরক্ষণ করুন", "Save"),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    // Reset all overrides to default empty
                    onSave("", "", "")
                }
            ) {
                Text(uiText("রিসেট", "Reset"), color = MaterialTheme.colorScheme.error)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * DUAL-LAYER LANGUAGE SELECTOR DIALOG
 */
@Composable
fun LanguageSelectorDialog(
    currentUiLang: String,
    currentContentLang: String,
    onSelect: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedUi by remember { mutableStateOf(com.example.data.util.SupportedLanguage.normalizeCode(currentUiLang)) }
    var selectedContent by remember { mutableStateOf(com.example.data.util.SupportedLanguage.normalizeCode(currentContentLang)) }
    var activeTab by remember { mutableStateOf(0) } // 0 = App UI Language, 1 = Content Translation Language
    var searchQuery by remember { mutableStateOf("") }

    val filteredLangs = remember(searchQuery) {
        val q = searchQuery.trim().lowercase()
        if (q.isEmpty()) com.example.data.util.SupportedLanguage.entries
        else com.example.data.util.SupportedLanguage.entries.filter {
            it.nativeName.lowercase().contains(q) ||
                it.englishName.lowercase().contains(q) ||
                it.regionLabel.lowercase().contains(q) ||
                it.code == q
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = uiText("ভাষা নির্ধারণ", "Language Settings"),
                style = MaterialTheme.typography.titleMedium.copy(color = ChampagneGold, fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val uiSel = activeTab == 0
                    val contentSel = activeTab == 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (uiSel) ChampagneGold else DarkEmeraldElevated)
                            .clickable { activeTab = 0 }
                            .padding(vertical = 9.dp)
                            .testTag("language_tab_ui"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiText("অ্যাপ ইন্টারফেস", "App UI"),
                            color = if (uiSel) ObsidianBackground else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (contentSel) MintGlow else DarkEmeraldElevated)
                            .clickable { activeTab = 1 }
                            .padding(vertical = 9.dp)
                            .testTag("language_tab_content"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiText("কুরআন/দু'আ অনুবাদ", "Translations"),
                            color = if (contentSel) ObsidianBackground else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        )
                    }
                }

                val uiObj = com.example.data.util.SupportedLanguage.fromCode(selectedUi)
                val contentObj = com.example.data.util.SupportedLanguage.fromCode(selectedContent)
                Text(
                    text = "UI: ${uiObj.nativeName} • ${uiText("অর্থ", "Meaning")}: ${contentObj.nativeName}",
                    color = ChampagneGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            uiText("ভাষা খুঁজুন (English, বাংলা, اردو, Türkçe)...", "Search language..."),
                            color = SlateGrayMuted,
                            fontSize = 11.5.sp
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ObsidianBackground,
                        unfocusedContainerColor = ObsidianBackground,
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = HairlineBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(270.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredLangs, key = { it.code }) { lang ->
                        val isSel = if (activeTab == 0) selectedUi == lang.code else selectedContent == lang.code
                        val tagPrefix = if (activeTab == 0) "ui_lang_option" else "content_lang_option"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) DarkEmeraldElevated else ObsidianBackground.copy(alpha = 0.45f))
                                .border(
                                    width = if (isSel) 1.2.dp else 0.5.dp,
                                    color = if (isSel) (if (activeTab == 0) ChampagneGold else MintGlow) else HairlineBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    if (activeTab == 0) selectedUi = lang.code
                                    else selectedContent = lang.code
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .testTag("${tagPrefix}_${lang.code}"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${lang.nativeName} (${lang.badgeCode})",
                                    color = if (isSel) (if (activeTab == 0) ChampagneGold else MintGlow) else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "${lang.englishName} • ${lang.regionLabel}",
                                    color = SlateGrayMuted,
                                    fontSize = 10.5.sp
                                )
                            }
                            if (isSel) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (activeTab == 0) ChampagneGold else MintGlow,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSelect(selectedUi, selectedContent) },
                colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = ObsidianBackground),
                modifier = Modifier.testTag("language_save_button")
            ) {
                Text(uiText("সংরক্ষণ করুন", "Save"))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(uiText("বাতিল", "Cancel"), color = SlateGrayMuted) }
        },
        containerColor = DarkEmeraldSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * TYPOGRAPHY & ARABIC SCRIPT DIALOG
 */
@Composable
fun TypographyScriptDialog(
    currentUiFont: String,
    currentArabicFont: String,
    onSelectUiFont: (String) -> Unit,
    onSelectArabicFont: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = uiText("ফন্ট ও আরবি লিপিশৈলী", "Fonts & Arabic Script"),
                style = MaterialTheme.typography.titleMedium.copy(color = ChampagneGold, fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = uiText("ইউজার ইন্টারফেস ফন্ট:", "User Interface Font:"), color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                AppUiFont.entries.forEach { font ->
                    val isSel = currentUiFont == font.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) DarkEmeraldElevated else DarkEmeraldSurface)
                            .border(1.dp, if (isSel) ChampagneGold else HairlineBorder, RoundedCornerShape(8.dp))
                            .clickable { onSelectUiFont(font.id) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (isEnglishUi()) font.titleEnglish else font.titleBengali, color = if (isSel) ChampagneGold else TextPrimary, fontSize = 12.sp)
                        if (isSel) Icon(Icons.Default.Check, contentDescription = null, tint = MintGlow, modifier = Modifier.size(16.dp))
                    }
                }

                Text(text = uiText("আরবি মুসহাফ লিপি (Mushaf Script):", "Arabic Mushaf Script:"), color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                AppArabicFont.entries.forEach { aFont ->
                    val isSel = currentArabicFont == aFont.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) DarkEmeraldElevated else DarkEmeraldSurface)
                            .border(1.dp, if (isSel) ChampagneGold else HairlineBorder, RoundedCornerShape(8.dp))
                            .clickable { onSelectArabicFont(aFont.id) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "${if (isEnglishUi()) aFont.titleEnglish else aFont.titleBengali} (بِسْمِ اللَّهِ)", color = if (isSel) ChampagneGold else TextPrimary, fontSize = 12.sp)
                        if (isSel) Icon(Icons.Default.Check, contentDescription = null, tint = MintGlow, modifier = Modifier.size(16.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(uiText("ঠিক আছে", "OK"), color = ChampagneGold) }
        },
        containerColor = DarkEmeraldSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * TEXT SCALE DIALOG
 */
@Composable
fun TextScaleDialog(
    currentScale: Float,
    onSelect: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = uiText("টেক্সট সাইজ ও স্কেলিং", "Text Size & Scaling"),
                style = MaterialTheme.typography.titleMedium.copy(color = ChampagneGold, fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppTextScale.entries.forEach { scalePreset ->
                    val isSel = kotlin.math.abs(currentScale - scalePreset.scaleFactor) < 0.01f
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) DarkEmeraldElevated else DarkEmeraldSurface)
                            .border(1.dp, if (isSel) ChampagneGold else HairlineBorder, RoundedCornerShape(8.dp))
                            .clickable { onSelect(scalePreset.scaleFactor) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isEnglishUi()) scalePreset.labelEnglish else scalePreset.labelBengali,
                            color = if (isSel) ChampagneGold else TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                        )
                        if (isSel) Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MintGlow, modifier = Modifier.size(18.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(uiText("ঠিক আছে", "OK"), color = ChampagneGold) }
        },
        containerColor = DarkEmeraldSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * FASTING CAUTIONS STEPPER DIALOG
 */
@Composable
fun FastingCautionsDialog(
    sahurMins: Int,
    iftarMins: Int,
    onSave: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var sMins by remember { mutableStateOf(sahurMins) }
    var iMins by remember { mutableStateOf(iftarMins) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = uiText("সাহুর ও ইফতার সতর্কতা সময়", "Sahur & Iftar Caution Offsets"),
                style = MaterialTheme.typography.titleMedium.copy(color = ChampagneGold, fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = uiText(
                        "ফজরের আযানের কত মিনিট পূর্বে সাহুর শেষ করবেন এবং মাগরিবের কত মিনিট পর ইফতার শুরু করবেন:",
                        "Set caution buffer minutes before Fajr for Sahur and after Maghrib for Iftar:"
                    ),
                    color = SlateGrayMuted,
                    fontSize = 11.sp
                )

                // Sahur stepper
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = uiText("সাহুর সতর্কতা বাফার:", "Sahur Caution Buffer:"), color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text(text = uiText("ফজর আযানের $sMins মিনিট পূর্বে সমাপ্তি", "Ends $sMins min before Fajr"), color = ChampagneGold, fontSize = 11.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(onClick = { if (sMins > 1) sMins-- }) {
                            Text("-", color = ChampagneGold, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(uiText("$sMins মি.", "${sMins}m"), color = Color.White, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { if (sMins < 10) sMins++ }) {
                            Text("+", color = ChampagneGold, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Iftar stepper
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = uiText("ইফতার সতর্কতা বাফার:", "Iftar Caution Buffer:"), color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text(text = uiText("মাগরিব আযানের $iMins মিনিট পর ইফতার শুরু", "Starts $iMins min after Maghrib"), color = MintGlow, fontSize = 11.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(onClick = { if (iMins > 1) iMins-- }) {
                            Text("-", color = MintGlow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(uiText("$iMins মি.", "${iMins}m"), color = Color.White, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { if (iMins < 10) iMins++ }) {
                            Text("+", color = MintGlow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(sMins, iMins) },
                colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = ObsidianBackground)
            ) {
                Text(uiText("সংরক্ষণ করুন", "Save"))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(uiText("বাতিল", "Cancel"), color = SlateGrayMuted) }
        },
        containerColor = DarkEmeraldSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * QARI SELECTOR DIALOG
 */
@Composable
fun QariSelectorDialog(
    selectedId: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = uiText("পছন্দের ক্বারী সাহেব নির্বাচন", "Select Preferred Qari"),
                style = MaterialTheme.typography.titleMedium.copy(color = ChampagneGold, fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QariDirectory.MALE_RECITERS.forEach { qari ->
                    val isSel = selectedId == qari.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) DarkEmeraldElevated else DarkEmeraldSurface)
                            .border(1.dp, if (isSel) ChampagneGold else HairlineBorder, RoundedCornerShape(10.dp))
                            .clickable { onSelect(qari.id) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = qari.name, color = if (isSel) ChampagneGold else TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${qari.arabicName} • ${qari.style}", color = SlateGrayMuted, fontSize = 11.sp)
                        }
                        if (isSel) Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MintGlow, modifier = Modifier.size(18.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(uiText("ঠিক আছে", "OK"), color = ChampagneGold) }
        },
        containerColor = DarkEmeraldSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * AUDIO STORAGE & CACHE MANAGER DIALOG
 */
@Composable
fun AudioStorageDialog(
    onClearCache: (includeDownloads: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var refreshTick by remember { mutableStateOf(0) }
    var includeOfflineDownloads by remember { mutableStateOf(false) }
    val storageInfo = remember(refreshTick) {
        com.example.data.audio.OfflineAudioManager.getStorageInfo(context)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = uiText("অডিও ক্যাশ ও মেমোরি", "Audio Cache & Storage"),
                style = MaterialTheme.typography.titleMedium.copy(color = ChampagneGold, fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = uiText(
                        "কুরআন তিলাওয়াত বা রুকইয়াহ শোনার সময় অডিও ফাইল স্বয়ংক্রিয়ভাবে লোকাল ক্যাশে জমা হয়, যা পরবর্তীতে দ্রুত ও ইন্টারনেট ছাড়াই শোনা যায়।",
                        "Quran recitations and Ruqyah tracks are cached locally for instant offline playback."
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontSize = 12.sp, lineHeight = 18.sp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkEmeraldElevated)
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = uiText("মোট অডিও ক্যাশ ও ডাউনলোড:", "Total Audio Cache & Offline:"), color = SlateGrayMuted, fontSize = 11.sp)
                                Text(text = storageInfo.formattedAudioCacheSize, color = MintGlow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Button(
                                onClick = {
                                    onClearCache(includeOfflineDownloads)
                                    refreshTick++
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))
                            ) {
                                Text(uiText("ক্যাশ মুছুন", "Clear Cache"), fontSize = 11.sp, color = Color.White)
                            }
                        }
                        Text(
                            text = "${uiText("অফলাইন ডাউনলোডকৃত সূরা:", "Downloaded Surahs:")} ${storageInfo.downloadedSurahCount} (${storageInfo.formattedOfflineDownloadedSize})",
                            color = ChampagneGold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "${uiText("ডিভাইসে খালি জায়গা:", "Available Device Space:")} ${storageInfo.formattedDeviceAvailableSpace}",
                            color = SlateGrayMuted,
                            fontSize = 10.5.sp
                        )
                        if (storageInfo.downloadedSurahCount > 0) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { includeOfflineDownloads = !includeOfflineDownloads },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = includeOfflineDownloads,
                                    onCheckedChange = { includeOfflineDownloads = it }
                                )
                                Text(
                                    text = uiText("অফলাইন সূরা ডাউনলোডও মুছে ফেলুন", "Also delete offline Surah downloads"),
                                    color = TextPrimary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(uiText("বন্ধ করুন", "Close"), color = ChampagneGold) }
        },
        containerColor = DarkEmeraldSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * BANGLADESH 64 DISTRICT SELECTOR DIALOG
 */
@Composable
fun BangladeshDistrictSelectorDialog(
    currentDistrictId: String,
    isDetectingLocation: Boolean = false,
    onAutoDetectLocation: () -> Unit = {},
    onSelectDistrict: (District) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val uiLang = LocalAppLanguage.current
    var searchQuery by remember { mutableStateOf("") }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        if (perms.values.any { it }) {
            onAutoDetectLocation()
        } else {
            Toast.makeText(
                context,
                localizedText(uiLang, "অটো লোকেশনের জন্য লোকেশন পারমিশন দিন", "Please grant location permission for auto-location"),
                Toast.LENGTH_SHORT
            ).show()
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
        title = {
            Column {
                Text(
                    text = uiText("অবস্থান ও জেলা নির্বাচন (৬৪ জেলা)", "Select District & Location"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = uiText("জিপিএস অটো-ডিটেক্ট করুন অথবা আপনার জেলা বেছে নিন", "Auto-detect GPS or pick your district"),
                    style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 11.sp)
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        if (fine || coarse) {
                            onAutoDetectLocation()
                        } else {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    enabled = !isDetectingLocation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("settings_auto_detect_gps_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MintGlow,
                        contentColor = ObsidianBackground
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isDetectingLocation) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = ObsidianBackground,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(uiText("জিপিএস শনাক্ত হচ্ছে...", "Detecting GPS..."), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    } else {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(uiText("বর্তমান জিপিএস অবস্থান নিন (Auto-Detect)", "Use Current GPS Location (Auto-Detect)"), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(uiText("জেলার নাম খুঁজুন...", "Search district name..."), color = SlateGrayMuted, fontSize = 12.sp)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkEmeraldSurface,
                        unfocusedContainerColor = DarkEmeraldSurface,
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = HairlineBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredDistricts, key = { it.id }) { district ->
                        val isSelected = district.id == currentDistrictId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) DarkEmeraldElevated else DarkEmeraldSurface)
                                .border(
                                    width = if (isSelected) 1.dp else 0.5.dp,
                                    color = if (isSelected) ChampagneGold else HairlineBorder,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { onSelectDistrict(district) }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (isEnglishUi()) district.nameEnglish else district.nameBengali,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = if (isSelected) ChampagneGold else TextPrimary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = if (isEnglishUi()) "${district.nameBengali} • Div: ${district.divisionBengali}" else "${district.nameEnglish} • বিভাগ: ${district.divisionBengali}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 10.sp)
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = MintGlow,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
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
        containerColor = DarkEmeraldSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * SUPPORT & LEGAL DIALOG
 */
@Composable
fun SupportDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = uiText("দ্বীনি খেদমত ও সহায়তা", "Support & Community"),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = ChampagneGold,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = uiText(
                        "নূর-এ-হেদায়াত সম্পূর্ণ অলাভজনক ও সাদাকাহ জারিয়াহ প্রকল্প। কোনো বিজ্ঞাপন বা ফি ছাড়াই মুসলিম উম্মাহর জন্য বিনামূল্যে উন্মুক্ত।",
                        "Noor-e-Hidayah is a non-profit Sadaqah Jariyah project—100% ad-free and free for the Muslim Ummah."
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, lineHeight = 18.sp)
                )
                HorizontalDivider(color = HairlineBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
                Text(
                    text = uiText("অফিসিয়াল সাপোর্ট ও যোগাযোগ ইমেইল:", "Official Support Email:"),
                    style = MaterialTheme.typography.labelSmall.copy(color = SlateGrayMuted)
                )
                Text(
                    text = "contact.noorehidayah@gmail.com",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MintGlow, fontWeight = FontWeight.Bold)
                )
                val emailErrMsg = uiText("ইমেইল অ্যাপ পাওয়া যায়নি: contact.noorehidayah@gmail.com", "No email app found: contact.noorehidayah@gmail.com")
                Button(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:contact.noorehidayah@gmail.com")
                                putExtra(Intent.EXTRA_SUBJECT, "[Noor-e-Hidayah] Support & Inquiry (v1.0)")
                                putExtra(Intent.EXTRA_TEXT, "Assalamu Alaikum Noor-e-Hidayah Team,\n\n[Write your message here]\n\n---\nDevice: ${Build.MANUFACTURER} ${Build.MODEL} (Android ${Build.VERSION.RELEASE})")
                            }
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            Toast.makeText(context, emailErrMsg, Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkEmeraldElevated,
                        contentColor = ChampagneGold
                    )
                ) {
                    Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(uiText("ইমেইল পাঠান (Direct Email)", "Send Direct Email"))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(uiText("জাযাকাল্লাহু খাইরান", "JazakAllahu Khayran"), color = ChampagneGold)
            }
        },
        containerColor = DarkEmeraldSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * FEEDBACK & BUG REPORT DIALOG
 */
@Composable
fun FeedbackDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var feedbackText by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (submitted) uiText("পরামর্শ গৃহীত হয়েছে", "Feedback Received") else uiText("ভুলত্রুটি বা পরামর্শ পাঠান", "Send Feedback / Report Issue"),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = ChampagneGold,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            if (submitted) {
                Text(
                    text = uiText(
                        "আপনার পরামর্শের জন্য অসংখ্য ধন্যবাদ। নূর-এ-হেদায়াত টিম আন্তরিকতার সাথে বিষয়টি পর্যালোচনা করবে। জাযাকাল্লাহু খাইরান!",
                        "Thank you for your feedback. Our team will carefully review your report. JazakAllahu Khayran!"
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, lineHeight = 18.sp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = uiText(
                            "কুরআন বা হাদিসের কোনো অনুবাদ, উচ্চারণ বা ওয়াক্তের তথ্যে কোনো অসঙ্গতি চোখে পড়লে অনুগ্রহ করে লিখে জানান (contact.noorehidayah@gmail.com):",
                            "Notice any issue in Quran/Hadith translations or prayer timings? Let us know (contact.noorehidayah@gmail.com):"
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontSize = 12.sp)
                    )
                    OutlinedTextField(
                        value = feedbackText,
                        onValueChange = { feedbackText = it },
                        placeholder = { Text(uiText("আপনার পরামর্শ বা সংশোধনী লিখুন...", "Write your feedback or suggestion..."), color = SlateGrayMuted, fontSize = 11.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkEmeraldSurface,
                            unfocusedContainerColor = DarkEmeraldSurface,
                            focusedBorderColor = ChampagneGold,
                            unfocusedBorderColor = HairlineBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            }
        },
        confirmButton = {
            if (submitted) {
                TextButton(onClick = onDismiss) {
                    Text(uiText("ঠিক আছে", "OK"), color = ChampagneGold)
                }
            } else {
                Button(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:contact.noorehidayah@gmail.com")
                                putExtra(Intent.EXTRA_SUBJECT, "[Noor-e-Hidayah] Feedback & Corrections (v1.0)")
                                putExtra(Intent.EXTRA_TEXT, "$feedbackText\n\n---\nDevice: ${Build.MANUFACTURER} ${Build.MODEL} (Android ${Build.VERSION.RELEASE})")
                            }
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                        submitted = true
                    },
                    enabled = feedbackText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MintGlow.copy(alpha = 0.2f),
                        contentColor = MintGlow
                    )
                ) {
                    Text(uiText("ইমেইলে প্রেরণ করুন", "Send via Email"))
                }
            }
        },
        dismissButton = {
            if (!submitted) {
                TextButton(onClick = onDismiss) {
                    Text(uiText("বাতিল", "Cancel"), color = SlateGrayMuted)
                }
            }
        },
        containerColor = DarkEmeraldSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * APP INTEGRITY & SECURITY REPORT DIALOG
 */
@Composable
fun SecurityReportDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val report = remember { SecurityGuard.getSecurityReport(context) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Security,
                    contentDescription = null,
                    tint = if (report.isSecure) MintGlow else Color(0xFFFF5252),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = uiText("অ্যাপ সিকিউরিটি ও ইন্টিগ্রিটি", "App Security & Integrity"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = report.environmentSummary,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold)
                )
                HorizontalDivider(color = HairlineBorder.copy(alpha = 0.5f), thickness = 0.5.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(uiText("ডিভাইস রুট স্ট্যাটাস:", "Device Root Status:"), style = MaterialTheme.typography.labelSmall.copy(color = SlateGrayMuted))
                    Text(
                        if (report.isRooted) uiText("রুটেড (সতর্কতা)", "Rooted (Warning)") else uiText("রুটমুক্ত (নিরাপদ)", "Unrooted (Safe)"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (report.isRooted) Color(0xFFFF5252) else MintGlow,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(uiText("প্যাকেজ অখণ্ডতা:", "Package Integrity:"), style = MaterialTheme.typography.labelSmall.copy(color = SlateGrayMuted))
                    Text(
                        uiText("অফিসিয়াল APK সিগনেচার", "Official APK Signature"),
                        style = MaterialTheme.typography.labelSmall.copy(color = MintGlow, fontWeight = FontWeight.Bold)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(uiText("নেটওয়ার্ক প্রটেকশন:", "Network Protection:"), style = MaterialTheme.typography.labelSmall.copy(color = SlateGrayMuted))
                    Text(
                        uiText("HTTPS-Only (MitM সুরক্ষিত)", "HTTPS-Only (MitM Protected)"),
                        style = MaterialTheme.typography.labelSmall.copy(color = MintGlow, fontWeight = FontWeight.Bold)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(uiText("App Check প্রোভাইডার:", "App Check Provider:"), style = MaterialTheme.typography.labelSmall.copy(color = SlateGrayMuted))
                    Text(
                        com.example.data.security.AppCheckDiagnostics.activeProviderMode,
                        style = MaterialTheme.typography.labelSmall.copy(color = ChampagneGold, fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(uiText("সার্টিফিকেট SHA-256 ফিঙ্গারপ্রিন্ট:", "Certificate SHA-256 Fingerprint:"), style = MaterialTheme.typography.labelSmall.copy(color = SlateGrayMuted))
                Text(
                    text = report.certFingerprintSha256.take(36) + "...",
                    style = MaterialTheme.typography.labelSmall.copy(color = ChampagneGold, fontSize = 10.sp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(uiText("ঠিক আছে", "OK"), color = ChampagneGold)
            }
        },
        containerColor = DarkEmeraldSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun HijriMoonSightingAdjustDialog(
    currentOffset: Int,
    onSave: (Int) -> Unit,
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
                    imageVector = Icons.Default.DarkMode,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = uiText("হিজরি তারিখ সমন্বয় (চাঁদ দেখা)", "Hijri Date Offset (Moon Sighting)"),
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
                        "Adjust the Hijri calendar by 1 or 2 days based on local crescent moon sighting. Applies globally to daily schedules and calendar grids."
                    ),
                    color = SlateGrayMuted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                val options = listOf(
                    -2 to uiText("২ দিন পিছিয়ে (-২ দিন)", "-2 Days (2 days behind)"),
                    -1 to uiText("১ দিন পিছিয়ে (-১ দিন)", "-1 Day (1 day behind)"),
                    0 to uiText("স্বাভাবিক জ্যোতির্বৈজ্ঞানিক হিসাব (০ দিন)", "Standard Astronomical (0 days)"),
                    1 to uiText("১ দিন এগিয়ে (+১ দিন)", "+1 Day (1 day ahead)"),
                    2 to uiText("২ দিন এগিয়ে (+২ দিন)", "+2 Days (2 days ahead)")
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
                onClick = { onSave(selectedOffset) },
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

