package com.example.playlist_maker.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object  TrackInternet {
    val retrofit= Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val trackApi: TrackApi = retrofit.create(TrackApi::class.java)
}
//возможно это нужно будет потом удалить.