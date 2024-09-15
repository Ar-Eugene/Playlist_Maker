package com.example.playlist_maker.presentation.theme_manager

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlist_maker.constans.Constants

object ThemeManager {
    fun isDarkTheme(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERERNCES, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(Constants.SEARCH_KEY, false)
    }
    fun applyTheme(context: Context) {
        val isDarkTheme = isDarkTheme(context)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
    fun switchTheme(context: Context, darkThemeEnabled: Boolean) {
        val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERERNCES, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(Constants.SEARCH_KEY, darkThemeEnabled).apply()
        applyTheme(context)
    }
}
