package com.example.playlist_maker.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.ActivitySettingsBinding
import com.example.playlist_maker.presentation.theme_manager.ThemeManager
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var mySwitch: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mySwitch = binding.mySwitch
        val backArrowplayButton = binding.backArrow
        backArrowplayButton.setOnClickListener {
            finish()
        }
        val shareAppImageView = binding.share
        shareAppImageView.setOnClickListener {
            startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = getString(R.string.text_plain)
                putExtra(Intent.EXTRA_TEXT, getString(R.string.URL_Android_developer))
            }, getString(R.string.share_app)))
        }
        val writeToSupportImageView = binding.writeToSupport
        writeToSupportImageView.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(getString(R.string.mailto))
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.letter_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.thanksToTheDevelopers))
            }
            startActivity(emailIntent)
        }
        val agreementImageView = binding.agreementLayout
        agreementImageView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.URL_agreement)))
            startActivity(browserIntent)
        }
        initializationSwitch()
        mySwitch.isChecked = ThemeManager.isDarkTheme()
    }

    private fun initializationSwitch() {
        mySwitch.setOnCheckedChangeListener { _, isChecked ->
            //Это предотвратит ненужное переключение темы, если текущее состояние переключателя уже соответствует текущей теме.
            if (isChecked != ThemeManager.isDarkTheme()) {
                ThemeManager.switchTheme(isChecked)
                recreate()
            }
        }
    }
}

