package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.audio.TasbeehSoundPool
import com.example.data.local.AppDatabase
import com.example.data.local.TasbihEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TasbeehPreset(
    val id: String,
    val arabic: String,
    val transliteration: String,
    val bengaliMeaning: String,
    val target: Int,
    val isCustom: Boolean = false,
    val englishMeaning: String = ""
) {
    fun localizedMeaning(lang: String): String =
        com.example.ui.theme.localizedText(lang, bengaliMeaning, englishMeaning.ifBlank { bengaliMeaning })

    fun localizedTransliteration(lang: String): String =
        com.example.data.util.ArabicTransliterationHelper.transliterate(
            arabic = arabic,
            langCode = lang,
            enCurated = transliteration
        )
}

val DEFAULT_PRESETS = listOf(
    TasbeehPreset("subhanallah", "سُبْحَانَ اللَّهِ", "SubhanAllah", "আল্লাহ পরম পবিত্র", 33, englishMeaning = "Glory be to Allah"),
    TasbeehPreset("alhamdulillah", "الْحَمْدُ لِلَّهِ", "Alhamdulillah", "সমস্ত প্রশংসা আল্লাহর জন্য", 33, englishMeaning = "All praise is due to Allah"),
    TasbeehPreset("allahuakbar", "اللَّهُ أَكْبَرُ", "Allahu Akbar", "আল্লাহ মহান", 34, englishMeaning = "Allah is the Greatest"),
    TasbeehPreset("astaghfirullah", "أَسْتَغْفِرُ اللَّهَ", "Astaghfirullah", "আমি আল্লাহর কাছে ক্ষমা চাই", 100, englishMeaning = "I seek forgiveness from Allah"),
    TasbeehPreset("lailahaillallah", "لَا إِلَهَ إِلَّا اللَّهُ", "La ilaha illallah", "আল্লাহ ব্যতীত কোনো উপাস্য নেই", 100, englishMeaning = "There is no deity worthy of worship except Allah"),
    TasbeehPreset("durood", "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ", "Allahumma Salli 'ala Muhammad", "রাসূল (সাঃ)-এর ওপর দরূদ", 100, englishMeaning = "O Allah, send blessings upon Muhammad"),
    TasbeehPreset("hasbunallah", "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ", "Hasbunallahu wa ni'mal wakeel", "আল্লাহই আমাদের জন্য যথেষ্ট", 100, englishMeaning = "Allah is sufficient for us, and He is the best Disposer of affairs"),
    TasbeehPreset("tasbih_yunus", "لَا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ", "La ilaha illa Anta Subhanaka Inni Kuntu Minaz-Zalimin", "দোয়ায়ে ইউনুস", 100, englishMeaning = "There is no deity except You; exalted are You, I have been among the wrongdoers")
)

data class TasbeehUiState(
    val currentPreset: TasbeehPreset = DEFAULT_PRESETS[0],
    val currentCount: Int = 0,
    val completedRounds: Int = 0,
    val targetCount: Int = 33,
    val totalCount: Long = 0L,
    val roundCelebrationMessage: String? = null,
    val beadOffsetFraction: Float = 0f
)

class TasbeehViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val tasbihDao = db.tasbihDao()
    private val soundPool = TasbeehSoundPool(application)
    private val prefs = com.example.data.preference.AppPreferences(application)
    private val cloudSyncRepo = com.example.data.cloud.CloudSyncRepository(application, prefs, db)
    private val worshipTracker = com.example.data.repository.WorshipTrackerRepository(application, prefs, db, cloudSyncRepo)

    private val _uiState = MutableStateFlow(TasbeehUiState())
    val uiState: StateFlow<TasbeehUiState> = _uiState.asStateFlow()

    private val _customDhikrs = MutableStateFlow<List<TasbeehPreset>>(emptyList())
    val customDhikrs: StateFlow<List<TasbeehPreset>> = _customDhikrs.asStateFlow()

    private val _uiLanguage = MutableStateFlow(com.example.data.util.LocaleHelper.getUiLanguageSync(application))

    val allAvailableDhikrs: StateFlow<List<TasbeehPreset>> = combine(
        _customDhikrs
    ) { (custom) ->
        DEFAULT_PRESETS + custom
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DEFAULT_PRESETS)

    init {
        viewModelScope.launch {
            prefs.uiLanguage.collect { _uiLanguage.value = it }
        }
        observeCustomDhikrs()
        loadPreset(DEFAULT_PRESETS[0])
    }

    private fun observeCustomDhikrs() {
        viewModelScope.launch(Dispatchers.IO) {
            tasbihDao.getAllCounters().collect { entities ->
                val customList = entities
                    .filter { it.isCustom }
                    .map { entity ->
                        TasbeehPreset(
                            id = entity.id,
                            arabic = entity.arabicPhrase,
                            transliteration = entity.transliteration,
                            bengaliMeaning = entity.bengaliMeaning,
                            target = entity.targetCount,
                            isCustom = true
                        )
                    }
                _customDhikrs.value = customList
            }
        }
    }

    fun selectPreset(preset: TasbeehPreset) {
        loadPreset(preset)
    }

    private fun loadPreset(preset: TasbeehPreset) {
        viewModelScope.launch(Dispatchers.IO) {
            val saved = tasbihDao.getCounterById(preset.id)
            val count = saved?.currentCount ?: 0
            val rounds = saved?.completedRounds ?: 0
            val total = saved?.totalHistoricalCount ?: 0L
            val target = saved?.targetCount ?: preset.target

            _uiState.update { current ->
                if (current.currentPreset.id == preset.id && current.targetCount != preset.target) {
                    current.copy(
                        currentPreset = preset,
                        currentCount = count,
                        completedRounds = rounds,
                        totalCount = total
                    )
                } else {
                    current.copy(
                        currentPreset = preset,
                        currentCount = count,
                        completedRounds = rounds,
                        targetCount = target,
                        totalCount = total,
                        roundCelebrationMessage = null
                    )
                }
            }
        }
    }

    fun increment() {
        val state = _uiState.value
        val newCount = state.currentCount + 1
        val newTotal = state.totalCount + 1

        if (newCount >= state.targetCount) {
            // Milestone reached: Completed a full round!
            val newRounds = state.completedRounds + 1
            soundPool.playRoundChime()
            soundPool.triggerMilestoneDoublePulse()
            val isBn = com.example.data.util.SupportedLanguage.normalizeCode(_uiLanguage.value) == "bn"
            val roundMsg = if (isBn) {
                "রাউন্ড ${com.example.data.util.SupportedLanguage.formatInt(newRounds, "bn")} সম্পন্ন! 🎉"
            } else {
                "Round $newRounds Complete! 🎉"
            }
            _uiState.value = state.copy(
                currentCount = 0,
                completedRounds = newRounds,
                totalCount = newTotal,
                roundCelebrationMessage = roundMsg
            )
        } else {
            // Instant mechanical bead click and haptic
            soundPool.playBeadClick()
            _uiState.value = state.copy(
                currentCount = newCount,
                totalCount = newTotal,
                roundCelebrationMessage = null
            )
        }

        saveCurrentState(recordTap = true)
    }

    fun reset() {
        _uiState.value = _uiState.value.copy(
            currentCount = 0,
            completedRounds = 0,
            roundCelebrationMessage = null
        )
        saveCurrentState()
    }

    fun setTarget(target: Int) {
        if (target <= 0) return
        _uiState.value = _uiState.value.copy(targetCount = target)
        saveCurrentState()
    }

    fun addCustomDua(
        arabic: String,
        transliteration: String,
        bengaliMeaning: String,
        target: Int
    ) {
        val safeTarget = target.coerceIn(1, 100_000)
        val customId = "custom_${System.currentTimeMillis()}"
        val safeArabic = com.example.data.security.InputSanitizer.sanitizeTextInput(arabic, 250).ifEmpty { 
            com.example.data.security.InputSanitizer.sanitizeTextInput(transliteration, 250) 
        }
        val lang = _uiLanguage.value
        val safeTrans = com.example.data.security.InputSanitizer.sanitizeTextInput(transliteration, 250).ifEmpty {
            com.example.ui.theme.localizedText(lang, "কাস্টম দোয়া", "Custom Dua")
        }
        val safeMeaning = com.example.data.security.InputSanitizer.sanitizeTextInput(bengaliMeaning, 300).ifEmpty {
            com.example.ui.theme.localizedText(lang, "ব্যক্তিগত আমল ও জিকির", "Personal Dhikr & Supplication")
        }

        val newPreset = TasbeehPreset(
            id = customId,
            arabic = safeArabic,
            transliteration = safeTrans,
            bengaliMeaning = safeMeaning,
            target = safeTarget,
            isCustom = true
        )

        // Switch to this newly added custom dua immediately for zero UI lag
        _uiState.value = TasbeehUiState(
            currentPreset = newPreset,
            currentCount = 0,
            completedRounds = 0,
            targetCount = safeTarget,
            totalCount = 0L,
            roundCelebrationMessage = com.example.ui.theme.localizedText(
                lang,
                "নতুন দোয়া সফলভাবে যুক্ত হয়েছে! ✨",
                "New Dua added successfully! ✨"
            )
        )

        viewModelScope.launch(Dispatchers.IO) {
            val entity = TasbihEntity(
                id = customId,
                arabicPhrase = safeArabic,
                transliteration = safeTrans,
                bengaliMeaning = safeMeaning,
                targetCount = safeTarget,
                currentCount = 0,
                completedRounds = 0,
                totalHistoricalCount = 0L,
                updatedAt = System.currentTimeMillis()
            )
            tasbihDao.insertOrUpdate(entity)
            cloudSyncRepo.autoSyncCustomZikr(entity)
        }
    }

    fun deleteCustomDua(presetId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            tasbihDao.deleteById(presetId)
            cloudSyncRepo.autoRemoveCustomZikr(presetId)
            if (_uiState.value.currentPreset.id == presetId) {
                loadPreset(DEFAULT_PRESETS[0])
            }
        }
    }

    fun dismissCelebration() {
        _uiState.value = _uiState.value.copy(roundCelebrationMessage = null)
    }

    private fun saveCurrentState(recordTap: Boolean = false) {
        val s = _uiState.value
        val isDurood = s.currentPreset.id == "durood" ||
            s.currentPreset.arabic.contains("صَلِّ") ||
            s.currentPreset.transliteration.contains("Salli", ignoreCase = true)
        viewModelScope.launch(Dispatchers.IO) {
            val entity = TasbihEntity(
                id = s.currentPreset.id,
                arabicPhrase = s.currentPreset.arabic,
                transliteration = s.currentPreset.transliteration,
                bengaliMeaning = s.currentPreset.bengaliMeaning,
                targetCount = s.targetCount,
                currentCount = s.currentCount,
                completedRounds = s.completedRounds,
                totalHistoricalCount = s.totalCount,
                updatedAt = System.currentTimeMillis()
            )
            tasbihDao.insertOrUpdate(entity)
            if (entity.isCustom) {
                cloudSyncRepo.autoSyncCustomZikr(entity)
            }
            if (recordTap) {
                worshipTracker.recordTasbihCount(1, isDurood = isDurood)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundPool.release()
    }
}
