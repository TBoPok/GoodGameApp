package com.goodgame.goodgameapp.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = White,
    onPrimary = Gray,
    primaryVariant = Dark1,
    secondary = Teal200,
    background = DarkSurface,

    onBackground = DarkOnBackground,
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color(0xFF010101),
    onBackground = Color(0xFF010101),
    onSurface = Color(0xFF010101),
    */
)

@Composable
fun GoodGameAppTheme(content: @Composable () -> Unit) {
    val colors = DarkColorPalette
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setNavigationBarColor(
            color = Dark1, //Your color
            darkIcons = false
        )
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = {
            ProvideTextStyle(
                value = TextStyle(SemiWhite),
                content = content
            )
        }
    )
}

