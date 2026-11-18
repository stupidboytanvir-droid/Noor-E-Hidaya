package com.example.data.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.R

class TasbeehSoundPool(private val context: Context) {

    private var soundPool: SoundPool? = null
    private var beadClickSoundId: Int = 0
    private var roundChimeSoundId: Int = 0
    private var isBeadLoaded = false
    private var isChimeLoaded = false

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(8)
            .setAudioAttributes(audioAttributes)
            .build().apply {
                setOnLoadCompleteListener { _, sampleId, status ->
                    if (status == 0) {
                        if (sampleId == beadClickSoundId) isBeadLoaded = true
                        if (sampleId == roundChimeSoundId) isChimeLoaded = true
                    }
                }
            }

        try {
            beadClickSoundId = soundPool?.load(context, R.raw.bead_click, 1) ?: 0
            roundChimeSoundId = soundPool?.load(context, R.raw.round_chime, 1) ?: 0
        } catch (_: Exception) {}
    }

    fun playBeadClick() {
        triggerTickHaptic()
        if (soundPool != null && beadClickSoundId != 0) {
            // Subtle pitch variation between 0.97f and 1.03f simulates physical bead string travel
            val pitch = 0.97f + (Math.random().toFloat() * 0.06f)
            soundPool?.play(beadClickSoundId, 1.0f, 1.0f, 1, 0, pitch)
        }
    }

    fun playRoundChime() {
        triggerCelebrationHaptic()
        if (soundPool != null && roundChimeSoundId != 0) {
            soundPool?.play(roundChimeSoundId, 1.0f, 1.0f, 2, 0, 1.0f)
        }
    }

    fun triggerMilestoneDoublePulse() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 60, 50, 120)
                val amplitudes = intArrayOf(0, 220, 0, 255)
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 60, 50, 120), -1)
            }
        } catch (_: Exception) {}
    }

    private fun triggerTickHaptic() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(15)
            }
        } catch (_: Exception) {}
    }

    private fun triggerCelebrationHaptic() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 60, 50, 60, 50, 120)
                val amplitudes = intArrayOf(0, 180, 0, 200, 0, 255)
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 60, 50, 60, 50, 120), -1)
            }
        } catch (_: Exception) {}
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}
