package com.slumber.soundmixer.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ── Raw palettes (private primitives) ────────────────────────────────────────

private object DarkPalette {
    val Bg           = Color(0xFF070B14)
    val Surface      = Color(0xFF0E1422)
    val Card         = Color(0xFF131926)
    val Card2        = Color(0xFF181F30)
    val Border       = Color(0x12FFFFFF)
    val AccentBlue   = Color(0xFF5B8DEF)
    val AccentPurple = Color(0xFF7B6CF6)
    val TextPrimary  = Color(0xFFE2E8F8)
    val TextMuted    = Color(0xFF5A6480)
    val TextSoft     = Color(0xFF8B95B0)
    val Pro          = Color(0xFFF0A045)
    val White        = Color(0xFFFFFFFF)
    val Error        = Color(0xFFBA1A1A)
}

private object LightPalette {
    val Bg           = Color(0xFFF5F7FF)
    val Surface      = Color(0xFFEEF1FC)
    val Card         = Color(0xFFE8ECF8)
    val Card2        = Color(0xFFE2E7F5)
    val Border       = Color(0x1A000000)
    val AccentBlue   = Color(0xFF5B8DEF)
    val AccentPurple = Color(0xFF7B6CF6)
    val TextPrimary  = Color(0xFF1A1F35)
    val TextMuted    = Color(0xFF6B748A)
    val TextSoft     = Color(0xFF8B95B0)
    val Pro          = Color(0xFFE8901A)
    val White        = Color(0xFFFFFFFF)
    val Error        = Color(0xFFBA1A1A)
}

// ── Contract — compiler enforces every token is defined in both themes ────────

interface SleepColorsContract {
    val bg: Color
    val surface: Color
    val card: Color
    val card2: Color
    val border: Color
    val accent: Color
    val accent2: Color
    val text: Color
    val muted: Color
    val soft: Color
    val pro: Color
    // Accent overlays
    val accentAlpha12: Color
    val accentAlpha25: Color
    val accentAlpha40: Color
    val accent2Alpha12: Color
    val accent2Alpha25: Color
    // Surface overlays (white-alpha on dark, black-alpha on light)
    val surfaceOverlay5: Color
    val surfaceOverlay8: Color
    val surfaceOverlay10: Color
    val white: Color
    val error: Color
}

// ── Dark ──────────────────────────────────────────────────────────────────────

object SleepColorsDark : SleepColorsContract {
    override val bg             = DarkPalette.Bg
    override val surface        = DarkPalette.Surface
    override val card           = DarkPalette.Card
    override val card2          = DarkPalette.Card2
    override val border         = DarkPalette.Border
    override val accent         = DarkPalette.AccentBlue
    override val accent2        = DarkPalette.AccentPurple
    override val text           = DarkPalette.TextPrimary
    override val muted          = DarkPalette.TextMuted
    override val soft           = DarkPalette.TextSoft
    override val pro            = DarkPalette.Pro
    override val accentAlpha12  = Color(0x1F5B8DEF)
    override val accentAlpha25  = Color(0x405B8DEF)
    override val accentAlpha40  = Color(0x665B8DEF)
    override val accent2Alpha12 = Color(0x1F7B6CF6)
    override val accent2Alpha25 = Color(0x407B6CF6)
    override val surfaceOverlay5  = Color(0x0DFFFFFF)
    override val surfaceOverlay8  = Color(0x14FFFFFF)
    override val surfaceOverlay10 = Color(0x1AFFFFFF)
    override val white          = DarkPalette.White
    override val error          = DarkPalette.Error
}

// ── Light ─────────────────────────────────────────────────────────────────────

object SleepColorsLight : SleepColorsContract {
    override val bg             = LightPalette.Bg
    override val surface        = LightPalette.Surface
    override val card           = LightPalette.Card
    override val card2          = LightPalette.Card2
    override val border         = LightPalette.Border
    override val accent         = LightPalette.AccentBlue
    override val accent2        = LightPalette.AccentPurple
    override val text           = LightPalette.TextPrimary
    override val muted          = LightPalette.TextMuted
    override val soft           = LightPalette.TextSoft
    override val pro            = LightPalette.Pro
    override val accentAlpha12  = Color(0x1F5B8DEF)
    override val accentAlpha25  = Color(0x405B8DEF)
    override val accentAlpha40  = Color(0x665B8DEF)
    override val accent2Alpha12 = Color(0x1F7B6CF6)
    override val accent2Alpha25 = Color(0x407B6CF6)
    override val surfaceOverlay5  = Color(0x0D000000)
    override val surfaceOverlay8  = Color(0x14000000)
    override val surfaceOverlay10 = Color(0x1A000000)
    override val white          = LightPalette.White
    override val error          = LightPalette.Error
}

// ── Delegating class — switches between implementations ───────────────────────

class SleepColors(isDark: Boolean) : SleepColorsContract
    by if (isDark) SleepColorsDark else SleepColorsLight

// ── M3 ColorScheme mapping ────────────────────────────────────────────────────

// ── Gradient extensions — use as AppTheme.colors.accentGradient inside composables ──

val SleepColorsContract.accentGradient: Brush
    get() = Brush.linearGradient(colors = listOf(accent, accent2))

val SleepColorsContract.accentGradientHoriz: Brush
    get() = Brush.horizontalGradient(colors = listOf(accent, Color(0xFF7EB8F7)))

val SleepColorsContract.purpleGradientHoriz: Brush
    get() = Brush.horizontalGradient(colors = listOf(accent2, Color(0xFFA89CF7)))

// ── M3 ColorScheme mapping ────────────────────────────────────────────────────

fun SleepColorsContract.toMaterialColorScheme(isDark: Boolean): ColorScheme =
    if (isDark)
        darkColorScheme(
            primary          = accent,
            onPrimary        = white,
            primaryContainer = accentAlpha12,
            secondary        = accent2,
            onSecondary      = white,
            background       = bg,
            onBackground     = text,
            surface          = surface,
            onSurface        = text,
            surfaceVariant   = card,
            onSurfaceVariant = soft,
            outline          = border,
            error            = error,
            onError          = white,
        )
    else
        lightColorScheme(
            primary          = accent,
            onPrimary        = white,
            primaryContainer = accentAlpha12,
            secondary        = accent2,
            onSecondary      = white,
            background       = bg,
            onBackground     = text,
            surface          = surface,
            onSurface        = text,
            surfaceVariant   = card,
            onSurfaceVariant = soft,
            outline          = border,
            error            = error,
            onError          = white,
        )
