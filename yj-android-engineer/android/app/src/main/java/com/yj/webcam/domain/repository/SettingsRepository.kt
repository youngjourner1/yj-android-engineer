package com.yj.webcam.domain.repository

import com.yj.webcam.domain.model.CameraConfig
import com.yj.webcam.domain.model.VideoResolution
import kotlinx.coroutines.flow.Flow

data class AppSettings(
    val deviceName: String = "Android YJ Webcam",
    val defaultResolution: VideoResolution = VideoResolution.RES_1080P,
    val defaultFps: Int = 60,
    val isDarkTheme: Boolean = true,
    val isUsbModeAutoEnabled: Boolean = true,
    val isAiBlurEnabled: Boolean = false,
    val isAiAutoFramingEnabled: Boolean = false
)

interface SettingsRepository {
    val settings: Flow<AppSettings>

    suspend fun updateDeviceName(name: String)
    suspend fun updateDefaultResolution(resolution: VideoResolution)
    suspend fun updateDefaultFps(fps: Int)
    suspend fun updateTheme(isDark: Boolean)
    suspend fun updateAiBlur(enabled: Boolean)
    suspend fun updateAiAutoFraming(enabled: Boolean)
}
