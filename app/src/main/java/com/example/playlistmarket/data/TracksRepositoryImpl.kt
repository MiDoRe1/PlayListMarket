package com.example.playlistmarket.data


import com.example.playlistmarket.data.dto.TrackInfoDto
import com.example.playlistmarket.data.dto.TrackInfoRequest
import com.example.playlistmarket.data.dto.TrackInfoResponse
import com.example.playlistmarket.data.dto.TracksHistoryDto
import com.example.playlistmarket.data.localStorage.SharedPreferencesPack.TracksHistoryPreferencesWorker
import com.example.playlistmarket.domain.api.OnChangesRegisterable
import com.example.playlistmarket.domain.api.TracksRepository
import com.example.playlistmarket.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random

class TracksRepositoryImpl(
    private val tracksNetworkClient: TracksNetworkClient,
    private val tracksHistoryLocalStorageWorker: LocalStorageWorker<TracksHistoryDto>
) : TracksRepository {

    override fun searchTracks(expression: String): List<Track> {
        val response = tracksNetworkClient.doRequest(TrackInfoRequest(expression))

        if (response.resultCode == 200) {
            return (response as TrackInfoResponse).results!!.map {
                trackInfoDtoToTrack(it)
            }
        } else {
            return emptyList()
        }
    }

    override fun getTracksHistory(): List<Track> {
        return tracksHistoryLocalStorageWorker.getData().tracks

    }

    override fun updateTracksHistory(tracks: List<Track>) {
        tracksHistoryLocalStorageWorker.saveData(TracksHistoryDto(tracks))
    }


    private fun trackInfoDtoToTrack(trackInfoDto: TrackInfoDto): Track {
        val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        val trackModel = Track(
            trackId = trackInfoDto.trackId ?: Random.nextLong(Long.MIN_VALUE, -3),
            trackName = trackInfoDto.trackName ?: "empty",
            artistName = trackInfoDto.artistName ?: "empty",
            trackTime = timeFormatter.format(trackInfoDto.trackTimeMillis ?: 0L),
            artworkUrl100 = trackInfoDto.artworkUrl100 ?: "empty",
            collectionName = trackInfoDto.collectionName ?: "",
            releaseDate = trackInfoDto.releaseDate ?: "",
            primaryGenreName = trackInfoDto.primaryGenreName ?: "",
            country = trackInfoDto.country ?: "",
            previewUrl = trackInfoDto.previewUrl ?: ""
        )
        return trackModel
    }

    override fun registerOnChanges(listener: OnChangesRegisterable.Listener) {
        tracksHistoryLocalStorageWorker.registerOnChanges(listener)
    }

    override fun unregisterOnChanges(listener: OnChangesRegisterable.Listener) {
        tracksHistoryLocalStorageWorker.registerOnChanges(listener)
    }
}