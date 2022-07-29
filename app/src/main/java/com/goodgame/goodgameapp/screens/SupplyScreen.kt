package com.goodgame.goodgameapp.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.goodgame.goodgameapp.R
import com.goodgame.goodgameapp.models.HeroInfo
import com.goodgame.goodgameapp.screens.views.MetallButton
import com.goodgame.goodgameapp.viewmodel.GameViewModel

@Composable
fun SupplyScreen(navController: NavController, viewModel: GameViewModel, initTab: Int = 0) {
    val heroInfo by viewModel.heroInfo.observeAsState()

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black))
    Column(modifier = Modifier
//        .verticalScroll(scrollState)
        .fillMaxHeight()
    ) {
        Row { // Head row
            HeadSupply(heroInfo)
        }
        Row (Modifier.fillMaxSize()) { // Action row
            ActionRowSupply(initTab)
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
                lineHeight = 16.sp,
                modifier = Modifier.padding(start = 1.dp, end = 3.dp))
        }
    }
}

data class testShopItem(val name: String, val cost: Int, val isAvailable: Boolean)
data class TestRewardModel(val name: String, val count: Int)

@Composable
fun ActionRowSupply(initTab: Int) {
    val testListShop = listOf(testShopItem("Тестовая карточка", 100, true),
        testShopItem("Гречка", 200, true),
        testShopItem("Картофель", 3300, true),
        testShopItem("Мастеркард", 999999, false))
    val testRewardModels = listOf(
        TestRewardModel("Ваз 2114", 1),
        TestRewardModel("Стиральная машина", 1),
        TestRewardModel("Кот", 1)
    )

    val tabState = remember { mutableStateOf(initTab)} // 0 - Магазин, 1 - мои награды
    Box {
        Image(
            painterResource(R.drawable.diag_bottom_bg),
            contentDescription = "scroll_bg_image",
            modifier = Modifier
                .fillMaxSize()
                .scale(scaleY = 1.45f, scaleX = 1f),
            contentScale = ContentScale.FillWidth,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(25.dp))
            TabChoice(state = tabState)
            Spacer(modifier = Modifier.height(10.dp))
            when (tabState.value) {
                0 -> {
                    ShopList(testListShop)
                }
                1 -> {
                    RewardsList(testRewardModels)
                }
            }

        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShopList(shopItems :List<testShopItem>) {
    val chosenItem = remember {mutableStateOf("")}
    val isItemChosen = remember {mutableStateOf(false)}
    LazyVerticalGrid(cells = GridCells.Fixed(2), modifier = Modifier.padding(bottom = 60.dp)) {
        items(items = shopItems) { product ->
            ShopCard(
                name = product.name,
                cost = product.cost,
                isAvailable = product.isAvailable,
                isActive = chosenItem.value == product.name,
                onClick = {chosenItem.value = it; isItemChosen.value = true}
            )
        }
    }
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 3.dp)) {
            MetallButton(isActive = isItemChosen, height = 55.dp, activeText = "Вот это мне заверните, пожалуйста") {

            }
        }
    }
}


@Composable
private fun RewardsList(rewardItems: List<TestRewardModel>) {
    val scrollState = rememberLazyListState()
    LazyColumn(state = scrollState) {
        items(items = rewardItems) {
            RewardCard(it.name,it.count)
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}

@Composable
private fun RewardCard(name: String, count: Int) {

    Row(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .clip(RoundedCornerShape(15.dp))
        .background(Color.White)
        .clickable { }) {
        Text(
            text = name,
            style = MaterialTheme.typography.body1,
            color = Color.Black,
            modifier = Modifier
                .padding(start = 15.dp)
                .align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(contentAlignment = Alignment.CenterEnd)
        {
            Image(
                painterResource(R.drawable.reward_count),
                contentDescription = "reward_count",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxHeight()
                    .border(
                        1.dp,
                        Color.White,
                        RoundedCornerShape(0.dp, 15.dp, 15.dp, 0.dp)
                    )
            )
            Box(Modifier.matchParentSize(), contentAlignment = Alignment.CenterEnd) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.body1,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
            }
        }

    }
}

@Composable
private fun TabChoice(state : MutableState<Int>) {
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(15.dp))
            .border(1.dp, Color(0xFFBFEFFC), RoundedCornerShape(15.dp))
    ) {
        Row (horizontalArrangement = Arrangement.Center) {
            Box (
                Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .clickable { state.value = 0 }) {
                Image(
                    painterResource(R.drawable.shop_tab),
                    contentDescription = "shop_tab",
                    contentScale = ContentScale.FillHeight,
                    alpha = if (state.value == 0) 1f else 0f,
                )
                Text (
                    text = "магазин",
                    style = MaterialTheme.typography.button,
                    color = if (state.value == 0) Color.Black else Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )
            }
            Box (modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .clickable { state.value = 1 }) {
                Image(
                    painterResource(R.drawable.reward_tab),
                    contentDescription = "reward_tab",
                    contentScale = ContentScale.FillHeight,
                    alpha = if (state.value == 1) 1f else 0f,
                )
                Text (
                    text = "мои награды",
                    style = MaterialTheme.typography.button,
                    color = if (state.value == 1) Color.Black else Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )}
        }
    }
}

@Composable
private fun ShopCard(name: String, cost: Int, isAvailable: Boolean, isActive: Boolean, onClick: (it: String) -> Unit) {
    val backgroundBrush = when {
//        isActive -> Brush.linearGradient(
//            colors = listOf(Color(0xFF5E5E5E), Color(0xFF303030)),
//            start = Offset(Offset.Infinite.x / 2, 0f),
//            end = Offset(Offset.Infinite.x * 2 / 3,Offset.Infinite.y * 2),
//            tileMode = TileMode.Clamp)
        isAvailable -> Brush.linearGradient(
            colors = listOf(Color(0xFF323232), Color(0xFF000000)),
            start = Offset(Offset.Infinite.x / 2, 0f),
            end = Offset(Offset.Infinite.x * 2 / 3,Offset.Infinite.y * 2),
            tileMode = TileMode.Clamp)
        else -> Brush.linearGradient(
            colors = listOf(Color(0x80323232), Color(0x80000000)),
            start = Offset(Offset.Infinite.x / 2, 0f),
            end = Offset(Offset.Infinite.x * 2 / 3,Offset.Infinite.y * 2),
            tileMode = TileMode.Clamp)
    }
    val activeBordersColor = Color.Blue
    val activeTextColor = Color(0xFFFFFFFF)
    val notActiveTextColor = Color(0x80FFFFFF)

    Box(modifier = Modifier
        .padding(2.dp)
        .fillMaxWidth()
        .height(90.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(backgroundBrush)
        .border(
            1.dp,
            if (isActive) activeBordersColor else Color.Transparent,
            RoundedCornerShape(10.dp),
        )
        .clickable { if (isAvailable) onClick(name) }) {
        Text (
            text = name,
            style = MaterialTheme.typography.button,
            color = if (isAvailable) activeTextColor else notActiveTextColor,
            modifier = Modifier.padding(start = 10.dp, top = 6.dp, end = 10.dp)
        )
        Row (modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(bottom = 6.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = "$cost",
                style = MaterialTheme.typography.subtitle1,
                fontSize = 15.sp,
                lineHeight = 16.sp,
                color = if (isAvailable) activeTextColor else notActiveTextColor)
            Spacer(modifier = Modifier.padding(end = 5.dp))
            Image(
                painterResource(R.drawable.coin),
                contentDescription = "coin",
                contentScale = ContentScale.FillHeight,
                alpha = if (isAvailable) 1f else 0.2f,
            )
        }
    }
}


@Composable
private fun HeadSupply(heroInfo: HeroInfo?) {
    val headTextStyle = TextStyle(
        color = Color.White,
        fontFamily = FontFamily(Font(R.font.micra_bold)),
        fontWeight = FontWeight.Bold,
        lineHeight = 24.sp,
        fontSize = 18.sp
    )
    Box (Modifier.fillMaxWidth()) {
        Image(
            painterResource(R.drawable.supply_head_bg),
            contentDescription = "head_image",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
        Column(modifier = Modifier.matchParentSize()) {
            Row(modifier = Modifier.weight(0.28f)) {}
            Row(modifier = Modifier.weight(0.16f)) {
                Text(
                    text = "СИСТЕМА\nСНАБЖЕНИЯ",
                    style = headTextStyle,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.8f)
                        .padding(start = 20.dp)
                )
            }
            Row(modifier = Modifier.weight(0.20f)) {}
            Row(modifier = Modifier.weight(0.24f)) {
                Column(Modifier.padding(start = 20.dp)) {
                    Row(Modifier.height(40.dp)) {
                        Image(
                            painterResource(R.drawable.coin),
                            contentDescription = "coin",
                            contentScale = ContentScale.FillHeight,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "333",
                            style = MaterialTheme.typography.h1,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                    Row {
                        Box (Modifier.clickable {  }) {
                            Text(
                                text = "Как пополнить?",
                                style = MaterialTheme.typography.subtitle1,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            Row(modifier = Modifier.weight(0.1f)) {}
        }
    }
}

@Preview@Composable
private fun SupplyPreview() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black))
    Column(modifier = Modifier
//        .verticalScroll(scrollState)
        .fillMaxHeight()
    ) {
        Row { // Head row
            HeadSupply(null)
        }
        Row (Modifier.fillMaxSize()) { // Action row
            ActionRowSupply(0)
        }

    }
    Box (modifier = Modifier
        .padding(start = 20.dp, top = 20.dp)
        .background(Color.Black)
        .clickable { }) {
        Row {
            Image(
                painter = painterResource(R.drawable.arrow_back_ios),
                contentDescription = "arrow_back",
                contentScale = ContentScale.FillHeight,
            )
            Icon(Icons.Filled.ArrowBack,"",tint = Color.White)
            Text(
                text = "На базу",
                color = Color.White,
                modifier = Modifier.padding(start = 1.dp, end = 3.dp))
        }
    }
}