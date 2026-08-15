package com.example.playlistmarket.search.domain.api.repositories

import com.example.playlistmarket.search.domain.models.Track
import com.example.playlistmarket.utils.Resource

interface TracksRepository {
    fun searchTracks(expression: String): Resource<List<Track>>

}