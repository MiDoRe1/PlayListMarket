package com.example.playlistmarket.settings.data


import android.content.Context
import android.content.res.Configuration
import com.example.playlistmarket.settings.domain.api.SettingsRepository
import com.example.playlistmarket.settings.domain.models.StringSettingParameter
import com.example.playlistmarket.utils.commonClients.LocalStorageClient
import com.example.playlistmarket.settings.domain.models.ThemeSettings
import com.example.playlistmarket.utils.Resource

class SettingsRepositoryImpl(
    private val localStorageClient: LocalStorageClient<ThemeSettings>,
    private val context: Context,
    private val resourceAndroidClient: ResourceAndroidClient = ResourceAndroidClient(context)
): SettingsRepository {

    override fun getThemeSettings(): Resource<ThemeSettings?> {
        var themeSettings = localStorageClient.getData()
        if (themeSettings == null) {
            themeSettings = ThemeSettings(isDarkTheme = getSystemIsDarkTheme(context))
            updateThemeSettings(themeSettings)
        }

        return Resource.Success(themeSettings)
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        localStorageClient.saveData(settings)
    }

    override fun getStringSettingParameter(name: String): StringSettingParameter {
        return StringSettingParameter(
            name,
            resourceAndroidClient.getResourceValue(name)
        )
    }

    private fun getSystemIsDarkTheme(context: Context): Boolean {
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isSystemDark = currentNightMode == Configuration.UI_MODE_NIGHT_YES
        return isSystemDark
    }

}