package com.goodgame.goodgameapp.download

import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.io.*
import java.util.concurrent.Executors


interface DownloadListener {
    fun onStart() // Download Start
    fun onProgress(progress: Int) // Download progress
    fun onFinish(path: String?) // Download complete
    fun onFail(errorInfo: String?) // Download failed
}

interface DownloadService {
    @Streaming
    @GET
    fun download(@Url url: String?): Call<ResponseBody?>?
}

object DownloadUtil {
    fun download(url: String, path: String, downloadListener: DownloadListener) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.xxx.com") // Obtained through thread pool 1 Threads, specifying callback Run in child threads.
            .callbackExecutor(Executors.newSingleThreadExecutor())
            .build()
        val service = retrofit.create(DownloadService::class.java)
        val call = service.download(url)
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                // Will Response Write to slave disk, see the following analysis for details
                // Note that this method runs in a child thread
                writeResponseToDisk(path, response, downloadListener)
            }

            override fun onFailure(call: Call<ResponseBody?>, throwable: Throwable) {
                downloadListener.onFail(" Network Error ~ ")
            }
        })
    }
}

private fun writeResponseToDisk(
    path: String,
    response: Response<ResponseBody?>,
    downloadListener: DownloadListener
) {
    // From response Get the input stream and the total size
    writeFileFromIS(
        File(path),
        response.body()!!.byteStream(),
        response.body()!!.contentLength(),
        downloadListener
    )
}

private const val sBufferSize = 8192

// Write the input stream to a file
private fun writeFileFromIS(
    file: File,
    inputStream: InputStream,
    totalLength: Long,
    downloadListener: DownloadListener
) {
    // Start downloading
    downloadListener.onStart()

    // Create a file
    if (!file.exists()) {
        if (!file.parentFile?.exists()!!) file.parentFile?.mkdir()
        try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            downloadListener.onFail("createNewFile IOException")
        }
    }
    var os: OutputStream? = null
    var currentLength: Long = 0
    try {
        os = BufferedOutputStream(FileOutputStream(file))
        val data = ByteArray(sBufferSize)
        var len: Int
        while (inputStream.read(data, 0, sBufferSize).also { len = it } != -1) {
            os.write(data, 0, len)
            currentLength += len.toLong()
            // Calculate the current download progress
            downloadListener.onProgress((100 * currentLength / totalLength).toInt())
        }
        // Download is complete and return to the saved file path
        downloadListener.onFinish(file.absolutePath)
    } catch (e: IOException) {
        e.printStackTrace()
        downloadListener.onFail("IOException")
    } finally {
        try {
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            os?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}





