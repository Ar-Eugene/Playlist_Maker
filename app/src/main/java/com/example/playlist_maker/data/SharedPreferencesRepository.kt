package com.example.playlist_maker.data

import android.content.Context
import com.example.playlist_maker.constans.Constants
import com.example.playlist_maker.presentation.theme_manager.PreferencesRepository

class SharedPreferencesRepository(private val context: Context) : PreferencesRepository {
    private val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERERNCES, Context.MODE_PRIVATE)

    override fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(Constants.SEARCH_KEY, false)
    }

    override fun setDarkTheme(isDark: Boolean) {
        sharedPreferences.edit().putBoolean(Constants.SEARCH_KEY, isDark).apply()
    }
}