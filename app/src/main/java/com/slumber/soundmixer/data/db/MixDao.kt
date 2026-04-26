package com.slumber.soundmixer.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MixDao {

    @Transaction
    @Query("SELECT * FROM mixes ORDER BY createdAt DESC")
    fun getAllMixes(): Flow<List<MixWithSounds>>

    @Insert
    suspend fun insertMix(mix: MixEntity): Long

    @Insert
    suspend fun insertMixSounds(sounds: List<MixSoundEntity>)

    @Delete
    suspend fun deleteMix(mix: MixEntity)

    @Query("SELECT COUNT(*) FROM mixes")
    suspend fun getMixCount(): Int

    @Query("SELECT COUNT(*) > 0 FROM mixes WHERE LOWER(TRIM(name)) = LOWER(TRIM(:name))")
    suspend fun nameExists(name: String): Boolean
}
