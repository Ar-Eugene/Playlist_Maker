package com.example.playlist_maker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker.databinding.TrackItemBinding

class TrackAdapter(private val trackList: List<Track>):RecyclerView.Adapter<TrackAdapter.TrackHolder>() {

    class TrackHolder(item:View):RecyclerView.ViewHolder(item) {
        val binding=TrackItemBinding.bind(item)

        fun bind(track: Track)= with(binding){
            nameOfTheSong.text=track.trackName
            artistName.text=track.artistName
            trackTime.text=track.trackTime
            Glide.with(itemView.context)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.error_image)
                .error(R.drawable.error_image)
                .centerCrop()
                .transform(RoundedCorners(10))
                .into(musicAlbumPicture)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item,parent,false)
        return TrackHolder(view)
    }

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        holder.bind(trackList[position])
    }

    override fun getItemCount(): Int {
        return trackList.size
    }
}