package com.slumber.soundmixer.presentation.createmix

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.slumber.soundmixer.R
import com.slumber.soundmixer.domain.model.Sound
import com.slumber.soundmixer.domain.model.SoundRegistry
import com.slumber.soundmixer.presentation.mixer.ActiveMixPanel
import com.slumber.soundmixer.presentation.mixer.SoundCard
import com.slumber.soundmixer.ui.components.GradientButton
import com.slumber.soundmixer.ui.components.SectionLabel
import com.slumber.soundmixer.ui.theme.AppTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CreateMixScreen(
    onClose: () -> Unit,
    onShowUpgrade: () -> Unit,
    viewModel: CreateMixViewModel = hiltViewModel()
) {
    val mixName by viewModel.mixName.collectAsState()
    val previewSounds by viewModel.previewSounds.collectAsState()
    val isPreviewPlaying by viewModel.isPreviewPlaying.collectAsState()
    val isPro by viewModel.isPro.collectAsState()
    val canSave by viewModel.canSave.collectAsState()
    val nameError by viewModel.nameError.collectAsState()
    val selectedTimerMinutes by viewModel.selectedTimerMinutes.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigateToUpgrade.collectLatest { onShowUpgrade() }
    }
    LaunchedEffect(Unit) {
        viewModel.mixSaved.collectLatest { onClose() }
    }

    val context = LocalContext.current

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
                        bottom = AppTheme.dimensions.spaces.x3
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "New Mix",
                    style = AppTheme.typography.HeadingLarge,
                    color = AppTheme.colors.text
                )
                Box(
                    modifier = Modifier
                        .size(AppTheme.dimensions.sizes.x10)
                        .clip(RoundedCornerShape(AppTheme.dimensions.radius.large))
                        .background(AppTheme.colors.card2)
                        .border(
                            AppTheme.dimensions.borders.veryLow,
                            AppTheme.colors.border,
                            RoundedCornerShape(AppTheme.dimensions.radius.large)
                        )
                        .clickable { onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("✕", fontSize = 14.sp, color = AppTheme.colors.muted)
                }
            }

            OutlinedTextField(
                value = mixName,
                onValueChange = { viewModel.setMixName(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTheme.dimensions.spaces.x4),
                placeholder = {
                    Text(
                        "Mix name",
                        style = AppTheme.typography.ButtonLabel,
                        color = AppTheme.colors.muted
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                textStyle = AppTheme.typography.ButtonLabel.copy(color = AppTheme.colors.text),
                isError = nameError,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppTheme.colors.accent,
                    unfocusedBorderColor = AppTheme.colors.border,
                    errorBorderColor = AppTheme.colors.error,
                    focusedTextColor = AppTheme.colors.text,
                    unfocusedTextColor = AppTheme.colors.text,
                    cursorColor = AppTheme.colors.accent
                ),
                shape = RoundedCornerShape(AppTheme.dimensions.radius.xlarge)
            )
            if (nameError) {
                Spacer(Modifier.height(AppTheme.dimensions.spaces.x1))
                Text(
                    text = "A mix with this name already exists",
                    style = AppTheme.typography.LabelTiny,
                    color = AppTheme.colors.error,
                    modifier = Modifier.padding(horizontal = AppTheme.dimensions.spaces.x5)
                )
            }

            Spacer(Modifier.height(AppTheme.dimensions.spaces.x4))

            Column(
                modifier = Modifier.padding(horizontal = AppTheme.dimensions.spaces.x4),
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spaces.x3)
            ) {
                SectionLabel("Choose sounds")
                allSounds.chunked(3).forEach { rowSounds ->
                    Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spaces.x3)) {
                        rowSounds.forEach { sound ->
                            SoundCard(
                                sound = sound,
                                isActive = sound.id in previewSounds,
                                activeIndex = previewSounds.keys.toList().indexOf(sound.id),
                                isLocked = sound.isPro && !isPro,
                                onClick = { viewModel.toggleSound(sound.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(3 - rowSounds.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }

            Spacer(Modifier.height(AppTheme.dimensions.spaces.x4))

            ActiveMixPanel(
                allSounds = allSounds,
                activeSounds = previewSounds,
                onVolumeChange = { id, vol -> viewModel.setVolume(id, vol) },
                modifier = Modifier.padding(horizontal = AppTheme.dimensions.spaces.x4)
            )

            Spacer(Modifier.height(AppTheme.dimensions.spaces.x3))

            GradientButton(
                text = if (isPreviewPlaying) "⏸  Pause" else "▶  Preview Mix",
                onClick = {
                    if (previewSounds.isEmpty()) {
                        android.widget.Toast.makeText(
                            context,
                            "Select a sound first",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.togglePreviewPlayPause()
                    }
                },
                modifier = Modifier.padding(horizontal = AppTheme.dimensions.spaces.x4)
            )

            Spacer(Modifier.height(AppTheme.dimensions.spaces.x4))

            Column(modifier = Modifier.padding(horizontal = AppTheme.dimensions.spaces.x4)) {
                SectionLabel("Timer (optional)")
                Spacer(Modifier.height(AppTheme.dimensions.spaces.x3))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spaces.x2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MixTimerChip("Off",   isSelected = selectedTimerMinutes == null, onClick = { viewModel.selectTimerMinutes(null) }, modifier = Modifier.weight(1f))
                    MixTimerChip("15m",   isSelected = selectedTimerMinutes == 15,   onClick = { viewModel.selectTimerMinutes(15) },   modifier = Modifier.weight(1f))
                    MixTimerChip("30m",   isSelected = selectedTimerMinutes == 30,   onClick = { viewModel.selectTimerMinutes(30) },   modifier = Modifier.weight(1f))
                    MixTimerChip("45m",   isSelected = selectedTimerMinutes == 45,   onClick = { viewModel.selectTimerMinutes(45) },   modifier = Modifier.weight(1f))
                    MixTimerChip("60m",   isSelected = selectedTimerMinutes == 60,   onClick = { viewModel.selectTimerMinutes(60) },   modifier = Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(AppTheme.dimensions.spaces.x3))

            GradientButton(
                text = "Save Mix",
                enabled = canSave,
                onClick = { viewModel.saveMix() },
                modifier = Modifier
                    .padding(horizontal = AppTheme.dimensions.spaces.x4)
                    .then(
                        if (canSave) Modifier.shadow(
                            elevation = AppTheme.dimensions.elevations.x4,
                            shape = RoundedCornerShape(AppTheme.dimensions.radius.xlarge),
                            ambientColor = AppTheme.colors.accent.copy(alpha = 0.4f),
                            spotColor = AppTheme.colors.accent.copy(alpha = 0.4f)
                        ) else Modifier
                    )
            )

            Spacer(Modifier.height(AppTheme.dimensions.spaces.x4))
        }
    }
}

@Composable
fun MixTimerChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(AppTheme.dimensions.radius.large))
            .background(if (isSelected) AppTheme.colors.accentAlpha12 else AppTheme.colors.card)
            .border(
                AppTheme.dimensions.borders.veryLow,
                if (isSelected) AppTheme.colors.accentAlpha40 else AppTheme.colors.border,
                RoundedCornerShape(AppTheme.dimensions.radius.large)
            )
            .clickable(onClick = onClick)
            .padding(vertical = AppTheme.dimensions.spaces.x2),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = AppTheme.typography.BodyText3Medium,
            color = if (isSelected) AppTheme.colors.accent else AppTheme.colors.muted
        )
    }
}
