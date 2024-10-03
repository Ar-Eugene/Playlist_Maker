package com.example.playlist_maker.settings.ui

import android.app.Application
import com.example.playlist_maker.creator.Creator

class SwitchTheme : Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.init(this)
        ThemeManager.applyTheme()
    }
}
