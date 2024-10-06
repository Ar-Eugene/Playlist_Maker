package com.example.playlist_maker.player.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker.player.domain.api.PlayerInteractor

class PlayerViewModel(private val playerInteractor: PlayerInteractor) : ViewModel() {

    private val mainHandler = Handler(Looper.getMainLooper())

    private val _currentPlayingTime = MutableLiveData(0)
    val updateTimeRunnable: LiveData<Int> = _currentPlayingTime

    private val _playButtonEnabled = MutableLiveData(false)
    val playButtonEnabled: LiveData<Boolean> = _playButtonEnabled

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val runnable = object : Runnable {
        override fun run() {
            //проверяет, воспроизводится ли в данный момент трек. Обновление времени происходит только тогда, когда трек играет.
            val currentPosition =
                playerInteractor.getCurrentPosition() //получает текущую позицию воспроизведения трека.
            //преобразует текущую позицию из миллисекунд в строку формата "минуты:секунды" и
            // обновляет TextView, чтобы отобразить текущее время воспроизведения.
            _currentPlayingTime.value = currentPosition
            mainHandler.postDelayed(this, 1000) // Обновление каждую секунду
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
    }

    fun playbackControl() {
        val isPlaying = playerInteractor.playbackControl()
        if (isPlaying) {
            _isPlaying.value = true
            mainHandler.post(runnable)
        } else {
            _isPlaying.value = false
            mainHandler.removeCallbacks(runnable)
        }
    }

    fun preparePlayer(previewUrl: String) {
        playerInteractor.preparePlayer(
            previewUrl = previewUrl,
            onPreparedCallback = { onPrepared() },
            onCompleteCallback = { onComplete() }
        )
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        _isPlaying.value = false
        mainHandler.removeCallbacks(runnable)
    }

    private fun onComplete() {
        mainHandler.removeCallbacks(runnable) //останавливает обновление времени воспроизведения.
        _isPlaying.value = false
        _currentPlayingTime.value = 0
    }

    private fun onPrepared() {
        _playButtonEnabled.value =
            true // включает кнопку воспроизведения, делая её активной для пользователя.
    }

}