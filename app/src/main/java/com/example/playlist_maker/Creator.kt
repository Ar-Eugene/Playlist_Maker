package com.example.playlist_maker

import android.content.Context
import android.content.SharedPreferences
import com.example.playlist_maker.data.SearchHistoryRepositoryImpl
import com.example.playlist_maker.data.SharedPreferencesRepositoryImpl
import com.example.playlist_maker.data.network.RetrofitNetworkClient
import com.example.playlist_maker.data.TracksRepositoryImpl
import com.example.playlist_maker.domain.api.TracksInteractor
import com.example.playlist_maker.domain.api.TracksRepository
import com.example.playlist_maker.domain.impl.TracksInteractorImpl
import com.example.playlist_maker.domain.api.SearchHistoryRepository
import com.example.playlist_maker.domain.api.ThemeRepository


object Creator {
    // для хранения темы
    private const val SHARED_PREFERERNCES = "data_preferences"
    // для хранения истории треков
    private const val HISTORY_TRACKLIST = "MY_TRACKS"

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
    fun providePreferencesRepository(): ThemeRepository {
        return SharedPreferencesRepositoryImpl(provideSharedPrefs(SHARED_PREFERERNCES))
    }
    fun provideSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(provideSharedPrefs(HISTORY_TRACKLIST))
    }

    private fun provideSharedPrefs(key: String): SharedPreferences =
        applicationContext.getSharedPreferences(key, Context.MODE_PRIVATE)

}
