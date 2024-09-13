package com.example.playlist_maker.data.network

import android.util.Log
import com.example.playlist_maker.data.NetworkClient
import com.example.playlist_maker.data.dto.Response
import com.example.playlist_maker.data.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitNetworkClient :NetworkClient {

    val retrofit= Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val trackApi: TrackApi = retrofit.create(TrackApi::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto is TracksSearchRequest) {
            try {
                val response = trackApi.getTrack(dto.term).execute()
                if (response.isSuccessful) {
                    val body = response.body() ?: Response()
                    return body.apply { resultCode = response.code() }
                } else {
                    return Response().apply { resultCode = response.code() }
                }
            } catch (e: IOException) {
                val networkErrorMessage = e.message ?: "Network error occurred"
                return Response().apply {
                    resultCode = -1
                    message = networkErrorMessage
                }
            } catch (e: Exception) {
                val generalErrorMessage = e.message ?: " Error occurred"
                return Response().apply {
                    resultCode = -1
                    message = generalErrorMessage
                }
            }
        } else {
            return Response().apply {
                resultCode = 400
                message = "Ошибка, dto не TracksSearchRequest"
            }
        }
    }
}