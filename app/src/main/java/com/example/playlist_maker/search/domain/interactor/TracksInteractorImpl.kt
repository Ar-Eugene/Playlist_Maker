package com.example.playlist_maker.search.domain.interactor

import com.example.playlist_maker.search.domain.models.Track
import com.example.playlist_maker.search.domain.repository.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    override fun searchTracks(term: String): Flow<Pair<List<Track>?, String?>> = flow {
        try {
            repository.searchTracks(term)
                .collect { tracks ->
                    if (tracks.isEmpty()) {
                        emit(null to "No results found") // Пустой список с сообщением об ошибке
                    } else {
                        emit(tracks to null) // Успешный результат без сообщения об ошибке
                    }
                }
        } catch (e: Exception) {
            emit(null to "Network error: ${e.message}") // Ошибка сети
        }

    }
}
