package com.example.playlistmarket.player.ui.viewmodel

import com.example.playlistmarket.search.domain.models.Track

sealed class State {
    object Initialization: State()
    data class ReadyToPlay(val timeOfTrack: String): State()
    object Pause: State()
    data class Playing(val timeOfTrack: String): State()

}