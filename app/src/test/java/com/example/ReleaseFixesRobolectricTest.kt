package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ai.GeminiIslamicAssistant
import com.example.data.audio.ArabicTtsManager
import com.example.data.audio.AudioPlayerManager
import com.example.data.update.InAppUpdateManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ReleaseFixesRobolectricTest {

    @Test
    fun `arabic tts strictly blocks all female voice identifiers and prioritizes male voices`() {
        val femaleIds = listOf(
            "ar-xa-x-arc-local",
            "ar-xa-x-ard-network",
            "ar-XA-Wavenet-A",
            "ar-XA-Standard-D",
            "ar-SA-Salma-Female"
        )
        femaleIds.forEach { voiceName ->
            assertTrue("Expected female voice to be blocked: $voiceName", ArabicTtsManager.isKnownFemaleVoiceName(voiceName))
        }

        val maleIds = listOf(
            "ar-xa-x-are-local",
            "ar-xa-x-arz-network",
            "ar-XA-Wavenet-B",
            "ar-XA-Standard-C",
            "ar-SA-HamedNeural"
        )
        maleIds.forEach { voiceName ->
            assertFalse("Male voice should not be flagged female: $voiceName", ArabicTtsManager.isKnownFemaleVoiceName(voiceName))
            assertTrue("Expected male voice to be recognized: $voiceName", ArabicTtsManager.isKnownMaleVoiceName(voiceName))
        }
    }

    @Test
    fun `audio player removes female google translate tts urls`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val manager = AudioPlayerManager.getInstance(context)
        val (primary, backups) = manager.buildArabicTtsUrls("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ")
        assertEquals("", primary)
        assertTrue(backups.isEmpty())
    }

    @Test
    fun `authentic fallback nasihah resolves non-blank localized advice without hanging`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val assistant = GeminiIslamicAssistant()

        val bnNasihah = assistant.buildAuthenticFallbackNasihah(context, "bn", "2026-11-18")
        assertFalse(bnNasihah.isAiGenerated)
        assertTrue(bnNasihah.title.isNotBlank())
        assertTrue(bnNasihah.arabicQuote.isNotBlank())
        assertTrue(bnNasihah.adviceText.isNotBlank())
        assertTrue(bnNasihah.reference.isNotBlank())

        val enNasihah = assistant.buildAuthenticFallbackNasihah(context, "en", "2026-11-18")
        assertFalse(enNasihah.isAiGenerated)
        assertTrue(enNasihah.title.isNotBlank())
        assertTrue(enNasihah.adviceText.isNotBlank())
    }

    @Test
    fun `github update version comparator detects newer versions accurately`() {
        assertTrue(
            InAppUpdateManager.isNewerVersion(
                remoteVersionName = "1.3",
                remoteVersionCode = 4,
                currentVersionName = "1.2",
                currentVersionCode = 3
            )
        )
        assertTrue(
            InAppUpdateManager.isNewerVersion(
                remoteVersionName = "1.2",
                remoteVersionCode = 0,
                currentVersionName = "1.1",
                currentVersionCode = 0
            )
        )
        assertFalse(
            InAppUpdateManager.isNewerVersion(
                remoteVersionName = "1.2",
                remoteVersionCode = 3,
                currentVersionName = "1.2",
                currentVersionCode = 3
            )
        )
        assertFalse(
            InAppUpdateManager.isNewerVersion(
                remoteVersionName = "Version-1",
                remoteVersionCode = 1,
                currentVersionName = "1.2",
                currentVersionCode = 3
            )
        )
    }
}
