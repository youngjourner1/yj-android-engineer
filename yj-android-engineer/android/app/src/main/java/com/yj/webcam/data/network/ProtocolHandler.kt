package com.yj.webcam.data.network

import com.yj.webcam.domain.model.FrameData
import com.yj.webcam.domain.model.VideoCodec
import java.nio.ByteBuffer
import java.nio.ByteOrder

object ProtocolHandler {

    private const val MAGIC_HEADER = 0x594A5731 // "YJW1" ascii
    private const val HEADER_SIZE = 32

    fun packFramePacket(frame: FrameData): ByteArray {
        val payloadSize = frame.data.size
        val buffer = ByteBuffer.allocate(HEADER_SIZE + payloadSize)
        buffer.order(ByteOrder.BIG_ENDIAN)

        // Magic 4 bytes
        buffer.putInt(MAGIC_HEADER)
        // Frame Type (1 = Video), Codec (1 = H264, 2 = HEVC), Flags (1 = IDR/Keyframe)
        buffer.put(1.toByte())
        val codecByte = if (frame.codec == VideoCodec.HEVC) 2.toByte() else 1.toByte()
        buffer.put(codecByte)
        buffer.put(if (frame.isKeyframe) 1.toByte() else 0.toByte())
        buffer.put(0.toByte()) // Reserved

        // Sequence number (8 bytes)
        buffer.putLong(frame.sequenceNumber)
        // Timestamp Us (8 bytes)
        buffer.putLong(frame.timestampUs)
        // Width & Height (uint16 each)
        buffer.putShort(frame.width.toShort())
        buffer.putShort(frame.height.toShort())
        // Payload Size (uint32)
        buffer.putInt(payloadSize)

        // Payload data
        buffer.put(frame.data)

        return buffer.array()
    }

    fun unpackFramePacket(packet: ByteArray): FrameData? {
        if (packet.size < HEADER_SIZE) return null
        val buffer = ByteBuffer.wrap(packet)
        buffer.order(ByteOrder.BIG_ENDIAN)

        val magic = buffer.int
        if (magic != MAGIC_HEADER) return null

        val type = buffer.get()
        val codecByte = buffer.get()
        val flags = buffer.get()
        buffer.get() // Reserved

        val sequenceNumber = buffer.long
        val timestampUs = buffer.long
        val width = buffer.short.toInt() and 0xFFFF
        val height = buffer.short.toInt() and 0xFFFF
        val payloadSize = buffer.int

        if (packet.size < HEADER_SIZE + payloadSize) return null

        val payload = ByteArray(payloadSize)
        buffer.get(payload)

        val codec = if (codecByte == 2.toByte()) VideoCodec.HEVC else VideoCodec.H264
        val isKeyframe = (flags == 1.toByte())

        return FrameData(
            data = payload,
            width = width,
            height = height,
            timestampUs = timestampUs,
            sequenceNumber = sequenceNumber,
            isKeyframe = isKeyframe,
            codec = codec
        )
    }
}
