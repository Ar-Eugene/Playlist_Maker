package com.example.playlist_maker.mediateca.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentEditPlaylistBinding
import com.example.playlist_maker.databinding.FragmentFavoriteTracksBinding
import com.example.playlist_maker.mediateca.domain.models.Playlist
import com.example.playlist_maker.mediateca.ui.view_model.EditPlaylistViewModel
import com.example.playlist_maker.search.ui.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class EditPlaylistFragment : Fragment() {
    private lateinit var binding: FragmentEditPlaylistBinding
    private val viewModel: EditPlaylistViewModel by viewModel()
    private lateinit var trackAdapter: TrackAdapter

    private val bottomSheetContainer by lazy {
        BottomSheetBehavior.from(binding.menuBottomSheet)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetContainer.state = BottomSheetBehavior.STATE_EXPANDED
        binding.backArrow.setOnClickListener {
            // Используем NavController для возврата на предыдущий фрагмент
            findNavController().navigateUp()
        }
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
        setupRecyclerView()
        setupObservers()

    }
    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter()
        binding.listOfTracksInPlaylist.apply {
            adapter = trackAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.tracks.collect { tracks ->
                trackAdapter.trackList = ArrayList(tracks)
                trackAdapter.notifyDataSetChanged()
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
    override fun onResume() {
        super.onResume()
        viewModel.refreshPlaylistTracks()
    }
}