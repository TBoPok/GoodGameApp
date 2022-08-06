package com.goodgame.goodgameapp.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.goodgame.goodgameapp.download.DownloadListener
import com.goodgame.goodgameapp.download.DownloadUtil.download
import com.goodgame.goodgameapp.models.*
import com.goodgame.goodgameapp.retrofit.ApiHelper
import com.goodgame.goodgameapp.retrofit.Response
import com.goodgame.goodgameapp.retrofit.Status
import com.goodgame.goodgameapp.sharedprefs.SharedPrefs
import kotlinx.coroutines.Dispatchers
import java.io.File


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
        stats_points = 0,
        stats = StatsModel(0,0,0,0),
        coins = 0,
    ))

    val loadingLiveData = MutableLiveData<Response<Int>>(Response.loading(data = 0))

    private var getToken : String = "0"
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
        return liveData(Dispatchers.Default) {
            emit(Response.loading(data = null))
            try {
                emit(Response.success(data = apiInterface.sendToken(token = token)))
            } catch (exception: Exception) {
                emit(Response.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }
    }

    fun createHero(hero_type: String) : LiveData<Response<HeroCreateResponse>> {
        return liveData(Dispatchers.Default) {
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
        return liveData(Dispatchers.Default) {
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
                    stats_points         = bufResponse.data.stats_points,
                    stats                = bufResponse.data.stats,
                    coins                = bufResponse.data.rsp
                )
                heroInfo.postValue(bufHeroInfo)
                isHeroInfoLoaded.postValue(true)
                emit(Response.success(data = null))
            } catch (exception: Exception) {
                emit(Response.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }
    }

    fun setHeroSkill(skill_type: String) : LiveData<Response<SkillResponse>> {
        return liveData(Dispatchers.Default) {
            emit(Response.loading(data = null))
            try {
                emit(Response.success(data = apiInterface.setHeroSkill(getToken, skill_type)))
            } catch (exception: Exception) {
                emit(Response.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }
    }

    fun getShopList() : LiveData<Response<List<ShopItem>>> {
        return liveData {
            emit(Response.loading(data = null))
            try {
                val shopList = apiInterface.getShopList().shop_items
                val coins = heroInfo.value?.coins ?: 0
                shopList.forEach {
                    if (heroInfo.value?.coins != null)
                        if (it.cost <= coins) {
                            it.isAvailable = true
                        }
                }
                emit(Response.success(data = shopList))
            } catch (exception: Exception) {
                emit(Response.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }
    }

    fun buyShopItem(shopItem: ShopItem) : LiveData<Response<ShopBuyResponse>> {
        return liveData(Dispatchers.Default) {
            emit(Response.loading(data = null))
            try {
                emit(Response.success(data = apiInterface.buyShopItem(getToken, shopItem.id)))
            } catch (exception: Exception) {
                emit(Response.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }
    }

    fun getRewardList() : LiveData<Response<List<Reward>>> {
        return liveData {
            emit(Response.loading(data = null))
            try {
                val rewardResponse = apiInterface.getRewards(getToken)
                if (rewardResponse.status)
                    emit(Response.success(data = rewardResponse.rewards))
                else
                    emit(Response.error(data = null, message = "Rewards response status false"))
            } catch (exception: Exception) {
                emit(Response.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }
    }

    fun getImage(url: String) {
        val uri = Uri.parse(url).path
        if (uri == null) {
            loadingLiveData.postValue(Response.error(data = null, message = "Error msg getImage: can't parse url"))
            Log.e("DOWNLOAD", "Error msg getImage: can't parse url")
            return
        }
        val fileName = File(uri).name
        val path = context.cacheDir.path + "/expeditionImages/$fileName"
        if (File(path).exists()) {
            Log.d("DOWNLOAD", "File exits")
            loadingLiveData.postValue(Response.success(data = 100))
            return
        }
        loadingLiveData.postValue(Response.loading(data = 0))
        try {
            download(url, path, object : DownloadListener {
                override fun onStart() {
                    loadingLiveData.postValue(Response.loading(data = 0))
                    Log.d("DOWNLOAD", "Start")
                }

                override fun onProgress(progress: Int) {
                    loadingLiveData.postValue(Response.loading(data = progress))
                    Log.d("DOWNLOAD", "Progress: $progress")
                }

                override fun onFinish(path: String?) {
                    loadingLiveData.postValue(Response.success(data = 100))
                    Log.d("DOWNLOAD", "Finish path: $path")
                }

                override fun onFail(errorInfo: String?) {
                    loadingLiveData.postValue(
                        Response.error(
                            data = null,
                            message = errorInfo ?: "Error msg getImage: no message"
                        )
                    )
                    Log.e("DOWNLOAD", "Error getImage msg: $errorInfo")
                }
            })
        } catch (exception: Exception) {
            Log.e("DOWNLOAD", "Error getImage msg: ${exception.message}")
        }
    }

    fun getExpedition() : LiveData<Response<Expedition>> {
        return liveData(Dispatchers.Default) {
            emit(Response.loading(data = null))
            try {
                emit(Response.success(data = apiInterface.getExpedition(getToken)))
            } catch (exception: Exception) {
                emit(Response.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }
    }

    fun getExpeditionResult(choice: String) : LiveData<Response<ExpeditionResult>> {
        return if (choice == "Do" || choice == "Run" )
            liveData(Dispatchers.Default) {
                emit(Response.loading(data = null))
                try {
                    emit(Response.success(data = apiInterface.getExpeditionResult(getToken, choice)))
                } catch (exception: Exception) {
                    emit(Response.error(data = null, message = exception.message ?: "Error Occurred!"))
                }
            }
        else
            liveData {emit(Response.error(data = null, message = "Wrong choice type"))}
    }

    fun exitAccount () {
        sharedPrefs.setPref(TOKEN, "0")
    }

}