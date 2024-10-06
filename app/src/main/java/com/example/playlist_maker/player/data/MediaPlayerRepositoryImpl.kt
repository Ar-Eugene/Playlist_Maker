package com.example.playlist_maker.player.data

import android.media.MediaPlayer
import com.example.playlist_maker.player.domain.api.MediaPlayerRepository

class MediaPlayerRepositoryImpl : MediaPlayerRepository {

    private var playerState = STATE_DEFAULT
    private var mediaPlayer: MediaPlayer? = null

    override fun preparePlayer(
        previewUrl: String,
        onPreparedCallback: () -> Unit,
        onCompleteCallback: () -> Unit
    ) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
        mediaPlayer?.apply {
            setDataSource(previewUrl)//устанавливает URL-адрес аудиотрека, который нужно воспроизвести.
            prepareAsync()//начинает асинхронную подготовку MediaPlayer, чтобы избежать блокировки основного потока.
            setOnPreparedListener {
                playerState =
                    STATE_PREPARED //обновляет состояние плеера, указывая, что он готов к воспроизведению.
                onPreparedCallback()
            }
            setOnCompletionListener { // устанавливает слушателя, который срабатывает, когда воспроизведение трека завершено.
                playerState = STATE_PREPARED//сбрасывает состояние плеера.
                onCompleteCallback()
            }
        }

    }

    private fun startPlayer() {
        mediaPlayer?.apply {
            start()
            playerState = STATE_PLAYING
        }

    }

    override fun pausePlayer() {
        mediaPlayer?.apply {
            pause()
            playerState = STATE_PAUSED
        }
    }

    // метод отвечает за управление воспроизведением аудиотрека в зависимости от текущего состояния плеера.
    override fun playbackControl(): Boolean {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
                return false
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
                return true
            }
            else -> return false
        }
    }

    override fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0

    override fun release() {
        mediaPlayer?.release()
    }

    private companion object {
        // константы для отслеживания состояние медиаплеера
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
    }
}