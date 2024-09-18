package com.example.playlist_maker.data

import android.content.SharedPreferences
import com.example.playlist_maker.domain.api.ThemeRepository


class SharedPreferencesRepositoryImpl(private val sharedPreferences: SharedPreferences) :
    ThemeRepository {

    override fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(SEARCH_KEY, false)
    }

    override fun setDarkTheme(isDark: Boolean) {
        sharedPreferences.edit().putBoolean(SEARCH_KEY, isDark).apply()
    }

    private companion object{
        // ключ для хранения темы
        const val SEARCH_KEY = "key_for_shared"
    }
}