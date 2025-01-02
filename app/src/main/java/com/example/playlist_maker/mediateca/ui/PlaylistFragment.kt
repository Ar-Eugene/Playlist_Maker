package com.example.playlist_maker.mediateca.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentPlaylistBinding
import com.example.playlist_maker.mediateca.ui.view_model.PlaylistViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {
    private val viewModel: PlaylistViewModel by viewModel()
    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        binding.placeholderNotPlaylist.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observePlaylists()
    }

    private fun setupRecyclerView() {
        playlistAdapter = PlaylistAdapter()
        binding.recyclerView.apply {
            adapter = playlistAdapter
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        }
    }

    private fun setupClickListeners() {
        binding.addPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediatekaFragment2_to_createPlaylistFragment)
        }
    }

    private fun observePlaylists() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.playlists.collect { playlists ->
                binding.recyclerView.isVisible = playlists.isNotEmpty()
                binding.placeholderNotPlaylist.isVisible = playlists.isEmpty()

                playlistAdapter.submitList(playlists)
            }
        }
    }

    companion object {
        fun newInstance() = PlaylistFragment()
    }
}