package com.example.playlistmarket.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmarket.R
import com.example.playlistmarket.sharing.domain.api.ExternalNavigator
import com.example.playlistmarket.sharing.domain.models.EmailData

class ExternalNavigatorImpl(
    private val context: Context
): ExternalNavigator {
    override fun shareLink(url: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            url
        )
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    override fun openLink(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW)
        browserIntent.data = Uri.parse(url)
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(browserIntent)
    }

    override fun openEmail(emailData: EmailData) {
        val mailIntent = Intent(Intent.ACTION_SENDTO)
        mailIntent.data = Uri.parse("mailto:")
        mailIntent.putExtra(
            Intent.EXTRA_EMAIL, arrayOf(
                emailData.mails
            )
        )
        mailIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            emailData.subject
        )
        mailIntent.putExtra(
            Intent.EXTRA_TEXT,
            emailData.text
        )
        mailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(mailIntent)
    }

}