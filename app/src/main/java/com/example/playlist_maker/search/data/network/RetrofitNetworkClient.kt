package com.example.playlist_maker.search.data.network

import com.example.playlist_maker.search.data.dto.Response
import com.example.playlist_maker.search.data.dto.TracksSearchRequest
import com.example.playlist_maker.search.data.dto.TracksSearchResponse

class RetrofitNetworkClient(private val trackApi: TrackApi) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        if (dto is TracksSearchRequest) {
            return try {
                val response = trackApi.getTrack(dto.term)
                // Успешный ответ
                response.apply {
                    resultCode = 200 // Успешный код
                }
            } catch (e: Exception) {
                // Обработка исключений
                TracksSearchResponse(0, emptyList()).apply {
                    resultCode = -1
                    message = "Ошибка: ${e.message}"
                }
            }
        } else {
            // Обработка неверного запроса
            return TracksSearchResponse(0, emptyList()).apply {
                resultCode = 400
                message = "Неверный запрос: ожидался TracksSearchRequest"
            }
        }
    }
}