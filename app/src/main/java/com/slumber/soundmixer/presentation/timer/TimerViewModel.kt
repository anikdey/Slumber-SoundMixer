package com.slumber.soundmixer.presentation.timer

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.slumber.soundmixer.R
import com.slumber.soundmixer.data.preferences.PreferencesRepository
import com.slumber.soundmixer.data.repository.PlaybackRepository
import com.slumber.soundmixer.service.SoundPlaybackService
import com.slumber.soundmixer.util.TimerConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    val repo: PlaybackRepository,
    private val prefs: PreferencesRepository
) : ViewModel() {

    val isPlaying: StateFlow<Boolean> = repo.isPlaying
    val timerMillisRemaining: StateFlow<Long> = repo.timerMillisRemaining
    val fadeEnabled: StateFlow<Boolean> = repo.fadeEnabled
    val activeSounds = repo.activeSounds

    private val _selectedMinutes = MutableStateFlow(prefs.lastTimerMinutes)
    val selectedMinutes: StateFlow<Int> = _selectedMinutes.asStateFlow()

    init {
        repo.setFadeEnabled(prefs.fadeEnabled)
    }

    fun selectMinutes(minutes: Int) {
        _selectedMinutes.value = minutes
        prefs.lastTimerMinutes = minutes
    }

    fun setTimer() {
        if (repo.activeSounds.value.isEmpty()) {
            android.widget.Toast.makeText(context, R.string.timer_toast_no_sounds, android.widget.Toast.LENGTH_SHORT).show()
            return
        }
        if (!repo.isPlaying.value) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, SoundPlaybackService::class.java).apply { action = SoundPlaybackService.ACTION_PLAY }
            )
        }
        val minutes = _selectedMinutes.value
        ContextCompat.startForegroundService(
            context,
            Intent(context, SoundPlaybackService::class.java).apply {
                action = SoundPlaybackService.ACTION_SET_TIMER
                putExtra(SoundPlaybackService.EXTRA_TIMER_MINUTES, minutes)
            }
        )
    }

    fun cancelTimer() {
        ContextCompat.startForegroundService(
            context,
            Intent(context, SoundPlaybackService::class.java).apply {
                action = SoundPlaybackService.ACTION_CANCEL_TIMER
            }
        )
    }

    fun togglePlayPause() {
        val action = if (repo.isPlaying.value) SoundPlaybackService.ACTION_PAUSE else SoundPlaybackService.ACTION_PLAY
        ContextCompat.startForegroundService(
            context,
            Intent(context, SoundPlaybackService::class.java).apply { this.action = action }
        )
    }

    fun setFadeEnabled(enabled: Boolean) {
        repo.setFadeEnabled(enabled)
        prefs.fadeEnabled = enabled
    }

    fun clampCustomMinutes(raw: Int): Int = raw.coerceIn(TimerConfig.MIN_MINUTES, TimerConfig.MAX_MINUTES)
}
