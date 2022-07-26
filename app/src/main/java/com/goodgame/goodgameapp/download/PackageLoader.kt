package com.goodgame.goodgameapp.download

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.goodgame.goodgameapp.retrofit.LiveResponse
import com.goodgame.goodgameapp.retrofit.Response
import com.goodgame.goodgameapp.retrofit.Status
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.security.MessageDigest
import java.util.*
import kotlin.concurrent.thread
import kotlin.experimental.and

data class PackageModel(val name: String, val url: String, val md5: String)

class PackageLoader(
    private val context: Context,
    private val dataListRequest: () -> List<PackageModel>,
    private val dataRequest: (url : String) -> ByteArray,
            var responseStatus: MutableLiveData<LiveResponse<Int>>
        = MutableLiveData<LiveResponse<Int>>(LiveResponse.loading(data = 0, message = "Loading"))
    ) {

    fun Start() {
        thread() {
            // Получаем список с элементами пакета для скачивания и сохранения
            val listPackageResponse = getPackage()
            if (listPackageResponse == null) {
                responseError("Сервер не отвечает")
                return@thread
            }
            if (listPackageResponse.status == Status.ERROR) {
                responseError(listPackageResponse.message!!)
                return@thread
            }
            if (listPackageResponse.data == null) {
                responseError("Отстутствуют данные для скачивания")
                return@thread
            }
            responseLoadingUpdate(10)
            // Проверяем целостность кэша, получаем не загруженные и сломанные элементы
            val notLoadedPackage = checkPackage(listPackage = listPackageResponse.data)

            responseLoadingUpdate(20)
            var currentPercents = 20
            val percentForItem : Int = 80 / (notLoadedPackage.size + 1)
            // Загружаем недостающие элементы пакета
            notLoadedPackage.forEach() {
                // Загружаем
                val itemResponse = getItem(it.url)
                if (itemResponse == null) {
                    responseError("Сервер не отвечает")
                    return@thread
                }
                if (itemResponse.status == Status.ERROR) {
                    responseError(listPackageResponse.message!!)
                    return@thread
                }
                if (itemResponse.data == null) {
                    responseError("Объект для скачивания " + it.name + " отсуствует")
                    return@thread
                }
                // Сохраняем
                try {
                    val fos = FileOutputStream(context.cacheDir.path + "/" + it.name)
                    fos.write(itemResponse.data)
                    fos.close()
                }
                catch (ex : Exception) {
                    responseError(ex.message.toString())
                    return@thread
                }
                // Записываем в преференс
                saveMd5(it)
                // Обновим проценты
                currentPercents += percentForItem
                responseLoadingUpdate(currentPercents)
            }
            responseSuccess()
        }

    }

    private fun getPackage() : Response<List<PackageModel>>? {
        return try {
            Response.success(data = dataListRequest())
        } catch (exception: Exception) {
            Response.error(data = null, message = exception.message ?: "Error Occurred!")
        }
    }

    private fun getItem(url: String) : Response<ByteArray>? {
        return try {
            Response.success(data = dataRequest(url))
        } catch (exception: Exception) {
            Response.error(data = null, message = exception.message ?: "Error Occurred!")
        }
    }

    private fun checkPackage(listPackage: List<PackageModel>) : List<PackageModel> {

        val sharedPrefs : SharedPreferences = context.getSharedPreferences("PACKAGE_DB", Context.MODE_PRIVATE)
        fun getPrefsMd5(name : String) : String = sharedPrefs.getString(name,"")?.uppercase(Locale.getDefault()) ?: ""

        val notLoadedList = mutableListOf<PackageModel>()
        listPackage.forEach() {
            if (sharedPrefs.contains(it.name)) {
                if (getPrefsMd5(it.name) == getFileMd5(it.name)) {
                    return@forEach
                } else notLoadedList.add(it)
            }
            else notLoadedList.add(it)
        }
        return notLoadedList
    }

    private fun getFileMd5(fileName : String) : String {
        var returnVal = ""
        try {
            val input: InputStream = FileInputStream(File(context.cacheDir, fileName))
            val buffer = ByteArray(1024)
            val md5Hash: MessageDigest = MessageDigest.getInstance("MD5")
            var numRead = 0
            while (numRead != -1) {
                numRead = input.read(buffer)
                if (numRead > 0) {
                    md5Hash.update(buffer, 0, numRead)
                }
            }
            input.close()
            val md5Bytes: ByteArray = md5Hash.digest()
            for (i in md5Bytes.indices) {
                returnVal += Integer.toString((md5Bytes[i] and 0xff.toByte()) + 0x100, 16).substring(1)
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return returnVal.uppercase(Locale.getDefault())
    }

    private fun saveMd5(item : PackageModel) {
        val sharedPrefs : SharedPreferences = context.getSharedPreferences("PACKAGE_DB", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString(item.name, item.md5).apply()
    }

    private fun responseError(message: String) {
        responseStatus.postValue(LiveResponse.error(data = null, message = message))
    }

    private fun responseLoadingUpdate(percent: Int) {
        responseStatus.postValue(LiveResponse.loading(data = percent, message = "Loading"))
    }

    private fun responseSuccess() {
        responseStatus.postValue(LiveResponse.success(data = 100))
    }
}