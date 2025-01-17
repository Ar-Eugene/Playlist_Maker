package com.example.playlist_maker.mediateca.data.impl

import android.net.Uri
import android.util.Log
import com.example.playlist_maker.mediateca.data.db.AppDatabase
import com.example.playlist_maker.mediateca.data.db.PlaylistDatabase
import com.example.playlist_maker.mediateca.data.db.entity.PlaylistEntity
import com.example.playlist_maker.mediateca.domain.db.PlaylistRepository
import com.example.playlist_maker.mediateca.domain.models.Playlist
import com.example.playlist_maker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDatabase: PlaylistDatabase,
    private val appDatabase: AppDatabase
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
        Log.d("PlaylistRepo", "Adding track $trackId to playlist $playlistId")
        playlistDatabase.playlistDao().getPlaylists().first().find { it.id == playlistId }?.let { playlist ->
            val currentTracks = playlist.trackIds?.split(",")?.toMutableList() ?: mutableListOf()
            Log.d("PlaylistRepo", "Current tracks: $currentTracks")

            if (!currentTracks.contains(trackId.toString())) {
                currentTracks.add(trackId.toString())
                val updatedPlaylist = playlist.copy(
                    trackIds = currentTracks.joinToString(","),
                    trackAmount = playlist.trackAmount + 1
                )
                Log.d("PlaylistRepo", "Updated tracks: ${updatedPlaylist.trackIds}")
                playlistDatabase.playlistDao().updatePlaylist(updatedPlaylist)
                return true
            }
            return false
        }
        return false
    }
    override suspend fun getTracksByIds(trackIds: List<String>): List<Track> {
        Log.d("Repository", "Requesting tracks with IDs: $trackIds")
        val entities = appDatabase.trackDao().getTracksByIds(trackIds)
        Log.d("Repository", "Found entities: ${entities.map { it.trackId }}")

        return entities.map { entity ->
            Track(
                trackName = entity.trackName,
                artistName = entity.artistName,
                trackTimeMillis = entity.trackTimeMillis,
                artworkUrl100 = entity.artworkUrl100,
                country = entity.country,
                collectionName = entity.collectionName,
                primaryGenreName = entity.primaryGenreName,
                releaseDate = entity.releaseDate,
                previewUrl = entity.previewUrl,
                trackId = entity.trackId
            )
        }.also {
            Log.d("Repository", "Mapped to tracks: ${it.map { track -> track.trackId }}")
        }
    }
    override suspend fun getPlaylistById(playlistId: Int): Playlist? {
        return playlistDatabase.playlistDao().getPlaylistById(playlistId)?.let { entity ->
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
    override suspend fun updatePlaylistTracks(playlistId: Int, newTrackIds: String, newAmount: Int) {
        playlistDatabase.playlistDao().updatePlaylistTracks(playlistId, newTrackIds, newAmount)

    }
    override suspend fun deletePlaylist(playlistId: Int) {
        playlistDatabase.playlistDao().deletePlaylist(playlistId)
    }


    }