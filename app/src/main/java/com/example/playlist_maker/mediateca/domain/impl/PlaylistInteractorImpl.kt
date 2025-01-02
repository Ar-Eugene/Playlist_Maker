package com.example.playlist_maker.mediateca.domain.impl

import com.example.playlist_maker.mediateca.domain.db.PlaylistInteractor
import com.example.playlist_maker.mediateca.domain.db.PlaylistRepository
import com.example.playlist_maker.mediateca.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
) : PlaylistInteractor {
    override suspend fun createPlaylist(playlist: Playlist) = repository.createPlaylist(playlist)

    override suspend fun getPlaylists(): Flow<List<Playlist>> = repository.getPlaylists()

    override suspend fun updatePlaylist(playlist: Playlist) = repository.updatePlaylist(playlist)
}