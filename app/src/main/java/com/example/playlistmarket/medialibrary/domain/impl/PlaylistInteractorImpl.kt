package com.example.playlistmarket.medialibrary.domain.impl

import com.example.playlistmarket.medialibrary.domain.api.PlaylistInteractor
import com.example.playlistmarket.medialibrary.domain.api.PlaylistRepository
import com.example.playlistmarket.medialibrary.domain.models.Playlist
import com.example.playlistmarket.medialibrary.domain.models.PlaylistPattern
import com.example.playlistmarket.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
): PlaylistInteractor {
    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }

    override suspend fun insertTrackInPlaylist(
        playlist: Playlist,
        track: Track
    ) {
        playlistRepository.insertTrackInPlaylist(playlist, track)
    }

    override suspend fun deleteTrackFromPlaylist(
        playlist: Playlist,
        track: Track
    ) {
        playlistRepository.deleteTrackFromPlaylist(playlist, track)
    }

    override suspend fun createPlaylist(playlistPattern: PlaylistPattern): Playlist {
        return playlistRepository.createPlaylist(playlistPattern)
    }

    override suspend fun getTracksFromPlaylist(playlist: Playlist): List<Track> {
        return playlistRepository.getTracksFromPlaylist(playlist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> =
            playlistRepository.getAllPlaylists().flowOn(Dispatchers.IO)


    override suspend fun isTrackInPlaylist(
        playlist: Playlist,
        track: Track
    ): Boolean {
        return playlistRepository.isTrackInPlaylist(playlist, track)
    }
}