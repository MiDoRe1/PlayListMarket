package com.example.playlistmarket.player.ui.viewmodel

sealed class State {
    object Initialization: State()
    data class ReadyToPlay(val timeOfTrack: String): State()
    object Pause: State()
    data class Playing(val timeOfTrack: String): State()
}