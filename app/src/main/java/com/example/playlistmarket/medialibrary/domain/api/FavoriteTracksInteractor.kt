package com.example.playlistmarket.medialibrary.domain.api

import com.example.playlistmarket.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {
    fun getFavoriteTracks(): Flow<List<Track>>
    fun getTrackById(id: Long): Flow<Track?>
    suspend fun insertTrack(track: Track)
    suspend fun updateTrack(track: Track)
    suspend fun deleteTrack(track: Track)

}