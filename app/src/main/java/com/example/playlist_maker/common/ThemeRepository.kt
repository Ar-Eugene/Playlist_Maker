package com.example.playlist_maker.common

interface ThemeRepository {
    fun isDarkTheme(): Boolean
    fun setDarkTheme(isDark: Boolean)
    fun applyTheme()
}