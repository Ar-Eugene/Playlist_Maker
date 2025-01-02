package com.example.playlist_maker.mediateca.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.mediateca.domain.db.PlaylistInteractor
import com.example.playlist_maker.mediateca.domain.models.Playlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {
    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists = _playlists.asStateFlow()

    init {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect {
                _playlists.value = it
            }
        }
    }
}