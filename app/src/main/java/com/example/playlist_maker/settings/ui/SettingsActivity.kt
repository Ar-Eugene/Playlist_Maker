package com.example.playlist_maker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlist_maker.creator.Creator
import com.example.playlist_maker.databinding.ActivitySettingsBinding
import com.example.playlist_maker.settings.ui.view_model.SettingsViewModel
import com.example.playlist_maker.settings.ui.view_model.SettingsViewModelFactory
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var mySwitch: SwitchMaterial
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val themeInteractor = Creator.provideThemeInteractor()
        val factory = SettingsViewModelFactory(themeInteractor)
        viewModel = ViewModelProvider(this, factory).get(SettingsViewModel::class.java)

        mySwitch = binding.mySwitch
        val backArrowplayButton = binding.backArrow
        backArrowplayButton.setOnClickListener { finish() }

        val shareAppImageView = binding.share
        shareAppImageView.setOnClickListener {
            startActivity(viewModel.getShareIntent(this))
        }

        val writeToSupportImageView = binding.writeToSupport
        writeToSupportImageView.setOnClickListener {
            startActivity(viewModel.getEmailIntent(this))
        }

        val agreementImageView = binding.agreementLayout
        agreementImageView.setOnClickListener {
            startActivity(viewModel.getAgreementIntent(this))
        }

        initializationSwitch()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.isDarkThemeLiveData.observe(this) { isDarkTheme ->
            mySwitch.isChecked = isDarkTheme
        }
    }

    private fun initializationSwitch() {
        binding.mySwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleTheme(isChecked)
        }
    }
}

