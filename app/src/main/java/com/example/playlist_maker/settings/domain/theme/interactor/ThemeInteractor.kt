package com.example.playlist_maker.settings.domain.theme.interactor

import com.example.playlist_maker.settings.domain.theme.repository.ThemeRepository

class ThemeInteractor(private val themeRepository: ThemeRepository) {
    fun isDarkTheme(): Boolean {
        return themeRepository.isDarkTheme()
    }

    fun setDarkTheme(isDark: Boolean) {
        themeRepository.setDarkTheme(isDark)
    }
}