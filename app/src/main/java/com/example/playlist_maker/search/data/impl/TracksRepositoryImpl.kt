package com.example.playlist_maker.search.data.impl

import com.example.playlist_maker.search.data.dto.TracksSearchRequest
import com.example.playlist_maker.search.data.dto.TracksSearchResponse
import com.example.playlist_maker.search.data.network.NetworkClient
import com.example.playlist_maker.search.domain.models.Track
import com.example.playlist_maker.search.domain.repository.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// тут мы реализацием интерфейс TracksRepository
class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(term: String): Flow<List<Track>> = flow {
        try {
            // Делаем сетевой запрос
            val response = networkClient.doRequest(TracksSearchRequest(term))

            // Проверяем, что ответ - это TracksSearchResponse
            if (response is TracksSearchResponse) {
                when {
                    response.resultCode == -1 -> throw Exception(
                        response.message ?: "Network error"
                    )

                    response.results.isEmpty() -> emit(emptyList())
                    else -> {
                        // Преобразуем данные в список объектов Track
                        val tracks = response.results.mapNotNull { result ->
                            try {
                                Track(
                                    result.trackName,
                                    result.artistName,
                                    result.trackTimeMillis,
                                    result.artworkUrl100,
                                    result.country,
                                    result.collectionName,
                                    result.primaryGenreName,
                                    result.releaseDate,
                                    result.previewUrl
                                )
                            } catch (e: Exception) {
                                null // Пропускаем невалидные объекты
                            }
                        }
                        emit(tracks)
                    }
                }
            } else {
                throw Exception("Unexpected response type: ${response::class.java.simpleName}")
            }
        } catch (e: Exception) {
            // Пробрасываем исключение дальше
            throw Exception("Exception occurred: ${e.message}")
        }
    }
}