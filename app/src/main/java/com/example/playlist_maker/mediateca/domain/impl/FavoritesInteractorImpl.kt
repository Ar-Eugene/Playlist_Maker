package com.example.playlist_maker.mediateca.domain.impl

import com.example.playlist_maker.mediateca.domain.db.FavoritesInteractor
import com.example.playlist_maker.mediateca.domain.db.FavoritesRepository
import com.example.playlist_maker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val favoritesRepository: FavoritesRepository) :
    FavoritesInteractor {
    override fun favoritesTracks(): Flow<List<Track>> {
        return favoritesRepository.favoritesTracks()
    }

    override suspend fun addFavoriteTrack(track: Track) {
        favoritesRepository.addFavoriteTrack(track)
    }

    override suspend fun removeFavoriteTrack(trackId: String) {
        favoritesRepository.removeFavoriteTrack(trackId)
    }
}