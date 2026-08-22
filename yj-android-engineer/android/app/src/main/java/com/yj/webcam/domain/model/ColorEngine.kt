package com.yj.webcam.domain.model

import android.graphics.Color

/**
 * Product tier entitlement state for JY Webcam.
 */
enum class TierState {
    FREE,
    PRO_MONTHLY,
    PRO_YEARLY,
    PRO_LIFETIME
}

/**
 * Processing mode selection: Phone processing vs Desktop PC processing.
 */
enum class ProcessingMode {
    MOBILE_MODE,
    DESKTOP_MODE
}

/**
 * 3D LUT Data representation parsed from .cube files.
 */
data class LutModel(
    val id: String,
    val title: String,
    val size: Int, // 17, 33, 65
    val domainMin: FloatArray = floatArrayOf(0f, 0f, 0f),
    val domainMax: FloatArray = floatArrayOf(1f, 1f, 1f),
    val data: FloatArray, // Size: size * size * size * 3
    val isBuiltIn: Boolean = false,
    val category: String = "General",
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val filePath: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LutModel

        if (id != other.id) return false
        if (title != other.title) return false
        if (size != other.size) return false
        if (!domainMin.contentEquals(other.domainMin)) return false
        if (!domainMax.contentEquals(other.domainMax)) return false
        if (!data.contentEquals(other.data)) return false
        if (isBuiltIn != other.isBuiltIn) return false
        if (category != other.category) return false
        if (tags != other.tags) return false
        if (isFavorite != other.isFavorite) return false
        if (filePath != other.filePath) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + size
        result = 31 * result + domainMin.contentHashCode()
        result = 31 * result + domainMax.contentHashCode()
        result = 31 * result + data.contentHashCode()
        result = 31 * result + isBuiltIn.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + tags.hashCode()
        result = 31 * result + isFavorite.hashCode()
        result = 31 * result + (filePath?.hashCode() ?: 0)
        return result
    }
}

/**
 * Professional Color Grading parameters.
 */
data class ColorAdjustment(
    // Basic Controls
    val exposure: Float = 0.0f,     // -3.0 to +3.0 EV
    val brightness: Float = 0.0f,   // -1.0 to +1.0
    val contrast: Float = 1.0f,     // 0.5 to 2.0
    val saturation: Float = 1.0f,   // 0.0 to 2.0
    val vibrance: Float = 0.0f,     // -1.0 to +1.0
    val temperature: Float = 0.0f,  // -1.0 to +1.0 (Cool to Warm)
    val tint: Float = 0.0f,         // -1.0 to +1.0 (Green to Magenta)
    val gamma: Float = 1.0f,        // 0.2 to 3.0

    // Tonal Controls
    val blacks: Float = 0.0f,       // -1.0 to +1.0
    val shadows: Float = 0.0f,      // -1.0 to +1.0
    val midtones: Float = 0.0f,     // -1.0 to +1.0
    val highlights: Float = 0.0f,   // -1.0 to +1.0
    val whites: Float = 0.0f,       // -1.0 to +1.0

    // Professional Color Balance (Lift, Gamma, Gain, Offset)
    val liftR: Float = 0f, val liftG: Float = 0f, val liftB: Float = 0f,
    val gainR: Float = 1f, val gainG: Float = 1f, val gainB: Float = 1f,
    val offsetR: Float = 0f, val offsetG: Float = 0f, val offsetB: Float = 0f,

    // Active LUT
    val selectedLutId: String? = null,
    val lutIntensity: Float = 1.0f,  // 0.0 (0%) to 1.0 (100%)

    // Secondary Effects
    val skinSmoothing: Float = 0.0f,
    val sharpening: Float = 0.0f,
    val vignette: Float = 0.0f,
    val filmGrain: Float = 0.0f,
    val bloom: Float = 0.0f
)

/**
 * Saved User Color Preset.
 */
data class ColorPreset(
    val id: String,
    val name: String,
    val adjustment: ColorAdjustment,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Video Scope Data computed from live video frames.
 */
data class ScopeData(
    val histogramLuma: IntArray = IntArray(256),
    val histogramRed: IntArray = IntArray(256),
    val histogramGreen: IntArray = IntArray(256),
    val histogramBlue: IntArray = IntArray(256),
    val waveform: Array<IntArray> = Array(64) { IntArray(256) },
    val vectorscope: Array<IntArray> = Array(64) { IntArray(64) }
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScopeData

        if (!histogramLuma.contentEquals(other.histogramLuma)) return false
        if (!histogramRed.contentEquals(other.histogramRed)) return false
        if (!histogramGreen.contentEquals(other.histogramGreen)) return false
        if (!histogramBlue.contentEquals(other.histogramBlue)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = histogramLuma.contentHashCode()
        result = 31 * result + histogramRed.contentHashCode()
        result = 31 * result + histogramGreen.contentHashCode()
        result = 31 * result + histogramBlue.contentHashCode()
        return result
    }
}
