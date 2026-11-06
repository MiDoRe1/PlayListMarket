package com.example.playlistmarket.medialibrary.data

import android.util.Log
import com.example.playlistmarket.medialibrary.data.db.dao.PlaylistDao
import com.example.playlistmarket.medialibrary.data.db.entity.PlaylistEntity
import com.example.playlistmarket.medialibrary.data.dto.PlaylistDbConvertor
import com.example.playlistmarket.medialibrary.data.dto.TrackDbConvertor
import com.example.playlistmarket.medialibrary.domain.api.PlaylistRepository
import com.example.playlistmarket.medialibrary.domain.models.Playlist
import com.example.playlistmarket.medialibrary.domain.models.PlaylistPattern
import com.example.playlistmarket.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val trackDbConvertor: TrackDbConvertor
): PlaylistRepository {

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlistDbConvertor.map(playlist))
    }

    override suspend fun insertTrackInPlaylist(
        playlist: Playlist,
        track: Track
    ) {

        playlistDao.insertTrackInPlaylist(
            playlistDbConvertor.map(playlist),
            trackDbConvertor.mapToTrackInPlaylist(track)
        )
    }

    override suspend fun deleteTrackFromPlaylist(
        playlist: Playlist,
        track: Track
    ) {
        playlistDao.deleteTrackFromPlaylist(
            playlistDbConvertor.map(playlist),
            trackDbConvertor.mapToTrackInPlaylist(track)
        )
    }

    override suspend fun createPlaylist(playlistPattern: PlaylistPattern): Playlist {
        val creatingPlaylistEntity = PlaylistEntity(
            0,
            playlistPattern.name,
            playlistPattern.description,
            playlistPattern.countOfTracksIn,
            playlistPattern.coverUrl
        )
        val new_id = playlistDao.insertPlaylist(creatingPlaylistEntity)
        return Playlist(
            new_id,
            creatingPlaylistEntity.name,
            creatingPlaylistEntity.description,
            creatingPlaylistEntity.countOfTracks,
            creatingPlaylistEntity.coverUrl
        )
    }

    override suspend fun getTracksFromPlaylist(playlist: Playlist): List<Track> {
        val playlistWithTracks = playlistDao.getPlaylistWithTracks(
            playlistDbConvertor.map(playlist).playlistId
        )
        return playlistWithTracks?.let { data ->
            data.trackEntities.map { TrackEntity -> trackDbConvertor.mapToTrack(TrackEntity) }
        } ?: emptyList()
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> =
        playlistDao.getAllPlaylists().map {playlistEntities ->
            playlistEntities.map { playlistEnt -> playlistDbConvertor.map(playlistEnt)}
        }

    override suspend fun isTrackInPlaylist(
        playlist: Playlist,
        track: Track
    ): Boolean {
        Log.d("addtrack", "looking for: ${playlist.id} --- ${track.trackId}")
        val res =  playlistDao.getPlaylistTracksCrossRef(playlist.id, track.trackId)
        Log.d("addtrack", "base: ${res==null}")
        if (res != null) Log.d("addtrack", "base: ${res}}")
        return res != null
    }

}