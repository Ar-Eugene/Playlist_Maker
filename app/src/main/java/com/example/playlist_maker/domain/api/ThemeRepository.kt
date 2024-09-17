package com.example.playlist_maker.domain.api

interface ThemeRepository {
    fun isDarkTheme(): Boolean
    fun setDarkTheme(isDark: Boolean)
}