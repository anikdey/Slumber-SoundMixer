package com.slumber.soundmixer.domain.model

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.slumber.soundmixer.R

data class SoundMeta(
    val id: String,
    val emoji: String,
    @RawRes val resId: Int,
    @StringRes val nameRes: Int,
    val isPro: Boolean = false
)

object SoundRegistry {
    val all = listOf(
        SoundMeta("rain",   "🌧️", R.raw.rain,   R.string.sound_rain),
        SoundMeta("ocean",  "🌊", R.raw.ocean,  R.string.sound_ocean),
        SoundMeta("forest", "🌲", R.raw.forest, R.string.sound_forest),
    )

    fun findById(id: String): SoundMeta? = all.find { it.id == id }
}
