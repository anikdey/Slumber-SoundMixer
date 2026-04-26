package com.slumber.soundmixer.billing

interface BillingManager {
    suspend fun purchasePro(): BillingResult
    suspend fun restorePurchases(): BillingResult
}
