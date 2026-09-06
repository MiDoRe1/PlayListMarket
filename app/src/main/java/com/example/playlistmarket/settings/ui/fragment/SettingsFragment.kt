package com.example.playlistmarket.settings.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmarket.core.ui.BindingFragment
import com.example.playlistmarket.databinding.FragmentSettingsBinding
import com.example.playlistmarket.settings.ui.viewmodel.SettingViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment : BindingFragment<FragmentSettingsBinding>() {

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    private val viewModel: SettingViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeIsDarkThemeEnabled().observe(viewLifecycleOwner) {
            binding.switchBlackTheme.isChecked = it
            switchTheme(it)
        }

        initButtonShareApp()
        initButtonWriteToSupport()
        initButtonUserAgreement()
        initSwitchBlackTheme()
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

}