package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.MonthlyWorshipEntity
import com.example.data.location.LocationHelper
import com.example.data.model.*
import com.example.data.mosque.MosqueModeScheduler
import com.example.data.mosque.PrayerMosqueConfig
import com.example.data.notification.PrayerAlertScheduler
import com.example.data.preference.AppPreferences
import com.example.data.repository.WorshipTrackerRepository
import com.example.data.util.TimeFormatUtils
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import com.example.data.ai.DailyNasihahCache
import com.example.data.ai.DailyNasihahUiState
import com.example.data.ai.GeminiIslamicAssistant
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class AppTab(val titleBengali: String, val titleEnglish: String) {
    HOME("হোম", "Home"),
    QURAN("কুরআন", "Quran"),
    DUA("দু'আ", "Dua"),
    TOOLS("টুলস", "Tools"),
    MORE("আরও", "More")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = AppPreferences(application)
    private val db = AppDatabase.getInstance(application)
    private val locationHelper = LocationHelper(application)
    private val worshipTracker = WorshipTrackerRepository(application, prefs, db)
    private val geminiAssistant = GeminiIslamicAssistant()
    private var nasihahJob: Job? = null
    private var activeNasihahKey: Pair<String, String>? = null
    private var connectivityManager: ConnectivityManager? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _schedule = MutableStateFlow<PrayerSchedule?>(null)
    val schedule: StateFlow<PrayerSchedule?> = _schedule.asStateFlow()

    private val _mosqueConfigs = MutableStateFlow<Map<PrayerName, PrayerMosqueConfig>>(emptyMap())
    val mosqueConfigs: StateFlow<Map<PrayerName, PrayerMosqueConfig>> = _mosqueConfigs.asStateFlow()

    private val _isDetectingLocation = MutableStateFlow(false)
    val isDetectingLocation: StateFlow<Boolean> = _isDetectingLocation.asStateFlow()

    private val _uiLanguage = MutableStateFlow(com.example.data.util.LocaleHelper.getUiLanguageSync(application))
    val uiLanguage: StateFlow<String> = _uiLanguage.asStateFlow()

    private val _locationDisplayName = MutableStateFlow(
        com.example.ui.theme.localizedText(_uiLanguage.value, "ঢাকা, বাংলাদেশ", "Dhaka, Bangladesh")
    )
    val locationDisplayName: StateFlow<String> = _locationDisplayName.asStateFlow()

    private val _isGpsLocation = MutableStateFlow(false)
    val isGpsLocation: StateFlow<Boolean> = _isGpsLocation.asStateFlow()

    private val _selectedHadith = MutableStateFlow<DailyHadith?>(null)
    val selectedHadith: StateFlow<DailyHadith?> = _selectedHadith.asStateFlow()

    private val _contentLanguage = MutableStateFlow(com.example.data.util.LocaleHelper.getContentLanguageSync(application))
    val contentLanguage: StateFlow<String> = _contentLanguage.asStateFlow()

    private val _themeStyle = MutableStateFlow("EMERALD_GOLD")
    val themeStyle: StateFlow<String> = _themeStyle.asStateFlow()

    private val _themeMode = MutableStateFlow(com.example.ui.theme.ThemeMode.DARK.id)
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _customAccentHex = MutableStateFlow("#E5C158")
    val customAccentHex: StateFlow<String> = _customAccentHex.asStateFlow()

    private val _customArabicColor = MutableStateFlow("")
    val customArabicColor: StateFlow<String> = _customArabicColor.asStateFlow()

    private val _customTranslationColor = MutableStateFlow("")
    val customTranslationColor: StateFlow<String> = _customTranslationColor.asStateFlow()

    private val _customTransliterationColor = MutableStateFlow("")
    val customTransliterationColor: StateFlow<String> = _customTransliterationColor.asStateFlow()

    private val _canvasTheme = MutableStateFlow(com.example.ui.theme.AppCanvasTheme.OBSIDIAN.id)
    val canvasTheme: StateFlow<String> = _canvasTheme.asStateFlow()

    private val _accentTheme = MutableStateFlow(com.example.ui.theme.AppAccentTheme.CHAMPAGNE_GOLD.id)
    val accentTheme: StateFlow<String> = _accentTheme.asStateFlow()

    private val _uiFont = MutableStateFlow(com.example.ui.theme.AppUiFont.INTER.id)
    val uiFont: StateFlow<String> = _uiFont.asStateFlow()

    private val _arabicFont = MutableStateFlow(com.example.ui.theme.AppArabicFont.AMIRI.id)
    val arabicFont: StateFlow<String> = _arabicFont.asStateFlow()

    private val _textScale = MutableStateFlow(1.0f)
    val textScale: StateFlow<Float> = _textScale.asStateFlow()

    private val _onboardingCompleted = MutableStateFlow(
        AppPreferences.hasCompletedOnboardingSync(application)
    )
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    private val _initialLanguageSelected = MutableStateFlow(
        com.example.data.util.LocaleHelper.hasSelectedInitialLanguageSync(application)
    )
    val initialLanguageSelected: StateFlow<Boolean> = _initialLanguageSelected.asStateFlow()

    private val _dailyNasihahState = MutableStateFlow<DailyNasihahUiState>(DailyNasihahUiState.Loading)
    val dailyNasihahState: StateFlow<DailyNasihahUiState> = _dailyNasihahState.asStateFlow()

    private val _prayerStreak = MutableStateFlow(0)
    val prayerStreak: StateFlow<Int> = _prayerStreak.asStateFlow()

    private val _quranReadMinutesToday = MutableStateFlow(0)
    val quranReadMinutesToday: StateFlow<Int> = _quranReadMinutesToday.asStateFlow()

    private val _salawatJumatCount = MutableStateFlow(0)
    val salawatJumatCount: StateFlow<Int> = _salawatJumatCount.asStateFlow()

    private val _duasReadCount = MutableStateFlow(0)
    val duasReadCount: StateFlow<Int> = _duasReadCount.asStateFlow()

    private val _totalTasbihCount = MutableStateFlow(0)
    val totalTasbihCount: StateFlow<Int> = _totalTasbihCount.asStateFlow()

    private val _hadithReadMinutes = MutableStateFlow(0)
    val hadithReadMinutes: StateFlow<Int> = _hadithReadMinutes.asStateFlow()

    private val _hadithReadCount = MutableStateFlow(0)
    val hadithReadCount: StateFlow<Int> = _hadithReadCount.asStateFlow()

    private val todayDateKey: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    val todayCompletedPrayersCount: StateFlow<Int> = db.amalDao()
        .getAmalForDate(todayDateKey)
        .map { amal ->
            if (amal == null) 0
            else listOf(amal.fajr, amal.dhuhr, amal.asr, amal.maghrib, amal.isha).count { it }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val monthlyWorshipHistory: StateFlow<List<MonthlyWorshipEntity>> = worshipTracker
        .observeLast12Months()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _requestedSurahNumber = MutableStateFlow<Int?>(null)
    val requestedSurahNumber: StateFlow<Int?> = _requestedSurahNumber.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            worshipTracker.ensureCurrentMonthAndArchiveIfNeeded()
        }
        viewModelScope.launch {
            prefs.uiLanguage.collect { lang ->
                val changed = _uiLanguage.value != lang
                _uiLanguage.value = lang
                if (changed && prefs.isGpsLocation.first()) {
                    try {
                        val lat = prefs.selectedDistrictLat.first()
                        val lng = prefs.selectedDistrictLng.first()
                        val resolved = locationHelper.resolveLocationDetails(lat, lng, lang)
                        prefs.setAutoDetectedLocation(
                            districtId = resolved.districtId,
                            districtName = resolved.districtName,
                            displayLocationName = resolved.displayLocationName,
                            lat = resolved.latitude,
                            lng = resolved.longitude
                        )
                        _locationDisplayName.value = resolved.displayLocationName
                    } catch (_: Exception) {}
                }
                updateSchedule()
                if (changed || _dailyNasihahState.value is DailyNasihahUiState.Loading) {
                    refreshDailyNasihah(forceRefresh = false, languageOverride = lang)
                }
            }
        }
        viewModelScope.launch {
            prefs.contentLanguage.collect { lang ->
                _contentLanguage.value = lang
                updateSchedule()
            }
        }
        viewModelScope.launch {
            prefs.getAllPrayerMosqueConfigs().collect { configs ->
                _mosqueConfigs.value = configs
                updateSchedule()
            }
        }
        viewModelScope.launch { prefs.locationDisplayName.collect { _locationDisplayName.value = it } }
        viewModelScope.launch { prefs.isGpsLocation.collect { _isGpsLocation.value = it } }
        viewModelScope.launch { prefs.themeStyle.collect { _themeStyle.value = it } }
        viewModelScope.launch { prefs.themeMode.collect { _themeMode.value = it } }
        viewModelScope.launch { prefs.customAccentHex.collect { _customAccentHex.value = it } }
        viewModelScope.launch { prefs.customArabicColor.collect { _customArabicColor.value = it } }
        viewModelScope.launch { prefs.customTranslationColor.collect { _customTranslationColor.value = it } }
        viewModelScope.launch { prefs.customTransliterationColor.collect { _customTransliterationColor.value = it } }
        viewModelScope.launch { prefs.canvasTheme.collect { _canvasTheme.value = it } }
        viewModelScope.launch { prefs.accentTheme.collect { _accentTheme.value = it } }
        viewModelScope.launch { prefs.uiFont.collect { _uiFont.value = it } }
        viewModelScope.launch { prefs.arabicFont.collect { _arabicFont.value = it } }
        viewModelScope.launch { prefs.textScale.collect { _textScale.value = it } }
        viewModelScope.launch {
            prefs.onboardingCompleted.collect { completed ->
                if (completed || !AppPreferences.hasCompletedOnboardingSync(getApplication())) {
                    _onboardingCompleted.value = completed
                }
            }
        }
        viewModelScope.launch {
            prefs.initialLanguageSelected.collect { selected ->
                if (selected || !com.example.data.util.LocaleHelper.hasSelectedInitialLanguageSync(getApplication())) {
                    _initialLanguageSelected.value = selected
                }
            }
        }
        viewModelScope.launch { prefs.prayerStreak.collect { _prayerStreak.value = it } }
        viewModelScope.launch { prefs.quranReadMinutesToday.collect { _quranReadMinutesToday.value = it } }
        viewModelScope.launch { prefs.salawatJumatCount.collect { _salawatJumatCount.value = it } }
        viewModelScope.launch { prefs.duasReadCount.collect { _duasReadCount.value = it } }
        viewModelScope.launch { prefs.totalTasbihCount.collect { _totalTasbihCount.value = it } }
        viewModelScope.launch { prefs.hadithReadMinutes.collect { _hadithReadMinutes.value = it } }
        viewModelScope.launch { prefs.hadithReadCount.collect { _hadithReadCount.value = it } }

        // Automatically detect user's location on app startup if permission is granted
        autoDetectLocationIfPermitted(force = false)

        // Pre-fetch / load today's AI Believer's Nasihah immediately on startup
        refreshDailyNasihah(forceRefresh = false, languageOverride = _uiLanguage.value)
        registerConnectivityRetryCallback()

        // Timer loop to update prayer times and live countdown every second
        viewModelScope.launch {
            while (isActive) {
                updateSchedule()
                delay(1000)
            }
        }
    }

    private fun registerConnectivityRetryCallback() {
        try {
            val cm = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return
            connectivityManager = cm
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    viewModelScope.launch {
                        ensureDailyNasihahLoaded()
                    }
                }

                override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                    if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                        viewModelScope.launch {
                            ensureDailyNasihahLoaded()
                        }
                    }
                }
            }
            networkCallback = callback
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            cm.registerNetworkCallback(request, callback)
        } catch (_: Exception) {}
    }

    override fun onCleared() {
        super.onCleared()
        try {
            val cm = connectivityManager
            val cb = networkCallback
            if (cm != null && cb != null) {
                cm.unregisterNetworkCallback(cb)
            }
        } catch (_: Exception) {}
    }

    /**
     * Automatically detects the device's GPS/network location on startup when location permission
     * is granted (unless the user manually chose a district, or if force=true).
     */
    fun autoDetectLocationIfPermitted(
        force: Boolean = false,
        onResult: ((Boolean, String) -> Unit)? = null
    ) {
        if (!locationHelper.hasLocationPermission()) {
            onResult?.invoke(
                false,
                com.example.ui.theme.localizedText(_uiLanguage.value, "লোকেশন পারমিশন দেওয়া হয়নি", "Location permission not granted")
            )
            return
        }
        viewModelScope.launch {
            if (!force && prefs.locationManuallySelected.first()) {
                return@launch
            }
            if (_isDetectingLocation.value) return@launch
            _isDetectingLocation.value = true
            try {
                val detected = locationHelper.detectCurrentLocation(_uiLanguage.value)
                if (detected != null) {
                    prefs.setAutoDetectedLocation(
                        districtId = detected.districtId,
                        districtName = detected.districtName,
                        displayLocationName = detected.displayLocationName,
                        lat = detected.latitude,
                        lng = detected.longitude
                    )
                    _locationDisplayName.value = detected.displayLocationName
                    _isGpsLocation.value = detected.isGpsDetected
                    updateSchedule()
                    if (prefs.prayerAlertsEnabled.first() || prefs.prePrayerAlertEnabled.first()) {
                        PrayerAlertScheduler.schedulePrayerAlerts(getApplication())
                    }
                    if (prefs.mosqueModeEnabled.first()) {
                        MosqueModeScheduler.scheduleAllMosquePrayers(getApplication())
                    }
                    val prefix = com.example.ui.theme.localizedText(_uiLanguage.value, "অবস্থান শনাক্ত হয়েছে", "Location detected")
                    onResult?.invoke(true, "$prefix: ${detected.displayLocationName}")
                } else {
                    onResult?.invoke(
                        false,
                        com.example.ui.theme.localizedText(
                            _uiLanguage.value,
                            "জিপিএস অবস্থান শনাক্ত করা যায়নি, অনুগ্রহ করে লোকেশন চালু করুন",
                            "Could not detect GPS location. Please turn on Location."
                        )
                    )
                }
            } catch (_: Exception) {
                onResult?.invoke(
                    false,
                    com.example.ui.theme.localizedText(_uiLanguage.value, "অবস্থান শনাক্ত করতে সমস্যা হয়েছে", "Failed to detect location")
                )
            } finally {
                _isDetectingLocation.value = false
            }
        }
    }

    fun selectDistrictFromHome(district: District) {
        val lang = _uiLanguage.value
        val isBn = com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn"
        viewModelScope.launch {
            prefs.setDistrict(district.id, district.nameBengali, district.latitude, district.longitude)
            val countryLabel = com.example.ui.theme.LocalizationRegistry.translate(lang, "বাংলাদেশ", "Bangladesh")
            _locationDisplayName.value = if (!isBn) {
                "${district.nameEnglish}, $countryLabel"
            } else {
                "${district.nameBengali}, বাংলাদেশ"
            }
            _isGpsLocation.value = false
            updateSchedule()
            if (prefs.prayerAlertsEnabled.first() || prefs.prePrayerAlertEnabled.first()) {
                PrayerAlertScheduler.schedulePrayerAlerts(getApplication())
            }
            if (prefs.mosqueModeEnabled.first()) {
                MosqueModeScheduler.scheduleAllMosquePrayers(getApplication())
            }
        }
    }

    fun updatePrayerAndJamatTime(
        prayer: PrayerName,
        customPrayerTime24: String?,
        customJamatTime24: String?
    ) {
        viewModelScope.launch {
            val currentCfg = prefs.getPrayerMosqueConfig(prayer).first()
            val updated = currentCfg.copy(
                customPrayerTime = customPrayerTime24?.takeIf { it.isNotBlank() },
                jamatTime = customJamatTime24?.takeIf { it.isNotBlank() }
            )
            prefs.savePrayerMosqueConfig(updated)
            updateSchedule()
            if (prefs.prayerAlertsEnabled.first() || prefs.prePrayerAlertEnabled.first()) {
                PrayerAlertScheduler.schedulePrayerAlerts(getApplication())
            }
            if (prefs.mosqueModeEnabled.first()) {
                MosqueModeScheduler.scheduleAllMosquePrayers(getApplication())
            }
        }
    }

    fun resetPrayerCustomTime(prayer: PrayerName) {
        updatePrayerAndJamatTime(prayer, null, null)
    }

    fun selectInitialLanguage(uiLang: String, contentLang: String) {
        val normalizedUi = com.example.data.util.LocaleHelper.normalizeLanguage(uiLang)
        val normalizedContent = com.example.data.util.LocaleHelper.normalizeLanguage(contentLang)
        com.example.data.util.LocaleHelper.applyLanguageSelection(
            activity = null,
            context = getApplication(),
            uiLang = normalizedUi,
            contentLang = normalizedContent,
            recreateIfUiChanged = false
        )
        com.example.data.util.LocaleHelper.setInitialLanguageSelectedSync(getApplication(), true)
        _uiLanguage.value = normalizedUi
        _contentLanguage.value = normalizedContent
        _initialLanguageSelected.value = true
        viewModelScope.launch {
            prefs.setUiLanguage(normalizedUi)
            prefs.setContentLanguage(normalizedContent)
            prefs.setInitialLanguageSelected(true)
            updateSchedule()
        }
    }

    fun completeOnboarding() {
        AppPreferences.setOnboardingCompletedSync(getApplication(), true)
        _onboardingCompleted.value = true
        refreshDailyNasihah(forceRefresh = false, languageOverride = _uiLanguage.value)
        viewModelScope.launch {
            prefs.setOnboardingCompleted(true)
            autoDetectLocationIfPermitted(force = false)
        }
    }

    fun ensureDailyNasihahLoaded() {
        val targetLang = com.example.data.util.LocaleHelper.normalizeLanguage(_uiLanguage.value)
        val dateKey = todayDateKey
        val currentState = _dailyNasihahState.value
        val alreadyAiReadyForToday = currentState is DailyNasihahUiState.Ready &&
            currentState.nasihah.isAiGenerated &&
            currentState.nasihah.dateKey == dateKey &&
            com.example.data.util.SupportedLanguage.normalizeCode(currentState.nasihah.languageCode) == targetLang
        if (!alreadyAiReadyForToday) {
            refreshDailyNasihah(forceRefresh = false, languageOverride = targetLang)
        }
    }

    fun refreshDailyNasihah(
        forceRefresh: Boolean = false,
        languageOverride: String? = null
    ) {
        val targetLang = com.example.data.util.LocaleHelper.normalizeLanguage(
            languageOverride ?: _uiLanguage.value
        )
        val dateKey = todayDateKey
        val requestKey = dateKey to targetLang
        val appContext = getApplication<Application>()

        if (!forceRefresh) {
            DailyNasihahCache.getCached(appContext, dateKey, targetLang)?.let { cached ->
                _dailyNasihahState.value = DailyNasihahUiState.Ready(cached)
                return
            }
            if (nasihahJob?.isActive == true && activeNasihahKey == requestKey) {
                return
            }
        }

        nasihahJob?.cancel()
        activeNasihahKey = requestKey
        nasihahJob = viewModelScope.launch {
            // Surface any recent cached Nasihah in this language immediately while fetching today's live entry
            val latestCached = if (!forceRefresh) DailyNasihahCache.getLatestForLanguage(appContext, targetLang) else null
            _dailyNasihahState.value = if (latestCached != null) {
                DailyNasihahUiState.Ready(latestCached)
            } else {
                DailyNasihahUiState.Loading
            }

            val backoffMs = listOf(1_500L)
            try {
                var attempt = 0
                while (attempt <= backoffMs.size && isActive) {
                    try {
                        val generated = geminiAssistant.generateDailyBelieversNasihah(
                            context = appContext,
                            languageCode = targetLang,
                            dateKey = dateKey,
                            forceRefresh = forceRefresh
                        )
                        _dailyNasihahState.value = DailyNasihahUiState.Ready(generated)
                        return@launch
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: TimeoutCancellationException) {
                        Log.w("MainViewModel", "Daily AI Nasihah attempt ${attempt + 1} timed out", e)
                    } catch (e: Exception) {
                        Log.w("MainViewModel", "Daily AI Nasihah attempt ${attempt + 1} failed", e)
                    }
                    if (attempt < backoffMs.size) {
                        delay(backoffMs[attempt])
                    }
                    attempt++
                }
                // Never leave user stuck in endless loading spinner; surface cached or authentic Hadith-based fallback
                val fallback = DailyNasihahCache.getLatestForLanguage(appContext, targetLang)
                    ?: geminiAssistant.buildAuthenticFallbackNasihah(appContext, targetLang, dateKey)
                _dailyNasihahState.value = DailyNasihahUiState.Ready(fallback)
            } finally {
                if (activeNasihahKey == requestKey) {
                    activeNasihahKey = null
                }
            }
        }
    }

    fun incrementSalawatJumat() {
        _salawatJumatCount.value += 1
        viewModelScope.launch(Dispatchers.IO) {
            worshipTracker.recordDuroodCount(1)
        }
    }

    fun resetSalawatJumat() {
        _salawatJumatCount.value = 0
        viewModelScope.launch {
            prefs.resetSalawatJumatCount()
        }
    }

    fun recordDuaEngagement(count: Int = 1) {
        viewModelScope.launch(Dispatchers.IO) {
            worshipTracker.recordDuaEngagement(count)
        }
    }

    fun recordQuranMinutes(minutes: Int = 1) {
        viewModelScope.launch(Dispatchers.IO) {
            worshipTracker.recordQuranMinutes(minutes)
        }
    }

    fun recordHadithMinutes(minutes: Int = 1) {
        viewModelScope.launch(Dispatchers.IO) {
            worshipTracker.recordHadithMinutes(minutes)
        }
    }

    fun recordHadithRead(count: Int = 1) {
        viewModelScope.launch(Dispatchers.IO) {
            worshipTracker.recordHadithRead(count)
        }
    }

    fun navigateToSurah(surahNumber: Int) {
        _requestedSurahNumber.value = surahNumber
        _currentTab.value = AppTab.QURAN
    }

    fun clearRequestedSurah() {
        _requestedSurahNumber.value = null
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun showHadithDetails(hadith: DailyHadith) {
        _selectedHadith.value = hadith
    }

    fun closeHadithDetails() {
        _selectedHadith.value = null
    }

    private suspend fun updateSchedule() {
        val calcMethodStr = prefs.calculationMethod.first()
        val juristicStr = prefs.juristicMethod.first()
        val districtLat = prefs.selectedDistrictLat.first()
        val districtLng = prefs.selectedDistrictLng.first()
        val locationDisplay = prefs.locationDisplayName.first()
        val hijriOffset = prefs.hijriOffset.first()

        val method = try {
            CalculationMethod.valueOf(calcMethodStr)
        } catch (_: Exception) {
            CalculationMethod.MUSLIM_WORLD_LEAGUE
        }

        val juristic = JuristicMethod.fromString(juristicStr)

        val is24Hour = TimeFormatUtils.is24HourFormat(getApplication())
        val tzOffset = TimeFormatUtils.getLocalTimeZoneOffsetHours()
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()

        val rawCalculated = PrayerCalculator.calculateSchedule(
            latitude = districtLat,
            longitude = districtLng,
            timeZone = tzOffset,
            calendar = cal,
            method = method,
            juristic = juristic,
            contentLang = _uiLanguage.value,
            is24Hour = is24Hour,
            hijriOffset = hijriOffset
        )

        val configs = _mosqueConfigs.value.ifEmpty { prefs.getAllPrayerMosqueConfigs().first() }
        val adjusted = PrayerCalculator.applyMosqueConfigs(
            schedule = rawCalculated,
            configs = configs,
            calendar = cal,
            is24Hour = is24Hour,
            now = now
        )

        val isGps = prefs.isGpsLocation.first()
        val districtId = prefs.selectedDistrictId.first()
        val uiLang = _uiLanguage.value
        val isBn = com.example.data.util.SupportedLanguage.normalizeCode(uiLang) == "bn"
        val resolvedLocation = if (!isBn) {
            val countryLabel = com.example.ui.theme.LocalizationRegistry.translate(uiLang, "বাংলাদেশ", "Bangladesh")
            val cityToken = locationDisplay.substringBefore(",").trim()
            val matchedDistrict = DistrictDirectory.BANGLADESH_DISTRICTS.find {
                it.id == districtId ||
                    it.nameBengali.equals(cityToken, ignoreCase = true) ||
                    it.nameEnglish.equals(cityToken, ignoreCase = true)
            }
            when {
                !isGps && matchedDistrict != null -> "${matchedDistrict.nameEnglish}, $countryLabel"
                locationDisplay == "ঢাকা, বাংলাদেশ" || locationDisplay == "Dhaka, Bangladesh" -> {
                    val dhaka = com.example.ui.theme.LocalizationRegistry.translate(uiLang, "ঢাকা", "Dhaka")
                    "$dhaka, $countryLabel"
                }
                locationDisplay == "বর্তমান জিপিএস অবস্থান" -> {
                    com.example.ui.theme.LocalizationRegistry.translate(uiLang, "বর্তমান জিপিএস অবস্থান", "Current GPS Location")
                }
                locationDisplay == "মক্কা মুকাররমা" -> {
                    com.example.ui.theme.LocalizationRegistry.translate(uiLang, "মক্কা মুকাররমা", "Makkah")
                }
                locationDisplay == "মদিনা মুনাওয়ারা" -> {
                    com.example.ui.theme.LocalizationRegistry.translate(uiLang, "মদিনা মুনাওয়ারা", "Madinah")
                }
                matchedDistrict != null -> "${matchedDistrict.nameEnglish}, $countryLabel"
                else -> locationDisplay
            }
        } else {
            locationDisplay
        }

        _schedule.value = adjusted.copy(
            locationName = resolvedLocation
        )
    }
}
