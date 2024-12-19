package com.example.playlist_maker.mediateca.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlist_maker.databinding.FragmentFavoriteTracksBinding
import com.example.playlist_maker.mediateca.ui.view_model.FavoriteTracksViewModel
import com.example.playlist_maker.mediateca.ui.view_model.FavoritesState
import com.example.playlist_maker.player.ui.PlayerActivity
import com.example.playlist_maker.search.domain.models.Track
import com.example.playlist_maker.search.ui.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private val viewModel: FavoriteTracksViewModel by viewModel()
    private lateinit var binding: FragmentFavoriteTracksBinding
    private val favoritesTrackAdapter = TrackAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        binding.emptyContent.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListener()
        observeViewModel()
    }
    private fun setupRecyclerView() {
        binding.favoriteRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favoritesTrackAdapter
        }
    }

    private fun setupClickListener() {
        favoritesTrackAdapter.onClickedTrack = { track ->
            startActivity(Intent(requireContext(), PlayerActivity::class.java).apply {
                putExtra(EXTRA_TRACK, track)
            })
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoritesState.Content -> showContent(state.tracks)
                FavoritesState.Empty -> showEmpty()
            }
        }
    }

    private fun showContent(tracks: List<Track>) {
        binding.apply {
            emptyContent.visibility = View.GONE
            favoriteRecyclerView.visibility = View.VISIBLE
            favoritesTrackAdapter.trackList = ArrayList(tracks)
            favoritesTrackAdapter.notifyDataSetChanged()
        }
    }

    private fun showEmpty() {
        binding.apply {
            emptyContent.visibility = View.VISIBLE
            favoriteRecyclerView.visibility = View.GONE
        }
    }
    companion object {
        const val EXTRA_TRACK = "track"
        fun newInstance() = FavoriteTracksFragment()
    }

}