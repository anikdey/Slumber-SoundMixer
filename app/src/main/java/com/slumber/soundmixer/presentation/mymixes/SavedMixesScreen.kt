package com.slumber.soundmixer.presentation.mymixes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.slumber.soundmixer.R
import com.slumber.soundmixer.data.db.MixWithSounds
import com.slumber.soundmixer.domain.model.SoundRegistry
import com.slumber.soundmixer.presentation.createmix.CreateMixScreen
import com.slumber.soundmixer.ui.components.NavTab
import com.slumber.soundmixer.ui.components.SleepBottomNav
import com.slumber.soundmixer.ui.components.SoundPill
import com.slumber.soundmixer.ui.theme.AppTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SavedMixesScreen(
    onTabSelected: (NavTab) -> Unit = {},
    onShowUpgrade: () -> Unit = {},
    viewModel: SavedMixesViewModel = hiltViewModel()
) {
    val mixes by viewModel.mixes.collectAsState()
    val isPro by viewModel.isPro.collectAsState()
    var showCreateMix by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.navigateToUpgrade.collectLatest { onShowUpgrade() }
    }

    if (showCreateMix) {
        CreateMixScreen(
            onClose = { showCreateMix = false },
            onShowUpgrade = onShowUpgrade
        )
        return
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
                        bottom = AppTheme.dimensions.spaces.x5
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.mixes_title),
                        style = AppTheme.typography.HeadingLarge,
                        color = AppTheme.colors.text
                    )
                    Spacer(Modifier.height(AppTheme.dimensions.spaces.x1))
                    Text(
                        text = stringResource(R.string.mixes_subtitle),
                        style = AppTheme.typography.BodyText3Regular,
                        color = AppTheme.colors.muted
                    )
                }

                Box(
                    modifier = Modifier
                        .size(AppTheme.dimensions.sizes.x8)
                        .clip(RoundedCornerShape(AppTheme.dimensions.radius.large))
                        .background(AppTheme.colors.accentAlpha12)
                        .border(
                            AppTheme.dimensions.borders.veryLow,
                            AppTheme.colors.accentAlpha25,
                            RoundedCornerShape(AppTheme.dimensions.radius.large)
                        )
                        .clickable {
                            viewModel.onAddMixClicked { showCreateMix = true }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        fontSize = 20.sp,
                        color = AppTheme.colors.accent,
                        fontWeight = FontWeight.Light
                    )
                }
            }

            if (mixes.isEmpty()) {
                EmptyMixesState(
                    onCreateMix = { viewModel.onAddMixClicked { showCreateMix = true } }
                )
            } else {
                Column(
                    modifier = Modifier.padding(horizontal = AppTheme.dimensions.spaces.x4),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spaces.x3)
                ) {
                    mixes.forEach { mix ->
                        MixCard(
                            mix = mix,
                            onPlay = { viewModel.playMix(mix) },
                            onDelete = { viewModel.deleteMix(mix.mix) }
                        )
                    }

                    if (!isPro) {
                        Spacer(Modifier.height(AppTheme.dimensions.spaces.x1))
                        ProBanner(onClick = onShowUpgrade)
                    }

                    Spacer(Modifier.height(AppTheme.dimensions.spaces.x2))
                }
            }
        }

        SleepBottomNav(
            selected = NavTab.Mixes,
            onTabSelected = onTabSelected
        )
    }
}

@Composable
fun EmptyMixesState(onCreateMix: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AppTheme.dimensions.spaces.x6),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spaces.x3)
    ) {
        Text("💾", fontSize = 40.sp)
        Spacer(Modifier.height(AppTheme.dimensions.spaces.x1))
        Text(
            text = "No saved mixes yet",
            style = AppTheme.typography.ButtonLabel,
            color = AppTheme.colors.text
        )
        Text(
            text = "Tap + to create your first mix",
            style = AppTheme.typography.BodyText2Regular,
            color = AppTheme.colors.muted
        )
        Spacer(Modifier.height(AppTheme.dimensions.spaces.x2))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(AppTheme.dimensions.radius.xlarge))
                .background(AppTheme.colors.accentAlpha12)
                .border(
                    AppTheme.dimensions.borders.veryLow,
                    AppTheme.colors.accentAlpha25,
                    RoundedCornerShape(AppTheme.dimensions.radius.xlarge)
                )
                .clickable { onCreateMix() }
                .padding(
                    horizontal = AppTheme.dimensions.spaces.x6,
                    vertical = AppTheme.dimensions.spaces.x3
                )
        ) {
            Text(
                text = "Create Mix",
                style = AppTheme.typography.HeadingMedium,
                color = AppTheme.colors.accent
            )
        }
    }
}

@Composable
fun MixCard(
    mix: MixWithSounds,
    onPlay: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    val soundLabels = mix.sounds.mapNotNull { mixSound ->
        SoundRegistry.findById(mixSound.soundId)?.let { meta ->
            "${meta.emoji} ${meta.id.replaceFirstChar { it.uppercase() }} ${(mixSound.volume * 100).toInt()}%"
        }
    }

    val accentColors = listOf(
        Color(0xFF5B8DEF) to Color(0xFF7B6CF6),
        Color(0xFF4ECDC4) to Color(0xFF44A08D),
        Color(0xFFF7A83A) to Color(0xFFF06B38),
        Color(0xFFE06C75) to Color(0xFFBE4B83),
    )
    val colorPair = accentColors[(mix.mix.id % accentColors.size).toInt()]

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.dimensions.radius.xxlarge))
            .background(AppTheme.colors.card)
            .border(
                AppTheme.dimensions.borders.veryLow,
                AppTheme.colors.border,
                RoundedCornerShape(AppTheme.dimensions.radius.xxlarge)
            )
    ) {
        Box(
            modifier = Modifier
                .width(AppTheme.dimensions.sizes.x1)
                .fillMaxHeight()
                .background(Brush.verticalGradient(listOf(colorPair.first, colorPair.second)))
        )

        Column(modifier = Modifier.padding(AppTheme.dimensions.spaces.x4)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(AppTheme.dimensions.sizes.x8)
                            .clip(RoundedCornerShape(AppTheme.dimensions.radius.large))
                            .background(
                                Brush.linearGradient(listOf(colorPair.first.copy(alpha = 0.15f), colorPair.second.copy(alpha = 0.15f)))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎵", fontSize = 16.sp)
                    }

                    Spacer(Modifier.width(AppTheme.dimensions.spaces.x3))

                    Column {
                        Text(
                            text = mix.mix.name,
                            style = AppTheme.typography.HeadingMedium,
                            color = AppTheme.colors.text
                        )
                        Spacer(Modifier.height(AppTheme.dimensions.spaces.x05))
                        val soundCount = if (mix.sounds.size == 1) "1 sound" else "${mix.sounds.size} sounds"
                        val timerLabel = mix.mix.timerMinutes?.let { " · ${it} min" } ?: ""
                        Text(
                            text = "$soundCount$timerLabel",
                            style = AppTheme.typography.LabelTiny,
                            color = AppTheme.colors.muted
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spaces.x2)) {
                    Box(
                        modifier = Modifier
                            .size(AppTheme.dimensions.sizes.x8)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(colorPair.first, colorPair.second))
                            )
                            .clickable { onPlay() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("▶", fontSize = 11.sp, color = Color.White)
                    }

                    Box(
                        modifier = Modifier
                            .size(AppTheme.dimensions.sizes.x8)
                            .clip(CircleShape)
                            .background(AppTheme.colors.card2)
                            .clickable { showMenu = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⋯", fontSize = 14.sp, color = AppTheme.colors.muted)

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(AppTheme.colors.card)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Delete",
                                        style = AppTheme.typography.LabelMedium,
                                        color = Color(0xFFE06C75)
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }
            }

            if (soundLabels.isNotEmpty()) {
                Spacer(Modifier.height(AppTheme.dimensions.spaces.x3))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spaces.x2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    soundLabels.forEach { label -> SoundPill(label = label) }
                }
            }
        }
    }
}

@Composable
fun ProBanner(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.dimensions.radius.xlarge))
            .background(
                Brush.linearGradient(listOf(Color(0x1FF0A045), Color(0x14F06038)))
            )
            .border(
                AppTheme.dimensions.borders.veryLow,
                Color(0x4DF0A045),
                RoundedCornerShape(AppTheme.dimensions.radius.xlarge)
            )
            .clickable { onClick() }
            .padding(AppTheme.dimensions.spaces.x4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("✨", fontSize = 20.sp)
        Spacer(Modifier.width(AppTheme.dimensions.spaces.x3))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.pro_title),
                style = AppTheme.typography.BodyText3Bold,
                color = AppTheme.colors.pro
            )
            Spacer(Modifier.height(AppTheme.dimensions.spaces.x05))
            Text(
                text = stringResource(R.string.pro_description),
                style = AppTheme.typography.LabelTiny,
                color = AppTheme.colors.muted
            )
        }
        Text(
            text = stringResource(R.string.pro_price),
            style = AppTheme.typography.HeadingSmall,
            color = AppTheme.colors.pro
        )
    }
}
