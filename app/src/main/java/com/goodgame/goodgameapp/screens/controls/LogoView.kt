package com.goodgame.goodgameapp.screens.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.goodgame.goodgameapp.R

@Composable
fun LogoView() {
    val logo = R.drawable.white_logo

    Image(
        painterResource(logo),
        contentDescription = "Top logo",
        modifier = Modifier.widthIn(min = 0.dp, max = 170.dp)
    )
}