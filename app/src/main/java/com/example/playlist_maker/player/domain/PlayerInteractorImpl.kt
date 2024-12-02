package com.example.playlist_maker.player.domain

import com.example.playlist_maker.player.domain.api.MediaPlayerRepository
import com.example.playlist_maker.player.domain.api.PlayerInteractor
import kotlinx.coroutines.flow.Flow

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

    override fun getCurrentPositionFlow(): Flow<Int> {
        return mediaPlayerRepository.getCurrentPositionFlow()
    }

    override fun getPlayerStateFlow(): Flow<Int> {
        return mediaPlayerRepository.getPlayerStateFlow()
    }

    override fun release() {
        mediaPlayerRepository.release()
    }

}