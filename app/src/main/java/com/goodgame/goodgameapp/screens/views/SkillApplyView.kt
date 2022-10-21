package com.goodgame.goodgameapp.screens.views

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import com.goodgame.goodgameapp.R
import com.goodgame.goodgameapp.models.SkillResponse
import com.goodgame.goodgameapp.models.StatsModel
import com.goodgame.goodgameapp.navigation.Screen
import com.goodgame.goodgameapp.navigation.clearBackStack
import com.goodgame.goodgameapp.retrofit.Response
import com.goodgame.goodgameapp.retrofit.Status
import com.goodgame.goodgameapp.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun SkillApplyView(
    skillApply: LiveData<Response<SkillResponse>>,
    onDone: (StatsModel?) -> Unit) {
    val title = remember { mutableStateOf("Подтверждение")}
    val subTitle = remember { mutableStateOf("Потратить очко исследования?")}
    val applyButtonText = remember { mutableStateOf("Подтверждаю")}
    val isButtonsApplyActive = remember {mutableStateOf(true)}
    val isButtonsCancelActive = remember {mutableStateOf(true)}
    val newStats = remember { mutableStateOf<StatsModel?>(null)}
    val applyPressed = remember { mutableStateOf(false)}

    BackHandler() {
        if (isButtonsCancelActive.value)
            onDone(newStats.value)
    }
    val interactionSource = remember { MutableInteractionSource() }
    Box(Modifier.fillMaxSize().clickable(
        interactionSource = interactionSource,
        indication = null
    ) {
        /* .... */
    }, contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .clip(RoundedCornerShape(15.dp))
                .border(1.dp, Color(0xFFACE9FA), RoundedCornerShape(15.dp))
                .background(Color.White)) {
            Column(Modifier.padding(vertical = 20.dp, horizontal = 20.dp)) {
                Text(
                    text = title.value,
                    style = MaterialTheme.typography.h1,
                    color = Color(0xFF010101),
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    modifier = Modifier.height(30.dp),
                    text = subTitle.value,
                    style = MaterialTheme.typography.subtitle1,
                    color = Color(0xFF010101)
                )
                Spacer(modifier = Modifier.height(15.dp))
                Row () {
                    Box(Modifier.weight(0.35f)) {
                        CancelButton(isActive = isButtonsCancelActive, height = 55.dp) {
                            onDone(newStats.value)
                        }
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                    Box(Modifier.weight(0.65f)) {
                        ApplyButton(isActive = isButtonsApplyActive, height = 55.dp, activeText = applyButtonText.value) {
                            if (applyButtonText.value == "Далее")
                                onDone(newStats.value)
                            else
                                applyPressed.value = true
                        }
                    }
                }
            }
        }
    }

    if (applyPressed.value) {
        skillApply.observe(LocalLifecycleOwner.current) {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data?.status == true) {
                        title.value = "Готово!"
                        subTitle.value = "Вы потрясающий почти как Годжи"
                        isButtonsApplyActive.value = true
                        isButtonsCancelActive.value = true
                        applyButtonText.value = "Далее"
                        newStats.value = it.data.stats
                    } else {
                        title.value = "Ошибка"
                        subTitle.value = it.data?.info ?: "текст ошибки куда-то пропал("
                        isButtonsApplyActive.value = false
                        isButtonsCancelActive.value = true
                    }
                    applyPressed.value = false
                }
                Status.ERROR -> {
                    title.value = "Ошибка"
                    subTitle.value = it.message ?: "текст ошибки куда-то пропал("
                    isButtonsApplyActive.value = false
                    isButtonsCancelActive.value = true
                    applyPressed.value = false
                }
                Status.LOADING -> {
                    title.value = "Применяем нейро-имплант..."
                    isButtonsApplyActive.value = false
                    isButtonsCancelActive.value = false
                }
            }
        }
    }
    val ticks = remember { mutableStateOf(0) }
    LaunchedEffect(title.value) {
        when (title.value) {
            "Применяем нейро-имплант..." -> while(true) {
                when (ticks.value) {
                    0 -> subTitle.value = ""
                    1 -> subTitle.value = "Ищем имплант..."
                    2 -> subTitle.value = "Так, куда бы его воткнуть..."
                    3 -> subTitle.value = "Вот эта дырка вроде подходит..."
                    4 -> subTitle.value = "Пошли байты родимые..."
                    5 -> subTitle.value = "11101110 11110101 11110011 11100101 11101101 11101101 11101110"
                    6 -> subTitle.value = "Еще чуть-чуть..."
                    7 -> { ticks. value = 0; return@LaunchedEffect }
                }
                delay(2000)
                ticks.value++
            }
            "Готово!" -> while(true) {
                delay(2000)
                onDone(newStats.value)
            }
            else -> ticks.value = 0
        }
    }
}

@Composable
private fun CancelButton(isActive: MutableState<Boolean>, height: Dp, onClick: () -> Unit) {
    Box (modifier = Modifier
        .fillMaxWidth()
        .height(height)
        .clip(RoundedCornerShape(15.dp))

        .border(
            1.dp,
            Color(0xFFACE9FA),
            RoundedCornerShape(15.dp)
        )
        .background(Color.Transparent)
        .clickable {
            if (isActive.value)
                onClick()
        },
        contentAlignment = Alignment.Center) {
        Text(
            text = "Отмена",
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            textDecoration = TextDecoration.None,
            letterSpacing = 0.sp,
            lineHeight = 19.sp,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .alpha(1f),
            color = if (isActive.value) Color(0xFF010101) else Color(0x80000000),
            style = MaterialTheme.typography.button
        )
    }
}

@Composable
private fun ApplyButton(isActive: MutableState<Boolean>,
                 activeText: String, notActiveText: String = activeText,
                 height : Dp = 63.dp,
                 onClick: () -> Unit) {

    val backgroundActive = remember { mutableStateOf(isActive.value)}
    val textColor = remember { mutableStateOf(Color.White)}

    val text = remember { mutableStateOf(notActiveText)}

    if (isActive.value == true) {
        backgroundActive.value = true
        textColor.value = Color(0xFF000000)
        text.value = activeText
    }
    else {
        backgroundActive.value = false
        textColor.value = Color(0x80000000)
        text.value = notActiveText
    }


    val context = LocalContext.current

    Box (modifier = Modifier
        .fillMaxWidth()
        .height(height)
        .clip(RoundedCornerShape(15.dp))

        .border(
            1.dp,
            Color(0xFFACE9FA),
            RoundedCornerShape(15.dp)
        )
        .background(Color.Transparent)
        .clickable {
            if (isActive.value)
                onClick()
            else
                Toast
                    .makeText(context, "Заполните все поля", Toast.LENGTH_SHORT)
                    .show()
        },
        contentAlignment = Alignment.Center) {

        Image(
            painterResource(R.drawable.button_met),
            contentDescription = "Top image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
            alpha = if (backgroundActive.value) 1f else 0.5f)
        Text(
            text = text.value,
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            textDecoration = TextDecoration.None,
            letterSpacing = 0.sp,
            lineHeight = 19.sp,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .alpha(1f),
            color = textColor.value,
            style = MaterialTheme.typography.button
        )
    }
}