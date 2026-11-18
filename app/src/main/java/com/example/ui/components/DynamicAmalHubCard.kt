package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AmalChecklistItem
import com.example.data.model.AmalPlanType
import com.example.data.model.DynamicAmalPlan
import com.example.data.model.DynamicHijriAmalProvider
import com.example.ui.theme.*
import java.util.Calendar

/**
 * Dynamic Hijri & Friday-Aware Amal Hub.
 * Automatically displays Jumat Special Hub on Fridays (or Thursday evening Maghrib onward),
 * and dynamic Islamic day-specific deeds (Ayyam al-Bid, Monday/Thursday, Ramadan, Dhul Hijjah, Daily Sunnah) on other days.
 */
@Composable
fun DynamicAmalHubCard(
    salawatCount: Int,
    onIncrementSalawat: () -> Unit,
    onOpenSurahKahf: () -> Unit,
    modifier: Modifier = Modifier
) {
    val plan = remember {
        DynamicHijriAmalProvider.getTodayAmalPlan(Calendar.getInstance())
    }

    if (plan.type == AmalPlanType.JUMAT_SPECIAL) {
        JumatSpecialHubCard(
            salawatCount = salawatCount,
            onIncrementSalawat = onIncrementSalawat,
            onOpenSurahKahf = onOpenSurahKahf,
            modifier = modifier
        )
    } else {
        DailyAmalHubCard(
            plan = plan,
            modifier = modifier
        )
    }
}

@Composable
private fun DailyAmalHubCard(
    plan: DynamicAmalPlan,
    modifier: Modifier = Modifier
) {
    val uiLang = LocalAppLanguage.current
    val haptic = LocalHapticFeedback.current
    var isExpanded by remember { mutableStateOf(true) }

    // Checklist state for today's items
    val checkedMap = remember { mutableStateMapOf<String, Boolean>() }
    val completedCount = checkedMap.values.count { it }
    val totalCount = plan.checklistItems.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "amal_progress")

    val badgeColor = remember(plan.badgeColorHex) {
        try {
            Color(android.graphics.Color.parseColor(plan.badgeColorHex))
        } catch (_: Exception) {
            ChampagneGold
        }
    }

    val localizedVirtue = plan.localizedFastingVirtue(uiLang)

    FrostedGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dynamic_amal_hub_card"),
        backgroundColor = DarkEmeraldElevated,
        borderColor = badgeColor.copy(alpha = 0.45f)
    ) {
        // Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .testTag("dynamic_amal_hub_toggle"),
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
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(badgeColor.copy(alpha = 0.25f), MintGlow.copy(alpha = 0.15f))
                            )
                        )
                        .border(0.5.dp, badgeColor.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (plan.isFastingDay) Icons.Filled.NightsStay else Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = badgeColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = plan.localizedTitle(uiLang),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = ChampagneGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = plan.localizedSubtitle(uiLang),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 11.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Hijri Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .border(0.5.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = plan.localizedHijriDayBadge(uiLang),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = badgeColor,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.5.sp
                        )
                    )
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = uiText("টগল করুন", "Toggle"),
                        tint = TextTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Expandable Content
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Fasting Announcement Bar if applicable
                if (plan.isFastingDay && !localizedVirtue.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(MintGlow.copy(alpha = 0.14f), DarkEmeraldSurface)
                                )
                            )
                            .border(0.5.dp, MintGlow.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 9.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "🌙", fontSize = 16.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = uiText("সুন্নাত সিয়াম (রোজা) ও ফযিলত", "Sunnah Fasting & Virtues"),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = MintGlow,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                )
                                Text(
                                    text = localizedVirtue,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextPrimary.copy(alpha = 0.85f),
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Progress Indicator Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiText("আজকের নেক আমলের তালিকা", "Today's Good Deeds Checklist"),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                    Text(
                        text = uiText(
                            "${formatLocalizedInt(completedCount, uiLang)} / ${formatLocalizedInt(totalCount, uiLang)} সম্পন্ন ${if (completedCount == totalCount && totalCount > 0) "🎉 মাশাআল্লাহ" else ""}",
                            "${formatLocalizedInt(completedCount, uiLang)} / ${formatLocalizedInt(totalCount, uiLang)} completed ${if (completedCount == totalCount && totalCount > 0) "🎉 MashaAllah" else ""}"
                        ),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (completedCount == totalCount && totalCount > 0) MintGlow else ChampagneGold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp
                        )
                    )
                }

                // Progress Bar
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (completedCount == totalCount && totalCount > 0) MintGlow else ChampagneGold,
                    trackColor = DarkEmeraldSurface
                )

                // Checklist Items
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    plan.checklistItems.forEach { item ->
                        AmalChecklistRow(
                            item = item,
                            uiLang = uiLang,
                            isChecked = checkedMap[item.id] == true,
                            onToggle = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                checkedMap[item.id] = !(checkedMap[item.id] ?: false)
                            }
                        )
                    }
                }

                // Footer Hadith Reference
                val localizedPlanRef = plan.localizedHadithReference(uiLang)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiText("📖 রেফারেন্স: $localizedPlanRef", "📖 Reference: $localizedPlanRef"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextTertiary,
                            fontSize = 10.5.sp
                        )
                    )

                    Text(
                        text = uiText("হিজরি ক্যালেন্ডার ভিত্তিক", "Based on Hijri Calendar"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextTertiary,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun AmalChecklistRow(
    item: AmalChecklistItem,
    uiLang: String,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    val subText = item.localizedSubText(uiLang)
    val hadithRef = item.localizedHadithRef(uiLang)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isChecked) MintGlow.copy(alpha = 0.08f) else DarkEmeraldSurface)
            .border(
                width = 0.5.dp,
                color = if (isChecked) MintGlow.copy(alpha = 0.35f) else HairlineBorder,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onToggle() }
            .padding(horizontal = 10.dp, vertical = 9.dp)
            .testTag("amal_checklist_${item.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (isChecked) MintGlow else Color.Transparent)
                .border(
                    width = 1.dp,
                    color = if (isChecked) MintGlow else TextTertiary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isChecked) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = ObsidianBackground,
                    modifier = Modifier.size(13.dp)
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.localizedTitle(uiLang),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isChecked) TextPrimary.copy(alpha = 0.6f) else TextPrimary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.5.sp
                )
            )
            if (!subText.isNullOrBlank()) {
                Text(
                    text = subText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextTertiary,
                        fontSize = 10.5.sp,
                        lineHeight = 14.sp
                    )
                )
            }
        }

        if (!hadithRef.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = hadithRef,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextTertiary,
                        fontSize = 9.5.sp
                    )
                )
            }
        }
    }
}
