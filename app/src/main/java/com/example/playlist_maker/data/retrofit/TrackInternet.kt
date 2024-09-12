package com.example.playlist_maker.data.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object  TrackInternet {
    val retrofit= Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val trackApi: TrackApi = retrofit.create(TrackApi::class.java)
}