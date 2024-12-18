package com.example.playlist_maker.mediateca.data.converter

import com.example.playlist_maker.mediateca.data.db.entity.TrackEntity
import com.example.playlist_maker.search.domain.models.Track

class TrackDbConverter {
    fun map(track: TrackEntity): Track {
        return Track(
            artistName = track.artistName,
            artworkUrl100 = track.artworkUrl100,
            trackName = track.trackName,
            trackTimeMillis = track.trackTimeMillis,
            trackId = track.trackId,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            isFavorite = true
        )
    }

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            trackId = track.trackId,
            artworkUrl100 = track.artworkUrl100,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName?: "",
            releaseDate = track.releaseDate ,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTimeMillis = track.trackTimeMillis,
            previewUrl = track.previewUrl,
        )
    }
}