package com.example.playlist_maker.player.ui.view_model

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlist_maker.player.ui.MediaPlayerManager

class PlayerViewModelFactory(private val mediaPlayerManager: MediaPlayerManager) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            return PlayerViewModel(mediaPlayerManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}