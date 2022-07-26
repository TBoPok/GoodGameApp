//package com.goodgame.goodgameapp

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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                fontSize = 36.sp,
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
        visualTransformation = {mobileFilter(it)},
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

const val phoneNumberMask = "+7                "
//                           012345678901234567

//const val phoneNumberMask = "+7 (___)-___-__-__"

fun mobileFilter(text: AnnotatedString) : TransformedText {
    val trimmed : String = when (text.text.length) {
        in(0..9) -> text.text
        else -> text.text.substring(0..9)
    }

    val annotatedString = AnnotatedString.Builder().run {
        var tr_i = 0
        for (i in phoneNumberMask.indices) {
            when (i) {
                0  -> append('+')
                1  -> append('7')
                2  -> append(' ')
                3  -> append(' ')
                7  -> append(' ')
                8  -> append(' ')
                12 -> append(' ')
                15 -> append(' ')
//                0  -> append('+')
//                1  -> append('7')
//                2  -> append(' ')
//                3  -> append('(')
//                7  -> append(')')
//                8  -> append('-')
//                12 -> append('-')
//                15 -> append('-')
                else -> {
                    if (tr_i != trimmed.length) {
                        append(trimmed[tr_i]);
                        tr_i++;
                    } else break
                }
            }
        }
        pushStyle(SpanStyle(color = Color.LightGray))
        append(phoneNumberMask.takeLast(phoneNumberMask.length - length))
        toAnnotatedString()
    }

    val phoneNumberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 3)  return offset  + 4
            if (offset <= 6)  return offset  + 6
            if (offset <= 8)  return offset  + 7
            if (offset <= 10) return offset  + 8
            return phoneNumberMask.length
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 4)   return 0
            if (offset <= 8)   return offset - 4
            if (offset <= 9)   return offset - 5
            if (offset <= 13)  return offset - 6
            if (offset <= 16)  return offset - 7
            return 16
        }
    }

    return TransformedText(annotatedString, phoneNumberOffsetTranslator)
}




