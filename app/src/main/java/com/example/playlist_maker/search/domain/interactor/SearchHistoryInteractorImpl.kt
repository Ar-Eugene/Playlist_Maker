package com.example.playlist_maker.search.domain.interactor

import com.example.playlist_maker.search.domain.models.Track
import com.example.playlist_maker.search.domain.repository.SearchHistoryRepository

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) :
    SearchHistoryInteractor {
    override fun saveTrackToHistory(track: Track) = repository.saveSearchHistory(track)
    override fun getSearchHistory(): List<Track> = repository.getSearchHistory()
    override fun clearSearchHistory() = repository.clearSearchHistory()
}