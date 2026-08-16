package com.example.playlistmarket.settings.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmarket.R
import com.example.playlistmarket.creator.Creator
import com.example.playlistmarket.databinding.ActivitySettingsBinding
import com.example.playlistmarket.settings.ui.viewmodel.SettingViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingViewModel
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.updatePadding(top = statusBar.top)
            insets
        }

        viewModel = ViewModelProvider(
            this,
            SettingViewModel.getFactory()
        ).get(SettingViewModel::class.java)

        viewModel.observeIsDarkThemeEnabled().observe(this) {
            binding.switchBlackTheme.isChecked = it
            switchTheme(it)
        }


        initSettingsToolBat()
        initButtonShareApp()
        initButtonWriteToSupport()
        initButtonUserAgreement()
        initSwitchBlackTheme()

    }

    private fun initSettingsToolBat() {
        binding.headerToolbar.setNavigationOnClickListener {
            finish()
        }


    }

    private fun initButtonShareApp() {
        binding.buttonShareApp.setOnClickListener {
           viewModel.shareApp()
        }
    }

    private fun initButtonUserAgreement() {
        binding.buttonUserAgreement.setOnClickListener {
           viewModel.openTerms()
        }
    }

    private fun initButtonWriteToSupport() {
        binding.buttonWriteToSupport.setOnClickListener {
            viewModel.writeToSupport()
        }
    }

    private fun initSwitchBlackTheme() {
                binding.switchBlackTheme.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(checked)
        }
    }

    private fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }
}