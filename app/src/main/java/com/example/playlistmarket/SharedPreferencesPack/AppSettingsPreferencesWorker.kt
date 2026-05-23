package com.example.playlistmarket.SharedPreferencesPack

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import kotlin.coroutines.Continuation

object AppSettingsPreferencesWorker {
    private const val PREF_NAME = "app_settings"
    const val KEY_THEME = "current_theme"
    const val DEFAULT_IS_DARK_THEME_ENABLED = false

    private lateinit var preferences : SharedPreferences
    private lateinit var appContext : Context

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        appContext = context.applicationContext
    }

    var isDarkThemeEnabled: Boolean
        get() {
            var value : Boolean
            if (preferences.contains(KEY_THEME)) {
                value = preferences.getBoolean(KEY_THEME, DEFAULT_IS_DARK_THEME_ENABLED)
            } else {
                val nightModeFlags = appContext.resources.configuration.uiMode and
                        Configuration.UI_MODE_NIGHT_MASK

                value = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
            }
            return value
        }
        set(value) = preferences.edit().putBoolean(KEY_THEME, value).apply()

    fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        preferences.unregisterOnSharedPreferenceChangeListener(listener)
    }
}