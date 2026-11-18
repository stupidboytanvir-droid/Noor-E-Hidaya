package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Dua
import com.example.data.model.HisnulPillar
import com.example.data.model.WaqtCategory
import com.example.ui.theme.*

enum class DuaCategoryIconType {
    SALAH_PROSTRATION,   // সালাত-নামাজ: Person in prostration/supplication
    DAILY_CHECKLIST,     // দৈনন্দিন জীবন: Checklist clipboard
    SAWM_CRESCENT,       // সাওম: Crescent moon with lantern
    AZKAR_SUN_HORIZON,   // সকাল-সন্ধ্যার যিকির: Sun over horizon wave
    QURAN_OPEN_MUSHAF,   // কুরআন থেকে: Open Mushaf book
    MISC_FOUR_DOTS       // বিবিধ: Four-dot grid/More menu
}

data class DuaCategoryItem(
    val id: String,
    val titleBengali: String,
    val subtitleBengali: String,
    val titleEnglish: String,
    val subtitleEnglish: String,
    val iconType: DuaCategoryIconType,
    val filterPredicate: (Dua) -> Boolean
) {
    fun localizedTitle(lang: String): String = LocalizationRegistry.translate(lang, titleBengali, titleEnglish)
    fun localizedSubtitle(lang: String): String = LocalizationRegistry.translate(lang, subtitleBengali, subtitleEnglish)
}

object DuaCategoryDirectory {
    val CATEGORIES: List<DuaCategoryItem> = listOf(
        DuaCategoryItem(
            id = "pillar_salah",
            titleBengali = "সালাত-নামাজ",
            subtitleBengali = "ওযু, আজান, সানা, রুকু, সেজদা ও সালাম",
            titleEnglish = "Salah & Prayer",
            subtitleEnglish = "Wudu, Adhan, Sana, Ruku, Sujood & Salam",
            iconType = DuaCategoryIconType.SALAH_PROSTRATION,
            filterPredicate = {
                it.hisnulPillar == HisnulPillar.SALAH_PRAYER ||
                it.waqtCategory == WaqtCategory.AFTER_SALAT ||
                it.category.contains("Salat", ignoreCase = true) ||
                it.titleBengali.contains("সালাত") ||
                it.titleBengali.contains("নামাজ") ||
                it.titleBengali.contains("ওযু") ||
                it.titleBengali.contains("আজান") ||
                it.titleBengali.contains("রুকু") ||
                it.titleBengali.contains("সেজদা") ||
                it.titleBengali.contains("তাশাহহুদ")
            }
        ),
        DuaCategoryItem(
            id = "pillar_daily",
            titleBengali = "দৈনন্দিন জীবন",
            subtitleBengali = "ঘুম, জাগরণ, সুরক্ষা, খাদ্য ও সফর",
            titleEnglish = "Daily Life",
            subtitleEnglish = "Sleep, waking, protection, meals & travel",
            iconType = DuaCategoryIconType.DAILY_CHECKLIST,
            filterPredicate = {
                it.hisnulPillar == HisnulPillar.DAILY_LIFE ||
                it.waqtCategory == WaqtCategory.DAILY_LIFE ||
                it.category.contains("Daily", ignoreCase = true) ||
                it.category.contains("Sleep", ignoreCase = true) ||
                it.category.contains("Travel", ignoreCase = true)
            }
        ),
        DuaCategoryItem(
            id = "pillar_fasting",
            titleBengali = "সাওম",
            subtitleBengali = "সেহরি, ইফতার ও রমজানের আমল",
            titleEnglish = "Sawm & Fasting",
            subtitleEnglish = "Sahur, Iftar & Ramadan supplications",
            iconType = DuaCategoryIconType.SAWM_CRESCENT,
            filterPredicate = {
                it.hisnulPillar == HisnulPillar.FASTING_SAWM ||
                it.category.contains("Fasting", ignoreCase = true) ||
                it.category.contains("Sawm", ignoreCase = true) ||
                it.titleBengali.contains("সাওম") ||
                it.titleBengali.contains("রোজা") ||
                it.titleBengali.contains("ইফতার") ||
                it.titleBengali.contains("সেহরি")
            }
        ),
        DuaCategoryItem(
            id = "pillar_azkar",
            titleBengali = "সকাল-সন্ধ্যার যিকির",
            subtitleBengali = "ফজর ও মাগরিব পরবর্তী সহীহ হিফাযত",
            titleEnglish = "Morning & Evening",
            subtitleEnglish = "Authentic Adhkar after Fajr & Maghrib",
            iconType = DuaCategoryIconType.AZKAR_SUN_HORIZON,
            filterPredicate = {
                it.waqtCategory in listOf(WaqtCategory.FAJR_MORNING, WaqtCategory.MAGHRIB_EVENING, WaqtCategory.ISHA_NIGHT) ||
                it.category.contains("Morning", ignoreCase = true) ||
                it.category.contains("Evening", ignoreCase = true) ||
                it.category.contains("Azkar", ignoreCase = true)
            }
        ),
        DuaCategoryItem(
            id = "pillar_rabbana",
            titleBengali = "কুরআন থেকে",
            subtitleBengali = "কুরআনের সর্বশ্রেষ্ঠ রব্বানা দোয়াসমূহ",
            titleEnglish = "From Al-Quran",
            subtitleEnglish = "Noble Rabbana supplications from the Quran",
            iconType = DuaCategoryIconType.QURAN_OPEN_MUSHAF,
            filterPredicate = {
                it.hisnulPillar == HisnulPillar.QURANIC_RABBANA ||
                it.textArabic.contains("رَبَّنَا") ||
                it.textArabic.contains("رَبِّ") ||
                it.reference.contains("সূরা", ignoreCase = true)
            }
        ),
        DuaCategoryItem(
            id = "pillar_misc",
            titleBengali = "বিবিধ",
            subtitleBengali = "রুকিয়া, বিপদমুক্তি, ক্ষমা ও সাধারণ দু'আ",
            titleEnglish = "Miscellaneous",
            subtitleEnglish = "Ruqyah, distress relief, Istighfar & general",
            iconType = DuaCategoryIconType.MISC_FOUR_DOTS,
            filterPredicate = {
                it.waqtCategory in listOf(WaqtCategory.RUQYAH_PROTECTION, WaqtCategory.DISTRESS_TRIALS, WaqtCategory.ISTIGHFAR, WaqtCategory.ALL) ||
                it.category.contains("Ruqyah", ignoreCase = true) ||
                it.category.contains("Distress", ignoreCase = true) ||
                it.category.contains("Forgiveness", ignoreCase = true)
            }
        )
    )
}

/**
 * 2-Column Prominent Circular Category Card
 */
@Composable
fun DuaCategoryCircularCard(
    category: DuaCategoryItem,
    duaCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FrostedGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("dua_category_card_${category.id}"),
        borderColor = ChampagneGold.copy(alpha = 0.25f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Prominent Circular Medallion (72dp) with Gold Border & Radial Gradient
            Box(
                modifier = Modifier
                    .size(72.dp)
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
                    .border(1.5.dp, ChampagneGold.copy(alpha = 0.65f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Volumetric line-art icon
                DuaCategoryIconCanvas(
                    type = category.iconType,
                    size = 40.dp,
                    color = ChampagneGold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category Label Underneath
            val uiLang = LocalAppLanguage.current
            Text(
                text = category.localizedTitle(uiLang),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(3.dp))

            // Dua Count Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(ChampagneGold.copy(alpha = 0.12f))
                    .border(0.5.dp, ChampagneGold.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = uiText(
                        "${formatLocalizedInt(duaCount, uiLang)}টি দু'আ",
                        "$duaCount Duas"
                    ),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ChampagneGold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

/**
 * Volumetric Line-Art Icon Canvas for the 6 Categories
 */
@Composable
fun DuaCategoryIconCanvas(
    type: DuaCategoryIconType,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    color: Color = ChampagneGold
) {
    Canvas(
        modifier = modifier.size(size)
    ) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 2.2f.dp.toPx()

        when (type) {
            // 1. SALAH_PROSTRATION: Person in prostration/sujood
            DuaCategoryIconType.SALAH_PROSTRATION -> {
                // Ground / prayer mat baseline
                drawLine(
                    color = color.copy(alpha = 0.45f),
                    start = Offset(w * 0.12f, h * 0.85f),
                    end = Offset(w * 0.88f, h * 0.85f),
                    strokeWidth = 1.5f.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Head in Sujood resting forward
                val headRadius = w * 0.10f
                val headCenter = Offset(w * 0.30f, h * 0.73f)
                drawCircle(
                    color = color,
                    radius = headRadius,
                    center = headCenter,
                    style = Stroke(width = strokeWidth)
                )

                // Specular highlight on forehead
                drawCircle(
                    color = Color.White.copy(alpha = 0.8f),
                    radius = headRadius * 0.35f,
                    center = Offset(headCenter.x - 2f, headCenter.y - 2f)
                )

                // Spine arching back from neck to lower back
                val spinePath = Path().apply {
                    moveTo(w * 0.38f, h * 0.68f)
                    cubicTo(
                        w * 0.48f, h * 0.40f,
                        w * 0.70f, h * 0.42f,
                        w * 0.74f, h * 0.65f
                    )
                }
                drawPath(
                    path = spinePath,
                    color = color,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Folded legs and feet near ground
                val legsPath = Path().apply {
                    moveTo(w * 0.74f, h * 0.65f)
                    lineTo(w * 0.82f, h * 0.84f)
                    lineTo(w * 0.68f, h * 0.84f)
                }
                drawPath(
                    path = legsPath,
                    color = color,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Arms extended forward to ground
                val armsPath = Path().apply {
                    moveTo(w * 0.44f, h * 0.58f)
                    lineTo(w * 0.38f, h * 0.84f)
                    lineTo(w * 0.22f, h * 0.84f)
                }
                drawPath(
                    path = armsPath,
                    color = color,
                    style = Stroke(width = strokeWidth * 0.9f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }

            // 2. DAILY_CHECKLIST: Checklist clipboard
            DuaCategoryIconType.DAILY_CHECKLIST -> {
                // Clipboard board outline
                val boardPath = Path().apply {
                    moveTo(w * 0.24f, h * 0.22f)
                    lineTo(w * 0.76f, h * 0.22f)
                    lineTo(w * 0.76f, h * 0.86f)
                    lineTo(w * 0.24f, h * 0.86f)
                    close()
                }
                drawPath(
                    path = boardPath,
                    color = color,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Top clip clamp
                val clipPath = Path().apply {
                    moveTo(w * 0.38f, h * 0.22f)
                    lineTo(w * 0.38f, h * 0.14f)
                    lineTo(w * 0.62f, h * 0.14f)
                    lineTo(w * 0.62f, h * 0.22f)
                }
                drawPath(
                    path = clipPath,
                    color = color,
                    style = Stroke(width = strokeWidth * 1.1f, cap = StrokeCap.Round)
                )

                // 3 Checklist rows (checkmark + line)
                val rowYs = listOf(h * 0.38f, h * 0.54f, h * 0.70f)
                rowYs.forEach { y ->
                    // Checkmark
                    val checkPath = Path().apply {
                        moveTo(w * 0.32f, y)
                        lineTo(w * 0.38f, y + h * 0.05f)
                        lineTo(w * 0.48f, y - h * 0.04f)
                    }
                    drawPath(
                        path = checkPath,
                        color = MintGlow,
                        style = Stroke(width = strokeWidth * 0.9f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )

                    // Text line bar
                    drawLine(
                        color = color.copy(alpha = 0.7f),
                        start = Offset(w * 0.53f, y),
                        end = Offset(w * 0.68f, y),
                        strokeWidth = strokeWidth * 0.85f,
                        cap = StrokeCap.Round
                    )
                }
            }

            // 3. SAWM_CRESCENT: Crescent moon with lantern
            DuaCategoryIconType.SAWM_CRESCENT -> {
                // Crescent Moon (Right-oriented arc)
                val moonPath = Path().apply {
                    moveTo(w * 0.54f, h * 0.12f)
                    cubicTo(
                        w * 0.88f, h * 0.26f,
                        w * 0.88f, h * 0.74f,
                        w * 0.54f, h * 0.88f
                    )
                    cubicTo(
                        w * 0.74f, h * 0.72f,
                        w * 0.74f, h * 0.28f,
                        w * 0.54f, h * 0.12f
                    )
                    close()
                }
                drawPath(
                    path = moonPath,
                    color = color,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Hanging Chain for the Fanous lantern
                drawLine(
                    color = color.copy(alpha = 0.6f),
                    start = Offset(w * 0.35f, h * 0.15f),
                    end = Offset(w * 0.35f, h * 0.40f),
                    strokeWidth = 1.2f.dp.toPx()
                )

                // Traditional Islamic Fanous (Lantern)
                val lanternTop = h * 0.40f
                val lanternPath = Path().apply {
                    // Cap dome
                    moveTo(w * 0.30f, lanternTop + h * 0.06f)
                    lineTo(w * 0.35f, lanternTop)
                    lineTo(w * 0.40f, lanternTop + h * 0.06f)
                    // Glass body
                    lineTo(w * 0.42f, lanternTop + h * 0.20f)
                    lineTo(w * 0.28f, lanternTop + h * 0.20f)
                    close()
                    // Bottom base
                    moveTo(w * 0.31f, lanternTop + h * 0.20f)
                    lineTo(w * 0.35f, lanternTop + h * 0.24f)
                    lineTo(w * 0.39f, lanternTop + h * 0.20f)
                }
                drawPath(
                    path = lanternPath,
                    color = color,
                    style = Stroke(width = strokeWidth * 0.9f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Glowing flame inside lantern
                drawCircle(
                    color = MintGlow,
                    radius = w * 0.035f,
                    center = Offset(w * 0.35f, lanternTop + h * 0.13f)
                )
            }

            // 4. AZKAR_SUN_HORIZON: Sun over horizon wave
            DuaCategoryIconType.AZKAR_SUN_HORIZON -> {
                val horizonY = h * 0.62f

                // Half Sun Disc rising above horizon
                val sunPath = Path().apply {
                    moveTo(w * 0.30f, horizonY)
                    cubicTo(
                        w * 0.30f, h * 0.34f,
                        w * 0.70f, h * 0.34f,
                        w * 0.70f, horizonY
                    )
                    close()
                }
                drawPath(
                    path = sunPath,
                    color = color.copy(alpha = 0.25f)
                )
                drawPath(
                    path = sunPath,
                    color = color,
                    style = Stroke(width = strokeWidth)
                )

                // 5 Radiant Sunrays
                val rays = listOf(
                    Offset(w * 0.24f, h * 0.42f) to Offset(w * 0.14f, h * 0.34f),
                    Offset(w * 0.36f, h * 0.28f) to Offset(w * 0.30f, h * 0.16f),
                    Offset(w * 0.50f, h * 0.24f) to Offset(w * 0.50f, h * 0.12f),
                    Offset(w * 0.64f, h * 0.28f) to Offset(w * 0.70f, h * 0.16f),
                    Offset(w * 0.76f, h * 0.42f) to Offset(w * 0.86f, h * 0.34f)
                )
                rays.forEach { (start, end) ->
                    drawLine(
                        color = color,
                        start = start,
                        end = end,
                        strokeWidth = strokeWidth * 0.9f,
                        cap = StrokeCap.Round
                    )
                }

                // 2 Horizon wave lines
                val wave1 = Path().apply {
                    moveTo(w * 0.12f, horizonY)
                    cubicTo(w * 0.32f, horizonY - 4f, w * 0.68f, horizonY + 4f, w * 0.88f, horizonY)
                }
                drawPath(
                    path = wave1,
                    color = color,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                val wave2 = Path().apply {
                    moveTo(w * 0.20f, horizonY + h * 0.14f)
                    cubicTo(w * 0.40f, horizonY + h * 0.18f, w * 0.65f, horizonY + h * 0.10f, w * 0.80f, horizonY + h * 0.14f)
                }
                drawPath(
                    path = wave2,
                    color = color.copy(alpha = 0.6f),
                    style = Stroke(width = strokeWidth * 0.8f, cap = StrokeCap.Round)
                )
            }

            // 5. QURAN_OPEN_MUSHAF: Open Mushaf book on wooden rehal
            DuaCategoryIconType.QURAN_OPEN_MUSHAF -> {
                val spineX = w * 0.50f
                val spineTop = h * 0.24f
                val spineBottom = h * 0.60f

                // Left & Right Open Pages
                val bookPath = Path().apply {
                    // Left page top curve
                    moveTo(spineX, spineTop)
                    cubicTo(w * 0.38f, spineTop - 5f, w * 0.24f, spineTop, w * 0.16f, spineTop + 6f)
                    // Left page side down
                    lineTo(w * 0.16f, spineBottom + 4f)
                    // Left page bottom curve back to spine
                    cubicTo(w * 0.24f, spineBottom - 2f, w * 0.38f, spineBottom - 6f, spineX, spineBottom)
                    // Right page bottom curve out
                    cubicTo(w * 0.62f, spineBottom - 6f, w * 0.76f, spineBottom - 2f, w * 0.84f, spineBottom + 4f)
                    // Right page side up
                    lineTo(w * 0.84f, spineTop + 6f)
                    // Right page top curve back to spine
                    cubicTo(w * 0.76f, spineTop, w * 0.62f, spineTop - 5f, spineX, spineTop)
                }
                drawPath(
                    path = bookPath,
                    color = color.copy(alpha = 0.18f)
                )
                drawPath(
                    path = bookPath,
                    color = color,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Central spine fold
                drawLine(
                    color = color,
                    start = Offset(spineX, spineTop),
                    end = Offset(spineX, spineBottom),
                    strokeWidth = strokeWidth * 0.85f
                )

                // Hanging bookmark ribbon
                drawLine(
                    color = MintGlow,
                    start = Offset(spineX, spineBottom),
                    end = Offset(spineX + w * 0.04f, spineBottom + h * 0.14f),
                    strokeWidth = strokeWidth * 0.9f,
                    cap = StrokeCap.Round
                )

                // X-shaped wooden Rehal stand below
                val rehalPath = Path().apply {
                    moveTo(w * 0.30f, spineBottom + 3f)
                    lineTo(w * 0.70f, h * 0.88f)
                    moveTo(w * 0.70f, spineBottom + 3f)
                    lineTo(w * 0.30f, h * 0.88f)
                }
                drawPath(
                    path = rehalPath,
                    color = color.copy(alpha = 0.75f),
                    style = Stroke(width = strokeWidth * 0.9f, cap = StrokeCap.Round)
                )
            }

            // 6. MISC_FOUR_DOTS: Four-dot grid/More menu
            DuaCategoryIconType.MISC_FOUR_DOTS -> {
                val centers = listOf(
                    Offset(w * 0.36f, h * 0.36f),
                    Offset(w * 0.64f, h * 0.36f),
                    Offset(w * 0.36f, h * 0.64f),
                    Offset(w * 0.64f, h * 0.64f)
                )
                val dotRadius = w * 0.11f

                centers.forEach { center ->
                    // Filled disc
                    drawCircle(
                        color = color.copy(alpha = 0.2f),
                        radius = dotRadius,
                        center = center
                    )
                    // Gold outline
                    drawCircle(
                        color = color,
                        radius = dotRadius,
                        center = center,
                        style = Stroke(width = strokeWidth)
                    )
                    // Inner bright specular highlight dot
                    drawCircle(
                        color = Color.White.copy(alpha = 0.85f),
                        radius = dotRadius * 0.32f,
                        center = Offset(center.x - 2f, center.y - 2f)
                    )
                }

                // Central connective diamond sparkle
                val sparkleSize = w * 0.06f
                val centerPath = Path().apply {
                    moveTo(w * 0.50f, h * 0.50f - sparkleSize)
                    lineTo(w * 0.50f + sparkleSize, h * 0.50f)
                    lineTo(w * 0.50f, h * 0.50f + sparkleSize)
                    lineTo(w * 0.50f - sparkleSize, h * 0.50f)
                    close()
                }
                drawPath(path = centerPath, color = MintGlow)
            }
        }
    }
}

/**
 * "MY DUA COLLECTIONS" Favorite Folder Card
 */
@Composable
fun MyDuaCollectionFavoriteCard(
    favoriteCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FrostedGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("dua_favorite_folder_card"),
        borderColor = ChampagneGold.copy(alpha = 0.35f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Gold Folder Emblem
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(DarkEmeraldElevated, DarkEmeraldSurface)
                            )
                        )
                        .border(1.dp, ChampagneGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = "Favourite",
                        tint = ChampagneGold,
                        modifier = Modifier.size(24.dp)
                    )
                }

                val uiLang = LocalAppLanguage.current
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = uiText("পছন্দের দু'আ", "Favourite"),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                        if (uiLang == "bn") {
                            Text(
                                text = "• সংরক্ষিত",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = ChampagneGold.copy(alpha = 0.85f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = uiText(
                            "আপনার নির্বাচিত ও বুকমার্ক করা ব্যক্তিগত আমলসমূহ",
                            "Your bookmarked and personal saved supplications"
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Count Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkEmeraldElevated)
                        .border(0.5.dp, ChampagneGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = uiText("${formatLocalizedInt(favoriteCount, "bn")}টি দু'আ", "$favoriteCount duas"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = uiText("প্রবেশ করুন", "Open"),
                    tint = ChampagneGold,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Custom Collection Folder Card
 */
@Composable
fun CustomDuaCollectionFolderCard(
    name: String,
    duaCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FrostedGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("custom_collection_$name"),
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
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkEmeraldElevated)
                        .border(0.5.dp, MintGlow.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Folder,
                        contentDescription = name,
                        tint = MintGlow,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = uiText("কাস্টম আমল তালিকা", "Custom Dua list"),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateGrayMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = uiText("${formatLocalizedInt(duaCount, "bn")}টি দু'আ", "$duaCount duas"),
                    color = SlateGrayMuted,
                    fontSize = 11.sp
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = SlateGrayMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Dialog to create a new custom Dua collection
 */
@Composable
fun CreateDuaListDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var listName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkEmeraldSurface,
        title = {
            Text(
                text = uiText("নতুন দু'আ তালিকা তৈরি করুন", "Create New Dua List"),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column {
                Text(
                    text = uiText(
                        "আপনার ব্যক্তিগত আমল বা বিশেষ সময়ের জন্য একটি নাম দিন (যেমন: তাহাজ্জুদ, পরীক্ষার দু'আ, সফর):",
                        "Give a name for your personal collection (e.g., Tahajjud, Exam Duas, Travel):"
                    ),
                    color = SlateGrayMuted,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = listName,
                    onValueChange = { listName = it },
                    placeholder = {
                        Text(
                            uiText("তালিকার নাম লিখুন...", "Enter list name..."),
                            color = SlateGrayMuted.copy(alpha = 0.6f)
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_dua_list_name_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = HairlineBorder,
                        focusedContainerColor = DarkEmeraldElevated,
                        unfocusedContainerColor = DarkEmeraldElevated
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (listName.isNotBlank()) {
                        onCreate(listName.trim())
                    }
                },
                enabled = listName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChampagneGold,
                    contentColor = ObsidianBackground
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("confirm_create_list_button")
            ) {
                Text(uiText("তৈরি করুন", "Create"), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(uiText("বাতিল", "Cancel"), color = SlateGrayMuted)
            }
        }
    )
}
