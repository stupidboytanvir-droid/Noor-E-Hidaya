package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.ChatMessage
import com.example.data.ai.GeminiIslamicAssistant
import com.example.data.audio.AudioPlayerManager
import com.example.data.model.PrayerName
import com.example.data.model.Qari
import com.example.data.model.QariDirectory
import com.example.data.notification.PrayerAlertScheduler
import com.example.data.preference.AppPreferences
import com.example.ui.theme.AppAccentTheme
import com.example.ui.theme.AppArabicFont
import com.example.ui.theme.AppCanvasTheme
import com.example.ui.theme.AppUiFont
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class MoreUiState(
    val uiLanguage: String = "bn",
    val contentLanguage: String = "bn",
    val selectedQariId: String = "alafasy",
    val calculationMethod: String = "MUSLIM_WORLD_LEAGUE",
    val juristicMethod: String = "STANDARD",
    val selectedDistrictId: String = "dhaka",
    val selectedDistrictName: String = "ঢাকা",
    val locationDisplayName: String = "ঢাকা, বাংলাদেশ",
    val isGpsLocation: Boolean = false,
    val isDetectingLocation: Boolean = false,
    val sahurCautionMins: Int = 3,
    val iftarCautionMins: Int = 3,
    val hijriOffset: Int = 0,
    val dailyAyahNotifEnabled: Boolean = true,
    val dailyHadithNotifEnabled: Boolean = true,
    val quranReadMinutes: Int = 0,
    val duasReadCount: Int = 0,
    val totalTasbihCount: Int = 0,
    val salawatCount: Int = 0,
    val hadithReadMinutes: Int = 0,
    val hadithReadCount: Int = 0,
    val monthlyHistory: List<com.example.data.local.MonthlyWorshipEntity> = emptyList(),
    val prayerAlertsEnabled: Boolean = true,
    val prePrayerAlertEnabled: Boolean = true,
    val prePrayerAlertMinutes: Int = 15,
    val customArabicText: String = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
    val ttsSpeed: Float = 1.0f,
    val isTtsPlaying: Boolean = false,
    val chatMessages: List<ChatMessage> = listOf(
        ChatMessage(
            id = "welcome",
            sender = "assistant",
            text = "আসসালামু আলাইকুম ওয়া রাহমাতুল্লাহ। নূর-এ-হেদায়াহ ইসলামিক এআই সহকারীতে স্বাগতম। আপনি কুরআন, হাদিস, নামাজের বিধান বা ইসলামিক জীবনবিধান সম্পর্কিত যেকোনো প্রশ্ন করতে পারেন।"
        )
    ),
    val isAiThinking: Boolean = false,
    val aiError: String? = null,
    val suggestedChips: List<String> = listOf(
        "তাহাজ্জুদের ফজিলত কী?",
        "যাকাত কার ওপর ফরজ?",
        "জুমু'আর দিনের বিশেষ আমল",
        "বিপদ মুক্তির শ্রেষ্ঠ দু'আ"
    ),
    val themeStyle: String = "EMERALD_GOLD",
    val themeMode: String = com.example.ui.theme.ThemeMode.DARK.id,
    val customAccentHex: String = "#E5C158",
    val customArabicColor: String = "",
    val customTranslationColor: String = "",
    val customTransliterationColor: String = "",
    val canvasTheme: String = AppCanvasTheme.OBSIDIAN.id,
    val accentTheme: String = AppAccentTheme.CHAMPAGNE_GOLD.id,
    val uiFont: String = AppUiFont.INTER.id,
    val arabicFont: String = AppArabicFont.AMIRI.id,
    val textScale: Float = 1.0f
)

class MoreViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = AppPreferences(application)
    private val db = com.example.data.local.AppDatabase.getInstance(application)
    private val worshipTracker = com.example.data.repository.WorshipTrackerRepository(application, prefs, db)
    private val geminiAssistant = GeminiIslamicAssistant()
    private val locationHelper = com.example.data.location.LocationHelper(application)
    val audioPlayer = AudioPlayerManager.getInstance(application)

    private val initialUiLang = com.example.data.util.LocaleHelper.getUiLanguageSync(application)

    private val _uiState = MutableStateFlow(
        MoreUiState(
            uiLanguage = initialUiLang,
            contentLanguage = com.example.data.util.LocaleHelper.getContentLanguageSync(application),
            chatMessages = listOf(defaultWelcomeMessage(initialUiLang)),
            suggestedChips = defaultSuggestedChips(initialUiLang)
        )
    )
    val uiState: StateFlow<MoreUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            worshipTracker.ensureCurrentMonthAndArchiveIfNeeded()
        }
        viewModelScope.launch {
            launch {
                prefs.uiLanguage.collect { lang ->
                    val current = _uiState.value
                    val updatedMsgs = if (current.chatMessages.size == 1 && current.chatMessages[0].id == "welcome") {
                        listOf(defaultWelcomeMessage(lang))
                    } else {
                        current.chatMessages
                    }
                    val resolvedNames = resolveDistrictDisplay(current.selectedDistrictId, current.isGpsLocation, current.locationDisplayName, lang)
                    _uiState.value = current.copy(
                        uiLanguage = lang,
                        selectedDistrictName = resolvedNames.first,
                        locationDisplayName = resolvedNames.second,
                        chatMessages = updatedMsgs,
                        suggestedChips = defaultSuggestedChips(lang)
                    )
                }
            }
            launch { prefs.contentLanguage.collect { _uiState.value = _uiState.value.copy(contentLanguage = it) } }
            launch { prefs.selectedQariId.collect { _uiState.value = _uiState.value.copy(selectedQariId = it) } }
            launch { prefs.calculationMethod.collect { _uiState.value = _uiState.value.copy(calculationMethod = it) } }
            launch { prefs.juristicMethod.collect { _uiState.value = _uiState.value.copy(juristicMethod = it) } }
            launch {
                prefs.selectedDistrictId.collect { id ->
                    val cur = _uiState.value
                    val resolved = resolveDistrictDisplay(id, cur.isGpsLocation, cur.locationDisplayName, cur.uiLanguage)
                    _uiState.value = cur.copy(
                        selectedDistrictId = id,
                        selectedDistrictName = resolved.first,
                        locationDisplayName = resolved.second
                    )
                }
            }
            launch {
                prefs.selectedDistrictName.collect { name ->
                    val cur = _uiState.value
                    val dist = com.example.data.model.DistrictDirectory.BANGLADESH_DISTRICTS.find { it.id == cur.selectedDistrictId }
                    val isBn = com.example.data.util.SupportedLanguage.normalizeCode(cur.uiLanguage) == "bn"
                    val localized = if (!isBn && dist != null) dist.nameEnglish else name
                    _uiState.value = cur.copy(selectedDistrictName = localized)
                }
            }
            launch {
                prefs.locationDisplayName.collect { display ->
                    val cur = _uiState.value
                    val resolved = resolveDistrictDisplay(cur.selectedDistrictId, cur.isGpsLocation, display, cur.uiLanguage)
                    _uiState.value = cur.copy(locationDisplayName = resolved.second)
                }
            }
            launch { prefs.isGpsLocation.collect { _uiState.value = _uiState.value.copy(isGpsLocation = it) } }
            launch { prefs.sahurCautionMins.collect { _uiState.value = _uiState.value.copy(sahurCautionMins = it) } }
            launch { prefs.iftarCautionMins.collect { _uiState.value = _uiState.value.copy(iftarCautionMins = it) } }
            launch { prefs.hijriOffset.collect { _uiState.value = _uiState.value.copy(hijriOffset = it) } }
            launch { prefs.dailyAyahNotifEnabled.collect { _uiState.value = _uiState.value.copy(dailyAyahNotifEnabled = it) } }
            launch { prefs.dailyHadithNotifEnabled.collect { _uiState.value = _uiState.value.copy(dailyHadithNotifEnabled = it) } }
            launch { prefs.prayerAlertsEnabled.collect { _uiState.value = _uiState.value.copy(prayerAlertsEnabled = it) } }
            launch { prefs.prePrayerAlertEnabled.collect { _uiState.value = _uiState.value.copy(prePrayerAlertEnabled = it) } }
            launch { prefs.prePrayerAlertMinutes.collect { _uiState.value = _uiState.value.copy(prePrayerAlertMinutes = it) } }
            launch { prefs.themeMode.collect { _uiState.value = _uiState.value.copy(themeMode = it) } }
            launch { prefs.customAccentHex.collect { _uiState.value = _uiState.value.copy(customAccentHex = it) } }
            launch { prefs.customArabicColor.collect { _uiState.value = _uiState.value.copy(customArabicColor = it) } }
            launch { prefs.customTranslationColor.collect { _uiState.value = _uiState.value.copy(customTranslationColor = it) } }
            launch { prefs.customTransliterationColor.collect { _uiState.value = _uiState.value.copy(customTransliterationColor = it) } }
            launch { prefs.canvasTheme.collect { _uiState.value = _uiState.value.copy(canvasTheme = it) } }
            launch { prefs.accentTheme.collect { _uiState.value = _uiState.value.copy(accentTheme = it) } }
            launch { prefs.uiFont.collect { _uiState.value = _uiState.value.copy(uiFont = it) } }
            launch { prefs.arabicFont.collect { _uiState.value = _uiState.value.copy(arabicFont = it) } }
            launch { prefs.textScale.collect { _uiState.value = _uiState.value.copy(textScale = it) } }
            launch { prefs.quranReadMinutesToday.collect { _uiState.value = _uiState.value.copy(quranReadMinutes = it) } }
            launch { prefs.duasReadCount.collect { _uiState.value = _uiState.value.copy(duasReadCount = it) } }
            launch { prefs.totalTasbihCount.collect { _uiState.value = _uiState.value.copy(totalTasbihCount = it) } }
            launch { prefs.salawatJumatCount.collect { _uiState.value = _uiState.value.copy(salawatCount = it) } }
            launch { prefs.hadithReadMinutes.collect { _uiState.value = _uiState.value.copy(hadithReadMinutes = it) } }
            launch { prefs.hadithReadCount.collect { _uiState.value = _uiState.value.copy(hadithReadCount = it) } }
            launch { worshipTracker.observeLast12Months().collect { _uiState.value = _uiState.value.copy(monthlyHistory = it) } }
        }

        viewModelScope.launch {
            val alertsEnabled = prefs.prayerAlertsEnabled.first()
            val preEnabled = prefs.prePrayerAlertEnabled.first()
            if (alertsEnabled || preEnabled) {
                PrayerAlertScheduler.schedulePrayerAlerts(getApplication())
            }
        }
    }

    fun setThemeMode(mode: String) {
        _uiState.value = _uiState.value.copy(themeMode = mode)
        viewModelScope.launch { prefs.setThemeMode(mode) }
    }

    fun setCustomAccentHex(hex: String) {
        _uiState.value = _uiState.value.copy(customAccentHex = hex)
        viewModelScope.launch { prefs.setCustomAccentHex(hex) }
    }

    fun setCustomArabicColor(hex: String) {
        _uiState.value = _uiState.value.copy(customArabicColor = hex)
        viewModelScope.launch { prefs.setCustomArabicColor(hex) }
    }

    fun setCustomTranslationColor(hex: String) {
        _uiState.value = _uiState.value.copy(customTranslationColor = hex)
        viewModelScope.launch { prefs.setCustomTranslationColor(hex) }
    }

    fun setCustomTransliterationColor(hex: String) {
        _uiState.value = _uiState.value.copy(customTransliterationColor = hex)
        viewModelScope.launch { prefs.setCustomTransliterationColor(hex) }
    }

    fun setCanvasTheme(theme: String) {
        _uiState.value = _uiState.value.copy(canvasTheme = theme)
        viewModelScope.launch { prefs.setCanvasTheme(theme) }
    }

    fun setAccentTheme(theme: String) {
        _uiState.value = _uiState.value.copy(accentTheme = theme)
        viewModelScope.launch { prefs.setAccentTheme(theme) }
    }

    fun setUiFont(font: String) {
        _uiState.value = _uiState.value.copy(uiFont = font)
        viewModelScope.launch { prefs.setUiFont(font) }
    }

    fun setArabicFont(font: String) {
        _uiState.value = _uiState.value.copy(arabicFont = font)
        viewModelScope.launch { prefs.setArabicFont(font) }
    }

    fun setTextScale(scale: Float) {
        _uiState.value = _uiState.value.copy(textScale = scale)
        viewModelScope.launch { prefs.setTextScale(scale) }
    }

    fun setThemeStyle(style: String) {
        _uiState.value = _uiState.value.copy(themeStyle = style)
        viewModelScope.launch { prefs.setThemeStyle(style) }
    }

    fun setUiLanguage(lang: String) {
        val normalized = com.example.data.util.LocaleHelper.normalizeLanguage(lang)
        com.example.data.util.LocaleHelper.setUiLanguageSync(getApplication(), normalized)
        val current = _uiState.value
        val updatedMsgs = if (current.chatMessages.size == 1 && current.chatMessages[0].id == "welcome") {
            listOf(defaultWelcomeMessage(normalized))
        } else {
            current.chatMessages
        }
        val resolved = resolveDistrictDisplay(current.selectedDistrictId, current.isGpsLocation, current.locationDisplayName, normalized)
        _uiState.value = current.copy(
            uiLanguage = normalized,
            selectedDistrictName = resolved.first,
            locationDisplayName = resolved.second,
            chatMessages = updatedMsgs,
            suggestedChips = defaultSuggestedChips(normalized)
        )
        viewModelScope.launch { prefs.setUiLanguage(normalized) }
    }

    fun setContentLanguage(lang: String) {
        val normalized = com.example.data.util.LocaleHelper.normalizeLanguage(lang)
        com.example.data.util.LocaleHelper.setContentLanguageSync(getApplication(), normalized)
        _uiState.value = _uiState.value.copy(contentLanguage = normalized)
        viewModelScope.launch { prefs.setContentLanguage(normalized) }
    }

    fun setQari(qariId: String) {
        _uiState.value = _uiState.value.copy(selectedQariId = qariId)
        viewModelScope.launch { prefs.setSelectedQariId(qariId) }
    }

    fun setCalculationMethod(method: String) {
        _uiState.value = _uiState.value.copy(calculationMethod = method)
        viewModelScope.launch {
            prefs.setCalculationMethod(method)
            if (_uiState.value.prayerAlertsEnabled || _uiState.value.prePrayerAlertEnabled) {
                PrayerAlertScheduler.schedulePrayerAlerts(getApplication())
            }
            if (prefs.mosqueModeEnabled.first()) {
                com.example.data.mosque.MosqueModeScheduler.scheduleAllMosquePrayers(getApplication())
            }
        }
    }

    fun setJuristicMethod(method: String) {
        _uiState.value = _uiState.value.copy(juristicMethod = method)
        viewModelScope.launch {
            prefs.setJuristicMethod(method)
            if (_uiState.value.prayerAlertsEnabled || _uiState.value.prePrayerAlertEnabled) {
                PrayerAlertScheduler.schedulePrayerAlerts(getApplication())
            }
            if (prefs.mosqueModeEnabled.first()) {
                com.example.data.mosque.MosqueModeScheduler.scheduleAllMosquePrayers(getApplication())
            }
        }
    }

    fun setDistrict(district: com.example.data.model.District) {
        val isBn = com.example.data.util.SupportedLanguage.normalizeCode(_uiState.value.uiLanguage) == "bn"
        _uiState.value = _uiState.value.copy(
            selectedDistrictId = district.id,
            selectedDistrictName = if (!isBn) district.nameEnglish else district.nameBengali,
            locationDisplayName = if (!isBn) "${district.nameEnglish}, Bangladesh" else "${district.nameBengali}, বাংলাদেশ",
            isGpsLocation = false
        )
        viewModelScope.launch {
            prefs.setDistrict(district.id, district.nameBengali, district.latitude, district.longitude)
            if (_uiState.value.prayerAlertsEnabled || _uiState.value.prePrayerAlertEnabled) {
                PrayerAlertScheduler.schedulePrayerAlerts(getApplication())
            }
            if (prefs.mosqueModeEnabled.first()) {
                com.example.data.mosque.MosqueModeScheduler.scheduleAllMosquePrayers(getApplication())
            }
        }
    }

    fun autoDetectCurrentLocation(onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        val lang = _uiState.value.uiLanguage
        if (!locationHelper.hasLocationPermission()) {
            onResult(false, com.example.ui.theme.localizedText(lang, "অনুগ্রহ করে লোকেশন পারমিশন দিন", "Please grant location permission"))
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDetectingLocation = true)
            try {
                val detected = locationHelper.detectCurrentLocation()
                if (detected != null) {
                    prefs.setAutoDetectedLocation(
                        districtId = detected.districtId,
                        districtName = detected.districtName,
                        displayLocationName = detected.displayLocationName,
                        lat = detected.latitude,
                        lng = detected.longitude
                    )
                    _uiState.value = _uiState.value.copy(
                        selectedDistrictId = detected.districtId,
                        selectedDistrictName = detected.districtName,
                        locationDisplayName = detected.displayLocationName,
                        isGpsLocation = detected.isGpsDetected
                    )
                    if (_uiState.value.prayerAlertsEnabled || _uiState.value.prePrayerAlertEnabled) {
                        PrayerAlertScheduler.schedulePrayerAlerts(getApplication())
                    }
                    if (prefs.mosqueModeEnabled.first()) {
                        com.example.data.mosque.MosqueModeScheduler.scheduleAllMosquePrayers(getApplication())
                    }
                    val prefix = com.example.ui.theme.localizedText(lang, "স্বয়ংক্রিয় অবস্থান সেট হয়েছে", "Auto-detected location")
                    onResult(true, "$prefix: ${detected.displayLocationName}")
                } else {
                    onResult(
                        false,
                        com.example.ui.theme.localizedText(lang, "জিপিএস অবস্থান পাওয়া যায়নি, আবার চেষ্টা করুন", "GPS location unavailable, please try again")
                    )
                }
            } catch (_: Exception) {
                onResult(
                    false,
                    com.example.ui.theme.localizedText(lang, "অবস্থান শনাক্ত করতে ব্যর্থ হয়েছে", "Failed to detect location")
                )
            } finally {
                _uiState.value = _uiState.value.copy(isDetectingLocation = false)
            }
        }
    }

    fun setSahurCautionMins(mins: Int) {
        _uiState.value = _uiState.value.copy(sahurCautionMins = mins)
        viewModelScope.launch { prefs.setSahurCautionMins(mins) }
    }

    fun setIftarCautionMins(mins: Int) {
        _uiState.value = _uiState.value.copy(iftarCautionMins = mins)
        viewModelScope.launch { prefs.setIftarCautionMins(mins) }
    }

    fun setHijriOffset(offset: Int) {
        _uiState.value = _uiState.value.copy(hijriOffset = offset)
        viewModelScope.launch { prefs.setHijriOffset(offset) }
    }

    fun setDailyAyahNotif(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(dailyAyahNotifEnabled = enabled)
        viewModelScope.launch { prefs.setDailyAyahNotif(enabled) }
    }

    fun setDailyHadithNotif(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(dailyHadithNotifEnabled = enabled)
        viewModelScope.launch { prefs.setDailyHadithNotif(enabled) }
    }

    fun togglePrayerAlerts(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(prayerAlertsEnabled = enabled)
        viewModelScope.launch {
            prefs.setPrayerAlertsEnabled(enabled)
            PrayerAlertScheduler.schedulePrayerAlerts(getApplication())
        }
    }

    fun togglePrePrayerAlert(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(prePrayerAlertEnabled = enabled)
        viewModelScope.launch {
            prefs.setPrePrayerAlertEnabled(enabled)
            PrayerAlertScheduler.schedulePrayerAlerts(getApplication())
        }
    }

    fun setPrePrayerAlertMinutes(minutes: Int) {
        val safe = minutes.coerceIn(5, 45)
        _uiState.value = _uiState.value.copy(prePrayerAlertMinutes = safe)
        viewModelScope.launch {
            prefs.setPrePrayerAlertMinutes(safe)
            PrayerAlertScheduler.schedulePrayerAlerts(getApplication())
        }
    }

    fun sendTestAlert(prayer: PrayerName = PrayerName.FAJR) {
        PrayerAlertScheduler.triggerTestAlert(getApplication(), prayer)
    }

    fun sendTestPrePrayerAlert(prayer: PrayerName = PrayerName.ASR) {
        PrayerAlertScheduler.triggerTestAlert(
            context = getApplication(),
            prayer = prayer,
            isPreAlert = true,
            preMinutes = _uiState.value.prePrayerAlertMinutes
        )
    }

    fun updateCustomArabicText(text: String) {
        _uiState.value = _uiState.value.copy(customArabicText = text)
    }

    fun playCustomArabicAudio(
        textOverride: String? = null,
        ttsManager: com.example.data.audio.ArabicTtsManager? = null
    ) {
        val text = (textOverride ?: _uiState.value.customArabicText).trim()
        if (text.isBlank()) return

        audioPlayer.playArabicTextSpeech(
            text = text,
            title = com.example.ui.theme.localizedText(_uiState.value.uiLanguage, "আরবি উচ্চারণ", "Arabic Pronunciation"),
            subtitle = "Noor-e-Hidayah",
            speed = _uiState.value.ttsSpeed,
            ttsManager = ttsManager,
            preferOnline = true
        )
    }

    fun setTtsSpeed(speed: Float) {
        _uiState.value = _uiState.value.copy(ttsSpeed = speed)
        audioPlayer.setSpeed(speed)
    }

    fun dismissAiError() {
        _uiState.value = _uiState.value.copy(aiError = null)
    }

    fun sendAiPrompt(prompt: String) {
        val cleanPrompt = prompt.trim()
        if (cleanPrompt.isBlank() || _uiState.value.isAiThinking) return

        val lang = _uiState.value.uiLanguage
        val priorHistory = _uiState.value.chatMessages
        val userMsg = ChatMessage(
            id = "u_${System.currentTimeMillis()}",
            sender = "user",
            text = cleanPrompt
        )
        val assistantMsgId = "a_${System.currentTimeMillis() + 1}"
        _uiState.value = _uiState.value.copy(
            chatMessages = priorHistory + userMsg,
            isAiThinking = true,
            aiError = null
        )

        viewModelScope.launch {
            var assistantAdded = false
            try {
                geminiAssistant.generateReflectionStream(
                    userPrompt = cleanPrompt,
                    uiLanguage = lang,
                    history = priorHistory
                ).collect { partialText ->
                    val currentList = _uiState.value.chatMessages
                    val updatedList = if (!assistantAdded) {
                        assistantAdded = true
                        currentList + ChatMessage(
                            id = assistantMsgId,
                            sender = "assistant",
                            text = partialText
                        )
                    } else {
                        currentList.map { msg ->
                            if (msg.id == assistantMsgId) msg.copy(text = partialText) else msg
                        }
                    }
                    _uiState.value = _uiState.value.copy(chatMessages = updatedList)
                }
                _uiState.value = _uiState.value.copy(isAiThinking = false)
            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                android.util.Log.e("MoreViewModel", "AI chat stream timed out", e)
                val errText = com.example.ui.theme.localizedText(
                    lang,
                    "উত্তর পেতে সময় বেশি লাগছে। ইন্টারনেট সংযোগ দেখে আবার চেষ্টা করুন।",
                    "The response timed out. Please check your connection and try again."
                )
                _uiState.value = _uiState.value.copy(
                    isAiThinking = false,
                    aiError = errText
                )
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                android.util.Log.e("MoreViewModel", "AI chat stream failed", e)
                val detail = e.localizedMessage?.takeIf { it.isNotBlank() }
                val errText = com.example.ui.theme.localizedText(
                    lang,
                    "এআই সহকারীর সাথে সংযোগে সমস্যা হয়েছে। অনুগ্রহ করে আবার চেষ্টা করুন।",
                    "Unable to reach Noor AI right now. Please try again."
                ) + if (detail != null) " ($detail)" else ""
                _uiState.value = _uiState.value.copy(
                    isAiThinking = false,
                    aiError = errText
                )
            }
        }
    }

    private fun defaultWelcomeMessage(lang: String): ChatMessage = ChatMessage(
        id = "welcome",
        sender = "assistant",
        text = com.example.ui.theme.localizedText(
            lang,
            "আসসালামু আলাইকুম ওয়া রাহমাতুল্লাহ। নূর-এ-হেদায়াহ ইসলামিক এআই সহকারীতে স্বাগতম। আপনি কুরআন, হাদিস, নামাজের বিধান বা ইসলামিক জীবনবিধান সম্পর্কিত যেকোনো প্রশ্ন করতে পারেন।",
            "Assalamu Alaikum wa Rahmatullah. Welcome to Noor-e-Hidayah Islamic AI Companion. Ask any question about the Quran, Hadith, prayer rulings, or daily spiritual life."
        )
    )

    private fun defaultSuggestedChips(lang: String): List<String> = listOf(
        com.example.ui.theme.localizedText(lang, "তাহাজ্জুদের ফজিলত কী?", "Virtues of Tahajjud?"),
        com.example.ui.theme.localizedText(lang, "যাকাত কার ওপর ফরজ?", "Who must pay Zakat?"),
        com.example.ui.theme.localizedText(lang, "জুমু'আর দিনের বিশেষ আমল", "Friday Sunnah deeds"),
        com.example.ui.theme.localizedText(lang, "বিপদ মুক্তির শ্রেষ্ঠ দু'আ", "Du'a for hardship")
    )

    private fun resolveDistrictDisplay(
        districtId: String,
        isGps: Boolean,
        rawDisplay: String,
        lang: String
    ): Pair<String, String> {
        val dist = com.example.data.model.DistrictDirectory.BANGLADESH_DISTRICTS.find { it.id == districtId }
        val isBn = com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn"
        return if (!isBn) {
            val shortName = dist?.nameEnglish ?: if (rawDisplay == "ঢাকা, বাংলাদেশ") "Dhaka" else rawDisplay
            val fullDisplay = if (!isGps && dist != null) {
                "${dist.nameEnglish}, Bangladesh"
            } else if (rawDisplay == "ঢাকা, বাংলাদেশ") {
                "Dhaka, Bangladesh"
            } else {
                rawDisplay
            }
            shortName to fullDisplay
        } else {
            val shortName = dist?.nameBengali ?: _uiState.value.selectedDistrictName
            val fullDisplay = if (!isGps && dist != null) {
                "${dist.nameBengali}, বাংলাদেশ"
            } else {
                rawDisplay
            }
            shortName to fullDisplay
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}
