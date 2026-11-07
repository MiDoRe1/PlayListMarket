package com.example.playlistmarket.medialibrary.domain.api

import com.example.playlistmarket.medialibrary.domain.models.Playlist
import com.example.playlistmarket.medialibrary.domain.models.PlaylistPattern
import com.example.playlistmarket.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun insertTrackInPlaylist(playlist: Playlist, track: Track)
    suspend fun deleteTrackFromPlaylist(playlist: Playlist, track: Track)
    suspend fun createPlaylist(playlistPattern: PlaylistPattern): Playlist
    suspend fun getTracksFromPlaylist(playlist: Playlist): List<Track>

    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun isTrackInPlaylist(playlist: Playlist, track: Track): Boolean
}