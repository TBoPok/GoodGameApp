package com.goodgame.goodgameapp.screens.views

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goodgame.goodgameapp.R

@Composable
fun MetallButton(isActive: Boolean,
                 activeText: String, notActiveText: String = activeText,
                 toastText: String = "",
                 height : Dp = 63.dp,
                 onClick: () -> Unit) {

    val backgroundActive = remember { mutableStateOf(isActive)}
    val textColor = remember { mutableStateOf(Color.White)}

    val text = remember { mutableStateOf(notActiveText)}

    if (isActive == true) {
        backgroundActive.value = true
        textColor.value = Color(0xFF010101)
        text.value = activeText
    }
    else {
        backgroundActive.value = false
        textColor.value = Color.White
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
            if (isActive)
                onClick()
            else if (toastText != "")
                Toast
                    .makeText(context, toastText, Toast.LENGTH_SHORT)
                    .show()
        },
        contentAlignment = Alignment.Center) {
        if (backgroundActive.value == true)
            Image(
                painterResource(R.drawable.button_met),
                contentDescription = "Top image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize())
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