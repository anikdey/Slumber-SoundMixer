package com.slumber.soundmixer.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mixes")
data class MixEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val timerMinutes: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
)
