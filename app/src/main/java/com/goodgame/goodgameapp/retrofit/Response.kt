package com.goodgame.goodgameapp.retrofit

enum class Status {
    SUCCESS,
    ERROR,
    LOADING,
}

data class Response<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): Response<T> = Response(status = Status.SUCCESS, data = data, message = null)

        fun <T> error(data: T?, message: String): Response<T> =
            Response(status = Status.ERROR, data = data, message = message)

        fun <T> loading(data: T?, message: String? = null): Response<T> = Response(status = Status.LOADING, data = data, message = message)

    }
}

data class LiveResponse<T>(var status: Status, var data: T?, var message: String?) {
    companion object {
        fun <T> success(data: T): LiveResponse<T> = LiveResponse(status = Status.SUCCESS, data = data, message = null)

        fun <T> error(data: T?, message: String): LiveResponse<T> =
            LiveResponse(status = Status.ERROR, data = data, message = message)

        fun <T> loading(data: T?, message: String? = null): LiveResponse<T> = LiveResponse(status = Status.LOADING, data = data, message = message)

    }
}