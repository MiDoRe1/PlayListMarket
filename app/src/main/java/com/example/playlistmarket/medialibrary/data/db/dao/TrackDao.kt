package com.example.playlistmarket.medialibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.playlistmarket.medialibrary.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(trackEntity: TrackEntity): Long

    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrack(trackEntity: TrackEntity)

    @Update(entity = TrackEntity::class)
    suspend fun updateTrack(trackEntity: TrackEntity)

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackIgnore(trackEntity: TrackEntity)

    @Query("UPDATE track_table SET isFavorites = :isFavorites WHERE trackId = :trackId")
    suspend fun updateFavoriteStatus(trackId: Long, isFavorites: Boolean)

    @Transaction
    suspend fun upsertFavoriteStatus(trackEntity: TrackEntity, isFavorites: Boolean) {
        insertTrackIgnore(trackEntity)
        updateFavoriteStatus(trackEntity.trackId, isFavorites)
    }

    @Query("Select * from track_table order by createdAt DESC")
    fun getAllTracks(): Flow<List<TrackEntity>>

    @Query("Select * from track_table WHERE isFavorites=1 order by createdAt DESC")
    fun getAllFavoriteTracks(): Flow<List<TrackEntity>>

    @Query("Select * from track_table where trackId = :trackId")
    fun getTrackById(trackId: Long): Flow<TrackEntity?>


}