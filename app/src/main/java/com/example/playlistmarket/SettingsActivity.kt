package com.example.playlistmarket

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.updatePadding(top = statusBar.top)
            insets
        }

        val settingsToolBar = findViewById<MaterialToolbar>(R.id.header_toolbar)
        settingsToolBar.setNavigationOnClickListener {
            finish()
        }

        val buttonShareApp = findViewById<MaterialTextView>(R.id.buttonShareApp)
        buttonShareApp.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.yandex_andriod_developer_course)
            )
            startActivity(shareIntent)
        }

        val buttonWriteToSupport = findViewById<MaterialTextView>(R.id.buttonWriteToSupport)
        buttonWriteToSupport.setOnClickListener {
            val mailIntent = Intent(Intent.ACTION_SENDTO)
            mailIntent.data = Uri.parse("mailto:")
            mailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("myWorkMail@mail.com"))
            mailIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.subject_for_email_to_support))
            mailIntent.putExtra(Intent.EXTRA_TEXT,
                R.string.text_for_email_to_support)
            startActivity(mailIntent)
        }

        val buttonUserAgreement = findViewById<MaterialTextView>(R.id.buttonUserAgreement)
        buttonUserAgreement.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW)
            browserIntent.data = Uri.parse("https://yandex.ru/legal/practicum_offer/ru/")
            startActivity(browserIntent)
        }
    }
}