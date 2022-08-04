package com.goodgame.goodgameapp.models

data class HeroInfoResponse(
    val status : Boolean,
    val level: Int,
    val type : String,
    val expeditions : List<ExpeditionStoryModel>,
    val planet_status : Int?,
    val lvl_exp: Int,
    val rsp: Int,
    val next_lvl_need : Int,
    val has_expeditions: Int,
    val stats_points: Int,
    val stats: StatsModel)

data class HeroInfo(
    var hasHero: Boolean,
    var heroClass : String,
    var expeditions : List<ExpeditionStoryModel>,
    var total_progress : Int?,
    var level: Int,
    var lvl_exp: Int,
    var next_lvl_need : Int,
    var has_expeditions: Int,
    var coins: Int,
    var stats_points: Int,
    var stats: StatsModel)