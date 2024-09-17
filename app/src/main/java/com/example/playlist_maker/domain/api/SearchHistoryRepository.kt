package com.example.playlist_maker.domain.api

import com.example.playlist_maker.domain.models.Track

interface SearchHistoryRepository {
    fun saveSearchHistory(track: Track)
    fun getSearchHistory(): List<Track>
    fun clearSearchHistory()
}