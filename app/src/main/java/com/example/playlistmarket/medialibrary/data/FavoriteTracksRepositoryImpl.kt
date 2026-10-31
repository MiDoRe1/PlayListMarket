package com.example.playlistmarket.medialibrary.data

import com.example.playlistmarket.medialibrary.data.db.AppDatabase
import com.example.playlistmarket.medialibrary.data.db.entity.TrackEntity
import com.example.playlistmarket.medialibrary.data.dto.TrackDbConvertor
import com.example.playlistmarket.medialibrary.domain.api.FavoriteTracksRepository
import com.example.playlistmarket.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : FavoriteTracksRepository {

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val trackEntities = appDatabase.trackDao().getAllTracks()
        emit(convertFromTrackEntity(trackEntities))

    }

    override fun getTrackById(id: Long): Flow<Track?> = flow {
        val trackEntity = appDatabase.trackDao().getTrackById(id)
        emit(trackEntity?.let{trackDbConvertor.map(trackEntity)})
    }

    override suspend fun insertTrack(track: Track) {
        appDatabase.trackDao().insertTrack(trackDbConvertor.map(track))
    }

    override suspend fun updateTrack(track: Track) {
        appDatabase.trackDao().updateTrack(trackDbConvertor.map(track))
    }

    override suspend fun deleteTrack(track: Track) {
        appDatabase.trackDao().deleteTrack(trackDbConvertor.map(track))
    }

    private fun convertFromTrackEntity(trackEntities: List<TrackEntity>): List<Track> {
        return trackEntities.map { trackEntity -> trackDbConvertor.map(trackEntity) }
    }

}