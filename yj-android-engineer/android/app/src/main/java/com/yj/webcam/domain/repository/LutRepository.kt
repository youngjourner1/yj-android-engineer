package com.yj.webcam.domain.repository

import com.yj.webcam.domain.model.LutModel
import kotlinx.coroutines.flow.Flow
import java.io.InputStream

interface LutRepository {
    fun getLutsFlow(): Flow<List<LutModel>>
    suspend fun getLutById(id: String): LutModel?
    suspend fun importLut(inputStream: InputStream, name: String): Result<LutModel>
    suspend fun deleteLut(id: String): Boolean
    suspend fun toggleFavorite(id: String): Boolean
    suspend fun renameLut(id: String, newTitle: String): Boolean
    suspend fun loadBuiltInLuts(): List<LutModel>
}
