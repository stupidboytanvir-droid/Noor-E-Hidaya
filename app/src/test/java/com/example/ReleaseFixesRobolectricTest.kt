package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ai.GeminiIslamicAssistant
import com.example.data.audio.ArabicTtsManager
import com.example.data.audio.AudioPlayerManager
import com.example.data.update.InAppUpdateManager
import kotlinx.coroutines.flow.first
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

    @Test
    fun `worship chart categories exclude dua while keeping quran tasbih durood and hadith`() {
        val categories = com.example.data.repository.WorshipCategory.CHART_CATEGORIES
        assertEquals(4, categories.size)
        assertFalse(categories.contains(com.example.data.repository.WorshipCategory.DUA))
        assertTrue(categories.contains(com.example.data.repository.WorshipCategory.QURAN))
        assertTrue(categories.contains(com.example.data.repository.WorshipCategory.TASBIH))
        assertTrue(categories.contains(com.example.data.repository.WorshipCategory.DUROOD))
        assertTrue(categories.contains(com.example.data.repository.WorshipCategory.HADITH))
    }

    @Test
    fun `tilawat studio presets and dua catalog expose verified male reciter https audio urls`() {
        val samples = com.example.ui.screens.HARAKAT_SAMPLES
        assertEquals(8, samples.size)
        samples.forEach { sample ->
            val urls = sample.allAudioUrls
            assertTrue("Sample ${sample.titleEnglish} must have at least one male MP3 URL", urls.isNotEmpty())
            urls.forEach { url ->
                assertTrue("Sample URL must be HTTPS: $url", url.startsWith("https://"))
                assertTrue(
                    "Sample URL must be from verified male reciter domain: $url",
                    url.contains("everyayah.com/data/Alafasy_128kbps/") ||
                        url.contains("hisnmuslim.com/audio/ar/")
                )
                assertFalse("Female translate TTS must never be used", url.contains("translate.google"))
            }
        }

        // Surah Al-Ikhlas preset must include all 4 ayahs
        val ikhlas = samples.first { it.titleEnglish.contains("Ikhlas") }
        assertEquals(4, ikhlas.allAudioUrls.size)

        // Verified male Dua audio catalog
        assertTrue(
            "Expected at least 78 verified male Dua audio URLs",
            com.example.data.model.DuaRepository.VERIFIED_MALE_DUA_AUDIO_URLS.size >= 78
        )
        val duasWithAudio = com.example.data.model.DuaRepository.DUAS.filter { !it.audioUrl.isNullOrBlank() }
        assertTrue("Expected at least 78 Duas enriched with male audio URLs", duasWithAudio.size >= 78)
        duasWithAudio.forEach { dua ->
            val url = dua.audioUrl!!
            assertTrue("Dua ${dua.id} URL must be HTTPS: $url", url.startsWith("https://"))
            assertTrue(
                "Dua ${dua.id} URL must use verified male reciter host: $url",
                url.contains("hisnmuslim.com/audio/ar/") || url.contains("everyayah.com/data/Alafasy_128kbps/")
            )
        }
    }

    @Test
    fun `account switching and sign out strictly isolate and wipe local user worship data`() = kotlinx.coroutines.runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = com.example.data.preference.AppPreferences(context)
        val db = androidx.room.Room.inMemoryDatabaseBuilder(
            context,
            com.example.data.local.AppDatabase::class.java
        ).allowMainThreadQueries().build()

        try {
            val syncRepo = com.example.data.cloud.CloudSyncRepository(context, prefs, db)

            // 1. Simulate Guest activity before signing into a brand-new Gmail/Phone account
            prefs.setActiveDataOwnerUid(com.example.data.preference.AppPreferences.GUEST_DATA_OWNER)
            prefs.incrementQuranReadMinutes(25)
            prefs.addTasbihCount(99)
            prefs.incrementHadithReadMinutes(12)
            prefs.toggleFavoriteDua("morning_1")
            db.monthlyWorshipDao().upsert(
                com.example.data.local.MonthlyWorshipEntity(
                    yearMonth = "2026-11",
                    quranMinutes = 25,
                    tasbihCount = 99,
                    duroodCount = 50,
                    hadithMinutes = 12
                )
            )
            db.tasbihDao().insertOrUpdate(
                com.example.data.local.TasbihEntity(
                    id = "subhanallah",
                    arabicPhrase = "سُبْحَانَ اللَّهِ",
                    transliteration = "SubhanAllah",
                    bengaliMeaning = "সুবহানাল্লাহ",
                    targetCount = 33,
                    currentCount = 21,
                    completedRounds = 3
                )
            )
            db.amalDao().saveAmal(
                com.example.data.local.AmalEntity(
                    dateString = "2026-11-19",
                    fajr = true,
                    dhuhr = true
                )
            )
            db.bookmarkDao().addBookmark(
                com.example.data.local.BookmarkEntity(
                    key = "2_255",
                    surahNumber = 2,
                    surahNameArabic = "البقرة",
                    surahNameEnglish = "Al-Baqarah",
                    ayahNumber = 255,
                    ayahTextArabic = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ"
                )
            )

            // 2. Switch from GUEST -> Brand-New Account A: MUST wipe all local guest data to 0/empty
            val switchedToA = syncRepo.prepareLocalStorageForAccount("uid_new_account_A")
            assertTrue("Switching GUEST -> uid_A must report account switch", switchedToA)
            assertEquals(0, prefs.quranReadMinutesToday.first())
            assertEquals(0, prefs.totalTasbihCount.first())
            assertEquals(0, prefs.hadithReadMinutes.first())
            assertTrue(prefs.favoriteDuaIds.first().isEmpty())
            assertTrue(db.monthlyWorshipDao().getAllMonths().isEmpty())
            assertTrue(db.tasbihDao().getAllCounters().first().isEmpty())
            assertTrue(db.amalDao().getAllRecentAmals().isEmpty())
            assertTrue(db.bookmarkDao().getAllBookmarks().first().isEmpty())
            assertEquals("uid_new_account_A", prefs.activeDataOwnerUid.first())

            // 3. Account A logs worship progress; relaunching SAME account A must preserve its data
            prefs.incrementQuranReadMinutes(15)
            prefs.addTasbihCount(33)
            val relaunchedSameA = syncRepo.prepareLocalStorageForAccount("uid_new_account_A")
            assertFalse("Same UID relaunch must NOT wipe Account A's data", relaunchedSameA)
            assertEquals(15, prefs.quranReadMinutesToday.first())
            assertEquals(33, prefs.totalTasbihCount.first())

            // 4. Switch from Account A -> Brand-New Account B: MUST wipe Account A's data cleanly
            val switchedToB = syncRepo.prepareLocalStorageForAccount("uid_new_account_B")
            assertTrue("Switching uid_A -> uid_B must wipe previous user's data", switchedToB)
            assertEquals(0, prefs.quranReadMinutesToday.first())
            assertEquals(0, prefs.totalTasbihCount.first())
            assertEquals("uid_new_account_B", prefs.activeDataOwnerUid.first())

            // 5. Account B logs data, then Signs Out: MUST wipe local data and revert owner to GUEST
            prefs.incrementQuranReadMinutes(40)
            syncRepo.resetLocalDataOnSignOut()
            assertEquals(0, prefs.quranReadMinutesToday.first())
            assertEquals(
                com.example.data.preference.AppPreferences.GUEST_DATA_OWNER,
                prefs.activeDataOwnerUid.first()
            )
        } finally {
            db.close()
        }
    }
}

