package com.yj.webcam.data.repository

import com.yj.webcam.core.result.Result
import com.yj.webcam.data.audio.AudioEncoderManager
import com.yj.webcam.domain.model.AudioConfig
import com.yj.webcam.domain.repository.AudioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioRepositoryImpl @Inject constructor(
    private val audioEncoderManager: AudioEncoderManager
) : AudioRepository {

    private val _audioConfig = MutableStateFlow(AudioConfig())
    override val audioConfig: Flow<AudioConfig> = _audioConfig.asStateFlow()

    override val pcmAudioData: Flow<ByteArray>
        get() = audioEncoderManager.audioFlow

    override suspend fun startAudioCapture(config: AudioConfig): Result<Unit> {
        _audioConfig.value = config
        return audioEncoderManager.startCapture(config)
    }

    override suspend fun stopAudioCapture(): Result<Unit> {
        audioEncoderManager.stopCapture()
        return Result.Success(Unit)
    }

    override suspend fun updateAudioConfig(config: AudioConfig): Result<Unit> {
        _audioConfig.value = config
        return Result.Success(Unit)
    }
}
