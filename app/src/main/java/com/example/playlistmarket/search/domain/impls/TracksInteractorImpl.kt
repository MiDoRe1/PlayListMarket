package com.example.playlistmarket.search.domain.impls


import com.example.playlistmarket.search.domain.api.interactors.TracksInteractor
import com.example.playlistmarket.search.domain.api.repositories.TracksRepository
import com.example.playlistmarket.search.domain.models.Track
import com.example.playlistmarket.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    override fun searchTracksWithFlow(expression: String): Flow<Pair<List<Track>?, String?>> {
        return tracksRepository.searchTracksWithFlow(expression).map { resource ->
            when (resource) {
                is Resource.Success -> Pair(resource.data, null)
                is Resource.Error -> Pair(null, resource.message)
            }
        }
    }


    companion object {
        const val MAX_COUNT_OF_SAVED_VIEWED_TRACKS = 10
    }
}