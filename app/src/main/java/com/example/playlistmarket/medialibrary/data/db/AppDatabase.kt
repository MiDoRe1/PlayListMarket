package com.example.playlistmarket.medialibrary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmarket.medialibrary.data.db.dao.PlaylistDao
import com.example.playlistmarket.medialibrary.data.db.dao.TrackDao
import com.example.playlistmarket.medialibrary.data.db.entity.PlaylistEntity
import com.example.playlistmarket.medialibrary.data.db.entity.PlaylistTracksCrossRef
import com.example.playlistmarket.medialibrary.data.db.entity.PlaylistWithTracks
import com.example.playlistmarket.medialibrary.data.db.entity.TrackEntity

@Database(version = 7, entities = [TrackEntity::class, PlaylistEntity::class, PlaylistTracksCrossRef::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao

    abstract fun playlistDao(): PlaylistDao

}