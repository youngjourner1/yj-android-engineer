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
