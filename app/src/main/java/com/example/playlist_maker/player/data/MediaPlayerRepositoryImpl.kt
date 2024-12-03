package com.example.playlist_maker.player.data

import android.media.MediaPlayer
import com.example.playlist_maker.player.domain.api.MediaPlayerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MediaPlayerRepositoryImpl(private val mediaPlayer: MediaPlayer?) : MediaPlayerRepository {

    private val _playerState = MutableStateFlow(STATE_DEFAULT)

    override fun preparePlayer(
        previewUrl: String,
        onPreparedCallback: () -> Unit,
        onCompleteCallback: () -> Unit
    ) {
        mediaPlayer?.apply {
            setDataSource(previewUrl)
            prepareAsync()
            setOnPreparedListener {
                _playerState.value = STATE_PREPARED
                onPreparedCallback()
            }
            setOnCompletionListener {
                _playerState.value = STATE_PREPARED
                onCompleteCallback()
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer?.apply {
            start()
            _playerState.value = STATE_PLAYING
        }
    }

    override fun pausePlayer() {
        mediaPlayer?.apply {
            pause()
            _playerState.value = STATE_PAUSED
        }
    }

    override fun playbackControl(): Boolean {
        return when (_playerState.value) {
            STATE_PLAYING -> {
                pausePlayer()
                false
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
                true
            }

            else -> false
        }
    }

    override fun release() {
        mediaPlayer?.release()
    }

    override fun getPlayerStateFlow(): Flow<Int> = _playerState

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun isPlaying(): Boolean {
        return _playerState.value == STATE_PLAYING
    }

    private companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
    }
}
