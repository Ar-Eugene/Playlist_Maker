package com.example.playlist_maker

import android.content.Context
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
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
    private val trackApi = TrackInternet.trackApi

    private lateinit var binding: ActivitySearchBinding

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                editTextContent = s.toString()
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        })

            // Получение RecyclerView и установка адаптера
            val recyclerView = binding.recyclerView
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = trackAdapter

            binding.refreshButton.setOnClickListener {
                search()
            }
            binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    search() // ВЫПОЛНЯЙТЕ ПОИСКОВЫЙ ЗАПРОС ЗДЕСЬ
                    true
                } else {
                    false
                }
            }


        }
    private fun buildRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        trackAdapter.trackList = trackList
        binding.recyclerView.adapter = trackAdapter
    }
    private fun search() {

        trackApi.getTrack(binding.inputEditText.text.toString())
            .enqueue(object : Callback<TreckResponse> {
                override fun onResponse(call: Call<TreckResponse>, response: Response<TreckResponse>) {
                    if (response.code() == 200) {
                        trackList.clear()
                    }
                    if (response.body()?.tracks?.isNotEmpty() == true){
                        trackList.addAll(response.body()?.tracks!!)
                        trackAdapter.notifyDataSetChanged()
                        // Скрыть сообщение об ошибке, если треки найдены
                        binding.placeholderError.visibility = View.GONE
                    }else{
                        //очищаем список и обновляем адаптер
                        showError(R.drawable.search_error, R.string.nothing_found, false)

                    }

                }

                override fun onFailure(call: Call<TreckResponse>, t: Throwable) {
                    //очищаем список и обновляем адаптер
                    showError(R.drawable.connection_problem, R.string.check_connection, true)
                }
            })
    }
    private fun showError(imageResId: Int, messageResId: Int, showRefreshButton: Boolean) {
        // Очистить список треков и обновить адаптер
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
        // Показать сообщение об ошибке
        binding.placeholderError.visibility = View.VISIBLE
        binding.placeholderImage.setImageResource(imageResId)
        binding.placeholderMessage.text = getString(messageResId)
        binding.refreshButton.visibility = if (showRefreshButton) View.VISIBLE else View.GONE

    }
    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
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

}
fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics).toInt()
}