package com.example.playlist_maker.player.domain

interface PlayerInteractor {
    fun preparePlayer(
        previewUrl: String,
        onPreparedCallback: () -> Unit,
        onCompleteCallback: () -> Unit
    )

    fun pausePlayer()
    fun playbackControl(): Boolean
    fun getCurrentPosition(): Int
    fun release()
}