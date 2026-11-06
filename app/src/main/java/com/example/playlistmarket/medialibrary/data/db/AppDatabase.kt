package com.example.playlistmarket.medialibrary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmarket.medialibrary.data.db.dao.FavoriteTrackDao
import com.example.playlistmarket.medialibrary.data.db.dao.PlaylistDao
import com.example.playlistmarket.medialibrary.data.db.entity.FavoriteTrackEntity
import com.example.playlistmarket.medialibrary.data.db.entity.PlaylistEntity
import com.example.playlistmarket.medialibrary.data.db.entity.PlaylistTracksCrossRef
import com.example.playlistmarket.medialibrary.data.db.entity.TrackInPlaylistEntity

@Database(
    version = 8,
    entities = [
        FavoriteTrackEntity::class,
        TrackInPlaylistEntity::class,
        PlaylistEntity::class,
        PlaylistTracksCrossRef::class
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteTrackDao(): FavoriteTrackDao
    abstract fun playlistDao(): PlaylistDao

}