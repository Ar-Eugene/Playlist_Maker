package com.example.playlist_maker.search.domain.interactor

import com.example.playlist_maker.search.domain.models.Track

interface SearchHistoryInteractor {
    fun saveTrackToHistory(track: Track)
    fun getSearchHistory(): List<Track>
    fun clearSearchHistory()
}