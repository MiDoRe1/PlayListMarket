package com.example.playlistmarket

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmarket.SharedPreferencesPack.AppSettingsPreferencesWorker
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsToolBar: MaterialToolbar
    private lateinit var buttonShareApp: MaterialTextView
    private lateinit var buttonWriteToSupport: MaterialTextView
    private lateinit var buttonUserAgreement: MaterialTextView
    private lateinit var switchBlackTheme: SwitchMaterial

    private val themeChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { pref, key ->
            if (key == AppSettingsPreferencesWorker.KEY_THEME) {
                if (switchBlackTheme.isChecked != AppSettingsPreferencesWorker.isDarkThemeEnabled) {
                    switchBlackTheme.isChecked = AppSettingsPreferencesWorker.isDarkThemeEnabled
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.updatePadding(top = statusBar.top)
            insets
        }

        initSettingsToolBat()
        initButtonShareApp()
        initButtonWriteToSupport()
        initButtonUserAgreement()
        initSwitchBlackTheme()

    }

    private fun initSettingsToolBat() {
        settingsToolBar = findViewById(R.id.header_toolbar)
        settingsToolBar.setNavigationOnClickListener {
            finish()
        }


    }

    private fun initButtonShareApp() {
        buttonShareApp = findViewById(R.id.buttonShareApp)
        buttonShareApp.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.yandex_android_developer_course)
            )
            startActivity(shareIntent)
        }
    }

    private fun initButtonUserAgreement() {
        buttonUserAgreement = findViewById(R.id.buttonUserAgreement)
        buttonUserAgreement.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW)
            browserIntent.data = Uri.parse(getString(R.string.user_agreement_url))
            startActivity(browserIntent)
        }
    }

    private fun initButtonWriteToSupport() {
        buttonWriteToSupport = findViewById(R.id.buttonWriteToSupport)
        buttonWriteToSupport.setOnClickListener {
            val mailIntent = Intent(Intent.ACTION_SENDTO)
            mailIntent.data = Uri.parse("mailto:")
            mailIntent.putExtra(
                Intent.EXTRA_EMAIL, arrayOf(
                    getString(R.string.work_mail)
                )
            )
            mailIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.subject_for_email_to_support)
            )
            mailIntent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.text_for_email_to_support)
            )
            startActivity(mailIntent)
        }
    }

    private fun initSwitchBlackTheme() {
        switchBlackTheme = findViewById(R.id.switchBlackTheme)
        switchBlackTheme.isChecked = AppSettingsPreferencesWorker.isDarkThemeEnabled
        switchBlackTheme.setOnCheckedChangeListener { _, checked ->
            AppSettingsPreferencesWorker.isDarkThemeEnabled = checked
        }
    }

    override fun onStart() {
        super.onStart()
        AppSettingsPreferencesWorker.registerListener(themeChangeListener)
    }

    override fun onStop() {
        super.onStop()
        AppSettingsPreferencesWorker.unregisterListener(themeChangeListener)
    }
}