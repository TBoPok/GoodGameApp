package com.goodgame.goodgameapp.models

data class CharacterModel(
    val name: String,
    val id_name: String,
    val description: String,
    val card_bg: Int? = null,
    val power: Int,
    val intelligence: Int,
    val charisma: Int,
    val luck: Int)
