package com.example.playlist_maker.mediateca.ui.view_model

import com.example.playlist_maker.search.domain.models.Track

sealed class FavoritesState {
    data class Content(
        val tracks: List<Track>
    ) : FavoritesState()

    object Empty : FavoritesState()
}