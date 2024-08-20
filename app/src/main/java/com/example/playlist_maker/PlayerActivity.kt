package com.example.playlist_maker


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker.databinding.ActivityPlayerBinding
import java.text.SimpleDateFormat
import java.util.Locale


class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // возвращаемся на экран "Поиск"
        backarrowButton()

        // Получаем переданные данные
        val trackName = intent.getStringExtra("trackName")
        val artistName = intent.getStringExtra("artistName")
        val trackTimeMillis = intent.getLongExtra("trackTimeMillis", 0)
        val artworkUrl100 = intent.getStringExtra("artworkUrl100")
        val country = intent.getStringExtra("country")
        val collectionName = intent.getStringExtra("collectionName")
        val primaryGenreName = intent.getStringExtra("primaryGenreName")
        val releaseDate = intent.getStringExtra("releaseDate")

        // Устанавливаем данные на View
        binding.trackName.text = trackName
        binding.trackArtist.text = artistName
        binding.textDurationValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
        val artworkUrl512 = artworkUrl100?.replaceAfterLast("/", "512x512bb.jpg")
        Glide.with(this)
            .load(artworkUrl512)
            .placeholder(R.drawable.error_image)
            .error(R.drawable.error_image)
            .transform(RoundedCorners(20))
            .into(binding.albumPosterImage)
        binding.textCountryValue.text = country
        binding.textGenreValue.text = primaryGenreName
        binding.textYearValue.text = SimpleDateFormat("yyyy", Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(releaseDate)!!)
        binding.textAlbumValue.text = collectionName
    }

    // Добавляем обработку возврата результата
    override fun onBackPressed() {
        val isFromHistory = intent.getBooleanExtra("isFromHistory", false)

        val resultIntent = Intent().apply {
            putExtra("isFromHistory", isFromHistory)
        }
        setResult(RESULT_OK, resultIntent)
        super.onBackPressed()
    }

    fun backarrowButton(){
        val backArrowplayButton = binding.backArrow
        backArrowplayButton.setOnClickListener {
            val isFromHistory = intent.getBooleanExtra("isFromHistory", false)

            val resultIntent = Intent().apply {
                putExtra("isFromHistory", isFromHistory)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}