package com.example.playlistmarket.player.ui.viewmodel

import com.example.playlistmarket.medialibrary.domain.models.Playlist

sealed class AddTrackInPlaylistState {
    data class ShowPlaylistState(val playlist: List<Playlist>): AddTrackInPlaylistState()
    object IdlingState: AddTrackInPlaylistState()
    object EndTransactionState: AddTrackInPlaylistState()
}