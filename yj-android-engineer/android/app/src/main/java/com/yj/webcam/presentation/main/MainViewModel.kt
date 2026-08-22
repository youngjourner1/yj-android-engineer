package com.yj.webcam.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yj.webcam.domain.model.CameraConfig
import com.yj.webcam.domain.model.ConnectionInfo
import com.yj.webcam.domain.model.ConnectionStatus
import com.yj.webcam.domain.model.LensFacing
import com.yj.webcam.domain.model.StreamStats
import com.yj.webcam.domain.repository.CameraRepository
import com.yj.webcam.domain.repository.StreamingRepository
import com.yj.webcam.domain.usecase.StreamVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val connectionInfo: ConnectionInfo = ConnectionInfo(),
    val cameraConfig: CameraConfig = CameraConfig(),
    val stats: StreamStats = StreamStats(),
    val isStreamingActive: Boolean = false,
    val isFlashOn: Boolean = false,
    val isAiBlurActive: Boolean = false,
    val isAiAutoFramingActive: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val streamVideoUseCase: StreamVideoUseCase,
    private val cameraRepository: CameraRepository,
    private val streamingRepository: StreamingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            streamingRepository.connectionState.collect { connection ->
                _uiState.value = _uiState.value.copy(
                    connectionInfo = connection,
                    isStreamingActive = (connection.status == ConnectionStatus.STREAMING)
                )
            }
        }
        viewModelScope.launch {
            cameraRepository.cameraConfig.collect { config ->
                _uiState.value = _uiState.value.copy(cameraConfig = config)
            }
        }
        viewModelScope.launch {
            streamingRepository.streamStats.collect { stats ->
                _uiState.value = _uiState.value.copy(stats = stats)
            }
        }
    }

    fun toggleStreaming() {
        viewModelScope.launch {
            if (_uiState.value.isStreamingActive) {
                streamVideoUseCase.stopStreaming()
            } else {
                streamVideoUseCase.startStreaming(_uiState.value.cameraConfig)
            }
        }
    }

    fun switchCamera() {
        viewModelScope.launch {
            val currentLens = _uiState.value.cameraConfig.lensFacing
            val nextLens = when (currentLens) {
                LensFacing.BACK_MAIN -> LensFacing.FRONT
                LensFacing.FRONT -> LensFacing.BACK_ULTRA_WIDE
                LensFacing.BACK_ULTRA_WIDE -> LensFacing.BACK_TELEPHOTO
                LensFacing.BACK_TELEPHOTO -> LensFacing.BACK_MAIN
            }
            cameraRepository.setLensFacing(nextLens)
        }
    }

    fun toggleFlash() {
        viewModelScope.launch {
            val nextFlash = !_uiState.value.isFlashOn
            cameraRepository.toggleFlash(nextFlash)
            _uiState.value = _uiState.value.copy(isFlashOn = nextFlash)
        }
    }

    fun toggleAiBlur() {
        _uiState.value = _uiState.value.copy(isAiBlurActive = !_uiState.value.isAiBlurActive)
    }

    fun toggleAiAutoFraming() {
        _uiState.value = _uiState.value.copy(isAiAutoFramingActive = !_uiState.value.isAiAutoFramingActive)
    }
}
