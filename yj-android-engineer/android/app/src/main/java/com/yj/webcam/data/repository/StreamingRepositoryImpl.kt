package com.yj.webcam.data.repository

import com.yj.webcam.core.result.Result
import com.yj.webcam.data.network.MdnsDiscovery
import com.yj.webcam.data.network.TcpSocketServer
import com.yj.webcam.domain.model.ConnectionInfo
import com.yj.webcam.domain.model.FrameData
import com.yj.webcam.domain.model.StreamStats
import com.yj.webcam.domain.repository.StreamingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreamingRepositoryImpl @Inject constructor(
    private val tcpSocketServer: TcpSocketServer,
    private val mdnsDiscovery: MdnsDiscovery
) : StreamingRepository {

    private val _streamStats = MutableStateFlow(StreamStats())
    override val streamStats: Flow<StreamStats> = _streamStats.asStateFlow()

    override val connectionState: Flow<ConnectionInfo>
        get() = tcpSocketServer.connectionInfo

    override suspend fun startServer(port: Int): Result<Unit> {
        val result = tcpSocketServer.startServer(port)
        if (result.isSuccess) {
            mdnsDiscovery.registerService("GalaxyS24", port)
        }
        return result
    }

    override suspend fun stopServer(): Result<Unit> {
        mdnsDiscovery.unregisterService()
        tcpSocketServer.stopServer()
        return Result.Success(Unit)
    }

    override suspend fun sendVideoFrame(frameData: FrameData): Result<Unit> {
        tcpSocketServer.sendFrame(frameData)
        return Result.Success(Unit)
    }

    override suspend fun sendAudioData(audioBytes: ByteArray, timestampUs: Long): Result<Unit> {
        return Result.Success(Unit)
    }
}
