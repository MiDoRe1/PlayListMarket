package com.example.playlistmarket.settings.domain.impl


import com.example.playlistmarket.settings.domain.api.SettingsInteractor
import com.example.playlistmarket.settings.domain.api.SettingsRepository
import com.example.playlistmarket.settings.domain.models.StringSettingParameter
import com.example.playlistmarket.settings.domain.models.ThemeSettings

import com.example.playlistmarket.utils.Resource

class SettingsInteractorImpl(
    val repository: SettingsRepository
): SettingsInteractor {

    override fun getThemeSettings(consumerThemeSettings: SettingsInteractor.ConsumerThemeSettings) {
        when (val res = repository.getThemeSettings()) {
            is Resource.Success -> {
                consumerThemeSettings.consume(res.data, null)
            }
            is Resource.Error -> {
                consumerThemeSettings.consume(res.data, res.message)
            }
        }

    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        repository.updateThemeSettings(settings)
    }

    override fun getStringSettingParameter(name: String): StringSettingParameter {
        return repository.getStringSettingParameter(name)
    }


}