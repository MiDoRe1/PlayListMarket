package com.example.playlistmarket.medialibrary.data

import com.example.playlistmarket.medialibrary.data.db.AppDatabase
import com.example.playlistmarket.medialibrary.data.db.dao.TrackDao
import com.example.playlistmarket.medialibrary.data.db.entity.TrackEntity
import com.example.playlistmarket.medialibrary.data.dto.TrackDbConvertor
import com.example.playlistmarket.medialibrary.domain.api.FavoriteTracksRepository
import com.example.playlistmarket.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val trackDao:  TrackDao,
    private val trackDbConvertor: TrackDbConvertor
) : FavoriteTracksRepository {

    override  fun getFavoriteTracks(): Flow<List<Track>> =
        trackDao.getAllTracks().map { trackEntitiesList ->
            convertFromTrackEntity(trackEntitiesList)
    }

    override fun getTrackById(id: Long): Flow<Track?> =
        trackDao.getTrackById(id).map {
            trackEntity ->
            trackEntity?.let{trackDbConvertor.map(trackEntity)
            }
        }


    override suspend fun insertTrack(track: Track) {
        trackDao.insertTrack(trackDbConvertor.map(track))
    }

    override suspend fun updateTrack(track: Track) {
        trackDao.updateTrack(trackDbConvertor.map(track))
    }

    override suspend fun deleteTrack(track: Track) {
        trackDao.deleteTrack(trackDbConvertor.map(track))
    }

    private fun convertFromTrackEntity(trackEntities: List<TrackEntity>): List<Track> {
        return trackEntities.map { trackEntity -> trackDbConvertor.map(trackEntity) }
    }

}