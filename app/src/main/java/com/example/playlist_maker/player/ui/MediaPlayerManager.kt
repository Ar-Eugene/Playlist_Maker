package com.example.playlist_maker.player.ui

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MediaPlayerManager {
    private var playerState = STATE_DEFAULT
    private var mediaPlayer: MediaPlayer? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _updateTimeRunnable = MutableLiveData<Int>(0)
    val updateTimeRunnable: LiveData<Int> = _updateTimeRunnable

    private val _playButtonEnabled = MutableLiveData<Boolean>(false)
    val playButtonEnabled: LiveData<Boolean> = _playButtonEnabled

    private val _isPlaying = MutableLiveData<Boolean>(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val runnable = object : Runnable {
        override fun run() {
            //проверяет, воспроизводится ли в данный момент трек. Обновление времени происходит только тогда, когда трек играет.
            if (playerState == STATE_PLAYING) {
                val currentPosition = mediaPlayer?.currentPosition
                    ?: 0 //получает текущую позицию воспроизведения трека.
                //преобразует текущую позицию из миллисекунд в строку формата "минуты:секунды" и
                // обновляет TextView, чтобы отобразить текущее время воспроизведения.
                _updateTimeRunnable.value = currentPosition
                mainHandler.postDelayed(this, 1000) // Обновление каждую секунду
            }
        }
    }

    fun preparePlayer(previewUrl: String) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
        mediaPlayer?.apply {
            setDataSource(previewUrl)//устанавливает URL-адрес аудиотрека, который нужно воспроизвести.
            prepareAsync()//начинает асинхронную подготовку MediaPlayer, чтобы избежать блокировки основного потока.
            setOnPreparedListener {
                _playButtonEnabled.value =
                    true// включает кнопку воспроизведения, делая её активной для пользователя.
                playerState =
                    STATE_PREPARED//обновляет состояние плеера, указывая, что он готов к воспроизведению.
            }
            setOnCompletionListener {// устанавливает слушателя, который срабатывает, когда воспроизведение трека завершено.
                playerState = STATE_PREPARED//сбрасывает состояние плеера.
                mainHandler.removeCallbacks(runnable)//останавливает обновление времени воспроизведения.
                _isPlaying.value = false
                _updateTimeRunnable.value = 0
            }
        }

    }

    private fun startPlayer() {
        mediaPlayer?.apply {
            start()
            _isPlaying.value = true

            playerState = STATE_PLAYING
            mainHandler.post(runnable)
        }

    }

    fun pausePlayer() {
        mediaPlayer?.apply {
            pause()
            _isPlaying.value = false
            playerState = STATE_PAUSED
            mainHandler.removeCallbacks(runnable)
        }

    }

    // метод отвечает за управление воспроизведением аудиотрека в зависимости от текущего состояния плеера.
    fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    fun release() {
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