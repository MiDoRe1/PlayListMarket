package com.example.playlistmarket.sharing.domain.impls

import com.example.playlistmarket.settings.domain.api.SettingsInteractor
import com.example.playlistmarket.settings.domain.api.SettingsRepository
import com.example.playlistmarket.sharing.domain.api.ExternalNavigator
import com.example.playlistmarket.sharing.domain.api.SharingInteractor
import com.example.playlistmarket.sharing.domain.models.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val settingsRepository: SettingsRepository
): SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return settingsRepository.getStringSettingParameter(
            SettingsRepository.DEVELOPER_COURCE
        ).value
    }

    private fun getSupportEmailData(): EmailData {
        val emails = listOf<String>(
            settingsRepository.getStringSettingParameter(SettingsRepository.WORK_MAIL).value
        )
        val subject = settingsRepository.getStringSettingParameter(
            SettingsRepository.SUBJECT_FOR_SUPPORT)
            .value
        val text = settingsRepository.getStringSettingParameter(SettingsRepository.TEXT_FOR_SUPPORT)
            .value
        return EmailData(emails, subject, text)
    }

    private fun getTermsLink(): String {
        return settingsRepository.getStringSettingParameter(
            SettingsRepository.USER_AGREEMENT_URL
        ).value
    }
}