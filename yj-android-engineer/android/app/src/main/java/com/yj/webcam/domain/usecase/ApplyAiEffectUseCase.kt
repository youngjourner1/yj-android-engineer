package com.yj.webcam.domain.usecase

import com.yj.webcam.domain.repository.SettingsRepository
import javax.inject.Inject

class ApplyAiEffectUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend fun setBackgroundBlur(enabled: Boolean) {
        settingsRepository.updateAiBlur(enabled)
    }

    suspend fun setAutoFraming(enabled: Boolean) {
        settingsRepository.updateAiAutoFraming(enabled)
    }
}
