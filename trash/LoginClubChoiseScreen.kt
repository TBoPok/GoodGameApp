package com.goodgame.goodgameapp.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.goodgame.goodgameapp.models.ClubModel
import com.goodgame.goodgameapp.retrofit.Status
import com.goodgame.goodgameapp.screens.views.ErrorAlert
import com.goodgame.goodgameapp.screens.views.LoadingView
import com.goodgame.goodgameapp.viewmodel.LoginViewModel


@Composable
fun LoginClubChoiceScreen (navController: NavController, viewModel: LoginViewModel) {
    fun MutableState<Boolean>.trigger() { value = !value }

    val clubsStatus = remember {mutableStateOf(Status.LOADING)}
    val clubsStatusMessage = remember {mutableStateOf("")}
    val allClubs = remember {mutableListOf<ClubModel>()}

    val lifecycleOwner = LocalLifecycleOwner.current
    val refreshKey = remember { mutableStateOf(true)}
    LaunchedEffect(refreshKey.value) {
        viewModel.getClubs().observe(lifecycleOwner, Observer {
            clubsStatus.value = it.status
            when (it.status) {
                Status.SUCCESS -> { allClubs.clear(); if (it.data != null) allClubs.addAll(it.data) }
                Status.ERROR   -> { clubsStatusMessage.value = it.message ?: "" }
            }
        })
    }

    Column() {
        TopAppBar(
            title = {
                Text(text = "Выберите свой клуб")
            },

            navigationIcon = {
                IconButton(onClick = {
                    navController.navigateUp()
                }) {
                    Icon(Icons.Filled.ArrowBack, "backIcon")
                }
            },
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colors.onBackground
        )

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
        ) {
            when(clubsStatus.value) {
                Status.SUCCESS -> {
                    GenerateClubs(clubs = allClubs, onClick = { club ->
                        viewModel.club.value = club
                        navController.navigate("LoginCodeConfirmScreen")
                    })
                }
                Status.LOADING -> {
                    LoadingView()
                }
                Status.ERROR -> {
                    ErrorAlert(
                        errorMessage = "Нет подключения к интернету",
                        refresh = {refreshKey.trigger(); clubsStatus.value = Status.LOADING},
                        cancel = {navController.navigateUp()})
                }

            }
        }
    }
}

@Composable
fun RegistrationClubChoiceScreen (navController: NavController, viewModel: LoginViewModel) {
    fun MutableState<Boolean>.trigger() { value = !value }

    val clubsStatus = remember {mutableStateOf(Status.LOADING)}
    val clubsStatusMessage = remember {mutableStateOf("")}
    val allClubs = remember {mutableListOf<ClubModel>()}

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val refreshKey = remember { mutableStateOf(true)}
    LaunchedEffect(refreshKey.value) {
        viewModel.getClubs().observe(lifecycleOwner, Observer {
            clubsStatus.value = it.status
            when (it.status) {
                Status.SUCCESS -> { allClubs.clear(); if (it.data != null) allClubs.addAll(it.data) }
                Status.ERROR   -> { clubsStatusMessage.value = it.message ?: "" }
            }
        })
    }

    Column() {
        TopAppBar(
            title = {
                Text(text = "Выберите свой клуб")
            },

            navigationIcon = {
                IconButton(onClick = {
                    navController.navigateUp()
                }) {
                    Icon(Icons.Filled.ArrowBack, "backIcon")
                }
            },
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colors.onBackground
        )

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
        ) {
            when(clubsStatus.value) {
                Status.SUCCESS -> {
                    GenerateClubs(clubs = allClubs, onClick = { club ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(club.telegram_bot_url))
                        startActivity(context, intent, null)
                        navController.navigate("LogOrRegScreen") {
                            popUpTo(navController.currentBackStackEntry?.destination?.route ?: return@navigate) {
                                inclusive = true
                            }
                        }
                    })
                }
                Status.LOADING -> {
                    LoadingView()
                }
                Status.ERROR -> {
                    ErrorAlert(
                        errorMessage = "Нет подключения к интернету",
                        refresh = {refreshKey.trigger(); clubsStatus.value = Status.LOADING},
                        cancel = {navController.navigateUp()})
                }

            }
        }
    }
}

@Composable
fun GenerateClubs(clubs : List<ClubModel>, onClick : (club : ClubModel) -> Unit) {
    var scrollState = rememberLazyListState()

    LazyColumn(state = scrollState,
        contentPadding = PaddingValues(vertical = 3.dp)) {
        items(items = clubs) { club ->
            ClubCard(club ,onClick)
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ClubCard(club : ClubModel, onClick : (club : ClubModel) -> Unit) {
    Card(shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 3.dp, end = 3.dp)
            .height(60.dp),
        onClick = {
            onClick(club)
        }

    ) {
        Box(modifier = Modifier
            .wrapContentSize(align = Alignment.CenterStart)
            .padding(start = 5.dp)) {
            Text(text = club.text_name)
        }
    }
}
