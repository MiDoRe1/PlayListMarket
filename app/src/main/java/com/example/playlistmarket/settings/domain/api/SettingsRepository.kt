package com.example.playlistmarket.settings.domain.api



import com.example.playlistmarket.settings.domain.models.StringSettingParameter
import com.example.playlistmarket.settings.domain.models.ThemeSettings
import com.example.playlistmarket.utils.Resource

interface SettingsRepository {
    fun getThemeSettings(): Resource<ThemeSettings?>
    fun updateThemeSettings(settings: ThemeSettings)

    fun getStringSettingParameter(name: String): StringSettingParameter

    companion object {
        val USER_AGREEMENT_URL = "user_agreement_url"
        val WORK_MAIL = "work_mail"
        val SUBJECT_FOR_SUPPORT = "subject_for_email_to_support"
        val TEXT_FOR_SUPPORT = "text_for_email_to_support"
        val DEVELOPER_COURCE = "yandex_android_developer_course"

    }
}