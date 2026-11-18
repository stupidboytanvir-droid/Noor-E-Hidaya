package com.example.data.audio

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.data.util.LocaleHelper
import com.example.ui.theme.LocalizationRegistry
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class AudioTrackItem(
    val url: String,
    val title: String,
    val subtitle: String = "",
    val mediaId: String = url,
    val fallbackUrls: List<String> = emptyList(),
    val fallbackArabicText: String? = null
)

data class PlaybackState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentTitle: String = "",
    val currentSubtitle: String = "",
    val currentUrl: String = "",
    val currentMediaId: String = "",
    val currentIndex: Int = 0,
    val playlistSize: Int = 0,
    val hasNext: Boolean = false,
    val hasPrevious: Boolean = false,
    val currentPositionMs: Int = 0,
    val durationMs: Int = 0,
    val speed: Float = 1.0f,
    val isRepeatOne: Boolean = false,
    val error: String? = null
) {
    val isActive: Boolean
        get() = currentTitle.isNotBlank() && (isPlaying || isBuffering || currentUrl.isNotBlank())
}

/**
 * Unified Singleton Audio Engine backed by Media3 MediaController + NoorMediaPlaybackService.
 * Handles seamless multi-Ayah Quran playlists, cached Ruqyah & Du'a streams with automatic fallback URLs,
 * online/offline Arabic TTS pronunciation chunks, and persistent system media controls.
 */
class AudioPlayerManager private constructor(context: Context) {

    private val appContext = context.applicationContext
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null
    private var pendingCommand: ((MediaController) -> Unit)? = null
    private var progressJob: Job? = null

    private var currentTracks: List<AudioTrackItem> = emptyList()
    private var activeFallbackQueue: ArrayDeque<String> = ArrayDeque()
    private var activeFallbackArabicText: String? = null
    private var activeTtsManager: ArabicTtsManager? = null
    private val defaultTtsManager: ArabicTtsManager by lazy { ArabicTtsManager(appContext) }
    private var isPlayingViaTts: Boolean = false

    var onPlaybackCompleted: (() -> Unit)? = null
    var onTrackChanged: ((Int) -> Unit)? = null
    var onPlaylistCompleted: (() -> Unit)? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    companion object {
        @Volatile
        private var INSTANCE: AudioPlayerManager? = null

        fun getInstance(context: Context): AudioPlayerManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AudioPlayerManager(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }

    init {
        ensureControllerConnected()
    }

    private fun isRobolectricRuntime(): Boolean =
        android.os.Build.FINGERPRINT.equals("robolectric", ignoreCase = true)

    @Synchronized
    private fun ensureControllerConnected() {
        if (isRobolectricRuntime()) return
        if (controller != null || controllerFuture != null) return
        try {
            val sessionToken = SessionToken(
                appContext,
                ComponentName(appContext, NoorMediaPlaybackService::class.java)
            )
            val future = MediaController.Builder(appContext, sessionToken).buildAsync()
            controllerFuture = future
            future.addListener({
                try {
                    val mc = future.get()
                    mc.addListener(playerListener)
                    controller = mc
                    mc.setPlaybackSpeed(_playbackState.value.speed)
                    val cmd = pendingCommand
                    pendingCommand = null
                    cmd?.invoke(mc)
                } catch (e: Exception) {
                    controllerFuture = null
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = false,
                        isBuffering = false,
                        error = localizedError(
                            "অডিও সার্ভিস চালু করা যায়নি",
                            "Could not connect audio service"
                        )
                    )
                }
            }, ContextCompat.getMainExecutor(appContext))
        } catch (e: Exception) {
            controllerFuture = null
        }
    }

    private fun withController(action: (MediaController) -> Unit) {
        val mc = controller
        if (mc != null) {
            action(mc)
        } else {
            pendingCommand = action
            ensureControllerConnected()
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            syncStateFromController()
            if (isPlaying) startProgressTracker() else stopProgressTracker()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            syncStateFromController()
            if (playbackState == Player.STATE_ENDED) {
                stopProgressTracker()
                val singleComplete = onPlaybackCompleted
                val playlistComplete = onPlaylistCompleted
                _playbackState.value = _playbackState.value.copy(
                    isPlaying = false,
                    isBuffering = false,
                    currentPositionMs = 0
                )
                singleComplete?.invoke()
                playlistComplete?.invoke()
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val mc = controller ?: return
            val index = mc.currentMediaItemIndex
            val track = currentTracks.getOrNull(index)
            if (track != null) {
                activeFallbackQueue = ArrayDeque(track.fallbackUrls)
                activeFallbackArabicText = track.fallbackArabicText ?: activeFallbackArabicText
                _playbackState.value = _playbackState.value.copy(
                    currentIndex = index,
                    currentTitle = track.title,
                    currentSubtitle = track.subtitle,
                    currentUrl = track.url,
                    currentMediaId = track.mediaId,
                    hasNext = index < currentTracks.lastIndex,
                    hasPrevious = index > 0,
                    error = null
                )
            }
            onTrackChanged?.invoke(index)
        }

        override fun onPlayerError(error: PlaybackException) {
            // 1. Try next fallback URL automatically
            val nextFallback = if (activeFallbackQueue.isNotEmpty()) activeFallbackQueue.removeFirst() else null
            if (!nextFallback.isNullOrBlank()) {
                val state = _playbackState.value
                val mc = controller
                if (mc != null) {
                    val idx = mc.currentMediaItemIndex.coerceAtLeast(0)
                    val updatedTrack = (currentTracks.getOrNull(idx) ?: AudioTrackItem(
                        url = nextFallback,
                        title = state.currentTitle,
                        subtitle = state.currentSubtitle,
                        mediaId = state.currentMediaId
                    )).copy(url = nextFallback)
                    val mutable = currentTracks.toMutableList()
                    if (idx < mutable.size) mutable[idx] = updatedTrack
                    currentTracks = mutable

                    val item = buildMediaItem(updatedTrack)
                    mc.replaceMediaItem(idx, item)
                    mc.prepare()
                    mc.play()
                    _playbackState.value = state.copy(
                        currentUrl = nextFallback,
                        isBuffering = true,
                        error = null
                    )
                    return
                }
            }

            // 2. Fallback to strictly-male Arabic TTS for Du'a / custom pronunciation text
            val fallbackArabic = activeFallbackArabicText
            val tts = activeTtsManager ?: defaultTtsManager
            if (!fallbackArabic.isNullOrBlank()) {
                val spoke = tts.speakArabic(fallbackArabic, _playbackState.value.speed.coerceIn(0.6f, 1.4f)) {
                    isPlayingViaTts = false
                    val cb = onPlaybackCompleted
                    _playbackState.value = _playbackState.value.copy(isPlaying = false, isBuffering = false)
                    cb?.invoke()
                }
                if (spoke) {
                    isPlayingViaTts = true
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = true,
                        isBuffering = false,
                        error = null
                    )
                    return
                }
            }

            stopProgressTracker()
            _playbackState.value = _playbackState.value.copy(
                isPlaying = false,
                isBuffering = false,
                error = localizedError(
                    "অডিও লোড করা সম্ভব হয়নি, ইন্টারনেট সংযোগ পরীক্ষা করুন",
                    "Unable to play audio. Please check your internet connection."
                )
            )
        }
    }

    private fun syncStateFromController() {
        val mc = controller ?: return
        val idx = mc.currentMediaItemIndex.coerceAtLeast(0)
        val track = currentTracks.getOrNull(idx)
        val pState = mc.playbackState
        _playbackState.value = _playbackState.value.copy(
            isPlaying = mc.isPlaying,
            isBuffering = pState == Player.STATE_BUFFERING && mc.playWhenReady,
            currentTitle = track?.title ?: _playbackState.value.currentTitle,
            currentSubtitle = track?.subtitle ?: _playbackState.value.currentSubtitle,
            currentUrl = track?.url ?: _playbackState.value.currentUrl,
            currentMediaId = track?.mediaId ?: _playbackState.value.currentMediaId,
            currentIndex = idx,
            playlistSize = currentTracks.size,
            hasNext = idx < currentTracks.lastIndex,
            hasPrevious = idx > 0,
            currentPositionMs = mc.currentPosition.coerceAtLeast(0L).toInt(),
            durationMs = mc.duration.takeIf { it > 0L }?.toInt() ?: _playbackState.value.durationMs
        )
    }

    private fun buildMediaItem(track: AudioTrackItem): MediaItem {
        val extras = Bundle().apply {
            putString("track_subtitle", track.subtitle)
        }
        val metadata = MediaMetadata.Builder()
            .setTitle(track.title)
            .setArtist(track.subtitle.ifBlank { "Noor-e-Hidayah" })
            .setSubtitle(track.subtitle)
            .setExtras(extras)
            .build()
        return MediaItem.Builder()
            .setMediaId(track.mediaId.ifBlank { track.url })
            .setUri(Uri.parse(track.url))
            .setMediaMetadata(metadata)
            .build()
    }

    fun playAudio(
        url: String,
        title: String,
        subtitle: String = "",
        onCompleted: (() -> Unit)? = null,
        fallbackUrls: List<String> = emptyList(),
        mediaId: String = "",
        fallbackArabicText: String? = null,
        ttsManager: ArabicTtsManager? = null
    ) {
        if (url.isBlank()) {
            if (!fallbackArabicText.isNullOrBlank()) {
                playArabicTextSpeech(
                    text = fallbackArabicText,
                    title = title,
                    subtitle = subtitle,
                    ttsManager = ttsManager,
                    onCompleted = onCompleted
                )
            }
            return
        }

        // Stop any active male TTS before starting MP3 stream
        if (isPlayingViaTts) {
            isPlayingViaTts = false
            (activeTtsManager ?: defaultTtsManager).stop()
        }

        // Toggle if already loaded on this exact URL
        if (_playbackState.value.currentUrl == url && currentTracks.size == 1 && controller != null) {
            togglePlayPause()
            return
        }

        onPlaybackCompleted = onCompleted
        onPlaylistCompleted = null
        onTrackChanged = null
        activeFallbackQueue = ArrayDeque(fallbackUrls.filter { it.isNotBlank() && it != url })
        activeFallbackArabicText = fallbackArabicText
        activeTtsManager = ttsManager ?: defaultTtsManager

        val track = AudioTrackItem(
            url = url,
            title = title,
            subtitle = subtitle,
            mediaId = mediaId.ifBlank { url },
            fallbackUrls = fallbackUrls
        )
        currentTracks = listOf(track)

        _playbackState.value = _playbackState.value.copy(
            isBuffering = true,
            isPlaying = false,
            currentTitle = title,
            currentSubtitle = subtitle,
            currentUrl = url,
            currentMediaId = track.mediaId,
            currentIndex = 0,
            playlistSize = 1,
            hasNext = false,
            hasPrevious = false,
            currentPositionMs = 0,
            durationMs = 0,
            error = null
        )

        withController { mc ->
            mc.repeatMode = if (_playbackState.value.isRepeatOne) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
            mc.setMediaItem(buildMediaItem(track))
            mc.prepare()
            mc.playWhenReady = true
            mc.play()
        }
    }

    /**
     * Removed female Google Translate TTS endpoint.
     * Non-Quranic Arabic text now routes exclusively through the strictly-male ArabicTtsManager.
     */
    fun buildArabicTtsUrls(arabicText: String): Pair<String, List<String>> = "" to emptyList()

    /**
     * Enqueues a full playlist into ExoPlayer for seamless gapless playback (no per-Ayah delay).
     */
    fun playPlaylist(
        items: List<AudioTrackItem>,
        startIndex: Int = 0,
        repeatOne: Boolean = false,
        ttsManager: ArabicTtsManager? = null,
        onTrackChanged: ((Int) -> Unit)? = null,
        onPlaylistCompleted: (() -> Unit)? = null
    ) {
        if (items.isEmpty()) return
        val safeIndex = startIndex.coerceIn(0, items.lastIndex)

        this.currentTracks = items
        this.onTrackChanged = onTrackChanged
        this.onPlaylistCompleted = onPlaylistCompleted
        this.onPlaybackCompleted = null
        this.activeFallbackArabicText = items[safeIndex].fallbackArabicText
        this.activeTtsManager = ttsManager
        this.activeFallbackQueue = ArrayDeque(items[safeIndex].fallbackUrls)

        val initialTrack = items[safeIndex]
        _playbackState.value = _playbackState.value.copy(
            isBuffering = true,
            isPlaying = false,
            currentTitle = initialTrack.title,
            currentSubtitle = initialTrack.subtitle,
            currentUrl = initialTrack.url,
            currentMediaId = initialTrack.mediaId,
            currentIndex = safeIndex,
            playlistSize = items.size,
            hasNext = safeIndex < items.lastIndex,
            hasPrevious = safeIndex > 0,
            currentPositionMs = 0,
            durationMs = 0,
            isRepeatOne = repeatOne,
            error = null
        )

        val mediaItems = items.map { buildMediaItem(it) }
        withController { mc ->
            mc.repeatMode = if (repeatOne) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
            mc.setMediaItems(mediaItems, safeIndex, 0L)
            mc.prepare()
            mc.playWhenReady = true
            mc.play()
        }
    }

    /**
     * Plays Arabic text (Du'as, Hadiths, or custom Arabic input) strictly using
     * the male-only ArabicTtsManager (never female Google Translate TTS).
     */
    fun playArabicTextSpeech(
        text: String,
        title: String,
        subtitle: String = "",
        speed: Float = _playbackState.value.speed,
        ttsManager: ArabicTtsManager? = null,
        @Suppress("UNUSED_PARAMETER") preferOnline: Boolean = false,
        onCompleted: (() -> Unit)? = null
    ) {
        val cleaned = extractSpeakableArabicOrClean(text)
        if (cleaned.isBlank()) {
            _playbackState.value = _playbackState.value.copy(
                error = localizedError(
                    "উচ্চারণের জন্য অনুগ্রহ করে আরবি টেক্সট লিখুন",
                    "Please enter Arabic text to pronounce"
                )
            )
            return
        }

        // Stop any running ExoPlayer audio first
        try {
            controller?.stop()
            controller?.clearMediaItems()
        } catch (_: Exception) {}
        stopProgressTracker()

        val resolvedTts = ttsManager ?: defaultTtsManager
        activeTtsManager = resolvedTts
        activeFallbackArabicText = cleaned
        onPlaybackCompleted = onCompleted
        onPlaylistCompleted = onCompleted
        currentTracks = emptyList()
        val ttsMediaId = "tts_${title.hashCode()}"

        val spoke = resolvedTts.speakArabic(
            text = cleaned,
            speed = speed.coerceIn(0.6f, 1.4f)
        ) {
            isPlayingViaTts = false
            _playbackState.value = PlaybackState(speed = _playbackState.value.speed)
            onCompleted?.invoke()
        }

        if (spoke) {
            isPlayingViaTts = true
            _playbackState.value = PlaybackState(
                isPlaying = true,
                isBuffering = false,
                currentTitle = title,
                currentSubtitle = subtitle.ifBlank { resolvedTts.state.value.maleVoiceName },
                currentUrl = "",
                currentMediaId = ttsMediaId,
                currentIndex = 0,
                playlistSize = 1,
                hasNext = false,
                hasPrevious = false,
                speed = speed,
                error = null
            )
        } else {
            isPlayingViaTts = false
            val ttsError = resolvedTts.state.value.errorMessage
            _playbackState.value = _playbackState.value.copy(
                isPlaying = false,
                isBuffering = false,
                error = ttsError ?: localizedError(
                    "ডিভাইসে পুরুষ আরবি ভয়েস প্যাক প্রস্তুত নয়",
                    "Male Arabic voice pack is not ready on this device"
                )
            )
        }
    }

    fun togglePlayPause() {
        if (isPlayingViaTts) {
            stop()
            return
        }
        val mc = controller
        if (mc == null) {
            val url = _playbackState.value.currentUrl
            if (url.isNotBlank()) {
                playAudio(url, _playbackState.value.currentTitle, _playbackState.value.currentSubtitle)
            }
            return
        }
        if (mc.isPlaying) {
            mc.pause()
        } else {
            if (mc.playbackState == Player.STATE_ENDED) {
                mc.seekTo(0L)
            }
            if (mc.mediaItemCount > 0) {
                mc.prepare()
                mc.play()
            } else if (_playbackState.value.currentUrl.isNotBlank()) {
                playAudio(
                    _playbackState.value.currentUrl,
                    _playbackState.value.currentTitle,
                    _playbackState.value.currentSubtitle
                )
            }
        }
        syncStateFromController()
    }

    fun pause() {
        controller?.pause()
        syncStateFromController()
    }

    fun resume() {
        controller?.let { mc ->
            if (mc.playbackState == Player.STATE_ENDED) mc.seekTo(0L)
            mc.prepare()
            mc.play()
            syncStateFromController()
        }
    }

    fun seekTo(positionMs: Int) {
        controller?.let { mc ->
            mc.seekTo(positionMs.coerceAtLeast(0).toLong())
            _playbackState.value = _playbackState.value.copy(currentPositionMs = positionMs.coerceAtLeast(0))
        }
    }

    fun seekToIndex(index: Int) {
        val mc = controller ?: return
        if (index in 0 until mc.mediaItemCount) {
            mc.seekTo(index, 0L)
            mc.prepare()
            mc.play()
        }
    }

    fun skipNext() {
        val mc = controller ?: return
        if (mc.hasNextMediaItem()) {
            mc.seekToNextMediaItem()
            mc.play()
        }
    }

    fun skipPrevious() {
        val mc = controller ?: return
        if (mc.hasPreviousMediaItem()) {
            mc.seekToPreviousMediaItem()
            mc.play()
        } else {
            mc.seekTo(0L)
        }
    }

    fun setRepeatOne(repeatOne: Boolean) {
        _playbackState.value = _playbackState.value.copy(isRepeatOne = repeatOne)
        controller?.repeatMode = if (repeatOne) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
    }

    fun setSpeed(speed: Float) {
        val clamped = speed.coerceIn(0.5f, 1.75f)
        _playbackState.value = _playbackState.value.copy(speed = clamped)
        controller?.setPlaybackSpeed(clamped)
    }

    fun stop() {
        stopProgressTracker()
        if (isPlayingViaTts) {
            isPlayingViaTts = false
            try {
                (activeTtsManager ?: defaultTtsManager).stop()
            } catch (_: Exception) {}
        }
        activeFallbackQueue.clear()
        activeFallbackArabicText = null
        currentTracks = emptyList()
        onPlaybackCompleted = null
        onPlaylistCompleted = null
        onTrackChanged = null
        try {
            controller?.stop()
            controller?.clearMediaItems()
        } catch (_: Exception) {}
        _playbackState.value = PlaybackState(speed = _playbackState.value.speed)
    }

    /**
     * Kept for backward compatibility; does NOT tear down the singleton background MediaSessionService
     * when individual ViewModels clear so audio continues seamlessly across screen transitions.
     */
    fun release() {
        // Deliberately no-op so singleton background playback persists across tabs/ViewModels
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressJob = scope.launch {
            while (isActive) {
                val mc = controller
                if (mc != null && mc.isPlaying) {
                    _playbackState.value = _playbackState.value.copy(
                        currentPositionMs = mc.currentPosition.coerceAtLeast(0L).toInt(),
                        durationMs = mc.duration.takeIf { it > 0L }?.toInt()
                            ?: _playbackState.value.durationMs
                    )
                }
                delay(400)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun localizedError(bn: String, en: String): String {
        val lang = LocaleHelper.getUiLanguageSync(appContext)
        return LocalizationRegistry.translate(lang, bn, en)
    }

    private fun extractSpeakableArabicOrClean(raw: String): String {
        val cleaned = raw
            .replace(Regex("[\\(\\[][০-৯0-9\\s]+[\\)\\]]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
        // If text contains mixed Arabic + other scripts (e.g. from AI Assistant response), extract Arabic segments
        val hasArabic = cleaned.any { it in '\u0600'..'\u06FF' || it in '\u0750'..'\u077F' }
        if (!hasArabic) return cleaned
        val nonArabicRatio = cleaned.count { it.isLetter() && it !in '\u0600'..'\u06FF' && it !in '\u0750'..'\u077F' }
        val arabicCount = cleaned.count { it in '\u0600'..'\u06FF' }
        if (nonArabicRatio > arabicCount * 2) {
            val arabicOnly = Regex("[\u0600-\u06FF\u0750-\u077F\u08A0-\u08FF\\s،؛؟.!]+")
                .findAll(cleaned)
                .map { it.value.trim() }
                .filter { it.any { c -> c in '\u0600'..'\u06FF' } }
                .joinToString(" ، ")
            if (arabicOnly.isNotBlank()) return arabicOnly
        }
        return cleaned
    }

    private fun chunkSpeechText(text: String, maxLength: Int = 175): List<String> {
        if (text.length <= maxLength) return listOf(text)
        val result = mutableListOf<String>()
        var remaining = text.trim()
        while (remaining.isNotEmpty()) {
            if (remaining.length <= maxLength) {
                result.add(remaining)
                break
            }
            var splitIdx = remaining.lastIndexOf(' ', maxLength)
            if (splitIdx < maxLength / 3) {
                splitIdx = remaining.lastIndexOf('،', maxLength)
            }
            if (splitIdx <= 0) splitIdx = maxLength
            val chunk = remaining.substring(0, splitIdx).trim()
            if (chunk.isNotEmpty()) result.add(chunk)
            remaining = remaining.substring(splitIdx).trim()
        }
        return result
    }
}
