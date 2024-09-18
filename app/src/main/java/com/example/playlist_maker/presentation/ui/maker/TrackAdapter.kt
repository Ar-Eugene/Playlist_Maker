package com.example.playlist_maker.presentation.ui.maker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker.R
import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.databinding.TrackItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TrackAdapter:RecyclerView.Adapter<TrackAdapter.TrackHolder>() {
    var trackList = ArrayList<Track>()
    var onClickedTrack: ((Track) -> Unit)? = null
    inner class TrackHolder(item:View):RecyclerView.ViewHolder(item) {
        val binding=TrackItemBinding.bind(item)

        fun bind(track: Track)= with(binding){
            nameOfTheSong.text=track.trackName
            artistName.text=track.artistName
            trackTime.text= SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
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

        holder.itemView.setOnClickListener {
            onClickedTrack?.invoke(trackList[position])
        }
    }
    override fun getItemCount(): Int {
        return trackList.size
    }

}