package com.goodgame.goodgameapp.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.*
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
import com.goodgame.goodgameapp.models.StatsModel
import com.goodgame.goodgameapp.navigation.Screen
import com.goodgame.goodgameapp.screens.views.LevelsView
import com.goodgame.goodgameapp.screens.views.SkillApplyView
import com.goodgame.goodgameapp.viewmodel.GameViewModel

@Composable
fun DiagnosticsScreen(navController: NavController, viewModel: GameViewModel) {
    val heroInfo by viewModel.heroInfo.observeAsState()
    val isHeroLoaded by viewModel.isHeroInfoLoaded.observeAsState()
    val isLvlsListActive = remember {mutableStateOf(false)}

    val tempUserStats = remember { mutableStateOf<StatsModel>(heroInfo?.stats?.copy() ?: StatsModel(0,0,0,0))}
    val tempUserStatsPoints = remember { mutableStateOf(heroInfo?.stats_points ?: 0)}

    val isSkillApplyViewActive = remember { mutableStateOf(false)}
    val skillTypeApply = remember { mutableStateOf("")}
    
    val scrollState = rememberScrollState()
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black))
    Column(modifier = Modifier
        .verticalScroll(scrollState)
        .fillMaxHeight()
        ) {
        Row { // Head row
            HeadDiagnostic(tempUserStatsPoints.value)
        }
        Row (Modifier.fillMaxSize()) { // Action row
            ActionRowDiagnostic(
                userStats = tempUserStats,
                userStatsPoints = tempUserStatsPoints,
                heroInfo = heroInfo,
                username = viewModel.username,
                openLevels = {isLvlsListActive.value = true},
                openRewards = {navController.navigate("${Screen.SupplyScreen.route}/1")},
                skillApply = {skillType -> isSkillApplyViewActive.value = true; skillTypeApply.value = skillType},)
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
                modifier = Modifier.padding(start = 1.dp, end = 3.dp))
        }
    }
    if (isLvlsListActive.value) {
        LevelsView {
            isLvlsListActive.value = false
        }
    }
    if (isSkillApplyViewActive.value) {
        SkillApplyView(
            skill_type = skillTypeApply.value,
            skillApply = viewModel.setHeroSkill(skillTypeApply.value),
            onDone = {newStats ->
                isSkillApplyViewActive.value = false
                if (newStats != null) {
                    heroInfo?.stats = newStats
                    tempUserStats.value = newStats
                    tempUserStatsPoints.value = tempUserStatsPoints.value - 1
                }
            }
        )
    }
}

@Composable
private fun HeadDiagnostic(tempUserStatsPoints: Int) {
    val headTextStyle = TextStyle(
        color = Color.White,
        fontFamily = FontFamily(Font(R.font.micra_bold)),
        fontWeight = FontWeight.Bold,
        lineHeight = 24.sp,
        fontSize = 18.sp
    )
    Box (Modifier.fillMaxWidth()) {
        Image(
            painterResource(R.drawable.diagn_head_bg),
            contentDescription = "head_image",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
        Column(modifier = Modifier.matchParentSize()) {
            Row(modifier = Modifier.weight(0.20f)) {}
            Row(modifier = Modifier.weight(0.30f)) {
                Text(
                    text = "СИСТЕМА\nДИАГНОСТИКИ",
                    style = headTextStyle,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.8f)
                        .padding(start = 20.dp)
                )
            }
            Row(modifier = Modifier
                .weight(0.32f)
                .padding(horizontal = 15.dp, vertical = 15.dp), verticalAlignment = Alignment.Bottom) {
                HasExpPoints(tempUserStatsPoints)
            }
            Row(modifier = Modifier.weight(0.18f)) {
                Text(
                    text = "Тут находится все показатели твоего персонажа и тут ты можешь распределять очки опыта",
                    style = MaterialTheme.typography.subtitle1,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.9f)
                        .padding(start = 20.dp)
                )
            }
        }
    }
}

@Composable
private fun HasExpPoints(points: Int) {
    Box(modifier = Modifier
        .clip(RoundedCornerShape(15.dp))
        .fillMaxWidth()
        .background(Color.Transparent)
        .border(
            1.dp,
            Color(0xFF0077FF),
            RoundedCornerShape(15.dp),
        )
    ) {
        Box (
            Modifier
                .background(Color(0xFF0077FF))
                .padding(start = 15.dp)) {
            Row (modifier = Modifier.height(35.dp)) {
                Text(text = "нераспределённые очки исследования",
                    style = MaterialTheme.typography.button,
                    color = Color(0xFFFFFFFF),
                    fontSize = 12.sp,
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
                    Text(text = "$points",
                        style = MaterialTheme.typography.button,
                        color = Color(0xFF000000),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(0.8f))
                }
            }
        }
    }
}

@Composable
private fun UserParametersScale(
    text: String,
    parameter: Int,
    @DrawableRes background: Int,
    isButtonActive: Boolean,
    onClickPlus : () -> Unit)  {

    Row() {
        Box (
            Modifier
                .weight(0.1f)
                .padding(end = 5.dp)) {
            CharacterParameterScale(text = text, parameter = parameter, background)
        }
        Box(
            Modifier
                .height(45.dp)
                .clickable { onClickPlus() }) {
            Image(
                painterResource(R.drawable.plus),
                contentDescription = "plus",
                modifier = Modifier
                    .align(Alignment.Center)
                    .height(37.dp)
                    .width(37.dp),
                contentScale = ContentScale.FillBounds,
                alpha = if (isButtonActive) 1f else 0.5f
            )
        }
    }
}

@Composable
private fun ActionRowDiagnostic(
    userStats: MutableState<StatsModel>,
    userStatsPoints: MutableState<Int>,
    heroInfo: HeroInfo?,
    username: String?,
    skillApply: (skill_type: String) -> Unit,
    openRewards: () -> Unit,
    openLevels: () -> Unit) {
    
    Box {
        Image(
            painterResource(R.drawable.diag_bottom_bg),
            contentDescription = "scroll_bg_image",
            modifier = Modifier
                .fillMaxSize()
                .scale(scaleY = 1.10f, scaleX = 1f),
            contentScale = ContentScale.FillWidth,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(15.dp))
            UserInfoRow(username, heroInfo?.heroClass)
            Spacer(modifier = Modifier.height(15.dp))
            Row (Modifier.height(30.dp)) {
                ExperienceGraphics(heroInfo = heroInfo)
            }
            Spacer(modifier = Modifier.height(20.dp))
            UserScores("ОЧКИ ИССЛЕДОВАНИЯ", heroInfo?.coins ?: 0)
            Spacer(modifier = Modifier.height(5.dp))
            UserScores("ОЧКИ ОПЫТА", heroInfo?.lvl_exp ?: 0)
            Spacer(modifier = Modifier.height(10.dp))
            UserParametersScale(
                text = "СИЛА",
                parameter = userStats.value.power,
                isButtonActive = userStatsPoints.value > 0,
                background = R.drawable.pl_red
            ) {
                skillApply("power")
            }
            UserParametersScale(
                text = "ИНТЕЛЛЕКТ",
                parameter = userStats.value.intellect,
                isButtonActive = userStatsPoints.value > 0,
                background = R.drawable.pl_blue
            ) {
                skillApply("intellect")
            }
            UserParametersScale(
                text = "ХАРИЗМА",
                parameter = userStats.value.charisma,
                isButtonActive = userStatsPoints.value > 0,
                background = R.drawable.pl_gold
            ) {
                skillApply("charisma")
            }
            UserParametersScale(
                text = "УДАЧА",
                parameter = userStats.value.fortune,
                isButtonActive = userStatsPoints.value > 0,
                background = R.drawable.pl_green
            ) {
                skillApply("fortune")
            }
            Spacer(modifier = Modifier.height(5.dp))
            BottomButtons(
                onClickAwards = {openRewards()},
                onClickLevels = {openLevels()},
            )
        }
    }
}

@Composable
private fun UserScores(text: String, points: Int) {
    val textStyle = TextStyle(
        color = Color.White,
        fontFamily = FontFamily(Font(R.font.micra_bold)),
        fontWeight = FontWeight.Bold,
        lineHeight = 17.sp,
        fontSize = 12.sp
    )
    Row() {
        Text(
            text = text,
            style = textStyle
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = points.toString(),
            style = textStyle,
            color = Color(0x80FFFFFF)
        )
        Spacer(modifier = Modifier.padding(end = 5.dp))
        Image(
            painterResource(R.drawable.coin),
            contentDescription = "coin",
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Composable
private fun CharacterParameterScale(text: String, parameter: Int, @DrawableRes background: Int) {
     val font = TextStyle(
        fontFamily = FontFamily(Font(R.font.micra_bold)),
        fontWeight = FontWeight(400),
        fontSize = 12.sp
    )

    Box(modifier = Modifier
        .height(45.dp)
        .fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Image(
            painter = painterResource(background),
            contentDescription = "scale_bg",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .height(37.dp)
                .clip(shape = RoundedCornerShape(12.dp)))

        Text(
            text = text,
            color = Color.White,
            style = font,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 10.dp)
        )
        Text(
            text = parameter.toString(),
            color = Color.White,
            style = MaterialTheme.typography.h4,
            fontSize = 30.sp,
            modifier = Modifier
                .padding(end = 10.dp)
                .align(Alignment.CenterEnd)
        )

    }
}
@Composable
private fun BottomButtons(onClickAwards: () -> Unit, onClickLevels: () -> Unit) {
    Row () {
        Box(
            modifier = Modifier
                .weight(0.65f)
                .height(50.dp)
                .padding(end = 5.dp)
                .background(Color(0x802B2B2B))
                .clip(RoundedCornerShape(5.dp))
                .clickable { onClickAwards() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Мои награды",
                style = MaterialTheme.typography.button,
                color = Color.White,
                modifier = Modifier.padding(start = 5.dp)
            )
        }
        Box(
            modifier = Modifier
                .weight(0.45f)
                .height(50.dp)
                .background(Color(0x802B2B2B))
                .clip(RoundedCornerShape(5.dp))
                .clickable { onClickLevels() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Уровни",
                style = MaterialTheme.typography.button,
                color = Color.White,
                modifier = Modifier.clickable {
                    onClickLevels()
                }
            )
        }
    }
}
