package com.example.playlist_maker.search.domain.interactor

import com.example.playlist_maker.search.domain.repository.TracksRepository
import com.example.playlist_maker.search.ui.models.DomainSearchError
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(term: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            try {
                val tracks = repository.searchTracks(term)
                if (tracks.isEmpty()) {
                    consumer.onError(DomainSearchError.EmptyResult)
                } else {
                    consumer.consume(tracks)
                }
            } catch (e: Exception) {
                consumer.onError(DomainSearchError.NetworkError)
            }
        }
    }
}

//class EmptyListException : Exception()