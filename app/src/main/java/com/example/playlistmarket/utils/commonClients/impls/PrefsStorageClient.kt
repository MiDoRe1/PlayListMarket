package com.example.playlistmarket.utils.commonClients.impls

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmarket.utils.commonClients.LocalStorageClient
import com.google.gson.Gson
import java.lang.reflect.Type

class PrefsStorageClient<T>(
    private val context: Context,
    private val prefName: String,
    private val key: String,
    private val type: Type
) : LocalStorageClient<T> {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        prefName,
        Context.MODE_PRIVATE
    )

    private val gson = Gson()


    override fun getData(): T? {
        val dataJson = prefs.getString(key, null)
        if (dataJson == null) {
            return null
        } else {
            return gson.fromJson(dataJson, type)
        }


    }

    override fun saveData(dto: T) {
        prefs.edit().putString(key, gson.toJson(dto, type)).apply()
    }

    companion object {
        public final val SEARCH_HISTORY_PREF_NAME: String = "search_history"
        public final val SEARCH_HISTORY_KEY: String = "search_history_list"

        public final val SETTINGS_PREF_NAME = "app_settings"
        public final val SETTINGS_THEME_KEY = "current_theme"
    }

}