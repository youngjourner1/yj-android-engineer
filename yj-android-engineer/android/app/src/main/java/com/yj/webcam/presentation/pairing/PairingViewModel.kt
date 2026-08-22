package com.yj.webcam.presentation.pairing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yj.webcam.domain.model.ConnectionInfo
import com.yj.webcam.domain.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PairingViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository
) : ViewModel() {

    private val _pairedDevices = MutableStateFlow<List<ConnectionInfo>>(emptyList())
    val pairedDevices: StateFlow<List<ConnectionInfo>> = _pairedDevices.asStateFlow()

    init {
        viewModelScope.launch {
            deviceRepository.pairedDevices.collect { list ->
                _pairedDevices.value = list
            }
        }
    }

    fun pairWithPayload(qrCode: String) {
        viewModelScope.launch {
            deviceRepository.pairDevice(qrCode)
        }
    }
}
