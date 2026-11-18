package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-precision 3D Iconography Engine for Noor-e-Hidayah.
 * Renders volumetric, physically shaded 3D emblems adhering to the
 * Obsidian Midnight, Architectural Dark Emerald, and Champagne Gold design system.
 */
enum class Icon3DType {
    MOSQUE_HOME,
    HOLY_QURAN,
    DUA_HANDS,
    TOOLS_ASTROLABE,
    MORE_SETTINGS,
    RUQYAH_SHIELD,
    ARABIC_TTS,
    AI_ASSISTANT,
    TASBEEH_BEADS,
    QIBLA_COMPASS,
    ZAKAT_SCALE,
    MOSQUE_DND,
    ASMA_UL_HUSNA,
    LOCATION_PIN,
    SEARCH_LENS,
    BOOKMARK_RIBBON,
    SUNNAH_HEALTH,
    INHERITANCE_WEALTH,
    JANAZAH_WASIYYAH,
    TADABBUR_QURAN
}

@Composable
fun ThreeDimensionalIcon(
    type: Icon3DType,
    modifier: Modifier = Modifier,
    size: Dp = 26.dp,
    isActive: Boolean = true,
    accentColor: Color = ChampagneGold,
    baseColor: Color = DarkEmeraldSurface
) {
    Box(
        modifier = modifier
            .size(size)
            .padding(1.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            // Render volumetric 3D emblem based on type
            when (type) {
                Icon3DType.MOSQUE_HOME -> draw3DMosque(w, h, isActive, accentColor)
                Icon3DType.HOLY_QURAN -> draw3DQuran(w, h, isActive, accentColor)
                Icon3DType.DUA_HANDS -> draw3DDuaHands(w, h, isActive, accentColor)
                Icon3DType.TOOLS_ASTROLABE -> draw3DAstrolabe(w, h, isActive, accentColor)
                Icon3DType.MORE_SETTINGS -> draw3DSettingsDials(w, h, isActive, accentColor)
                Icon3DType.RUQYAH_SHIELD -> draw3DRuqyahShield(w, h, isActive, accentColor)
                Icon3DType.ARABIC_TTS -> draw3DArabicTts(w, h, isActive, accentColor)
                Icon3DType.AI_ASSISTANT -> draw3DAiSparkle(w, h, isActive, accentColor)
                Icon3DType.TASBEEH_BEADS -> draw3DTasbeehBeads(w, h, isActive, accentColor)
                Icon3DType.QIBLA_COMPASS -> draw3DQiblaCompass(w, h, isActive, accentColor)
                Icon3DType.ZAKAT_SCALE -> draw3DZakatScale(w, h, isActive, accentColor)
                Icon3DType.MOSQUE_DND -> draw3DMosqueDnd(w, h, isActive, accentColor)
                Icon3DType.ASMA_UL_HUSNA -> draw3DAsmaUlHusna(w, h, isActive, accentColor)
                Icon3DType.LOCATION_PIN -> draw3DLocationPin(w, h, isActive, accentColor)
                Icon3DType.SEARCH_LENS -> draw3DSearchLens(w, h, isActive, accentColor)
                Icon3DType.BOOKMARK_RIBBON -> draw3DBookmarkRibbon(w, h, isActive, accentColor)
                Icon3DType.SUNNAH_HEALTH -> draw3DSunnahHealth(w, h, isActive, accentColor)
                Icon3DType.INHERITANCE_WEALTH -> draw3DInheritanceWealth(w, h, isActive, accentColor)
                Icon3DType.JANAZAH_WASIYYAH -> draw3DJanazahWasiyyah(w, h, isActive, accentColor)
                Icon3DType.TADABBUR_QURAN -> draw3DTadabburQuran(w, h, isActive, accentColor)
            }
        }
    }
}

/**
 * 3D Layered Vessel Container for arbitrary vectors with volumetric bevel and specular highlight
 */
@Composable
fun ThreeDimensionalVectorPill(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    iconSize: Dp = 22.dp,
    isActive: Boolean = true,
    accentColor: Color = ChampagneGold,
    containerColor: Color = DarkEmeraldElevated
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.32f))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        containerColor.copy(alpha = 0.95f),
                        ObsidianBackground.copy(alpha = 0.98f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(100f, 100f)
                )
            )
            .border(
                width = 0.8.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        accentColor.copy(alpha = if (isActive) 0.65f else 0.25f),
                        Color.White.copy(alpha = if (isActive) 0.25f else 0.05f),
                        ObsidianBackground.copy(alpha = 0.8f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(100f, 100f)
                ),
                shape = RoundedCornerShape(size * 0.32f)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Specular top-left illumination glint
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = if (isActive) 0.22f else 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(this.size.width * 0.25f, this.size.height * 0.22f),
                    radius = this.size.width * 0.45f
                )
            )
        }

        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isActive) accentColor else SlateGrayMuted,
            modifier = Modifier.size(iconSize)
        )
    }
}

// -------------------------------------------------------------
// HIGH-PRECISION 3D DRAWING IMPLEMENTATIONS
// -------------------------------------------------------------

private fun DrawScope.draw3DMosque(w: Float, h: Float, isActive: Boolean, gold: Color) {
    val goldLight = if (isActive) Color(0xFFFFF2B2) else Color(0xFFD4B36A)
    val goldMid = if (isActive) gold else Color(0xFF9E8440)
    val goldDark = Color(0xFF4A3C12)
    val emeraldDeep = Color(0xFF071F1A)

    // Base contact shadow
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent),
            center = Offset(w * 0.5f, h * 0.92f),
            radius = w * 0.45f
        ),
        topLeft = Offset(w * 0.1f, h * 0.84f),
        size = Size(w * 0.8f, h * 0.14f)
    )

    // Main Mosque Wall / Base (3D block)
    val baseRect = Rect(w * 0.22f, h * 0.52f, w * 0.78f, h * 0.90f)
    val basePath = Path().apply {
        addRoundRect(RoundRect(baseRect, CornerRadius(w * 0.04f, w * 0.04f)))
    }
    drawPath(
        path = basePath,
        brush = Brush.linearGradient(
            colors = listOf(emeraldDeep, Color(0xFF030D0B)),
            start = Offset(0f, h * 0.52f),
            end = Offset(0f, h * 0.90f)
        )
    )
    drawPath(
        path = basePath,
        brush = Brush.linearGradient(
            colors = listOf(goldMid.copy(alpha = 0.7f), goldDark.copy(alpha = 0.3f)),
            start = Offset(0f, h * 0.52f),
            end = Offset(w, h * 0.90f)
        ),
        style = Stroke(width = w * 0.035f)
    )

    // Horseshoe Gateway Arch with depth
    val archPath = Path().apply {
        moveTo(w * 0.40f, h * 0.90f)
        lineTo(w * 0.40f, h * 0.70f)
        cubicTo(w * 0.40f, h * 0.60f, w * 0.60f, h * 0.60f, w * 0.60f, h * 0.70f)
        lineTo(w * 0.60f, h * 0.90f)
        close()
    }
    drawPath(
        path = archPath,
        brush = Brush.radialGradient(
            colors = listOf(goldMid.copy(alpha = 0.3f), Color.Black),
            center = Offset(w * 0.5f, h * 0.72f),
            radius = w * 0.15f
        )
    )
    drawPath(
        path = archPath,
        color = goldMid,
        style = Stroke(width = w * 0.03f)
    )

    // Volumetric 3D Central Dome
    val domePath = Path().apply {
        moveTo(w * 0.24f, h * 0.54f)
        cubicTo(w * 0.24f, h * 0.36f, w * 0.42f, h * 0.22f, w * 0.50f, h * 0.16f)
        cubicTo(w * 0.58f, h * 0.22f, w * 0.76f, h * 0.36f, w * 0.76f, h * 0.54f)
        close()
    }
    // Dome volumetric fill with top-left illumination
    drawPath(
        path = domePath,
        brush = Brush.radialGradient(
            colors = listOf(goldLight, goldMid, goldDark),
            center = Offset(w * 0.42f, h * 0.32f),
            radius = w * 0.35f
        )
    )
    // Dome rim stroke
    drawPath(
        path = domePath,
        brush = Brush.linearGradient(
            colors = listOf(goldLight, goldMid, goldDark),
            start = Offset(w * 0.24f, h * 0.2f),
            end = Offset(w * 0.76f, h * 0.54f)
        ),
        style = Stroke(width = w * 0.04f)
    )

    // Finial Spindle & Crescent on Dome Apex
    drawLine(
        brush = Brush.verticalGradient(listOf(goldLight, goldMid)),
        start = Offset(w * 0.5f, h * 0.16f),
        end = Offset(w * 0.5f, h * 0.06f),
        strokeWidth = w * 0.045f,
        cap = StrokeCap.Round
    )
    drawCircle(
        color = goldLight,
        radius = w * 0.035f,
        center = Offset(w * 0.5f, h * 0.08f)
    )

    // Left & Right Minarets (slender 3D towers)
    fun drawMinaret(x: Float) {
        drawLine(
            brush = Brush.horizontalGradient(listOf(goldMid, goldDark)),
            start = Offset(x, h * 0.88f),
            end = Offset(x, h * 0.34f),
            strokeWidth = w * 0.065f,
            cap = StrokeCap.Square
        )
        // Balcony ring
        drawLine(
            color = goldLight,
            start = Offset(x - w * 0.06f, h * 0.34f),
            end = Offset(x + w * 0.06f, h * 0.34f),
            strokeWidth = w * 0.04f,
            cap = StrokeCap.Round
        )
        // Spire
        val spire = Path().apply {
            moveTo(x - w * 0.04f, h * 0.34f)
            lineTo(x, h * 0.20f)
            lineTo(x + w * 0.04f, h * 0.34f)
            close()
        }
        drawPath(
            path = spire,
            brush = Brush.linearGradient(listOf(goldLight, goldMid))
        )
    }

    drawMinaret(w * 0.14f)
    drawMinaret(w * 0.86f)
}

private fun DrawScope.draw3DQuran(w: Float, h: Float, isActive: Boolean, gold: Color) {
    val goldLight = if (isActive) Color(0xFFFFF6C8) else Color(0xFFDEC585)
    val goldMid = if (isActive) gold else Color(0xFFB09546)
    val goldDark = Color(0xFF5A4410)
    val pageWhite = Color(0xFFEDE8D6)
    val pageShadow = Color(0xFFBFB7A0)

    // Drop shadow under Rihal
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color.Black.copy(alpha = 0.55f), Color.Transparent),
            center = Offset(w * 0.5f, h * 0.90f),
            radius = w * 0.42f
        ),
        topLeft = Offset(w * 0.15f, h * 0.82f),
        size = Size(w * 0.70f, h * 0.16f)
    )

    // Wooden Rihal Stand (X-cross stand supporting the Mushaf)
    val woodLeft = Path().apply {
        moveTo(w * 0.22f, h * 0.88f)
        lineTo(w * 0.78f, h * 0.62f)
        lineTo(w * 0.72f, h * 0.58f)
        lineTo(w * 0.16f, h * 0.84f)
        close()
    }
    val woodRight = Path().apply {
        moveTo(w * 0.78f, h * 0.88f)
        lineTo(w * 0.22f, h * 0.62f)
        lineTo(w * 0.28f, h * 0.58f)
        lineTo(w * 0.84f, h * 0.84f)
        close()
    }
    drawPath(woodLeft, brush = Brush.linearGradient(listOf(Color(0xFF38230D), Color(0xFF1E1105))))
    drawPath(woodRight, brush = Brush.linearGradient(listOf(Color(0xFF533414), Color(0xFF281708))))

    // 3D Gilded Book Covers (Beveled base under pages)
    val coverLeft = Path().apply {
        moveTo(w * 0.50f, h * 0.64f)
        lineTo(w * 0.12f, h * 0.48f)
        lineTo(w * 0.16f, h * 0.72f)
        lineTo(w * 0.50f, h * 0.78f)
        close()
    }
    val coverRight = Path().apply {
        moveTo(w * 0.50f, h * 0.64f)
        lineTo(w * 0.88f, h * 0.48f)
        lineTo(w * 0.84f, h * 0.72f)
        lineTo(w * 0.50f, h * 0.78f)
        close()
    }
    drawPath(coverLeft, brush = Brush.linearGradient(listOf(goldLight, goldMid, goldDark)))
    drawPath(coverRight, brush = Brush.linearGradient(listOf(goldMid, goldDark, Color(0xFF281C06))))

    // Open Pages (Curved 3D parchment leafs)
    val pageLeft = Path().apply {
        moveTo(w * 0.50f, h * 0.60f)
        cubicTo(w * 0.38f, h * 0.63f, w * 0.24f, h * 0.54f, w * 0.14f, h * 0.46f)
        lineTo(w * 0.16f, h * 0.22f)
        cubicTo(w * 0.28f, h * 0.30f, w * 0.42f, h * 0.24f, w * 0.50f, h * 0.28f)
        close()
    }
    val pageRight = Path().apply {
        moveTo(w * 0.50f, h * 0.60f)
        cubicTo(w * 0.62f, h * 0.63f, w * 0.76f, h * 0.54f, w * 0.86f, h * 0.46f)
        lineTo(w * 0.84f, h * 0.22f)
        cubicTo(w * 0.72f, h * 0.30f, w * 0.58f, h * 0.24f, w * 0.50f, h * 0.28f)
        close()
    }
    drawPath(
        path = pageLeft,
        brush = Brush.linearGradient(
            colors = listOf(pageWhite, pageShadow),
            start = Offset(w * 0.2f, h * 0.2f),
            end = Offset(w * 0.5f, h * 0.6f)
        )
    )
    drawPath(
        path = pageRight,
        brush = Brush.linearGradient(
            colors = listOf(pageWhite, pageShadow.copy(alpha = 0.8f)),
            start = Offset(w * 0.8f, h * 0.2f),
            end = Offset(w * 0.5f, h * 0.6f)
        )
    )

    // Golden Book Border / Page Edges
    drawPath(pageLeft, color = goldMid, style = Stroke(width = w * 0.03f))
    drawPath(pageRight, color = goldMid, style = Stroke(width = w * 0.03f))

    // Text Lines (Gold micro-calligraphy ridges)
    drawLine(goldMid.copy(alpha = 0.5f), Offset(w * 0.24f, h * 0.32f), Offset(w * 0.44f, h * 0.34f), strokeWidth = w * 0.02f)
    drawLine(goldMid.copy(alpha = 0.5f), Offset(w * 0.22f, h * 0.40f), Offset(w * 0.43f, h * 0.42f), strokeWidth = w * 0.02f)
    drawLine(goldMid.copy(alpha = 0.5f), Offset(w * 0.56f, h * 0.34f), Offset(w * 0.76f, h * 0.32f), strokeWidth = w * 0.02f)
    drawLine(goldMid.copy(alpha = 0.5f), Offset(w * 0.57f, h * 0.42f), Offset(w * 0.78f, h * 0.40f), strokeWidth = w * 0.02f)

    // Center Bookmark Silk Ribbon with Golden Tassel draping down
    val ribbon = Path().apply {
        moveTo(w * 0.50f, h * 0.26f)
        cubicTo(w * 0.48f, h * 0.45f, w * 0.54f, h * 0.65f, w * 0.50f, h * 0.82f)
    }
    drawPath(ribbon, color = goldLight, style = Stroke(width = w * 0.05f, cap = StrokeCap.Round))
    drawCircle(goldMid, radius = w * 0.04f, center = Offset(w * 0.50f, h * 0.82f))
}

private fun DrawScope.draw3DDuaHands(w: Float, h: Float, isActive: Boolean, gold: Color) {
    val goldLight = if (isActive) Color(0xFFFFF4BF) else Color(0xFFE2C98C)
    val goldMid = if (isActive) gold else Color(0xFFAD9346)
    val goldDark = Color(0xFF5A4410)
    val emeraldGlow = Color(0xFF00E676)

    // Radiant celestial orb glowing between palms
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                goldLight.copy(alpha = 0.85f),
                emeraldGlow.copy(alpha = 0.35f),
                Color.Transparent
            ),
            center = Offset(w * 0.50f, h * 0.30f),
            radius = w * 0.32f
        )
    )

    // Left Palm in 3D perspective
    val leftPalm = Path().apply {
        moveTo(w * 0.46f, h * 0.86f)
        cubicTo(w * 0.30f, h * 0.84f, w * 0.20f, h * 0.68f, w * 0.16f, h * 0.50f)
        // Pinky to Index finger tips
        lineTo(w * 0.24f, h * 0.36f)
        lineTo(w * 0.32f, h * 0.30f)
        lineTo(w * 0.42f, h * 0.28f)
        lineTo(w * 0.47f, h * 0.42f)
        close()
    }
    // Right Palm
    val rightPalm = Path().apply {
        moveTo(w * 0.54f, h * 0.86f)
        cubicTo(w * 0.70f, h * 0.84f, w * 0.80f, h * 0.68f, w * 0.84f, h * 0.50f)
        lineTo(w * 0.76f, h * 0.36f)
        lineTo(w * 0.68f, h * 0.30f)
        lineTo(w * 0.58f, h * 0.28f)
        lineTo(w * 0.53f, h * 0.42f)
        close()
    }

    drawPath(
        leftPalm,
        brush = Brush.radialGradient(
            colors = listOf(goldLight, goldMid, goldDark),
            center = Offset(w * 0.35f, h * 0.45f),
            radius = w * 0.30f
        )
    )
    drawPath(
        rightPalm,
        brush = Brush.radialGradient(
            colors = listOf(goldLight, goldMid, goldDark),
            center = Offset(w * 0.65f, h * 0.45f),
            radius = w * 0.30f
        )
    )

    // Palm bevel outlines
    drawPath(leftPalm, color = goldMid, style = Stroke(width = w * 0.035f, cap = StrokeCap.Round))
    drawPath(rightPalm, color = goldMid, style = Stroke(width = w * 0.035f, cap = StrokeCap.Round))

    // Finger separation notches
    drawLine(goldDark, Offset(w * 0.32f, h * 0.30f), Offset(w * 0.34f, h * 0.48f), strokeWidth = w * 0.025f)
    drawLine(goldDark, Offset(w * 0.68f, h * 0.30f), Offset(w * 0.66f, h * 0.48f), strokeWidth = w * 0.025f)

    // Noor sparkle star above the palms
    drawCircle(Color.White, radius = w * 0.04f, center = Offset(w * 0.50f, h * 0.22f))
}

private fun DrawScope.draw3DAstrolabe(w: Float, h: Float, isActive: Boolean, gold: Color) {
    val goldLight = if (isActive) Color(0xFFFFF6C8) else Color(0xFFDEC585)
    val goldMid = if (isActive) gold else Color(0xFFB09546)
    val goldDark = Color(0xFF5A4410)

    val cx = w * 0.50f
    val cy = h * 0.54f
    val radius = w * 0.38f

    // Top Shackle / Throne hanging ring
    drawCircle(
        color = goldMid,
        radius = w * 0.10f,
        center = Offset(cx, h * 0.16f),
        style = Stroke(width = w * 0.045f)
    )
    drawCircle(
        color = goldLight,
        radius = w * 0.05f,
        center = Offset(cx, h * 0.16f)
    )

    // Outer Armillary Ring (Mete)
    drawCircle(
        brush = Brush.sweepGradient(
            listOf(goldLight, goldMid, goldDark, goldLight),
            center = Offset(cx, cy)
        ),
        radius = radius,
        center = Offset(cx, cy),
        style = Stroke(width = w * 0.06f)
    )

    // Inner Celestial Plate
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF0C2420), Color(0xFF030D0C)),
            center = Offset(cx * 0.85f, cy * 0.85f),
            radius = radius * 0.9f
        ),
        radius = radius * 0.88f,
        center = Offset(cx, cy)
    )

    // Graduated Ring Axis Marks
    for (i in 0 until 8) {
        val angle = Math.toRadians((i * 45).toDouble())
        val startX = cx + (radius * 0.72f * cos(angle)).toFloat()
        val startY = cy + (radius * 0.72f * sin(angle)).toFloat()
        val endX = cx + (radius * 0.86f * cos(angle)).toFloat()
        val endY = cy + (radius * 0.86f * sin(angle)).toFloat()
        drawLine(goldLight.copy(alpha = 0.8f), Offset(startX, startY), Offset(endX, endY), strokeWidth = w * 0.025f)
    }

    // 3D Alidade (Pointer Needle)
    val needle = Path().apply {
        moveTo(cx - w * 0.04f, cy + radius * 0.65f)
        lineTo(cx + w * 0.04f, cy + radius * 0.65f)
        lineTo(cx + w * 0.02f, cy - radius * 0.65f)
        lineTo(cx, cy - radius * 0.72f)
        lineTo(cx - w * 0.02f, cy - radius * 0.65f)
        close()
    }
    drawPath(needle, brush = Brush.linearGradient(listOf(goldLight, goldMid)))

    // Central Brass Pivot Pin
    drawCircle(goldLight, radius = w * 0.07f, center = Offset(cx, cy))
    drawCircle(goldDark, radius = w * 0.03f, center = Offset(cx, cy))
}

private fun DrawScope.draw3DSettingsDials(w: Float, h: Float, isActive: Boolean, gold: Color) {
    val goldLight = if (isActive) Color(0xFFFFF6C8) else Color(0xFFDEC585)
    val goldMid = if (isActive) gold else Color(0xFFB09546)
    val goldDark = Color(0xFF5A4410)

    // Three horizontal grooved slider tracks
    fun drawTrack(y: Float, knobX: Float) {
        // Recessed track
        drawLine(
            color = Color(0xFF030D0C),
            start = Offset(w * 0.16f, y),
            end = Offset(w * 0.84f, y),
            strokeWidth = w * 0.08f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = goldDark.copy(alpha = 0.5f),
            start = Offset(w * 0.16f, y),
            end = Offset(w * 0.84f, y),
            strokeWidth = w * 0.02f,
            cap = StrokeCap.Round
        )

        // 3D Knurled Metallic Knob
        val knobRadius = w * 0.12f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent),
                center = Offset(knobX, y + knobRadius * 0.5f),
                radius = knobRadius * 1.2f
            ),
            radius = knobRadius * 1.1f,
            center = Offset(knobX, y + knobRadius * 0.3f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(goldLight, goldMid, goldDark),
                center = Offset(knobX - knobRadius * 0.3f, y - knobRadius * 0.3f),
                radius = knobRadius * 1.2f
            ),
            radius = knobRadius,
            center = Offset(knobX, y)
        )
        drawCircle(
            color = goldLight,
            radius = knobRadius * 0.4f,
            center = Offset(knobX, y)
        )
    }

    drawTrack(h * 0.28f, w * 0.68f)
    drawTrack(h * 0.52f, w * 0.36f)
    drawTrack(h * 0.76f, w * 0.62f)
}

private fun DrawScope.draw3DRuqyahShield(w: Float, h: Float, isActive: Boolean, gold: Color) {
    val goldLight = if (isActive) Color(0xFFFFF6C8) else Color(0xFFDEC585)
    val goldMid = if (isActive) gold else Color(0xFFB09546)
    val goldDark = Color(0xFF5A4410)
    val emeraldGlow = Color(0xFF00E676)

    // Outer Shield Bevel Path
    val shield = Path().apply {
        moveTo(w * 0.50f, h * 0.12f)
        lineTo(w * 0.84f, h * 0.24f)
        cubicTo(w * 0.84f, h * 0.58f, w * 0.68f, h * 0.78f, w * 0.50f, h * 0.90f)
        cubicTo(w * 0.32f, h * 0.78f, w * 0.16f, h * 0.58f, w * 0.16f, h * 0.24f)
        close()
    }

    // Shield body in deep architectural emerald
    drawPath(
        path = shield,
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF00A86B), Color(0xFF0A2621), Color(0xFF04100E)),
            center = Offset(w * 0.40f, h * 0.35f),
            radius = w * 0.55f
        )
    )

    // Gilded Shield Bevel Rim
    drawPath(
        path = shield,
        brush = Brush.linearGradient(
            colors = listOf(goldLight, goldMid, goldDark, goldLight),
            start = Offset(w * 0.2f, h * 0.1f),
            end = Offset(w * 0.8f, h * 0.9f)
        ),
        style = Stroke(width = w * 0.055f)
    )

    // Central Embossed Golden Crescent Moon
    val crescentPath = Path().apply {
        moveTo(w * 0.55f, h * 0.36f)
        cubicTo(w * 0.40f, h * 0.40f, w * 0.38f, h * 0.58f, w * 0.52f, h * 0.68f)
        cubicTo(w * 0.44f, h * 0.65f, w * 0.42f, h * 0.48f, w * 0.55f, h * 0.36f)
        close()
    }
    drawPath(crescentPath, brush = Brush.linearGradient(listOf(goldLight, goldMid)))

    // Protective 8-pointed star sparkle in center
    val starSize = w * 0.08f
    val scx = w * 0.58f
    val scy = h * 0.48f
    drawLine(goldLight, Offset(scx - starSize, scy), Offset(scx + starSize, scy), strokeWidth = w * 0.03f, cap = StrokeCap.Round)
    drawLine(goldLight, Offset(scx, scy - starSize), Offset(scx, scy + starSize), strokeWidth = w * 0.03f, cap = StrokeCap.Round)
    drawCircle(Color.White, radius = w * 0.03f, center = Offset(scx, scy))
}

private fun DrawScope.draw3DArabicTts(w: Float, h: Float, isActive: Boolean, gold: Color) {
    val goldLight = if (isActive) Color(0xFFFFF6C8) else Color(0xFFDEC585)
    val goldMid = if (isActive) gold else Color(0xFFB09546)
    val goldDark = Color(0xFF5A4410)

    // 3D Flared Brass Horn Speaker
    val horn = Path().apply {
        moveTo(w * 0.18f, h * 0.40f)
        lineTo(w * 0.34f, h * 0.40f)
        lineTo(w * 0.52f, h * 0.22f)
        lineTo(w * 0.52f, h * 0.78f)
        lineTo(w * 0.34f, h * 0.60f)
        lineTo(w * 0.18f, h * 0.60f)
        close()
    }
    drawPath(
        path = horn,
        brush = Brush.radialGradient(
            colors = listOf(goldLight, goldMid, goldDark),
            center = Offset(w * 0.32f, h * 0.45f),
            radius = w * 0.35f
        )
    )
    drawPath(horn, color = goldLight, style = Stroke(width = w * 0.035f))

    // Volumetric 3D Soundwaves (Expanding acoustic ripples)
    fun drawWave(radiusFactor: Float, sweep: Float) {
        val rect = Rect(w * 0.52f - w * radiusFactor, h * 0.50f - w * radiusFactor, w * 0.52f + w * radiusFactor, h * 0.50f + w * radiusFactor)
        drawArc(
            brush = Brush.sweepGradient(listOf(goldLight, goldMid, goldLight)),
            startAngle = -35f,
            sweepAngle = 70f,
            useCenter = false,
            topLeft = rect.topLeft,
            size = rect.size,
            style = Stroke(width = w * 0.05f, cap = StrokeCap.Round)
        )
    }
    drawWave(0.20f, 60f)
    drawWave(0.32f, 70f)
    drawWave(0.44f, 80f)
}

private fun DrawScope.draw3DAiSparkle(w: Float, h: Float, isActive: Boolean, gold: Color) {
    val goldLight = if (isActive) Color(0xFFFFF6C8) else Color(0xFFDEC585)
    val goldMid = if (isActive) gold else Color(0xFFB09546)
    val emeraldGlow = Color(0xFF00E676)

    // Central 4-point Diamond Prism
    val cx = w * 0.50f
    val cy = h * 0.50f
    val rLong = w * 0.42f
    val rShort = w * 0.14f

    val star = Path().apply {
        moveTo(cx, cy - rLong)
        cubicTo(cx + rShort * 0.2f, cy - rShort, cx + rShort, cy - rShort * 0.2f, cx + rLong, cy)
        cubicTo(cx + rShort, cy + rShort * 0.2f, cx + rShort * 0.2f, cy + rShort, cx, cy + rLong)
        cubicTo(cx - rShort * 0.2f, cy + rShort, cx - rShort, cy + rShort * 0.2f, cx - rLong, cy)
        cubicTo(cx - rShort, cy - rShort * 0.2f, cx - rShort * 0.2f, cy - rShort, cx, cy - rLong)
        close()
    }

    drawPath(
        path = star,
        brush = Brush.radialGradient(
            colors = listOf(Color.White, goldLight, goldMid, Color(0xFF00A86B)),
            center = Offset(cx - rShort * 0.4f, cy - rShort * 0.4f),
            radius = rLong
        )
    )
    drawPath(star, color = goldLight, style = Stroke(width = w * 0.035f))

    // Tiny orbiting satellite sparkle
    val scx = w * 0.80f
    val scy = h * 0.25f
    val sR = w * 0.14f
    val sSh = w * 0.04f
    val smallStar = Path().apply {
        moveTo(scx, scy - sR)
        lineTo(scx + sSh, scy)
        lineTo(scx, scy + sR)
        lineTo(scx - sSh, scy)
        close()
    }
    drawPath(smallStar, brush = Brush.linearGradient(listOf(Color.White, emeraldGlow)))
}

private fun DrawScope.draw3DTasbeehBeads(w: Float, h: Float, isActive: Boolean, gold: Color) {
    val goldLight = if (isActive) Color(0xFFFFF6C8) else Color(0xFFDEC585)
    val goldMid = if (isActive) gold else Color(0xFFB09546)
    val goldDark = Color(0xFF5A4410)

    // Center circular bead loop
    val cx = w * 0.50f
    val cy = h * 0.44f
    val loopRadius = w * 0.28f

    // 3D Spherical Beads along loop
    val numBeads = 7
    for (i in 0 until numBeads) {
        val angle = Math.toRadians((i * (360.0 / numBeads)).toDouble())
        val bx = cx + (loopRadius * cos(angle)).toFloat()
        val by = cy + (loopRadius * sin(angle)).toFloat()
        val beadR = w * 0.075f

        // Bead drop shadow
        drawCircle(
            color = Color.Black.copy(alpha = 0.5f),
            radius = beadR,
            center = Offset(bx + 1f, by + 1f)
        )
        // Volumetric amber/sandalwood sphere with top-left specular highlight
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(goldLight, goldMid, goldDark, Color(0xFF261805)),
                center = Offset(bx - beadR * 0.35f, by - beadR * 0.35f),
                radius = beadR * 1.2f
            ),
            radius = beadR,
            center = Offset(bx, by)
        )
        // Specular glint
        drawCircle(
            color = Color.White.copy(alpha = 0.75f),
            radius = beadR * 0.25f,
            center = Offset(bx - beadR * 0.35f, by - beadR * 0.35f)
        )
    }

    // Top Imam / Marker Bead
    val imamY = cy + loopRadius + w * 0.08f
    drawCircle(goldLight, radius = w * 0.06f, center = Offset(cx, imamY))

    // Hanging Silk Tassel
    val tassel = Path().apply {
        moveTo(cx, imamY)
        lineTo(cx - w * 0.07f, h * 0.90f)
        lineTo(cx + w * 0.07f, h * 0.90f)
        close()
    }
    drawPath(tassel, brush = Brush.linearGradient(listOf(goldMid, goldDark)))
}

private fun DrawScope.draw3DQiblaCompass(w: Float, h: Float, isActive: Boolean, gold: Color) {
    val goldLight = if (isActive) Color(0xFFFFF6C8) else Color(0xFFDEC585)
    val goldMid = if (isActive) gold else Color(0xFFB09546)
    val goldDark = Color(0xFF5A4410)

    val cx = w * 0.50f
    val cy = h * 0.50f
    val r = w * 0.42f

    // Outer Brass Bezel
    drawCircle(
        brush = Brush.sweepGradient(listOf(goldLight, goldMid, goldDark, goldLight), center = Offset(cx, cy)),
        radius = r,
        center = Offset(cx, cy),
        style = Stroke(width = w * 0.05f)
    )
    // Dark dial face
    drawCircle(
        brush = Brush.radialGradient(listOf(Color(0xFF0F2624), Color(0xFF020909)), center = Offset(cx, cy), radius = r),
        radius = r * 0.9f,
        center = Offset(cx, cy)
    )

    // Miniature 3D Kaaba Cube at Center
    val kw = w * 0.22f
    val kh = w * 0.22f
    val kx = cx - kw * 0.5f
    val ky = cy - kh * 0.5f
    drawRect(Color(0xFF0B1414), topLeft = Offset(kx, ky), size = Size(kw, kh))
    drawRect(goldMid, topLeft = Offset(kx, ky), size = Size(kw, kh), style = Stroke(width = w * 0.02f))
    // Golden Kiswah Band across top third
    drawLine(goldLight, Offset(kx, ky + kh * 0.3f), Offset(kx + kw, ky + kh * 0.3f), strokeWidth = w * 0.035f)

    // 3D Qibla Pointer Arrow pointing up-right toward Makkah
    val arrow = Path().apply {
        moveTo(cx, cy - r * 0.85f)
        lineTo(cx + w * 0.09f, cy - r * 0.45f)
        lineTo(cx, cy - r * 0.55f)
        lineTo(cx - w * 0.09f, cy - r * 0.45f)
        close()
    }
    drawPath(arrow, brush = Brush.linearGradient(listOf(goldLight, goldMid)))
}

private fun DrawScope.draw3DZakatScale(w: Float, h: Float, isActive: Boolean, gold: Color) {
    val goldLight = if (isActive) Color(0xFFFFF6C8) else Color(0xFFDEC585)
    val goldMid = if (isActive) gold else Color(0xFFB09546)
    val goldDark = Color(0xFF5A4410)

    val cx = w * 0.50f

    // Central Pillar with base
    drawLine(goldMid, Offset(cx, h * 0.20f), Offset(cx, h * 0.84f), strokeWidth = w * 0.055f, cap = StrokeCap.Round)
    drawOval(goldDark, topLeft = Offset(cx - w * 0.22f, h * 0.80f), size = Size(w * 0.44f, h * 0.12f))
    drawCircle(goldLight, radius = w * 0.05f, center = Offset(cx, h * 0.20f))

    // Horizontal Balance Beam
    drawLine(goldLight, Offset(w * 0.16f, h * 0.32f), Offset(w * 0.84f, h * 0.32f), strokeWidth = w * 0.045f, cap = StrokeCap.Round)

    // Left Pan & Chains
    drawLine(goldDark, Offset(w * 0.18f, h * 0.32f), Offset(w * 0.10f, h * 0.56f), strokeWidth = w * 0.02f)
    drawLine(goldDark, Offset(w * 0.18f, h * 0.32f), Offset(w * 0.26f, h * 0.56f), strokeWidth = w * 0.02f)
    drawArc(goldLight, startAngle = 0f, sweepAngle = 180f, useCenter = false, topLeft = Offset(w * 0.10f, h * 0.52f), size = Size(w * 0.16f, h * 0.12f), style = Stroke(width = w * 0.035f))

    // Right Pan & Chains (with gold coins inside)
    drawLine(goldDark, Offset(w * 0.82f, h * 0.32f), Offset(w * 0.74f, h * 0.56f), strokeWidth = w * 0.02f)
    drawLine(goldDark, Offset(w * 0.82f, h * 0.32f), Offset(w * 0.90f, h * 0.56f), strokeWidth = w * 0.02f)
    drawArc(goldLight, startAngle = 0f, sweepAngle = 180f, useCenter = false, topLeft = Offset(w * 0.74f, h * 0.52f), size = Size(w * 0.16f, h * 0.12f), style = Stroke(width = w * 0.035f))
    drawCircle(goldLight, radius = w * 0.035f, center = Offset(w * 0.82f, h * 0.54f))
}

private fun DrawScope.draw3DMosqueDnd(w: Float, h: Float, isActive: Boolean, gold: Color) {
    val goldLight = if (isActive) Color(0xFFFFF6C8) else Color(0xFFDEC585)
    val goldMid = if (isActive) gold else Color(0xFFB09546)
    val emeraldGlow = Color(0xFF00E676)

    // Outer acoustic silence halo ring
    drawCircle(
        color = emeraldGlow.copy(alpha = 0.8f),
        radius = w * 0.42f,
        center = Offset(w * 0.50f, h * 0.50f),
        style = Stroke(width = w * 0.045f)
    )

    // Inner Mosque Dome silhouette
    val dome = Path().apply {
        moveTo(w * 0.28f, h * 0.72f)
        cubicTo(w * 0.28f, h * 0.44f, w * 0.44f, h * 0.28f, w * 0.50f, h * 0.22f)
        cubicTo(w * 0.56f, h * 0.28f, w * 0.72f, h * 0.44f, w * 0.72f, h * 0.72f)
        close()
    }
    drawPath(dome, brush = Brush.linearGradient(listOf(goldLight, goldMid)))

    // Central DND silence horizontal bar
    drawLine(
        color = Color(0xFFFFF5C0),
        start = Offset(w * 0.32f, h * 0.52f),
        end = Offset(w * 0.68f, h * 0.52f),
        strokeWidth = w * 0.06f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.draw3DAsmaUlHusna(w: Float, h: Float, isActive: Boolean, gold: Color) {
    val goldLight = if (isActive) Color(0xFFFFF6C8) else Color(0xFFDEC585)
    val goldMid = if (isActive) gold else Color(0xFFB09546)
    val goldDark = Color(0xFF5A4410)

    val cx = w * 0.50f
    val cy = h * 0.50f
    val r = w * 0.40f

    // 8-Pointed Rub-el-Hizb / Octagram Medallion
    fun drawRotatedSquare(deg: Float) {
        val path = Path()
        val rad = Math.toRadians(deg.toDouble())
        for (i in 0 until 4) {
            val a = rad + Math.toRadians((i * 90.0))
            val x = cx + (r * cos(a)).toFloat()
            val y = cy + (r * sin(a)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path, brush = Brush.linearGradient(listOf(goldLight, goldMid, goldDark)), style = Stroke(width = w * 0.045f))
    }

    drawRotatedSquare(0f)
    drawRotatedSquare(45f)

    // Inner embossed golden calligraphic seal
    drawCircle(
        brush = Brush.radialGradient(listOf(goldLight, goldMid, goldDark)),
        radius = r * 0.55f,
        center = Offset(cx, cy)
    )
    drawCircle(Color.White, radius = r * 0.20f, center = Offset(cx - r * 0.15f, cy - r * 0.15f))
}

private fun DrawScope.draw3DLocationPin(w: Float, h: Float, isActive: Boolean, gold: Color) {
    val goldLight = if (isActive) Color(0xFFFFF6C8) else Color(0xFFDEC585)
    val goldMid = if (isActive) gold else Color(0xFFB09546)
    val goldDark = Color(0xFF5A4410)

    // Contact drop shadow
    drawOval(
        color = Color.Black.copy(alpha = 0.5f),
        topLeft = Offset(w * 0.28f, h * 0.82f),
        size = Size(w * 0.44f, h * 0.12f)
    )

    // 3D Teardrop Pin
    val pin = Path().apply {
        moveTo(w * 0.50f, h * 0.88f)
        cubicTo(w * 0.28f, h * 0.58f, w * 0.18f, h * 0.40f, w * 0.18f, h * 0.32f)
        cubicTo(w * 0.18f, h * 0.15f, w * 0.32f, h * 0.08f, w * 0.50f, h * 0.08f)
        cubicTo(w * 0.68f, h * 0.08f, w * 0.82f, h * 0.15f, w * 0.82f, h * 0.32f)
        cubicTo(w * 0.82f, h * 0.40f, w * 0.72f, h * 0.58f, w * 0.50f, h * 0.88f)
        close()
    }

    drawPath(
        path = pin,
        brush = Brush.radialGradient(
            colors = listOf(goldLight, goldMid, goldDark),
            center = Offset(w * 0.40f, h * 0.25f),
            radius = w * 0.45f
        )
    )
    drawPath(pin, color = goldLight, style = Stroke(width = w * 0.035f))

    // Inner Emerald Lens
    drawCircle(
        brush = Brush.radialGradient(listOf(Color(0xFF00E676), Color(0xFF07241F))),
        radius = w * 0.16f,
        center = Offset(w * 0.50f, h * 0.32f)
    )
}

private fun DrawScope.draw3DSearchLens(w: Float, h: Float, isActive: Boolean, gold: Color) {
    val goldLight = if (isActive) Color(0xFFFFF6C8) else Color(0xFFDEC585)
    val goldMid = if (isActive) gold else Color(0xFFB09546)
    val goldDark = Color(0xFF5A4410)

    val cx = w * 0.42f
    val cy = h * 0.42f
    val r = w * 0.28f

    // Beveled Brass Lens Rim
    drawCircle(
        brush = Brush.sweepGradient(listOf(goldLight, goldMid, goldDark, goldLight), center = Offset(cx, cy)),
        radius = r,
        center = Offset(cx, cy),
        style = Stroke(width = w * 0.06f)
    )

    // Glass Optical Face with Specular Reflection
    drawCircle(
        brush = Brush.radialGradient(listOf(Color(0xFF163833), Color(0xFF04100E)), center = Offset(cx, cy), radius = r),
        radius = r * 0.9f,
        center = Offset(cx, cy)
    )
    drawArc(
        color = Color.White.copy(alpha = 0.55f),
        startAngle = -120f,
        sweepAngle = 60f,
        useCenter = false,
        topLeft = Offset(cx - r * 0.7f, cy - r * 0.7f),
        size = Size(r * 1.4f, r * 1.4f),
        style = Stroke(width = w * 0.03f, cap = StrokeCap.Round)
    )

    // 3D Angled Beveled Handle
    val handle = Path().apply {
        moveTo(cx + r * 0.65f, cy + r * 0.65f)
        lineTo(w * 0.84f, h * 0.84f)
    }
    drawPath(handle, brush = Brush.linearGradient(listOf(goldLight, goldMid, goldDark)), style = Stroke(width = w * 0.09f, cap = StrokeCap.Round))
}

private fun DrawScope.draw3DBookmarkRibbon(w: Float, h: Float, isActive: Boolean, gold: Color) {
    val goldLight = if (isActive) Color(0xFFFFF6C8) else Color(0xFFDEC585)
    val goldMid = if (isActive) gold else Color(0xFFB09546)
    val goldDark = Color(0xFF5A4410)

    // 3D Folded Ribbon Flag with swallow-tail cut
    val ribbon = Path().apply {
        moveTo(w * 0.26f, h * 0.12f)
        lineTo(w * 0.74f, h * 0.12f)
        lineTo(w * 0.74f, h * 0.88f)
        lineTo(w * 0.50f, h * 0.70f)
        lineTo(w * 0.26f, h * 0.88f)
        close()
    }
    drawPath(
        path = ribbon,
        brush = Brush.linearGradient(
            colors = listOf(goldLight, goldMid, goldDark),
            start = Offset(w * 0.26f, h * 0.12f),
            end = Offset(w * 0.74f, h * 0.88f)
        )
    )
    drawPath(ribbon, color = goldLight, style = Stroke(width = w * 0.035f))
    drawCircle(Color.White, radius = w * 0.045f, center = Offset(w * 0.50f, h * 0.35f))
}

private fun DrawScope.draw3DSunnahHealth(w: Float, h: Float, isActive: Boolean, gold: Color) {
    val mint = if (isActive) Color(0xFF00E676) else Color(0xFF00A86B)
    val mintDark = Color(0xFF0D382B)
    val goldLight = if (isActive) Color(0xFFFFF6C8) else Color(0xFFDEC585)

    // 3D Prophetic Medicine Bowl / Mortar
    val bowl = Path().apply {
        moveTo(w * 0.20f, h * 0.50f)
        cubicTo(w * 0.22f, h * 0.82f, w * 0.78f, h * 0.82f, w * 0.80f, h * 0.50f)
        close()
    }
    drawPath(
        path = bowl,
        brush = Brush.radialGradient(
            colors = listOf(goldLight, gold, Color(0xFF4A380B)),
            center = Offset(w * 0.5f, h * 0.65f),
            radius = w * 0.35f
        )
    )
    drawPath(bowl, color = goldLight, style = Stroke(width = w * 0.035f))

    // 3D Olive / Black Seed Leaf rising from bowl
    val leaf = Path().apply {
        moveTo(w * 0.50f, h * 0.52f)
        cubicTo(w * 0.35f, h * 0.35f, w * 0.40f, h * 0.18f, w * 0.50f, h * 0.15f)
        cubicTo(w * 0.60f, h * 0.18f, w * 0.65f, h * 0.35f, w * 0.50f, h * 0.52f)
        close()
    }
    drawPath(
        path = leaf,
        brush = Brush.linearGradient(listOf(Color(0xFFB9F6CA), mint, mintDark))
    )
    drawPath(leaf, color = Color.White.copy(alpha = 0.6f), style = Stroke(width = w * 0.025f))
}

private fun DrawScope.draw3DInheritanceWealth(w: Float, h: Float, isActive: Boolean, gold: Color) {
    val goldLight = if (isActive) Color(0xFFFFF6C8) else Color(0xFFDEC585)
    val goldDark = Color(0xFF5A4410)

    // 3D Balance Beam for Equitable Distribution
    drawLine(
        brush = Brush.linearGradient(listOf(goldLight, gold, goldDark)),
        start = Offset(w * 0.18f, h * 0.36f),
        end = Offset(w * 0.82f, h * 0.36f),
        strokeWidth = w * 0.05f,
        cap = StrokeCap.Round
    )

    // Center Stand
    drawLine(
        brush = Brush.linearGradient(listOf(goldLight, goldDark)),
        start = Offset(w * 0.50f, h * 0.22f),
        end = Offset(w * 0.50f, h * 0.85f),
        strokeWidth = w * 0.06f,
        cap = StrokeCap.Round
    )

    // Base Pedestal
    drawRoundRect(
        brush = Brush.linearGradient(listOf(goldLight, gold)),
        topLeft = Offset(w * 0.32f, h * 0.82f),
        size = Size(w * 0.36f, h * 0.08f),
        cornerRadius = CornerRadius(w * 0.03f)
    )

    // Left Scale Pan
    drawArc(
        brush = Brush.linearGradient(listOf(goldLight, goldDark)),
        startAngle = 0f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(w * 0.14f, h * 0.48f),
        size = Size(w * 0.22f, h * 0.16f),
        style = Stroke(width = w * 0.035f)
    )

    // Right Scale Pan
    drawArc(
        brush = Brush.linearGradient(listOf(goldLight, goldDark)),
        startAngle = 0f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(w * 0.64f, h * 0.48f),
        size = Size(w * 0.22f, h * 0.16f),
        style = Stroke(width = w * 0.035f)
    )
}

private fun DrawScope.draw3DJanazahWasiyyah(w: Float, h: Float, isActive: Boolean, gold: Color) {
    val goldLight = if (isActive) Color(0xFFFFF6C8) else Color(0xFFDEC585)
    val goldDark = Color(0xFF5A4410)

    // 3D Ancient Parchment Scroll
    val scroll = Path().apply {
        moveTo(w * 0.28f, h * 0.18f)
        lineTo(w * 0.72f, h * 0.18f)
        cubicTo(w * 0.80f, h * 0.18f, w * 0.80f, h * 0.26f, w * 0.72f, h * 0.26f)
        lineTo(w * 0.72f, h * 0.76f)
        cubicTo(w * 0.80f, h * 0.76f, w * 0.80f, h * 0.84f, w * 0.72f, h * 0.84f)
        lineTo(w * 0.28f, h * 0.84f)
        cubicTo(w * 0.20f, h * 0.84f, w * 0.20f, h * 0.76f, w * 0.28f, h * 0.76f)
        lineTo(w * 0.28f, h * 0.26f)
        cubicTo(w * 0.20f, h * 0.26f, w * 0.20f, h * 0.18f, w * 0.28f, h * 0.18f)
        close()
    }
    drawPath(
        path = scroll,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFF223A36), Color(0xFF102220), Color(0xFF081514)),
            start = Offset(w * 0.2f, h * 0.2f),
            end = Offset(w * 0.8f, h * 0.8f)
        )
    )
    drawPath(scroll, color = goldLight, style = Stroke(width = w * 0.03f))

    // Gold Wax Seal
    drawCircle(
        brush = Brush.radialGradient(listOf(goldLight, gold, goldDark), center = Offset(w * 0.50f, h * 0.52f), radius = w * 0.14f),
        radius = w * 0.12f,
        center = Offset(w * 0.50f, h * 0.52f)
    )
    drawCircle(Color.White.copy(alpha = 0.7f), radius = w * 0.03f, center = Offset(w * 0.47f, h * 0.49f))
}

private fun DrawScope.draw3DTadabburQuran(w: Float, h: Float, isActive: Boolean, gold: Color) {
    val goldLight = if (isActive) Color(0xFFFFF6C8) else Color(0xFFDEC585)
    val mint = if (isActive) Color(0xFF00E676) else Color(0xFF00A86B)

    // Open Book Wings
    val leftPage = Path().apply {
        moveTo(w * 0.50f, h * 0.72f)
        lineTo(w * 0.18f, h * 0.65f)
        cubicTo(w * 0.22f, h * 0.42f, w * 0.22f, h * 0.35f, w * 0.48f, h * 0.40f)
        close()
    }
    val rightPage = Path().apply {
        moveTo(w * 0.50f, h * 0.72f)
        lineTo(w * 0.82f, h * 0.65f)
        cubicTo(w * 0.78f, h * 0.42f, w * 0.78f, h * 0.35f, w * 0.52f, h * 0.40f)
        close()
    }
    drawPath(leftPage, brush = Brush.linearGradient(listOf(goldLight, gold)))
    drawPath(rightPage, brush = Brush.linearGradient(listOf(gold, goldLight)))

    // Radiating Spiritual Light Sparks
    drawCircle(mint, radius = w * 0.04f, center = Offset(w * 0.50f, h * 0.24f))
    drawLine(goldLight, start = Offset(w * 0.50f, h * 0.14f), end = Offset(w * 0.50f, h * 0.20f), strokeWidth = w * 0.03f, cap = StrokeCap.Round)
    drawLine(goldLight, start = Offset(w * 0.38f, h * 0.20f), end = Offset(w * 0.43f, h * 0.24f), strokeWidth = w * 0.03f, cap = StrokeCap.Round)
    drawLine(goldLight, start = Offset(w * 0.62f, h * 0.20f), end = Offset(w * 0.57f, h * 0.24f), strokeWidth = w * 0.03f, cap = StrokeCap.Round)
}

