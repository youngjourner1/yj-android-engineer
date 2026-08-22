package com.yj.webcam.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.yj.webcam.domain.model.VideoResolution
import com.yj.webcam.domain.repository.AppSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "yj_webcam_settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val DEVICE_NAME = stringPreferencesKey("device_name")
    private val RESOLUTION = stringPreferencesKey("resolution")
    private val FPS = intPreferencesKey("fps")
    private val DARK_THEME = booleanPreferencesKey("dark_theme")
    private val AI_BLUR = booleanPreferencesKey("ai_blur")
    private val AI_AUTO_FRAMING = booleanPreferencesKey("ai_auto_framing")

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { preferences ->
        AppSettings(
            deviceName = preferences[DEVICE_NAME] ?: "Android YJ Webcam",
            defaultResolution = try {
                VideoResolution.valueOf(preferences[RESOLUTION] ?: "RES_1080P")
            } catch (e: Exception) {
                VideoResolution.RES_1080P
            },
            defaultFps = preferences[FPS] ?: 60,
            isDarkTheme = preferences[DARK_THEME] ?: true,
            isAiBlurEnabled = preferences[AI_BLUR] ?: false,
            isAiAutoFramingEnabled = preferences[AI_AUTO_FRAMING] ?: false
        )
    }

    suspend fun setDeviceName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[DEVICE_NAME] = name
        }
    }

    suspend fun setResolution(resolution: VideoResolution) {
        context.dataStore.edit { preferences ->
            preferences[RESOLUTION] = resolution.name
        }
    }

    suspend fun setFps(fps: Int) {
        context.dataStore.edit { preferences ->
            preferences[FPS] = fps
        }
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME] = isDark
        }
    }

    suspend fun setAiBlur(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AI_BLUR] = enabled
        }
    }

    suspend fun setAiAutoFraming(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AI_AUTO_FRAMING] = enabled
        }
    }
}
