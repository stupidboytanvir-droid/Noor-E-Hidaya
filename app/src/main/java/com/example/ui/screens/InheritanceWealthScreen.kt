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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

data class HeirShareResult(
    val heirTitleBengali: String,
    val count: Int,
    val fractionText: String,
    val percentage: Double,
    val totalAmountBdt: Double,
    val perPersonAmountBdt: Double,
    val quranicReference: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InheritanceWealthScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val uiLang = LocalAppLanguage.current
    var selectedTab by remember { mutableStateOf(0) } // 0: Faraidh Calculator, 1: Wealth Purification

    // Faraidh State
    var totalEstateInput by remember { mutableStateOf("1000000") }
    var debtsAndBurialInput by remember { mutableStateOf("50000") }
    var bequestInput by remember { mutableStateOf("0") }

    // Heirs Selection
    var deceasedGender by remember { mutableStateOf("MALE") } // Deceased was MALE (leaves wife) or FEMALE (leaves husband)
    var wifeCount by remember { mutableStateOf(1) }
    var hasHusband by remember { mutableStateOf(true) }
    var sonsCount by remember { mutableStateOf(2) }
    var daughtersCount by remember { mutableStateOf(1) }
    var isFatherAlive by remember { mutableStateOf(false) }
    var isMotherAlive by remember { mutableStateOf(true) }

    // Wealth Purification State
    var doubtfulIncomeInput by remember { mutableStateOf("5000") }
    var interestReceivedInput by remember { mutableStateOf("2500") }

    // Calculation Engine
    val grossEstate = totalEstateInput.toDoubleOrNull() ?: 0.0
    val debts = debtsAndBurialInput.toDoubleOrNull() ?: 0.0
    val maxBequest = maxOf(0.0, (grossEstate - debts) / 3.0)
    val userBequest = (bequestInput.toDoubleOrNull() ?: 0.0).coerceAtMost(maxBequest)
    val netEstate = maxOf(0.0, grossEstate - debts - userBequest)

    val hasChildren = (sonsCount + daughtersCount) > 0

    val heirResults = remember(
        uiLang, netEstate, deceasedGender, wifeCount, hasHusband, sonsCount, daughtersCount, isFatherAlive, isMotherAlive
    ) {
        val list = mutableListOf<HeirShareResult>()
        if (netEstate <= 0.0) return@remember list

        var remainingEstate = netEstate

        // 1. Spouse
        if (deceasedGender == "MALE") {
            if (wifeCount > 0) {
                val wifeFraction = if (hasChildren) 1.0 / 8.0 else 1.0 / 4.0
                val wifeFractionStr = localizedText(
                    uiLang,
                    if (hasChildren) "১/৮ অংশ" else "১/৪ অংশ",
                    if (hasChildren) "1/8 Share" else "1/4 Share"
                )
                val wifeTotalAmount = netEstate * wifeFraction
                remainingEstate -= wifeTotalAmount
                list.add(
                    HeirShareResult(
                        heirTitleBengali = localizedText(
                            uiLang,
                            if (wifeCount > 1) "স্ত্রীগণ ($wifeCount জন)" else "স্ত্রী (১ জন)",
                            if (wifeCount > 1) "Wives ($wifeCount)" else "Wife (1)"
                        ),
                        count = wifeCount,
                        fractionText = wifeFractionStr,
                        percentage = wifeFraction * 100.0,
                        totalAmountBdt = wifeTotalAmount,
                        perPersonAmountBdt = wifeTotalAmount / wifeCount,
                        quranicReference = localizedText(uiLang, "সূরা আন-নিসা: ১২", "Surah An-Nisa: 12")
                    )
                )
            }
        } else {
            if (hasHusband) {
                val husbandFraction = if (hasChildren) 1.0 / 4.0 else 1.0 / 2.0
                val husbandFractionStr = localizedText(
                    uiLang,
                    if (hasChildren) "১/৪ অংশ" else "১/২ অংশ",
                    if (hasChildren) "1/4 Share" else "1/2 Share"
                )
                val husbandAmount = netEstate * husbandFraction
                remainingEstate -= husbandAmount
                list.add(
                    HeirShareResult(
                        heirTitleBengali = localizedText(uiLang, "স্বামী (১ জন)", "Husband (1)"),
                        count = 1,
                        fractionText = husbandFractionStr,
                        percentage = husbandFraction * 100.0,
                        totalAmountBdt = husbandAmount,
                        perPersonAmountBdt = husbandAmount,
                        quranicReference = localizedText(uiLang, "সূরা আন-নিসা: ১২", "Surah An-Nisa: 12")
                    )
                )
            }
        }

        // 2. Mother
        if (isMotherAlive) {
            val motherFraction = if (hasChildren) 1.0 / 6.0 else 1.0 / 3.0
            val motherFractionStr = localizedText(
                uiLang,
                if (hasChildren) "১/৬ অংশ" else "১/৩ অংশ",
                if (hasChildren) "1/6 Share" else "1/3 Share"
            )
            val motherAmount = netEstate * motherFraction
            remainingEstate -= motherAmount
            list.add(
                HeirShareResult(
                    heirTitleBengali = localizedText(uiLang, "মাতা", "Mother"),
                    count = 1,
                    fractionText = motherFractionStr,
                    percentage = motherFraction * 100.0,
                    totalAmountBdt = motherAmount,
                    perPersonAmountBdt = motherAmount,
                    quranicReference = localizedText(uiLang, "সূরা আন-নিসা: ১১", "Surah An-Nisa: 11")
                )
            )
        }

        // 3. Father
        if (isFatherAlive) {
            val fatherAmount = if (hasChildren) {
                netEstate * (1.0 / 6.0)
            } else {
                0.0 // will take as Asabah below
            }
            if (hasChildren) {
                remainingEstate -= fatherAmount
                list.add(
                    HeirShareResult(
                        heirTitleBengali = localizedText(uiLang, "পিতা (নির্দিষ্ট অংশ)", "Father (Fixed Share)"),
                        count = 1,
                        fractionText = localizedText(uiLang, "১/৬ অংশ", "1/6 Share"),
                        percentage = (1.0 / 6.0) * 100.0,
                        totalAmountBdt = fatherAmount,
                        perPersonAmountBdt = fatherAmount,
                        quranicReference = localizedText(uiLang, "সূরা আন-নিসা: ১১", "Surah An-Nisa: 11")
                    )
                )
            }
        }

        // 4. Children & Residuary (Asabah)
        if (sonsCount > 0) {
            // Sons + Daughters together as Asabah (2:1 ratio)
            val totalParts = (sonsCount * 2) + daughtersCount
            val residue = maxOf(0.0, remainingEstate)
            val partValue = residue / totalParts

            val sonsTotalAmount = partValue * (sonsCount * 2)
            list.add(
                HeirShareResult(
                    heirTitleBengali = localizedText(
                        uiLang,
                        "পুত্র ($sonsCount জন)",
                        if (sonsCount == 1) "Son (1)" else "Sons ($sonsCount)"
                    ),
                    count = sonsCount,
                    fractionText = localizedText(uiLang, "আসাবা (কন্যার দ্বিগুণ)", "Asabah (2x Daughter's share)"),
                    percentage = (sonsTotalAmount / netEstate) * 100.0,
                    totalAmountBdt = sonsTotalAmount,
                    perPersonAmountBdt = sonsTotalAmount / sonsCount,
                    quranicReference = localizedText(uiLang, "সূরা আন-নিসা: ১১", "Surah An-Nisa: 11")
                )
            )

            if (daughtersCount > 0) {
                val daughtersTotalAmount = partValue * daughtersCount
                list.add(
                    HeirShareResult(
                        heirTitleBengali = localizedText(
                            uiLang,
                            "কন্যা ($daughtersCount জন)",
                            if (daughtersCount == 1) "Daughter (1)" else "Daughters ($daughtersCount)"
                        ),
                        count = daughtersCount,
                        fractionText = localizedText(uiLang, "আসাবা (পুত্রের অর্ধেক)", "Asabah (1/2 Son's share)"),
                        percentage = (daughtersTotalAmount / netEstate) * 100.0,
                        totalAmountBdt = daughtersTotalAmount,
                        perPersonAmountBdt = daughtersTotalAmount / daughtersCount,
                        quranicReference = localizedText(uiLang, "সূরা আন-নিসা: ১১", "Surah An-Nisa: 11")
                    )
                )
            }
            remainingEstate = 0.0
        } else if (daughtersCount > 0) {
            // Only daughters (no sons)
            val daughtersFraction = if (daughtersCount == 1) 1.0 / 2.0 else 2.0 / 3.0
            val daughtersFractionStr = localizedText(
                uiLang,
                if (daughtersCount == 1) "১/২ অংশ" else "২/৩ অংশ",
                if (daughtersCount == 1) "1/2 Share" else "2/3 Share"
            )
            val daughtersAmount = netEstate * daughtersFraction
            remainingEstate -= daughtersAmount
            list.add(
                HeirShareResult(
                    heirTitleBengali = localizedText(
                        uiLang,
                        if (daughtersCount == 1) "একমাত্র কন্যা" else "কন্যাগণ ($daughtersCount জন সমভাবে)",
                        if (daughtersCount == 1) "Only Daughter" else "Daughters ($daughtersCount equally)"
                    ),
                    count = daughtersCount,
                    fractionText = daughtersFractionStr,
                    percentage = daughtersFraction * 100.0,
                    totalAmountBdt = daughtersAmount,
                    perPersonAmountBdt = daughtersAmount / daughtersCount,
                    quranicReference = localizedText(uiLang, "সূরা আন-নিসা: ১১", "Surah An-Nisa: 11")
                )
            )

            // If father is alive, he takes the remaining as Asabah
            if (isFatherAlive && remainingEstate > 0.0) {
                list.add(
                    HeirShareResult(
                        heirTitleBengali = localizedText(uiLang, "পিতা (আসাবা অবশিষ্টভোগী)", "Father (Asabah Residuary)"),
                        count = 1,
                        fractionText = localizedText(uiLang, "অবশিষ্ট সমুদয়", "Entire Residue"),
                        percentage = (remainingEstate / netEstate) * 100.0,
                        totalAmountBdt = remainingEstate,
                        perPersonAmountBdt = remainingEstate,
                        quranicReference = localizedText(uiLang, "সুন্নাহ ও ইজমা", "Sunnah & Ijma")
                    )
                )
                remainingEstate = 0.0
            }
        } else if (isFatherAlive && remainingEstate > 0.0) {
            // No children, father takes remaining residue
            list.add(
                HeirShareResult(
                    heirTitleBengali = localizedText(uiLang, "পিতা (আসাবা পূর্ণ অবশিষ্ট)", "Father (Asabah Full Residue)"),
                    count = 1,
                    fractionText = localizedText(uiLang, "অবশিষ্ট সমুদয়", "Entire Residue"),
                    percentage = (remainingEstate / netEstate) * 100.0,
                    totalAmountBdt = remainingEstate,
                    perPersonAmountBdt = remainingEstate,
                    quranicReference = localizedText(uiLang, "সূরা আন-নিসা: ১১", "Surah An-Nisa: 11")
                )
            )
            remainingEstate = 0.0
        }

        list
    }

    val currencyFormatter = remember(uiLang) {
        NumberFormat.getNumberInstance(com.example.data.util.SupportedLanguage.fromCode(uiLang).locale)
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
                modifier = Modifier.testTag("inheritance_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = uiText("ফিরে যান", "Back"),
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = uiText("ফারায়েজ ও হালাল ফাইন্যান্স", "Faraidh & Halal Finance"),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = uiText(
                        "কুরআনিক মিরাস বণ্টন ও হালাল সম্পদ পরিশোধন",
                        "Quranic inheritance distribution & Halal wealth purification"
                    ),
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
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiText("ফারায়েজ ক্যালকুলেটর", "Faraidh Calculator"),
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
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiText("সম্পদ পরিশোধন গাইড", "Wealth Purification"),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (selectedTab == 1) ObsidianBackground else TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedTab == 0) {
            // Faraidh (Inheritance) Calculator View
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Step 1: Estate Inputs Card
                item {
                    FrostedGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = DarkEmeraldSurface
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
                                    Text(uiText("১", "1"), color = ChampagneGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Text(
                                    text = uiText(
                                        "তাকায়ূমুত তারাকাহ (মোট রেখে যাওয়া সম্পদ)",
                                        "Tarika Valuation (Total Estate Left)"
                                    ),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = ChampagneGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = totalEstateInput,
                                onValueChange = { totalEstateInput = it },
                                label = { Text(uiText("মোট স্থাবর ও অস্থাবর সম্পত্তির মূল্য (৳)", "Total Movable & Immovable Estate (৳)")) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ChampagneGold,
                                    unfocusedBorderColor = HairlineBorder,
                                    focusedLabelColor = ChampagneGold
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = debtsAndBurialInput,
                                    onValueChange = { debtsAndBurialInput = it },
                                    label = { Text(uiText("দাফন খরচ ও ঋণ (৳)", "Burial & Debts (৳)")) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ChampagneGold,
                                        unfocusedBorderColor = HairlineBorder
                                    )
                                )
                                OutlinedTextField(
                                    value = bequestInput,
                                    onValueChange = { bequestInput = it },
                                    label = { Text(uiText("বৈধ অসিয়ত (সর্বোচ্চ ১/৩)", "Valid Wasiyyah (Max 1/3)")) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ChampagneGold,
                                        unfocusedBorderColor = HairlineBorder
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MintGlow.copy(alpha = 0.12f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = uiText("বণ্টনযোগ্য নিট সম্পত্তি:", "Distributable Net Estate:"),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    Text(
                                        text = "৳ ${currencyFormatter.format(netEstate.toLong())}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = MintGlow,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Step 2: Heirs Selection Card
                item {
                    FrostedGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = DarkEmeraldSurface
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
                                    Text(uiText("২", "2"), color = ChampagneGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Text(
                                    text = uiText("ওয়ারিশদের তালিকা নির্বাচন করুন", "Select Surviving Heirs"),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = ChampagneGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Deceased Gender Selection
                            Text(
                                text = uiText("মৃত ব্যক্তির পরিচয়:", "Deceased Identity:"),
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateGrayMuted)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = deceasedGender == "MALE",
                                    onClick = { deceasedGender = "MALE" },
                                    label = { Text(uiText("পুরুষ (স্ত্রী রেখে গেছেন)", "Male (Leaves Wife)")) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ChampagneGold,
                                        selectedLabelColor = ObsidianBackground
                                    )
                                )
                                FilterChip(
                                    selected = deceasedGender == "FEMALE",
                                    onClick = { deceasedGender = "FEMALE" },
                                    label = { Text(uiText("নারী (স্বামী রেখে গেছেন)", "Female (Leaves Husband)")) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ChampagneGold,
                                        selectedLabelColor = ObsidianBackground
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Counter rows for children & spouse
                            if (deceasedGender == "MALE") {
                                CounterRow(
                                    title = uiText("জীবিত স্ত্রীর সংখ্যা", "Surviving Wives"),
                                    count = wifeCount,
                                    onDecrement = { if (wifeCount > 0) wifeCount-- },
                                    onIncrement = { if (wifeCount < 4) wifeCount++ }
                                )
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = uiText("জীবিত স্বামী আছেন?", "Surviving Husband?"),
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                                    )
                                    Switch(
                                        checked = hasHusband,
                                        onCheckedChange = { hasHusband = it },
                                        colors = SwitchDefaults.colors(checkedThumbColor = ChampagneGold)
                                    )
                                }
                            }

                            CounterRow(
                                title = uiText("জীবিত পুত্রের সংখ্যা", "Surviving Sons"),
                                count = sonsCount,
                                onDecrement = { if (sonsCount > 0) sonsCount-- },
                                onIncrement = { sonsCount++ }
                            )

                            CounterRow(
                                title = uiText("জীবিত কন্যার সংখ্যা", "Surviving Daughters"),
                                count = daughtersCount,
                                onDecrement = { if (daughtersCount > 0) daughtersCount-- },
                                onIncrement = { daughtersCount++ }
                            )

                            // Parents Toggles
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = uiText("পিতা জীবিত আছেন?", "Father Alive?"),
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                                )
                                Switch(
                                    checked = isFatherAlive,
                                    onCheckedChange = { isFatherAlive = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = ChampagneGold)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = uiText("মাতা জীবিত আছেন?", "Mother Alive?"),
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                                )
                                Switch(
                                    checked = isMotherAlive,
                                    onCheckedChange = { isMotherAlive = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = ChampagneGold)
                                )
                            }
                        }
                    }
                }

                // Step 3: Exact Shariah Distribution Results
                item {
                    Text(
                        text = uiText("শরিয়তসম্মত হিস্যা ও প্রাপ্য বণ্টন:", "Shariah Shares & Distribution:"),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                }

                if (heirResults.isEmpty()) {
                    item {
                        Text(
                            text = uiText("কোনো ওয়ারিশ বা বণ্টনযোগ্য সম্পত্তি নেই।", "No heirs selected or distributable estate is zero."),
                            style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted)
                        )
                    }
                } else {
                    items(heirResults) { result ->
                        HeirResultCard(result = result, currencyFormatter = currencyFormatter)
                    }
                }
            }
        } else {
            // Wealth Purification & Halal Finance Hub
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Calculator Card
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
                                Icon(
                                    imageVector = Icons.Filled.AccountBalance,
                                    contentDescription = null,
                                    tint = ChampagneGold,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = uiText(
                                        "ব্যাংক সুদ ও সন্দেহযুক্ত অর্থ পরিশোধন",
                                        "Bank Interest & Doubtful Wealth Purification"
                                    ),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = ChampagneGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = uiText(
                                    "ব্যাংক একাউন্টে জমাকৃত সুদ বা অনিচ্ছাকৃত কোনো সন্দেহযুক্ত উপার্জন থাকলে, তা সাওয়াবের নিয়ত ব্যতীত নিঃশর্তভাবে কোনো অভাবী বা জনকল্যাণমূলক কাজে বিতরণ করে মূল সম্পদকে হালাল রাখতে হয়।",
                                    "Any interest accrued in bank accounts or unintended doubtful income must be given away unconditionally to the needy or public welfare—without expecting spiritual reward (Sadaqah)—to keep your core wealth Halal."
                                ),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextPrimary.copy(alpha = 0.85f),
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = interestReceivedInput,
                                onValueChange = { interestReceivedInput = it },
                                label = { Text(uiText("ব্যাংক সুদ বাবদ প্রাপ্ত অর্থ (৳)", "Bank Interest Received (৳)")) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ChampagneGold,
                                    unfocusedBorderColor = HairlineBorder
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = doubtfulIncomeInput,
                                onValueChange = { doubtfulIncomeInput = it },
                                label = { Text(uiText("অন্যান্য সন্দেহযুক্ত বা মিশ্রিত অর্থ (৳)", "Other Doubtful or Mixed Earnings (৳)")) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ChampagneGold,
                                    unfocusedBorderColor = HairlineBorder
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            val interestAmt = interestReceivedInput.toDoubleOrNull() ?: 0.0
                            val doubtfulAmt = doubtfulIncomeInput.toDoubleOrNull() ?: 0.0
                            val totalPurificationAmt = interestAmt + doubtfulAmt

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF2C1114))
                                    .border(0.5.dp, Color(0xFF8B2635), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = uiText("পরিশোধনযোগ্য মোট অর্থ:", "Total Amount to Purify:"),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color(0xFFFFB4AB),
                                                fontSize = 11.sp
                                            )
                                        )
                                        Text(
                                            text = uiText("(সাওয়াবের নিয়ত ব্যতীত সদকা করুন)", "(Give away without expecting Thawab)"),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = SlateGrayMuted,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                    Text(
                                        text = "৳ ${currencyFormatter.format(totalPurificationAmt.toLong())}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = Color(0xFFFFB4AB),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Core Islamic Investing & Earnings Principles
                item {
                    FrostedGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = DarkEmeraldSurface
                    ) {
                        Column {
                            Text(
                                text = uiText(
                                    "হালাল বিনিয়োগ ও উপার্জনের ৪ মূলনীতি:",
                                    "4 Core Principles of Halal Finance:"
                                ),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = ChampagneGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            listOf(
                                uiText("১. রিবা (সুদ) পরিহার:", "1. Avoid Riba (Interest):") to
                                    uiText(
                                        "নির্দিষ্ট হারে গ্যারান্টিযুক্ত অতিরিক্ত লাভ বা ঋণের ওপর সুদ গ্রহণ সম্পূর্ণ হারাম (সূরা আল-বাক্বারাহ: ২৭৫)।",
                                        "Guaranteed fixed returns or charging interest on loans is strictly forbidden (Surah Al-Baqarah: 275)."
                                    ),
                                uiText("২. গারার (প্রতারণামূলক অনিশ্চয়তা) বর্জন:", "2. Avoid Gharar (Excessive Uncertainty):") to
                                    uiText(
                                        "পণ্যের গুণমান বা সরবরাহে লুকোচুরি কিংবা অতিমাত্রিক অনিশ্চিত চুক্তি নিষিদ্ধ।",
                                        "Contracts involving concealment of product quality, delivery, or excessive ambiguity are prohibited."
                                    ),
                                uiText("৩. মায়সির (জুয়া ও স্পেকুলেশন) পরিহার:", "3. Avoid Maysir (Gambling & Speculation):") to
                                    uiText(
                                        "লটারি, অপশন ট্রেডিং বা নিখাদ ভাগ্যের ওপর নির্ভরশীল ফাটকাবাজি শরিয়তে নিষিদ্ধ।",
                                        "Lotteries, speculative derivatives, and chance-based wagering are impermissible in Shariah."
                                    ),
                                uiText("৪. প্রকৃত অংশীদারিত্ব (মুদারাবা/মুশারাকা):", "4. Profit & Risk Sharing (Mudarabah/Musharakah):") to
                                    uiText(
                                        "লাভের পাশাপাশি আনুপাতিক ঝুঁকি ও ব্যবসায়িক ক্ষতি বহনের ভিত্তিতেই বিনিয়োগ হালাল হয়।",
                                        "Permissible investment requires real asset backing and proportionate sharing of both profit and business risk."
                                    )
                            ).forEach { (title, desc) ->
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MintGlow,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Text(
                                        text = desc,
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
    }
}

@Composable
fun CounterRow(
    title: String,
    count: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    val uiLang = LocalAppLanguage.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = onDecrement, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Filled.Remove, contentDescription = uiText("কমান", "Decrease"), tint = SlateGrayMuted)
            }
            Text(
                text = uiText("${formatLocalizedInt(count, uiLang)} জন", "$count"),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = ChampagneGold,
                    fontWeight = FontWeight.Bold
                )
            )
            IconButton(onClick = onIncrement, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Filled.Add, contentDescription = uiText("বাড়ান", "Increase"), tint = ChampagneGold)
            }
        }
    }
}

@Composable
fun HeirResultCard(
    result: HeirShareResult,
    currencyFormatter: NumberFormat
) {
    FrostedGlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = DarkEmeraldSurface,
        borderColor = ChampagneGold.copy(alpha = 0.25f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.heirTitleBengali,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = result.fractionText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    )
                    Text("•", color = SlateGrayMuted, fontSize = 10.sp)
                    Text(
                        text = "(${String.format(Locale.US, "%.1f", result.percentage)}%)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateGrayMuted,
                            fontSize = 10.sp
                        )
                    )
                    Text("•", color = SlateGrayMuted, fontSize = 10.sp)
                    Text(
                        text = result.quranicReference,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MintGlow,
                            fontSize = 10.sp
                        )
                    )
                }

                if (result.count > 1) {
                    Text(
                        text = uiText(
                            "জনপ্রতি: ৳ ${currencyFormatter.format(result.perPersonAmountBdt.toLong())}",
                            "Per person: ৳ ${currencyFormatter.format(result.perPersonAmountBdt.toLong())}"
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateGrayMuted,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Text(
                text = "৳ ${currencyFormatter.format(result.totalAmountBdt.toLong())}",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MintGlow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            )
        }
    }
}
