package com.yj.webcam.data.color

import com.yj.webcam.domain.model.LutModel
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.UUID

/**
 * High-performance parser and validator for .cube 3D LUT files.
 */
object CubeLutParser {

    private const val MAX_LUT_SIZE = 65
    private const val MIN_LUT_SIZE = 2

    @Throws(IllegalArgumentException::class)
    fun parse(inputStream: InputStream, defaultTitle: String = "Custom LUT", filePath: String? = null): LutModel {
        val reader = BufferedReader(InputStreamReader(inputStream))
        var title = defaultTitle
        var lutSize = 0
        var domainMin = floatArrayOf(0f, 0f, 0f)
        var domainMax = floatArrayOf(1f, 1f, 1f)

        val rawFloats = ArrayList<Float>(33 * 33 * 33 * 3)

        reader.useLines { lines ->
            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.isEmpty() || trimmed.startsWith("#")) continue

                when {
                    trimmed.startsWith("TITLE", ignoreCase = true) -> {
                        val parts = trimmed.split(Regex("\"|\\s+"), limit = 2)
                        if (parts.size > 1) {
                            title = parts[1].replace("\"", "").trim()
                        }
                    }
                    trimmed.startsWith("LUT_3D_SIZE", ignoreCase = true) -> {
                        val parts = trimmed.split(Regex("\\s+"))
                        if (parts.size >= 2) {
                            lutSize = parts[1].toIntOrNull() ?: 0
                        }
                    }
                    trimmed.startsWith("DOMAIN_MIN", ignoreCase = true) -> {
                        val parts = trimmed.split(Regex("\\s+"))
                        if (parts.size >= 4) {
                            domainMin = floatArrayOf(
                                parts[1].toFloatOrNull() ?: 0f,
                                parts[2].toFloatOrNull() ?: 0f,
                                parts[3].toFloatOrNull() ?: 0f
                            )
                        }
                    }
                    trimmed.startsWith("DOMAIN_MAX", ignoreCase = true) -> {
                        val parts = trimmed.split(Regex("\\s+"))
                        if (parts.size >= 4) {
                            domainMax = floatArrayOf(
                                parts[1].toFloatOrNull() ?: 1f,
                                parts[2].toFloatOrNull() ?: 1f,
                                parts[3].toFloatOrNull() ?: 1f
                            )
                        }
                    }
                    else -> {
                        // Numeric RGB data row
                        val parts = trimmed.split(Regex("\\s+"))
                        if (parts.size >= 3) {
                            val r = parts[0].toFloatOrNull()
                            val g = parts[1].toFloatOrNull()
                            val b = parts[2].toFloatOrNull()

                            if (r != null && g != null && b != null) {
                                // Safeguard against NaN / Infinity
                                val cleanR = if (r.isNaN() || r.isInfinite()) 0f else r.coerceIn(0f, 10f)
                                val cleanG = if (g.isNaN() || g.isInfinite()) 0f else g.coerceIn(0f, 10f)
                                val cleanB = if (b.isNaN() || b.isInfinite()) 0f else b.coerceIn(0f, 10f)

                                rawFloats.add(cleanR)
                                rawFloats.add(cleanG)
                                rawFloats.add(cleanB)
                            }
                        }
                    }
                }
            }
        }

        // Infer size if not explicitly declared in header
        if (lutSize == 0) {
            val totalRGB = rawFloats.size / 3
            val cubeRoot = Math.cbrt(totalRGB.toDouble()).toInt()
            if (cubeRoot * cubeRoot * cubeRoot == totalRGB) {
                lutSize = cubeRoot
            }
        }

        require(lutSize in MIN_LUT_SIZE..MAX_LUT_SIZE) {
            "Invalid LUT dimension size: $lutSize. Supported range is 2..65."
        }

        val expectedElements = lutSize * lutSize * lutSize * 3
        require(rawFloats.size >= expectedElements) {
            "Corrupted .cube file: Expected $expectedElements color samples, found ${rawFloats.size}."
        }

        val floatData = FloatArray(expectedElements)
        for (i in 0 until expectedElements) {
            floatData[i] = rawFloats[i]
        }

        return LutModel(
            id = UUID.randomUUID().toString(),
            title = title,
            size = lutSize,
            domainMin = domainMin,
            domainMax = domainMax,
            data = floatData,
            filePath = filePath
        )
    }
}
