package com.example.data.util

/**
 * Multi-script phonetic transliteration engine for all 12 supported languages:
 * bn, en, ar, ur, id, ms, hi, tr, de, fr, sq, bs.
 *
 * Converts vocalized Arabic (letters + harakat + shaddah + tanween + al- sun/moon assimilation)
 * into natural phonetic spelling matching the user's currently selected app/content language.
 */
object ArabicTransliterationHelper {

    private const val FATHA = '\u064E'
    private const val DAMMA = '\u064F'
    private const val KASRA = '\u0650'
    private const val FATHATAN = '\u064B'
    private const val DAMMATAN = '\u064C'
    private const val KASRATAN = '\u064D'
    private const val SHADDA = '\u0651'
    private const val SUKUN = '\u0652'
    private const val DAGGER_ALIF = '\u0670'
    private const val MADDA = '\u0653'
    private const val HAMZA_ABOVE = '\u0654'
    private const val HAMZA_BELOW = '\u0655'
    private const val ALIF_WASLA = '\u0671'

    private val DIACRITICS = setOf(
        FATHA, DAMMA, KASRA, FATHATAN, DAMMATAN, KASRATAN,
        SHADDA, SUKUN, DAGGER_ALIF, MADDA, HAMZA_ABOVE, HAMZA_BELOW
    )

    private val QURAN_STOP_MARKS = setOf(
        '\u06D6', '\u06D7', '\u06D8', '\u06D9', '\u06DA', '\u06DB', '\u06DC',
        '\u06DD', '\u06DE', '\u06DF', '\u06E0', '\u06E1', '\u06E2', '\u06E3',
        '\u06E4', '\u06E5', '\u06E6', '\u06E7', '\u06E8', '\u06E9', '\u06EA',
        '\u06EB', '\u06EC', '\u06ED', '۞', '۝'
    )

    private val SUN_LETTERS = setOf(
        'ت', 'ث', 'د', 'ذ', 'ر', 'ز', 'س', 'ش', 'ص', 'ض', 'ط', 'ظ', 'ل', 'ن'
    )

    private enum class Vowel {
        NONE, A, I, U, AA, II, UU, AY, AW, AN, IN, UN
    }

    private data class LatinOrthography(
        val b: String = "b",
        val t: String = "t",
        val th: String = "th",
        val j: String = "j",
        val h: String = "h",
        val kh: String = "kh",
        val d: String = "d",
        val dh: String = "dh",
        val r: String = "r",
        val z: String = "z",
        val s: String = "s",
        val sh: String = "sh",
        val sad: String = "s",
        val dad: String = "d",
        val tah: String = "t",
        val zah: String = "z",
        val ayn: String = "'",
        val gh: String = "gh",
        val f: String = "f",
        val q: String = "q",
        val k: String = "k",
        val l: String = "l",
        val m: String = "m",
        val n: String = "n",
        val w: String = "w",
        val y: String = "y",
        val glottal: String = "'",
        val uVowel: String = "u",
        val uuVowel: String = "uu"
    )

    private fun orthographyFor(code: String): LatinOrthography = when (code) {
        "id", "ms" -> LatinOrthography(
            th = "ts",
            dh = "dz",
            sh = "sy",
            ayn = "'",
            gh = "gh",
            q = "q",
            w = "w",
            y = "y"
        )
        "tr" -> LatinOrthography(
            th = "s",
            j = "c",
            kh = "h",
            dh = "z",
            sh = "ş",
            gh = "ğ",
            q = "k",
            w = "v",
            y = "y"
        )
        "de" -> LatinOrthography(
            th = "th",
            j = "dsch",
            kh = "ch",
            dh = "dh",
            sh = "sch",
            w = "w",
            y = "j"
        )
        "fr" -> LatinOrthography(
            th = "th",
            j = "dj",
            kh = "kh",
            dh = "dh",
            sh = "ch",
            w = "w",
            y = "y",
            uVowel = "ou",
            uuVowel = "ou"
        )
        "sq" -> LatinOrthography(
            th = "th",
            j = "xh",
            kh = "h",
            dh = "dh",
            sh = "sh",
            w = "v",
            y = "j"
        )
        "bs" -> LatinOrthography(
            th = "s",
            j = "dž",
            kh = "h",
            dh = "z",
            sh = "š",
            w = "v",
            y = "j"
        )
        else -> LatinOrthography() // English / default
    }

    /**
     * Produces phonetic transliteration in the target language code.
     * - For "bn": returns [bnCurated] if provided, else phonetic Bengali script generated from [arabic].
     * - For "en": returns [enCurated] if provided, else English phonetic generated from [arabic].
     * - For "hi": phonetic Devanagari generated from [arabic].
     * - For "id", "ms", "tr", "de", "fr", "sq", "bs": tailored phonetic orthography generated from [arabic].
     * - For "ar", "ur": clean vocalized Arabic reading text.
     */
    fun transliterate(
        arabic: String,
        langCode: String,
        bnCurated: String = "",
        enCurated: String = ""
    ): String {
        val normLang = SupportedLanguage.normalizeCode(langCode)
        val cleanArabic = sanitizeArabicForDisplay(arabic)
        if (normLang == "bn" && bnCurated.isNotBlank()) return bnCurated.trim()
        if (normLang == "en" && enCurated.isNotBlank()) return enCurated.trim()

        if (cleanArabic.isBlank()) {
            return when (normLang) {
                "bn" -> bnCurated.ifBlank { enCurated }
                else -> enCurated.ifBlank { bnCurated }
            }
        }

        return when (normLang) {
            "ar", "ur" -> cleanArabic
            "bn" -> convertArabicToBengali(cleanArabic)
            "hi" -> convertArabicToHindi(cleanArabic)
            else -> convertArabicToLatin(cleanArabic, orthographyFor(normLang))
        }.ifBlank { enCurated.ifBlank { bnCurated.ifBlank { cleanArabic } } }
    }

    private fun sanitizeArabicForDisplay(raw: String): String {
        val sb = StringBuilder(raw.length)
        for (ch in raw) {
            when {
                ch in QURAN_STOP_MARKS -> sb.append(' ')
                ch == 'ـ' -> Unit // tatweel
                else -> sb.append(ch)
            }
        }
        return sb.toString().replace(Regex("\\s+"), " ").trim()
    }

    private fun isConsonantLetter(ch: Char): Boolean {
        if (ch in DIACRITICS || ch in QURAN_STOP_MARKS) return false
        return ch in '\u0621'..'\u064A' || ch == ALIF_WASLA
    }

    private fun stripMarks(word: String): String =
        word.filter { it !in DIACRITICS && it !in QURAN_STOP_MARKS }

    private fun parseWordUnits(
        rawWord: String,
        prefixCheck: Boolean = true
    ): Pair<String, List<Triple<Char, Boolean, Vowel>>> {
        val normalizedWord = rawWord
            .replace(ALIF_WASLA, 'ا')
            .replace('ٱ', 'ا')
        val stripped = stripMarks(normalizedWord)

        // Special handling for the Divine Name (الله / لله)
        if (stripped == "الله" || stripped == "لله" || stripped == "والله" || stripped == "بالله" || stripped == "تالله") {
            return stripped to emptyList()
        }

        // Separate trailing punctuation
        var endIdx = normalizedWord.length
        while (endIdx > 0 && !isConsonantLetter(normalizedWord[endIdx - 1]) && normalizedWord[endIdx - 1] !in DIACRITICS) {
            endIdx--
        }
        val core = normalizedWord.substring(0, endIdx)

        // Detect definite article "ال" or attached prefix "و/ب/ل/ف/ك + ال"
        val units = mutableListOf<Triple<Char, Boolean, Vowel>>()
        var i = 0
        while (i < core.length) {
            val ch = core[i]
            if (!isConsonantLetter(ch)) {
                i++
                continue
            }

            // Read attached diacritics
            var hasFatha = false
            var hasKasra = false
            var hasDamma = false
            var hasFathatan = false
            var hasKasratan = false
            var hasDammatan = false
            var hasShadda = false
            var hasSukun = false
            var hasDaggerAlif = false
            var hasMadda = false

            var j = i + 1
            while (j < core.length && core[j] in DIACRITICS) {
                when (core[j]) {
                    FATHA -> hasFatha = true
                    KASRA -> hasKasra = true
                    DAMMA -> hasDamma = true
                    FATHATAN -> hasFathatan = true
                    KASRATAN -> hasKasratan = true
                    DAMMATAN -> hasDammatan = true
                    SHADDA -> hasShadda = true
                    SUKUN -> hasSukun = true
                    DAGGER_ALIF -> hasDaggerAlif = true
                    MADDA -> hasMadda = true
                }
                j++
            }

            val nextLetter = if (j < core.length && isConsonantLetter(core[j])) core[j] else null
            var nextHasDiacritic = false
            var nextHasSukun = false
            var nextHasShadda = false
            if (nextLetter != null) {
                var k = j + 1
                while (k < core.length && core[k] in DIACRITICS) {
                    nextHasDiacritic = true
                    if (core[k] == SUKUN) nextHasSukun = true
                    if (core[k] == SHADDA) nextHasShadda = true
                    k++
                }
            }

            var vowel = when {
                hasDaggerAlif || hasMadda -> Vowel.AA
                hasFathatan -> Vowel.AN
                hasKasratan -> Vowel.IN
                hasDammatan -> Vowel.UN
                hasFatha -> Vowel.A
                hasKasra -> Vowel.I
                hasDamma -> Vowel.U
                else -> Vowel.NONE
            }

            // Long vowel / diphthong absorption
            if (nextLetter == 'ا' && (vowel == Vowel.A || vowel == Vowel.NONE) && !nextHasDiacritic && !nextHasShadda) {
                if (ch != 'ا') {
                    vowel = Vowel.AA
                    j++
                    while (j < core.length && core[j] in DIACRITICS) j++
                }
            } else if (nextLetter == 'ى' && (vowel == Vowel.A || vowel == Vowel.NONE) && !nextHasDiacritic) {
                vowel = Vowel.AA
                j++
                while (j < core.length && core[j] in DIACRITICS) j++
            } else if (nextLetter == 'ي' && (nextHasSukun || !nextHasDiacritic) && !nextHasShadda) {
                if (vowel == Vowel.I) {
                    vowel = Vowel.II
                    j++
                    while (j < core.length && core[j] in DIACRITICS) j++
                } else if (vowel == Vowel.A && nextHasSukun) {
                    vowel = Vowel.AY
                    j++
                    while (j < core.length && core[j] in DIACRITICS) j++
                }
            } else if (nextLetter == 'و' && (nextHasSukun || !nextHasDiacritic) && !nextHasShadda) {
                if (vowel == Vowel.U) {
                    vowel = Vowel.UU
                    j++
                    while (j < core.length && core[j] in DIACRITICS) j++
                } else if (vowel == Vowel.A && nextHasSukun) {
                    vowel = Vowel.AW
                    j++
                    while (j < core.length && core[j] in DIACRITICS) j++
                }
            } else if (nextLetter == 'ا' && vowel == Vowel.AN) {
                // Trailing silent alif on fathatan (e.g. أَحَدًا)
                j++
                while (j < core.length && core[j] in DIACRITICS) j++
            }

            // Default inherent vowel if unvocalized mid-word alif / hamza
            if (ch == 'آ') vowel = Vowel.AA
            if (ch == 'إ' && vowel == Vowel.NONE) vowel = Vowel.I
            if (ch == 'أ' && vowel == Vowel.NONE && !hasSukun) vowel = Vowel.A
            if (ch == 'ا' && i == 0 && prefixCheck) {
                if (nextLetter == 'ل' && core.length > j + 1) {
                    // Definite article "ال"
                    val afterLamIdx = run {
                        var p = j + 1
                        while (p < core.length && core[p] in DIACRITICS) p++
                        p
                    }
                    val sun = if (afterLamIdx < core.length) core[afterLamIdx] else null
                    if (sun != null && sun in SUN_LETTERS) {
                        // Assimilate lam -> a<sun>-
                        units.add(Triple('ا', false, Vowel.A))
                        units.add(Triple(sun, false, Vowel.NONE))
                        i = afterLamIdx
                        continue
                    } else {
                        units.add(Triple('ا', false, Vowel.A))
                        units.add(Triple('ل', false, Vowel.NONE))
                        i = afterLamIdx
                        continue
                    }
                } else if (vowel == Vowel.NONE) {
                    vowel = Vowel.A
                }
            }

            units.add(Triple(ch, hasShadda, vowel))
            i = j
        }
        return "" to units
    }

    private fun convertArabicToLatin(text: String, ortho: LatinOrthography): String {
        val words = text.split(' ').filter { it.isNotBlank() }
        val resultWords = words.mapIndexed { index, rawWord ->
            val cleanLowerPunct = rawWord.filter { it in ",.;:!?،؛؟" }
            val punctOut = cleanLowerPunct
                .replace('،', ',')
                .replace('؛', ';')
                .replace('؟', '?')

            val (special, units) = parseWordUnits(rawWord)
            val wordStr = when (special) {
                "الله" -> "Allāh"
                "لله" -> "Lillāh"
                "والله" -> "Wallāh"
                "بالله" -> "Billāh"
                "تالله" -> "Tallāh"
                else -> buildString {
                    units.forEachIndexed { uIdx, (ch, shadda, vowel) ->
                        val cons = latinConsonant(ch, uIdx == 0, ortho)
                        if (cons.isNotEmpty()) {
                            append(cons)
                            if (shadda && cons.length <= 2) {
                                append(cons)
                            }
                        }
                        append(latinVowel(vowel, ortho))
                    }
                }
            }

            val finalWord = if (index == 0 && wordStr.isNotEmpty()) {
                wordStr.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            } else {
                wordStr
            }
            finalWord + punctOut
        }
        return resultWords.joinToString(" ").replace(Regex("\\s+([,.;:?])"), "$1").trim()
    }

    private fun latinConsonant(ch: Char, isWordStart: Boolean, o: LatinOrthography): String = when (ch) {
        'ا', 'أ', 'إ', 'آ', 'ى' -> if (isWordStart) "" else ""
        'ء', 'ؤ', 'ئ' -> if (isWordStart) "" else o.glottal
        'ب' -> o.b
        'ت' -> o.t
        'ث' -> o.th
        'ج' -> o.j
        'ح' -> o.h
        'خ' -> o.kh
        'د' -> o.d
        'ذ' -> o.dh
        'ر' -> o.r
        'ز' -> o.z
        'س' -> o.s
        'ش' -> o.sh
        'ص' -> o.sad
        'ض' -> o.dad
        'ط' -> o.tah
        'ظ' -> o.zah
        'ع' -> if (isWordStart) "" else o.ayn
        'غ' -> o.gh
        'ف' -> o.f
        'ق' -> o.q
        'ك' -> o.k
        'ل' -> o.l
        'م' -> o.m
        'ن' -> o.n
        'ه' -> o.h
        'ة' -> "h"
        'و' -> o.w
        'ي' -> o.y
        else -> ""
    }

    private fun latinVowel(v: Vowel, o: LatinOrthography): String = when (v) {
        Vowel.NONE -> ""
        Vowel.A -> "a"
        Vowel.I -> "i"
        Vowel.U -> o.uVowel
        Vowel.AA -> "aa"
        Vowel.II -> "ee"
        Vowel.UU -> o.uuVowel
        Vowel.AY -> "ay"
        Vowel.AW -> "aw"
        Vowel.AN -> "an"
        Vowel.IN -> "in"
        Vowel.UN -> "un"
    }

    private fun convertArabicToBengali(text: String): String {
        val words = text.split(' ').filter { it.isNotBlank() }
        return words.joinToString(" ") { rawWord ->
            val punctOut = rawWord.filter { it in ",.;:!?،؛؟" }
                .replace('،', ',')
                .replace('؛', ';')
                .replace('؟', '।')
            val (special, units) = parseWordUnits(rawWord)
            val wordStr = when (special) {
                "الله" -> "আল্লা-হ"
                "لله" -> "লিল্লা-হ"
                "والله" -> "ওয়াল্লা-হ"
                "بالله" -> "বিল্লা-হ"
                "تالله" -> "তাল্লা-হ"
                else -> buildString {
                    units.forEachIndexed { idx, (ch, shadda, vowel) ->
                        val isPureVowelCarrier = ch in setOf('ا', 'أ', 'إ', 'آ', 'ء', 'ؤ', 'ئ', 'ى', 'ع')
                        if (isPureVowelCarrier) {
                            if (ch == 'ع' && idx > 0) append("‘")
                            append(bengaliStandaloneVowel(vowel))
                        } else {
                            val cons = bengaliConsonant(ch)
                            append(cons)
                            if (shadda && cons.isNotEmpty()) {
                                append('্').append(cons)
                            }
                            append(bengaliMatra(vowel))
                        }
                    }
                }
            }
            wordStr + punctOut
        }.trim()
    }

    private fun bengaliConsonant(ch: Char): String = when (ch) {
        'ب' -> "ব"
        'ت', 'ط' -> "ত"
        'ث' -> "ছ"
        'ج' -> "জ"
        'ح', 'ه', 'ة' -> "হ"
        'خ' -> "খ"
        'د' -> "দ"
        'ذ', 'ز', 'ظ' -> "য"
        'ر' -> "র"
        'س', 'ص' -> "স"
        'ش' -> "শ"
        'ض' -> "দ"
        'غ' -> "গ"
        'ف' -> "ফ"
        'ق' -> "ক্ব"
        'ك' -> "ক"
        'ل' -> "ল"
        'م' -> "ম"
        'ن' -> "ন"
        'و' -> "ওয়"
        'ي' -> "ইয়"
        else -> ""
    }

    private fun bengaliStandaloneVowel(v: Vowel): String = when (v) {
        Vowel.NONE -> ""
        Vowel.A -> "আ"
        Vowel.I -> "ই"
        Vowel.U -> "উ"
        Vowel.AA -> "আ-"
        Vowel.II -> "ঈ"
        Vowel.UU -> "ঊ"
        Vowel.AY -> "আই"
        Vowel.AW -> "আও"
        Vowel.AN -> "আন"
        Vowel.IN -> "ইন"
        Vowel.UN -> "উন"
    }

    private fun bengaliMatra(v: Vowel): String = when (v) {
        Vowel.NONE -> ""
        Vowel.A -> ""
        Vowel.I -> "ি"
        Vowel.U -> "ু"
        Vowel.AA -> "া-"
        Vowel.II -> "ী"
        Vowel.UU -> "ূ"
        Vowel.AY -> "াই"
        Vowel.AW -> "াও"
        Vowel.AN -> "ান"
        Vowel.IN -> "িন"
        Vowel.UN -> "ুন"
    }

    private fun convertArabicToHindi(text: String): String {
        val words = text.split(' ').filter { it.isNotBlank() }
        return words.joinToString(" ") { rawWord ->
            val punctOut = rawWord.filter { it in ",.;:!?،؛؟" }
                .replace('،', ',')
                .replace('؛', ';')
                .replace('؟', '?')
            val (special, units) = parseWordUnits(rawWord)
            val wordStr = when (special) {
                "الله" -> "अल्लाह"
                "لله" -> "लिल्लाह"
                "والله" -> "वल्लाह"
                "بالله" -> "बिल्लाह"
                "تالله" -> "तल्लाह"
                else -> buildString {
                    units.forEachIndexed { idx, (ch, shadda, vowel) ->
                        val isPureVowelCarrier = ch in setOf('ا', 'أ', 'إ', 'آ', 'ء', 'ؤ', 'ئ', 'ى', 'ع')
                        if (isPureVowelCarrier) {
                            if (ch == 'ع' && idx > 0) append("'")
                            append(hindiStandaloneVowel(vowel))
                        } else {
                            val cons = hindiConsonant(ch)
                            append(cons)
                            if (shadda && cons.isNotEmpty()) {
                                append('्').append(cons)
                            }
                            if (vowel == Vowel.NONE && idx < units.lastIndex) {
                                append('्')
                            } else {
                                append(hindiMatra(vowel))
                            }
                        }
                    }
                }
            }
            wordStr + punctOut
        }.trim()
    }

    private fun hindiConsonant(ch: Char): String = when (ch) {
        'ب' -> "ब"
        'ت', 'ط' -> "त"
        'ث' -> "स"
        'ج' -> "ज"
        'ح', 'ه', 'ة' -> "ह"
        'خ' -> "ख़"
        'د' -> "द"
        'ذ', 'ز', 'ظ' -> "ज़"
        'ر' -> "र"
        'س', 'ص' -> "स"
        'ش' -> "श"
        'ض' -> "द"
        'غ' -> "ग़"
        'ف' -> "फ़"
        'ق' -> "क़"
        'ك' -> "क"
        'ل' -> "ल"
        'م' -> "म"
        'ن' -> "न"
        'و' -> "व"
        'ي' -> "य"
        else -> ""
    }

    private fun hindiStandaloneVowel(v: Vowel): String = when (v) {
        Vowel.NONE -> ""
        Vowel.A -> "अ"
        Vowel.I -> "इ"
        Vowel.U -> "उ"
        Vowel.AA -> "आ"
        Vowel.II -> "ई"
        Vowel.UU -> "ऊ"
        Vowel.AY -> "ऐ"
        Vowel.AW -> "औ"
        Vowel.AN -> "अन"
        Vowel.IN -> "इन"
        Vowel.UN -> "उन"
    }

    private fun hindiMatra(v: Vowel): String = when (v) {
        Vowel.NONE -> ""
        Vowel.A -> ""
        Vowel.I -> "ि"
        Vowel.U -> "ु"
        Vowel.AA -> "ा"
        Vowel.II -> "ी"
        Vowel.UU -> "ू"
        Vowel.AY -> "ै"
        Vowel.AW -> "ौ"
        Vowel.AN -> "न"
        Vowel.IN -> "िन"
        Vowel.UN -> "ुन"
    }
}
