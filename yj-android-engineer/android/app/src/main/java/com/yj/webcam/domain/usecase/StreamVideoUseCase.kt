package com.yj.webcam.domain.usecase

import com.yj.webcam.core.result.Result
import com.yj.webcam.domain.model.CameraConfig
import com.yj.webcam.domain.repository.CameraRepository
import com.yj.webcam.domain.repository.StreamingRepository
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class StreamVideoUseCase @Inject constructor(
    private val cameraRepository: CameraRepository,
    private val streamingRepository: StreamingRepository
) {
    suspend fun startStreaming(config: CameraConfig, port: Int = 9876): Result<Unit> {
        val serverResult = streamingRepository.startServer(port)
        if (serverResult.isError) return serverResult

        val cameraResult = cameraRepository.startCamera(config)
        if (cameraResult.isError) {
            streamingRepository.stopServer()
            return cameraResult
        }

        return Result.Success(Unit)
    }

    suspend fun stopStreaming(): Result<Unit> {
        cameraRepository.stopCamera()
        streamingRepository.stopServer()
        return Result.Success(Unit)
    }
}
