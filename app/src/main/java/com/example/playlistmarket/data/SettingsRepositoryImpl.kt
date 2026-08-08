package com.example.playlistmarket.data

import com.example.playlistmarket.domain.api.OnChangesRegisterable
import com.example.playlistmarket.domain.api.SettingsRepository

class SettingsRepositoryImpl(
    private val localStorageWorker: LocalStorageWorker<Boolean>
): SettingsRepository {
    override fun getDarkThemeEnabledValue(): Boolean {
        return localStorageWorker.getData()
    }

    override fun setDarkThemeEnabledValue(value: Boolean) {
        localStorageWorker.saveData(value)
    }

    override fun registerOnChanges(listener: OnChangesRegisterable.Listener) {
        localStorageWorker.registerOnChanges(listener)
    }

    override fun unregisterOnChanges(listener: OnChangesRegisterable.Listener) {
        localStorageWorker.unregisterOnChanges(listener)
    }
}