package com.example.playlist_maker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker.player.ui.MediaPlayerManager

class PlayerViewModel(private val mediaPlayerManager: MediaPlayerManager) : ViewModel() {

    val updateTimeRunnable: LiveData<Int> = mediaPlayerManager.updateTimeRunnable

    val playButtonEnabled: LiveData<Boolean> = mediaPlayerManager.playButtonEnabled

    val isPlaying: LiveData<Boolean> = mediaPlayerManager.isPlaying

    override fun onCleared() {
        super.onCleared()
        mediaPlayerManager.release()
    }

    fun playbackControl() {
        mediaPlayerManager.playbackControl()
    }

    fun preparePlayer(previewUrl: String) {
        mediaPlayerManager.preparePlayer(previewUrl)
    }

    fun pausePlayer() {
        mediaPlayerManager.pausePlayer()
    }


}