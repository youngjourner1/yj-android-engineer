package com.yj.webcam.domain.model

data class StreamStats(
    val currentFps: Float = 0.0f,
    val targetFps: Int = 60,
    val bitrateKbps: Long = 0,
    val latencyMs: Float = 0.0f,
    val droppedFrames: Long = 0,
    val totalFrames: Long = 0,
    val batteryLevel: Int = 100,
    val thermalStatus: String = "NORMAL",
    val cpuUsagePercent: Float = 0.0f
)
