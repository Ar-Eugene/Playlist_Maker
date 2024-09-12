package com.example.playlist_maker.data.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackApi {

    @GET("search/entity")

    fun getTrack(@Query("term") text: String):Call<TreckResponse>
}