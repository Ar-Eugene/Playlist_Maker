package com.example.playlist_maker.mediateca.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.PlaylistItemBigBinding
import com.example.playlist_maker.mediateca.domain.models.Playlist

class PlaylistAdapter : ListAdapter<Playlist, PlaylistAdapter.PlaylistViewHolder>(
    object : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist) = oldItem == newItem
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = PlaylistItemBigBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PlaylistViewHolder(
        private val binding: PlaylistItemBigBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(playlist: Playlist) {
            binding.apply {
                bigNameOfThePlaylist.text = playlist.title
                bigAmountTreks.text = itemView.context.resources.getQuantityString(
                    R.plurals.tracks_count,
                    playlist.trackAmount,
                    playlist.trackAmount
                )

                val context = itemView.context
                val contentResolver = context.contentResolver
                val hasPermission = contentResolver.persistedUriPermissions.any { it.uri == playlist.imagePath && it.isReadPermission }

                if (playlist.imagePath != null && hasPermission) {
                    Glide.with(context)
                        .load(playlist.imagePath)
                        .centerCrop()
                        .into(binding.bigNameOfTheSong)
                } else {
                    bigNameOfTheSong.setImageResource(R.drawable.error_image)
                }
            }
        }
    }
}