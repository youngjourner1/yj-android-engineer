package com.yj.webcam.domain.model

enum class VideoCodec {
    H264,
    HEVC,
    MJPEG
}

data class FrameData(
    val data: ByteArray,
    val width: Int,
    val height: Int,
    val timestampUs: Long,
    val sequenceNumber: Long,
    val isKeyframe: Boolean,
    val codec: VideoCodec
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FrameData

        if (!data.contentEquals(other.data)) return false
        if (width != other.width) return false
        if (height != other.height) return false
        if (timestampUs != other.timestampUs) return false
        if (sequenceNumber != other.sequenceNumber) return false
        if (isKeyframe != other.isKeyframe) return false
        if (codec != other.codec) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + timestampUs.hashCode()
        result = 31 * result + sequenceNumber.hashCode()
        result = 31 * result + isKeyframe.hashCode()
        result = 31 * result + codec.hashCode()
        return result
    }
}
