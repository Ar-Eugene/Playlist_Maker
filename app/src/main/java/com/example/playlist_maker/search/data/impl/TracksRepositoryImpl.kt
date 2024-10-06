package com.example.playlist_maker.search.data.impl

import com.example.playlist_maker.search.data.dto.TracksSearchRequest
import com.example.playlist_maker.search.data.dto.TracksSearchResponse
import com.example.playlist_maker.search.data.network.NetworkClient
import com.example.playlist_maker.search.domain.repository.TracksRepository
import com.example.playlist_maker.search.domain.models.Track

// тут мы реализацием интерфейс TracksRepository
class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(term: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(term))
        if (response is TracksSearchResponse) {
            if (response.resultCode == -1) {
                throw Exception(response.message ?: "Network error")
            }
            if (response.results.isEmpty()) {
                return emptyList()
            }
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
            throw Exception("Unexpected response type: ${response::class.java.simpleName}")
        }
    }
}