package com.example.playlistmarket.search.domain.api.interactors

import com.example.playlistmarket.search.domain.models.Track

interface SearchHistoryInteractor {
    fun saveToHistory(track: Track)
    fun getHistory(consumer: Consumer)
    fun clearHistory()

    fun interface Consumer {
        fun consume(tracks: List<Track>)
    }
}