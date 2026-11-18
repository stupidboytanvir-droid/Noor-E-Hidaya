package com.example.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

// Global 2-Mode Theme Definitions (Dark & Custom)
enum class ThemeMode(
    val id: String,
    val titleBengali: String,
    val titleEnglish: String
) {
    DARK("DARK", "ডার্ক মোড", "Dark Mode"),
    CUSTOM("CUSTOM", "কাস্টম মোড", "Custom Mode");

    companion object {
        fun fromId(id: String): ThemeMode =
            entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: DARK
    }
}

// Light Mode Color Constants
val LightBackground = Color(0xFFF8F9FA) // Pristine light canvas background
val LightSurface = Color(0xFFFFFFFF)    // Pure white surface card
val LightElevated = Color(0xFFEFF2F5)   // Light elevated card
val LightCard = Color(0xFFFFFFFF)       // Pure white card container
val LightTextPrimary = Color(0xFF111827) // Deep charcoal / midnight black for ultra high legibility
val LightTextSecondary = Color(0xFF4B5563) // Slate gray secondary text
val LightTextTertiary = Color(0xFF6B7280) // Muted tertiary text
val LightBorder = Color(0x26111827)     // Subtle border in light mode
val LightHairlineGold = Color(0x40B39642)
val LightDockBackground = Color(0xF2FFFFFF)
val LightDockBorder = Color(0x33B39642)

// Prohibited / Makruh Waqt (Light & Dark)
val MakruhBackground = Color(0xFF2C1114)
val MakruhBorder = Color(0xFFE05D68)
val ProhibitedRose = MakruhBorder
val ProhibitedSurface = MakruhBackground
val LightMakruhBackground = Color(0xFFFFECEE)
val LightMakruhBorder = Color(0xFFD32F2F)

// Dark Default / Base Color Constants
val ObsidianBackground = Color(0xFF030707)
val DarkEmeraldSurface = Color(0xFF0A1312)
val DarkEmeraldElevated = Color(0xFF112220)
val DarkEmeraldCard = Color(0xFF0D1A18)
val ChampagneGold = Color(0xFFE5C158)
val ChampagneGoldMuted = Color(0xFFB39642)
val MintGlow = Color(0xFF00E676)
val MintGlowSubdued = Color(0xFF00965E)

// Dark Typography Colors
val TextPrimary = Color(0xFFF3F7F6)
val SlateGrayMuted = Color(0xFF8E9A98)
val TextSecondary = Color(0xFF8E9A98)
val TextTertiary = Color(0xFF5A6664)

// Dark Hairline Elements
val HairlineBorder = Color(0x1AFFFFFF)
val HairlineGold = Color(0x33E5C158)

// Dark Navigation Dock
val DockBackground = Color(0xE6060D0C)
val DockBorder = Color(0x26E5C158)

/**
 * 1. Dynamic Canvas Background & Surface System
 */
enum class AppCanvasTheme(
    val id: String,
    val titleBengali: String,
    val titleEnglish: String,
    val background: Color,
    val surface: Color,
    val elevated: Color,
    val card: Color,
    val border: Color
) {
    OBSIDIAN(
        id = "OBSIDIAN",
        titleBengali = "অবসিডিয়ান মিডনাইট",
        titleEnglish = "Obsidian Midnight",
        background = Color(0xFF030707),
        surface = Color(0xFF0A1312),
        elevated = Color(0xFF112220),
        card = Color(0xFF0D1A18),
        border = Color(0x1AFFFFFF)
    ),
    DEEP_NAVY(
        id = "DEEP_NAVY",
        titleBengali = "ডিপ নেভি ব্লু",
        titleEnglish = "Deep Navy Blue",
        background = Color(0xFF050C16),
        surface = Color(0xFF0A1626),
        elevated = Color(0xFF112238),
        card = Color(0xFF0D1B2F),
        border = Color(0x244FC3F7)
    ),
    RICH_EMERALD(
        id = "RICH_EMERALD",
        titleBengali = "রিচ এমারেল্ড ডার্ক",
        titleEnglish = "Rich Emerald Dark",
        background = Color(0xFF041512),
        surface = Color(0xFF07211C),
        elevated = Color(0xFF0D322B),
        card = Color(0xFF0A2B24),
        border = Color(0x2400E676)
    ),
    AMOLED_BLACK(
        id = "AMOLED_BLACK",
        titleBengali = "পিওর অ্যামোলেড ব্ল্যাক",
        titleEnglish = "Pure AMOLED Black",
        background = Color(0xFF000000),
        surface = Color(0xFF0D0D0D),
        elevated = Color(0xFF1A1A1A),
        card = Color(0xFF141414),
        border = Color(0x2BFFFFFF)
    );

    companion object {
        fun fromId(id: String): AppCanvasTheme = entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: OBSIDIAN
    }
}

/**
 * 2. Dynamic Accent & Highlight System
 */
enum class AppAccentTheme(
    val id: String,
    val titleBengali: String,
    val titleEnglish: String,
    val primaryAccent: Color,
    val primaryAccentMuted: Color,
    val secondaryAccent: Color,
    val previewDotColor: Color
) {
    CHAMPAGNE_GOLD(
        id = "CHAMPAGNE_GOLD",
        titleBengali = "শ্যাম্পেন গোল্ড",
        titleEnglish = "Brushed Champagne Gold",
        primaryAccent = Color(0xFFE5C158),
        primaryAccentMuted = Color(0xFFB39642),
        secondaryAccent = Color(0xFF00E676),
        previewDotColor = Color(0xFFE5C158)
    ),
    MINT_GLOW(
        id = "MINT_GLOW",
        titleBengali = "মিন্ট গ্লো",
        titleEnglish = "Mint Glow",
        primaryAccent = Color(0xFF00E676),
        primaryAccentMuted = Color(0xFF00A352),
        secondaryAccent = Color(0xFFE5C158),
        previewDotColor = Color(0xFF00E676)
    ),
    ROYAL_AMBER(
        id = "ROYAL_AMBER",
        titleBengali = "রয়্যাল অ্যাম্বার",
        titleEnglish = "Royal Amber",
        primaryAccent = Color(0xFFFFB300),
        primaryAccentMuted = Color(0xFFC68400),
        secondaryAccent = Color(0xFF00E676),
        previewDotColor = Color(0xFFFFB300)
    ),
    DEEP_CRIMSON(
        id = "DEEP_CRIMSON",
        titleBengali = "ডিপ ক্রিমসন রুবি",
        titleEnglish = "Deep Crimson Ruby",
        primaryAccent = Color(0xFFFF5252),
        primaryAccentMuted = Color(0xFFD32F2F),
        secondaryAccent = Color(0xFFFFD54F),
        previewDotColor = Color(0xFFFF5252)
    ),
    CELESTIAL_CYAN(
        id = "CELESTIAL_CYAN",
        titleBengali = "সেলেস্টিয়াল সায়ান",
        titleEnglish = "Celestial Cyan",
        primaryAccent = Color(0xFF00E5FF),
        primaryAccentMuted = Color(0xFF00B0FF),
        secondaryAccent = Color(0xFFE5C158),
        previewDotColor = Color(0xFF00E5FF)
    );

    companion object {
        fun fromId(id: String): AppAccentTheme = entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: CHAMPAGNE_GOLD
    }
}

/**
 * App Colors Data Container with Full Theme Support & Granular Custom Color Overrides
 */
data class AppColors(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val elevated: Color,
    val card: Color,
    val primaryAccent: Color,
    val primaryAccentMuted: Color,
    val secondaryAccent: Color,
    val border: Color,
    val hairlineGold: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val dockBackground: Color,
    val dockBorder: Color,
    val makruhBackground: Color,
    val makruhBorder: Color,
    // Granular Element Overrides
    val arabicTextColor: Color,
    val translationTextColor: Color,
    val transliterationTextColor: Color
)

val LocalAppColors = staticCompositionLocalOf {
    AppColors(
        isDark = true,
        background = ObsidianBackground,
        surface = DarkEmeraldSurface,
        elevated = DarkEmeraldElevated,
        card = DarkEmeraldCard,
        primaryAccent = ChampagneGold,
        primaryAccentMuted = ChampagneGoldMuted,
        secondaryAccent = MintGlow,
        border = HairlineBorder,
        hairlineGold = HairlineGold,
        textPrimary = TextPrimary,
        textSecondary = TextSecondary,
        textTertiary = TextTertiary,
        dockBackground = DockBackground,
        dockBorder = DockBorder,
        makruhBackground = MakruhBackground,
        makruhBorder = MakruhBorder,
        arabicTextColor = TextPrimary,
        translationTextColor = TextPrimary,
        transliterationTextColor = MintGlow
    )
}

/**
 * Helper to parse hex string into Color safely
 */
fun parseHexColor(hex: String, defaultColor: Color = ChampagneGold): Color {
    return try {
        val cleanHex = hex.trim().removePrefix("#")
        when (cleanHex.length) {
            6 -> Color(android.graphics.Color.parseColor("#$cleanHex"))
            8 -> Color(android.graphics.Color.parseColor("#$cleanHex"))
            else -> defaultColor
        }
    } catch (_: Exception) {
        defaultColor
    }
}

fun Color.toHex(): String {
    val argb = this.toArgb()
    return String.format("#%06X", 0xFFFFFF and argb)
}
