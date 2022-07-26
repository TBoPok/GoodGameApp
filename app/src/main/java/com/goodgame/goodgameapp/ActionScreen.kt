package com.goodgame.goodgameapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource

@Composable
fun ActionScreen() {

    Image(
        painter = painterResource(id = R.drawable.logo_1),
        contentDescription = null,
        modifier = Modifier.fillMaxHeight()
    )

    Column(modifier = Modifier
        .fillMaxSize()) {

    }

}
