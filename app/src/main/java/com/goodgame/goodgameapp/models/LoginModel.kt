package com.goodgame.goodgameapp.models

data class LoginModel (val phone_number : String, val club_id : String)

data class LoginResponse (val status : Boolean = false, val info : String)

