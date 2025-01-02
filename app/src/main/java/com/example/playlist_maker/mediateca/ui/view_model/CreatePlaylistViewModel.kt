package com.example.playlist_maker.mediateca.ui.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker.mediateca.domain.db.PlaylistInteractor
import com.example.playlist_maker.mediateca.domain.models.Playlist

class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {
    private val _hasChanges = MutableLiveData(false)
    val hasChanges: LiveData<Boolean> = _hasChanges

    private var selectedImagePath: Uri? = null
    private var playlistTitle: String = ""
    private var playlistDescription: String = ""

    fun setChangesMade(changesMade: Boolean) {
        _hasChanges.value = changesMade
    }

    fun setImagePath(uri: Uri?) {
        selectedImagePath = uri
        setChangesMade(true)
    }

    fun setPlaylistTitle(title: String) {
        playlistTitle = title
        setChangesMade(true)
    }

    fun setPlaylistDescription(description: String) {
        playlistDescription = description
        setChangesMade(true)
    }

    suspend fun createPlaylist(): Boolean {
        return try {
            val playlist = Playlist(
                id = 0,
                title = playlistTitle,
                description = playlistDescription,
                imagePath = selectedImagePath,
                trackIds = null,
                trackAmount = 0
            )
            playlistInteractor.createPlaylist(playlist)
            true
        } catch (e: Exception) {
            false
        }
    }
}