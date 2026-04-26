package com.slumber.soundmixer.data.db

import androidx.room.Embedded
import androidx.room.Relation

data class MixWithSounds(
    @Embedded val mix: MixEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "mixId"
    )
    val sounds: List<MixSoundEntity>
)
