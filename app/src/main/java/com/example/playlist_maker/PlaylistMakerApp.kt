package com.example.playlist_maker

import android.app.Application
import com.example.playlist_maker.creator.Creator

class PlaylistMakerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.init(this)

        // Применяем тему при запуске приложения
        val themeInteractor = Creator.provideThemeInteractor()
        themeInteractor.applyTheme()
    }
}
