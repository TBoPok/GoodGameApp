package com.goodgame.goodgameapp.models

data class ExpeditionModel(
    val number: Int,
    val text: String,
    val dangerLevel: String,
    val imageUrl: String,
    val winReward: Int,
    val looseReward: Int,
)
