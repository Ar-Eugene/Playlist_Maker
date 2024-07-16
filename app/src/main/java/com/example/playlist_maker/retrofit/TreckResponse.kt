package com.example.playlist_maker.retrofit

import com.example.playlist_maker.Track
import com.google.gson.annotations.SerializedName


data class TreckResponse( @SerializedName("results") val tracks:List<Track>)
