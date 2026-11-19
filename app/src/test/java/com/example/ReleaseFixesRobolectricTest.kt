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

    @Test
    fun `app check ttl parser and error diagnostics preserve actionable details`() {
        assertEquals(3_600_000L, com.example.data.security.parseTtlMillis("3600s"))
        assertEquals(1_800_000L, com.example.data.security.parseTtlMillis("1800.5s"))
        assertEquals(60_000L, com.example.data.security.parseTtlMillis("10s"))

        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = com.example.data.auth.AuthRepository(
            context,
            com.example.data.preference.AppPreferences(context)
        )
        val attestationMsg = repo.buildAppCheckFriendlyMessage("App Check attestation HTTP 403: App attestation failed.")
        assertTrue(attestationMsg.contains("HTTP 403"))
        assertTrue(attestationMsg.contains("Debug Token"))

        val timeoutMsg = repo.buildAppCheckFriendlyMessage("reCAPTCHA(NETWORK_ERROR): timeout")
        assertTrue(timeoutMsg.contains("NETWORK_ERROR"))

        // Verify Firebase SDK exact JSON payload contract (omits limited_use when false)
        val standardPayload = com.example.data.security.buildExchangeRecaptchaPayload("tok_abc", limitedUse = false)
        assertTrue(standardPayload.contains("\"recaptchaEnterpriseToken\":\"tok_abc\""))
        assertFalse(standardPayload.contains("limitedUse"))
        assertFalse(standardPayload.contains("limited_use"))

        val limitedPayload = com.example.data.security.buildExchangeRecaptchaPayload("tok_abc", limitedUse = true)
        assertTrue(limitedPayload.contains("\"limited_use\":true"))

        // Verify persistent copyable device debug token
        val tok1 = com.example.data.security.getOrCreateDeviceDebugToken(context)
        val tok2 = com.example.data.security.getOrCreateDeviceDebugToken(context)
        assertEquals(tok1, tok2)
        assertTrue(tok1.length >= 32)
    }

    @Test
    fun `mosque mode never gets trapped in vibrate and auto-restores to normal mode on deactivate or expiry`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
        audioManager.ringerMode = android.media.AudioManager.RINGER_MODE_NORMAL
        com.example.data.preference.AppPreferences.setMosqueRingerModeSync(
            context,
            com.example.data.mosque.MosqueRingerMode.VIBRATE
        )
        com.example.data.mosque.MosqueModeManager.deactivateMosqueMode(context)

        val now = System.currentTimeMillis()
        val futureDeadline = now + 20 * 60_000L

        // 1. Primary AlarmManager silence triggers vibrate
        com.example.data.mosque.MosqueModeManager.activateMosqueMode(
            context = context,
            prayerName = "আসর",
            preferredMode = com.example.data.mosque.MosqueRingerMode.VIBRATE,
            restoreAtMillis = futureDeadline
        )
        assertEquals(android.media.AudioManager.RINGER_MODE_VIBRATE, audioManager.ringerMode)
        assertTrue(com.example.data.mosque.MosqueModeManager.isCurrentlySilenced(context))

        // 2. Duplicate WorkManager backup fires while already in vibrate — must NOT poison saved mode
        com.example.data.mosque.MosqueModeManager.activateMosqueMode(
            context = context,
            prayerName = "আসর",
            preferredMode = com.example.data.mosque.MosqueRingerMode.VIBRATE,
            restoreAtMillis = futureDeadline
        )
        assertEquals(
            android.media.AudioManager.RINGER_MODE_NORMAL,
            com.example.data.preference.AppPreferences.getSavedRingerModeSync(context)
        )

        // 3. Expired deadline watchdog / resume recovery MUST restore Normal mode
        val restored = com.example.data.mosque.MosqueModeManager.restoreIfExpired(
            context = context,
            now = futureDeadline + 1_000L
        )
        assertTrue(restored)
        assertFalse(com.example.data.mosque.MosqueModeManager.isCurrentlySilenced(context))
        assertEquals(android.media.AudioManager.RINGER_MODE_NORMAL, audioManager.ringerMode)

        // 4. Stale delayed silence trigger firing AFTER Jamat ended must NOT silence phone
        com.example.data.mosque.MosqueModeManager.activateMosqueMode(
            context = context,
            prayerName = "মাগরিব",
            preferredMode = com.example.data.mosque.MosqueRingerMode.VIBRATE,
            restoreAtMillis = now - 60_000L
        )
        assertFalse(com.example.data.mosque.MosqueModeManager.isCurrentlySilenced(context))
        assertEquals(android.media.AudioManager.RINGER_MODE_NORMAL, audioManager.ringerMode)
    }

    @Test
    fun `pre-prayer alert targets jamat time while waqt alert announces waqt transition separately`() {
        val waqtStartMs = 1_700_000_000_000L // arbitrary base epoch
        val jamatMs = waqtStartMs + 45 * 60_000L // Jama'at is 45 min after Waqt start
        val item = com.example.data.model.PrayerTimeItem(
            prayer = com.example.data.model.PrayerName.ASR,
            timeMillis = waqtStartMs,
            formattedTime = "4:00 PM",
            jamatTimeMillis = jamatMs,
            formattedJamatTime = "4:45 PM"
        )
        val cfg = com.example.data.mosque.PrayerMosqueConfig.defaultFor(com.example.data.model.PrayerName.ASR)

        // Pre-prayer alert 15 min prior MUST be 15 min before JAMA'AT (not before Waqt start)
        val (triggerAt, formattedJamat) = com.example.data.notification.PrayerAlertScheduler.resolvePrePrayerJamatTrigger(
            item = item,
            config = cfg,
            preMinutes = 15,
            is24Hour = false
        )
        assertEquals(jamatMs - 15 * 60_000L, triggerAt)
        assertEquals("4:45 PM", formattedJamat)

        // Pre-prayer copy mentions Jama'at, Wudu & cleanliness
        val preCopyBn = com.example.data.notification.PrayerNotificationWorker.buildNotificationCopy(
            prayerName = "ASR",
            prayerArabic = "العصر",
            prayerBengali = "আসর",
            formattedTime = "4:45 PM",
            isPreAlert = true,
            preMinutes = 15,
            uiLang = "bn"
        )
        assertTrue(preCopyBn.title.contains("জামাত"))
        assertTrue(preCopyBn.content.contains("ওযু"))
        assertTrue(preCopyBn.content.contains("পরিষ্কার-পরিচ্ছন্ন"))

        // Waqt alert copy strictly announces Waqt transition (Dhuhr ended -> Asr started)
        val waqtCopyBn = com.example.data.notification.PrayerNotificationWorker.buildNotificationCopy(
            prayerName = "ASR",
            prayerArabic = "العصر",
            prayerBengali = "আসর",
            formattedTime = "4:00 PM",
            isPreAlert = false,
            preMinutes = 15,
            uiLang = "bn"
        )
        assertTrue(waqtCopyBn.content.contains("যোহর"))
        assertTrue(waqtCopyBn.content.contains("শেষ"))
        assertTrue(waqtCopyBn.content.contains("আসর"))
        assertTrue(waqtCopyBn.content.contains("শুরু"))
        assertFalse(waqtCopyBn.content.contains("ওযু"))
    }
}
