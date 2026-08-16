package com.example.playlistmarket.sharing.domain.api

import com.example.playlistmarket.sharing.domain.models.EmailData

interface ExternalNavigator {
    fun shareLink(url: String)
    fun openLink(url: String)
    fun openEmail(emailData: EmailData)
}