package com.slumber.soundmixer.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable data object Onboarding : Route
    @Serializable data object Mixer      : Route
    @Serializable data object Timer      : Route
    @Serializable data object Mixes      : Route
    @Serializable data object Settings   : Route
    @Serializable data object Upgrade    : Route
    @Serializable data object CreateMix  : Route
}
