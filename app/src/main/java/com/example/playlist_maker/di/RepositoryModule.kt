package com.example.playlist_maker.di

import com.example.playlist_maker.common.ThemeRepository
import com.example.playlist_maker.mediateca.data.FavoritesRepositoryImpl
import com.example.playlist_maker.mediateca.data.converter.TrackDbConverter
import com.example.playlist_maker.mediateca.data.impl.PlaylistRepositoryImpl
import com.example.playlist_maker.mediateca.domain.db.FavoritesRepository
import com.example.playlist_maker.mediateca.domain.db.PlaylistRepository
import com.example.playlist_maker.player.data.MediaPlayerRepositoryImpl
import com.example.playlist_maker.player.domain.api.MediaPlayerRepository
import com.example.playlist_maker.search.data.impl.SearchHistoryRepositoryImpl
import com.example.playlist_maker.search.data.impl.TracksRepositoryImpl
import com.example.playlist_maker.search.domain.repository.SearchHistoryRepository
import com.example.playlist_maker.search.domain.repository.TracksRepository
import com.example.playlist_maker.settings.data.repository.ThemeRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {

    factory<TracksRepository> {
        TracksRepositoryImpl(networkClient = get())
    }

    factory<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(sharedPreferences = get(), gson = get())
    }

    factory<ThemeRepository> {
        ThemeRepositoryImpl(sharedPreferences = get())
    }

    factory<MediaPlayerRepository> {
        MediaPlayerRepositoryImpl(mediaPlayer = get())
    }

    factory { TrackDbConverter() }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }

    factory<PlaylistRepository> {
        PlaylistRepositoryImpl(get(),get())
    }
}