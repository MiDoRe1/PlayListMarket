package com.example.playlistmarket.domain.api

import com.example.playlistmarket.domain.models.Track

interface TracksRepository: OnChangesRegisterable {
    fun searchTracks(expression: String): List<Track>

    fun getTracksHistory(): List<Track>

    fun updateTracksHistory(tracks: List<Track>)
}