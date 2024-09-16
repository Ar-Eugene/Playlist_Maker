package com.example.playlist_maker.presentation.theme_manager

interface PreferencesRepository {
    fun isDarkTheme(): Boolean
    fun setDarkTheme(isDark: Boolean)
}