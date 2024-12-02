package com.example.playlist_maker.player.domain.api

import kotlinx.coroutines.flow.Flow

interface MediaPlayerRepository {
    fun preparePlayer(
        previewUrl: String,
        onPreparedCallback: () -> Unit,
        onCompleteCallback: () -> Unit
    )

    fun pausePlayer()
    fun playbackControl(): Boolean
    fun release()
    fun getCurrentPositionFlow(): Flow<Int>
    fun getPlayerStateFlow(): Flow<Int>
}