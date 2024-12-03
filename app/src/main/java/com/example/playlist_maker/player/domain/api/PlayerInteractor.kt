package com.example.playlist_maker.player.domain.api

import kotlinx.coroutines.flow.Flow

interface PlayerInteractor {
    fun preparePlayer(
        previewUrl: String,
        onPreparedCallback: () -> Unit,
        onCompleteCallback: () -> Unit
    )

    fun pausePlayer()
    fun playbackControl(): Boolean
    fun release()
    fun getPlayerStateFlow(): Flow<Int>
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
}