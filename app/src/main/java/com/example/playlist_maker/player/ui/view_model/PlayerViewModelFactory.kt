package com.example.playlist_maker.player.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlist_maker.player.domain.api.PlayerInteractor

class PlayerViewModelFactory(private val playerInteractor: PlayerInteractor) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            return PlayerViewModel(playerInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}