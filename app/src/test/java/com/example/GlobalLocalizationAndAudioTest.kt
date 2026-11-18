package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.audio.OfflineAudioManager
import com.example.data.util.SupportedLanguage
import com.example.ui.theme.LocalizationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GlobalLocalizationAndAudioTest {

    private val bengaliScriptRegex = Regex("[\\u0980-\\u09FF]")
    private val testQariId = "test_alafasy_unit"

    @After
    fun cleanupTestFiles() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val offlineQariDir = File(context.filesDir, "quran_offline/$testQariId")
        if (offlineQariDir.exists()) {
            offlineQariDir.deleteRecursively()
        }
    }

    @Test
    fun `supported language enum contains 12 languages with accurate RTL and normalization`() {
        val codes = SupportedLanguage.entries.map { it.code }.toSet()
        assertEquals(
            setOf("bn", "en", "ar", "ur", "id", "ms", "hi", "tr", "de", "fr", "sq", "bs"),
            codes
        )

        // Normalization rules
        assertEquals("bn", SupportedLanguage.normalizeCode(null))
        assertEquals("bn", SupportedLanguage.normalizeCode(""))
        assertEquals("en", SupportedLanguage.normalizeCode("en-US"))
        assertEquals("ar", SupportedLanguage.normalizeCode("ar_SA"))
        assertEquals("id", SupportedLanguage.normalizeCode("in"))
        assertEquals("tr", SupportedLanguage.normalizeCode("TR-tr"))
        assertEquals("en", SupportedLanguage.normalizeCode("xx-unknown"))

        // RTL checks
        assertTrue(SupportedLanguage.fromCode("ar").isRtl)
        assertTrue(SupportedLanguage.fromCode("ur").isRtl)
        assertFalse(SupportedLanguage.fromCode("bn").isRtl)
        assertFalse(SupportedLanguage.fromCode("en").isRtl)
        assertFalse(SupportedLanguage.fromCode("tr").isRtl)
        assertFalse(SupportedLanguage.fromCode("de").isRtl)
    }

    @Test
    fun `digit formatting prayer names and hijri months localize cleanly without bengali leakage`() {
        assertEquals("১২৩", SupportedLanguage.formatInt(123, "bn"))
        assertEquals("١٢٣", SupportedLanguage.formatInt(123, "ar"))
        assertEquals("123", SupportedLanguage.formatInt(123, "en"))
        assertEquals("123", SupportedLanguage.formatInt(123, "tr"))

        val prayers = listOf("FAJR", "SUNRISE", "DHUHR", "JUMUAH", "ASR", "MAGHRIB", "ISHA")
        for (lang in SupportedLanguage.entries) {
            for (prayer in prayers) {
                val localized = SupportedLanguage.localizedPrayerName(prayer, lang.code)
                assertTrue("Prayer $prayer blank for ${lang.code}", localized.isNotBlank())
                if (lang != SupportedLanguage.BENGALI) {
                    assertFalse(
                        "Bengali script leaked into $prayer for ${lang.code}: $localized",
                        bengaliScriptRegex.containsMatchIn(localized)
                    )
                }
            }

            val months = (1..12).map { SupportedLanguage.localizedHijriMonth(it, lang.code) }
            assertEquals(12, months.toSet().size)
            if (lang != SupportedLanguage.BENGALI) {
                months.forEach { monthName ->
                    assertFalse(
                        "Bengali script leaked into Hijri month for ${lang.code}: $monthName",
                        bengaliScriptRegex.containsMatchIn(monthName)
                    )
                }
            }
        }
    }

    @Test
    fun `localization registry translates core navigation and pattern strings for all 12 languages`() {
        val corePairs = listOf(
            "হোম" to "Home",
            "কুরআন" to "Quran",
            "দোয়া" to "Du'a",
            "টুলস" to "Tools",
            "আরো" to "More",
            "সেভ করুন" to "Save",
            "পৃষ্ঠা 5" to "Page 5",
            "10টি অধ্যায়" to "10 Chapters"
        )

        for (lang in SupportedLanguage.entries) {
            for ((bn, en) in corePairs) {
                val out = LocalizationRegistry.translate(lang.code, bn, en)
                assertTrue("Blank translation for '$en' in ${lang.code}", out.isNotBlank())
                when (lang) {
                    SupportedLanguage.BENGALI -> assertEquals(bn, out)
                    SupportedLanguage.ENGLISH -> assertEquals(en, out)
                    else -> {
                        assertFalse(
                            "Bengali script leaked for '$en' in ${lang.code}: $out",
                            bengaliScriptRegex.containsMatchIn(out)
                        )
                    }
                }
            }
        }

        // Verify distinct localized translations for key non-English languages
        assertEquals("الرئيسية", LocalizationRegistry.translate("ar", "হোম", "Home"))
        assertEquals("Kur'an", LocalizationRegistry.translate("tr", "কুরআন", "Quran"))
        assertEquals("Speichern", LocalizationRegistry.translate("de", "সেভ করুন", "Save"))
        assertEquals("Sayfa 5", LocalizationRegistry.translate("tr", "পৃষ্ঠা 5", "Page 5"))
    }

    @Test
    fun `offline audio manager resolves local file URIs fallback remote URLs and byte formatting`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val remote = "https://everyayah.com/data/Alafasy_128kbps/001001.mp3"

        val expectedFile = OfflineAudioManager.getLocalAyahFile(context, testQariId, 1, 1)
        assertTrue(expectedFile.path.endsWith("quran_offline/$testQariId/001001.mp3"))

        // Before download -> falls back to HTTPS remote URL
        assertEquals(
            remote,
            OfflineAudioManager.getPlayableUrl(context, testQariId, 1, 1, remote)
        )
        assertFalse(OfflineAudioManager.isSurahDownloaded(context, testQariId, 1, 7))

        // Write valid dummy MP3 bytes (> 1024 bytes) for Surah Al-Fatihah (7 ayahs)
        for (ayah in 1..7) {
            val file = OfflineAudioManager.getLocalAyahFile(context, testQariId, 1, ayah)
            file.parentFile?.mkdirs()
            file.writeBytes(ByteArray(2048) { 0x1A })
        }

        val resolved = OfflineAudioManager.getPlayableUrl(context, testQariId, 1, 1, remote)
        assertTrue("Expected file:// URI when offline MP3 exists, got $resolved", resolved.startsWith("file://"))
        assertTrue(OfflineAudioManager.isSurahDownloaded(context, testQariId, 1, 7))

        // Delete downloaded surah
        assertTrue(OfflineAudioManager.deleteSurah(context, testQariId, 1))
        assertFalse(OfflineAudioManager.isSurahDownloaded(context, testQariId, 1, 7))

        // Format bytes utility
        assertEquals("0 MB", OfflineAudioManager.formatBytes(0L))
        assertEquals("512 B", OfflineAudioManager.formatBytes(512L))
        assertEquals("2 KB", OfflineAudioManager.formatBytes(2048L))
        assertEquals("1.5 MB", OfflineAudioManager.formatBytes((1.5 * 1024 * 1024).toLong()))
    }

    @Test
    fun `arabic transliteration helper produces script matched phonetic pronunciation across all 12 languages`() {
        val sampleArabic = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
        for (lang in SupportedLanguage.entries) {
            val out = com.example.data.util.ArabicTransliterationHelper.transliterate(
                arabic = sampleArabic,
                langCode = lang.code
            )
            assertTrue("Transliteration blank for ${lang.code}", out.isNotBlank())
        }
        val bnOut = com.example.data.util.ArabicTransliterationHelper.transliterate(sampleArabic, "bn")
        val hiOut = com.example.data.util.ArabicTransliterationHelper.transliterate(sampleArabic, "hi")
        val enOut = com.example.data.util.ArabicTransliterationHelper.transliterate(sampleArabic, "en")
        assertTrue("Expected Bengali script for bn transliteration, got $bnOut", bengaliScriptRegex.containsMatchIn(bnOut))
        assertTrue("Expected Devanagari script for hi transliteration, got $hiOut", Regex("[\\u0900-\\u097F]").containsMatchIn(hiOut))
        assertFalse("English transliteration should not leak Bengali script", bengaliScriptRegex.containsMatchIn(enOut))
    }

    @Test
    fun `hadith repository and ruqyah tracks provide instant cdn streams and arabic previews`() {
        assertTrue(com.example.data.model.HadithRepository.HADITHS.size >= 10)
        val today = com.example.data.model.HadithRepository.getTodayHadith()
        assertTrue(today.textArabic.isNotBlank())
        assertTrue(today.translationEnglish.isNotBlank())

        val ruqyahTracks = com.example.data.model.RuqyahRepository.TRACKS
        assertTrue(ruqyahTracks.isNotEmpty())
        ruqyahTracks.forEach { track ->
            assertFalse("Slow archive.org URL should be replaced on ${track.id}", track.audioUrl.contains("archive.org"))
            assertTrue("Ruqyah track ${track.id} missing arabicVersePreview", track.arabicVersePreview.isNotBlank())
            assertTrue("Ruqyah track ${track.id} missing playlistUrls", track.playlistUrls.isNotEmpty())
        }
    }
}
