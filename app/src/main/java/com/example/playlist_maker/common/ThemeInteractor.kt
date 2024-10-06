package com.example.playlist_maker.common

interface ThemeInteractor {
    fun isDarkTheme(): Boolean
    fun setDarkTheme(isDark: Boolean)
    fun applyTheme()
}