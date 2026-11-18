package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.LayoutDirection
import com.example.data.util.LocaleHelper

// Legacy ThemeStyle enum preserved for backward compatibility
enum class ThemeStyle(
    val id: String,
    val titleBengali: String,
    val titleEnglish: String,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val surfaceColor: Color,
    val elevatedColor: Color
) {
    EMERALD_GOLD(
        id = "EMERALD_GOLD",
        titleBengali = "মরোদ্দী পান্না ও সোনা (সিগনেচার)",
        titleEnglish = "Emerald Gold (Signature)",
        primaryAccent = Color(0xFFE5C158),
        secondaryAccent = Color(0xFF00D084),
        surfaceColor = Color(0xFF0A1312),
        elevatedColor = Color(0xFF112220)
    ),
    MIDNIGHT_DEEP(
        id = "MIDNIGHT_DEEP",
        titleBengali = "মধ্যরাত ও গাঢ় সায়ান",
        titleEnglish = "Midnight Deep Cyan",
        primaryAccent = Color(0xFFE5C158),
        secondaryAccent = Color(0xFF00E5FF),
        surfaceColor = Color(0xFF071415),
        elevatedColor = Color(0xFF0D2124)
    ),
    GOLDEN_ROYAL(
        id = "GOLDEN_ROYAL",
        titleBengali = "রাজকীয় সোনালী হরমাইন",
        titleEnglish = "Haramain Golden Royal",
        primaryAccent = Color(0xFFFFD700),
        secondaryAccent = Color(0xFF00D084),
        surfaceColor = Color(0xFF12140D),
        elevatedColor = Color(0xFF202417)
    ),
    VELVET_TEAL(
        id = "VELVET_TEAL",
        titleBengali = "ভেলভেট টিল ও পুদিনা",
        titleEnglish = "Velvet Teal Glow",
        primaryAccent = Color(0xFFE5C158),
        secondaryAccent = Color(0xFF1DE9B6),
        surfaceColor = Color(0xFF081816),
        elevatedColor = Color(0xFF0F2C28)
    );

    companion object {
        fun fromId(id: String): ThemeStyle = entries.firstOrNull { it.id == id } ?: EMERALD_GOLD
    }
}

val LocalThemeStyle = staticCompositionLocalOf { ThemeStyle.EMERALD_GOLD }
val LocalThemeMode = staticCompositionLocalOf { ThemeMode.DARK }

fun buildColorScheme(
    appColors: AppColors
): ColorScheme {
    return if (appColors.isDark) {
        darkColorScheme(
            primary = appColors.primaryAccent,
            onPrimary = appColors.background,
            primaryContainer = appColors.elevated,
            onPrimaryContainer = appColors.primaryAccent,
            secondary = appColors.secondaryAccent,
            onSecondary = appColors.background,
            secondaryContainer = appColors.card,
            onSecondaryContainer = appColors.secondaryAccent,
            background = appColors.background,
            onBackground = appColors.textPrimary,
            surface = appColors.surface,
            onSurface = appColors.textPrimary,
            surfaceVariant = appColors.elevated,
            onSurfaceVariant = appColors.textSecondary,
            outline = appColors.textSecondary,
            outlineVariant = appColors.border
        )
    } else {
        lightColorScheme(
            primary = appColors.primaryAccent,
            onPrimary = Color.White,
            primaryContainer = appColors.elevated,
            onPrimaryContainer = appColors.textPrimary,
            secondary = appColors.secondaryAccent,
            onSecondary = Color.White,
            secondaryContainer = appColors.card,
            onSecondaryContainer = appColors.textPrimary,
            background = appColors.background,
            onBackground = appColors.textPrimary,
            surface = appColors.surface,
            onSurface = appColors.textPrimary,
            surfaceVariant = appColors.elevated,
            onSurfaceVariant = appColors.textSecondary,
            outline = appColors.textSecondary,
            outlineVariant = appColors.border
        )
    }
}

/**
 * Noor-e-Hidayah Master Theme
 * Dynamically binds Global 3-Mode Theme (Light, Dark, Custom), Canvas Background,
 * Accent Colors, UI Fonts, Arabic Scripts & Text Scaling.
 */
@Composable
fun NoorEHidayahTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    customAccentColorHex: String = "#E5C158",
    customArabicColorHex: String = "",
    customTranslationColorHex: String = "",
    customTransliterationColorHex: String = "",
    canvasTheme: AppCanvasTheme = AppCanvasTheme.OBSIDIAN,
    accentTheme: AppAccentTheme = AppAccentTheme.CHAMPAGNE_GOLD,
    uiFont: AppUiFont = AppUiFont.INTER,
    arabicFont: AppArabicFont = AppArabicFont.AMIRI,
    textScale: Float = 1.0f,
    style: ThemeStyle = ThemeStyle.EMERALD_GOLD,
    content: @Composable () -> Unit
) {
    val appColors = remember(
        themeMode,
        customAccentColorHex,
        customArabicColorHex,
        customTranslationColorHex,
        customTransliterationColorHex,
        canvasTheme,
        accentTheme
    ) {
        val baseColors = when (themeMode) {
            ThemeMode.DARK -> {
                AppColors(
                    isDark = true,
                    background = canvasTheme.background,
                    surface = canvasTheme.surface,
                    elevated = canvasTheme.elevated,
                    card = canvasTheme.card,
                    primaryAccent = accentTheme.primaryAccent,
                    primaryAccentMuted = accentTheme.primaryAccentMuted,
                    secondaryAccent = accentTheme.secondaryAccent,
                    border = canvasTheme.border,
                    hairlineGold = accentTheme.primaryAccent.copy(alpha = 0.28f),
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
            ThemeMode.CUSTOM -> {
                val customPrimary = parseHexColor(customAccentColorHex, ChampagneGold)
                val customPrimaryMuted = customPrimary.copy(alpha = 0.75f)
                val customSecondary = Color(0xFF00E676)
                AppColors(
                    isDark = true,
                    background = canvasTheme.background,
                    surface = canvasTheme.surface,
                    elevated = canvasTheme.elevated,
                    card = canvasTheme.card,
                    primaryAccent = customPrimary,
                    primaryAccentMuted = customPrimaryMuted,
                    secondaryAccent = customSecondary,
                    border = customPrimary.copy(alpha = 0.25f),
                    hairlineGold = customPrimary.copy(alpha = 0.35f),
                    textPrimary = TextPrimary,
                    textSecondary = TextSecondary,
                    textTertiary = TextTertiary,
                    dockBackground = DockBackground,
                    dockBorder = customPrimary.copy(alpha = 0.30f),
                    makruhBackground = MakruhBackground,
                    makruhBorder = MakruhBorder,
                    arabicTextColor = TextPrimary,
                    translationTextColor = TextPrimary,
                    transliterationTextColor = customSecondary
                )
            }
        }

        // Apply granular user overrides if specified
        baseColors.copy(
            arabicTextColor = if (customArabicColorHex.isNotBlank()) parseHexColor(customArabicColorHex, baseColors.arabicTextColor) else baseColors.arabicTextColor,
            translationTextColor = if (customTranslationColorHex.isNotBlank()) parseHexColor(customTranslationColorHex, baseColors.translationTextColor) else baseColors.translationTextColor,
            transliterationTextColor = if (customTransliterationColorHex.isNotBlank()) parseHexColor(customTransliterationColorHex, baseColors.transliterationTextColor) else baseColors.transliterationTextColor
        )
    }

    val selectedUiFontFamily = remember(uiFont) {
        when (uiFont) {
            AppUiFont.INTER -> InterFontFamily
            AppUiFont.OUTFIT -> OutfitFontFamily
            AppUiFont.LORA -> LoraFontFamily
            AppUiFont.SYSTEM -> FontFamily.Default
        }
    }

    val selectedArabicFontFamily = remember(arabicFont) {
        when (arabicFont) {
            AppArabicFont.AMIRI -> ClassicalAmiriFontFamily
            AppArabicFont.NOTO_NASKH -> NotoNaskhFontFamily
            AppArabicFont.SCHEHERAZADE -> ScheherazadeFontFamily
        }
    }

    val colorScheme = remember(appColors) {
        buildColorScheme(appColors)
    }

    val appTypography = remember(selectedUiFontFamily, textScale) {
        createAppTypography(selectedUiFontFamily, textScale)
    }

    val uiLang = LocalAppLanguage.current
    val layoutDirection = if (LocaleHelper.isRtl(uiLang)) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(
        LocalAppColors provides appColors,
        LocalThemeMode provides themeMode,
        LocalThemeStyle provides style,
        LocalArabicFontFamily provides selectedArabicFontFamily,
        LocalUiFontFamily provides selectedUiFontFamily,
        LocalLayoutDirection provides layoutDirection
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = appTypography,
            content = content
        )
    }
}
