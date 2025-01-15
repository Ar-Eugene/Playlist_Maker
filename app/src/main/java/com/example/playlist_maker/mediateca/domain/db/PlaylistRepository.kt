package com.example.playlist_maker.mediateca.domain.db

import com.example.playlist_maker.mediateca.domain.models.Playlist
import com.example.playlist_maker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun getPlaylists(): Flow<List<Playlist>>
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun addTrackToPlaylist(trackId: Int, playlistId: Int):Boolean
    suspend fun getTracksByIds(trackIds: List<String>): List<Track>
}