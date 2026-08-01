package com.example.playlistmarket.domain.impls

import com.example.playlistmarket.domain.api.OnChangesRegisterable
import com.example.playlistmarket.domain.api.SettingsInteractor
import com.example.playlistmarket.domain.api.SettingsRepository

class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository
): SettingsInteractor, OnChangesRegisterable {
    override fun isDarkThemeEnabled(): Boolean {
        return settingsRepository.getDarkThemeEnabledValue()
    }

    override fun setDarkThemeEnabledValue(value: Boolean) {
        settingsRepository.setDarkThemeEnabledValue(value)
    }

    override fun registerOnChanges(listener: OnChangesRegisterable.Listener) {
        settingsRepository.registerOnChanges(listener)
    }

    override fun unregisterOnChanges(listener: OnChangesRegisterable.Listener) {
        settingsRepository.unregisterOnChanges(listener)
    }
}