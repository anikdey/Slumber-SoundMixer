package com.slumber.soundmixer.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "mix_sounds",
    foreignKeys = [
        ForeignKey(
            entity = MixEntity::class,
            parentColumns = ["id"],
            childColumns = ["mixId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("mixId")]
)
data class MixSoundEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mixId: Long,
    val soundId: String,
    val volume: Float
)
