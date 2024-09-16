package com.example.playlist_maker.data.network

import com.example.playlist_maker.data.NetworkClient
import com.example.playlist_maker.data.dto.TracksSearchRequest
import com.example.playlist_maker.data.dto.TracksSearchResponse
import com.example.playlist_maker.domain.api.TracksRepository
import com.example.playlist_maker.domain.models.Track

// тут мы реализацием интерфейс TracksRepository
class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(term: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(term))
        if (response is TracksSearchResponse) {
            return response.results.mapNotNull {
                try {
                    Track(
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.country,
                        it.collectionName,
                        it.primaryGenreName,
                        it.releaseDate,
                        it.previewUrl
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } else {
            throw Exception("Неожиданный тип ответа: ${response::class.java.simpleName}")
        }
    }
}