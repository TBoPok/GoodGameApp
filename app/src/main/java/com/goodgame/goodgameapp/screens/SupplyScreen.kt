package com.goodgame.goodgameapp.screens

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.goodgame.goodgameapp.R
import com.goodgame.goodgameapp.models.HeroInfo
import com.goodgame.goodgameapp.models.Reward
import com.goodgame.goodgameapp.models.ShopItem
import com.goodgame.goodgameapp.pager.Pager
import com.goodgame.goodgameapp.pager.PagerState
import com.goodgame.goodgameapp.retrofit.Response
import com.goodgame.goodgameapp.retrofit.Status
import com.goodgame.goodgameapp.screens.views.*
import com.goodgame.goodgameapp.viewmodel.GameViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newCoroutineContext
import kotlin.coroutines.CoroutineContext

@Composable
fun SupplyScreen(navController: NavController, viewModel: GameViewModel, initTab: Int = 0) {
    val heroInfo by viewModel.heroInfo.observeAsState()

    val howToActivateActive = remember { mutableStateOf(false)}
    val showReward = remember { mutableStateOf(false)}
    val textReward = remember { mutableStateOf("")}

    val showBuyConfirm = remember { mutableStateOf(false)}
    val itemConfirm = remember { mutableStateOf<ShopItem?>(null)}

    val loadingStateShop = remember { mutableStateOf(true)}
    val shopList = remember { mutableStateOf<List<ShopItem>?>(null)}
    if (loadingStateShop.value) {
        viewModel.getShopList().observe(LocalLifecycleOwner.current) {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data == null)
                        TODO()
                    else
                        shopList.value = it.data
                    loadingStateShop.value = false
                }
                Status.ERROR -> {
                    Log.d("HTTP", "SupplyScreen ${it.message}")
                    loadingStateShop.value = false
                }
                Status.LOADING -> {}
            }
        }
    }

    val loadingStateRewards = remember { mutableStateOf(true)}
    val rewardsList = remember { mutableStateOf<List<Reward>?>(null)}
    if (loadingStateRewards.value) {
        viewModel.getRewardList().observe(LocalLifecycleOwner.current) {
            when (it.status) {
                Status.SUCCESS -> {
                    rewardsList.value = it.data ?: listOf()
                    loadingStateRewards.value = false
                }
                Status.ERROR -> {
                    Log.d("HTTP", "SupplyScreen ${it.message}")
                    loadingStateRewards.value = false
                }
                Status.LOADING -> {}
            }
        }
    }

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
            ActionRowSupply(
                initTab,
                shopList = shopList.value,
                rewardList = rewardsList.value,
                buyClick = {shopItem -> showBuyConfirm.value = true; itemConfirm.value = shopItem},
                rewardClick = {reward -> showReward.value = true; textReward.value = reward},
                howToActivate = {howToActivateActive.value = true},)
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
                modifier = Modifier
                    .height(10.dp)
                    .padding(start = 5.dp)
                    .align(Alignment.CenterVertically),
                contentScale = ContentScale.FillHeight,
                )
            Text(
                text = "На базу",
                color = Color.White,
                lineHeight = 16.sp,
                modifier = Modifier.padding(start = 1.dp, end = 3.dp))
        }
    }
    if (howToActivateActive.value)
        HowToActivateView {
            howToActivateActive.value = false
        }
    if (showReward.value)
        RewardView(username = viewModel.username ?: "Юзернейм", text = textReward.value) {
            showReward.value = false
        }
    if (showBuyConfirm.value)
        BuyConfirmView(
            shopItem = itemConfirm.value!!,
            buyApply = viewModel.buyShopItem(itemConfirm.value!!),
            getHeroInfo = viewModel.getHeroInfo(),
            onDone = {
                showBuyConfirm.value = false
                rewardsList.value = null
                loadingStateRewards.value = true
            }
        )
}

data class TestRewardModel(val name: String, val count: Int)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ActionRowSupply(
    initTab: Int,
    shopList: List<ShopItem>?,
    rewardList: List<Reward>?,
    buyClick: (product: ShopItem) -> Unit,
    rewardClick: (reward: String) -> Unit,
    howToActivate: () -> Unit) {

    val pagesState = rememberPagerState(initialPage = initTab)
    val coroutineScope = rememberCoroutineScope()
    val newPage = remember { mutableStateOf(initTab)}
    val setPage: (page: Int) -> Unit = {
        coroutineScope.launch {
            pagesState.animateScrollToPage(it)
        }
    }

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

            TabChoice(pagesState.currentPage, onClick = setPage)
            Spacer(modifier = Modifier.height(10.dp))

            HorizontalPager(state = pagesState, verticalAlignment = Alignment.Top, count = 2) { page ->
                when (page) {
                    0 -> {
                        ShopList(
                            placeHolder = shopList == null,
                            shopItems = shopList ?: emptyList(),
                            buyClick = buyClick)
                    }
                    1 -> {
                        RewardsList(
                            placeholder = rewardList == null,
                            rewardItems = rewardList ?: emptyList(),
                            howToActivate = howToActivate,
                            rewardClicked = rewardClick)
                    }
                }
            }


        }
    }
}

@Composable
fun ShopList(
    placeHolder: Boolean,
    shopItems :List<ShopItem>,
    buyClick: (product: ShopItem) -> Unit,) {

    val chosenItem = remember {mutableStateOf(-1)}
    val isItemChosen = remember {mutableStateOf(false)}

    Box(Modifier
        .fillMaxHeight())
    {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(bottom = 60.dp)
        ) {
            if (placeHolder) {
                repeat(8) {
                    item {
                        ShopCardPlaceholder()
                    }
                }
            } else {
                items(items = shopItems) { shopItem ->
                    ShopCard(
                        shopItem = shopItem,
                        isActive = chosenItem.value == shopItem.id,
                        onClick = {
                            chosenItem.value = shopItem.id;
                            isItemChosen.value = true;
                        }
                    )
                }
            }
        }
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 3.dp)) {
            MetallButton(isActive = isItemChosen, height = 55.dp, activeText = "Вот это мне заверните, пожалуйста") {
                val chosenItem = shopItems.find {it.id == chosenItem.value}
                if (chosenItem != null)
                    buyClick(chosenItem)
            }
        }
    }
}

@Composable
fun ShopCardPlaceholder() {
    val transition = rememberInfiniteTransition() // animate infinite times
    val translateAnimation = transition.animateFloat( //animate the transition
        initialValue = -200f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500, // duration for the animation
                easing = FastOutLinearInEasing
            ),
        )
    )
    val backgroundBrush = Brush.linearGradient(
        colors = listOf(Color(0x80323232), Color(0x4D000000), Color(0x80323232)),
        start = Offset(translateAnimation.value + 200, translateAnimation.value + 200),
        end = Offset(translateAnimation.value,translateAnimation.value))

    Box(modifier = Modifier
        .padding(2.dp)
        .fillMaxWidth()
        .height(90.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(backgroundBrush)
        .border(
            1.dp,
            Color.Transparent,
            RoundedCornerShape(10.dp),
        )
    ) {

    }
}


@Composable
private fun RewardsList(placeholder: Boolean, rewardItems: List<Reward>, rewardClicked: (reward: String) -> Unit, howToActivate: () -> Unit) {
    val scrollState = rememberLazyListState()
    LazyColumn(state = scrollState, modifier = Modifier.padding(bottom = 60.dp)) {
        if (placeholder)
            repeat(8){ item { RewardCardPlaceholder()} }
        else
            items(items = rewardItems) {
                RewardCard(it.reward,1, onClick = rewardClicked)
                Spacer(modifier = Modifier.height(15.dp))
            }
    }
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 3.dp)) {
            HowToActivateButton {howToActivate()}
        }
    }
}

@Composable
private fun HowToActivateButton(onClick: () -> Unit) {
    Box (modifier = Modifier.clickable { onClick() },
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
            Row (modifier = Modifier.height(55.dp)) {
                Text(text = "Как активировать покупку",
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
                        Text(text = "?",
                            style = MaterialTheme.typography.button,
                            color = Color(0xFF000000),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(0.7f))
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardCard(name: String, count: Int, onClick: (reward: String) -> Unit) {

    Row(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .clip(RoundedCornerShape(15.dp))
        .background(Color.White)
        .clickable { onClick(name) }) {
        Text(
            text = name,
            style = MaterialTheme.typography.body1,
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier
                .padding(start = 15.dp)
                .align(Alignment.CenterVertically)
                .fillMaxWidth(0.7f)
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
                    fontSize = 15.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
            }
        }

    }
}


@Composable
private fun RewardCardPlaceholder() {
    val transition = rememberInfiniteTransition() // animate infinite times

    val translateAnimation = transition.animateFloat( //animate the transition
        initialValue = -200f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500, // duration for the animation
                easing = FastOutLinearInEasing
            ),
        )
    )
    val backgroundBrush = Brush.linearGradient(
        colors = listOf(Color(0x80ADADAD), Color(0x4DADADAD), Color(0x80ADADAD)),
        start = Offset(translateAnimation.value + 200, translateAnimation.value + 200),
        end = Offset(translateAnimation.value,translateAnimation.value))
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .clip(RoundedCornerShape(15.dp))
        .background(Color.White)
        .clickable { }) {

        Spacer(modifier = Modifier.weight(1f))
        Box(contentAlignment = Alignment.CenterEnd)
        {
            Image(
                painterResource(R.drawable.reward_count),
                contentDescription = "reward_count",
                contentScale = ContentScale.FillHeight,
                alpha = 0.5f,
                modifier = Modifier
                    .fillMaxHeight()
                    .border(
                        1.dp,
                        Color.White,
                        RoundedCornerShape(0.dp, 15.dp, 15.dp, 0.dp)
                    )
            )
            Box(Modifier.matchParentSize(), contentAlignment = Alignment.CenterEnd) {

            }
        }

    }
}

@Composable
private fun TabChoice(page: Int, onClick: (page: Int) -> Unit) {

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
                    .clickable { onClick(0) }) {
                Image(
                    painterResource(R.drawable.shop_tab),
                    contentDescription = "shop_tab",
                    contentScale = ContentScale.FillHeight,
                    alpha = if (page == 0) 1f else 0f,
                )
                Text (
                    text = "магазин",
                    style = MaterialTheme.typography.button,
                    color = if (page == 0) Color.Black else Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )
            }
            Box (modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .clickable { onClick(1) }) {
                Image(
                    painterResource(R.drawable.reward_tab),
                    contentDescription = "reward_tab",
                    contentScale = ContentScale.FillHeight,
                    alpha = if (page == 1) 1f else 0f,
                )
                Text (
                    text = "мои награды",
                    style = MaterialTheme.typography.button,
                    color = if (page == 1) Color.Black else Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )}
        }
    }
}

@Composable
private fun ShopCard(shopItem: ShopItem, isActive: Boolean, onClick: (it: ShopItem) -> Unit) {
    val backgroundBrush = when {
        shopItem.isAvailable -> Brush.linearGradient(
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
        .clickable { if (shopItem.isAvailable) onClick(shopItem) }) {
        Text (
            text = shopItem.title,
            style = MaterialTheme.typography.button,
            fontSize = 14.sp,
            color = if (shopItem.isAvailable) activeTextColor else notActiveTextColor,
            modifier = Modifier.padding(start = 10.dp, top = 6.dp, end = 10.dp)
        )
        Row (modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(bottom = 6.dp, end = 10.dp)
            .height(20.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = "${shopItem.cost}",
                style = MaterialTheme.typography.subtitle1,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                color = if (shopItem.isAvailable) activeTextColor else notActiveTextColor)
            Spacer(modifier = Modifier.padding(end = 5.dp))
            Image(
                painterResource(R.drawable.coin),
                contentDescription = "coin",
                contentScale = ContentScale.FillHeight,
                alpha = if (shopItem.isAvailable) 1f else 0.2f,
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
    val animCoins = animateFloatAsState(
        targetValue = (heroInfo?.coins ?: 0).toFloat(),
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing,
        )
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
                            text = animCoins.value.toInt().toString(),
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