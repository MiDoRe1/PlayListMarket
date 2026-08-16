package com.example.playlistmarket.search.domain.api.interactors

import com.example.playlistmarket.search.domain.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String,
                     consumer: TracksConsumer
    )

    fun interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }
}