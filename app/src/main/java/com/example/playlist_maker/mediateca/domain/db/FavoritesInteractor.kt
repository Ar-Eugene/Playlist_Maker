package com.example.playlist_maker.mediateca.domain.db

import com.example.playlist_maker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {

    fun favoritesTracks(): Flow<List<Track>>

    suspend fun addFavoriteTrack(track: Track)

    suspend fun removeFavoriteTrack(trackId: String)
}