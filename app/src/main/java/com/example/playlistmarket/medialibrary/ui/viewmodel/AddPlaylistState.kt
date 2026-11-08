package com.example.playlistmarket.medialibrary.ui.viewmodel

import com.example.playlistmarket.medialibrary.domain.models.Playlist

sealed class AddPlaylistState {
    data class EnteringDataState(val isEnough: Boolean = false): AddPlaylistState()
    object WarningState: AddPlaylistState()
    data class PlaylistCreatedState(val playlist: Playlist): AddPlaylistState()
    object CloseState: AddPlaylistState()
    object FinishEditState: AddPlaylistState()
}