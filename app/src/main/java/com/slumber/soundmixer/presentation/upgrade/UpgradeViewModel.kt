package com.slumber.soundmixer.presentation.upgrade

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slumber.soundmixer.billing.BillingManager
import com.slumber.soundmixer.billing.BillingResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpgradeViewModel @Inject constructor(
    private val billing: BillingManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _purchaseSuccess = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val purchaseSuccess: SharedFlow<Unit> = _purchaseSuccess

    private val _errorMessage = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val errorMessage: SharedFlow<String> = _errorMessage

    fun purchase() {
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = billing.purchasePro()) {
                is BillingResult.Success -> _purchaseSuccess.tryEmit(Unit)
                is BillingResult.NotFound -> _errorMessage.tryEmit("Purchase not found")
                is BillingResult.Error -> _errorMessage.tryEmit(result.message)
            }
            _isLoading.value = false
        }
    }
}
