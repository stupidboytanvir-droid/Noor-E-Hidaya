package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.audio.AudioPlayerManager
import com.example.data.audio.PlaybackState
import com.example.data.cloud.CloudSyncRepository
import com.example.data.local.AppDatabase
import com.example.data.local.BookmarkEntity
import com.example.data.model.Ayah
import com.example.data.model.QariDirectory
import com.example.data.model.QuranData
import com.example.data.model.Surah
import com.example.data.preference.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.data.util.LocaleHelper
import com.example.ui.theme.localizedName
import com.example.ui.theme.localizedText

enum class QuranAudioMode(
    val id: String,
    val titleBengali: String,
    val shortTitleBengali: String,
    val descriptionBengali: String,
    val titleEnglish: String = "",
    val shortTitleEnglish: String = "",
    val descriptionEnglish: String = ""
) {
    FULL_SURAH_CONTINUOUS(
        id = "continuous",
        titleBengali = "সম্পূর্ণ সূরা একনাগাড়ে",
        shortTitleBengali = "একনাগাড়ে সূরা",
        descriptionBengali = "প্রথম থেকে শেষ আয়াত পর্যন্ত কোনো বিরতি ছাড়া অবিরাম তিলাওয়াত",
        titleEnglish = "Full Surah Continuous",
        shortTitleEnglish = "Continuous",
        descriptionEnglish = "Uninterrupted recitation from the first Ayah to the last"
    ),
    VERSE_BY_VERSE(
        id = "verse_by_verse",
        titleBengali = "আয়াতওয়ারী পৃথক",
        shortTitleBengali = "আয়াতওয়ারী",
        descriptionBengali = "নির্দিষ্ট আয়াতে মনোযোগ, পুনরাবৃত্তি ও হিফজ অনুশীলন",
        titleEnglish = "Verse by Verse",
        shortTitleEnglish = "Verse-by-Verse",
        descriptionEnglish = "Focus on individual Ayahs with repetition and Hifz practice"
    );

    fun localizedTitle(lang: String): String = com.example.ui.theme.LocalizationRegistry.translate(lang, titleBengali, titleEnglish)
    fun localizedShortTitle(lang: String): String = com.example.ui.theme.LocalizationRegistry.translate(lang, shortTitleBengali, shortTitleEnglish)
    fun localizedDescription(lang: String): String = com.example.ui.theme.LocalizationRegistry.translate(lang, descriptionBengali, descriptionEnglish)
}

data class QuranUiState(
    val searchQuery: String = "",
    val filteredSurahs: List<Surah> = QuranData.SURAHS,
    val selectedSurah: Surah? = null,
    val ayahs: List<Ayah> = emptyList(),
    val currentPlayingAyah: Ayah? = null,
    val currentPlayingIndex: Int = -1,
    val audioMode: QuranAudioMode = QuranAudioMode.FULL_SURAH_CONTINUOUS,
    val isLoopingSingleAyah: Boolean = false,
    val selectedQariId: String = "alafasy",
    val uiLanguage: String = "bn",
    val contentLanguage: String = "bn",
    val fontSizeSp: Int = 22,
    // Hifz & Memorization Assistant State
    val isHifzMode: Boolean = false,
    val isArabicTextHidden: Boolean = false,
    val ayahRepeatTarget: Int = 3, // 1, 3, 5, 10, or -1 (loop forever)
    val currentAyahPlayedCount: Int = 0,
    val revealedAyahNumbers: Set<Int> = emptySet()
)

class QuranViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val bookmarkDao = db.bookmarkDao()
    private val prefs = AppPreferences(application)
    val audioPlayer = AudioPlayerManager.getInstance(application)
    val cloudSyncRepo = CloudSyncRepository(application, prefs, db)
    private val worshipTracker = com.example.data.repository.WorshipTrackerRepository(application, prefs, db, cloudSyncRepo)

    val playbackState: StateFlow<PlaybackState> = audioPlayer.playbackState
    val surahDownloads = com.example.data.audio.OfflineAudioManager.surahDownloads

    private val _downloadedSurahNumbers = MutableStateFlow<Set<Int>>(emptySet())
    val downloadedSurahNumbers: StateFlow<Set<Int>> = _downloadedSurahNumbers.asStateFlow()

    val bookmarks: StateFlow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(
        QuranUiState(
            uiLanguage = LocaleHelper.getUiLanguageSync(application),
            contentLanguage = LocaleHelper.getContentLanguageSync(application)
        )
    )
    val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            prefs.selectedQariId.collect { qari ->
                _uiState.value = _uiState.value.copy(selectedQariId = qari)
                refreshDownloadedSurahs(qari)
            }
        }
        viewModelScope.launch {
            prefs.uiLanguage.collect { lang ->
                _uiState.value = _uiState.value.copy(uiLanguage = lang)
            }
        }
        viewModelScope.launch {
            prefs.contentLanguage.collect { lang ->
                _uiState.value = _uiState.value.copy(contentLanguage = lang)
            }
        }

        // Keep highlight synced with Media3 playlist transitions
        audioPlayer.onTrackChanged = { newIndex ->
            val state = _uiState.value
            val ayah = state.ayahs.getOrNull(newIndex)
            if (ayah != null && state.selectedSurah != null) {
                _uiState.value = state.copy(
                    currentPlayingAyah = ayah,
                    currentPlayingIndex = newIndex
                )
            }
        }

        // Setup automated gapless advance, Hifz verse repetition, or single verse loop upon playback completion
        audioPlayer.onPlaybackCompleted = {
            handlePlaybackCompleted()
        }
    }

    private fun refreshDownloadedSurahs(qariId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val counts = QuranData.SURAHS.associate { it.number to it.numberOfAyahs }
            _downloadedSurahNumbers.value = com.example.data.audio.OfflineAudioManager.getDownloadedSurahNumbers(
                getApplication(), qariId, counts
            )
        }
    }

    fun downloadSurah(surah: Surah) {
        val qariId = _uiState.value.selectedQariId
        val qari = QariDirectory.getById(qariId)
        viewModelScope.launch(Dispatchers.IO) {
            val ok = com.example.data.audio.OfflineAudioManager.downloadSurah(
                context = getApplication(),
                qariId = qariId,
                qariServerBaseUrl = qari.audioBaseUrl,
                surahNumber = surah.number,
                ayahCount = surah.numberOfAyahs
            )
            if (ok) refreshDownloadedSurahs(qariId)
        }
    }

    fun deleteDownloadedSurah(surah: Surah) {
        val qariId = _uiState.value.selectedQariId
        viewModelScope.launch(Dispatchers.IO) {
            com.example.data.audio.OfflineAudioManager.deleteSurah(getApplication(), qariId, surah.number)
            refreshDownloadedSurahs(qariId)
        }
    }

    private fun handlePlaybackCompleted() {
        val state = _uiState.value
        if (state.selectedSurah == null || state.ayahs.isEmpty()) return

        // 1. Hifz Memorization Mode: Repetition loop according to target (e.g. 3x, 5x, 10x, loop)
        if (state.isHifzMode && state.currentPlayingAyah != null) {
            val nextPlayedCount = state.currentAyahPlayedCount + 1
            if (state.ayahRepeatTarget == -1 || nextPlayedCount < state.ayahRepeatTarget) {
                // Replay the same Ayah for memorization
                _uiState.value = state.copy(currentAyahPlayedCount = nextPlayedCount)
                playAyahAtIndex(state.currentPlayingIndex)
                return
            } else {
                // Target repetition reached for this Ayah! Reset count
                _uiState.value = state.copy(currentAyahPlayedCount = 0)
                if (state.audioMode == QuranAudioMode.FULL_SURAH_CONTINUOUS) {
                    val nextIdx = state.currentPlayingIndex + 1
                    if (nextIdx in state.ayahs.indices) {
                        playAyahAtIndex(nextIdx)
                        return
                    }
                }
                _uiState.value = _uiState.value.copy(
                    currentPlayingAyah = null,
                    currentPlayingIndex = -1
                )
                return
            }
        }

        // 2. Single-ayah loop in standard mode
        if (state.isLoopingSingleAyah && state.currentPlayingAyah != null) {
            playAyahAtIndex(state.currentPlayingIndex)
            return
        }

        // 3. Full Surah Continuous Mode: auto-advance to next Ayah
        if (state.audioMode == QuranAudioMode.FULL_SURAH_CONTINUOUS) {
            val nextIdx = state.currentPlayingIndex + 1
            if (nextIdx in state.ayahs.indices) {
                playAyahAtIndex(nextIdx)
            } else {
                // Entire Surah completed!
                _uiState.value = state.copy(
                    currentPlayingAyah = null,
                    currentPlayingIndex = -1
                )
            }
        } else {
            // VERSE_BY_VERSE Mode: stop cleanly after finishing the single verse
            _uiState.value = state.copy(
                currentPlayingAyah = null,
                currentPlayingIndex = -1
            )
        }
    }

    fun search(query: String) {
        val filtered = if (query.isBlank()) {
            QuranData.SURAHS
        } else {
            QuranData.SURAHS.filter {
                it.nameEnglish.contains(query, ignoreCase = true) ||
                        it.nameBengali.contains(query, ignoreCase = true) ||
                        it.translationEnglish.contains(query, ignoreCase = true) ||
                        it.translationBengali.contains(query, ignoreCase = true) ||
                        it.nameArabic.contains(query, ignoreCase = true) ||
                        it.number.toString() == query.trim()
            }
        }
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredSurahs = filtered
        )
    }

    fun openSurah(surah: Surah) {
        val ayahs = QuranData.getAyahsForSurah(surah.number, _uiState.value.selectedQariId)
        _uiState.value = _uiState.value.copy(
            selectedSurah = surah,
            ayahs = ayahs,
            currentPlayingAyah = null,
            currentPlayingIndex = -1
        )
    }

    fun closeSurah() {
        audioPlayer.stop()
        _uiState.value = _uiState.value.copy(
            selectedSurah = null,
            ayahs = emptyList(),
            currentPlayingAyah = null,
            currentPlayingIndex = -1,
            isHifzMode = false,
            isArabicTextHidden = false,
            revealedAyahNumbers = emptySet()
        )
    }

    fun openSurahByNumber(number: Int) {
        val surah = QuranData.SURAHS.find { it.number == number } ?: return
        openSurah(surah)
    }

    fun toggleHifzMode() {
        val next = !_uiState.value.isHifzMode
        _uiState.value = _uiState.value.copy(
            isHifzMode = next,
            isArabicTextHidden = if (!next) false else _uiState.value.isArabicTextHidden,
            currentAyahPlayedCount = 0
        )
    }

    fun toggleHideArabicText() {
        val next = !_uiState.value.isArabicTextHidden
        _uiState.value = _uiState.value.copy(
            isArabicTextHidden = next,
            revealedAyahNumbers = emptySet()
        )
    }

    fun toggleRevealAyah(ayahNumber: Int) {
        val current = _uiState.value.revealedAyahNumbers
        val next = if (current.contains(ayahNumber)) {
            current - ayahNumber
        } else {
            current + ayahNumber
        }
        _uiState.value = _uiState.value.copy(revealedAyahNumbers = next)
    }

    fun setAyahRepeatTarget(target: Int) {
        _uiState.value = _uiState.value.copy(
            ayahRepeatTarget = target,
            currentAyahPlayedCount = 0
        )
    }

    /**
     * Starts continuous playback from Ayah 1 of the open Surah to the very end.
     */
    fun playSurahFromStart() {
        val state = _uiState.value
        if (state.selectedSurah != null && state.ayahs.isNotEmpty()) {
            _uiState.value = state.copy(audioMode = QuranAudioMode.FULL_SURAH_CONTINUOUS)
            playAyahAtIndex(0)
        }
    }

    /**
     * Plays a specific Ayah by object. Optionally forces a specific playback mode.
     */
    fun playAyahAudio(ayah: Ayah, forcedMode: QuranAudioMode? = null) {
        val state = _uiState.value
        val index = state.ayahs.indexOf(ayah).let { if (it >= 0) it else 0 }
        if (forcedMode != null) {
            _uiState.value = state.copy(audioMode = forcedMode)
        }
        playAyahAtIndex(index)
    }

    /**
     * Internal playback trigger for a specific index in the active Surah.
     */
    fun playAyahAtIndex(index: Int) {
        val state = _uiState.value
        if (index !in state.ayahs.indices) return
        val ayah = state.ayahs[index]
        val surah = state.selectedSurah ?: return
        val qari = QariDirectory.getById(state.selectedQariId)
        val app = getApplication<Application>()

        _uiState.value = state.copy(
            currentPlayingAyah = ayah,
            currentPlayingIndex = index
        )

        val ayahWord = localizedText(state.uiLanguage, "আয়াত", "Ayah")

        // When playing continuously (standard Surah listening), enqueue the entire Surah as a Media3 playlist
        // so ExoPlayer pre-buffers upcoming Ayahs in background with zero lag between verses!
        if (state.audioMode == QuranAudioMode.FULL_SURAH_CONTINUOUS && !state.isHifzMode && !state.isLoopingSingleAyah) {
            val playlist = state.ayahs.map { item ->
                val resolvedUri = com.example.data.audio.OfflineAudioManager.getPlayableUrl(
                    context = app,
                    qariId = state.selectedQariId,
                    surahNumber = item.surahNumber,
                    ayahNumber = item.ayahNumber,
                    remoteUrl = item.audioUrl
                )
                com.example.data.audio.AudioTrackItem(
                    url = resolvedUri,
                    title = "${surah.localizedName(state.contentLanguage)} • $ayahWord ${item.ayahNumber}",
                    subtitle = "${qari.name} (${item.ayahNumber}/${state.ayahs.size})",
                    mediaId = "quran_${state.selectedQariId}_${item.surahNumber}_${item.ayahNumber}"
                )
            }
            audioPlayer.playPlaylist(
                items = playlist,
                startIndex = index,
                repeatOne = false,
                onTrackChanged = { newIdx ->
                    val latest = _uiState.value
                    val nextAyah = latest.ayahs.getOrNull(newIdx)
                    if (nextAyah != null) {
                        _uiState.value = latest.copy(
                            currentPlayingAyah = nextAyah,
                            currentPlayingIndex = newIdx
                        )
                    }
                },
                onPlaylistCompleted = {
                    _uiState.value = _uiState.value.copy(
                        currentPlayingAyah = null,
                        currentPlayingIndex = -1
                    )
                }
            )
            return
        }

        val resolvedSingleUrl = com.example.data.audio.OfflineAudioManager.getPlayableUrl(
            context = app,
            qariId = state.selectedQariId,
            surahNumber = ayah.surahNumber,
            ayahNumber = ayah.ayahNumber,
            remoteUrl = ayah.audioUrl
        )
        val modeLabel = if (state.isLoopingSingleAyah) {
            localizedText(state.uiLanguage, "আয়াত ${ayah.ayahNumber} (লুপ)", "Ayah ${ayah.ayahNumber} (Loop)")
        } else {
            localizedText(state.uiLanguage, "আয়াত ${ayah.ayahNumber}", "Ayah ${ayah.ayahNumber}")
        }

        audioPlayer.onTrackChanged = null
        audioPlayer.playAudio(
            url = resolvedSingleUrl,
            title = "${surah.localizedName(state.contentLanguage)} • $ayahWord ${ayah.ayahNumber}",
            subtitle = "${qari.name} ($modeLabel)",
            mediaId = "quran_${state.selectedQariId}_${ayah.surahNumber}_${ayah.ayahNumber}",
            onCompleted = { handlePlaybackCompleted() }
        )
    }

    fun playNextAyah() {
        val state = _uiState.value
        if (state.ayahs.isEmpty()) return
        if (audioPlayer.playbackState.value.playlistSize > 1) {
            audioPlayer.skipNext()
            return
        }
        val nextIndex = (state.currentPlayingIndex + 1).coerceAtMost(state.ayahs.size - 1)
        playAyahAtIndex(nextIndex)
    }

    fun playPreviousAyah() {
        val state = _uiState.value
        if (state.ayahs.isEmpty()) return
        if (audioPlayer.playbackState.value.playlistSize > 1) {
            audioPlayer.skipPrevious()
            return
        }
        val prevIndex = (state.currentPlayingIndex - 1).coerceAtLeast(0)
        playAyahAtIndex(prevIndex)
    }

    fun setAudioMode(mode: QuranAudioMode) {
        _uiState.value = _uiState.value.copy(audioMode = mode)
    }

    fun toggleLoopSingleAyah() {
        val current = _uiState.value.isLoopingSingleAyah
        _uiState.value = _uiState.value.copy(isLoopingSingleAyah = !current)
    }

    fun toggleBookmark(ayah: Ayah) {
        val surah = _uiState.value.selectedSurah ?: return
        val key = "${ayah.surahNumber}_${ayah.ayahNumber}"
        viewModelScope.launch(Dispatchers.IO) {
            val exists = bookmarkDao.isBookmarked(key)
            val entity = BookmarkEntity(
                key = key,
                surahNumber = ayah.surahNumber,
                surahNameArabic = surah.nameArabic,
                surahNameEnglish = surah.nameEnglish,
                ayahNumber = ayah.ayahNumber,
                ayahTextArabic = ayah.textArabic
            )
            if (exists) {
                bookmarkDao.removeBookmark(key)
                cloudSyncRepo.autoSyncBookmark(entity, isAdded = false)
            } else {
                bookmarkDao.addBookmark(entity)
                cloudSyncRepo.autoSyncBookmark(entity, isAdded = true)
            }
        }
    }

    fun setQari(qariId: String) {
        _uiState.value = _uiState.value.copy(selectedQariId = qariId)
        viewModelScope.launch {
            prefs.setSelectedQariId(qariId)
            // If a surah is currently opened, reload its ayahs with the new Qari's audio URLs
            _uiState.value.selectedSurah?.let { surah ->
                val updatedAyahs = QuranData.getAyahsForSurah(surah.number, qariId)
                _uiState.value = _uiState.value.copy(ayahs = updatedAyahs)
            }
        }
    }

    fun isSurahBookmarked(surahNumber: Int): Boolean {
        val key = "surah_$surahNumber"
        return bookmarks.value.any { it.key == key || (it.surahNumber == surahNumber && it.ayahNumber == 0) }
    }

    fun toggleSurahBookmark(surah: Surah) {
        val key = "surah_${surah.number}"
        viewModelScope.launch(Dispatchers.IO) {
            val exists = bookmarkDao.isBookmarked(key) || bookmarkDao.isBookmarked("${surah.number}_0")
            val entity = BookmarkEntity(
                key = key,
                surahNumber = surah.number,
                surahNameArabic = surah.nameArabic,
                surahNameEnglish = surah.nameEnglish,
                ayahNumber = 0,
                ayahTextArabic = surah.nameBengali
            )
            if (exists) {
                bookmarkDao.removeBookmark(key)
                bookmarkDao.removeBookmark("${surah.number}_0")
                cloudSyncRepo.autoSyncBookmark(entity, isAdded = false)
            } else {
                bookmarkDao.addBookmark(entity)
                cloudSyncRepo.autoSyncBookmark(entity, isAdded = true)
            }
        }
    }

    fun recordQuranReadingMinute(minutes: Int = 1) {
        viewModelScope.launch(Dispatchers.IO) {
            worshipTracker.recordQuranMinutes(minutes)
        }
    }

    fun setFontSize(size: Int) {
        _uiState.value = _uiState.value.copy(fontSizeSp = size)
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}
