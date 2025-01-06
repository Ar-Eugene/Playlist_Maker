package com.example.playlist_maker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.mediateca.domain.db.FavoritesInteractor
import com.example.playlist_maker.mediateca.domain.db.PlaylistInteractor
import com.example.playlist_maker.mediateca.domain.models.Playlist
import com.example.playlist_maker.player.domain.api.PlayerInteractor
import com.example.playlist_maker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _currentPlayingTime = MutableLiveData(0)
    val currentPlayingTime: LiveData<Int> = _currentPlayingTime

    private val _playButtonEnabled = MutableLiveData(false)
    val playButtonEnabled: LiveData<Boolean> = _playButtonEnabled

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite
    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    private var currentTrack: Track? = null

    private var playbackJob: Job? = null // добавил ссылки на корутину.

    init {
        viewModelScope.launch {
            playerInteractor.getPlayerStateFlow()
                .collect { state ->
                    _isPlaying.postValue(state == STATE_PLAYING)
                    if (state == STATE_PLAYING) {
                        startUpdatingCurrentTime()
                    } else {
                        stopUpdatingCurrentTime()
                    }
                }
        }

        // Загрузка плейлистов
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlistsList ->
                _playlists.value = playlistsList
            }
        }
    }

    fun addTrackToPlaylist(playlistId: Int) {
        currentTrack?.let { track ->
            viewModelScope.launch {
                playlistInteractor.addTrackToPlaylist(track.trackId.toInt(), playlistId)
            }
        }
    }
    fun setCurrentTrack(track: Track) {
        currentTrack = track
        viewModelScope.launch {
            val favoriteIds = favoritesInteractor.favoritesTracks().first().map { it.trackId }
            _isFavorite.value = favoriteIds.contains(track.trackId)
        }
    }

    fun onFavoriteClicked() {
        currentTrack?.let { track ->
            viewModelScope.launch {
                if (_isFavorite.value == true) {
                    favoritesInteractor.removeFavoriteTrack(track.trackId)
                    _isFavorite.value = false
                } else {
                    favoritesInteractor.addFavoriteTrack(track)
                    _isFavorite.value = true
                }
            }
        }
    }

    fun playbackControl() {
        val isPlaying = playerInteractor.playbackControl()
        _isPlaying.value = isPlaying

        if (isPlaying) {
            startUpdatingCurrentTime()
        } else {
            stopUpdatingCurrentTime()
        }
    }

    fun preparePlayer(previewUrl: String) {
        try {
            playerInteractor.preparePlayer(
                previewUrl = previewUrl,
                onPreparedCallback = { onPrepared() },
                onCompleteCallback = { onComplete() }
            )
        } catch (e: IllegalStateException) {
            _playButtonEnabled.value = false
            _isPlaying.value = false
            stopUpdatingCurrentTime()
        }
    }

    fun release() {
        stopUpdatingCurrentTime()
        playerInteractor.release()
    }

    private fun onPrepared() {
        _playButtonEnabled.value = true
    }

    private fun onComplete() {
        _isPlaying.value = false
        _currentPlayingTime.value = 0
        stopUpdatingCurrentTime()
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        _isPlaying.value = false
        stopUpdatingCurrentTime()
    }

    private fun startUpdatingCurrentTime() {
        // Если уже есть активная корутина, отменяем её
        playbackJob?.cancel()

        playbackJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                _currentPlayingTime.postValue(playerInteractor.getCurrentPosition())
                delay(300)
            }
        }
    }

    private fun stopUpdatingCurrentTime() {
        playbackJob?.cancel()
        playbackJob = null
    }

    init {
        viewModelScope.launch {
            playerInteractor.getPlayerStateFlow()
                .collect { state ->
                    _isPlaying.postValue(state == STATE_PLAYING)
                    if (state == STATE_PLAYING) {
                        startUpdatingCurrentTime()
                    } else {
                        stopUpdatingCurrentTime()
                    }
                }
        }
    }

    fun preparePlayerIfNeeded() {
        currentTrack?.let { track ->
            if (!isPlaying.value!!) {
                preparePlayer(track.previewUrl)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
        stopUpdatingCurrentTime()// добавил
    }

    private companion object {
        const val STATE_PLAYING = 2
    }
}
