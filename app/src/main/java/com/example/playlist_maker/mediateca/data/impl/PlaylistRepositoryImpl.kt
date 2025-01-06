package com.example.playlist_maker.mediateca.data.impl

import android.net.Uri
import com.example.playlist_maker.mediateca.data.db.PlaylistDatabase
import com.example.playlist_maker.mediateca.data.db.entity.PlaylistEntity
import com.example.playlist_maker.mediateca.domain.db.PlaylistRepository
import com.example.playlist_maker.mediateca.domain.models.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDatabase: PlaylistDatabase
) : PlaylistRepository {
    override suspend fun createPlaylist(playlist: Playlist) {
        val entity = PlaylistEntity(
            title = playlist.title,
            description = playlist.description,
            imagePath = playlist.imagePath?.toString(),
            trackIds = playlist.trackIds,
            trackAmount = playlist.trackAmount
        )
        playlistDatabase.playlistDao().insertPlaylist(entity)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDatabase.playlistDao().getPlaylists().map { entities ->
            entities.map { entity ->
                Playlist(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    imagePath = entity.imagePath?.let { Uri.parse(it) },
                    trackIds = entity.trackIds,
                    trackAmount = entity.trackAmount
                )
            }
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val entity = PlaylistEntity(
            id = playlist.id,
            title = playlist.title,
            description = playlist.description,
            imagePath = playlist.imagePath?.toString(),
            trackIds = playlist.trackIds,
            trackAmount = playlist.trackAmount
        )
        playlistDatabase.playlistDao().updatePlaylist(entity)
    }
    override suspend fun addTrackToPlaylist(trackId: Int, playlistId: Int): Boolean {
        playlistDatabase.playlistDao().getPlaylists().first().find { it.id == playlistId }?.let { playlist ->
            val currentTracks = playlist.trackIds?.split(",")?.toMutableList() ?: mutableListOf()

            if (!currentTracks.contains(trackId.toString())) {
                currentTracks.add(trackId.toString())
                val updatedPlaylist = playlist.copy(
                    trackIds = currentTracks.joinToString(","),
                    trackAmount = playlist.trackAmount + 1
                )
                playlistDatabase.playlistDao().updatePlaylist(updatedPlaylist)
                return true
            }
            return false
        }
        return false
    }
}