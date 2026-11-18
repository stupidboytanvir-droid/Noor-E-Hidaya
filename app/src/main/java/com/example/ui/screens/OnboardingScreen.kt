package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.util.LanguageRegion
import com.example.data.util.SupportedLanguage
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.Icon3DType
import com.example.ui.components.ThreeDimensionalIcon
import com.example.ui.theme.*

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    onLocationGranted: () -> Unit = {},
    initialLanguageSelected: Boolean = true,
    onSelectInitialLanguage: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    if (!initialLanguageSelected) {
        WelcomeLanguagePickerStep(
            onConfirmLanguage = onSelectInitialLanguage,
            modifier = modifier
        )
        return
    }

    val context = LocalContext.current

    var isLocationGranted by remember {
        mutableStateOf(checkLocationPermission(context))
    }
    var isNotificationGranted by remember {
        mutableStateOf(checkNotificationPermission(context))
    }
    var isPhoneSmsGranted by remember {
        mutableStateOf(checkPhoneSmsPermission(context))
    }

    LaunchedEffect(isLocationGranted) {
        if (isLocationGranted) {
            onLocationGranted()
        }
    }

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        isLocationGranted = checkLocationPermission(context)
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        isNotificationGranted = checkNotificationPermission(context)
    }

    val phoneSmsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        isPhoneSmsGranted = checkPhoneSmsPermission(context)
    }

    val batchLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        isLocationGranted = checkLocationPermission(context)
        isNotificationGranted = checkNotificationPermission(context)
        isPhoneSmsGranted = checkPhoneSmsPermission(context)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ObsidianBackground,
                        DarkEmeraldSurface,
                        ObsidianBackground
                    )
                )
            )
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ThreeDimensionalIcon(
                        type = Icon3DType.MOSQUE_HOME,
                        size = 72.dp,
                        isActive = true,
                        accentColor = ChampagneGold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = AmiriFontFamily,
                            color = MintGlow,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = uiText("নূর-এ-হেদায়াত-এ স্বাগতম", "Welcome to Noor-e-Hidayah"),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = uiText(
                            "সঠিক কিবলা কম্পাস, নামাজের সময়সূচি এবং স্বয়ংক্রিয় মসজিদ সাইলেন্ট মোড কার্যকর করতে অনুগ্রহ করে নিচের সুবিধাসমূহ সক্রিয় করুন।",
                            "Please enable the permissions below for accurate Qibla compass, prayer times, and automatic Mosque Silent Mode."
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateGrayMuted,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            item {
                PermissionCard(
                    title = uiText("১. অবস্থান ও কিবলা কম্পাস", "1. Location & Qibla Compass"),
                    subtitle = uiText(
                        "কা'বা শরীফের সঠিক দিক এবং আপনার এলাকার নির্ভুল নামাজের ওয়াক্ত হিসাব করতে লোকেশন অনুমতি দিন।",
                        "Allow location access to calculate accurate prayer times and Qibla direction to the Kaaba."
                    ),
                    icon = Icons.Filled.LocationOn,
                    isGranted = isLocationGranted,
                    onGrantClick = {
                        locationLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                )
            }

            item {
                PermissionCard(
                    title = uiText("২. নামাজের আজান ও ১৫ মি. পূর্বে অ্যালার্ট", "2. Adhan & 15-Min Pre-Prayer Alerts"),
                    subtitle = uiText(
                        "প্রতি ওয়াক্তের আজান, নামাজের ১৫ মিনিট পূর্বে সতর্কীকরণ ও সাহরির অ্যালার্ট পেতে নোটিফিকেশন চালু করুন।",
                        "Enable notifications for Adhan calls, 15-minute pre-prayer reminders, and Sahur alerts."
                    ),
                    icon = Icons.Filled.NotificationsActive,
                    isGranted = isNotificationGranted,
                    onGrantClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            isNotificationGranted = true
                        }
                    }
                )
            }

            item {
                PermissionCard(
                    title = uiText("৩. জামাতে ফ্রি অটো-এসএমএস", "3. Free Auto-SMS During Jama'at"),
                    subtitle = uiText(
                        "জামাত চলাকালীন ফোন স্বয়ংক্রিয় ভাইব্রেশন/সাইলেন্ট মোডে থাকলে কলারকে ফ্রি সম্মানজনক এসএমএস পাঠাতে।",
                        "Automatically send a respectful SMS reply via your SIM when calls arrive during Mosque Mode."
                    ),
                    icon = Icons.Filled.Sms,
                    isGranted = isPhoneSmsGranted,
                    onGrantClick = {
                        phoneSmsLauncher.launch(
                            arrayOf(
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.READ_CALL_LOG,
                                Manifest.permission.SEND_SMS
                            )
                        )
                    }
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            ObsidianBackground.copy(alpha = 0.95f),
                            ObsidianBackground
                        )
                    )
                )
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val allStandardGranted = isLocationGranted && isNotificationGranted && isPhoneSmsGranted

            if (!allStandardGranted) {
                Button(
                    onClick = {
                        val permissions = mutableListOf<String>()
                        if (!isLocationGranted) {
                            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
                            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
                        }
                        if (!isNotificationGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        if (!isPhoneSmsGranted) {
                            permissions.add(Manifest.permission.READ_PHONE_STATE)
                            permissions.add(Manifest.permission.READ_CALL_LOG)
                            permissions.add(Manifest.permission.SEND_SMS)
                        }
                        if (permissions.isNotEmpty()) {
                            batchLauncher.launch(permissions.toTypedArray())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("onboarding_grant_all_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MintGlow,
                        contentColor = ObsidianBackground
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Security,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiText("এক সাথে সব অনুমতি দিন", "Grant All Permissions"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Button(
                onClick = onComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("onboarding_enter_app_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChampagneGold,
                    contentColor = ObsidianBackground
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (allStandardGranted) uiText("অ্যাপে প্রবেশ করুন", "Enter App")
                    else uiText("শুরু করুন (পরবর্তীতে করুন)", "Get Started (Skip for Now)"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun WelcomeLanguagePickerStep(
    onConfirmLanguage: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeUiLang = LocalAppLanguage.current
    var selectedUiLang by remember { mutableStateOf(SupportedLanguage.normalizeCode(activeUiLang)) }
    var syncTranslation by remember { mutableStateOf(true) }
    var selectedContentLang by remember { mutableStateOf(SupportedLanguage.normalizeCode(activeUiLang)) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf<LanguageRegion?>(null) }

    val filteredLanguages = remember(searchQuery, selectedRegion) {
        SupportedLanguage.entries.filter { lang ->
            val matchesRegion = selectedRegion == null || lang.region == selectedRegion
            val q = searchQuery.trim().lowercase()
            val matchesSearch = q.isEmpty() ||
                lang.nativeName.lowercase().contains(q) ||
                lang.englishName.lowercase().contains(q) ||
                lang.regionLabel.lowercase().contains(q) ||
                lang.code.lowercase() == q
            matchesRegion && matchesSearch
        }
    }

    val chosenLang = SupportedLanguage.fromCode(selectedUiLang)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ObsidianBackground,
                        DarkEmeraldSurface,
                        ObsidianBackground
                    )
                )
            )
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
                .padding(top = 18.dp, bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                ThreeDimensionalIcon(
                    type = Icon3DType.MOSQUE_HOME,
                    size = 56.dp,
                    isActive = true,
                    accentColor = ChampagneGold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = AmiriFontFamily,
                        color = MintGlow,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = LocalizationRegistry.translate(
                        selectedUiLang,
                        "আপনার ভাষা নির্বাচন করুন",
                        "Choose Your Language"
                    ),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 21.sp,
                        textAlign = TextAlign.Center
                    )
                )
                Text(
                    text = LocalizationRegistry.translate(
                        selectedUiLang,
                        "এশিয়া, মধ্যপ্রাচ্য ও ইউরোপের ১২টি ভাষায় সম্পূর্ণ অ্যাপ ও কুরআন-দু'আ অনুবাদ",
                        "Complete app interface & Quran/Du'a translations in 12 global languages"
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SlateGrayMuted,
                        fontSize = 11.5.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = LocalizationRegistry.translate(
                            selectedUiLang,
                            "ভাষা খুঁজুন (যেমন: English, বাংলা, اردو, Türkçe)...",
                            "Search language (English, Arabic, Urdu, Turkish)..."
                        ),
                        fontSize = 12.sp,
                        color = SlateGrayMuted
                    )
                },
                leadingIcon = {
                    Icon(Icons.Filled.Translate, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(18.dp))
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkEmeraldSurface,
                    unfocusedContainerColor = DarkEmeraldSurface,
                    focusedBorderColor = ChampagneGold,
                    unfocusedBorderColor = HairlineBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("welcome_language_search")
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedRegion == null,
                    onClick = { selectedRegion = null },
                    label = {
                        Text(
                            LocalizationRegistry.translate(selectedUiLang, "সব অঞ্চল", "All Regions"),
                            fontSize = 11.5.sp
                        )
                    }
                )
                LanguageRegion.entries.forEach { region ->
                    FilterChip(
                        selected = selectedRegion == region,
                        onClick = { selectedRegion = if (selectedRegion == region) null else region },
                        label = { Text(region.labelEnglish, fontSize = 11.5.sp) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredLanguages, key = { it.code }) { lang ->
                    val isSelected = selectedUiLang == lang.code
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) DarkEmeraldElevated else DarkEmeraldSurface)
                            .border(
                                width = if (isSelected) 1.5.dp else 0.75.dp,
                                color = if (isSelected) ChampagneGold else HairlineBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                selectedUiLang = lang.code
                                if (syncTranslation) selectedContentLang = lang.code
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                            .testTag("welcome_lang_${lang.code}"),
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
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) ChampagneGold else ObsidianBackground)
                                    .border(0.75.dp, ChampagneGold.copy(alpha = 0.45f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lang.badgeCode,
                                    color = if (isSelected) ObsidianBackground else ChampagneGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = lang.nativeName,
                                        color = if (isSelected) ChampagneGold else TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.5.sp
                                    )
                                    if (lang.isRtl) {
                                        Text(
                                            text = "RTL",
                                            color = MintGlow,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(MintGlow.copy(alpha = 0.14f))
                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${lang.englishName} • ${lang.regionLabel}",
                                    color = SlateGrayMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = MintGlow,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkEmeraldSurface)
                    .border(0.5.dp, HairlineBorder, RoundedCornerShape(10.dp))
                    .clickable {
                        syncTranslation = !syncTranslation
                        if (syncTranslation) selectedContentLang = selectedUiLang
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = LocalizationRegistry.translate(
                        selectedUiLang,
                        "কুরআন ও দু'আ অনুবাদ একই ভাষায় রাখুন",
                        "Also use ${chosenLang.englishName} for Quran & Du'a meanings"
                    ),
                    color = TextPrimary,
                    fontSize = 11.5.sp,
                    modifier = Modifier.weight(1f)
                )
                Checkbox(
                    checked = syncTranslation,
                    onCheckedChange = {
                        syncTranslation = it
                        if (it) selectedContentLang = selectedUiLang
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(ObsidianBackground.copy(alpha = 0.96f))
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Button(
                onClick = {
                    val finalContent = if (syncTranslation) selectedUiLang else selectedContentLang
                    onConfirmLanguage(selectedUiLang, finalContent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("welcome_language_continue_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChampagneGold,
                    contentColor = ObsidianBackground
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = LocalizationRegistry.translate(
                        selectedUiLang,
                        "${chosenLang.nativeName} দিয়ে এগিয়ে যান",
                        "Continue in ${chosenLang.nativeName}"
                    ),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun PermissionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isGranted: Boolean,
    actionButtonText: String = if (isGranted) uiText("অনুমোদিত", "Granted") else uiText("অনুমতি দিন", "Allow"),
    onGrantClick: () -> Unit
) {
    FrostedGlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = if (isGranted) DarkEmeraldElevated else DarkEmeraldSurface,
        borderColor = if (isGranted) MintGlow.copy(alpha = 0.5f) else HairlineBorder
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isGranted) MintGlow.copy(alpha = 0.15f) else DarkEmeraldElevated)
                        .border(
                            0.5.dp,
                            if (isGranted) MintGlow else ChampagneGold.copy(alpha = 0.3f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isGranted) Icons.Filled.CheckCircle else icon,
                        contentDescription = null,
                        tint = if (isGranted) MintGlow else ChampagneGold,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = if (isGranted) MintGlow else TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateGrayMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isGranted) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MintGlow.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = uiText("অনুমোদিত", "Granted"),
                        color = MintGlow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                Button(
                    onClick = onGrantClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChampagneGold,
                        contentColor = ObsidianBackground
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(actionButtonText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun checkLocationPermission(context: Context): Boolean {
    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    return fine || coarse
}

private fun checkNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

private fun checkPhoneSmsPermission(context: Context): Boolean {
    val p1 = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
    val p2 = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED
    val p3 = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
    return p1 && p2 && p3
}
