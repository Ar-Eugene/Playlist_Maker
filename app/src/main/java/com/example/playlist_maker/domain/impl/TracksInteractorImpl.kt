package com.example.playlist_maker.domain.impl

import com.example.playlist_maker.domain.api.TracksInteractor
import com.example.playlist_maker.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(term: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            try {
                val tracks = repository.searchTracks(term)
                consumer.consume(tracks)
            } catch (e: Exception) {
                e.printStackTrace() // Добавьте это для отладки
                consumer.onError(e.message ?: "Произошла неизвестная ошибка")
            }
        }
    }
}