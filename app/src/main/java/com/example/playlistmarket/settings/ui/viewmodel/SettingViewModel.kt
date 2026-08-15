package com.example.playlistmarket.settings.ui.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmarket.settings.domain.api.SettingsInteractor
import com.example.playlistmarket.settings.domain.models.ThemeSettings
import com.example.playlistmarket.sharing.domain.api.SharingInteractor
import com.example.playlistmarket.creator.Creator
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate


class SettingViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
): ViewModel() {

    private val isDarkThemeEnabledLiveData = MutableLiveData<Boolean>().apply {
        val liveData = this
        settingsInteractor.getThemeSettings {
            themeSetting, errorMessage ->
            liveData.postValue(themeSetting?.isDarkTheme)
        }
    }
    fun observeIsDarkThemeEnabled(): LiveData<Boolean> = isDarkThemeEnabledLiveData

    fun switchTheme(isDarkTheme: Boolean) {
        settingsInteractor.updateThemeSettings(
            ThemeSettings(isDarkTheme)
        )
        settingsInteractor.getThemeSettings(
            { themeSettings, message ->
                isDarkThemeEnabledLiveData.postValue(themeSettings?.isDarkTheme)
            }
        )

    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    fun writeToSupport() {
        sharingInteractor.openSupport()
    }





    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as Application)
                val currentNightMode = app.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                SettingViewModel(
                    Creator.provideSharingInteractor(),
                    Creator.provideSettingsInteractor()
                )
            }
        }


    }
}