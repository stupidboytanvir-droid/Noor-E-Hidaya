package com.example.data.audio

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class ArabicVoiceTone(
    val id: String,
    val titleBengali: String,
    val descriptionBengali: String,
    val pitch: Float,
    val speechRate: Float
) {
    SOULFUL_MELODIOUS(
        id = "soulful",
        titleBengali = "সুললিত ও প্রশান্ত কণ্ঠ",
        descriptionBengali = "কোমল, সুমধুর ও হৃদয়গ্রাহী পুরুষ তিলাওয়াত",
        pitch = 0.88f,
        speechRate = 0.80f
    ),
    DEEP_REVERENT(
        id = "reverent",
        titleBengali = "গম্ভীর ও অনুধ্যানী কণ্ঠ",
        descriptionBengali = "ধীরস্থির, গম্ভীর ও ভক্তিপূর্ণ গাম্ভীর্য",
        pitch = 0.82f,
        speechRate = 0.77f
    ),
    CLEAR_TAJWEED(
        id = "tajweed",
        titleBengali = "ধীর ও স্পষ্ট তালিম",
        descriptionBengali = "মাখরাজ ও হারাকাত শেখার স্পষ্ট উচ্চারণ",
        pitch = 0.90f,
        speechRate = 0.73f
    )
}

data class ArabicTtsState(
    val isInitialized: Boolean = false,
    val isSpeaking: Boolean = false,
    val isAvailable: Boolean = true,
    val maleVoiceName: String = "সুললিত পুরুষ কণ্ঠ (Arabic Male)",
    val selectedTone: ArabicVoiceTone = ArabicVoiceTone.SOULFUL_MELODIOUS,
    val speechRate: Float = 0.80f,
    val pitch: Float = 0.88f,
    val availableVoicesCount: Int = 0,
    val isNeuralVoice: Boolean = false,
    val errorMessage: String? = null,
    val requiresLanguageData: Boolean = false
)

/**
 * High-definition, soulful Arabic Text-to-Speech manager.
 * Strictly enforces a 100% natural, warm, and melodious male voice profile.
 * Incorporates Quranic Tajweed breathing pauses and initial DAC pre-buffering
 * to eliminate clipped or harsh robotic outputs.
 */
class ArabicTtsManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var triedGoogleEngine: Boolean = false
    private var isInitializing: Boolean = false
    private var pendingSpeakText: String? = null
    private var pendingSpeakSpeed: Float = 0.80f
    private var onSpeakCompleted: (() -> Unit)? = null
    private var hasVerifiedMaleVoice: Boolean = false

    private val _state = MutableStateFlow(ArabicTtsState())
    val state: StateFlow<ArabicTtsState> = _state.asStateFlow()

    init {
        initEngine(preferGoogle = true)
    }

    private fun initEngine(preferGoogle: Boolean) {
        if (isInitializing) return
        isInitializing = true
        triedGoogleEngine = preferGoogle
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {}
        tts = null

        try {
            tts = if (preferGoogle) {
                TextToSpeech(context.applicationContext, this, GOOGLE_TTS_PACKAGE)
            } else {
                TextToSpeech(context.applicationContext, this)
            }
        } catch (e: Exception) {
            isInitializing = false
            if (preferGoogle) {
                initEngine(preferGoogle = false)
            } else {
                failPendingSpeech("টিটিএস ইঞ্জিন শুরু করতে ব্যর্থ হয়েছে: ${e.localizedMessage}")
            }
        }
    }

    fun retryInitIfNeeded() {
        if (!_state.value.isInitialized && !isInitializing) {
            _state.value = _state.value.copy(
                isAvailable = true,
                requiresLanguageData = false,
                errorMessage = null
            )
            initEngine(preferGoogle = true)
        }
    }

    private fun failPendingSpeech(message: String, requiresData: Boolean = false) {
        pendingSpeakText = null
        val cb = onSpeakCompleted
        onSpeakCompleted = null
        _state.value = _state.value.copy(
            isInitialized = false,
            isSpeaking = false,
            isAvailable = !requiresData,
            requiresLanguageData = requiresData,
            errorMessage = message
        )
        cb?.invoke()
    }

    override fun onInit(status: Int) {
        isInitializing = false
        if (status == TextToSpeech.SUCCESS) {
            setupHighQualityMaleVoice()
        } else if (triedGoogleEngine) {
            // Fallback seamlessly to the device's default TTS engine
            initEngine(preferGoogle = false)
        } else {
            failPendingSpeech("টিটিএস ইঞ্জিন প্রস্তুত করা সম্ভব হয়নি (Code: $status)")
        }
    }

    private fun setupHighQualityMaleVoice() {
        val engine = tts ?: return

        // 1. Configure High-Fidelity Audio Attributes for Media Playback
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
            engine.setAudioAttributes(audioAttributes)
        } catch (_: Exception) {}

        // 2. Inspect available Arabic voices FIRST (captures online Google network male voices even without offline pack!)
        var bestMaleVoice: Voice? = null
        var isNeural = false
        var maleCount = 0

        try {
            val voices = engine.voices
            if (!voices.isNullOrEmpty()) {
                val arabicVoices = voices.filter { voice ->
                    voice.locale.language.equals("ar", ignoreCase = true) ||
                        voice.name.lowercase().startsWith("ar-") ||
                        voice.name.lowercase().startsWith("ar_")
                }

                // Filter out ALL female voice signatures (including Google TTS -arc-/-ard- female IDs and voice features)
                val nonFemaleVoices = arabicVoices.filter { voice ->
                    !isKnownFemaleVoice(voice)
                }

                val confirmedMaleVoices = nonFemaleVoices.filter { isKnownMaleVoiceName(it.name) }
                val candidatePool = if (confirmedMaleVoices.isNotEmpty()) confirmedMaleVoices else nonFemaleVoices
                maleCount = candidatePool.size

                bestMaleVoice = candidatePool.maxByOrNull { rateVoiceQuality(it) }
                hasVerifiedMaleVoice = bestMaleVoice != null && isKnownMaleVoiceName(bestMaleVoice.name)

                if (bestMaleVoice != null) {
                    val nameLower = bestMaleVoice.name.lowercase()
                    isNeural = nameLower.contains("wavenet") ||
                            nameLower.contains("neural") ||
                            nameLower.contains("network") ||
                            nameLower.contains("premium") ||
                            bestMaleVoice.quality >= Voice.QUALITY_HIGH
                }
            }
        } catch (_: Exception) {
            // In case voices API is restricted, rely on deep masculine pitch tuning
        }

        // 3. Select Arabic locale with Saudi Arabia preference (and apply discovered male voice if available)
        val arabicSaLocale = Locale.forLanguageTag("ar-SA")
        var langResult = engine.setLanguage(arabicSaLocale)
        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            langResult = engine.setLanguage(Locale.forLanguageTag("ar"))
        }

        var voiceApplied = false
        if (bestMaleVoice != null) {
            try {
                val voiceResult = engine.setVoice(bestMaleVoice)
                voiceApplied = (voiceResult == TextToSpeech.SUCCESS)
                if (!voiceApplied) {
                    engine.voice = bestMaleVoice
                    voiceApplied = true
                }
            } catch (_: Exception) {}
        }

        val localeReady = langResult != TextToSpeech.LANG_MISSING_DATA &&
            langResult != TextToSpeech.LANG_NOT_SUPPORTED

        if (!localeReady && !voiceApplied) {
            if (triedGoogleEngine) {
                // Retry with system default TTS engine in case it has Arabic installed
                initEngine(preferGoogle = false)
                return
            }
            val msg = if (langResult == TextToSpeech.LANG_MISSING_DATA) {
                "ডিভাইসে আরবি ভাষা প্যাক ডাউনলোড করা নেই — নিচের বাটনে ট্যাপ করে আরবি ভয়েস প্যাক ইনস্টল করুন"
            } else {
                "এই ডিভাইসে আরবি টিটিএস ভয়েস প্যাক পাওয়া যায়নি"
            }
            failPendingSpeech(msg, requiresData = true)
            return
        }

        // 4. Apply Strictly Masculine Acoustic Tuning
        val currentTone = _state.value.selectedTone
        val effectivePitch = if (hasVerifiedMaleVoice) currentTone.pitch else currentTone.pitch.coerceAtMost(0.80f)
        engine.setPitch(effectivePitch)
        engine.setSpeechRate(currentTone.speechRate)

        // 5. Utterance Progress Listener
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _state.value = _state.value.copy(
                    isSpeaking = true,
                    errorMessage = null
                )
            }

            override fun onDone(utteranceId: String?) {
                _state.value = _state.value.copy(isSpeaking = false)
                onSpeakCompleted?.invoke()
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _state.value = _state.value.copy(
                    isSpeaking = false,
                    errorMessage = "উচ্চারণ বাজাতে ত্রুটি ঘটেছে"
                )
                onSpeakCompleted?.invoke()
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                _state.value = _state.value.copy(
                    isSpeaking = false,
                    errorMessage = "উচ্চারণ ত্রুটি কোড: $errorCode"
                )
                onSpeakCompleted?.invoke()
            }
        })

        val displayVoiceName = when {
            bestMaleVoice != null && isNeural -> "নিউরাল সুললিত পুরুষ কণ্ঠ (${bestMaleVoice.name.takeLast(10)})"
            bestMaleVoice != null -> "বিশুদ্ধ পুরুষ কণ্ঠ (${bestMaleVoice.name.takeLast(8)})"
            else -> "সুললিত পুরুষ কণ্ঠ (প্রশান্ত সুর)"
        }

        _state.value = _state.value.copy(
            isInitialized = true,
            isAvailable = true,
            maleVoiceName = displayVoiceName,
            availableVoicesCount = maleCount,
            isNeuralVoice = isNeural,
            pitch = effectivePitch,
            speechRate = currentTone.speechRate,
            errorMessage = null
        )

        // Flush any speech queued while TTS engine was still binding
        val queuedText = pendingSpeakText
        val queuedSpeed = pendingSpeakSpeed
        pendingSpeakText = null
        if (!queuedText.isNullOrBlank()) {
            speakArabic(queuedText, queuedSpeed, onSpeakCompleted)
        }
    }

    private fun isKnownFemaleVoice(voice: Voice): Boolean {
        if (isKnownFemaleVoiceName(voice.name)) return true
        return try {
            voice.features?.any { feature ->
                feature.contains("female", ignoreCase = true) ||
                    feature.contains("woman", ignoreCase = true) ||
                    feature.contains("girl", ignoreCase = true)
            } == true
        } catch (_: Exception) {
            false
        }
    }

    companion object {
        private const val GOOGLE_TTS_PACKAGE = "com.google.android.tts"

        internal fun isKnownFemaleVoiceName(voiceName: String): Boolean {
            val lower = voiceName.lowercase()
            val femaleMarkers = listOf(
                "female", "woman", "girl", "fem", "#female",
                "-f-", "_f_", "-arc-", "-ard-", "x-arc", "x-ard",
                "wavenet-a", "wavenet-d", "standard-a", "standard-d",
                "neural2-a", "neural2-d", "chirp-hd-f", "chirp3-hd-aoede",
                "laila", "salma", "rana", "zaynab", "amina", "fatima",
                "mariam", "noura", "hana", "zeina", "sara"
            )
            return femaleMarkers.any { lower.contains(it) }
        }

        internal fun isKnownMaleVoiceName(voiceName: String): Boolean {
            val lower = voiceName.lowercase()
            val maleMarkers = listOf(
                "male", "masculine", "#male",
                "-m-", "_m_", "-are-", "-arz-", "x-are", "x-arz",
                "wavenet-b", "wavenet-c", "standard-b", "standard-c",
                "neural2-b", "neural2-c", "chirp-hd-m",
                "hamed", "tarik", "maged", "naayf", "bassel", "shakir", "omar", "zaid", "hamdan"
            )
            return maleMarkers.any { lower.contains(it) }
        }
    }

    private fun rateVoiceQuality(voice: Voice): Int {
        val name = voice.name.lowercase()
        var score = 100

        // Strongly prefer verified male Arabic voice IDs
        if (isKnownMaleVoiceName(name)) score += 800
        if (isKnownFemaleVoiceName(name)) score -= 5000

        // Prioritize High/Very High quality
        if (voice.quality == Voice.QUALITY_VERY_HIGH) score += 500
        else if (voice.quality == Voice.QUALITY_HIGH) score += 300

        // Prioritize neural / wavenet / network models
        if (name.contains("wavenet")) score += 450
        if (name.contains("neural")) score += 450
        if (name.contains("premium")) score += 350
        if (name.contains("network")) score += 250

        // Prioritize Saudi accent for authentic Tajweed
        if (voice.locale.country.equals("SA", ignoreCase = true)) score += 150

        // Penalize low quality or very high latency
        if (voice.quality == Voice.QUALITY_LOW) score -= 300
        if (voice.latency == Voice.LATENCY_VERY_HIGH) score -= 80

        return score
    }

    /**
     * Synthesizes and recites the given Arabic text with strictly male intonation.
     * Pre-processes Arabic text with breath pauses at punctuation and DAC pre-roll.
     */
    fun speakArabic(
        text: String,
        speed: Float = _state.value.speechRate,
        onCompleted: (() -> Unit)? = null
    ): Boolean {
        val trimmed = text.trim()
        if (trimmed.isBlank()) {
            _state.value = _state.value.copy(errorMessage = "অনুগ্রহ করে কোনো আরবি টেক্সট বা দু'আ লিখুন")
            return false
        }

        if (onCompleted != null) {
            onSpeakCompleted = onCompleted
        }

        val engine = tts
        if (engine == null || !_state.value.isInitialized) {
            if (isInitializing && engine != null) {
                // Queue utterance until onInit completes
                pendingSpeakText = trimmed
                pendingSpeakSpeed = speed
                _state.value = _state.value.copy(isSpeaking = true, errorMessage = null)
                return true
            }
            // Retry initialization once in case user just installed Arabic speech data
            if (_state.value.requiresLanguageData || engine == null) {
                pendingSpeakText = trimmed
                pendingSpeakSpeed = speed
                _state.value = _state.value.copy(isSpeaking = true, errorMessage = null)
                initEngine(preferGoogle = true)
                return true
            }
            _state.value = _state.value.copy(errorMessage = "টিটিএস ইঞ্জিন এখনও প্রস্তুত নয়")
            return false
        }

        // Apply strictly masculine pitch & rate
        val safePitch = if (hasVerifiedMaleVoice) _state.value.pitch else _state.value.pitch.coerceAtMost(0.80f)
        engine.setSpeechRate(speed)
        engine.setPitch(safePitch)

        // Pre-process text for natural Tajweed breath pauses and DAC anti-clip
        val soulfulText = prepareSoulfulArabicText(trimmed)

        val params = Bundle()
        val utteranceId = "arabic_soulful_${System.currentTimeMillis()}"
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)

        val result = engine.speak(soulfulText, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        if (result == TextToSpeech.SUCCESS) {
            _state.value = _state.value.copy(
                isSpeaking = true,
                speechRate = speed,
                errorMessage = null
            )
            return true
        } else {
            _state.value = _state.value.copy(
                isSpeaking = false,
                errorMessage = "টিটিএস চালু করা সম্ভব হয়নি"
            )
            return false
        }
    }

    /**
     * Pre-processes Arabic text:
     * 1. Inserts micro-pause leading space to avoid initial audio buffer clipping (e.g. clipped "Bismillah").
     * 2. Replaces Quranic stop symbols with soft breath-pauses (commas).
     * 3. Strips distracting bracketed numbers and brackets.
     */
    private fun prepareSoulfulArabicText(raw: String): String {
        var processed = raw
            // Replace Quranic stops with gentle pauses
            .replace("ۚ", "، ")
            .replace("ۖ", "، ")
            .replace("ۗ", "، ")
            .replace("ۘ", "، ")
            .replace("ۙ", "، ")
            .replace("۞", "، ")
            .replace("۝", " . ")
            // Clean bracketed numbers like (১), (৩৩) or [1]
            .replace(Regex("[\\(\\[][০-৯0-9\\s]+[\\)\\]]"), "")
            // Normalize spaces
            .replace(Regex("\\s+"), " ")
            .trim()

        // Prepend gentle breathing pause so Android DAC initializes cleanly without clipping
        return "  ، $processed"
    }

    fun setVoiceTone(tone: ArabicVoiceTone) {
        _state.value = _state.value.copy(
            selectedTone = tone,
            pitch = tone.pitch,
            speechRate = tone.speechRate
        )
        try {
            tts?.setPitch(tone.pitch)
            tts?.setSpeechRate(tone.speechRate)
        } catch (_: Exception) {}
    }

    fun setSpeechRate(rate: Float) {
        val clamped = rate.coerceIn(0.5f, 1.5f)
        _state.value = _state.value.copy(speechRate = clamped)
        try {
            tts?.setSpeechRate(clamped)
        } catch (_: Exception) {}
    }

    fun setPitch(pitch: Float) {
        val clamped = pitch.coerceIn(0.6f, 1.4f)
        _state.value = _state.value.copy(pitch = clamped)
        try {
            tts?.setPitch(clamped)
        } catch (_: Exception) {}
    }

    fun stop() {
        pendingSpeakText = null
        onSpeakCompleted = null
        try {
            tts?.stop()
        } catch (_: Exception) {}
        _state.value = _state.value.copy(isSpeaking = false)
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {}
        tts = null
    }
}
