package com.example.playlistmarket.settings.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmarket.databinding.ActivitySettingsBinding
import com.example.playlistmarket.settings.ui.viewmodel.SettingViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingViewModel by viewModel()
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