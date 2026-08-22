package com.yj.webcam.data.color

import android.opengl.GLES20
import android.opengl.GLES30
import com.yj.webcam.domain.model.ColorAdjustment
import com.yj.webcam.domain.model.LutModel
import com.yj.webcam.domain.model.ScopeData
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Real-time OpenGL ES Color Engine for camera video processing & 3D LUT filtering.
 */
class GlColorEngine {

    private var programHandle: Int = 0
    private var lutTextureId: Int = 0
    private var activeLutSize: Int = 0

    // Vertex & Texture Coordinates
    private val squareVertices = floatArrayOf(
        -1.0f, -1.0f,
         1.0f, -1.0f,
        -1.0f,  1.0f,
         1.0f,  1.0f
    )

    private val textureVertices = floatArrayOf(
        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f
    )

    private val vertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(squareVertices.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .apply {
            put(squareVertices)
            position(0)
        }

    private val textureBuffer: FloatBuffer = ByteBuffer.allocateDirect(textureVertices.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .apply {
            put(textureVertices)
            position(0)
        }

    fun init() {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE)

        programHandle = GLES20.glCreateProgram().also { program ->
            GLES20.glAttachShader(program, vertexShader)
            GLES20.glAttachShader(program, fragmentShader)
            GLES20.glLinkProgram(program)
        }

        // Generate 3D LUT Texture ID
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        lutTextureId = textures[0]

        Timber.d("GlColorEngine initialized successfully with program ID: $programHandle")
    }

    fun updateLut(lut: LutModel?) {
        if (lut == null) {
            activeLutSize = 0
            return
        }

        activeLutSize = lut.size
        val dataBuffer = ByteBuffer.allocateDirect(lut.data.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(lut.data)
                position(0)
            }

        GLES30.glBindTexture(GLES30.GL_TEXTURE_3D, lutTextureId)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_3D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_3D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_3D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_3D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_3D, GLES30.GL_TEXTURE_WRAP_R, GLES30.GL_CLAMP_TO_EDGE)

        GLES30.glTexImage3D(
            GLES30.GL_TEXTURE_3D, 0, GLES30.GL_RGB16F,
            lut.size, lut.size, lut.size, 0,
            GLES30.GL_RGB, GLES30.GL_FLOAT, dataBuffer
        )
    }

    fun processFrame(inputTextureId: Int, adjustment: ColorAdjustment, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        GLES20.glUseProgram(programHandle)

        val positionHandle = GLES20.glGetAttribLocation(programHandle, "aPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 8, vertexBuffer)

        val texCoordHandle = GLES20.glGetAttribLocation(programHandle, "aTexCoord")
        GLES20.glEnableVertexAttribArray(texCoordHandle)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 8, textureBuffer)

        // Bind Base Camera Texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, inputTextureId)
        GLES20.glUniform1i(GLES20.glGetUniformLocation(programHandle, "uInputTexture"), 0)

        // Bind 3D LUT Texture
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_3D, lutTextureId)
        GLES20.glUniform1i(GLES20.glGetUniformLocation(programHandle, "uLutTexture"), 1)

        // Set Shader Uniforms
        GLES20.glUniform1f(GLES20.glGetUniformLocation(programHandle, "uExposure"), adjustment.exposure)
        GLES20.glUniform1f(GLES20.glGetUniformLocation(programHandle, "uBrightness"), adjustment.brightness)
        GLES20.glUniform1f(GLES20.glGetUniformLocation(programHandle, "uContrast"), adjustment.contrast)
        GLES20.glUniform1f(GLES20.glGetUniformLocation(programHandle, "uSaturation"), adjustment.saturation)
        GLES20.glUniform1f(GLES20.glGetUniformLocation(programHandle, "uTemperature"), adjustment.temperature)
        GLES20.glUniform1f(GLES20.glGetUniformLocation(programHandle, "uTint"), adjustment.tint)
        GLES20.glUniform1f(GLES20.glGetUniformLocation(programHandle, "uGamma"), adjustment.gamma)
        GLES20.glUniform1f(GLES20.glGetUniformLocation(programHandle, "uLutIntensity"), adjustment.lutIntensity)
        GLES20.glUniform1f(GLES20.glGetUniformLocation(programHandle, "uLutSize"), activeLutSize.toFloat())
        GLES20.glUniform1f(GLES20.glGetUniformLocation(programHandle, "uVignette"), adjustment.vignette)
        GLES20.glUniform1f(GLES20.glGetUniformLocation(programHandle, "uGrain"), adjustment.filmGrain)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }

    fun computeScopes(pixelBuffer: ByteBuffer, width: Int, height: Int): ScopeData {
        val lumaHist = IntArray(256)
        val redHist = IntArray(256)
        val greenHist = IntArray(256)
        val blueHist = IntArray(256)

        val totalPixels = width * height
        pixelBuffer.rewind()

        val step = (totalPixels / 2000).coerceAtLeast(1) // Sample subset for high performance
        var index = 0
        while (pixelBuffer.hasRemaining() && index < totalPixels * 4) {
            val r = (pixelBuffer.get().toInt() and 0xFF)
            val g = (pixelBuffer.get().toInt() and 0xFF)
            val b = (pixelBuffer.get().toInt() and 0xFF)
            pixelBuffer.get() // Skip Alpha

            redHist[r]++
            greenHist[g]++
            blueHist[b]++

            val luma = (0.299 * r + 0.587 * g + 0.114 * b).toInt().coerceIn(0, 255)
            lumaHist[luma]++

            index += step * 4
        }

        return ScopeData(
            histogramLuma = lumaHist,
            histogramRed = redHist,
            histogramGreen = greenHist,
            histogramBlue = blueHist
        )
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    companion object {
        private const val VERTEX_SHADER_CODE = """
            attribute vec4 aPosition;
            attribute vec2 aTexCoord;
            varying vec2 vTexCoord;
            void main() {
                gl_Position = aPosition;
                vTexCoord = aTexCoord;
            }
        """

        private const val FRAGMENT_SHADER_CODE = """
            #extension GL_OES_texture_3D : enable
            precision mediump float;
            varying vec2 vTexCoord;

            uniform sampler2D uInputTexture;
            uniform sampler3D uLutTexture;

            uniform float uExposure;
            uniform float uBrightness;
            uniform float uContrast;
            uniform float uSaturation;
            uniform float uTemperature;
            uniform float uTint;
            uniform float uGamma;
            uniform float uLutIntensity;
            uniform float uLutSize;
            uniform float uVignette;
            uniform float uGrain;

            void main() {
                vec4 color = texture2D(uInputTexture, vTexCoord);

                // Exposure & Brightness
                color.rgb *= pow(2.0, uExposure);
                color.rgb += uBrightness;

                // Contrast
                color.rgb = (color.rgb - 0.5) * uContrast + 0.5;

                // Temperature & Tint
                color.r += uTemperature * 0.1;
                color.b -= uTemperature * 0.1;
                color.g += uTint * 0.1;

                // Saturation
                float luma = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
                color.rgb = mix(vec3(luma), color.rgb, uSaturation);

                // Gamma
                color.rgb = pow(max(color.rgb, vec3(0.0)), vec3(1.0 / uGamma));

                // 3D LUT Lookup & Intensity Blending
                if (uLutSize > 0.0 && uLutIntensity > 0.0) {
                    vec3 lutCoord = color.rgb * ((uLutSize - 1.0) / uLutSize) + (0.5 / uLutSize);
                    vec3 lutColor = texture3D(uLutTexture, lutCoord).rgb;
                    color.rgb = mix(color.rgb, lutColor, uLutIntensity);
                }

                // Vignette Effect
                if (uVignette > 0.0) {
                    float dist = distance(vTexCoord, vec2(0.5));
                    color.rgb *= smoothstep(0.8, 0.8 - uVignette * 0.4, dist);
                }

                gl_FragColor = vec4(clamp(color.rgb, 0.0, 1.0), color.a);
            }
        """
    }
}
