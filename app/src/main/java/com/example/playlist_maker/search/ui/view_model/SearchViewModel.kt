package com.example.playlist_maker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker.R
import com.example.playlist_maker.search.domain.interactor.SearchHistoryInteractor
import com.example.playlist_maker.search.domain.interactor.TracksInteractor
import com.example.playlist_maker.search.domain.models.Track
import com.example.playlist_maker.search.ui.models.DomainSearchError
import com.example.playlist_maker.search.ui.models.SearchError

//SearchViewModel принимает два параметра в конструкторе:
// interactor для поиска треков и searchHistoryRepository для работы с историей поиска.
class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    // приватная изменяемая (_tracks) и публичная неизменяемая (tracks) LiveData
    // для хранения списка треков. Публичная версия используется для наблюдения за изменениями из Activity.
    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

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
        _error.postValue(null)
        _isLoading.value = true
        tracksInteractor.searchTracks(query, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>) {
                _tracks.postValue(foundTracks)
                _isLoading.postValue(false)
            }

            override fun onError(error: DomainSearchError) {
                when (error) {
                    DomainSearchError.EmptyResult -> {
                        _error.postValue(
                            SearchError(
                                icon = R.drawable.search_error,
                                text = R.string.nothing_found,
                                showRefreshButton = false
                            )
                        )
                    }

                    DomainSearchError.NetworkError -> {
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
                _isLoading.postValue(false)
            }
        })
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