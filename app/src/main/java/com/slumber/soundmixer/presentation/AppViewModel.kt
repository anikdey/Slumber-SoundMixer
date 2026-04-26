package com.slumber.soundmixer.presentation

import androidx.lifecycle.ViewModel
import com.slumber.soundmixer.data.preferences.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val prefs: PreferencesRepository
) : ViewModel() {
    val isDarkMode: StateFlow<Boolean> = prefs.isDarkModeFlow

    val notificationPermissionRequested: Boolean
        get() = prefs.notificationPermissionRequested

    fun markNotificationPermissionRequested() {
        prefs.notificationPermissionRequested = true
    }
}
