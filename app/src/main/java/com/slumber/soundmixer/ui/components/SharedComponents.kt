package com.slumber.soundmixer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.slumber.soundmixer.R
import com.slumber.soundmixer.ui.theme.AppTheme
import com.slumber.soundmixer.ui.theme.accentGradient

enum class NavTab(val emoji: String, @StringRes val labelRes: Int) {
    Mixer("🎵", R.string.nav_mixer),
    Timer("⏱️", R.string.nav_timer),
    Mixes("💾", R.string.nav_mixes),
    Settings("⚙️", R.string.nav_settings)
}

@Composable
fun SleepBottomNav(
    selected: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppTheme.dimensions.sizes.x0)
                .background(AppTheme.colors.border)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppTheme.colors.surface),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavTab.entries.forEach { tab ->
                val isActive = tab == selected
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(AppTheme.dimensions.radius.large))
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = AppTheme.dimensions.spaces.x2)
                ) {
                    Text(
                        text = tab.emoji,
                        fontSize = 22.sp,
                        lineHeight = 24.sp
                    )
                    Spacer(Modifier.height(AppTheme.dimensions.spaces.x1))
                    Text(
                        text = stringResource(tab.labelRes),
                        style = AppTheme.typography.Caption,
                        color = if (isActive) AppTheme.colors.accent else AppTheme.colors.muted
                    )
                    Spacer(Modifier.height(AppTheme.dimensions.spaces.x1))
                    Box(
                        modifier = Modifier
                            .height(AppTheme.dimensions.sizes.x05)
                            .width(if (isActive) AppTheme.dimensions.sizes.x4 else AppTheme.dimensions.sizes.x0)
                            .clip(CircleShape)
                            .background(AppTheme.colors.accent)
                    )
                }
            }
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingEmoji: String? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(if (!enabled) Modifier.alpha(0.4f) else Modifier)
            .clip(RoundedCornerShape(AppTheme.dimensions.radius.xlarge))
            .background(AppTheme.colors.accentGradient)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = AppTheme.dimensions.spaces.x4),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = AppTheme.dimensions.spaces.x6)
        ) {
            if (leadingEmoji != null) {
                Text(text = leadingEmoji, fontSize = 16.sp)
                Spacer(Modifier.width(AppTheme.dimensions.spaces.x2))
            }
            Text(
                text = text,
                style = AppTheme.typography.HeadingMedium,
                color = Color.White
            )
        }
    }
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = AppTheme.typography.LabelCaps,
        color = AppTheme.colors.muted,
        modifier = modifier
    )
}

@Composable
fun SoundPill(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(AppTheme.dimensions.radius.xxlarge))
            .background(AppTheme.colors.surfaceOverlay5)
            .border(AppTheme.dimensions.borders.veryLow, AppTheme.colors.border, RoundedCornerShape(AppTheme.dimensions.radius.xxlarge))
            .padding(horizontal = AppTheme.dimensions.spaces.x3, vertical = AppTheme.dimensions.spaces.x1)
    ) {
        Text(
            text = label,
            style = AppTheme.typography.Caption,
            color = AppTheme.colors.soft
        )
    }
}

@Composable
fun SleepCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.dimensions.radius.xxlarge))
            .background(AppTheme.colors.card)
            .border(AppTheme.dimensions.borders.veryLow, AppTheme.colors.border, RoundedCornerShape(AppTheme.dimensions.radius.xxlarge))
            .padding(AppTheme.dimensions.spaces.x4),
        content = content
    )
}

@Composable
fun PageDots(total: Int, current: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spaces.x1)) {
        repeat(total) { i ->
            val isActive = i == current
            Box(
                modifier = Modifier
                    .height(AppTheme.dimensions.sizes.x1)
                    .width(if (isActive) AppTheme.dimensions.sizes.x4 else AppTheme.dimensions.sizes.x1)
                    .clip(RoundedCornerShape(AppTheme.dimensions.radius.small))
                    .background(
                        if (isActive) AppTheme.colors.accentGradient
                        else Brush.linearGradient(listOf(AppTheme.colors.muted, AppTheme.colors.muted))
                    )
            )
        }
    }
}
