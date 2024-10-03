package com.example.playlist_maker.player.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker.R
import com.example.playlist_maker.creator.Creator
import com.example.playlist_maker.databinding.ActivityPlayerBinding
import com.example.playlist_maker.player.ui.view_model.PlayerViewModel
import com.example.playlist_maker.player.ui.view_model.PlayerViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var playerViewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playerViewModel = ViewModelProvider(
            this,
            PlayerViewModelFactory(Creator.provideMediaPlayerManager())
        )[PlayerViewModel::class.java]

        observeViewModel()

        binding.playButton.setOnClickListener {
            playerViewModel.playbackControl()
        }

        // Получаем переданные данные
        val previewUrl = intent.getStringExtra("previewUrl") ?: return
        val trackName = intent.getStringExtra("trackName")
        val artistName = intent.getStringExtra("artistName")
        val trackTimeMillis = intent.getLongExtra("trackTimeMillis", 0)
        val artworkUrl100 = intent.getStringExtra("artworkUrl100")
        val country = intent.getStringExtra("country")
        val collectionName = intent.getStringExtra("collectionName")
        val primaryGenreName = intent.getStringExtra("primaryGenreName")
        val releaseDate = intent.getStringExtra("releaseDate")

        // Установка значений на экран
        binding.trackName.text = trackName
        binding.trackArtist.text = artistName
        binding.textDurationValue.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
        val artworkUrl512 = artworkUrl100?.replaceAfterLast("/", "512x512bb.jpg")
        Glide.with(this)
            .load(artworkUrl512)
            .placeholder(R.drawable.error_image)
            .error(R.drawable.error_image)
            .transform(RoundedCorners(20))
            .into(binding.albumPosterImage)
        binding.textCountryValue.text = country
        binding.textGenreValue.text = primaryGenreName
        binding.textYearValue.text = SimpleDateFormat(
            "yyyy",
            Locale.getDefault()
        ).format(
            SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                Locale.getDefault()
            ).parse(releaseDate)!!
        )
        binding.textAlbumValue.text = collectionName

        playerViewModel.preparePlayer(previewUrl)
        // Возвращаемся на экран "Поиск"
        backArrowButton()
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.pausePlayer()
    }

    //отвечает за подготовку MediaPlayer для воспроизведения аудиотрека.
    override fun onBackPressed() {
        val isFromHistory = intent.getBooleanExtra("isFromHistory", false)
        val resultIntent = Intent().apply {
            putExtra("isFromHistory", isFromHistory)
        }
        setResult(RESULT_OK, resultIntent)
        super.onBackPressed()
    }

    private fun backArrowButton() {
        val backArrowPlayButton = binding.backArrow
        backArrowPlayButton.setOnClickListener {
            val isFromHistory = intent.getBooleanExtra("isFromHistory", false)
            val resultIntent = Intent().apply {
                putExtra("isFromHistory", isFromHistory)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun observeViewModel() {
        playerViewModel.updateTimeRunnable.observe(this) { time ->
            if (time == 0) {
                binding.textTrackTimeValue.text = getString(R.string.current_time)
            } else {
                binding.textTrackTimeValue.text =
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)
            }
        }

        playerViewModel.playButtonEnabled.observe(this) { isEnabled ->
            binding.playButton.isEnabled = isEnabled
        }

        playerViewModel.isPlaying.observe(this) { isPlaying ->
            if (isPlaying) {
                binding.playButton.setImageResource(R.drawable.pause_button)
            } else {
                binding.playButton.setImageResource(R.drawable.play_button)
            }
        }
    }
}