package com.example.playlist_maker.settings.ui

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlist_maker.creator.Creator
import com.example.playlist_maker.settings.domain.theme.repository.ThemeRepository

object ThemeManager {
    private val preferencesRepository: ThemeRepository by lazy {
        Creator.providePreferencesRepository()
    }

    fun applyTheme() {
        val isDarkTheme = preferencesRepository.isDarkTheme()
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
