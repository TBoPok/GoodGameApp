package com.goodgame.goodgameapp.screens.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T> FadeTransition(state: MutableState<T>, visibleStates: List<T>, content: @Composable () -> Unit) {
    val transition = updateTransition(state.value, label = "")
    transition.AnimatedVisibility(
        visible = { targetSelected ->
            var isVisibleState = false
            visibleStates.forEach {
                if (it == targetSelected)
                    isVisibleState = true
            }
            isVisibleState
        },
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        content()
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T> FadeTransition(state: MutableState<T>, visibleState: T, content: @Composable () -> Unit) {
    val transition = updateTransition(state.value, label = "")
    transition.AnimatedVisibility(
        visible = { targetSelected -> targetSelected == visibleState
        },
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        content()
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T> FadeTransition(state: Boolean, visibleState: T, content: @Composable () -> Unit) {
    val transition = updateTransition(state, label = "")
    transition.AnimatedVisibility(
        visible = { targetSelected -> targetSelected == visibleState
        },
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        content()
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FadeTransitionFloat(state: Float, visibleState: Float, content: @Composable () -> Unit) {
    val transition = updateTransition(state, label = "")
    transition.AnimatedVisibility(
        visible = { targetSelected -> targetSelected >= visibleState
        },
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        content()
    }
}