package com.goodgame.goodgameapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import com.goodgame.goodgameapp.screens.MainScreen
import com.goodgame.goodgameapp.screens.*
import com.goodgame.goodgameapp.viewmodel.GameViewModel
import com.goodgame.goodgameapp.viewmodel.LoginViewModel

sealed class Screen(val route: String) {
    object SplashScreen : Screen("SplashScreen")
    object LoginScreenNew : Screen("LoginScreenNew")
    object RegistrationScreen : Screen("RegistrationScreen")
    object LoginCodeConfirmScreen : Screen("LoginCodeConfirmScreen")
    object MainScreen : Screen("MainScreen")
    object IntroScreen : Screen("IntroScreen")
    object CharacterCreationScreen: Screen("CharacterCreationScreen")
    object PlanningCenterScreen: Screen("PlanningCenterScreen")
    object DiagnosticsScreen: Screen("DiagnosticsScreen")
    object SupplyScreen: Screen("SupplyScreen")
}



fun clearBackStack(navController: NavController, navOptionsBuilder: NavOptionsBuilder) {
    navOptionsBuilder.popUpTo(
        navController.currentBackStackEntry?.destination?.route
            ?: return
    ) { inclusive = true }
}

class AppNavGraph() {
    // TODO Сделать кастомный навконтроллер для более удобного сброса графа переходов

    @Composable
    fun InitGraph(navController: NavHostController, loginViewModel: LoginViewModel, gameViewModel: GameViewModel) {
        NavHost(
            navController = navController,
            startDestination = Screen.SplashScreen.route
        ) {
            composable(Screen.SplashScreen.route) {
                SplashScreen(navController, gameViewModel)
            }
            composable(Screen.LoginScreenNew.route) {
                LoginScreenNew(navController, loginViewModel)
            }
            composable(Screen.RegistrationScreen.route) {
                RegistrationScreen(navController, loginViewModel)
            }
            composable(Screen.LoginCodeConfirmScreen.route) {
                LoginCodeConfirmScreen(navController, loginViewModel)
            }
            composable(Screen.MainScreen.route) {
                MainScreen(navController, gameViewModel)
            }
            composable(
                "${Screen.IntroScreen.route}/{isCharCreate}",
                arguments = listOf(navArgument("isCharCreate") {
                    type = NavType.BoolType
                })
            ) {
                IntroScreen(navController, gameViewModel, it.arguments?.getBoolean("isCharCreate") ?: true)
            }
            composable(Screen.CharacterCreationScreen.route) {
                CharacterCreationScreen(navController, gameViewModel)
            }
            composable(Screen.PlanningCenterScreen.route) {
                PlanningCenterScreen(navController, gameViewModel)
            }
            composable(Screen.DiagnosticsScreen.route) {
                DiagnosticsScreen(navController, gameViewModel)
            }
            composable(
                "${Screen.SupplyScreen.route}/{tab}",
                arguments = listOf(navArgument("tab") {
                    type = NavType.IntType
                })
            ) {
                SupplyScreen(navController, gameViewModel, it.arguments?.getInt("tab") ?: 0)
            }
            composable(Screen.SupplyScreen.route) {
                SupplyScreen(navController, gameViewModel)
            }
        }
    }
}
