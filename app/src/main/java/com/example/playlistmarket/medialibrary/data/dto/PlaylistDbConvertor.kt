package com.example.playlistmarket.medialibrary.data.dto

import com.example.playlistmarket.medialibrary.data.db.entity.PlaylistEntity
import com.example.playlistmarket.medialibrary.data.db.entity.TrackEntity
import com.example.playlistmarket.medialibrary.domain.models.Playlist
import com.example.playlistmarket.search.domain.models.Track

class PlaylistDbConvertor {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.id,
            playlist.name,
            playlist.description,
            playlist.countOfTracksIn,
            playlist.coverUrl
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            playlistEntity.playlistId,
            playlistEntity.name,
            playlistEntity.description,
            playlistEntity.countOfTracks,
            playlistEntity.coverUrl
        )
    }
}