package com.example.playlistmarket.search.ui.viewmodel

import com.example.playlistmarket.search.domain.models.Track

sealed class Event {
    data class MusicPlayerInvokeEvent(val track: Track) : Event()
}