package com.slumber.soundmixer.presentation.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.slumber.soundmixer.R
import com.slumber.soundmixer.ui.components.NavTab
import com.slumber.soundmixer.ui.components.SectionLabel
import com.slumber.soundmixer.ui.components.SleepBottomNav
import com.slumber.soundmixer.ui.theme.AppTheme
import com.slumber.soundmixer.util.AppConfig

@Composable
fun SettingsScreen(
    onTabSelected: (NavTab) -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val isPro by viewModel.isPro.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isRestoring by viewModel.isRestoring.collectAsState()
    val context = LocalContext.current

    val versionName = remember {
        try { context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0" }
        catch (e: Exception) { "1.0" }
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
        Column(
            modifier = Modifier.padding(
                start = AppTheme.dimensions.spaces.x4,
                end = AppTheme.dimensions.spaces.x4,
                top = AppTheme.dimensions.spaces.x5,
                bottom = AppTheme.dimensions.spaces.x5
            )
        ) {
            Text(
                text = stringResource(R.string.settings_title),
                style = AppTheme.typography.HeadingLarge,
                color = AppTheme.colors.text
            )
            Spacer(Modifier.height(AppTheme.dimensions.spaces.x1))
            Text(
                text = stringResource(R.string.settings_subtitle),
                style = AppTheme.typography.BodyText3Regular,
                color = AppTheme.colors.muted
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = AppTheme.dimensions.spaces.x4),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spaces.x4)
        ) {
            SectionLabel("Display")
            SettingsGroup {
                SettingsToggleRow(
                    emoji = if (isDarkMode) "🌙" else "☀️",
                    title = stringResource(R.string.settings_dark_mode_title),
                    subtitle = if (isDarkMode) stringResource(R.string.settings_dark_mode_on)
                               else stringResource(R.string.settings_dark_mode_off),
                    checked = isDarkMode,
                    onToggle = { viewModel.toggleDarkMode() }
                )
            }

            SectionLabel("Pro")
            SettingsGroup {
                SettingsRow(
                    emoji = "✨",
                    title = stringResource(R.string.settings_restore_title),
                    subtitle = when {
                        isRestoring -> "Checking purchases…"
                        isPro -> stringResource(R.string.settings_restore_already_pro)
                        else -> stringResource(R.string.settings_restore_subtitle)
                    },
                    showChevron = !isPro && !isRestoring,
                    onClick = { if (!isRestoring) viewModel.restorePurchases() }
                )
            }

            SectionLabel("Support")
            SettingsGroup {
                SettingsRow(
                    emoji = "⭐",
                    title = stringResource(R.string.settings_rate_title),
                    subtitle = stringResource(R.string.settings_rate_subtitle),
                    onClick = {
                        try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}")))
                        } catch (e: ActivityNotFoundException) {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")))
                        }
                    }
                )
                SettingsDivider()
                SettingsRow(
                    emoji = "🔒",
                    title = stringResource(R.string.settings_privacy_title),
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.PRIVACY_POLICY_URL)))
                    }
                )
                SettingsDivider()
                SettingsRow(
                    emoji = "📄",
                    title = stringResource(R.string.settings_terms_title),
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.TERMS_URL)))
                    }
                )
            }

            SectionLabel("About")
            SettingsGroup {
                SettingsRow(
                    emoji = "📱",
                    title = stringResource(R.string.settings_version_title),
                    subtitle = versionName,
                    showChevron = false,
                    onClick = {}
                )
            }
        }

            Spacer(Modifier.height(AppTheme.dimensions.spaces.x4))
        } // end inner scrollable column

        SleepBottomNav(
            selected = NavTab.Settings,
            onTabSelected = onTabSelected
        )
    }
}

@Composable
fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.dimensions.radius.xxlarge))
            .background(AppTheme.colors.card)
            .border(AppTheme.dimensions.borders.veryLow, AppTheme.colors.border, RoundedCornerShape(AppTheme.dimensions.radius.xxlarge)),
        content = content
    )
}

@Composable
fun SettingsRow(
    emoji: String,
    title: String,
    subtitle: String? = null,
    showChevron: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = AppTheme.dimensions.spaces.x4, vertical = AppTheme.dimensions.spaces.x3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(AppTheme.dimensions.sizes.x7)
                .clip(RoundedCornerShape(AppTheme.dimensions.radius.medium))
                .background(AppTheme.colors.accentAlpha12),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 14.sp)
        }

        Spacer(Modifier.width(AppTheme.dimensions.spaces.x3))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = AppTheme.typography.LabelMedium,
                color = AppTheme.colors.text
            )
            if (subtitle != null) {
                Spacer(Modifier.height(AppTheme.dimensions.spaces.x05))
                Text(
                    text = subtitle,
                    style = AppTheme.typography.LabelTiny,
                    color = AppTheme.colors.muted
                )
            }
        }

        if (showChevron) {
            Text(text = "›", fontSize = 18.sp, color = AppTheme.colors.muted)
        }
    }
}

@Composable
fun SettingsToggleRow(
    emoji: String,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.dimensions.spaces.x4, vertical = AppTheme.dimensions.spaces.x3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(AppTheme.dimensions.sizes.x7)
                .clip(RoundedCornerShape(AppTheme.dimensions.radius.medium))
                .background(AppTheme.colors.accentAlpha12),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 14.sp)
        }

        Spacer(Modifier.width(AppTheme.dimensions.spaces.x3))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = AppTheme.typography.LabelMedium,
                color = AppTheme.colors.text
            )
            if (subtitle != null) {
                Spacer(Modifier.height(AppTheme.dimensions.spaces.x05))
                Text(
                    text = subtitle,
                    style = AppTheme.typography.LabelTiny,
                    color = AppTheme.colors.muted
                )
            }
        }

        Switch(
            checked = checked,
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

@Composable
fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = AppTheme.dimensions.spaces.x4 + AppTheme.dimensions.sizes.x7 + AppTheme.dimensions.spaces.x3)
            .height(AppTheme.dimensions.borders.veryLow)
            .background(AppTheme.colors.border)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF070B14)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}
