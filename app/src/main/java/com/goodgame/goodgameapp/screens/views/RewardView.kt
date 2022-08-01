package com.goodgame.goodgameapp.screens.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun RewardView(username: String, text: String, onClose: () -> Unit) {
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
                    text = text,
                    style = MaterialTheme.typography.h1,
                    fontSize = 25.sp,
                    color = Color.Black,
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text =  "Ник: $username\n" +
                            "Предъяви это администратору и получи забери свою награду!",
                    style = MaterialTheme.typography.subtitle2,
                    lineHeight = 19.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(15.dp))
                MetallButton(isActive = mutableStateOf(true), height = 55.dp, activeText = "Понятно") {
                    onClose()
                }
            }
        }
    }
}