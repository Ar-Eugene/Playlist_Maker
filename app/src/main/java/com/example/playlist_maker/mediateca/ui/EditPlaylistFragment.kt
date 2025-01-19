package com.example.playlist_maker.mediateca.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentEditPlaylistBinding
import com.example.playlist_maker.mediateca.domain.models.Playlist
import com.example.playlist_maker.mediateca.ui.FavoriteTracksFragment.Companion.EXTRA_TRACK
import com.example.playlist_maker.mediateca.ui.view_model.EditPlaylistViewModel
import com.example.playlist_maker.player.ui.PlayerActivity
import com.example.playlist_maker.search.domain.models.Track
import com.example.playlist_maker.search.ui.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class EditPlaylistFragment : Fragment() {
    private lateinit var binding: FragmentEditPlaylistBinding
    private val viewModel: EditPlaylistViewModel by viewModel()
    private lateinit var trackAdapter: TrackAdapter

    private lateinit var bottomSheetContainer: BottomSheetBehavior<LinearLayout>
    private lateinit var menuBottomSheetContainer: BottomSheetBehavior<ConstraintLayout>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetContainer = BottomSheetBehavior.from(binding.trackListBottomSheet)
        bottomSheetContainer.state = BottomSheetBehavior.STATE_COLLAPSED

        menuBottomSheetContainer = BottomSheetBehavior.from(binding.menuBottomSheet)
        menuBottomSheetContainer.state = BottomSheetBehavior.STATE_HIDDEN


        // Получаем плейлист из аргументов
        arguments?.getParcelable<Playlist>("playlist_key")?.let { playlist ->
            viewModel.setPlaylist(playlist)
        }

        // Наблюдаем за данными
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.playlist.collect { playlist ->
                playlist?.let { updateUI(it) }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.length.collect { time ->
                binding.tracksTime.text = resources.getQuantityString(
                    R.plurals.minutes,
                    time,
                    time
                )

            }
        }
        setupRecyclerView()
        setupObservers()
        setupClickListeners()

    }

    private fun setupClickListeners() {
        binding.menuBottomSheetEditPlaylist.setOnClickListener {
            menuBottomSheetContainer.state = BottomSheetBehavior.STATE_HIDDEN

            val bundle = Bundle().apply {
                putParcelable("edit_playlist", viewModel.playlist.value)
                putBoolean("is_edit_mode", true)
            }
            findNavController().navigate(
                R.id.action_editPlaylistFragment_to_createPlaylistFragment,
                bundle
            )
        }
        binding.backArrow.setOnClickListener {
            // Используем NavController для возврата на предыдущий фрагмент
            findNavController().navigateUp()
        }
        binding.btnEdit.setOnClickListener {
            menuBottomSheetContainer.state = BottomSheetBehavior.STATE_EXPANDED
        }
        trackAdapter.onClickedTrack = { track ->
            startActivity(Intent(requireContext(), PlayerActivity::class.java).apply {
                putExtra(EXTRA_TRACK, track)
            })
        }

        setupShareClickListeners()
        setupDeleteClickListener()
    }

    private fun setupShareClickListeners() {
        val shareClickListener = View.OnClickListener {
            menuBottomSheetContainer.state = BottomSheetBehavior.STATE_HIDDEN
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.playlist.value?.let { playlist ->
                    if (playlist.trackAmount == 0) {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.empty_playlist_share_message),
                            Snackbar.LENGTH_LONG
                        ).show()
                    } else {
                        sharePlaylist(playlist)
                    }
                }
            }
        }
        // Применяем один и тот же слушатель к обеим view
        binding.btnShare.setOnClickListener(shareClickListener)
        binding.menuBottomSheetSharePlaylist.setOnClickListener(shareClickListener)
    }

    private fun sharePlaylist(playlist: Playlist) {
        val tracks = viewModel.tracks.value
        val messageBuilder = StringBuilder().apply {
            // Playlist название
            appendLine(playlist.title)

            // Playlist описание
            if (!playlist.description.isNullOrEmpty()) {
                appendLine(playlist.description)
            }

            // Данные о треке
            appendLine(
                resources.getQuantityString(
                    R.plurals.tracks_count,
                    playlist.trackAmount,
                    playlist.trackAmount
                )
            )
            appendLine()

            // Track list
            tracks.forEachIndexed { index, track ->
                val duration = formatDuration(track.trackTimeMillis)
                appendLine("${index + 1}. ${track.artistName} - ${track.trackName} ($duration)")
            }
        }

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, messageBuilder.toString())
        }

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_playlist_title)))
    }

    private fun formatDuration(durationMillis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter().apply {
            onLongClickTrack = { track ->
                showDeleteTrackDialog(track)
            }
        }

        binding.listOfTracksInPlaylist.apply {
            adapter = trackAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_track))
            .setMessage(getString(R.string.delete_track_message))
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                viewModel.deleteTrackFromPlaylist(track.trackId)
                dialog.dismiss()
            }
            .show()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.tracks.collect { tracks ->
                trackAdapter.trackList = ArrayList(tracks)
                trackAdapter.notifyDataSetChanged()
                if (tracks.isEmpty()) {
                    binding.emptyPlaylistMessage.visibility = View.VISIBLE
                    binding.listOfTracksInPlaylist.visibility = View.GONE
                } else {
                    binding.emptyPlaylistMessage.visibility = View.GONE
                    binding.listOfTracksInPlaylist.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun updateUI(playlist: Playlist) {
        binding.apply {
            // Загружаем изображение
            if (playlist.imagePath != null) {
                Glide.with(requireContext())
                    .load(playlist.imagePath)
                    .centerCrop()
                    .into(imageCover)

                Glide.with(requireContext())
                    .load(playlist.imagePath)
                    .centerCrop()
                    .into(menuBottomSheetPlaylistCover)
            } else {
                menuBottomSheetPlaylistCover.setImageResource(R.drawable.error_image)
            }

            // Устанавливаем название
            playlistName.text = playlist.title
            menuBottomSheetPlaylistName.text = playlist.title

            // Устанавливаем описание
            playlistDescription.text = playlist.description

            // Устанавливаем количество треков в меню
            tracksCount.text = resources.getQuantityString(
                R.plurals.tracks_count,
                playlist.trackAmount,
                playlist.trackAmount
            )

            // Устанавливаем количество треков в меню
            menuBottomSheetTracksCount.text = resources.getQuantityString(
                R.plurals.tracks_count,
                playlist.trackAmount,
                playlist.trackAmount
            )
        }
    }

    private fun setupDeleteClickListener() {
        binding.menuBottomSheetDeletePlaylist.setOnClickListener {
            // Скрываем menuBottomSheetContainer
            menuBottomSheetContainer.state = BottomSheetBehavior.STATE_HIDDEN

            // Показываем диалоговое окно
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_playlist_title)
                .setMessage(R.string.delete_playlist_message)
                .setNegativeButton(R.string.no) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(R.string.yes) { _, _ ->
                    lifecycleScope.launch {
                        viewModel.deletePlaylist()
                        findNavController().navigateUp()
                    }
                }
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        menuBottomSheetContainer.state = BottomSheetBehavior.STATE_HIDDEN
        // Обновляем playlist
        arguments?.getParcelable<Playlist>("playlist_key")?.let { playlist ->
            viewModel.setPlaylist(playlist)
            viewModel.refreshPlaylistData()
        }
        viewModel.refreshPlaylistTracks()
    }
}