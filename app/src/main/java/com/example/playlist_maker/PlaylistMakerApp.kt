package com.example.playlist_maker

import android.app.Application
import com.example.playlist_maker.creator.Creator
import com.example.playlist_maker.di.dataModule
import com.example.playlist_maker.di.interactorModule
import com.example.playlist_maker.di.repositoryModule
import com.example.playlist_maker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PlaylistMakerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.init(this)

        startKoin {
            androidContext(this@PlaylistMakerApp)
            modules(dataModule, interactorModule, repositoryModule, viewModelModule)
        }
        // Применяем тему при запуске приложения
        val themeInteractor = Creator.provideThemeInteractor()
        themeInteractor.applyTheme()
    }
}
