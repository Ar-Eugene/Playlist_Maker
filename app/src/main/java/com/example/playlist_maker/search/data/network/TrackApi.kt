package com.example.playlist_maker.search.data.network

import com.example.playlist_maker.search.data.dto.TracksSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackApi {
    @GET("search/entity")
    fun getTrack(@Query("term") text: String): Call<TracksSearchResponse>
}