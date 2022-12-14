package com.goodgame.goodgameapp.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.goodgame.goodgameapp.R
import com.goodgame.goodgameapp.models.Expedition
import com.goodgame.goodgameapp.models.HeroInfo
import com.goodgame.goodgameapp.retrofit.Status
import com.goodgame.goodgameapp.viewmodel.GameViewModel
import java.io.File
import coil.compose.rememberImagePainter
import coil.size.OriginalSize
import com.goodgame.goodgameapp.models.ExpeditionResult
import com.goodgame.goodgameapp.navigation.Screen
import com.goodgame.goodgameapp.navigation.clearBackStack
import com.goodgame.goodgameapp.retrofit.Response
import com.goodgame.goodgameapp.screens.views.ErrorAlert
import com.goodgame.goodgameapp.screens.views.FadeTransition
import com.goodgame.goodgameapp.screens.views.FadeTransitionFloat
import com.goodgame.goodgameapp.screens.views.MetallButton
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.exp

private enum class ExpeditionScreenState {
    LOADING_EXPEDITION,
    LOADING_IMAGE,
    ACTION,
    RESULT_LOADING,
    HERO_INFO_UPDATE,
    RESULT,
    ERROR,
}

private val MASS_TEST = false
private val MASS_TEST_DESCRIPTION =
        "?????????????? ?????? ???????????????????? ?? ???????? ??????????. ?????????? ?????????????????? ??????????" +
        " ?????????? ?????????? ???????????????? ?????????????????????? Godji. ?? ???????? ???????????? ?????? ????????????????, ?????????? ?????? ???????? ??????????????????" +
        "????????????. Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque " +
        "laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto " +
        "beatae vitae dicta sunt explicabo."

private val MASS_TEST_RESULT =
    "?? ?????????? ?????????? ?????????????????? ???????????????????? \n" +
    "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque " +
    "laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto " +
    "beatae vitae dicta sunt explicabo."

@Composable
fun ExpeditionScreen(navController: NavController, viewModel: GameViewModel) {
    val expeditionState = remember { mutableStateOf(ExpeditionScreenState.LOADING_EXPEDITION)}
    val errorMessage = remember { mutableStateOf("")}
    val loadingProgress = remember { mutableStateOf(0)}
    val expedition = remember { mutableStateOf<Expedition?> (null)}
    val expeditionResultBuf = remember { mutableStateOf<ExpeditionResult?> (null)}
    val expeditionResult = remember { mutableStateOf<ExpeditionResult?> (null)}
    val userAction = remember { mutableStateOf("")}

    val heroInfo by viewModel.heroInfo.observeAsState()
    if (heroInfo?.stats == null) {
        navController.navigate(Screen.SplashScreen.route) {
            navController.backQueue.clear()
        }
    }

    FadeTransition(state = expeditionState, visibleStates =
        listOf(
            ExpeditionScreenState.LOADING_EXPEDITION,
            ExpeditionScreenState.LOADING_IMAGE)) {
        LoadingExpedition()
    }

    FadeTransition(state = expeditionState, visibleStates =
        listOf(
            ExpeditionScreenState.ACTION,
            ExpeditionScreenState.RESULT_LOADING,
            ExpeditionScreenState.HERO_INFO_UPDATE,
            ExpeditionScreenState.RESULT
        )) {

        if (expedition.value == null) {
            expeditionState.value = ExpeditionScreenState.ERROR
            errorMessage.value = "expeditionModel = null"
        } else {
            ShowExpedition(
                expedition = expedition.value!!,
                expeditionResult = expeditionResult.value,
                heroInfo = heroInfo,
                onAction = {action ->
                    userAction.value = action
                    expeditionState.value = ExpeditionScreenState.RESULT_LOADING
                },
                onClose = {navController.navigate(Screen.MainScreen.route) { clearBackStack(navController, this)} })
        }
    }

    FadeTransition(state = expeditionState, visibleState = ExpeditionScreenState.ERROR)
    {
        Box(Modifier.fillMaxSize()) {
            Image(
                painterResource(R.drawable.loading_expedition_bg),
                contentDescription = "first_screen",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (errorMessage.value.substring(0,11) != "??????????????????????") {
            ErrorAlert(
                errorMessage = errorMessage.value,
                isRefreshActive = true,
                cancel = { navController.navigateUp() },
                refresh = {
                    expeditionState.value = ExpeditionScreenState.LOADING_EXPEDITION
                })
        } else {
            ErrorAlert(
                headText = "??????!",
                errorMessage = errorMessage.value,
                cancelText = "????????????",
                isRefreshExists = false,
                isRefreshActive = false,
                cancel = { navController.navigateUp() },
                )
        }

    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(expeditionState.value) {
        when (expeditionState.value) {
            ExpeditionScreenState.LOADING_EXPEDITION -> {
                viewModel.getExpedition().observe(lifecycleOwner) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            if (it.data?.status == true) {
                                expedition.value = it.data
                                expeditionState.value = ExpeditionScreenState.LOADING_IMAGE
                            } else {
                                expeditionState.value = ExpeditionScreenState.ERROR
                                Log.d("expedition", it.data?.info ?: "info null")
                                errorMessage.value = it.data?.info
                                    ?: "Error no msg getExpedition, data.status = false"
                            }
                        }
                        Status.ERROR -> {
                            expeditionState.value = ExpeditionScreenState.ERROR
                            errorMessage.value =
                                it.message ?: "Error no msg getExpedition, status = error"
                        }
                        Status.LOADING -> {}
                    }
                }
            }
            ExpeditionScreenState.LOADING_IMAGE -> {
                val loadingLiveData = MutableLiveData<Response<Int>>(Response.loading(data = 0))

                if (expedition.value?.image != null)
                    viewModel.getImage(expedition.value!!.image!!, loadingLiveData)
                else
                    expeditionState.value = ExpeditionScreenState.ACTION


                loadingLiveData.observe(lifecycleOwner) {
                    when (it.status) {
                        Status.LOADING -> {
                            loadingProgress.value = it.data ?: 0
                        }
                        Status.SUCCESS -> {
                            coroutineScope.launch {
                                delay(500)
                                loadingProgress.value = 100
                                expeditionState.value = ExpeditionScreenState.ACTION
                            }
                        }
                        Status.ERROR -> {
                            expeditionState.value = ExpeditionScreenState.ERROR
                            errorMessage.value = it.message ?: "Error loading image, no msg"
                        }
                    }
                }
            }
            ExpeditionScreenState.RESULT_LOADING -> {
                viewModel.getExpeditionResult(userAction.value)
                    .observe(lifecycleOwner) {
                        when (it.status) {
                            Status.LOADING -> {

                            }
                            Status.SUCCESS -> {
                                expeditionResultBuf.value = it.data
                                Log.d("expedition", "Result: ${expeditionResultBuf.value}")
                                expeditionState.value = ExpeditionScreenState.HERO_INFO_UPDATE
                            }
                            Status.ERROR -> {
                                expeditionState.value = ExpeditionScreenState.ERROR
                                errorMessage.value = it.message ?: "Error loading result, no msg"
                            }
                        }
                    }
            }
            ExpeditionScreenState.HERO_INFO_UPDATE -> {
                viewModel.getHeroInfo(initial = false).observe(lifecycleOwner) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            expeditionResult.value = expeditionResultBuf.value
                            expeditionState.value = ExpeditionScreenState.RESULT
                        }
                        Status.ERROR -> {
                            expeditionState.value = ExpeditionScreenState.ERROR
                            errorMessage.value = it.message ?: "Error loading heroInfo, no msg"
                        }
                        Status.LOADING -> {

                        }
                    }
                }
            }
            ExpeditionScreenState.RESULT -> {

            }
            else -> {}
        }
    }
}

@Composable
private fun LoadingExpedition() {
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
            .clickable { }) {
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
private fun ShowExpedition(
    expedition: Expedition,
    expeditionResult: ExpeditionResult?,
    heroInfo: HeroInfo?,
    onAction: (action: String) -> Unit,
    onClose: () -> Unit) {
    val scrollState = rememberLazyListState()

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
    val context = LocalContext.current
    val path = remember {
        mutableStateOf(
            getPathFromUrl(expedition.image, context)
        )
    }
    val painter = rememberImagePainter(
        data = File(path.value),
        builder = {
            size(OriginalSize)
        },
    )
    val bufOpeningAnimFloat = remember { mutableStateOf(0f)}
    val openingAnimFloat = animateFloatAsState(
        targetValue = bufOpeningAnimFloat.value,
        animationSpec = tween(
            durationMillis = 1800,
            easing = LinearEasing,
        )
    )
    LaunchedEffect(Unit) {
        bufOpeningAnimFloat.value = 300f
    }

    val loadingState = remember { mutableStateOf("")}

    val buttonTransition = rememberInfiniteTransition() // animate infinite times
    val buttonAlphaAnimation = buttonTransition.animateFloat( //animate the transition
        initialValue = 1f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 700, // duration for the animation
                easing = FastOutLinearInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        )

    LaunchedEffect(expeditionResult != null) {
        if (loadingState.value == "") {
            scrollState.animateScrollToItem(2)
        }
    }


    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF010101)),
        state = scrollState) {
        item()
        {
            Box() {
                Image(
                    painterResource(R.drawable.expedition_head),
                    contentDescription = "first_screen",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .padding(start = 15.dp, bottom = 25.dp)
                        .align(Alignment.BottomStart)
                )
                {
                    FadeTransitionFloat(state = openingAnimFloat.value, visibleState = 40f) {
                        Text(
                            text = "???????????????????? ",
                            style = headStyle,
                            color = Color.White,
                            modifier = Modifier
                        )
                    }
                }

            }
        }
        item(){
            FadeTransitionFloat(state = openingAnimFloat.value, visibleState = 130f) {
                Text(
                    text = if (MASS_TEST) MASS_TEST_DESCRIPTION else expedition.description,
                    style = MaterialTheme.typography.subtitle2,
                    lineHeight = 19.sp,
                    color = Color(0xFFD1D1D1),
                    modifier = Modifier.padding(horizontal = 15.dp)
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            if (path.value != "") {
                FadeTransitionFloat(state = openingAnimFloat.value, visibleState = 210f) {
                    Image(
                        painter,
                        contentDescription = "monster_image",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 360.dp)
                    )
                }
            }
            FadeTransitionFloat(state = openingAnimFloat.value, visibleState = 250f) {
                when (expedition.difficulty) {
                    "??????????????" -> Image(
                        painterResource(R.drawable.danger_1),
                        contentDescription = "first_screen",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                    "??????????????" -> Image(
                        painterResource(R.drawable.danger_2),
                        contentDescription = "first_screen",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                    "????????????" -> Image(
                        painterResource(R.drawable.danger_3),
                        contentDescription = "first_screen",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                    else -> Image(
                        painterResource(R.drawable.danger_1),
                        contentDescription = "first_screen",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            FadeTransitionFloat(state = openingAnimFloat.value, visibleState = 250f) {
                Box() {
                    val columnSize = remember { mutableStateOf(Size.Zero) }
                    val imageHeight = with(LocalDensity.current) {
                        if (columnSize.value.width == 0f)
                            360.dp
                        else
                            columnSize.value.width.toInt().toDp()
                    }
                    Image(
                        painterResource(R.drawable.expedition_bottom),
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(imageHeight)
                    )
                    Column(Modifier.onGloballyPositioned { coords ->
                        columnSize.value = coords.size.toSize()
                    }) {
                        Spacer(modifier = Modifier.height(25.dp))
                        MyParameters(heroInfo)
                        Spacer(modifier = Modifier.height(25.dp))

                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically)
                            {
                                Text(
                                    text = "????????????",
                                    style = resultStyle,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 15.dp)
                                )
                                Text(
                                    text = "+" + expedition.win_rsp.toString(),
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
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically)
                            {
                                Text(
                                    text = "??????????????????",
                                    style = resultStyle,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 15.dp)
                                )
                                Text(
                                    text = "-" + expedition.lose_rsp.toString(),
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
                            ButtonGo(
                                text = "????, ??????????????????????",
                                isActive = loadingState.value != "Run",
                                modifier = Modifier.alpha(
                                    when (loadingState.value) {
                                        "Do" -> buttonAlphaAnimation.value
                                        "Run" -> 0.4f
                                        else -> 1f
                                    }
                                )
                            ) {
                                if (expeditionResult == null) {
                                    loadingState.value = "Do"
                                    onAction("Do")
                                }
                            }
                            Spacer(modifier = Modifier.height(15.dp))
                            ButtonBack(
                                text = "??????, ???????? ????????????????????????\n????????????????????",
                                isActive = loadingState.value != "Do",
                                modifier = Modifier.alpha(
                                    when (loadingState.value) {
                                        "Run" -> buttonAlphaAnimation.value
                                        "Do" -> 0.4f
                                        else -> 1f
                                    }
                                )
                            ) {
                                if (expeditionResult == null) {
                                    loadingState.value = "Run"
                                    onAction("Run")
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
        item()
        {
            if (expeditionResult != null) {
                val context = LocalContext.current
                val path = getPathFromUrl(expedition.image, context)
                if (path != "") {
                    Box() {
                        Image(
                            painter,
                            contentDescription = "monster_image",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 360.dp)
                        )
                        if (expeditionResult.result == "win") {
                            Image(
                                painterResource(R.drawable.expedition_success),
                                contentDescription = null,
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                            )
                        }
                        if (expeditionResult.result == "lose") {
                            Image(
                                painterResource(R.drawable.expedition_failure),
                                contentDescription = null,
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                            )
                        }
                        if (expeditionResult.result == "run") {
                            Image(
                                painterResource(R.drawable.expedition_cancel),
                                contentDescription = null,
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                            )
                        }
                    }
                }
                else {
                    if (expeditionResult.result == "win") {
                        Image(
                            painterResource(R.drawable.expedition_success),
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                    if (expeditionResult.result == "lose") {
                        Image(
                            painterResource(R.drawable.expedition_failure),
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                    if (expeditionResult.result == "run") {
                        Image(
                            painterResource(R.drawable.expedition_cancel),
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = if (MASS_TEST) MASS_TEST_RESULT else expeditionResult.description,
                    style = MaterialTheme.typography.subtitle2,
                    lineHeight = 19.sp,
                    color = Color(0xFFD1D1D1),
                    modifier = Modifier.padding(horizontal = 15.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                val micra_bold = TextStyle(
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.micra_bold)),
                    fontWeight = FontWeight.Bold,
                    lineHeight = 19.sp,
                    fontSize = 15.sp
                )
                Text(
                    text = "???? ?????????????? ${expeditionResult.exp} ?????????? ?????????? ?? ${expeditionResult.rsp} ?????????? ????????????????????????",
                    style = micra_bold,
                    modifier = Modifier.padding(horizontal = 15.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "?????????????? ?????????????????????? ???? ${expeditionResult.planet_status}%",
                    style = MaterialTheme.typography.subtitle2,
                    lineHeight = 19.sp,
                    color = Color(0xFFD1D1D1),
                    modifier = Modifier.padding(horizontal = 15.dp)
                )
                Spacer(modifier = Modifier.height(15.dp))
                MetallButton(
                    isActive = true,
                    activeText = "?????????????????? ???? ????????"
                ) {
                    onClose()
                }
                Spacer(modifier = Modifier.height(10.dp))
                loadingState.value = ""
            }
        }
    }

}

fun getPathFromUrl(url: String?, context: Context): String {
    if (url == null) return ""
    val uri = Uri.parse(url).path ?: return ""
    val fileName = File(uri).name
    return context.cacheDir.path + "/expeditionImages/$fileName"
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
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        15.dp,
                        15.dp,
                        if (state) 0.dp else 15.dp,
                        if (state) 0.dp else 15.dp,
                    )
                )
                .background(Color(0x802B2B2B))
                .clickable { state = !state }) {
            Row (verticalAlignment = Alignment.CenterVertically){
                Text (
                    text = "?????? ?????????????? ????????????????????????????",
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
                    .clip(
                        RoundedCornerShape(
                            0.dp,
                            0.dp,
                            15.dp,
                            15.dp
                        )
                    )
                    .background(Color(0x802B2B2B)))
            {
                Column(Modifier.padding(horizontal = 10.dp)) {
                    UserParametersScale(
                        text = "????????",
                        parameter = heroInfo?.stats?.power ?: 0,
                        isButtonActive = false,
                        background = R.drawable.pl_red
                    ) {}
                    UserParametersScale(
                        text = "??????????????????",
                        parameter = heroInfo?.stats?.intellect ?: 0,
                        isButtonActive = false,
                        background = R.drawable.pl_blue
                    ) {}
                    UserParametersScale(
                        text = "??????????????",
                        parameter = heroInfo?.stats?.charisma ?: 0,
                        isButtonActive = false,
                        background = R.drawable.pl_gold
                    ) {}
                    UserParametersScale(
                        text = "??????????",
                        parameter = heroInfo?.stats?.fortune ?: 0,
                        isButtonActive = false,
                        background = R.drawable.pl_green
                    ) {}
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
        }

    }

}