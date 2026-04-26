package com.slumber.soundmixer.di

import android.content.Context
import androidx.room.Room
import com.slumber.soundmixer.data.db.MixDao
import com.slumber.soundmixer.data.db.SlumberDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SlumberDatabase =
        Room.databaseBuilder(context, SlumberDatabase::class.java, "slumber_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideMixDao(db: SlumberDatabase): MixDao = db.mixDao()
}
