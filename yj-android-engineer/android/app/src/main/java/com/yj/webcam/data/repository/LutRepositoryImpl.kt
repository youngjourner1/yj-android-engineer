package com.yj.webcam.data.repository

import android.content.Context
import com.yj.webcam.data.color.CubeLutParser
import com.yj.webcam.domain.model.LutModel
import com.yj.webcam.domain.repository.LutRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LutRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LutRepository {

    private val lutMap = LinkedHashMap<String, LutModel>()
    private val _lutsFlow = MutableStateFlow<List<LutModel>>(emptyList())

    init {
        // Initialize default built-in LUTs
        initBuiltInLuts()
    }

    private fun initBuiltInLuts() {
        val builtIns = createBuiltInLuts()
        builtIns.forEach { lutMap[it.id] = it }
        _lutsFlow.value = lutMap.values.toList()
    }

    override fun getLutsFlow(): Flow<List<LutModel>> = _lutsFlow.asStateFlow()

    override suspend fun getLutById(id: String): LutModel? = lutMap[id]

    override suspend fun importLut(inputStream: InputStream, name: String): Result<LutModel> {
        return runCatching {
            val lutDir = File(context.filesDir, "luts")
            if (!lutDir.exists()) lutDir.mkdirs()

            val lutFile = File(lutDir, "${System.currentTimeMillis()}_$name.cube")
            FileOutputStream(lutFile).use { out ->
                inputStream.copyTo(out)
            }

            val parsedLut = FileInputStream(lutFile).use { input ->
                CubeLutParser.parse(input, defaultTitle = name, filePath = lutFile.absolutePath)
            }

            lutMap[parsedLut.id] = parsedLut
            _lutsFlow.value = lutMap.values.toList()
            parsedLut
        }
    }

    override suspend fun deleteLut(id: String): Boolean {
        val lut = lutMap[id] ?: return false
        if (lut.isBuiltIn) return false // Built-ins cannot be deleted

        lut.filePath?.let { path ->
            File(path).delete()
        }

        lutMap.remove(id)
        _lutsFlow.value = lutMap.values.toList()
        return true
    }

    override suspend fun toggleFavorite(id: String): Boolean {
        val lut = lutMap[id] ?: return false
        val updated = lut.copy(isFavorite = !lut.isFavorite)
        lutMap[id] = updated
        _lutsFlow.value = lutMap.values.toList()
        return updated.isFavorite
    }

    override suspend fun renameLut(id: String, newTitle: String): Boolean {
        val lut = lutMap[id] ?: return false
        val updated = lut.copy(title = newTitle)
        lutMap[id] = updated
        _lutsFlow.value = lutMap.values.toList()
        return true
    }

    override suspend fun loadBuiltInLuts(): List<LutModel> = lutMap.values.filter { it.isBuiltIn }

    private fun createBuiltInLuts(): List<LutModel> {
        // Generates identity and cinematic standard default LUTs programmatically
        val size = 17
        val identityData = FloatArray(size * size * size * 3)
        var idx = 0
        for (b in 0 until size) {
            for (g in 0 until size) {
                for (r in 0 until size) {
                    identityData[idx++] = r.toFloat() / (size - 1)
                    identityData[idx++] = g.toFloat() / (size - 1)
                    identityData[idx++] = b.toFloat() / (size - 1)
                }
            }
        }

        val warmCinematicData = FloatArray(size * size * size * 3)
        idx = 0
        for (b in 0 until size) {
            for (g in 0 until size) {
                for (r in 0 until size) {
                    val normR = r.toFloat() / (size - 1)
                    val normG = g.toFloat() / (size - 1)
                    val normB = b.toFloat() / (size - 1)

                    warmCinematicData[idx++] = (normR * 1.15f).coerceIn(0f, 1f)
                    warmCinematicData[idx++] = (normG * 1.02f).coerceIn(0f, 1f)
                    warmCinematicData[idx++] = (normB * 0.88f).coerceIn(0f, 1f)
                }
            }
        }

        val coolTealOrangeData = FloatArray(size * size * size * 3)
        idx = 0
        for (b in 0 until size) {
            for (g in 0 until size) {
                for (r in 0 until size) {
                    val normR = r.toFloat() / (size - 1)
                    val normG = g.toFloat() / (size - 1)
                    val normB = b.toFloat() / (size - 1)

                    val tealR = normR * 0.8f
                    val tealG = normG * 1.05f
                    val tealB = normB * 1.2f

                    coolTealOrangeData[idx++] = tealR.coerceIn(0f, 1f)
                    coolTealOrangeData[idx++] = tealG.coerceIn(0f, 1f)
                    coolTealOrangeData[idx++] = tealB.coerceIn(0f, 1f)
                }
            }
        }

        return listOf(
            LutModel(
                id = "lut_neutral_identity",
                title = "Neutral Standard",
                size = size,
                data = identityData,
                isBuiltIn = true,
                category = "Standard"
            ),
            LutModel(
                id = "lut_warm_gold",
                title = "Warm Sunset",
                size = size,
                data = warmCinematicData,
                isBuiltIn = true,
                category = "Cinematic"
            ),
            LutModel(
                id = "lut_teal_orange",
                title = "Teal & Orange",
                size = size,
                data = coolTealOrangeData,
                isBuiltIn = true,
                category = "Cinematic"
            )
        )
    }
}
