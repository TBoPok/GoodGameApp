package com.goodgame.goodgameapp.screens.views

import android.content.Intent
import android.net.Uri
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.goodgame.goodgameapp.models.ClubModel

@Composable
fun NotRegistredView (navController: NavController, club : ClubModel) {

    val openDialog = remember { mutableStateOf(true)  }
    val context = LocalContext.current

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { navController.navigateUp(); openDialog.value = false },
            title = { Text(text = "Аккаунт не найден") },
            text = { Text(text = "В клубе по адресу ${club.text_name} пользователь с указанным " +
                "номером не найден") },
            confirmButton = {
                Button(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(club.telegram_bot_url))
                    ContextCompat.startActivity(context, intent, null)
                    navController.navigateUp()
                    openDialog.value = false
                    } ) {
                        Text(text = "Регистрация")
                }
            },
            dismissButton  = {
                Button(onClick = { navController.navigateUp(); openDialog.value = false } ) {
                    Text(text = "Назад")
                }
            })
    }
}