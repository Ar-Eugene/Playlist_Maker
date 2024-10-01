package com.example.playlist_maker.search.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlist_maker.search.domain.interactor.TracksInteractor
import com.example.playlist_maker.search.domain.repository.SearchHistoryRepository

class SearchViewModelFactory(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(tracksInteractor, searchHistoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}