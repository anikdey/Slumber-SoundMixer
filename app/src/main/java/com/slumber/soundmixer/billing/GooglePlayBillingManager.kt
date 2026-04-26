package com.slumber.soundmixer.billing

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GooglePlayBillingManager @Inject constructor(
    @ApplicationContext private val context: Context
) : BillingManager {

    override suspend fun purchasePro(): BillingResult {
        return BillingResult.Error("Google Play Billing not yet configured")
    }

    override suspend fun restorePurchases(): BillingResult {
        return BillingResult.Error("Google Play Billing not yet configured")
    }
}
