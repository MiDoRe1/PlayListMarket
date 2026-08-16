package com.example.playlistmarket

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmarket.di.searchDataModule
import com.example.playlistmarket.di.searchDomainModule
import com.example.playlistmarket.di.settingDataModule
import com.example.playlistmarket.di.settingDomainModule
import com.example.playlistmarket.di.sharingDataModule
import com.example.playlistmarket.di.sharingDomainModule
import com.example.playlistmarket.di.utilsModule
import com.example.playlistmarket.di.viewModelModule
import com.example.playlistmarket.settings.domain.api.SettingsInteractor
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class PlayListMarketApp : Application() {

    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PlayListMarketApp)
            modules(searchDataModule, settingDataModule, sharingDataModule,
                searchDomainModule, settingDomainModule, sharingDomainModule,
                viewModelModule,
                utilsModule
                )
        }

        settingsInteractor = getKoin().get()
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