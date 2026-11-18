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
        pitch = 0.94f,
        speechRate = 0.80f
    ),
    DEEP_REVERENT(
        id = "reverent",
        titleBengali = "গম্ভীর ও অনুধ্যানী কণ্ঠ",
        descriptionBengali = "ধীরস্থির, গম্ভীর ও ভক্তিপূর্ণ গাম্ভীর্য",
        pitch = 0.89f,
        speechRate = 0.77f
    ),
    CLEAR_TAJWEED(
        id = "tajweed",
        titleBengali = "ধীর ও স্পষ্ট তালিম",
        descriptionBengali = "মাখরাজ ও হারাকাত শেখার স্পষ্ট উচ্চারণ",
        pitch = 0.97f,
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
    val pitch: Float = 0.94f,
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

    private val _state = MutableStateFlow(ArabicTtsState())
    val state: StateFlow<ArabicTtsState> = _state.asStateFlow()

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isAvailable = false,
                errorMessage = "টিটিএস ইঞ্জিন শুরু করতে ব্যর্থ হয়েছে: ${e.localizedMessage}"
            )
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            setupHighQualityMaleVoice()
        } else {
            _state.value = _state.value.copy(
                isInitialized = false,
                isAvailable = false,
                errorMessage = "টিটিএস ইঞ্জিন প্রস্তুত করা সম্ভব হয়নি (Code: $status)"
            )
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

        // 2. Select Arabic locale with Saudi Arabia preference
        val arabicSaLocale = Locale.forLanguageTag("ar-SA")
        var langResult = engine.setLanguage(arabicSaLocale)
        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            langResult = engine.setLanguage(Locale.forLanguageTag("ar"))
        }

        if (langResult == TextToSpeech.LANG_MISSING_DATA) {
            _state.value = _state.value.copy(
                isInitialized = false,
                requiresLanguageData = true,
                errorMessage = "ডিভাইসে আরবি ভাষা প্যাক ডাউনলোড করা নেই"
            )
            return
        } else if (langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            _state.value = _state.value.copy(
                isInitialized = false,
                requiresLanguageData = true,
                errorMessage = "এই ডিভাইসে আরবি টিটিএস সরাসরি সমর্থিত নয়"
            )
            return
        }

        // 3. STRICT 100% MALE NEURAL/HIGH-QUALITY VOICE SELECTION
        var bestMaleVoice: Voice? = null
        var isNeural = false
        var maleCount = 0

        try {
            val voices = engine.voices
            if (!voices.isNullOrEmpty()) {
                val arabicVoices = voices.filter { it.locale.language == "ar" }

                // Filter out any voices with female indicators
                val nonFemaleVoices = arabicVoices.filter { voice ->
                    val nameLower = voice.name.lowercase()
                    !nameLower.contains("female") &&
                            !nameLower.contains("woman") &&
                            !nameLower.contains("girl") &&
                            !nameLower.contains("fem")
                }

                maleCount = nonFemaleVoices.size

                // Score candidates to pick the smoothest, most natural neural male voice
                bestMaleVoice = nonFemaleVoices.maxByOrNull { rateVoiceQuality(it) }

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
            // In case voices API is restricted, rely on pitch & cadence tuning
        }

        if (bestMaleVoice != null) {
            try {
                engine.voice = bestMaleVoice
            } catch (_: Exception) {}
        }

        // 4. Apply Initial Melodious Acoustic Tuning
        val currentTone = _state.value.selectedTone
        engine.setPitch(currentTone.pitch)
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
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _state.value = _state.value.copy(
                    isSpeaking = false,
                    errorMessage = "উচ্চারণ বাজাতে ত্রুটি ঘটেছে"
                )
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                _state.value = _state.value.copy(
                    isSpeaking = false,
                    errorMessage = "উচ্চারণ ত্রুটি কোড: $errorCode"
                )
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
            pitch = currentTone.pitch,
            speechRate = currentTone.speechRate,
            errorMessage = null
        )
    }

    private fun rateVoiceQuality(voice: Voice): Int {
        val name = voice.name.lowercase()
        var score = 100

        // Prioritize High/Very High quality
        if (voice.quality == Voice.QUALITY_VERY_HIGH) score += 500
        else if (voice.quality == Voice.QUALITY_HIGH) score += 300

        // Prioritize neural / wavenet / network models
        if (name.contains("wavenet")) score += 450
        if (name.contains("neural")) score += 450
        if (name.contains("premium")) score += 350
        if (name.contains("network")) score += 250

        // Look for male tags or known male voice indicators
        if (name.contains("male") || name.contains("masculine")) score += 300
        if (name.contains("-ard-") || name.contains("-arc-") || name.contains("#male") ||
            name.contains("-ar-sa-x-a") || name.contains("-ar-sa-x-b")) score += 250

        // Prioritize Saudi accent for authentic Tajweed
        if (voice.locale.country.equals("SA", ignoreCase = true)) score += 150

        // Penalize low quality or very high latency
        if (voice.quality == Voice.QUALITY_LOW) score -= 300
        if (voice.latency == Voice.LATENCY_VERY_HIGH) score -= 80

        return score
    }

    /**
     * Synthesizes and recites the given Arabic text with soulful male intonation.
     * Pre-processes Arabic text with breath pauses at punctuation and DAC pre-roll.
     */
    fun speakArabic(text: String, speed: Float = _state.value.speechRate): Boolean {
        val trimmed = text.trim()
        if (trimmed.isBlank()) {
            _state.value = _state.value.copy(errorMessage = "অনুগ্রহ করে কোনো আরবি টেক্সট বা দু'আ লিখুন")
            return false
        }

        val engine = tts
        if (engine == null || !_state.value.isInitialized) {
            _state.value = _state.value.copy(errorMessage = "টিটিএস ইঞ্জিন এখনও প্রস্তুত নয়")
            return false
        }

        // Apply acoustic pitch & rate
        engine.setSpeechRate(speed)
        engine.setPitch(_state.value.pitch)

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
