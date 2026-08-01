package com.example.playlistmarket.domain.api

interface SettingsRepository: OnChangesRegisterable {
    fun getDarkThemeEnabledValue(): Boolean
    fun setDarkThemeEnabledValue(value: Boolean)

}