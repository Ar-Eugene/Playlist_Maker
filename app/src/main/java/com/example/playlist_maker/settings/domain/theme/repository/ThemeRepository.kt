package com.example.playlist_maker.settings.domain.theme.repository

interface ThemeRepository {
    fun isDarkTheme(): Boolean
    fun setDarkTheme(isDark: Boolean)
}