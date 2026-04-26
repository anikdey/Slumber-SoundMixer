package com.slumber.soundmixer.presentation.onboarding

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slumber.soundmixer.ui.theme.AppTheme

@Composable
fun OrbitalAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "orbital")

    val rotation1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(20_000, easing = LinearEasing)),
        label = "ring1"
    )
    val rotation2 by infiniteTransition.animateFloat(
        initialValue = 360f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(14_000, easing = LinearEasing)),
        label = "ring2"
    )
    val rotation3 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(8_000, easing = LinearEasing)),
        label = "ring3"
    )

    Box(
        modifier = Modifier.size(AppTheme.dimensions.sizes.x35),
        contentAlignment = Alignment.Center
    ) {
        OrbitalRing(size = AppTheme.dimensions.sizes.x35, rotation = rotation1, dotColor = AppTheme.colors.accent)
        OrbitalRing(size = AppTheme.dimensions.sizes.x25, rotation = rotation2, dotColor = AppTheme.colors.accent2)
        OrbitalRing(size = AppTheme.dimensions.sizes.x16, rotation = rotation3, dotColor = null)

        Text(
            text = "🌙",
            fontSize = 36.sp
        )
    }
}

@Composable
fun OrbitalRing(size: Dp, rotation: Float, dotColor: Color?) {
    Box(
        modifier = Modifier
            .size(size)
            .rotate(rotation),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .then(Modifier.clip(CircleShape))
            )
        }

        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color.Transparent, Color.Transparent),
                            radius = size.value
                        )
                    )
            )
        }

        Canvas(modifier = Modifier.size(size)) {
            drawCircle(
                color = Color.White.copy(alpha = 0.12f),
                radius = (size.toPx() / 2) - 1,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        if (dotColor != null) {
            Box(
                modifier = Modifier
                    .size(AppTheme.dimensions.sizes.x2)
                    .clip(CircleShape)
                    .background(dotColor)
                    .align(Alignment.TopCenter)
            )
        }
    }
}
