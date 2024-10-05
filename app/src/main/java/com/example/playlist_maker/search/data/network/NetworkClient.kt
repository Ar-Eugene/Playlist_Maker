package com.example.playlist_maker.search.data.network

import com.example.playlist_maker.search.data.dto.Response

//этот interface связывает data и domain слои
// слою Domain не важно, какой инструмент будет работать с сетью.
// когда Domain будет делать запрос то ответ будет приходить через этот interface
interface NetworkClient {
    fun doRequest(dto: Any): Response
}