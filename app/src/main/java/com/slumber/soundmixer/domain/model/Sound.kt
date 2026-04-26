package com.slumber.soundmixer.domain.model

import androidx.annotation.RawRes

data class Sound(
    val id: String,
    val emoji: String,
    val name: String,
    @RawRes val resId: Int,
    val isPro: Boolean = false
)

data class ActiveSound(val sound: Sound, val volume: Float)
