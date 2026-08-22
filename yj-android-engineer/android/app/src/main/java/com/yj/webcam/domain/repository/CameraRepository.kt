package com.yj.webcam.domain.repository

import com.yj.webcam.core.result.Result
import com.yj.webcam.domain.model.CameraConfig
import com.yj.webcam.domain.model.FrameData
import com.yj.webcam.domain.model.LensFacing
import kotlinx.coroutines.flow.Flow

interface CameraRepository {
    val cameraConfig: Flow<CameraConfig>
    val frameStream: Flow<FrameData>
    
    suspend fun startCamera(config: CameraConfig): Result<Unit>
    suspend fun stopCamera(): Result<Unit>
    suspend fun setLensFacing(lensFacing: LensFacing): Result<Unit>
    suspend fun setZoomRatio(zoomRatio: Float): Result<Unit>
    suspend fun toggleFlash(enabled: Boolean): Result<Unit>
    suspend fun setFocusAndExposure(x: Float, y: Float): Result<Unit>
    suspend fun setExposureCompensation(value: Int): Result<Unit>
    suspend fun setMirrorMode(enabled: Boolean): Result<Unit>
    suspend fun takePicture(): Result<String>
}
