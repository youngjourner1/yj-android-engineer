package com.yj.webcam.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yj.webcam.presentation.diagnostics.DiagnosticsScreen
import com.yj.webcam.presentation.main.MainScreen
import com.yj.webcam.presentation.onboarding.OnboardingScreen
import com.yj.webcam.presentation.pairing.PairingScreen
import com.yj.webcam.presentation.settings.SettingsScreen

object ScreenRoutes {
    const val ONBOARDING = "onboarding"
    const val MAIN = "main"
    const val SETTINGS = "settings"
    const val DIAGNOSTICS = "diagnostics"
    const val PAIRING = "pairing"
    const val COLOR_ENGINE = "color_engine"
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = ScreenRoutes.MAIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(ScreenRoutes.ONBOARDING) {
            OnboardingScreen(onFinishOnboarding = {
                navController.navigate(ScreenRoutes.MAIN) {
                    popUpTo(ScreenRoutes.ONBOARDING) { inclusive = true }
                }
            })
        }
        composable(ScreenRoutes.MAIN) {
            MainScreen(
                onNavigateToSettings = { navController.navigate(ScreenRoutes.SETTINGS) },
                onNavigateToDiagnostics = { navController.navigate(ScreenRoutes.DIAGNOSTICS) },
                onNavigateToPairing = { navController.navigate(ScreenRoutes.PAIRING) }
            )
        }
        composable(ScreenRoutes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable(ScreenRoutes.DIAGNOSTICS) {
            DiagnosticsScreen(onBack = { navController.popBackStack() })
        }
        composable(ScreenRoutes.PAIRING) {
            PairingScreen(onBack = { navController.popBackStack() })
        }
        composable(ScreenRoutes.COLOR_ENGINE) {
            com.yj.webcam.presentation.color.ColorEngineScreen()
        }
    }
}
