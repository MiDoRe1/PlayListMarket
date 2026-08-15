package com.example.playlistmarket.search.ui.viewmodel

import com.example.playlistmarket.search.domain.models.Track

sealed class State {
    data class FoundTracksState(val tracks: List<Track>): State()
    object  LoadingState: State()
    object EmptyResultState: State()
    data class ErrorState(val message: String): State()
    class HistoryState(val tracks: List<Track>): State()
    object DefaultState: State()
}
