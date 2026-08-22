package com.yj.webcam.domain.usecase

import com.yj.webcam.core.result.Result
import com.yj.webcam.domain.model.CameraConfig
import com.yj.webcam.domain.repository.CameraRepository
import com.yj.webcam.domain.repository.StreamingRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class StreamVideoUseCaseTest {

    private val cameraRepository: CameraRepository = mock()
    private val streamingRepository: StreamingRepository = mock()
    private lateinit var useCase: StreamVideoUseCase

    @Before
    fun setUp() {
        useCase = StreamVideoUseCase(cameraRepository, streamingRepository)
    }

    @Test
    fun testStartStreamingSuccess() = runTest {
        whenever(streamingRepository.startServer(any())).thenReturn(Result.Success(Unit))
        whenever(cameraRepository.startCamera(any())).thenReturn(Result.Success(Unit))

        val result = useCase.startStreaming(CameraConfig())
        assertTrue(result.isSuccess)
    }
}
