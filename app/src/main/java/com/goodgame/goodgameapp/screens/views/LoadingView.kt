package com.goodgame.goodgameapp.screens.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color

@Composable
fun LoadingView(isTransparent : Boolean = false) {
    val background = if (isTransparent) Color.Transparent else Color(0x34000000)
    Box (modifier = Modifier.fillMaxSize().background(background), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}