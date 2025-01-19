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
    private var editingPlaylistId: Int? = null
    private var currentTrackIds: String? = null
    private var currentTrackAmount: Int = 0


    fun initializeWithPlaylist(playlist: Playlist) {
        editingPlaylistId = playlist.id
        playlistTitle = playlist.title
        playlistDescription = playlist.description ?: ""
        selectedImagePath = playlist.imagePath
        currentTrackIds = playlist.trackIds
        currentTrackAmount = playlist.trackAmount
        _hasChanges.value = false
    }

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
    suspend fun updatePlaylist(): Boolean {
        return try {
            if (playlistTitle.isBlank()) return false

            val updatedPlaylist = Playlist(
                id = editingPlaylistId ?: return false,
                title = playlistTitle,
                description = playlistDescription,
                imagePath = selectedImagePath,
                trackIds = currentTrackIds,
                trackAmount = currentTrackAmount
            )
            playlistInteractor.updatePlaylist(updatedPlaylist)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getPlaylistTitle(): String {
        return playlistTitle
    }
    fun isEditMode(): Boolean = editingPlaylistId != null
}