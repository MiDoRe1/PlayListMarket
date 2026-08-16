package com.example.playlistmarket.settings.domain.api

import com.example.playlistmarket.settings.domain.models.StringSettingParameter
import com.example.playlistmarket.settings.domain.models.ThemeSettings


interface SettingsInteractor {
    fun getThemeSettings(consumerThemeSettings: ConsumerThemeSettings)
    fun updateThemeSettings(settings: ThemeSettings)

    fun interface ConsumerThemeSettings {
        fun consume(settings: ThemeSettings?, errorMessage: String?)
    }

    fun getStringSettingParameter(name: String): StringSettingParameter

}