package com.slumber.soundmixer.data.preferences

import android.content.Context
import com.slumber.soundmixer.util.TimerConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("slumber_prefs", Context.MODE_PRIVATE)

    var onboardingComplete: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETE, value).apply()

    var notificationPermissionRequested: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATION_PERMISSION_REQUESTED, false)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATION_PERMISSION_REQUESTED, value).apply()

    var lastTimerMinutes: Int
        get() = prefs.getInt(KEY_LAST_TIMER_MINUTES, 45).coerceIn(TimerConfig.MIN_MINUTES, TimerConfig.MAX_MINUTES)
        set(value) = prefs.edit().putInt(KEY_LAST_TIMER_MINUTES, value).apply()

    var fadeEnabled: Boolean
        get() = prefs.getBoolean(KEY_FADE_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_FADE_ENABLED, value).apply()

    private val _isProFlow = MutableStateFlow(prefs.getBoolean(KEY_IS_PRO, false))
    val isProFlow: StateFlow<Boolean> = _isProFlow.asStateFlow()

    var isPro: Boolean
        get() = _isProFlow.value
        set(value) {
            prefs.edit().putBoolean(KEY_IS_PRO, value).apply()
            _isProFlow.value = value
        }

    private val _isDarkModeFlow = MutableStateFlow(prefs.getBoolean(KEY_IS_DARK_MODE, true))
    val isDarkModeFlow: StateFlow<Boolean> = _isDarkModeFlow.asStateFlow()

    var isDarkMode: Boolean
        get() = _isDarkModeFlow.value
        set(value) {
            prefs.edit().putBoolean(KEY_IS_DARK_MODE, value).apply()
            _isDarkModeFlow.value = value
        }

    fun saveActiveSoundVolumes(volumes: Map<String, Float>) {
        prefs.edit()
            .putStringSet(KEY_ACTIVE_SOUND_IDS, volumes.keys)
            .also { editor -> volumes.forEach { (id, vol) -> editor.putFloat("$KEY_VOLUME_PREFIX$id", vol) } }
            .apply()
    }

    fun loadActiveSoundVolumes(): Map<String, Float> {
        val ids = prefs.getStringSet(KEY_ACTIVE_SOUND_IDS, emptySet()) ?: emptySet()
        return ids.associateWith { id -> prefs.getFloat("$KEY_VOLUME_PREFIX$id", 0.6f) }
    }

    companion object {
        private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
        private const val KEY_NOTIFICATION_PERMISSION_REQUESTED = "notification_permission_requested"
        private const val KEY_LAST_TIMER_MINUTES = "last_timer_minutes"
        private const val KEY_FADE_ENABLED = "fade_enabled"
        private const val KEY_IS_PRO = "is_pro"
        private const val KEY_IS_DARK_MODE = "is_dark_mode"
        private const val KEY_ACTIVE_SOUND_IDS = "active_sound_ids"
        private const val KEY_VOLUME_PREFIX = "volume_"
    }
}
