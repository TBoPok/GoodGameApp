package com.goodgame.goodgameapp.screens.views

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.goodgame.goodgameapp.R

@Composable
fun LevelsView(closeEvent: () -> Unit) {
    val scrollState = rememberLazyListState()
    BackHandler() {
        closeEvent()
    }
    val background = Brush.linearGradient(
        listOf(Color(0xFF000000), Color(0xFF333C3F)),
        end = Offset(x = 0f, y = 0f),
        start = Offset(x = Offset.Infinite.x / 20, y = Offset.Infinite.y / 3)
    )
    val interactionSource = remember { MutableInteractionSource() }
    Column (modifier = Modifier.padding(14.dp).clickable (
        interactionSource = interactionSource,
        indication = null) {}) {
        Row(
            modifier = Modifier
                .weight(1f)
                .background(Color.Transparent)) {
            CloseRow(closeEvent = {closeEvent()})
        }
        Row(modifier = Modifier.weight(8f))
        {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(15.dp))
                    .border(
                        1.dp,
                        Color(0xFFACE9FA),
                        RoundedCornerShape(15.dp)
                    )
                    .background(background)

            ) {
                LazyColumn(state = scrollState) {
                    item {
                        Image(
                            painterResource(R.drawable.lvls),
                            contentDescription = "coin",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
        Row(modifier = Modifier.weight(1f)) {} // Пустое поле
    }
}

@Composable
private fun CloseRow(closeEvent: () -> Unit) {
    Row {
        Spacer(modifier = Modifier.weight(1f))
        Box(modifier = Modifier.padding(top = 28.dp, end = 10.dp)) {
            CloseButton {
                closeEvent()
            }
        }
    }
}