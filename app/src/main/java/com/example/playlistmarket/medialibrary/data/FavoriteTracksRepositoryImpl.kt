package com.example.playlistmarket.medialibrary.data

import com.example.playlistmarket.medialibrary.data.db.dao.FavoriteTrackDao
import com.example.playlistmarket.medialibrary.data.db.entity.FavoriteTrackEntity
import com.example.playlistmarket.medialibrary.data.dto.TrackDbConvertor
import com.example.playlistmarket.medialibrary.domain.api.FavoriteTracksRepository
import com.example.playlistmarket.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val favoriteTrackDao:  FavoriteTrackDao,
    private val trackDbConvertor: TrackDbConvertor
) : FavoriteTracksRepository {

    override  fun getFavoriteTracks(): Flow<List<Track>> =
        favoriteTrackDao.getAllFavoriteTracks().map { trackEntitiesList ->
            convertFromFavoriteTrackEntity(trackEntitiesList)
    }

    override fun getTrackById(id: Long): Flow<Track?> =
        favoriteTrackDao.getFavoriteTrackById(id).map {
            trackEntity ->
            trackEntity?.let { trackEntity ->
                trackDbConvertor.mapToTrack(trackEntity)
            }
        }


    override suspend fun insertTrack(track: Track) {
        val insertTrack = track.copy(isFavorites = true)
        favoriteTrackDao.insertFavoriteTrack(trackDbConvertor.mapToFavorite(insertTrack))
    }


    override suspend fun deleteTrack(track: Track) {
        favoriteTrackDao.deleteFavoriteTrack(trackDbConvertor.mapToFavorite(track))
    }

    private fun convertFromFavoriteTrackEntity(trackEntities: List<FavoriteTrackEntity>): List<Track> {
        return trackEntities.map { trackEntity -> trackDbConvertor.mapToTrack(trackEntity) }
    }


}