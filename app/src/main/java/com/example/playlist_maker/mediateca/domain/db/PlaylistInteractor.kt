package com.example.playlist_maker.mediateca.domain.db

import com.example.playlist_maker.mediateca.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun getPlaylists(): Flow<List<Playlist>>
    suspend fun updatePlaylist(playlist: Playlist)
}