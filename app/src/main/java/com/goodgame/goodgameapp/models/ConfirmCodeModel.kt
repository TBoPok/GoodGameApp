package com.goodgame.goodgameapp.models

data class ConfirmCodeModel(val confirm_key : String, val phone_number : String, val club_id : String)

data class ConfirmCodeResponse (val status : Boolean, val private_key: String)
