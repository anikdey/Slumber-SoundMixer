package com.slumber.soundmixer.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MixEntity::class, MixSoundEntity::class],
    version = 2,
    exportSchema = false
)
abstract class SlumberDatabase : RoomDatabase() {
    abstract fun mixDao(): MixDao
}
