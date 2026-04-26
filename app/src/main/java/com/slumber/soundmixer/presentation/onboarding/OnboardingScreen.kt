package com.slumber.soundmixer.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.slumber.soundmixer.ui.components.GradientButton
import com.slumber.soundmixer.ui.theme.AppRadius
import com.slumber.soundmixer.ui.theme.AppTheme

data class OnboardFeature(val emoji: String, val boldText: String, val rest: String)

val onboardFeatures = listOf(
    OnboardFeature("🎚️", "Mix sounds", " with individual volume control"),
    OnboardFeature("⏱️", "Sleep timer", " with gentle fade out"),
    OnboardFeature("📴", "Works offline", " — no internet needed"),
)

@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.surface)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppTheme.dimensions.sizes.x55),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(AppTheme.dimensions.sizes.x55)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                AppTheme.colors.accent.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .size(AppTheme.dimensions.sizes.x40)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                AppTheme.colors.accent2.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
            )
            OrbitalAnimation()
        }

        Spacer(Modifier.height(AppTheme.dimensions.spaces.x5))

        Text(
            text = buildAnnotatedString {
                append("Sleep better\nwith ")
                withStyle(
                    SpanStyle(
                        brush = Brush.linearGradient(
                            listOf(AppTheme.colors.accent, AppTheme.colors.accent2)
                        ),
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Sound Mixes")
                }
            },
            style = AppTheme.typography.HeadingHero,
            textAlign = TextAlign.Center,
            color = AppTheme.colors.text,
            modifier = Modifier.padding(horizontal = AppTheme.dimensions.spaces.x6)
        )

        Spacer(Modifier.height(AppTheme.dimensions.spaces.x2))

        Text(
            text = "Blend calming sounds to create your perfect\nsleep environment. No account needed.",
            style = AppTheme.typography.BodyText2Regular,
            color = AppTheme.colors.muted,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = AppTheme.dimensions.spaces.x6)
        )

        Spacer(Modifier.height(AppTheme.dimensions.spaces.x4))

        Column(
            modifier = Modifier.padding(horizontal = AppTheme.dimensions.spaces.x6),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spaces.x2)
        ) {
            onboardFeatures.forEach { feat ->
                OnboardFeatureRow(feature = feat)
            }
        }

        Spacer(Modifier.height(AppTheme.dimensions.spaces.x4))

        GradientButton(
            text = "Get Started — It's Free",
            onClick = onGetStarted,
            modifier = Modifier.padding(horizontal = AppTheme.dimensions.spaces.x6)
        )

        Spacer(Modifier.height(AppTheme.dimensions.spaces.x4))
    }
}

@Composable
fun OnboardFeatureRow(feature: OnboardFeature) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(AppTheme.dimensions.sizes.x7)
                .clip(RoundedCornerShape(AppRadius.medium))
                .background(AppTheme.colors.accentAlpha12),
            contentAlignment = Alignment.Center
        ) {
            Text(text = feature.emoji, fontSize = 13.sp)
        }

        Spacer(Modifier.width(AppTheme.dimensions.spaces.x2))

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Medium, color = AppTheme.colors.text)) {
                    append(feature.boldText)
                }
                withStyle(SpanStyle(color = AppTheme.colors.soft)) {
                    append(feature.rest)
                }
            },
            style = AppTheme.typography.BodyText3Regular
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0E1422)
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen()
}
