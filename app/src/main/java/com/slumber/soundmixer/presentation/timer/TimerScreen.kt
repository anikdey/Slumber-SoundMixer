package com.slumber.soundmixer.presentation.timer

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import android.content.res.Configuration
import androidx.navigation.NavController
import com.slumber.soundmixer.R
import com.slumber.soundmixer.ui.components.SectionLabel
import com.slumber.soundmixer.ui.components.SleepBottomNav
import com.slumber.soundmixer.ui.theme.AppTheme
import com.slumber.soundmixer.ui.theme.DMSans
import com.slumber.soundmixer.ui.theme.Sora
import com.slumber.soundmixer.util.TimerConfig

data class TimerOption(val minutes: Int, val label: String)

private fun Long.toTimerDisplay(): String {
    val m = (this / 60_000L).toInt()
    val s = ((this % 60_000L) / 1_000L).toInt()
    return "%d:%02d".format(m, s)
}

@Composable
fun TimerScreen(
    navController: NavController,
    viewModel: TimerViewModel = hiltViewModel()
) {
    val selectedMinutes by viewModel.selectedMinutes.collectAsState()
    val fadeEnabled by viewModel.fadeEnabled.collectAsState()
    val timerMillisRemaining by viewModel.timerMillisRemaining.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val activeSounds by viewModel.activeSounds.collectAsState()

    val labelQuickNap   = stringResource(R.string.timer_preset_quick_nap)
    val labelLightSleep = stringResource(R.string.timer_preset_light_sleep)
    val labelDeepSleep  = stringResource(R.string.timer_preset_deep_sleep)
    val labelFullCycle  = stringResource(R.string.timer_preset_full_cycle)
    val timerOptions = remember {
        listOf(
            TimerOption(15, labelQuickNap),
            TimerOption(30, labelLightSleep),
            TimerOption(45, labelDeepSleep),
            TimerOption(60, labelFullCycle),
        )
    }

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    var showCustomDialog by remember { mutableStateOf(false) }

    if (showCustomDialog) {
        CustomDurationDialog(
            onConfirm = { minutes ->
                viewModel.selectMinutes(minutes)
                showCustomDialog = false
            },
            onDismiss = { showCustomDialog = false }
        )
    }

    val soundsText = if (activeSounds.isEmpty()) "No sounds selected"
    else activeSounds.values.joinToString(" · ") { "${it.sound.emoji} ${it.sound.name}" }

    val timerActive = timerMillisRemaining > 0L

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
        Column(
            modifier = Modifier.padding(
                start = AppTheme.dimensions.spaces.x4,
                end = AppTheme.dimensions.spaces.x4,
                top = AppTheme.dimensions.spaces.x5,
                bottom = AppTheme.dimensions.spaces.x5
            )
        ) {
            Text(
                text = stringResource(R.string.timer_title),
                style = AppTheme.typography.HeadingLarge,
                color = AppTheme.colors.text
            )
            Spacer(Modifier.height(AppTheme.dimensions.spaces.x1))
            Text(
                text = stringResource(R.string.timer_subtitle),
                style = AppTheme.typography.BodyText3Regular,
                color = AppTheme.colors.muted
            )
        }

        NowPlayingBar(
            soundsText = soundsText,
            isPlaying = isPlaying,
            timerText = if (timerActive) timerMillisRemaining.toTimerDisplay() else null,
            onTogglePlayPause = { viewModel.togglePlayPause() },
            modifier = Modifier.padding(horizontal = AppTheme.dimensions.spaces.x4)
        )

        Spacer(Modifier.height(AppTheme.dimensions.spaces.x5))

        Column(modifier = Modifier.padding(horizontal = AppTheme.dimensions.spaces.x4)) {
            SectionLabel(stringResource(R.string.timer_section_stop_after))
            Spacer(Modifier.height(AppTheme.dimensions.spaces.x3))

            if (isLandscape) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spaces.x3),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    timerOptions.forEach { opt ->
                        TimerChip(
                            option = opt,
                            isSelected = selectedMinutes == opt.minutes,
                            onClick = { viewModel.selectMinutes(opt.minutes) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spaces.x3),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spaces.x3),
                        modifier = Modifier.weight(1f)
                    ) {
                        timerOptions.filterIndexed { i, _ -> i % 2 == 0 }.forEach { opt ->
                            TimerChip(
                                option = opt,
                                isSelected = selectedMinutes == opt.minutes,
                                onClick = { viewModel.selectMinutes(opt.minutes) }
                            )
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spaces.x3),
                        modifier = Modifier.weight(1f)
                    ) {
                        timerOptions.filterIndexed { i, _ -> i % 2 == 1 }.forEach { opt ->
                            TimerChip(
                                option = opt,
                                isSelected = selectedMinutes == opt.minutes,
                                onClick = { viewModel.selectMinutes(opt.minutes) }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(AppTheme.dimensions.spaces.x3))

            TimerChip(
                option = TimerOption(0, stringResource(R.string.timer_custom_description)),
                isSelected = timerOptions.none { it.minutes == selectedMinutes },
                customLabel = if (timerOptions.none { it.minutes == selectedMinutes })
                    stringResource(R.string.duration_minutes, selectedMinutes)
                else stringResource(R.string.timer_custom_label),
                onClick = { showCustomDialog = true },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(AppTheme.dimensions.spaces.x4))

        FadeToggleRow(
            enabled = fadeEnabled,
            onToggle = { viewModel.setFadeEnabled(it) },
            modifier = Modifier.padding(horizontal = AppTheme.dimensions.spaces.x4)
        )

        Spacer(Modifier.height(AppTheme.dimensions.spaces.x4))

        Box(
            modifier = Modifier
                .padding(horizontal = AppTheme.dimensions.spaces.x4)
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppTheme.dimensions.radius.xlarge))
                .background(if (timerActive) AppTheme.colors.accentAlpha12 else AppTheme.colors.card2)
                .border(
                    AppTheme.dimensions.borders.veryLow,
                    if (timerActive) AppTheme.colors.accentAlpha40 else AppTheme.colors.accentAlpha25,
                    RoundedCornerShape(AppTheme.dimensions.radius.xlarge)
                )
                .clickable { if (timerActive) viewModel.cancelTimer() else viewModel.setTimer() }
                .padding(vertical = AppTheme.dimensions.spaces.x4),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = if (timerActive) "✕" else "⏱️", fontSize = 16.sp)
                Spacer(Modifier.width(AppTheme.dimensions.spaces.x2))
                Text(
                    text = if (timerActive)
                        "Cancel — ${timerMillisRemaining.toTimerDisplay()}"
                    else
                        stringResource(R.string.timer_btn_set, selectedMinutes),
                    style = AppTheme.typography.HeadingMedium,
                    color = AppTheme.colors.accent
                )
            }
        }

            Spacer(Modifier.height(AppTheme.dimensions.spaces.x4))
        } // end inner scrollable column

        SleepBottomNav(navController = navController)
    }
}

@Composable
fun CustomDurationDialog(
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var input by remember { mutableStateOf("") }
    val parsed = input.toIntOrNull()
    val isValid = parsed != null && parsed in TimerConfig.MIN_MINUTES..TimerConfig.MAX_MINUTES

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppTheme.colors.card,
        title = {
            Text(
                text = stringResource(R.string.timer_custom_label),
                style = AppTheme.typography.ButtonLabel,
                color = AppTheme.colors.text
            )
        },
        text = {
            Column {
                Text(
                    text = "${TimerConfig.MIN_MINUTES}–${TimerConfig.MAX_MINUTES} minutes",
                    style = AppTheme.typography.BodyText3Regular,
                    color = AppTheme.colors.muted
                )
                Spacer(Modifier.height(AppTheme.dimensions.spaces.x3))
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it.filter { c -> c.isDigit() }.take(3) },
                    placeholder = {
                        Text("e.g. 20", color = AppTheme.colors.muted, fontSize = 14.sp)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = input.isNotEmpty() && !isValid,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppTheme.colors.accent,
                        unfocusedBorderColor = AppTheme.colors.border,
                        focusedTextColor = AppTheme.colors.text,
                        unfocusedTextColor = AppTheme.colors.text,
                        errorBorderColor = MaterialTheme.colorScheme.error
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (isValid) onConfirm(parsed!!) },
                enabled = isValid
            ) {
                Text("Set", color = if (isValid) AppTheme.colors.accent else AppTheme.colors.muted)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = AppTheme.colors.muted)
            }
        }
    )
}

@Composable
fun NowPlayingBar(
    soundsText: String,
    isPlaying: Boolean,
    timerText: String?,
    onTogglePlayPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.dimensions.radius.xxlarge))
            .background(
                Brush.linearGradient(
                    listOf(AppTheme.colors.accentAlpha12, AppTheme.colors.accent2Alpha12)
                )
            )
            .border(AppTheme.dimensions.borders.veryLow, AppTheme.colors.accentAlpha25, RoundedCornerShape(AppTheme.dimensions.radius.xxlarge))
            .padding(AppTheme.dimensions.spaces.x4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedWaveBars(isPlaying = isPlaying)

        Spacer(Modifier.width(AppTheme.dimensions.spaces.x3))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = soundsText,
                style = AppTheme.typography.BodyText2Medium,
                color = AppTheme.colors.text
            )
            if (timerText != null) {
                Spacer(Modifier.height(AppTheme.dimensions.spaces.x05))
                Text(
                    text = timerText,
                    style = AppTheme.typography.LabelTiny,
                    color = AppTheme.colors.accent
                )
            }
        }

        Box(
            modifier = Modifier
                .size(AppTheme.dimensions.sizes.x8)
                .clip(CircleShape)
                .background(AppTheme.colors.accentAlpha12)
                .clickable { onTogglePlayPause() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = if (isPlaying) "⏸" else "▶", fontSize = 13.sp)
        }
    }
}

@Composable
fun AnimatedWaveBars(isPlaying: Boolean = true) {
    val delays = listOf(0, 150, 300, 100, 250)
    val heights = listOf(8, 16, 10, 18, 12)

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spaces.x05),
        modifier = Modifier.height(AppTheme.dimensions.sizes.x5)
    ) {
        delays.forEachIndexed { i, delay ->
            val infiniteTransition = rememberInfiniteTransition(label = "wave$i")
            val animatedScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 0.4f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1000, delayMillis = delay, easing = EaseInOut),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "waveScale$i"
            )
            val scale = if (isPlaying) animatedScale else 0.4f
            Box(
                modifier = Modifier
                    .width(AppTheme.dimensions.sizes.x1)
                    .height((heights[i] * scale).dp)
                    .clip(RoundedCornerShape(AppTheme.dimensions.radius.xsmall))
                    .background(AppTheme.colors.accent)
            )
        }
    }
}

@Composable
fun TimerChip(
    option: TimerOption,
    isSelected: Boolean,
    customLabel: String? = null,
    compact: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor     = if (isSelected) AppTheme.colors.accentAlpha12 else AppTheme.colors.card
    val borderColor = if (isSelected) AppTheme.colors.accentAlpha40 else AppTheme.colors.border

    val vertPadding  = if (compact) AppTheme.dimensions.spaces.x2 else AppTheme.dimensions.spaces.x4
    val horizPadding = if (compact) AppTheme.dimensions.spaces.x3 else AppTheme.dimensions.spaces.x4
    val radioSize    = if (compact) AppTheme.dimensions.sizes.x3  else AppTheme.dimensions.sizes.x4
    val dotSize      = if (compact) AppTheme.dimensions.sizes.x05 else AppTheme.dimensions.sizes.x1
    val labelSize    = if (compact) 12.sp else 14.sp
    val subLabelSize = if (compact) 9.sp  else 10.sp

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(AppTheme.dimensions.radius.xlarge))
            .background(bgColor)
            .border(AppTheme.dimensions.borders.veryLow, borderColor, RoundedCornerShape(AppTheme.dimensions.radius.xlarge))
            .clickable(onClick = onClick)
            .padding(horizontal = horizPadding, vertical = vertPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(radioSize)
                .clip(CircleShape)
                .background(Color.Transparent)
                .border(
                    AppTheme.dimensions.borders.low,
                    if (isSelected) AppTheme.colors.accent else AppTheme.colors.muted,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .clip(CircleShape)
                        .background(AppTheme.colors.accent)
                )
            }
        }

        Spacer(Modifier.width(AppTheme.dimensions.spaces.x2))

        Column {
            Text(
                text = customLabel ?: stringResource(R.string.duration_minutes, option.minutes),
                fontFamily = Sora,
                fontWeight = FontWeight.SemiBold,
                fontSize = labelSize,
                color = if (isSelected) AppTheme.colors.accent else AppTheme.colors.text
            )
            if (option.label.isNotEmpty()) {
                Spacer(Modifier.height(AppTheme.dimensions.spaces.x05))
                Text(
                    text = option.label,
                    fontFamily = DMSans,
                    fontSize = subLabelSize,
                    color = AppTheme.colors.muted
                )
            }
        }
    }
}

@Composable
fun FadeToggleRow(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.dimensions.radius.xlarge))
            .background(AppTheme.colors.card)
            .border(AppTheme.dimensions.borders.veryLow, AppTheme.colors.border, RoundedCornerShape(AppTheme.dimensions.radius.xlarge))
            .padding(horizontal = AppTheme.dimensions.spaces.x4, vertical = AppTheme.dimensions.spaces.x4),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = stringResource(R.string.timer_fade_title),
                style = AppTheme.typography.BodyText2Medium,
                color = AppTheme.colors.text
            )
            Spacer(Modifier.height(AppTheme.dimensions.spaces.x05))
            Text(
                text = stringResource(R.string.timer_fade_subtitle),
                style = AppTheme.typography.LabelTiny,
                color = AppTheme.colors.muted
            )
        }

        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AppTheme.colors.accent,
                uncheckedThumbColor = AppTheme.colors.muted,
                uncheckedTrackColor = AppTheme.colors.card2
            )
        )
    }
}

