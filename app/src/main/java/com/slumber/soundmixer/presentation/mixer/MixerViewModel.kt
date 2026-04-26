package com.slumber.soundmixer.presentation.mixer

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slumber.soundmixer.data.preferences.PreferencesRepository
import com.slumber.soundmixer.data.repository.PlaybackRepository
import com.slumber.soundmixer.domain.model.Sound
import com.slumber.soundmixer.domain.model.SoundRegistry
import com.slumber.soundmixer.service.SoundPlaybackService
import com.slumber.soundmixer.util.AppConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class MixerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: PlaybackRepository,
    private val prefs: PreferencesRepository
) : ViewModel() {

    val activeSounds: StateFlow<Map<String, Float>> = repo.activeSounds
        .map { it.mapValues { (_, v) -> v.volume } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    val isPlaying: StateFlow<Boolean> = repo.isPlaying
    val isPro: StateFlow<Boolean> = prefs.isProFlow

    private val _navigateToUpgrade = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToUpgrade: SharedFlow<Unit> = _navigateToUpgrade.asSharedFlow()

    init {
        prefs.loadActiveSoundVolumes().forEach { (id, volume) ->
            val meta = SoundRegistry.findById(id) ?: return@forEach
            repo.addSound(Sound(id, meta.emoji, context.getString(meta.nameRes), meta.resId), volume)
        }

        viewModelScope.launch {
            activeSounds.debounce(500).collect { prefs.saveActiveSoundVolumes(it) }
        }
    }

    fun toggleSound(sound: Sound) {
        val current = repo.activeSounds.value
        val limit = if (prefs.isPro) AppConfig.PRO_SOUND_LIMIT else AppConfig.FREE_SOUND_LIMIT
        when {
            sound.id in current -> repo.removeSound(sound.id)
            sound.isPro && !prefs.isPro -> _navigateToUpgrade.tryEmit(Unit)
            current.size >= limit -> _navigateToUpgrade.tryEmit(Unit)
            else -> repo.addSound(sound)
        }
    }

    fun setVolume(id: String, volume: Float) {
        repo.setVolume(id, volume)
    }

    fun togglePlayPause() {
        val action = if (repo.isPlaying.value) SoundPlaybackService.ACTION_PAUSE else SoundPlaybackService.ACTION_PLAY
        ContextCompat.startForegroundService(
            context,
            Intent(context, SoundPlaybackService::class.java).apply { this.action = action }
        )
    }
}
