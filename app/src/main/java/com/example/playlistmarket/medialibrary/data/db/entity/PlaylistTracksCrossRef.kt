package com.example.playlistmarket.medialibrary.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "playlist_track_crossref_table",
    primaryKeys = ["playlistId", "trackId"],
    indices = [Index("trackId")],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["playlistId"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TrackInPlaylistEntity::class,
            parentColumns = ["trackId"],
            childColumns = ["trackId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class PlaylistTracksCrossRef (
    val playlistId: Long,
    val trackId: Long
)
