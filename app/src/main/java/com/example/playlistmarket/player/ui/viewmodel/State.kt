package com.example.playlistmarket.player.ui.viewmodel

sealed class State {
    object Initialization: State()
    object ReadyToPlay: State()
    object Pause: State()
    object Playing: State()
}