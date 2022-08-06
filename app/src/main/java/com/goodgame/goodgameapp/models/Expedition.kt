package com.goodgame.goodgameapp.models

data class Expedition(
    val status: Boolean,
    val description: String,
    val difficulty: String,
    val image: String?,
    val win_rsp: Int,
    val lose_rsp: Int,
    val info: String,
)

data class ExpeditionResult(
    val result: String,
    val rsp: Int,
    val exp: Int,
    val status: Boolean,
    val description: String,
    val planet_status: Int,
    val info: String,
)
