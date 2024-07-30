package com.example.playlist_maker

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.example.playlist_maker.constans.Constants
import com.google.gson.Gson


class SearchHistory(private val sharedPreferences: SharedPreferences) {
    val trackListHistory: MutableList<Track> = mutableListOf()
    private val gson = Gson()

    init {
        sharedPreferences.getString(Constants.TRACKLIST_HISTORY, "")?.let {
            if (it.isNotEmpty()) trackListHistory.addAll(fromJsonToTracklist(it))
        }
    }

    fun fromJsonToTracklist(json: String): List<Track> =
        gson.fromJson(json, Array<Track>::class.java).toList()

    private fun Array<Track>.fromTracklistToJson(): String =
        gson.toJson(this)

    @SuppressLint("CommitPrefEdits")
    fun addTrack(track: Track) {
        if (track in trackListHistory) {
            trackListHistory.remove(track)
        } else if (trackListHistory.size == 10) {
            trackListHistory.removeLast()
        }
        trackListHistory.add(0, track)
        sharedPreferences.edit()
            .putString(Constants.TRACKLIST_HISTORY, trackListHistory.toTypedArray().fromTracklistToJson())
            .apply()
    }

    fun clearHistory() {
        trackListHistory.clear()
        sharedPreferences.edit()
            .remove(Constants.TRACKLIST_HISTORY)
            .apply()
    }
}