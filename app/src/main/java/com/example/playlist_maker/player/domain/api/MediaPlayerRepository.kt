package com.example.playlist_maker.player.domain.api

interface MediaPlayerRepository {

    fun preparePlayer(
        previewUrl: String,
        onPreparedCallback: () -> Unit,
        onCompleteCallback: () -> Unit
    )

    fun pausePlayer()

    // метод отвечает за управление воспроизведением аудиотрека в зависимости от текущего состояния плеера.
    //возвращает состояние воспроизведения
    fun playbackControl(): Boolean

    fun getCurrentPosition(): Int

    fun release()
}