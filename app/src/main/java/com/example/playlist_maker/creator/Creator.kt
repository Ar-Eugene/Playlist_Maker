package com.example.playlist_maker.creator

import android.content.Context
import android.content.SharedPreferences
import com.example.playlist_maker.player.ui.MediaPlayerManager
import com.example.playlist_maker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlist_maker.settings.data.repository.ThemeRepositoryImpl
import com.example.playlist_maker.search.data.network.RetrofitNetworkClient
import com.example.playlist_maker.search.data.network.TrackApi
import com.example.playlist_maker.search.data.repository.TracksRepositoryImpl
import com.example.playlist_maker.search.domain.interactor.TracksInteractor
import com.example.playlist_maker.search.domain.repository.TracksRepository
import com.example.playlist_maker.search.domain.interactor.TracksInteractorImpl
import com.example.playlist_maker.search.domain.repository.SearchHistoryRepository
import com.example.playlist_maker.settings.domain.theme.repository.ThemeRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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
        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val trackApi = retrofit.create(TrackApi::class.java)
        val networkClient = RetrofitNetworkClient(trackApi) // Используем вашу реализацию NetworkClient
        return TracksRepositoryImpl(networkClient)  // TracksRepositoryImpl - конкретная реализация репозитория
    }

    // Создаем и возвращаем интерактор
    fun provideTracksInteractor(): TracksInteractor {
        val repository = provideTracksRepository() // Получаем репозиторий через Creator
        return TracksInteractorImpl(repository)    // TracksInteractorImpl - реализация интерактора
    }
    fun providePreferencesRepository(): ThemeRepository {
        return ThemeRepositoryImpl(provideSharedPrefs(SHARED_PREFERERNCES))
    }
    fun provideSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(provideSharedPrefs(HISTORY_TRACKLIST))
    }

    private fun provideSharedPrefs(key: String): SharedPreferences =
        applicationContext.getSharedPreferences(key, Context.MODE_PRIVATE)

    fun provideMediaPlayerManager(): MediaPlayerManager {
        return MediaPlayerManager()
    }

}
