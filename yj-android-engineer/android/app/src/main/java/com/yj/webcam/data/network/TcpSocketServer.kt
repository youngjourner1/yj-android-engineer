package com.yj.webcam.data.network

import com.yj.webcam.core.result.Result
import com.yj.webcam.domain.model.ConnectionInfo
import com.yj.webcam.domain.model.ConnectionStatus
import com.yj.webcam.domain.model.ConnectionType
import com.yj.webcam.domain.model.FrameData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TcpSocketServer @Inject constructor() {

    private val _connectionInfo = MutableStateFlow(ConnectionInfo())
    val connectionInfo: StateFlow<ConnectionInfo> = _connectionInfo.asStateFlow()

    private var serverSocket: ServerSocket? = null
    private var clientSocket: Socket? = null
    private var outputStream: OutputStream? = null
    private var serverJob: Job? = null
    private var isRunning = false

    fun startServer(port: Int = 9876): Result<Unit> {
        return try {
            serverSocket = ServerSocket(port)
            isRunning = true
            _connectionInfo.value = ConnectionInfo(
                port = port,
                status = ConnectionStatus.CONNECTING,
                connectionType = ConnectionType.USB_ADB_TCP
            )

            serverJob = CoroutineScope(Dispatchers.IO).launch {
                while (isRunning) {
                    try {
                        val socket = serverSocket?.accept() ?: break
                        clientSocket = socket
                        outputStream = socket.getOutputStream()
                        _connectionInfo.value = _connectionInfo.value.copy(
                            clientIp = socket.inetAddress.hostAddress ?: "127.0.0.1",
                            status = ConnectionStatus.STREAMING
                        )
                        Timber.i("Client connected to TCP server: ${socket.inetAddress.hostAddress}")
                    } catch (e: Exception) {
                        if (isRunning) {
                            Timber.e(e, "Error accepting client connection")
                        }
                    }
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to start TCP socket server")
            Result.Error(e, "Server start error: ${e.message}")
        }
    }

    fun sendFrame(frame: FrameData) {
        val stream = outputStream ?: return
        try {
            val packet = ProtocolHandler.packFramePacket(frame)
            synchronized(stream) {
                stream.write(packet)
                stream.flush()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error writing frame packet to socket")
            _connectionInfo.value = _connectionInfo.value.copy(status = ConnectionStatus.RECONNECTING)
        }
    }

    fun stopServer() {
        isRunning = false
        serverJob?.cancel()
        serverJob = null
        try {
            outputStream?.close()
            clientSocket?.close()
            serverSocket?.close()
        } catch (e: Exception) {
            Timber.e(e, "Error stopping server socket")
        }
        _connectionInfo.value = ConnectionInfo(status = ConnectionStatus.DISCONNECTED)
    }
}
