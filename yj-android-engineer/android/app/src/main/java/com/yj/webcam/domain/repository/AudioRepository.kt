package com.yj.webcam.domain.repository

import com.yj.webcam.core.result.Result
import com.yj.webcam.domain.model.AudioConfig
import kotlinx.coroutines.flow.Flow

interface AudioRepository {
    val audioConfig: Flow<AudioConfig>
    val pcmAudioData: Flow<ByteArray>

    suspend fun startAudioCapture(config: AudioConfig): Result<Unit>
    suspend fun stopAudioCapture(): Result<Unit>
    suspend fun updateAudioConfig(config: AudioConfig): Result<Unit>
}
