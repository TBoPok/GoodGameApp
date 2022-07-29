package com.goodgame.goodgameapp.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.goodgame.goodgameapp.R
import com.goodgame.goodgameapp.models.ClubModel
import com.goodgame.goodgameapp.screens.views.*
import com.goodgame.goodgameapp.viewmodel.LoginViewModel

@Composable
fun RegistrationScreen(navController: NavHostController, viewModel: LoginViewModel) {
    val currentClub = remember { mutableStateOf<ClubModel?>(null) }

    val showClubListView = remember { mutableStateOf(false) }

    val enterInAccountButtonActive = remember { mutableStateOf(false) }
    enterInAccountButtonActive.value = currentClub.value != null

    val keyboardController = LocalFocusManager.current
    val context = LocalContext.current

    Column(modifier = Modifier
        .background(Color(0xff00131d))
    ) {

        Column() {
            Row() {
                TopImage {
                    Image(
                        painterResource(R.drawable.white_logo),
                        contentDescription = "Top logo")
                }
            }
            Row (modifier = Modifier.padding(14.dp).weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Column()
                {
//                    Spacer(modifier = Modifier.padding(15.dp))
                    LoginHeadText(
                        "Для регистрации мы используем телеграм бота",
                        "Выбери свой клуб и пройди короткую регистрацию через бота"
                    )
                    Spacer(modifier = Modifier.padding(20.dp))
                    ChoseClubNew_Input(currentClub) {
                        keyboardController.clearFocus()
                        showClubListView.value = true
                    }
                    Spacer(modifier = Modifier.padding(5.dp))
                    MetallButton(isActive = enterInAccountButtonActive, activeText = "Перейти в чат") {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentClub.value!!.telegram_bot_url))
                        ContextCompat.startActivity(context, intent, null)
                        navController.navigateUp()
                    }
                    Spacer(modifier = Modifier.padding(5.dp))
                    WeDontUseYourData()
                }

            }
            Row() {
                BottomImage {
                    BackToLogin {
                        navController.navigateUp()
                    }
                }
            }

        }
    }
    if (showClubListView.value == true) {
        ClubListView(viewModel = viewModel) { chosenClub ->
            currentClub.value = chosenClub.value
            showClubListView.value = false
        }
    }

}

@Composable
fun BackToLogin(onClick: () -> Unit) {
    Row (horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.clickable { onClick() }) {
            Text(
                text = "вернуться к логину",
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                textDecoration = TextDecoration.None,
                letterSpacing = 0.sp,
                lineHeight = 19.sp,
                overflow = TextOverflow.Ellipsis,
                color = Color(red = 1f, green = 1f, blue = 1f, alpha = 1f),
                style = MaterialTheme.typography.button
            )
        }
    }
}