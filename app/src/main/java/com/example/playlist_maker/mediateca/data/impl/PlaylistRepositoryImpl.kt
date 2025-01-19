package com.example.playlist_maker.mediateca.data.impl

import android.net.Uri
import android.util.Log
import com.example.playlist_maker.mediateca.data.db.AppDatabase
import com.example.playlist_maker.mediateca.data.db.PlaylistDatabase
import com.example.playlist_maker.mediateca.data.db.entity.PlaylistEntity
import com.example.playlist_maker.mediateca.data.db.entity.PlaylistTrackEntity
import com.example.playlist_maker.mediateca.domain.db.PlaylistRepository
import com.example.playlist_maker.mediateca.domain.models.Playlist
import com.example.playlist_maker.search.domain.models.Track
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

    override suspend fun addTrackToPlaylist(trackId: Int, playlistId: Int, track: Track): Boolean {
        Log.d("PlaylistRepo", "Adding track $trackId to playlist $playlistId")

        val playlist = playlistDatabase.playlistDao().getPlaylistById(playlistId)
        if (playlist != null) {
            val playlistTrackEntity = PlaylistTrackEntity(
                trackId = trackId.toString(),
                trackName = track.trackName,
                artistName = track.artistName,
                trackTimeMillis = track.trackTimeMillis,
                artworkUrl100 = track.artworkUrl100,
                country = track.country,
                collectionName = track.collectionName,
                primaryGenreName = track.primaryGenreName,
                releaseDate = track.releaseDate,
                previewUrl = track.previewUrl
            )
            playlistDatabase.playlistTrackDao().insertTrack(playlistTrackEntity)

            val currentTracks = playlist.trackIds?.split(",")?.filterNot { it.isEmpty() }?.toMutableList() ?: mutableListOf()

            if (!currentTracks.contains(trackId.toString())) {
                currentTracks.add(trackId.toString())
                val newTrackIds = currentTracks.joinToString(",")

                playlistDatabase.playlistDao().updatePlaylistTracks(
                    playlistId = playlistId,
                    newTrackIds = newTrackIds,
                    newAmount = playlist.trackAmount + 1
                )
                return true
            }
            return false
        }
        return false
    }

    override suspend fun getTracksByIds(trackIds: List<String>): List<Track> {
        Log.d("Repository", "Requesting tracks with IDs: $trackIds")
        val entities = playlistDatabase.playlistTrackDao().getTracksByIds(trackIds)
        Log.d("Repository", "Found entities: ${entities.map { it.trackId }}")

        // Создаем map для быстрого доступа к трекам по id
        val tracksMap = entities.associateBy { it.trackId }

        // Возвращаем треки в том же порядке, что и в trackIds (последний добавленный будет последним в списке)
        val orderedTracks = trackIds.mapNotNull { id -> tracksMap[id] }

        // Переворачиваем список, чтобы последний добавленный трек был первым
        return orderedTracks.reversed().map { entity ->
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