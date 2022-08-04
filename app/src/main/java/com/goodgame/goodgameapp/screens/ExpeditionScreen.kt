package com.goodgame.goodgameapp.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.goodgame.goodgameapp.R
import com.goodgame.goodgameapp.models.ExpeditionModel
import com.goodgame.goodgameapp.models.HeroInfo
import com.goodgame.goodgameapp.viewmodel.GameViewModel

private enum class ExpeditionScreenState {
    LOADING,
    ACTION,
    RESULT_LOADING,
    RESULT
}

@Composable
fun ExpeditionScreen(navController: NavController, viewModel: GameViewModel) {
    val expeditionState = remember { mutableStateOf(ExpeditionScreenState.LOADING)}
    val heroInfo by viewModel.heroInfo.observeAsState()
//    val expeditionModel = remember { mutableStateOf<ExpeditionModel?>(null)}
    val expeditionModel = remember { mutableStateOf<ExpeditionModel?>(
        ExpeditionModel(3, "A time-delay-integration (TDI) image sensor is a special linear\n" +
                "pushbroom imaging sensor with the structure of a planar array.\n" +
                "It can achieve a high signal-to-noise ratio (SNR) by multiple\n" +
                "sampling and accumulating the signal from the same ground\n" +
                "object [1]. It is especially appropriate for the case of low illu-\n" +
                "mination and high relative velocity and is broadly applied in the\n" +
                "fields of satellite remote sensing, machine vision, document\n" +
                "scanning, etc. The TDI functionality is more easily realized\n" +
                "in a charge-coupled device (CCD) than CMOS because charge\n" +
                "signals can be transferred intrinsically and accumulated without\n" +
                "noise in the imaging process [2]. However, the CCD requires\n" +
                "high-voltage operation, and it is difficult to integrate signal\n" +
                "processing circuits. On the other hand, with the rapid develop-\n" +
                "ment of integrated circuit manufacturing technology, the", "Сложная", "", 100, 100)
    )}
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {
        when (expeditionState.value) {
            ExpeditionScreenState.LOADING -> {
                LoadingExpedition {
                    expeditionState.value = ExpeditionScreenState.ACTION
                }
            }
            ExpeditionScreenState.ACTION -> {
                if (expeditionModel.value == null) {

                } else {
                    ShowExpedition(
                        expeditionModel = expeditionModel.value!!,
                        heroInfo = heroInfo,
                        onRun = {},
                        onCancel = {})
                }
            }
            ExpeditionScreenState.RESULT_LOADING -> {
                TODO()
            }
            ExpeditionScreenState.RESULT -> {
                TODO()
            }
        }
    }
}

@Composable
private fun LoadingExpedition(onDone: () -> Unit) {
    val transition = rememberInfiniteTransition() // animate infinite times
    val alphaAnimation = transition.animateFloat( //animate the transition
        initialValue = 1f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500, // duration for the animation
                easing = FastOutLinearInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),

    )
    Box(
        Modifier
            .fillMaxSize()
            .clickable { onDone() }) {
        Image(
            painterResource(R.drawable.loading_expedition_bg),
            contentDescription = "first_screen",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Image(
            painterResource(R.drawable.loading_expedition),
            contentDescription = "first_screen",
            contentScale = ContentScale.FillWidth,
            alpha = alphaAnimation.value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
private fun ShowExpedition(expeditionModel: ExpeditionModel, heroInfo: HeroInfo?, onRun: () -> Unit, onCancel: () -> Unit) {
    val scrollState = rememberScrollState()

    val headStyle = TextStyle(
        color = Color.White,
        fontFamily = FontFamily(Font(R.font.micra_bold)),
        fontWeight = FontWeight.Bold,
        lineHeight = 20.sp,
        fontSize = 18.sp
    )
    val resultStyle = TextStyle(
        color = Color.White,
        fontFamily = FontFamily(Font(R.font.micra_bold)),
        fontWeight = FontWeight.Bold,
        lineHeight = 15.sp,
        fontSize = 12.sp
    )
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
        .verticalScroll(scrollState)) {
        Box() {
            Image(
                painterResource(R.drawable.expedition_head),
                contentDescription = "first_screen",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth()
            )
            Text (
                text = "Экспедиция " + expeditionModel.number.toString(),
                style = headStyle,
                color = Color.White,
                modifier = Modifier
                    .padding(start = 15.dp, bottom = 25.dp)
                    .align(Alignment.BottomStart))
        }
        Text (
            text = expeditionModel.text,
            style = MaterialTheme.typography.subtitle2,
            color = Color(0xFFD1D1D1),
            modifier = Modifier.padding(horizontal = 15.dp)
        )
        Spacer(modifier = Modifier.height(30.dp))
        Image(
            painterResource(R.drawable.diagn_head_bg),
            contentDescription = "first_screen",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
        when(expeditionModel.dangerLevel) {
            "Сложная" -> Image(
                painterResource(R.drawable.danger_1),
                contentDescription = "first_screen",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth())
            "Средняя" -> Image(
                painterResource(R.drawable.danger_2),
                contentDescription = "first_screen",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth())
            "Легкая" -> Image(
                painterResource(R.drawable.danger_3),
                contentDescription = "first_screen",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth())
            else -> Image(
                painterResource(R.drawable.godji_logo),
                contentDescription = "first_screen",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth())
        }
        Box () {
            val columnSize = remember {mutableStateOf(Size.Zero)}
            val imageHeight = with(LocalDensity.current) {
                if (columnSize.value.width == 0f)
                    360.dp
                else
                    columnSize.value.width.toInt().toDp()
            }
            Image(
                painterResource(R.drawable.expedition_bottom),
                contentDescription = "first_screen",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight)
            )
            Column(Modifier.onGloballyPositioned { coords -> columnSize.value = coords.size.toSize() }) {
                Spacer(modifier = Modifier.height(25.dp))
                MyParameters(heroInfo)
                Spacer(modifier = Modifier.height(25.dp))

                Box(modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically)
                    {
                        Text (
                            text = "ПОБЕДА",
                            style = resultStyle,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 15.dp)
                        )
                        Text (
                            text = "+" + expeditionModel.winReward.toString(),
                            style = resultStyle,
                            color = Color(0x80FFFFFF),
                            modifier = Modifier.padding(start = 15.dp)
                        )
                        Spacer(modifier = Modifier.padding(end = 5.dp))
                        Image(
                            painterResource(R.drawable.coin),
                            contentDescription = "coin",
                            contentScale = ContentScale.Fit,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))
                Box(modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically)
                    {
                        Text(
                            text = "ПОРАЖЕНИЕ",
                            style = resultStyle,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 15.dp)
                        )
                        Text(
                            text = "-" + expeditionModel.winReward.toString(),
                            style = resultStyle,
                            color = Color(0x80FFFFFF),
                            modifier = Modifier.padding(start = 15.dp)
                        )
                        Spacer(modifier = Modifier.padding(end = 5.dp))
                        Image(
                            painterResource(R.drawable.coin),
                            contentDescription = "coin",
                            contentScale = ContentScale.Fit,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(25.dp))
                Column(Modifier.padding(horizontal = 15.dp)) {
                    ButtonGo(text = "Да, исследовать") {
                        onRun()
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    ButtonBack(text = "нет, пока недостаточно\nинформации") {
                        onCancel()
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun MyParameters(heroInfo: HeroInfo?) {
    var state by remember { mutableStateOf(false) }

    Column(modifier = Modifier) {
        Box(contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .height(45.dp)
                .background(Color(0x802B2B2B))
                .fillMaxWidth()
                .clickable { state = !state }) {
            Row (verticalAlignment = Alignment.CenterVertically){
                Text (
                    text = "Мои текущие характеристики",
                    style = MaterialTheme.typography.button,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                if (state == false)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "arrow_down")
                else
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "arrow_down")
            }
        }
        val transition = updateTransition(state, label = "")
        transition.AnimatedVisibility(
            visible = { targetSelected -> targetSelected },
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Box(
                Modifier
                    .padding(horizontal = 15.dp)
                    .background(Color(0x802B2B2B)))
            {
                Column(Modifier.padding(horizontal = 10.dp)) {
                    UserParametersScale(
                        text = "СИЛА",
                        parameter = heroInfo?.stats?.power ?: 0,
                        isButtonActive = false,
                        background = R.drawable.pl_red
                    ) {
                    }
                    UserParametersScale(
                        text = "ИНТЕЛЛЕКТ",
                        parameter = heroInfo?.stats?.intellect ?: 0,
                        isButtonActive = false,
                        background = R.drawable.pl_blue
                    ) {
                    }
                    UserParametersScale(
                        text = "ХАРИЗМА",
                        parameter = heroInfo?.stats?.charisma ?: 0,
                        isButtonActive = false,
                        background = R.drawable.pl_gold
                    ) {
                    }
                    UserParametersScale(
                        text = "УДАЧА",
                        parameter = heroInfo?.stats?.fortune ?: 0,
                        isButtonActive = false,
                        background = R.drawable.pl_green
                    ) {
                    }
                }
            }
        }

    }

}