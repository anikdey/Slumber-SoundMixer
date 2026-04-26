package com.slumber.soundmixer.presentation.onboarding

import androidx.lifecycle.ViewModel
import com.slumber.soundmixer.data.preferences.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val prefs: PreferencesRepository
) : ViewModel() {

    private val _onboardingComplete = MutableStateFlow(prefs.onboardingComplete)
    val onboardingComplete: StateFlow<Boolean> = _onboardingComplete.asStateFlow()

    fun completeOnboarding() {
        prefs.onboardingComplete = true
        _onboardingComplete.value = true
    }
}
