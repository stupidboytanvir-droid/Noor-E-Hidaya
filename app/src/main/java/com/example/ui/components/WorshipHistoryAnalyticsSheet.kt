package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MonthlyWorshipEntity
import com.example.data.repository.WorshipCategory
import com.example.data.repository.WorshipTrackerRepository
import com.example.ui.theme.*

/**
 * Full 12-Month Worship Analytics & History Sheet covering all 5 Worship Categories:
 * Quran (mins), Du'a (count), Tasbih (count), Durood (count), Hadith (mins + count).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorshipHistoryAnalyticsBottomSheet(
    monthlyHistory: List<MonthlyWorshipEntity>,
    currentQuranMinutes: Int,
    currentDuaCount: Int,
    currentTasbihCount: Int,
    currentDuroodCount: Int,
    currentHadithMinutes: Int,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkEmeraldSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SlateGrayMuted.copy(alpha = 0.5f))
            )
        }
    ) {
        WorshipHistoryAnalyticsContent(
            monthlyHistory = monthlyHistory,
            currentQuranMinutes = currentQuranMinutes,
            currentDuaCount = currentDuaCount,
            currentTasbihCount = currentTasbihCount,
            currentDuroodCount = currentDuroodCount,
            currentHadithMinutes = currentHadithMinutes,
            onClose = onDismiss
        )
    }
}

@Composable
fun WorshipHistoryAnalyticsContent(
    monthlyHistory: List<MonthlyWorshipEntity>,
    currentQuranMinutes: Int,
    currentDuaCount: Int,
    currentTasbihCount: Int,
    currentDuroodCount: Int,
    currentHadithMinutes: Int,
    onClose: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf(WorshipCategory.QURAN) }

    val series12Months = remember(
        monthlyHistory,
        currentQuranMinutes,
        currentDuaCount,
        currentTasbihCount,
        currentDuroodCount,
        currentHadithMinutes
    ) {
        val baseSeries = WorshipTrackerRepository.build12MonthSeries(monthlyHistory)
        val lastIdx = baseSeries.lastIndex
        baseSeries.mapIndexed { idx, entity ->
            if (idx == lastIdx) {
                entity.copy(
                    quranMinutes = maxOf(entity.quranMinutes, currentQuranMinutes),
                    duaCount = maxOf(entity.duaCount, currentDuaCount),
                    tasbihCount = maxOf(entity.tasbihCount, currentTasbihCount),
                    duroodCount = maxOf(entity.duroodCount, currentDuroodCount),
                    hadithMinutes = maxOf(entity.hadithMinutes, currentHadithMinutes)
                )
            } else {
                entity
            }
        }
    }

    val uiLang = LocalAppLanguage.current
    var selectedMonthIndex by remember(series12Months.size) { mutableIntStateOf(series12Months.lastIndex) }

    val categoryValues = remember(series12Months, selectedCategory) {
        series12Months.map { WorshipTrackerRepository.valueForCategory(it, selectedCategory) }
    }
    val total12Months = categoryValues.sum()
    val maxVal = categoryValues.maxOrNull() ?: 0
    val currentMonthVal = categoryValues.lastOrNull() ?: 0
    val selectedMonthEntity = series12Months.getOrNull(selectedMonthIndex) ?: series12Months.last()
    val selectedMonthVal = WorshipTrackerRepository.valueForCategory(selectedMonthEntity, selectedCategory)

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.88f)
            .padding(horizontal = 16.dp)
            .testTag("worship_history_analytics_sheet"),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ChampagneGold.copy(alpha = 0.16f))
                            .border(1.dp, ChampagneGold.copy(alpha = 0.45f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.BarChart,
                            contentDescription = uiText("১২ মাসের চার্ট", "12-Month Chart"),
                            tint = ChampagneGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = uiText("১২ মাসের ইবাদত ও আমল বিশ্লেষণ", "12-Month Worship Analytics"),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = uiText("কুরআন, তাসবীহ, দরূদ ও হাদিসের মাসিক চার্ট", "Monthly trends for Quran, Tasbih, Durood & Hadith"),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateGrayMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = uiText("বন্ধ করুন", "Close"),
                        tint = SlateGrayMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 4 Category Selector Chips (Quran, Tasbih, Durood, Hadith)
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(WorshipCategory.CHART_CATEGORIES) { cat ->
                    val isSelected = selectedCategory == cat
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) ChampagneGold.copy(alpha = 0.22f)
                                else DarkEmeraldElevated
                            )
                            .border(
                                width = if (isSelected) 1.dp else 0.5.dp,
                                color = if (isSelected) ChampagneGold else HairlineBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("category_chip_${cat.name.lowercase()}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = cat.iconEmoji, fontSize = 14.sp)
                        Text(
                            text = cat.localizedTitle(uiLang),
                            color = if (isSelected) ChampagneGold else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // KPI Summary Row for selected category
        item {
            val unitLabel = selectedCategory.localizedUnit(uiLang)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricSummaryPill(
                    title = uiText("চলতি মাস", "This Month"),
                    value = "${WorshipTrackerRepository.formatDigits(currentMonthVal.toString(), uiLang)} $unitLabel",
                    accent = MintGlow,
                    modifier = Modifier.weight(1f)
                )
                MetricSummaryPill(
                    title = uiText("১২ মাসে মোট", "12-Month Total"),
                    value = "${WorshipTrackerRepository.formatDigits(total12Months.toString(), uiLang)} $unitLabel",
                    accent = ChampagneGold,
                    modifier = Modifier.weight(1f)
                )
                MetricSummaryPill(
                    title = uiText("সর্বোচ্চ মাস", "Peak Month"),
                    value = "${WorshipTrackerRepository.formatDigits(maxVal.toString(), uiLang)} $unitLabel",
                    accent = Color(0xFF81D4FA),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 12-Month Interactive Bar + Trend Line Chart
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkEmeraldElevated)
                    .border(0.8.dp, HairlineBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${selectedCategory.iconEmoji} ${selectedCategory.localizedTitle(uiLang)} — ${uiText("১২ মাসের গ্রাফ", "12-Month Chart")}",
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ObsidianBackground.copy(alpha = 0.7f))
                            .border(0.5.dp, ChampagneGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${WorshipTrackerRepository.formatYearMonth(selectedMonthEntity.yearMonth, uiLang, short = false)}: " +
                                "${WorshipTrackerRepository.formatDigits(selectedMonthVal.toString(), uiLang)} ${selectedCategory.localizedUnit(uiLang)}",
                            color = MintGlow,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Canvas Bar + Line Chart
                val chartMax = maxVal.coerceAtLeast(1).toFloat()
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(145.dp)
                ) {
                    val slotWidth = size.width / 12f
                    val barWidth = (slotWidth * 0.48f).coerceIn(10f, 26f)
                    val topPad = 14f
                    val bottomPad = 8f
                    val chartHeight = (size.height - topPad - bottomPad).coerceAtLeast(20f)

                    // Subtle dashed reference lines
                    val gridSteps = 3
                    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    for (g in 0..gridSteps) {
                        val y = topPad + (chartHeight / gridSteps) * g
                        drawLine(
                            color = Color.White.copy(alpha = 0.08f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1f,
                            pathEffect = dashEffect
                        )
                    }

                    val linePath = Path()
                    val points = ArrayList<Offset>(12)

                    categoryValues.forEachIndexed { idx, value ->
                        val fraction = if (maxVal == 0) 0f else (value / chartMax).coerceIn(0f, 1f)
                        val barH = if (value == 0) 4f else (chartHeight * fraction).coerceAtLeast(10f)
                        val centerX = slotWidth * idx + slotWidth / 2f
                        val left = centerX - barWidth / 2f
                        val top = topPad + (chartHeight - barH)
                        val isSelectedBar = idx == selectedMonthIndex

                        drawRoundRect(
                            brush = if (isSelectedBar) {
                                Brush.verticalGradient(listOf(ChampagneGold, Color(0xFFB38E24)))
                            } else if (value > 0) {
                                Brush.verticalGradient(listOf(MintGlow, MintGlow.copy(alpha = 0.35f)))
                            } else {
                                Brush.verticalGradient(
                                    listOf(SlateGrayMuted.copy(alpha = 0.25f), SlateGrayMuted.copy(alpha = 0.1f))
                                )
                            },
                            topLeft = Offset(left, top),
                            size = Size(barWidth, barH),
                            cornerRadius = CornerRadius(6f, 6f)
                        )

                        val pt = Offset(centerX, top)
                        points.add(pt)
                        if (idx == 0) linePath.moveTo(pt.x, pt.y) else linePath.lineTo(pt.x, pt.y)
                    }

                    // Trend overlay line
                    if (maxVal > 0 && points.size >= 2) {
                        drawPath(
                            path = linePath,
                            color = ChampagneGold.copy(alpha = 0.65f),
                            style = Stroke(width = 2.dp.toPx())
                        )
                        points.forEachIndexed { idx, pt ->
                            drawCircle(
                                color = if (idx == selectedMonthIndex) Color.White else ChampagneGold,
                                radius = if (idx == selectedMonthIndex) 4.dp.toPx() else 2.5.dp.toPx(),
                                center = pt
                            )
                        }
                    }
                }

                // Tappable Month Labels Row underneath bars
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    series12Months.forEachIndexed { idx, entity ->
                        val isSel = idx == selectedMonthIndex
                        val monthNum = entity.yearMonth.takeLast(2)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) ChampagneGold.copy(alpha = 0.18f) else Color.Transparent)
                                .clickable { selectedMonthIndex = idx }
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = WorshipTrackerRepository.formatDigits(monthNum, uiLang),
                                color = if (isSel) ChampagneGold else SlateGrayMuted,
                                fontSize = 9.5.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Text(
                    text = uiText(
                        "টিপস: যেকোনো মাসের সংখ্যায় ট্যাপ করলে ওই মাসের বিস্তারিত মান উপরে দেখাবে",
                        "Tip: Tap any month number to inspect its exact total above"
                    ),
                    color = SlateGrayMuted,
                    fontSize = 10.sp
                )
            }
        }

        // Section Title for 12-Month Breakdown Table
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.History,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = uiText("১২ মাসের সম্পূর্ণ ইবাদত তালিকা (আর্কাইভ)", "12-Month Complete Worship Archive"),
                    color = ChampagneGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                )
            }
        }

        // 12-Month Rows (Most Recent First)
        itemsIndexed(series12Months.asReversed()) { revIdx, entity ->
            val isCurrentMonth = revIdx == 0
            MonthlyHistoryRowCard(entity = entity, isCurrentMonth = isCurrentMonth)
        }
    }
}

@Composable
private fun MetricSummaryPill(
    title: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkEmeraldElevated)
            .border(0.6.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = title,
            color = SlateGrayMuted,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = value,
            color = accent,
            fontWeight = FontWeight.Bold,
            fontSize = 12.5.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun MonthlyHistoryRowCard(
    entity: MonthlyWorshipEntity,
    isCurrentMonth: Boolean
) {
    val uiLang = LocalAppLanguage.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkEmeraldElevated.copy(alpha = if (isCurrentMonth) 0.95f else 0.7f))
            .border(
                width = if (isCurrentMonth) 1.dp else 0.5.dp,
                color = if (isCurrentMonth) ChampagneGold.copy(alpha = 0.55f) else HairlineBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = WorshipTrackerRepository.formatYearMonth(entity.yearMonth, uiLang, short = false),
                color = if (isCurrentMonth) ChampagneGold else Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isCurrentMonth) MintGlow.copy(alpha = 0.16f) else ObsidianBackground.copy(alpha = 0.5f))
                    .padding(horizontal = 7.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (isCurrentMonth) uiText("চলতি মাস", "Current") else uiText("সংরক্ষিত", "Archived"),
                    color = if (isCurrentMonth) MintGlow else SlateGrayMuted,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MonthMetricMini(
                uiText("📖 কুরআন", "📖 Quran"),
                uiText("${WorshipTrackerRepository.toBengaliDigits(entity.quranMinutes.toString())} মি.", "${entity.quranMinutes}m")
            )
            MonthMetricMini(uiText("📿 তাসবীহ", "📿 Tasbih"), WorshipTrackerRepository.formatDigits(entity.tasbihCount.toString(), uiLang))
            MonthMetricMini(uiText("💚 দরূদ", "💚 Durood"), WorshipTrackerRepository.formatDigits(entity.duroodCount.toString(), uiLang))
            MonthMetricMini(
                uiText("📚 হাদিস", "📚 Hadith"),
                uiText("${WorshipTrackerRepository.toBengaliDigits(entity.hadithMinutes.toString())} মি.", "${entity.hadithMinutes}m")
            )
        }
    }
}

@Composable
private fun MonthMetricMini(label: String, valText: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = valText, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
        Text(text = label, color = SlateGrayMuted, fontSize = 9.sp)
    }
}
