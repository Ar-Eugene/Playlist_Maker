package com.example.playlist_maker.data.retrofit

import com.example.playlist_maker.domain.Track
import com.google.gson.annotations.SerializedName


data class TreckResponse( @SerializedName("results") val tracks:List<Track>)
