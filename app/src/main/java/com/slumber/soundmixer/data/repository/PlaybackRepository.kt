package com.slumber.soundmixer.data.repository

import com.slumber.soundmixer.domain.model.ActiveSound
import com.slumber.soundmixer.domain.model.Sound
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackRepository @Inject constructor() {

    private val _activeSounds = MutableStateFlow<Map<String, ActiveSound>>(emptyMap())
    val activeSounds: StateFlow<Map<String, ActiveSound>> = _activeSounds.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _timerMillisRemaining = MutableStateFlow(0L)
    val timerMillisRemaining: StateFlow<Long> = _timerMillisRemaining.asStateFlow()

    private val _timerTotalMillis = MutableStateFlow(0L)
    val timerTotalMillis: StateFlow<Long> = _timerTotalMillis.asStateFlow()

    private val _fadeEnabled = MutableStateFlow(false)
    val fadeEnabled: StateFlow<Boolean> = _fadeEnabled.asStateFlow()

    fun addSound(sound: Sound, volume: Float = 0.6f) {
        _activeSounds.value = _activeSounds.value + (sound.id to ActiveSound(sound, volume))
    }

    fun removeSound(id: String) {
        _activeSounds.value = _activeSounds.value - id
        if (_activeSounds.value.isEmpty()) _isPlaying.value = false
    }

    fun setVolume(id: String, volume: Float) {
        val current = _activeSounds.value[id] ?: return
        _activeSounds.value = _activeSounds.value + (id to current.copy(volume = volume))
    }

    fun setPlaying(playing: Boolean) {
        _isPlaying.value = playing
    }

    fun setFadeEnabled(enabled: Boolean) {
        _fadeEnabled.value = enabled
    }

    fun setTimer(minutes: Int) {
        val millis = minutes * 60_000L
        _timerTotalMillis.value = millis
        _timerMillisRemaining.value = millis
    }

    fun tickTimer(remainingMillis: Long) {
        _timerMillisRemaining.value = remainingMillis
    }

    fun clearTimer() {
        _timerMillisRemaining.value = 0L
        _timerTotalMillis.value = 0L
    }
}
