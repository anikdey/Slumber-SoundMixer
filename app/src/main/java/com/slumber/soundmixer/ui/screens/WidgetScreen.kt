package com.slumber.soundmixer.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.slumber.soundmixer.R
import com.slumber.soundmixer.ui.theme.DMSans
import com.slumber.soundmixer.ui.theme.AppTheme
import com.slumber.soundmixer.ui.theme.Sora

@Composable
fun WidgetPreviewScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF141E46), AppTheme.colors.bg),
                    radius = 900f
                )
            )
    ) {
        StarsBackground()

        Box(
            modifier = Modifier
                .size(AppTheme.dimensions.sizes.x12)
                .align(Alignment.TopEnd)
                .padding(top = AppTheme.dimensions.spaces.x8, end = AppTheme.dimensions.spaces.x8)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFFF5F0DC), Color(0xFFC8B87A)),
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppTheme.dimensions.spaces.x4),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spaces.x4)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTheme.dimensions.spaces.x1, vertical = AppTheme.dimensions.spaces.x0),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "22:14",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "📶 🔋",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Widget2x2()

            Widget4x1()

            Text(
                text = stringResource(R.string.widget_screen_label),
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.3f),
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.6.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun Widget2x2() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.dimensions.radius.xxlarge))
            .background(Color(0xD90E1422))
            .border(AppTheme.dimensions.borders.veryLow, Color.White.copy(alpha = 0.1f), RoundedCornerShape(AppTheme.dimensions.radius.xxlarge))
            .padding(AppTheme.dimensions.spaces.x4)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.widget_label),
                fontFamily = DMSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                letterSpacing = 0.8.sp,
                color = AppTheme.colors.muted
            )
            Text(text = "🌙", fontSize = 14.sp)
        }

        Spacer(Modifier.height(AppTheme.dimensions.spaces.x3))

        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spaces.x2)) {
            WidgetSoundChip(stringResource(R.string.widget_chip_rain))
            WidgetSoundChip(stringResource(R.string.widget_chip_ocean))
        }

        Spacer(Modifier.height(AppTheme.dimensions.spaces.x4))

        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spaces.x2),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(AppTheme.dimensions.radius.large))
                    .background(
                        Brush.linearGradient(
                            listOf(AppTheme.colors.accent, AppTheme.colors.accent2)
                        )
                    )
                    .clickable { }
                    .padding(vertical = AppTheme.dimensions.spaces.x3),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "▶", fontSize = 11.sp, color = Color.White)
                    Spacer(Modifier.width(AppTheme.dimensions.spaces.x2))
                    Text(
                        text = stringResource(R.string.widget_btn_play),
                        fontFamily = Sora,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(AppTheme.dimensions.radius.large))
                    .background(AppTheme.colors.surfaceOverlay8)
                    .border(AppTheme.dimensions.borders.veryLow, AppTheme.colors.surfaceOverlay10, RoundedCornerShape(AppTheme.dimensions.radius.large))
                    .clickable { }
                    .padding(horizontal = AppTheme.dimensions.spaces.x4, vertical = AppTheme.dimensions.spaces.x3),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spaces.x1)
                ) {
                    Text(text = "⏱️", fontSize = 11.sp)
                    Text(
                        text = stringResource(R.string.widget_timer_duration),
                        fontFamily = DMSans,
                        fontSize = 11.sp,
                        color = AppTheme.colors.soft
                    )
                }
            }
        }
    }
}

@Composable
fun Widget4x1() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.dimensions.radius.xlarge))
            .background(Color(0xD90E1422))
            .border(AppTheme.dimensions.borders.veryLow, Color.White.copy(alpha = 0.1f), RoundedCornerShape(AppTheme.dimensions.radius.xlarge))
            .padding(horizontal = AppTheme.dimensions.spaces.x4, vertical = AppTheme.dimensions.spaces.x3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "🌧️", fontSize = 20.sp)

        Spacer(Modifier.width(AppTheme.dimensions.spaces.x3))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.widget_mix_name),
                fontFamily = Sora,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = AppTheme.colors.text
            )
            Spacer(Modifier.height(AppTheme.dimensions.sizes.x0))
            Text(
                text = stringResource(R.string.widget_last_used),
                fontFamily = DMSans,
                fontSize = 10.sp,
                color = AppTheme.colors.muted
            )
        }

        Box(
            modifier = Modifier
                .size(AppTheme.dimensions.sizes.x8)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(AppTheme.colors.accent, AppTheme.colors.accent2)
                    )
                )
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "▶", fontSize = 11.sp, color = Color.White)
        }
    }
}

@Composable
fun WidgetSoundChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(AppTheme.dimensions.radius.xxlarge))
            .background(AppTheme.colors.accentAlpha12)
            .border(AppTheme.dimensions.borders.veryLow, AppTheme.colors.accentAlpha25, RoundedCornerShape(AppTheme.dimensions.radius.xxlarge))
            .padding(horizontal = AppTheme.dimensions.spaces.x3, vertical = AppTheme.dimensions.spaces.x1)
    ) {
        Text(
            text = label,
            fontFamily = DMSans,
            fontSize = 11.sp,
            color = Color(0xFFAAC4F8)
        )
    }
}

@Composable
fun StarsBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0x0A5B8DEF), Color.Transparent),
                    radius = 600f
                )
            )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF070B14)
@Composable
fun WidgetPreviewScreenPreview() {
    WidgetPreviewScreen()
}
