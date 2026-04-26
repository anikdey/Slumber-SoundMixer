package com.slumber.soundmixer.di

import com.slumber.soundmixer.billing.BillingManager
import com.slumber.soundmixer.billing.FakeBillingManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BillingModule {

    @Binds
    @Singleton
    abstract fun bindBillingManager(fake: FakeBillingManager): BillingManager
}
