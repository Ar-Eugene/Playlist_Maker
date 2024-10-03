package com.example.playlist_maker.settings.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlist_maker.settings.domain.theme.repository.ThemeRepository

class SettingsViewModelFactory(private val themeRepository: ThemeRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(themeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}