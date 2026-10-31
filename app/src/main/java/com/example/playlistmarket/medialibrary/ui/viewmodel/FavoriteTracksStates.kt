package com.example.playlistmarket.medialibrary.ui.viewmodel

import com.example.playlistmarket.search.domain.models.Track

sealed class FavoriteTracksStates {
    object  DefaultState: FavoriteTracksStates()
    object EmptyResultState: FavoriteTracksStates()
    data class ShowFavoriteTracks(val tracks: List<Track>): FavoriteTracksStates()

}

sealed class RequestToUIState {
    data class OpenMusicPlayerRequestState(val track: Track): RequestToUIState()
}