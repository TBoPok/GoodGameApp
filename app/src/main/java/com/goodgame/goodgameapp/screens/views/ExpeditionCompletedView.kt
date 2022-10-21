package com.goodgame.goodgameapp.screens.views

import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.goodgame.goodgameapp.R
import com.goodgame.goodgameapp.models.ExpeditionStoryModel
import com.goodgame.goodgameapp.models.HeroInfo
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newCoroutineContext
import java.io.File
import kotlin.coroutines.CoroutineContext
import kotlin.math.exp

@Composable
fun ExpeditionCompletedView(expedition: ExpeditionStoryModel, closeEvent: () -> Unit) {
    BackHandler() {
        closeEvent()
    }
    val micra_bold = TextStyle(
        color = Color.White,
        fontFamily = FontFamily(Font(R.font.micra_bold)),
        fontWeight = FontWeight.Bold,
        lineHeight = 20.sp,
        fontSize = 14.sp,
    )
    val micra = TextStyle(
        color = Color.White,
        fontFamily = FontFamily(Font(R.font.micradi)),
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
    )

    val context = LocalContext.current
    val video = remember {
        when (expedition.result) {
            "win" -> R.raw.coin_1
            "lose" -> R.raw.coin_2
            else -> R.raw.exp
        }
    }
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val uri = RawResourceDataSource.buildRawResourceUri(video)
            setMediaItem(MediaItem.fromUri(uri))
            repeatMode = Player.REPEAT_MODE_ONE
            prepare()
            playWhenReady = true
        }
    }
    // player view
    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
            exoPlayer.release()
        }
    }
    var showView by remember { mutableStateOf(false)}
    LaunchedEffect(true) {
        delay(200)
        showView = true
    }

    val interactionSource = remember { MutableInteractionSource() }
    Box(
        Modifier
            .fillMaxSize()
            .alpha(if (showView) 1f else 0f)
            .padding(15.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                /* .... */
            }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .clip(RoundedCornerShape(15.dp))
                .border(
                    1.dp,
                    Color(0xFFA7FAE9),
                    RoundedCornerShape(15.dp)
                )
                .background(Color(0xFF010101))

        ) {
            Column() {

                VideoPlayer(exoPlayer, modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .clip(
                        RoundedCornerShape(15.dp)
                    ))

                Column(
                    modifier = Modifier
                        .padding(horizontal = 14.dp),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = "Экспедиция пройдена",
                        style = micra_bold,
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val sign = if (expedition.rsp < 0) "" else "+"
                        Text(text = "$sign${expedition.rsp}",
                            style = MaterialTheme.typography.subtitle1,
                            color = Color(0x80FFFFFF))
                        Spacer(modifier = Modifier.padding(end = 5.dp))
                        Image(
                            painterResource(R.drawable.coin),
                            contentDescription = "coin",
                            contentScale = ContentScale.FillHeight,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.padding(end = 20.dp))
                        Text(text = "+${expedition.exp} очков опыта",
                            style = MaterialTheme.typography.subtitle1,
                            color = Color(0x80FFFFFF))
                    }
                    Spacer(modifier = Modifier.height(15.dp))

                    MetallButton(isActive = true, activeText = "Продолжить") {
                        closeEvent()
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }

    Column (modifier = Modifier.padding(14.dp)) {
        Row(
            modifier = Modifier
                .weight(1f)
                .background(Color.Transparent)) {
            CloseRow(closeEvent = {closeEvent()})
        }
        Row(modifier = Modifier.weight(8f)) {}
        Row(modifier = Modifier.weight(1f)) {} // Пустое поле
    }

}

@Composable
fun LevelUpView(heroInfo: HeroInfo?, closeEvent: () -> Unit) {
    BackHandler() {
        closeEvent()
    }
    val micra_bold = TextStyle(
        color = Color.White,
        fontFamily = FontFamily(Font(R.font.micra_bold)),
        fontWeight = FontWeight.Bold,
        lineHeight = 35.sp,
        fontSize = 30.sp,
    )
    val micra = TextStyle(
        color = Color.White,
        fontFamily = FontFamily(Font(R.font.micradi)),
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
    )
    val lvlDescription = when(heroInfo?.level) {
        1 ->  "+3% к получаемым очкам исследования"
        2 ->  "+6% к получаемым очкам исследования"
        3 ->  "+9% к получаемым очкам исследования"
        4 -> "+12% к получаемым очкам исследования"
        5 -> "+15% к получаемым очкам исследования"
        6 -> "+18% к получаемым очкам исследования"
        7 -> "+21% к получаемым очкам исследования"
        8 -> "+24% к получаемым очкам исследования"
        9 -> "+27% к получаемым очкам исследования"
        10-> "+30% к получаемым очкам исследования"
        else -> "+30% к получаемым очкам исследования"
    }
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val uri = RawResourceDataSource.buildRawResourceUri(R.raw.level_up)
            setMediaItem(MediaItem.fromUri(uri))
            repeatMode = Player.REPEAT_MODE_ONE
            prepare()
            playWhenReady = true
        }
    }
    // player view
    DisposableEffect(exoPlayer) {
        onDispose {
            // relase player when no longer needed
            Log.d("ExoPlayer", "ExoPlayer disposed")
            exoPlayer.release()
        }
    }

    var showView by remember { mutableStateOf(false)}
    LaunchedEffect(true) {
        delay(200)
        showView = true
    }
    Box(
        Modifier
            .fillMaxSize()
            .alpha(if (showView) 1f else 0f)
            .padding(15.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .clip(RoundedCornerShape(15.dp))
                .border(
                    1.dp,
                    Color(0xFFA7FAE9),
                    RoundedCornerShape(15.dp)
                )
                .background(Color(0xFF010101))

        ) {
            Column() {
                VideoPlayer(
                    exoPlayer, modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .clip(
                            RoundedCornerShape(15.dp)
                        )
                )
                Column(
                    modifier = Modifier
                        .padding(horizontal = 14.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = "${heroInfo?.level} LVL",
                        style = micra_bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = lvlDescription,
                        style = MaterialTheme.typography.subtitle1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0x80FFFFFF)
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    HasExpPoints(points = heroInfo?.stats_points ?: 0)
                    Spacer(modifier = Modifier.height(15.dp))
                    MetallButton(
                        isActive = true,
                        activeText = "Продолжить"
                    ) {
                        closeEvent()
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
    Column (modifier = Modifier.padding(14.dp)) {
        Row(
            modifier = Modifier
                .weight(1f)
                .background(Color.Transparent)) {
            CloseRow(closeEvent = {closeEvent()})
        }
        Row(modifier = Modifier.weight(8f))
        {

        }
        Row(modifier = Modifier.weight(1f)) {} // Пустое поле
    }
}

@Composable
private fun HasExpPoints(points: Int) {
    Box(modifier = Modifier
        .clip(RoundedCornerShape(15.dp))
        .fillMaxWidth()
        .background(Color.Transparent)
        .border(
            1.dp,
            Color(0xFF0077FF),
            RoundedCornerShape(15.dp),
        )
    ) {
        Box (
            Modifier
                .background(Color(0xFF0077FF))) {
            Row (modifier = Modifier.height(35.dp)) {
                Text(text = "вам доступны очки навыков",
                    style = MaterialTheme.typography.button,
                    color = Color(0xFFFFFFFF),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .fillMaxWidth()
                        .padding(start = 15.dp))

            }
        }
    }
}

@Composable
private fun CloseRow(closeEvent: () -> Unit) {
    Row {
        Spacer(modifier = Modifier.weight(1f))
        Box(modifier = Modifier.padding(top = 28.dp, end = 10.dp)) {
            CloseButton {
                closeEvent()
            }
        }
    }
}

@Composable
fun VideoPlayer(exoPlayer: ExoPlayer, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier,
        factory = {
            // exo player view for our video player
            StyledPlayerView(context).apply {
                useController = false
                player = exoPlayer
                layoutParams =
                    FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams
                            .MATCH_PARENT,
                        ViewGroup.LayoutParams
                            .WRAP_CONTENT
                    )
            }
        },
    )

}
