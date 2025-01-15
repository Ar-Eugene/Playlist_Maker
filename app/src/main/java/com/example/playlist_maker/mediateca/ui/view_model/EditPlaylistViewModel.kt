package com.example.playlist_maker.mediateca.ui.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.mediateca.domain.db.PlaylistInteractor
import com.example.playlist_maker.mediateca.domain.models.Playlist
import com.example.playlist_maker.search.domain.models.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {
    private val _playlist = MutableStateFlow<Playlist?>(null)
    val playlist = _playlist.asStateFlow()

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks = _tracks.asStateFlow()

    private fun loadPlaylistTracks(playlist: Playlist) {
        viewModelScope.launch {
            Log.d("EditViewModel", "Loading tracks for playlist ${playlist.id}")
            val trackIds = playlist.trackIds?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
            Log.d("EditViewModel", "Track IDs to load: $trackIds")
            val tracks = playlistInteractor.getTracksByIds(trackIds)
            Log.d("EditViewModel", "Loaded tracks: ${tracks.size}")
            _tracks.value = tracks
        }
    }

    fun setPlaylist(playlist: Playlist) {
        _playlist.value = playlist
        loadPlaylistTracks(playlist)
    }
    fun refreshPlaylistTracks() {
        _playlist.value?.let { playlist ->
            loadPlaylistTracks(playlist)
        }
    }
}