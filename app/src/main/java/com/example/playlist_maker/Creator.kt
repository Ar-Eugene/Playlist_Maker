package com.example.playlist_maker

import com.example.playlist_maker.data.network.RetrofitNetworkClient
import com.example.playlist_maker.data.network.TracksRepositoryImpl
import com.example.playlist_maker.domain.api.TracksInteractor
import com.example.playlist_maker.domain.api.TracksRepository
import com.example.playlist_maker.domain.impl.TracksInteractorImpl

object Creator {
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
}