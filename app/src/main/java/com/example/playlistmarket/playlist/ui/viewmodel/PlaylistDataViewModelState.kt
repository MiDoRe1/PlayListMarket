package com.example.playlistmarket.playlist.ui.viewmodel

import com.example.playlistmarket.medialibrary.domain.models.Playlist
import com.example.playlistmarket.search.domain.models.Track

sealed class PlaylistDataViewModelState {
    data class Success(val playlist: Playlist, val tracks:List<Track>, val totalTime: String): PlaylistDataViewModelState()
    object DefaultState: PlaylistDataViewModelState()
    object ClearedState: PlaylistDataViewModelState()
}