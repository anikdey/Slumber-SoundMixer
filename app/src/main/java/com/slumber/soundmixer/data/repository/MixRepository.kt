package com.slumber.soundmixer.data.repository

import com.slumber.soundmixer.data.db.MixDao
import com.slumber.soundmixer.data.db.MixEntity
import com.slumber.soundmixer.data.db.MixSoundEntity
import com.slumber.soundmixer.data.db.MixWithSounds
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MixRepository @Inject constructor(private val dao: MixDao) {

    fun getAllMixes(): Flow<List<MixWithSounds>> = dao.getAllMixes()

    suspend fun getMixCount(): Int = dao.getMixCount()

    suspend fun nameExists(name: String): Boolean = dao.nameExists(name)

    suspend fun saveMix(name: String, sounds: Map<String, Float>, timerMinutes: Int? = null) {
        val mixId = dao.insertMix(MixEntity(name = name.trim(), timerMinutes = timerMinutes))
        val entities = sounds.map { (id, vol) ->
            MixSoundEntity(mixId = mixId, soundId = id, volume = vol)
        }
        dao.insertMixSounds(entities)
    }

    suspend fun deleteMix(mix: MixEntity) = dao.deleteMix(mix)
}
