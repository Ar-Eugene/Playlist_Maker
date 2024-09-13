package com.example.playlist_maker.domain.api

import com.bumptech.glide.load.engine.Resource
import com.example.playlist_maker.domain.models.Track

// с помощью этого интерфеса мы будем получать список треков из интернет
interface TracksRepository {
    //метод возвращает список  типа Track (не TrackDto), а это значит,
    // что при реализации этого интерфейса нужно преобразовать данные с сервера
    // в данные, приемлемые для слоя Domain.
    fun searchTracks(term: String): List<Track>
}