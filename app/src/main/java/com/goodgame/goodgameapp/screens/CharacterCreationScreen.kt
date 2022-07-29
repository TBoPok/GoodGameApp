package com.goodgame.goodgameapp.screens

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import com.goodgame.goodgameapp.R
import com.goodgame.goodgameapp.models.CharacterModel
import com.goodgame.goodgameapp.models.characterTypes
import com.goodgame.goodgameapp.navigation.Screen
import com.goodgame.goodgameapp.navigation.clearBackStack
import com.goodgame.goodgameapp.pager.Pager
import com.goodgame.goodgameapp.pager.PagerState
import com.goodgame.goodgameapp.pager.rememberPagerState
import com.goodgame.goodgameapp.retrofit.Status
import com.goodgame.goodgameapp.screens.controls.CardFace
import com.goodgame.goodgameapp.screens.controls.FlipCard
import com.goodgame.goodgameapp.screens.views.MetallButton
import com.goodgame.goodgameapp.screens.views.ErrorAlert
import com.goodgame.goodgameapp.screens.views.LoadingView
import com.goodgame.goodgameapp.viewmodel.GameViewModel
import com.google.accompanist.pager.ExperimentalPagerApi

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun CharacterCreationScreen(navController: NavController, viewModel: GameViewModel) {
    val loadingViewActive = remember { mutableStateOf(false)}
    val isErrorMessageActive = remember { mutableStateOf(false)}
    val errorMessage = remember { mutableStateOf("")}


    val fontUsername = remember {
        TextStyle(
            fontFamily = FontFamily(Font(R.font.micra)),
            fontWeight = FontWeight(400),
            fontSize = 18.sp
        )
    }
    val fontSubtitle = remember {
        TextStyle(
            fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
            fontWeight = FontWeight(600),
            fontSize = 12.sp
        )
    }
    val pagerState = rememberPagerState()

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)){
        Row() { // Username row
            Box(modifier = Modifier
                .fillMaxWidth(),
                contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.pers_top),
                    contentDescription = "pers_top",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )

                Column() {
                    Text (
                        text = viewModel.username ?: "ЮЗЕРНЕЙМ",
                        style = fontUsername,
                        color = Color.White,
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                    Text (
                        text = "тебя ждут многие испытания\nне промахнись с выбором",
                        style = fontSubtitle,
                        color = Color.White,
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                }

            }
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.CenterHorizontally)) { // Character types
            Box(contentAlignment = Alignment.Center) {
                CharacterChoseRow(pagerState, characterTypes.map { it.name })
            }
        }
        Row (modifier = Modifier.weight(1f)) { // Cards row
            val cardFace = remember {
                val bufList = mutableListOf<MutableState<CardFace>>()
                characterTypes.forEach {
                    bufList.add(mutableStateOf(CardFace.Front))
                }
                bufList
            }
            Pager(
                items = characterTypes,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                itemFraction = .8f,
                overshootFraction = .3f,
                initialIndex = 0,
                itemSpacing = 0.dp,
                pagerState = pagerState,
                onClick = {cardFace[it].value = cardFace[it].value.next},
                contentFactory = { item ->
                    FlipCard(
                        cardFace = cardFace[item.id].value,
                        front = {
                            CharacterCard(characterModel = item)
                        },
                        back = {
                            CharacterCardBack(characterModel = item)
                        }
                    )
                }
            )
        }
        Row (modifier = Modifier.padding(horizontal = 30.dp, vertical = 12.dp)) { // ApprButton(isActive = mutableStateOf(true), activeText = "Выбрать его", height = 55.dp) {
            MetallButton(isActive = mutableStateOf(true), activeText = "Выбрать его") {
                loadingViewActive.value = true
          
            }
        }
    }
    if (loadingViewActive.value) {
        LoadingView()
        val chosenHeroType = characterTypes[pagerState.currentIndex].id_name
        viewModel.createHero(chosenHeroType).observe(LocalLifecycleOwner.current) {
            when (it.status) {
                Status.SUCCESS -> {
                    loadingViewActive.value = false
                    if (it.data?.status == true)
                        navController.navigate(Screen.SplashScreen.route) {
                            clearBackStack(navController, this)
                        }
                    else {
                        isErrorMessageActive.value = true
                        errorMessage.value = it.message ?: "Error create hero, no message"
                    }
                }
                Status.ERROR -> {
                    isErrorMessageActive.value = true
                    errorMessage.value = it.message ?: "Error create hero, no message"
                }
                Status.LOADING -> {}
            }
        }
    }
    if (isErrorMessageActive.value) {
        ErrorAlert(errorMessage = errorMessage.value,
            refresh = {
                isErrorMessageActive.value = false
                loadingViewActive.value = true
            },
            cancel = {
                isErrorMessageActive.value = false
            })
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun CharacterChoseRow(currentCharacter: PagerState, characterTypes: List<String>) {
    val scrollState = rememberLazyListState()
    val activeColor = Color(0xFFFFFFFF)
    val disactiveColor = Color(0x66FFFFFF)

    LazyRow (
        state = scrollState,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .height(34.dp)
            .fillMaxWidth())
    {
        items(items = characterTypes) { character ->
            Box(modifier = Modifier) {
                val micra = TextStyle(
                    fontFamily = FontFamily(Font(R.font.micra)),
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp
                )
                val itemColor = remember { mutableStateOf(disactiveColor) }
                if (characterTypes[currentCharacter.currentIndex] == character)
                    itemColor.value = activeColor
                else
                    itemColor.value = disactiveColor
                Text(
                    text = character,
                    style = micra,
                    color = itemColor.value,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp))
            }
        }
    }
}

@Composable
private fun CharacterCard(characterModel: CharacterModel)
{
    val cardSize = remember {mutableStateOf(Size.Zero)}

    Box(modifier = Modifier
        .fillMaxHeight()
        .onGloballyPositioned { coords -> cardSize.value = coords.size.toSize() },
    ) {
        Image(
            painterResource(characterModel.card_bg ?: R.drawable.bg),
            contentDescription = "bg_card_image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        val cardPaddingHorizontal = with(LocalDensity.current) {
            (cardSize.value.width * 0.13f).toInt().toDp()
        }
        val cardPaddingVertical = with(LocalDensity.current) {
            val Y2 = (cardSize.value.height - (cardSize.value.width / 0.57f))
            val Y1 = (Y2 / 2)
            (Y1 + (cardSize.value.width / 0.57f) * 0.07f).toInt().toDp()
        }
        if (cardSize.value != Size.Zero)
            Column (modifier = Modifier
                .padding(
                    start = cardPaddingHorizontal,
                    end = cardPaddingHorizontal,
                    bottom = cardPaddingVertical
                )
                .align(Alignment.BottomCenter)
            ) {
                Row () {
                    val font = TextStyle(
                        fontFamily = FontFamily(Font(R.font.micra)),
                        fontWeight = FontWeight(400),
                        fontSize = 12.sp
                    )
                    Text (
                        text = "Нажми для просмотра описания",
                        style = MaterialTheme.typography.body1,
                        fontSize = 15.sp,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }
                Row () {
                    Text (
                        text = "стартовые характеристики",
                        style = MaterialTheme.typography.subtitle1,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
                Row () {
                    CharacterParameterScale(text = "СИЛА", parameter = characterModel.power, R.drawable.pl_red)
                }
                Row () {
                    CharacterParameterScale(text = "ИНТЕЛЛЕКТ", parameter = characterModel.intelligence, R.drawable.pl_blue)
                }
                Row () {
                    CharacterParameterScale(text = "ХАРИЗМА", parameter = characterModel.charisma, R.drawable.pl_gold)
                }
                Row () {
                    CharacterParameterScale(text = "УДАЧА", parameter = characterModel.luck, R.drawable.pl_green)
                }
            }
    }
}

@Composable
private fun CharacterCardBack(characterModel: CharacterModel)
{
    val cardSize = remember {mutableStateOf(Size.Zero)}

    Box(modifier = Modifier
        .fillMaxHeight()
        .onGloballyPositioned { coords -> cardSize.value = coords.size.toSize() },
    ) {
        Image(
            painterResource(characterModel.card_bg ?: R.drawable.bg),
            contentDescription = "bg_card_image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        val cardPaddingHorizontal = with(LocalDensity.current) {
            (cardSize.value.width * 0.13f).toInt().toDp()
        }
        val cardPaddingVertical = with(LocalDensity.current) {
            val Y2 = (cardSize.value.height - (cardSize.value.width / 0.57f))
            val Y1 = (Y2 / 2)
            (Y1 + (cardSize.value.width / 0.57f) * 0.07f).toInt().toDp()
        }
        if (cardSize.value != Size.Zero)
            Column (modifier = Modifier
                .padding(
                    start = cardPaddingHorizontal,
                    end = cardPaddingHorizontal,
                    bottom = cardPaddingVertical
                )
                .align(Alignment.BottomCenter)
                .fillMaxHeight(0.50f)
            ) {
                Row () {
                    val font = TextStyle(
                        fontFamily = FontFamily(Font(R.font.micra)),
                        fontWeight = FontWeight(400),
                        fontSize = 15.sp
                    )
                    Text (
                        text = characterModel.description,
                        style = MaterialTheme.typography.subtitle2,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }
            }
    }
}

@Composable
private fun CharacterParameterScale(text: String, parameter: Int, @DrawableRes background: Int) {
    val maxParameter = 7
    val minParameter = 3
    val heroParameter = remember {
        when {
            parameter < minParameter -> return@remember minParameter
            parameter > maxParameter -> return@remember maxParameter
            else -> return@remember parameter
        }
    }
    val minLengthScale = 50
    val pointLength = (100 - minLengthScale) / (maxParameter - minParameter)
    val scaleLength = (minLengthScale + (heroParameter - minParameter) * pointLength) / 100f

    val font = TextStyle(
        fontFamily = FontFamily(Font(R.font.micra_bold)),
        fontWeight = FontWeight(400),
        fontSize = 12.sp
    )

    Box(modifier = Modifier
        .height(38.dp)
        .fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
        ) {
        Image(
            painter = painterResource(background),
            contentDescription = "scale_bg",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth(fraction = scaleLength - 0.1f)
                .height(30.dp)
                .clip(shape = RoundedCornerShape(12.dp)))

        Text(
            text = text,
            color = Color.White,
            style = font,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
        Text(
            text = heroParameter.toString(),
            color = Color.White,
            style = MaterialTheme.typography.subtitle2,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterEnd)
        )

    }
}



@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalPagerApi::class)
@Preview
@Composable
private fun CreateCharPreview() {
    val pagerState = rememberPagerState()

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)){
        Row() { // Username row
            Box(modifier = Modifier
                .fillMaxWidth(),
                contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.pers_top),
                    contentDescription = "pers_top",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )
                val fontUsername = TextStyle(
                    fontFamily = FontFamily(Font(R.font.micra)),
                    fontWeight = FontWeight(400),
                    fontSize = 18.sp
                )
                val fontSubtitle = TextStyle(
                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                    fontWeight = FontWeight(600),
                    fontSize = 12.sp
                )
                Column() {
                    Text (
                        text = "ЮЗЕРНЕЙМ",
                        style = fontUsername,
                        color = Color.White,
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                    Text (
                        text = "тебя ждут многие испытания\nне промахнись с выбором",
                        style = fontSubtitle,
                        color = Color.White,
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                }

            }
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.CenterHorizontally)) { // Character types
            Box(contentAlignment = Alignment.Center) {
                CharacterChoseRow(pagerState, characterTypes.map { it.name })
            }
        }
        Row (modifier = Modifier.weight(1f)) { // Cards row
            Pager(
                items = characterTypes,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                itemFraction = .75f,
                overshootFraction = .3f,
                initialIndex = 0,
                itemSpacing = 0.dp,
                pagerState = pagerState,
                contentFactory = { item ->
                    CharacterCard(characterModel = item)
                }
            )
        }
        Row (modifier = Modifier.padding(horizontal = 20.dp, vertical = 7.dp)) { // Apply row
            MetallButton(isActive = mutableStateOf(true), activeText = "Выбрать его", height = 55.dp) {

            }
        }
        Row() {

        }
    }
}