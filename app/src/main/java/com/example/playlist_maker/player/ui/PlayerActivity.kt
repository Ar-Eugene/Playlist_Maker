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
    private var currentTrack: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeViewModel()
        setupClickListeners()

        // Восстанавливаем трек либо из savedInstanceState, либо из intent
        currentTrack = if (savedInstanceState != null) {
            savedInstanceState.getSerializable(KEY_TRACK) as? Track
        } else {
            intent.getSerializableExtra(EXTRA_TRACK) as? Track
        }

        currentTrack?.let { track ->
            setupTrackInfo(track)
            playerViewModel.setCurrentTrack(track)
            if (savedInstanceState == null) {
                playerViewModel.preparePlayer(track.previewUrl)
            }
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            playButton.setOnClickListener {
                playerViewModel.playbackControl()
            }

            heardLikeButton.setOnClickListener {
                playerViewModel.onFavoriteClicked()
            }

            backArrow.setOnClickListener {
                handleBackPress()
            }
        }
    }

    private fun setupTrackInfo(track: Track) {
        with(binding) {
            trackName.text = track.trackName
            trackArtist.text = track.artistName
            textDurationValue.text = SimpleDateFormat("mm:ss", Locale.getDefault())
                .format(track.trackTimeMillis)

            setupAlbumArtwork(track.artworkUrl100)
            setupAdditionalInfo(track)
        }
    }

    private fun setupAlbumArtwork(artworkUrl: String) {
        val artworkUrl512 = artworkUrl.replaceAfterLast("/", "512x512bb.jpg")
        Glide.with(this)
            .load(artworkUrl512)
            .placeholder(R.drawable.error_image)
            .error(R.drawable.error_image)
            .transform(RoundedCorners(20))
            .into(binding.albumPosterImage)
    }

    private fun setupAdditionalInfo(track: Track) {
        with(binding) {
            textCountryValue.text = track.country
            textGenreValue.text = track.primaryGenreName
            textYearValue.text = formatReleaseDate(track.releaseDate)
            textAlbumValue.text = track.collectionName
        }
    }

    private fun formatReleaseDate(releaseDate: String): String {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val formatter = SimpleDateFormat("yyyy", Locale.getDefault())
        return parser.parse(releaseDate)?.let { formatter.format(it) } ?: ""
    }

    private fun observeViewModel() {
        with(playerViewModel) {
            currentPlayingTime.observe(this@PlayerActivity) { time ->
                binding.textTrackTimeValue.text = if (time == 0) {
                    getString(R.string.current_time)
                } else {
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)
                }
            }

            playButtonEnabled.observe(this@PlayerActivity) { isEnabled ->
                binding.playButton.isEnabled = isEnabled
            }

            isPlaying.observe(this@PlayerActivity) { isPlaying ->
                binding.playButton.setImageResource(
                    if (isPlaying) R.drawable.pause_button else R.drawable.play_button
                )
            }

            isFavorite.observe(this@PlayerActivity) { isFavorite ->
                binding.heardLikeButton.setImageResource(
                    if (isFavorite) R.drawable.favorite_heart_button
                    else R.drawable.heart_button
                )
            }
        }
    }

    private fun handleBackPress() {
        val isFromHistory = intent.getBooleanExtra(EXTRA_IS_FROM_HISTORY, false)
        setResult(
            RESULT_OK,
            Intent().apply {
                putExtra(EXTRA_IS_FROM_HISTORY, isFromHistory)
            }
        )
        finish()
    }

    override fun onBackPressed() {
        handleBackPress()
        super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(KEY_TRACK, currentTrack)
        playerViewModel.pausePlayer()
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            playerViewModel.release()
        }
    }

    private companion object {
        const val EXTRA_TRACK = "track"
        const val EXTRA_IS_FROM_HISTORY = "isFromHistory"
        const val KEY_TRACK = "current_track"
    }
}