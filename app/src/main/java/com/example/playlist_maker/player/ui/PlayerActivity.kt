package com.example.playlist_maker.player.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.ActivityPlayerBinding
import com.example.playlist_maker.player.ui.view_model.PlayerViewModel
import com.example.playlist_maker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private val playerViewModel: PlayerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeViewModel()

        binding.playButton.setOnClickListener {
            playerViewModel.playbackControl()
        }

        // Получаем переданные данные
        val track = intent.getSerializableExtra(EXTRA_TRACK) as? Track
            ?: return  // Используем getSerializableExtra

        // Установка значений на экран
        with(binding) {
            trackName.text = track.trackName
            trackArtist.text = track.artistName
            textDurationValue.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        }
        val artworkUrl512 = track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg")
        Glide.with(this)
            .load(artworkUrl512)
            .placeholder(R.drawable.error_image)
            .error(R.drawable.error_image)
            .transform(RoundedCorners(20))
            .into(binding.albumPosterImage)
        with(binding) {
            textCountryValue.text = track.country
            textGenreValue.text = track.primaryGenreName
            textYearValue.text = SimpleDateFormat(
                "yyyy",
                Locale.getDefault()
            ).format(
                SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss'Z'",
                    Locale.getDefault()
                ).parse(track.releaseDate)!!
            )
            textAlbumValue.text = track.collectionName
        }
        playerViewModel.preparePlayer(track.previewUrl)
        // Возвращаемся на экран "Поиск"
        backArrowButton()
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.pausePlayer()
    }

    //отвечает за подготовку MediaPlayer для воспроизведения аудиотрека.
    override fun onBackPressed() {
        val isFromHistory = intent.getBooleanExtra(EXTRA_IS_FROM_HISTORY, false)
        val resultIntent = Intent().apply {
            putExtra(EXTRA_IS_FROM_HISTORY, isFromHistory)
        }
        setResult(RESULT_OK, resultIntent)
        super.onBackPressed()
    }

    private fun backArrowButton() {
        val backArrowPlayButton = binding.backArrow
        backArrowPlayButton.setOnClickListener {
            val isFromHistory = intent.getBooleanExtra(EXTRA_IS_FROM_HISTORY, false)
            val resultIntent = Intent().apply {
                putExtra(EXTRA_IS_FROM_HISTORY, isFromHistory)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun observeViewModel() {
        playerViewModel.currentPlayingTime.observe(this) { time ->
            binding.textTrackTimeValue.text =
                if (time == 0) getString(R.string.current_time)
                else SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)
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

    private companion object {
        const val EXTRA_TRACK = "track"
        const val EXTRA_IS_FROM_HISTORY = "isFromHistory"
    }
}