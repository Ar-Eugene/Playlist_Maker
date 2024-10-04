package com.example.playlist_maker.search.domain.models

import java.io.Serializable


data class Track(val trackName: String,
                 val artistName: String,
                 val trackTimeMillis: Long,
                 val artworkUrl100: String,
                 val country: String,
                 val collectionName: String?,
                 val primaryGenreName: String,
                 val releaseDate: String,
                 val previewUrl:String): Serializable


