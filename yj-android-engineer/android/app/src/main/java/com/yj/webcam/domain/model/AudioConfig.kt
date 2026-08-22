package com.yj.webcam.domain.model

data class AudioConfig(
    val isEnabled: Boolean = true,
    val sampleRate: Int = 48000,
    val channels: Int = 2,
    val isNoiseSuppressionEnabled: Boolean = true,
    val isEchoCancellationEnabled: Boolean = true,
    val isGainControlEnabled: Boolean = true,
    val gainFactor: Float = 1.0f,
    val audioDelayMs: Int = 0
)

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
