package com.slumber.soundmixer.presentation.mymixes

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slumber.soundmixer.data.db.MixEntity
import com.slumber.soundmixer.data.db.MixWithSounds
import com.slumber.soundmixer.data.preferences.PreferencesRepository
import com.slumber.soundmixer.data.repository.MixRepository
import com.slumber.soundmixer.data.repository.PlaybackRepository
import com.slumber.soundmixer.domain.model.SoundRegistry
import com.slumber.soundmixer.service.SoundPlaybackService
import com.slumber.soundmixer.util.AppConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedMixesViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mixRepo: MixRepository,
    private val playbackRepo: PlaybackRepository,
    private val prefs: PreferencesRepository
) : ViewModel() {

    val mixes: StateFlow<List<MixWithSounds>> = mixRepo.getAllMixes()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val isPro: StateFlow<Boolean> = prefs.isProFlow

    private val _navigateToUpgrade = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToUpgrade: SharedFlow<Unit> = _navigateToUpgrade

    fun canCreateMix(): Boolean {
        val count = mixes.value.size
        return prefs.isPro || count < AppConfig.FREE_MIX_LIMIT
    }

    fun onAddMixClicked(onOpen: () -> Unit) {
        if (canCreateMix()) {
            onOpen()
        } else {
            _navigateToUpgrade.tryEmit(Unit)
        }
    }

    fun playMix(mix: MixWithSounds) {
        val soundsWithMeta = mix.sounds.mapNotNull { mixSound ->
            SoundRegistry.findById(mixSound.soundId)?.let { meta -> meta to mixSound.volume }
        }
        if (soundsWithMeta.isEmpty()) return

        val currentIds = playbackRepo.activeSounds.value.keys.toSet()
        currentIds.forEach { playbackRepo.removeSound(it) }
        soundsWithMeta.forEach { (meta, volume) ->
            playbackRepo.addSound(
                com.slumber.soundmixer.domain.model.Sound(
                    id = meta.id,
                    emoji = meta.emoji,
                    name = context.getString(meta.nameRes),
                    resId = meta.resId,
                    isPro = meta.isPro
                ),
                volume
            )
        }

        context.startForegroundService(
            Intent(context, SoundPlaybackService::class.java).apply {
                action = SoundPlaybackService.ACTION_PLAY
            }
        )

        mix.mix.timerMinutes?.let { minutes ->
            context.startForegroundService(
                Intent(context, SoundPlaybackService::class.java).apply {
                    action = SoundPlaybackService.ACTION_SET_TIMER
                    putExtra(SoundPlaybackService.EXTRA_TIMER_MINUTES, minutes)
                }
            )
        }
    }

    fun deleteMix(mix: MixEntity) {
        viewModelScope.launch { mixRepo.deleteMix(mix) }
    }
}
