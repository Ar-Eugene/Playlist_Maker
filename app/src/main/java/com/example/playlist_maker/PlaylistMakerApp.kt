package com.example.playlist_maker

import android.app.Application
import com.example.playlist_maker.common.ThemeInteractor
import com.example.playlist_maker.di.dataModule
import com.example.playlist_maker.di.interactorModule
import com.example.playlist_maker.di.repositoryModule
import com.example.playlist_maker.di.viewModelModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PlaylistMakerApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@PlaylistMakerApp)
            modules(dataModule, interactorModule, repositoryModule, viewModelModule)
        }

        val themeInteractor: ThemeInteractor by inject()
        themeInteractor.applyTheme()
    }
}
