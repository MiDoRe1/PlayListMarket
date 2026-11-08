package com.example.playlistmarket.medialibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.playlistmarket.medialibrary.data.db.entity.PlaylistEntity
import com.example.playlistmarket.medialibrary.data.db.entity.PlaylistTracksCrossRef
import com.example.playlistmarket.medialibrary.data.db.entity.PlaylistWithTracks
import com.example.playlistmarket.medialibrary.data.db.entity.TrackInPlaylistEntity
import kotlinx.coroutines.flow.Flow
import retrofit2.http.DELETE

@Dao
interface PlaylistDao {

    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity): Long

    @Update(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePlaylist(playlistEntity: PlaylistEntity): Int
    @Insert(entity = TrackInPlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackIfNotExist(trackEntity: TrackInPlaylistEntity)

    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistIfNotExist(playlistEntity: PlaylistEntity)

    @Insert(entity = PlaylistTracksCrossRef::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackInPlaylistUsingCrossRef(playlistTracksCrossRef: PlaylistTracksCrossRef): Long

    @Transaction
    suspend fun insertTrackInPlaylist(playlistEntity: PlaylistEntity, trackInPlaylistEntity: TrackInPlaylistEntity) {
        insertTrackIfNotExist(trackInPlaylistEntity)
        insertPlaylistIfNotExist(playlistEntity)
        val newRowId = insertTrackInPlaylistUsingCrossRef(
            PlaylistTracksCrossRef(playlistEntity.playlistId, trackInPlaylistEntity.trackId)
        )
        if (newRowId > 0) incrementTrackCount(playlistEntity.playlistId)
    }

    @Transaction
    suspend fun deleteTrackFromPlaylist(playlistEntity: PlaylistEntity, trackInPlaylistEntity: TrackInPlaylistEntity) {
        val deleteRowsCount = deleteTrackFromListById(playlistEntity.playlistId, trackInPlaylistEntity.trackId)
        if (deleteRowsCount > 0) decrementTrackCount(playlistEntity.playlistId)
        val isDeletingTrack = !isTrackInAnyPlaylist(trackInPlaylistEntity.trackId)
        if(isDeletingTrack) fullDeleteTrackById(trackInPlaylistEntity.trackId)

    }

    @Query("DELETE FROM track_in_playlist_table WHERE trackId = :trackId")
    suspend fun fullDeleteTrackById(trackId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM playlist_track_crossref_table WHERE trackId = :trackId)")
    suspend fun isTrackInAnyPlaylist(trackId: Long): Boolean

    @Query("UPDATE playlist_table SET countOfTracks = countOfTracks + 1 WHERE playlistId = :playlistId")
    suspend fun incrementTrackCount(playlistId: Long)

    @Query("DELETE FROM playlist_track_crossref_table WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deleteTrackFromListById(playlistId: Long, trackId: Long): Int

    @Query("UPDATE playlist_table SET countOfTracks = MAX(0, countOfTracks - 1) WHERE playlistId = :playlistId")
    suspend fun decrementTrackCount(playlistId: Long)

    @Transaction
    @Query("SELECT * FROM playlist_table WHERE playlistId = :playlistId")
    suspend fun getPlaylistWithTracks(playlistId: Long): PlaylistWithTracks?

    @Query("SELECT * FROM playlist_table WHERE playlistId = :playlistId")
    fun getPlaylistById(playlistId: Long): Flow<PlaylistEntity?>

    @Query("SELECT * FROM playlist_table")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlist_track_crossref_table WHERE playlistId = :playlistId AND trackId = :trackId ")
    suspend fun getPlaylistTracksCrossRef(playlistId: Long, trackId: Long): PlaylistTracksCrossRef?

    @Query("DELETE FROM playlist_table WHERE playlistId = :playlistId")
    suspend fun fullDeletePlaylist(playlistId: Long)
}