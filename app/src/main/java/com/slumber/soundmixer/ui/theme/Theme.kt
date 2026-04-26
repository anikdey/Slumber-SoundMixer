package com.slumber.soundmixer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.slumber.soundmixer.R

val Sora = FontFamily(
    Font(R.font.sora_regular,  FontWeight.Normal),
    Font(R.font.sora_medium,   FontWeight.Medium),
    Font(R.font.sora_semibold, FontWeight.SemiBold),
    Font(R.font.sora_bold,     FontWeight.Bold),
)
val DMSans = FontFamily(
    Font(R.font.dm_sans_regular, FontWeight.Normal),
    Font(R.font.dm_sans_medium,  FontWeight.Medium),
)

private val SleepMaterialTypography = Typography(
    displayLarge  = AppTextStyles.HeadLine1,
    displayMedium = AppTextStyles.HeadLine2,
    headlineLarge = AppTextStyles.HeadLine3,
    titleLarge    = AppTextStyles.HeadingLarge,
    titleMedium   = AppTextStyles.HeadingMedium,
    bodyLarge     = AppTextStyles.BodyText1Regular,
    bodyMedium    = AppTextStyles.BodyText2Regular,
    bodySmall     = AppTextStyles.BodyText3Regular,
    labelLarge    = AppTextStyles.ButtonLabel,
    labelMedium   = AppTextStyles.LabelMedium,
    labelSmall    = AppTextStyles.Caption,
)

private val sleepDimensions = AppDimensions()

val LocalSleepColors     = staticCompositionLocalOf<SleepColors> { SleepColors(isDark = true) }
val LocalSleepDimensions = staticCompositionLocalOf { sleepDimensions }
val LocalSleepTypography = staticCompositionLocalOf { AppTextStyles }

object AppTheme {
    val colors: SleepColors
        @Composable get() = LocalSleepColors.current

    val dimensions: AppDimensions
        @Composable get() = LocalSleepDimensions.current

    val typography: AppTextStyles
        @Composable get() = LocalSleepTypography.current
}

@Composable
fun SlumberSoundMixerTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = SleepColors(isDark = isDarkTheme)
    CompositionLocalProvider(
        LocalSleepColors     provides colors,
        LocalSleepDimensions provides sleepDimensions,
        LocalSleepTypography provides AppTextStyles,
    ) {
        MaterialTheme(
            colorScheme = colors.toMaterialColorScheme(isDarkTheme),
            typography  = SleepMaterialTypography,
            content     = content,
        )
    }
}
