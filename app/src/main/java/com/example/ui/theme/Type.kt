package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.R

// UI Font Families
val InterFontFamily = FontFamily(
    Font(R.font.inter, FontWeight.Normal),
    Font(R.font.inter, FontWeight.Medium),
    Font(R.font.inter, FontWeight.Light),
    Font(R.font.inter, FontWeight.SemiBold)
)

val OutfitFontFamily = FontFamily(
    Font(R.font.outfit, FontWeight.Normal),
    Font(R.font.outfit, FontWeight.Medium),
    Font(R.font.outfit, FontWeight.Light),
    Font(R.font.outfit, FontWeight.SemiBold)
)

val LoraFontFamily = FontFamily(
    Font(R.font.lora, FontWeight.Normal),
    Font(R.font.lora, FontWeight.Medium),
    Font(R.font.lora, FontWeight.SemiBold)
)

val SystemDefaultFontFamily = FontFamily.Default

// Arabic Script Font Families
val ClassicalAmiriFontFamily = FontFamily(
    Font(R.font.amiri, FontWeight.Normal)
)

val NotoNaskhFontFamily = FontFamily(
    Font(R.font.noto_naskh_arabic, FontWeight.Normal),
    Font(R.font.noto_naskh_arabic, FontWeight.Medium),
    Font(R.font.noto_naskh_arabic, FontWeight.Bold)
)

val ScheherazadeFontFamily = FontFamily(
    Font(R.font.scheherazade_new, FontWeight.Normal),
    Font(R.font.scheherazade_new, FontWeight.Bold)
)

/**
 * 1. UI Font Selection Options
 */
enum class AppUiFont(
    val id: String,
    val titleBengali: String,
    val titleEnglish: String,
    val description: String
) {
    INTER(
        id = "INTER",
        titleBengali = "ইন্টার (ক্লিন সান্স-সেরিফ)",
        titleEnglish = "Clean Sans (Inter)",
        description = "আধুনিক, পরিচ্ছন্ন ও সুস্পষ্ট ডিজিটাল ডিজাইন"
    ),
    OUTFIT(
        id = "OUTFIT",
        titleBengali = "আউটফিট (মডার্ন জিওমেট্রিক)",
        titleEnglish = "Modern Geometric (Outfit)",
        description = "প্রাণবন্ত জ্যামিতিক কার্ভ ও প্রিমিয়াম স্টাইল"
    ),
    LORA(
        id = "LORA",
        titleBengali = "লোরা (ক্লাসিক এলিগ্যান্ট সেরিফ)",
        titleEnglish = "Classic Clean Serif (Lora)",
        description = "মার্জিত ও ঐতিহ্যবাহী বইপাঠের অভিজ্ঞতা"
    ),
    SYSTEM(
        id = "SYSTEM",
        titleBengali = "সিস্টেম ডিফল্ট",
        titleEnglish = "System Default",
        description = "আপনার অ্যান্ড্রয়েড ডিভাইসের নিজস্ব সিস্টেম ফন্ট"
    );

    companion object {
        fun fromId(id: String): AppUiFont = entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: INTER
    }
}

/**
 * 2. Arabic Script Font Selection Options
 */
enum class AppArabicFont(
    val id: String,
    val titleBengali: String,
    val titleEnglish: String,
    val sampleWord: String,
    val description: String
) {
    AMIRI(
        id = "AMIRI",
        titleBengali = "আমিরি (ক্লাসিক্যাল উসমানিক)",
        titleEnglish = "Classical Uthmanic (Amiri)",
        sampleWord = "نَسْتَعِينُ",
        description = "মদিনা শরীফের প্রিন্টেড মুসহাফের বিশুদ্ধ ক্যালিগ্রাফি"
    ),
    NOTO_NASKH(
        id = "NOTO_NASKH",
        titleBengali = "নাসখ (মডার্ন হাফস লিপি)",
        titleEnglish = "Modern Uthmanic Hafs (Noto Naskh)",
        sampleWord = "نَسْتَعِينُ",
        description = "উচ্চ বৈসাদৃশ্যপূর্ণ ও পাঠোপযোগী ডিজিটাল নাসখ হরফ"
    ),
    SCHEHERAZADE(
        id = "SCHEHERAZADE",
        titleBengali = "শেহেরজাদে (ঐতিহ্যবাহী ইন্দো-পাক)",
        titleEnglish = "Traditional Indo-Pak (Scheherazade)",
        sampleWord = "نَسْتَعِينُ",
        description = "উপমহাদেশের ঐতিহ্যবাহী পরিচিত ও সুবিন্যস্ত হরফ"
    );

    companion object {
        fun fromId(id: String): AppArabicFont = entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: AMIRI
    }
}

/**
 * 3. Dynamic Text Scaling
 */
enum class AppTextScale(
    val id: String,
    val labelBengali: String,
    val labelEnglish: String,
    val scaleFactor: Float
) {
    COMPACT("COMPACT", "ছোট (০.৯x)", "Compact (0.9x)", 0.90f),
    NORMAL("NORMAL", "স্বাভাবিক (১.০x)", "Normal (1.0x)", 1.00f),
    MEDIUM_LARGE("MEDIUM_LARGE", "মাঝারি বড় (১.১৫x)", "Medium (1.15x)", 1.15f),
    LARGE("LARGE", "বড় ও স্পষ্ট (১.৩x)", "Large (1.30x)", 1.30f);

    companion object {
        fun fromScale(scale: Float): AppTextScale {
            return entries.minByOrNull { kotlin.math.abs(it.scaleFactor - scale) } ?: NORMAL
        }

        fun fromId(id: String): AppTextScale = entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: NORMAL
    }
}

val LocalUiFontFamily = staticCompositionLocalOf { InterFontFamily }
val LocalArabicFontFamily = staticCompositionLocalOf { ClassicalAmiriFontFamily }

/**
 * Dynamic Composable accessor for AmiriFontFamily.
 * Ensures all existing and new composables referencing `AmiriFontFamily`
 * reactively display the user's selected Arabic script font!
 */
val AmiriFontFamily: FontFamily
    @Composable
    get() = LocalArabicFontFamily.current

/**
 * Creates dynamic M3 Typography with selected UI font family and scaling factor.
 */
fun createAppTypography(uiFontFamily: FontFamily, scaleFactor: Float = 1.0f): Typography {
    fun scaledSp(baseSp: Float): TextUnit = (baseSp * scaleFactor).sp

    return Typography(
        displayLarge = TextStyle(
            fontFamily = uiFontFamily,
            fontWeight = FontWeight.Light,
            fontSize = scaledSp(28f),
            lineHeight = scaledSp(34f),
            letterSpacing = (-0.5).sp,
            color = TextPrimary
        ),
        displayMedium = TextStyle(
            fontFamily = uiFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = scaledSp(24f),
            lineHeight = scaledSp(30f),
            letterSpacing = (-0.25).sp,
            color = TextPrimary
        ),
        headlineMedium = TextStyle(
            fontFamily = uiFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = scaledSp(20f),
            lineHeight = scaledSp(26f),
            color = TextPrimary
        ),
        titleLarge = TextStyle(
            fontFamily = uiFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = scaledSp(18f),
            lineHeight = scaledSp(24f),
            color = TextPrimary
        ),
        titleMedium = TextStyle(
            fontFamily = uiFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = scaledSp(15f),
            lineHeight = scaledSp(20f),
            color = TextPrimary
        ),
        titleSmall = TextStyle(
            fontFamily = uiFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = scaledSp(13f),
            lineHeight = scaledSp(18f),
            color = TextPrimary
        ),
        bodyLarge = TextStyle(
            fontFamily = uiFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = scaledSp(15f),
            lineHeight = scaledSp(22f),
            color = TextPrimary
        ),
        bodyMedium = TextStyle(
            fontFamily = uiFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = scaledSp(13f),
            lineHeight = scaledSp(18f),
            color = SlateGrayMuted
        ),
        bodySmall = TextStyle(
            fontFamily = uiFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = scaledSp(11f),
            lineHeight = scaledSp(15f),
            color = SlateGrayMuted
        ),
        labelLarge = TextStyle(
            fontFamily = uiFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = scaledSp(13f),
            lineHeight = scaledSp(18f),
            letterSpacing = 0.2.sp,
            color = TextPrimary
        ),
        labelMedium = TextStyle(
            fontFamily = uiFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = scaledSp(11f),
            lineHeight = scaledSp(15f),
            letterSpacing = 0.3.sp,
            color = TextPrimary
        ),
        labelSmall = TextStyle(
            fontFamily = uiFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = scaledSp(10f),
            lineHeight = scaledSp(14f),
            letterSpacing = 0.5.sp,
            color = SlateGrayMuted
        )
    )
}

val AppTypography = createAppTypography(InterFontFamily, 1.0f)
