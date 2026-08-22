package com.yj.webcam.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yj.webcam.domain.model.VideoResolution
import com.yj.webcam.domain.repository.AppSettings
import com.yj.webcam.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { appSettings ->
                _settings.value = appSettings
            }
        }
    }

    fun updateResolution(resolution: VideoResolution) {
        viewModelScope.launch { settingsRepository.updateDefaultResolution(resolution) }
    }

    fun updateFps(fps: Int) {
        viewModelScope.launch { settingsRepository.updateDefaultFps(fps) }
    }

    fun updateDeviceName(name: String) {
        viewModelScope.launch { settingsRepository.updateDeviceName(name) }
    }
}
