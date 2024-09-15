package com.example.playlist_maker.data.network

import com.bumptech.glide.load.engine.Resource
import com.example.playlist_maker.data.NetworkClient
import com.example.playlist_maker.data.dto.TracksSearchRequest
import com.example.playlist_maker.data.dto.TracksSearchResponse
import com.example.playlist_maker.domain.api.TracksRepository
import com.example.playlist_maker.domain.models.Track

// тут мы реализацием интерфейс TracksRepository
class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(term: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(term))
        if (response.resultCode == 200) {
            val tracksResponse = response as? TracksSearchResponse
            return tracksResponse?.results?.map {
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
            } ?: emptyList()
        } else {
            throw Exception("Ошибка сети: ${response.message}")
        }
    }
}