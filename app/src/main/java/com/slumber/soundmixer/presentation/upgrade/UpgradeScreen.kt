package com.slumber.soundmixer.presentation.upgrade

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.slumber.soundmixer.presentation.onboarding.OrbitalAnimation
import com.slumber.soundmixer.ui.components.GradientButton
import com.slumber.soundmixer.ui.theme.AppTheme
import kotlinx.coroutines.flow.collectLatest

private data class UpgradeFeature(val emoji: String, val bold: String, val rest: String)

private val upgradeFeatures = listOf(
    UpgradeFeature("🎚️", "Up to 4 sounds", " layered simultaneously"),
    UpgradeFeature("🎵", "20+ premium sounds", " from nature & ambient"),
    UpgradeFeature("🚫", "No ads", ", ever"),
)

@Composable
fun UpgradeScreen(
    onDismiss: () -> Unit,
    viewModel: UpgradeViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.purchaseSuccess.collectLatest { onDismiss() }
    }
    LaunchedEffect(Unit) {
        viewModel.errorMessage.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

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
                .padding(
                    top = AppTheme.dimensions.spaces.x3,
                    end = AppTheme.dimensions.spaces.x4
                ),
            contentAlignment = Alignment.CenterEnd
        ) {
            Box(
                modifier = Modifier
                    .size(AppTheme.dimensions.sizes.x8)
                    .clip(RoundedCornerShape(AppTheme.dimensions.radius.xlarge))
                    .background(AppTheme.colors.card)
                    .border(AppTheme.dimensions.borders.veryLow, AppTheme.colors.border, RoundedCornerShape(AppTheme.dimensions.radius.xlarge))
                    .clickable(enabled = !isLoading) { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Text("✕", fontSize = 14.sp, color = AppTheme.colors.muted)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppTheme.dimensions.sizes.x40),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(AppTheme.dimensions.sizes.x40)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(AppTheme.colors.accent.copy(alpha = 0.25f), Color.Transparent)
                        )
                    )
            )
            OrbitalAnimation()
        }

        Spacer(Modifier.height(AppTheme.dimensions.spaces.x4))

        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        brush = Brush.linearGradient(listOf(AppTheme.colors.accent, AppTheme.colors.accent2))
                    )
                ) { append("Unlock Pro") }
            },
            style = AppTheme.typography.HeadingDisplay,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(AppTheme.dimensions.spaces.x2))

        Text(
            text = "Everything you need for a\nperfect night's sleep",
            style = AppTheme.typography.BodyText2Regular,
            color = AppTheme.colors.muted,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = AppTheme.dimensions.spaces.x6)
        )

        Spacer(Modifier.height(AppTheme.dimensions.spaces.x5))

        Column(
            modifier = Modifier.padding(horizontal = AppTheme.dimensions.spaces.x6),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spaces.x3)
        ) {
            upgradeFeatures.forEach { feature ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(AppTheme.dimensions.sizes.x7)
                            .clip(RoundedCornerShape(AppTheme.dimensions.radius.medium))
                            .background(AppTheme.colors.accentAlpha12),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = feature.emoji, fontSize = 13.sp)
                    }
                    Spacer(Modifier.width(AppTheme.dimensions.spaces.x3))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Medium, color = AppTheme.colors.text)) {
                                append(feature.bold)
                            }
                            withStyle(SpanStyle(color = AppTheme.colors.soft)) {
                                append(feature.rest)
                            }
                        },
                        style = AppTheme.typography.BodyText2Regular
                    )
                }
            }
        }

        Spacer(Modifier.height(AppTheme.dimensions.spaces.x5))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(AppTheme.dimensions.radius.round))
                .background(AppTheme.colors.accentAlpha12)
                .border(AppTheme.dimensions.borders.veryLow, AppTheme.colors.accentAlpha40, RoundedCornerShape(AppTheme.dimensions.radius.round))
                .padding(horizontal = AppTheme.dimensions.spaces.x4, vertical = AppTheme.dimensions.spaces.x2)
        ) {
            Text(
                text = "€1.99 · One-time purchase",
                style = AppTheme.typography.BodyText2Medium,
                color = AppTheme.colors.accent
            )
        }

        Spacer(Modifier.height(AppTheme.dimensions.spaces.x4))

        Box(
            modifier = Modifier.padding(horizontal = AppTheme.dimensions.spaces.x6),
            contentAlignment = Alignment.Center
        ) {
            GradientButton(
                text = if (isLoading) "Processing…" else "Unlock Pro — €1.99",
                leadingEmoji = if (isLoading) null else "✨",
                enabled = !isLoading,
                onClick = { viewModel.purchase() },
                modifier = Modifier.fillMaxWidth()
            )
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(AppTheme.dimensions.sizes.x5),
                    color = AppTheme.colors.surface,
                    strokeWidth = AppTheme.dimensions.borders.low
                )
            }
        }

        Spacer(Modifier.height(AppTheme.dimensions.spaces.x3))

        Text(
            text = "Maybe Later",
            style = AppTheme.typography.BodyText2Regular,
            color = AppTheme.colors.muted,
            modifier = Modifier
                .clickable(enabled = !isLoading) { onDismiss() }
                .padding(AppTheme.dimensions.spaces.x3)
        )

        Spacer(Modifier.height(AppTheme.dimensions.spaces.x4))
    }
}
