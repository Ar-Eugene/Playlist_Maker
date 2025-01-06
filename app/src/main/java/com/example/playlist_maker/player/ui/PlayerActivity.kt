package com.example.playlist_maker.player.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.ActivityPlayerBinding
import com.example.playlist_maker.main.ui.MainActivity
import com.example.playlist_maker.main.ui.MainActivity.Companion.EXTRA_NAVIGATE_TO_CREATE_PLAYLIST
import com.example.playlist_maker.player.ui.view_model.PlayerViewModel
import com.example.playlist_maker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private val playerViewModel: PlayerViewModel by viewModel()
    private var currentTrack: Track? = null
    private val bottomSheetPlaylistAdapter = BottomSheetPlaylistAdapter()

    private val bottomSheetContainer by lazy {
        BottomSheetBehavior.from(binding.bottomSheet)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        bottomSheetContainer.state = BottomSheetBehavior.STATE_HIDDEN
        setupBottomSheet()
        setupObservers()
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
    private fun setupBottomSheet() {
        binding.bottomList.apply {
            adapter = bottomSheetPlaylistAdapter
            layoutManager = LinearLayoutManager(this@PlayerActivity)
        }

        bottomSheetPlaylistAdapter.setOnItemClickListener { playlist ->
            playerViewModel.addTrackToPlaylist(playlist.id)
        }
    }

    private fun setupObservers() {
        playerViewModel.playlists.observe(this) { playlists ->
            bottomSheetPlaylistAdapter.submitList(playlists)
        }

        playerViewModel.addToPlaylistResult.observe(this) { result ->
            when (result) {
                is PlayerViewModel.AddToPlaylistResult.Added -> {
                    bottomSheetContainer.state = BottomSheetBehavior.STATE_HIDDEN
                    Toast.makeText(
                        this,
                        getString(R.string.added_to_playlist, result.playlistName),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is PlayerViewModel.AddToPlaylistResult.AlreadyExists -> {
                    // Не закрываем BottomSheet
                    Toast.makeText(
                        this,
                        getString(R.string.track_already_in_playlist, result.playlistName),
                        Toast.LENGTH_SHORT
                    ).show()
                }
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
            newPlaylist.setOnClickListener {
                navigateToCreatePlaylist()
            }
            addToPlaylistButton.setOnClickListener {
                // Показываем BottomSheet при нажатии на кнопку
                bottomSheetContainer.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun navigateToCreatePlaylist() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_NAVIGATE_TO_CREATE_PLAYLIST, true)
        }
        startActivity(intent)
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

    override fun onResume() {
        super.onResume()
        playerViewModel.preparePlayerIfNeeded()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(KEY_TRACK, currentTrack)
        playerViewModel.pausePlayer()
        outState.putBoolean("isPlaying", playerViewModel.isPlaying.value ?: false)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val isPlaying = savedInstanceState.getBoolean("isPlaying", false)
        if (isPlaying) {
            playerViewModel.playbackControl() // Запускаем воспроизведение, если было запущено
        }
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