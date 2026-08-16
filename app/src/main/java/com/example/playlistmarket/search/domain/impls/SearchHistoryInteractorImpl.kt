package com.example.playlistmarket.search.domain.impls

import com.example.playlistmarket.search.domain.api.interactors.SearchHistoryInteractor
import com.example.playlistmarket.search.domain.api.repositories.SearchHistoryRepository
import com.example.playlistmarket.search.domain.models.Track

class SearchHistoryInteractorImpl(
    private val searchHistoryRepository: SearchHistoryRepository
): SearchHistoryInteractor {

    override fun saveToHistory(track: Track) {
        getHistory { tracks ->
            val oldList = mutableListOf<Track>()
            oldList.addAll(tracks.reversed())
            oldList.removeIf { it.trackId == track.trackId }
            oldList.add(track)
            while (oldList.size > MAX_COUNT_TRACKS_IN_HISTORY) {
                oldList.removeAt(0)
            }
            searchHistoryRepository.clearHistory()
            oldList.forEach { searchHistoryRepository.saveToHistory(it) }
        }
    }

    override fun getHistory(consumer: SearchHistoryInteractor.Consumer) {
        val tracks = searchHistoryRepository.getHistory().data ?: listOf()
        consumer.consume(tracks.reversed())
    }

    override fun clearHistory() {
        searchHistoryRepository.clearHistory()
    }


    companion object {
        val MAX_COUNT_TRACKS_IN_HISTORY = 10
    }
}