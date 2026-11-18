package com.example.data.model

data class HadithBook(
    val id: String,
    val nameBengali: String,
    val nameArabic: String,
    val compiler: String,
    val totalHadithCount: String,
    val totalChaptersCount: Int,
    val totalPagesCount: Int,
    val descriptionBengali: String,
    val colorHex: String,
    val verifiedSahih: Boolean = true,
    val nameEnglish: String = "",
    val compilerEnglish: String = "",
    val totalHadithCountEnglish: String = "",
    val descriptionEnglish: String = ""
) {
    fun localizedName(lang: String): String =
        com.example.ui.theme.LocalizationRegistry.translate(lang, nameBengali, nameEnglish.ifBlank { nameBengali })

    fun localizedCompiler(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") compiler
        else compilerEnglish.ifBlank { compiler }

    fun localizedTotalHadithCount(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") totalHadithCount
        else totalHadithCountEnglish.ifBlank { totalHadithCount }

    fun localizedDescription(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") descriptionBengali
        else descriptionEnglish.ifBlank { descriptionBengali }
}

private fun toAsciiDigits(input: String): String {
    val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    val sb = StringBuilder(input.length)
    for (ch in input) {
        val idx = bnDigits.indexOf(ch)
        if (idx >= 0) sb.append(('0'.code + idx).toChar()) else sb.append(ch)
    }
    return sb.toString()
}

data class HadithChapter(
    val id: String,
    val bookId: String,
    val chapterNumber: Int,
    val nameBengali: String,
    val nameArabic: String,
    val hadithRange: String,
    val hadithCount: Int,
    val startPage: Int = 1,
    val endPage: Int = 1,
    val nameEnglish: String = ""
) {
    fun localizedName(lang: String): String =
        com.example.ui.theme.LocalizationRegistry.translate(lang, nameBengali, nameEnglish.ifBlank { nameBengali })

    fun localizedHadithRange(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") hadithRange
        else com.example.data.util.SupportedLanguage.formatDigits(toAsciiDigits(hadithRange), lang)
}

data class Hadith(
    val id: String,
    val bookId: String,
    val bookNameBengali: String,
    val chapterId: String,
    val chapterNameBengali: String,
    val chapterNumber: Int = 1,
    val hadithNumber: String,
    val pageNumber: Int = 1,
    val narrator: String,
    val arabicText: String,
    val banglaText: String,
    val explanation: String = "",
    val grade: String = "সহীহ",
    val bookNameEnglish: String = "",
    val chapterNameEnglish: String = "",
    val narratorEnglish: String = "",
    val englishText: String = "",
    val explanationEnglish: String = "",
    val gradeEnglish: String = ""
) {
    fun localizedBookName(lang: String): String {
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") return bookNameBengali
        if (bookNameEnglish.isNotBlank()) return com.example.ui.theme.LocalizationRegistry.translate(lang, bookNameBengali, bookNameEnglish)
        val enFallback = when (bookId) {
            "bukhari" -> "Sahih al-Bukhari"
            "muslim" -> "Sahih Muslim"
            "tirmidhi" -> "Jami' at-Tirmidhi"
            "abudawood" -> "Sunan Abi Dawud"
            "nasai" -> "Sunan an-Nasa'i"
            "ibnmajah" -> "Sunan Ibn Majah"
            "riyadus_salihin" -> "Riyad as-Salihin"
            else -> bookNameBengali
        }
        return com.example.ui.theme.LocalizationRegistry.translate(lang, bookNameBengali, enFallback)
    }

    fun localizedChapterName(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") chapterNameBengali
        else chapterNameEnglish.ifBlank { chapterNameBengali }

    fun localizedHadithNumber(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") hadithNumber
        else com.example.data.util.SupportedLanguage.formatDigits(toAsciiDigits(hadithNumber), lang)

    fun localizedNarrator(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") narrator
        else narratorEnglish.ifBlank { narrator }

    fun localizedTranslation(contentLang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(contentLang) == "bn") {
            banglaText.ifBlank { englishText }
        } else {
            englishText.ifBlank { banglaText }
        }

    fun localizedExplanation(contentLang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(contentLang) == "bn") {
            explanation.ifBlank { explanationEnglish }
        } else {
            explanationEnglish.ifBlank { explanation }
        }

    fun localizedGrade(lang: String): String {
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") return grade
        if (gradeEnglish.isNotBlank()) return com.example.ui.theme.LocalizationRegistry.translate(lang, grade, gradeEnglish)
        val enGrade = when {
            grade.contains("মুত্তাফাক") -> "Muttafaqun Alayh (Bukhari & Muslim)"
            grade.contains("হাসান সহীহ") -> "Hasan Sahih"
            grade.contains("সহীহ বুখারী") -> "Sahih al-Bukhari"
            grade.contains("তিরমিযী") -> "Sahih (Tirmidhi)"
            grade.contains("হাসান") -> "Hasan"
            grade.contains("সহীহ") -> "Sahih"
            else -> grade
        }
        return com.example.ui.theme.LocalizationRegistry.translate(lang, grade, enGrade)
    }
}

data class LastReadHadith(
    val bookId: String,
    val chapterId: String,
    val hadithId: String,
    val hadithNumber: String,
    val pageNumber: Int,
    val timestamp: Long = System.currentTimeMillis()
)
