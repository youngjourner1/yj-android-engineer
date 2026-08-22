package com.yj.webcam.domain.service

import com.yj.webcam.domain.model.TierState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service managing user subscription tier and entitlement permissions.
 */
@Singleton
class EntitlementManager @Inject constructor() {

    private val _tierState = MutableStateFlow(TierState.FREE)
    val tierState: StateFlow<TierState> = _tierState.asStateFlow()

    fun updateTier(newTier: TierState) {
        _tierState.value = newTier
    }

    val isProUnlocked: Boolean
        get() = _tierState.value != TierState.FREE

    fun canUseUnlimitedLuts(): Boolean = isProUnlocked
    fun canUseScopes(): Boolean = isProUnlocked
    fun canUseStacking(): Boolean = isProUnlocked
    fun canUseCustomPresets(): Boolean = isProUnlocked
    fun canUseAdvancedEffects(): Boolean = isProUnlocked
}
