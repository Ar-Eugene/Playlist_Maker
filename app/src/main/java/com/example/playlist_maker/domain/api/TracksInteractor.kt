package com.example.playlist_maker.domain.api

import com.example.playlist_maker.domain.models.Track

//это интерфейс, с помощью которого слой Presentation
// будет общаться со слоем Domain.

interface TracksInteractor {
    fun searchTracks(term: String, consumer: TracksConsumer)

// этот интерфейс выполняет роль Callback  Для передачи
// результатов поискового запроса, который будет выполняться в отдельном потоке
    interface TracksConsumer {
        fun consume(foundTracks: List<Track>)
    }
}