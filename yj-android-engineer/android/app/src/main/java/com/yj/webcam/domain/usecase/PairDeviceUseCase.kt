package com.yj.webcam.domain.usecase

import com.yj.webcam.core.result.Result
import com.yj.webcam.domain.model.ConnectionInfo
import com.yj.webcam.domain.repository.DeviceRepository
import javax.inject.Inject

class PairDeviceUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository
) {
    suspend fun pairWithQrPayload(qrData: String): Result<ConnectionInfo> {
        if (qrData.isBlank()) {
            return Result.Error(IllegalArgumentException("QR payload cannot be empty"))
        }
        return deviceRepository.pairDevice(qrData)
    }

    suspend fun trustDevice(ip: String): Result<Unit> {
        return deviceRepository.trustDevice(ip)
    }
}
