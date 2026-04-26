package com.slumber.soundmixer

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.slumber.soundmixer.navigation.AppNavHost
import com.slumber.soundmixer.navigation.Route
import com.slumber.soundmixer.presentation.AppViewModel
import com.slumber.soundmixer.presentation.onboarding.OnboardingViewModel
import com.slumber.soundmixer.ui.theme.AppTheme
import com.slumber.soundmixer.ui.theme.SlumberSoundMixerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SleepSoundsApp()
        }
    }
}

@Composable
fun SleepSoundsApp() {
    val appViewModel: AppViewModel = hiltViewModel()
    val isDarkMode by appViewModel.isDarkMode.collectAsState()

    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val onboardingComplete by onboardingViewModel.onboardingComplete.collectAsState()

    var showNotificationRationale by rememberSaveable { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    val activity = LocalView.current.context as Activity

    LaunchedEffect(onboardingComplete) {
        if (!onboardingComplete) return@LaunchedEffect
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return@LaunchedEffect

        val granted = ContextCompat.checkSelfPermission(
            activity, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) return@LaunchedEffect

        val alreadyRequested = appViewModel.notificationPermissionRequested
        val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            activity, Manifest.permission.POST_NOTIFICATIONS
        )
        val permanentlyDenied = alreadyRequested && !shouldShowRationale
        if (!permanentlyDenied) {
            showNotificationRationale = true
        }
    }

    if (showNotificationRationale) {
        AlertDialog(
            onDismissRequest = { showNotificationRationale = false },
            containerColor = AppTheme.colors.card,
            title = {
                Text(
                    text = stringResource(R.string.notif_rationale_title),
                    style = AppTheme.typography.ButtonLabel,
                    color = AppTheme.colors.text
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.notif_rationale_body),
                    style = AppTheme.typography.BodyText2Regular,
                    color = AppTheme.colors.soft
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showNotificationRationale = false
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        appViewModel.markNotificationPermissionRequested()
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }) {
                    Text(
                        text = stringResource(R.string.notif_rationale_confirm),
                        style = AppTheme.typography.BodyText2Bold,
                        color = AppTheme.colors.accent
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotificationRationale = false }) {
                    Text(
                        text = stringResource(R.string.notif_rationale_skip),
                        style = AppTheme.typography.BodyText2Regular,
                        color = AppTheme.colors.muted
                    )
                }
            }
        )
    }

    SlumberSoundMixerTheme(isDarkTheme = isDarkMode) {
        val view = LocalView.current
        if (!view.isInEditMode) {
            SideEffect {
                val window = (view.context as Activity).window
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkMode
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDarkMode
            }
        }

        val navController = rememberNavController()
        val startDestination = if (onboardingComplete) Route.Mixer else Route.Onboarding

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.colors.surface)
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            AppNavHost(
                navController = navController,
                startDestination = startDestination
            )
        }
    }
}
