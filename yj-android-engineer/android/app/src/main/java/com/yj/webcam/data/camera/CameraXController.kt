package com.yj.webcam.data.camera

import android.content.Context
import android.util.Size
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.yj.webcam.core.result.Result
import com.yj.webcam.domain.model.CameraConfig
import com.yj.webcam.domain.model.FrameData
import com.yj.webcam.domain.model.LensFacing
import com.yj.webcam.domain.model.VideoCodec
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CameraXController @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _cameraConfig = MutableStateFlow(CameraConfig())
    val cameraConfig: StateFlow<CameraConfig> = _cameraConfig.asStateFlow()

    private val _frameFlow = MutableSharedFlow<FrameData>(
        extraBufferCapacity = 60,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val frameFlow: SharedFlow<FrameData> = _frameFlow.asSharedFlow()

    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private val executor = Executors.newSingleThreadExecutor()
    private var sequenceCounter = 0L

    suspend fun startCamera(lifecycleOwner: LifecycleOwner, config: CameraConfig): Result<Unit> {
        return try {
            _cameraConfig.value = config
            val providerFuture = ProcessCameraProvider.getInstance(context)
            cameraProvider = providerFuture.get()

            val cameraSelector = when (config.lensFacing) {
                LensFacing.FRONT -> CameraSelector.DEFAULT_FRONT_CAMERA
                LensFacing.BACK_MAIN -> CameraSelector.DEFAULT_BACK_CAMERA
                LensFacing.BACK_ULTRA_WIDE, LensFacing.BACK_TELEPHOTO -> CameraSelector.DEFAULT_BACK_CAMERA
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(config.resolution.width, config.resolution.height))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(executor) { imageProxy ->
                processImageProxy(imageProxy)
            }

            cameraProvider?.unbindAll()
            camera = cameraProvider?.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                imageAnalysis
            )

            setZoomRatio(config.zoomRatio)
            toggleFlash(config.isFlashEnabled)
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize CameraX")
            Result.Error(e, "Camera initialization failed: ${e.message}")
        }
    }

    private fun processImageProxy(imageProxy: ImageProxy) {
        val plane = imageProxy.planes[0]
        val buffer = plane.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        val frame = FrameData(
            data = bytes,
            width = imageProxy.width,
            height = imageProxy.height,
            timestampUs = imageProxy.info.timestamp / 1000,
            sequenceNumber = ++sequenceCounter,
            isKeyframe = (sequenceCounter % 30 == 0L),
            codec = VideoCodec.H264
        )
        _frameFlow.tryEmit(frame)
        imageProxy.close()
    }

    fun setZoomRatio(ratio: Float) {
        camera?.cameraControl?.setZoomRatio(ratio)
        _cameraConfig.value = _cameraConfig.value.copy(zoomRatio = ratio)
    }

    fun toggleFlash(enabled: Boolean) {
        camera?.cameraControl?.enableTorch(enabled)
        _cameraConfig.value = _cameraConfig.value.copy(isFlashEnabled = enabled)
    }

    fun setFocusAndExposure(x: Float, y: Float) {
        val factory = SurfaceOrientedMeteringPointFactory(1.0f, 1.0f)
        val point = factory.createPoint(x, y)
        val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE)
            .setAutoCancelDuration(3, TimeUnit.SECONDS)
            .build()
        camera?.cameraControl?.startFocusAndMetering(action)
    }

    fun stopCamera() {
        cameraProvider?.unbindAll()
        camera = null
    }
}
