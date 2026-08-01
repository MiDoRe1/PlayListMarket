package com.example.playlistmarket.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmarket.Creator
import com.example.playlistmarket.data.localStorage.SharedPreferencesPack.TracksHistoryPreferencesWorker
import com.example.playlistmarket.domain.api.OnChangesRegisterable
import com.example.playlistmarket.domain.api.SettingsInteractor

class PlayListMarketApp : Application() {

    private lateinit var settingsInteractor: SettingsInteractor

    private lateinit var settingsAppChangeListener: OnChangesRegisterable.Listener



    override fun onCreate() {
        super.onCreate()
        Creator.init(this)
        settingsInteractor = Creator.provideSettingsInteractor()
        settingsAppChangeListener = OnChangesRegisterable.Listener {
            Handler(Looper.getMainLooper()).post {
                switchTheme(settingsInteractor.isDarkThemeEnabled())
            }
        }

        //AppSettingsPreferencesWorker.init(this)
        TracksHistoryPreferencesWorker.init(this)
        //switchTheme(AppSettingsPreferencesWorker.isDarkThemeEnabled)
        switchTheme(settingsInteractor.isDarkThemeEnabled())
        settingsInteractor.registerOnChanges(settingsAppChangeListener)
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