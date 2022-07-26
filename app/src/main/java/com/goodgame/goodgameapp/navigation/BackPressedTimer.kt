package com.goodgame.goodgameapp.navigation

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.time.seconds

@Composable
fun BackPressedTimer(navController: NavController) {
    val context = LocalContext.current
    val backPressed = remember { mutableStateOf(false)}
    BackHandler() {
        if (navController.currentBackStackEntry?.destination?.route == Screen.MainScreen.route) {
            if (backPressed.value == true)
                (context as Activity).finish()
            backPressed.value = true
            Toast
                .makeText(context, "Нажмите еще раз для выхода", Toast.LENGTH_SHORT)
                .show()
        }
    }

    LaunchedEffect(backPressed.value) {
        if (backPressed.value == true)
            while(true) {
                delay(2000)
                backPressed.value = false
            }
    }
}