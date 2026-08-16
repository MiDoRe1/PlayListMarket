package com.example.playlistmarket.search.domain.api.repositories

import com.example.playlistmarket.search.domain.models.Track
import com.example.playlistmarket.utils.Resource

interface SearchHistoryRepository {
    fun saveToHistory(track: Track)
    fun getHistory(): Resource<List<Track>>

    fun clearHistory()
}