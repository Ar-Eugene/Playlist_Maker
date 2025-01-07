package com.example.playlist_maker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.PlaylistItemMineBinding
import com.example.playlist_maker.mediateca.domain.models.Playlist

class BottomSheetPlaylistAdapter : ListAdapter<Playlist, BottomSheetPlaylistAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist) = oldItem == newItem
    }
) {
    private var onItemClickListener: ((Playlist) -> Unit)? = null

    fun setOnItemClickListener(listener: (Playlist) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PlaylistItemMineBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: PlaylistItemMineBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(playlist: Playlist) {
            binding.apply {
                miniNameOfThePlaylist.text = playlist.title
                miniAmountTreks.text = itemView.context.resources.getQuantityString(
                    R.plurals.tracks_count,
                    playlist.trackAmount,
                    playlist.trackAmount
                )

                val context = itemView.context
                val contentResolver = context.contentResolver
                val hasPermission = contentResolver.persistedUriPermissions.any {
                    it.uri == playlist.imagePath && it.isReadPermission
                }

                if (playlist.imagePath != null && hasPermission) {
                    Glide.with(context)
                        .load(playlist.imagePath)
                        .centerCrop()
                        .into(miniMusicAlbumPicture)
                } else {
                    miniMusicAlbumPicture.setImageResource(R.drawable.error_image)
                }

                // Добавляем обработчик клика
                itemView.setOnClickListener {
                    onItemClickListener?.invoke(playlist)
                }
            }
        }
    }
}