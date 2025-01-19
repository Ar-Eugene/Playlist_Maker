package com.example.playlist_maker.mediateca.domain.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Playlist(
    val id: Int,
    val title: String,
    val description: String?,
    val imagePath: Uri?,
    val trackIds: String?,
    var trackAmount: Int
): Parcelable
