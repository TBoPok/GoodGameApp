package com.goodgame.goodgameapp.screens.views

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.goodgame.goodgameapp.R
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goodgame.goodgameapp.models.ExpeditionStoryModel
import com.goodgame.goodgameapp.retrofit.Status
import com.goodgame.goodgameapp.viewmodel.GameViewModel
import java.util.*

@Composable
fun ExpeditionsListView(viewModel: GameViewModel, closeEvent: () -> Unit) {
    val heroInfo by viewModel.heroInfo.observeAsState()

    BackHandler() {
        closeEvent()
    }

    val background = Brush.linearGradient(
        listOf(Color(0xFF000000), Color(0xFF333C3F)),
        end = Offset(x = 0f, y = 0f),
        start = Offset(x = Offset.Infinite.x / 20, y = Offset.Infinite.y / 3))
    val interactionSource = remember { MutableInteractionSource() }
    Column (modifier = Modifier
        .padding(14.dp)
        .clickable(
        interactionSource = interactionSource,
        indication = null
    ) {
        /* .... */
    }) {
        Row(
            modifier = Modifier
                .weight(1f)
                .background(Color.Transparent)
        ) {
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
                Column(modifier = Modifier.padding(vertical = 15.dp, horizontal = 17.dp)) {
                    ContentHeadingRow(heroInfo?.total_progress)
                    Spacer(modifier = Modifier.padding(vertical = 5.dp))
                        if (heroInfo != null)
                            ContentRow(heroInfo?.expeditions, heroInfo?.total_progress)
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

@Composable
private fun ContentHeadingRow(expeditionsCompleted: Int?) {
    val headingText = TextStyle(
        fontFamily = FontFamily(Font(R.font.micra_bold)),
        fontWeight = FontWeight.Bold,
        lineHeight = 24.sp,
        fontSize = 18.sp
    )
    val expeditionsCompletedText =
        expeditionsCompleted?.toString() ?: "XX"
    Row(
        Modifier
            .fillMaxWidth()
            .height(60.dp)) {
        Text(
            text = "ЖУРНАЛ\nЭКСПЕДИЦИЙ",
            style = headingText,
            color = Color.White,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "$expeditionsCompletedText%",
            style = MaterialTheme.typography.h4,
            color = Color(0x80FFFFFF),
        )
    }
}

@Composable
private fun ContentRow(expeditions: List<ExpeditionStoryModel>?, totalProgress: Int?) {
    val scrollState = rememberLazyListState()
    if (expeditions == null || expeditions.isEmpty()) {
        Text(
            text = "ЖУРНАЛ ПОКА ПУСТ",
            style = MaterialTheme.typography.h2,
            color = Color.White,
        )
    }
    else {
        var progress = 0
        if (expeditions.isNotEmpty())
        progress = totalProgress?.div(expeditions.size) ?: 0
        LazyColumn(state = scrollState) {
            items(items = expeditions) { expedition ->
                ExpeditionCard(
                    index = expedition.number,
                    result = expedition.result,
                    name = expedition.name ?: "[ДАННЫЕ УДАЛЕНЫ]",
                    coins = expedition.rsp,
                    experience = expedition.exp,
                    progress = progress ?: 0)
            }
        }
    }
}

@Composable
private fun ExpeditionCard(index: Int, result: String, name: String, coins: Int, experience: Int, progress: Int) {
    val gradient = when (result) {
        "win" -> {
            Brush.linearGradient(
                colors = listOf(Color(0x6655A623), Color(0x0055A623)),
                start = Offset(0f, Offset.Infinite.y),
                end = Offset(65f,0f),
                tileMode = TileMode.Clamp)
        }
        "run" -> {
            Brush.linearGradient(
                colors = listOf(Color(0x66A69F23), Color(0x00A69F23)),
                start = Offset(0f, Offset.Infinite.y),
                end = Offset(65f,0f),
                tileMode = TileMode.Clamp)
        }
        else -> {
            Brush.linearGradient(
                colors = listOf(Color(0x66FF8588), Color(0x00FF8588)),
                start = Offset(0f, Offset.Infinite.y),
                end = Offset(65f, 0f),
                tileMode = TileMode.Clamp)
        }
    }
    val fontMicradi12 = TextStyle(
        fontFamily = FontFamily(Font(R.font.micradi)),
        fontSize = 12.sp,
        lineHeight = 20.sp
    )
    val fontMicra = TextStyle(
        fontFamily = FontFamily(Font(R.font.micra_bold)),
        fontSize = 12.sp,
        lineHeight = 20.sp
    )
    val fontSubtitle = TextStyle(
        fontFamily = FontFamily(Font(R.font.montserrat_medium)),
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp)
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(gradient)) {
        Column (Modifier.padding(12.dp)) {
            Row() {
                Text(text ="Экспедиция $index",
                    style = fontMicradi12,
                    color = Color.White)
                Spacer(modifier = Modifier.weight(1f))
                Text(text ="$progress%",
                    style = fontMicradi12,
                    color = Color(0x80FFFFFF))
            }

//            Spacer(modifier = Modifier.padding(bottom = 5.dp))
//            Text(name.uppercase(Locale.getDefault()),
//                style = fontMicra,
//                color = Color.White)
            Spacer(modifier = Modifier.padding(bottom = 10.dp))
            Row {
                val sign = if (coins < 0) "" else "+"
                Text(text = "$sign$coins",
                    style = fontSubtitle,
                    color = Color(0x80FFFFFF))
                Spacer(modifier = Modifier.padding(end = 5.dp))
                Image(
                    painterResource(R.drawable.coin),
                    contentDescription = "coin",
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.padding(end = 20.dp))
                Text(text = "+$experience очков опыта",
                    style = fontSubtitle,
                    color = Color(0x80FFFFFF))
            }
        }
    }
}

@Preview
@Composable
private fun CardPreview() {
    val background = Brush.linearGradient(
        listOf(Color(0xFF000000), Color(0xFF333C3F)),
        end = Offset(x = 0f, y = 0f),
        start = Offset(x = Offset.Infinite.x / 20, y = Offset.Infinite.y / 3))
    Column (modifier = Modifier.padding(14.dp)) {
        Row(
            modifier = Modifier
                .weight(1f)
                .background(Color.Transparent)) {
            CloseRow(closeEvent = {})
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
                Column(modifier = Modifier.padding(vertical = 15.dp, horizontal = 17.dp)) {
//                    ContentHeadingRow()
//                    Spacer(modifier = Modifier.padding(vertical = 5.dp))
//                    ContentRow()
                }
            }
        }
        Row(modifier = Modifier.weight(1f)) {} // Пустое поле
    }
}