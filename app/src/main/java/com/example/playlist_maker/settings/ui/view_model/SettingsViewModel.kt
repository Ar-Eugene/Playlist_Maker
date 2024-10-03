package com.example.playlist_maker.settings.ui.view_model

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker.R
import com.example.playlist_maker.settings.domain.theme.repository.ThemeRepository

class SettingsViewModel(private val themeRepository: ThemeRepository) : ViewModel() {

    private val _darkTheme = MutableLiveData<Boolean>()
    val darkTheme: LiveData<Boolean> get() = _darkTheme

    init {
        _darkTheme.value = themeRepository.isDarkTheme()
    }

    fun toggleTheme(isDark: Boolean) {
        themeRepository.setDarkTheme(isDark)
        _darkTheme.value = isDark
    }

    fun getShareIntent(context: Context): Intent {
        return Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            type = context.getString(R.string.text_plain)
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.URL_Android_developer))
        }, context.getString(R.string.share_app))
    }

    fun getEmailIntent(context: Context): Intent {
        return Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(context.getString(R.string.mailto))
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.my_email)))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.letter_subject))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.thanksToTheDevelopers))
        }
    }

    fun getAgreementIntent(context: Context): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.URL_agreement)))

    }

}