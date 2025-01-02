package com.example.playlist_maker.mediateca.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String?,
    val imagePath: String?,
    @ColumnInfo(name = "track_ids")
    val trackIds: String?,
    @ColumnInfo(name = "track_amount")
    val trackAmount: Int
)
