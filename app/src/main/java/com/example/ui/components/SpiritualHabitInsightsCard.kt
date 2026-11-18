package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MonthlyWorshipEntity
import com.example.data.repository.WorshipTrackerRepository
import com.example.ui.theme.*

@Composable
fun SpiritualHabitInsightsCard(
    streakDays: Int,
    quranMinutesToday: Int,
    completedPrayersToday: Int = 0,
    duasReadCount: Int = 0,
    totalTasbihCount: Int = 0,
    salawatTotal: Int = 0,
    hadithMinutes: Int = 0,
    monthlyHistory: List<MonthlyWorshipEntity> = emptyList(),
    onOpenMonthlyAnalytics: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiLang = LocalAppLanguage.current
    // Build real 6-month trend bars from Room archive + current active month (zero-based for new users)
    val recent6Months = remember(
        monthlyHistory,
        quranMinutesToday,
        duasReadCount,
        totalTasbihCount,
        salawatTotal,
        hadithMinutes,
        uiLang
    ) {
        val full12 = WorshipTrackerRepository.build12MonthSeries(monthlyHistory)
        val last6 = full12.takeLast(6)
        last6.mapIndexed { idx, entity ->
            val effectiveTotal = if (idx == last6.lastIndex) {
                maxOf(entity.tasbihCount, totalTasbihCount) +
                    maxOf(entity.duaCount, duasReadCount) +
                    maxOf(entity.duroodCount, salawatTotal) +
                    maxOf(entity.quranMinutes, quranMinutesToday) +
                    maxOf(entity.hadithMinutes, hadithMinutes)
            } else {
                entity.tasbihCount + entity.duaCount + entity.duroodCount + entity.quranMinutes + entity.hadithMinutes
            }
            val label = WorshipTrackerRepository.formatYearMonth(entity.yearMonth, uiLang, short = true)
                .substringBefore("'")
            label to effectiveTotal
        }
    }
    val maxCount = (recent6Months.maxOfOrNull { it.second } ?: 0).coerceAtLeast(1).toFloat()

    FrostedGlassCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = DarkEmeraldSurface,
        borderColor = HairlineBorder
    ) {
        // Header
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
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(DarkEmeraldElevated)
                        .border(0.5.dp, ChampagneGold.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Insights,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = uiText("আধ্যাত্মিক অগ্রগতি ও আমল ইনসাইটস", "Spiritual Progress & Insights"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = ChampagneGold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                )
            }

            // Streak Flame Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF331A05), Color(0xFF1F1003))
                        )
                    )
                    .border(0.5.dp, ChampagneGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = Color(0xFFFFA726),
                    modifier = Modifier.size(14.dp)
                )
                val streakDigits = WorshipTrackerRepository.formatDigits(streakDays.toString(), uiLang)
                Text(
                    text = uiText("$streakDigits দিন স্ট্রিক", "$streakDigits Day Streak"),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFFFFA726),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Metrics Summary Row (Quran & Daily Salah)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkEmeraldElevated)
                    .border(0.5.dp, HairlineBorder, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Headphones,
                            contentDescription = null,
                            tint = MintGlow,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = uiText("কুরআন তিলাওয়াত", "Quran Recitation"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SlateGrayMuted,
                                fontSize = 10.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    val quranDigits = WorshipTrackerRepository.formatDigits(quranMinutesToday.toString(), uiLang)
                    Text(
                        text = uiText("$quranDigits মিনিট", "$quranDigits min"),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MintGlow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = uiText("চলতি মাসের পঠিত সময়", "This month reading time"),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateGrayMuted,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkEmeraldElevated)
                    .border(0.5.dp, HairlineBorder, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = ChampagneGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = uiText("দৈনিক নামাজ", "Daily Salah"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SlateGrayMuted,
                                fontSize = 10.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    val prayerDigits = WorshipTrackerRepository.formatDigits(completedPrayersToday.toString(), uiLang)
                    Text(
                        text = uiText("$prayerDigits/৫ ওয়াক্ত", "$prayerDigits/5 Waqt"),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = uiText("আজকের সালাত ট্র্যাকার", "Today's Salah tracker"),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateGrayMuted,
                            fontSize = 9.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Live 5-Category Counters Strip (Du'a, Tasbih, Durood, Hadith)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(DarkEmeraldElevated.copy(alpha = 0.7f))
                .border(0.5.dp, HairlineBorder, RoundedCornerShape(10.dp))
                .padding(horizontal = 8.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MiniInsightStat(uiText("🤲 দু'আ", "🤲 Du'a"), WorshipTrackerRepository.formatDigits(duasReadCount.toString(), uiLang))
            MiniInsightStat(uiText("📿 তাসবীহ", "📿 Tasbih"), WorshipTrackerRepository.formatDigits(totalTasbihCount.toString(), uiLang))
            MiniInsightStat(uiText("💚 দরূদ", "💚 Durood"), WorshipTrackerRepository.formatDigits(salawatTotal.toString(), uiLang))
            MiniInsightStat(
                uiText("📚 হাদিস", "📚 Hadith"),
                uiText("${WorshipTrackerRepository.toBengaliDigits(hadithMinutes.toString())} মি.", "${hadithMinutes}m")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Monthly Activity Mini Bar Graph + Open 12-Month Full Chart CTA
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(DarkEmeraldElevated.copy(alpha = 0.6f))
                .border(0.5.dp, HairlineBorder, RoundedCornerShape(10.dp))
                .clickable { onOpenMonthlyAnalytics() }
                .padding(10.dp)
                .testTag("home_open_12month_analytics")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiText("৬ মাসের সামগ্রিক আমল ট্রেন্ড", "6-Month Worship Trend"),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ChampagneGold,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = uiText("১২ মাসের চার্ট দেখুন →", "View 12-Month Chart →"),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MintGlow,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                recent6Months.forEachIndexed { idx, (monthLabel, count) ->
                    val fraction = if (count == 0) 0.12f else (count / maxCount).coerceIn(0.2f, 1f)
                    val isCurrent = idx == recent6Months.lastIndex

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(42.dp)
                    ) {
                        Text(
                            text = WorshipTrackerRepository.formatDigits(count.toString(), uiLang),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.5.sp,
                                color = if (isCurrent) ChampagneGold else SlateGrayMuted
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .width(13.dp)
                                .height((38 * fraction).dp)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(
                                    if (isCurrent && count > 0) {
                                        Brush.verticalGradient(
                                            colors = listOf(ChampagneGold, Color(0xFF9E7C1A))
                                        )
                                    } else if (count > 0) {
                                        Brush.verticalGradient(
                                            colors = listOf(MintGlow.copy(alpha = 0.75f), DarkEmeraldSurface)
                                        )
                                    } else {
                                        Brush.verticalGradient(
                                            colors = listOf(SlateGrayMuted.copy(alpha = 0.25f), DarkEmeraldSurface)
                                        )
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = monthLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                color = if (isCurrent) ChampagneGold else SlateGrayMuted,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniInsightStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.5.sp
        )
        Text(
            text = label,
            color = SlateGrayMuted,
            fontSize = 9.5.sp
        )
    }
}
