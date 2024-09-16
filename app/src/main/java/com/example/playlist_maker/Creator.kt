package com.example.playlist_maker

import android.content.Context
import com.example.playlist_maker.data.SharedPreferencesRepository
import com.example.playlist_maker.data.network.RetrofitNetworkClient
import com.example.playlist_maker.data.network.TracksRepositoryImpl
import com.example.playlist_maker.domain.api.TracksInteractor
import com.example.playlist_maker.domain.api.TracksRepository
import com.example.playlist_maker.domain.impl.TracksInteractorImpl
import com.example.playlist_maker.presentation.theme_manager.PreferencesRepository

object Creator {
    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }
    // Создаем и возвращаем репозиторий
    fun provideTracksRepository(): TracksRepository {
        val networkClient = RetrofitNetworkClient() // Используем вашу реализацию NetworkClient
        return TracksRepositoryImpl(networkClient)  // TracksRepositoryImpl - конкретная реализация репозитория
    }

    // Создаем и возвращаем интерактор
    fun provideTracksInteractor(): TracksInteractor {
        val repository = provideTracksRepository() // Получаем репозиторий через Creator
        return TracksInteractorImpl(repository)    // TracksInteractorImpl - реализация интерактора
    }
    fun providePreferencesRepository(): PreferencesRepository {
        return SharedPreferencesRepository(applicationContext)
    }
}