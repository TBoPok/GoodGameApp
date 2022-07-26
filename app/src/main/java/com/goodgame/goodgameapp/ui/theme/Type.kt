package com.goodgame.goodgameapp.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.goodgame.goodgameapp.R

// Set of Material typography styles to start with
val Typography = Typography(
    h1 = TextStyle(
        fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp
    ),
    h2 = TextStyle(
        fontFamily = FontFamily(Font(R.font.montserrat_medium)),
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),

    h4 = TextStyle(
        fontFamily = FontFamily(Font(R.font.montserrat_extrabold)),
        fontWeight = FontWeight.ExtraBold,
        fontSize = 15.sp
    ),

    body1 = TextStyle(
        fontFamily = FontFamily(Font(R.font.montserrat_medium)),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    button = TextStyle(
        fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = FontFamily(Font(R.font.montserrat_medium)),
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    subtitle2 = TextStyle(
        fontFamily = FontFamily(Font(R.font.montserrat_medium)),
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp
    ),

    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
)