package com.slumber.soundmixer.presentation.mixer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.slumber.soundmixer.R
import com.slumber.soundmixer.domain.model.Sound
import com.slumber.soundmixer.domain.model.SoundRegistry
import com.slumber.soundmixer.ui.components.GradientButton
import com.slumber.soundmixer.ui.components.SectionLabel
import com.slumber.soundmixer.ui.components.SleepBottomNav
import com.slumber.soundmixer.ui.theme.AppTheme
import com.slumber.soundmixer.util.showToast

@Composable
fun MixerScreen(
    navController: NavController,
    onShowUpgrade: () -> Unit = {},
    viewModel: MixerViewModel = hiltViewModel()
) {
    val activeSounds by viewModel.activeSounds.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val isPro by viewModel.isPro.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigateToUpgrade.collectLatest { onShowUpgrade() }
    }

    val allSounds = SoundRegistry.all.map { meta ->
        Sound(meta.id, meta.emoji, stringResource(meta.nameRes), meta.resId, meta.isPro)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.surface)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = AppTheme.dimensions.spaces.x4,
                        end = AppTheme.dimensions.spaces.x4,
                        top = AppTheme.dimensions.spaces.x5,
                        bottom = AppTheme.dimensions.spaces.x4
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.mixer_greeting),
                        style = AppTheme.typography.HeadingLarge,
                        color = AppTheme.colors.text
                    )
                    Spacer(Modifier.height(AppTheme.dimensions.spaces.x1))
                    Text(
                        text = stringResource(R.string.mixer_subtitle),
                        style = AppTheme.typography.BodyText3Regular,
                        color = AppTheme.colors.muted
                    )
                }

                Box(
                    modifier = Modifier
                        .size(AppTheme.dimensions.sizes.x10)
                        .clip(RoundedCornerShape(AppTheme.dimensions.radius.large))
                        .background(AppTheme.colors.card2)
                        .border(
                            AppTheme.dimensions.borders.veryLow,
                            AppTheme.colors.accentAlpha25,
                            RoundedCornerShape(AppTheme.dimensions.radius.large)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🌙", fontSize = 18.sp)
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = AppTheme.dimensions.spaces.x4),
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spaces.x3)
            ) {
                allSounds.chunked(3).forEach { rowSounds ->
                    Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spaces.x3)) {
                        rowSounds.forEach { sound ->
                            SoundCard(
                                sound = sound,
                                isActive = sound.id in activeSounds,
                                activeIndex = activeSounds.keys.toList().indexOf(sound.id),
                                isLocked = sound.isPro && !isPro,
                                onClick = { viewModel.toggleSound(sound) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(3 - rowSounds.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(AppTheme.dimensions.spaces.x4))

            ActiveMixPanel(
                allSounds = allSounds,
                activeSounds = activeSounds,
                onVolumeChange = { id, vol -> viewModel.setVolume(id, vol) },
                modifier = Modifier.padding(horizontal = AppTheme.dimensions.spaces.x4)
            )

            Spacer(Modifier.height(AppTheme.dimensions.spaces.x3))

            val context = LocalContext.current
            val toastMessageNoSound = stringResource(R.string.mixer_toast_no_sounds)
            GradientButton(
                text = stringResource(if (isPlaying) R.string.mixer_btn_pause else R.string.mixer_btn_play),
                onClick = {
                    if (activeSounds.isEmpty()) {
                        context.showToast(toastMessageNoSound)
                    } else {
                        viewModel.togglePlayPause()
                    }
                },
                modifier = Modifier
                    .padding(horizontal = AppTheme.dimensions.spaces.x4)
                    .shadow(
                        elevation = AppTheme.dimensions.elevations.x4,
                        shape = RoundedCornerShape(AppTheme.dimensions.radius.xlarge),
                        ambientColor = AppTheme.colors.accent.copy(alpha = 0.4f),
                        spotColor = AppTheme.colors.accent.copy(alpha = 0.4f)
                    )
            )

            Spacer(Modifier.height(AppTheme.dimensions.spaces.x4))
        }

        SleepBottomNav(navController = navController)
    }
}

@Composable
fun SoundCard(
    sound: Sound,
    isActive: Boolean,
    activeIndex: Int,
    modifier: Modifier = Modifier,
    isLocked: Boolean = false,
    onClick: () -> Unit,
) {
    val useBlue   = isActive && activeIndex == 0
    val usePurple = isActive && activeIndex >= 1

    val bgColor = when {
        useBlue   -> AppTheme.colors.accentAlpha12
        usePurple -> AppTheme.colors.accent2Alpha12
        else      -> AppTheme.colors.card
    }
    val borderColor = when {
        useBlue   -> AppTheme.colors.accentAlpha40
        usePurple -> AppTheme.colors.accent2Alpha25
        else      -> AppTheme.colors.border
    }
    val nameColor = when {
        useBlue   -> AppTheme.colors.accent
        usePurple -> AppTheme.colors.accent2
        else      -> AppTheme.colors.soft
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(AppTheme.dimensions.radius.xlarge))
            .background(bgColor)
            .border(AppTheme.dimensions.borders.veryLow, borderColor, RoundedCornerShape(AppTheme.dimensions.radius.xlarge))
            .alpha(if (isLocked) 0.5f else 1f)
            .clickable(onClick = onClick)
            .padding(vertical = AppTheme.dimensions.spaces.x3, horizontal = AppTheme.dimensions.spaces.x2)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = sound.emoji, fontSize = 24.sp, lineHeight = 24.sp)
            Spacer(Modifier.height(AppTheme.dimensions.spaces.x2))
            Text(
                text = sound.name,
                style = AppTheme.typography.Caption,
                color = nameColor
            )
        }

        if (isLocked) {
            Text(
                text = "🔒",
                fontSize = 8.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(AppTheme.dimensions.spaces.x2)
            )
        }
    }
}

@Composable
fun ActiveMixPanel(
    allSounds: List<Sound>,
    activeSounds: Map<String, Float>,
    onVolumeChange: (String, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.dimensions.radius.xxlarge))
            .background(AppTheme.colors.card)
            .border(AppTheme.dimensions.borders.veryLow, AppTheme.colors.border, RoundedCornerShape(AppTheme.dimensions.radius.xxlarge))
            .padding(horizontal = AppTheme.dimensions.spaces.x4, vertical = AppTheme.dimensions.spaces.x3)
    ) {
        SectionLabel(stringResource(R.string.mixer_section_active_mix))
        Spacer(Modifier.height(AppTheme.dimensions.spaces.x3))

        if (activeSounds.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AppTheme.dimensions.spaces.x3),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.mixer_empty_mix),
                    style = AppTheme.typography.BodyText3Regular,
                    color = AppTheme.colors.muted
                )
            }
        }

        activeSounds.entries.forEachIndexed { index, (id, volume) ->
            val sound = allSounds.find { it.id == id } ?: return@forEachIndexed
            val isFirst = index == 0
            val thumbColor = if (isFirst) AppTheme.colors.accent else AppTheme.colors.accent2

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = sound.emoji, fontSize = 14.sp)
                Spacer(Modifier.width(AppTheme.dimensions.spaces.x3))
                Text(
                    text = sound.name,
                    style = AppTheme.typography.LabelTiny,
                    color = AppTheme.colors.soft,
                    modifier = Modifier.width(AppTheme.dimensions.sizes.x11)
                )

                Slider(
                    value = volume,
                    onValueChange = { onVolumeChange(id, it) },
                    modifier = Modifier
                        .weight(1f)
                        .height(AppTheme.dimensions.sizes.x5),
                    colors = SliderDefaults.colors(
                        thumbColor = thumbColor,
                        activeTrackColor = thumbColor,
                        inactiveTrackColor = AppTheme.colors.surfaceOverlay8
                    )
                )

                Spacer(Modifier.width(AppTheme.dimensions.spaces.x2))
                Text(
                    text = "${(volume * 100).toInt()}%",
                    style = AppTheme.typography.Caption,
                    color = AppTheme.colors.muted,
                    modifier = Modifier.width(AppTheme.dimensions.sizes.x7)
                )
            }

            if (index < activeSounds.size - 1) Spacer(Modifier.height(AppTheme.dimensions.spaces.x2))
        }
    }
}

