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
        val themeRepository =
            Creator.providePreferencesRepository() // Получаем репозиторий через Creator

        val factory = SettingsViewModelFactory(themeRepository)
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

        mySwitch.isChecked = ThemeManager.isDarkTheme()

        viewModel.darkTheme.observe(this) { isDark ->
            mySwitch.isChecked
            ThemeManager.applyTheme()// Применяем тему при изменении состояния
        }
    }

    private fun initializationSwitch() {
        mySwitch.setOnCheckedChangeListener { _, isChecked ->
            //Это предотвратит ненужное переключение темы, если текущее состояние переключателя уже соответствует текущей теме.
            if (isChecked != ThemeManager.isDarkTheme()) {
                viewModel.toggleTheme(isChecked)
                recreate()
            }
        }
    }
}

