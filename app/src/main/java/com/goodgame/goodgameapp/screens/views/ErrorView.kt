package com.goodgame.goodgameapp.screens.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Retrofit
import java.io.IOException
import java.lang.Exception
import java.net.UnknownHostException

enum class InternetErrors {
    NO_INTERNET,
    ELSE_ERROR,
}

@Composable
fun ErrorView(errorMessage : String, debugMessage : String = "", refresh : () -> Unit) {

    Box (modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.padding(40.dp)) {
            Text(text = errorMessage)
            Button(onClick = { refresh() }, modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)) {
                Text(text = "Подключиться снова")
            }

        }
        Text(text = debugMessage, fontSize = 5.sp,modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun ErrorAlert(errorMessage : String,
               isRefreshActive : Boolean = true,
               refresh : () -> Unit = { },
               cancel : () -> Unit) {

    val openDialog = remember { mutableStateOf(true)  }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { cancel(); openDialog.value = false },
            title = { Text(text = "Ошибка") },
            text = { Text(text = errorMessage) },
            confirmButton = {
                if (isRefreshActive)
                    Button(onClick = { refresh() ; openDialog.value = false },
                        modifier = Modifier.background(Color.DarkGray))
                    {
                        Text(text = "Обновить", color = Color.White)
                    }
            },
            dismissButton  = {
                Button(onClick = { cancel() ; openDialog.value = false },
                    modifier = Modifier.background(Color.DarkGray))
                {
                    Text(text = "Назад", color = Color.White)
                }
            })
    }

}