package com.example.playlist_maker.settings.domain.theme.interactor

import com.example.playlist_maker.common.ThemeInteractor
import com.example.playlist_maker.common.ThemeRepository

class ThemeInteractorImpl(private val themeRepository: ThemeRepository) : ThemeInteractor {
    override fun isDarkTheme(): Boolean {
        return themeRepository.isDarkTheme()
    }

    override fun setDarkTheme(isDark: Boolean) {
        themeRepository.setDarkTheme(isDark)
    }
}