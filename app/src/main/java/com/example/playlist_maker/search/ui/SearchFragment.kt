package com.example.playlist_maker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentSearchBinding
import com.example.playlist_maker.player.ui.PlayerActivity
import com.example.playlist_maker.search.domain.models.Track
import com.example.playlist_maker.search.ui.models.SearchError
import com.example.playlist_maker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var handler: Handler
    private lateinit var searchRunnable: Runnable
    private lateinit var progressBar: ProgressBar
    private var editTextContent: String? = null
    private var trackList = ArrayList<Track>()
    private val trackAdapter = TrackAdapter()
    private val historyAdapter = TrackAdapter()
    private var isClickAllowed = true
    private val searchViewModel: SearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Подписываемся на обновления данных из ViewModel
        observeViewModel()

        val inputEditText = binding.inputEditText
        val clearButton = binding.clearIcon
        progressBar = binding.progressBar
        progressBar.visibility = View.GONE
        handler = Handler(Looper.getMainLooper())
        searchRunnable = Runnable { search() }

        // Настраиваем click listener для адаптера поиска
        trackAdapter.onClickedTrack = { track ->
            handleTrackClick(track, false)
        }
        // Настраиваем click listener для адаптера истории
        historyAdapter.onClickedTrack = { track ->
            handleTrackClick(track, true)
        }
        binding.buttonClearHistory.setOnClickListener {
            searchViewModel.clearHistory() // Вызываем очистку истории через ViewModel
        }

        clearButton.setOnClickListener {
            inputEditText.text.clear()
            hideKeyboard()
            it.visibility = View.GONE
            searchViewModel.clearTrackList()
        }

        // Обработка ввода текста
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

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

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        })
        inputEditText.setOnFocusChangeListener { _, _ ->
            searchViewModel.updateHistory()
        }

        binding.refreshButton.setOnClickListener {
            search()
        }

        clickDebounce()
        buildRecyclerView()
    }

    // метод, отвечающий за то, что поиск происходит без нажатия на кнопку, а каждые 2 сек после ввода символа
    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        if (!editTextContent.isNullOrEmpty()) {
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }
    }

    // метод, отвечающий за то, что после каждого нажатия будет задержка в 1 сек, чтобы пользователь не нажал еще раз и не было бага
    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
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

    // Метод для поиска через интерактор
    private fun search() {
        searchViewModel.search(binding.inputEditText.text.toString())
    }

    // Функция обработки клика на трек (из поиска или истории)
    private fun handleTrackClick(track: Track, isFromHistory: Boolean = false) {
        // Добавляем трек в историю, если его там нет
        searchViewModel.saveTrackToHistory(track)
        hideHistory()
        // Переход в PlayerActivity
        val intent = Intent(activity, PlayerActivity::class.java).apply {
            putExtra(EXTRA_TRACK, track)  // Передаем объект Track, который реализует Serializable
            putExtra(EXTRA_IS_FROM_HISTORY, isFromHistory)  // Передаем флаг
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(getString(R.string.EDIT_TEXT_CONTENT), editTextContent)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            editTextContent = savedInstanceState.getString(getString(R.string.EDIT_TEXT_CONTENT))
            binding.inputEditText.setText(editTextContent) // используем binding для доступа к inputEditText
        }
    }


    private fun observeViewModel() {
        searchViewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            trackList.clear()
            trackList.addAll(tracks)
            trackAdapter.notifyDataSetChanged()
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
        }

        searchViewModel.error.observe(viewLifecycleOwner) { error ->
            if (error == null) {
                binding.placeholderError.visibility = View.GONE
            } else {
                showError(error)
            }
        }

        searchViewModel.history.observe(viewLifecycleOwner) { history ->
            historyAdapter.trackList = ArrayList(history)
            historyAdapter.notifyDataSetChanged()
            // Скрываем historyLayout, если нет данных или EditText в фокусе
            binding.historyLayout.visibility =
                if (history.isEmpty() || !binding.inputEditText.hasFocus()) View.GONE else View.VISIBLE
        }
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