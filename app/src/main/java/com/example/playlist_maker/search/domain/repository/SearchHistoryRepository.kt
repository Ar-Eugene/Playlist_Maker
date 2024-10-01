package com.example.playlist_maker.search.domain.repository

import com.example.playlist_maker.search.domain.models.Track

interface SearchHistoryRepository {
    fun saveSearchHistory(track: Track)
    fun getSearchHistory(): List<Track>
    fun clearSearchHistory()
}