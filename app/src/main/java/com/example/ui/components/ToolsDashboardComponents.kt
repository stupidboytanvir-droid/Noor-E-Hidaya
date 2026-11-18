package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.preference.AppPreferences
import com.example.ui.theme.*
import kotlinx.coroutines.launch

/**
 * 📊 1. TOP AMAL TRACKER CARD (আমল প্রোগ্রেস বার)
 */
@Composable
fun TopAmalTrackerCard(
    completedCount: Int,
    totalCount: Int = 14,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiLang = LocalAppLanguage.current
    val percentage = if (totalCount > 0) (completedCount * 100) / totalCount else 0
    val progressFraction = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    FrostedGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("top_amal_tracker_card"),
        cornerRadius = 16.dp,
        backgroundColor = DarkEmeraldSurface,
        borderColor = ChampagneGold.copy(alpha = 0.35f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Circular Progress Ring (52dp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier.size(54.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeW = 4.5.dp.toPx()
                        val diameter = size.minDimension - strokeW
                        val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
                        val arcSize = Size(diameter, diameter)

                        // Track Background
                        drawArc(
                            color = HairlineBorder,
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeW, cap = StrokeCap.Round)
                        )

                        // Progress Foreground
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(ChampagneGold, MintGlow, ChampagneGold)
                            ),
                            startAngle = -90f,
                            sweepAngle = 360f * progressFraction,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeW, cap = StrokeCap.Round)
                        )
                    }

                    // Percentage Text in Center
                    Text(
                        text = "${formatLocalizedInt(percentage, uiLang)}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }

                // Center Column Info
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = uiText("আজকের আমল ট্র্যাক করুন", "Track today's Amal"),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MintGlow.copy(alpha = 0.15f))
                                .border(0.5.dp, MintGlow.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${formatLocalizedInt(completedCount, uiLang)}/${formatLocalizedInt(totalCount, uiLang)}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MintGlow,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = uiText(
                            "আজকের ১৪টি ফরজ ও সুন্নাত আমলের আত্মিক হিসাব",
                            "Spiritual checklist for today's 14 Fard & Sunnah deeds"
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateGrayMuted,
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Right Chevron Navigation Arrow (48dp target)
            Box(
                modifier = Modifier
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = uiText("আমল ট্র্যাকার খুলুন", "Open Amal Tracker"),
                    tint = ChampagneGold,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Data Model for the 14 Daily Amals
 */
data class AmalItem(
    val key: String,
    val titleBengali: String,
    val subtitleBengali: String,
    val categoryBengali: String,
    val titleEnglish: String = "",
    val subtitleEnglish: String = "",
    val categoryEnglish: String = ""
) {
    fun localizedTitle(lang: String): String =
        LocalizationRegistry.translate(lang, titleBengali, titleEnglish.ifBlank { titleBengali })

    fun localizedSubtitle(lang: String): String =
        LocalizationRegistry.translate(lang, subtitleBengali, subtitleEnglish.ifBlank { subtitleBengali })

    fun localizedCategory(lang: String): String =
        LocalizationRegistry.translate(lang, categoryBengali, categoryEnglish.ifBlank { categoryBengali })
}

val DAILY_14_AMALS = listOf(
    // পঞ্চওয়াক্ত ফরজ নামাজ
    AmalItem("fajr", "ফজর নামাজ", "ওয়াক্তমত জামা'আতের সাথে আদায়", "ফরজ সালাত", "Fajr Prayer", "Pray on time with congregation", "Fard Salah"),
    AmalItem("dhuhr", "যোহর নামাজ", "৪ রাকাত সুন্নত ও ফরজ আদায়", "ফরজ সালাত", "Dhuhr Prayer", "4 Rak'at Sunnah & Fard prayer", "Fard Salah"),
    AmalItem("asr", "আসর নামাজ", "ওয়াক্ত শুরুর সাথে সাথে সালাত", "ফরজ সালাত", "Asr Prayer", "Offer prayer promptly in its time", "Fard Salah"),
    AmalItem("maghrib", "মাগরিব নামাজ", "আউয়াল ওয়াক্তে ৩ রাকাত ফরজ", "ফরজ সালাত", "Maghrib Prayer", "3 Rak'at Fard at early Waqt", "Fard Salah"),
    AmalItem("isha", "ইশা নামাজ ও বিতর", "ইশার ফরজ ও ৩ রাকাত বিতর সালাত", "ফরজ সালাত", "Isha & Witr Prayer", "Isha Fard & 3 Rak'at Witr", "Fard Salah"),
    // নফল সালাত ও কিয়াম
    AmalItem("tahajjud", "তাহাজ্জুদ সালাত", "শেষ রাতে কিয়ামুল লাইল ও কান্নাকাটি", "নফল কিয়াম", "Tahajjud Prayer", "Night prayer & heartfelt supplication", "Nafl Qiyam"),
    AmalItem("ishraq_duha", "ইশরাক ও চাশত (দুহা)", "সূর্যোদয়ের পর ২/৪ রাকাত সালাতুদ দুহা", "নফল সালাত", "Ishraq & Duha", "2 or 4 Rak'at after sunrise", "Nafl Salah"),
    // কুরআন ও যিকির
    AmalItem("quran_tilawat", "কুরআন তিলাওয়াত", "কমপক্ষে ১ রুকু বা ১০টি আয়াত তাদাব্বুর", "কুরআনুল কারীম", "Quran Recitation", "At least 1 Ruku or 10 Ayahs with reflection", "Holy Quran"),
    AmalItem("morning_evening_azkar", "সকাল-সন্ধ্যার আযকার", "ফজর ও মাগরিব পরবর্তী মাসনুন দোয়া", "দৈনিক যিকির", "Morning & Evening Adhkar", "Masnun Du'as after Fajr & Maghrib", "Daily Dhikr"),
    AmalItem("tasbeeh_dhikr", "১০০ বার তাসবীহ পাঠ", "সুবহানাল্লাহ, আলহামদুলিল্লাহ, আল্লাহু আকবার", "মাসনুন তাসবীহ", "100x Tasbeeh", "SubhanAllah, Alhamdulillah, Allahu Akbar", "Masnun Tasbeeh"),
    // আত্মশুদ্ধি ও নেক আমল
    AmalItem("durood_salawat", "দরূদ ও সালাওয়াত", "রাসূলুল্লাহ ﷺ এর প্রতি ভক্তিভরে দরূদ", "সালাওয়াত", "Durood & Salawat", "Send heartfelt blessings upon the Prophet ﷺ", "Salawat"),
    AmalItem("istighfar", "ইস্তিগফার ৭০-১০০ বার", "আস্তাগফিরুল্লাহ ওয়া আতূবু ইলাইহি", "তওবা ও মাগফিরাত", "Istighfar (70–100x)", "Astaghfirullah wa atubu ilayh", "Repentance"),
    AmalItem("sadaqah", "সদকা ও ভালো আচরণ", "দান, মুখে হাসি বা অন্যকে সাহায্য করা", "সদকায়ে জারিয়া", "Sadaqah & Kindness", "Charity, a warm smile, or helping someone", "Sadaqah"),
    AmalItem("sunnah_fasting", "নফল সিয়াম বা রোজা", "সোমবার/বৃহস্পতিবার বা আইয়ামে বিজ রোজা", "সিয়াম সাধনা", "Sunnah Fasting", "Monday/Thursday or Ayyam al-Beed fast", "Fasting")
)

/**
 * Interactive Amal Tracker Dialog (14 Daily Deeds)
 */
@Composable
fun AmalTrackerDialog(
    dateString: String,
    checkedAmals: Set<String>,
    onToggleAmal: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val uiLang = LocalAppLanguage.current
    val completedCount = checkedAmals.size
    val percentage = (completedCount * 100) / DAILY_14_AMALS.size
    val haptic = LocalHapticFeedback.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkEmeraldSurface,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiText("দৈনিক আমল ট্র্যাকার", "Daily Amal Tracker"),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (completedCount == 14) MintGlow.copy(alpha = 0.2f) else DarkEmeraldElevated)
                            .border(0.5.dp, if (completedCount == 14) MintGlow else ChampagneGold, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${formatLocalizedInt(completedCount, uiLang)}/${formatLocalizedInt(14, uiLang)} (${formatLocalizedInt(percentage, uiLang)}%)",
                            color = if (completedCount == 14) MintGlow else ChampagneGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = uiText(
                        "তারিখ: $dateString • আপনার আত্মিক উন্নতির দৈনিক হিসাব",
                        "Date: $dateString • Daily spiritual growth checklist"
                    ),
                    color = SlateGrayMuted,
                    fontSize = 11.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (completedCount == 14) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MintGlow.copy(alpha = 0.15f))
                            .border(0.5.dp, MintGlow, RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiText(
                                "🎉 আলহামদুলিল্লাহ! আজকের ১৪টি আমল সফলভাবে সম্পন্ন হয়েছে!",
                                "🎉 Alhamdulillah! All 14 daily deeds completed today!"
                            ),
                            color = MintGlow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                DAILY_14_AMALS.forEach { amal ->
                    val isChecked = checkedAmals.contains(amal.key)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isChecked) DarkEmeraldElevated else DarkEmeraldSurface)
                            .border(
                                0.5.dp,
                                if (isChecked) MintGlow.copy(alpha = 0.5f) else HairlineBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                onToggleAmal(amal.key)
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = amal.localizedTitle(uiLang),
                                    color = if (isChecked) Color.White else TextPrimary,
                                    fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(DarkEmeraldSurface)
                                        .border(0.5.dp, ChampagneGold.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = amal.localizedCategory(uiLang),
                                        color = ChampagneGold,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = amal.localizedSubtitle(uiLang),
                                color = SlateGrayMuted,
                                fontSize = 11.sp
                            )
                        }

                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = {
                                onToggleAmal(amal.key)
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MintGlow,
                                checkmarkColor = ObsidianBackground,
                                uncheckedColor = SlateGrayMuted
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChampagneGold,
                    contentColor = ObsidianBackground
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(uiText("সম্পন্ন", "Done"), fontWeight = FontWeight.Bold)
            }
        }
    )
}

/**
 * 📚 2. "REQUIRED KNOWLEDGE" SECTION (প্রয়োজনীয় জ্ঞান)
 */
data class KnowledgeItem(
    val id: String,
    val titleEnglish: String = "",
    val titleBengali: String,
    val icon3DType: Icon3DType,
    val onClick: () -> Unit
) {
    fun localizedTitle(lang: String): String =
        LocalizationRegistry.translate(lang, titleBengali, titleEnglish.ifBlank { titleBengali })
}

@Composable
fun RequiredKnowledgeSection(
    items: List<KnowledgeItem>,
    modifier: Modifier = Modifier
) {
    val uiLang = LocalAppLanguage.current
    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = uiText("প্রয়োজনীয় জ্ঞান", "Required Knowledge"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = uiText(
                        "প্রয়োজনীয় দ্বীনি জ্ঞান ও মৌলিক ইবাদতসমূহ",
                        "Essential Islamic knowledge & core acts of worship"
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SlateGrayMuted,
                        fontSize = 11.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Horizontal Row of Circular Icon Buttons
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(items) { item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable(onClick = item.onClick)
                        .testTag("knowledge_item_${item.id}")
                ) {
                    // Circular Dark Emerald Medallion (48dp)
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        DarkEmeraldElevated,
                                        DarkEmeraldSurface,
                                        ObsidianBackground
                                    )
                                )
                            )
                            .border(1.2.dp, ChampagneGold.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        ThreeDimensionalIcon(
                            type = item.icon3DType,
                            size = 26.dp,
                            isActive = true,
                            accentColor = ChampagneGold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Concise Localized Label
                    Text(
                        text = item.localizedTitle(uiLang),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * 🛠️ 3. "TOOLS" SECTION (মূল টুলস ও ইউটিলিটি গ্রিড - কম্প্যাক্ট ২ কলাম)
 */
data class ToolCardItem(
    val id: String,
    val titleBengali: String,
    val subtitleBengali: String = "",
    val icon3DType: Icon3DType,
    val isPremiumCrown: Boolean = false,
    val statusBadge: String = "",
    val statusColor: Color = MintGlow,
    val titleEnglish: String = "",
    val subtitleEnglish: String = "",
    val statusBadgeEnglish: String = "",
    val onClick: () -> Unit
) {
    fun localizedTitle(lang: String): String =
        LocalizationRegistry.translate(lang, titleBengali, titleEnglish.ifBlank { titleBengali })

    fun localizedSubtitle(lang: String): String =
        LocalizationRegistry.translate(lang, subtitleBengali, subtitleEnglish.ifBlank { subtitleBengali })

    fun localizedStatusBadge(lang: String): String =
        LocalizationRegistry.translate(lang, statusBadge, statusBadgeEnglish.ifBlank { statusBadge })
}

@Composable
fun ToolsGridSection(
    tools: List<ToolCardItem>,
    modifier: Modifier = Modifier
) {
    val uiLang = LocalAppLanguage.current
    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = uiText("টুলস ও ইউটিলিটি", "Tools & Utilities"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = uiText("প্রয়োজনীয় দৈনন্দিন ইসলামিক সেবা", "Essential daily Islamic services"),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SlateGrayMuted,
                        fontSize = 11.5.sp
                    )
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkEmeraldSurface)
                    .border(0.5.dp, HairlineBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = uiText("${formatLocalizedInt(tools.size, uiLang)}টি টুলস", "${tools.size} Tools"),
                    color = ChampagneGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2-Column Compact Grid (Balanced density & complete label visibility)
        val toolPairs = remember(tools) { tools.chunked(2) }
        toolPairs.forEach { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                pair.forEach { tool ->
                    Box(modifier = Modifier.weight(1f)) {
                        ToolCardView(tool = tool)
                    }
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun ToolCardView(tool: ToolCardItem) {
    val uiLang = LocalAppLanguage.current
    val localizedBadge = tool.localizedStatusBadge(uiLang)
    val localizedSubtitle = tool.localizedSubtitle(uiLang)

    FrostedGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(108.dp)
            .clickable(onClick = tool.onClick)
            .testTag("tool_card_${tool.id}"),
        cornerRadius = 14.dp,
        backgroundColor = DarkEmeraldSurface,
        borderColor = ChampagneGold.copy(alpha = 0.22f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 9.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: 38dp Icon Container with Crown + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(DarkEmeraldElevated, ObsidianBackground)
                                )
                            )
                            .border(
                                0.5.dp,
                                Brush.linearGradient(
                                    listOf(ChampagneGold.copy(alpha = 0.45f), Color.Transparent)
                                ),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        ThreeDimensionalIcon(
                            type = tool.icon3DType,
                            size = 22.dp,
                            isActive = true,
                            accentColor = tool.statusColor
                        )
                    }

                    // Gold Crown Badge 👑 on top-right of the icon container
                    if (tool.isPremiumCrown) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-4).dp)
                                .clip(CircleShape)
                                .background(ChampagneGold)
                                .border(0.5.dp, ObsidianBackground, CircleShape)
                                .padding(horizontal = 3.dp, vertical = 0.5.dp)
                        ) {
                            Text(
                                text = "👑",
                                fontSize = 7.sp,
                                lineHeight = 9.sp
                            )
                        }
                    }
                }

                // Status pill badge if provided
                if (localizedBadge.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(tool.statusColor.copy(alpha = 0.12f))
                            .border(0.5.dp, tool.statusColor.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(tool.statusColor)
                        )
                        Text(
                            text = localizedBadge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = tool.statusColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            // Bottom Section: Prominent Short Title & Functional Sub-Label
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(1.5.dp)
            ) {
                Text(
                    text = tool.localizedTitle(uiLang),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 13.5.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (localizedSubtitle.isNotBlank()) {
                    Text(
                        text = localizedSubtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.5.sp,
                            color = SlateGrayMuted
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * ⏰ 4. "REMINDER" SECTION (রিমাইন্ডার ও অ্যালার্ট)
 */
data class ReminderHubItem(
    val id: String,
    val titleBengali: String,
    val subtitleBengali: String,
    val isEnabled: Boolean,
    val titleEnglish: String = "",
    val subtitleEnglish: String = "",
    val onToggle: (Boolean) -> Unit
) {
    fun localizedTitle(lang: String): String =
        LocalizationRegistry.translate(lang, titleBengali, titleEnglish.ifBlank { titleBengali })

    fun localizedSubtitle(lang: String): String =
        LocalizationRegistry.translate(lang, subtitleBengali, subtitleEnglish.ifBlank { subtitleBengali })
}

@Composable
fun ReminderSection(
    reminders: List<ReminderHubItem>,
    modifier: Modifier = Modifier
) {
    val uiLang = LocalAppLanguage.current
    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = uiText("রিমাইন্ডার", "Reminders"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = uiText(
                        "নামাজ, রোজা ও আমলের সতর্কবার্তা ও অ্যালার্ট",
                        "Alerts & reminders for Salah, fasting & daily deeds"
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SlateGrayMuted,
                        fontSize = 11.sp
                    )
                )
            }

            Icon(
                imageVector = Icons.Filled.NotificationsActive,
                contentDescription = null,
                tint = ChampagneGold,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Vertical List of Reminder Cards
        reminders.forEach { reminder ->
            FrostedGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("reminder_card_${reminder.id}"),
                cornerRadius = 14.dp,
                backgroundColor = DarkEmeraldSurface,
                borderColor = HairlineBorder
            ) {
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
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkEmeraldElevated)
                                .border(0.5.dp, if (reminder.isEnabled) MintGlow.copy(alpha = 0.4f) else HairlineBorder, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (reminder.id) {
                                    "prayer_alerts" -> Icons.Filled.AccessTimeFilled
                                    "sahur_iftar" -> Icons.Filled.NightsStay
                                    "jumuah_kahf" -> Icons.AutoMirrored.Filled.MenuBook
                                    else -> Icons.Filled.Campaign
                                },
                                contentDescription = null,
                                tint = if (reminder.isEnabled) MintGlow else SlateGrayMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = reminder.localizedTitle(uiLang),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = reminder.localizedSubtitle(uiLang),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SlateGrayMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Switch(
                        checked = reminder.isEnabled,
                        onCheckedChange = reminder.onToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ObsidianBackground,
                            checkedTrackColor = MintGlow,
                            uncheckedThumbColor = SlateGrayMuted,
                            uncheckedTrackColor = DarkEmeraldElevated
                        ),
                        modifier = Modifier.testTag("switch_${reminder.id}")
                    )
                }
            }
        }
    }
}

/**
 * 📅 5. HIJRI CALENDAR DIALOG
 */
@Composable
fun HijriCalendarDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkEmeraldSurface,
        title = {
            Text(
                text = uiText("ইসলামিক হিজরি ক্যালেন্ডার", "Islamic Hijri Calendar"),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Today's Hijri Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(DarkEmeraldElevated, DarkEmeraldSurface)
                            )
                        )
                        .border(1.dp, ChampagneGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = uiText("আজকের হিজরি তারিখ", "Today's Hijri Date"),
                            color = ChampagneGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiText("১৫ রবিউস সানি ১৪৪৮ হিজরি", "15 Rabi' al-Thani 1448 AH"),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiText("১ নভেম্বর ২০২৬ খ্রিষ্টাব্দ • রবিবার", "1 November 2026 CE • Sunday"),
                            color = SlateGrayMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = uiText("গুরুত্বপূর্ণ ইসলামিক দিবস ও নফল সিয়াম:", "Key Islamic Dates & Sunnah Fasts:"),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                val events = listOf(
                    uiText("আইয়ামে বিজ (১৩, ১৪, ১৫ রবিউস সানি)", "Ayyam al-Beed (13th, 14th, 15th)") to
                        uiText("নফল রোজা রাখার বিশেষ সুন্নাত", "Recommended Sunnah white-day fasts"),
                    uiText("জুমাতুল বিদা ও মাহে রমজান", "Jumu'atul-Wida & Ramadan") to
                        uiText("পবিত্র সিয়াম ও ইবাদতের মাস", "Blessed month of fasting & worship"),
                    uiText("লাইলাতুল কদর ও শবে বরাত", "Laylatul Qadr & Mid-Sha'ban") to
                        uiText("মাগফিরাত ও বিশেষ ইবাদতের রজনী", "Nights of forgiveness & deep devotion"),
                    uiText("ঈদুল ফিতর ও ঈদুল আজহা", "Eid al-Fitr & Eid al-Adha") to
                        uiText("মুসলিম উম্মাহর মহান আনন্দ উৎসব", "Joyous celebrations of the Ummah"),
                    uiText("আশুরা (১০ মহররম) ও আরাফাহ", "Ashura (10 Muharram) & Arafah") to
                        uiText("গুনাহ মাফের সুন্নাত রোজা", "Sunnah fasts of expiation")
                )

                events.forEach { (name, desc) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkEmeraldElevated)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ChampagneGold)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(text = desc, color = SlateGrayMuted, fontSize = 10.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChampagneGold,
                    contentColor = ObsidianBackground
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(uiText("ঠিক আছে", "OK"), fontWeight = FontWeight.Bold)
            }
        }
    )
}

/**
 * 📜 6. HADITH VIEWER DIALOG
 */
@Composable
fun HadithViewerDialog(
    onDismiss: () -> Unit
) {
    val contentLang = LocalContentLanguage.current
    val hadiths = listOf(
        listOf(
            "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ",
            "নিশ্চয়ই সমস্ত কাজের ফলাফল নিয়তের ওপর নির্ভরশীল।",
            "Actions are judged solely by their intentions.",
            "হযরত উমর ইবনুল খাত্তাব (রাঃ) • সহীহ বুখারী: ১",
            "Umar ibn al-Khattab (RA) • Sahih al-Bukhari: 1"
        ),
        listOf(
            "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ",
            "তোমাদের মধ্যে সর্বোত্তম ব্যক্তি সে, যে নিজে কুরআন শিখে এবং অন্যকে তা শেখায়।",
            "The best among you are those who learn the Quran and teach it.",
            "হযরত উসমান ইবনে আফফান (রাঃ) • সহীহ বুখারী: ৫০২৭",
            "Uthman ibn Affan (RA) • Sahih al-Bukhari: 5027"
        ),
        listOf(
            "مَنْ دَلَّ عَلَى خَيْرٍ فَلَهُ مِثْلُ أَجْرِ فَاعِلِهِ",
            "যে ব্যক্তি কোনো সৎকাজের পথ দেখায়, সে ওই কাজ সম্পাদনকারীর সমান সওয়াব পাবে।",
            "Whoever guides someone to goodness will have a reward like the one who did it.",
            "হযরত আবু মাসউদ আল-আনসারী (রাঃ) • সহীহ মুসলিম: ১৮৯৩",
            "Abu Mas'ud al-Ansari (RA) • Sahih Muslim: 1893"
        )
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkEmeraldSurface,
        title = {
            Text(
                text = uiText("সহীহ হাদিস ও সুন্নাহ সংকলন", "Authentic Hadith & Sunnah"),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                hadiths.forEach { item ->
                    val arabic = item[0]
                    val translation = localizedText(contentLang, item[1], item[2])
                    val ref = localizedText(contentLang, item[3], item[4])
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkEmeraldElevated)
                            .border(0.5.dp, ChampagneGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = arabic,
                                color = ChampagneGold,
                                fontFamily = AmiriFontFamily,
                                fontSize = 17.sp,
                                lineHeight = 28.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = translation,
                                color = Color.White,
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = ref,
                                color = MintGlow,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChampagneGold,
                    contentColor = ObsidianBackground
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(uiText("পড়েছি", "Done"), fontWeight = FontWeight.Bold)
            }
        }
    )
}
