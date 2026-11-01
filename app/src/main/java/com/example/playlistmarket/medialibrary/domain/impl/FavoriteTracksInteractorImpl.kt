package com.example.playlistmarket.medialibrary.domain.impl

import com.example.playlistmarket.medialibrary.domain.api.FavoriteTracksInteractor
import com.example.playlistmarket.medialibrary.domain.api.FavoriteTracksRepository
import com.example.playlistmarket.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class FavoriteTracksInteractorImpl(
    private val favoriteTracksRepository: FavoriteTracksRepository
) : FavoriteTracksInteractor {
    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTracksRepository.getFavoriteTracks()
    }

    override fun getTrackById(id: Long): Flow<Track?> {
        return favoriteTracksRepository.getTrackById(id)
    }

    override suspend fun insertTrack(track: Track) {
        withContext(Dispatchers.IO) {
            favoriteTracksRepository.insertTrack(track)
        }
    }

    override suspend fun updateTrack(track: Track) {
        withContext(Dispatchers.IO) {
        favoriteTracksRepository.updateTrack(track)
        }
    }

    override suspend fun deleteTrack(track: Track) {
        withContext(Dispatchers.IO) {
        favoriteTracksRepository.deleteTrack(track)
        }
    }
}