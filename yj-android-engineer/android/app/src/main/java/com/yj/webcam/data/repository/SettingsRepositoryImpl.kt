package com.yj.webcam.data.repository

import com.yj.webcam.data.datastore.SettingsDataStore
import com.yj.webcam.domain.model.VideoResolution
import com.yj.webcam.domain.repository.AppSettings
import com.yj.webcam.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : SettingsRepository {

    override val settings: Flow<AppSettings>
        get() = settingsDataStore.settingsFlow

    override suspend fun updateDeviceName(name: String) {
        settingsDataStore.setDeviceName(name)
    }

    override suspend fun updateDefaultResolution(resolution: VideoResolution) {
        settingsDataStore.setResolution(resolution)
    }

    override suspend fun updateDefaultFps(fps: Int) {
        settingsDataStore.setFps(fps)
    }

    override suspend fun updateTheme(isDark: Boolean) {
        settingsDataStore.setDarkTheme(isDark)
    }

    override suspend fun updateAiBlur(enabled: Boolean) {
        settingsDataStore.setAiBlur(enabled)
    }

    override suspend fun updateAiAutoFraming(enabled: Boolean) {
        settingsDataStore.setAiAutoFraming(enabled)
    }
}
