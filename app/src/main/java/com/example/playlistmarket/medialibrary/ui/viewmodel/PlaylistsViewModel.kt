package com.example.playlistmarket.medialibrary.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmarket.medialibrary.domain.api.PlaylistInteractor
import com.example.playlistmarket.medialibrary.domain.models.Playlist
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private var playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val liveDataVmState = MutableLiveData<PlaylistsVmState>()
    fun observeLiveDataVmState(): LiveData<PlaylistsVmState> = liveDataVmState



    fun getPlayLists() {
        viewModelScope.launch {
            val playlists = playlistInteractor.getAllPlaylists().firstOrNull()
            if (playlists != null && playlists.isNotEmpty()) {
                liveDataVmState.postValue(
                    PlaylistsVmState.ShowPlaylistsState(playlists)
                )
            } else {
                liveDataVmState.postValue(
                    PlaylistsVmState.EmptyResults
                )
            }
        }
    }

    fun showPlaylistInfo(playlist: Playlist) {

    }
}