package com.yj.webcam.data.network

import com.yj.webcam.domain.model.FrameData
import com.yj.webcam.domain.model.VideoCodec
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ProtocolHandlerTest {

    @Test
    fun testFramePackingAndUnpackingSuccess() {
        val originalPayload = byteArrayOf(0x00, 0x00, 0x00, 0x01, 0x67, 0x42, 0x00, 0x0A)
        val originalFrame = FrameData(
            data = originalPayload,
            width = 1920,
            height = 1080,
            timestampUs = 123456789L,
            sequenceNumber = 42L,
            isKeyframe = true,
            codec = VideoCodec.H264
        )

        val packedBytes = ProtocolHandler.packFramePacket(originalFrame)
        assertNotNull(packedBytes)

        val unpackedFrame = ProtocolHandler.unpackFramePacket(packedBytes)
        assertNotNull(unpackedFrame)
        assertEquals(originalFrame.width, unpackedFrame?.width)
        assertEquals(originalFrame.height, unpackedFrame?.height)
        assertEquals(originalFrame.sequenceNumber, unpackedFrame?.sequenceNumber)
        assertEquals(originalFrame.timestampUs, unpackedFrame?.timestampUs)
        assertEquals(originalFrame.isKeyframe, unpackedFrame?.isKeyframe)
        assertArrayEquals(originalFrame.data, unpackedFrame?.data)
    }
}
