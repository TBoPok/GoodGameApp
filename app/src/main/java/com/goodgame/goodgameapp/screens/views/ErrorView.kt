package com.goodgame.goodgameapp.screens.views

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goodgame.goodgameapp.R
import com.goodgame.goodgameapp.download.PackageModel
import com.goodgame.goodgameapp.retrofit.Status
import retrofit2.Retrofit
import java.io.IOException
import java.lang.Exception
import java.net.UnknownHostException

@Composable
fun ErrorAlert(headText: String = "Что-то пошло не так",
               errorMessage : String,
               cancelText : String = "Отмена",
               isRefreshActive : Boolean = true,
               isRefreshExists: Boolean = true,
               refresh : () -> Unit = { },
               cancel : () -> Unit) {


    Box(Modifier.fillMaxSize().background(Color(0x80000000)), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .clip(RoundedCornerShape(15.dp))
                .border(1.dp, Color(0xFFACE9FA), RoundedCornerShape(15.dp))
                .background(Color.White)) {
            Column(Modifier.padding(vertical = 20.dp, horizontal = 20.dp)) {
                Text(
                    text = headText,
                    style = MaterialTheme.typography.h1,
                    fontSize = 25.sp,
                    color = Color(0xFF010101),
                )
                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text =  errorMessage,
                    style = MaterialTheme.typography.subtitle2,
                    lineHeight = 19.sp,
                    color = Color(0xFF010101)
                )
                Spacer(modifier = Modifier.height(15.dp))
                Row () {
                    Box(Modifier.weight(0.35f)) {
                        CancelButton(text = cancelText, isActive = remember {mutableStateOf(true)}, height = 55.dp) {
                            cancel()
                        }
                    }
                    if (isRefreshExists) {
                        Spacer(modifier = Modifier.width(15.dp))
                        Box(Modifier.weight(0.65f)) {
                            ApplyButton(
                                isActive = remember { mutableStateOf(isRefreshActive) },
                                height = 55.dp,
                                activeText = "Повторить"
                            ) {
                                refresh()
                            }
                        }
                    }
                }
            }
        }
    }

}
@Composable
private fun CancelButton(text: String, isActive: MutableState<Boolean>, height: Dp, onClick: () -> Unit) {
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
            text = text,
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