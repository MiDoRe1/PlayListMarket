package com.example.playlistmarket.domain.impls

import com.example.playlistmarket.domain.api.OnChangesRegisterable
import com.example.playlistmarket.domain.api.TracksInteractor
import com.example.playlistmarket.domain.api.TracksRepository
import com.example.playlistmarket.domain.models.Track
import java.util.concurrent.Executors

class TracksInteractorImpl(
    private val tracksRepository: TracksRepository
) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(
        expression: String,
        consumer: TracksInteractor.TracksConsumer,
        doOnfail: ()->Unit
    ) {
            executor.execute {
                try {
                    consumer.consume(tracksRepository.searchTracks(expression))
                }
                catch (e: Exception) {
                    doOnfail()
                }
            }
    }

    override fun getTracksHistory(consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(tracksRepository.getTracksHistory())
        }
    }

    fun getTracksHistory(): List<Track> {
        return tracksRepository.getTracksHistory()
    }


    override fun insertTrackInTracksHistory(track: Track) {
        executor.execute {
            val tracks = tracksRepository.getTracksHistory().toMutableList()
            tracks.remove(track)
            tracks.add(track)
            while (tracks.size > MAX_COUNT_OF_SAVED_VIEWED_TRACKS) {
                tracks.removeAt(0)
            }
            tracksRepository.updateTracksHistory(tracks)
        }

    }

    override fun clearTracksHistory() {
        tracksRepository.updateTracksHistory(listOf<Track>())
    }

    override fun registerOnChanges(listener: OnChangesRegisterable.Listener) {
        tracksRepository.registerOnChanges(listener)
    }

    override fun unregisterOnChanges(listener: OnChangesRegisterable.Listener) {
        tracksRepository.unregisterOnChanges(listener)
    }

    companion object {
        const val MAX_COUNT_OF_SAVED_VIEWED_TRACKS = 10
    }
}