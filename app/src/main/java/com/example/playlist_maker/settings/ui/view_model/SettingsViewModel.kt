package com.example.playlist_maker.settings.ui.view_model

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker.R
import com.example.playlist_maker.common.ThemeInteractor

class SettingsViewModel(private val themeInteractor: ThemeInteractor) : ViewModel() {

    private val _isDarkThemeLiveData = MutableLiveData<Boolean>()
    val isDarkThemeLiveData: LiveData<Boolean> get() = _isDarkThemeLiveData

    init {
        _isDarkThemeLiveData.value = themeInteractor.isDarkTheme()
    }

    fun toggleTheme(isDark: Boolean) {
        themeInteractor.setDarkTheme(isDark)
        _isDarkThemeLiveData.value = isDark
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
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