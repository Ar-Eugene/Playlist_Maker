package com.example.playlist_maker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlist_maker.databinding.ActivitySettingsBinding
import com.example.playlist_maker.settings.ui.view_model.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var mySwitch: SwitchMaterial
    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mySwitch = binding.mySwitch
        val backArrowplayButton = binding.backArrow
        backArrowplayButton.setOnClickListener { finish() }

        val shareAppImageView = binding.share
        shareAppImageView.setOnClickListener {
            startActivity(settingsViewModel.getShareIntent(this))
        }

        val writeToSupportImageView = binding.writeToSupport
        writeToSupportImageView.setOnClickListener {
            startActivity(settingsViewModel.getEmailIntent(this))
        }

        val agreementImageView = binding.agreementLayout
        agreementImageView.setOnClickListener {
            startActivity(settingsViewModel.getAgreementIntent(this))
        }

        initializationSwitch()
        observeViewModel()
    }

    private fun observeViewModel() {
        settingsViewModel.isDarkThemeLiveData.observe(this) { isDarkTheme ->
            mySwitch.isChecked = isDarkTheme
        }
    }

    private fun initializationSwitch() {
        binding.mySwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.toggleTheme(isChecked)
        }
    }
}

