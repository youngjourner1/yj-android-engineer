package com.yj.webcam.data.color

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream

class CubeLutParserTest {

    @Test
    fun parse_valid3dCubeLut_returnsCorrectLutModel() {
        val sampleCubeContent = """
            # Standard 3D LUT Test File
            TITLE "Cinematic Test LUT"
            LUT_3D_SIZE 2
            DOMAIN_MIN 0.0 0.0 0.0
            DOMAIN_MAX 1.0 1.0 1.0
            
            0.0 0.0 0.0
            1.0 0.0 0.0
            0.0 1.0 0.0
            1.0 1.0 0.0
            0.0 0.0 1.0
            1.0 0.0 1.0
            0.0 1.0 1.0
            1.0 1.0 1.0
        """.trimIndent()

        val inputStream = ByteArrayInputStream(sampleCubeContent.toByteArray())
        val lut = CubeLutParser.parse(inputStream, defaultTitle = "Fallback")

        assertEquals("Cinematic Test LUT", lut.title)
        assertEquals(2, lut.size)
        assertEquals(24, lut.data.size) // 2 * 2 * 2 * 3 = 24 floats
        assertEquals(0.0f, lut.data[0], 0.001f)
        assertEquals(1.0f, lut.data[23], 0.001f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun parse_invalidLutSize_throwsException() {
        val invalidContent = """
            TITLE "Invalid LUT"
            LUT_3D_SIZE 100
        """.trimIndent()

        val inputStream = ByteArrayInputStream(invalidContent.toByteArray())
        CubeLutParser.parse(inputStream)
    }

    @Test
    fun parse_handlesNanAndInfinityValues() {
        val contentWithNan = """
            TITLE "NaN Test"
            LUT_3D_SIZE 2
            
            NaN Infinity -1.0
            0.5 0.5 0.5
            0.0 0.0 0.0
            1.0 1.0 1.0
            0.0 0.0 0.0
            1.0 1.0 1.0
            0.0 0.0 0.0
            1.0 1.0 1.0
        """.trimIndent()

        val inputStream = ByteArrayInputStream(contentWithNan.toByteArray())
        val lut = CubeLutParser.parse(inputStream)

        assertNotNull(lut)
        // NaN should be coerced to 0f, Infinity coerced to 10f then clamped
        assertEquals(0.0f, lut.data[0], 0.001f)
    }
}
