package com.example.playlist_maker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlist_maker.databinding.FragmentSearchBinding
import com.example.playlist_maker.player.ui.PlayerActivity
import com.example.playlist_maker.search.domain.models.Track
import com.example.playlist_maker.search.ui.models.SearchError
import com.example.playlist_maker.search.ui.view_model.SearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var progressBar: ProgressBar
    private var editTextContent: String? = null
    private var trackList = ArrayList<Track>()
    private val trackAdapter = TrackAdapter()
    private val historyAdapter = TrackAdapter()
    private var isClickAllowed = true
    private val searchViewModel: SearchViewModel by viewModel()

    private var debounceJob: Job? = null
    private var clickJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Обновляем историю поиска при возвращении в фрагмент
        searchViewModel.updateHistory()
        updateHistoryVisibility()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()

        val inputEditText = binding.inputEditText
        val clearButton = binding.clearIcon
        progressBar = binding.progressBar
        progressBar.visibility = View.GONE

        trackAdapter.onClickedTrack = { track ->
            handleTrackClick(track, false)
        }
        historyAdapter.onClickedTrack = { track ->
            handleTrackClick(track, true)
        }
        binding.buttonClearHistory.setOnClickListener {
            searchViewModel.clearHistory()
        }

        clearButton.setOnClickListener {
            inputEditText.text.clear()
            hideKeyboard()
            it.visibility = View.GONE
            searchViewModel.clearTrackList()
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                editTextContent = s.toString()
                searchDebounce()
                if (s.isNullOrEmpty()) {
                    clearButton.visibility = View.GONE
                    searchViewModel.clearTrackList()
                } else {
                    hideHistory()
                    clearButton.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        inputEditText.setOnFocusChangeListener { _, _ ->
            searchViewModel.updateHistory() // Обновляем историю, когда поле получает фокус
        }

        binding.refreshButton.setOnClickListener {
            search()
        }
        clickDebounce()
        buildRecyclerView()
    }

    private fun searchDebounce() {
        debounceJob?.cancel()
        debounceJob = lifecycleScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            if (!editTextContent.isNullOrEmpty()) {
                search()
            }
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            clickJob?.cancel()
            clickJob = lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun buildRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        trackAdapter.trackList = trackList
        binding.recyclerView.adapter = trackAdapter

        binding.historyRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.historyRecyclerView.adapter = historyAdapter
        searchViewModel.updateHistory()
    }

    private fun search() {
        searchViewModel.search(binding.inputEditText.text.toString())
    }

    private fun handleTrackClick(track: Track, isFromHistory: Boolean = false) {
        searchViewModel.saveTrackToHistory(track)
        hideHistory()
        val intent = Intent(activity, PlayerActivity::class.java).apply {
            putExtra(EXTRA_TRACK, track)
            putExtra(EXTRA_IS_FROM_HISTORY, isFromHistory)
        }
        startActivityForResult(intent, REQUEST_CODE_PLAYER)
    }

    private fun showError(error: SearchError) {
        with(binding) {
            placeholderError.visibility = View.VISIBLE
            placeholderImage.setImageResource(error.icon)
            placeholderMessage.text = getString(error.text)
            refreshButton.visibility = if (error.showRefreshButton) View.VISIBLE else View.GONE
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        view?.let {
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun hideHistory() {
        binding.historyLayout.visibility = View.GONE
    }

    private fun observeViewModel() {
        searchViewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            trackList.clear()
            tracks?.let {
                trackList.addAll(it)
            }
            trackAdapter.notifyDataSetChanged()
            updateHistoryVisibility()
        }

        searchViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.recyclerView.visibility = View.GONE
                binding.historyLayout.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
            updateHistoryVisibility()
        }

        searchViewModel.error.observe(viewLifecycleOwner) { error ->
            if (error == null) {
                binding.placeholderError.visibility = View.GONE
            } else {
                showError(error)
            }
            updateHistoryVisibility()
        }

        searchViewModel.history.observe(viewLifecycleOwner) { history ->
            historyAdapter.trackList = ArrayList(history)
            historyAdapter.notifyDataSetChanged()
            updateHistoryVisibility()
        }

    }

    private fun updateHistoryVisibility() {
        val hasTracks = trackList.isNotEmpty()
        val hasError = searchViewModel.error.value != null
        val isInputFocused = binding.inputEditText.hasFocus()
        val hasHistory = searchViewModel.history.value?.isNotEmpty() == true
        val isLoading = progressBar.visibility == View.VISIBLE
        binding.historyLayout.visibility =
            if (isInputFocused && !hasTracks && !hasError && hasHistory && !isLoading) View.VISIBLE else View.GONE
    }

    private companion object {
        // для хранения времени отклика
        const val SEARCH_DEBOUNCE_DELAY = 2000L

        // для хранения задержки отклика от пользователя
        const val CLICK_DEBOUNCE_DELAY = 1000L

        // кажется это для хранения передачи данных между поиском и медиаплеером
        const val REQUEST_CODE_PLAYER = 1
        const val EXTRA_TRACK = "track"
        const val EXTRA_IS_FROM_HISTORY = "isFromHistory"
    }
}