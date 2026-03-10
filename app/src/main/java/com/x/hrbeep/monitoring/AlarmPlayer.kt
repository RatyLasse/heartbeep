package com.x.hrbeep.monitoring

import android.media.AudioManager
import android.media.ToneGenerator

class AlarmPlayer {
    private val generators = mutableMapOf<AlarmSoundStyle, ToneGenerator>()
    private val generatorVolumes = mutableMapOf<AlarmSoundStyle, Int>()

    @Synchronized
    fun beep(style: AlarmSoundStyle, intensity: Int) {
        val clampedIntensity = intensity.coerceIn(0, 100)
        val effectiveVolume = (style.volume * (clampedIntensity / 100f))
            .toInt()
            .coerceIn(0, 100)

        val generator = generators[style]
        val activeGenerator = if (generator != null && generatorVolumes[style] == effectiveVolume) {
            generator
        } else {
            generator?.release()
            ToneGenerator(AudioManager.STREAM_MUSIC, effectiveVolume).also {
                generators[style] = it
                generatorVolumes[style] = effectiveVolume
            }
        }
        activeGenerator.startTone(style.toneCode, style.durationMs)
    }

    @Synchronized
    fun release() {
        generators.values.forEach(ToneGenerator::release)
        generators.clear()
        generatorVolumes.clear()
    }
}
