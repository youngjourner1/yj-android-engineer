package com.yj.webcam.data.repository

import com.yj.webcam.core.result.Result
import com.yj.webcam.data.db.DeviceDao
import com.yj.webcam.data.db.DeviceEntity
import com.yj.webcam.domain.model.ConnectionInfo
import com.yj.webcam.domain.model.ConnectionStatus
import com.yj.webcam.domain.model.ConnectionType
import com.yj.webcam.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepositoryImpl @Inject constructor(
    private val deviceDao: DeviceDao
) : DeviceRepository {

    override val pairedDevices: Flow<List<ConnectionInfo>>
        get() = deviceDao.getAllPairedDevices().map { entities ->
            entities.map { entity ->
                ConnectionInfo(
                    deviceName = entity.deviceName,
                    clientIp = entity.clientIp,
                    connectionType = ConnectionType.WIFI_WEBRTC,
                    status = ConnectionStatus.CONNECTED,
                    isPaired = entity.isTrusted,
                    securityToken = entity.securityToken
                )
            }
        }

    override suspend fun pairDevice(qrPayloadOrIp: String): Result<ConnectionInfo> {
        val entity = DeviceEntity(
            clientIp = qrPayloadOrIp,
            deviceName = "Windows PC Client",
            isTrusted = true,
            lastConnectedTimestamp = System.currentTimeMillis(),
            securityToken = "TOKEN_${System.currentTimeMillis()}"
        )
        deviceDao.insertDevice(entity)
        val info = ConnectionInfo(
            deviceName = entity.deviceName,
            clientIp = entity.clientIp,
            isPaired = true,
            securityToken = entity.securityToken
        )
        return Result.Success(info)
    }

    override suspend fun unpairDevice(clientIp: String): Result<Unit> {
        deviceDao.deleteDevice(clientIp)
        return Result.Success(Unit)
    }

    override suspend fun trustDevice(clientIp: String): Result<Unit> {
        return Result.Success(Unit)
    }
}
