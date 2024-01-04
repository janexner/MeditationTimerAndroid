package com.exner.tools.meditationtimer.audio

import android.os.VibrationEffect
import android.os.Vibrator
import com.exner.tools.meditationtimer.audio.SoundIDs.SOUND_ID_INTERVAL
import com.exner.tools.meditationtimer.audio.SoundIDs.SOUND_ID_PROCESS_END
import com.exner.tools.meditationtimer.audio.SoundIDs.SOUND_ID_PROCESS_START

object VibratorHolder {
    private lateinit var vibrator: Vibrator

    fun initialise(newVibrator: Vibrator) {
        vibrator = newVibrator
    }

    fun vibrate(soundID: Long) {
        val pattern = when (soundID) {
            SOUND_ID_PROCESS_START -> {
                longArrayOf(50L, 100L, 50L, 200L, 50L, 300L)
            }
            SOUND_ID_PROCESS_END -> {
                longArrayOf(50L, 300L, 50L, 200L, 50L, 100L)
            }
            SOUND_ID_INTERVAL -> {
                longArrayOf(50L, 200L, 50L, 200L)
            }
            else -> {
                // should never happen, really!
                longArrayOf()
            }
        }
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
    }
}