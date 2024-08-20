package com.example.playlist_maker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlist_maker.constans.Constants
import com.example.playlist_maker.databinding.ActivitySearchBinding
import com.example.playlist_maker.retrofit.TrackInternet
import com.example.playlist_maker.retrofit.TreckResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {
    private var editTextContent: String? = null
    private var trackList = ArrayList<Track>()
    private val trackAdapter = TrackAdapter()
    private val historyAdapter = TrackAdapter()
    private val trackApi = TrackInternet.trackApi
    private lateinit var historyTrackList: SearchHistory
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivitySearchBinding
    private var errorShown = false // Флаг, показан ли placeholderError после последнего запроса

    companion object {
        private const val REQUEST_CODE_PLAYER = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(Constants.HISTORY_TRACKLIST, Context.MODE_PRIVATE)
        historyTrackList = SearchHistory(sharedPreferences)

        // Настраиваем click listener для адаптера поиска
        trackAdapter.onClickedTrack = { track ->
            handleTrackClick(track, false)
        }

        // Настраиваем click listener для адаптера истории
        historyAdapter.onClickedTrack = { track ->
            handleTrackClick(track, true)
        }

        binding.buttonClearHistory.setOnClickListener {
            historyTrackList.clearHistory()
            updateHistoryRecyclerView()
        }

        buildRecyclerView()

        val backArrowplayButton = binding.backArrow
        backArrowplayButton.setOnClickListener {
            finish()
        }

        val inputEditText = binding.inputEditText
        val clearButton = binding.clearIcon

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

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                editTextContent = s.toString()
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

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
            search()
        }

        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            } else {
                false
            }
        }

        if (inputEditText.text.isEmpty() && !inputEditText.hasFocus()) {
            hideHistory()
        }
    }

    private fun buildRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        trackAdapter.trackList = trackList
        binding.recyclerView.adapter = trackAdapter

        historyAdapter.trackList = ArrayList(historyTrackList.trackListHistory)
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter
    }

    private fun search() {
        trackApi.getTrack(binding.inputEditText.text.toString())
            .enqueue(object : Callback<TreckResponse> {
                override fun onResponse(call: Call<TreckResponse>, response: Response<TreckResponse>) {
                    if (response.code() == 200) {
                        trackList.clear()
                    }
                    if (response.body()?.tracks?.isNotEmpty() == true) {
                        trackList.addAll(response.body()?.tracks!!)
                        trackAdapter.notifyDataSetChanged()
                        binding.placeholderError.visibility = View.GONE
                        errorShown = false
                    } else {
                        showError(R.drawable.search_error, R.string.nothing_found, false)
                        errorShown = true
                    }
                }

                override fun onFailure(call: Call<TreckResponse>, t: Throwable) {
                    showError(R.drawable.connection_problem, R.string.check_connection, true)
                    errorShown = true
                }
            })
    }

    // Функция обработки клика на трек (из поиска или истории)
    private fun handleTrackClick(track: Track, isFromHistory: Boolean = false) {
        // Добавляем трек в историю, если его там нет
        historyTrackList.addTrack(track)
        updateHistoryRecyclerView()

        // Переход в PlayerActivity
        val intent = Intent(this, PlayerActivity::class.java).apply {
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
        historyAdapter.trackList = ArrayList(historyTrackList.trackListHistory)
        historyAdapter.notifyDataSetChanged()
        val isHistoryEmpty = historyAdapter.trackList.isEmpty()

        // Скрываем historyLayout, если нет данных или EditText в фокусе
        binding.historyLayout.visibility = if (isHistoryEmpty || binding.inputEditText.hasFocus()) View.GONE else View.VISIBLE
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
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
}
fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics).toInt()
}