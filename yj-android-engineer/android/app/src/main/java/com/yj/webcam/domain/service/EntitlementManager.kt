package com.yj.webcam.domain.service

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.yj.webcam.domain.model.TierState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "entitlements")

@Singleton
class EntitlementManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_entitlements",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _tierState = MutableStateFlow(loadTier())
    val tierState: StateFlow<TierState> = _tierState.asStateFlow()

    private fun loadTier(): TierState {
        val tierString = sharedPreferences.getString("tier_state", TierState.FREE.name) ?: TierState.FREE.name
        return try {
            TierState.valueOf(tierString)
        } catch (e: IllegalArgumentException) {
            TierState.FREE
        }
    }

    fun updateTier(newTier: TierState) {
        _tierState.value = newTier
        sharedPreferences.edit().putString("tier_state", newTier.name).apply()
    }

    val isProUnlocked: Boolean
        get() = _tierState.value != TierState.FREE

    fun canUseUnlimitedLuts(): Boolean = isProUnlocked
    fun canUseScopes(): Boolean = isProUnlocked
    fun canUseStacking(): Boolean = isProUnlocked
    fun canUseCustomPresets(): Boolean = isProUnlocked
    fun canUseAdvancedEffects(): Boolean = isProUnlocked
}
