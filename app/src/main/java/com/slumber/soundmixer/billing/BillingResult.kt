package com.slumber.soundmixer.billing

sealed class BillingResult {
    data object Success : BillingResult()
    data object NotFound : BillingResult()
    data class Error(val message: String) : BillingResult()
}
