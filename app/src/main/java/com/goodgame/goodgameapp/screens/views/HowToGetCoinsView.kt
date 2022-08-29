package com.goodgame.goodgameapp.screens.views

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HowToGetCoinsView(onClose: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .clip(RoundedCornerShape(15.dp))
                .border(1.dp, Color(0xFFACE9FA), RoundedCornerShape(15.dp))
                .background(Color.White)) {
            Column(Modifier.padding(vertical = 20.dp, horizontal = 20.dp)) {
                Text(
                    text = "Как получить очки исследования?",
                    style = MaterialTheme.typography.h4,
                    fontSize = 25.sp,
                    color = Color(0xFF010101),
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Исследуй планету GG-265 вместе с godji\n"
                        + "В награду ты будешь получать очки исследования. Планета наполнена опасностями, поэтому помни, что за не верные решения ты можешь их потерять",
                    style = MaterialTheme.typography.subtitle2,
                    lineHeight = 19.sp,
                    color = Color(0xFF010101)
                )
                Spacer(modifier = Modifier.height(15.dp))
                MetallButton(isActive = true, height = 55.dp, activeText = "Понятно") {
                    onClose()
                }
            }
        }
    }
    BackHandler() {
        onClose()
    }
}