package com.example.playlist_maker.mediateca.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.mediateca.domain.db.FavoritesInteractor
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(private val favoritesInteractor: FavoritesInteractor) : ViewModel() {

    private val _state = MutableLiveData<FavoritesState>()
    val state: LiveData<FavoritesState> = _state

    init {
        viewModelScope.launch {
            favoritesInteractor.favoritesTracks()
                .collect { tracks ->
                    _state.value = if (tracks.isEmpty()) {
                        FavoritesState.Empty
                    } else {
                        FavoritesState.Content(tracks)
                    }
                }
        }
    }
}