package com.example.playlistmarket

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmarket.SharedPreferencesPack.AppSettingsPreferencesWorker
import com.example.playlistmarket.SharedPreferencesPack.SearchHistoryPreferencesWorker

class PlayListMarketApp : Application() {

    private val settingsAppChangeListener = SharedPreferences.OnSharedPreferenceChangeListener {
        pref, key ->
        if (key == AppSettingsPreferencesWorker.KEY_THEME) {
            switchTheme(AppSettingsPreferencesWorker.isDarkThemeEnabled)
        }
    }

    override fun onCreate() {
        super.onCreate()
        AppSettingsPreferencesWorker.init(this)
        SearchHistoryPreferencesWorker.init(this)
        switchTheme(AppSettingsPreferencesWorker.isDarkThemeEnabled)
        AppSettingsPreferencesWorker.registerListener(settingsAppChangeListener)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}