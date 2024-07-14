package com.example.playlist_maker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlist_maker.retrofit.TrackApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private var editTextContent: String? = null

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)

        val backArrowplayButton = findViewById<ImageView>(R.id.back_arrow)
        backArrowplayButton.setOnClickListener {
            finish()
        }
        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        clearButton.setOnClickListener {
            inputEditText.text.clear()
            hideKeyboard()
            it.visibility = View.GONE
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
            val tracks = listOf(
                Track(getString(R.string.SmellsLikeTeenSpirit), getString(R.string.Nirvana), getString(R.string.Time5_01),getString(R.string.imageNirvanaUrl)),
                Track(getString(R.string.BillieJean),getString(R.string.MichaelJackson), getString(R.string.Time4_35),getString(R.string.imageMichaelJacksonUrl)),
                Track(getString(R.string.StayinAlive),getString(R.string.BeeGees), getString(R.string.Time4_10),getString(R.string.imageBeeGeesUrl)),
                Track(getString(R.string.WholeLottaLove),getString(R.string.LedZeppelin), getString(R.string.Time5_33),getString(R.string.imageLedZeppelinUrl)),
                Track(getString(R.string.SweetChildOMine),getString(R.string.GunsNRoses), getString(R.string.Time5_03),getString(R.string.imageGunsNRosesUrl)),

            )

            // Создание адаптера с этим списком
            val adapter = TrackAdapter(tracks)

            // Получение RecyclerView и установка адаптера
            val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter



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