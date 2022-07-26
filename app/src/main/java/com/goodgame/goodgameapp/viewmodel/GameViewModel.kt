package com.goodgame.goodgameapp.viewmodel

import android.annotation.SuppressLint
import android.app.Application
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

class GameViewModel (application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val APP_PREFERENCES = "APP_PREFERENCES"
    private val TOKEN = "TOKEN"
    private val INTRO = "INTRO"

    private val LAST_LVL            = "LAST_LVL"
    private val LAST_EXP            = "LAST_EXP"
    private val LAST_EXP_TO_NXT_LVL = "LAST_EXP_TO_NXT_LVL"
    private val LAST_CLASS          = "LAST_CLASS"
    private val EXPED_COMPLETED     = "EXPED_COMPLETED"

    private val sharedPrefs = SharedPrefs(context = context, APP_PREFERENCES)

    lateinit var apiInterface : ApiHelper

    var username : String? = null

    var isHeroInfoLoaded = MutableLiveData(false)
    val heroInfo = MutableLiveData(HeroInfo(
        hasHero = false,
        heroClass = "",
        expeditions = emptyList(),
        total_progress = 0,
        level = 0,
        lvl_exp = 0,
        next_lvl_need = 0,
        has_expeditions = 0,
    ))

    private var appToken : String = "0"
        get() {
            if (field == "0")
                field = getToken()
            return field
        }

    private fun getToken () : String {
        return sharedPrefs.getPref<String>(TOKEN) ?: "0"
    }

    fun checkToken() : LiveData<Response<TokenConfirmResponse>> {
        val bufResource = Response(
            status = Status.SUCCESS,
            data = TokenConfirmResponse(status = false, username = null, hasHero = null),
            message = null)
        if (!sharedPrefs.checkPrefExists(TOKEN)) return liveData {emit(bufResource)}
        val token = getToken()
        return liveData(Dispatchers.IO) {
            emit(Response.loading(data = null))
            try {
                emit(Response.success(data = apiInterface.sendToken(token = token)))
            } catch (exception: Exception) {
                emit(Response.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }
    }

    fun createHero(hero_type: String) : LiveData<Response<HeroCreateResponse>> {
        return liveData(Dispatchers.IO) {
            emit(Response.loading(data = null))
            try {
                emit(Response.success(data = apiInterface.heroCreate(getToken(), hero_type)))
            } catch (exception: Exception) {
                emit(Response.error(data = null, message = exception.message.toString()))
            }
        }
    }

    private fun getLastHeroData() {
        heroInfo.value?.level                = sharedPrefs.getPref<Int>(LAST_LVL) ?: 0
        heroInfo.value?.lvl_exp              = sharedPrefs.getPref<Int>(LAST_EXP) ?: 0
        heroInfo.value?.next_lvl_need        = sharedPrefs.getPref<Int>(LAST_EXP_TO_NXT_LVL) ?: 0
        heroInfo.value?.heroClass            = sharedPrefs.getPref<String>(LAST_CLASS) ?: ""
        heroInfo.value?.total_progress = sharedPrefs.getPref<Int>(EXPED_COMPLETED) ?: 0
    }

    private fun saveLastHeroData() {
        sharedPrefs.setPref(LAST_LVL, heroInfo.value?.level ?: 0)
        sharedPrefs.setPref(LAST_EXP, heroInfo.value?.lvl_exp ?: 0)
        sharedPrefs.setPref(LAST_EXP_TO_NXT_LVL, heroInfo.value?.next_lvl_need ?: 0)
        sharedPrefs.setPref(LAST_CLASS, heroInfo.value?.heroClass ?: "")
        sharedPrefs.setPref(EXPED_COMPLETED, heroInfo.value?.total_progress ?: 0)
    }

    fun getHeroInfo() : LiveData<Response<Nothing?>> {
        return liveData(Dispatchers.IO) {
            emit(Response.loading(data = null))
            try {
                val bufResponse = Response.success(data = apiInterface.getHeroInfo(token = getToken()))
                val bufHeroInfo = HeroInfo(
                    level                = bufResponse.data!!.level,
                    lvl_exp              = bufResponse.data.lvl_exp,
                    next_lvl_need        = bufResponse.data.next_lvl_need,
                    has_expeditions      = bufResponse.data.has_expeditions,
                    heroClass            = bufResponse.data.type,
                    total_progress       = bufResponse.data.planet_status,
                    expeditions          = bufResponse.data.expeditions,
                    hasHero              = heroInfo.value?.hasHero ?: false,
                )
                heroInfo.postValue(bufHeroInfo)
                saveLastHeroData()
                isHeroInfoLoaded.postValue(true)
                emit(Response.success(data = null))
            } catch (exception: Exception) {
                emit(Response.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }
    }

    fun exitAccount () {
        sharedPrefs.setPref(TOKEN, "0")
    }

}