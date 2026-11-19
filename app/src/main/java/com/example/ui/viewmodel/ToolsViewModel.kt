package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AmalEntity
import com.example.data.local.AppDatabase
import com.example.data.mosque.MosqueModeScheduler
import com.example.data.preference.AppPreferences
import com.example.data.sensor.QiblaCompassManager
import com.example.data.sensor.QiblaState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ZakatState(
    val cashOnHand: Double = 0.0,
    val bankBalance: Double = 0.0,
    val goldGrams: Double = 0.0,
    val goldPricePerGram: Double = 11000.0,
    val silverGrams: Double = 0.0,
    val silverPricePerGram: Double = 180.0,
    val businessGoods: Double = 0.0,
    val debtsAndLiabilities: Double = 0.0,
    val familyMembersForFitrah: Int = 1,
    val fitrahPerPerson: Double = 120.0
) {
    val totalWealth: Double
        get() = cashOnHand + bankBalance + (goldGrams * goldPricePerGram) +
                (silverGrams * silverPricePerGram) + businessGoods

    val netWealth: Double
        get() = maxOf(0.0, totalWealth - debtsAndLiabilities)

    val silverNisabValue: Double
        get() = 595.0 * silverPricePerGram

    val isEligibleForZakat: Boolean
        get() = netWealth >= silverNisabValue

    val calculatedZakat: Double
        get() = if (isEligibleForZakat) netWealth * 0.025 else 0.0

    val calculatedFitrah: Double
        get() = familyMembersForFitrah * fitrahPerPerson
}

enum class ActiveToolTab {
    DASHBOARD,
    RUQYAH_SHARIAH,
    ARABIC_PRONUNCIATION,
    AI_ASSISTANT,
    TASBEEH,
    QIBLA,
    ZAKAT,
    MOSQUE_MODE,
    ASMA_UL_HUSNA,
    SUNNAH_HEALTH,
    ISLAMIC_INHERITANCE,
    JANAZAH_WASIYYAH,
    HADITH_LIBRARY,
    DEBT_LEDGER,
    NEARBY_MOSQUE,
    HIJRI_CALENDAR
}

class ToolsViewModel(application: Application) : AndroidViewModel(application) {

    val audioPlayer = com.example.data.audio.AudioPlayerManager.getInstance(application)

    private val db = AppDatabase.getInstance(application)
    private val amalDao = db.amalDao()
    private val prefs = AppPreferences(application)
    val qiblaManager = QiblaCompassManager(application)

    val qiblaState: StateFlow<QiblaState> = qiblaManager.qiblaState

    private val _activeTool = MutableStateFlow(ActiveToolTab.DASHBOARD)
    val activeTool: StateFlow<ActiveToolTab> = _activeTool.asStateFlow()

    private val _zakatState = MutableStateFlow(ZakatState())
    val zakatState: StateFlow<ZakatState> = _zakatState.asStateFlow()

    private val _mosqueModeEnabled = MutableStateFlow(false)
    val mosqueModeEnabled: StateFlow<Boolean> = _mosqueModeEnabled.asStateFlow()

    private val _mosqueRingerMode = MutableStateFlow(com.example.data.mosque.MosqueRingerMode.VIBRATE)
    val mosqueRingerMode: StateFlow<com.example.data.mosque.MosqueRingerMode> = _mosqueRingerMode.asStateFlow()

    private val _prePrayerAlertEnabled = MutableStateFlow(true)
    val prePrayerAlertEnabled: StateFlow<Boolean> = _prePrayerAlertEnabled.asStateFlow()

    private val _prePrayerAlertMinutes = MutableStateFlow(15)
    val prePrayerAlertMinutes: StateFlow<Int> = _prePrayerAlertMinutes.asStateFlow()

    private val _prayerSchedule = MutableStateFlow<com.example.data.model.PrayerSchedule?>(null)
    val prayerSchedule: StateFlow<com.example.data.model.PrayerSchedule?> = _prayerSchedule.asStateFlow()

    private val _mosqueBufferMins = MutableStateFlow(5)
    val mosqueBufferMins: StateFlow<Int> = _mosqueBufferMins.asStateFlow()

    private val _mosqueDurationMins = MutableStateFlow(25)
    val mosqueDurationMins: StateFlow<Int> = _mosqueDurationMins.asStateFlow()

    private val _mosqueConfigs = MutableStateFlow<Map<com.example.data.model.PrayerName, com.example.data.mosque.PrayerMosqueConfig>>(
        com.example.data.mosque.MosqueModeScheduler.PRAYERS.associateWith { prayer ->
            com.example.data.mosque.PrayerMosqueConfig.defaultFor(prayer)
        }
    )
    val mosqueConfigs: StateFlow<Map<com.example.data.model.PrayerName, com.example.data.mosque.PrayerMosqueConfig>> = _mosqueConfigs.asStateFlow()

    private val _isMosqueSilencedNow = MutableStateFlow(false)
    val isMosqueSilencedNow: StateFlow<Boolean> = _isMosqueSilencedNow.asStateFlow()

    private val _mosqueAutoReplySmsEnabled = MutableStateFlow(
        AppPreferences.isMosqueAutoReplySmsEnabledSync(application)
    )
    val mosqueAutoReplySmsEnabled: StateFlow<Boolean> = _mosqueAutoReplySmsEnabled.asStateFlow()

    private val _mosqueAutoReplyMessage = MutableStateFlow(
        AppPreferences.getMosqueAutoReplyMsgSync(application)
    )
    val mosqueAutoReplyMessage: StateFlow<String> = _mosqueAutoReplyMessage.asStateFlow()

    private val _lastAutoSmsStatus = MutableStateFlow(
        AppPreferences.getLastAutoSmsStatusSync(application)
    )
    val lastAutoSmsStatus: StateFlow<Triple<String, Boolean, Long>> = _lastAutoSmsStatus.asStateFlow()

    private val _tawafCircuit = MutableStateFlow(0)
    val tawafCircuit: StateFlow<Int> = _tawafCircuit.asStateFlow()

    val todayString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val todayAmal: StateFlow<AmalEntity?> = amalDao.getAmalForDate(todayString)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val weeklyAmal: StateFlow<List<AmalEntity>> = amalDao.getRecentWeekAmal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            launch { prefs.mosqueModeEnabled.collect { _mosqueModeEnabled.value = it } }
            launch { prefs.mosqueRingerMode.collect { _mosqueRingerMode.value = it } }
            launch { prefs.prePrayerAlertEnabled.collect { _prePrayerAlertEnabled.value = it } }
            launch { prefs.prePrayerAlertMinutes.collect { _prePrayerAlertMinutes.value = it } }
            launch { prefs.mosqueBufferMins.collect { _mosqueBufferMins.value = it } }
            launch { prefs.mosqueDurationMins.collect { _mosqueDurationMins.value = it } }
            launch {
                val dsEnabled = prefs.mosqueAutoReplySmsEnabled.first()
                if (!autoSmsToggleUpdatedLocally && !AppPreferences.isMosqueAutoReplySmsEnabledSync(getApplication()) && dsEnabled) {
                    AppPreferences.setMosqueAutoReplySmsEnabledSync(getApplication(), true)
                    _mosqueAutoReplySmsEnabled.value = true
                }
            }
            launch {
                val dsMsg = prefs.mosqueAutoReplyMessage.first()
                if (!autoSmsMsgUpdatedLocally && dsMsg.isNotBlank() && AppPreferences.getMosqueAutoReplyMsgSync(getApplication()) == AppPreferences.DEFAULT_AUTO_REPLY_SMS) {
                    _mosqueAutoReplyMessage.value = dsMsg
                }
            }
            launch {
                prefs.getAllPrayerMosqueConfigs().collect { loadedConfigs ->
                    _mosqueConfigs.value = loadedConfigs
                    refreshPrayerSchedule(loadedConfigs)
                }
            }
        }
        viewModelScope.launch {
            _isMosqueSilencedNow.value = com.example.data.mosque.MosqueModeManager.isCurrentlySilenced(getApplication())
            refreshPrayerSchedule(_mosqueConfigs.value)
            if (prefs.mosqueModeEnabled.first()) {
                com.example.data.mosque.MosqueModeScheduler.scheduleAllMosquePrayers(getApplication())
            }
        }
    }

    suspend fun refreshPrayerSchedule(
        configs: Map<com.example.data.model.PrayerName, com.example.data.mosque.PrayerMosqueConfig> = _mosqueConfigs.value
    ) {
        val calcMethodStr = prefs.calculationMethod.first()
        val juristicStr = prefs.juristicMethod.first()
        val lat = prefs.selectedDistrictLat.first()
        val lng = prefs.selectedDistrictLng.first()
        val locationDisplay = prefs.locationDisplayName.first()
        val hijriOffset = prefs.hijriOffset.first()
        val contentLang = prefs.contentLanguage.first()
        val method = try {
            com.example.data.model.CalculationMethod.valueOf(calcMethodStr)
        } catch (_: Exception) {
            com.example.data.model.CalculationMethod.MUSLIM_WORLD_LEAGUE
        }
        val juristic = com.example.data.model.JuristicMethod.fromString(juristicStr)
        val is24Hour = com.example.data.util.TimeFormatUtils.is24HourFormat(getApplication())
        val tzOffset = com.example.data.util.TimeFormatUtils.getLocalTimeZoneOffsetHours()
        val cal = java.util.Calendar.getInstance()
        val raw = com.example.data.model.PrayerCalculator.calculateSchedule(
            latitude = lat,
            longitude = lng,
            timeZone = tzOffset,
            calendar = cal,
            method = method,
            juristic = juristic,
            contentLang = contentLang,
            is24Hour = is24Hour,
            hijriOffset = hijriOffset
        )
        _prayerSchedule.value = com.example.data.model.PrayerCalculator.applyMosqueConfigs(
            schedule = raw,
            configs = configs,
            calendar = cal,
            is24Hour = is24Hour,
            now = System.currentTimeMillis()
        ).copy(locationName = locationDisplay)
    }

    fun openTool(tool: ActiveToolTab) {
        _activeTool.value = tool
        if (tool == ActiveToolTab.QIBLA) {
            qiblaManager.startListening()
        } else {
            qiblaManager.stopListening()
        }
        if (tool == ActiveToolTab.MOSQUE_MODE) {
            viewModelScope.launch { refreshPrayerSchedule() }
        }
    }

    fun closeTool() {
        qiblaManager.stopListening()
        _activeTool.value = ActiveToolTab.DASHBOARD
    }

    fun updateZakat(
        cash: Double = _zakatState.value.cashOnHand,
        bank: Double = _zakatState.value.bankBalance,
        goldGrams: Double = _zakatState.value.goldGrams,
        silverGrams: Double = _zakatState.value.silverGrams,
        goods: Double = _zakatState.value.businessGoods,
        debts: Double = _zakatState.value.debtsAndLiabilities,
        familyMembers: Int = _zakatState.value.familyMembersForFitrah
    ) {
        _zakatState.value = _zakatState.value.copy(
            cashOnHand = cash,
            bankBalance = bank,
            goldGrams = goldGrams,
            silverGrams = silverGrams,
            businessGoods = goods,
            debtsAndLiabilities = debts,
            familyMembersForFitrah = familyMembers
        )
    }

    fun toggleMosqueMode(enabled: Boolean) {
        _mosqueModeEnabled.value = enabled
        viewModelScope.launch {
            prefs.setMosqueModeEnabled(enabled)
            if (enabled) {
                com.example.data.mosque.MosqueModeScheduler.scheduleAllMosquePrayers(getApplication())
                _isMosqueSilencedNow.value = com.example.data.mosque.MosqueModeManager.isCurrentlySilenced(getApplication())
            } else {
                com.example.data.mosque.MosqueModeScheduler.cancelMosqueSchedule(getApplication())
                _isMosqueSilencedNow.value = false
            }
        }
    }

    fun setMosqueRingerMode(mode: com.example.data.mosque.MosqueRingerMode) {
        _mosqueRingerMode.value = mode
        viewModelScope.launch {
            prefs.setMosqueRingerMode(mode)
            if (_isMosqueSilencedNow.value) {
                val uiLang = com.example.data.util.LocaleHelper.getUiLanguageSync(getApplication())
                com.example.data.mosque.MosqueModeManager.activateMosqueMode(
                    context = getApplication(),
                    prayerName = com.example.ui.theme.localizedText(uiLang, "মসজিদ মোড", "Mosque Mode"),
                    preferredMode = mode
                )
            }
        }
    }

    fun togglePrePrayerAlert(enabled: Boolean) {
        _prePrayerAlertEnabled.value = enabled
        viewModelScope.launch {
            prefs.setPrePrayerAlertEnabled(enabled)
            com.example.data.notification.PrayerAlertScheduler.schedulePrayerAlerts(getApplication())
        }
    }

    fun setPrePrayerAlertMinutes(minutes: Int) {
        val safe = minutes.coerceIn(5, 45)
        _prePrayerAlertMinutes.value = safe
        viewModelScope.launch {
            prefs.setPrePrayerAlertMinutes(safe)
            com.example.data.notification.PrayerAlertScheduler.schedulePrayerAlerts(getApplication())
        }
    }

    fun setMosqueBuffers(bufferMins: Int, durationMins: Int) {
        _mosqueBufferMins.value = bufferMins
        _mosqueDurationMins.value = durationMins
        viewModelScope.launch {
            prefs.setMosqueBuffers(bufferMins, durationMins)
            if (_mosqueModeEnabled.value) {
                com.example.data.mosque.MosqueModeScheduler.scheduleAllMosquePrayers(getApplication())
            }
        }
    }

    fun updatePrayerConfig(config: com.example.data.mosque.PrayerMosqueConfig) {
        val current = _mosqueConfigs.value.toMutableMap()
        current[config.prayer] = config
        _mosqueConfigs.value = current
        viewModelScope.launch {
            prefs.savePrayerMosqueConfig(config)
            refreshPrayerSchedule(current)
            if (_mosqueModeEnabled.value) {
                com.example.data.mosque.MosqueModeScheduler.scheduleAllMosquePrayers(getApplication())
                _isMosqueSilencedNow.value = com.example.data.mosque.MosqueModeManager.isCurrentlySilenced(getApplication())
            }
            if (prefs.prayerAlertsEnabled.first() || prefs.prePrayerAlertEnabled.first()) {
                com.example.data.notification.PrayerAlertScheduler.schedulePrayerAlerts(getApplication())
            }
        }
    }

    fun togglePrayerMosque(prayer: com.example.data.model.PrayerName, enabled: Boolean) {
        val currentCfg = _mosqueConfigs.value[prayer] ?: com.example.data.mosque.PrayerMosqueConfig.defaultFor(prayer)
        updatePrayerConfig(currentCfg.copy(enabled = enabled))
    }

    fun setPrayerBuffer(prayer: com.example.data.model.PrayerName, bufferMins: Int) {
        val currentCfg = _mosqueConfigs.value[prayer] ?: com.example.data.mosque.PrayerMosqueConfig.defaultFor(prayer)
        updatePrayerConfig(currentCfg.copy(bufferBeforeMinutes = bufferMins))
    }

    fun setPrayerDuration(prayer: com.example.data.model.PrayerName, durationMins: Int) {
        val currentCfg = _mosqueConfigs.value[prayer] ?: com.example.data.mosque.PrayerMosqueConfig.defaultFor(prayer)
        updatePrayerConfig(currentCfg.copy(durationMinutes = durationMins))
    }

    fun setPrayerCustomTimes(
        prayer: com.example.data.model.PrayerName,
        startTime: String?,
        endTime: String?
    ) {
        val currentCfg = _mosqueConfigs.value[prayer] ?: com.example.data.mosque.PrayerMosqueConfig.defaultFor(prayer)
        updatePrayerConfig(
            currentCfg.copy(
                customStartTime = startTime?.takeIf { it.isNotBlank() },
                customEndTime = endTime?.takeIf { it.isNotBlank() }
            )
        )
    }

    fun setPrayerWaqtAndJamatTimes(
        prayer: com.example.data.model.PrayerName,
        customWaqt24: String?,
        jamat24: String?
    ) {
        val currentCfg = _mosqueConfigs.value[prayer] ?: com.example.data.mosque.PrayerMosqueConfig.defaultFor(prayer)
        updatePrayerConfig(
            currentCfg.copy(
                customPrayerTime = customWaqt24?.takeIf { it.isNotBlank() },
                jamatTime = jamat24?.takeIf { it.isNotBlank() }
            )
        )
    }

    fun resetPrayerTimings(prayer: com.example.data.model.PrayerName) {
        setPrayerWaqtAndJamatTimes(prayer, null, null)
    }

    fun testSilenceNow() {
        val uiLang = com.example.data.util.LocaleHelper.getUiLanguageSync(getApplication())
        com.example.data.mosque.MosqueModeManager.activateMosqueMode(
            context = getApplication(),
            prayerName = com.example.ui.theme.localizedText(uiLang, "পরীক্ষামূলক", "Test"),
            preferredMode = _mosqueRingerMode.value
        )
        _isMosqueSilencedNow.value = true
    }

    fun testRestoreNow() {
        com.example.data.mosque.MosqueModeScheduler.testRestoreNow(getApplication())
        _isMosqueSilencedNow.value = false
    }

    fun syncMosqueSilencedState() {
        com.example.data.mosque.MosqueModeManager.restoreIfExpired(getApplication())
        _isMosqueSilencedNow.value = com.example.data.mosque.MosqueModeManager.isCurrentlySilenced(getApplication())
    }

    fun refreshLastAutoSmsStatus() {
        _lastAutoSmsStatus.value = AppPreferences.getLastAutoSmsStatusSync(getApplication())
    }

    @Volatile
    private var autoSmsToggleUpdatedLocally = false

    @Volatile
    private var autoSmsMsgUpdatedLocally = false

    fun toggleMosqueAutoReplySms(enabled: Boolean) {
        autoSmsToggleUpdatedLocally = true
        _mosqueAutoReplySmsEnabled.value = enabled
        AppPreferences.setMosqueAutoReplySmsEnabledSync(getApplication(), enabled)
        viewModelScope.launch(Dispatchers.IO) {
            prefs.setMosqueAutoReplySmsEnabled(enabled)
        }
    }

    fun setMosqueAutoReplyMessage(message: String) {
        val clean = message.trim()
        if (clean.isEmpty()) return
        autoSmsMsgUpdatedLocally = true
        _mosqueAutoReplyMessage.value = clean
        AppPreferences.setMosqueAutoReplyMsgSync(getApplication(), clean)
        viewModelScope.launch(Dispatchers.IO) {
            prefs.setMosqueAutoReplyMessage(clean)
        }
    }

    fun sendTestAutoReplySms(targetNumber: String, onResult: (Boolean, String) -> Unit) {
        val trimmed = targetNumber.trim()
        if (trimmed.isBlank()) {
            val uiLang = com.example.data.util.LocaleHelper.getUiLanguageSync(getApplication())
            onResult(
                false,
                com.example.ui.theme.localizedText(uiLang, "দয়া করে একটি সঠিক ফোন নম্বর লিখুন", "Please enter a valid phone number")
            )
            return
        }
        val (success, message) = com.example.data.mosque.MosqueCallReceiver.sendNativeSimSms(
            context = getApplication(),
            rawPhoneNumber = trimmed,
            rawMessageText = _mosqueAutoReplyMessage.value,
            isTest = true
        )
        refreshLastAutoSmsStatus()
        onResult(success, message)
    }

    fun togglePrayerAmal(prayerName: String) {
        val current = todayAmal.value ?: AmalEntity(dateString = todayString)
        val updated = when (prayerName.lowercase()) {
            "fajr" -> current.copy(fajr = !current.fajr)
            "dhuhr" -> current.copy(dhuhr = !current.dhuhr)
            "asr" -> current.copy(asr = !current.asr)
            "maghrib" -> current.copy(maghrib = !current.maghrib)
            "isha" -> current.copy(isha = !current.isha)
            "tahajjud" -> current.copy(tahajjud = !current.tahajjud)
            "surahkahf" -> current.copy(surahKahf = !current.surahKahf)
            else -> current
        }
        viewModelScope.launch(Dispatchers.IO) {
            amalDao.saveAmal(updated)
            com.example.data.cloud.CloudSyncRepository(getApplication(), prefs, db)
                .autoSyncDailyPrayerAmal(updated)
        }
    }

    val ttsManager = com.example.data.audio.ArabicTtsManager(application)
    val ttsState = ttsManager.state

    fun playRuqyahTrack(track: com.example.data.model.RuqyahTrack, uiLang: String) {
        val subtitle = "${track.localizedReciterName(uiLang)} • ${track.durationText}"
        val mediaId = "ruqyah_${track.id}"
        if (track.playlistUrls.size > 1) {
            val title = track.localizedTitle(uiLang)
            val items = track.playlistUrls.mapIndexed { idx, url ->
                com.example.data.audio.AudioTrackItem(
                    url = url,
                    title = title,
                    subtitle = "$subtitle (${idx + 1}/${track.playlistUrls.size})",
                    mediaId = "${mediaId}_$idx",
                    fallbackUrls = if (idx == 0) track.fallbackUrls else emptyList(),
                    fallbackArabicText = track.arabicVersePreview
                )
            }
            audioPlayer.playPlaylist(
                items = items,
                startIndex = 0,
                ttsManager = ttsManager
            )
        } else {
            audioPlayer.playAudio(
                url = track.audioUrl,
                title = track.localizedTitle(uiLang),
                subtitle = subtitle,
                fallbackUrls = track.fallbackUrls,
                mediaId = mediaId,
                fallbackArabicText = track.arabicVersePreview,
                ttsManager = ttsManager
            )
        }
    }

    fun speakArabic(text: String, speed: Float = 0.85f): Boolean {
        val uiLang = com.example.data.util.LocaleHelper.getUiLanguageSync(getApplication())
        return audioPlayer.playArabicTextSpeech(
            text = text,
            title = com.example.ui.theme.localizedText(uiLang, "আরবি উচ্চারণ", "Arabic Pronunciation"),
            subtitle = "Noor-e-Hidayah",
            speed = speed,
            ttsManager = ttsManager,
            preferOnline = true
        )
    }

    fun playTilawatSample(
        urls: List<String>,
        title: String,
        subtitle: String,
        fallbackArabicText: String
    ) {
        val validUrls = urls.filter { it.isNotBlank() }
        if (validUrls.isEmpty()) {
            speakArabic(fallbackArabicText)
            return
        }
        if (validUrls.size == 1) {
            audioPlayer.playAudio(
                url = validUrls.first(),
                title = title,
                subtitle = subtitle,
                mediaId = "tilawat_${title.hashCode()}",
                fallbackArabicText = fallbackArabicText,
                ttsManager = ttsManager
            )
        } else {
            val items = validUrls.mapIndexed { idx, url ->
                com.example.data.audio.AudioTrackItem(
                    url = url,
                    title = title,
                    subtitle = "$subtitle (${idx + 1}/${validUrls.size})",
                    mediaId = "tilawat_${title.hashCode()}_$idx",
                    fallbackArabicText = fallbackArabicText
                )
            }
            audioPlayer.playPlaylist(
                items = items,
                startIndex = 0,
                ttsManager = ttsManager
            )
        }
    }

    fun stopTts() {
        ttsManager.stop()
        audioPlayer.stop()
    }

    fun setTtsSpeechRate(speed: Float) {
        ttsManager.setSpeechRate(speed)
    }

    fun setTtsVoiceTone(tone: com.example.data.audio.ArabicVoiceTone) {
        ttsManager.setVoiceTone(tone)
    }

    fun incrementTawaf() {
        if (_tawafCircuit.value < 7) {
            _tawafCircuit.value += 1
        }
    }

    fun resetTawaf() {
        _tawafCircuit.value = 0
    }

    override fun onCleared() {
        super.onCleared()
        qiblaManager.stopListening()
        ttsManager.shutdown()
        audioPlayer.release()
    }
}
