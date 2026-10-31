package com.example.playlistmarket.search.data.ITunesSearchAPI



import com.example.playlistmarket.search.data.NetworkClient
import com.example.playlistmarket.search.data.dto.TrackInfoRequest
import com.example.playlistmarket.search.data.dto.TrackInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.playlistmarket.search.data.dto.Response as dtoResponse
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ITunesSearchApiTracksNetworkClient(
    private val iTunesService: ITunesSearchAPIInterface
): NetworkClient {

    override fun doRequest(dto: Any): dtoResponse {
        try {
            if (dto is TrackInfoRequest) {
                val resp = iTunesService.getTracks(dto.expression).execute()
                val body = resp.body() ?: dtoResponse()
                return body.apply { resultCode = resp.code() }
            } else {
                return dtoResponse().apply { resultCode = 400 }
            }
        } catch (e: UnknownHostException) {
            return dtoResponse().apply { resultCode = -1 }
        } catch (e: SocketTimeoutException) {
            return dtoResponse().apply { resultCode = -1 }
        }


    }

    override suspend fun doRequestSuspend(dto: Any): dtoResponse {
        if (dto is TrackInfoRequest) {
            return withContext(Dispatchers.IO) {
                try {
                    val resp = iTunesService.getTracksSuspend(dto.expression)
                    resp.apply {
                        resultCode = 200
                    }
                } catch (e: Throwable) {
                    dtoResponse().apply { resultCode = -1 }
                }
            }
        } else {
            return dtoResponse().apply { resultCode = 400 }
        }
    }


    fun interface OnResponseReactable {
        fun doOnResponse(tracksData: TrackInfoResponse?)
    }

    fun interface OnFailureReactable {
        fun doOnFailure()
    }

    companion object {
        const val DEFAULT_URL_ITUNES : String =  "https://itunes.apple.com/"
    }

}