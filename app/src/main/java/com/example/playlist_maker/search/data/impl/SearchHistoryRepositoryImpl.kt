package com.example.playlist_maker.search.data.impl

import android.content.SharedPreferences
import com.example.playlist_maker.search.domain.repository.SearchHistoryRepository
import com.example.playlist_maker.search.domain.models.Track
import com.google.gson.Gson

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : SearchHistoryRepository {

    private val trackListHistory: MutableList<Track> = mutableListOf()

    init {
        sharedPreferences.getString(TRACKLIST_HISTORY, "")
            ?.takeIf { it.isNotBlank() }
            ?.let { trackListHistory.addAll(it.fromJsonToTracklist()) }
    }

    override fun saveSearchHistory(track: Track) {
        if (track in trackListHistory) {
            trackListHistory.remove(track)
        } else if (trackListHistory.size == 10) {
            trackListHistory.remove(trackListHistory.last())
        }
        trackListHistory.add(0, track)
        sharedPreferences.edit()
            .putString(
                TRACKLIST_HISTORY,
                trackListHistory.toTypedArray().fromTracklistToJson()
            )
            .apply()
    }

    override fun getSearchHistory(): List<Track> = trackListHistory
    override fun clearSearchHistory() {
        trackListHistory.clear()
        sharedPreferences.edit()
            .remove(TRACKLIST_HISTORY)
            .apply()
    }

    private fun String.fromJsonToTracklist(): List<Track> =
        gson.fromJson(this, Array<Track>::class.java).toList()

    private fun Array<Track>.fromTracklistToJson(): String =
        gson.toJson(this)

    private companion object {
        // для json преобразования
        const val TRACKLIST_HISTORY = "TRACKLIST_HISTORY"
    }
}