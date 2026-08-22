package com.yj.webcam.data.network

import android.content.Context
import com.yj.webcam.core.result.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebRTCManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var rootEglBase: EglBase? = null
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null

    fun initializeWebRTC(): Result<Unit> {
        return try {
            rootEglBase = EglBase.create()
            PeerConnectionFactory.initialize(
                PeerConnectionFactory.InitializationOptions.builder(context)
                    .setEnableInternalTracer(true)
                    .createInitializationOptions()
            )

            val encoderFactory = DefaultVideoEncoderFactory(
                rootEglBase?.eglBaseContext,
                true,
                true
            )

            peerConnectionFactory = PeerConnectionFactory.builder()
                .setVideoEncoderFactory(encoderFactory)
                .createPeerConnectionFactory()

            Timber.i("WebRTC PeerConnectionFactory initialized successfully")
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize WebRTC engine")
            Result.Error(e, "WebRTC init error: ${e.message}")
        }
    }

    fun close() {
        peerConnection?.close()
        peerConnection = null
        peerConnectionFactory?.dispose()
        peerConnectionFactory = null
        rootEglBase?.release()
        rootEglBase = null
    }
}
