package com.goodgame.goodgameapp.screens

import Devices.NEXUS_5
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.goodgame.goodgameapp.R
import com.goodgame.goodgameapp.screens.views.LogoView
import com.goodgame.goodgameapp.ui.theme.GoodGameAppTheme
import com.goodgame.goodgameapp.viewmodel.LoginViewModel

@Composable
fun LogOrRegScreen(navController: NavController, viewModel: LoginViewModel) {

    Box(modifier = Modifier // Background box
        .fillMaxSize()
        .background(color = MaterialTheme.colors.background)
    ) {
        Column(modifier = Modifier // Logo
            .padding(30.dp)
            .align(Alignment.Center)
        ) {
            LogoView()
            TextUnderLogo()
        }

        Column( // Buttons in bottom
            modifier = Modifier
                .padding(30.dp)
                .align(Alignment.BottomCenter)
        ) { // Login and register button
            LoginButton(navController)
            Spacer(modifier = Modifier.padding(5.dp))
        }
    }
}



@Composable
fun TextUnderLogo() {
    Text(
        text = "Приложение компьютерного клуба GoodGame. Думаю текст надо поменять",
        fontSize = 18.sp,
        color = MaterialTheme.colors.onBackground
        // Добавить шрифты и т.п.
    )
}

@Composable
fun LoginButton(navController : NavController) {
    Button(
        onClick = { navController.navigate("LoginPhoneScreen") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        shape = RoundedCornerShape(35)
    ) {
        Text(text = "Вход и регистрация")
    }
}

@Composable
fun RegisterButton(navController : NavController) {
    Button(
        onClick = { navController.navigate("RegistrationScreen") },
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(50)
    ) {
        Text(text = "Регистрация")
    }
}

@Preview(device = NEXUS_5)
@Composable
fun MainPreview() {
    GoodGameAppTheme(darkTheme = true) {
        //LogOrRegScreen()
    }
}
