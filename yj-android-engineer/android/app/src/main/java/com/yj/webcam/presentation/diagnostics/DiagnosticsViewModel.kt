package com.yj.webcam.presentation.diagnostics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yj.webcam.domain.model.StreamStats
import com.yj.webcam.domain.repository.StreamingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiagnosticsViewModel @Inject constructor(
    private val streamingRepository: StreamingRepository
) : ViewModel() {

    private val _stats = MutableStateFlow(StreamStats())
    val stats: StateFlow<StreamStats> = _stats.asStateFlow()

    init {
        viewModelScope.launch {
            streamingRepository.streamStats.collect { streamStats ->
                _stats.value = streamStats
            }
        }
    }
}
