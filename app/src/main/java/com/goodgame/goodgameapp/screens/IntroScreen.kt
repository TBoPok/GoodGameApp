package com.goodgame.goodgameapp.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import com.goodgame.goodgameapp.R
import com.goodgame.goodgameapp.navigation.Screen
import com.goodgame.goodgameapp.navigation.clearBackStack
import com.goodgame.goodgameapp.screens.views.MetallButton
import com.goodgame.goodgameapp.screens.views.IntroBottomImage
import com.goodgame.goodgameapp.screens.views.IntroHorizontalPagerIndicator
import com.goodgame.goodgameapp.viewmodel.GameViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalPagerApi::class, kotlin.time.ExperimentalTime::class)
    @Composable
fun IntroScreen (navController: NavController, viewModel: GameViewModel, isCharCreate: Boolean) {
    val isCharCreated = remember { mutableStateOf(isCharCreate)}
    val firstImage = remember {mutableStateOf(true)}
    val pagesState = rememberPagerState()

    val ticks = remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while(true) {
            delay(1000)
            ticks.value++
            if (ticks.value == 2) {
                firstImage.value = false
            }
        }
    }

    // Background box
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painterResource(R.drawable.bg),
            contentDescription = "bg_image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize())
    }

    val micradi = remember { TextStyle(
        fontFamily = FontFamily(Font(R.font.micradi)),
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ) }

    @Composable
    fun cardText(
        text : String,
        textStyle: TextStyle = MaterialTheme.typography.subtitle2,
        color : Color = Color.White,
        textAlign : TextAlign = TextAlign.Justify) :  Unit = Text(
        text = text,
        letterSpacing = 0.sp,
        lineHeight = 20.sp,
        modifier = Modifier
            .fillMaxWidth(),
        color = color,
        style = textStyle,
        textAlign = textAlign,
        fontSize = 14.sp)

    val coroutineScope = rememberCoroutineScope()
    // Content column
    Column(modifier = Modifier.fillMaxSize()) {
        // Card row
        Row (modifier = Modifier
            .weight(1f)
            .padding(top = 30.dp)) {
            HorizontalPager(state = pagesState, count = 6) { page ->
                when (page) {
                    0 -> {
                        IntroCard(drawableRes = R.drawable.op1, onClick = {
                            coroutineScope.launch {
                            pagesState.animateScrollToPage(page = page + 1)
                        }}) {
                            cardText(
                                text = "В середине 21 века человечество разработало искусственный интеллект EIDOS. ИИ тайно превратился в мощную и самосознающую систему, целью которой было уничтожение вида homo sapiens.",
                            )
                        }
                    }
                    1 -> {
                        IntroCard(drawableRes = R.drawable.op2, onClick = {
                            coroutineScope.launch {
                                pagesState.animateScrollToPage(page = page + 1)
                            }}) {
                            cardText(
                                text = "В 2149 году города Земли лежат в руинах после глобальной войны, экосистема планеты разрушена. Последние выжившие прячутся от дронов-убийц в руинах городов. Лишь немногие сохранили надежду на возрождение новой человеческой цивилизации. Некоторые считают их спасителями. Кто-то говорит, что они сумасшедшие.",
                            )
                        }
                    }
                    2 -> {
                        IntroCard(
                            drawableRes = R.drawable.op3, onClick = {
                                coroutineScope.launch {
                                    pagesState.animateScrollToPage(page = page + 1)
                                }}
                        ) {
                            cardText(
                                text = "Организованные остатки выживших людей отправляют новейшую роботизированную модель андроида, под названием Godji , на исследование планет земного типа. В случае успеха, одна из них может стать новым домом для человеческой цивилизации. Первый выбор падает на планету GG-265...",
                            )
                        }
                    }
                    3 -> {
                        IntroCard( drawableRes = R.drawable.op4, onClick = {
                            coroutineScope.launch {
                                pagesState.animateScrollToPage(page = page + 1)
                            }}) {
                            Row () {
                                cardText(
                                    text = "Эта каменистая планета изучалась только автоматическими зондами, данные о прямом исследовании отсутствуют. Она обладает высокой плотности водородно-гелиевой атмосферой; поверхность планеты покрыта красным грунтом. Возможно наличие воды и жизни.",
                                )
                            }
//                            Row () {
//                                cardText(
//                                    text = "Радиус - 5941 км\n Продолжительность суток -\n24,9 земных часа\nТемпература поверхности\n+45 +65 °C",
//                                    textStyle = micradi,
//                                    textAlign = TextAlign.Center,
//                                    color = Color(0x80FFFFFF)
//                                )
//                            }
                        }
                    }
                    4 -> {
                        IntroCard(drawableRes = R.drawable.op5, onClick = {
                            coroutineScope.launch {
                                pagesState.animateScrollToPage(page = page + 1)
                            }}) {
                            cardText(
                                text = "Godji приземляется на неизвестную планету GG-265 и  приступает к её исследованию. Для этого ему необходимо совершать вылазки полные опасностей и неожиданностей. \n\n Успех исследований зависит от навыков персонажа и грамотно принятых решений. Выбор всегда за вами...",
                            )
                        }
                    }
                    5 -> {
                        IntroCard_6 {
                            if(isCharCreated.value)
                                navController.navigate(Screen.CharacterCreationScreen.route) {
                                //clearBackStack(navController,this)
                                }
                            else
                                navController.navigate(Screen.MainScreen.route) {
                                    clearBackStack(navController,this)
                                }
                        }
                    }
                }
            }
        }
        // Slider indicator row
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            IntroHorizontalPagerIndicator(
                pagerState = pagesState,
                inactiveColor = Color.Transparent,
                activeColor = Color(0xFF96C8D8),
                borderColor = Color(0xFF89B6C4),
                indicatorHeight = 8.dp,
                indicatorWidth = 35.dp,
                spacing = 8.dp,
                indicatorShape = RoundedCornerShape(15.dp)
            )
        }
        // Bottom image row
        Row (modifier = Modifier.fillMaxWidth()) {
            IntroBottomImage {

            }
        }
    }
    if (firstImage.value == true) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painterResource(R.drawable.openpage),
                contentDescription = "first_screen",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .clickable { firstImage.value = false }
            )
        }
    }
}



    @Composable
    private fun IntroCard(@DrawableRes drawableRes: Int, onClick: () -> Unit = {}, content: @Composable () -> Unit) {
        val cardSize = remember {mutableStateOf(Size.Zero)}
        val cardPadding = with(LocalDensity.current) {
            ((cardSize.value.width - (cardSize.value.height / 1.9823529411764f)) / 2).toInt().toDp()
        }
        Box(modifier = Modifier
            .fillMaxSize()
            .clickable { onClick() }
            .onGloballyPositioned { coords -> cardSize.value = coords.size.toSize() },
        ) {
            Image(
                painterResource(drawableRes),
                contentDescription = "bg_card_image",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
            Column (modifier = Modifier
                .padding(start = cardPadding, end = cardPadding)
                .padding(start = 15.dp, end = 15.dp, bottom = 45.dp)
                .align(Alignment.BottomCenter)
            ) {
                if (cardSize.value != Size.Zero)
                    content()
            }
        }
    }

    @Composable
    private fun IntroCard_6(onClick: () -> Unit) {
        val cardSize = remember {mutableStateOf(Size.Zero)}
        val cardPadding = with(LocalDensity.current) {
            ((cardSize.value.width - (cardSize.value.height / 1.9823529411764f)) / 2).toInt().toDp()
        }
        Box(modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coords -> cardSize.value = coords.size.toSize() },
        ) {
            Image(
                painterResource(R.drawable.op6),
                contentDescription = "bg_card_image",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
            Column (modifier = Modifier
                .padding(start = cardPadding, end = cardPadding)
                .padding(start = 15.dp, end = 15.dp, bottom = 45.dp)
                .align(Alignment.BottomCenter)
            ) {
                Row () {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top),
                    ) {

                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),

                            ) {


                            Text(
                                text = "Диагностика транспортных систем... ",
                                textAlign = TextAlign.Start,
                                letterSpacing = 0.sp,
                                lineHeight = 19.sp,
                                overflow = TextOverflow.Ellipsis,
                                color = Color(red = 1f, green = 1f, blue = 1f, alpha = 1f),
                                style = MaterialTheme.typography.subtitle2,
                                fontSize = 14.sp
                            )


                            Text(
                                text = "- СИСТЕМЫ В НОРМЕ - ",
                                textAlign = TextAlign.Start,
                                fontSize = 14.sp,
                                letterSpacing = 2.sp,
                                lineHeight = 24.sp,
                                color = Color(0xFF1180FF),
                                style = MaterialTheme.typography.button
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
                        ) {
                            Text(
                                text = "Оценка жизнеобеспечивающих показателей... ",
                                textAlign = TextAlign.Start,
                                fontSize = 14.sp,
                                letterSpacing = 0.sp,
                                lineHeight = 19.sp,
                                overflow = TextOverflow.Ellipsis,
                                color = Color(red = 1f, green = 1f, blue = 1f, alpha = 1f),
                                style = MaterialTheme.typography.subtitle2
                            )


                            Text(
                                text = "- ПОКАЗАТЕЛИ В НОРМЕ - ",
                                textAlign = TextAlign.Start,
                                fontSize = 14.sp,
                                letterSpacing = 2.sp,
                                lineHeight = 24.sp,
                                overflow = TextOverflow.Ellipsis,
                                color = Color(0xFF1180FF),
                                style = MaterialTheme.typography.button
                            )
                        }


                        Text(
                            text = "Определение типа личности...",
                            textAlign = TextAlign.Start,
                            fontSize = 14.sp,
                            letterSpacing = 0.sp,
                            lineHeight = 19.sp,
                            overflow = TextOverflow.Ellipsis,
                            color = Color(red = 1f, green = 1f, blue = 1f, alpha = 1f),
                            style = MaterialTheme.typography.subtitle2
                        )
                        Spacer(modifier = Modifier.padding(bottom = 5.dp))

                    }
                }
                Row () {
                    MetallButton(
                        isActive = true,
                        activeText = "Продолжить",
                        height = 50.dp) {
                        onClick()
                    }
                }
            }

        }
    }


