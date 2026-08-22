package com.yj.webcam.domain.repository

import com.yj.webcam.core.result.Result
import com.yj.webcam.domain.model.ConnectionInfo
import com.yj.webcam.domain.model.FrameData
import com.yj.webcam.domain.model.StreamStats
import kotlinx.coroutines.flow.Flow

interface StreamingRepository {
    val connectionState: Flow<ConnectionInfo>
    val streamStats: Flow<StreamStats>

    suspend fun startServer(port: Int): Result<Unit>
    suspend fun stopServer(): Result<Unit>
    suspend fun sendVideoFrame(frameData: FrameData): Result<Unit>
    suspend fun sendAudioData(audioBytes: ByteArray, timestampUs: Long): Result<Unit>
}
