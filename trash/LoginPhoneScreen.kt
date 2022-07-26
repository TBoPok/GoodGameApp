package com.goodgame.goodgameapp

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.dpanStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.goodgame.goodgameapp.viewmodel.LoginViewModel

@Composable
fun LoginPhoneScreen(navController: NavHostController, viewModel: LoginViewModel) {
    val phoneNumber = remember { mutableStateOf(viewModel.phoneNumber.value ?: "") }
    val context = LocalContext.current

    Column() {
        TopAppBar(
            title = {
                Text(text = "")
            },

            navigationIcon = {
                IconButton(onClick = {
                    navController.navigateUp()
                }) {
                    Icon(Icons.Filled.ArrowBack, "backIcon")
                    viewModel.phoneNumber.value = phoneNumber.value
                }
            },
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colors.onBackground
        )

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
        ) {
            Text(
                text = "Вход и регистрация",
                fontSize = 30.dp,
                maxLines = 2,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding()
            )

            Column(modifier = Modifier
                .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {

                PhoneNumber_Input(phoneNumber)

                Button(
                    modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
                    onClick = {

                        if (phoneNumber.value.length == 10) {
                            navController.navigate("LoginClubChoiseScreen")
                            viewModel.phoneNumber.value = phoneNumber.value
                        } else {
                            Toast.makeText(
                                context,
                                "Введите номер телефона",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }) {
                    Text(text = "Далее")
                }
            }

        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PhoneNumber_Input(phoneNumber : MutableState<String>) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Text(text = "Номер телефона",
        modifier = Modifier.padding(bottom = 2.dp))
    TextField(
        value = phoneNumber.value,
        onValueChange = { changedValue ->
            val filteredValue = changedValue.filter { char -> char.isDigit() }
            phoneNumber.value = if (filteredValue.length < 10) filteredValue else filteredValue.substring(0..9)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide();
                focusManager.moveFocus(FocusDirection.Down)
            }),
        //visualTransformation = {mobileFilter(it)},
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}







