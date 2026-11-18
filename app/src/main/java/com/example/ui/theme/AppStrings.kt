package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.example.data.model.Ayah
import com.example.data.model.DailyHadith
import com.example.data.model.Dua
import com.example.data.model.PrayerName
import com.example.data.model.Surah
import com.example.data.util.SupportedLanguage
import com.example.ui.viewmodel.AppTab

val LocalAppLanguage = compositionLocalOf { "bn" }
val LocalContentLanguage = compositionLocalOf { "bn" }

@Composable
fun isEnglishUi(): Boolean = SupportedLanguage.normalizeCode(LocalAppLanguage.current) != "bn"

@Composable
fun isEnglishContent(): Boolean = SupportedLanguage.normalizeCode(LocalContentLanguage.current) != "bn"

@Composable
fun isBanglaUi(): Boolean = SupportedLanguage.normalizeCode(LocalAppLanguage.current) == "bn"

@Composable
fun isBanglaContent(): Boolean = SupportedLanguage.normalizeCode(LocalContentLanguage.current) == "bn"

@Composable
fun uiText(bn: String, en: String): String =
    LocalizationRegistry.translate(LocalAppLanguage.current, bn, en)

@Composable
fun contentText(bn: String, en: String): String =
    LocalizationRegistry.translate(LocalContentLanguage.current, bn, en)

fun localizedText(lang: String, bn: String, en: String): String =
    LocalizationRegistry.translate(lang, bn, en)

fun formatLocalizedInt(value: Int, lang: String): String =
    SupportedLanguage.formatInt(value, lang)

fun formatLocalizedDigits(raw: String, lang: String): String =
    SupportedLanguage.formatDigits(raw, lang)

fun AppTab.localizedTitle(lang: String): String =
    LocalizationRegistry.translate(lang, titleBengali, titleEnglish)

fun PrayerName.localizedName(lang: String): String =
    SupportedLanguage.localizedPrayerName(name, lang)

fun Surah.localizedName(contentLang: String): String =
    if (SupportedLanguage.normalizeCode(contentLang) == "bn") nameBengali
    else nameEnglish

fun Surah.localizedTranslation(contentLang: String): String =
    if (SupportedLanguage.normalizeCode(contentLang) == "bn") translationBengali
    else translationEnglish

fun Surah.localizedRevelation(uiLang: String): String {
    val isMeccan = revelationType.equals("Meccan", ignoreCase = true)
    return LocalizationRegistry.translate(
        langCode = uiLang,
        bn = if (isMeccan) "মাক্কী" else "মাদানী",
        en = if (isMeccan) "Makki" else "Madani"
    )
}

fun Ayah.localizedTranslation(contentLang: String): String =
    if (SupportedLanguage.normalizeCode(contentLang) == "bn") {
        translationBengali.ifBlank { translationEnglish }
    } else {
        translationEnglish.ifBlank { translationBengali }
    }

fun Ayah.localizedTransliteration(contentLang: String): String =
    com.example.data.util.ArabicTransliterationHelper.transliterate(
        arabic = textArabic,
        langCode = contentLang,
        bnCurated = transliterationBengali,
        enCurated = transliteration
    )

fun Dua.localizedTitle(contentLang: String): String =
    if (SupportedLanguage.normalizeCode(contentLang) == "bn") {
        titleBengali.ifBlank { titleEnglish }
    } else {
        titleEnglish.ifBlank { titleBengali }
    }

fun Dua.localizedMeaning(contentLang: String): String =
    if (SupportedLanguage.normalizeCode(contentLang) == "bn") {
        meaningBengali.ifBlank { meaningEnglish }
    } else {
        meaningEnglish.ifBlank { meaningBengali }
    }

fun Dua.localizedTransliteration(contentLang: String): String =
    com.example.data.util.ArabicTransliterationHelper.transliterate(
        arabic = textArabic,
        langCode = contentLang,
        bnCurated = transliterationBengali,
        enCurated = transliteration
    )

fun Dua.localizedCategory(lang: String): String {
    val bn = categoryBengali.ifBlank { category }
    val en = category.ifBlank { categoryBengali }
    return LocalizationRegistry.translate(lang, bn, en)
}

fun DailyHadith.localizedTranslation(contentLang: String): String =
    if (SupportedLanguage.normalizeCode(contentLang) == "bn") {
        translationBengali.ifBlank { translationEnglish }
    } else {
        translationEnglish.ifBlank { translationBengali }
    }

fun DailyHadith.localizedTransliteration(contentLang: String): String =
    com.example.data.util.ArabicTransliterationHelper.transliterate(
        arabic = textArabic,
        langCode = contentLang
    )

fun DailyHadith.localizedTitle(lang: String): String =
    if (SupportedLanguage.normalizeCode(lang) == "bn") {
        title.ifBlank { titleEnglish }
    } else {
        LocalizationRegistry.translate(lang, title, titleEnglish.ifBlank { title })
    }

fun DailyHadith.localizedNarrator(lang: String): String =
    if (SupportedLanguage.normalizeCode(lang) == "bn") {
        narrator.ifBlank { narratorEnglish }
    } else {
        narratorEnglish.ifBlank { narrator }
    }

fun DailyHadith.localizedBookReference(lang: String): String =
    if (SupportedLanguage.normalizeCode(lang) == "bn") {
        bookReference.ifBlank { bookReferenceEnglish }
    } else {
        bookReferenceEnglish.ifBlank { bookReference }
    }

fun DailyHadith.localizedExplanation(lang: String): String =
    if (SupportedLanguage.normalizeCode(lang) == "bn") {
        explanationBengali.ifBlank { explanationEnglish }
    } else {
        explanationEnglish.ifBlank { explanationBengali }
    }

fun com.example.data.model.Hadith.localizedTransliteration(contentLang: String): String =
    com.example.data.util.ArabicTransliterationHelper.transliterate(
        arabic = arabicText,
        langCode = contentLang
    )

fun com.example.data.model.AllahName.localizedMeaning(contentLang: String): String =
    if (SupportedLanguage.normalizeCode(contentLang) == "bn") {
        meaningBengali.ifBlank { meaningEnglish }
    } else {
        meaningEnglish.ifBlank { meaningBengali }
    }

fun com.example.data.model.AllahName.localizedTransliteration(contentLang: String): String =
    com.example.data.util.ArabicTransliterationHelper.transliterate(
        arabic = arabic,
        langCode = contentLang,
        enCurated = transliteration
    )

fun com.example.data.model.RuqyahTrack.localizedArabicTransliteration(contentLang: String): String {
    val arabicSource = arabicVersePreview.ifBlank { titleArabic }
    return com.example.data.util.ArabicTransliterationHelper.transliterate(
        arabic = arabicSource,
        langCode = contentLang
    )
}

fun com.example.data.model.RuqyahCategory.localizedTitle(lang: String): String =
    LocalizationRegistry.translate(
        langCode = lang,
        bn = titleBengali.ifBlank { titleEnglish },
        en = titleEnglish.ifBlank { titleBengali }
    )
