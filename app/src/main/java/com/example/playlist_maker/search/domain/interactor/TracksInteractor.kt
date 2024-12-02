package com.example.playlist_maker.search.domain.interactor

import com.example.playlist_maker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

//это интерфейс, с помощью которого слой Presentation
// будет общаться со слоем Domain.

interface TracksInteractor {
    fun searchTracks(term: String): Flow<Pair<List<Track>?, String?>>
}