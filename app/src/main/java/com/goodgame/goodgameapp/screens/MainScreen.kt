package com.goodgame.goodgameapp.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import com.goodgame.goodgameapp.R
import com.goodgame.goodgameapp.models.ExpeditionStoryModel
import com.goodgame.goodgameapp.viewmodel.GameViewModel
import com.goodgame.goodgameapp.models.HeroInfo
import com.goodgame.goodgameapp.models.characterTypes
import com.goodgame.goodgameapp.navigation.BackPressedTimer
import com.goodgame.goodgameapp.navigation.Screen
import com.goodgame.goodgameapp.navigation.clearBackStack
import com.goodgame.goodgameapp.screens.views.ExpeditionCompletedView
import com.goodgame.goodgameapp.screens.views.FadeTransition
import com.goodgame.goodgameapp.screens.views.LevelUpView
import com.goodgame.goodgameapp.screens.views.SimplePopUp
import kotlinx.coroutines.delay
import java.io.IOException

@Composable
fun MainScreen(navController: NavController, viewModel: GameViewModel) {
    val scrollState = rememberScrollState()

    val heroInfo by viewModel.heroInfo.observeAsState()
    var exitAccountPopUp by remember { mutableStateOf(false)}

    if (heroInfo?.stats == null) {
        navController.navigate(Screen.SplashScreen.route) {
            navController.backQueue.clear()
        }
    }

    BackPressedTimer()

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF010101))
        .verticalScroll(scrollState)
    ) {
        Row  { // Head image and info
            HeadMain(username = viewModel.username, heroInfo = heroInfo, navController = navController)
        }
        Row (modifier = Modifier
            .fillMaxWidth()
        ) { // Actions row
            ActionMain(navController = navController, heroInfo = heroInfo, exitAccount = {exitAccountPopUp = true})
        }
    }

    var showExpeditionSuccess by remember { mutableStateOf(false)}
    var showLvlUp by remember { mutableStateOf(false)}

    LaunchedEffect(heroInfo?.expedition_passed) {
        delay(300)
        showExpeditionSuccess = heroInfo?.expedition_passed ?: false
        delay(300)
        showLvlUp = heroInfo?.new_level == true && !showExpeditionSuccess
    }


    if (showLvlUp && !showExpeditionSuccess) {
        LevelUpView(
            heroInfo = heroInfo) {
            showLvlUp = false
            heroInfo?.new_level = false
            navController.navigate(Screen.DiagnosticsScreen.route)
        }
    }

    if (showExpeditionSuccess) {
        ExpeditionCompletedView(
            expedition = heroInfo?.expeditions?.last()
                ?: ExpeditionStoryModel(exp = 0, rsp = 0, name = null, 0, result = "exp", dateTime = null)) {
            heroInfo?.expedition_passed = false
            showExpeditionSuccess = false
        }
    }

    if (exitAccountPopUp) {
        SimplePopUp(headText = "?????????? ???? ?????????????????",
            onApply = {
                viewModel.exitAccount()
                navController.navigate(Screen.SplashScreen.route) {
                    clearBackStack(navController, this)
                }
                exitAccountPopUp = false
            },
            onClose = {
                exitAccountPopUp = false
            })
    }

}

@Composable
private fun HeadMain(username: String?, heroInfo: HeroInfo?, navController: NavController) {
    Box (Modifier.fillMaxWidth()) {
        Image(
            painterResource(R.drawable.basehead),
            contentDescription = "head_image",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
        Column(modifier = Modifier.matchParentSize()) {
            Row (Modifier.weight(.08f)) { } // Empty row
            Row (
                Modifier
                    .weight(.094f)
                    .fillMaxWidth()) {
                ExperienceGraphics(heroInfo = heroInfo) {
                    navController.navigate(Screen.DiagnosticsScreen.route)
                }
            } // Experience row
            Row (
                Modifier
                    .padding(horizontal = 30.dp)
                    .weight(0.07f),
                verticalAlignment = Alignment.CenterVertically) {
                UserInfoRow(username, heroInfo?.heroClass)
            }
            Row (Modifier.weight(.79f) ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomEnd)
                {
                    // TODO ?????? ???????????? ?????????????? ???????????? ????????????????
                    when (heroInfo?.heroClass) {
                        "charisma" -> {
                            Image(
                                painterResource(R.drawable.hero_lead),
                                contentDescription = "godji",
                                modifier = Modifier.fillMaxHeight(),
                                contentScale = ContentScale.FillHeight,
                            )
                        }
                        "power" -> {
                            Image(
                                painterResource(R.drawable.hero_warrior),
                                contentDescription = "godji",
                                modifier = Modifier.fillMaxHeight(),
                                contentScale = ContentScale.FillHeight,
                            )
                        }
                        "fortune" -> {
                            Image(
                                painterResource(R.drawable.hero_gamer),
                                contentDescription = "godji",
                                modifier = Modifier.fillMaxHeight(),
                                contentScale = ContentScale.FillHeight,
                            )
                        }
                        "intellect" -> {
                            Image(
                                painterResource(R.drawable.hero_sci),
                                contentDescription = "godji",
                                modifier = Modifier.fillMaxHeight(),
                                contentScale = ContentScale.FillHeight,
                            )
                        }
                        else -> {
                            Image(
                                painterResource(R.drawable.hero_warrior),
                                contentDescription = "godji",
                                modifier = Modifier.fillMaxHeight(),
                                contentScale = ContentScale.FillHeight,
                            )
                        }
                    }


                }
            } // Godji row
        }

    }
}

@Composable
fun ExperienceGraphics(heroInfo: HeroInfo?, onClick: () -> Unit = {}) {
    val fontLevel = TextStyle(
        color = Color.White,
        fontFamily = FontFamily(Font(R.font.micra_bold)),
        fontWeight = FontWeight(400),
        fontSize = 18.sp
    )
    val fontNextLevel = TextStyle(
        color = Color.White,
        fontFamily = FontFamily(Font(R.font.micra_bold)),
        fontWeight = FontWeight(400),
        fontSize = 9.sp
    )
    val lvlImgSize = remember { mutableStateOf(Size.Zero) }
    val lvlNextImgSize = remember { mutableStateOf(Size.Zero) }
    val progressPadding = with(LocalDensity.current) {
        PaddingValues(
            start = (lvlImgSize.value.width * 0.43f).toInt().toDp(),
            end = (lvlNextImgSize.value.width * 0.68f).toInt().toDp())
    }
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }, contentAlignment = Alignment.Center) {
        Box (modifier = Modifier
            .fillMaxHeight(0.65f)
            .fillMaxWidth()
            .border(1.dp, Color.White, shape = RectangleShape)
        ) {
            ProgressScale(
                padding = progressPadding,
                min = 0,
                max = heroInfo?.next_lvl_need ?: 0,
                value = heroInfo?.lvl_exp ?: 0)
        }
        Row {
            Box()
            {
                Image(
                    painterResource(R.drawable.lvl),
                    contentDescription = "lvl",
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .fillMaxHeight()
                        .onGloballyPositioned {
                            lvlImgSize.value = it.size.toSize()
                        },
                )
                Box (modifier = Modifier.matchParentSize(), contentAlignment = Alignment.CenterStart ) {
                    Text(text = heroInfo?.level.toString(),
                        style = fontLevel,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(0.7f))
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Box()
            {
                Image(
                    painterResource(R.drawable.lvl_next),
                    contentDescription = "lvl_next",
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .fillMaxHeight()
                        .onGloballyPositioned {
                            lvlNextImgSize.value = it.size.toSize()
                        },
                )
                Box (modifier = Modifier.matchParentSize(), contentAlignment = Alignment.CenterEnd ) {
                    Text(text = heroInfo?.next_lvl_need.toString(),
                        textAlign = TextAlign.Center,
                        style = fontNextLevel,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(0.7f))
                }

            }
        }
    }
}

@Composable
private fun ProgressScale(
    padding : PaddingValues,
    min: Int,
    max: Int,
    value: Int
) {
    val progressSize = remember { mutableStateOf(Size.Zero) }
    val offset = with(LocalDensity.current) {
        if (max != 0)
            ((progressSize.value.width / (max - min)) * (value - max)).toInt().toDp()
        else
            0f.toDp()
    }
    val fontNextLevel = TextStyle(
        fontFamily = FontFamily(Font(R.font.micra)),
        fontWeight = FontWeight(400),
        fontSize = 11.sp
    )
    Box(modifier = Modifier
        .fillMaxWidth()
        .offset(x = offset)
        .padding(padding),
        ) {
        Image(
            painterResource(R.drawable.progresslvl),
            contentDescription = "progresslvl",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { progressSize.value = it.size.toSize() }
                .clip(RoundedCornerShape(15.dp)),
        )
    }
    Box (modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Text (
            text = value.toString(),
            style = fontNextLevel,
            color = Color.White
        )
    }
}

@Composable
private fun ActionMain(heroInfo: HeroInfo?, navController: NavController, exitAccount: () -> Unit) {
    Box (Modifier.background(Color(0xFF010101))) {
        Image(
//            painterResource(R.drawable.base_scroll_bg),
            painterResource(R.drawable.diag_bottom_bg),
            contentDescription = "scroll_bg_image",
            modifier = Modifier
                .fillMaxSize()
                .scale(scaleY = 1.10f, scaleX = 1f),
            contentScale = ContentScale.FillWidth,
        )
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.padding(vertical = 2.dp))
            LobbyButton(
                drawableRes = R.drawable.plan,
                text = "??????????\n????????????????????????") { navController.navigate(Screen.PlanningCenterScreen.route)}
            Spacer(modifier = Modifier.padding(vertical = 5.dp))
            LobbyButton(
                drawableRes = R.drawable.diagn,
                text = "??????????????\n??????????????????????") { navController.navigate(Screen.DiagnosticsScreen.route)}
            Spacer(modifier = Modifier.padding(vertical = 5.dp))
            LobbyButton(
                drawableRes = R.drawable.snab,
                text = "??????????????\n??????????????????") {navController.navigate(Screen.SupplyScreen.route)}
            Spacer(modifier = Modifier.padding(vertical = 5.dp))
            if (heroInfo != null) {
                if (heroInfo.has_expeditions > 0) {
                    ButtonGo(text = "?????? ???????????????? ????????????????????") {navController.navigate(Screen.PlanningCenterScreen.route)}
                    Spacer(modifier = Modifier.padding(vertical = 5.dp))
                }
            }

            Row {
                Box (modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .padding(end = 5.dp)
                    .background(Color(0x802B2B2B))
                    .clip(RoundedCornerShape(5.dp))
                    .clickable {
                        navController.navigate("${Screen.IntroScreen.route}/false") {

                        }
                    },
                    contentAlignment = Alignment.Center) {
                    Row {
                        Image(
                            painterResource(id = R.drawable.globus_v),
                            contentDescription = "globus",
                            contentScale = ContentScale.FillHeight)
                        Text(
                            text = "?????????????? ????????",
                            style = MaterialTheme.typography.button,
                            color = Color.White,
                            modifier = Modifier.padding(start = 5.dp)
                        )
                    }
                }
                Box (modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .padding(end = 5.dp)
                    .background(Color(0x802B2B2B))
                    .clip(RoundedCornerShape(5.dp))
                    .clickable {
                        exitAccount()
                    },
                    contentAlignment = Alignment.Center) {
                    Row {
                        Text(
                            text = "?????????? ???? ????????????????",
                            style = MaterialTheme.typography.button,
                            color = Color.White,
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun UserInfoRow(username : String?, heroClass: String?) {
    val fontUsername = TextStyle(
        color = Color.White,
        fontFamily = FontFamily(Font(R.font.micra_bold)),
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    )
    val fontHeroClass = TextStyle(
        color = Color.White,
        fontFamily = FontFamily(Font(R.font.micra)),
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    )
    Row {
        Text(
            text = username ?: "??????????????",
            style = fontUsername,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = characterTypes.find { it.id_name == heroClass }?.name ?: "",
            style = fontHeroClass,
        )
    }
}

@Composable
fun LobbyButton(drawableRes : Int, text : String = "", onClick : () -> Unit) {
    val fontText = TextStyle(
        fontFamily = FontFamily(Font(R.font.micra_bold)),
        fontWeight = FontWeight.Bold,
        lineHeight = 19.sp,
        fontSize = 13.sp
    )
    Box (modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(15.dp))
        .clickable { onClick() }
    ) {
        Image(
            painterResource(drawableRes),
            contentDescription = "Lobby button",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
        )
        Text(
            text = text,
            style = fontText,
            color = Color.White,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.8f)
                .padding(start = 20.dp)
                .align(Alignment.CenterStart)
        )
    }
}

@Composable
fun ButtonGo(
    text : String = "",
    isActive: Boolean = true,
    modifier: Modifier = Modifier,
    onClick : () -> Unit) {
    val fontText = TextStyle(
        fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
        fontWeight = FontWeight.Bold,
        lineHeight = 16.sp,
        fontSize = 15.sp
    )
    Box (modifier = modifier
        .fillMaxWidth()
        .clickable { if (isActive) onClick() }
    ) {
        Image(
            painterResource(R.drawable.button_go),
            contentDescription = "Go button",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
        )
        Text(
            text = text,
            style = fontText,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.8f)
                .padding(bottom = 5.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
fun ButtonBack(text : String = "", isActive: Boolean, modifier: Modifier = Modifier, onClick : () -> Unit) {
    val fontText = TextStyle(
        fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
        fontWeight = FontWeight.Bold,
        lineHeight = 16.sp,
        fontSize = 15.sp
    )
    Box (modifier = modifier
        .fillMaxWidth()
        .clickable { if (isActive) onClick() }
    ) {
        Image(
            painterResource(R.drawable.button_back),
            contentDescription = "Go button",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
        )
        Text(
            text = text,
            style = fontText,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.8f)
                .padding(top = 5.dp)
                .align(Alignment.Center)
        )
    }
}