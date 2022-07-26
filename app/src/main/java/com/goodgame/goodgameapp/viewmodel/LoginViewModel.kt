package com.goodgame.goodgameapp.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.goodgame.goodgameapp.models.*
import com.goodgame.goodgameapp.retrofit.ApiHelper
import com.goodgame.goodgameapp.retrofit.Response
import com.goodgame.goodgameapp.retrofit.Status
import com.goodgame.goodgameapp.sharedprefs.SharedPrefs
import kotlinx.coroutines.Dispatchers


class LoginViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    val phoneNumber = MutableLiveData<String>().apply { value = "" }
    val club = MutableLiveData<ClubModel>()
    val confirmKey = MutableLiveData<String>().apply { value = "" }

    private val APP_PREFERENCES = "APP_PREFERENCES"
    private val TOKEN = "TOKEN"
    val sharedPrefs = SharedPrefs(context = context, APP_PREFERENCES)

    lateinit var apiInterface : ApiHelper

    fun getClubs() = liveData(Dispatchers.IO) {
        emit(Response.loading(data = null))
        try {
            emit(Response.success(data = apiInterface.getUsers()))
        } catch (exception: Exception) {
            emit(Response.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun sendLoginData() : LiveData<Response<LoginResponse>> {
        val loginData = MutableLiveData<LoginModel>()
        if (phoneNumber.value == null)
            return MutableLiveData(Response.error(data = null, message = "Номер телефона не введён"))
        if (club.value == null)
            return MutableLiveData(Response.error(data = null, message = "Клуб не введён"))

        loginData.value = LoginModel(phone_number = "+7" + phoneNumber.value, club_id = club.value!!.id_name)

        return liveData(Dispatchers.IO) {
            emit(Response.loading(data = null))
            try {
                emit(Response.success(data = apiInterface.sendLoginData(loginData.value!!)))
            } catch (exception: Exception) {
                emit(Response.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }
    }

    fun sendConfirmCode() : LiveData<Response<ConfirmCodeResponse>> {
        val confirmData = ConfirmCodeModel(confirm_key = confirmKey.value!!, phone_number = phoneNumber.value!!, club_id = club.value!!.id_name)

        return liveData(Dispatchers.IO) {
            emit(Response.loading(data = null))
            try {
                emit(Response.success(data = apiInterface.sendConfirmData(confirmData)))
            } catch (exception: Exception) {
                emit(Response.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }
    }

    fun saveToken(token : String) {
        sharedPrefs.setPref(TOKEN, token)
    }

    private fun getToken() : String {
        return sharedPrefs.getPref<String>(TOKEN) ?: "0"
    }


}