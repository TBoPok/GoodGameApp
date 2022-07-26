package com.goodgame.goodgameapp.screens

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.goodgame.goodgameapp.R
import com.goodgame.goodgameapp.navigation.Screen
import com.goodgame.goodgameapp.navigation.clearBackStack
import com.goodgame.goodgameapp.retrofit.Status
import com.goodgame.goodgameapp.screens.views.BottomImage
import com.goodgame.goodgameapp.screens.views.ErrorAlert
import com.goodgame.goodgameapp.screens.views.LogoView
import com.goodgame.goodgameapp.screens.views.TopImage
import com.goodgame.goodgameapp.viewmodel.GameViewModel
import com.goodgame.goodgameapp.viewmodel.LoginViewModel

enum class SplashScreenState {
    WAIT_RESPONSE,
    LOGGED_IN,
    NOT_LOGGED_IN,
    INTERNET_ERROR,
    GOT_HERO_INFO,
    WAIT_HERO_INFO,
}

@Composable
fun SplashScreen(navController: NavHostController, viewModel : GameViewModel) {

    SplashScreenGraphics()

    val splashScreenState = remember { mutableStateOf(SplashScreenState.WAIT_RESPONSE)}

    val activity = (LocalContext.current as? Activity)
    val lifecycleOwner = LocalLifecycleOwner.current
    val refreshKey = remember { mutableStateOf(true)}
    fun MutableState<Boolean>.trigger() { value = !value } // refreshKey trigger

    LaunchedEffect(refreshKey.value) {
        if (splashScreenState.value == SplashScreenState.WAIT_RESPONSE)
            viewModel.checkToken().observe(lifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        if (it.data?.status == false)
                            splashScreenState.value =  SplashScreenState.NOT_LOGGED_IN
                        if (it.data?.status == true) {
                            splashScreenState.value = SplashScreenState.LOGGED_IN
                            viewModel.username = it.data.username
                            viewModel.heroInfo.value?.hasHero = it.data.hasHero ?: false
                        }
                    }
                    Status.ERROR   -> { splashScreenState.value = SplashScreenState.INTERNET_ERROR }
                    Status.LOADING -> { }
                }
            })
        if (splashScreenState.value == SplashScreenState.WAIT_HERO_INFO)
            viewModel.getHeroInfo().observe(lifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        splashScreenState.value = SplashScreenState.GOT_HERO_INFO
                    }
                    Status.ERROR   -> { splashScreenState.value = SplashScreenState.INTERNET_ERROR }
                    Status.LOADING -> { }
                }
            })
    }

    when (splashScreenState.value) {
        SplashScreenState.LOGGED_IN -> {
            if (viewModel.heroInfo.value?.hasHero == false)
                navController.navigate("${Screen.IntroScreen.route}/true") {
                    clearBackStack(navController, this)
                }
            else {
                splashScreenState.value = SplashScreenState.WAIT_HERO_INFO
                refreshKey.trigger()
            }
        }
        SplashScreenState.WAIT_HERO_INFO -> {}
        SplashScreenState.GOT_HERO_INFO -> {
            if (viewModel.heroInfo.value?.hasHero == false)
                navController.navigate(Screen.IntroScreen.route) {
                    clearBackStack(navController, this)
                }
            else
                navController.navigate(Screen.MainScreen.route) {
                    clearBackStack(navController, this)
                }
        }
        SplashScreenState.WAIT_RESPONSE -> {

        }
        SplashScreenState.NOT_LOGGED_IN -> {
            navController.navigate(Screen.LoginScreenNew.route) {
                clearBackStack(navController, this)
            }
        }
        SplashScreenState.INTERNET_ERROR -> {
            ErrorAlert(
                errorMessage = "Нет подключения к интернету",
                refresh = { refreshKey.trigger(); splashScreenState.value = SplashScreenState.WAIT_RESPONSE },
                cancel = { activity?.finish() })
        }
    }
}

@Composable
private fun SplashScreenGraphics() {
    Box(modifier = Modifier // Background box
        .fillMaxSize()
        .background(color = MaterialTheme.colors.background)
    ) {

        Column(modifier = Modifier.fillMaxSize())
        {
            Row(modifier = Modifier.fillMaxWidth()) {
                TopImage { }
            }
            Row(modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically)
            {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    LogoView()
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                BottomImage {}
            }
        }
    }
}