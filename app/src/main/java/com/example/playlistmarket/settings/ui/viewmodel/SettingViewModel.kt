package com.example.playlistmarket.settings.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmarket.settings.domain.api.SettingsInteractor
import com.example.playlistmarket.settings.domain.models.ThemeSettings
import com.example.playlistmarket.sharing.domain.api.SharingInteractor


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

}