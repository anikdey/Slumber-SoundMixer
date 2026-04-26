package com.slumber.soundmixer.billing

import com.slumber.soundmixer.data.preferences.PreferencesRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeBillingManager @Inject constructor(
    private val prefs: PreferencesRepository
) : BillingManager {

    override suspend fun purchasePro(): BillingResult {
        delay(1500)
        prefs.isPro = true
        return BillingResult.Success
    }

    override suspend fun restorePurchases(): BillingResult {
        delay(1500)
        return if (prefs.isPro) BillingResult.Success else BillingResult.NotFound
    }
}
