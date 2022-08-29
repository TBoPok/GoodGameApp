package com.goodgame.goodgameapp

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.Log.DEBUG
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        gameViewModel = ViewModelProvider(this)[GameViewModel::class.java]
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContent {
            HideSystemUI()
            GoodGameAppTheme {
                // A surface container using the 'background' color from the theme
                val navController = rememberAnimatedNavController()
                val appNavGraph = AppNavGraph()
                Box (Modifier.fillMaxSize().background(Color.Black)) {
                    appNavGraph.InitGraph(navController = navController, loginViewModel, gameViewModel)
                }
            }
        }
    }

    private fun adjustFontScale2(configuration: Configuration) {
        if (configuration.smallestScreenWidthDp <= 400) configuration.fontScale = 1.0f
        else configuration.fontScale = 1.2f

    }

    @Composable
    fun HideSystemUI() {

    }
}


