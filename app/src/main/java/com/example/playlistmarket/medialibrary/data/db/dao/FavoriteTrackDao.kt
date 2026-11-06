package com.example.playlistmarket.medialibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.playlistmarket.medialibrary.data.db.entity.FavoriteTrackEntity
import com.example.playlistmarket.medialibrary.data.db.entity.PlaylistTracksCrossRef
import com.example.playlistmarket.medialibrary.data.db.entity.TrackInPlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTrackDao {

    @Insert(entity = FavoriteTrackEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavoriteTrack(trackEntity: FavoriteTrackEntity): Long

    @Delete(entity = FavoriteTrackEntity::class)
    suspend fun deleteFavoriteTrack(trackEntity: FavoriteTrackEntity)

    @Query("SELECT EXISTS(Select 1 FROM favorite_track_table WHERE trackId=:trackId)")
    suspend fun isFavoriteTrack(trackId: Long): Boolean

    @Query("Select * from favorite_track_table order by createdAt DESC")
    fun getAllFavoriteTracks(): Flow<List<FavoriteTrackEntity>>

    @Query("Select * from favorite_track_table WHERE trackId=:id")
    fun getFavoriteTrackById(id: Long): Flow<FavoriteTrackEntity?>

}