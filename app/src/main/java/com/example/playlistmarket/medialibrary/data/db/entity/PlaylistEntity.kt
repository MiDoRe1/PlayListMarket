package com.example.playlistmarket.medialibrary.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
class PlaylistEntity (
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long = 0,
    val name: String,
    val description: String,
    val countOfTracks: Long = 0,
    val coverUrl: String = ""
)