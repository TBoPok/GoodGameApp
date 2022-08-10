package com.goodgame.goodgameapp.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.goodgame.goodgameapp.R
import com.goodgame.goodgameapp.models.HeroInfo
import com.goodgame.goodgameapp.navigation.Screen
import com.goodgame.goodgameapp.navigation.clearBackStack
import com.goodgame.goodgameapp.screens.views.ExpeditionsListView
import com.goodgame.goodgameapp.viewmodel.GameViewModel

@Composable
fun PlanningCenterScreen(navController: NavController, viewModel: GameViewModel) {
    val scrollState = rememberScrollState()
    val heroInfo by viewModel.heroInfo.observeAsState()

    val isExpeditionsListActive = remember {mutableStateOf(false)}

    Column(modifier = Modifier
        .verticalScroll(scrollState)
        .fillMaxSize()
        .background(Color.Black)) {
        Row { // Head row
            HeadPlanning()
        }
        Row { // Action row
            ActionRow(heroInfo, navController)
        }
        Row { // Info row
            InfoRow(
                completedPercent = heroInfo?.total_progress ?: 0,
                completed = heroInfo?.expeditions?.size ?: 0) {
                isExpeditionsListActive.value = true
            }
        }
    }
    Box (modifier = Modifier
        .padding(start = 20.dp, top = 20.dp)
        .background(Color.Black)
        .clickable { navController.navigateUp() }) {
        Row {
            Image(
                painter = painterResource(R.drawable.arrow_back_ios),
                contentDescription = "arrow_back",
                modifier = Modifier.height(10.dp).padding(start = 5.dp).align(Alignment.CenterVertically),
                contentScale = ContentScale.FillHeight,
            )
            Text(
                text = "На базу",
                color = Color.White,
                modifier = Modifier.padding(start = 1.dp, end = 3.dp))
        }
    }
    if (isExpeditionsListActive.value)
        ExpeditionsListView(viewModel = viewModel) {
            isExpeditionsListActive.value = false
        }
}

@Composable
private fun HeadPlanning() {
    val headTextStyle = TextStyle(
        color = Color.White,
        fontFamily = FontFamily(Font(R.font.micra_bold)),
        fontWeight = FontWeight.Bold,
        lineHeight = 24.sp,
        fontSize = 18.sp
    )
    Box (Modifier.fillMaxWidth()) {
        Image(
            painterResource(R.drawable.planning_center_head),
            contentDescription = "head_image",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
        Column(modifier = Modifier.matchParentSize()) {
            Row(modifier = Modifier.weight(0.20f)) {}
            Row(modifier = Modifier.weight(0.14f)) {
                Text(
                    text = "ЦЕНТР\nПЛАНИРОВАНИЯ",
                    style = headTextStyle,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.8f)
                        .padding(start = 20.dp)
                )
            }
            Row(modifier = Modifier.weight(0.32f)) {}
            Row(modifier = Modifier.weight(0.12f)) {
                Text(
                    text = "Здесь ты отправляешься в свои экспедиции и не забудь взять пивко",
                    style = MaterialTheme.typography.subtitle1,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.9f)
                        .padding(start = 20.dp)
                )
            }
            Row(modifier = Modifier.weight(0.1f)) {}
        }
    }
}

@Composable
private fun ActionRow(heroInfo: HeroInfo?, navController: NavController) {

    @Composable fun TopText(text: String) = Text(
        text = text,
        style = MaterialTheme.typography.body1,
        color = Color.White,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(8.dp))

    @Composable fun SubtitleText(text: String, isActive: Boolean) {
        val color = if (isActive)
            Color.White
        else
            Color(0x80FFFFFF)
        return Text(
            text = text,
            style = MaterialTheme.typography.subtitle1,
            color = color,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(8.dp))
    }

    Box (contentAlignment = Alignment.Center) {
        Image(
            painterResource(R.drawable.planning_center_bg2),
            contentDescription = "scroll_bg_image",
            modifier = Modifier
                .fillMaxWidth()
                .scale(scaleY = 1.1f, scaleX = 1f),
            contentScale = ContentScale.FillWidth,
        )
        Box(contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()) {
            if (heroInfo != null) {
                Column {
                    when {
                        heroInfo.has_expeditions == 1 -> {
                            TopText("Тебе сейчас доступна 1 экспедиция")
                        }
                        heroInfo.has_expeditions in (2..4) -> {
                            TopText("Тебе сейчас доступны " + heroInfo.has_expeditions + " экспедиции")
                        }
                        heroInfo.has_expeditions > 4 -> {
                            TopText("Тебе сейчас доступно " + heroInfo.has_expeditions + " экспедиций")
                        }
                        else -> Box(Modifier.fillMaxWidth().clickable {  }) {
                            TopText(
                                "Экспедиция недоступна\n"
                                        + "Приходи завтра в 10:00 по МСК и отправляйся в новое путешествие!\n"
                                        + "Или нажми здесь, чтобы узнать другие способы исследовать планету GG-265"
                            ) }
                    }

                    if (heroInfo.has_expeditions > 0) {
                        ButtonGo(text = "Да, исследовать") {navController.navigate(Screen.ExpeditionScreen.route) { clearBackStack(navController,this)} }
                        SubtitleText("Нет информации об опасности", true)
                    }
                }

            } else {
                TopText("Загрузка...")
            }

        }
    }
}

@Composable
private fun InfoRow(
    completed: Int,
    completedPercent: Int,
    onClick: () -> Unit) {

    Box (contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(Color.Transparent)) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(35.dp)
                .padding(top = 10.dp)
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
        ) {
            Image(
                painterResource(R.drawable.planning_center_progress),
                contentDescription = "scroll_bg_image",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth,
            )
            Column(
                Modifier
                    .matchParentSize()
                    .padding(horizontal = 20.dp, vertical = 30.dp)) {
                Box (modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()) {
                    Row (Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                        Text (
                            text = "Твой прогресс\nизучения планеты",
                            style = MaterialTheme.typography.body1,
                            color = Color(0x80FFFFFF))
                        Spacer(Modifier.weight(1f))
                        Text (text = "$completedPercent%",
                            style = MaterialTheme.typography.h1,
                            color = Color(0xFFFFFFFF))
                    }
                }
                Spacer(Modifier.weight(0.4f))
                Box (modifier = Modifier
                    .weight(1f),
                    contentAlignment = Alignment.Center
                    ) {
                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(15.dp))
                        .fillMaxWidth()
                        .background(Color.Transparent)
                        .border(
                            1.dp,
                            Color(0xFFBFEFFC),
                            RoundedCornerShape(15.dp),
                        )
                    ) {
                        Row (modifier = Modifier.height(50.dp)) {
                            Text(text = "Журнал экспедиций",
                                style = MaterialTheme.typography.h1,
                                color = Color(0xFFFFFFFF),
                                fontSize = 15.sp,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 15.dp))
                            Spacer(Modifier.weight(1f))
                            Box(contentAlignment = Alignment.CenterEnd) {
                                Image(
                                    painterResource(R.drawable.button_param),
                                    contentDescription = "scroll_bg_image",
                                    modifier = Modifier.fillMaxHeight(),
                                    contentScale = ContentScale.FillHeight,
                                )
                                Box(Modifier.matchParentSize(), contentAlignment = Alignment.CenterEnd) {
                                    Text(text = "$completed",
                                        style = MaterialTheme.typography.button,
                                        color = Color(0xFF000000),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth(0.8f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

//@Preview
//@Composable
//private fun PlanningPreview() {
//    val scrollState = rememberScrollState()
//    val heroInfo = HeroInfo(true,"charisma", emptyList(), 0,0,0,2, 6)
//
//    Column(modifier = Modifier
//        .verticalScroll(scrollState)
//        .fillMaxSize()
//        .background(Color.Black)) {
//        Row { // Head row
//            HeadPlanning()
//        }
//        Row { // Action row
//            ActionRow(heroInfo)
//        }
//        Row { // Info row
//            InfoRow (50,4) {}
//        }
//    }
//    Box (modifier = Modifier
//        .padding(start = 20.dp, top = 20.dp)
//        .background(Color.Black)
//        .height(IntrinsicSize.Max)
//        .clickable { }) {
//        Row {
//            Icon(Icons.Filled.KeyboardArrowLeft,"",tint = Color.White)
//            Text(
//                text = "На базу",
//                color = Color.White,
//                textAlign = TextAlign.Center,
//                modifier = Modifier
//                    .padding(start = 1.dp, end = 3.dp)
//                    .wrapContentHeight())
//        }
//    }
//}