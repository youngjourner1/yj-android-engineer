package com.yj.webcam.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paired_devices")
data class DeviceEntity(
    @PrimaryKey val clientIp: String,
    val deviceName: String,
    val isTrusted: Boolean,
    val lastConnectedTimestamp: Long,
    val securityToken: String
)

@Entity(tableName = "app_logs")
data class LogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tag: String,
    val message: String,
    val timestamp: Long,
    val isError: Boolean
)
