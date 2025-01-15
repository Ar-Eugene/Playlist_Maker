package com.example.playlist_maker.mediateca.domain.impl

import com.example.playlist_maker.mediateca.domain.db.PlaylistInteractor
import com.example.playlist_maker.mediateca.domain.db.PlaylistRepository
import com.example.playlist_maker.mediateca.domain.models.Playlist
import com.example.playlist_maker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
) : PlaylistInteractor {
    override suspend fun createPlaylist(playlist: Playlist) = repository.createPlaylist(playlist)

    override suspend fun getPlaylists(): Flow<List<Playlist>> = repository.getPlaylists()

    override suspend fun updatePlaylist(playlist: Playlist) = repository.updatePlaylist(playlist)

    override suspend fun addTrackToPlaylist(trackId: Int, playlistId: Int): Boolean {
        return repository.addTrackToPlaylist(trackId, playlistId)
    }
    override suspend fun getTracksByIds(trackIds: List<String>): List<Track> {
        return repository.getTracksByIds(trackIds)
    }
}