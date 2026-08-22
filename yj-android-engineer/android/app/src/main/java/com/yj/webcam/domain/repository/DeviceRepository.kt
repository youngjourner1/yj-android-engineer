package com.yj.webcam.domain.repository

import com.yj.webcam.core.result.Result
import com.yj.webcam.domain.model.ConnectionInfo
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    val pairedDevices: Flow<List<ConnectionInfo>>

    suspend fun pairDevice(qrPayloadOrIp: String): Result<ConnectionInfo>
    suspend fun unpairDevice(clientIp: String): Result<Unit>
    suspend fun trustDevice(clientIp: String): Result<Unit>
}
