package com.goodgame.goodgameapp.sharedprefs

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs (val context: Context, val path: String) {

    fun checkPrefExists(pref: String) : Boolean {
        val sharedPrefs : SharedPreferences = context.getSharedPreferences(path, Context.MODE_PRIVATE)
        return sharedPrefs.contains(pref)
    }

    inline fun <reified T> getPref(pref: String) : T? {
        val sharedPrefs : SharedPreferences = context.getSharedPreferences(path, Context.MODE_PRIVATE)
        when (T::class) {
            String::class -> return sharedPrefs.getString(pref, "") as T
            Int::class -> return sharedPrefs.getInt(pref, 0) as T
            Boolean::class -> return sharedPrefs.getBoolean(pref, false) as T
            Float::class -> return sharedPrefs.getFloat(pref, 0f) as T
            Long::class -> return sharedPrefs.getLong(pref, 0) as T
            else -> return null
        }
    }

    fun setPref(pref: String, data: Any)  {
        val sharedPrefs : SharedPreferences = context.getSharedPreferences(path, Context.MODE_PRIVATE)
        when (data) {
            is String -> sharedPrefs.edit().putString(pref, data).apply()
            is Int -> sharedPrefs.edit().putInt(pref, data).apply()
            is Boolean -> sharedPrefs.edit().putBoolean(pref, data).apply()
            is Float -> sharedPrefs.edit().putFloat(pref, data).apply()
            is Long -> sharedPrefs.edit().putLong(pref, data).apply()
        }
    }
}