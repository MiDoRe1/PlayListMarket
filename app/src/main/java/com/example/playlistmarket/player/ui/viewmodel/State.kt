package com.example.playlistmarket.player.ui.viewmodel

sealed class State {
    object Initialization: State()
    class ReadyToPlay(val timeOfTrack: String): State()
    object Pause: State()
    class Playing(val timeOfTrack: String): State()
}