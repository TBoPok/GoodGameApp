package com.goodgame.goodgameapp.screens.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.goodgame.goodgameapp.R

@Composable
fun TopImage(content: @Composable () -> Unit) {
    Box(contentAlignment = Alignment.Center)
    {
        Image(
            painterResource(R.drawable.bord_1_4),
            contentDescription = "Top image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth())
        content()
    }
}