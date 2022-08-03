package com.goodgame.goodgameapp.models

data class Reward(val reward: String, val count: Int?, val dateTime: String)

data class RewardResponse(val status: Boolean, val rewards: List<Reward>)
