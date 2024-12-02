package com.example.playlist_maker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.R
import com.example.playlist_maker.search.domain.interactor.SearchHistoryInteractor
import com.example.playlist_maker.search.domain.interactor.TracksInteractor
import com.example.playlist_maker.search.domain.models.Track
import com.example.playlist_maker.search.ui.models.SearchError
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    // приватная изменяемая (_tracks) и публичная неизменяемая (tracks) LiveData
    // для хранения списка треков. Публичная версия используется для наблюдения за изменениями из Activity.
    private val _tracks = MutableLiveData<List<Track>?>()
    val tracks: LiveData<List<Track>?> = _tracks

    // отслеживания состояния загрузки.
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    //для хранения сообщений об ошибках.
    private val _error = MutableLiveData<SearchError?>()
    val error: LiveData<SearchError?> = _error

    //для хранения истории поиска.
    private val _history = MutableLiveData<List<Track>>()
    val history: LiveData<List<Track>> = _history

    fun search(query: String) {
        viewModelScope.launch {
            _error.postValue(null) // Сбрасываем предыдущую ошибку
            _isLoading.postValue(true) // Устанавливаем состояние загрузки

            tracksInteractor.searchTracks(query)
                .collect { (foundTracks, errorMessage) ->
                    if (errorMessage != null) {
                        // Обрабатываем ошибку
                        when (errorMessage) {
                            "No results found" -> {
                                _error.postValue(
                                    SearchError(
                                        icon = R.drawable.search_error,
                                        text = R.string.nothing_found,
                                        showRefreshButton = false
                                    )
                                )
                            }

                            else -> {
                                _error.postValue(
                                    SearchError(
                                        icon = R.drawable.connection_problem,
                                        text = R.string.check_connection,
                                        showRefreshButton = true
                                    )
                                )
                            }
                        }
                        _tracks.postValue(emptyList())
                    } else if (foundTracks != null) {
                        // Обрабатываем успешный результат
                        _tracks.postValue(foundTracks)
                    }

                    _isLoading.postValue(false) // Завершаем состояние загрузки
                }
        }
    }

    fun saveTrackToHistory(track: Track) {
        searchHistoryInteractor.saveTrackToHistory(track)
        updateHistory()
    }

    fun clearHistory() {
        searchHistoryInteractor.clearSearchHistory()
        updateHistory()
    }

    fun updateHistory() {
        _history.value = searchHistoryInteractor.getSearchHistory()
    }

    fun clearTrackList() {
        _tracks.postValue(emptyList())
        _error.postValue(null)
        updateHistory()
    }
}