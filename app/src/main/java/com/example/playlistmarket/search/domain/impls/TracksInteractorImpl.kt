package com.example.playlistmarket.search.domain.impls


import com.example.playlistmarket.search.domain.api.interactors.TracksInteractor
import com.example.playlistmarket.search.domain.api.repositories.TracksRepository
import com.example.playlistmarket.utils.Resource
import java.util.concurrent.Executors

class TracksInteractorImpl(
    private val tracksRepository: TracksRepository
) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(
        expression: String,
        consumer: TracksInteractor.TracksConsumer
    ) {
            executor.execute {
                when (val res = tracksRepository.searchTracks(expression)){
                    is Resource.Success -> {
                        consumer.consume(res.data, null)
                    }
                    is Resource.Error -> {
                        consumer.consume(res.data, res.message)
                    }
                }
            }
    }



    companion object {
        const val MAX_COUNT_OF_SAVED_VIEWED_TRACKS = 10
    }
}