package com.x.hrbeep.monitoring

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class AlarmPlayer(
    context: Context,
) {
    enum class BeepProfile {
        AboveUpperBound,
        BelowLowerBound,
    }

    private companion object {
        const val UPPER_BOUND_TONE_CODE = ToneGenerator.TONE_CDMA_PIP
        const val LOWER_BOUND_TONE_CODE = ToneGenerator.TONE_CDMA_LOW_L
        const val TONE_DURATION_MS = 110
        const val BASE_VOLUME = 100
        const val EXTRA_DUCK_MS = 350L
        const val SPEECH_FOCUS_DURATION_MS = 2_500
    }

    private val appContext = context.applicationContext
    private val audioManager = context.getSystemService(AudioManager::class.java)
    private var generator: ToneGenerator? = null
    private var generatorVolume: Int? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTextToSpeechReady = false
    private var isInitializingTextToSpeech = false
    private val pendingSpeech = ArrayDeque<String>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var abandonFocusJob: Job? = null
    private var persistentDucking = false

    private val audioFocusRequest: AudioFocusRequest? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAcceptsDelayedFocusGain(false)
                .setWillPauseWhenDucked(false)
                .build()
        } else {
            null
    }

    @Synchronized
    fun beep(
        intensity: Int,
        profile: BeepProfile = BeepProfile.AboveUpperBound,
    ) {
        val clampedIntensity = intensity.coerceIn(0, 100)
        val effectiveVolume = (BASE_VOLUME * (clampedIntensity / 100f))
            .toInt()
            .coerceIn(0, 100)
        if (!persistentDucking) {
            requestTransientAudioFocus(TONE_DURATION_MS)
        }

        val activeGenerator = if (generator != null && generatorVolume == effectiveVolume) {
            generator!!
        } else {
            generator?.release()
            ToneGenerator(AudioManager.STREAM_MUSIC, effectiveVolume).also {
                generator = it
                generatorVolume = effectiveVolume
            }
        }
        activeGenerator.startTone(toneCode(profile), TONE_DURATION_MS)
    }

    @Synchronized
    fun speak(message: String) {
        if (message.isBlank()) {
            return
        }

        if (!isTextToSpeechReady) {
            pendingSpeech += message
            initializeTextToSpeechIfNeeded()
            return
        }

        requestTransientAudioFocus(SPEECH_FOCUS_DURATION_MS)
        textToSpeech?.speak(message, TextToSpeech.QUEUE_ADD, null, "monitoring-${System.nanoTime()}")
    }

    @Synchronized
    fun setPersistentDucking(enabled: Boolean) {
        if (persistentDucking == enabled) {
            return
        }

        persistentDucking = enabled
        abandonFocusJob?.cancel()
        abandonFocusJob = null

        if (enabled) {
            requestAudioFocus()
        } else {
            abandonAudioFocus()
        }
    }

    @Synchronized
    fun release() {
        persistentDucking = false
        abandonFocusJob?.cancel()
        abandonAudioFocus()
        generator?.release()
        generator = null
        generatorVolume = null
        pendingSpeech.clear()
        isTextToSpeechReady = false
        isInitializingTextToSpeech = false
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
    }

    private fun requestTransientAudioFocus(durationMs: Int) {
        requestAudioFocus()

        if (persistentDucking) {
            return
        }

        abandonFocusJob?.cancel()
        abandonFocusJob = scope.launch {
            delay(durationMs.toLong() + EXTRA_DUCK_MS)
            if (!persistentDucking) {
                abandonAudioFocus()
            }
        }
    }

    private fun requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let(audioManager::requestAudioFocus)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                null,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK,
            )
        }
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let(audioManager::abandonAudioFocusRequest)
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(null)
        }
    }

    private fun initializeTextToSpeechIfNeeded() {
        if (isTextToSpeechReady || isInitializingTextToSpeech) {
            return
        }

        isInitializingTextToSpeech = true
        textToSpeech = TextToSpeech(appContext) { status ->
            synchronized(this) {
                isInitializingTextToSpeech = false
                if (status != TextToSpeech.SUCCESS) {
                    pendingSpeech.clear()
                    textToSpeech?.shutdown()
                    textToSpeech = null
                    return@synchronized
                }

                textToSpeech?.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                textToSpeech?.setLanguage(Locale.US)
                isTextToSpeechReady = true

                while (pendingSpeech.isNotEmpty()) {
                    requestTransientAudioFocus(SPEECH_FOCUS_DURATION_MS)
                    textToSpeech?.speak(
                        pendingSpeech.removeFirst(),
                        TextToSpeech.QUEUE_ADD,
                        null,
                        "monitoring-${System.nanoTime()}",
                    )
                }
            }
        }
    }

    private fun toneCode(profile: BeepProfile): Int = when (profile) {
        BeepProfile.AboveUpperBound -> UPPER_BOUND_TONE_CODE
        BeepProfile.BelowLowerBound -> LOWER_BOUND_TONE_CODE
    }
}
