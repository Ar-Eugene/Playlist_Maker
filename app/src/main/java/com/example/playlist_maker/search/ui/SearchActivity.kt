package com.example.playlist_maker.search.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlist_maker.creator.Creator
import com.example.playlist_maker.R
import com.example.playlist_maker.search.domain.models.Track
import com.example.playlist_maker.databinding.ActivitySearchBinding
import com.example.playlist_maker.search.domain.repository.SearchHistoryRepository
import com.example.playlist_maker.search.domain.interactor.TracksInteractor
import com.example.playlist_maker.player.ui.PlayerActivity
import com.example.playlist_maker.search.ui.view_model.SearchViewModel
import com.example.playlist_maker.search.ui.view_model.SearchViewModelFactory


class SearchActivity : AppCompatActivity() {
    private lateinit var searchHistoryRepository: SearchHistoryRepository
    private lateinit var interactor: TracksInteractor
    private lateinit var binding: ActivitySearchBinding
    private lateinit var handler: Handler
    private lateinit var searchRunnable: Runnable
    private lateinit var progressBar: ProgressBar
    private var editTextContent: String? = null
    private var trackList = ArrayList<Track>()
    private val trackAdapter = TrackAdapter()
    private val historyAdapter = TrackAdapter()
    private var errorShown = false // Флаг, показан ли placeholderError после последнего запроса
    private var isClickAllowed = true
    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        interactor = Creator.provideTracksInteractor()
        searchHistoryRepository = Creator.provideSearchHistoryRepository()


        // Инициализируем ViewModel через фабрику
        viewModel = ViewModelProvider(this, SearchViewModelFactory(interactor, searchHistoryRepository))
            .get(SearchViewModel::class.java)

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
            viewModel.clearHistory() // Вызываем очистку истории через ViewModel
        }
        val backArrowplayButton = binding.backArrow
        backArrowplayButton.setOnClickListener {
            finish()
        }
        clearButton.setOnClickListener {
            inputEditText.text.clear()
            hideKeyboard()
            it.visibility = View.GONE
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            showHistory()
            binding.placeholderError.visibility = View.GONE
            errorShown = false
        }

        // Обработка ввода текста
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                editTextContent = s.toString()
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                searchDebounce()
                if (s.isNullOrEmpty()) {
                    trackList.clear()
                    trackAdapter.notifyDataSetChanged()
                    showHistory()
                    if (errorShown) {
                        binding.placeholderError.visibility = View.GONE
                        errorShown = false
                    }
                } else {
                    hideHistory()
                }
                // Скрываем placeholderError, если пользователь начал вводить текст
                if (errorShown) {
                    binding.placeholderError.visibility = View.GONE
                    errorShown = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        })
        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {
                binding.historyLayout.visibility = View.GONE
                updateHistoryRecyclerView()
                showHistory()
            } else {
                updateHistoryRecyclerView()
                hideHistory()
            }
        }
        binding.refreshButton.setOnClickListener {
            binding.placeholderError.visibility = View.GONE
            search()
        }


        if
                (inputEditText.text.isEmpty() && !inputEditText.hasFocus()) {
            hideHistory()
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
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        trackAdapter.trackList = trackList
        binding.recyclerView.adapter = trackAdapter


        historyAdapter.trackList = ArrayList(searchHistoryRepository.getSearchHistory())
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter
    }


    // Метод для поиска через интерактор
    private fun search() {
        viewModel.search(binding.inputEditText.text.toString())
    }


    // Функция обработки клика на трек (из поиска или истории)
    private fun handleTrackClick(track: Track, isFromHistory: Boolean = false) {
        // Добавляем трек в историю, если его там нет
        searchHistoryRepository.saveSearchHistory(track)
        updateHistoryRecyclerView()
        // Переход в PlayerActivity
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra("previewUrl", track.previewUrl)
            putExtra("trackName", track.trackName)
            putExtra("artistName", track.artistName)
            putExtra("trackTimeMillis", track.trackTimeMillis)
            putExtra("artworkUrl100", track.artworkUrl100)
            putExtra("country", track.country)
            putExtra("collectionName", track.collectionName)
            putExtra("primaryGenreName", track.primaryGenreName)
            putExtra("releaseDate", track.releaseDate)
            putExtra("isFromHistory", isFromHistory) // Добавляем флаг
        }
        startActivityForResult(intent, REQUEST_CODE_PLAYER)
    }


    private fun updateHistoryRecyclerView() {
        historyAdapter.trackList = ArrayList(searchHistoryRepository.getSearchHistory())
        historyAdapter.notifyDataSetChanged()
        val isHistoryEmpty = historyAdapter.trackList.isEmpty()
        // Скрываем historyLayout, если нет данных или EditText в фокусе
        binding.historyLayout.visibility =
            if (isHistoryEmpty || binding.inputEditText.hasFocus()) View.GONE else View.VISIBLE
    }


    private fun showError(imageResId: Int, messageResId: Int, showRefreshButton: Boolean) {
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
        binding.placeholderError.visibility = View.VISIBLE
        binding.placeholderImage.setImageResource(imageResId)
        binding.placeholderMessage.text = getString(messageResId)
        binding.refreshButton.visibility = if (showRefreshButton) View.VISIBLE else View.GONE
        errorShown = true
    }


    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }


    private fun showHistory() {
        if (historyAdapter.trackList.isNotEmpty()) {
            binding.historyLayout.visibility = View.VISIBLE
        } else {
            binding.historyLayout.visibility = View.GONE
        }
    }


    private fun hideHistory() {
        binding.historyLayout.visibility = View.GONE
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(getString(R.string.EDIT_TEXT_CONTENT), editTextContent)
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        editTextContent = savedInstanceState.getString(getString(R.string.EDIT_TEXT_CONTENT))
        findViewById<EditText>(R.id.inputEditText).setText(editTextContent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PLAYER && resultCode == RESULT_OK) {
            val isFromHistory = data?.getBooleanExtra("isFromHistory", false) ?: false
            if (isFromHistory) {
                updateHistoryRecyclerView()
                showHistory()
            }
        }
    }
    private fun observeViewModel() {
        viewModel.tracks.observe(this) { tracks ->
            trackList.clear()
            if (tracks.isNotEmpty()) {
                trackList.addAll(tracks)
                trackAdapter.notifyDataSetChanged()
                binding.placeholderError.visibility = View.GONE
                errorShown = false
            } else {
                showError(R.drawable.search_error, R.string.nothing_found, false)
                errorShown = true
            }
        }


        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }


        viewModel.error.observe(this) { errorMessage ->
            showError(R.drawable.connection_problem, R.string.check_connection, true)
            errorShown = true
        }


        viewModel.history.observe(this) { history ->
            historyAdapter.trackList = ArrayList(history)
            historyAdapter.notifyDataSetChanged()
            showHistory()
        }
    }


    private companion object{
        // для хранения времени отклика
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        // для хранения задержки отклика от пользователя
        const val CLICK_DEBOUNCE_DELAY = 1000L
        // кажется это для хранения передачи данных между поиском и медиаплеером
        const val REQUEST_CODE_PLAYER = 1
    }
}


fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics).toInt()
}

