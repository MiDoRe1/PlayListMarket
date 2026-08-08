package com.example.playlistmarket.domain.api

interface SettingsInteractor: OnChangesRegisterable {
    fun isDarkThemeEnabled(): Boolean

    fun setDarkThemeEnabledValue(value: Boolean)

}