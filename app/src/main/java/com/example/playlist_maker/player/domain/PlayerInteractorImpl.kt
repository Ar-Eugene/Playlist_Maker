package com.example.playlist_maker.player.domain

import com.example.playlist_maker.player.domain.api.MediaPlayerRepository
import com.example.playlist_maker.player.domain.api.PlayerInteractor

class PlayerInteractorImpl(private val mediaPlayerRepository: MediaPlayerRepository) :
    PlayerInteractor {

    override fun preparePlayer(
        previewUrl: String,
        onPreparedCallback: () -> Unit,
        onCompleteCallback: () -> Unit
    ) {
        mediaPlayerRepository.preparePlayer(previewUrl, onPreparedCallback, onCompleteCallback)
    }

    override fun pausePlayer() {
        mediaPlayerRepository.pausePlayer()
    }

    override fun playbackControl(): Boolean {
        return mediaPlayerRepository.playbackControl()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayerRepository.getCurrentPosition()
    }

    override fun release() {
        mediaPlayerRepository.release()
    }
}