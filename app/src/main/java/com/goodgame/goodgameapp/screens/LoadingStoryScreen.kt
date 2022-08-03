package com.goodgame.goodgameapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.goodgame.goodgameapp.R
import com.goodgame.goodgameapp.download.PackageLoader
import com.goodgame.goodgameapp.navigation.Screen
import com.goodgame.goodgameapp.retrofit.Status
import com.goodgame.goodgameapp.screens.views.MetallButton
import com.goodgame.goodgameapp.viewmodel.GameViewModel


@Composable
fun LoadingStoryScreen(navController: NavController, viewModel: GameViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val downloader = remember {
        mutableStateOf(PackageLoader(
            context = context,
            dataRequest = {viewModel.apiInterface.getImage(it)}
        ))
    }
    val loadingProgress = remember { mutableStateOf(0)}
    val message = remember { mutableStateOf("")}
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painterResource(R.drawable.openpage),
            contentDescription = "first_screen",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .clip(RoundedCornerShape(15.dp))
                .border(1.dp, Color(0xFFACE9FA), RoundedCornerShape(15.dp))
                .background(Color.White)) {
            Column(Modifier.padding(vertical = 20.dp, horizontal = 20.dp)) {
                Text(
                    text = "Тестовая загрузка",
                    style = MaterialTheme.typography.h1,
                    fontSize = 25.sp,
                    color = Color.Black,
                )
                Spacer(modifier = Modifier.height(15.dp))

//                downloader.value.start().observe(lifecycleOwner) {
//                    when(it.status) {
//                        Status.LOADING -> {
//                            loadingProgress.value = it.data ?: 0
//                        }
//                        Status.SUCCESS -> {
//                            loadingProgress.value = it.data ?: 0
//                        }
//                        Status.ERROR -> {
//                            message.value = it.message ?: ""
//                        }
//                    }
//                }
                Text(
                    text =  loadingProgress.value.toString(),
                    style = MaterialTheme.typography.subtitle2,
                    lineHeight = 19.sp,
                    color = Color.Black
                )
                Text(
                    text =  message.value,
                    style = MaterialTheme.typography.subtitle2,
                    lineHeight = 19.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(15.dp))
                MetallButton(isActive = mutableStateOf(true), height = 55.dp, activeText = "Понятно") {

                }
            }
        }
    }
}