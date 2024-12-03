package com.example.playlist_maker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.player.domain.api.PlayerInteractor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(private val playerInteractor: PlayerInteractor) : ViewModel() {

    private val _currentPlayingTime = MutableLiveData(0)
    val currentPlayingTime: LiveData<Int> = _currentPlayingTime

    private val _playButtonEnabled = MutableLiveData(false)
    val playButtonEnabled: LiveData<Boolean> = _playButtonEnabled

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    fun playbackControl() {
        val isPlaying = playerInteractor.playbackControl()
        _isPlaying.value = isPlaying
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
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        _isPlaying.value = false
    }

    init {
        viewModelScope.launch {
            while (true) {
                if (playerInteractor.isPlaying()) {
                    _currentPlayingTime.postValue(playerInteractor.getCurrentPosition())
                }
                delay(300)
            }
        }

        viewModelScope.launch {
            playerInteractor.getPlayerStateFlow()
                .collect { state ->
                    _isPlaying.postValue(state == STATE_PLAYING)
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
    }

    private companion object {
        const val STATE_PLAYING = 2
    }
}
