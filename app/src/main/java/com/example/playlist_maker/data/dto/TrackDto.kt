package com.example.playlist_maker.data.dto

//это модель нашей песни, которую мы будем использовать при получении
// данных с сервера вместо класса Track, живущего в Domain-слое.
data class TrackDto(val trackName: String,
                    val artistName: String,
                    val trackTimeMillis: Long,
                    val artworkUrl100: String,
                    val country: String,
                    val collectionName: String,
                    val primaryGenreName: String,
                    val releaseDate: String,
                    val previewUrl:String)
