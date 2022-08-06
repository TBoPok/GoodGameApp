package com.goodgame.goodgameapp

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.Log.DEBUG
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.goodgame.goodgameapp.navigation.AppNavGraph
import com.goodgame.goodgameapp.navigation.BackPressedTimer
import com.goodgame.goodgameapp.retrofit.ApiHelper
import com.goodgame.goodgameapp.retrofit.RetrofitBuilder
import com.goodgame.goodgameapp.ui.theme.GoodGameAppTheme
import com.goodgame.goodgameapp.viewmodel.GameViewModel
import com.goodgame.goodgameapp.viewmodel.LoginViewModel
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController


class MainActivity : ComponentActivity() {
    lateinit var loginViewModel : LoginViewModel
    lateinit var gameViewModel : GameViewModel

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adjustFontScale2(resources.configuration)

//        adjustFontScale(resources.configuration)
        setContent {
            HideSystemUI()
            GoodGameAppTheme {
                // A surface container using the 'background' color from the theme
                loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
                gameViewModel = ViewModelProvider(this).get(GameViewModel::class.java)

                val apiInterface = ApiHelper(RetrofitBuilder.apiService)
                loginViewModel.apiInterface = apiInterface
                gameViewModel.apiInterface = apiInterface
                val navController = rememberAnimatedNavController()
                val appNavGraph = AppNavGraph()
                Scaffold (
                    topBar = {},
                    bottomBar = {}
                ) {
                    appNavGraph.InitGraph(navController = navController, loginViewModel, gameViewModel)
                }
                BackPressedTimer(navController = navController)
            }
        }
    }

    private fun adjustFontScale2(configuration: Configuration) {
        if (configuration.smallestScreenWidthDp <= 360) configuration.fontScale = 1.0f
        else configuration.fontScale = 1.2f

    }

    @Composable
    fun HideSystemUI() {

        val systemUiController: SystemUiController = rememberSystemUiController()
        systemUiController.setStatusBarColor(Color.Black, darkIcons = false)

        systemUiController.isStatusBarVisible = true // Status bar

    }
}


