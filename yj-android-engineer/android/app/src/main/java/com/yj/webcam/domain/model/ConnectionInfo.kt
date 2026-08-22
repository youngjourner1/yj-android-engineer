package com.yj.webcam.domain.model

enum class ConnectionType {
    WIFI_WEBRTC,
    USB_ADB_TCP,
    LAN_DIRECT
}

enum class ConnectionStatus {
    DISCONNECTED,
    PAIRING,
    CONNECTING,
    CONNECTED,
    STREAMING,
    RECONNECTING,
    ERROR
}

data class ConnectionInfo(
    val deviceName: String = "",
    val clientIp: String = "",
    val port: Int = 9876,
    val connectionType: ConnectionType = ConnectionType.WIFI_WEBRTC,
    val status: ConnectionStatus = ConnectionStatus.DISCONNECTED,
    val isPaired: Boolean = false,
    val securityToken: String = ""
)
