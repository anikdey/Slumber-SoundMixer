package com.slumber.soundmixer.presentation.createmix

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slumber.soundmixer.data.preferences.PreferencesRepository
import com.slumber.soundmixer.data.repository.MixRepository
import com.slumber.soundmixer.data.repository.PlaybackRepository
import com.slumber.soundmixer.domain.model.SoundRegistry
import com.slumber.soundmixer.util.AppConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class CreateMixViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val playbackRepo: PlaybackRepository,
    private val mixRepo: MixRepository,
    private val prefs: PreferencesRepository
) : ViewModel() {

    val isPro: StateFlow<Boolean> = prefs.isProFlow

    private val _mixName = MutableStateFlow("My Mix")
    val mixName: StateFlow<String> = _mixName

    private val _previewSounds = MutableStateFlow<Map<String, Float>>(emptyMap())
    val previewSounds: StateFlow<Map<String, Float>> = _previewSounds

    private val _isPreviewPlaying = MutableStateFlow(false)
    val isPreviewPlaying: StateFlow<Boolean> = _isPreviewPlaying

    private val _selectedTimerMinutes = MutableStateFlow<Int?>(null)
    val selectedTimerMinutes: StateFlow<Int?> = _selectedTimerMinutes

    private val _nameError = MutableStateFlow(false)
    val nameError: StateFlow<Boolean> = _nameError

    val canSave: StateFlow<Boolean> = combine(_mixName, _previewSounds, _nameError) { name, sounds, hasError ->
        name.trim().isNotEmpty() && sounds.isNotEmpty() && !hasError
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private var nameCheckJob: Job? = null

    private val _navigateToUpgrade = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToUpgrade: SharedFlow<Unit> = _navigateToUpgrade

    private val _mixSaved = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val mixSaved: SharedFlow<Unit> = _mixSaved

    private val mediaPlayers = mutableMapOf<String, MediaPlayer>()

    init {
        if (playbackRepo.isPlaying.value) {
            playbackRepo.setPlaying(false)
        }
    }

    fun reset() {
        nameCheckJob?.cancel()
        stopAndReleaseAll()
        _mixName.value = "My Mix"
        _previewSounds.value = emptyMap()
        _selectedTimerMinutes.value = null
        _nameError.value = false
        if (playbackRepo.isPlaying.value) {
            playbackRepo.setPlaying(false)
        }
    }

    fun setMixName(name: String) {
        _mixName.value = name
        _nameError.value = false
        nameCheckJob?.cancel()
        if (name.trim().isEmpty()) return
        nameCheckJob = viewModelScope.launch {
            delay(400)
            _nameError.value = mixRepo.nameExists(name)
        }
    }

    fun selectTimerMinutes(minutes: Int?) {
        _selectedTimerMinutes.value = minutes
    }

    fun toggleSound(soundId: String) {
        val current = _previewSounds.value
        if (soundId in current) {
            releasePlayer(soundId)
            _previewSounds.value = current - soundId
        } else {
            val limit = if (prefs.isPro) AppConfig.PRO_SOUND_LIMIT else AppConfig.FREE_SOUND_LIMIT
            if (current.size >= limit) {
                _navigateToUpgrade.tryEmit(Unit)
                return
            }
            _previewSounds.value = current + (soundId to 0.8f)
            if (_isPreviewPlaying.value) {
                startPlayer(soundId, 0.8f)
            }
        }
    }

    fun setVolume(id: String, volume: Float) {
        _previewSounds.value = _previewSounds.value.toMutableMap().also { it[id] = volume }
        mediaPlayers[id]?.setVolume(volume, volume)
    }

    fun togglePreviewPlayPause() {
        if (_isPreviewPlaying.value) {
            mediaPlayers.values.forEach { it.pause() }
            _isPreviewPlaying.value = false
        } else {
            if (_previewSounds.value.isEmpty()) return
            _previewSounds.value.forEach { (id, volume) ->
                if (id in mediaPlayers) {
                    mediaPlayers[id]?.start()
                } else {
                    startPlayer(id, volume)
                }
            }
            _isPreviewPlaying.value = true
        }
    }

    fun saveMix() {
        viewModelScope.launch {
            val exists = mixRepo.nameExists(_mixName.value)
            if (exists) {
                _nameError.value = true
                return@launch
            }
            mixRepo.saveMix(_mixName.value, _previewSounds.value, _selectedTimerMinutes.value)
            stopAndReleaseAll()
            _mixSaved.tryEmit(Unit)
        }
    }

    private fun startPlayer(soundId: String, volume: Float) {
        val meta = SoundRegistry.findById(soundId) ?: return
        val mp = MediaPlayer.create(context, meta.resId) ?: return
        mp.isLooping = true
        mp.setVolume(volume, volume)
        mp.start()
        mediaPlayers[soundId] = mp
    }

    private fun releasePlayer(soundId: String) {
        mediaPlayers[soundId]?.let {
            try { it.stop() } catch (_: Exception) {}
            it.release()
        }
        mediaPlayers.remove(soundId)
    }

    private fun stopAndReleaseAll() {
        mediaPlayers.keys.toList().forEach { releasePlayer(it) }
        _isPreviewPlaying.value = false
    }

    override fun onCleared() {
        super.onCleared()
        stopAndReleaseAll()
    }
}
