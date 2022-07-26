package com.goodgame.goodgameapp.models

data class TokenModel(val private_key : String)

data class TokenConfirmResponse(val status : Boolean, val username : String?, val hasHero : Boolean?)
