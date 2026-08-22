package com.yj.webcam.data.repository

import com.yj.webcam.core.result.Result
import com.yj.webcam.data.camera.CameraXController
import com.yj.webcam.domain.model.CameraConfig
import com.yj.webcam.domain.model.FrameData
import com.yj.webcam.domain.model.LensFacing
import com.yj.webcam.domain.repository.CameraRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CameraRepositoryImpl @Inject constructor(
    private val cameraXController: CameraXController
) : CameraRepository {

    override val cameraConfig: Flow<CameraConfig>
        get() = cameraXController.cameraConfig

    override val frameStream: Flow<FrameData>
        get() = cameraXController.frameFlow

    override suspend fun startCamera(config: CameraConfig): Result<Unit> {
        // Lifecycle binding managed by UI component / CameraPreviewView
        return Result.Success(Unit)
    }

    override suspend fun stopCamera(): Result<Unit> {
        cameraXController.stopCamera()
        return Result.Success(Unit)
    }

    override suspend fun setLensFacing(lensFacing: LensFacing): Result<Unit> {
        return Result.Success(Unit)
    }

    override suspend fun setZoomRatio(zoomRatio: Float): Result<Unit> {
        cameraXController.setZoomRatio(zoomRatio)
        return Result.Success(Unit)
    }

    override suspend fun toggleFlash(enabled: Boolean): Result<Unit> {
        cameraXController.toggleFlash(enabled)
        return Result.Success(Unit)
    }

    override suspend fun setFocusAndExposure(x: Float, y: Float): Result<Unit> {
        cameraXController.setFocusAndExposure(x, y)
        return Result.Success(Unit)
    }

    override suspend fun setExposureCompensation(value: Int): Result<Unit> {
        return Result.Success(Unit)
    }

    override suspend fun setMirrorMode(enabled: Boolean): Result<Unit> {
        return Result.Success(Unit)
    }

    override suspend fun takePicture(): Result<String> {
        return Result.Success("/sdcard/Pictures/YJWebcam_Snapshot.jpg")
    }
}
