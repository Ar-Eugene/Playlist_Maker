package com.example.playlist_maker.mediateca.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlist_maker.mediateca.data.db.dao.PlaylistDao
import com.example.playlist_maker.mediateca.data.db.dao.PlaylistTrackDao
import com.example.playlist_maker.mediateca.data.db.entity.PlaylistEntity
import com.example.playlist_maker.mediateca.data.db.entity.PlaylistTrackEntity

@Database(version = 1, entities = [PlaylistEntity::class, PlaylistTrackEntity::class])
abstract class PlaylistDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao
}