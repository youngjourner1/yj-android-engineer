package com.yj.webcam.domain.model

enum class LensFacing {
    FRONT,
    BACK_MAIN,
    BACK_ULTRA_WIDE,
    BACK_TELEPHOTO
}

enum class VideoResolution(val width: Int, val height: Int) {
    RES_720P(1280, 720),
    RES_1080P(1920, 1080),
    RES_4K(3840, 2160)
}

data class CameraConfig(
    val lensFacing: LensFacing = LensFacing.BACK_MAIN,
    val resolution: VideoResolution = VideoResolution.RES_1080P,
    val targetFps: Int = 60,
    val zoomRatio: Float = 1.0f,
    val maxZoomRatio: Float = 10.0f,
    val isFlashEnabled: Boolean = false,
    val isHdrEnabled: Boolean = true,
    val isAutoExposureEnabled: Boolean = true,
    val exposureCompensation: Int = 0,
    val isMirrorModeEnabled: Boolean = false,
    val isPortraitMode: Boolean = false
)
