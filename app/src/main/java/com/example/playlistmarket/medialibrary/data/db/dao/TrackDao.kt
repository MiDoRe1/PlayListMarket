package com.example.playlistmarket.medialibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmarket.medialibrary.data.db.entity.TrackEntity

@Dao
interface TrackDao {

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(trackEntity: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrack(trackEntity: TrackEntity)

    @Update(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTrack(trackEntity: TrackEntity)

    @Query("Select * from track_table order by createdAt DESC")
    suspend fun getAllTracks(): List<TrackEntity>

    @Query("Select * from track_table where trackId = :trackId")
    suspend fun getTrackById(trackId: Long): TrackEntity?
}