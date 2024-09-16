package com.example.playlist_maker.domain

import com.example.playlist_maker.domain.models.Track

interface SearchHistoryRepository {
    fun saveSearchHistory(track: Track)
    fun getSearchHistory(): List<Track>
    fun clearSearchHistory()
}