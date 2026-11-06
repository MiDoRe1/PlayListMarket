package com.example.playlistmarket.medialibrary.data.dto

import com.example.playlistmarket.medialibrary.data.db.entity.FavoriteTrackEntity
import com.example.playlistmarket.medialibrary.data.db.entity.TrackInPlaylistEntity
import com.example.playlistmarket.search.domain.models.Track

class TrackDbConvertor {

    fun mapToFavorite(track: Track): FavoriteTrackEntity {
        return FavoriteTrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.isFavorites
        )
    }

    fun mapToFavorite(trackInPlaylistEntity: TrackInPlaylistEntity): FavoriteTrackEntity {
        return FavoriteTrackEntity(
            trackInPlaylistEntity.trackId,
            trackInPlaylistEntity.trackName,
            trackInPlaylistEntity.artistName,
            trackInPlaylistEntity.trackTime,
            trackInPlaylistEntity.artworkUrl100,
            trackInPlaylistEntity.collectionName,
            trackInPlaylistEntity.releaseDate,
            trackInPlaylistEntity.primaryGenreName,
            trackInPlaylistEntity.country,
            trackInPlaylistEntity.previewUrl,
            trackInPlaylistEntity.isFavorites
        )
    }

    fun mapToTrackInPlaylist(track: Track): TrackInPlaylistEntity {
        return TrackInPlaylistEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.isFavorites
        )
    }

    fun mapToTrackInPlaylist(favoriteTrackEntity: FavoriteTrackEntity): TrackInPlaylistEntity {
        return TrackInPlaylistEntity(
            favoriteTrackEntity.trackId,
            favoriteTrackEntity.trackName,
            favoriteTrackEntity.artistName,
            favoriteTrackEntity.trackTime,
            favoriteTrackEntity.artworkUrl100,
            favoriteTrackEntity.collectionName,
            favoriteTrackEntity.releaseDate,
            favoriteTrackEntity.primaryGenreName,
            favoriteTrackEntity.country,
            favoriteTrackEntity.previewUrl,
            favoriteTrackEntity.isFavorites
        )
    }

    fun mapToTrack(trackInPlaylistEntity: TrackInPlaylistEntity): Track {
        return Track(
            trackInPlaylistEntity.trackId,
            trackInPlaylistEntity.trackName,
            trackInPlaylistEntity.artistName,
            trackInPlaylistEntity.trackTime,
            trackInPlaylistEntity.artworkUrl100,
            trackInPlaylistEntity.collectionName,
            trackInPlaylistEntity.releaseDate,
            trackInPlaylistEntity.primaryGenreName,
            trackInPlaylistEntity.country,
            trackInPlaylistEntity.previewUrl,
            trackInPlaylistEntity.isFavorites
        )
    }

    fun mapToTrack(favoriteTrackEntity: FavoriteTrackEntity): Track {
        return Track(
            favoriteTrackEntity.trackId,
            favoriteTrackEntity.trackName,
            favoriteTrackEntity.artistName,
            favoriteTrackEntity.trackTime,
            favoriteTrackEntity.artworkUrl100,
            favoriteTrackEntity.collectionName,
            favoriteTrackEntity.releaseDate,
            favoriteTrackEntity.primaryGenreName,
            favoriteTrackEntity.country,
            favoriteTrackEntity.previewUrl,
            favoriteTrackEntity.isFavorites
        )
    }

}