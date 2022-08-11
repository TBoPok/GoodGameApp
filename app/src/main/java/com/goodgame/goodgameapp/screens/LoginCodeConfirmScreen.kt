package com.goodgame.goodgameapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.goodgame.goodgameapp.R
import com.goodgame.goodgameapp.navigation.Screen
import com.goodgame.goodgameapp.retrofit.Status
import com.goodgame.goodgameapp.screens.views.*
import com.goodgame.goodgameapp.viewmodel.LoginViewModel

enum class LoginCCState {
    CONFIRM,        // Вводим код из бота
    CHECK_KEY,      // Проверяем код
    INTERNET_ERROR, // Ошибка интернета
}

@Composable
fun LoginCodeConfirmScreen (navController: NavController, viewModel: LoginViewModel) {

    val screenState = remember { mutableStateOf(LoginCCState.CONFIRM)}
    val errorMessage = remember {mutableStateOf("")}
    val wrongKey = remember { mutableStateOf(false)}

    val lifecycleOwner = LocalLifecycleOwner.current
    val refreshKey = remember { mutableStateOf(true)}
    fun MutableState<Boolean>.trigger() { value = !value } // refreshKey trigger

    LaunchedEffect(refreshKey.value) {
        when (screenState.value) {
            LoginCCState.CONFIRM -> {

            }
            LoginCCState.CHECK_KEY -> {
                viewModel.sendConfirmCode().observe(lifecycleOwner) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            if (it.data?.status == true) {
                                viewModel.saveToken(it.data.private_key)
                                navController.navigate(Screen.SplashScreen.route) {
                                    popUpTo(
                                        navController.currentBackStackEntry?.destination?.route
                                            ?: return@navigate
                                    ) { inclusive = true }
                                }
                            }
                            else {
                                wrongKey.value = true
                                screenState.value = LoginCCState.CONFIRM
                            }
                        }
                        Status.ERROR -> {
                            screenState.value = LoginCCState.INTERNET_ERROR
                            errorMessage.value = it.message ?: ""
                        }
                        else -> {}
                    }
                }
            }
            LoginCCState.INTERNET_ERROR -> {
                screenState.value = LoginCCState.CONFIRM
            }
        }
    }

    Column {

        when (screenState.value) {
            LoginCCState.CONFIRM -> {
                CodeConfirmView(
                    wrongKey.value,
                    done = { confirmKey ->
                        viewModel.confirmKey.value = confirmKey
                        screenState.value = LoginCCState.CHECK_KEY
                        refreshKey.trigger()
                    }
                )
            }
            LoginCCState.CHECK_KEY -> {
                LoadingView()
            }
            LoginCCState.INTERNET_ERROR -> {
                ErrorAlert(
                    errorMessage = "Нет подключения к интернету",
                    refresh = { refreshKey.trigger(); screenState.value = LoginCCState.CONFIRM },
                    cancel = { screenState.value = LoginCCState.CONFIRM })
            }
        }


    }
}

@Composable
fun CodeConfirmView (wrongKey : Boolean, done : (confirmCode : String) -> Unit) {
    val buttonActive = remember { mutableStateOf(false) }
    var confirmCode = remember { mutableStateOf("") }

    buttonActive.value = confirmCode.value.length == 6

    Column(modifier = Modifier
        .background(Color(0xff00131d))
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            TopImage {
                Image(
                    painterResource(R.drawable.white_logo),
                    contentDescription = "Top logo"
                )
            }
        }
        Row (modifier = Modifier.weight(1f)) {
            Column(modifier = Modifier.padding(14.dp)) {
                Spacer(modifier = Modifier.padding(top = 80.dp))
                Text(
                    text = "Осталось совсем немного",
                    style = MaterialTheme.typography.body1,
                    color = Color.White)
                Spacer(modifier = Modifier.padding(top = 15.dp))
                Text(text = "Введи код\nиз телеграма",
                    style = MaterialTheme.typography.h1,
                    color = Color.White)
                if (wrongKey == true) {
                    Text(
                        text = "Неверный код. Проверьте и введите заново",
                        color = Color.Red,
                        style = MaterialTheme.typography.subtitle1
                    )
                }
                Spacer(modifier = Modifier.padding(top = 55.dp))
                CodeConfirmField(confirmCode)
                Spacer(modifier = Modifier.padding(top = 15.dp))
                MetallButton(isActive = buttonActive, activeText = "Продолжить") {
                    done(confirmCode.value)
                }
            }
        }
        Row (modifier = Modifier.fillMaxWidth()) {
            BottomImage {}
        }
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CodeConfirmField(confirmCode : MutableState<String>) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
        .fillMaxWidth()
        .padding(top = 10.dp)) {
        TextField(
            value = confirmCode.value,
            onValueChange = {changedValue ->
                val filteredValue = changedValue.filter { char -> char.isDigit() }
                confirmCode.value = if (filteredValue.length <= 6) filteredValue else filteredValue.substring(0..5) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(
                onDone = {keyboardController?.hide(); focusManager.moveFocus(FocusDirection.Down)}),
            visualTransformation = { codeFilter(it) },
            singleLine = true,
            textStyle = TextStyle(fontSize = 25.sp, textAlign = TextAlign.Center),
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color(0xFF010101),
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                cursorColor = Color(0xFF010101)),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp, bottomStart = 15.dp, bottomEnd = 15.dp))
                .background(Color(red = 1f, green = 1f, blue = 1f, alpha = 1f)),

        )
    }
}


fun codeFilter(text: AnnotatedString) : TransformedText {
    val codeMask = "_   _   _   _   _   _"

    val trimmed : String = when (text.text.length) {
        in(0..5) -> text.text
        else -> text.text.substring(0..5)
    }
    val annotatedString = AnnotatedString.Builder().run {
        var tr_i = 0
        for (i in codeMask.indices) {
            when (i) {
                in (1..3)   -> append(' ')
                in (5..7)   -> append(' ')
                in (9..11)  -> append(' ')
                in (13..15) -> append(' ')
                in (17..19) -> append(' ')
                else -> {
                    if (tr_i != trimmed.length) {
                        append(trimmed[tr_i]);
                        tr_i++;
                    } else break
                }
            }
        }
        pushStyle(SpanStyle(color = Color.LightGray))
        append(codeMask.takeLast(codeMask.length - length))
        toAnnotatedString()
    }

    val confirmCodeOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 1)   return 1
            if (offset == 2)   return 4 + 1
            if (offset == 3)   return 8 + 1
            if (offset == 4)   return 12 + 1
            if (offset == 5)   return 16 + 1
            return 20 + 1
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 3)   return 1
            if (offset <= 7)   return 2
            if (offset <= 11)  return 3
            if (offset <= 15)  return 4
            if (offset <= 19)  return 5
            return 6
        }
    }

    return TransformedText(annotatedString, confirmCodeOffsetTranslator)
}
