package com.example.playlist_maker.di

import com.example.playlist_maker.common.ThemeInteractor
import com.example.playlist_maker.mediateca.domain.db.FavoritesInteractor
import com.example.playlist_maker.mediateca.domain.db.PlaylistInteractor
import com.example.playlist_maker.mediateca.domain.impl.FavoritesInteractorImpl
import com.example.playlist_maker.mediateca.domain.impl.PlaylistInteractorImpl
import com.example.playlist_maker.player.domain.PlayerInteractorImpl
import com.example.playlist_maker.player.domain.api.PlayerInteractor
import com.example.playlist_maker.search.domain.interactor.SearchHistoryInteractor
import com.example.playlist_maker.search.domain.interactor.SearchHistoryInteractorImpl
import com.example.playlist_maker.search.domain.interactor.TracksInteractor
import com.example.playlist_maker.search.domain.interactor.TracksInteractorImpl
import com.example.playlist_maker.settings.domain.theme.interactor.ThemeInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    factory<TracksInteractor> {
        TracksInteractorImpl(repository = get())
    }

    factory<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(repository = get())
    }

    factory<ThemeInteractor> {
        ThemeInteractorImpl(themeRepository = get())
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(mediaPlayerRepository = get())
    }

    factory<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }

    factory<PlaylistInteractor> {
        PlaylistInteractorImpl(get())
    }

}