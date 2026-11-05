package com.example.playlistmarket.medialibrary.ui.viewmodel

import com.example.playlistmarket.medialibrary.domain.models.Playlist

sealed class PlaylistsVmState {
    data class ShowPlaylistsState(val playlists: List<Playlist>): PlaylistsVmState()
    object EmptyResults: PlaylistsVmState()
}