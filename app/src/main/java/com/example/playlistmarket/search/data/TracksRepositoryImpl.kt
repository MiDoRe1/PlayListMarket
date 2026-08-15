package com.example.playlistmarket.search.data


import com.example.playlistmarket.search.data.dto.TrackInfoDto
import com.example.playlistmarket.search.data.dto.TrackInfoRequest
import com.example.playlistmarket.search.data.dto.TrackInfoResponse
import com.example.playlistmarket.search.domain.api.repositories.TracksRepository
import com.example.playlistmarket.search.domain.models.Track
import com.example.playlistmarket.utils.Resource
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
) : TracksRepository {

    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TrackInfoRequest(expression))

        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }
            200 -> {
                Resource.Success( (response as TrackInfoResponse).results!!.map {
                    trackInfoDtoToTrack(it) })
            }
            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
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

}