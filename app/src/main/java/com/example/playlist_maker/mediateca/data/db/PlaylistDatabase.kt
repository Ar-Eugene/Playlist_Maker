package com.example.playlist_maker.mediateca.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlist_maker.mediateca.data.db.dao.PlaylistDao
import com.example.playlist_maker.mediateca.data.db.entity.PlaylistEntity

@Database(version = 1, entities = [PlaylistEntity::class])
abstract class PlaylistDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
}