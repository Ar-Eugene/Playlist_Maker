package com.example.playlist_maker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.player.domain.api.PlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(private val playerInteractor: PlayerInteractor) : ViewModel() {

    private val _currentPlayingTime = MutableLiveData(0)
    val currentPlayingTime: LiveData<Int> = _currentPlayingTime

    private val _playButtonEnabled = MutableLiveData(false)
    val playButtonEnabled: LiveData<Boolean> = _playButtonEnabled

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private var playbackJob: Job? = null // добавил ссылки на корутину.

    fun playbackControl() {
        val isPlaying = playerInteractor.playbackControl()
        _isPlaying.value = isPlaying

        if (isPlaying) {//добавил эту обработку
            startUpdatingCurrentTime()
        } else {
            stopUpdatingCurrentTime()
        }
    }

    fun preparePlayer(previewUrl: String) {
        playerInteractor.preparePlayer(
            previewUrl = previewUrl,
            onPreparedCallback = { onPrepared() },
            onCompleteCallback = { onComplete() }
        )
    }

    private fun onPrepared() {
        _playButtonEnabled.value = true
    }

    private fun onComplete() {
        _isPlaying.value = false
        _currentPlayingTime.value = 0
        stopUpdatingCurrentTime()// добавил
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        _isPlaying.value = false
        stopUpdatingCurrentTime()// добавил
    }

    private fun startUpdatingCurrentTime() { // добавил метод
        // Если уже есть активная корутина, отменяем её
        playbackJob?.cancel()

        playbackJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                _currentPlayingTime.postValue(playerInteractor.getCurrentPosition())
                delay(300)
            }
        }
    }

    private fun stopUpdatingCurrentTime() {// добавил метод
        playbackJob?.cancel()
        playbackJob = null
    }

    //    init {
//        viewModelScope.launch {
//            while (true) {
//                if (playerInteractor.isPlaying()) {
//                    _currentPlayingTime.postValue(playerInteractor.getCurrentPosition())
//                }
//                delay(300)
//            }
//        }
//
//        viewModelScope.launch {
//            playerInteractor.getPlayerStateFlow()
//                .collect { state ->
//                    _isPlaying.postValue(state == STATE_PLAYING)
//                }
//        }
//    }
    init {
        viewModelScope.launch {
            playerInteractor.getPlayerStateFlow()
                .collect { state ->
                    _isPlaying.postValue(state == STATE_PLAYING)
                    if (state == STATE_PLAYING) {
                        startUpdatingCurrentTime()
                    } else {
                        stopUpdatingCurrentTime()
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
        stopUpdatingCurrentTime()// добавил
    }

    private companion object {
        const val STATE_PLAYING = 2
    }
}
