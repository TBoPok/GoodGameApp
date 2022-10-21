package com.goodgame.goodgameapp.screens.views

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
fun HowToActivateView(onClose: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(Modifier
        .fillMaxSize()
        .clickable(
            interactionSource = interactionSource,
            indication = null
        ) {
            /* .... */
        }, contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .clip(RoundedCornerShape(15.dp))
                .border(1.dp, Color(0xFFACE9FA), RoundedCornerShape(15.dp))
                .background(Color.White)) {
            Column(Modifier.padding(vertical = 20.dp, horizontal = 20.dp)) {
                Text(
                    text = "Как активировать покупку?",
                    style = MaterialTheme.typography.h1,
                    fontSize = 25.sp,
                    color = Color(0xFF010101),
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text =  "1. Для начала выбери и обменяй очки исследования на награду\n\n" +
                            "2. Прийди в твой любимый компьютерный клуб\n\n" +
                            "3. Перейди в меню «Мои награды»\n\n" +
                            "4. Покажи админу выбранную награду\n\n" +
                            "5. Готово! Можно наслаждаться:)",
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