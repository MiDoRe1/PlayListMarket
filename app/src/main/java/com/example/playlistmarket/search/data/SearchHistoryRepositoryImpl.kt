package com.example.playlistmarket.search.data

import com.example.playlistmarket.search.domain.api.repositories.SearchHistoryRepository
import com.example.playlistmarket.search.domain.models.Track
import com.example.playlistmarket.utils.Resource
import com.example.playlistmarket.utils.commonClients.LocalStorageClient

class SearchHistoryRepositoryImpl(
    private val localStorageClient: LocalStorageClient<ArrayList<Track>>
): SearchHistoryRepository {

    override fun saveToHistory(track: Track) {
        val tracks = localStorageClient.getData() ?: arrayListOf()
        tracks.add(track)
        localStorageClient.saveData(tracks)
    }

    override fun getHistory(): Resource<List<Track>> {
        val tracks = localStorageClient.getData() ?: arrayListOf()
        return Resource.Success(tracks)
    }

    override fun clearHistory() {
        localStorageClient.saveData(arrayListOf())
    }
}