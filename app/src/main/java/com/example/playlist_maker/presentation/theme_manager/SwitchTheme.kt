package com.example.playlist_maker.presentation.theme_manager

import android.app.Application
import com.example.playlist_maker.Creator

class SwitchTheme: Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.init(this)
        ThemeManager.applyTheme()
    }
}
