package com.yj.webcam.presentation.color

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yj.webcam.domain.model.ColorAdjustment
import com.yj.webcam.domain.model.LutModel
import com.yj.webcam.domain.model.ProcessingMode
import com.yj.webcam.domain.model.ScopeData
import com.yj.webcam.domain.repository.LutRepository
import com.yj.webcam.domain.service.EntitlementManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

data class ColorEngineUiState(
    val adjustment: ColorAdjustment = ColorAdjustment(),
    val activeLut: LutModel? = null,
    val isBeforeAfterSplit: Boolean = false,
    val processingMode: ProcessingMode = ProcessingMode.MOBILE_MODE,
    val scopes: ScopeData = ScopeData(),
    val showScopes: Boolean = false,
    val showPaywall: Boolean = false
)

@HiltViewModel
class ColorEngineViewModel @Inject constructor(
    private val lutRepository: LutRepository,
    val entitlementManager: EntitlementManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ColorEngineUiState())
    val uiState: StateFlow<ColorEngineUiState> = _uiState.asStateFlow()

    val luts: StateFlow<List<LutModel>> = lutRepository.getLutsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateExposure(value: Float) {
        _uiState.value = _uiState.value.copy(
            adjustment = _uiState.value.adjustment.copy(exposure = value)
        )
    }

    fun updateContrast(value: Float) {
        _uiState.value = _uiState.value.copy(
            adjustment = _uiState.value.adjustment.copy(contrast = value)
        )
    }

    fun updateSaturation(value: Float) {
        _uiState.value = _uiState.value.copy(
            adjustment = _uiState.value.adjustment.copy(saturation = value)
        )
    }

    fun updateTemperature(value: Float) {
        _uiState.value = _uiState.value.copy(
            adjustment = _uiState.value.adjustment.copy(temperature = value)
        )
    }

    fun updateTint(value: Float) {
        _uiState.value = _uiState.value.copy(
            adjustment = _uiState.value.adjustment.copy(tint = value)
        )
    }

    fun updateLutIntensity(value: Float) {
        _uiState.value = _uiState.value.copy(
            adjustment = _uiState.value.adjustment.copy(lutIntensity = value)
        )
    }

    fun selectLut(lut: LutModel?) {
        if (lut != null && !lut.isBuiltIn && !entitlementManager.canUseUnlimitedLuts()) {
            _uiState.value = _uiState.value.copy(showPaywall = true)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                activeLut = lut,
                adjustment = _uiState.value.adjustment.copy(selectedLutId = lut?.id)
            )
        }
    }

    fun toggleBeforeAfterSplit() {
        _uiState.value = _uiState.value.copy(
            isBeforeAfterSplit = !_uiState.value.isBeforeAfterSplit
        )
    }

    fun toggleScopes() {
        if (!entitlementManager.canUseScopes()) {
            _uiState.value = _uiState.value.copy(showPaywall = true)
            return
        }
        _uiState.value = _uiState.value.copy(
            showScopes = !_uiState.value.showScopes
        )
    }

    fun setProcessingMode(mode: ProcessingMode) {
        _uiState.value = _uiState.value.copy(processingMode = mode)
    }

    fun importLutFile(inputStream: InputStream, filename: String) {
        if (!entitlementManager.canUseUnlimitedLuts()) {
            _uiState.value = _uiState.value.copy(showPaywall = true)
            return
        }

        viewModelScope.launch {
            lutRepository.importLut(inputStream, filename).onSuccess { imported ->
                selectLut(imported)
            }
        }
    }

    fun dismissPaywall() {
        _uiState.value = _uiState.value.copy(showPaywall = false)
    }

    fun resetAdjustments() {
        _uiState.value = _uiState.value.copy(
            adjustment = ColorAdjustment()
        )
    }
}
