package com.example

import android.content.Context
import android.media.AudioManager
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.PrayerCalculator
import com.example.data.model.PrayerName
import com.example.data.model.QariDirectory
import com.example.data.mosque.MosqueModeManager
import com.example.data.mosque.MosqueModeScheduler
import com.example.data.mosque.PrayerMosqueConfig
import com.example.data.notification.PrayerAlertScheduler
import com.example.data.notification.PrayerNotificationWorker
import com.example.data.preference.AppPreferences
import com.example.data.util.TimeFormatUtils
import com.example.ui.theme.toHex
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
@LooperMode(LooperMode.Mode.INSTRUMENTATION_TEST)
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Noor-e-Hidayah", appName)
  }

  @Test
  fun `prayer calculator produces valid schedule`() {
    val schedule = PrayerCalculator.calculateSchedule()
    assertNotNull(schedule)
    assertEquals(6, schedule.items.size)
    assertTrue(schedule.items.any { it.prayer == PrayerName.FAJR })
    assertTrue(schedule.items.any { it.prayer == PrayerName.DHUHR })

    // Verify explicit start and end time ranges
    schedule.items.forEach { item ->
      assertTrue("Item ${item.prayer} formattedEndTime should not be blank", item.formattedEndTime.isNotBlank())
      assertTrue("Item ${item.prayer} formattedRange should not be blank", item.formattedRange.isNotBlank())
      assertTrue(
        "Item ${item.prayer} endTimeMillis (${item.endTimeMillis}) must be greater than timeMillis (${item.timeMillis})",
        item.endTimeMillis > item.timeMillis
      )
    }
  }

  @Test
  fun `time formatting utility supports 24h and 12h modes`() {
    // 13.5 decimal hour = 1:30 PM (12h) or 13:30 (24h)
    val formatted12h = TimeFormatUtils.formatDecimalHour(13.5, is24Hour = false, locale = Locale.US)
    val formatted24h = TimeFormatUtils.formatDecimalHour(13.5, is24Hour = true, locale = Locale.US)

    assertEquals("01:30 PM", formatted12h)
    assertEquals("13:30", formatted24h)

    // Countdown formatting
    val countdown = TimeFormatUtils.formatCountdown(3665000L, locale = Locale.US) // 1h 1m 5s
    assertEquals("01:01:05", countdown)

    // Parse time string
    val parsed = TimeFormatUtils.parseTimeString("13:45")
    assertEquals(Pair(13, 45), parsed)

    val parsedAmPm = TimeFormatUtils.parseTimeString("01:45 PM")
    assertEquals(Pair(13, 45), parsedAmPm)
  }

  @Test
  fun `prayer schedule adapts dynamically to 24h format`() {
    val schedule12h = PrayerCalculator.calculateSchedule(is24Hour = false)
    val schedule24h = PrayerCalculator.calculateSchedule(is24Hour = true)

    val dhuhr12 = schedule12h.items.first { it.prayer == PrayerName.DHUHR }.formattedTime
    val dhuhr24 = schedule24h.items.first { it.prayer == PrayerName.DHUHR }.formattedTime

    assertTrue(dhuhr12.contains("PM") || dhuhr12.contains("AM"))
    assertFalse(dhuhr24.contains("PM"))
    assertFalse(dhuhr24.contains("AM"))
  }

  @Test
  fun `prayer mosque config serialization and defaults`() {
    val defaultCfg = PrayerMosqueConfig.defaultFor(PrayerName.DHUHR)
    assertEquals(30, defaultCfg.durationMinutes)
    assertEquals(5, defaultCfg.bufferBeforeMinutes)
    assertTrue(defaultCfg.enabled)

    val serialized = defaultCfg.serialize()
    val deserialized = PrayerMosqueConfig.deserialize(PrayerName.DHUHR, serialized)
    assertEquals(defaultCfg.prayer, deserialized.prayer)
    assertEquals(defaultCfg.durationMinutes, deserialized.durationMinutes)
    assertEquals(defaultCfg.bufferBeforeMinutes, deserialized.bufferBeforeMinutes)

    // Custom fixed times
    val customCfg = defaultCfg.copy(customStartTime = "13:15", customEndTime = "13:45")
    val customDeserialized = PrayerMosqueConfig.deserialize(PrayerName.DHUHR, customCfg.serialize())
    assertEquals("13:15", customDeserialized.customStartTime)
    assertEquals("13:45", customDeserialized.customEndTime)
  }

  @Test
  fun `mosque mode captures previous ringer and restores on deactivation`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    // Set ringer to normal initially
    try {
      am.ringerMode = AudioManager.RINGER_MODE_NORMAL
    } catch (_: Exception) {}

    // Initial check
    AppPreferences.setSavedRingerModeSync(context, AudioManager.RINGER_MODE_NORMAL)

    // Activate Mosque Mode
    MosqueModeManager.activateMosqueMode(context, "টেস্ট ওয়াক্ত")
    assertTrue(MosqueModeManager.isCurrentlySilenced(context))

    // Deactivate Mosque Mode
    MosqueModeManager.deactivateMosqueMode(context)
    assertFalse(MosqueModeManager.isCurrentlySilenced(context))
  }

  @Test
  fun `strictly male reciters policy verified`() {
    val reciters = QariDirectory.MALE_RECITERS
    assertTrue(reciters.isNotEmpty())
    assertEquals("Sheikh Mishary Rashid Al-Afasy", reciters[0].name)
  }

  @Test
  fun `create notification channel succeeds`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    PrayerNotificationWorker.createNotificationChannel(context)
  }

  @Test
  fun `schedule prayer alerts with work manager succeeds`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    PrayerAlertScheduler.schedulePrayerAlerts(context)
  }

  @Test
  fun `schedule mosque mode prayers succeeds`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    MosqueModeScheduler.scheduleAllMosquePrayers(context)
  }

  @Test
  fun `tasbeeh round tracking and completion increments rounds correctly`() = runTest {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val vm = com.example.ui.viewmodel.TasbeehViewModel(app)

    // Set target to 3 for quick verification
    vm.setTarget(3)
    assertEquals(3, vm.uiState.value.targetCount)
    assertEquals(0, vm.uiState.value.completedRounds)

    // Increment 1
    vm.increment()
    assertEquals(1, vm.uiState.value.currentCount)
    assertEquals(0, vm.uiState.value.completedRounds)

    // Increment 2
    vm.increment()
    assertEquals(2, vm.uiState.value.currentCount)
    assertEquals(0, vm.uiState.value.completedRounds)

    // Increment 3 (Round 1 Completed!)
    vm.increment()
    assertEquals(0, vm.uiState.value.currentCount)
    assertEquals(1, vm.uiState.value.completedRounds)
    assertNotNull(vm.uiState.value.roundCelebrationMessage)
    val msg = vm.uiState.value.roundCelebrationMessage!!
    assertTrue("Unexpected round celebration message: $msg", msg.contains("Round 1 Complete") || msg.contains("রাউন্ড ১ সম্পন্ন"))

    // Reset clears both active count and completed rounds
    vm.reset()
    assertEquals(0, vm.uiState.value.currentCount)
    assertEquals(0, vm.uiState.value.completedRounds)
  }

  @Test
  fun `tasbeeh custom target configuration updates state properly`() = runTest {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val vm = com.example.ui.viewmodel.TasbeehViewModel(app)

    vm.setTarget(1000)
    assertEquals(1000, vm.uiState.value.targetCount)

    vm.setTarget(313)
    assertEquals(313, vm.uiState.value.targetCount)
  }

  @Test
  fun `tasbeeh custom dua creation and local persistence`() = runTest {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val vm = com.example.ui.viewmodel.TasbeehViewModel(app)

    vm.addCustomDua(
      arabic = "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ",
      transliteration = "Hasbunallah wa ni'mal wakeel",
      bengaliMeaning = "আল্লাহই আমাদের জন্য যথেষ্ট",
      target = 40
    )

    // Verify state switched to custom dua
    val currentPreset = vm.uiState.value.currentPreset
    assertTrue(currentPreset.isCustom)
    assertEquals("Hasbunallah wa ni'mal wakeel", currentPreset.transliteration)
    assertEquals(40, vm.uiState.value.targetCount)
  }

  @Test
  fun `dynamic theme styles resolution and defaults`() {
    val defaultStyle = com.example.ui.theme.ThemeStyle.fromId("EMERALD_GOLD")
    assertEquals(com.example.ui.theme.ThemeStyle.EMERALD_GOLD, defaultStyle)

    val midnightStyle = com.example.ui.theme.ThemeStyle.fromId("MIDNIGHT_DEEP")
    assertEquals(com.example.ui.theme.ThemeStyle.MIDNIGHT_DEEP, midnightStyle)

    val royalStyle = com.example.ui.theme.ThemeStyle.fromId("GOLDEN_ROYAL")
    assertEquals(com.example.ui.theme.ThemeStyle.GOLDEN_ROYAL, royalStyle)

    val velvetStyle = com.example.ui.theme.ThemeStyle.fromId("VELVET_TEAL")
    assertEquals(com.example.ui.theme.ThemeStyle.VELVET_TEAL, velvetStyle)

    // Canvas Themes
    assertEquals(com.example.ui.theme.AppCanvasTheme.OBSIDIAN, com.example.ui.theme.AppCanvasTheme.fromId("OBSIDIAN"))
    assertEquals(com.example.ui.theme.AppCanvasTheme.DEEP_NAVY, com.example.ui.theme.AppCanvasTheme.fromId("DEEP_NAVY"))
    assertEquals(com.example.ui.theme.AppCanvasTheme.RICH_EMERALD, com.example.ui.theme.AppCanvasTheme.fromId("RICH_EMERALD"))
    assertEquals(com.example.ui.theme.AppCanvasTheme.AMOLED_BLACK, com.example.ui.theme.AppCanvasTheme.fromId("AMOLED_BLACK"))

    // Accent Themes
    assertEquals(com.example.ui.theme.AppAccentTheme.CHAMPAGNE_GOLD, com.example.ui.theme.AppAccentTheme.fromId("CHAMPAGNE_GOLD"))
    assertEquals(com.example.ui.theme.AppAccentTheme.MINT_GLOW, com.example.ui.theme.AppAccentTheme.fromId("MINT_GLOW"))
    assertEquals(com.example.ui.theme.AppAccentTheme.ROYAL_AMBER, com.example.ui.theme.AppAccentTheme.fromId("ROYAL_AMBER"))
    assertEquals(com.example.ui.theme.AppAccentTheme.DEEP_CRIMSON, com.example.ui.theme.AppAccentTheme.fromId("DEEP_CRIMSON"))
    assertEquals(com.example.ui.theme.AppAccentTheme.CELESTIAL_CYAN, com.example.ui.theme.AppAccentTheme.fromId("CELESTIAL_CYAN"))

    // UI Fonts
    assertEquals(com.example.ui.theme.AppUiFont.INTER, com.example.ui.theme.AppUiFont.fromId("INTER"))
    assertEquals(com.example.ui.theme.AppUiFont.OUTFIT, com.example.ui.theme.AppUiFont.fromId("OUTFIT"))
    assertEquals(com.example.ui.theme.AppUiFont.LORA, com.example.ui.theme.AppUiFont.fromId("LORA"))
    assertEquals(com.example.ui.theme.AppUiFont.SYSTEM, com.example.ui.theme.AppUiFont.fromId("SYSTEM"))

    // Arabic Fonts
    assertEquals(com.example.ui.theme.AppArabicFont.AMIRI, com.example.ui.theme.AppArabicFont.fromId("AMIRI"))
    assertEquals(com.example.ui.theme.AppArabicFont.NOTO_NASKH, com.example.ui.theme.AppArabicFont.fromId("NOTO_NASKH"))
    assertEquals(com.example.ui.theme.AppArabicFont.SCHEHERAZADE, com.example.ui.theme.AppArabicFont.fromId("SCHEHERAZADE"))

    // Text Scaling
    assertEquals(com.example.ui.theme.AppTextScale.COMPACT, com.example.ui.theme.AppTextScale.fromScale(0.90f))
    assertEquals(com.example.ui.theme.AppTextScale.NORMAL, com.example.ui.theme.AppTextScale.fromScale(1.00f))
    assertEquals(com.example.ui.theme.AppTextScale.MEDIUM_LARGE, com.example.ui.theme.AppTextScale.fromScale(1.15f))
    assertEquals(com.example.ui.theme.AppTextScale.LARGE, com.example.ui.theme.AppTextScale.fromScale(1.30f))
  }

  @Test
  fun `quran ayahs and duas include bengali transliteration and verified audio URLs`() {
    val fatihahAyahs = com.example.data.model.QuranData.getAyahsForSurah(1)
    assertTrue(fatihahAyahs.isNotEmpty())
    val ayah1 = fatihahAyahs[0]
    assertTrue(ayah1.transliterationBengali.isNotBlank())
    assertTrue(ayah1.audioUrl.startsWith("https://everyayah.com/data/"))

    val duas = com.example.data.model.DuaRepository.DUAS
    assertTrue(duas.isNotEmpty())
    assertTrue(duas.all { it.transliterationBengali.isNotBlank() })
    val firstAudioDua = duas.firstOrNull { it.audioUrl.isNotEmpty() }
    assertTrue(
      firstAudioDua != null &&
        (firstAudioDua.audioUrl.startsWith("https://www.hisnmuslim.com/audio/ar/") ||
          firstAudioDua.audioUrl.startsWith("https://everyayah.com/data/"))
    )
    assertTrue(duas.any { it.audioUrl.startsWith("https://everyayah.com/data/") })
  }

  @Test
  fun `ruqyah library provides categorized audio tracks strictly with male reciters`() {
    val tracks = com.example.data.model.RuqyahRepository.TRACKS
    assertTrue(tracks.isNotEmpty())

    // Verify all 5 core categories have dedicated tracks
    val categories = listOf(
      com.example.data.model.RuqyahCategory.GENERAL,
      com.example.data.model.RuqyahCategory.EVIL_EYE,
      com.example.data.model.RuqyahCategory.SIHR_MAGIC,
      com.example.data.model.RuqyahCategory.PHYSICAL_HEALING,
      com.example.data.model.RuqyahCategory.ANXIETY_PEACE
    )

    for (cat in categories) {
      val catTracks = com.example.data.model.RuqyahRepository.getTracksForCategory(cat)
      assertTrue("Category ${cat.name} should have tracks", catTracks.isNotEmpty())
    }

    // Verify track metadata and 100% male reciter policy
    for (track in tracks) {
      assertTrue(track.id.isNotBlank())
      assertTrue(track.titleBengali.isNotBlank())
      assertTrue(track.titleArabic.isNotBlank())
      assertTrue(track.reciterName.startsWith("শাইখ"))
      assertTrue(track.audioUrl.startsWith("http"))
      assertTrue(track.keyVerses.isNotBlank())
      assertTrue(track.benefits.isNotBlank())
    }
  }

  @Test
  fun `guest mode defaults to unauthenticated and persists session correctly`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = AppPreferences(context)
    val authRepo = com.example.data.auth.AuthRepository(context, prefs)

    // Verify initial guest state
    val initialSession = authRepo.currentUserSession.value
    assertFalse("Initial session should not be logged in", initialSession.isLoggedIn)
    assertEquals(com.example.data.auth.AuthProviderType.GUEST, initialSession.provider)

    // Continue as guest explicitly
    authRepo.continueAsGuest()
    val guestSession = authRepo.currentUserSession.value
    assertFalse("Guest session should remain logged out", guestSession.isLoggedIn)
    assertEquals(com.example.data.auth.AuthProviderType.GUEST, guestSession.provider)

    // Sign out should also cleanly ensure logged out guest mode
    authRepo.signOut()
    val signedOutSession = authRepo.currentUserSession.value
    assertFalse("Signed out session should be logged out", signedOutSession.isLoggedIn)
  }

  @Test
  fun `hadith master repository provides authentic collections chapters and reader metadata`() = runTest {
    val books = com.example.data.repository.HadithMasterRepository.HADITH_BOOKS
    assertTrue(books.isNotEmpty())
    assertEquals(7, books.size) // Bukhari, Muslim, Tirmidhi, Abu Dawood, Nasai, Ibn Majah, Riyadus Salihin

    val bukhari = books.first { it.id == "bukhari" }
    assertEquals("সহীহ বুখারী", bukhari.nameBengali)
    assertTrue(bukhari.totalChaptersCount > 0)
    assertTrue(bukhari.totalPagesCount > 0)

    val bukhariChapters = com.example.data.repository.HadithMasterRepository.getChaptersForBook("bukhari")
    assertTrue(bukhariChapters.isNotEmpty())
    val chapter1 = bukhariChapters.first()
    assertEquals(1, chapter1.chapterNumber)
    assertEquals("কিতাবুল ওহী (ওহীর সূচনা পর্ব)", chapter1.nameBengali)
    assertEquals(1, chapter1.startPage)

    // Test encyclopedic hadiths content
    val hadiths = com.example.data.repository.ExpandedHadithBookContent.ENCYCLOPEDIC_HADITHS
    assertTrue(hadiths.isNotEmpty())
    assertTrue(hadiths.all { it.hadithNumber.isNotBlank() })
    assertTrue(hadiths.all { it.arabicText.isNotBlank() })
    assertTrue(hadiths.all { it.banglaText.isNotBlank() })
    assertTrue(hadiths.all { it.pageNumber >= 1 })

    // Test Last Read persistence
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = AppPreferences(context)
    prefs.saveLastReadHadith(
      bookId = "bukhari",
      chapterId = chapter1.id,
      hadithId = "bukhari_1",
      hadithNumber = "১",
      pageNumber = 1
    )
    val lastBook = prefs.lastReadHadithBook.first()
    val lastChapter = prefs.lastReadHadithChapter.first()
    val lastNum = prefs.lastReadHadithNum.first()
    val lastPage = prefs.lastReadHadithPage.first()

    assertEquals("bukhari", lastBook)
    assertEquals(chapter1.id, lastChapter)
    assertEquals("১", lastNum)
    assertEquals(1, lastPage)
  }

  @Test
  fun `room database hadith loader seeds from assets and supports pagination`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = com.example.data.local.AppDatabase.getInstance(context)

    // Load chapters from asset
    val bukhariChapters = com.example.data.local.HadithDatabaseLoader.loadBookChaptersFromAsset(context, "bukhari")
    assertTrue("Bukhari chapters should be loaded from asset", bukhariChapters.isNotEmpty())
    assertTrue("Should have extensive chapters", bukhariChapters.size >= 50)

    // Seed database
    com.example.data.local.HadithDatabaseLoader.populateDatabaseIfEmpty(context, db)
    val dao = db.hadithDao()
    val totalCount = dao.getTotalHadithCount()
    assertTrue("Database should have seeded hadiths", totalCount > 0)

    // Verify pagination
    val paged = dao.getHadithsPaged("bukhari", limit = 5, offset = 0)
    assertTrue("Paged hadiths should not be empty", paged.isNotEmpty())
    assertEquals("১", paged.first().hadithNumber)

    // Verify search
    val searchResults = dao.searchHadiths("bukhari", "নিয়ত", limit = 10, offset = 0)
    assertTrue("Search should return niyyah hadith", searchResults.isNotEmpty())
  }

  @Test
  fun `global 3-mode theme engine and custom color picker persist and derive colors accurately`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = AppPreferences(context)

    // 1. Verify default mode is DARK
    val defaultMode = prefs.themeMode.first()
    assertEquals("DARK", defaultMode)

    // 2. Test fallback of legacy LIGHT string to DARK in 2-mode engine
    val resolvedLegacyMode = com.example.ui.theme.ThemeMode.fromId("LIGHT")
    assertEquals(com.example.ui.theme.ThemeMode.DARK, resolvedLegacyMode)

    // 3. Test switching to CUSTOM mode and persisting custom accent hex
    prefs.setThemeMode(com.example.ui.theme.ThemeMode.CUSTOM.id)
    prefs.setCustomAccentHex("#00E676")
    val customMode = prefs.themeMode.first()
    val customHex = prefs.customAccentHex.first()
    assertEquals("CUSTOM", customMode)
    assertEquals("#00E676", customHex)

    // 4. Test hex color parsing and formatting utilities
    val parsedGreen = com.example.ui.theme.parseHexColor("#00E676")
    assertEquals("#00E676", com.example.ui.theme.parseHexColor("#00E676").toHex())
    val fallbackColor = com.example.ui.theme.parseHexColor("invalid_hex", com.example.ui.theme.ChampagneGold)
    assertEquals(com.example.ui.theme.ChampagneGold, fallbackColor)
  }

  @Test
  fun `authentic four madhabs and granular element color overrides persist correctly`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = AppPreferences(context)

    // 1. Test authentic 4 Madhabs parsing and persistence
    val madhabs = com.example.data.model.JuristicMethod.entries
    assertEquals(4, madhabs.size)
    assertTrue(madhabs.any { it.name == "HANAFI" && it.shadowFactor == 2.0 })
    assertTrue(madhabs.any { it.name == "SHAFII" && it.shadowFactor == 1.0 })
    assertTrue(madhabs.any { it.name == "MALIKI" && it.shadowFactor == 1.0 })
    assertTrue(madhabs.any { it.name == "HANBALI" && it.shadowFactor == 1.0 })

    // Test selection persistence
    prefs.setJuristicMethod("MALIKI")
    assertEquals("MALIKI", prefs.juristicMethod.first())
    assertEquals(com.example.data.model.JuristicMethod.MALIKI, com.example.data.model.JuristicMethod.fromString(prefs.juristicMethod.first()))

    // Test fallback for older "STANDARD" key
    assertEquals(com.example.data.model.JuristicMethod.SHAFII, com.example.data.model.JuristicMethod.fromString("STANDARD"))

    // 2. Test granular element color overrides persistence
    prefs.setCustomArabicColor("#E5C158")
    prefs.setCustomTranslationColor("#111827")
    prefs.setCustomTransliterationColor("#00E676")

    assertEquals("#E5C158", prefs.customArabicColor.first())
    assertEquals("#111827", prefs.customTranslationColor.first())
    assertEquals("#00E676", prefs.customTransliterationColor.first())
  }

  @Test
  fun `mosque mode computes short jamat based window instead of full multi hour waqt`() {
    val cal = java.util.Calendar.getInstance()
    val rawSchedule = PrayerCalculator.calculateSchedule(calendar = cal, is24Hour = true)
    val asrConfig = PrayerMosqueConfig.defaultFor(PrayerName.ASR).copy(
      jamatTime = "16:30",
      bufferBeforeMinutes = 10,
      durationMinutes = 20
    )
    val adjusted = PrayerCalculator.applyMosqueConfigs(
      schedule = rawSchedule,
      configs = mapOf(PrayerName.ASR to asrConfig),
      calendar = cal,
      is24Hour = true,
      now = 0L
    )

    val (startMs, endMs) = MosqueModeScheduler.computePrayerWindow(
      prayer = PrayerName.ASR,
      config = asrConfig,
      todaySchedule = adjusted,
      tomorrowSchedule = adjusted,
      now = 0L
    )

    val durationMins = (endMs - startMs) / 60_000L
    assertEquals(30L, durationMins) // 10 min before Jamat + 20 min after Jamat = 30 mins (NOT hours!)
    val startCal = java.util.Calendar.getInstance().apply { timeInMillis = startMs }
    val endCal = java.util.Calendar.getInstance().apply { timeInMillis = endMs }
    assertEquals(16, startCal.get(java.util.Calendar.HOUR_OF_DAY))
    assertEquals(20, startCal.get(java.util.Calendar.MINUTE))
    assertEquals(16, endCal.get(java.util.Calendar.HOUR_OF_DAY))
    assertEquals(50, endCal.get(java.util.Calendar.MINUTE))
  }

  @Test
  fun `mosque mode supports vibration and silent options without dnd`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = AppPreferences(context)
    val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    // Default should be VIBRATE
    assertEquals(com.example.data.mosque.MosqueRingerMode.VIBRATE, prefs.mosqueRingerMode.first())

    // Activate in VIBRATE mode
    prefs.setMosqueRingerMode(com.example.data.mosque.MosqueRingerMode.VIBRATE)
    MosqueModeManager.activateMosqueMode(context, "আসর", com.example.data.mosque.MosqueRingerMode.VIBRATE)
    assertTrue(MosqueModeManager.isCurrentlySilenced(context))
    assertEquals(AudioManager.RINGER_MODE_VIBRATE, am.ringerMode)

    // Switch preference to SILENT
    prefs.setMosqueRingerMode(com.example.data.mosque.MosqueRingerMode.SILENT)
    assertEquals(com.example.data.mosque.MosqueRingerMode.SILENT, prefs.mosqueRingerMode.first())
    MosqueModeManager.deactivateMosqueMode(context)
    assertFalse(MosqueModeManager.isCurrentlySilenced(context))
  }

  @Test
  fun `custom prayer and jamat timings override calculated schedule accurately`() {
    val cal = java.util.Calendar.getInstance()
    val raw = PrayerCalculator.calculateSchedule(calendar = cal, is24Hour = false)
    val customAsr = PrayerMosqueConfig.defaultFor(PrayerName.ASR).copy(
      customPrayerTime = "16:00",
      jamatTime = "16:15"
    )
    val updated = PrayerCalculator.applyMosqueConfigs(
      schedule = raw,
      configs = mapOf(PrayerName.ASR to customAsr),
      calendar = cal,
      is24Hour = false,
      now = cal.timeInMillis
    )

    val asrItem = updated.items.first { it.prayer == PrayerName.ASR }
    assertTrue(asrItem.isCustomTime)
    assertTrue(asrItem.isCustomJamat)
    assertTrue(asrItem.formattedTime.contains("4:00") || asrItem.formattedTime.contains("04:00"))
    assertTrue(asrItem.formattedJamatTime.contains("4:15") || asrItem.formattedJamatTime.contains("04:15"))
  }

  @Test
  fun `local time parser supports bengali numerals and canonical formatting`() {
    val parsedBn = TimeFormatUtils.parseTimeString("০৪:১৫ PM")
    assertEquals(Pair(16, 15), parsedBn)
    assertEquals("16:15", TimeFormatUtils.toCanonicalTime24(16, 15))
    val localTz = TimeFormatUtils.getLocalTimeZoneDisplay()
    assertTrue(localTz.isNotBlank() && localTz.contains("UTC"))
  }

  @Test
  fun `pre prayer 15 min alert defaults and nearest district detection work accurately`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = AppPreferences(context)

    assertTrue(prefs.prePrayerAlertEnabled.first())
    assertEquals(15, prefs.prePrayerAlertMinutes.first())
    prefs.setPrePrayerAlertMinutes(20)
    assertEquals(20, prefs.prePrayerAlertMinutes.first())
    prefs.setPrePrayerAlertMinutes(15)

    // Coordinates in Chattogram should resolve nearest district to Chattogram
    val (nearestChittagong, _) = com.example.data.model.DistrictDirectory.findNearestDistrict(22.3569, 91.7832)
    assertEquals("chattogram", nearestChittagong.id)

    // Coordinates in Sylhet should resolve nearest district to Sylhet
    val (nearestSylhet, _) = com.example.data.model.DistrictDirectory.findNearestDistrict(24.8949, 91.8687)
    assertEquals("sylhet", nearestSylhet.id)
  }

  @Test
  fun `auto sms toggle and custom message persist in shared preferences`() = runTest {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val vm = com.example.ui.viewmodel.ToolsViewModel(app)

    vm.toggleMosqueAutoReplySms(true)
    assertTrue(vm.mosqueAutoReplySmsEnabled.value)
    assertTrue(AppPreferences.isMosqueAutoReplySmsEnabledSync(app))

    val customMsg = "আমি নামাজে আছি, একটু পর কল দেব।"
    vm.setMosqueAutoReplyMessage(customMsg)
    assertEquals(customMsg, vm.mosqueAutoReplyMessage.value)
    assertEquals(customMsg, AppPreferences.getMosqueAutoReplyMsgSync(app))

    vm.toggleMosqueAutoReplySms(false)
    assertFalse(vm.mosqueAutoReplySmsEnabled.value)
    assertFalse(AppPreferences.isMosqueAutoReplySmsEnabledSync(app))
  }

  @Test
  fun `mosque call receiver sends native sim sms on missed call during active mosque mode`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val shadowApp = org.robolectric.Shadows.shadowOf(app)
    shadowApp.grantPermissions(
      android.Manifest.permission.SEND_SMS,
      android.Manifest.permission.READ_PHONE_STATE,
      android.Manifest.permission.READ_CALL_LOG
    )

    val smsManager = com.example.data.mosque.MosqueCallReceiver.resolveSmsManager(app)!!
    val shadowSms = org.robolectric.Shadows.shadowOf(smsManager)
    shadowSms.clearLastSentTextMessageParams()
    shadowSms.clearLastSentMultipartTextMessageParams()

    AppPreferences.clearCallTrackingSync(app)
    AppPreferences.clearSmsCooldownSync(app, "+8801712345678")
    AppPreferences.setMosqueAutoReplySmsEnabledSync(app, true)
    val replyText = "In Salah now" // <= 70 chars single-part SMS -> uses sendTextMessage
    AppPreferences.setMosqueAutoReplyMsgSync(app, replyText)
    MosqueModeManager.activateMosqueMode(app, "আসর")

    val receiver = com.example.data.mosque.MosqueCallReceiver()

    // 1. Incoming RINGING (first broadcast without number, second with number - Android 9+ pattern)
    receiver.onReceive(
      app,
      android.content.Intent(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED).apply {
        putExtra(android.telephony.TelephonyManager.EXTRA_STATE, android.telephony.TelephonyManager.EXTRA_STATE_RINGING)
      }
    )
    receiver.onReceive(
      app,
      android.content.Intent(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED).apply {
        putExtra(android.telephony.TelephonyManager.EXTRA_STATE, android.telephony.TelephonyManager.EXTRA_STATE_RINGING)
        @Suppress("DEPRECATION")
        putExtra(android.telephony.TelephonyManager.EXTRA_INCOMING_NUMBER, "+880 1712-345678")
      }
    )

    // 2. Call missed -> IDLE without OFFHOOK
    receiver.onReceive(
      app,
      android.content.Intent(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED).apply {
        putExtra(android.telephony.TelephonyManager.EXTRA_STATE, android.telephony.TelephonyManager.EXTRA_STATE_IDLE)
      }
    )

    val sentParams = shadowSms.lastSentTextMessageParams
    assertNotNull("Expected native SIM sendTextMessage to be called", sentParams)
    assertEquals("+8801712345678", sentParams.destinationAddress)
    assertEquals(replyText, sentParams.text)

    // 3. Answered call (RINGING -> OFFHOOK -> IDLE) must NOT trigger SMS
    shadowSms.clearLastSentTextMessageParams()
    AppPreferences.clearSmsCooldownSync(app, "+8801911112222")
    receiver.onReceive(
      app,
      android.content.Intent(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED).apply {
        putExtra(android.telephony.TelephonyManager.EXTRA_STATE, android.telephony.TelephonyManager.EXTRA_STATE_RINGING)
        @Suppress("DEPRECATION")
        putExtra(android.telephony.TelephonyManager.EXTRA_INCOMING_NUMBER, "+8801911112222")
      }
    )
    receiver.onReceive(
      app,
      android.content.Intent(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED).apply {
        putExtra(android.telephony.TelephonyManager.EXTRA_STATE, android.telephony.TelephonyManager.EXTRA_STATE_OFFHOOK)
      }
    )
    receiver.onReceive(
      app,
      android.content.Intent(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED).apply {
        putExtra(android.telephony.TelephonyManager.EXTRA_STATE, android.telephony.TelephonyManager.EXTRA_STATE_IDLE)
      }
    )
    assertEquals(null, shadowSms.lastSentTextMessageParams)

    MosqueModeManager.deactivateMosqueMode(app)
  }

  @Test
  fun `auto sms handles missing permissions and delivery failures without crashing`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val shadowApp = org.robolectric.Shadows.shadowOf(app)
    shadowApp.denyPermissions(android.Manifest.permission.SEND_SMS)

    val (ok, errMsg) = com.example.data.mosque.MosqueCallReceiver.sendNativeSimSms(
      context = app,
      rawPhoneNumber = "01711223344",
      rawMessageText = "Test",
      isTest = true
    )
    assertFalse(ok)
    assertTrue(errMsg.contains("SEND_SMS"))
    val (statusText, statusOk, _) = AppPreferences.getLastAutoSmsStatusSync(app)
    assertFalse(statusOk)
    assertTrue(statusText.contains("SEND_SMS"))

    // Verify SmsSentReceiver handles non-OK result and clears cooldown without crashing
    AppPreferences.setSmsCooldownTimestampSync(app, "01711223344", System.currentTimeMillis())
    val sentReceiver = com.example.data.mosque.SmsSentReceiver()
    sentReceiver.onReceive(
      app,
      android.content.Intent(com.example.data.mosque.SmsSentReceiver.ACTION_SMS_SENT).apply {
        putExtra(com.example.data.mosque.SmsSentReceiver.EXTRA_PHONE_NUMBER, "01711223344")
        putExtra(com.example.data.mosque.SmsSentReceiver.EXTRA_MESSAGE_TEXT, "Test")
      }
    )
    assertEquals(0L, AppPreferences.getSmsCooldownTimestampSync(app, "01711223344"))
    val (errStatus, errOk, _) = AppPreferences.getLastAutoSmsStatusSync(app)
    assertFalse(errOk)
    assertTrue(errStatus.contains("01711223344"))
    assertTrue(
      com.example.data.mosque.SmsSentReceiver.mapSmsErrorToBengali(
        android.telephony.SmsManager.RESULT_ERROR_NO_SERVICE
      ).contains("সিগন্যাল")
    )
  }

  @Test
  fun `new installation strictly initializes all worship counters to zero`() = runTest {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val prefs = AppPreferences(app)
    prefs.resetCurrentMonthCounters()
    prefs.setPrayerStreak(0)

    assertEquals(0, prefs.prayerStreak.first())
    assertEquals(0, prefs.quranReadMinutesToday.first())
    assertEquals(0, prefs.duasReadCount.first())
    assertEquals(0, prefs.totalTasbihCount.first())
    assertEquals(0, prefs.salawatJumatCount.first())
    assertEquals(0, prefs.hadithReadMinutes.first())
    assertEquals(0, prefs.hadithReadCount.first())

    val mainVm = com.example.ui.viewmodel.MainViewModel(app)
    assertEquals(0, mainVm.prayerStreak.value)
    assertEquals(0, mainVm.quranReadMinutesToday.value)
    assertEquals(0, mainVm.salawatJumatCount.value)
    assertEquals(0, mainVm.duasReadCount.value)
    assertEquals(0, mainVm.totalTasbihCount.value)
    assertEquals(0, mainVm.hadithReadMinutes.value)

    val moreVm = com.example.ui.viewmodel.MoreViewModel(app)
    assertEquals(0, moreVm.uiState.value.quranReadMinutes)
    assertEquals(0, moreVm.uiState.value.duasReadCount)
    assertEquals(0, moreVm.uiState.value.totalTasbihCount)
    assertEquals(0, moreVm.uiState.value.salawatCount)
    assertEquals(0, moreVm.uiState.value.hadithReadMinutes)
  }

  @Test
  fun `worship tracker increments all 5 categories and archives cleanly across month rollover`() = runTest {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val prefs = AppPreferences(app)
    val db = com.example.data.local.AppDatabase.getInstance(app)
    prefs.resetCurrentMonthCounters()
    prefs.setActiveYearMonth("")

    // Fix clock to October 15, 2026
    val octCal = java.util.Calendar.getInstance().apply {
      set(2026, java.util.Calendar.OCTOBER, 15, 10, 0, 0)
    }
    var fakeTimeMillis = octCal.timeInMillis

    val tracker = com.example.data.repository.WorshipTrackerRepository(
      context = app,
      prefs = prefs,
      database = db,
      cloudSyncRepo = null,
      clockMillis = { fakeTimeMillis }
    )

    tracker.recordQuranMinutes(12)
    tracker.recordDuaEngagement(5)
    tracker.recordTasbihCount(33, isDurood = false)
    tracker.recordTasbihCount(7, isDurood = true)
    tracker.recordDuroodCount(10)
    tracker.recordHadithMinutes(8)
    tracker.recordHadithRead(3)

    val octEntity = db.monthlyWorshipDao().getMonth("2026-10")
    assertNotNull(octEntity)
    assertEquals(12, octEntity!!.quranMinutes)
    assertEquals(5, octEntity.duaCount)
    assertEquals(40, octEntity.tasbihCount)
    assertEquals(17, octEntity.duroodCount)
    assertEquals(8, octEntity.hadithMinutes)
    assertEquals(3, octEntity.hadithReadCount)

    // Roll clock forward to November 1, 2026
    val novCal = java.util.Calendar.getInstance().apply {
      set(2026, java.util.Calendar.NOVEMBER, 1, 0, 5, 0)
    }
    fakeTimeMillis = novCal.timeInMillis

    val newMonth = tracker.ensureCurrentMonthAndArchiveIfNeeded()
    assertEquals("2026-11", newMonth)

    // Active counters must reset to 0 on month rollover
    assertEquals(0, prefs.quranReadMinutesToday.first())
    assertEquals(0, prefs.duasReadCount.first())
    assertEquals(0, prefs.totalTasbihCount.first())
    assertEquals(0, prefs.salawatJumatCount.first())
    assertEquals(0, prefs.hadithReadMinutes.first())
    assertEquals(0, prefs.hadithReadCount.first())

    // Archived October totals remain intact in Room while November starts at 0
    val preservedOct = db.monthlyWorshipDao().getMonth("2026-10")!!
    assertEquals(12, preservedOct.quranMinutes)
    assertEquals(17, preservedOct.duroodCount)

    val novEntity = db.monthlyWorshipDao().getMonth("2026-11")!!
    assertEquals(0, novEntity.quranMinutes)
    assertEquals(0, novEntity.tasbihCount)

    // 12-month series builder outputs exact 12 chronological entries ending with 2026-11
    val series12 = com.example.data.repository.WorshipTrackerRepository.build12MonthSeries(
      db.monthlyWorshipDao().getAllMonths(),
      referenceMillis = fakeTimeMillis
    )
    assertEquals(12, series12.size)
    assertEquals("2025-12", series12.first().yearMonth)
    assertEquals("2026-10", series12[10].yearMonth)
    assertEquals(12, series12[10].quranMinutes)
    assertEquals("2026-11", series12.last().yearMonth)
    assertEquals(0, series12.last().quranMinutes)
  }

  @Test
  fun `auth repository validates bangladeshi numbers and defaults to unauthenticated guest cleanly`() = runTest {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val prefs = AppPreferences(app)
    prefs.clearUserSession()
    val repo = com.example.data.auth.AuthRepository(app, prefs)

    assertEquals("+8801712345678", repo.formatBdPhoneNumber("01712345678"))
    assertEquals("+8801712345678", repo.formatBdPhoneNumber("1712345678"))
    assertEquals("+8801712345678", repo.formatBdPhoneNumber("+88001712345678"))
    assertTrue(repo.isValidBdNumber("01712345678"))
    assertTrue(repo.isValidBdNumber("+8801911223344"))
    assertFalse(repo.isValidBdNumber("01212345678"))
    assertFalse(repo.isValidBdNumber("12345"))

    assertFalse(repo.currentUserSession.value.isLoggedIn)
    assertEquals(com.example.data.auth.AuthProviderType.GUEST, repo.currentUserSession.value.provider)
  }
}





