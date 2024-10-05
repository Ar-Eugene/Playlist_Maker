package com.example.playlist_maker.search.data.network

import com.example.playlist_maker.search.data.dto.Response
import com.example.playlist_maker.search.data.dto.TracksSearchRequest
import com.example.playlist_maker.search.data.dto.TracksSearchResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val trackApi: TrackApi = retrofit.create(TrackApi::class.java)
    override fun doRequest(dto: Any): Response {
        if (dto is TracksSearchRequest) {
            try {
                val response = trackApi.getTrack(dto.term).execute()
                return if (response.isSuccessful) {
                    val body = response.body()
                    if (body == null) {
                        TracksSearchResponse(0, emptyList()).apply {
                            resultCode = response.code()
                            message = "Пустой ответ от сервера"
                        }
                    } else {
                        body.apply {
                            resultCode = response.code()
                        }
                    }
                } else {
                    TracksSearchResponse(0, emptyList()).apply {
                        resultCode = response.code()
                        message = "HTTP ${response.code()}: ${response.message()}"
                    }
                }
            } catch (e: Exception) {
                return TracksSearchResponse(0, emptyList()).apply {
                    resultCode = -1
                    message = "Ошибка: ${e.message}"
                }
            }
        } else {
            return TracksSearchResponse(0, emptyList()).apply {
                resultCode = 400
                message = "Неверный запрос: ожидался TracksSearchRequest"
            }
        }
    }
}