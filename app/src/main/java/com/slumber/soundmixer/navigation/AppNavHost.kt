package com.slumber.soundmixer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.slumber.soundmixer.presentation.createmix.CreateMixScreen
import com.slumber.soundmixer.presentation.mixer.MixerScreen
import com.slumber.soundmixer.presentation.mymixes.SavedMixesScreen
import com.slumber.soundmixer.presentation.onboarding.OnboardingScreen
import com.slumber.soundmixer.presentation.onboarding.OnboardingViewModel
import com.slumber.soundmixer.presentation.settings.SettingsScreen
import com.slumber.soundmixer.presentation.timer.TimerScreen
import com.slumber.soundmixer.presentation.upgrade.UpgradeScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Route,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable<Route.Onboarding> {
            val viewModel: OnboardingViewModel = hiltViewModel()
            OnboardingScreen(
                onGetStarted = {
                    viewModel.completeOnboarding()
                    navController.navigate(Route.Mixer) {
                        popUpTo<Route.Onboarding> { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Mixer> {
            MixerScreen(
                navController = navController,
                onShowUpgrade = { navController.navigate(Route.Upgrade) }
            )
        }

        composable<Route.Timer> {
            TimerScreen(navController = navController)
        }

        composable<Route.Mixes> {
            SavedMixesScreen(
                navController = navController,
                onCreateMix = { navController.navigate(Route.CreateMix) },
                onShowUpgrade = { navController.navigate(Route.Upgrade) }
            )
        }

        composable<Route.Settings> {
            SettingsScreen(navController = navController)
        }

        composable<Route.Upgrade> {
            UpgradeScreen(onDismiss = { navController.popBackStack() })
        }

        composable<Route.CreateMix> {
            CreateMixScreen(
                onClose = { navController.popBackStack() },
                onShowUpgrade = { navController.navigate(Route.Upgrade) }
            )
        }
    }
}
