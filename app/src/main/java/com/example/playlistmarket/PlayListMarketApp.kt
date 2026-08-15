package com.example.playlistmarket

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmarket.creator.Creator
import com.example.playlistmarket.settings.domain.api.SettingsInteractor


class PlayListMarketApp : Application() {

    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate() {
        super.onCreate()
        Creator.init(this)
        settingsInteractor = Creator.provideSettingsInteractor()
        settingsInteractor.getThemeSettings {
            themeSettings, message ->
            switchTheme(themeSettings?.isDarkTheme ?: true)
        }
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