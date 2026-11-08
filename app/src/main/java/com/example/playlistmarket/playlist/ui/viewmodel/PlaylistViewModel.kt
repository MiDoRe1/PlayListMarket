package com.example.playlistmarket.playlist.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmarket.medialibrary.domain.api.PlaylistInteractor
import com.example.playlistmarket.medialibrary.domain.models.Playlist
import com.example.playlistmarket.search.domain.models.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Duration

class PlaylistViewModel(
    private var playlistInteractor: PlaylistInteractor,
    private val playlistId: Long
): ViewModel() {

    private val _playlistData: MutableStateFlow<PlaylistDataViewModelState> = MutableStateFlow(PlaylistDataViewModelState.DefaultState)
    val playlistData: StateFlow<PlaylistDataViewModelState> = _playlistData.asStateFlow()


    init {
        viewModelScope.launch {
            updateData()
        }
    }

    suspend fun updateData() {
        val playlist = playlistInteractor.getPlaylistById(playlistId).first()
        val tracksList = playlistInteractor.getTracksFromPlaylist(playlist!!)
        val totalTime = (tracksList.sumOf { track ->
            Log.d("time",track.trackTime)
            val parts = track.trackTime.trim().split(":")
            parts[0].toLong()*60+parts[1].toLong()
        }/60).toString()
        _playlistData.value = PlaylistDataViewModelState.Success(
            playlist = playlist,
            tracks = tracksList,
            totalTime = totalTime
        )
    }

    fun deleteTrackFromPlaylist(track: Track) {
        viewModelScope.launch {
            if (playlistData.value is PlaylistDataViewModelState.Success) {
                val playlist = (playlistData.value as PlaylistDataViewModelState.Success).playlist
                playlistInteractor.deleteTrackFromPlaylist(playlist, track)
                updateData()
            }
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            if (playlistData.value is PlaylistDataViewModelState.Success) {
                val playlist = (playlistData.value as PlaylistDataViewModelState.Success).playlist
                playlistInteractor.deletePlaylist(playlist)
                _playlistData.value = PlaylistDataViewModelState.ClearedState
            }
        }
    }



}