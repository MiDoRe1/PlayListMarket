package com.example.playlistmarket.domain.api

import com.example.playlistmarket.domain.models.Track

interface TracksInteractor: OnChangesRegisterable {
    fun searchTracks(expression: String,
                     consumer: TracksConsumer,
                     doOnfail: ()->Unit)

    fun getTracksHistory(consumer: TracksConsumer)

    fun insertTrackInTracksHistory(track: Track)

    fun clearTracksHistory()

    fun interface TracksConsumer {
        fun consume(foundTracks: List<Track>)
    }
}