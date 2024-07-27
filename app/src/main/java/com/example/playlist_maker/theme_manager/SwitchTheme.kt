package com.example.playlist_maker.theme_manager

import android.app.Application

class SwitchTheme: Application() {
    override fun onCreate() {
        super.onCreate()
        ThemeManager.applyTheme(this)
    }


}
