package com.slumber.soundmixer.presentation.settings

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slumber.soundmixer.R
import com.slumber.soundmixer.billing.BillingManager
import com.slumber.soundmixer.billing.BillingResult
import com.slumber.soundmixer.data.preferences.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: PreferencesRepository,
    private val billing: BillingManager
) : ViewModel() {

    val isPro: StateFlow<Boolean> = prefs.isProFlow
    val isDarkMode: StateFlow<Boolean> = prefs.isDarkModeFlow

    private val _isRestoring = MutableStateFlow(false)
    val isRestoring: StateFlow<Boolean> = _isRestoring

    fun toggleDarkMode() {
        prefs.isDarkMode = !prefs.isDarkMode
    }

    fun restorePurchases() {
        if (_isRestoring.value) return
        viewModelScope.launch {
            _isRestoring.value = true
            val messageRes = when (billing.restorePurchases()) {
                is BillingResult.Success -> R.string.settings_restore_already_pro
                is BillingResult.NotFound -> R.string.settings_restore_not_found
                is BillingResult.Error -> R.string.settings_restore_not_found
            }
            Toast.makeText(context, messageRes, Toast.LENGTH_SHORT).show()
            _isRestoring.value = false
        }
    }
}
