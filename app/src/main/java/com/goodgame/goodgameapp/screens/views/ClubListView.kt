package com.goodgame.goodgameapp.screens.views

import android.graphics.Matrix
import android.graphics.RectF
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.PathParser
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.goodgame.goodgameapp.R
import com.goodgame.goodgameapp.models.ClubModel
import com.goodgame.goodgameapp.retrofit.Status
import com.goodgame.goodgameapp.viewmodel.LoginViewModel

@Composable
fun ClubListView(viewModel: LoginViewModel, closeEvent: (currentClub: MutableState<ClubModel?>) -> Unit) {
    // TODO("Стремный цвет при клике на город")
    fun MutableState<Boolean>.trigger() { value = !value }
    val lifecycleOwner = LocalLifecycleOwner.current

    val clubsStatus = remember {mutableStateOf(Status.LOADING)}
    val clubsStatusMessage = remember {mutableStateOf("")}

    val allClubs = remember {mutableListOf<ClubModel>()}
    val cities = remember { mutableListOf<String>() }
    val cityClubs = remember { mutableStateListOf<ClubModel>() }
    val currentCity = remember { mutableStateOf("") }
    val currentClub = remember { mutableStateOf<ClubModel?>(null) }

    if (currentCity.value != "") {
        cityClubs.clear()
        cityClubs.addAll(allClubs.filter { it.club_city == currentCity.value })
    }

    val refreshKey = remember { mutableStateOf(true)}

    LaunchedEffect(refreshKey.value) {
        viewModel.getClubs().observe(lifecycleOwner, Observer { clubsResponse ->
            clubsStatus.value = clubsResponse.status
            when (clubsResponse.status) {
                Status.SUCCESS -> {
                    allClubs.clear()
                    cities.clear()
                    cityClubs.clear()
                    if (clubsResponse.data != null) {
                        allClubs.addAll(clubsResponse.data)
                        cities.addAll(allClubs.map { it.club_city }.toSet())
                        currentCity.value = cities[0]
                        cityClubs.addAll(allClubs.filter { it.club_city == currentCity.value})
                    }
                }
                Status.ERROR   -> { clubsStatusMessage.value = clubsResponse.message ?: "" }
                else -> {}
            }
        })
    }

    BackHandler() {
        closeEvent(currentClub)
    }
    val interactionSource = remember { MutableInteractionSource() }
    Column (modifier = Modifier.padding(14.dp)
        .clickable(
        interactionSource = interactionSource,
        indication = null
    ) {
        /* .... */
    }) {
        Row(modifier = Modifier.weight(1f).background(Color.Transparent)) // Прозрачное поле с крестиком
        {
            Spacer(modifier = Modifier.weight(1f))
            Box (modifier = Modifier.padding(top = 28.dp, end = 10.dp)) {
                CloseButton {
                    closeEvent(currentClub)
                }
            }

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
                    .background(Color(0xFF00131D))
            ) {
                when(clubsStatus.value) {
                    Status.LOADING -> {
                        Column(modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                        ) {
                            TopText {closeEvent(currentClub)}
                            LoadingView()
                        }
                    }
                    Status.SUCCESS -> {
                        Column(modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                        ) {
                            TopText {}
                            CityHorizontalMenu(cities, currentCity) {
                                cityClubs.clear()
                                cityClubs.addAll(allClubs.filter { it.club_city == currentCity.value})
                            }
                            Column() {
                                Row(modifier = Modifier.weight(1f).padding(top = 5.dp)) {
                                    ClubList(cityClubs, currentClub)
                                }
                                Row(modifier = Modifier.padding(top = 20.dp, bottom = 25.dp)) {
                                    ApplyButton { closeEvent(currentClub) }
                                }
                            }
                        }
                    }
                    Status.ERROR -> {
                        ErrorAlert(
                            errorMessage = "Нет подключения к интернету",
                            refresh = {refreshKey.trigger(); clubsStatus.value = Status.LOADING},
                            cancel = {closeEvent(mutableStateOf(null))})
                    }
                }



            }
        }
        Row(modifier = Modifier.weight(1f).background(Color.Transparent)) {}
    }
}

@Composable
fun TopText(closeEvent: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Выбери свой клуб",
            color = Color.White,
            style = MaterialTheme.typography.body1,
        )

        Spacer(Modifier.weight(1f))
        IconButton(onClick = { closeEvent() }) {
            Icon(
                painterResource(R.drawable.ic_baseline_expand_less_24),
                contentDescription = "Закрыть список клубов",
                tint = Color(red = 1f, green = 1f, blue = 1f, alpha = 0.5f)
            )
        }
    }
}

@Composable
fun CityHorizontalMenu(cities: MutableList<String>, currentCity: MutableState<String>, onClick: () -> Unit) {

    val scrollState = rememberLazyListState()
    val cityActive = Color(0xFF0085FF)
    val cityNotActive = Color(0xFFFFFFFF)

    LazyRow (
        state = scrollState,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(34.dp))
    {
        items(items = cities) { city ->
            Box(modifier = Modifier.clickable {
                currentCity.value = city
                onClick()
            }) {
                val itemColor = remember { mutableStateOf(cityNotActive) }
                if (currentCity.value == city)
                    itemColor.value = cityActive
                else
                    itemColor.value = cityNotActive
                Text(
                    text = city,
                    style = MaterialTheme.typography.button,
                    color = itemColor.value,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp))
            }

        }
    }
}

@Composable
fun ClubList(clubs: MutableList<ClubModel>, currentClub: MutableState<ClubModel?>) {
    val scrollState = rememberLazyListState()

    LazyColumn(
        state = scrollState) {
        items(items = clubs) { club ->
            val itemActive = remember { mutableStateOf(false) }
            itemActive.value = currentClub.value == club
            ClubCard(club = club, isActive = itemActive) {
                currentClub.value = club
            }
            Spacer(modifier = Modifier.padding(7.dp))
        }
    }
}

@Composable
fun ClubCard(club: ClubModel, isActive: MutableState<Boolean>, onClick: () -> Unit) {
    val backgroundNotActive = Color(0x1AFFFFFF)
    val backgroundActive = Color(0xFF1180FF)
    var backgroundColor = remember {mutableStateOf(backgroundNotActive)}
    val clubName = club.text_name
        .replaceFirst("GoodGame ", "")
        .replaceFirst("Goodgame ", "")
        .replaceFirst("GodjiGame ","")

    if (isActive.value == true)
        backgroundColor.value = backgroundActive
    else
        backgroundColor.value = backgroundNotActive

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 5.dp,
                    topEnd = 5.dp,
                    bottomStart = 5.dp,
                    bottomEnd = 5.dp
                )
            )
            .background(backgroundColor.value)
            .clickable {
                onClick()
            }
        ) {
        Spacer(modifier = Modifier.padding(5.dp))
        Box(
            Modifier
                .width(20.dp)
                .height(20.dp)) {
            Image(
                painterResource(id = R.drawable.godji_logo),
                contentDescription = "godji_logo",
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.padding(10.dp))
        Text(
            text = clubName,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            color = Color(red = 1f, green = 1f, blue = 1f, alpha = 1f),
            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp
        )
    }
}

@Composable
fun ApplyButton(onClick: () -> Unit) {
    Button(
        onClick = {onClick()},
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            disabledBackgroundColor = Color.Transparent,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(63.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 15.dp,
                    topEnd = 15.dp,
                    bottomStart = 15.dp,
                    bottomEnd = 15.dp
                )
            )

            .border(
                1.dp,
                Color(
                    red = 0xAC,
                    green = 0xE9,
                    blue = 0xFA,
                    alpha = 0xFF
                ),
                RoundedCornerShape(
                    topStart = 15.dp,
                    topEnd = 15.dp,
                    bottomStart = 15.dp,
                    bottomEnd = 15.dp
                )
            )

    ) {
        Text(
            text = "Подтвердить выбор",
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            textDecoration = TextDecoration.None,
            letterSpacing = 0.sp,
            lineHeight = 19.sp,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .alpha(1f),
            color = Color(red = 1f, green = 1f, blue = 1f, alpha = 1f),
            style = MaterialTheme.typography.button
        )
    }
}

@Composable
fun CloseButton(onClick: () -> Unit) {

    Box(
        modifier = Modifier
            .width(42.dp)
            .height(42.dp)
            .clip(
                RoundedCornerShape(50)
            )
            .border(
                1.dp,
                Color(0xFFACE9FA),
                RoundedCornerShape(50)
            )
            .background(Color(0xFF0B0B0B))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {

        Icon(
            painterResource(id = R.drawable.ic_baseline_close_24),
            contentDescription = "Закрыть",
            tint = Color(0xFFACE9FA))
    }

}