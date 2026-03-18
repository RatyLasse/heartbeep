package com.x.hrbeep.data

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

class ThresholdRepository(
    private val context: Context,
) {
    val thresholdFlow: Flow<Int> = context.settingsDataStore.data.map { preferences ->
        preferences[KEY_THRESHOLD] ?: DEFAULT_THRESHOLD_BPM
    }

    val soundIntensityFlow: Flow<Int> = context.settingsDataStore.data.map { preferences ->
        preferences[KEY_SOUND_INTENSITY] ?: DEFAULT_SOUND_INTENSITY
    }

    suspend fun saveThreshold(value: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[KEY_THRESHOLD] = value
        }
    }

    suspend fun saveSoundIntensity(value: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[KEY_SOUND_INTENSITY] = value.coerceIn(0, 100)
        }
    }

    companion object {
        const val DEFAULT_THRESHOLD_BPM = 140
        const val DEFAULT_SOUND_INTENSITY = 80
        private val KEY_THRESHOLD = intPreferencesKey("threshold_bpm")
        private val KEY_SOUND_INTENSITY = intPreferencesKey("sound_intensity")
    }
}
