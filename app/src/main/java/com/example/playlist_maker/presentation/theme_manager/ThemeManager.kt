package com.example.playlist_maker.presentation.theme_manager

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlist_maker.Creator
import com.example.playlist_maker.domain.api.ThemeRepository

object ThemeManager {
    private val preferencesRepository: ThemeRepository by lazy {
        Creator.providePreferencesRepository()
    }
    fun isDarkTheme(): Boolean {
        return preferencesRepository.isDarkTheme()
    }
    fun applyTheme() {
        val isDarkTheme = isDarkTheme()
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
    fun switchTheme(darkThemeEnabled: Boolean) {
        preferencesRepository.setDarkTheme(darkThemeEnabled)
        applyTheme()
    }
}
