package com.goodgame.goodgameapp.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import com.goodgame.goodgameapp.screens.MainScreen
import com.goodgame.goodgameapp.screens.*
import com.goodgame.goodgameapp.viewmodel.GameViewModel
import com.goodgame.goodgameapp.viewmodel.LoginViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

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
    object ExpeditionScreen: Screen("ExpeditionScreen")
}



fun clearBackStack(navController: NavController, navOptionsBuilder: NavOptionsBuilder) {
    navOptionsBuilder.popUpTo(
        navController.currentBackStackEntry?.destination?.route
            ?: return
    ) { inclusive = true }
}

class AppNavGraph() {
    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun InitGraph(navController: NavHostController, loginViewModel: LoginViewModel, gameViewModel: GameViewModel) {
        AnimatedNavHost(
            navController = navController,
            startDestination = Screen.SplashScreen.route
        ) {
            composable(
                route = Screen.SplashScreen.route) {
                SplashScreen(navController, gameViewModel)
            }
            composable(route = Screen.LoginScreenNew.route) {
                LoginScreenNew(navController, loginViewModel)
            }
            composable(route = Screen.RegistrationScreen.route) {
                RegistrationScreen(navController, loginViewModel)
            }
            composable(route = Screen.LoginCodeConfirmScreen.route) {
                LoginCodeConfirmScreen(navController, loginViewModel)
            }
            composable(route = Screen.MainScreen.route){
                MainScreen(navController, gameViewModel)
            }
            composable(route = "${Screen.IntroScreen.route}/{isCharCreate}",
                arguments = listOf(navArgument("isCharCreate") {
                    type = NavType.BoolType
                })
            ) {
                IntroScreen(navController, gameViewModel, it.arguments?.getBoolean("isCharCreate") ?: true)
            }
            composable(route = Screen.CharacterCreationScreen.route) {
                CharacterCreationScreen(navController, gameViewModel)
            }
            composable(route = Screen.PlanningCenterScreen.route,
                //enterTransition = { slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(300))})
            ){
                PlanningCenterScreen(navController, gameViewModel)
            }
            composable(route = Screen.DiagnosticsScreen.route,
//                enterTransition = { slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(300))}
            ) {
                DiagnosticsScreen(navController, gameViewModel)
            }
            composable(route = "${Screen.SupplyScreen.route}/{tab}",
                arguments = listOf(navArgument("tab") {
                    type = NavType.IntType
                },),
//                enterTransition = { slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(300))}
            ) {
                SupplyScreen(navController, gameViewModel, it.arguments?.getInt("tab") ?: 0)
            }
            composable(route = Screen.SupplyScreen.route) {
                SupplyScreen(navController, gameViewModel)
            }
            composable(route = Screen.ExpeditionScreen.route) {
                ExpeditionScreen(navController, gameViewModel)
            }
        }
    }
}
